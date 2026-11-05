package com.tonywww.foodindustrial.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;
import com.tonywww.foodindustrial.world.ModWorldBlocks.Entry;
import com.tonywww.foodindustrial.world.ModWorldBlocks.Kind;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data generation for the world blocks: 26.1 item definitions, loot tables (vanilla's oak, carrot,
 * cocoa, sweet berry and coal ore tables with our ids), vanilla block and item tags, and the rubber
 * wood crafting recipes. Block states and models are common resources ({@code tools/world/world_assets.py}).
 */
final class ModWorldData {
    private static final String NS = FoodIndustrial.MOD_ID;
    /** Tag of the four rubber logs and woods, like vanilla's {@code oak_logs}. */
    static final String RUBBER_LOGS = NS + ":rubber_logs";
    static final String RUBBER_PLANKS = NS + ":rubber_planks";

    private ModWorldData() {
    }

    static void bootstrap() {
        PlatformDatagen.register("world blocks", ModWorldData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        for (Entry entry : ModWorldBlocks.all()) {
            if (entry.item() != null) {
                itemDefinition(sink, entry.id());
            }
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, entry.id(), loot(entry));
        }
        for (ModWorldBlocks.Crop crop : ModWorldBlocks.crops()) {
            itemDefinition(sink, crop.id());
        }
        itemDefinition(sink, "gardenia_fruit");
        itemDefinition(sink, "vanilla_pod");
        tags(sink);
        recipes(sink);
    }

