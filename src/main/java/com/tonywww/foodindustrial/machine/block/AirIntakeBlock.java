package com.tonywww.foodindustrial.machine.block;

import com.tonywww.foodindustrial.machine.MultiblockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Air intake of a multiblock (machines/03 §1, I15): a member that may stand where the casing goes; the
 * structure has air while an intake has a side outside it facing an air block. A neighbour change makes
 * the controllers around it look again.
 */
public class AirIntakeBlock extends CasingBlock {
    public AirIntakeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onNeighborChanged(Level level, BlockPos pos) {
        MultiblockRegistry.memberChanged(level, pos);
    }
}
