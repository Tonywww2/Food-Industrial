package com.tonywww.foodindustrial.platform.world;

import com.tonywww.foodindustrial.platform.PlatformItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
//? if >=26.1 {
/*import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.LevelReader;
*///?}
//? if neoforge && <26.1 {
/*import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.LevelReader;
*///?}

/**
 * A shrub or wild herb that grows through ages 0–3 like the sweet berry bush (I44): a right click picks what
 * {@link PlantHarvest} says (by hand, or with shears for leaves), neither slows nor hurts entities, and stands on
 * dirt and farmland, or on sand too when {@code sand}. Herbs have a small flower-sized shape.
 */
public class HarvestPlantBlock extends SweetBerryBushBlock {
    private static final VoxelShape HERB = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
    private final PlantHarvest harvest;
    private final boolean herb;
    private final boolean sand;

    public HarvestPlantBlock(BlockBehaviour.Properties properties, PlantHarvest harvest, boolean herb, boolean sand) {
        super(properties);
        this.harvest = harvest;
        this.herb = herb;
        this.sand = sand;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return herb ? HERB : super.getShape(state, level, pos, context);
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        // vanilla's own ground rule (dirt and farmland; 26.1 #supports_vegetation), plus sand for desert and beach plants
        return super.mayPlaceOn(state, level, pos) || state.is(Blocks.FARMLAND) || sand && state.is(BlockTags.SAND);
    }

    //? if >=26.1 {
    /*@Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return harvest.pickBlock();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
    }

    @Override
    protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                          BlockHitResult hitResult) {
        if (tryPick(state, level, pos, player, hand, held)) {
            return InteractionResult.SUCCESS;
        }
        return held.isEmpty() ? InteractionResult.TRY_WITH_EMPTY_HAND : InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return tryPick(state, level, pos, player, InteractionHand.MAIN_HAND, ItemStack.EMPTY) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    *///?}
    //? if neoforge && <26.1 {
    /*@Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return harvest.pickBlock();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (tryPick(state, level, pos, player, hand, held)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return held.isEmpty() ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return tryPick(state, level, pos, player, InteractionHand.MAIN_HAND, ItemStack.EMPTY)
            ? InteractionResult.sidedSuccess(level.isClientSide()) : InteractionResult.PASS;
    }
    *///?}
    //? if forge {
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return harvest.pickBlock();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return tryPick(state, level, pos, player, hand, player.getItemInHand(hand))
            ? InteractionResult.sidedSuccess(level.isClientSide()) : InteractionResult.PASS;
    }
    //?}

    /** Picks on the server (the client only reports that something can be picked, so the arm swings). */
    private boolean tryPick(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack held) {
        PlantHarvest.Picked picked = harvest.pick(state.getValue(AGE), held, level.getRandom());
        if (picked == null) {
            return false;
        }
        if (level instanceof ServerLevel server) {
            for (ItemStack item : picked.items()) {
                Block.popResource(server, pos, item);
            }
            if (picked.shears()) {
                PlatformItems.damage(held, 1, player, hand);
                server.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F,
                    0.8F + server.getRandom().nextFloat() * 0.4F);
            }
            BlockState after = state.setValue(AGE, picked.age());
            server.setBlock(pos, after, Block.UPDATE_CLIENTS);
            server.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, after));
        }
        return true;
    }
}
