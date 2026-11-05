package com.tonywww.foodindustrial.machine.entity;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.EnergyStore;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.ItemSlots;
import com.tonywww.foodindustrial.core.storage.ItemTransfer;
import com.tonywww.foodindustrial.core.storage.StorageHost;
import com.tonywww.foodindustrial.machine.MachinePerf;
import com.tonywww.foodindustrial.machine.block.MachineBlock;
import com.tonywww.foodindustrial.machine.component.Backoff;
import com.tonywww.foodindustrial.machine.component.CatalystCounters;
import com.tonywww.foodindustrial.machine.component.EnergyBuffer;
import com.tonywww.foodindustrial.machine.component.HeatSource;
import com.tonywww.foodindustrial.machine.component.MachineInventory;
import com.tonywww.foodindustrial.machine.component.RedstoneControl;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.component.TankSet;
import com.tonywww.foodindustrial.machine.component.UpgradeSet;
import com.tonywww.foodindustrial.machine.component.VentMode;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.TickPhase;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.platform.PlatformFuel;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A machine (machines/02 §4–6): owns the components, runs a batch every 4 ticks on its type's phase,
 * sleeps when there is nothing to do and wakes on events, exposes its slots per face and moves items
 * and fluids in and out automatically with back-off.
 * <p>
 * Hot path (machines/02 §6.3 rule 10): {@link #serverTick} and a batch that completes nothing allocate
 * nothing; matching runs only after the contents changed.
 */
public abstract class MachineBlockEntity extends FIBlockEntity implements StorageHost {
    /** Item data key of the settings a dismantled machine keeps (machines/04 §4). */
    public static final String CONFIG_ITEM_KEY = "FoodIndustrialConfig";
    /** Ticks the block stays {@code active} after the last running batch. */
    private static final int ACTIVE_LINGER = 40;

    protected final MachineDefinition definition;
    protected final MachineInventory inventory;
    protected final TankSet tanks;
    @Nullable
    protected final EnergyBuffer energy;
    @Nullable
    protected final HeatSource heat;
    protected final SideConfig sides = new SideConfig();
    protected final UpgradeSet upgrades = new UpgradeSet();
    protected final CatalystCounters catalysts;
    protected RedstoneControl redstone = RedstoneControl.IGNORE;
    private int mode;
    private boolean powered;
    private Capabilities capabilities;

    private MachineStatus status = MachineStatus.WAITING;
    private boolean sleeping;
    private long wakeAt = Long.MAX_VALUE;
    private long lastRunning = Long.MIN_VALUE / 2;
    private final Backoff pushItems = new Backoff();
    private final Backoff pushFluids = new Backoff();
    private final Backoff pullItems = new Backoff();
    private final Backoff pullFluids = new Backoff();

    private final ItemSlots[] itemViews = new ItemSlots[7];
    private final FluidTanks[] fluidViews = new FluidTanks[7];

    protected MachineBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition.blockEntityType(), pos, state);
        this.definition = definition;
        this.inventory = new MachineInventory(definition.layout(), this::slotsChanged);
        this.tanks = new TankSet(definition.layout().fluidIn(), definition.layout().fluidOut(), definition.tankCapacity(), this::tanksChanged);
        this.energy = definition.power() == PowerKind.ENERGY
            ? new EnergyBuffer(definition.energyCapacity(), Math.max(1, definition.energyCapacity() / 20), this::energyReceived) : null;
        this.heat = definition.power() == PowerKind.FUEL ? new HeatSource() : null;
        this.catalysts = new CatalystCounters(definition.layout().catalyst());
        this.capabilities = definition.capabilities();
        recomputeCapabilities();
    }

    // ---- accessors ----

    public MachineDefinition definition() {
        return definition;
    }

    public MachineInventory inventory() {
        return inventory;
    }

    public TankSet tanks() {
        return tanks;
    }

    @Nullable
    public EnergyBuffer energy() {
        return energy;
    }

    @Nullable
    public HeatSource heat() {
        return heat;
    }

    public SideConfig sides() {
        return sides;
    }

    public UpgradeSet upgrades() {
        return upgrades;
    }

    public CatalystCounters catalysts() {
        return catalysts;
    }

    public MachineStatus status() {
        return status;
    }

    public boolean isPowered() {
        return powered;
    }

    public RedstoneControl redstone() {
        return redstone;
    }

    /** Front of the machine. */
    public Direction facing() {
        return MachineBlock.facing(getBlockState());
    }

    /** Current mode, or {@code null} for machines without modes. */
    @Nullable
    public MachineMode mode() {
        return definition.modes().isEmpty() ? null : definition.modes().get(mode);
    }

    /** Index of the current mode (0 for machines without modes). */
    public int modeIndex() {
        return mode;
    }

    /** Capabilities right now: the machine's own, the mode's and the installed modules'. */
    public Capabilities capabilities() {
        return capabilities;
    }

    /** Whether the current mode allows recipes of {@code type}. */
    public boolean allows(ProcessType type) {
        MachineMode current = mode();
        return current == null || current.allows(type);
    }

    // ---- settings (also used by the configuration screens, I10) ----

    public void setRedstone(RedstoneControl value) {
        redstone = value;
        settingsChanged();
    }

    public void setMode(int index) {
        if (definition.modes().isEmpty()) {
            return;
        }
        mode = Math.floorMod(index, definition.modes().size());
        recomputeCapabilities();
        modeChanged();
        settingsChanged();
    }

    /** After the side configuration or automation switches changed. */
    public void sidesChanged() {
        invalidateStorage();
        java.util.Arrays.fill(itemViews, null);
        java.util.Arrays.fill(fluidViews, null);
        pushItems.reset();
        pushFluids.reset();
        pullItems.reset();
        pullFluids.reset();
        settingsChanged();
    }

    protected void settingsChanged() {
        markDirty();
        wake();
    }

    /** Called when the mode changed; processing machines drop progress the new mode cannot finish. */
    protected void modeChanged() {
    }

    private void recomputeCapabilities() {
        Capabilities result = definition.capabilities();
        MachineMode current = mode();
        if (current != null) {
            result = result.with(current.capabilities());
        }
        capabilities = result.with(upgrades.moduleCapabilities());
    }

    // ---- events (machines/02 §6.2 rule 6) ----

    /** Wakes the machine: the next batch on its phase runs. */
    public final void wake() {
        sleeping = false;
    }

    private void slotsChanged(SlotRole role) {
        markDirty();
        switch (role) {
            case INPUT, CATALYST -> inputsChanged();
            case OUTPUT -> {
                outputsChanged();
                pushItems.reset();
            }
            case UPGRADE, MODULE -> {
                upgrades.recompute(inventory);
                recomputeCapabilities();
                inputsChanged();
            }
            case FUEL -> {
            }
        }
        wake();
    }

    private void tanksChanged(SlotRole role) {
        markDirty();
        if (role == SlotRole.INPUT) {
            inputsChanged();
        } else {
            outputsChanged();
            pushFluids.reset();
        }
        wake();
    }

    private void energyReceived() {
        markUnsaved();
        if (status == MachineStatus.NO_POWER) {
            wake();
        } else if (sleeping) {
            flushDirty();
        }
    }

    /** The recipe inputs (items, fluids, catalysts) or capabilities changed. */
    protected void inputsChanged() {
    }

    /** The output slots or tanks changed. */
    protected void outputsChanged() {
    }

    @Override
    protected void onNeighborBlockChanged() {
        boolean now = level != null && level.hasNeighborSignal(worldPosition);
        if (now != powered) {
            powered = now;
            wake();
        }
    }

    @Override
    protected void onLoaded() {
        if (level != null && !level.isClientSide()) {
            powered = level.hasNeighborSignal(worldPosition);
            upgrades.recompute(inventory);
            recomputeCapabilities();
            wake();
        }
    }

    /** Placed by a player (after the block entity exists). */
    public void onPlaced() {
        wake();
    }

    // ---- scheduling (machines/02 §6.1) ----

    @Override
    public final void serverTick() {
        long time = level.getGameTime();
        if ((time & 3) != definition.phase().tick()) {
            return;
        }
        if (sleeping && time < wakeAt) {
            return;
        }
        if (MachinePerf.enabled()) {
            long start = System.nanoTime();
            batch(time);
            MachinePerf.record(definition.id(), System.nanoTime() - start, sleeping);
        } else {
            batch(time);
        }
    }

    private void batch(long time) {
        sleeping = false;
        boolean vented = redstone.allows(isPowered()) && vent(VentMode.ALL);
        status = tickBatch(TickPhase.BATCH, time);
        if (status == MachineStatus.RUNNING) {
            lastRunning = time;
        }
        if (status == MachineStatus.OUTPUT_FULL && outputFluidsBlocked()) {
            vented |= vent(VentMode.OVERFLOW);
        }
        if (heat != null && heat.lit()) {
            heat.burn(TickPhase.BATCH);
            markUnsaved();
        }
        autoIo(time);
        updateActive(time);
        flushDirty();
        if (status.sleeps() && (heat == null || !heat.lit()) && !vented) {
            sleeping = true;
            wakeAt = nextTimer(time);
        }
    }

    /** Whether the planned fluid products do not fit (processing machines). */
    protected boolean outputFluidsBlocked() {
        return false;
    }

    /**
     * Vents up to {@link VentMode#PER_BATCH} mB of gas from each output tank switched to {@code mode}
     * (machines/03 §4); returns whether anything was vented.
     */
    private boolean vent(VentMode mode) {
        if (!tanks.hasVents()) {
            return false;
        }
        boolean vented = false;
        for (int tank = tanks.inputs(); tank < tanks.size(); tank++) {
            FluidAmount fluid = tanks.get(tank);
            if (tanks.vent(tank) == mode && !fluid.isEmpty() && com.tonywww.foodindustrial.platform.PlatformFluids.isGas(fluid.fluid())) {
                tanks.drain(tank, Math.min(fluid.amount(), VentMode.PER_BATCH));
                vented = true;
            }
        }
        if (vented && level instanceof net.minecraft.server.level.ServerLevel server) {
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, worldPosition.getX() + 0.5, worldPosition.getY() + 1.05,
                worldPosition.getZ() + 0.5, 3, 0.2, 0.05, 0.2, 0.01);
        }
        return vented;
    }

    /** Sets the venting switch of an output tank (machines/04 §1). */
    public void setVent(int tank, VentMode mode) {
        if (tank >= tanks.inputs() && tank < tanks.size()) {
            tanks.setVent(tank, mode);
            settingsChanged();
        }
    }

    /**
     * One batch of work over {@code delta} ticks.
     *
     * @return the status after the batch; sleeping states end the batch work until an event
     */
    protected abstract MachineStatus tickBatch(int delta, long gameTime);

    private long nextTimer(long time) {
        long next = Long.MAX_VALUE;
        if (getBlockState().getValue(MachineBlock.ACTIVE)) {
            next = Math.min(next, lastRunning + ACTIVE_LINGER);
        }
        if (autoOutputsItems() && inventory.any(SlotRole.OUTPUT)) {
            next = Math.min(next, pushItems.next());
        }
        if (autoOutputsFluids() && tanks.any(SlotRole.OUTPUT)) {
            next = Math.min(next, pushFluids.next());
        }
        return Math.max(next, time + 1);
    }

    private void updateActive(long time) {
        BlockState state = getBlockState();
        if (!state.hasProperty(MachineBlock.ACTIVE)) {
            return;
        }
        boolean active = state.getValue(MachineBlock.ACTIVE);
        boolean running = status == MachineStatus.RUNNING || (heat != null && heat.lit());
        if (running && !active) {
            level.setBlock(worldPosition, state.setValue(MachineBlock.ACTIVE, true), 3);
        } else if (!running && active && time - lastRunning >= ACTIVE_LINGER) {
            level.setBlock(worldPosition, state.setValue(MachineBlock.ACTIVE, false), 3);
        }
    }

    // ---- fuel ----

    /**
     * For a run: lights a new fuel item if none burns; returns whether the fire is lit (furnace rules,
     * 2026-11-04).
     */
    public boolean fireForRecipe() {
        if (heat == null || heat.lit()) {
            return heat != null;
        }
        for (int i = 0; i < inventory.count(SlotRole.FUEL); i++) {
            int slot = inventory.slot(SlotRole.FUEL, i);
            ItemStack fuel = inventory.get(slot);
            int ticks = PlatformFuel.burnTime(level, fuel);
            if (ticks > 0) {
                inventory.set(slot, PlatformFuel.afterBurning(fuel));
                heat.light(ticks);
                return true;
            }
        }
        return false;
    }

    // ---- automatic input and output (machines/02 §6.3 rule 7, machines/04 §3.3) ----

    /** Whether item outputs are pushed automatically (the side switch; multiblocks ask their ports). */
    protected boolean autoOutputsItems() {
        return sides.autoOutputItems();
    }

    protected boolean autoOutputsFluids() {
        return sides.autoOutputFluids();
    }

    private void autoIo(long time) {
        if (autoOutputsItems() && inventory.any(SlotRole.OUTPUT) && pushItems.due(time)) {
            if (pushItems()) {
                pushItems.succeeded(time);
            } else {
                pushItems.failed(time);
            }
        }
        if (autoOutputsFluids() && tanks.any(SlotRole.OUTPUT) && pushFluids.due(time)) {
            if (pushFluids()) {
                pushFluids.succeeded(time);
            } else {
                pushFluids.failed(time);
            }
        }
        if (sides.autoInputItems() && status == MachineStatus.WAITING && pullItems.due(time)) {
            if (pullItems()) {
                pullItems.succeeded(time);
            } else {
                pullItems.failed(time);
            }
        }
        if (sides.autoInputFluids() && status == MachineStatus.WAITING && pullFluids.due(time)) {
            if (pullFluids()) {
                pullFluids.succeeded(time);
            } else {
                pullFluids.failed(time);
            }
        }
    }

    /** Neighbouring storage changed (Forge / NeoForge neighbour notifications): retry automation. */
    @Override
    protected void onNeighborStorageChanged() {
        if (sides.autoInputItems() || sides.autoInputFluids()) {
            pullItems.reset();
            pullFluids.reset();
            wake();
        }
        if (autoOutputsItems() || autoOutputsFluids()) {
            pushItems.reset();
            pushFluids.reset();
            wake();
        }
    }

    /** Pushes one output stack into a neighbour on an item output face; returns whether something moved. */
    protected boolean pushItems() {
        Direction facing = facing();
        for (SideConfig.Face face : SideConfig.Face.values()) {
            if (!sides.items(face).outputs()) {
                continue;
            }
            ItemTransfer target = neighbors().items(face.toDirection(facing));
            if (target == null) {
                continue;
            }
            SideConfig.OutputScope scope = sides.itemScope(face);
            for (int i = 0; i < inventory.count(SlotRole.OUTPUT); i++) {
                int slot = inventory.slot(SlotRole.OUTPUT, i);
                ItemStack stack = inventory.get(slot);
                if (stack.isEmpty() || !scopeAllows(scope, slot)) {
                    continue;
                }
                ItemStack rest = target.insert(stack, false);
                if (rest.getCount() != stack.getCount()) {
                    inventory.set(slot, rest);
                    return true;
                }
            }
        }
        return false;
    }

    /** Pushes one output tank into a neighbour on a fluid output face. */
    protected boolean pushFluids() {
        Direction facing = facing();
        for (SideConfig.Face face : SideConfig.Face.values()) {
            if (!sides.fluids(face).outputs()) {
                continue;
            }
            FluidTransfer target = neighbors().fluids(face.toDirection(facing));
            if (target == null) {
                continue;
            }
            int only = sides.fluidOutputTank(face);
            for (int tank = tanks.inputs(); tank < tanks.size(); tank++) {
                FluidAmount fluid = tanks.get(tank);
                if (fluid.isEmpty() || (only != SideConfig.ALL_TANKS && tank != tanks.inputs() + only)) {
                    continue;
                }
                int filled = target.fill(fluid, false);
                if (filled > 0) {
                    tanks.drain(tank, filled);
                    return true;
                }
            }
        }
        return false;
    }

    /** Pulls one stack a recipe of this machine can use from a neighbour on an item input face. */
    protected boolean pullItems() {
        RecipeIndex index = RecipeIndex.get(level);
        Direction facing = facing();
        for (SideConfig.Face face : SideConfig.Face.values()) {
            if (!sides.items(face).inputs()) {
                continue;
            }
            ItemTransfer source = neighbors().items(face.toDirection(facing));
            if (source == null) {
                continue;
            }
            ItemStack pulled = source.extract(64, stack -> wantsItem(index, stack), true);
            if (pulled.isEmpty()) {
                continue;
            }
            int room = roomFor(pulled);
            if (room <= 0) {
                continue;
            }
            ItemStack taken = source.extract(Math.min(room, pulled.getCount()), stack -> PlatformItems.sameItemSameData(stack, pulled), false);
            if (!taken.isEmpty()) {
                insertInput(taken);
                return true;
            }
        }
        return false;
    }

    /** Pulls fluid a recipe of this machine can use from a neighbour on a fluid input face. */
    protected boolean pullFluids() {
        RecipeIndex index = RecipeIndex.get(level);
        Direction facing = facing();
        for (SideConfig.Face face : SideConfig.Face.values()) {
            if (!sides.fluids(face).inputs()) {
                continue;
            }
            FluidTransfer source = neighbors().fluids(face.toDirection(facing));
            if (source == null) {
                continue;
            }
            FluidAmount offered = source.drain(tanks.capacity(), true);
            if (offered.isEmpty() || !usesFluid(index, offered)) {
                continue;
            }
            int only = sides.fluidInputTank(face);
            for (int tank = 0; tank < tanks.inputs(); tank++) {
                if ((only == SideConfig.ALL_TANKS || tank == only) && tanks.allows(tank, offered) && tanks.joins(tank, offered)) {
                    int room = tanks.space(tank, offered);
                    if (room > 0) {
                        FluidAmount drained = source.drain(offered.withAmount(Math.min(room, offered.amount())), false);
                        if (!drained.isEmpty()) {
                            tanks.set(tank, tanks.joined(tank, drained, drained.amount()));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean usesFluid(RecipeIndex index, FluidAmount fluid) {
        for (ProcessType type : definition.types()) {
            if (allows(type) && index.usesFluid(type, fluid.fluid())) {
                return true;
            }
        }
        return false;
    }

    /** Whether automatic input should take {@code stack}: a locked slot wants it, or a recipe of this machine uses it. */
    protected boolean wantsItem(RecipeIndex index, ItemStack stack) {
        boolean locked = false;
        for (int i = 0; i < inventory.count(SlotRole.INPUT); i++) {
            ItemStack lock = inventory.lock(inventory.slot(SlotRole.INPUT, i));
            if (!lock.isEmpty()) {
                locked = true;
                if (PlatformItems.sameItemSameData(lock, stack)) {
                    return roomFor(stack) > 0;
                }
            }
        }
        if (locked && roomFor(stack) <= 0) {
            return false;
        }
        for (ProcessType type : definition.types()) {
            if (allows(type) && index.usesItem(type, stack.getItem())) {
                return roomFor(stack) > 0;
            }
        }
        return false;
    }

    /** Room for {@code stack} in the input slots. */
    protected int roomFor(ItemStack stack) {
        int room = 0;
        for (int i = 0; i < inventory.count(SlotRole.INPUT); i++) {
            int slot = inventory.slot(SlotRole.INPUT, i);
            if (!inventory.allowedByLock(slot, stack)) {
                continue;
            }
            ItemStack current = inventory.get(slot);
            if (current.isEmpty()) {
                room += Math.min(64, stack.getMaxStackSize());
            } else if (PlatformItems.sameItemSameData(current, stack)) {
                room += Math.min(64, stack.getMaxStackSize()) - current.getCount();
            }
        }
        return room;
    }

    /** Puts a stack into the input slots (equal stacks first). */
    protected void insertInput(ItemStack stack) {
        ItemStack rest = stack;
        for (int pass = 0; pass < 2 && !rest.isEmpty(); pass++) {
            for (int i = 0; i < inventory.count(SlotRole.INPUT) && !rest.isEmpty(); i++) {
                int slot = inventory.slot(SlotRole.INPUT, i);
                ItemStack current = inventory.get(slot);
                if (!inventory.allowedByLock(slot, rest) || (pass == 0) == current.isEmpty()
                    || (!current.isEmpty() && !PlatformItems.sameItemSameData(current, rest))) {
                    continue;
                }
                int moved = Math.min(Math.min(64, rest.getMaxStackSize()) - current.getCount(), rest.getCount());
                if (moved > 0) {
                    inventory.set(slot, current.isEmpty() ? rest.copyWithCount(moved) : current.copyWithCount(current.getCount() + moved));
                    rest = rest.getCount() == moved ? ItemStack.EMPTY : rest.copyWithCount(rest.getCount() - moved);
                }
            }
        }
    }

    // ---- automation views (StorageHost) ----

    @Override
    public @Nullable ItemSlots itemsFor(@Nullable Direction side) {
        int index = side == null ? 6 : side.ordinal();
        SideConfig.Face face = side == null ? null : SideConfig.Face.of(side, facing());
        SideConfig.Mode mode = face == null ? SideConfig.Mode.BOTH : sides.items(face);
        if (mode == SideConfig.Mode.OFF || inventory.size() == 0) {
            return null;
        }
        ItemSlots view = itemViews[index];
        if (view == null) {
            view = new ItemView(mode, face == null ? SideConfig.OutputScope.ALL : sides.itemScope(face));
            itemViews[index] = view;
        }
        return view;
    }

    @Override
    public @Nullable FluidTanks fluidsFor(@Nullable Direction side) {
        int index = side == null ? 6 : side.ordinal();
        SideConfig.Face face = side == null ? null : SideConfig.Face.of(side, facing());
        SideConfig.Mode mode = face == null ? SideConfig.Mode.BOTH : sides.fluids(face);
        if (mode == SideConfig.Mode.OFF || tanks.size() == 0) {
            return null;
        }
        FluidTanks view = fluidViews[index];
        if (view == null) {
            view = new FluidView(mode, face == null ? SideConfig.ALL_TANKS : sides.fluidInputTank(face),
                face == null ? SideConfig.ALL_TANKS : sides.fluidOutputTank(face));
            fluidViews[index] = view;
        }
        return view;
    }

    /** Whether an output face with {@code scope} serves output slot {@code slot}. */
    protected final boolean scopeAllows(SideConfig.OutputScope scope, int slot) {
        return switch (scope) {
            case ALL -> true;
            case MAIN -> inventory.outputServes(slot, true);
            case BYPRODUCT -> inventory.outputServes(slot, false);
        };
    }

    @Override
    public @Nullable EnergyStore energyFor(@Nullable Direction side) {
        if (energy == null || (side != null && sides.closed(SideConfig.Face.of(side, facing())))) {
            return null;
        }
        return energy;
    }

    /** Item slots as one face of automation sees them. */
    protected final class ItemView implements ItemSlots {
        private final SideConfig.Mode mode;
        private final SideConfig.OutputScope scope;

        ItemView(SideConfig.Mode mode, SideConfig.OutputScope scope) {
            this.mode = mode;
            this.scope = scope;
        }

        @Override
        public int slots() {
            return inventory.size();
        }

        @Override
        public ItemStack getStack(int slot) {
            return inventory.get(slot);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            inventory.set(slot, stack);
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack) {
            SlotRole role = inventory.role(slot);
            if (!mode.inputs() || !role.automationInserts()) {
                return false;
            }
            return switch (role) {
                case INPUT -> inventory.allowedByLock(slot, stack);
                case FUEL -> PlatformFuel.burnTime(level, stack) > 0;
                default -> true;
            };
        }

        @Override
        public boolean canExtract(int slot) {
            return mode.outputs() && inventory.role(slot).automationExtracts() && scopeAllows(scope, slot);
        }
    }

    /** Tanks as one face of automation sees them. */
    protected final class FluidView implements FluidTanks {
        private final SideConfig.Mode mode;
        private final int inputTank;
        private final int outputTank;

        FluidView(SideConfig.Mode mode, int inputTank, int outputTank) {
            this.mode = mode;
            this.inputTank = inputTank;
            this.outputTank = outputTank;
        }

        @Override
        public int tanks() {
            return tanks.size();
        }

        @Override
        public FluidAmount getFluid(int tank) {
            return tanks.get(tank);
        }

        @Override
        public void setFluid(int tank, FluidAmount fluid) {
            tanks.set(tank, fluid);
        }

        @Override
        public int getCapacity(int tank) {
            return tanks.capacity();
        }

        @Override
        public boolean canFill(int tank, FluidAmount fluid) {
            return mode.inputs() && tanks.role(tank) == SlotRole.INPUT && (inputTank == SideConfig.ALL_TANKS || tank == inputTank)
                && tanks.allows(tank, fluid);
        }

        @Override
        public boolean canDrain(int tank) {
            return mode.outputs() && tanks.role(tank) == SlotRole.OUTPUT
                && (outputTank == SideConfig.ALL_TANKS || tank == tanks.inputs() + outputTank);
        }

        @Override
        public boolean joins(int tank, FluidAmount current, FluidAmount offered) {
            return current.fluid() == offered.fluid();
        }

        @Override
        public FluidAmount joined(int tank, FluidAmount current, FluidAmount offered, int filled) {
            return tanks.joined(tank, offered, filled);
        }
    }

    // ---- persistence ----

    @Override
    protected void saveData(DataWriter out) {
        inventory.save(out);
        tanks.save(out);
        if (energy != null) {
            energy.save(out);
        }
        if (heat != null) {
            heat.save(out);
        }
        catalysts.save(out);
        saveConfig(out);
    }

    @Override
    protected void loadData(DataReader in) {
        inventory.load(in);
        tanks.load(in);
        if (energy != null) {
            energy.load(in);
        }
        if (heat != null) {
            heat.load(in);
        }
        catalysts.load(in);
        loadConfig(in);
        java.util.Arrays.fill(itemViews, null);
        java.util.Arrays.fill(fluidViews, null);
    }

    /**
     * Settings a player made (machines/04 §4): sides with their scopes, automation, redstone, mode, locks
     * and venting switches. Written only when not default, so dismantled machines with default settings still stack.
     */
    public void saveConfig(DataWriter out) {
        sides.save(out);
        if (redstone != RedstoneControl.IGNORE) {
            out.putByte("Redstone", (byte) redstone.ordinal());
        }
        if (mode != 0) {
            out.putInt("Mode", mode);
        }
        inventory.saveLocks(out);
        tanks.saveLocks(out);
        tanks.saveVents(out);
    }

    public void loadConfig(DataReader in) {
        sides.load(in);
        sides.clampTanks(tanks.inputs(), tanks.outputs());
        redstone = RedstoneControl.byIndex(in.getByte("Redstone", (byte) 0));
        mode = definition.modes().isEmpty() ? 0 : Math.floorMod(in.getInt("Mode", 0), definition.modes().size());
        inventory.loadLocks(in);
        tanks.loadLocks(in);
        tanks.loadVents(in);
        recomputeCapabilities();
    }

    /** Whether the settings are all default. */
    public boolean hasDefaultConfig() {
        return sides.isDefault() && redstone == RedstoneControl.IGNORE && mode == 0 && !inventory.hasLocks() && !tanks.hasLocks() && !tanks.hasVents();
    }

    /**
     * Applies settings from a dismantled machine's item or a redprint (machines/04 §4) to a machine in
     * the world: faces stay relative to the front, so they follow this machine's facing.
     */
    public void applyConfig(DataReader in) {
        loadConfig(in);
        modeChanged();
        inputsChanged();
        sidesChanged();
    }

    // ---- player actions (machines/04 §1, §4) ----

    /**
     * Alt + click on an input slot: unlocks a locked slot; otherwise locks it to its contents, or to
     * {@code carried} when empty.
     */
    public void toggleSlotLock(int slot, ItemStack carried) {
        if (slot < 0 || slot >= inventory.size() || inventory.role(slot) != SlotRole.INPUT) {
            return;
        }
        if (!inventory.lock(slot).isEmpty()) {
            inventory.setLock(slot, ItemStack.EMPTY);
        } else {
            ItemStack current = inventory.get(slot);
            inventory.setLock(slot, current.isEmpty() ? carried : current);
        }
        flushDirty();
    }

    /** Alt + click on a tank: unlocks, or locks to its contents or to {@code carried} when empty. */
    public void toggleTankLock(int tank, FluidAmount carried) {
        if (tank < 0 || tank >= tanks.size()) {
            return;
        }
        if (!tanks.lock(tank).isEmpty()) {
            tanks.setLock(tank, FluidAmount.EMPTY);
        } else {
            FluidAmount current = tanks.get(tank);
            tanks.setLock(tank, current.isEmpty() ? carried : current);
        }
        flushDirty();
    }

    /** Device wrench: turns the machine 90° clockwise (seen from above). */
    public void rotate() {
        BlockState state = getBlockState();
        if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) {
            return;
        }
        level.setBlock(worldPosition, state.rotate(net.minecraft.world.level.block.Rotation.CLOCKWISE_90), 3);
        sidesChanged();
        flushDirty();
    }

    /** Settings changed from outside a batch (screens, tools): report them now. */
    public void settingsApplied() {
        flushDirty();
    }

    /**
     * The menu changed slot contents directly (vanilla slot handling can change a stack in place):
     * re-reads everything that depends on the slots.
     */
    public void contentsEdited() {
        upgrades.recompute(inventory);
        recomputeCapabilities();
        inputsChanged();
        outputsChanged();
        pushItems.reset();
        markDirty();
        wake();
        flushDirty();
    }

    /** Drops the item contents when the block is broken (also by the device wrench). */
    @Override
    protected void onBroken() {
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.get(slot);
            if (!stack.isEmpty()) {
                net.minecraft.world.level.block.Block.popResource(level, worldPosition, stack.copy());
            }
        }
    }
}
