package com.tonywww.foodindustrial.platform.world;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/** A farmland crop whose produce is also what is planted (like carrots). */
final class ModCropBlock extends CropBlock {
    private final Supplier<? extends ItemLike> produce;

    ModCropBlock(BlockBehaviour.Properties properties, Supplier<? extends ItemLike> produce) {
        super(properties);
        this.produce = produce;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return produce.get();
    }
}
