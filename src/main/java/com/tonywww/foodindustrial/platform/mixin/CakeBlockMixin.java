package com.tonywww.foodindustrial.platform.mixin;

import com.tonywww.foodindustrial.platform.PlatformPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reports each bite of a cake as one serving eaten ({@link PlatformPlayerEvents#onConsumed}, I35). */
@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
    @Inject(method = "eat", at = @At("RETURN"))
    private static void foodindustrial$ateBite(LevelAccessor level, BlockPos pos, BlockState state, Player player,
                                               CallbackInfoReturnable<InteractionResult> info) {
        if (info.getReturnValue().consumesAction()) {
            PlatformPlayerEvents.ateBite(player, state);
        }
    }
}
