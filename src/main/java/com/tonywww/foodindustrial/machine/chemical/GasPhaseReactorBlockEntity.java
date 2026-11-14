package com.tonywww.foodindustrial.machine.chemical;

import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * M27 gas-phase reactor (I16, additives/06 §T3 化工：操作与实现规格): a 3 × 3 × 3 furnace with 1–4
 * furnace tube segments on top; in cracking mode the main product comes out at ×0.7 with one segment
 * and 0.1 more per further segment (×1.0 with four).
 */
public class GasPhaseReactorBlockEntity extends MultiblockControllerBlockEntity {
    public static final String CRACKING = "cracking";
    private static final int FURNACE_LAYERS = 3;

    public GasPhaseReactorBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    /** Furnace tube segments of the formed reactor. */
    public int segments() {
        return formed() ? Math.max(0, layers() - FURNACE_LAYERS) : 0;
    }

    @Override
    public int outputPermille(ProcessRecipe recipe, int index) {
        MachineMode current = mode();
        return index == 0 && current != null && current.id().equals(CRACKING) ? CrackingYield.permille(segments()) : 1000;
    }
}
