package com.tonywww.foodindustrial.platform.mixin;

import com.tonywww.foodindustrial.platform.world.PlatformFluidPickup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26.1 {
/*import net.minecraft.world.entity.LivingEntity;
*///?}
//? if >=1.21 && <26.1 {
/*import net.minecraft.world.entity.player.Player;
*///?}

/** Lets {@link PlatformFluidPickup} replace what a bucket takes from a liquid source (seawater). */
@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {
    //? if >=26.1 {
    /*@Inject(method = "pickupBlock", at = @At("RETURN"), cancellable = true)
    private void foodindustrial$pickup(LivingEntity user, LevelAccessor level, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> info) {
        info.setReturnValue(PlatformFluidPickup.picked(level, pos, info.getReturnValue()));
    }
    *///?}
    //? if >=1.21 && <26.1 {
    /*@Inject(method = "pickupBlock", at = @At("RETURN"), cancellable = true)
    private void foodindustrial$pickup(Player player, LevelAccessor level, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> info) {
        info.setReturnValue(PlatformFluidPickup.picked(level, pos, info.getReturnValue()));
    }
    *///?}
    //? if <1.21 {
    @Inject(method = "pickupBlock", at = @At("RETURN"), cancellable = true)
    private void foodindustrial$pickup(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> info) {
        info.setReturnValue(PlatformFluidPickup.picked(level, pos, info.getReturnValue()));
    }
    //?}
}
