package com.tonywww.foodindustrial.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;

import java.util.Map;

/**
 * Effect of one environment capability on a recipe (machines/01 §3.1 {@code environment_factors},
 * 2026-11-10): while the device offers the capability, the time and the main product's amount are
 * multiplied. The environment never changes purity (additives/10 §2.2 rule 6).
 *
 * @param time  time multiplier
 * @param yield main product multiplier
 */
public record EnvironmentFactor(double time, double yield) {
    public static final Codec<EnvironmentFactor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        StrictCodecs.optional("time", Codec.doubleRange(0.05, 20.0), 1.0).forGetter(EnvironmentFactor::time),
        StrictCodecs.optional("yield", Codec.doubleRange(0.05, 20.0), 1.0).forGetter(EnvironmentFactor::yield)
    ).apply(instance, EnvironmentFactor::new));

    /** An environment word ({@code cold_biome}), stored with its category ({@code environment.cold_biome}). */
    private static final Codec<String> KEY = Codec.STRING.comapFlatMap(word -> {
        try {
            Capabilities.check("environment." + word);
            return DataResult.success("environment." + word);
        } catch (IllegalArgumentException exception) {
            return DataResult.error(exception::getMessage);
        }
    }, key -> key.substring("environment.".length()));

    /** {@code { "cold_biome": { "time": 2.0 } }}, keyed by capability. */
    public static final Codec<Map<String, EnvironmentFactor>> MAP = Codec.unboundedMap(KEY, CODEC);

    /** Product of the time multipliers of the capabilities {@code current} offers. */
    public static double time(Map<String, EnvironmentFactor> factors, Capabilities current) {
        double product = 1.0;
        for (Map.Entry<String, EnvironmentFactor> entry : factors.entrySet()) {
            if (current.keys().contains(entry.getKey())) {
                product *= entry.getValue().time();
            }
        }
        return product;
    }

    /** Product of the yield multipliers of the capabilities {@code current} offers. */
    public static double yield(Map<String, EnvironmentFactor> factors, Capabilities current) {
        double product = 1.0;
        for (Map.Entry<String, EnvironmentFactor> entry : factors.entrySet()) {
            if (current.keys().contains(entry.getKey())) {
                product *= entry.getValue().yield();
            }
        }
        return product;
    }
}
