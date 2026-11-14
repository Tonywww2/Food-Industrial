package com.tonywww.foodindustrial.food;

import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

/** Generated item models (and 26.1 item definitions) of the finished products (I21). */
final class FoodData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private FoodData() {
    }

    static void bootstrap() {
        PlatformDatagen.register("foods", FoodData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        for (String id : FinishedFoods.ownIds()) {
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
    }
}
