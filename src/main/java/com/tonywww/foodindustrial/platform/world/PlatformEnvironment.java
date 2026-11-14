package com.tonywww.foodindustrial.platform.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

/**
 * Facts about a block's surroundings for the environment conditions of T0 devices and world
 * interactions (additives/07 §操作与实现规格, 2026-11-10), read the same way on every node.
 */
public final class PlatformEnvironment {
    private PlatformEnvironment() {
    }

    /**
     * @param openSky       the sky is visible from the space above the block
     * @param raining       the level has rain or a thunderstorm
     * @param rainingHere   rain falls on the space above the block
     * @param precipitation the biome has rain or snow at all (deserts do not)
     * @param day           daytime
     * @param temperature   base temperature of the biome
     * @param ocean         biome tag {@code #minecraft:is_ocean}
     * @param jungle        biome tag {@code #minecraft:is_jungle} (bamboo jungle included)
     * @param bamboo        the bamboo jungle
     */
    public record Facts(boolean openSky, boolean raining, boolean rainingHere, boolean precipitation, boolean day, float temperature,
                        boolean ocean, boolean jungle, boolean bamboo) {
    }

    /** Facts at {@code pos} (a device or world block). */
    public static Facts at(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        Holder<Biome> biome = level.getBiome(pos);
        return new Facts(level.canSeeSky(above), level.isRaining(), level.isRainingAt(above), biome.value().hasPrecipitation(), isDay(level),
            biome.value().getBaseTemperature(), biome.is(BiomeTags.IS_OCEAN), biome.is(BiomeTags.IS_JUNGLE), biome.is(Biomes.BAMBOO_JUNGLE));
    }

    private static boolean isDay(Level level) {
        //? if >=26.1 {
        /*return level.isBrightOutside();
        *///?} else {
        return level.isDay();
        //?}
    }
}
