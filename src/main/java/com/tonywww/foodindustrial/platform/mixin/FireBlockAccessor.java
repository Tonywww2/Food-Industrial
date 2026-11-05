package com.tonywww.foodindustrial.platform.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Calls {@code FireBlock.setFlammable}, which is private on 1.20.1 and 26.1. */
@Mixin(FireBlock.class)
public interface FireBlockAccessor {
    @Invoker("setFlammable")
    void foodindustrial$setFlammable(Block block, int igniteOdds, int burnOdds);
}
