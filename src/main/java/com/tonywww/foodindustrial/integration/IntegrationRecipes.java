package com.tonywww.foodindustrial.integration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.compat.integration.PlatformIntegrationFormats;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipeConditions;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.OutcomeResolver;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.RecipeGen;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data generation of this mod's recipes for other mods' machines (additives/06 §他模组机器参与, additives/10
 * §7, I25). For every {@link ExternalMachine} on this node and every recipe it can hold, one file per
 * group of device tiers with the same result: outputs carry the purity a device of that tier gives
 * with every input at its threshold (or external default). Each file loads only with the other mod
 * installed and the integration enabled at one of its tiers ({@code foodindustrial:integration}).
 */
public final class IntegrationRecipes {
    private static final List<Tier> TIERS = List.of(Tier.T1, Tier.T2, Tier.T3, Tier.T4);

    private IntegrationRecipes() {
    }

    /** Writes the integration recipes of {@code recipes} (by path) and a report of how many each machine got. */
    public static void generate(PlatformDatagen.Sink sink, Map<String, ProcessRecipe> recipes) {
        StringBuilder report = new StringBuilder("# 联动配方（I25）\n\n| 机器 | 配方数 |\n| --- | --- |\n");
        StringBuilder clashes = new StringBuilder();
        java.util.Set<String> types = new java.util.HashSet<>();
        ExternalMachine.ALL.forEach(machine -> types.add(machine.type()));
        ForeignRecipes foreign = ForeignRecipes.load(types);
        // a hand (T0) recipe with a machine recipe of the same inputs stays with the T0 devices: machines take the machine
        // recipe (11 问题 41, the rule the uniqueness check of RecipeGen lets such pairs through by)
        Set<String> machineKeys = new java.util.HashSet<>();
        Set<String> handOnly = new java.util.HashSet<>();
        recipes.forEach((path, recipe) -> {
            if (!path.startsWith("manual/")) {
                machineKeys.add(RecipeGen.uniquenessKey(RecipeGen.toJson(recipe, sink.ops())));
            }
        });
        recipes.forEach((path, recipe) -> {
            if (path.startsWith("manual/") && machineKeys.contains(RecipeGen.uniquenessKey(RecipeGen.toJson(recipe, sink.ops())))) {
                handOnly.add(path);
            }
        });
        for (ExternalMachine machine : ExternalMachine.ALL) {
            if (!PlatformIntegrationFormats.available(machine.mod().id())) {
                continue;
            }
            int count = 0;
            for (Map.Entry<String, ProcessRecipe> entry : recipes.entrySet()) {
                if (handOnly.contains(entry.getKey())) {
                    continue;
                }
                Map<Tier, ExternalRecipe> plans = new LinkedHashMap<>();
                for (Tier tier : TIERS) {
                    ExternalRecipe plan = plan(entry.getValue(), machine, tier, sink.ops());
                    if (plan != null && PlatformIntegrationFormats.write(machine.type(), plan) != null) {
                        plans.put(tier, plan);
                    }
                }
                if (plans.isEmpty()) {
                    continue;
                }
                // the inputs the machine's own recipes take are left to those (2026-11-11, user); a recipe of
                // the machine that loads only with a further mod (Create's for Thermal's ore) cuts ours only then
                ExternalRecipe first = plans.values().iterator().next();
                String own = machine.mod().modid();
                Set<String> extra = new java.util.TreeSet<>(foreign.requiredMods(machine.type(), first));
                extra.remove(own);
                List<Variant> variants = new ArrayList<>();
                if (!extra.isEmpty()) {
                    Set<String> present = new java.util.HashSet<>(extra);
                    present.add(own);
                    variants.add(new Variant(foreign.narrow(machine.type(), first, present), extra, true));
                }
                variants.add(new Variant(foreign.narrow(machine.type(), first, Set.of(own)), extra, false));
                boolean written = false;
                for (Variant variant : variants) {
                    ForeignRecipes.Narrowing narrowing = variant.narrowing();
                    Map<String, JsonObject> files = new LinkedHashMap<>();
                    Map<String, List<Tier>> tiers = new LinkedHashMap<>();
                    if (narrowing == null || narrowing.recipe() != null) {
                        plans.forEach((tier, plan) -> {
                            ExternalRecipe narrowed = narrowing == null ? plan
                                : plan.withItems(narrowing.recipe().items()).withFluids(narrowing.recipe().fluids());
                            JsonObject json = PlatformIntegrationFormats.write(machine.type(), narrowed);
                            if (json != null) {
                                files.putIfAbsent(json.toString(), json);
                                tiers.computeIfAbsent(json.toString(), k -> new ArrayList<>()).add(tier);
                            }
                        });
                    }
                    if (narrowing != null) {
                        String when = variant.mods().isEmpty() ? "" : (variant.loaded() ? "装有 " : "未装 ") + String.join("、", variant.mods()) + " 时：";
                        clashes.append("| ").append(machine.type()).append(" | ").append(entry.getKey()).append(" | ")
                            .append(String.join("、", narrowing.foreign())).append(" | ").append(when)
                            .append(files.isEmpty() ? "整条不生成" : "只留 " + narrowing.kept()).append(" |\n");
                    }
                    String base = "compat/" + machine.mod().id() + "/" + machine.path() + "/" + entry.getKey()
                        + (variant.loaded() ? "_with_" + String.join("_", variant.mods()) : "");
                    for (Map.Entry<String, JsonObject> file : files.entrySet()) {
                        List<Tier> group = tiers.get(file.getKey());
                        boolean all = files.size() == 1 && group.equals(TIERS);
                        List<String> tierIds = all ? List.of() : group.stream().map(Tier::id).toList();
                        String path = all ? base : base + "_" + String.join("_", tierIds);
                        sink.json(PlatformDatagen.Kind.RECIPE, FoodIndustrial.MOD_ID, path, PlatformRecipeConditions.conditional(
                            file.getValue(), machine.mod().modid(), machine.mod().id(), tierIds, variant.mods(), variant.loaded()));
                        written = true;
                    }
                }
                if (written) {
                    count++;
                }
            }
            report.append("| ").append(machine.type()).append(" | ").append(count).append(" |\n");
        }
        if (!foreign.loaded()) {
            report.append("\n未读取他模组的配方，没有去掉与其自带配方重叠的输入。\n");
        } else if (!clashes.isEmpty()) {
            report.append("\n## 与他模组自带配方输入重叠的联动配方（重叠部分交给他模组）\n\n| 机器 | 本模组配方 | 他模组配方 | 结果 |\n| --- | --- | --- | --- |\n").append(clashes);
        }
        sink.report("integration_report.md", report.toString());
    }

