package com.tonywww.foodindustrial.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.PlatformWorldgen;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Configured and placed features and loader biome modifiers for the world blocks. The numbers are the
 * drafts in additives/08 §C “世界生成” (2026-11-04, under review); fallback features carry the
 * {@code foodindustrial:enabled} filter so they only generate while the fallback is active.
 */
public final class ModWorldgen {
    /** Configured feature of the rubber tree, grown by the sapling. */
    public static final String RUBBER_TREE = "rubber_tree";

    private static final String NS = FoodIndustrial.MOD_ID;
    private static final List<String> DESERTS = List.of("minecraft:desert", "minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands");
    private static final List<String> MOUNTAINS = List.of("minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:frozen_peaks",
        "minecraft:snowy_slopes", "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest");
    private static final String OVERWORLD = "#minecraft:is_overworld";
    private static final List<String> JUNGLES = List.of("minecraft:jungle", "minecraft:sparse_jungle", "minecraft:bamboo_jungle");
    private static final String ORES = "underground_ores";
    private static final String VEGETATION = "vegetal_decoration";

    private ModWorldgen() {
    }

    static void bootstrap() {
        PlatformDatagen.register("worldgen", ModWorldgen::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        ore(sink, ModWorldBlocks.GYPSUM_ORE.id(), 12, 8, 0, 80, DESERTS, null);
        ore(sink, ModWorldBlocks.ALUNITE_ORE.id(), 9, 6, 32, 160, MOUNTAINS, null);
        ore(sink, ModWorldBlocks.ROCK_SALT.id(), 24, 3, 0, 48, OVERWORLD, null);
        ore(sink, ModWorldBlocks.MAGNESITE_ORE.id(), 9, 4, 0, 64, OVERWORLD, null);
        ore(sink, ModWorldBlocks.APATITE_ORE.id(), 9, 4, 0, 64, OVERWORLD, Fallbacks.APATITE);
        ore(sink, ModWorldBlocks.SULFUR_ORE.id(), 7, 5, 0, 32, OVERWORLD, Fallbacks.SULFUR);
        crust(sink, ModWorldBlocks.TRONA_ORE.id(), 20, 6);
        crust(sink, ModWorldBlocks.NITRATINE.id(), 16, 10);

        List<String> plains = List.of("minecraft:plains", "minecraft:sunflower_plains");
        patch(sink, "wild_stevia", state("wild_stevia", null), 24, join(plains, List.of("minecraft:savanna", "minecraft:savanna_plateau")), null);
        patch(sink, "wild_mint", state("wild_mint", null), 24, join(plains, List.of("minecraft:meadow")), null);
        patch(sink, "wild_rosemary", state("wild_rosemary", null), 24,
            List.of("minecraft:meadow", "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest"), null);
        patch(sink, "wild_indigo_plant", state("wild_indigo_plant", null), 16, List.of("minecraft:river", "minecraft:swamp"), null);
        patch(sink, "wild_rice", state("wild_rice", null), 16, List.of("minecraft:swamp", "minecraft:mangrove_swamp", "minecraft:river"), Fallbacks.RICE);
        patch(sink, "gardenia_bush", state("gardenia_bush", "age", "3"), 32, JUNGLES, null);
        vanillaPods(sink);
        seaPlant(sink, "gelidium", 8, List.of("minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"));
        seaPlant(sink, "eucheuma", 6, List.of("minecraft:warm_ocean"));
        rubberTree(sink);
    }

    // ------------------------------------------------------------------ features

    private static void ore(PlatformDatagen.Sink sink, String block, int size, int count, int minY, int maxY, Object biomes, @Nullable Fallbacks fallback) {
        JsonObject config = new JsonObject();
        config.addProperty("size", size);
        config.addProperty("discard_chance_on_air_exposure", 0.0);
        config.add("targets", array(target("minecraft:stone_ore_replaceables", block)));
        configured(sink, block, "minecraft:ore", config);
        JsonObject height = new JsonObject();
        height.addProperty("type", "minecraft:uniform");
        height.add("min_inclusive", absolute(minY));
        height.add("max_inclusive", absolute(maxY));
        JsonObject range = placement("minecraft:height_range");
        range.add("height", height);
        JsonArray placements = fallbackFilter(fallback);
        placements.add(count(count));
        placements.add(placement("minecraft:in_square"));
        placements.add(range);
        placements.add(placement("minecraft:biome"));
        placed(sink, block, block, placements, biomes, ORES);
    }

    /** A surface crust: an ore blob at the surface that only replaces sand and terracotta. */
    private static void crust(PlatformDatagen.Sink sink, String block, int size, int rarity) {
        JsonObject config = new JsonObject();
        config.addProperty("size", size);
        config.addProperty("discard_chance_on_air_exposure", 0.0);
        config.add("targets", array(target("minecraft:sand", block), target("minecraft:terracotta", block)));
        configured(sink, block, "minecraft:ore", config);
        JsonArray placements = new JsonArray();
        placements.add(rarity(rarity));
        placements.add(placement("minecraft:in_square"));
        placements.add(heightmap("WORLD_SURFACE_WG"));
        placements.add(placement("minecraft:biome"));
        placed(sink, block, block, placements, DESERTS, ORES);
    }

    /** A patch of plants on the surface, like vanilla's berry bushes (32 tries, ±7 across, ±3 up). */
    private static void patch(PlatformDatagen.Sink sink, String name, JsonObject state, int rarity, Object biomes, @Nullable Fallbacks fallback) {
        simpleBlock(sink, name, state);
        JsonArray placements = fallbackFilter(fallback);
        placements.add(rarity(rarity));
        placements.add(placement("minecraft:in_square"));
        placements.add(heightmap("WORLD_SURFACE_WG"));
        placements.add(placement("minecraft:biome"));
        placements.add(count(32));
        placements.add(randomOffset(PlatformDatagen.uniformInt(-7, 7), PlatformDatagen.uniformInt(-3, 3)));
        placements.add(predicateFilter(allOf(matchingBlocks("minecraft:air"), wouldSurvive(state))));
        placed(sink, name, name, placements, biomes, VEGETATION);
    }

    /**
     * Vanilla pods on the sides of jungle logs, one feature per facing (the pod faces its log). Heights
     * count from the ground under the leaves, so the tries hit the trunks; a pod may replace a trunk vine.
     */
    private static void vanillaPods(PlatformDatagen.Sink sink) {
        JsonArray features = new JsonArray();
        for (String facing : List.of("north", "east", "south", "west")) {
            String name = "vanilla_" + facing;
            JsonObject state = state("vanilla", "age", "2");
            state.getAsJsonObject("Properties").addProperty("facing", facing);
            simpleBlock(sink, name, state);
            JsonArray placements = new JsonArray();
            placements.add(count(16));
            placements.add(placement("minecraft:in_square"));
            placements.add(heightmap("MOTION_BLOCKING_NO_LEAVES"));
            placements.add(randomOffset(new JsonPrimitive(0), PlatformDatagen.uniformInt(1, 8)));
            placements.add(placement("minecraft:biome"));
            JsonObject freeOrVine = new JsonObject();
            freeOrVine.addProperty("type", "minecraft:matching_blocks");
            JsonArray replaceable = new JsonArray();
            replaceable.add("minecraft:air");
            replaceable.add("minecraft:vine");
            freeOrVine.add("blocks", replaceable);
            placements.add(predicateFilter(allOf(freeOrVine, wouldSurvive(state))));
            placedFeature(sink, name, name, placements);
            features.add(NS + ":" + name);
        }
        biomeModifier(sink, "vanilla", JUNGLES, features, VEGETATION);
    }

    private static void seaPlant(PlatformDatagen.Sink sink, String block, int count, Object biomes) {
        JsonObject state = state(block, null);
        simpleBlock(sink, block, state);
        JsonArray placements = new JsonArray();
        placements.add(placement("minecraft:in_square"));
        placements.add(heightmap("OCEAN_FLOOR_WG"));
        placements.add(count(count));
        placements.add(placement("minecraft:biome"));
        placements.add(predicateFilter(allOf(matchingBlocks("minecraft:water"), wouldSurvive(state))));
        placed(sink, block, block, placements, biomes, VEGETATION);
    }

    /** Straight trunk of 5–7 rubber logs with a blob of leaves (radius 2), like vanilla oak but taller. */
    private static void rubberTree(PlatformDatagen.Sink sink) {
        JsonObject config = new JsonObject();
        JsonObject log = state("rubber_log", "axis", "y");
        config.add("trunk_provider", simpleProvider(log));
        JsonObject trunk = new JsonObject();
        trunk.addProperty("type", "minecraft:straight_trunk_placer");
        trunk.addProperty("base_height", 5);
        trunk.addProperty("height_rand_a", 2);
        trunk.addProperty("height_rand_b", 0);
        config.add("trunk_placer", trunk);
        JsonObject leaves = state("rubber_leaves", "distance", "7");
        leaves.getAsJsonObject("Properties").addProperty("persistent", "false");
        leaves.getAsJsonObject("Properties").addProperty("waterlogged", "false");
        config.add("foliage_provider", simpleProvider(leaves));
        JsonObject foliage = new JsonObject();
        foliage.addProperty("type", "minecraft:blob_foliage_placer");
        foliage.addProperty("radius", 2);
        foliage.addProperty("offset", 0);
        foliage.addProperty("height", 3);
        config.add("foliage_placer", foliage);
        JsonObject size = new JsonObject();
        size.addProperty("type", "minecraft:two_layers_feature_size");
        size.addProperty("limit", 1);
        size.addProperty("lower_size", 0);
        size.addProperty("upper_size", 1);
        config.add("minimum_size", size);
        config.add("decorators", new JsonArray());
        config.addProperty("ignore_vines", true);
        PlatformDatagen.treeBelowTrunk(config);
        configured(sink, RUBBER_TREE, "minecraft:tree", config);
        JsonArray placements = new JsonArray();
        placements.add(rarity(3));
        placements.add(placement("minecraft:in_square"));
        JsonObject water = placement("minecraft:surface_water_depth_filter");
        water.addProperty("max_water_depth", 0);
        placements.add(water);
        placements.add(heightmap("OCEAN_FLOOR"));
        placements.add(predicateFilter(wouldSurvive(state("rubber_sapling", "stage", "0"))));
        placements.add(placement("minecraft:biome"));
        placed(sink, RUBBER_TREE, RUBBER_TREE, placements, List.of("minecraft:jungle", "minecraft:sparse_jungle"), VEGETATION);
    }

    // ------------------------------------------------------------------ JSON helpers

    private static void configured(PlatformDatagen.Sink sink, String name, String type, JsonObject config) {
        JsonObject feature = new JsonObject();
        feature.addProperty("type", type);
        feature.add("config", config);
        sink.json(PlatformDatagen.Kind.WORLDGEN, NS, "configured_feature/" + name, feature);
    }

    private static void simpleBlock(PlatformDatagen.Sink sink, String name, JsonObject state) {
        JsonObject config = new JsonObject();
        config.add("to_place", simpleProvider(state));
        configured(sink, name, "minecraft:simple_block", config);
    }

    private static void placed(PlatformDatagen.Sink sink, String name, String feature, JsonArray placements, Object biomes, String step) {
        placedFeature(sink, name, feature, placements);
        biomeModifier(sink, name, biomes, new JsonPrimitive(NS + ":" + name), step);
    }

    private static void placedFeature(PlatformDatagen.Sink sink, String name, String feature, JsonArray placements) {
        JsonObject placed = new JsonObject();
        placed.addProperty("feature", NS + ":" + feature);
        placed.add("placement", placements);
        sink.json(PlatformDatagen.Kind.WORLDGEN, NS, "placed_feature/" + name, placed);
    }

    private static void biomeModifier(PlatformDatagen.Sink sink, String name, Object biomes, JsonElement features, String step) {
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", PlatformDatagen.loaderNamespace() + ":add_features");
        if (biomes instanceof String tag) {
            modifier.addProperty("biomes", tag);
        } else {
            JsonArray list = new JsonArray();
            for (Object biome : (List<?>) biomes) {
                list.add((String) biome);
            }
            modifier.add("biomes", list);
        }
        modifier.add("features", features);
        modifier.addProperty("step", step);
        sink.json(PlatformDatagen.Kind.BIOME_MODIFIER, NS, "add_" + name, modifier);
    }

    private static JsonArray fallbackFilter(@Nullable Fallbacks fallback) {
        JsonArray placements = new JsonArray();
        if (fallback != null) {
            JsonObject filter = placement(PlatformWorldgen.ENABLED_FILTER);
            filter.addProperty("switch", fallback.switchName());
            placements.add(filter);
        }
        return placements;
    }

    private static JsonObject target(String tag, String block) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty("predicate_type", "minecraft:tag_match");
        predicate.addProperty("tag", tag);
        JsonObject target = new JsonObject();
        target.add("target", predicate);
        target.add("state", state(block, null));
        return target;
    }

