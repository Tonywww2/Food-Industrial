package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.machine.entity.PortBlockEntity;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.machine.processing.RecipeProcessor;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * What a machine screen shows (machines/04 §6), captured on the server and sent while the menu is open.
 * Values are grouped; only groups that changed since the last packet are sent, and the client merges
 * them. A captured view is never changed after it is sent.
 */
public final class MachineView {
    public static final int STATUS = 1;
    public static final int POWER = 1 << 1;
    public static final int PROGRESS = 1 << 2;
    public static final int TANKS = 1 << 3;
    public static final int LOCKS = 1 << 4;
    public static final int CATALYST = 1 << 5;
    public static final int CONFIG = 1 << 6;
    public static final int UPGRADES = 1 << 7;
    public static final int STRUCTURE = 1 << 8;
    public static final int PORT = 1 << 9;
    private static final int GROUPS = 10;

    // STATUS
    public MachineStatus status = MachineStatus.WAITING;
    /** Missing condition keys joined by commas, or empty. */
    public String missing = "";
    // POWER
    public long energy;
    public long energyCapacity;
    public int burnLeft;
    public int burnTotal;
    // PROGRESS
    public int progress;
    public int total;
    /** Progress per tick in thousandths (device speed × upgrades). */
    public int rate;
    public long energyPerTick;
    public ItemStack productItem = ItemStack.EMPTY;
    public FluidAmount productFluid = FluidAmount.EMPTY;
    public int yieldPermille = 1000;
    // TANKS
    public int tankCapacity;
    public FluidAmount[] tanks = new FluidAmount[0];
    public FluidAmount[] tankLocks = new FluidAmount[0];
    /** What each tank takes: 0 liquids, 1 gases, 2 both. */
    public int[] tankPhases = new int[0];
    /** Venting switch of each tank ({@code VentMode} ordinal). */
    public int[] tankVents = new int[0];
    // LOCKS
    public ItemStack[] slotLocks = new ItemStack[0];
    // CATALYST
    /** Remaining uses per catalyst slot before one item is used up; 0 when unused. */
    public int[] catalystLeft = new int[0];
    // CONFIG
    public int[] sideItems = new int[6];
    public int[] sideFluids = new int[6];
    public int[] itemScopes = new int[6];
    public int[] fluidOutTanks = new int[6];
    public int[] fluidInTanks = new int[6];
    /** Bits: 1 auto input items, 2 auto input fluids, 4 auto output items, 8 auto output fluids. */
    public int auto;
    public int redstone;
    public int mode;
    public boolean powered;
    // UPGRADES
    public int speedPermille = 1000;
    public int energyPermille = 1000;
    public int parallel = 1;
    public List<String> capabilities = List.of();
    public List<String> moduleCapabilities = List.of();
    // STRUCTURE
    public boolean formed;
    public List<BlockPos> wrong = List.of();
    public List<String> expected = List.of();
    public List<BlockPos> ports = List.of();
    public int[] portKinds = new int[0];
    public int[] portModes = new int[0];
    // PORT
    public boolean portAttached;
    public int portMode;
    public boolean portAuto;

