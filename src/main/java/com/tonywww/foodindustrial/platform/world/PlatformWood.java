package com.tonywww.foodindustrial.platform.world;

import com.tonywww.foodindustrial.platform.mixin.FireBlockAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Wooden building blocks whose constructors differ between versions. They use the vanilla oak block
 * set and wood type for sounds and behaviour (no signs or boats yet, so no own wood type is needed).
 */
public final class PlatformWood {
    /** Ticks a wooden button stays pressed (vanilla). */
    private static final int WOODEN_BUTTON_TICKS = 30;

    private PlatformWood() {
    }

    public static Function<BlockBehaviour.Properties, Block> stairs(Supplier<? extends Block> base) {
        return properties -> new StairBlock(base.get().defaultBlockState(), properties);
    }

    public static Function<BlockBehaviour.Properties, Block> door() {
        //? if forge {
        return properties -> new DoorBlock(properties, BlockSetType.OAK);
        //?} else {
        /*return properties -> new DoorBlock(BlockSetType.OAK, properties);
        *///?}
    }

    public static Function<BlockBehaviour.Properties, Block> trapdoor() {
        //? if forge {
        return properties -> new TrapDoorBlock(properties, BlockSetType.OAK);
        //?} else {
        /*return properties -> new TrapDoorBlock(BlockSetType.OAK, properties);
        *///?}
    }

    public static Function<BlockBehaviour.Properties, Block> button() {
        //? if forge {
        return properties -> new ButtonBlock(properties, BlockSetType.OAK, WOODEN_BUTTON_TICKS, true);
        //?} else {
        /*return properties -> new ButtonBlock(BlockSetType.OAK, WOODEN_BUTTON_TICKS, properties);
        *///?}
    }

    public static Function<BlockBehaviour.Properties, Block> pressurePlate() {
        //? if forge {
        return properties -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, properties, BlockSetType.OAK);
        //?} else {
        /*return properties -> new PressurePlateBlock(BlockSetType.OAK, properties);
        *///?}
    }

    public static Function<BlockBehaviour.Properties, Block> fenceGate() {
        //? if forge {
        return properties -> new FenceGateBlock(properties, WoodType.OAK);
        //?} else {
        /*return properties -> new FenceGateBlock(WoodType.OAK, properties);
        *///?}
    }

    /** Fire odds for a block, like vanilla's {@code FireBlock.bootStrap}. Call during common setup. */
    public static void setFlammable(Block block, int igniteOdds, int burnOdds) {
        ((FireBlockAccessor) (Object) Blocks.FIRE).foodindustrial$setFlammable(block, igniteOdds, burnOdds);
    }
}
