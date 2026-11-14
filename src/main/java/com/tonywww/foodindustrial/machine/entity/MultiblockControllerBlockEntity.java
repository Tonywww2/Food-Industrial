package com.tonywww.foodindustrial.machine.entity;

import com.tonywww.foodindustrial.core.storage.EnergyStore;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.ItemSlots;
import com.tonywww.foodindustrial.core.storage.ItemTransfer;
import com.tonywww.foodindustrial.machine.MultiblockRegistry;
import com.tonywww.foodindustrial.machine.block.AirIntakeBlock;
import com.tonywww.foodindustrial.machine.block.PortBlock;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.StructurePattern;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Controller of a multiblock (machines/02 §4, §6.3 rule 12): validates its structure when it loads and
 * whenever a member block around it changes (a port's direction too), registers the ports found, and
 * processes recipes like a single-block machine while formed. The controller block exposes no storage
 * and its window shows no slots; the ports do (2026-11-10). It forms only with a port set to each
 * direction its layout needs ({@link #portNeeds}).
 */
public class MultiblockControllerBlockEntity extends ProcessingMachineBlockEntity {
    private boolean formed;
    private boolean validate = true;
    private List<BlockPos> wrong = List.of();
    private List<String> wrongExpected = List.of();
    private List<BlockPos> ports = List.of();
    /** Port directions the structure lacks ({@link #portNeeds}), at the last check. */
    private List<String> missingPorts = List.of();
    /** Layers of the formed structure (variable-height multiblocks). */
    private int layers;
    /** Air intakes in the structure (machines/03 §1). */
    private List<BlockPos> intakes = List.of();
    @Nullable
    private BlockPos[] bounds;
    /** Box of the formed shape (smaller than {@link #bounds} for a short tower). */
    @Nullable
    private BlockPos[] formedBounds;
    private final Map<SideConfig.Mode, ItemSlots> portItems = new EnumMap<>(SideConfig.Mode.class);
    private final Map<SideConfig.Mode, FluidTanks> portFluids = new EnumMap<>(SideConfig.Mode.class);

    public MultiblockControllerBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    public boolean formed() {
        return formed;
    }

    /** Positions holding a wrong block at the last check (structure page, I10). */
    public List<BlockPos> wrongBlocks() {
        return wrong;
    }

    public List<BlockPos> ports() {
        return ports;
    }

    /** Whether {@code pos} lies in the box of the largest shape (member changes there re-validate). */
    public boolean covers(BlockPos pos) {
        return within(bounds(), pos);
    }

    /** Whether {@code pos} lies in the formed shape's box (ports push and intakes breathe outside it). */
    private boolean inside(BlockPos pos) {
        if (formedBounds == null) {
            formedBounds = structure().bounds(worldPosition, facing(), layers);
        }
        return within(formedBounds, pos);
    }

    private static boolean within(BlockPos[] box, BlockPos pos) {
        return pos.getX() >= box[0].getX() && pos.getX() <= box[1].getX() && pos.getY() >= box[0].getY() && pos.getY() <= box[1].getY()
            && pos.getZ() >= box[0].getZ() && pos.getZ() <= box[1].getZ();
    }

    private BlockPos[] bounds() {
        if (bounds == null) {
            bounds = structure().bounds(worldPosition, facing());
        }
        return bounds;
    }

    private StructurePattern structure() {
        return definition.structure();
    }

    /** A member block changed: validate at the next batch. */
    public void structureChanged() {
        validate = true;
        wake();
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (level != null && !level.isClientSide()) {
            MultiblockRegistry.add(this);
            structureChanged();
        }
    }

    @Override
    protected void onRemoved(boolean unloaded) {
        if (level != null && !level.isClientSide()) {
            MultiblockRegistry.remove(this);
            if (!unloaded) {
                attachPorts(List.of());
            }
        }
    }

    @Override
    public void onPlaced() {
        bounds = null;
        formedBounds = null;
        structureChanged();
    }

    @Override
    public void rotate() {
        super.rotate();
        bounds = null;
        formedBounds = null;
        structureChanged();
    }

    /** Expected block at each wrong position, in the order of {@link #wrongBlocks()} (structure page). */
    public List<String> wrongExpected() {
        return wrongExpected;
    }

    /** Layers of the structure at the last check. */
    public int layers() {
        return layers;
    }

    /** Speed of a tall structure (M19, 2026-11-11). */
    @Override
    protected int structureSpeedPermille() {
        return formed ? definition.heightSpeedPermille(layers) : 1000;
    }

    private List<BlockPos> findIntakes() {
        List<BlockPos> found = new ArrayList<>();
        BlockPos[] box = structure().bounds(worldPosition, facing(), layers);
        for (BlockPos pos : BlockPos.betweenClosed(box[0], box[1])) {
            if (level.getBlockState(pos).getBlock() instanceof AirIntakeBlock) {
                found.add(pos.immutable());
            }
        }
        return List.copyOf(found);
    }

    /** An air intake with a side outside the structure facing an air block lets air in. */
    @Override
    protected boolean checkAir() {
        for (BlockPos pos : intakes) {
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (!inside(next) && level.getBlockState(next).isAir()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Port directions the structure lacks at the last check ({@code item_input} …; structure page). */
    public List<String> missingPorts() {
        return missingPorts;
    }

    /**
     * Port directions a multiblock needs to form (2026-11-10): the controller takes no items or fluids
     * itself, so every side its layout has needs a port set to that direction — an item input port for
     * item inputs, catalysts or fuel, an item output port for item products, fluid input and output
     * ports for its tanks, an energy input port for FE.
     */
    public static List<String> portNeeds(MachineDefinition definition) {
        IoLayout io = definition.layout();
        List<String> needs = new ArrayList<>(5);
        if (io.itemIn() + io.catalyst() + io.fuel() > 0) {
            needs.add("item_input");
        }
        if (io.itemOut() > 0) {
            needs.add("item_output");
        }
        if (io.fluidIn() > 0) {
            needs.add("fluid_input");
        }
        if (io.fluidOut() > 0) {
            needs.add("fluid_output");
        }
        if (definition.power() == PowerKind.ENERGY) {
            needs.add("energy_input");
        }
        return needs;
    }

    /** The needs no port among {@code found} serves with its direction. */
    private List<String> lackingPorts(List<BlockPos> found) {
        List<String> lacking = new ArrayList<>(portNeeds(definition));
        for (BlockPos pos : found) {
            if (level.getBlockEntity(pos) instanceof PortBlockEntity port && port.mode() != SideConfig.Mode.OFF) {
                String direction = port.mode() == SideConfig.Mode.OUTPUT ? "output" : "input";
                lacking.remove(port.kind().name().toLowerCase(java.util.Locale.ROOT) + "_" + direction);
            }
        }
        return List.copyOf(lacking);
    }

    @Override
    protected MachineStatus tickBatch(int delta, long gameTime) {
        if (validate) {
            validate = false;
            validateNow();
        }
        if (!formed) {
            return MachineStatus.UNFORMED;
        }
        return super.tickBatch(delta, gameTime);
    }

    private void validateNow() {
        bounds = null;
        formedBounds = null;
        StructurePattern.Result result = structure().check(level, worldPosition, facing(), state -> state.getBlock() instanceof PortBlock);
        wrong = List.copyOf(result.wrong());
        wrongExpected = List.copyOf(result.expected());
        List<String> lacking = lackingPorts(result.ports());
        if (!lacking.equals(missingPorts)) {
            missingPorts = lacking;
            markDirty();
        }
        boolean was = formed;
        formed = result.formed() && lacking.isEmpty();
        if (result.layers() != layers) {
            layers = result.layers();
            markDirty();
            // what a recipe may need of the height (trays, tube segments, vessel layers) changed
            inputsChanged();
        }
        attachPorts(formed ? result.ports() : List.of());
        intakes = formed && definition.airIntake() ? findIntakes() : List.of();
        refreshAir();
        if (was != formed) {
            markDirty();
        }
    }

    private void attachPorts(List<BlockPos> found) {
        for (BlockPos pos : ports) {
            if (!found.contains(pos) && level.getBlockEntity(pos) instanceof PortBlockEntity port && port.controller() == this) {
                port.attach(null);
            }
        }
        List<BlockPos> attached = new ArrayList<>();
        for (BlockPos pos : found) {
            if (level.getBlockEntity(pos) instanceof PortBlockEntity port) {
                port.attach(this);
                attached.add(pos);
            }
        }
        ports = List.copyOf(attached);
    }

    // ---- storage through ports ----

    @Override
    public @Nullable ItemSlots itemsFor(@Nullable Direction side) {
        return null;
    }

    @Override
    public @Nullable FluidTanks fluidsFor(@Nullable Direction side) {
        return null;
    }

    @Override
    public @Nullable EnergyStore energyFor(@Nullable Direction side) {
        return null;
    }

    /** Item slots as an item port in {@code mode} shows them. */
    public ItemSlots portItems(SideConfig.Mode mode) {
        return portItems.computeIfAbsent(mode, key -> new ItemView(key, SideConfig.OutputScope.ALL));
    }

    /** Tanks as a fluid port in {@code mode} shows them. */
    public FluidTanks portFluids(SideConfig.Mode mode) {
        return portFluids.computeIfAbsent(mode, key -> new FluidView(key, SideConfig.ALL_TANKS, SideConfig.ALL_TANKS));
    }

    @Override
    protected boolean pushItems() {
        for (BlockPos pos : ports) {
            if (level.getBlockEntity(pos) instanceof PortBlockEntity port && port.kind() == PortBlockEntity.Kind.ITEM
                && port.mode().outputs() && port.autoOutput()) {
                for (Direction direction : Direction.values()) {
                    if (inside(pos.relative(direction))) {
                        continue;
                    }
                    ItemTransfer target = port.neighbors().items(direction);
                    if (target == null) {
                        continue;
                    }
                    for (int i = 0; i < inventory.count(SlotRole.OUTPUT); i++) {
                        int slot = inventory.slot(SlotRole.OUTPUT, i);
                        ItemStack stack = inventory.get(slot);
                        if (!stack.isEmpty()) {
                            ItemStack rest = target.insert(stack, false);
                            if (rest.getCount() != stack.getCount()) {
                                inventory.set(slot, rest);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean pushFluids() {
        for (BlockPos pos : ports) {
            if (level.getBlockEntity(pos) instanceof PortBlockEntity port && port.kind() == PortBlockEntity.Kind.FLUID
                && port.mode().outputs() && port.autoOutput()) {
                for (Direction direction : Direction.values()) {
                    if (inside(pos.relative(direction))) {
                        continue;
                    }
                    FluidTransfer target = port.neighbors().fluids(direction);
                    if (target == null) {
                        continue;
                    }
                    for (int tank = tanks.inputs(); tank < tanks.size(); tank++) {
                        FluidAmount fluid = tanks.get(tank);
                        if (!fluid.isEmpty()) {
                            int filled = target.fill(fluid, false);
                            if (filled > 0) {
                                tanks.drain(tank, filled);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean autoOutputsItems() {
        return anyAutoPort(PortBlockEntity.Kind.ITEM);
    }

    @Override
    protected boolean autoOutputsFluids() {
        return anyAutoPort(PortBlockEntity.Kind.FLUID);
    }

    private boolean anyAutoPort(PortBlockEntity.Kind kind) {
        for (BlockPos pos : ports) {
            if (level.getBlockEntity(pos) instanceof PortBlockEntity port && port.kind() == kind && port.autoOutput() && port.mode().outputs()) {
                return true;
            }
        }
        return false;
    }

    /** Multiblocks have no automatic input (ports only switch automatic output, machines/04 §5). */
    @Override
    protected boolean pullItems() {
        return false;
    }

    @Override
    protected boolean pullFluids() {
        return false;
    }
}
