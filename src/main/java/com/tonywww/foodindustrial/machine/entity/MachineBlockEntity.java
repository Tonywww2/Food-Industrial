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
import com.tonywww.foodindustrial.machine.definition.Climate;
import com.tonywww.foodindustrial.machine.definition.GasSupply;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.TickPhase;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.platform.PlatformFuel;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.Environment;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
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
    /** Running ticks stored by the hand crank (manual machines). */
    private int manualPower;
    /** Environment of the position while in an outdoors mode, and when to look again. */
    private Capabilities environment = Capabilities.NONE;
    private long environmentDue;
    /** Translation key of what the machine lacks to run (a {@code MachineRequirement}), or {@code null}. */
    @Nullable
    protected String lacking;
    /** Climate of a fermentation room while it runs (M06). */
    private boolean warm;
    private boolean humid;
    /** Running ticks since the room last used water. */
    private int climateTicks;
    /** The air intake is open (machines/03 §1): the back face of a single block, an intake of a multiblock. */
    private boolean airOpen;
    /** An input tank holds the machine's gas supply (M14 nitrogen), and running ticks since the last use. */
    private boolean gasPresent;
    private int gasTicks;

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
        this.heat = definition.layout().fuel() > 0 ? new HeatSource() : null;
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

    /** Running ticks the hand crank has stored (manual machines). */
    public int manualPower() {
        return manualPower;
    }

    /** Translation key of what the machine lacks to run, or {@code null}. */
    @Nullable
    public String lacking() {
        return lacking;
    }

    /** Whether fuel burns while the room runs (M06: warm). */
    public boolean warm() {
        return warm;
    }

    /** Whether water is used while the room runs (M06: humid). */
    public boolean humid() {
        return humid;
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
        environmentDue = 0;
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

    protected final void recomputeCapabilities() {
        Capabilities result = definition.capabilities();
        if (definition.airIntake() && airOpen) {
            result = result.with(Capabilities.of(MachineDefinition.AIR));
        }
        if (definition.gas() != null && gasPresent) {
            result = result.with(Capabilities.of(definition.gas().capability()));
        }
        MachineMode current = mode();
        if (current != null) {
            result = result.with(current.capabilities());
            if (current.outdoors()) {
                result = result.with(environment);
            }
        }
        capabilities = result.with(upgrades.moduleCapabilities()).with(extraCapabilities());
    }

    /** Capabilities a machine class adds now (computed with the others; call {@link #recomputeCapabilities} when they change). */
    protected Capabilities extraCapabilities() {
        return Capabilities.NONE;
    }

    // ---- hooks of machines with their own rules (I16, I17) ----

    /**
     * Whether the machine takes {@code recipe} at all right now; recipes it does not take are passed
     * over without a message (M25: only regeneration recipes while the adsorbent is saturated).
     */
    public boolean offers(ProcessRecipe recipe) {
        return true;
    }

    /**
     * Translation key of what the machine lacks for {@code recipe} besides capabilities (M22: trays for
     * the fractions), or {@code null}.
     */
    @Nullable
    public String recipeLacking(ProcessRecipe recipe) {
        return null;
    }

    /** Copies the structure allows per parallel copy of the upgrades (M29: one per vessel layer). */
    public int structureParallel() {
        return 1;
    }

    /** Ticks at speed ×1 in front of every run (M29: sterilization in place). */
    public int setupTicks() {
        return 0;
    }

    /** Amount of output {@code index} of the completion being made, per mille (M27 cracking, M29 contamination). */
    public int outputPermille(ProcessRecipe recipe, int index) {
        return 1000;
    }

    /** A completion of {@code recipe} is about to be made. */
    public void completing(ProcessRecipe recipe) {
    }

    /** A completion of {@code recipe} was made with the catalysts in {@code catalystSlots}. */
    public void completed(ProcessRecipe recipe, int[] catalystSlots) {
    }

    /** One use of the catalyst in catalyst slot {@code index}: at {@code runs} uses one item is used up. */
    public void wearCatalyst(int index, int runs) {
        if (catalysts.wear(index, runs)) {
            inventory.shrink(inventory.slot(SlotRole.CATALYST, index), 1);
        }
    }

    /** Whether the last batch was contaminated (M29, info panel). */
    public boolean lastBatchContaminated() {
        return false;
    }

    /** Whether the catalyst in slot {@code index} is spent but kept for regeneration (M25 adsorbents). */
    public boolean catalystSaturated(int index) {
        return false;
    }

    /** The catalyst slot {@code index} was emptied: its count starts over. */
    protected void catalystEmptied(int index) {
        catalysts.reset(index);
    }

    /**
     * In an outdoors mode, looks at the environment of the position again every 20 ticks (as the T0
     * passive devices); a change re-matches the recipe.
     */
    private void refreshEnvironment(long time) {
        MachineMode current = mode();
        if (current == null || !current.outdoors() || time < environmentDue) {
            return;
        }
        environmentDue = time + TickPhase.PASSIVE_BATCH;
        Capabilities now = Environment.at(level, worldPosition);
        if (!now.equals(environment)) {
            environment = now;
            recomputeCapabilities();
            inputsChanged();
        }
    }

    // ---- air, gas and speed (I15) ----

    /** Speed multiplier of the upgrades and, for a tall multiblock, its height (per mille). */
    public int speedPermille() {
        return (int) Math.min(Integer.MAX_VALUE, (long) upgrades.speedPermille() * structureSpeedPermille() / 1000L);
    }

    /** Speed from the structure (variable-height multiblocks), per mille. */
    protected int structureSpeedPermille() {
        return 1000;
    }

    /** Whether the machine has air now (machines/03 §1). */
    public boolean airOpen() {
        return airOpen;
    }

    /** Whether an input tank holds the machine's gas supply now (M14 nitrogen). */
    public boolean gasPresent() {
        return gasPresent;
    }

    /** Single block: whether the block behind the machine lets air in. */
    protected boolean checkAir() {
        return level != null && level.getBlockState(worldPosition.relative(facing().getOpposite())).isAir();
    }

    /** Looks at the air intake again; a change re-matches the recipe. */
    protected final void refreshAir() {
        if (!definition.airIntake() || level == null || level.isClientSide()) {
            return;
        }
        boolean now = checkAir();
        if (now != airOpen) {
            airOpen = now;
            recomputeCapabilities();
            inputsChanged();
            markDirty();
            wake();
        }
    }

    private void refreshGas() {
        GasSupply supply = definition.gas();
        if (supply == null) {
            return;
        }
        boolean now = gasTank(supply) >= 0;
        if (now != gasPresent) {
            gasPresent = now;
            recomputeCapabilities();
        }
    }

    private int gasTank(GasSupply supply) {
        for (int tank = 0; tank < tanks.inputs(); tank++) {
            FluidAmount fluid = tanks.get(tank);
            if (!fluid.isEmpty() && fluid.amount() >= supply.amount()
                && PlatformTags.fluidId(fluid.fluid()).equals(supply.fluid())) {
                return tank;
            }
        }
        return -1;
    }

    /** A running batch of a recipe that needs the gas's capability: uses gas every interval. */
    public void gasBatch(int delta) {
        GasSupply supply = definition.gas();
        if (supply == null) {
            return;
        }
        gasTicks += delta;
        if (gasTicks >= supply.interval()) {
            gasTicks -= supply.interval();
            int tank = gasTank(supply);
            if (tank >= 0) {
                tanks.drain(tank, supply.amount());
            }
        }
    }

    // ---- manual power and climate (I14) ----

    /**
     * One turn of the hand crank: stores {@link PowerKind#MANUAL_PER_TURN} running ticks, up to
     * {@link PowerKind#MANUAL_MAX}; returns whether anything was stored.
     */
    public boolean crank() {
        if (definition.power() != PowerKind.MANUAL || manualPower >= PowerKind.MANUAL_MAX) {
            return false;
        }
        manualPower = Math.min(PowerKind.MANUAL_MAX, manualPower + PowerKind.MANUAL_PER_TURN);
        markDirty();
        wake();
        flushDirty();
        return true;
    }

    /**
     * Speed of a drive by Create's transmission interface on top (I25, 2026-11-11), per mille of the hand
     * crank: 32 RPM runs as fast as cranking, faster shafts faster, at most ×4; 0 without a turning
     * interface (or with the Create integration switched off).
     */
    public int drivePermille() {
        if (definition.power() != PowerKind.MANUAL || level == null || !com.tonywww.foodindustrial.integration.Integrations.enabled("create")) {
            return 0;
        }
        float rpm = com.tonywww.foodindustrial.platform.PlatformKinetics.speedAt(level, worldPosition.above());
        return rpm <= 0 ? 0 : (int) Math.min(4000L, Math.round(rpm * 1000.0 / 32.0));
    }

    /** Takes up to {@code ticks} of stored manual power for a run; returns the ticks taken. */
    public int takeManualPower(int ticks) {
        int taken = Math.min(ticks, manualPower);
        if (taken > 0) {
            manualPower -= taken;
            markUnsaved();
        }
        return taken;
    }

    /**
     * A running batch of {@code delta} ticks of a fermentation room (M06): optional fuel is lit as in a
     * furnace; water in an input tank makes it humid and is used every {@link Climate#interval()} ticks;
     * returns whether the room is warm and humid.
     */
    public boolean climateBatch(int delta) {
        Climate climate = definition.climate();
        if (climate == null) {
            return false;
        }
        boolean wasWarm = warm;
        boolean wasHumid = humid;
        warm = fireForRecipe();
        int waterTank = -1;
        for (int tank = 0; tank < tanks.inputs() && waterTank < 0; tank++) {
            FluidAmount fluid = tanks.get(tank);
            if (!fluid.isEmpty() && fluid.fluid() == Fluids.WATER && fluid.amount() >= climate.water()) {
                waterTank = tank;
            }
        }
        humid = waterTank >= 0;
        climateTicks += delta;
        if (climateTicks >= climate.interval()) {
            climateTicks -= climate.interval();
            if (humid) {
                tanks.drain(waterTank, climate.water());
            }
        }
        if (warm != wasWarm || humid != wasHumid) {
            markDirty();
        }
        return warm && humid;
    }

    // ---- events (machines/02 §6.2 rule 6) ----

    /** Wakes the machine: the next batch on its phase runs. */
    public final void wake() {
        sleeping = false;
    }

    private void slotsChanged(SlotRole role) {
        markDirty();
        switch (role) {
            case INPUT -> inputsChanged();
            case CATALYST -> {
                for (int i = 0; i < inventory.count(SlotRole.CATALYST); i++) {
                    if (inventory.get(SlotRole.CATALYST, i).isEmpty()) {
                        catalystEmptied(i);
                    }
                }
                inputsChanged();
            }
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
            refreshGas();
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
        if (definition.watchesNeighbours()) {
            wake();
        }
        refreshAir();
    }

    @Override
    protected void onLoaded() {
        if (level != null && !level.isClientSide()) {
            powered = level.hasNeighborSignal(worldPosition);
            upgrades.recompute(inventory);
            airOpen = definition.airIntake() && checkAir();
            gasPresent = definition.gas() != null && gasTank(definition.gas()) >= 0;
            recomputeCapabilities();
            wake();
        }
    }

    /** Placed by a player (after the block entity exists). */
    public void onPlaced() {
        refreshAir();
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
        refreshEnvironment(time);
        boolean vented = redstone.allows(isPowered()) && vent(VentMode.ALL);
        status = tickBatch(TickPhase.BATCH, time);
        if (status.running()) {
            lastRunning = time;
        } else if (warm || humid) {
            // the climate of a fermentation room counts only while it runs
            warm = false;
            humid = false;
            markDirty();
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

    /** Development: runs {@code count} batches at once, as if that many 4-tick cycles passed. */
    public void runBatches(int count) {
        long time = level.getGameTime();
        for (int i = 0; i < count; i++) {
            batch(time);
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
        MachineMode current = mode();
        if (current != null && current.outdoors() && status == MachineStatus.MISSING) {
            next = Math.min(next, environmentDue);
        }
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
        boolean running = status.running() || (heat != null && heat.lit());
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

    /**
     * Whether automation should put {@code stack} only into the fuel slots: the machine burns fuel and
     * no recipe of the machine uses the item (I14: an item port also takes fuel).
     */
    public boolean fuelOnly(ItemStack stack) {
        if (inventory.count(SlotRole.FUEL) == 0 || PlatformFuel.burnTime(level, stack) <= 0) {
            return false;
        }
        RecipeIndex index = RecipeIndex.get(level);
        for (ProcessType type : definition.types()) {
            if (index.usesItem(type, stack.getItem())) {
                return false;
            }
        }
        return true;
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
            if (!definition.slotAccepts(role, slot - definition.layout().start(role), stack)) {
                return false;
            }
            return switch (role) {
                case INPUT -> inventory.allowedByLock(slot, stack) && (!inventory.lock(slot).isEmpty() || !fuelOnly(stack));
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
        if (manualPower > 0) {
            out.putInt("ManualPower", manualPower);
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
        manualPower = Math.max(0, Math.min(PowerKind.MANUAL_MAX, in.getInt("ManualPower", 0)));
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

    /** Locks input slot {@code slot} to {@code item} (JEI drop, I22). */
    public void lockSlotTo(int slot, ItemStack item) {
        if (slot < 0 || slot >= inventory.size() || inventory.role(slot) != SlotRole.INPUT || item.isEmpty()) {
            return;
        }
        ItemStack lock = item.copy();
        lock.setCount(1);
        inventory.setLock(slot, lock);
        flushDirty();
    }

    /** Locks tank {@code tank} to {@code fluid} (JEI drop, I22). */
    public void lockTankTo(int tank, FluidAmount fluid) {
        if (tank < 0 || tank >= tanks.size() || fluid.isEmpty()) {
            return;
        }
        tanks.setLock(tank, fluid);
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
        refreshAir();
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
