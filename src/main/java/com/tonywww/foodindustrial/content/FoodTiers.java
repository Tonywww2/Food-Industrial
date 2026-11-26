package com.tonywww.foodindustrial.content;

import com.google.gson.JsonElement;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.food.FinishedFoods;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.material.tags.TagPlan;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.recipe.Catalyst;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.RecipeGen;
import com.tonywww.foodindustrial.registry.SharedTags;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The earliest tier each finished product can be made at (I47 on; the acceptance "每一项在设计写明的最早档次上能做出"):
 * {@code build/reports/foodindustrial/food_tiers.md}. A step's tier is the lowest device that runs it (the crafting
 * table counts as T0), raised to its inputs' and catalysts' tiers; an input is the best of what its tag holds. Items
 * and fluids no recipe makes are taken as obtainable at T0 (crops, vanilla, crafted tools); tags that only list other
 * mods' tags are obtainable with those mods; tags with nothing in them are not obtainable. Semi-finished items count
 * by their key.
 */
public final class FoodTiers {
    private static final String NS = FoodIndustrial.MOD_ID;
    private static final String EXTERNAL = "external";

    /** One step for the report: what it needs (each a list of alternatives) and what it makes, from its lowest device on. */
    record Step(String id, Tier base, List<List<String>> needs, List<String> makes) {
    }

    /** Crafting-table steps written by {@link Workbench} during the run. */
    private static final List<Step> WORKBENCH = new ArrayList<>();

    private FoodTiers() {
    }

    static void bootstrap() {
        RecipeGen.addFollower(FoodTiers::report);
    }

    static void workbench(String id, List<List<String>> needs, String makes) {
        WORKBENCH.add(new Step(id, Tier.T0, needs, List.of(makes)));
    }

    /** What an item ingredient stands for: item ids and {@code #tags}, read from the loader's form. */
    public static List<String> refs(Ingredient ingredient) {
        List<String> result = new ArrayList<>();
        collect(PlatformIngredients.references(PlatformIngredients.unwrap(ingredient)), result);
        return result;
    }

