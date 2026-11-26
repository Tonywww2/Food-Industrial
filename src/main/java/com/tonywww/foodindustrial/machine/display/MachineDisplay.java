package com.tonywww.foodindustrial.machine.display;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.component.MachineInventory;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.component.TankSet;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformConfig;
import com.tonywww.foodindustrial.platform.network.PlatformNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * Immersive display (machines/04 §9, I37): what a client needs to draw a machine's work in the world.
 * A client with the display on asks, a few times a second, about the machines it is drawing; the
 * server answers with each machine's progress and, when its contents changed since the client last
 * heard, the contents. Machines do nothing for it in their own ticks, and nothing is sent while no
 * client asks.
 */
public final class MachineDisplay {
    /** Machines one request may name; the rest are dropped. */
    public static final int MAX_REQUEST = 48;
    /** Ints per machine in a request: x, y, z, the hash the client holds, and whether it holds one. */
    public static final int REQUEST_STRIDE = 5;
    /** Requests of one player closer together than this many ticks are dropped. */
    private static final int MIN_INTERVAL = 4;
    /** A player hears about machines within this many blocks. */
    private static final double MAX_DISTANCE = 48;

    /** Flag: no machine is there (any more). */
    public static final int GONE = 1;
    /** Flag: the machine is working. */
    public static final int RUNNING = 1 << 1;
    /** Flag: the update carries the contents. */
    public static final int CONTENTS = 1 << 2;
    /** The status ordinal sits above the flags. */
    public static final int STATUS_SHIFT = 8;

    private static final String SECTION = "display";
    /** Client options ({@code config/foodindustrial-client.toml}). */
    public static final BooleanSupplier DEFAULT_ON = PlatformConfig.clientBoolean(SECTION, "immersive_display",
        "Start each session with the immersive display on: machines show their inputs, progress and products in the world. "
            + "The button in every machine window switches it for the session", false);
    public static final IntSupplier RANGE = PlatformConfig.clientInt(SECTION, "immersive_range",
        "Blocks from the camera within which machines show their panel", 16, 4, 32);
    public static final IntSupplier LIMIT = PlatformConfig.clientInt(SECTION, "immersive_limit",
        "Most panels shown at once; the nearest machines win", 24, 1, MAX_REQUEST);

    /** What a machine holds, as its panel shows it: inputs, then products; the stacks are copies. */
    public record Contents(ItemStack[] inItems, FluidAmount[] inFluids, ItemStack[] outItems, FluidAmount[] outFluids) {
    }

    /**
     * One machine in a reply.
     *
     * @param progress ticks done at speed × 1, of {@code total}; both 0 without a recipe
     * @param rate     progress per tick in thousandths
     * @param contents the contents when they changed since the client last heard, else {@code null}
     */
    public record Update(int x, int y, int z, int hash, int flags, int progress, int total, int rate, @Nullable Contents contents) {
        public boolean gone() {
            return (flags & GONE) != 0;
        }

        public boolean running() {
            return (flags & RUNNING) != 0;
        }

        public int status() {
            return flags >>> STATUS_SHIFT;
        }
    }

    /** Client → server: the machines the client is drawing, {@link #REQUEST_STRIDE} ints each. */
    public record Request(int[] machines) implements PlatformNetwork.Payload {
        @Override
        public void write(DataWriter out) {
            out.putIntArray("M", machines);
        }

        static Request read(DataReader in) {
            return new Request(in.getIntArray("M"));
        }
    }

    /** Server → client: one update per machine asked about. */
    public record Reply(List<Update> updates) implements PlatformNetwork.Payload {
        @Override
        public void write(DataWriter out) {
            DataWriter.ListWriter list = out.list("M");
            for (Update update : updates) {
                DataWriter entry = list.add();
                entry.putIntArray("H", new int[] {update.x, update.y, update.z, update.hash, update.flags, update.progress, update.total, update.rate});
                Contents contents = update.contents;
                if (contents != null) {
                    items(entry.list("A"), contents.inItems);
                    fluids(entry.list("B"), contents.inFluids);
                    items(entry.list("C"), contents.outItems);
                    fluids(entry.list("D"), contents.outFluids);
                }
            }
        }

        static Reply read(DataReader in) {
            List<Update> updates = new ArrayList<>();
            for (DataReader entry : in.list("M")) {
                int[] head = entry.getIntArray("H");
                if (head.length < 8) {
                    continue;
                }
                Contents contents = (head[4] & CONTENTS) == 0 ? null
                    : new Contents(items(entry.list("A")), fluids(entry.list("B")), items(entry.list("C")), fluids(entry.list("D")));
                updates.add(new Update(head[0], head[1], head[2], head[3], head[4], head[5], head[6], head[7], contents));
            }
            return new Reply(updates);
        }

        private static void items(DataWriter.ListWriter list, ItemStack[] stacks) {
            for (ItemStack stack : stacks) {
                list.add().putItem("I", stack);
            }
        }

        private static void fluids(DataWriter.ListWriter list, FluidAmount[] amounts) {
            for (FluidAmount amount : amounts) {
                list.add().putFluid("F", amount);
            }
        }

        private static ItemStack[] items(List<DataReader> list) {
            ItemStack[] stacks = new ItemStack[list.size()];
            for (int i = 0; i < stacks.length; i++) {
                stacks[i] = list.get(i).getItem("I");
            }
            return stacks;
        }

        private static FluidAmount[] fluids(List<DataReader> list) {
            FluidAmount[] amounts = new FluidAmount[list.size()];
            for (int i = 0; i < amounts.length; i++) {
                amounts[i] = list.get(i).getFluid("F");
            }
            return amounts;
        }
    }

