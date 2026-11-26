package com.tonywww.foodindustrial.machine.kitchen;

import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * M48 candy table (I41, foods/07 §5): the fuel heats; set on stone ({@link KitchenMachines#COOLING_STONE}) it also
 * cools, like a marble slab for tempering chocolate.
 */
public class CandyTableBlockEntity extends ProcessingMachineBlockEntity {
    private static final Capabilities COOLING = Capabilities.of("temperature.cooling");

    public CandyTableBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    @Override
    protected Capabilities extraCapabilities() {
        return level != null && level.getBlockState(worldPosition.below()).is(PlatformTags.blockTag(KitchenMachines.COOLING_STONE))
            ? COOLING : Capabilities.NONE;
    }

    @Override
    protected void onNeighborBlockChanged() {
        super.onNeighborBlockChanged();
        recomputeCapabilities();
        wake();
    }
}
