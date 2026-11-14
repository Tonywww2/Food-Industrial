package com.tonywww.foodindustrial.platform.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
//? if >=26.1 {
/*import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
*///?} else {
import net.minecraft.world.level.LevelAccessor;
//?}
//? if >=1.21 && <26.1 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}

/**
 * Base class of plain blocks without a block entity that players use (I14: the hand crank): the
 * per-version interaction, shape and survival hooks, as {@link PlatformEntityBlock} has them for blocks
 * with a block entity. A block that can no longer stay ({@link #canStay}) breaks and drops after a
 * neighbour changes.
 */
public abstract class PlatformUsableBlock extends Block {
    protected PlatformUsableBlock(Properties properties) {
        super(properties);
    }

    /**
     * Player interaction. {@code held} is the stack in {@code hand}, or empty for the empty-hand pass
     * that follows a {@link PlatformEntityBlock.UseResult#PASS} with an item.
     */
    protected PlatformEntityBlock.UseResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                                  BlockHitResult hit, ItemStack held) {
        return PlatformEntityBlock.UseResult.PASS;
    }

    /** Outline and collision shape; default a full block. */
    protected VoxelShape shape(BlockState state) {
        return Shapes.block();
    }

    /** Whether the block can stay at {@code pos}; default yes. */
    protected boolean canStay(LevelReader level, BlockPos pos) {
        return true;
    }

    //? if >=1.21 {
    /*@Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape(state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canStay(level, pos);
    }
    *///?} else {
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canStay(level, pos);
    }
    //?}

    //? if >=26.1 {
    /*@Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
                                     BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        return canStay(level, pos) ? state : Blocks.AIR.defaultBlockState();
    }
    *///?}
    //? if >=1.21 && <26.1 {
    /*@Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return canStay(level, pos) ? state : Blocks.AIR.defaultBlockState();
    }
    *///?}
    //? if <1.21 {
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return canStay(level, pos) ? state : Blocks.AIR.defaultBlockState();
    }
    //?}

    private static InteractionResult toInteractionResult(PlatformEntityBlock.UseResult result) {
        return switch (result) {
            case SUCCESS -> InteractionResult.SUCCESS;
            case CONSUME -> InteractionResult.CONSUME;
            case FAIL -> InteractionResult.FAIL;
            case PASS, PASS_TO_ITEM -> InteractionResult.PASS;
        };
    }

    //? if >=26.1 {
    /*@Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        PlatformEntityBlock.UseResult result = onUse(state, level, pos, player, hand, hit, stack);
        return result == PlatformEntityBlock.UseResult.PASS ? InteractionResult.TRY_WITH_EMPTY_HAND : toInteractionResult(result);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return toInteractionResult(onUse(state, level, pos, player, InteractionHand.MAIN_HAND, hit, ItemStack.EMPTY));
    }
    *///?}

    //? if >=1.21 && <26.1 {
    /*@Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return switch (onUse(state, level, pos, player, hand, hit, stack)) {
            case SUCCESS -> ItemInteractionResult.SUCCESS;
            case CONSUME -> ItemInteractionResult.CONSUME;
            case FAIL -> ItemInteractionResult.FAIL;
            case PASS -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            case PASS_TO_ITEM -> ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return toInteractionResult(onUse(state, level, pos, player, InteractionHand.MAIN_HAND, hit, ItemStack.EMPTY));
    }
    *///?}

    //? if <1.21 {
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        PlatformEntityBlock.UseResult result = onUse(state, level, pos, player, hand, hit, held);
        if (result == PlatformEntityBlock.UseResult.PASS && !held.isEmpty()) {
            result = onUse(state, level, pos, player, hand, hit, ItemStack.EMPTY);
        }
        return toInteractionResult(result);
    }
    //?}
}
