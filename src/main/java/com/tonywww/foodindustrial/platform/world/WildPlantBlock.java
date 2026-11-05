package com.tonywww.foodindustrial.platform.world;

import net.minecraft.world.level.block.state.BlockBehaviour;
//? if >=26.1 {
/*import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.VegetationBlock;
*///?}
//? if neoforge && <26.1 {
/*import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
*///?}
//? if forge {
import net.minecraft.world.level.block.BushBlock;
//?}

/** A wild plant on grass or dirt (vanilla plant placement rules); its drops come from the loot table. */
//? if >=26.1 {
/*final class WildPlantBlock extends VegetationBlock {
    private static final MapCodec<WildPlantBlock> CODEC = simpleCodec(WildPlantBlock::new);
*///?}
//? if neoforge && <26.1 {
/*final class WildPlantBlock extends BushBlock {
    private static final MapCodec<WildPlantBlock> CODEC = simpleCodec(WildPlantBlock::new);
*///?}
//? if forge {
final class WildPlantBlock extends BushBlock {
//?}

    WildPlantBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    //? if >=1.21 {
    /*@Override
    protected MapCodec<WildPlantBlock> codec() {
        return CODEC;
    }
    *///?}
}
