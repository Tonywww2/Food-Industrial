package com.tonywww.foodindustrial.platform.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Random ticks for world interactions (W01, additives/07 H11, 2026-11-10). Dirt and coarse dirt take
 * random ticks like grass does (they have none in vanilla); a hook receives the random ticks of the
 * blocks it asks for. Wired by a mixin on {@code BlockStateBase}.
 */
public final class PlatformRandomTicks {
    /** Receives random ticks of the hooked blocks. */
    @FunctionalInterface
    public interface Hook {
        void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);
    }

    private static volatile Set<Block> hooked = Set.of();
    private static Hook hook = (state, level, pos, random) -> {
    };

    private PlatformRandomTicks() {
    }

    /**
     * Whether the mod makes {@code block} take random ticks (dirt and coarse dirt). Compared by id: the
     * state caches are built while the blocks register, before the {@code Blocks} fields are set.
     */
    public static boolean forced(Block block) {
        var key = BuiltInRegistries.BLOCK.getKey(block);
        return key != null && key.getNamespace().equals("minecraft") && (key.getPath().equals("dirt") || key.getPath().equals("coarse_dirt"));
    }

    /** Sets the hook; it receives random ticks of {@link #setHooked hooked} blocks only. */
    public static void setHook(Hook value) {
        hook = value;
    }

    /** The blocks whose random ticks go to the hook (only blocks that take random ticks get any). */
    public static void setHooked(Set<Block> blocks) {
        Set<Block> set = Collections.newSetFromMap(new IdentityHashMap<>());
        set.addAll(blocks);
        hooked = set;
    }

    /** Called by the mixin at the start of every random tick. */
    public static void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (hooked.contains(state.getBlock())) {
            hook.randomTick(state, level, pos, random);
        }
    }
}
