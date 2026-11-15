package com.tonywww.foodindustrial.platform.compat.create;
//? if <26.1 {

import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.world.level.block.Block;

/** The interface's stress impact on Create 6, kept apart so Create 0.5.1 never loads its API classes. */
final class CreateStress {
    private CreateStress() {
    }

    static void register(Block block, double impact) {
        BlockStressValues.IMPACTS.register(block, () -> impact);
    }
}
//?}
