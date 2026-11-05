package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Positions of a machine window (machines/04 §1), relative to the window's top-left corner, shared by
 * the menu (slot positions) and the screen. Width 176; height 166, or 190 when the machine has tanks or
 * more than two slots of a role in a column. Left to right: energy bar or fuel, item inputs, catalysts,
 * input tanks, the progress arrow with the product purity under it, item outputs (main product first),
 * output tanks with their venting switch underneath. Too many columns make the tanks narrower. Upgrade and module slots sit in the left
 * upgrades panel (§2).
 */
public final class GuiLayout {
    public static final int WIDTH = 176;
    public static final int SHORT = 166;
    public static final int TALL = 190;
    public static final int SLOT = 18;
    public static final int ARROW_W = 24;
    public static final int ARROW_H = 17;
    /** Top of the side panels and their collapsed size. */
    public static final int PANEL_TOP = 4;
    public static final int TAB = 24;
    public static final int PANEL_GAP = 2;
    /** Width of the upgrades panel (left 2). */
    public static final int UPGRADES_WIDTH = 100;
    /** Header height of an expanded panel (icon and title). */
    public static final int PANEL_HEADER = 22;

    /** A rectangle relative to the window. */
    public record Box(int x, int y, int w, int h) {
        public boolean contains(double px, double py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }

    private final int height;
    private final Box[] slots;
    private final Box[] tanks;
    private final Box[] vents;
    @Nullable
    private final Box energy;
    @Nullable
    private final Box flame;
    private final Box arrow;
    private final int statusY;

    private GuiLayout(int height, Box[] slots, Box[] tanks, Box[] vents, @Nullable Box energy, @Nullable Box flame, Box arrow, int statusY) {
        this.height = height;
        this.slots = slots;
        this.tanks = tanks;
        this.vents = vents;
        this.energy = energy;
        this.flame = flame;
        this.arrow = arrow;
        this.statusY = statusY;
    }

    public int height() {
        return height;
    }

    /** Slot frame (18 × 18) of inventory slot {@code index}; the item sits 1 pixel inside. */
    public Box slot(int index) {
        return slots[index];
    }

    public Box tank(int index) {
        return tanks[index];
    }

    /** Venting switch under an output tank (machines/04 §1), or {@code null} for input tanks. */
    @Nullable
    public Box vent(int tank) {
        return vents[tank];
    }

    /** Height of a venting switch. */
    public static final int VENT_H = 9;

    @Nullable
    public Box energy() {
        return energy;
    }

    /** Fuel flame of T1 machines. */
    @Nullable
    public Box flame() {
        return flame;
    }

    public Box arrow() {
        return arrow;
    }

    /** Baseline of the status line. */
    public int statusY() {
        return statusY;
    }

    /** Top of the player inventory label. */
    public int inventoryLabelY() {
        return height - 94;
    }

    /** First row of the player inventory slots (the item position). */
    public int playerInventoryY() {
        return height - 83;
    }

    /** Top of the upgrades panel when expanded (the info panel above it is collapsed). */
    public static int upgradesPanelTop() {
        return PANEL_TOP + TAB + PANEL_GAP;
    }

    /** Builds the automatic layout of a machine. */
    public static GuiLayout of(MachineDefinition definition) {
        return of(definition.layout(), definition.power());
    }

