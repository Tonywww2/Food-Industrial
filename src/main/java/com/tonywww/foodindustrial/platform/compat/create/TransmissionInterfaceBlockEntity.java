package com.tonywww.foodindustrial.platform.compat.create;
//? if <26.1 {

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Block entity of the transmission interface (I25): a plain kinetic consumer; the machine below reads its speed. */
public class TransmissionInterfaceBlockEntity extends KineticBlockEntity {
    public TransmissionInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        changed();
    }

    /** Overstress stops the shaft without a speed change; the machine below must hear of both. */
    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        boolean before = isOverStressed();
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        if (before != isOverStressed()) {
            changed();
        }
    }

    private void changed() {
        if (level != null && !level.isClientSide()) {
            PlatformKinetics.speedChanged(level, worldPosition);
        }
    }
}
//?}
