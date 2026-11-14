package com.tonywww.foodindustrial.world.interaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

import java.util.List;

/**
 * Generated data of the world interactions (I12): the knife, the liquid bottle, the H11 nitre soil
 * transformation and the H15 harvests (latex from rubber logs, resin from spruce logs), with their
 * tags. Numbers are game settings (2026-11-10).
 */
final class WorldInteractionData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private WorldInteractionData() {
    }

    static void bootstrap() {
        PlatformDatagen.register("world interactions", WorldInteractionData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        String common = PlatformDatagen.commonTagNamespace();
        item(sink, "flint_knife", "minecraft:item/handheld");
        item(sink, FluidBottleItem.ID, "minecraft:item/generated");
        tag(sink, PlatformDatagen.Kind.ITEM_TAG, common + ":tools/knives", List.of(NS + ":flint_knife"));
        tag(sink, PlatformDatagen.Kind.ITEM_TAG, WorldInteractions.KNIVES, List.of("?#" + common + ":tools/knives", NS + ":flint_knife"));
        tag(sink, PlatformDatagen.Kind.BLOCK_TAG, NS + ":nitre_soil_sources", List.of("minecraft:dirt", "minecraft:coarse_dirt"));
        tag(sink, PlatformDatagen.Kind.BLOCK_TAG, NS + ":harvest/latex", List.of("#" + NS + ":rubber_logs", "?#" + common + ":logs/rubber"));
        tag(sink, PlatformDatagen.Kind.BLOCK_TAG, NS + ":harvest/resin", List.of("#minecraft:spruce_logs"));
        tag(sink, PlatformDatagen.Kind.ENTITY_TAG, NS + ":livestock", List.of("minecraft:cow", "minecraft:mooshroom", "minecraft:sheep",
            "minecraft:pig", "minecraft:chicken", "minecraft:horse", "minecraft:donkey", "minecraft:mule", "minecraft:goat", "minecraft:llama"));

        JsonObject knife = new JsonObject();
        knife.addProperty("type", "minecraft:crafting_shaped");
        knife.addProperty("category", "equipment");
        JsonObject key = new JsonObject();
        key.add("F", PlatformDatagen.ingredient("minecraft:flint"));
        key.add("S", PlatformDatagen.ingredient("#" + common + ":rods/wooden"));
        knife.add("key", key);
        JsonArray pattern = new JsonArray();
        pattern.add(" F");
        pattern.add("S ");
        knife.add("pattern", pattern);
        knife.add("result", PlatformDatagen.recipeResult(NS + ":flint_knife", 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "flint_knife", knife);

        JsonObject bottle = new JsonObject();
        bottle.addProperty("type", "minecraft:crafting_shapeless");
        bottle.addProperty("category", "misc");
        JsonArray ingredients = new JsonArray();
        ingredients.add(PlatformDatagen.ingredient("minecraft:glass_bottle"));
        bottle.add("ingredients", ingredients);
        bottle.add("result", PlatformDatagen.recipeResult(NS + ":" + FluidBottleItem.ID, 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, FluidBottleItem.ID, bottle);

        JsonObject nitre = new JsonObject();
        nitre.addProperty("type", NS + ":block_transform");
        nitre.addProperty("block", "#" + NS + ":nitre_soil_sources");
        nitre.addProperty("result", NS + ":nitre_soil");
        nitre.addProperty("random_tick_chance", 0.01);
        nitre.addProperty("entities", "#" + NS + ":livestock");
        nitre.addProperty("entity_chance", 0.1);
        JsonObject requirements = new JsonObject();
        JsonArray environment = new JsonArray();
        environment.add("sheltered");
        requirements.add("environment", environment);
        nitre.add("requirements", requirements);
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "world/nitre_soil", nitre);

        harvest(sink, "latex", "#" + NS + ":harvest/latex");
        harvest(sink, "resin", "#" + NS + ":harvest/resin");
    }

    private static void harvest(PlatformDatagen.Sink sink, String fluid, String block) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", NS + ":harvesting");
        recipe.addProperty("block", block);
        recipe.add("tool", PlatformDatagen.ingredient("#" + WorldInteractions.KNIVES));
        recipe.add("container", PlatformDatagen.ingredient("minecraft:glass_bottle"));
        JsonObject result = new JsonObject();
        result.addProperty("fluid", NS + ":" + fluid);
        result.addProperty("amount", FluidBottleItem.CAPACITY);
        recipe.add("result", result);
        recipe.addProperty("cooldown", 6000);
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "world/harvest_" + fluid, recipe);
    }

    private static void item(PlatformDatagen.Sink sink, String id, String parent) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", parent);
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

    /** {@code value} with a {@code ?} prefix is written as an optional entry. */
    private static void tag(PlatformDatagen.Sink sink, PlatformDatagen.Kind kind, String tag, List<String> values) {
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
}
