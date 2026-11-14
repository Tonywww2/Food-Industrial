package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
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
 * not take part are assumed available. {@code purity_range} counts with its high end. Materials that no
 * recipe produces (mined, dropped, or from other mods) count at their external default from T0 on
 * (2026-11-11, I18).
 * <p>
 * A second table gives the earliest tier at which each product can be made at all, purity aside: a
 * recipe can run from the lowest tier of its devices once every item and fluid input it names (by its
 * material) and its catalysts can be had; inputs no recipe produces are assumed available.
 */
public final class PurityReport {
    private static final int MAX_ROUNDS = 1000;

    private PurityReport() {
    }

    /**
     * Tags (path in this mod's namespace) of the raw materials that have no source yet (additives/08 §C, I30),
     * exported by {@code tools/materials/export_materials.py} into {@code materials.json}.
     */
    public static Set<String> pendingSources() {
        Set<String> tags = new java.util.TreeSet<>();
        try (InputStream in = PurityReport.class.getResourceAsStream("/foodindustrial/materials.json")) {
            if (in == null) {
                return tags;
            }
            JsonElement root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            JsonElement list = root.getAsJsonObject().get("pending_sources");
            if (list != null) {
                list.getAsJsonArray().forEach(element -> tags.add(element.getAsString()));
            }
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Cannot read materials.json", exception);
        }
        return tags;
    }

    /**
     * Recipes that take a raw material with no source yet, with the tags they miss (I30): the report lists them
     * apart and leaves them out of the tables, so the stages are checked on the routes that work today.
     */
    public static Map<String, List<String>> pendingRoutes(List<PlatformRecipes.Loaded<ProcessRecipe>> recipes) {
        Set<String> pending = pendingSources();
        Map<String, List<String>> routes = new TreeMap<>();
        for (PlatformRecipes.Loaded<ProcessRecipe> loaded : recipes) {
            List<String> missing = new ArrayList<>();
            for (ProcessInput input : loaded.data().inputs()) {
                missing.addAll(missingOf(input, pending));
            }
            for (Catalyst catalyst : loaded.data().catalysts()) {
                String text = String.valueOf(PlatformIngredients.references(PlatformIngredients.unwrap(catalyst.ingredient())));
                pending.stream().filter(tag -> text.contains(FoodIndustrial.MOD_ID + ":" + tag + "\"")).forEach(missing::add);
            }
            if (!missing.isEmpty()) {
                routes.put(loaded.id(), missing);
            }
        }
        return routes;
    }

    private static List<String> missingOf(ProcessInput input, Set<String> pending) {
        String text;
        if (input instanceof ProcessInput.ItemInput item) {
            text = String.valueOf(PlatformIngredients.references(PlatformIngredients.unwrap(item.ingredient())));
        } else if (input instanceof ProcessInput.FluidInput fluid) {
            text = fluid.fluid().tag().map(tag -> "\"" + PlatformTags.tagId(tag) + "\"").orElse("");
        } else {
            return List.of();
        }
        List<String> missing = new ArrayList<>();
        for (String tag : pending) {
            if (text.contains(FoodIndustrial.MOD_ID + ":" + tag + "\"")) {
                missing.add(tag);
            }
        }
        return missing;
    }

    /** The recipes whose raw materials all have a source. */
    private static List<PlatformRecipes.Loaded<ProcessRecipe>> available(List<PlatformRecipes.Loaded<ProcessRecipe>> recipes) {
        Set<String> pending = pendingRoutes(recipes).keySet();
        return recipes.stream().filter(loaded -> !pending.contains(loaded.id())).toList();
    }

