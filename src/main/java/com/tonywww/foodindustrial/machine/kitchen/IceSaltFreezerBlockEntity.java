package com.tonywww.foodindustrial.machine.kitchen;

import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * M50 ice-salt freezer (I41, foods/07 §5, after Nancy Johnson's 1843 hand-cranked freezer): ice in the first
 * coolant slot cools; ice with salt in the second freezes, salt making the ice melt colder. Each batch that needs
 * cooling uses one ice, each batch that needs freezing one ice and one salt; recipes without a temperature (agar,
 * gelatin setting) use neither. The coolant slots have the fuel role (machines/03 §3, I47.5); the catalyst slot holds
 * the mold.
 */
public class IceSaltFreezerBlockEntity extends ProcessingMachineBlockEntity {
    static final int ICE = 0;
    static final int SALT = 1;
    private static final Capabilities COOLING = Capabilities.of("temperature.cooling");
    private static final Capabilities FREEZING = Capabilities.of("temperature.cooling", "temperature.freezing");

    public IceSaltFreezerBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    @Override
    protected Capabilities extraCapabilities() {
        if (inventory().get(SlotRole.FUEL, ICE).isEmpty()) {
            return Capabilities.NONE;
        }
        return inventory().get(SlotRole.FUEL, SALT).isEmpty() ? COOLING : FREEZING;
    }

    @Override
    protected void inputsChanged() {
        recomputeCapabilities();
        super.inputsChanged();
    }

    @Override
    public void completed(ProcessRecipe recipe, int[] catalystSlots) {
        super.completed(recipe, catalystSlots);
        Capabilities required = recipe.required();
        boolean freezing = required.keys().contains("temperature.freezing");
        boolean cooling = freezing || required.keys().contains("temperature.cooling");
        if (cooling) {
            inventory().shrink(inventory().slot(SlotRole.FUEL, ICE), 1);
        }
        if (freezing) {
            inventory().shrink(inventory().slot(SlotRole.FUEL, SALT), 1);
        }
    }
}
