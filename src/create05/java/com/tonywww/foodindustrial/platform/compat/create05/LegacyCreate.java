package com.tonywww.foodindustrial.platform.compat.create05;

import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.utility.Couple;
import com.tonywww.foodindustrial.FoodIndustrial;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * The transmission interface on Create 0.5.1 with Flywheel 0.6 (2026-11-14): its stress impact and its
 * rendering. This source set exists only on the 1.20.1 Forge node and is compiled against Create 0.5.1;
 * {@code compat.create.CreateTransmission} calls these methods by reflection when Flywheel's version is 0.x.
 */
public final class LegacyCreate {
    private LegacyCreate() {
    }

    /** Gives {@code block} a stress impact per RPM: Create 0.5.1 asks a provider per mod namespace. */
    public static void registerStress(Block block, double impact) {
        BlockStressValues.registerProvider(FoodIndustrial.MOD_ID, new BlockStressValues.IStressValueProvider() {
            @Override
            public double getImpact(Block other) {
                return other == block ? impact : 0;
            }

            @Override
            public double getCapacity(Block other) {
                return 0;
            }

            @Override
            public boolean hasImpact(Block other) {
                return other == block;
            }

            @Override
            public boolean hasCapacity(Block other) {
                return false;
            }

            @Override
            public Couple<Integer> getGeneratedRPM(Block other) {
                return null;
            }
        });
    }

    /** Registers the Flywheel 0.6 instance and the fallback renderer of the half shaft on top. */
    public static void bindClient(IEventBus modBus) {
        LegacyCreateClient.bind(modBus);
    }
}