    /** Best purity byte per material id at each tier limit (index = tier ordinal); routes with sourceless raws left out. */
    public static Map<String, int[]> compute(List<PlatformRecipes.Loaded<ProcessRecipe>> all, List<DeviceSpec> devices) {
        List<PlatformRecipes.Loaded<ProcessRecipe>> recipes = available(all);
        Map<String, int[]> result = new TreeMap<>();
        for (Tier limit : Tier.values()) {
            Map<String, Integer> best = fixpoint(recipes, all, devices, limit);
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

    private static Map<String, Integer> fixpoint(List<PlatformRecipes.Loaded<ProcessRecipe>> recipes,
                                                 List<PlatformRecipes.Loaded<ProcessRecipe>> all, List<DeviceSpec> devices, Tier limit) {
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
        // seeds from all recipes: a material made only by a sourceless route is not an outside material
        Map<String, Integer> best = new HashMap<>(externalSeeds(all));
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

    /** Materials some recipe takes but none produces, at their external default (2026-11-11). */
    private static Map<String, Integer> externalSeeds(List<PlatformRecipes.Loaded<ProcessRecipe>> recipes) {
        java.util.Set<String> produced = madeFromOthers(recipes);
        Map<String, Integer> seeds = new HashMap<>();
        for (PlatformRecipes.Loaded<ProcessRecipe> loaded : recipes) {
            for (ProcessInput input : loaded.data().inputs()) {
                String material = input.material();
                if (material == null || produced.contains(material)) {
                    continue;
                }
                PurityData.Material data = PurityData.current().material(material);
                if (data != null && data.missing() != PurityData.NONE) {
                    seeds.put(material, data.missing());
                }
            }
        }
        return seeds;
    }

    /**
     * Earliest tier at which each material some recipe produces can be made (index of {@link Tier}), purity
     * aside; inputs and catalysts that no recipe produces count as available.
     */
    public static Map<String, Tier> earliest(List<PlatformRecipes.Loaded<ProcessRecipe>> all, List<DeviceSpec> devices) {
        List<PlatformRecipes.Loaded<ProcessRecipe>> recipes = available(all);
        java.util.Set<String> produced = new java.util.HashSet<>();
        List<ProcessRecipe> list = new ArrayList<>();
        List<Tier> lowest = new ArrayList<>();
        for (PlatformRecipes.Loaded<ProcessRecipe> loaded : recipes) {
            Tier base = null;
            for (DeviceSpec device : devices) {
                if (device.canRun(loaded.data()) && (base == null || device.tier().ordinal() < base.ordinal())) {
                    base = device.tier();
                }
            }
            if (base != null) {
                list.add(loaded.data());
                lowest.add(base);
            }
        }
        produced.addAll(madeFromOthers(all));
        Map<String, Tier> earliest = new TreeMap<>();
        boolean changed = true;
        for (int round = 0; round < MAX_ROUNDS && changed; round++) {
            changed = false;
            for (int r = 0; r < list.size(); r++) {
                ProcessRecipe recipe = list.get(r);
                Tier tier = lowest.get(r);
                boolean ready = true;
                for (ProcessInput input : recipe.inputs()) {
                    for (String needed : keys(input)) {
                        if (produced.contains(needed)) {
                            Tier at = earliest.get(needed);
                            if (at == null) {
                                ready = false;
                            } else if (at.ordinal() > tier.ordinal()) {
                                tier = at;
                            }
                        }
                    }
                }
                for (Catalyst catalyst : recipe.catalysts()) {
                    for (String needed : catalystKeys(catalyst)) {
                        if (produced.contains(needed)) {
                            Tier at = earliest.get(needed);
                            if (at == null) {
                                ready = false;
                            } else if (at.ordinal() > tier.ordinal()) {
                                tier = at;
                            }
                        }
                    }
                }
                if (!ready) {
                    continue;
                }
                for (ProcessOutput output : recipe.outputs()) {
                    Tier at = output.minTier() != null && output.minTier().ordinal() > tier.ordinal() ? output.minTier() : tier;
                    Tier old = earliest.get(key(output));
                    if (old == null || at.ordinal() < old.ordinal()) {
                        earliest.put(key(output), at);
                        changed = true;
                    }
                }
            }
        }
        return earliest;
    }

    /**
     * Ids that some recipe makes from other inputs; a material only recrystallized or recycled by recipes
     * (tartar scraped from barrels, mined salts) counts as obtained elsewhere.
     */
    private static java.util.Set<String> madeFromOthers(List<PlatformRecipes.Loaded<ProcessRecipe>> recipes) {
        java.util.Set<String> made = new java.util.HashSet<>();
        for (PlatformRecipes.Loaded<ProcessRecipe> loaded : recipes) {
            java.util.Set<String> inputs = new java.util.HashSet<>();
            loaded.data().inputs().forEach(input -> inputs.addAll(keys(input)));
            for (ProcessOutput output : loaded.data().outputs()) {
                if (!inputs.contains(key(output))) {
                    made.add(key(output));
                    // materialOf builds a stack, which 26.1 cannot do for unbound items during data generation
                    if (output.bearsPurity() && output.material() != null) {
                        made.add(output.material());
                    }
                }
            }
        }
        return made;
    }

    private static String key(ProcessOutput output) {
        return output.id();
    }

    /** Ids an input may stand for: its material's id, or nothing to wait for. */
    private static List<String> keys(ProcessInput input) {
        String material = input.material() != null ? input.material() : input.plainMaterial();
        return material == null ? List.of() : List.of(material);
    }

    private static List<String> catalystKeys(Catalyst catalyst) {
        String material = com.tonywww.foodindustrial.core.purity.Purity.materialHint(
            com.tonywww.foodindustrial.platform.recipe.PlatformIngredients.references(
                com.tonywww.foodindustrial.platform.recipe.PlatformIngredients.unwrap(catalyst.ingredient())), false);
        return material == null ? List.of() : List.of(material);
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
                device.ordinal() - base.ordinal(), device == Tier.T0, false);
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

    /** The report as Markdown, with the earliest tiers and the routes whose raw materials have no source yet. */
    public static String markdown(Map<String, int[]> table, Map<String, Tier> earliest, Map<String, List<String>> pending) {
        StringBuilder text = new StringBuilder(markdown(table, earliest));
        text.append("\n## 原料暂无来源\n\n");
        text.append("这些配方用到暂时没有获取途径的原料（additives/08 §C 标“暂无来源”），不计入上面两张表（I30）。\n\n");
        text.append("| 配方 | 缺的原料 |\n| --- | --- |\n");
        pending.forEach((id, missing) -> text.append("| ").append(id).append(" | ").append(String.join("、", missing)).append(" |\n"));
        return text.toString();
    }

    /** The report as Markdown, with the earliest tiers. */
    public static String markdown(Map<String, int[]> table, Map<String, Tier> earliest) {
        Map<String, String> names = new HashMap<>();
        for (MaterialDef def : MaterialTable.load()) {
            names.put(FoodIndustrial.MOD_ID + ":" + def.id(), def.nameZh());
        }
        StringBuilder text = new StringBuilder(markdown(table));
        text.append("\n## 最早可做出的档次\n\n");
        text.append("不看纯度，只看配方能否在该档次的设备上做出（原料、催化剂都能得到）；没有配方产出的原料视为可得（矿物、原版或他模组物品）。\n\n");
        text.append("| 产物 | 名称 | 最早 |\n| --- | --- | --- |\n");
        earliest.forEach((id, tier) -> text.append("| ").append(id).append(" | ").append(names.getOrDefault(id, "")).append(" | ")
            .append(tier.name()).append(" |\n"));
        return text.toString();
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
