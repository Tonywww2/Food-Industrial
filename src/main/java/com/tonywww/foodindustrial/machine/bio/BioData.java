package com.tonywww.foodindustrial.machine.bio;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated assets and data of the T4 members and items (I17): the sterile vessel wall, item models,
 * mining tag and the crafting recipes of the T4 machines, their members, the membrane module and the T4
 * modules (additives/06 §T4 生物 / 精细, game settings). The machine blocks come from {@code MachineData}.
 */
final class BioData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private BioData() {
    }

    static void bootstrap() {
        SharedTags.block("minecraft:mineable/pickaxe", NS + ":sterile_vessel_wall");
        PlatformDatagen.register("bio", BioData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        simpleBlock(sink, "sterile_vessel_wall", cubeAll("sterile_vessel_wall"));
        for (String item : List.of("clean_frame", "membrane_module")) {
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

    // ---- recipes (additives/06 §T4 生物 / 精细：操作与实现规格, game settings, 2026-11-11) ----

    private static void recipes(PlatformDatagen.Sink sink) {
        String common = PlatformDatagen.commonTagNamespace();
        String iron = "#" + common + ":ingots/iron";
        String copper = "#" + common + ":ingots/copper";
        String redstone = "#" + common + ":dusts/redstone";
        String diamond = "#" + common + ":gems/diamond";
        String quartz = "#" + common + ":gems/quartz";
        String ironBlock = "#" + common + ":storage_blocks/iron";
        String glass = "minecraft:glass";
        String frame = NS + ":clean_frame";
        String wall = NS + ":sterile_vessel_wall";
        String membrane = NS + ":membrane_module";
        String vacuum = NS + ":vacuum_pump";
        shaped(sink, "clean_frame", 1, keys('Q', quartz, 'D', diamond, 'G', glass, 'F', NS + ":chemical_frame"), "QDQ", "GFG", "QDQ");
        shaped(sink, "sterile_vessel_wall", 8, keys('I', iron, 'Q', "minecraft:quartz_block", 'W', NS + ":chemical_tower_wall"),
            "IQI", "QWQ", "IQI");
        shaped(sink, "membrane_module", 2, keys('P', "minecraft:paper", 'G', "minecraft:glass_pane", 'S', "minecraft:slime_ball"),
            "PGP", "GSG", "PGP");
        // machines
        shaped(sink, "sterile_fermenter", 1, keys('V', wall, 'T', NS + ":fermentation_vat", 'R', redstone, 'F', frame), "VTV", "RFR", "VRV");
        shaped(sink, "culture_bench", 1, keys('G', glass, 'S', "minecraft:spyglass", 'C', "minecraft:crafting_table", 'R', redstone, 'F', frame),
            "GSG", "RFR", "GCG");
        shaped(sink, "membrane_separator", 1, keys('M', membrane, 'C', NS + ":adsorption_column", 'R', redstone, 'F', frame,
            'P', "minecraft:piston"), "MCM", "RFR", "MPM");
        // T4 modules (I15, I16 left them creative-only)
        shaped(sink, "uv_lamp", 1, keys('G', glass, 'A', "minecraft:amethyst_shard", 'I', iron, 'O', "minecraft:glowstone"), "GAG", "IOI", "GAG");
        shaped(sink, "freeze_dryer", 1, keys('I', iron, 'C', NS + ":cryo_jacket", 'D', diamond, 'V', vacuum), "III", "CDV", "III");
        shaped(sink, "supercritical_unit", 1, keys('B', ironBlock, 'G', glass, 'P', "minecraft:piston", 'D', diamond), "BGB", "PDP", "BGB");
        shaped(sink, "molecular_distillation", 1, keys('G', glass, 'V', vacuum, 'C', copper, 'D', diamond), "GVG", "CDC", "GVG");
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
