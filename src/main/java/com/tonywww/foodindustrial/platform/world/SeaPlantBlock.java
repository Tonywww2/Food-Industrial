package com.tonywww.foodindustrial.platform.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An underwater plant like seagrass. Bone meal spreads it to a nearby water source block with a valid
 * floor instead of growing tall seagrass, so it can be farmed.
 */
final class SeaPlantBlock extends SeagrassBlock {
    private static final int TRIES = 8;

    SeaPlantBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        for (int i = 0; i < TRIES; i++) {
            BlockPos target = pos.offset(random.nextInt(5) - 2, random.nextInt(3) - 1, random.nextInt(5) - 2);
            if (level.getBlockState(target).is(Blocks.WATER) && level.getFluidState(target).isSource()
                    && defaultBlockState().canSurvive(level, target)) {
                level.setBlock(target, defaultBlockState(), Block.UPDATE_CLIENTS);
                return;
            }
        }
    }
}
