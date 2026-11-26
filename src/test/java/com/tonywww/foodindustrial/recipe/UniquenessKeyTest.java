package com.tonywww.foodindustrial.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/** The data generation's conflict key (machines/01 §3.2) on food-line recipes (I38). */
class UniquenessKeyTest {
    private static JsonObject json(String text) {
        return JsonParser.parseString(text).getAsJsonObject();
    }

    @Test
    void amountsDoNotTellRecipesApart() {
        String a = UniquenessKey.of(json("""
            {"type":"foodindustrial:forming","mode":"dough_mixing","inputs":[{"ingredient":"#foodindustrial:materials/flour","count":3},
             {"fluid":{"type":"foodindustrial:fluid","tag":"foodindustrial:water","amount":250}}],"quality":{"items":["state"]}}"""));
        String b = UniquenessKey.of(json("""
            {"type":"foodindustrial:forming","mode":"dough_mixing","inputs":[{"fluid":{"type":"foodindustrial:fluid","tag":"foodindustrial:water","amount":500}},
             {"ingredient":"#foodindustrial:materials/flour","count":1}]}"""));
        assertEquals(a, b, "same type, mode and input materials conflict whatever the amounts and quality slots");
    }

    @Test
    void modeCatalystAndSemiFinishedKeysTellThemApart() {
        String dough = """
            {"type":"foodindustrial:cooking","mode":"%s","inputs":[{"semi_finished":"%s"}]%s}""";
        String steamed = UniquenessKey.of(json(dough.formatted("steaming", "steamed_bun|dough", "")));
        assertNotEquals(steamed, UniquenessKey.of(json(dough.formatted("water_boiling", "steamed_bun|dough", ""))));
        assertNotEquals(steamed, UniquenessKey.of(json(dough.formatted("steaming", "dumpling|raw", ""))));
        assertNotEquals(steamed, UniquenessKey.of(json(dough.formatted("steaming", "steamed_bun|dough",
            ",\"catalyst\":{\"ingredient\":\"foodindustrial:loaf_pan\",\"runs\":64}"))));
    }

    /** RecipeGen.uniquenessKey without loading the data generator's game classes. */
    private static final class UniquenessKey {
        static String of(JsonObject json) {
            return RecipeGen.uniquenessKey(json);
        }
    }
}
