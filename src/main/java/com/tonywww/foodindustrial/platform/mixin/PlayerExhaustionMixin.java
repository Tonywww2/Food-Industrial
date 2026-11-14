package com.tonywww.foodindustrial.platform.mixin;

import com.tonywww.foodindustrial.platform.PlatformPlayerEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Lets {@link PlatformPlayerEvents#scaleExhaustion} scale the food exhaustion a player gains (I35). */
@Mixin(Player.class)
public abstract class PlayerExhaustionMixin {
    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), argsOnly = true)
    private float foodindustrial$scaleExhaustion(float amount) {
        return PlatformPlayerEvents.scaleExhaustion((Player) (Object) this, amount);
    }
}
