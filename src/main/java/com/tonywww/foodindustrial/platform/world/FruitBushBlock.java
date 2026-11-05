package com.tonywww.foodindustrial.platform.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;
//? if >=26.1 {
/*import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.LevelReader;
*///?}
//? if neoforge && <26.1 {
/*import net.minecraft.world.level.LevelReader;
*///?}
//? if forge {
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
//?}

/**
 * A shrub like the sweet berry bush (age 0–3, right-click picks the fruit from age 2 and drops back to
 * age 1) that gives its own fruit and neither slows nor hurts entities.
 */
final class FruitBushBlock extends SweetBerryBushBlock {
    private final Supplier<? extends ItemLike> fruit;

    FruitBushBlock(BlockBehaviour.Properties properties, Supplier<? extends ItemLike> fruit) {
        super(properties);
        this.fruit = fruit;
    }

    //? if >=26.1 {
    /*@Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(fruit.get());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(AGE) > 1) {
            pick(state, level, pos, player);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    *///?}
    //? if neoforge && <26.1 {
    /*@Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(fruit.get());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(AGE) > 1) {
            pick(state, level, pos, player);
            return InteractionResult.sidedSuccess(!(level instanceof ServerLevel));
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    *///?}
    //? if forge {
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(fruit.get());
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age != MAX_AGE && player.getItemInHand(hand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }
        if (age > 1) {
            pick(state, level, pos, player);
            return InteractionResult.sidedSuccess(!(level instanceof ServerLevel));
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }
    //?}

    /** Drops 1–2 fruits (one more when fully grown) and resets the bush to age 1, on the server. */
    private void pick(BlockState state, Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        int count = 1 + level.getRandom().nextInt(2) + (state.getValue(AGE) == MAX_AGE ? 1 : 0);
        Block.popResource(level, pos, new ItemStack(fruit.get(), count));
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
        BlockState picked = state.setValue(AGE, 1);
        level.setBlock(pos, picked, Block.UPDATE_CLIENTS);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, picked));
    }
}
