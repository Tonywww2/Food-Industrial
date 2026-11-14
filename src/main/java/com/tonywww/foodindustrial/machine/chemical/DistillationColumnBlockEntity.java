package com.tonywww.foodindustrial.machine.chemical;

import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * M22 distillation column (I16, additives/06 §T3 化工：操作与实现规格): one tray per layer between the
 * bottom and the top (2–6); a recipe may not have more fluid products (fractions) than trays.
 */
public class DistillationColumnBlockEntity extends MultiblockControllerBlockEntity {
    /** Translation key of the missing trays. */
    public static final String NEED_TRAYS = "gui.foodindustrial.need.trays";

    public DistillationColumnBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    /** Trays of the formed column. */
    public int trays() {
        return formed() ? Math.max(0, layers() - 2) : 0;
    }

    /** Fluid products of {@code recipe} this column makes (its fractions). */
    static int fractions(ProcessRecipe recipe, MachineDefinition definition) {
        int fractions = 0;
        for (ProcessOutput output : recipe.outputs()) {
            if (output.isFluid() && output.producedAt(definition.tier())) {
                fractions++;
            }
        }
        return fractions;
    }

    @Override
    @Nullable
    public String recipeLacking(ProcessRecipe recipe) {
        return fractions(recipe, definition) > trays() ? NEED_TRAYS : null;
    }
}
