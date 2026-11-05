package com.tonywww.foodindustrial.logistics;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.StorageHost;
import com.tonywww.foodindustrial.machine.component.Backoff;
import com.tonywww.foodindustrial.machine.entity.FIBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A wooden or metal tank (machines/03 §5, 2026-11-05). Holds one fluid; open to automation and other
 * mods' pipes on every face in every mode. In drain mode it pushes up to 400 mB per batch into the
 * block below. Metal tanks stacked vertically form a column of up to 8 that shares its storage: fluid
 * fills from the bottom block up and is taken from the top block down; the drain mode is the column's
 * and the bottom block pushes.
 */
public class TankBlockEntity extends FIBlockEntity implements StorageHost {
    private final TankKind kind;
    private FluidAmount fluid = FluidAmount.EMPTY;
    private boolean draining;
    private boolean pendingSync;
    private final Backoff drainBackoff = new Backoff();
    @Nullable
    private FluidTanks view;

    public TankBlockEntity(BlockEntityType<?> type, TankKind kind, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.kind = kind;
    }

    public TankKind kind() {
        return kind;
    }

    /** This block's own contents. */
    public FluidAmount fluid() {
        return fluid;
    }

    public boolean draining() {
        return draining;
    }

    // ---- column ----

    /** The tanks sharing storage with this one, bottom first (just this one for wooden tanks). */
    public List<TankBlockEntity> column() {
        List<TankBlockEntity> below = new ArrayList<>();
        if (level != null && kind.stacks()) {
            BlockPos pos = worldPosition.below();
            while (below.size() < kind.maxColumn() && level.getBlockEntity(pos) instanceof TankBlockEntity tank && tank.kind == kind) {
                below.add(0, tank);
                pos = pos.below();
            }
        }
        List<TankBlockEntity> column = new ArrayList<>(below);
        column.add(this);
        if (level != null && kind.stacks()) {
            BlockPos pos = worldPosition.above();
            while (column.size() < kind.maxColumn() * 2 && level.getBlockEntity(pos) instanceof TankBlockEntity tank && tank.kind == kind) {
                column.add(tank);
                pos = pos.above();
            }
        }
        return column;
    }

    /** Contents of a column: the members' fluid merged (one kind per column). */
    public static FluidAmount total(List<TankBlockEntity> column) {
        FluidAmount total = FluidAmount.EMPTY;
        for (TankBlockEntity tank : column) {
            if (!tank.fluid.isEmpty()) {
                total = total.isEmpty() ? tank.fluid : Purity.merge(total, tank.fluid);
            }
        }
        return total;
    }

    /** Puts {@code contents} into the column, filling from the bottom block up. */
    public static void distribute(List<TankBlockEntity> column, FluidAmount contents) {
        int left = contents.isEmpty() ? 0 : contents.amount();
        for (TankBlockEntity tank : column) {
            int amount = Math.min(tank.kind.capacity(), left);
            left -= amount;
            tank.setOwn(amount > 0 ? contents.withAmount(amount) : FluidAmount.EMPTY);
        }
    }

    /** Re-fills the column this tank belongs to from the bottom up (after placing or removing a member). */
    public void settle() {
        List<TankBlockEntity> column = column();
        distribute(column, total(column));
    }

    private void setOwn(FluidAmount value) {
        FluidAmount next = value.isEmpty() ? FluidAmount.EMPTY : value;
        if (!next.equals(fluid)) {
            fluid = next;
            changed();
        }
    }

    /** Sets this block's contents directly (placing a tank item). */
    public void load(FluidAmount value, boolean drain) {
        fluid = value.isEmpty() ? FluidAmount.EMPTY : value;
        draining = drain;
        changed();
    }

    /** Toggles the drain mode of the column; returns the new mode. */
    public boolean toggleDraining() {
        boolean next = !draining;
        for (TankBlockEntity tank : column()) {
            tank.draining = next;
            tank.drainBackoff.reset();
            tank.changed();
        }
        return next;
    }

    private void changed() {
        markDirty();
        flushDirty();
        pendingSync = true;
    }

    // ---- ticking: drain mode ----

    @Override
    public void serverTick() {
        if (pendingSync) {
            pendingSync = false;
            syncToClient();
        }
        long time = level.getGameTime();
        if (!draining || (time & 3) != 1 || !drainBackoff.due(time)) {
            return;
        }
        if (kind.stacks() && level.getBlockEntity(worldPosition.below()) instanceof TankBlockEntity below && below.kind == kind) {
            return;
        }
        if (push()) {
            drainBackoff.succeeded(time);
        } else {
            drainBackoff.failed(time);
        }
    }

    private boolean push() {
        List<TankBlockEntity> column = column();
        FluidAmount contents = total(column);
        if (contents.isEmpty()) {
            return false;
        }
        FluidTransfer target = neighbors().fluids(Direction.DOWN);
        if (target == null) {
            return false;
        }
        int filled = target.fill(contents.withAmount(Math.min(contents.amount(), TankKind.DRAIN_PER_BATCH)), false);
        if (filled <= 0) {
            return false;
        }
        distribute(column, contents.withAmount(contents.amount() - filled));
        return true;
    }

    @Override
    protected void onNeighborStorageChanged() {
        drainBackoff.reset();
    }

    @Override
    protected void onRemoved(boolean unloaded) {
        if (!unloaded && level != null && !level.isClientSide() && kind.stacks()
            && level.getBlockEntity(worldPosition.above()) instanceof TankBlockEntity above && above.kind == kind) {
            above.settle();
        }
    }

    // ---- automation ----

    @Override
    public @Nullable FluidTanks fluidsFor(@Nullable Direction side) {
        if (view == null) {
            view = new View();
        }
        return view;
    }

    /** The column as one tank. */
    private final class View implements FluidTanks {
        @Override
        public int tanks() {
            return 1;
        }

        @Override
        public FluidAmount getFluid(int tank) {
            return total(column());
        }

        @Override
        public void setFluid(int tank, FluidAmount value) {
            distribute(column(), value);
        }

        @Override
        public int getCapacity(int tank) {
            return column().size() * kind.capacity();
        }

        @Override
        public boolean canFill(int tank, FluidAmount offered) {
            return kind.accepts(offered.fluid());
        }

        @Override
        public boolean canDrain(int tank) {
            return true;
        }

        @Override
        public boolean joins(int tank, FluidAmount current, FluidAmount offered) {
            return current.fluid() == offered.fluid();
        }

        @Override
        public FluidAmount joined(int tank, FluidAmount current, FluidAmount offered, int filled) {
            return current.isEmpty() ? offered.withAmount(filled) : Purity.merge(current, offered.withAmount(filled));
        }
    }

    // ---- persistence and client data ----

    @Override
    protected void saveData(DataWriter out) {
        out.putFluid("Fluid", fluid);
        if (draining) {
            out.putBoolean("Draining", true);
        }
    }

    @Override
    protected void loadData(DataReader in) {
        fluid = in.getFluid("Fluid");
        draining = in.getBoolean("Draining", false);
    }

    @Override
    protected void saveSyncData(DataWriter out) {
        saveData(out);
    }

    @Override
    protected void loadSyncData(DataReader in) {
        loadData(in);
    }
}
