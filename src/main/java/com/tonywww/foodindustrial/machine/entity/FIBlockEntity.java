package com.tonywww.foodindustrial.machine.entity;

import com.tonywww.foodindustrial.platform.machine.PlatformBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base of this mod's block entities with contents (machines/02 §4): changes are collected with
 * {@link #markDirty} (contents: neighbours are notified) or {@link #markUnsaved} (progress, energy:
 * only saved) and reported to the game once per batch by {@link #flushDirty}.
 */
public abstract class FIBlockEntity extends PlatformBlockEntity {
    private boolean dirty;
    private boolean unsaved;

    protected FIBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Marks the contents changed; reported at the end of the batch. */
    public final void markDirty() {
        dirty = true;
    }

    /**
     * Marks state that only needs saving (progress, energy, fuel): the chunk is marked unsaved without
     * the neighbour notifications a contents change sends.
     */
    public final void markUnsaved() {
        unsaved = true;
    }

    /** Reports collected changes; call at the end of a batch or after an outside change. */
    protected final void flushDirty() {
        if (dirty) {
            dirty = false;
            unsaved = false;
            setChanged();
        } else if (unsaved) {
            unsaved = false;
            if (level != null) {
                level.blockEntityChanged(worldPosition);
            }
        }
    }
}
