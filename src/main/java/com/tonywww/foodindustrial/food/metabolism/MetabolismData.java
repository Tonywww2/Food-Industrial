package com.tonywww.foodindustrial.food.metabolism;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

/** Generated data of the metabolism: the alcohol and nitrite poisoning damage types and their tag (I35, I45). */
final class MetabolismData {
    private MetabolismData() {
    }

    static void bootstrap() {
        PlatformDatagen.register("metabolism", MetabolismData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        JsonArray values = new JsonArray();
        for (String id : new String[] {Metabolism.ALCOHOL_POISONING, Metabolism.NITRITE_POISONING}) {
            String path = id.substring(id.indexOf(':') + 1);
            JsonObject type = new JsonObject();
            type.addProperty("message_id", FoodIndustrial.MOD_ID + "." + path);
            type.addProperty("scaling", "never");
            type.addProperty("exhaustion", 0.0);
            sink.json(PlatformDatagen.Kind.DATA, FoodIndustrial.MOD_ID, "damage_type/" + path, type);
            values.add(id);
        }
        JsonObject tag = new JsonObject();
        tag.add("values", values);
        sink.json(PlatformDatagen.Kind.DATA, "minecraft", "tags/damage_type/bypasses_armor", tag);
    }
}
