package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.StorageHost;
import com.tonywww.foodindustrial.machine.component.Backoff;
import com.tonywww.foodindustrial.machine.entity.FIBlockEntity;
import com.tonywww.foodindustrial.world.interaction.PumpingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * H16 hand seawater pump (I43, additives/07 §H16): one 1,000 mB tank. An empty-hand right click on the coast draws
 * an eighth of a W03 batch (125 mB of seawater, eight clicks to fill); buckets and liquid bottles take it out, and
 * like the leaching barrel it drains into a container right below. Pipes may take it out too, never put in.
 */
public class HandPumpBlockEntity extends FIBlockEntity implements StorageHost {
    public static final int CAPACITY = 1000;
    /** Clicks to draw one batch. */
    public static final int STROKES = 8;
    /** Most it hands to the container below per drain, mB (as the wooden tank). */
    private static final int DRAIN = 400;

    private FluidAmount fluid = FluidAmount.EMPTY;
    private final Backoff drainBackoff = new Backoff();
    private boolean pendingSync;
    @Nullable
    private View view;

    public HandPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public FluidAmount fluid() {
        return fluid;
    }

    /** One stroke: the batch the coast gives here divided by {@link #STROKES}; {@code false} when not coastal or full. */
    public Stroke stroke() {
        if (level == null) {
            return Stroke.NOT_COASTAL;
        }
        FluidAmount batch = PumpingRecipe.batchAt(level, worldPosition);
        if (batch == null || batch.isEmpty()) {
            return Stroke.NOT_COASTAL;
        }
        FluidAmount drawn = batch.withAmount(Math.max(1, batch.amount() / STROKES));
        if (!fluid.isEmpty() && !fluid.joins(drawn)) {
            return Stroke.FULL;
        }
        int room = CAPACITY - fluid.amount();
        if (room <= 0) {
            return Stroke.FULL;
        }
        int amount = Math.min(room, drawn.amount());
        set(fluid.isEmpty() ? drawn.withAmount(amount) : fluid.withAmount(fluid.amount() + amount));
        return Stroke.DRAWN;
    }

    public enum Stroke { DRAWN, FULL, NOT_COASTAL }

    /** Replaces the contents (containers took some out). */
    public void set(FluidAmount value) {
        fluid = value.amount() <= 0 ? FluidAmount.EMPTY : value;
        drainBackoff.reset();
        pendingSync = true;
        markDirty();
    }

    @Override
    public void serverTick() {
        if (pendingSync) {
            pendingSync = false;
            syncToClient();
        }
        long time = level.getGameTime();
        if ((time & 3) != 1 || fluid.isEmpty() || !drainBackoff.due(time)) {
            return;
        }
        FluidTransfer target = neighbors().fluids(Direction.DOWN);
        int filled = target == null ? 0 : target.fill(fluid.withAmount(Math.min(fluid.amount(), DRAIN)), false);
        if (filled > 0) {
            set(fluid.withAmount(fluid.amount() - filled));
            drainBackoff.succeeded(time);
        } else {
            drainBackoff.failed(time);
        }
    }

    @Override
    protected void onNeighborStorageChanged() {
        drainBackoff.reset();
    }

    @Override
    public @Nullable FluidTanks fluidsFor(@Nullable Direction side) {
        if (view == null) {
            view = new View();
        }
        return view;
    }

    /** The tank, drain only. */
    private final class View implements FluidTanks {
        @Override
        public int tanks() {
            return 1;
        }

        @Override
        public FluidAmount getFluid(int tank) {
            return fluid;
        }

        @Override
        public void setFluid(int tank, FluidAmount value) {
            set(value);
        }

        @Override
        public int getCapacity(int tank) {
            return CAPACITY;
        }

        @Override
        public boolean canFill(int tank, FluidAmount offered) {
            return false;
        }

        @Override
        public boolean canDrain(int tank) {
            return true;
        }

        @Override
        public boolean joins(int tank, FluidAmount current, FluidAmount offered) {
            return current.joins(offered);
        }

        @Override
        public FluidAmount joined(int tank, FluidAmount current, FluidAmount offered, int filled) {
            return current.isEmpty() ? offered.withAmount(filled) : current.withAmount(current.amount() + filled);
        }
    }

    @Override
    protected void saveData(DataWriter out) {
        out.putFluid("Fluid", fluid);
    }

    @Override
    protected void loadData(DataReader in) {
        fluid = in.getFluid("Fluid");
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
