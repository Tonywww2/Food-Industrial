package com.tonywww.foodindustrial.machine.entity;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.EnergyStore;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.ItemSlots;
import com.tonywww.foodindustrial.core.storage.StorageHost;
import com.tonywww.foodindustrial.machine.MultiblockRegistry;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A multiblock port (machines/02 §4, machines/03 §3, machines/04 §5): connects automation to the
 * controller's item slots, tanks or energy. Does not tick; reads and writes go to the controller. A
 * port is an input, an output or closed, and can push its outputs out automatically (the controller
 * does the pushing in its batch).
 */
public class PortBlockEntity extends FIBlockEntity implements StorageHost {
    /** What the port connects. */
    public enum Kind {
        ITEM, FLUID, ENERGY
    }

    private final Kind kind;
    private SideConfig.Mode mode = SideConfig.Mode.INPUT;
    private boolean autoOutput;
    @Nullable
    private MultiblockControllerBlockEntity controller;

    public PortBlockEntity(BlockEntityType<?> type, Kind kind, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    public SideConfig.Mode mode() {
        return mode;
    }

    public boolean autoOutput() {
        return autoOutput;
    }

    /** Sets direction (input, output or off; "both" counts as input) and the automatic output switch. */
    public void configure(SideConfig.Mode value, boolean auto) {
        mode = value == SideConfig.Mode.BOTH ? SideConfig.Mode.INPUT : value;
        autoOutput = auto;
        invalidateStorage();
        markDirty();
        flushDirty();
        if (controller != null) {
            controller.wake();
        }
    }

    @Nullable
    public MultiblockControllerBlockEntity controller() {
        return controller;
    }

    /** Called by the controller when the structure forms with this port, or breaks. */
    public void attach(@Nullable MultiblockControllerBlockEntity value) {
        if (controller != value) {
            controller = value;
            invalidateStorage();
        }
    }

    @Override
    protected void onLoaded() {
        if (level != null && !level.isClientSide()) {
            MultiblockRegistry.memberChanged(level, worldPosition);
        }
    }

    @Override
    protected void onRemoved(boolean unloaded) {
        if (level != null && !level.isClientSide() && !unloaded) {
            MultiblockRegistry.memberChanged(level, worldPosition);
        }
    }

    @Override
    protected void onNeighborStorageChanged() {
        if (controller != null && autoOutput) {
            controller.wake();
        }
    }

    @Override
    public @Nullable ItemSlots itemsFor(@Nullable Direction side) {
        return kind == Kind.ITEM && controller != null && controller.formed() && mode != SideConfig.Mode.OFF ? controller.portItems(mode) : null;
    }

    @Override
    public @Nullable FluidTanks fluidsFor(@Nullable Direction side) {
        return kind == Kind.FLUID && controller != null && controller.formed() && mode != SideConfig.Mode.OFF ? controller.portFluids(mode) : null;
    }

    @Override
    public @Nullable EnergyStore energyFor(@Nullable Direction side) {
        return kind == Kind.ENERGY && controller != null && controller.formed() && mode != SideConfig.Mode.OFF ? controller.energy() : null;
    }

    @Override
    protected void saveData(DataWriter out) {
        out.putByte("Mode", (byte) mode.ordinal());
        if (autoOutput) {
            out.putBoolean("AutoOutput", true);
        }
    }

    @Override
    protected void loadData(DataReader in) {
        mode = SideConfig.Mode.values()[Math.floorMod(in.getByte("Mode", (byte) SideConfig.Mode.INPUT.ordinal()), 4)];
        autoOutput = in.getBoolean("AutoOutput", false);
    }
}
