package com.tonywww.foodindustrial.platform;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

/**
 * Block property helpers whose vanilla names differ between versions.
 */
public final class PlatformBlocks {
    private PlatformBlocks() {
    }

    /**
     * A full copy of a vanilla block's properties ({@code copy} on 1.20.1, {@code ofFullCopy} from
     * 1.20.5 on), for blocks that behave like it.
     */
    public static BlockBehaviour.Properties copy(BlockBehaviour block) {
        //? if forge {
        return BlockBehaviour.Properties.copy(block);
        //?} else {
        /*return BlockBehaviour.Properties.ofFullCopy(block);
        *///?}
    }

    /** A block that drops experience ({@code min}–{@code max}) when mined without Silk Touch, like ores. */
    public static Function<BlockBehaviour.Properties, Block> experienceDropping(int min, int max) {
        //? if forge {
        return properties -> new DropExperienceBlock(properties, UniformInt.of(min, max));
        //?} else {
        /*return properties -> new DropExperienceBlock(UniformInt.of(min, max), properties);
        *///?}
    }

    /** {@code noCollission()} before 26.1, {@code noCollision()} from 26.1 on. */
    public static BlockBehaviour.Properties noCollision(BlockBehaviour.Properties properties) {
        //? if >=26.1 {
        /*return properties.noCollision();
        *///?} else {
        return properties.noCollission();
        //?}
    }
}