    /** Builds the automatic layout for slot counts and a power kind. */
    public static GuiLayout of(IoLayout io, PowerKind power) {
        boolean tall = io.tanks() > 0 || io.itemIn() > 2 || io.itemOut() > 2 || io.catalyst() > 2;
        int height = tall ? TALL : SHORT;
        int rows = tall ? 3 : 2;
        int top = 18;
        int area = rows * SLOT + (rows - 1) * 2;
        Box[] slots = new Box[io.itemSlots()];
        int left = 8;
        Box energy = null;
        Box flame = null;
        int fuelStart = io.start(SlotRole.FUEL);
        if (power == PowerKind.ENERGY) {
            energy = new Box(8, top, 12, area);
            left = 24;
        } else if (io.fuel() > 0) {
            flame = new Box(10, top + 2, 14, 14);
            for (int i = 0; i < io.fuel(); i++) {
                slots[fuelStart + i] = new Box(8, top + 20 + i * 20, SLOT, SLOT);
            }
            left = 30;
        }
        int[] tankWidths = {18, 14, 10};
        int[] gaps = {2, 1, 1};
        int[] groupGaps = {4, 4, 2};
        int[] arrowGaps = {6, 4, 2};
        Plan plan = null;
        for (int attempt = 0; attempt < tankWidths.length; attempt++) {
            plan = plan(io, rows, tankWidths[attempt], gaps[attempt], groupGaps[attempt], arrowGaps[attempt]);
            if (plan.width <= 168 - left) {
                break;
            }
        }
        int x = left + Math.max(0, (168 - left - plan.width) / 2);
        Box[] tanks = new Box[io.tanks()];
        Box[] vents = new Box[io.tanks()];
        Box arrow = null;
        for (Group group : plan.groups) {
            switch (group.kind) {
                case ITEMS -> placeSlots(slots, io.start(group.role), group.count, rows, x, top, area, plan.gap);
                case TANKS -> {
                    for (int i = 0; i < group.count; i++) {
                        int tank = group.first + i;
                        int tx = x + i * (plan.tankWidth + plan.gap);
                        if (tank >= io.fluidIn()) {
                            tanks[tank] = new Box(tx, top, plan.tankWidth, area - VENT_H - 1);
                            vents[tank] = new Box(tx, top + area - VENT_H, plan.tankWidth, VENT_H);
                        } else {
                            tanks[tank] = new Box(tx, top, plan.tankWidth, area);
                        }
                    }
                }
                case ARROW -> arrow = new Box(x, top + (area - ARROW_H) / 2 - 4, ARROW_W, ARROW_H);
            }
            x += group.width + group.after;
        }
        int panelTop = upgradesPanelTop() + PANEL_HEADER;
        int panelLeft = -UPGRADES_WIDTH + 8;
        int upgradeRows = (io.upgrades() + 3) / 4;
        for (int i = 0; i < io.upgrades(); i++) {
            slots[io.start(SlotRole.UPGRADE) + i] = new Box(panelLeft + (i % 4) * SLOT, panelTop + (i / 4) * SLOT, SLOT, SLOT);
        }
        int moduleTop = panelTop + upgradeRows * SLOT + (upgradeRows > 0 ? 4 : 0);
        for (int i = 0; i < io.modules(); i++) {
            slots[io.start(SlotRole.MODULE) + i] = new Box(panelLeft + (i % 4) * SLOT, moduleTop + (i / 4) * SLOT, SLOT, SLOT);
        }
        return new GuiLayout(height, slots, tanks, vents, energy, flame, arrow, top + area + 4);
    }

    /** Bottom of the upgrade and module slots in the upgrades panel, relative to the panel's top. */
    public static int upgradesSlotsBottom(IoLayout io) {
        int upgradeRows = (io.upgrades() + 3) / 4;
        int moduleRows = (io.modules() + 3) / 4;
        return PANEL_HEADER + (upgradeRows + moduleRows) * SLOT + (upgradeRows > 0 && moduleRows > 0 ? 4 : 0);
    }

    private static void placeSlots(Box[] slots, int first, int count, int rows, int x, int top, int area, int gap) {
        for (int i = 0; i < count; i++) {
            int column = i / rows;
            int row = i % rows;
            int inColumn = Math.min(rows, count - column * rows);
            int y0 = top + (area - (inColumn * SLOT + (inColumn - 1) * 2)) / 2;
            slots[first + i] = new Box(x + column * (SLOT + gap), y0 + row * (SLOT + 2), SLOT, SLOT);
        }
    }

    private enum Kind {
        ITEMS, TANKS, ARROW
    }

    private record Group(Kind kind, SlotRole role, int first, int count, int width, int after) {
    }

    private record Plan(List<Group> groups, int width, int tankWidth, int gap) {
    }

    private static Plan plan(IoLayout io, int rows, int tankWidth, int gap, int groupGap, int arrowGap) {
        List<Group> groups = new ArrayList<>();
        addItems(groups, SlotRole.INPUT, io.itemIn(), rows, gap, groupGap);
        addItems(groups, SlotRole.CATALYST, io.catalyst(), rows, gap, groupGap);
        addTanks(groups, 0, io.fluidIn(), tankWidth, gap, groupGap);
        int beforeArrow = groups.size();
        groups.add(new Group(Kind.ARROW, SlotRole.INPUT, 0, 0, ARROW_W, arrowGap));
        addItems(groups, SlotRole.OUTPUT, io.itemOut(), rows, gap, groupGap);
        addTanks(groups, io.fluidIn(), io.fluidOut(), tankWidth, gap, groupGap);
        if (beforeArrow > 0) {
            Group last = groups.get(beforeArrow - 1);
            groups.set(beforeArrow - 1, new Group(last.kind, last.role, last.first, last.count, last.width, arrowGap));
        }
        Group end = groups.get(groups.size() - 1);
        groups.set(groups.size() - 1, new Group(end.kind, end.role, end.first, end.count, end.width, 0));
        int width = 0;
        for (Group group : groups) {
            width += group.width + group.after;
        }
        return new Plan(groups, width, tankWidth, gap);
    }

    private static void addItems(List<Group> groups, SlotRole role, int count, int rows, int gap, int after) {
        if (count > 0) {
            int columns = (count + rows - 1) / rows;
            groups.add(new Group(Kind.ITEMS, role, 0, count, columns * SLOT + (columns - 1) * gap, after));
        }
    }

    private static void addTanks(List<Group> groups, int first, int count, int tankWidth, int gap, int after) {
        if (count > 0) {
            groups.add(new Group(Kind.TANKS, SlotRole.INPUT, first, count, count * tankWidth + (count - 1) * gap, after));
        }
    }
}
