package com.tonywww.foodindustrial.food.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipeConditions;

import java.util.List;

/**
 * Item models and recipes of the molds and containers (I40). Container recipes follow machines/05 §4; the plastic
 * casing and the PET bottle wait for their materials (PVDC, batch 5; PET, batch 7). Mold recipes as the user
 * confirmed them (2026-11-18, machines/05 §7 #3).
 */
final class FoodToolData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private FoodToolData() {
    }

    static void bootstrap() {
        PlatformDatagen.register("food_tools", FoodToolData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        for (FoodTools.Entry entry : FoodTools.MOLDS) {
            model(sink, entry.id());
        }
        for (FoodTools.Entry entry : FoodTools.CONTAINERS) {
            model(sink, entry.id());
        }
        String common = PlatformDatagen.commonTagNamespace();
        // machines/05 §4: amounts as designed; the two paper recipes take shapes so that they differ
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "paper_bag", shaped("paper_bag", 3, List.of("P", "P", "P"), "P", "minecraft:paper"));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "paper_cup", shaped("paper_cup", 3, List.of("P P", " P "), "P", "minecraft:paper"));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "glass_jar", shapeless("glass_jar", 3,
            "minecraft:glass", "minecraft:glass", "minecraft:glass", "#" + common + ":nuggets/iron"));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "plastic_bag", shapeless("plastic_bag", 4, "#" + NS + ":chemicals/polyethylene"));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "tea_bag", shapeless("tea_bag", 8, "minecraft:paper"));
        String tin = common + ":ingots/tin";
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "tin_can", PlatformRecipeConditions.whenTagNotEmpty(
            shapeless("tin_can", 4, "#" + common + ":ingots/iron", "#" + common + ":ingots/iron", "#" + tin), tin));
        String aluminum = common + ":ingots/aluminum";
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "aluminum_can", PlatformRecipeConditions.whenTagNotEmpty(
            shapeless("aluminum_can", 4, "#" + aluminum, "#" + aluminum), aluminum));
        // molds (I40, user 2026-11-18): one each on the workbench
        String iron = "#" + common + ":ingots/iron";
        String nugget = "#" + common + ":nuggets/iron";
        String stick = "#" + common + ":rods/wooden";
        String planks = "#minecraft:planks";
        String bars = "minecraft:iron_bars";
        String string = "minecraft:string";
        mold(sink, "granulating_screen", keys('S', stick, 'B', bars), " S ", "SBS", " S ");
        mold(sink, "tablet_die", keys('I', iron, 'N', nugget), "I", "N", "I");
        mold(sink, "filling_mold", keys('I', iron, 'N', nugget), "N N", " I ");
        mold(sink, "stretching_mold", keys('I', iron, 'S', stick), "SIS");
        mold(sink, "extrusion_die", keys('I', iron, 'B', bars), "IBI");
        mold(sink, "cake_mold", keys('I', iron, 'N', nugget), "I I", "NIN");
        mold(sink, "loaf_pan", keys('I', iron, 'N', nugget), "N N", "III");
        mold(sink, "tart_pan", keys('I', iron, 'N', nugget), "NIN");
        mold(sink, "baking_iron", keys('I', iron, 'S', stick), "IIS");
        mold(sink, "mooncake_mold", keys('P', planks, 'S', stick), "S", "P");
        mold(sink, "cheese_mold", keys('P', planks, 'N', nugget), "P P", "PNP");
        mold(sink, "popsicle_mold", keys('I', iron, 'N', nugget), "NNN", "I I");
        mold(sink, "stuffing_nozzle", keys('I', iron, 'N', nugget), "I I", " N ");
        mold(sink, "ham_mold", keys('I', iron, 'N', nugget), "INI", "I I");
        mold(sink, "tofu_mold", keys('P', planks, 'T', string), "P P", "PTP");
        mold(sink, "chocolate_mold", keys('N', nugget), "N N", "NNN");
        mold(sink, "hard_candy_mold", keys('I', iron, 'N', nugget), "NIN", "N N");
        mold(sink, "lollipop_mold", keys('S', stick, 'N', nugget), "NSN", "N N");
        mold(sink, "starch_mogul", keys('P', planks, 'S', stick), "S S", "PPP");
        mold(sink, "blow_mold", keys('I', iron, 'B', bars), "IBI", "I I");
        // panning drum: a mold-like catalyst of R27 panning (foods/20 §1; I41 draft, to review)
        mold(sink, "panning_drum", keys('C', "#" + common + ":ingots/copper"), "C C", "CCC");
        mold(sink, "oak_barrel", keys('P', planks, 'I', iron), "PIP", "P P", "PIP");
        // glass ×3 with the blow mold (kept) → ×4, on the workbench (foods/21 §1.2)
        JsonObject bottle = new JsonObject();
        bottle.addProperty("type", NS + ":food_crafting");
        bottle.addProperty("process", "forming");
        JsonArray ingredients = new JsonArray();
        JsonObject glass = new JsonObject();
        glass.add("ingredient", PlatformDatagen.ingredient("minecraft:glass"));
        glass.addProperty("count", 3);
        ingredients.add(glass);
        bottle.add("ingredients", ingredients);
        JsonArray tools = new JsonArray();
        tools.add(PlatformDatagen.ingredient(NS + ":blow_mold"));
        bottle.add("tools", tools);
        JsonObject result = new JsonObject();
        result.addProperty("item", NS + ":glass_bottle");
        result.addProperty("count", 4);
        bottle.add("result", result);
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "glass_bottle", bottle);
    }

    private static void model(PlatformDatagen.Sink sink, String id) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", NS + ":item/" + id);
        model.add("textures", textures);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, model);
        JsonObject inner = new JsonObject();
        inner.addProperty("type", "minecraft:model");
        inner.addProperty("model", NS + ":item/" + id);
        JsonObject definition = new JsonObject();
        definition.add("model", inner);
        sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, id, definition);
    }

    private static java.util.Map<Character, String> keys(Object... pairs) {
        java.util.Map<Character, String> map = new java.util.LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((Character) pairs[i], (String) pairs[i + 1]);
        }
        return map;
    }

    private static void mold(PlatformDatagen.Sink sink, String id, java.util.Map<Character, String> keys, String... pattern) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");
        recipe.addProperty("category", "equipment");
        JsonArray rows = new JsonArray();
        for (String row : pattern) {
            rows.add(row);
        }
        recipe.add("pattern", rows);
        JsonObject key = new JsonObject();
        keys.forEach((symbol, value) -> key.add(String.valueOf(symbol), PlatformDatagen.ingredient(value)));
        recipe.add("key", key);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + id, 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, id, recipe);
    }

    private static JsonObject shapeless(String result, int count, String... ingredients) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", "misc");
        JsonArray list = new JsonArray();
        for (String ingredient : ingredients) {
            list.add(PlatformDatagen.ingredient(ingredient));
        }
        recipe.add("ingredients", list);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + result, count));
        return recipe;
    }

    private static JsonObject shaped(String result, int count, List<String> rows, String key, String ingredient) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");
        recipe.addProperty("category", "misc");
        JsonArray pattern = new JsonArray();
        rows.forEach(pattern::add);
        recipe.add("pattern", pattern);
        JsonObject keys = new JsonObject();
        keys.add(key, PlatformDatagen.ingredient(ingredient));
        recipe.add("key", keys);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + result, count));
        return recipe;
    }
}
