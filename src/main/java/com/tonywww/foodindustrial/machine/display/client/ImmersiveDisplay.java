package com.tonywww.foodindustrial.machine.display.client;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.block.MachineBlock;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.display.MachineDisplay;
import com.tonywww.foodindustrial.machine.display.Motion;
import com.tonywww.foodindustrial.machine.display.Motions;
import com.tonywww.foodindustrial.machine.display.PanelLayout;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.platform.client.PlatformClient;
import com.tonywww.foodindustrial.platform.client.PlatformPanels;
import com.tonywww.foodindustrial.platform.client.PlatformPanels.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Client side of the immersive display (machines/04 §9, I37): one switch for the session, the machines
 * the client is drawing with what the server last said about them, and the panel of each for a frame.
 * <p>
 * Cost: while the switch is off the panels' view distance is 0, so no machine is asked for a panel and
 * nothing is sent. While it is on, only machines on screen within the range are tracked, the nearest
 * {@code immersive_limit} of them are asked about every {@link #POLL} ticks in one packet, and a frame
 * fills each panel without allocating.
 */
public final class ImmersiveDisplay {
    private static final String TEXTURE = "textures/gui/immersive_panel.png";
    private static final int TEXTURE_SIZE = 64;
    /** Ticks between two requests. */
    private static final int POLL = 10;
    /** A machine is asked about while it was drawn within this many ticks, and forgotten after {@link #FORGET}. */
    private static final int SEEN = 30;
    private static final int FORGET = 100;
    /** Counts and amounts are written only this close to the camera, in blocks. */
    private static final float TEXT_RANGE = 8;
    /** The panel floats this far above a machine, or stands this far in front of its face, in blocks. */
    private static final float ABOVE = 0.14F;
    private static final float IN_FRONT = 0.64F;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int LAYER_FRAME = 1;
    private static final int LAYER_FILL = 2;
    /** In front of the item icons. */
    private static final int LAYER_OVER = 17;

    private static boolean enabled;
    private static boolean inWorld;
    /** Client ticks while in a world: the clock of everything here. */
    private static long ticks;
    private static final Map<BlockPos, Shown> SHOWN = new HashMap<>();
    private static final List<Shown> NEAREST = new ArrayList<>();
    private static final float[] MOVE = new float[5];

    private ImmersiveDisplay() {
    }

    /** Called during mod construction on the physical client, after the machines are defined. */
    public static void bootstrap() {
        for (MachineDefinition definition : MachineDefinition.all()) {
            PlatformPanels.<MachineBlockEntity>register(definition::blockEntityType, TEXTURE, TEXTURE_SIZE,
                ImmersiveDisplay::viewDistance, ImmersiveDisplay::panel);
        }
        PlatformClient.onClientTick(ImmersiveDisplay::tick);
    }

    /** Whether machines show their work in the world in this session. */
    public static boolean enabled() {
        return enabled;
    }

    /** The button in the machine windows. */
    public static void toggle() {
        enabled = !enabled;
        if (!enabled) {
            SHOWN.clear();
        }
    }

    private static int viewDistance() {
        return enabled ? MachineDisplay.RANGE.getAsInt() : 0;
    }

    private static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (minecraft.level == null || player == null) {
            if (inWorld) {
                // the session ended: the next one starts from the configured default
                inWorld = false;
                enabled = false;
                SHOWN.clear();
            }
            return;
        }
        if (!inWorld) {
            inWorld = true;
            enabled = MachineDisplay.DEFAULT_ON.getAsBoolean();
        }
        if (minecraft.isPaused()) {
            return;
        }
        ticks++;
        if (!enabled || ticks % POLL != 0) {
            return;
        }
        ask(player);
    }

    /** Forgets machines long unseen, picks the nearest of those on screen and asks the server about them. */
    private static void ask(Player player) {
        NEAREST.clear();
        double range = MachineDisplay.RANGE.getAsInt() + 2;
        for (Iterator<Shown> iterator = SHOWN.values().iterator(); iterator.hasNext(); ) {
            Shown shown = iterator.next();
            shown.selected = false;
            if (ticks - shown.lastSeen > FORGET) {
                iterator.remove();
                continue;
            }
            shown.distance = player.distanceToSqr(shown.pos.getX() + 0.5, shown.pos.getY() + 0.5, shown.pos.getZ() + 0.5);
            if (ticks - shown.lastSeen <= SEEN && shown.distance <= range * range) {
                NEAREST.add(shown);
            }
        }
        if (NEAREST.isEmpty()) {
            return;
        }
        NEAREST.sort((a, b) -> Double.compare(a.distance, b.distance));
        int count = Math.min(NEAREST.size(), Math.min(MachineDisplay.LIMIT.getAsInt(), MachineDisplay.MAX_REQUEST));
        int[] machines = new int[count * MachineDisplay.REQUEST_STRIDE];
        for (int n = 0; n < count; n++) {
            Shown shown = NEAREST.get(n);
            shown.selected = true;
            int i = n * MachineDisplay.REQUEST_STRIDE;
            machines[i] = shown.pos.getX();
            machines[i + 1] = shown.pos.getY();
            machines[i + 2] = shown.pos.getZ();
            machines[i + 3] = shown.hash;
            machines[i + 4] = shown.contents != null ? 1 : 0;
        }
        NEAREST.clear();
        MachineDisplay.request(machines);
    }

    /** The server's answer (client thread). */
    public static void receive(MachineDisplay.Reply reply) {
        Level level = Minecraft.getInstance().level;
        if (!enabled || level == null) {
            return;
        }
        for (MachineDisplay.Update update : reply.updates()) {
            BlockPos pos = new BlockPos(update.x(), update.y(), update.z());
            Shown shown = SHOWN.get(pos);
            if (shown == null) {
                continue;
            }
            if (update.gone()) {
                SHOWN.remove(pos);
            } else {
                shown.apply(update, level);
            }
        }
    }

    /** The panel of a machine for this frame: asked only for machines on screen within the range. */
    @Nullable
    private static Panel panel(MachineBlockEntity machine, float partialTick, double cameraX, double cameraY, double cameraZ) {
        if (!enabled) {
            return null;
        }
        BlockPos pos = machine.getBlockPos();
        Shown shown = SHOWN.get(pos);
        if (shown == null) {
            shown = new Shown(pos, Motion.of(machine.definition()));
            SHOWN.put(pos, shown);
        }
        shown.lastSeen = ticks;
        if (!shown.selected || shown.contents == null || shown.idleAndEmpty()) {
            return null;
        }
        return shown.fill(partialTick, cameraX, cameraY, cameraZ);
    }

    /** One machine the client is drawing. */
    private static final class Shown {
        final BlockPos pos;
        final Motion motion;
        final Panel panel = new Panel();
        long lastSeen;
        boolean selected;
        double distance;

        int hash;
        @Nullable
        MachineDisplay.Contents contents;
        int status;
        boolean running;
        int progress;
        int total;
        int rate;
        long heardAt;

        PanelLayout layout = new PanelLayout(0, 0);
        /** The shown inputs and products: item stacks first, then fluids, each with its label or {@code null}. */
        Object[] inputs = new Object[0];
        Object[] outputs = new Object[0];
        String[] inputLabels = new String[0];
        String[] outputLabels = new String[0];
        long producedAmount;
        long poppedAt = Long.MIN_VALUE / 2;

        Shown(BlockPos pos, Motion motion) {
            this.pos = pos;
            this.motion = motion;
        }

        boolean idleAndEmpty() {
            return !running && inputs.length == 0 && outputs.length == 0;
        }

        void apply(MachineDisplay.Update update, Level level) {
            hash = update.hash();
            status = update.status();
            running = update.running();
            progress = update.progress();
            total = update.total();
            rate = update.rate();
            heardAt = ticks;
            MachineDisplay.Contents now = update.contents();
            if (now != null) {
                boolean first = contents == null;
                contents = now;
                inputs = entries(now.inItems(), now.inFluids());
                outputs = entries(now.outItems(), now.outFluids());
                inputLabels = labels(inputs);
                outputLabels = labels(outputs);
                layout = new PanelLayout(inputs.length, outputs.length);
                long amount = amount(outputs);
                if (!first && amount > producedAmount) {
                    poppedAt = ticks;
                }
                producedAmount = amount;
            }
            anchor(level);
        }

        /** Above the machine when the block above is air, else in front of the top of its face. */
        private void anchor(Level level) {
            float half = layout.height() * panel.unit / 2;
            if (level.getBlockState(pos.above()).isAir()) {
                panel.x = 0.5F;
                panel.y = 1 + ABOVE + half;
                panel.z = 0.5F;
            } else {
                Direction front = MachineBlock.facing(level.getBlockState(pos));
                panel.x = 0.5F + front.getStepX() * IN_FRONT;
                panel.y = 1;
                panel.z = 0.5F + front.getStepZ() * IN_FRONT;
            }
        }

        Panel fill(float partialTick, double cameraX, double cameraY, double cameraZ) {
            Panel p = panel;
            p.clear();
            PanelLayout l = layout;
            float w = l.width();
            float h = l.height();
            backing(p, -w / 2, -h / 2, w, h);
            for (int side = 0; side < 2; side++) {
                boolean output = side == 1;
                for (int i = 0; i < l.slots(output); i++) {
                    p.rect(l.slotX(output, i), l.slotY(output, i), PanelLayout.SLOT, PanelLayout.SLOT, 16, 0, 18, 18, LAYER_FRAME, WHITE);
                }
            }
            float since = ticks - heardAt + partialTick;
            float done = Motions.progress(progress, total, rate, since, running);
            p.rect(l.arrowX(), l.arrowY(), PanelLayout.ARROW_W, PanelLayout.ARROW_H, 0, 20, 24, 17, LAYER_FRAME, WHITE);
            if (done > 0) {
                float filled = PanelLayout.ARROW_W * done;
                p.rect(l.arrowX(), l.arrowY(), filled, PanelLayout.ARROW_H, 24, 20, filled, 17, LAYER_FILL, WHITE);
            }
            p.rect(w / 2 - 3.5F, h / 2 - 3.5F, 2, 2, 41, 1, 2, 2, LAYER_FILL, lamp());

            float dx = (float) cameraX - p.x;
            float dy = (float) cameraY - p.y;
            float dz = (float) cameraZ - p.z;
            boolean near = dx * dx + dy * dy + dz * dz <= TEXT_RANGE * TEXT_RANGE;
            float time = ticks % 4800 + partialTick;
            for (int i = 0; i < inputs.length; i++) {
                float x = l.slotX(false, i);
                float y = l.slotY(false, i);
                if (inputs[i] instanceof ItemStack stack) {
                    if (running) {
                        Motions.input(motion, time, i, inputs.length, MOVE);
                        p.item(stack, x + 9 + MOVE[Motions.DX], y + 9 + MOVE[Motions.DY], 16,
                            MOVE[Motions.STRETCH_X], MOVE[Motions.STRETCH_Y], MOVE[Motions.TURN]);
                    } else {
                        p.item(stack, x + 9, y + 9, 16, 1, 1, 0);
                    }
                } else {
                    p.fluid((FluidAmount) inputs[i], x + 1, y + 1, 16, 16);
                }
                label(p, inputLabels[i], x, y, near);
            }
            float pop = Motions.pop(ticks - poppedAt + partialTick);
            for (int i = 0; i < outputs.length; i++) {
                float x = l.slotX(true, i);
                float y = l.slotY(true, i);
                if (outputs[i] instanceof ItemStack stack) {
                    p.item(stack, x + 9, y + 9, 16 * pop, 1, 1, 0);
                } else {
                    float grow = (pop - 1) * 8;
                    p.fluid((FluidAmount) outputs[i], x + 1 - grow, y + 1 - grow, 16 + 2 * grow, 16 + 2 * grow);
                }
                label(p, outputLabels[i], x, y, near);
            }
            if (running) {
                if (motion == Motion.HEAT) {
                    steam(p, l, time);
                } else if (motion == Motion.FERMENT) {
                    bubbles(p, l, time);
                }
            }
            return p;
        }

        /** Colour of the lamp in the panel's corner: green while working, red when something is missing. */
        private int lamp() {
            if (running) {
                return 0xFF4CE06A;
            }
            MachineStatus[] values = MachineStatus.values();
            MachineStatus current = status >= 0 && status < values.length ? values[status] : MachineStatus.WAITING;
            return switch (current) {
                case WAITING -> 0xFF7F8FA0;
                case NO_POWER, MISSING, UNFORMED -> 0xFFF0483C;
                default -> 0xFFF2B631;
            };
        }

        private static void label(Panel p, @Nullable String label, float slotX, float slotY, boolean near) {
            if (near && label != null) {
                boolean small = label.length() > 3;
                p.text(label, slotX + 17.5F, slotY + (small ? 5.5F : 6.5F), small ? 0.55F : 0.7F, true, WHITE);
            }
        }

        /** The backing: the 16 × 16 frame of the panel texture stretched over the panel, corners kept. */
        private static void backing(Panel p, float x, float y, float w, float h) {
            float c = 3;
            float mw = w - 2 * c;
            float mh = h - 2 * c;
            p.rect(x, y + h - c, c, c, 0, 0, 3, 3, 0, WHITE);
            p.rect(x + c, y + h - c, mw, c, 3, 0, 10, 3, 0, WHITE);
            p.rect(x + w - c, y + h - c, c, c, 13, 0, 3, 3, 0, WHITE);
            p.rect(x, y + c, c, mh, 0, 3, 3, 10, 0, WHITE);
            p.rect(x + c, y + c, mw, mh, 3, 3, 10, 10, 0, WHITE);
            p.rect(x + w - c, y + c, c, mh, 13, 3, 3, 10, 0, WHITE);
            p.rect(x, y, c, c, 0, 13, 3, 3, 0, WHITE);
            p.rect(x + c, y, mw, c, 3, 13, 10, 3, 0, WHITE);
            p.rect(x + w - c, y, c, c, 13, 13, 3, 3, 0, WHITE);
        }

        /** Heating: three puffs rise from the inputs and fade. */
        private static void steam(Panel p, PanelLayout l, float time) {
            for (int j = 0; j < 3; j++) {
                float phase = Motions.phase(time, 36, j, 3);
                float size = 4 + 3 * phase;
                float x = l.inputLeft() + l.inputWidth() * (0.25F + 0.25F * j) + 1.5F * (float) Math.sin(0.1F * time + 2 * j);
                float y = l.inputTop() - 4 + 11 * phase;
                int alpha = (int) (150 * (1 - phase));
                p.rect(x - size / 2, y - size / 2, size, size, 48, 0, 6, 6, LAYER_OVER, alpha << 24 | 0xFFFFFF);
            }
        }

        /** Fermenting: four bubbles rise through the inputs. */
        private static void bubbles(Panel p, PanelLayout l, float time) {
            for (int j = 0; j < 4; j++) {
                float phase = Motions.phase(time, 30, j, 4);
                float column = j * 0.618F + 0.2F;
                float x = l.inputLeft() + l.inputWidth() * (column - (float) Math.floor(column)) * 0.86F + 1;
                float y = l.inputTop() - l.inputHeight() + 1 + (l.inputHeight() - 5) * phase;
                int alpha = (int) (210 * Math.min(1, 4 * phase) * (1 - phase * phase));
                p.rect(x, y, 3, 3, 56, 0, 4, 4, LAYER_OVER, alpha << 24 | 0xFFFFFF);
            }
        }

        /** Item stacks, then fluids, up to what a side shows. */
        private static Object[] entries(ItemStack[] items, FluidAmount[] fluids) {
            int count = Math.min(PanelLayout.MAX_ENTRIES, items.length + fluids.length);
            Object[] entries = new Object[count];
            for (int i = 0; i < count; i++) {
                entries[i] = i < items.length ? items[i] : fluids[i - items.length];
            }
            return entries;
        }

        /** A count above 1 for a stack, the amount for a fluid. */
        private static String[] labels(Object[] entries) {
            String[] labels = new String[entries.length];
            for (int i = 0; i < entries.length; i++) {
                if (entries[i] instanceof ItemStack stack) {
                    labels[i] = stack.getCount() > 1 ? String.valueOf(stack.getCount()) : null;
                } else {
                    int amount = ((FluidAmount) entries[i]).amount();
                    labels[i] = amount >= 1000 ? String.format(Locale.ROOT, "%.1fB", amount / 1000.0) : amount + "mB";
                }
            }
            return labels;
        }

        private static long amount(Object[] entries) {
            long amount = 0;
            for (Object entry : entries) {
                amount += entry instanceof ItemStack stack ? stack.getCount() * 1000L : ((FluidAmount) entry).amount();
            }
            return amount;
        }
    }
}
