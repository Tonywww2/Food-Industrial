package com.tonywww.foodindustrial.recipe;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.tonywww.foodindustrial.platform.world.PlatformEnvironment;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Environment capabilities and factors (additives/07 §环境, machines/01 §3.1; 2026-11-10). */
class EnvironmentTest {
    private static PlatformEnvironment.Facts facts(boolean openSky, boolean raining, boolean rainingHere, boolean precipitation,
                                                   boolean day, float temperature, boolean jungle, boolean bamboo) {
        return new PlatformEnvironment.Facts(openSky, raining, rainingHere, precipitation, day, temperature, false, jungle, bamboo);
    }

    @Test
    void clearDayInTheOpen() {
        Capabilities capabilities = Environment.of(facts(true, false, false, true, true, 0.8F, false, false));
        assertEquals(Set.of("environment.open_sky", "environment.clear_weather", "environment.daytime", "environment.non_cold_biome"),
            capabilities.keys());
    }

    @Test
    void rainUnderAndOutsideARoof() {
        Capabilities open = Environment.of(facts(true, true, true, true, true, 0.8F, false, false));
        assertTrue(open.keys().contains("environment.rain"));
        assertTrue(!open.keys().contains("environment.clear_weather"));
        Capabilities sheltered = Environment.of(facts(false, true, false, true, false, 0.8F, false, false));
        assertEquals(Set.of("environment.sheltered", "environment.non_cold_biome"), sheltered.keys());
        // a dry biome stays clear while it rains elsewhere
        Capabilities desert = Environment.of(facts(true, true, false, false, true, 2.0F, false, false));
        assertTrue(desert.keys().containsAll(Set.of("environment.clear_weather", "environment.hot_biome")));
    }

    @Test
    void biomes() {
        assertTrue(Environment.of(facts(true, false, false, true, true, 0.0F, false, false)).keys().contains("environment.cold_biome"));
        Capabilities bambooJungle = Environment.of(facts(false, false, false, true, true, 0.95F, true, true));
        assertTrue(bambooJungle.keys().contains("environment.bamboo_biome"));
        assertTrue(!bambooJungle.keys().contains("environment.jungle_biome"));
        assertTrue(Environment.of(facts(false, false, false, true, true, 0.95F, true, false)).keys().contains("environment.jungle_biome"));
    }

    @Test
    void factorsMultiplyWhileOffered() {
        Map<String, EnvironmentFactor> factors = EnvironmentFactor.MAP.parse(JsonOps.INSTANCE,
            JsonParser.parseString("{\"hot_biome\":{\"time\":0.5,\"yield\":1.5},\"cold_biome\":{\"time\":2.0}}")).result().orElseThrow();
        Capabilities hot = Capabilities.of("environment.hot_biome", "environment.open_sky");
        assertEquals(0.5, EnvironmentFactor.time(factors, hot));
        assertEquals(1.5, EnvironmentFactor.yield(factors, hot));
        Capabilities cold = Capabilities.of("environment.cold_biome");
        assertEquals(2.0, EnvironmentFactor.time(factors, cold));
        assertEquals(1.0, EnvironmentFactor.yield(factors, cold));
        assertTrue(EnvironmentFactor.MAP.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"sunny\":{\"time\":2.0}}")).result().isEmpty());
    }
}
