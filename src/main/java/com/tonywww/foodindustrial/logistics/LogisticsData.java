package com.tonywww.foodindustrial.logistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;

/**
 * Generated assets and data of the tanks and the gas cylinder (machines/03 §5): block states, the
 * open-top wooden tank model, the framed metal tank model (cutout), item models, loot tables (the block
 * drops itself; the fluid is added to the dropped item by the block), tool tags and recipes. Textures
 * come from {@code tools/logistics/logistics_textures.py}.
 */
final class LogisticsData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private LogisticsData() {
    }

    static void bootstrap() {
        SharedTags.block("minecraft:mineable/axe", NS + ":" + TankKind.WOODEN.id());
        SharedTags.block("minecraft:mineable/pickaxe", NS + ":" + TankKind.METAL.id());
        PlatformDatagen.register("logistics", LogisticsData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        for (ModLogistics.Tank tank : ModLogistics.tanks()) {
            String id = tank.kind().id();
            JsonObject variant = new JsonObject();
            variant.addProperty("model", NS + ":block/" + id);
            JsonObject variants = new JsonObject();
            variants.add("", variant);
            JsonObject state = new JsonObject();
            state.add("variants", variants);
            sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
            sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, tank.kind() == TankKind.WOODEN ? woodenModel() : metalModel());
            itemModel(sink, id, NS + ":block/" + id);
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, id, dropSelf(NS + ":" + id));
        }
        JsonObject cylinder = new JsonObject();
        cylinder.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", NS + ":item/" + GasCylinderItem.ID);
        cylinder.add("textures", textures);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + GasCylinderItem.ID, cylinder);
        itemDefinition(sink, GasCylinderItem.ID, NS + ":item/" + GasCylinderItem.ID);
        recipes(sink);
    }

    /** Open-top tub: a 1-pixel bottom and four 2-pixel walls. */
    private static JsonObject woodenModel() {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        JsonObject textures = new JsonObject();
        String base = NS + ":block/" + TankKind.WOODEN.id();
        textures.addProperty("particle", base + "_side");
        textures.addProperty("side", base + "_side");
        textures.addProperty("inner", base + "_inner");
        textures.addProperty("bottom", base + "_bottom");
        model.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(element(0, 0, 0, 16, 1, 16, "bottom", "inner", "side", "side", "side", "side", true));
        elements.add(element(0, 1, 0, 16, 16, 2, null, "inner", "side", "inner", "side", "side", false));
        elements.add(element(0, 1, 14, 16, 16, 16, null, "inner", "inner", "side", "side", "side", false));
        elements.add(element(0, 1, 2, 2, 16, 14, null, "inner", null, null, "side", "inner", false));
        elements.add(element(14, 1, 2, 16, 16, 14, null, "inner", null, null, "inner", "side", false));
        model.add("elements", elements);
        return model;
    }

    /** An element; faces in the order down, up, north, south, west, east ({@code null} = no face). */
    private static JsonObject element(int x0, int y0, int z0, int x1, int y1, int z1, String down, String up,
                                      String north, String south, String west, String east, boolean cullBottom) {
        JsonObject element = new JsonObject();
        element.add("from", array(x0, y0, z0));
        element.add("to", array(x1, y1, z1));
        JsonObject faces = new JsonObject();
        String[] names = {"down", "up", "north", "south", "west", "east"};
        String[] textures = {down, up, north, south, west, east};
        for (int i = 0; i < 6; i++) {
            if (textures[i] == null) {
                continue;
            }
            JsonObject face = new JsonObject();
            face.addProperty("texture", "#" + textures[i]);
            boolean outer = (i == 0 && y0 == 0 && cullBottom) || (i == 2 && z0 == 0) || (i == 3 && z1 == 16) || (i == 4 && x0 == 0) || (i == 5 && x1 == 16);
            if (outer) {
                face.addProperty("cullface", names[i]);
            }
            faces.add(names[i], face);
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

    /** Iron frame with see-through sides (cutout). */
    private static JsonObject metalModel() {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_bottom_top");
        PlatformDatagen.cutoutRenderType(false).ifPresent(type -> model.addProperty("render_type", type));
        JsonObject textures = new JsonObject();
        String base = NS + ":block/" + TankKind.METAL.id();
        textures.addProperty("side", base + "_side");
        textures.addProperty("top", base + "_top");
        textures.addProperty("bottom", base + "_top");
        model.add("textures", textures);
        return model;
    }

    private static void itemModel(PlatformDatagen.Sink sink, String id, String model) {
        JsonObject item = new JsonObject();
        item.addProperty("parent", model);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, item);
        itemDefinition(sink, id, model);
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

    /** Recipes of machines/03 §5 (2026-11-05). */
    private static void recipes(PlatformDatagen.Sink sink) {
        String common = PlatformDatagen.commonTagNamespace();
        shaped(sink, TankKind.WOODEN.id(), "misc", new String[][]{{"P", "#minecraft:planks"}, {"S", "#minecraft:wooden_slabs"}},
            "P P", "P P", "PSP");
        shaped(sink, TankKind.METAL.id(), "misc", new String[][]{{"I", "#" + common + ":ingots/iron"}, {"G", "minecraft:glass"}},
            "IGI", "G G", "IGI");
        shaped(sink, GasCylinderItem.ID, "misc", new String[][]{{"I", "#" + common + ":ingots/iron"}, {"C", "#" + common + ":ingots/copper"}},
            " C ", "I I", " I ");
    }

    private static void shaped(PlatformDatagen.Sink sink, String result, String category, String[][] keys, String... pattern) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");
        recipe.addProperty("category", category);
        JsonObject key = new JsonObject();
        for (String[] entry : keys) {
            key.add(entry[0], PlatformDatagen.ingredient(entry[1]));
        }
        recipe.add("key", key);
        JsonArray rows = new JsonArray();
        for (String row : pattern) {
            rows.add(row);
        }
        recipe.add("pattern", rows);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + result, 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, result, recipe);
    }
}
