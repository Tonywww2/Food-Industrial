package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.platform.world.PlatformEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Environment capabilities of a position (additives/07 §操作与实现规格, 2026-11-10): what T0 devices
 * and world interactions offer to the {@code environment} conditions of recipes. Computed when a
 * device or a world event needs them, never cached across batches.
 */
public final class Environment {
    /** Biome temperature below which a biome is cold (where vanilla snows at sea level). */
    public static final float COLD_BELOW = 0.15F;
    /** Biome temperature above which a biome is hot (deserts, savannas, badlands). */
    public static final float HOT_ABOVE = 1.0F;

    /** Every environment word: what a device placed anywhere may offer. */
    public static final Capabilities ALL = Capabilities.of(
        "environment.open_sky", "environment.sheltered", "environment.clear_weather", "environment.rain", "environment.daytime",
        "environment.cold_biome", "environment.non_cold_biome", "environment.hot_biome", "environment.ocean_biome",
        "environment.jungle_biome", "environment.bamboo_biome", "environment.coastal");

    private Environment() {
    }

    /** Environment capabilities at {@code pos}. */
    public static Capabilities at(Level level, BlockPos pos) {
        Capabilities here = of(PlatformEnvironment.at(level, pos));
        return coastal(level, pos) ? here.with(COASTAL) : here;
    }

    private static final Capabilities COASTAL = Capabilities.of("environment.coastal");

    /**
     * Coastal (I43, additives/07 §H16): an ocean or beach biome, and water on one of the six sides. The seawater
     * pumps need it.
     */
    public static boolean coastal(Level level, BlockPos pos) {
        var biome = level.getBiome(pos);
        if (!biome.is(net.minecraft.tags.BiomeTags.IS_OCEAN) && !biome.is(net.minecraft.tags.BiomeTags.IS_BEACH)) {
            return false;
        }
        for (net.minecraft.core.Direction side : net.minecraft.core.Direction.values()) {
            if (level.getFluidState(pos.relative(side)).is(net.minecraft.tags.FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    /** Capabilities from the facts of a position. */
    public static Capabilities of(PlatformEnvironment.Facts facts) {
        List<String> keys = new ArrayList<>(6);
        keys.add(facts.openSky() ? "environment.open_sky" : "environment.sheltered");
        if (!facts.raining() || !facts.precipitation()) {
            keys.add("environment.clear_weather");
        }
        if (facts.rainingHere()) {
            keys.add("environment.rain");
        }
        if (facts.day()) {
            keys.add("environment.daytime");
        }
        keys.add(facts.temperature() < COLD_BELOW ? "environment.cold_biome" : "environment.non_cold_biome");
        if (facts.temperature() > HOT_ABOVE) {
            keys.add("environment.hot_biome");
        }
        if (facts.ocean()) {
            keys.add("environment.ocean_biome");
        }
        if (facts.bamboo()) {
            keys.add("environment.bamboo_biome");
        } else if (facts.jungle()) {
            keys.add("environment.jungle_biome");
        }
        return Capabilities.of(keys);
    }
}
