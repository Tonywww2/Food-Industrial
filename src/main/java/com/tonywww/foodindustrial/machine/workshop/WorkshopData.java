package com.tonywww.foodindustrial.machine.workshop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generated assets and data of the workshop members (I14): casing, condenser and hand crank block
 * states, models and loot, the module's item model, mining tags and the crafting recipes of the ten
 * machines, their members and the multiblock ports (additives/06 §T1 作坊, game settings). The machine
 * blocks themselves come from {@code MachineData}.
 */
final class WorkshopData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private WorkshopData() {
    }

    static void bootstrap() {
        SharedTags.block("minecraft:mineable/axe", NS + ":koji_room_wall");
        SharedTags.block("minecraft:mineable/pickaxe", NS + ":kiln_brick");
        SharedTags.block("minecraft:mineable/pickaxe", NS + ":drying_room_wall");
        SharedTags.block("minecraft:mineable/pickaxe", NS + ":condenser");
        SharedTags.block("minecraft:mineable/axe", NS + ":" + HandCrankBlock.ID);
        for (MachineDefinition machine : WorkshopMachines.wooden()) {
            SharedTags.block("minecraft:mineable/axe", NS + ":" + machine.id());
        }
        PlatformDatagen.register("workshop", WorkshopData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        for (String casing : WorkshopMachines.casings()) {
            simpleBlock(sink, casing, cubeAll(casing));
        }
        JsonObject condenser = new JsonObject();
        condenser.addProperty("parent", "minecraft:block/cube_column");
        JsonObject textures = new JsonObject();
        textures.addProperty("end", NS + ":block/condenser_top");
        textures.addProperty("side", NS + ":block/condenser_side");
        condenser.add("textures", textures);
        simpleBlock(sink, "condenser", condenser);
        crank(sink);
        transmission(sink);
        generatedItem(sink, "essential_oil_separator");
        recipes(sink);
    }

    // ---- blocks ----

    private static JsonObject cubeAll(String texture) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", NS + ":block/" + texture);
        model.add("textures", textures);
        return model;
    }

    /** One block state, its model, the block item model and a self drop. */
    private static void simpleBlock(PlatformDatagen.Sink sink, String id, JsonObject model) {
        JsonObject variants = new JsonObject();
        variants.add("", variant(NS + ":block/" + id, 0));
        JsonObject state = new JsonObject();
        state.add("variants", variants);
        sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, model);
        blockItem(sink, id);
        sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, id, dropSelf(NS + ":" + id));
    }

    /** Hand crank: base, axle, arm and grip of oak; a quarter turn per {@code turn}. */
    private static void crank(PlatformDatagen.Sink sink) {
        String id = HandCrankBlock.ID;
        JsonObject variants = new JsonObject();
        for (int turn = 0; turn < 4; turn++) {
            variants.add("turn=" + turn, variant(NS + ":block/" + id, turn * 90));
        }
        JsonObject state = new JsonObject();
        state.add("variants", variants);
        sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
        JsonArray elements = new JsonArray();
        elements.add(box(5, 0, 5, 11, 2, 11, "planks"));
        elements.add(box(7, 2, 7, 9, 12, 9, "log"));
        elements.add(box(7, 10, 7, 14, 12, 9, "log"));
        elements.add(box(12, 12, 7, 14, 15, 9, "log"));
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("planks", "minecraft:block/oak_planks");
        textures.addProperty("log", "minecraft:block/stripped_oak_log");
        textures.addProperty("particle", "minecraft:block/oak_planks");
        model.add("textures", textures);
        model.add("elements", elements);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, model);
        blockItem(sink, id);
        sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, id, dropSelf(NS + ":" + id));
    }

    /**
     * Create's transmission interface (I25): a low andesite casing; the half shaft on top is drawn by
     * Create. Written on every node, used where Create registers the block; the recipe loads only with
     * Create installed and the integration enabled.
     */
    private static void transmission(PlatformDatagen.Sink sink) {
        String id = com.tonywww.foodindustrial.platform.PlatformKinetics.INTERFACE_ID;
        JsonObject variants = new JsonObject();
        variants.add("", variant(NS + ":block/" + id, 0));
        JsonObject state = new JsonObject();
        state.add("variants", variants);
        sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
        JsonObject element = box(0, 0, 0, 16, 12, 16, "side");
        element.getAsJsonObject("faces").getAsJsonObject("up").addProperty("texture", "#top");
        for (String face : new String[]{"north", "south", "west", "east"}) {
            JsonArray uv = new JsonArray();
            uv.add(0);
            uv.add(4);
            uv.add(16);
            uv.add(16);
            element.getAsJsonObject("faces").getAsJsonObject(face).add("uv", uv);
        }
        JsonArray elements = new JsonArray();
        elements.add(element);
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("side", "create:block/andesite_casing");
        textures.addProperty("top", "create:block/gearbox_top");
        textures.addProperty("particle", "create:block/andesite_casing");
        model.add("textures", textures);
        model.add("elements", elements);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, model);
        blockItem(sink, id);
        // No loot table: the block drops itself in code (a table would name a block missing without Create).
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", "misc");
        JsonArray ingredients = new JsonArray();
        ingredients.add(PlatformDatagen.ingredient("create:andesite_casing"));
        ingredients.add(PlatformDatagen.ingredient("create:shaft"));
        recipe.add("ingredients", ingredients);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + id, 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, id, com.tonywww.foodindustrial.platform.recipe.PlatformRecipeConditions.conditional(
            recipe, "create", com.tonywww.foodindustrial.integration.Integrations.CREATE.id(), java.util.List.of()));
    }

    private static JsonObject box(int x0, int y0, int z0, int x1, int y1, int z1, String texture) {
        JsonObject element = new JsonObject();
        element.add("from", array(x0, y0, z0));
        element.add("to", array(x1, y1, z1));
        JsonObject faces = new JsonObject();
        for (String face : new String[]{"down", "up", "north", "south", "west", "east"}) {
            JsonObject entry = new JsonObject();
            entry.addProperty("texture", "#" + texture);
            faces.add(face, entry);
        }
        element.add("faces", faces);
        return element;
    }

    private static JsonArray array(int... values) {
        JsonArray array = new JsonArray();
        for (int value : values) {
            array.add(value);
        }
        return array;
    }

    private static JsonObject variant(String model, int y) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", model);
        if (y != 0) {
            variant.addProperty("y", y);
        }
        return variant;
    }

    private static void blockItem(PlatformDatagen.Sink sink, String id) {
        JsonObject item = new JsonObject();
        item.addProperty("parent", NS + ":block/" + id);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, item);
        itemDefinition(sink, id, NS + ":block/" + id);
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

    // ---- recipes (additives/06 §T1 作坊 合成, game settings, 2026-11-10) ----

    private static void recipes(PlatformDatagen.Sink sink) {
        String common = PlatformDatagen.commonTagNamespace();
        String stick = "#" + common + ":rods/wooden";
        String iron = "#" + common + ":ingots/iron";
        String copper = "#" + common + ":ingots/copper";
        String planks = "#minecraft:planks";
        String brick = "minecraft:brick";
        String furnace = "minecraft:furnace";
        String tank = NS + ":wooden_tank";
        shaped(sink, "stone_mill", 1, keys('P', planks, 'K', stick, 'S', "minecraft:stone"), "PKP", "SSS", "SSS");
        shaped(sink, "screw_press", 1, keys('P', planks, 'I', iron, 'X', "minecraft:piston"), "PPP", "IXI", "P P");
        shaped(sink, "boiling_stove", 1, keys('B', brick, 'O', NS + ":iron_pot", 'F', furnace), "BOB", "BFB", " B ");
        shaped(sink, "steeping_vat", 1, keys('P', planks, 'T', tank), " P ", "PTP", " P ");
        shaped(sink, "fermentation_vat", 1, keys('I', iron, 'C', NS + ":crock", 'T', tank), "ICI", " T ");
        shaped(sink, "koji_room", 1, keys('W', NS + ":koji_room_wall", 'K', NS + ":koji_tray", 'F', furnace), "WKW", "KFK", "WKW");
        shaped(sink, "earth_kiln", 1, keys('B', NS + ":kiln_brick", 'F', furnace), "BBB", "BFB", "BBB");
        shaped(sink, "pot_still", 1, keys('C', copper, 'T', tank, 'F', furnace), "CTC", "CFC", " C ");
        shaped(sink, "crystallizing_tank", 1, keys('B', brick, 'T', tank), "BTB", "BBB");
        shaped(sink, "drying_room", 1, keys('W', NS + ":drying_room_wall", 'R', NS + ":drying_rack", 'F', furnace), "WRW", "WFW", " R ");
        shaped(sink, HandCrankBlock.ID, 1, keys('K', stick, 'P', planks), "KK", "K ", "P ");
        shaped(sink, "condenser", 1, keys('C', copper, 'T', tank), " C ", "CTC", " C ");
        shaped(sink, "essential_oil_separator", 1, keys('G', "minecraft:glass", 'C', copper), "GCG", "GCG");
        shaped(sink, "koji_room_wall", 8, keys('P', planks, 'C', "minecraft:clay_ball", 'H', "minecraft:hay_block"), "PCP", "CHC", "PCP");
        shaped(sink, "kiln_brick", 8, keys('B', brick, 'C', "minecraft:clay"), "BBB", "BCB", "BBB");
        shaped(sink, "drying_room_wall", 8, keys('B', brick, 'P', planks, 'I', iron), "BPB", "PIP", "BPB");
        // ports of every multiblock (I14 adds them for the T1 rooms; pending review)
        shaped(sink, "item_port", 1, keys('I', iron, 'C', "minecraft:chest"), " I ", "ICI", " I ");
        shaped(sink, "fluid_port", 1, keys('I', iron, 'U', "minecraft:bucket"), " I ", "IUI", " I ");
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
