package com.tonywww.foodindustrial.machine.kitchen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Recipes and mining tags of the food machines: T1 (I41) and the T2 lines with their casing and modules (I42);
 * recipes drafted for review. Machine models come from MachineData; the casing and the modules are made here.
 */
final class KitchenData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private KitchenData() {
    }

    static void bootstrap() {
        for (MachineDefinition machine : KitchenMachines.wooden()) {
            SharedTags.block("minecraft:mineable/axe", NS + ":" + machine.id());
        }
        SharedTags.block("minecraft:mineable/pickaxe", NS + ":food_machine_casing");
        PlatformDatagen.register("kitchen", KitchenData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        String common = PlatformDatagen.commonTagNamespace();
        String iron = "#" + common + ":ingots/iron";
        String stick = "#" + common + ":rods/wooden";
        String planks = "#minecraft:planks";
        String tank = NS + ":wooden_tank";
        shaped(sink, "noodle_press", keys('I', iron, 'S', stick, 'P', planks), "III", "PSP", "P P");
        shaped(sink, "mixing_churn", keys('I', iron, 'S', stick, 'T', tank), " S ", "ISI", " T ");
        shaped(sink, "brick_oven", keys('B', "minecraft:bricks", 'F', "minecraft:furnace"), "BBB", "BFB");
        shaped(sink, "meat_grinder", keys('I', iron, 'H', "minecraft:hopper"), " H ", "III", " I ");
        shaped(sink, "candy_table", keys('S', "minecraft:smooth_stone_slab", 'P', planks, 'F', "minecraft:furnace"), "SSS", "PFP", "P P");
        shaped(sink, "ice_salt_freezer", keys('P', planks, 'I', iron, 'B', "minecraft:bucket"), "PIP", "PBP", "PPP");
        shaped(sink, "filling_sealer", keys('I', iron, 'S', stick, 'X', "minecraft:piston"), "ISI", "IXI", "I I");
        // T2 lines (I42): the frame and the T1 machine each grows from, like the additive line's T2 machines
        String copper = "#" + common + ":ingots/copper";
        String redstone = "#" + common + ":dusts/redstone";
        String frame = NS + ":machine_frame";
        String[][] lines = {{"forming_line", "noodle_press"}, {"homogenizing_sterilizer", "mixing_churn"}, {"baking_line", "brick_oven"},
            {"cooker_fryer", "boiling_stove"}, {"aging_cellar", "fermentation_vat"}, {"cutting_station", "meat_grinder"},
            {"confectionery_line", "candy_table"}, {"freezing_unit", "ice_salt_freezer"}, {"filling_retort_line", "filling_sealer"}};
        for (String[] line : lines) {
            shaped(sink, line[0], keys('I', iron, 'X', NS + ":" + line[1], 'R', redstone, 'F', frame), "IXI", "RFR", "IRI");
        }
        shapedCount(sink, "food_machine_casing", 8, keys('I', iron, 'G', "minecraft:glass", 'C', copper), "IGI", "GCG", "IGI");
        shaped(sink, "extrusion_section", keys('I', iron, 'P', "minecraft:piston", 'R', redstone, 'E', NS + ":extrusion_die"),
            "IPI", "RER", "IPI");
        shaped(sink, "uht_section", keys('C', copper, 'I', iron, 'B', "minecraft:blast_furnace"), "CIC", "IBI", "CIC");
        casingBlock(sink, "food_machine_casing");
        for (String module : new String[]{"extrusion_section", "uht_section"}) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", NS + ":item/" + module);
            model.add("textures", textures);
            sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + module, model);
            itemDefinition(sink, module, NS + ":item/" + module);
        }
    }

    /** Block state, cube model, item model and self drop of the casing. */
    private static void casingBlock(PlatformDatagen.Sink sink, String id) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", NS + ":block/" + id);
        JsonObject variants = new JsonObject();
        variants.add("", variant);
        JsonObject state = new JsonObject();
        state.add("variants", variants);
        sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", NS + ":block/" + id);
        model.add("textures", textures);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, model);
        JsonObject item = new JsonObject();
        item.addProperty("parent", NS + ":block/" + id);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, item);
        itemDefinition(sink, id, NS + ":block/" + id);
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", NS + ":" + id);
        JsonArray entries = new JsonArray();
        entries.add(entry);
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:survives_explosion");
        JsonArray conditions = new JsonArray();
        conditions.add(condition);
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);
        pool.add("entries", entries);
        pool.add("conditions", conditions);
        JsonArray pools = new JsonArray();
        pools.add(pool);
        JsonObject loot = new JsonObject();
        loot.addProperty("type", "minecraft:block");
        loot.add("pools", pools);
        sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, id, loot);
    }

    private static void itemDefinition(PlatformDatagen.Sink sink, String id, String model) {
        JsonObject inner = new JsonObject();
        inner.addProperty("type", "minecraft:model");
        inner.addProperty("model", model);
        JsonObject definition = new JsonObject();
        definition.add("model", inner);
        sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, id, definition);
    }

    private static Map<Character, String> keys(Object... pairs) {
        Map<Character, String> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((Character) pairs[i], (String) pairs[i + 1]);
        }
        return map;
    }

    private static void shaped(PlatformDatagen.Sink sink, String result, Map<Character, String> keys, String... pattern) {
        shapedCount(sink, result, 1, keys, pattern);
    }

    private static void shapedCount(PlatformDatagen.Sink sink, String result, int count, Map<Character, String> keys, String... pattern) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");
        recipe.addProperty("category", "misc");
        JsonObject key = new JsonObject();
        keys.forEach((symbol, value) -> key.add(String.valueOf(symbol), PlatformDatagen.ingredient(value)));
        recipe.add("key", key);
        JsonArray rows = new JsonArray();
        for (String row : pattern) {
            rows.add(row);
        }
        recipe.add("pattern", rows);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + result, count));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, result, recipe);
    }
}
