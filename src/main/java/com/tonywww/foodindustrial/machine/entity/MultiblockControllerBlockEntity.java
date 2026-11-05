package com.tonywww.foodindustrial.machine.entity;

import com.tonywww.foodindustrial.core.storage.EnergyStore;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.ItemSlots;
import com.tonywww.foodindustrial.core.storage.ItemTransfer;
import com.tonywww.foodindustrial.machine.MultiblockRegistry;
import com.tonywww.foodindustrial.machine.block.PortBlock;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
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
 * whenever a member block around it changes, registers the ports found, and processes recipes like a
 * single-block machine while formed. The controller block exposes no storage; the ports do.
 */
public class MultiblockControllerBlockEntity extends ProcessingMachineBlockEntity {
    private boolean formed;
    private boolean validate = true;
    private List<BlockPos> wrong = List.of();
    private List<String> wrongExpected = List.of();
    private List<BlockPos> ports = List.of();
    @Nullable
    private BlockPos[] bounds;
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

    /** Whether {@code pos} lies in the structure's box. */
    public boolean covers(BlockPos pos) {
        BlockPos[] box = bounds();
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
        structureChanged();
    }

    @Override
    public void rotate() {
        super.rotate();
        bounds = null;
        structureChanged();
    }

    /** Expected block at each wrong position, in the order of {@link #wrongBlocks()} (structure page). */
    public List<String> wrongExpected() {
        return wrongExpected;
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
        StructurePattern.Result result = structure().check(level, worldPosition, facing(), state -> state.getBlock() instanceof PortBlock);
        wrong = List.copyOf(result.wrong());
        wrongExpected = List.copyOf(result.expected());
        boolean was = formed;
        formed = result.formed();
        attachPorts(formed ? result.ports() : List.of());
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
                    if (covers(pos.relative(direction))) {
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
                    if (covers(pos.relative(direction))) {
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
