package com.tonywww.foodindustrial.machine.bio;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.RecipeGen;
import com.tonywww.foodindustrial.registry.SharedTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The four input slots of the M30 culture bench take only the items of their tags (2026-11-11, user):
 * sample or culture, medium, donor sample, expression vector. Every recipe the bench can run must name
 * its inputs by entries of these tags, one slot each; the data run checks it.
 * <p>
 * The tags list other tags: every culture's tag, the natural samples ({@code foodindustrial:samples/<kind>},
 * vanilla items, game settings), the media and the donors of the recipes.
 */
public final class CultureBenchSlots {
    private static final String NS = FoodIndustrial.MOD_ID;
    public static final String SAMPLES = NS + ":culture_bench/samples";
    public static final String MEDIA = NS + ":culture_bench/media";
    public static final String DONORS = NS + ":culture_bench/donors";
    public static final String VECTORS = NS + ":culture_bench/vectors";
    /** The slot tags in slot order. */
    public static final List<String> SLOTS = List.of(SAMPLES, MEDIA, DONORS, VECTORS);

    /** Natural samples for isolation: tag {@code foodindustrial:samples/<kind>} and its items (game settings). */
    private static final Map<String, List<String>> NATURAL_SAMPLES = new LinkedHashMap<>();
    /** Direct entries of each slot tag. */
    private static final Map<String, Set<String>> ENTRIES = new LinkedHashMap<>();

    static {
        NATURAL_SAMPLES.put("soil", List.of("minecraft:dirt", "minecraft:coarse_dirt", "minecraft:rooted_dirt", "minecraft:podzol",
            "minecraft:mud"));
        NATURAL_SAMPLES.put("decaying_plant", List.of("minecraft:dead_bush", "minecraft:poisonous_potato"));
        NATURAL_SAMPLES.put("fruit", List.of("minecraft:apple", "minecraft:sweet_berries", "minecraft:glow_berries", "minecraft:melon_slice"));
        NATURAL_SAMPLES.put("mold", List.of(NS + ":wild_mold"));
        NATURAL_SAMPLES.put("dairy", List.of("minecraft:milk_bucket"));
        NATURAL_SAMPLES.put("honey", List.of("minecraft:honeycomb", "minecraft:honey_bottle"));
        NATURAL_SAMPLES.put("leaf", List.of("#minecraft:leaves"));
        NATURAL_SAMPLES.put("aquatic_plant", List.of("minecraft:lily_pad", "minecraft:seagrass"));
        NATURAL_SAMPLES.put("rotten_wood", List.of("minecraft:stripped_oak_log", "minecraft:mushroom_stem"));
        NATURAL_SAMPLES.put("straw", List.of("minecraft:hay_block", "minecraft:wheat"));
        NATURAL_SAMPLES.put("compost", List.of("minecraft:bone_meal", "minecraft:composter"));
        NATURAL_SAMPLES.put("marine", List.of("minecraft:mangrove_propagule", "minecraft:mangrove_roots"));
        NATURAL_SAMPLES.put("rotten_fat", List.of("minecraft:rotten_flesh"));
        // I30: raw meat (E. coli) and beetroot (Protaminobacter), game settings
        NATURAL_SAMPLES.put("raw_meat", List.of("minecraft:beef", "minecraft:porkchop", "minecraft:chicken", "minecraft:mutton",
            "minecraft:rabbit"));
        NATURAL_SAMPLES.put("beetroot", List.of("minecraft:beetroot"));
        NATURAL_SAMPLES.put("mushroom", List.of("minecraft:brown_mushroom"));  // edible fungus tissue (I31)
        for (String slot : SLOTS) {
            ENTRIES.put(slot, new LinkedHashSet<>());
        }
        NATURAL_SAMPLES.forEach((kind, items) -> {
            items.forEach(item -> SharedTags.item(NS + ":samples/" + kind, item));
            entry(SAMPLES, "#" + NS + ":samples/" + kind);
        });
        for (ModMaterials.Entry material : ModMaterials.all()) {
            if (material.def().category().equals("culture")) {
                entry(SAMPLES, "#" + NS + ":" + material.def().tag());
            }
        }
        entry(MEDIA, "#" + NS + ":chemicals/agar");
        entry(MEDIA, "#" + NS + ":chemicals/glucose");
        for (String donor : List.of("abomasum", "pancreas", "liver", "gastric_mucosa", "raw_fish", "red_algae_carrageenan")) {
            entry(DONORS, "#" + NS + ":materials/" + donor);
        }
        // I30 (additives/15 P94): environmental samples and a culture as gene donors
        for (String sample : List.of("soil", "compost", "straw", "leaf", "aquatic_plant")) {
            entry(DONORS, "#" + NS + ":samples/" + sample);
        }
        entry(DONORS, "#" + NS + ":cultures/aspergillus_niger");
        entry(VECTORS, "#" + NS + ":cultures/expression_vector");
    }

    private CultureBenchSlots() {
    }

    private static void entry(String slot, String value) {
        ENTRIES.get(slot).add(value);
        SharedTags.item(slot, value);
    }

    /** Loads the tags and registers the data-run check. */
    static void bootstrap() {
        RecipeGen.addSetCheck(CultureBenchSlots::check);
    }

    /** Problems of the recipes the culture bench can run whose inputs do not fit its slot tags. */
    static List<String> check(Map<String, ProcessRecipe> recipes) {
        DeviceSpec bench = BioMachines.CULTURE_BENCH.spec();
        List<String> problems = new ArrayList<>();
        recipes.forEach((path, recipe) -> {
            if (!bench.canRun(recipe)) {
                return;
            }
            List<Set<Integer>> fits = new ArrayList<>();
            boolean placed = true;
            for (ProcessInput input : recipe.inputs()) {
                if (!(input instanceof ProcessInput.ItemInput item)) {
                    continue;
                }
                List<String> refs = references(PlatformIngredients.references(PlatformIngredients.unwrap(item.ingredient())));
                Set<Integer> slots = new LinkedHashSet<>();
                for (int slot = 0; slot < SLOTS.size(); slot++) {
                    if (!refs.isEmpty() && ENTRIES.get(SLOTS.get(slot)).containsAll(refs)) {
                        slots.add(slot);
                    }
                }
                if (slots.isEmpty()) {
                    problems.add(path + ": culture bench input " + refs + " is in none of its slot tags");
                    placed = false;
                }
                fits.add(slots);
            }
            if (placed && !assign(fits, 0, new boolean[SLOTS.size()])) {
                problems.add(path + ": culture bench inputs need more than one item in the same slot");
            }
        });
        return problems;
    }

    private static boolean assign(List<Set<Integer>> fits, int index, boolean[] used) {
        if (index == fits.size()) {
            return true;
        }
        for (int slot : fits.get(index)) {
            if (!used[slot]) {
                used[slot] = true;
                if (assign(fits, index + 1, used)) {
                    return true;
                }
                used[slot] = false;
            }
        }
        return false;
    }

    /** Tags ({@code #id}) and items an ingredient's JSON names. */
    private static List<String> references(@Nullable JsonElement json) {
        List<String> refs = new ArrayList<>();
        if (json == null) {
            return refs;
        }
        if (json instanceof JsonArray array) {
            array.forEach(element -> refs.addAll(references(element)));
        } else if (json instanceof JsonObject object) {
            if (object.has("tag")) {
                refs.add("#" + object.get("tag").getAsString());
            } else if (object.has("item")) {
                refs.add(object.get("item").getAsString());
            }
        } else if (json.isJsonPrimitive()) {
            String value = json.getAsString();
            refs.add(value);
        }
        return refs;
    }
}
