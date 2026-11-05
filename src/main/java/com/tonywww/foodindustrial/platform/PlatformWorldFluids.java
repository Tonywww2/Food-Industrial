package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Fluid blocks in the world, bucket-like (machines/03 §5): taking 1,000 mB from a source block and
 * placing a source block. A picked-up fluid carries the purity the bucket would get (the fluid's
 * pick-up purity, or none); a placed block keeps no purity.
 */
public final class PlatformWorldFluids {
    public static final int UNIT = 1000;

    private PlatformWorldFluids() {
    }

    /** What the player looks at within reach, fluid source blocks included ({@code null} when nothing). */
    @Nullable
    public static BlockHitResult lookAt(Level level, Player player, boolean sourcesToo) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(PlatformPlayers.blockReach(player)));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE,
            sourcesToo ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE, player));
        return hit.getType() == HitResult.Type.BLOCK ? hit : null;
    }

    /** The source fluid at {@code pos}, or {@code null} when there is none. */
    @Nullable
    public static Fluid sourceAt(Level level, BlockPos pos) {
        FluidState state = level.getFluidState(pos);
        return state.isSource() ? state.getType() : null;
    }

    /** Takes 1,000 mB from the source block at {@code pos}; empty when it cannot be taken. */
    public static FluidAmount pickup(Level level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BucketPickup pickup) || sourceAt(level, pos) == null) {
            return FluidAmount.EMPTY;
        }
        //? if >=1.21 {
        /*ItemStack bucket = pickup.pickupBlock(player, level, pos, state);
        *///?} else {
        ItemStack bucket = pickup.pickupBlock(level, pos, state);
        //?}
        FluidAmount fluid = PlatformFluidContainers.contained(bucket);
        if (!fluid.isEmpty()) {
            level.playSound((Player) null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return fluid.isEmpty() ? FluidAmount.EMPTY : fluid.withAmount(UNIT);
    }

    /** Whether {@code fluid} can stand in the world as a block (liquids with a source form). */
    public static boolean placeable(Fluid fluid) {
        return fluid instanceof FlowingFluid flowing && !PlatformFluids.isGas(fluid) && flowing.getSource().defaultFluidState().createLegacyBlock() != null
            && !flowing.getSource().defaultFluidState().createLegacyBlock().isAir();
    }

    /** Places a source block of {@code fluid} at {@code pos} if the position is empty or replaceable. */
    public static boolean place(Level level, BlockPos pos, Fluid fluid) {
        if (!placeable(fluid)) {
            return false;
        }
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.canBeReplaced(fluid)) {
            return false;
        }
        Fluid source = ((FlowingFluid) fluid).getSource();
        if (!level.isClientSide()) {
            if (!current.isAir() && current.getFluidState().isEmpty()) {
                level.destroyBlock(pos, true);
            }
            level.setBlock(pos, source.defaultFluidState().createLegacyBlock(), 11);
        }
        level.playSound((Player) null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }
}