    private static void collect(@Nullable JsonElement json, List<String> into) {
        if (json == null || json.isJsonNull()) {
            return;
        }
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> collect(element, into));
        } else if (json.isJsonPrimitive()) {
            into.add(json.getAsString());
        } else if (json.isJsonObject()) {
            if (json.getAsJsonObject().has("tag")) {
                into.add("#" + json.getAsJsonObject().get("tag").getAsString());
            } else if (json.getAsJsonObject().has("item")) {
                into.add(json.getAsJsonObject().get("item").getAsString());
            }
        }
    }

    private static void report(PlatformDatagen.Sink sink, Map<String, ProcessRecipe> recipes) {
        Map<String, List<String>> itemTags = tags(false);
        Map<String, List<String>> fluidTags = tags(true);
        List<DeviceSpec> devices = DeviceSpec.all().stream().filter(device -> !device.id().contains(":dev_")).toList();
        List<Step> steps = new ArrayList<>(WORKBENCH);
        recipes.forEach((path, recipe) -> {
            Tier base = null;
            for (DeviceSpec device : devices) {
                if (device.canRun(recipe) && (base == null || device.tier().ordinal() < base.ordinal())) {
                    base = device.tier();
                }
            }
            if (base == null) {
                return;
            }
            List<List<String>> needs = new ArrayList<>();
            for (ProcessInput input : recipe.inputs()) {
                if (input instanceof ProcessInput.ItemInput item) {
                    needs.add(item.isSemi() ? List.of("semi:" + item.semi()) : expand(refs(item.ingredient()), itemTags));
                } else {
                    ProcessInput.FluidInput fluid = (ProcessInput.FluidInput) input;
                    needs.add(fluid.fluid().tag().map(tag -> expand(List.of("#" + PlatformTags.tagId(tag)), fluidTags))
                        .orElse(List.of(EXTERNAL)));
                }
            }
            for (Catalyst catalyst : recipe.catalysts()) {
                needs.add(expand(refs(catalyst.ingredient()), itemTags));
            }
            List<String> makes = new ArrayList<>();
            for (ProcessOutput output : recipe.outputs()) {
                makes.add(output.isSemi() ? "semi:" + output.semi() : output.id());
            }
            steps.add(new Step(NS + ":" + path, base, needs, makes));
        });
        // expand the crafting-table steps' tags as well
        List<Step> resolved = new ArrayList<>();
        for (Step step : steps) {
            List<List<String>> needs = new ArrayList<>();
            for (List<String> need : step.needs()) {
                if (need.size() == 1 && need.get(0).startsWith("semi:")) {
                    needs.add(need);
                } else if (need.size() == 1 && need.get(0).startsWith("fluid:")) {
                    // a fluid of the crafting table, in a container (I48)
                    needs.add(expand(List.of(need.get(0).substring("fluid:".length())), fluidTags));
                } else {
                    needs.add(expand(need, itemTags));
                }
            }
            resolved.add(new Step(step.id(), step.base(), needs, step.makes()));
        }
        Set<String> made = new HashSet<>();
        resolved.forEach(step -> made.addAll(step.makes()));
        Map<String, Tier> tier = new HashMap<>();
        Map<String, String> via = new HashMap<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Step step : resolved) {
                Tier at = step.base();
                boolean ready = true;
                for (List<String> need : step.needs()) {
                    Tier best = best(need, made, tier);
                    if (best == null) {
                        ready = false;
                        break;
                    }
                    at = at.ordinal() < best.ordinal() ? best : at;
                }
                if (!ready) {
                    continue;
                }
                for (String product : step.makes()) {
                    Tier known = tier.get(product);
                    if (known == null || at.ordinal() < known.ordinal()) {
                        tier.put(product, at);
                        via.put(product, step.id());
                        changed = true;
                    }
                }
            }
        }
        StringBuilder text = new StringBuilder("# 成品最早可做出的档次\n\n");
        text.append("每一步取能跑它的最低档设备（工作台算 T0），再升到各输入、催化剂的最早档次；输入按标签里最早可得的一项算。")
            .append("没有配方产出的物品与流体视为 T0 可得（作物、原版、合成的工具与容器）；只含他模组标签的视为装了他模组才有；")
            .append("空标签与还没有配方的成品视为无来源。\n\n| 成品 | 名称 | 最早 | 经由 |\n| --- | --- | --- | --- |\n");
        for (FinishedFoods.Food food : FinishedFoods.foods()) {
            String id = NS + ":" + food.id();
            if (!made.contains(id)) {
                continue;
            }
            Tier at = tier.get(id);
            text.append("| ").append(food.id()).append(" | ").append(food.zh()).append(" | ")
                .append(at == null ? "做不出" : at.name()).append(" | ").append(at == null ? missing(id, resolved, made, tier) : via.get(id))
                .append(" |\n");
        }
        // the flavourings (I48): one row per type, any dosage form
        text.append("\n## 香精\n\n| 香精 | 最早 | 经由 |\n| --- | --- | --- |\n");
        for (com.tonywww.foodindustrial.food.flavor.FlavorType type : com.tonywww.foodindustrial.food.flavor.FlavorType.values()) {
            String id = NS + ":" + type.itemId();
            Tier at = tier.get(id);
            text.append("| ").append(type.itemId()).append(" | ").append(at == null ? "做不出" : at.name()).append(" | ")
                .append(at == null ? (made.contains(id) ? missing(id, resolved, made, tier) : "（没有配方）") : via.get(id)).append(" |\n");
        }
        // the inputs of the product batches' steps that come late or not at all: where a product's tier comes from
        Map<String, Tier> inputs = new java.util.TreeMap<>();
        Set<String> missingInputs = new java.util.TreeSet<>();
        for (Step step : resolved) {
            if (!step.id().startsWith(NS + ":food")) {
                continue;
            }
            for (List<String> need : step.needs()) {
                for (String id : need) {
                    if (id.equals(EXTERNAL) || id.startsWith("semi:") || !made.contains(id)) {
                        continue;
                    }
                    Tier at = tier.get(id);
                    if (at == null) {
                        missingInputs.add(id);
                    } else if (at != Tier.T0) {
                        inputs.put(id, at);
                    }
                }
            }
        }
        text.append("\n## 成品线用到的非 T0 原料\n\n| 原料 | 最早 | 经由 |\n| --- | --- | --- |\n");
        inputs.forEach((id, at) -> text.append("| ").append(id).append(" | ").append(at.name()).append(" | ").append(via.get(id)).append(" |\n"));
        missingInputs.forEach(id -> text.append("| ").append(id).append(" | 做不出 | |\n"));
        text.append("\n## 成品线各步的设备\n\n能跑该步的设备，最低档在前（只列成品批次的配方）。\n\n| 配方 | 档 | 设备 |\n| --- | --- | --- |\n");
        recipes.forEach((path, recipe) -> {
            if (!path.startsWith("food")) {
                return;
            }
            List<String> runners = new ArrayList<>();
            Tier lowest = null;
            for (DeviceSpec device : DeviceSpec.runnersOf(recipe)) {
                if (device.id().contains(":dev_")) {
                    continue;
                }
                lowest = lowest == null ? device.tier() : lowest;
                runners.add(device.id().substring(device.id().indexOf(':') + 1));
            }
            text.append("| ").append(path).append(" | ").append(lowest == null ? "—" : lowest.name()).append(" | ")
                .append(String.join("、", runners)).append(" |\n");
        });
        sink.report("food_tiers.md", text.toString());
    }

    /** What keeps a product from being made: the inputs of its steps that have no source. */
    private static String missing(String product, List<Step> steps, Set<String> made, Map<String, Tier> tier) {
        Set<String> lacking = new java.util.TreeSet<>();
        for (Step step : steps) {
            if (!step.makes().contains(product)) {
                continue;
            }
            for (List<String> need : step.needs()) {
                if (best(need, made, tier) == null) {
                    lacking.add(need.isEmpty() ? "（空标签）" : String.join(" / ", need));
                }
            }
        }
        return String.join("；", lacking);
    }

    /**
     * The earliest tier of the best alternative; other mods' tags count only when the need holds nothing of this mod or
     * vanilla (the report is about this mod alone).
     */
    @Nullable
    private static Tier best(List<String> alternatives, Set<String> made, Map<String, Tier> tier) {
        boolean ownOnly = alternatives.stream().anyMatch(id -> !id.equals(EXTERNAL));
        Tier best = null;
        for (String id : alternatives) {
            if (id.equals(EXTERNAL) && ownOnly) {
                continue;
            }
            // a finished product no recipe makes yet comes with a later batch: not obtainable
            Tier at = id.equals(EXTERNAL) ? Tier.T0 : made.contains(id) ? tier.get(id) : products().contains(id) ? null : Tier.T0;
            if (at != null && (best == null || at.ordinal() < best.ordinal())) {
                best = at;
            }
        }
        return best;
    }

    private static Set<String> products;

    private static Set<String> products() {
        if (products == null) {
            products = new HashSet<>();
            FinishedFoods.foods().forEach(food -> products.add(NS + ":" + food.id()));
        }
        return products;
    }

    /** Item or fluid ids a list of ids and {@code #tags} stands for; other mods' tags become {@link #EXTERNAL}. */
    private static List<String> expand(List<String> refs, Map<String, List<String>> tags) {
        Set<String> result = new java.util.LinkedHashSet<>();
        for (String ref : refs) {
            expand(ref, tags, result, new HashSet<>());
        }
        return new ArrayList<>(result);
    }

    private static void expand(String ref, Map<String, List<String>> tags, Set<String> into, Set<String> seen) {
        if (!ref.startsWith("#")) {
            String path = ref.substring(ref.indexOf(':') + 1);
            if (ref.startsWith(NS + ":") && path.endsWith("_bucket")) {
                // a bucket of this mod's fluid stands for the fluid (the crafting table's filling steps)
                into.add(NS + ":" + path.substring(0, path.length() - "_bucket".length()));
            } else if (!path.startsWith("flowing_")) {
                into.add(ref);
            }
            return;
        }
        String tag = ref.substring(1);
        if (!seen.add(tag)) {
            return;
        }
        List<String> members = tags.get(tag);
        if (members == null) {
            if (!tag.startsWith(NS + ":")) {
                into.add(EXTERNAL);
            }
            return;
        }
        for (String member : members) {
            expand(member, tags, into, seen);
        }
    }

    /** The mod's own tags on this node: the material plan and the shared item tags. */
    private static Map<String, List<String>> tags(boolean fluid) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<TagPlan.Value>> entry : ModTags.planned(fluid).entrySet()) {
            List<String> ids = new ArrayList<>();
            entry.getValue().forEach(value -> ids.add(value.id()));
            result.put(entry.getKey(), ids);
        }
        if (!fluid) {
            SharedTags.items().forEach((tag, values) -> result.computeIfAbsent(tag, key -> new ArrayList<>()).addAll(values));
            // the knives tag is written by the world interactions (H15): the flint knife and the common knives (I49: R26 steps)
            result.computeIfAbsent(com.tonywww.foodindustrial.world.interaction.WorldInteractions.KNIVES, key -> new ArrayList<>())
                .add(NS + ":flint_knife");
        }
        return result;
    }
}
