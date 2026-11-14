package com.tonywww.foodindustrial.machine.chemical;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * M25 adsorption column (I16, additives/06 §T3 化工：操作与实现规格): the adsorbent in the catalyst slot
 * is not used up at its {@code runs} but saturates (2026-11-11). While saturated the column takes only
 * regeneration recipes ({@code "mode": "regeneration"}, the adsorbent as catalyst, a regenerant in, waste
 * out), which start by themselves when the regenerant is in a tank; one completion clears the saturation.
 * Without the regenerant the status says so. An emptied slot starts over.
 */
public class AdsorptionColumnBlockEntity extends ProcessingMachineBlockEntity {
    public static final String REGENERATION = "mode.regeneration";
    /** Translation key of the missing regenerant. */
    public static final String NEED_REGENERANT = "gui.foodindustrial.need.regenerant";

    private final boolean[] saturated;

    public AdsorptionColumnBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
        saturated = new boolean[definition.layout().catalyst()];
    }

    private static boolean regenerates(ProcessRecipe recipe) {
        return recipe.required().keys().contains(REGENERATION);
    }

    private boolean anySaturated() {
        for (boolean value : saturated) {
            if (value) {
                return true;
            }
        }
        return false;
    }

    /** Whether the adsorbent in catalyst slot {@code index} is saturated. */
    @Override
    public boolean catalystSaturated(int index) {
        return index >= 0 && index < saturated.length && saturated[index];
    }

    @Override
    public boolean offers(ProcessRecipe recipe) {
        return regenerates(recipe) == anySaturated();
    }

    @Override
    public void wearCatalyst(int index, int runs) {
        if (saturated[index]) {
            return;
        }
        if (catalysts().wear(index, runs)) {
            saturated[index] = true;
            markDirty();
        }
    }

    @Override
    public void completed(ProcessRecipe recipe, int[] catalystSlots) {
        if (!regenerates(recipe)) {
            return;
        }
        for (int slot : catalystSlots) {
            if (slot >= 0 && slot < saturated.length) {
                saturated[slot] = false;
                catalysts().reset(slot);
            }
        }
        markDirty();
    }

    @Override
    protected void catalystEmptied(int index) {
        super.catalystEmptied(index);
        if (saturated[index]) {
            saturated[index] = false;
            markDirty();
        }
    }

    @Override
    protected MachineStatus tickBatch(int delta, long gameTime) {
        MachineStatus status = super.tickBatch(delta, gameTime);
        if (anySaturated() && status == MachineStatus.WAITING) {
            lacking = NEED_REGENERANT;
            return MachineStatus.MISSING;
        }
        return status;
    }

    @Override
    protected void saveData(DataWriter out) {
        super.saveData(out);
        int[] values = new int[saturated.length];
        boolean any = false;
        for (int i = 0; i < saturated.length; i++) {
            values[i] = saturated[i] ? 1 : 0;
            any |= saturated[i];
        }
        if (any) {
            out.putIntArray("Saturated", values);
        }
    }

    @Override
    protected void loadData(DataReader in) {
        super.loadData(in);
        int[] values = in.getIntArray("Saturated");
        for (int i = 0; i < saturated.length; i++) {
            saturated[i] = i < values.length && values[i] != 0 && !inventory().get(SlotRole.CATALYST, i).isEmpty();
        }
    }
}
