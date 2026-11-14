package com.tonywww.foodindustrial.food.metabolism;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

/** Generated data of the metabolism: the alcohol poisoning damage type and its tag (I35). */
final class MetabolismData {
    private MetabolismData() {
    }

    static void bootstrap() {
        PlatformDatagen.register("metabolism", MetabolismData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        String path = Metabolism.ALCOHOL_POISONING.substring(Metabolism.ALCOHOL_POISONING.indexOf(':') + 1);
        JsonObject type = new JsonObject();
        type.addProperty("message_id", FoodIndustrial.MOD_ID + "." + path);
        type.addProperty("scaling", "never");
        type.addProperty("exhaustion", 0.0);
        sink.json(PlatformDatagen.Kind.DATA, FoodIndustrial.MOD_ID, "damage_type/" + path, type);
        JsonObject tag = new JsonObject();
        JsonArray values = new JsonArray();
        values.add(Metabolism.ALCOHOL_POISONING);
        tag.add("values", values);
        sink.json(PlatformDatagen.Kind.DATA, "minecraft", "tags/damage_type/bypasses_armor", tag);
    }
}
