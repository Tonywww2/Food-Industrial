package com.tonywww.foodindustrial.food;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The packaged forms of the products in foods.json (tools/foods/forms.json, I47, I49). */
class FoodFormsTest {
    private static Map<String, JsonObject> foods() throws Exception {
        Map<String, JsonObject> result = new HashMap<>();
        try (Reader reader = new InputStreamReader(FoodFormsTest.class.getResourceAsStream(FinishedFoods.RESOURCE), StandardCharsets.UTF_8)) {
            for (JsonElement element : JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("foods")) {
                result.put(element.getAsJsonObject().get("id").getAsString(), element.getAsJsonObject());
            }
        }
        return result;
    }

    @Test
    void looseFreshFoodsStackTo16() throws Exception {
        Map<String, JsonObject> foods = foods();
        // foods/01 §3.2: loose fresh foods 16, packaged and dry goods 64 (no row)
        assertEquals(16, foods.get("dumplings").get("stack").getAsInt());
        assertEquals(16, foods.get("white_bread").get("stack").getAsInt());
        assertFalse(foods.get("mahua").has("stack"), "crisp dry goods stack to 64");
        assertFalse(foods.get("dumplings").has("container"));
        assertEquals("plastic_bag", foods.get("instant_noodles").get("container").getAsString());
        assertEquals("paper_bag", foods.get("dried_noodles").get("container").getAsString());
        // rice pudding is a bulk fluid jarred; whipping cream's bulk form is registered for the cream cake
        assertEquals("glass_jar", foods.get("rice_pudding").get("container").getAsString());
        assertTrue(foods.get("rice_pudding").has("fluid"));
        assertTrue(foods.get("whipping_cream").has("fluid"));
    }
}
