package com.tonywww.foodindustrial.content;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.gui.GuiLayout;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Slots against recipes (I47.5, machines/03 rule 4): {@code build/reports/foodindustrial/slot_fit.md} lists, for every
 * recipe, the devices that run it and its tightest slot role on the lowest of them, then, for every device, the most each
 * slot role is asked for by the recipes it runs. That every recipe has a device is the recipe check's "no registered
 * device can run it"; this class adds the device-side checks: the ingredient-step devices (the ones with additive slots)
 * have nine item inputs like the crafting table, and every machine window holds its slots.
 */
public final class SlotFit {
    /** Item inputs of the ingredient-step devices: the crafting table's grid (machines/03 rule 4). */
    public static final int INGREDIENT_STEP_INPUTS = 9;
    private static final String[] ROLES = {"I", "FI", "C", "O", "FO"};

    private SlotFit() {
    }

    static void bootstrap() {
        RecipeGen.addSetCheck(recipes -> devices());
        RecipeGen.addFollower(SlotFit::report);
    }

    /** What a recipe asks of each role, in the order of {@link #ROLES}, on a device of the recipe's tier. */
    private static int[] needs(ProcessRecipe recipe, DeviceSpec device) {
        int itemOut = 0;
        int fluidOut = 0;
        for (ProcessOutput output : recipe.outputs()) {
            if (output.producedAt(device.tier())) {
                if (output.isFluid()) {
                    fluidOut++;
                } else {
                    itemOut++;
                }
            }
        }
        if (recipe.plate().isPresent()) {
            // a culture plate gives the one colony the player picks (M30, as DeviceSpec#whyNot)
            itemOut = Math.min(itemOut, 1);
        }
        return new int[]{recipe.itemInputCount(), recipe.fluidInputCount(), recipe.catalysts().size(), itemOut, fluidOut};
    }

    private static int[] slots(DeviceSpec device) {
        DeviceSpec.Slots slots = device.slots();
        return new int[]{slots.itemIn(), slots.fluidIn(), slots.catalyst(), slots.itemOut(), slots.fluidOut()};
    }

    /** Problems of the devices themselves. */
    static List<String> devices() {
        List<String> problems = new ArrayList<>();
        for (MachineDefinition definition : MachineDefinition.all()) {
            if (definition.development()) {
                continue;
            }
            IoLayout io = definition.layout();
            if (io.additives() > 0 && io.itemIn() < INGREDIENT_STEP_INPUTS) {
                problems.add(definition.id() + " takes additives, so it is an ingredient-step device, but has " + io.itemIn()
                    + " item inputs, fewer than the crafting table's " + INGREDIENT_STEP_INPUTS + " (machines/03 rule 4)");
            }
            if (!GuiLayout.fits(GuiLayout.full(definition))) {
                problems.add(definition.id() + ": the slots and tanks do not fit the machine window (machines/04 §1)");
            }
        }
        return problems;
    }

    private static String name(DeviceSpec device) {
        String id = device.id();
        return id.startsWith(FoodIndustrial.MOD_ID + ":") ? id.substring(FoodIndustrial.MOD_ID.length() + 1) : id;
    }

    private static void report(PlatformDatagen.Sink sink, Map<String, ProcessRecipe> recipes) {
        List<DeviceSpec> devices = DeviceSpec.all().stream().filter(device -> !device.id().contains(":dev_"))
            .sorted(Comparator.comparingInt(device -> device.tier().ordinal())).toList();
        Map<DeviceSpec, int[]> most = new LinkedHashMap<>();
        StringBuilder rows = new StringBuilder();
        int unrunnable = 0;
        for (Map.Entry<String, ProcessRecipe> entry : recipes.entrySet()) {
            ProcessRecipe recipe = entry.getValue();
            List<DeviceSpec> runners = devices.stream().filter(device -> device.canRun(recipe)).toList();
            String tightest = "—";
            if (runners.isEmpty()) {
                unrunnable++;
            } else {
                DeviceSpec lowest = runners.get(0);
                int[] need = needs(recipe, lowest);
                int[] have = slots(lowest);
                int best = -1;
                for (int i = 0; i < ROLES.length; i++) {
                    if (need[i] > 0 && (best < 0 || have[i] - need[i] < have[best] - need[best])) {
                        best = i;
                    }
                }
                if (best >= 0) {
                    tightest = ROLES[best] + " " + need[best] + "/" + have[best] + "（" + name(lowest) + "）";
                }
                for (DeviceSpec device : runners) {
                    int[] max = most.computeIfAbsent(device, key -> new int[ROLES.length]);
                    int[] asked = needs(recipe, device);
                    for (int i = 0; i < ROLES.length; i++) {
                        max[i] = Math.max(max[i], asked[i]);
                    }
                }
            }
            int[] need = runners.isEmpty() ? needs(recipe, devices.get(0)) : needs(recipe, runners.get(0));
            rows.append("| ").append(entry.getKey()).append(" | ").append(need[0]).append('/').append(need[1]).append('/').append(need[2])
                .append('/').append(need[3]).append('/').append(need[4]).append(" | ")
                .append(runners.isEmpty() ? "**无设备可执行**" : String.join("、", runners.stream().map(SlotFit::name).toList()))
                .append(" | ").append(tightest).append(" |\n");
        }
        StringBuilder text = new StringBuilder("# 配方与设备槽位\n\n");
        text.append("数据生成时写出（I47.5，machines/03 规则 4）。“需要”依次为物品输入、流体输入、催化剂、物品输出、流体输出；")
            .append("“最紧”取最低档设备上余量最少的一项（需要/槽位）。共 ").append(recipes.size()).append(" 条配方，无设备可执行 ")
            .append(unrunnable).append(" 条。\n\n");
        text.append("## 各设备被配方用到的最大值\n\n| 设备 | 档次 | I | FI | C | O | FO |\n| --- | --- | --- | --- | --- | --- | --- |\n");
        for (DeviceSpec device : devices) {
            int[] max = most.get(device);
            if (max == null) {
                continue;
            }
            int[] have = slots(device);
            text.append("| ").append(name(device)).append(" | ").append(device.tier().name());
            for (int i = 0; i < ROLES.length; i++) {
                text.append(" | ").append(max[i]).append('/').append(have[i]);
            }
            text.append(" |\n");
        }
        text.append("\n## 每条配方\n\n| 配方 | 需要 | 放得下的设备 | 最紧 |\n| --- | --- | --- | --- |\n").append(rows);
        sink.report("slot_fit.md", text.toString());
    }
}
