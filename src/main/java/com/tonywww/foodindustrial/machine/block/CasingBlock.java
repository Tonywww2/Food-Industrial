package com.tonywww.foodindustrial.machine.block;

import com.tonywww.foodindustrial.machine.MultiblockRegistry;
import com.tonywww.foodindustrial.platform.machine.PlatformMemberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** A plain multiblock member (casing, wall, functional block without storage): tells the controllers around it when it changes. */
public class CasingBlock extends PlatformMemberBlock {
    public CasingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlaced(Level level, BlockPos pos) {
        MultiblockRegistry.memberChanged(level, pos);
    }

    @Override
    protected void onRemoved(Level level, BlockPos pos) {
        MultiblockRegistry.memberChanged(level, pos);
    }
}
