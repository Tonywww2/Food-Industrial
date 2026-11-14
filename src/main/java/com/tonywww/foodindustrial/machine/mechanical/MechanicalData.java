package com.tonywww.foodindustrial.machine.mechanical;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated assets and data of the T2 members and items (I15): casing and air intake block states,
 * models and loot, item models of the frame, upgrades and modules, mining tags and the crafting recipes
 * of the machines, members, energy port, upgrades and T2 modules (additives/06 §T2 机械化, game
 * settings). T3 and T4 modules get recipes with I16 and I17. The machine blocks come from
 * {@code MachineData}.
 */
final class MechanicalData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private MechanicalData() {
    }

    static void bootstrap() {
        for (String casing : MechanicalMachines.casings()) {
            SharedTags.block("minecraft:mineable/pickaxe", NS + ":" + casing);
        }
        SharedTags.block("minecraft:mineable/pickaxe", NS + ":air_intake");
        PlatformDatagen.register("mechanical", MechanicalData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        List<String> blocks = new ArrayList<>(MechanicalMachines.casings());
        blocks.add("air_intake");
        for (String block : blocks) {
            simpleBlock(sink, block);
        }
        List<String> items = new ArrayList<>();
        items.add("machine_frame");
        items.addAll(MechanicalMachines.upgrades());
        items.addAll(MechanicalMachines.modules());
        for (String item : items) {
            generatedItem(sink, item);
        }
        recipes(sink);
    }

    // ---- blocks and items ----

    /** Block state, cube model, block item model and self drop of a block with one texture. */
    private static void simpleBlock(PlatformDatagen.Sink sink, String id) {
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

    // ---- recipes (additives/06 §T2 机械化：操作与实现规格, game settings, 2026-11-11) ----

    private static void recipes(PlatformDatagen.Sink sink) {
        String common = PlatformDatagen.commonTagNamespace();
        String iron = "#" + common + ":ingots/iron";
        String copper = "#" + common + ":ingots/copper";
        String gold = "#" + common + ":ingots/gold";
        String redstone = "#" + common + ":dusts/redstone";
        String frame = NS + ":machine_frame";
        shaped(sink, "machine_frame", 1, keys('I', iron, 'R', redstone, 'C', copper), "IRI", "ICI", "IRI");
        // machines: the frame and the T1 machine they grow from
        shaped(sink, "crusher", 1, keys('L', "minecraft:flint", 'M', NS + ":stone_mill", 'F', frame, 'I', iron), "LML", "LFL", "III");
        shaped(sink, "solid_liquid_separator", 1, keys('I', iron, 'P', NS + ":screw_press", 'R', redstone, 'F', frame), "IPI", "RFR", "IRI");
        shaped(sink, "stirred_reactor", 1, keys('I', iron, 'B', NS + ":boiling_stove", 'R', redstone, 'F', frame), "IBI", "RFR", "IRI");
        shaped(sink, "evaporating_crystallizer", 1, keys('I', iron, 'T', NS + ":crystallizing_tank", 'R', redstone, 'F', frame),
            "ITI", "RFR", "IRI");
        shaped(sink, "dryer", 1, keys('I', iron, 'D', NS + ":drying_rack", 'R', redstone, 'F', frame), "IDI", "RFR", "IDI");
        shaped(sink, "extraction_tank", 1, keys('C', copper, 'V', NS + ":steeping_vat", 'R', redstone, 'F', frame), "CVC", "RFR", "CRC");
        shaped(sink, "absorption_tower", 1, keys('W', NS + ":tower_wall", 'T', NS + ":metal_tank", 'R', redstone, 'F', frame),
            "WTW", "RFR", "WRW");
        shaped(sink, "rotary_kiln", 1, keys('S', NS + ":kiln_shell", 'B', "minecraft:blast_furnace", 'R', redstone, 'F', frame),
            "SBS", "RFR", "SRS");
        shaped(sink, "mixer", 1, keys('I', iron, 'P', "minecraft:piston", 'R', redstone, 'F', frame), "IPI", "RFR", "IPI");
        // members and ports
        shaped(sink, "tower_wall", 8, keys('I', iron, 'B', "minecraft:brick", 'C', copper), "IBI", "BCB", "IBI");
        shaped(sink, "kiln_shell", 8, keys('I', iron, 'K', NS + ":kiln_brick", 'C', copper), "IKI", "KCK", "IKI");
        shaped(sink, "air_intake", 1, keys('I', iron, 'B', "minecraft:iron_bars"), " I ", "IBI", " I ");
        shaped(sink, "energy_port", 1, keys('I', iron, 'R', "#" + common + ":storage_blocks/redstone"), " I ", "IRI", " I ");
        // upgrades
        shaped(sink, "speed_upgrade", 1, keys('I', iron, 'R', redstone, 'G', gold), "IRI", "RGR", "IRI");
        shaped(sink, "efficiency_upgrade", 1, keys('I', iron, 'R', redstone, 'C', copper), "IRI", "RCR", "IRI");
        shaped(sink, "parallel_upgrade", 1, keys('I', iron, 'R', redstone, 'C', "minecraft:chest"), "IRI", "RCR", "IRI");
        shaped(sink, "standardization_module", 1, keys('I', iron, 'R', redstone, 'C', "minecraft:comparator"), "IRI", "RCR", "IRI");
        // T2 modules
        shaped(sink, "filter_plates", 1, keys('I', iron, 'P', "minecraft:paper", 'R', redstone), "IPI", "PRP", "IPI");
        shaped(sink, "centrifuge_drum", 1, keys('I', iron, 'R', redstone, 'C', copper), "III", "RCR", "III");
        shaped(sink, "corrosion_lining", 1, keys('I', iron, 'C', "minecraft:clay_ball", 'G', "minecraft:glass"), "ICI", "CGC", "ICI");
        shaped(sink, "vacuum_pump", 1, keys('I', iron, 'P', "minecraft:piston", 'G', "minecraft:glass"), "IPI", "IGI", " P ");
        shaped(sink, "chill_roll", 1, keys('I', iron, 'E', "minecraft:ice", 'R', redstone), "III", "ERE", "III");
        shaped(sink, "lead_chamber", 1, keys('I', iron, 'C', copper, 'B', "minecraft:brick"), "ICI", "CBC", "ICI");
        shaped(sink, "emulsifying_head", 1, keys('I', iron, 'R', redstone, 'S', "minecraft:shears"), " I ", "RSR", "III");
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
