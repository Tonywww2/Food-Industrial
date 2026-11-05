package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Highest purity each material reaches with the devices of T0, T0–T1, … T0–T4 (machines/01 §1.1),
 * for the designers to compare with the stages in additives/03, 13 and 14. Only a table; it does not
 * judge (2026-11-04).
 * <p>
 * For each tier limit the recipes are applied repeatedly until nothing improves: every recipe runs on
 * its best device within the limit, participating inputs at the best purity found so far for their
 * material (a recipe waits until each such input has one), products keep the maximum. Inputs that do
 * not take part are assumed available. {@code purity_range} counts with its high end.
 */
public final class PurityReport {
    private static final int MAX_ROUNDS = 1000;

    private PurityReport() {
    }

    /** Best purity byte per material id at each tier limit (index = tier ordinal). */
    public static Map<String, int[]> compute(List<PlatformRecipes.Loaded<ProcessRecipe>> recipes, List<DeviceSpec> devices) {
        Map<String, int[]> result = new TreeMap<>();
        for (Tier limit : Tier.values()) {
            Map<String, Integer> best = fixpoint(recipes, devices, limit);
            best.forEach((material, b) -> {
                int[] row = result.computeIfAbsent(material, key -> {
                    int[] empty = new int[Tier.values().length];
                    java.util.Arrays.fill(empty, PurityData.NONE);
                    return empty;
                });
                row[limit.ordinal()] = b;
            });
        }
        return result;
    }

    private static Map<String, Integer> fixpoint(List<PlatformRecipes.Loaded<ProcessRecipe>> recipes, List<DeviceSpec> devices, Tier limit) {
        List<ProcessRecipe> usable = new ArrayList<>();
        List<Tier> deviceTiers = new ArrayList<>();
        List<Tier> baseTiers = new ArrayList<>();
        for (PlatformRecipes.Loaded<ProcessRecipe> loaded : recipes) {
            Tier base = null;
            Tier top = null;
            for (DeviceSpec device : devices) {
                if (device.canRun(loaded.data())) {
                    Tier tier = device.tier();
                    if (base == null || tier.ordinal() < base.ordinal()) {
                        base = tier;
                    }
                    if (limit.atLeast(tier) && (top == null || tier.ordinal() > top.ordinal())) {
                        top = tier;
                    }
                }
            }
            if (top != null) {
                usable.add(loaded.data());
                deviceTiers.add(top);
                baseTiers.add(base);
            }
        }
        Map<String, Integer> best = new HashMap<>();
        for (int round = 0; round < MAX_ROUNDS; round++) {
            boolean changed = false;
            for (int r = 0; r < usable.size(); r++) {
                changed |= apply(usable.get(r), deviceTiers.get(r), baseTiers.get(r), best);
            }
            if (!changed) {
                break;
            }
        }
        return best;
    }

    private static boolean apply(ProcessRecipe recipe, Tier device, Tier base, Map<String, Integer> best) {
        int size = recipe.inputs().size();
        int[] values = new int[size];
        long[] amounts = new long[size];
        for (int i = 0; i < size; i++) {
            values[i] = PurityData.NONE;
            if (recipe.participates(i)) {
                Integer value = best.get(recipe.inputs().get(i).material());
                if (value == null) {
                    return false;
                }
                values[i] = value;
                amounts[i] = recipe.inputs().get(i).weight();
            }
        }
        double start = OutcomeResolver.start(recipe, values, amounts);
        boolean changed = false;
        for (int index = 0; index < recipe.outputs().size(); index++) {
            ProcessOutput output = recipe.outputs().get(index);
            if (!output.producedAt(device) || !output.bearsPurity() || output.material() == null) {
                continue;
            }
            int b = index == 0 && recipe.purityRange().isPresent() ? recipe.rangeHigh()
                : OutcomeResolver.apply(recipe.ruleOf(index), PurityData.current().material(output.material()), start,
                device.ordinal() - base.ordinal());
            if (b == PurityData.NONE) {
                continue;
            }
            Integer old = best.get(output.material());
            if (old == null || b > old) {
                best.put(output.material(), b);
                changed = true;
            }
        }
        return changed;
    }

    /** The report as Markdown. */
    public static String markdown(Map<String, int[]> table) {
        Map<String, String> names = new HashMap<>();
        for (MaterialDef def : MaterialTable.load()) {
            names.put(FoodIndustrial.MOD_ID + ":" + def.id(), def.nameZh());
        }
        StringBuilder text = new StringBuilder();
        text.append("# 纯度报表\n\n");
        text.append("每种物料在只用 T0、T0–T1 … T0–T4 设备时能达到的最高纯度，以及最早达到食品级的档次（machines/01 §1.1）。");
        text.append("由数据生成写出，只出表，不判定是否与 additives/03、13、14 一致。\n\n");
        text.append("| 物料 | 名称 | T0 | T1 | T2 | T3 | T4 | 最早食品级 |\n");
        text.append("| --- | --- | --- | --- | --- | --- | --- | --- |\n");
        table.forEach((material, row) -> {
            text.append("| ").append(material).append(" | ").append(names.getOrDefault(material, "")).append(" |");
            for (int b : row) {
                text.append(' ').append(b == PurityData.NONE ? "—" : PurityScale.format(b) + "%").append(" |");
            }
            text.append(' ').append(earliestFood(material, row)).append(" |\n");
        });
        if (table.isEmpty()) {
            text.append("| （没有配方产出带纯度的物料） | | | | | | | |\n");
        }
        return text.toString();
    }

    private static String earliestFood(String material, int[] row) {
        PurityData.Material data = PurityData.current().material(material);
        if (data == null || data.lowerBound(PurityGrade.FOOD).isEmpty()) {
            return "无食品级";
        }
        int bound = data.lowerBound(PurityGrade.FOOD).getAsInt();
        for (Tier tier : Tier.values()) {
            if (row[tier.ordinal()] != PurityData.NONE && row[tier.ordinal()] >= bound) {
                return tier.name();
            }
        }
        return "达不到";
    }
}
