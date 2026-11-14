package com.tonywww.foodindustrial.manual;

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
 * Generated assets and data of the T0 tools and devices (I12, additives/07 §操作与实现规格): block
 * states, simple element models, item models, loot tables, tags and the crafting recipes (game
 * settings, 2026-11-10). Textures come from {@code tools/manual/manual_textures.py}.
 */
final class ManualData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private ManualData() {
    }

    static void bootstrap() {
        for (T0Kind kind : T0Kind.values()) {
            SharedTags.block(kind == T0Kind.IRON_POT || kind.isPot() || kind == T0Kind.CROCK || kind == T0Kind.SALT_PAN
                ? "minecraft:mineable/pickaxe" : "minecraft:mineable/axe", NS + ":" + kind.id());
        }
        PlatformDatagen.register("manual", ManualData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        for (T0Kind kind : T0Kind.values()) {
            blockState(sink, kind);
            itemModel(sink, kind.id(), NS + ":block/" + kind.id());
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, kind.id(), dropSelf(NS + ":" + kind.id()));
        }
        models(sink);
        for (HandTool tool : HandTool.values()) {
            generatedItem(sink, tool.id());
        }
        generatedItem(sink, "dried_apple");
        generatedItem(sink, "dried_berries");
        tags(sink);
        recipes(sink);
    }

    // ---- block states and models ----

    private static void blockState(PlatformDatagen.Sink sink, T0Kind kind) {
        JsonObject state = new JsonObject();
        String model = NS + ":block/" + kind.id();
        if (kind == T0Kind.CROCK) {
            JsonObject variants = new JsonObject();
            variants.add("sealed=false", variant(model));
            variants.add("sealed=true", variant(model + "_sealed"));
            state.add("variants", variants);
        } else if (kind == T0Kind.SALT_PAN) {
            JsonArray multipart = new JsonArray();
            JsonObject base = new JsonObject();
            base.add("apply", variant(model));
            multipart.add(base);
            String[] sides = {"north", "east", "south", "west"};
            for (int i = 0; i < 4; i++) {
                JsonObject part = new JsonObject();
                JsonObject when = new JsonObject();
                when.addProperty(sides[i], "false");
                part.add("when", when);
                JsonObject apply = variant(NS + ":block/salt_pan_rim");
                if (i > 0) {
                    apply.addProperty("y", i * 90);
                }
                part.add("apply", apply);
                multipart.add(part);
            }
            state.add("multipart", multipart);
        } else {
            JsonObject variants = new JsonObject();
            variants.add("", variant(model));
            state.add("variants", variants);
        }
        sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, kind.id(), state);
    }

    private static JsonObject variant(String model) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", model);
        return variant;
    }

    private static void models(PlatformDatagen.Sink sink) {
        for (String pot : new String[]{"clay_pot", "iron_pot"}) {
            Model model = new Model(pot);
            model.box(2, 0, 2, 14, 1, 14);
            model.box(2, 1, 2, 14, 8, 3);
            model.box(2, 1, 13, 14, 8, 14);
            model.box(2, 1, 3, 3, 8, 13);
            model.box(13, 1, 3, 14, 8, 13);
            model.write(sink, "block/" + pot);
        }
        Model crock = new Model("crock");
        crock.box(3, 0, 3, 13, 12, 13);
        crock.box(5, 12, 5, 11, 14, 11);
        crock.write(sink, "block/crock");
        Model sealed = new Model("crock");
        sealed.box(3, 0, 3, 13, 12, 13);
        sealed.box(5, 12, 5, 11, 14, 11);
        sealed.box(4, 14, 4, 12, 15, 12);
        sealed.write(sink, "block/crock_sealed");
        Model barrel = new Model("leach_barrel");
        barrel.box(0, 0, 0, 16, 2, 16);
        barrel.box(0, 2, 0, 16, 16, 2);
        barrel.box(0, 2, 14, 16, 16, 16);
        barrel.box(0, 2, 2, 2, 16, 14);
        barrel.box(14, 2, 2, 16, 16, 14);
        barrel.write(sink, "block/leach_barrel");
        Model pan = new Model("salt_pan");
        pan.box(0, 0, 0, 16, 1, 16);
        pan.write(sink, "block/salt_pan");
        Model rim = new Model("salt_pan");
        rim.box(0, 1, 0, 16, 4, 2);
        rim.write(sink, "block/salt_pan_rim");
        Model rack = new Model("drying_rack");
        rack.box(0, 0, 0, 2, 12, 2);
        rack.box(14, 0, 0, 16, 12, 2);
        rack.box(0, 0, 14, 2, 12, 16);
        rack.box(14, 0, 14, 16, 12, 16);
        for (int z = 1; z < 16; z += 3) {
            rack.box(0, 11, z, 16, 12, Math.min(16, z + 2));
        }
        rack.write(sink, "block/drying_rack");
        Model tray = new Model("koji_tray");
        tray.box(1, 0, 1, 15, 1, 15);
        tray.box(1, 1, 1, 15, 3, 2);
        tray.box(1, 1, 14, 15, 3, 15);
        tray.box(1, 1, 2, 2, 3, 14);
        tray.box(14, 1, 2, 15, 3, 14);
        tray.write(sink, "block/koji_tray");
        Model box = new Model("fumigation_box");
        box.box(0, 0, 0, 16, 16, 16);
        box.write(sink, "block/fumigation_box");
    }

    /** A model of axis-aligned boxes with one texture ({@code block/<texture>}) on every face. */
    private static final class Model {
        private final String texture;
        private final JsonArray elements = new JsonArray();

        Model(String texture) {
            this.texture = texture;
        }

        void box(int x0, int y0, int z0, int x1, int y1, int z1) {
            JsonObject element = new JsonObject();
            element.add("from", array(x0, y0, z0));
            element.add("to", array(x1, y1, z1));
            JsonObject faces = new JsonObject();
            for (String face : new String[]{"down", "up", "north", "south", "west", "east"}) {
                JsonObject entry = new JsonObject();
                entry.addProperty("texture", "#all");
                faces.add(face, entry);
            }
            element.add("faces", faces);
            elements.add(element);
        }

        void write(PlatformDatagen.Sink sink, String path) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:block/block");
            JsonObject textures = new JsonObject();
            textures.addProperty("all", NS + ":block/" + texture);
            textures.addProperty("particle", NS + ":block/" + texture);
            model.add("textures", textures);
            model.add("elements", elements);
            sink.json(PlatformDatagen.Kind.MODEL, NS, path, model);
        }
    }

    private static JsonArray array(int... values) {
        JsonArray array = new JsonArray();
        for (int value : values) {
            array.add(value);
        }
        return array;
    }

    private static void itemModel(PlatformDatagen.Sink sink, String id, String model) {
        JsonObject item = new JsonObject();
        item.addProperty("parent", model);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, item);
        itemDefinition(sink, id, model);
    }

    /** A flat item model with {@code item/<id>}. */
    static void generatedItem(PlatformDatagen.Sink sink, String id) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/handheld");
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

    // ---- tags ----

    /** {@code value} with a {@code ?} prefix is written as an optional entry. */
    static void tag(PlatformDatagen.Sink sink, PlatformDatagen.Kind kind, String tag, List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            if (value.startsWith("?")) {
                JsonObject optional = new JsonObject();
                optional.addProperty("id", value.substring(1));
                optional.addProperty("required", false);
                array.add(optional);
            } else {
                array.add(value);
            }
        }
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        json.add("values", array);
        int colon = tag.indexOf(':');
        sink.json(kind, tag.substring(0, colon), tag.substring(colon + 1), json);
    }

    private static void tags(PlatformDatagen.Sink sink) {
        tag(sink, PlatformDatagen.Kind.BLOCK_TAG, T0Devices.HEAT_SOURCES, List.of("minecraft:campfire", "minecraft:soul_campfire"));
        tag(sink, PlatformDatagen.Kind.ITEM_TAG, Fumigation.FOOD_TAG, List.of(NS + ":dried_apple", NS + ":dried_berries"));
    }

    // ---- recipes (game settings, 2026-11-10) ----

    private static void recipes(PlatformDatagen.Sink sink) {
        String common = PlatformDatagen.commonTagNamespace();
        String stick = "#" + common + ":rods/wooden";
        String string = "minecraft:string";
        String iron = "#" + common + ":ingots/iron";
        shaped(sink, "mortar", map('S', "#foodindustrial:mortar_stones", 'K', stick), "  K", "S S", " S ");
        shaped(sink, "sieve", map('K', stick, 'T', string), "K K", "TTT", "K K");
        shaped(sink, "cloth_bag", map('T', string, 'W', "#minecraft:wool"), "T T", " W ");
        shaped(sink, "clay_pot", map('B', "minecraft:brick"), "B B", "BBB");
        shaped(sink, "iron_pot", map('I', iron), "I I", "III");
        shaped(sink, "crock", map('B', "minecraft:brick"), "B B", "B B", "BBB");
        shaped(sink, "leach_barrel", map('L', "minecraft:barrel", 'H', "minecraft:hay_block"), "H", "L");
        shaped(sink, "leach_barrel", "leach_barrel_from_wool", map('L', "minecraft:barrel", 'W', "#minecraft:wool"), "W", "L");
        shaped(sink, "salt_pan", map('B', "minecraft:brick"), "B B", "BBB");
        shaped(sink, "drying_rack", map('K', stick, 'P', "#minecraft:planks"), "PPP", "K K", "K K");
        shaped(sink, "koji_tray", map('B', "minecraft:bamboo"), "B B", "BBB", "BBB");
        shaped(sink, "fumigation_box", map('P', "#minecraft:planks", 'I', iron), "PPP", "P P", "PIP");
        tag(sink, PlatformDatagen.Kind.ITEM_TAG, NS + ":mortar_stones", List.of("minecraft:stone", "minecraft:calcite"));

        // 07 #107: the liver of a cod (game setting)
        JsonObject liver = new JsonObject();
        liver.addProperty("type", "minecraft:crafting_shapeless");
        liver.addProperty("category", "misc");
        JsonArray ingredients = new JsonArray();
        ingredients.add(PlatformDatagen.ingredient("minecraft:cod"));
        liver.add("ingredients", ingredients);
        liver.add("result", PlatformDatagen.recipeResult(NS + ":fish_liver", 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "fish_liver", liver);
    }

    private static Map<Character, String> map(Object... pairs) {
        Map<Character, String> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((Character) pairs[i], (String) pairs[i + 1]);
        }
        return map;
    }

    static void shaped(PlatformDatagen.Sink sink, String result, Map<Character, String> keys, String... pattern) {
        shaped(sink, result, result, keys, pattern);
    }

    /** A shaped recipe for {@code result} written as recipe {@code name}. */
    static void shaped(PlatformDatagen.Sink sink, String result, String name, Map<Character, String> keys, String... pattern) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");
        recipe.addProperty("category", "misc");
        JsonObject key = new JsonObject();
        keys.forEach((symbol, value) -> key.add(String.valueOf(symbol), PlatformDatagen.ingredient(value)));
        recipe.add("key", key);
        JsonArray rows = new JsonArray();
        List<String> lines = new ArrayList<>(List.of(pattern));
        lines.forEach(rows::add);
        recipe.add("pattern", rows);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + result, 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, name, recipe);
    }
}
