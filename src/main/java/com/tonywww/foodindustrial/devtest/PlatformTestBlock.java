package com.tonywww.foodindustrial.devtest;

import com.tonywww.foodindustrial.platform.machine.PlatformBlockEntity;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/** Block of {@link PlatformTestBlockEntity}. */
public final class PlatformTestBlock extends PlatformEntityBlock {
    public PlatformTestBlock(Properties properties, Supplier<? extends BlockEntityType<? extends PlatformBlockEntity>> type) {
        super(properties, type);
    }
}