    private static void itemDefinition(PlatformDatagen.Sink sink, String id) {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", NS + ":item/" + id);
        JsonObject definition = new JsonObject();
        definition.add("model", model);
        sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, id, definition);
    }

    // ------------------------------------------------------------------ loot

    private static JsonObject loot(Entry entry) {
        String self = NS + ":" + entry.id();
        String produce = entry.produce() == null ? null : PlatformTags.itemId(entry.produce().get());
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:block");
        JsonArray pools = new JsonArray();
        switch (entry.kind()) {
            case ORE -> {
                if (entry == ModWorldBlocks.SULFUR_ORE) {
                    JsonObject silk = item(self);
                    silk.add("conditions", array(matchTool(PlatformDatagen.toolHasSilkTouch())));
                    JsonObject drop = item(PlatformTags.itemId(ModMaterials.get("sulfur").item().get()));
                    JsonObject bonus = new JsonObject();
                    bonus.addProperty("function", "minecraft:apply_bonus");
                    bonus.addProperty("enchantment", "minecraft:fortune");
                    bonus.addProperty("formula", "minecraft:ore_drops");
                    drop.add("functions", array(bonus, function("minecraft:explosion_decay")));
                    JsonObject alternatives = new JsonObject();
                    alternatives.addProperty("type", "minecraft:alternatives");
                    alternatives.add("children", array(silk, drop));
                    pools.add(pool(array(alternatives), null));
                } else {
                    pools.add(dropSelf(self));
                }
            }
            case SLAB -> {
                JsonObject item = item(self);
                JsonObject doubled = setCount(2);
                doubled.add("conditions", array(stateIs(self, "type", "double")));
                item.add("functions", array(doubled, function("minecraft:explosion_decay")));
                pools.add(pool(array(item), null));
            }
            case DOOR -> {
                JsonObject item = item(self);
                item.add("conditions", array(stateIs(self, "half", "lower")));
                pools.add(pool(array(item), array(condition("minecraft:survives_explosion"))));
            }
            case LEAVES -> leaves(pools, self);
            case CROP -> {
                table.add("functions", array(function("minecraft:explosion_decay")));
                pools.add(pool(array(item(produce)), null));
                JsonObject bonus = item(produce);
                JsonObject apply = new JsonObject();
                apply.addProperty("function", "minecraft:apply_bonus");
                apply.addProperty("enchantment", "minecraft:fortune");
                apply.addProperty("formula", "minecraft:binomial_with_bonus_count");
                JsonObject parameters = new JsonObject();
                parameters.addProperty("extra", 3);
                parameters.addProperty("probability", 0.5714286);
                apply.add("parameters", parameters);
                bonus.add("functions", array(apply));
                pools.add(pool(array(bonus), array(stateIs(self, "age", "7"))));
            }
            case WILD_PLANT -> {
                JsonObject item = item(produce);
                item.add("functions", array(setCountUniform(1, 2), function("minecraft:explosion_decay")));
                pools.add(pool(array(item), null));
            }
            case BUSH -> {
                table.add("functions", array(function("minecraft:explosion_decay")));
                pools.add(bushPool(self, produce, 3, 2, 3));
                pools.add(bushPool(self, produce, 2, 1, 2));
            }
            case POD -> {
                JsonObject item = item(produce);
                JsonObject three = setCount(3);
                three.add("conditions", array(stateIs(self, "age", "2")));
                item.add("functions", array(three, function("minecraft:explosion_decay")));
                pools.add(pool(array(item), null));
            }
            default -> pools.add(dropSelf(self));
        }
        table.add("pools", pools);
        table.addProperty("random_sequence", NS + ":blocks/" + entry.id());
        return table;
    }

    private static void leaves(JsonArray pools, String self) {
        JsonObject anyOf = new JsonObject();
        anyOf.addProperty("condition", "minecraft:any_of");
        anyOf.add("terms", array(matchTool(PlatformDatagen.toolIsShears()), matchTool(PlatformDatagen.toolHasSilkTouch())));
        JsonObject leaves = item(self);
        leaves.add("conditions", array(anyOf));
        JsonObject sapling = item(NS + ":rubber_sapling");
        sapling.add("conditions", array(condition("minecraft:survives_explosion"), tableBonus(0.05, 0.0625, 0.083333336, 0.1)));
        JsonObject alternatives = new JsonObject();
        alternatives.addProperty("type", "minecraft:alternatives");
        alternatives.add("children", array(leaves, sapling));
        pools.add(pool(array(alternatives), null));
        JsonObject notShears = new JsonObject();
        notShears.addProperty("condition", "minecraft:inverted");
        notShears.add("term", anyOf.deepCopy());
        JsonObject stick = item("minecraft:stick");
        stick.add("conditions", array(tableBonus(0.02, 0.022222223, 0.025, 0.033333335, 0.1)));
        stick.add("functions", array(setCountUniform(1, 2), function("minecraft:explosion_decay")));
        pools.add(pool(array(stick), array(notShears)));
    }

    private static JsonObject bushPool(String self, String fruit, int age, int min, int max) {
        JsonObject pool = pool(array(item(fruit)), array(stateIs(self, "age", Integer.toString(age))));
        JsonObject bonus = new JsonObject();
        bonus.addProperty("function", "minecraft:apply_bonus");
        bonus.addProperty("enchantment", "minecraft:fortune");
        bonus.addProperty("formula", "minecraft:uniform_bonus_count");
        JsonObject parameters = new JsonObject();
        parameters.addProperty("bonusMultiplier", 1);
        bonus.add("parameters", parameters);
        pool.add("functions", array(setCountUniform(min, max), bonus));
        return pool;
    }

    private static JsonObject dropSelf(String self) {
        return pool(array(item(self)), array(condition("minecraft:survives_explosion")));
    }

    private static JsonObject pool(JsonArray entries, JsonArray conditions) {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);
        if (conditions != null) {
            pool.add("conditions", conditions);
        }
        pool.add("entries", entries);
        return pool;
    }

    private static JsonObject item(String id) {
        JsonObject item = new JsonObject();
        item.addProperty("type", "minecraft:item");
        item.addProperty("name", id);
        return item;
    }

    private static JsonObject condition(String type) {
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", type);
        return condition;
    }

    private static JsonObject function(String type) {
        JsonObject function = new JsonObject();
        function.addProperty("function", type);
        return function;
    }

    private static JsonObject matchTool(JsonObject predicate) {
        JsonObject condition = condition("minecraft:match_tool");
        condition.add("predicate", predicate);
        return condition;
    }

    private static JsonObject stateIs(String block, String property, String value) {
        JsonObject condition = condition("minecraft:block_state_property");
        condition.addProperty("block", block);
        JsonObject properties = new JsonObject();
        properties.addProperty(property, value);
        condition.add("properties", properties);
        return condition;
    }

    private static JsonObject tableBonus(double... chances) {
        JsonObject condition = condition("minecraft:table_bonus");
        condition.addProperty("enchantment", "minecraft:fortune");
        JsonArray list = new JsonArray();
        for (double chance : chances) {
            list.add(chance);
        }
        condition.add("chances", list);
        return condition;
    }

    private static JsonObject setCount(int count) {
        JsonObject function = function("minecraft:set_count");
        function.addProperty("count", (double) count);
        function.addProperty("add", false);
        return function;
    }

    private static JsonObject setCountUniform(int min, int max) {
        JsonObject count = new JsonObject();
        count.addProperty("type", "minecraft:uniform");
        count.addProperty("min", (double) min);
        count.addProperty("max", (double) max);
        JsonObject function = function("minecraft:set_count");
        function.add("count", count);
        function.addProperty("add", false);
        return function;
    }

    private static JsonArray array(JsonElement... elements) {
        JsonArray array = new JsonArray();
        for (JsonElement element : elements) {
            array.add(element);
        }
        return array;
    }

    // ------------------------------------------------------------------ tags

    private static void tags(PlatformDatagen.Sink sink) {
        Map<String, List<String>> blocks = new LinkedHashMap<>();
        Map<String, List<String>> items = new LinkedHashMap<>();
        Set<Kind> wood = EnumSet.of(Kind.LOG, Kind.WOOD, Kind.PLANKS, Kind.STAIRS, Kind.SLAB, Kind.FENCE, Kind.FENCE_GATE,
            Kind.DOOR, Kind.TRAPDOOR, Kind.PRESSURE_PLATE, Kind.BUTTON);
        for (Entry entry : ModWorldBlocks.all()) {
            String id = NS + ":" + entry.id();
            Kind kind = entry.kind();
            switch (kind) {
                case ORE -> add(blocks, "minecraft:mineable/pickaxe", id);
                case CRUST, SOIL -> add(blocks, "minecraft:mineable/shovel", id);
                case CROP -> {
                    add(blocks, "minecraft:crops", id);
                    add(blocks, "minecraft:maintains_farmland", id);
                    add(blocks, "minecraft:bee_growables", id);
                }
                case BUSH -> add(blocks, "minecraft:bee_growables", id);
                case LEAVES -> {
                    add(blocks, "minecraft:mineable/hoe", id);
                    add(blocks, "minecraft:replaceable_by_trees", id);
                }
                default -> {
                }
            }
            if (wood.contains(kind)) {
                add(blocks, "minecraft:mineable/axe", id);
            }
            String vanilla = switch (kind) {
                case LOG, WOOD -> RUBBER_LOGS;
                case PLANKS -> "minecraft:planks";
                case STAIRS -> "minecraft:wooden_stairs";
                case SLAB -> "minecraft:wooden_slabs";
                case FENCE -> "minecraft:wooden_fences";
                case FENCE_GATE -> "minecraft:fence_gates";
                case DOOR -> "minecraft:wooden_doors";
                case TRAPDOOR -> "minecraft:wooden_trapdoors";
                case PRESSURE_PLATE -> "minecraft:wooden_pressure_plates";
                case BUTTON -> "minecraft:wooden_buttons";
                case LEAVES -> "minecraft:leaves";
                case SAPLING -> "minecraft:saplings";
                default -> null;
            };
            if (vanilla != null) {
                add(blocks, vanilla, id);
                add(items, vanilla, id);
            }
        }
        add(blocks, "minecraft:logs_that_burn", "#" + RUBBER_LOGS);
        add(items, "minecraft:logs_that_burn", "#" + RUBBER_LOGS);
        add(items, RUBBER_PLANKS, NS + ":" + ModWorldBlocks.RUBBER_PLANKS.id());
        SharedTags.blocks().forEach((tag, values) -> values.forEach(value -> add(blocks, tag, value)));
        SharedTags.items().forEach((tag, values) -> values.forEach(value -> add(items, tag, value)));
        write(sink, PlatformDatagen.Kind.BLOCK_TAG, blocks);
        write(sink, PlatformDatagen.Kind.ITEM_TAG, items);
    }

    private static void add(Map<String, List<String>> tags, String tag, String value) {
        tags.computeIfAbsent(tag, key -> new ArrayList<>()).add(value);
    }

    private static void write(PlatformDatagen.Sink sink, PlatformDatagen.Kind kind, Map<String, List<String>> tags) {
        tags.forEach((tag, values) -> {
            JsonArray array = new JsonArray();
            values.forEach(array::add);
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            json.add("values", array);
            int colon = tag.indexOf(':');
            sink.json(kind, tag.substring(0, colon), tag.substring(colon + 1), json);
        });
    }

    // ------------------------------------------------------------------ recipes

    private static void recipes(PlatformDatagen.Sink sink) {
        String planks = "#" + RUBBER_PLANKS;
        String stick = "#" + PlatformDatagen.commonTagNamespace() + ":rods/wooden";
        shapeless(sink, "rubber_planks", "building", "planks", 4, "#" + RUBBER_LOGS);
        shaped(sink, "rubber_wood", "building", "bark", 3, Map.of('#', NS + ":rubber_log"), "##", "##");
        shaped(sink, "stripped_rubber_wood", "building", "bark", 3, Map.of('#', NS + ":stripped_rubber_log"), "##", "##");
        shaped(sink, "rubber_stairs", "building", "wooden_stairs", 4, Map.of('#', planks), "#  ", "## ", "###");
        shaped(sink, "rubber_slab", "building", "wooden_slab", 6, Map.of('#', planks), "###");
        shaped(sink, "rubber_fence", "misc", "wooden_fence", 3, Map.of('W', planks, '#', stick), "W#W", "W#W");
        shaped(sink, "rubber_fence_gate", "redstone", "wooden_fence_gate", 1, Map.of('W', planks, '#', stick), "#W#", "#W#");
        shaped(sink, "rubber_door", "redstone", "wooden_door", 3, Map.of('#', planks), "##", "##", "##");
        shaped(sink, "rubber_trapdoor", "redstone", "wooden_trapdoor", 2, Map.of('#', planks), "###", "###");
        shaped(sink, "rubber_pressure_plate", "redstone", "wooden_pressure_plate", 1, Map.of('#', planks), "##");
        shapeless(sink, "rubber_button", "redstone", "wooden_button", 1, planks);
    }

    private static void shaped(PlatformDatagen.Sink sink, String result, String category, String group, int count,
                               Map<Character, String> key, String... pattern) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");
        recipe.addProperty("category", category);
        recipe.addProperty("group", group);
        JsonObject keys = new JsonObject();
        key.forEach((symbol, input) -> keys.add(String.valueOf(symbol), PlatformDatagen.ingredient(input)));
        recipe.add("key", keys);
        JsonArray rows = new JsonArray();
        for (String row : pattern) {
            rows.add(row);
        }
        recipe.add("pattern", rows);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + result, count));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, result, recipe);
    }

    private static void shapeless(PlatformDatagen.Sink sink, String result, String category, String group, int count, String input) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", category);
        recipe.addProperty("group", group);
        recipe.add("ingredients", array(PlatformDatagen.ingredient(input)));
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + result, count));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, result, recipe);
    }
}