    private static final PlatformNetwork.Channel<Request> REQUEST =
        PlatformNetwork.toServer("machine_display_request", Request::read, MachineDisplay::onRequest);
    private static final PlatformNetwork.Channel<Reply> REPLY =
        PlatformNetwork.toClient("machine_display", Reply::read,
            reply -> com.tonywww.foodindustrial.machine.display.client.ImmersiveDisplay.receive(reply));

    /** Game time of each player's last accepted request. */
    private static final Map<ServerPlayer, long[]> LAST_REQUEST = new WeakHashMap<>();

    private MachineDisplay() {
    }

    /** Declares the options and channels and, on the client, the panels. Called during mod construction. */
    public static void bootstrap() {
        if (Platform.isPhysicalClient()) {
            com.tonywww.foodindustrial.machine.display.client.ImmersiveDisplay.bootstrap();
        }
    }

    /** Client: asks about {@code machines} ({@link #REQUEST_STRIDE} ints each). */
    public static void request(int[] machines) {
        PlatformNetwork.sendToServer(REQUEST, new Request(machines));
    }

    private static void onRequest(Request request, ServerPlayer sender) {
        Level level = sender.level();
        long now = level.getGameTime();
        long[] last = LAST_REQUEST.computeIfAbsent(sender, player -> new long[] {Long.MIN_VALUE / 2});
        if (now - last[0] < MIN_INTERVAL && now >= last[0]) {
            return;
        }
        last[0] = now;
        int[] machines = request.machines();
        int count = Math.min(MAX_REQUEST, machines.length / REQUEST_STRIDE);
        List<Update> updates = new ArrayList<>(count);
        for (int n = 0; n < count; n++) {
            int i = n * REQUEST_STRIDE;
            int x = machines[i];
            int y = machines[i + 1];
            int z = machines[i + 2];
            if (sender.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) > MAX_DISTANCE * MAX_DISTANCE) {
                continue;
            }
            BlockPos pos = new BlockPos(x, y, z);
            if (!level.isLoaded(pos)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof MachineBlockEntity machine) {
                updates.add(capture(machine, machines[i + 3], machines[i + 4] != 0));
            } else {
                updates.add(new Update(x, y, z, 0, GONE, 0, 0, 0, null));
            }
        }
        if (!updates.isEmpty()) {
            PlatformNetwork.sendToPlayer(sender, REPLY, new Reply(updates));
        }
    }

    /** The update for a machine; with the contents unless the client holds {@code known} and it still fits. */
    static Update capture(MachineBlockEntity machine, int known, boolean hasKnown) {
        BlockPos pos = machine.getBlockPos();
        int hash = hash(machine);
        MachineStatus status = machine.status();
        int flags = status.ordinal() << STATUS_SHIFT | (status.running() ? RUNNING : 0);
        int progress = 0;
        int total = 0;
        int rate = 0;
        if (machine instanceof ProcessingMachineBlockEntity processing) {
            ProcessingMachineBlockEntity.Job job = processing.job();
            if (job != null) {
                progress = job.progress();
                total = job.total();
                rate = machine.definition().profile().speed() * machine.speedPermille();
            }
        }
        Contents contents = null;
        if (!hasKnown || known != hash) {
            contents = contents(machine);
            flags |= CONTENTS;
        }
        return new Update(pos.getX(), pos.getY(), pos.getZ(), hash, flags, progress, total, rate, contents);
    }

    /**
     * A number that changes when what the panel shows of the contents changes: the status and every
     * input and product. Only the server computes it, so identity hashes of items and fluids do.
     */
    static int hash(MachineBlockEntity machine) {
        int hash = machine.status().ordinal() + 1;
        MachineInventory inventory = machine.inventory();
        hash = slots(hash, inventory, SlotRole.INPUT);
        hash = slots(hash, inventory, SlotRole.OUTPUT);
        TankSet tanks = machine.tanks();
        for (int tank = 0; tank < tanks.size(); tank++) {
            FluidAmount amount = tanks.get(tank);
            hash = 31 * hash + (amount.isEmpty() ? 0 : amount.fluid().hashCode() * 31 + amount.amount());
        }
        return hash;
    }

    private static int slots(int hash, MachineInventory inventory, SlotRole role) {
        for (int i = 0; i < inventory.count(role); i++) {
            ItemStack stack = inventory.get(role, i);
            hash = 31 * hash + (stack.isEmpty() ? 0 : stack.getItem().hashCode() * 31 + stack.getCount());
        }
        return hash;
    }

    private static Contents contents(MachineBlockEntity machine) {
        MachineInventory inventory = machine.inventory();
        TankSet tanks = machine.tanks();
        return new Contents(stacks(inventory, SlotRole.INPUT), amounts(tanks, 0, tanks.inputs()),
            stacks(inventory, SlotRole.OUTPUT), amounts(tanks, tanks.inputs(), tanks.size()));
    }

    /** Copies of the stacks in the slots of {@code role}, empty slots left out. */
    private static ItemStack[] stacks(MachineInventory inventory, SlotRole role) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inventory.count(role); i++) {
            ItemStack stack = inventory.get(role, i);
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }
        return stacks.toArray(new ItemStack[0]);
    }

    private static FluidAmount[] amounts(TankSet tanks, int from, int to) {
        List<FluidAmount> amounts = new ArrayList<>();
        for (int tank = from; tank < to; tank++) {
            FluidAmount amount = tanks.get(tank);
            if (!amount.isEmpty()) {
                amounts.add(amount);
            }
        }
        return amounts.toArray(new FluidAmount[0]);
    }
}
