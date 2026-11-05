package com.tonywww.foodindustrial.platform.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
//? if >=26.1 {
/*import net.minecraft.server.level.ServerLevel;
*///?}

/**
 * Base class of plain blocks that report their placement and removal on the server (multiblock casings
 * and other member blocks without a block entity): {@link #onPlaced} and {@link #onRemoved}, also for
 * changes by pistons and explosions. 26.1 has no removal hook with the level before removal; both
 * hooks run after the change on every node.
 */
public abstract class PlatformMemberBlock extends Block {
    protected PlatformMemberBlock(Properties properties) {
        super(properties);
    }

    /** Server only: this block was placed at {@code pos}. */
    protected void onPlaced(Level level, BlockPos pos) {
    }

    /** Server only: this block was removed from {@code pos} (or replaced by another block). */
    protected void onRemoved(Level level, BlockPos pos) {
    }

    //? if >=26.1 {
    /*@Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !oldState.is(this)) {
            onPlaced(level, pos);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        if (!level.getBlockState(pos).is(this)) {
            onRemoved(level, pos);
        }
    }
    *///?}
    //? if >=1.21 && <26.1 {
    /*@Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !oldState.is(this)) {
            onPlaced(level, pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!level.isClientSide() && !newState.is(this)) {
            onRemoved(level, pos);
        }
    }
    *///?}
    //? if <1.21 {
    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !oldState.is(this)) {
            onPlaced(level, pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!level.isClientSide() && !newState.is(this)) {
            onRemoved(level, pos);
        }
    }
    //?}
}
