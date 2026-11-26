package com.tonywww.foodindustrial.machine.kitchen;

import com.tonywww.foodindustrial.food.FoodLine;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * M45 aging cellar (I42, machines/05 §2.3): all of R25, but of R16 only the recipes whose main product carries
 * quality (a finished product or a semi-finished one); the additive line's fermentations stay with M05 and M29.
 */
public class AgingCellarBlockEntity extends MultiblockControllerBlockEntity {
    public AgingCellarBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    @Override
    public boolean offers(ProcessRecipe recipe) {
        if (recipe.type() != ProcessTypes.FERMENTING) {
            return true;
        }
        ProcessOutput main = recipe.outputs().isEmpty() ? null : recipe.outputs().get(0);
        if (main == null) {
            return false;
        }
        if (main.isSemi()) {
            return true;
        }
        return main.isFluid() ? FoodLine.isProduct(main.fluid()) : FoodLine.isProduct(main.item());
    }
}
