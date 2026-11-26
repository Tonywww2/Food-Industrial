package com.tonywww.foodindustrial.platform.world;

import com.tonywww.foodindustrial.platform.PlatformItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
//? if >=26.1 {
/*import net.minecraft.world.level.block.TintedParticleLeavesBlock;
*///?} else {
import net.minecraft.world.level.block.LeavesBlock;
//?}
//? if neoforge && <26.1 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}

/**
 * Leaves of an I44 tree that ripen through ages 0–3 on random ticks (one chance in five a tick, like the sweet
 * berry bush): flowers and fruit are picked by hand, leaf products with shears, as {@link PlantHarvest} says.
 * Decay and the rest are vanilla's.
 */
//? if >=26.1 {
/*public class FruitLeavesBlock extends TintedParticleLeavesBlock {
*///?} else {
public class FruitLeavesBlock extends LeavesBlock {
//?}
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private final PlantHarvest harvest;

    public FruitLeavesBlock(BlockBehaviour.Properties properties, PlantHarvest harvest) {
        //? if >=26.1 {
        /*super(0.01F, properties);
        *///?} else {
        super(properties);
        //?}
        this.harvest = harvest;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3 || super.isRandomlyTicking(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        BlockState now = level.getBlockState(pos);
        if (now.is(this) && now.getValue(AGE) < 3 && random.nextInt(5) == 0) {
            level.setBlock(pos, now.setValue(AGE, now.getValue(AGE) + 1), Block.UPDATE_CLIENTS);
        }
    }

    //? if >=26.1 {
    /*@Override
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return tryPick(state, level, pos, player, hand, player.getItemInHand(hand))
            ? InteractionResult.sidedSuccess(level.isClientSide()) : InteractionResult.PASS;
    }
    //?}

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
