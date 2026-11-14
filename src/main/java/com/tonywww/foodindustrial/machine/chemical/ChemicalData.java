package com.tonywww.foodindustrial.machine.chemical;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated assets and data of the T3 members and items (I16): block states, models and loot of the
 * casings and the furnace tube, item models, mining tags and the crafting recipes of the T3 machines,
 * their members, the electrode and the T3 modules (additives/06 §T3 化工, game settings). The machine
 * blocks come from {@code MachineData}.
 */
final class ChemicalData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private ChemicalData() {
    }

    static void bootstrap() {
        for (String member : ChemicalMachines.members()) {
            SharedTags.block("minecraft:mineable/pickaxe", NS + ":" + member);
        }
        PlatformDatagen.register("chemical", ChemicalData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        simpleBlock(sink, "chemical_tower_wall", cubeAll("chemical_tower_wall"));
        simpleBlock(sink, "refractory_bricks", cubeAll("refractory_bricks"));
        JsonObject tube = new JsonObject();
        tube.addProperty("parent", "minecraft:block/cube_column");
        JsonObject textures = new JsonObject();
        textures.addProperty("end", NS + ":block/furnace_tube_top");
        textures.addProperty("side", NS + ":block/furnace_tube_side");
        tube.add("textures", textures);
        simpleBlock(sink, "furnace_tube", tube);
        for (String item : List.of("chemical_frame", "graphite_electrode", "molecular_distillation")) {
            generatedItem(sink, item);
        }
        recipes(sink);
    }

    private static JsonObject cubeAll(String texture) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", NS + ":block/" + texture);
        model.add("textures", textures);
        return model;
    }

    /** Block state, model, block item model and self drop. */
    private static void simpleBlock(PlatformDatagen.Sink sink, String id, JsonObject model) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", NS + ":block/" + id);
        JsonObject variants = new JsonObject();
        variants.add("", variant);
        JsonObject state = new JsonObject();
        state.add("variants", variants);
        sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, model);
        JsonObject item = new JsonObject();
        item.addProperty("parent", NS + ":block/" + id);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, item);
        itemDefinition(sink, id, NS + ":block/" + id);
        sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, id, dropSelf(NS + ":" + id));
    }

    private static void generatedItem(PlatformDatagen.Sink sink, String id) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", NS + ":item/" + id);
        model.add("textures", textures);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, model);
        itemDefinition(sink, id, NS + ":item/" + id);
    }

    private static void itemDefinition(PlatformDatagen.Sink sink, String id, String model) {
        JsonObject inner = new JsonObject();
        inner.addProperty("type", "minecraft:model");
        inner.addProperty("model", model);
        JsonObject definition = new JsonObject();
        definition.add("model", inner);
        sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, id, definition);
    }

    private static JsonObject dropSelf(String id) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", id);
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
        return loot;
    }

    // ---- recipes (additives/06 §T3 化工：操作与实现规格, game settings, 2026-11-11) ----

    private static void recipes(PlatformDatagen.Sink sink) {
        String common = PlatformDatagen.commonTagNamespace();
        String iron = "#" + common + ":ingots/iron";
        String copper = "#" + common + ":ingots/copper";
        String gold = "#" + common + ":ingots/gold";
        String redstone = "#" + common + ":dusts/redstone";
        String ironBlock = "#" + common + ":storage_blocks/iron";
        String copperBlock = "#" + common + ":storage_blocks/copper";
        String redstoneBlock = "#" + common + ":storage_blocks/redstone";
        String frame = NS + ":chemical_frame";
        String wall = NS + ":chemical_tower_wall";
        String bricks = NS + ":refractory_bricks";
        String electrode = NS + ":graphite_electrode";
        shaped(sink, "chemical_frame", 1, keys('I', ironBlock, 'G', gold, 'L', NS + ":corrosion_lining", 'M', NS + ":machine_frame",
            'R', redstoneBlock), "IGI", "LML", "RGR");
        shaped(sink, "chemical_tower_wall", 8, keys('W', NS + ":tower_wall", 'I', iron, 'G', gold), "IWI", "WGW", "IWI");
        shaped(sink, "refractory_bricks", 8, keys('K', NS + ":kiln_brick", 'N', "minecraft:nether_bricks", 'I', iron), "KNK", "NIN", "KNK");
        shaped(sink, "furnace_tube", 2, keys('I', iron, 'B', bricks), "I I", "IBI", "I I");
        shaped(sink, "graphite_electrode", 4, keys('C', "minecraft:coal_block", 'I', iron), "C", "I", "C");
        // machines: the frame and the machine they grow from
        shaped(sink, "distillation_column", 1, keys('W', wall, 'S', NS + ":pot_still", 'R', redstone, 'F', frame), "WSW", "RFR", "WRW");
        shaped(sink, "high_pressure_reactor", 1, keys('I', ironBlock, 'S', NS + ":stirred_reactor", 'R', redstone, 'F', frame),
            "ISI", "RFR", "IRI");
        shaped(sink, "electrolyzer", 1, keys('C', copperBlock, 'E', electrode, 'R', redstone, 'F', frame), "CEC", "RFR", "CEC");
        shaped(sink, "adsorption_column", 1, keys('G', "minecraft:glass", 'A', NS + ":activated_carbon", 'R', redstone, 'F', frame),
            "GAG", "RFR", "GAG");
        shaped(sink, "electric_arc_furnace", 1, keys('B', bricks, 'E', electrode, 'R', redstone, 'F', frame, 'K', NS + ":rotary_kiln"),
            "BEB", "RFK", "BEB");
        shaped(sink, "gas_phase_reactor", 1, keys('B', bricks, 'T', NS + ":furnace_tube", 'R', redstone, 'F', frame), "BTB", "RFR", "BTB");
        shaped(sink, "spray_dryer", 1, keys('W', wall, 'D', NS + ":dryer", 'R', redstone, 'F', frame), "WDW", "RFR", "WRW");
        // T3 modules of the T2 machines (I15 left them creative-only)
        shaped(sink, "cryo_jacket", 1, keys('I', iron, 'B', "minecraft:blue_ice", 'C', copper, 'R', redstone), "IBI", "CRC", "IBI");
        shaped(sink, "multi_effect", 1, keys('I', iron, 'G', "minecraft:glass", 'C', copperBlock, 'R', redstone), "IGI", "CRC", "IGI");
        shaped(sink, "internal_mixer", 1, keys('I', iron, 'P', "minecraft:piston", 'B', ironBlock, 'R', redstone), "IPI", "BRB", "IPI");
    }

    private static Map<Character, String> keys(Object... pairs) {
        Map<Character, String> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((Character) pairs[i], (String) pairs[i + 1]);
        }
        return map;
    }

    private static void shaped(PlatformDatagen.Sink sink, String result, int count, Map<Character, String> keys, String... pattern) {
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
