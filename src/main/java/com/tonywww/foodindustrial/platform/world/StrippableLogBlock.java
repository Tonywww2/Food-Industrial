package com.tonywww.foodindustrial.platform.world;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
//?} else {
/*import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
*///?}

/** A log or wood block that an axe strips into {@code stripped}, keeping the axis. */
final class StrippableLogBlock extends RotatedPillarBlock {
    private final Supplier<? extends Block> stripped;

    StrippableLogBlock(BlockBehaviour.Properties properties, Supplier<? extends Block> stripped) {
        super(properties);
        this.stripped = stripped;
    }

    //? if forge {
    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction action, boolean simulate) {
        if (action == ToolActions.AXE_STRIP) {
            return stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        return super.getToolModifiedState(state, context, action, simulate);
    }
    //?} else {
    /*@Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility ability, boolean simulate) {
        if (ability == ItemAbilities.AXE_STRIP) {
            return stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        return super.getToolModifiedState(state, context, ability, simulate);
    }
    *///?}
}
