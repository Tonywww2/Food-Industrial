package com.tonywww.foodindustrial.machine.definition;

import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Something a machine needs besides a recipe to run at all (I14, additives/06 §T1 作坊): a block on a
 * face (M08's condenser on top). While it is lacking the machine shows what is missing and sleeps; a
 * neighbour change or a mode change wakes it.
 */
public interface MachineRequirement {
    /** Translation key of what is lacking now, or {@code null} when the requirement is met. */
    @Nullable
    String lacking(MachineBlockEntity machine);

    /** Whether a neighbouring block change can change the answer (the machine then wakes on one). */
    default boolean watchesNeighbours() {
        return false;
    }

    /** A block of {@code block} directly above the machine. */
    static MachineRequirement blockAbove(Supplier<? extends Block> block, String translationKey) {
        return new MachineRequirement() {
            @Override
            public @Nullable String lacking(MachineBlockEntity machine) {
                BlockPos above = machine.getBlockPos().above();
                return machine.getLevel().getBlockState(above).is(block.get()) ? null : translationKey;
            }

            @Override
            public boolean watchesNeighbours() {
                return true;
            }
        };
    }
}
