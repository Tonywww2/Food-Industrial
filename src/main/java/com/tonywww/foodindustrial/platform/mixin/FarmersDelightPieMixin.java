package com.tonywww.foodindustrial.platform.mixin;

import com.tonywww.foodindustrial.platform.PlatformPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Reports each bite of a Farmer's Delight pie as one serving eaten (I35). {@code PieBlock#consumeBite} is the
 * same on 1.20.1 and 1.21.1; without Farmer's Delight (26.1.2) the mixin is skipped.
 */
@Pseudo
@Mixin(targets = "vectorwing.farmersdelight.common.block.PieBlock")
public abstract class FarmersDelightPieMixin {
    @Inject(method = "consumeBite", at = @At("RETURN"), remap = false, require = 0)
    private void foodindustrial$ateBite(Level level, BlockPos pos, BlockState state, Player player,
                                        CallbackInfoReturnable<InteractionResult> info) {
        if (info.getReturnValue().consumesAction()) {
            PlatformPlayerEvents.ateBite(player, state);
        }
    }
}
