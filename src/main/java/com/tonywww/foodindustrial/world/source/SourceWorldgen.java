package com.tonywww.foodindustrial.world.source;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * World generation of the I44 plants: a tree feature per tree (grown by its sapling too), a patch per bush, wild herb
 * and wild crop, and one biome modifier per biome group listing them. Trees spawn with leaves at mixed ages, bushes
 * and herbs grown. Rarities are drafts (I44 待审): trees 1 in 8 chunks of their biomes, bushes 1 in 32, herbs 1 in 24.
 */
final class SourceWorldgen {
    private static final String NS = FoodIndustrial.MOD_ID;
    private static final String VEGETATION = "vegetal_decoration";

    private SourceWorldgen() {
    }

    static void bootstrap() {
        PlatformDatagen.register("world source features", SourceWorldgen::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        SourceTable.Data data = SourceTable.get();
        Map<String, List<String>> byGroup = new LinkedHashMap<>();
        for (SourceBlocks.TreeBlocks tree : SourceBlocks.trees()) {
            SourceTable.Tree t = tree.tree();
            String name;
            if (tree.bush() != null) {
                name = t.id() + "_bush";
                patch(sink, name, state(name, "age", "3"), t.rarity());
            } else {
                name = SourceBlocks.treeFeature(t);
                tree(sink, t);
            }
            byGroup.computeIfAbsent(t.biome(), key -> new ArrayList<>()).add(name);
        }
        for (SourceBlocks.WildBlocks wild : SourceBlocks.wild()) {
            String name = "wild_" + wild.wild().id();
            patch(sink, name, state(name, "age", "3"), wild.wild().rarity());
            byGroup.computeIfAbsent(wild.wild().biome(), key -> new ArrayList<>()).add(name);
        }
        for (SourceBlocks.CropBlocks crop : SourceBlocks.crops()) {
            if (crop.wild() != null) {
                String name = "wild_" + crop.crop().id();
                patch(sink, name, state(name, null, null), 16);
                byGroup.computeIfAbsent(crop.crop().wild(), key -> new ArrayList<>()).add(name);
            }
        }
        nests(sink);
        byGroup.forEach((group, features) -> {
            JsonArray list = new JsonArray();
            features.forEach(feature -> list.add(NS + ":" + feature));
            biomeModifier(sink, "sources_" + group, data.biomes().get(group).biomes(), list);
        });
    }

    /** A tree: vanilla trunk or own log; oak-like blob, spruce-like cone (conifer) or acacia-like fork (palm). */
    private static void tree(PlatformDatagen.Sink sink, SourceTable.Tree tree) {
        JsonObject config = new JsonObject();
        String log = tree.ownLog() ? NS + ":" + tree.id() + "_log" : "minecraft:" + tree.trunk() + "_log";
        JsonObject trunkState = new JsonObject();
        trunkState.addProperty("Name", log);
        JsonObject axis = new JsonObject();
        axis.addProperty("axis", "y");
        trunkState.add("Properties", axis);
        config.add("trunk_provider", simpleProvider(trunkState));
        JsonObject trunk = new JsonObject();
        JsonObject foliage = new JsonObject();
        JsonObject size = new JsonObject();
        size.addProperty("type", "minecraft:two_layers_feature_size");
        size.addProperty("lower_size", 0);
        switch (tree.form()) {
            case "conifer" -> {
                trunk.addProperty("type", "minecraft:straight_trunk_placer");
                trunk.addProperty("base_height", 6);
                trunk.addProperty("height_rand_a", 2);
                trunk.addProperty("height_rand_b", 1);
                foliage.addProperty("type", "minecraft:spruce_foliage_placer");
                foliage.add("radius", PlatformDatagen.uniformInt(2, 3));
                foliage.add("offset", PlatformDatagen.uniformInt(0, 2));
                foliage.add("trunk_height", PlatformDatagen.uniformInt(1, 2));
                size.addProperty("limit", 2);
                size.addProperty("upper_size", 2);
            }
            case "palm" -> {
                trunk.addProperty("type", "minecraft:forking_trunk_placer");
                trunk.addProperty("base_height", 5);
                trunk.addProperty("height_rand_a", 2);
                trunk.addProperty("height_rand_b", 2);
                foliage.addProperty("type", "minecraft:acacia_foliage_placer");
                foliage.addProperty("radius", 2);
                foliage.addProperty("offset", 0);
                size.addProperty("limit", 1);
                size.addProperty("upper_size", 2);
            }
            default -> {
                trunk.addProperty("type", "minecraft:straight_trunk_placer");
                trunk.addProperty("base_height", 4);
                trunk.addProperty("height_rand_a", 2);
                trunk.addProperty("height_rand_b", 0);
                foliage.addProperty("type", "minecraft:blob_foliage_placer");
                foliage.addProperty("radius", 2);
                foliage.addProperty("offset", 0);
                foliage.addProperty("height", 3);
                size.addProperty("limit", 1);
                size.addProperty("upper_size", 1);
            }
        }
        config.add("trunk_placer", trunk);
        config.add("foliage_placer", foliage);
        config.add("minimum_size", size);
        // leaves at mixed ages, so some trees are found in flower or fruit
        JsonArray entries = new JsonArray();
        int[][] weights = {{0, 3}, {1, 1}, {2, 1}, {3, 1}};
        for (int[] weight : weights) {
            JsonObject leaves = state(tree.id() + "_leaves", "age", String.valueOf(weight[0]));
            JsonObject properties = leaves.getAsJsonObject("Properties");
            properties.addProperty("distance", "7");
            properties.addProperty("persistent", "false");
            properties.addProperty("waterlogged", "false");
            JsonObject entry = new JsonObject();
            entry.addProperty("weight", weight[1]);
            entry.add("data", leaves);
            entries.add(entry);
        }
        JsonObject weighted = new JsonObject();
        weighted.addProperty("type", "minecraft:weighted_state_provider");
        weighted.add("entries", entries);
        config.add("foliage_provider", weighted);
        config.add("decorators", new JsonArray());
        config.addProperty("ignore_vines", true);
        PlatformDatagen.treeBelowTrunk(config);
        String name = SourceBlocks.treeFeature(tree);
        configured(sink, name, "minecraft:tree", config);
        JsonArray placements = new JsonArray();
        placements.add(rarity(tree.rarity()));
        placements.add(placement("minecraft:in_square"));
        JsonObject water = placement("minecraft:surface_water_depth_filter");
        water.addProperty("max_water_depth", 0);
        placements.add(water);
        placements.add(heightmap("OCEAN_FLOOR"));
        placements.add(predicateFilter(wouldSurvive(state(tree.id() + "_sapling", "stage", "0"))));
        placements.add(placement("minecraft:biome"));
        placedFeature(sink, name, placements);
    }

    /** Swiftlet nests on rock faces in mountains, Y 64–192: one feature per facing (the facing is the wall). */
    private static void nests(PlatformDatagen.Sink sink) {
        JsonArray features = new JsonArray();
        for (String facing : List.of("north", "east", "south", "west")) {
            String name = SourceBlocks.NEST_ID + "_" + facing;
            JsonObject state = state(SourceBlocks.NEST_ID, "facing", facing);
            JsonObject config = new JsonObject();
            config.add("to_place", simpleProvider(state));
            configured(sink, name, "minecraft:simple_block", config);
            JsonArray placements = new JsonArray();
            placements.add(count(24));
            placements.add(placement("minecraft:in_square"));
            JsonObject height = new JsonObject();
            height.addProperty("type", "minecraft:uniform");
            JsonObject min = new JsonObject();
            min.addProperty("absolute", 64);
            JsonObject max = new JsonObject();
            max.addProperty("absolute", 192);
            height.add("min_inclusive", min);
            height.add("max_inclusive", max);
            JsonObject range = placement("minecraft:height_range");
            range.add("height", height);
            placements.add(range);
            placements.add(placement("minecraft:biome"));
            JsonObject air = new JsonObject();
            air.addProperty("type", "minecraft:matching_blocks");
            air.addProperty("blocks", "minecraft:air");
            JsonObject allOf = new JsonObject();
            allOf.addProperty("type", "minecraft:all_of");
            JsonArray predicates = new JsonArray();
            predicates.add(air);
            predicates.add(wouldSurvive(state));
            allOf.add("predicates", predicates);
            placements.add(predicateFilter(allOf));
            placedFeature(sink, name, placements);
            features.add(NS + ":" + name);
        }
        biomeModifier(sink, "sources_nests", List.of("minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:stony_peaks",
            "minecraft:jagged_peaks", "minecraft:stony_shore"), features);
    }

    /** A patch on the surface, like vanilla's berry bushes (32 tries, ±7 across, ±3 up). */
    private static void patch(PlatformDatagen.Sink sink, String name, JsonObject state, int rarity) {
        JsonObject config = new JsonObject();
        config.add("to_place", simpleProvider(state));
        configured(sink, name, "minecraft:simple_block", config);
        JsonArray placements = new JsonArray();
        placements.add(rarity(rarity));
        placements.add(placement("minecraft:in_square"));
        placements.add(heightmap("WORLD_SURFACE_WG"));
        placements.add(placement("minecraft:biome"));
        placements.add(count(32));
        JsonObject offset = placement("minecraft:random_offset");
        offset.add("xz_spread", PlatformDatagen.uniformInt(-7, 7));
        offset.add("y_spread", PlatformDatagen.uniformInt(-3, 3));
        placements.add(offset);
        JsonObject air = new JsonObject();
        air.addProperty("type", "minecraft:matching_blocks");
        air.addProperty("blocks", "minecraft:air");
        JsonObject allOf = new JsonObject();
        allOf.addProperty("type", "minecraft:all_of");
        JsonArray predicates = new JsonArray();
        predicates.add(air);
        predicates.add(wouldSurvive(state));
        allOf.add("predicates", predicates);
        placements.add(predicateFilter(allOf));
        placedFeature(sink, name, placements);
    }

    // ------------------------------------------------------------------ JSON helpers

    private static void configured(PlatformDatagen.Sink sink, String name, String type, JsonObject config) {
        JsonObject feature = new JsonObject();
        feature.addProperty("type", type);
        feature.add("config", config);
        sink.json(PlatformDatagen.Kind.WORLDGEN, NS, "configured_feature/" + name, feature);
    }

    private static void placedFeature(PlatformDatagen.Sink sink, String name, JsonArray placements) {
        JsonObject placed = new JsonObject();
        placed.addProperty("feature", NS + ":" + name);
        placed.add("placement", placements);
        sink.json(PlatformDatagen.Kind.WORLDGEN, NS, "placed_feature/" + name, placed);
    }

    private static void biomeModifier(PlatformDatagen.Sink sink, String name, List<String> biomes, JsonElement features) {
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", PlatformDatagen.loaderNamespace() + ":add_features");
        JsonArray list = new JsonArray();
        biomes.forEach(list::add);
        modifier.add("biomes", list);
        modifier.add("features", features);
        modifier.addProperty("step", VEGETATION);
        sink.json(PlatformDatagen.Kind.BIOME_MODIFIER, NS, "add_" + name, modifier);
    }

    private static JsonObject state(String block, String property, String value) {
        JsonObject state = new JsonObject();
        state.addProperty("Name", NS + ":" + block);
        if (property != null) {
            JsonObject properties = new JsonObject();
            properties.addProperty(property, value);
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

    private static JsonObject predicateFilter(JsonObject predicate) {
        JsonObject placement = placement("minecraft:block_predicate_filter");
        placement.add("predicate", predicate);
        return placement;
    }

    private static JsonObject wouldSurvive(JsonObject state) {
        JsonObject predicate = new JsonObject();
        predicate.addProperty("type", "minecraft:would_survive");
        predicate.add("state", state);
        return predicate;
    }
}
