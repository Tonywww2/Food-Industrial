package com.tonywww.foodindustrial.platform.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

/**
 * Replaces the bucket a player fills from a liquid source block (seawater from water in ocean and
 * beach biomes, additives/08 §C). Called by a mixin on {@code LiquidBlock.pickupBlock}.
 */
public final class PlatformFluidPickup {
    /** Decides the filled container; returns {@code taken} to keep it. */
    @FunctionalInterface
    public interface Hook {
        ItemStack picked(LevelAccessor level, BlockPos pos, ItemStack taken);
    }

    private static Hook hook = (level, pos, taken) -> taken;

    private PlatformFluidPickup() {
    }

    public static void setHook(Hook value) {
        hook = value;
    }

    public static ItemStack picked(LevelAccessor level, BlockPos pos, ItemStack taken) {
        return taken.isEmpty() ? taken : hook.picked(level, pos, taken);
    }
}
