package com.tonywww.foodindustrial.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.tonywww.foodindustrial.core.Tier;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionsTest {
    private static Conditions parse(String json) {
        return Conditions.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json)).result().orElseThrow();
    }

    @Test
    void parsesByVocabularyCategory() {
        String json = "{\"min_tier\":\"t3\",\"temperature\":\"high\",\"methods\":[\"centrifuge\",\"filtration\"],"
            + "\"sterile\":true,\"environment\":[\"open_sky\",\"clear_weather\"]}";
        Conditions conditions = parse(json);
        assertEquals(Tier.T3, conditions.minTier());
        assertEquals(Set.of("temperature.high", "method.centrifuge", "method.filtration", "sterile.sterile",
            "environment.open_sky", "environment.clear_weather"), conditions.required().keys());
        JsonElement encoded = Conditions.CODEC.encodeStart(JsonOps.INSTANCE, conditions).result().orElseThrow();
        assertEquals(JsonParser.parseString(json), encoded);
    }

    @Test
    void rejectsUnknownWords() {
        assertTrue(Conditions.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"temperature\":\"lukewarm\"}")).result().isEmpty());
        assertTrue(Conditions.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"colour\":\"red\"}")).result().isEmpty());
        assertTrue(Conditions.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"random\":\"chance\"}")).result().isEmpty());
        assertTrue(Conditions.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"min_tier\":\"t9\"}")).result().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> Capabilities.of("method.teleport"));
    }

    @Test
    void capabilitiesCoverRequirements() {
        Capabilities device = Capabilities.of("temperature.heated", "temperature.high", "method.open_flame", "pressure.vacuum");
        assertTrue(device.covers(parse("{\"temperature\":\"high\",\"methods\":[\"open_flame\"]}").required()));
        assertTrue(device.covers(Conditions.NONE.required()));
        assertFalse(device.covers(parse("{\"temperature\":\"extreme\"}").required()));
        assertFalse(Capabilities.NONE.covers(parse("{\"sterile\":true}").required()));
        assertTrue(parse("{\"sterile\":false}").required().isEmpty());
        assertEquals(Set.of("temperature.extreme"), device.missing(parse("{\"temperature\":\"extreme\"}").required()));
        Capabilities wide = device.with(Capabilities.of("method.centrifuge"));
        assertTrue(wide.covers(Capabilities.of("method.centrifuge", "pressure.vacuum")));
    }

    @Test
    void defaultRulesFollowTheRecipe() {
        Conditions plain = Conditions.NONE;
        Conditions fractions = parse("{\"methods\":[\"multi_fraction\"]}");
        Conditions membrane = parse("{\"methods\":[\"membrane\"]}");
        assertEquals(PurityRule.removal(75), ProcessTypes.DISTILLING.defaultRule(plain, true));
        assertEquals(PurityRule.removal(90), ProcessTypes.DISTILLING.defaultRule(fractions, true));
        assertEquals(PurityRule.removal(75), ProcessTypes.PURIFYING.defaultRule(plain, true));
        assertEquals(PurityRule.removal(90), ProcessTypes.PURIFYING.defaultRule(membrane, true));
        assertEquals(PurityRule.KEEP, ProcessTypes.GRINDING.defaultRule(plain, true));
        assertEquals(PurityRule.removal(99), ProcessTypes.CULTURING.defaultRule(plain, true));
        assertEquals(PurityRule.SOURCE_DEFAULT, ProcessTypes.FERMENTING.defaultRule(plain, true));
        assertEquals(PurityRule.SOURCE_DEFAULT, ProcessTypes.CRYSTALLIZING.defaultRule(plain, false));
        assertEquals(PurityRule.SOURCE_DEFAULT, ProcessTypes.HARVESTING.defaultRule(plain, true));
        assertEquals(19, ProcessTypes.recipeTypes().size());
        assertEquals(21, ProcessTypes.all().size());
    }

    @Test
    void purityRuleCodec() {
        assertTrue(PurityRule.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"mode\":\"removal\"}")).result().isEmpty());
        assertTrue(PurityRule.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"mode\":\"keep\",\"value\":3}")).result().isEmpty());
        assertTrue(PurityRule.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"mode\":\"source\",\"value\":83.25}")).result().isEmpty());
        JsonElement json = JsonParser.parseString("{\"mode\":\"source\",\"value\":3.5,\"max\":97}");
        PurityRule rule = PurityRule.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
        assertEquals(new PurityRule(PurityRule.Mode.SOURCE, 3.5, 97), rule);
        assertEquals(json.toString(), PurityRule.CODEC.encodeStart(JsonOps.INSTANCE, rule).result().orElseThrow().toString());
    }
}
