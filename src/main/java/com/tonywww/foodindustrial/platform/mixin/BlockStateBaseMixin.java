package com.tonywww.foodindustrial.platform.mixin;

import com.tonywww.foodindustrial.platform.world.PlatformRandomTicks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Random ticks of dirt and coarse dirt, and the random tick hook ({@link PlatformRandomTicks}). */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow
    private boolean isRandomlyTicking;

    @Shadow
    public abstract Block getBlock();

    @Inject(method = "initCache", at = @At("TAIL"))
    private void foodindustrial$forceRandomTicks(CallbackInfo info) {
        if (PlatformRandomTicks.forced(getBlock())) {
            isRandomlyTicking = true;
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void foodindustrial$randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo info) {
        PlatformRandomTicks.onRandomTick((BlockState) (Object) this, level, pos, random);
    }
}
