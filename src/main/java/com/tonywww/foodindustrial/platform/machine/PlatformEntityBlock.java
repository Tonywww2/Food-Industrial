package com.tonywww.foodindustrial.platform.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
//? if >=1.21 {
/*import com.mojang.serialization.MapCodec;
*///?}
//? if >=26.1 {
/*import net.minecraft.world.level.redstone.Orientation;
*///?}
//? if >=1.21 && <26.1 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}

/**
 * Base class for blocks that own a {@link PlatformBlockEntity}. Supplies the per-version pieces a
 * {@code BaseEntityBlock} needs (codec, render shape, ticker wiring, interaction signatures) and
 * exposes the stable hook {@link #onUse}.
 */
public abstract class PlatformEntityBlock extends BaseEntityBlock {
    /** Result of {@link #onUse}, mapped to each version's interaction result type. */
    public enum UseResult {
        /** Not handled: vanilla continues (item use, then empty-hand interaction). */
        PASS,
        /** Handled; swing the arm. */
        SUCCESS,
        /** Handled without swinging. */
        CONSUME,
        /** Refused; stop processing. */
        FAIL,
        /** Not handled and no empty-hand interaction either: the held item acts (for example a block is placed). */
        PASS_TO_ITEM
    }

    private static final BlockEntityTicker<BlockEntity> SERVER_TICKER =
        (level, pos, state, blockEntity) -> ((PlatformBlockEntity) blockEntity).serverTick();
    private static final BlockEntityTicker<BlockEntity> CLIENT_TICKER =
        (level, pos, state, blockEntity) -> ((PlatformBlockEntity) blockEntity).clientTick();

    private final Supplier<? extends BlockEntityType<? extends PlatformBlockEntity>> blockEntityType;

    protected PlatformEntityBlock(Properties properties, Supplier<? extends BlockEntityType<? extends PlatformBlockEntity>> blockEntityType) {
        super(properties);
        this.blockEntityType = blockEntityType;
    }

    /** Whether {@link PlatformBlockEntity#serverTick()} should run. Default: yes. */
    protected boolean ticksOnServer() {
        return true;
    }

    /** Whether {@link PlatformBlockEntity#clientTick()} should run. Default: no client ticking. */
    protected boolean ticksOnClient() {
        return false;
    }

    /**
     * Player interaction with this block. {@code held} is the stack in {@code hand}, or empty for the
     * empty-hand pass that follows a {@link UseResult#PASS} with an item.
     */
    protected UseResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, ItemStack held) {
        return UseResult.PASS;
    }

    /**
     * Server: the drops of this block from its loot table, before they spawn (breaking, explosions).
     * Return the list to drop; {@code blockEntity} is the block entity being broken, if any.
     */
    protected List<ItemStack> adjustDrops(BlockState state, @Nullable BlockEntity blockEntity, List<ItemStack> drops) {
        return drops;
    }

    //? if <1.21 {
    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return adjustDrops(state, params.getOptionalParameter(LootContextParams.BLOCK_ENTITY), super.getDrops(state, params));
    }
    //?} else {
    /*@Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return adjustDrops(state, params.getOptionalParameter(LootContextParams.BLOCK_ENTITY), super.getDrops(state, params));
    }
    *///?}

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType.get()) {
            return null;
        }
        if (level.isClientSide()) {
            return ticksOnClient() ? (BlockEntityTicker<T>) CLIENT_TICKER : null;
        }
        return ticksOnServer() ? (BlockEntityTicker<T>) SERVER_TICKER : null;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        if (level.getBlockEntity(pos) instanceof PlatformBlockEntity blockEntity) {
            blockEntity.onNeighborChanged();
        }
    }

    //? if >=26.1 {
    /*@Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        notifyNeighborUpdate(level, pos);
    }
    *///?}
    //? if >=1.21 && <26.1 {
    /*@Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, fromPos, movedByPiston);
        notifyNeighborUpdate(level, pos);
    }
    *///?}
    //? if <1.21 {
    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, fromPos, movedByPiston);
        notifyNeighborUpdate(level, pos);
    }
    //?}

    //? if >=1.21 && <26.1 {
    /*@Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        notifyBroken(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    *///?}
    //? if <1.21 {
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        notifyBroken(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    //?}

    private static void notifyBroken(BlockState state, Level level, BlockPos pos, BlockState newState) {
        if (!level.isClientSide() && !state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof PlatformBlockEntity blockEntity) {
            blockEntity.broken();
        }
    }

    private static void notifyNeighborUpdate(Level level, BlockPos pos) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PlatformBlockEntity blockEntity) {
            blockEntity.onNeighborBlockChanged();
        }
    }

    private static InteractionResult toInteractionResult(UseResult result) {
        return switch (result) {
            case SUCCESS -> InteractionResult.SUCCESS;
            case CONSUME -> InteractionResult.CONSUME;
            case FAIL -> InteractionResult.FAIL;
            case PASS, PASS_TO_ITEM -> InteractionResult.PASS;
        };
    }

    //? if >=26.1 {
    /*private final MapCodec<PlatformEntityBlock> codec = simpleCodec(properties -> this);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return codec;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        UseResult result = onUse(state, level, pos, player, hand, hit, stack);
        // PASS_TO_ITEM maps to PASS: the empty-hand pass is skipped and the item is used
        return result == UseResult.PASS ? InteractionResult.TRY_WITH_EMPTY_HAND : toInteractionResult(result);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return toInteractionResult(onUse(state, level, pos, player, InteractionHand.MAIN_HAND, hit, ItemStack.EMPTY));
    }
    *///?}

    //? if >=1.21 && <26.1 {
    /*private final MapCodec<PlatformEntityBlock> codec = simpleCodec(properties -> this);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return codec;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        UseResult result = onUse(state, level, pos, player, hand, hit, held);
        if (result == UseResult.PASS && !held.isEmpty()) {
            result = onUse(state, level, pos, player, hand, hit, ItemStack.EMPTY);
        }
        return toInteractionResult(result);
    }
    //?}
}