    private static JsonObject state(String block, @Nullable String property, String... value) {
        JsonObject state = new JsonObject();
        state.addProperty("Name", NS + ":" + block);
        if (property != null) {
            JsonObject properties = new JsonObject();
            properties.addProperty(property, value[0]);
            state.add("Properties", properties);
        }
        return state;
    }

    private static JsonObject simpleProvider(JsonObject state) {
        JsonObject provider = new JsonObject();
        provider.addProperty("type", "minecraft:simple_state_provider");
        provider.add("state", state);
        return provider;
    }

    private static JsonObject placement(String type) {
        JsonObject placement = new JsonObject();
        placement.addProperty("type", type);
        return placement;
    }

    private static JsonObject count(int count) {
        JsonObject placement = placement("minecraft:count");
        placement.addProperty("count", count);
        return placement;
    }

    private static JsonObject rarity(int chance) {
        JsonObject placement = placement("minecraft:rarity_filter");
        placement.addProperty("chance", chance);
        return placement;
    }

    private static JsonObject heightmap(String type) {
        JsonObject placement = placement("minecraft:heightmap");
        placement.addProperty("heightmap", type);
        return placement;
    }

    private static JsonObject randomOffset(JsonElement xz, JsonElement y) {
        JsonObject placement = placement("minecraft:random_offset");
        placement.add("xz_spread", xz);
        placement.add("y_spread", y);
        return placement;
    }

    private static JsonObject predicateFilter(JsonObject predicate) {
        JsonObject placement = placement("minecraft:block_predicate_filter");
        placement.add("predicate", predicate);
        return placement;
    }

    private static JsonObject allOf(JsonObject... predicates) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty("type", "minecraft:all_of");
        predicate.add("predicates", array(predicates));
        return predicate;
    }

    private static JsonObject matchingBlocks(String block) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty("type", "minecraft:matching_blocks");
        predicate.addProperty("blocks", block);
        return predicate;
    }

    private static JsonObject wouldSurvive(JsonObject state) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty("type", "minecraft:would_survive");
        predicate.add("state", state);
        return predicate;
    }

    private static JsonObject absolute(int y) {
        JsonObject anchor = new JsonObject();
        anchor.addProperty("absolute", y);
        return anchor;
    }

    private static JsonArray array(JsonObject... elements) {
        JsonArray array = new JsonArray();
        for (JsonObject element : elements) {
            array.add(element);
        }
        return array;
    }

    private static List<String> join(List<String> first, List<String> second) {
        return java.util.stream.Stream.concat(first.stream(), second.stream()).toList();
    }
}