    /**
     * One file set of a recipe on a machine: narrowed by {@code narrowing} ({@code null}: as it is), and
     * loading only when the further {@code mods} are all installed ({@code loaded}) or not all installed.
     */
    private record Variant(ForeignRecipes.@Nullable Narrowing narrowing, Set<String> mods, boolean loaded) {
    }

    /** {@code recipe} as {@code machine} at {@code tier} runs it, or {@code null} when it cannot. */
    @Nullable
    static ExternalRecipe plan(ProcessRecipe recipe, ExternalMachine machine, Tier tier, DynamicOps<JsonElement> ops) {
        if (!machine.types().contains(recipe.type()) || !recipe.catalysts().isEmpty() || recipe.plate().isPresent()
            || !recipe.environmentFactors().isEmpty() || !machine.capabilities().covers(recipe.required())) {
            return null;
        }
        if (!machine.anyOf().isEmpty() && machine.anyOf().stream().noneMatch(key -> recipe.required().keys().contains(key))) {
            return null;
        }
        Tier minTier = recipe.conditions().minTier();
        if (minTier != null && !tier.atLeast(minTier)) {
            return null;
        }
        List<ExternalRecipe.ItemIn> items = new ArrayList<>();
        List<ExternalRecipe.FluidIn> fluids = new ArrayList<>();
        for (ProcessInput input : recipe.inputs()) {
            if (input instanceof ProcessInput.ItemInput item) {
                boolean purity = input.threshold().isPresent();
                if (purity && !machine.purityInputs() || item.count() > machine.maxCount()) {
                    return null;
                }
                JsonElement json = PlatformIngredients.codec().encodeStart(ops, item.ingredient()).result().orElse(null);
                if (json == null) {
                    return null;
                }
                items.add(new ExternalRecipe.ItemIn(json, item.count(), purity));
            } else if (input instanceof ProcessInput.FluidInput fluid) {
                if (input.threshold().isPresent()) {
                    return null;
                }
                String tag = fluid.fluid().tag().map(key -> key.location().toString()).orElse(null);
                String id = fluid.fluid().fluid().map(PlatformTags::fluidId).orElse(null);
                fluids.add(new ExternalRecipe.FluidIn(tag, id, fluid.amount()));
            }
        }
        if (!machine.items().contains(items.size()) || !machine.fluids().contains(fluids.size())) {
            return null;
        }
        List<DeviceSpec> runners = DeviceSpec.runnersOf(recipe);
        Tier base = runners.isEmpty() ? Tier.T0 : runners.get(0).tier();
        DeviceProfile profile = DeviceProfile.standard(tier);
        List<ExternalRecipe.ItemOut> itemOutputs = new ArrayList<>();
        List<ExternalRecipe.FluidOut> fluidOutputs = new ArrayList<>();
        for (int i = 0; i < recipe.outputs().size(); i++) {
            ProcessOutput output = recipe.outputs().get(i);
            if (!output.producedAt(tier)) {
                continue;
            }
            if (output.isChanced() && !machine.chances()) {
                return null;
            }
            int purity = OutcomeResolver.atThreshold(recipe, i, profile, base);
            if (output.isFluid()) {
                if (purity != Purity.NONE && !machine.fluidPurity()) {
                    return null;
                }
                fluidOutputs.add(new ExternalRecipe.FluidOut(output.id(), output.amount(), purity));
            } else {
                itemOutputs.add(new ExternalRecipe.ItemOut(output.id(), output.amount(), output.chance(), purity));
            }
        }
        if (itemOutputs.isEmpty() && fluidOutputs.isEmpty()
            || !machine.itemOutputs().contains(itemOutputs.size()) || !machine.fluidOutputs().contains(fluidOutputs.size())) {
            return null;
        }
        int energyPerTick = recipe.energy() > 0 ? recipe.energy() : recipe.type().defaultEnergy();
        return new ExternalRecipe(recipe.type().id(), items, fluids, itemOutputs, fluidOutputs, recipe.time(), (long) energyPerTick * recipe.time(),
            recipe.required().keys().contains("temperature.heated"), recipe.required().keys().contains("temperature.cooling"));
    }
}