    /** Captures the groups in {@code groups} from a machine (server side). */
    public static MachineView of(MachineBlockEntity machine, int groups) {
        MachineView view = new MachineView();
        if ((groups & STATUS) != 0) {
            view.status = machine.status();
            if (machine instanceof ProcessingMachineBlockEntity processing && processing.processor().missing() != null) {
                view.missing = processing.processor().missing();
            }
        }
        if ((groups & POWER) != 0) {
            if (machine.energy() != null) {
                view.energy = machine.energy().getEnergy();
                view.energyCapacity = machine.energy().getCapacity();
            }
            if (machine.heat() != null) {
                view.burnLeft = machine.heat().burnLeft();
                view.burnTotal = machine.heat().burnTotal();
            }
        }
        if ((groups & PROGRESS) != 0 && machine instanceof ProcessingMachineBlockEntity processing) {
            RecipeProcessor processor = processing.processor();
            RecipeIndex.Entry entry = processor.current();
            DeviceProfile profile = machine.definition().profile();
            view.rate = profile.speed() * machine.upgrades().speedPermille();
            if (entry != null) {
                ProcessRecipe recipe = entry.recipe();
                view.progress = processor.progressTicks();
                view.total = processor.totalTicks();
                view.energyPerTick = profile.energyPerTick(recipe) * machine.upgrades().energyPermille() / 1000L;
                view.productItem = processor.plannedItem();
                view.productFluid = processor.plannedFluid();
                view.yieldPermille = recipe.type().yieldScalable() ? profile.yieldPermille() : 1000;
            }
        }
        if ((groups & TANKS) != 0) {
            view.tankCapacity = machine.tanks().capacity();
            int size = machine.tanks().size();
            view.tanks = new FluidAmount[size];
            view.tankLocks = new FluidAmount[size];
            view.tankPhases = new int[size];
            view.tankVents = new int[size];
            for (int i = 0; i < size; i++) {
                view.tankVents[i] = machine.tanks().vent(i).ordinal();
                view.tanks[i] = machine.tanks().get(i);
                view.tankLocks[i] = machine.tanks().lock(i);
                view.tankPhases[i] = machine.tanks().phase(i).ordinal();
            }
        }
        if ((groups & LOCKS) != 0) {
            int size = machine.inventory().size();
            view.slotLocks = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                view.slotLocks[i] = machine.inventory().lock(i);
            }
        }
        if ((groups & CATALYST) != 0) {
            int count = machine.inventory().count(SlotRole.CATALYST);
            view.catalystLeft = new int[count];
            if (machine instanceof ProcessingMachineBlockEntity processing) {
                for (int i = 0; i < count; i++) {
                    int runs = processing.processor().catalystRuns(i);
                    view.catalystLeft[i] = runs > 0 ? runs - machine.catalysts().used(i) : 0;
                }
            }
        }
        if ((groups & CONFIG) != 0) {
            SideConfig sides = machine.sides();
            for (SideConfig.Face face : SideConfig.Face.values()) {
                int i = face.ordinal();
                view.sideItems[i] = sides.items(face).ordinal();
                view.sideFluids[i] = sides.fluids(face).ordinal();
                view.itemScopes[i] = sides.itemScope(face).ordinal();
                view.fluidOutTanks[i] = sides.fluidOutputTank(face);
                view.fluidInTanks[i] = sides.fluidInputTank(face);
            }
            view.auto = (sides.autoInputItems() ? 1 : 0) | (sides.autoInputFluids() ? 2 : 0)
                | (sides.autoOutputItems() ? 4 : 0) | (sides.autoOutputFluids() ? 8 : 0);
            view.redstone = machine.redstone().ordinal();
            view.mode = machine.modeIndex();
            view.powered = machine.isPowered();
        }
        if ((groups & UPGRADES) != 0) {
            view.speedPermille = machine.upgrades().speedPermille();
            view.energyPermille = machine.upgrades().energyPermille();
            view.parallel = machine.upgrades().parallel();
            view.capabilities = List.copyOf(machine.capabilities().keys());
            view.moduleCapabilities = List.copyOf(machine.upgrades().moduleCapabilities().keys());
        }
        if ((groups & STRUCTURE) != 0 && machine instanceof MultiblockControllerBlockEntity controller) {
            view.formed = controller.formed();
            view.wrong = controller.wrongBlocks();
            view.expected = controller.wrongExpected();
            List<BlockPos> ports = new ArrayList<>();
            List<Integer> kinds = new ArrayList<>();
            List<Integer> modes = new ArrayList<>();
            for (BlockPos pos : controller.ports()) {
                if (machine.getLevel().getBlockEntity(pos) instanceof PortBlockEntity port) {
                    ports.add(pos);
                    kinds.add(port.kind().ordinal());
                    modes.add(port.mode().ordinal() | (port.autoOutput() ? 4 : 0));
                }
            }
            view.ports = List.copyOf(ports);
            view.portKinds = kinds.stream().mapToInt(Integer::intValue).toArray();
            view.portModes = modes.stream().mapToInt(Integer::intValue).toArray();
        }
        return view;
    }

    /** Captures a port's settings and, when attached, its controller's groups in {@code groups}. */
    public static MachineView of(PortBlockEntity port, int groups) {
        MultiblockControllerBlockEntity controller = port.controller();
        MachineView view = controller != null && controller.formed() ? of(controller, groups & ~PORT) : new MachineView();
        if ((groups & PORT) != 0) {
            view.portAttached = controller != null && controller.formed();
            view.portMode = port.mode().ordinal();
            view.portAuto = port.autoOutput();
        }
        return view;
    }

    /** Groups in which {@code this} differs from {@code other}. */
    public int diff(MachineView other, int groups) {
        int changed = 0;
        for (int bit = 0; bit < GROUPS; bit++) {
            int group = 1 << bit;
            if ((groups & group) != 0 && !same(other, group)) {
                changed |= group;
            }
        }
        return changed;
    }

    private boolean same(MachineView o, int group) {
        return switch (group) {
            case STATUS -> status == o.status && missing.equals(o.missing);
            case POWER -> energy == o.energy && energyCapacity == o.energyCapacity && burnLeft == o.burnLeft && burnTotal == o.burnTotal;
            case PROGRESS -> progress == o.progress && total == o.total && rate == o.rate && energyPerTick == o.energyPerTick
                && sameStack(productItem, o.productItem) && productFluid.equals(o.productFluid) && yieldPermille == o.yieldPermille;
            case TANKS -> tankCapacity == o.tankCapacity && Arrays.equals(tanks, o.tanks) && Arrays.equals(tankLocks, o.tankLocks)
                && Arrays.equals(tankPhases, o.tankPhases) && Arrays.equals(tankVents, o.tankVents);
            case LOCKS -> sameStacks(slotLocks, o.slotLocks);
            case CATALYST -> Arrays.equals(catalystLeft, o.catalystLeft);
            case CONFIG -> Arrays.equals(sideItems, o.sideItems) && Arrays.equals(sideFluids, o.sideFluids)
                && Arrays.equals(itemScopes, o.itemScopes) && Arrays.equals(fluidOutTanks, o.fluidOutTanks)
                && Arrays.equals(fluidInTanks, o.fluidInTanks) && auto == o.auto && redstone == o.redstone && mode == o.mode
                && powered == o.powered;
            case UPGRADES -> speedPermille == o.speedPermille && energyPermille == o.energyPermille && parallel == o.parallel
                && capabilities.equals(o.capabilities) && moduleCapabilities.equals(o.moduleCapabilities);
            case STRUCTURE -> formed == o.formed && wrong.equals(o.wrong) && expected.equals(o.expected) && ports.equals(o.ports)
                && Arrays.equals(portKinds, o.portKinds) && Arrays.equals(portModes, o.portModes);
            case PORT -> portAttached == o.portAttached && portMode == o.portMode && portAuto == o.portAuto;
            default -> true;
        };
    }

    private static boolean sameStack(ItemStack a, ItemStack b) {
        return a.isEmpty() ? b.isEmpty() : !b.isEmpty() && a.getCount() == b.getCount() && PlatformItems.sameItemSameData(a, b);
    }

    private static boolean sameStacks(ItemStack[] a, ItemStack[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!sameStack(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    /** Takes the groups in {@code groups} from {@code other} (client side). */
    public void merge(MachineView o, int groups) {
        if ((groups & STATUS) != 0) {
            status = o.status;
            missing = o.missing;
        }
        if ((groups & POWER) != 0) {
            energy = o.energy;
            energyCapacity = o.energyCapacity;
            burnLeft = o.burnLeft;
            burnTotal = o.burnTotal;
        }
        if ((groups & PROGRESS) != 0) {
            progress = o.progress;
            total = o.total;
            rate = o.rate;
            energyPerTick = o.energyPerTick;
            productItem = o.productItem;
            productFluid = o.productFluid;
            yieldPermille = o.yieldPermille;
        }
        if ((groups & TANKS) != 0) {
            tankCapacity = o.tankCapacity;
            tanks = o.tanks;
            tankLocks = o.tankLocks;
            tankPhases = o.tankPhases;
            tankVents = o.tankVents;
        }
        if ((groups & LOCKS) != 0) {
            slotLocks = o.slotLocks;
        }
        if ((groups & CATALYST) != 0) {
            catalystLeft = o.catalystLeft;
        }
        if ((groups & CONFIG) != 0) {
            sideItems = o.sideItems;
            sideFluids = o.sideFluids;
            itemScopes = o.itemScopes;
            fluidOutTanks = o.fluidOutTanks;
            fluidInTanks = o.fluidInTanks;
            auto = o.auto;
            redstone = o.redstone;
            mode = o.mode;
            powered = o.powered;
        }
        if ((groups & UPGRADES) != 0) {
            speedPermille = o.speedPermille;
            energyPermille = o.energyPermille;
            parallel = o.parallel;
            capabilities = o.capabilities;
            moduleCapabilities = o.moduleCapabilities;
        }
        if ((groups & STRUCTURE) != 0) {
            formed = o.formed;
            wrong = o.wrong;
            expected = o.expected;
            ports = o.ports;
            portKinds = o.portKinds;
            portModes = o.portModes;
        }
        if ((groups & PORT) != 0) {
            portAttached = o.portAttached;
            portMode = o.portMode;
            portAuto = o.portAuto;
        }
    }

    // ---- wire format ----

    /** Writes the groups in {@code groups}; the reader must be given the same groups. */
    public void write(DataWriter out, int groups) {
        if ((groups & STATUS) != 0) {
            out.putByte("Status", (byte) status.ordinal());
            out.putString("Missing", missing);
        }
        if ((groups & POWER) != 0) {
            out.putLong("Energy", energy);
            out.putLong("EnergyCap", energyCapacity);
            out.putInt("BurnLeft", burnLeft);
            out.putInt("BurnTotal", burnTotal);
        }
        if ((groups & PROGRESS) != 0) {
            out.putInt("Progress", progress);
            out.putInt("Total", total);
            out.putInt("Rate", rate);
            out.putLong("EnergyPerTick", energyPerTick);
            out.putItem("Product", productItem);
            out.putFluid("ProductFluid", productFluid);
            out.putInt("Yield", yieldPermille);
        }
        if ((groups & TANKS) != 0) {
            out.putInt("TankCap", tankCapacity);
            out.putInt("Tanks", tanks.length);
            for (int i = 0; i < tanks.length; i++) {
                out.putFluid("Tank" + i, tanks[i]);
                out.putFluid("TankLock" + i, tankLocks[i]);
            }
            out.putIntArray("TankPhases", tankPhases);
            out.putIntArray("TankVents", tankVents);
        }
        if ((groups & LOCKS) != 0) {
            out.putInt("Slots", slotLocks.length);
            for (int i = 0; i < slotLocks.length; i++) {
                out.putItem("Lock" + i, slotLocks[i]);
            }
        }
        if ((groups & CATALYST) != 0) {
            out.putIntArray("Catalyst", catalystLeft);
        }
        if ((groups & CONFIG) != 0) {
            out.putIntArray("SideItems", sideItems);
            out.putIntArray("SideFluids", sideFluids);
            out.putIntArray("ItemScopes", itemScopes);
            out.putIntArray("FluidOut", fluidOutTanks);
            out.putIntArray("FluidIn", fluidInTanks);
            out.putInt("Auto", auto);
            out.putInt("Redstone", redstone);
            out.putInt("Mode", mode);
            out.putBoolean("Powered", powered);
        }
        if ((groups & UPGRADES) != 0) {
            out.putInt("Speed", speedPermille);
            out.putInt("EnergyUse", energyPermille);
            out.putInt("Parallel", parallel);
            out.putString("Capabilities", String.join(",", capabilities));
            out.putString("ModuleCapabilities", String.join(",", moduleCapabilities));
        }
        if ((groups & STRUCTURE) != 0) {
            out.putBoolean("Formed", formed);
            DataWriter.ListWriter wrongList = out.list("Wrong");
            for (int i = 0; i < wrong.size(); i++) {
                DataWriter entry = wrongList.add();
                entry.putLong("Pos", wrong.get(i).asLong());
                entry.putString("Expected", i < expected.size() ? expected.get(i) : "");
            }
            DataWriter.ListWriter portList = out.list("Ports");
            for (int i = 0; i < ports.size(); i++) {
                DataWriter entry = portList.add();
                entry.putLong("Pos", ports.get(i).asLong());
                entry.putInt("Kind", portKinds[i]);
                entry.putInt("Mode", portModes[i]);
            }
        }
        if ((groups & PORT) != 0) {
            out.putBoolean("PortAttached", portAttached);
            out.putInt("PortMode", portMode);
            out.putBoolean("PortAuto", portAuto);
        }
    }

    /** Reads the groups in {@code groups} written by {@link #write}. */
    public static MachineView read(DataReader in, int groups) {
        MachineView view = new MachineView();
        if ((groups & STATUS) != 0) {
            MachineStatus[] values = MachineStatus.values();
            view.status = values[Math.floorMod(in.getByte("Status", (byte) 0), values.length)];
            view.missing = in.getString("Missing", "");
        }
        if ((groups & POWER) != 0) {
            view.energy = in.getLong("Energy", 0);
            view.energyCapacity = in.getLong("EnergyCap", 0);
            view.burnLeft = in.getInt("BurnLeft", 0);
            view.burnTotal = in.getInt("BurnTotal", 0);
        }
        if ((groups & PROGRESS) != 0) {
            view.progress = in.getInt("Progress", 0);
            view.total = in.getInt("Total", 0);
            view.rate = in.getInt("Rate", 0);
            view.energyPerTick = in.getLong("EnergyPerTick", 0);
            view.productItem = in.getItem("Product");
            view.productFluid = in.getFluid("ProductFluid");
            view.yieldPermille = in.getInt("Yield", 1000);
        }
        if ((groups & TANKS) != 0) {
            view.tankCapacity = in.getInt("TankCap", 0);
            int size = Math.max(0, Math.min(64, in.getInt("Tanks", 0)));
            view.tanks = new FluidAmount[size];
            view.tankLocks = new FluidAmount[size];
            for (int i = 0; i < size; i++) {
                view.tanks[i] = in.getFluid("Tank" + i);
                view.tankLocks[i] = in.getFluid("TankLock" + i);
            }
            view.tankPhases = in.getIntArray("TankPhases");
            view.tankVents = in.getIntArray("TankVents");
        }
        if ((groups & LOCKS) != 0) {
            int size = Math.max(0, Math.min(256, in.getInt("Slots", 0)));
            view.slotLocks = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                view.slotLocks[i] = in.getItem("Lock" + i);
            }
        }
        if ((groups & CATALYST) != 0) {
            view.catalystLeft = in.getIntArray("Catalyst");
        }
        if ((groups & CONFIG) != 0) {
            view.sideItems = six(in.getIntArray("SideItems"), 0);
            view.sideFluids = six(in.getIntArray("SideFluids"), 0);
            view.itemScopes = six(in.getIntArray("ItemScopes"), 0);
            view.fluidOutTanks = six(in.getIntArray("FluidOut"), SideConfig.ALL_TANKS);
            view.fluidInTanks = six(in.getIntArray("FluidIn"), SideConfig.ALL_TANKS);
            view.auto = in.getInt("Auto", 0);
            view.redstone = in.getInt("Redstone", 0);
            view.mode = in.getInt("Mode", 0);
            view.powered = in.getBoolean("Powered", false);
        }
        if ((groups & UPGRADES) != 0) {
            view.speedPermille = in.getInt("Speed", 1000);
            view.energyPermille = in.getInt("EnergyUse", 1000);
            view.parallel = in.getInt("Parallel", 1);
            view.capabilities = split(in.getString("Capabilities", ""));
            view.moduleCapabilities = split(in.getString("ModuleCapabilities", ""));
        }
        if ((groups & STRUCTURE) != 0) {
            view.formed = in.getBoolean("Formed", false);
            List<BlockPos> wrong = new ArrayList<>();
            List<String> expected = new ArrayList<>();
            for (DataReader entry : in.list("Wrong")) {
                wrong.add(BlockPos.of(entry.getLong("Pos", 0)));
                expected.add(entry.getString("Expected", ""));
            }
            view.wrong = List.copyOf(wrong);
            view.expected = List.copyOf(expected);
            List<DataReader> ports = in.list("Ports");
            List<BlockPos> positions = new ArrayList<>();
            view.portKinds = new int[ports.size()];
            view.portModes = new int[ports.size()];
            for (int i = 0; i < ports.size(); i++) {
                positions.add(BlockPos.of(ports.get(i).getLong("Pos", 0)));
                view.portKinds[i] = ports.get(i).getInt("Kind", 0);
                view.portModes[i] = ports.get(i).getInt("Mode", 0);
            }
            view.ports = List.copyOf(positions);
        }
        if ((groups & PORT) != 0) {
            view.portAttached = in.getBoolean("PortAttached", false);
            view.portMode = in.getInt("PortMode", 0);
            view.portAuto = in.getBoolean("PortAuto", false);
        }
        return view;
    }

    private static int[] six(int[] values, int fill) {
        int[] result = new int[6];
        Arrays.fill(result, fill);
        System.arraycopy(values, 0, result, 0, Math.min(6, values.length));
        return result;
    }

    private static List<String> split(String joined) {
        return joined.isEmpty() ? List.of() : List.of(joined.split(","));
    }

    @Override
    public String toString() {
        return "MachineView[" + status + ", " + progress + "/" + total + ", " + Objects.toString(missing) + "]";
    }
}
