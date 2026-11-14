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
 * the menu (slot positions) and the screen. Width 176, height 190 for every machine (2026-11-05).
 *
 * <p>The machine area is split into framed zones, left to right (2026-11-05): energy (energy bar, or
 * fuel flame and fuel slots), inputs (item inputs, then input tanks), processing (progress arrow on the
 * top row, the product purity in its own frame under it, catalysts on the bottom row) and products (item
 * outputs with the main product first, then output tanks with their venting switch underneath). Zones
 * without content are left out; the processing zone takes the width left over, so the zones always span
 * the window. Elements sit on an 18-pixel grid of three rows: slots touch, columns start at the top row
 * and are centred horizontally in their zone; tanks and the energy bar fill the zone height. The energy
 * zone holds either the energy bar or the fuel and is centred vertically. Too many columns make the
 * tanks narrower. Upgrade and module slots sit in the left upgrades panel (§2).
 */
public final class GuiLayout {
    public static final int WIDTH = 176;
    /** Height of a machine window. */
    public static final int TALL = 190;
    /** Height of a port window that is not linked to a controller. */
    public static final int SHORT = 166;
    /** Rows of the machine area grid. */
    public static final int ROWS = 3;
    /** Product purity frame (holds "100%"), centred under the arrow. */
    public static final int PURITY_W = 30;
    public static final int PURITY_H = 12;
    public static final int SLOT = 18;
    public static final int ARROW_W = 24;
    public static final int ARROW_H = 17;
    /** Top of the zone contents; the zone frames are {@link #ZONE_PAD} outside. */
    public static final int TOP = 19;
    /** Frame line plus padding around a zone's contents. */
    public static final int ZONE_PAD = 3;
    /** Space between two zone frames. */
    public static final int ZONE_GAP = 2;
    /** Left and right edge of the zone frames. */
    public static final int AREA_LEFT = 7;
    public static final int AREA_RIGHT = WIDTH - 7;
    /** Width of the energy bar. */
    public static final int ENERGY_W = 12;
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

    /** The zones of the machine area, left to right. */
    public enum ZoneKind {
        ENERGY, INPUTS, PROCESSING, PRODUCTS
    }

    /** A zone and its frame (the outer edge of the frame line). */
    public record Zone(ZoneKind kind, Box frame) {
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
    private final Box purity;
    private final List<Zone> zones;
    private final int statusY;

    private GuiLayout(int height, Box[] slots, Box[] tanks, Box[] vents, @Nullable Box energy, @Nullable Box flame, Box arrow, Box purity,
                      List<Zone> zones, int statusY) {
        this.height = height;
        this.slots = slots;
        this.tanks = tanks;
        this.vents = vents;
        this.energy = energy;
        this.flame = flame;
        this.arrow = arrow;
        this.purity = purity;
        this.zones = zones;
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

    /** Frame of the product purity, under the arrow (2026-11-05). */
    public Box purity() {
        return purity;
    }

    /** The framed zones, left to right. */
    public List<Zone> zones() {
        return zones;
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

    /**
     * Builds the window of a machine. A multiblock controller shows no item slots or tanks (2026-11-10):
     * only the energy bar or the fuel flame, the progress and the product purity; its upgrade and module
     * slots stay in the upgrades panel.
     */
    public static GuiLayout of(MachineDefinition definition) {
        return of(definition.layout(), definition.power(), definition.isMultiblock());
    }

    /** Positions of every slot and tank of a machine, as a single-block window has them (port windows). */
    public static GuiLayout full(MachineDefinition definition) {
        return of(definition.layout(), definition.power(), false);
    }

    /** Where hidden slots and tanks of a controller window sit: outside the window. */
    private static final Box HIDDEN = new Box(-10000, -10000, SLOT, SLOT);

    /** Tank widths tried in turn until the zones fit. */
    private static final int[] TANK_WIDTHS = {18, 16, 14, 12, 10, 8, 7, 6};

    /** Builds the automatic layout for slot counts and a power kind. */
    public static GuiLayout of(IoLayout io, PowerKind power) {
        return of(io, power, false);
    }

    /** Builds the automatic layout; {@code controller}: a multiblock controller window without slots or tanks. */
    public static GuiLayout of(IoLayout io, PowerKind power, boolean controller) {
        int rows = ROWS;
        int area = rows * SLOT;
        boolean bar = power == PowerKind.ENERGY || power == PowerKind.MANUAL;
        int energyContent = bar ? ENERGY_W : io.fuel() > 0 ? SLOT : 0;
        int processingContent = Math.max(Math.max(ARROW_W, PURITY_W), controller ? 0 : io.catalyst() * SLOT);
        int tankWidth = TANK_WIDTHS[0];
        int[] contents = new int[4];
        for (int width : TANK_WIDTHS) {
            tankWidth = width;
            contents[0] = energyContent;
            contents[1] = controller ? 0 : columns(io.itemIn(), rows) * SLOT + io.fluidIn() * width;
            contents[2] = processingContent;
            contents[3] = controller ? 0 : columns(io.itemOut(), rows) * SLOT + io.fluidOut() * width;
            if (naturalWidth(contents) <= AREA_RIGHT - AREA_LEFT) {
                break;
            }
        }
        contents[2] += Math.max(0, AREA_RIGHT - AREA_LEFT - naturalWidth(contents));

        Box[] slots = new Box[io.itemSlots()];
        Box[] tanks = new Box[io.tanks()];
        Box[] vents = new Box[io.tanks()];
        Box energy = null;
        Box flame = null;
        Box arrow = null;
        Box purity = null;
        List<Zone> zones = new ArrayList<>();
        int frameX = AREA_LEFT;
        for (ZoneKind kind : ZoneKind.values()) {
            int content = contents[kind.ordinal()];
            if (content == 0) {
                continue;
            }
            zones.add(new Zone(kind, new Box(frameX, TOP - ZONE_PAD, content + 2 * ZONE_PAD, area + 2 * ZONE_PAD)));
            int x = frameX + ZONE_PAD;
            switch (kind) {
                case ENERGY -> {
                    if (bar) {
                        // FE, or the hand crank's stored running time (I14)
                        energy = new Box(x, TOP, ENERGY_W, area);
                    } else {
                        int shownFuel = controller ? 0 : io.fuel();
                        int y = TOP + (area - (1 + shownFuel) * SLOT) / 2;
                        flame = new Box(x + 2, y + 2, 14, 14);
                        for (int i = 0; i < shownFuel; i++) {
                            slots[io.start(SlotRole.FUEL) + i] = new Box(x, y + (1 + i) * SLOT, SLOT, SLOT);
                        }
                    }
                }
                case INPUTS -> {
                    x = placeSlots(slots, io.start(SlotRole.INPUT), io.itemIn(), rows, x);
                    for (int i = 0; i < io.fluidIn(); i++) {
                        tanks[i] = new Box(x + i * tankWidth, TOP, tankWidth, area);
                    }
                }
                case PROCESSING -> {
                    // top row: arrow; middle row: purity frame; bottom row: catalysts
                    arrow = new Box(x + (content - ARROW_W) / 2, TOP, ARROW_W, ARROW_H);
                    purity = new Box(x + (content - PURITY_W) / 2, TOP + SLOT + (SLOT - PURITY_H) / 2, PURITY_W, PURITY_H);
                    int catalystX = x + (content - io.catalyst() * SLOT) / 2;
                    for (int i = 0; i < (controller ? 0 : io.catalyst()); i++) {
                        slots[io.start(SlotRole.CATALYST) + i] = new Box(catalystX + i * SLOT, TOP + 2 * SLOT, SLOT, SLOT);
                    }
                }
                case PRODUCTS -> {
                    x = placeSlots(slots, io.start(SlotRole.OUTPUT), io.itemOut(), rows, x);
                    for (int i = 0; i < io.fluidOut(); i++) {
                        int tank = io.fluidIn() + i;
                        tanks[tank] = new Box(x + i * tankWidth, TOP, tankWidth, area - VENT_H - 1);
                        vents[tank] = new Box(x + i * tankWidth, TOP + area - VENT_H, tankWidth, VENT_H);
                    }
                }
            }
            frameX += content + 2 * ZONE_PAD + ZONE_GAP;
        }
        if (controller) {
            for (int i = 0; i < io.start(SlotRole.UPGRADE); i++) {
                slots[i] = HIDDEN;
            }
            for (int i = 0; i < tanks.length; i++) {
                tanks[i] = HIDDEN;
            }
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
        return new GuiLayout(TALL, slots, tanks, vents, energy, flame, arrow, purity, List.copyOf(zones), TOP + area + ZONE_PAD + 2);
    }

    /** Width of the zones with their frames and gaps for the given content widths (0 = no zone). */
    private static int naturalWidth(int[] contents) {
        int width = 0;
        int zones = 0;
        for (int content : contents) {
            if (content > 0) {
                width += content + 2 * ZONE_PAD;
                zones++;
            }
        }
        return width + Math.max(0, zones - 1) * ZONE_GAP;
    }

    private static int columns(int count, int rows) {
        return (count + rows - 1) / rows;
    }

    /** Bottom of the upgrade and module slots in the upgrades panel, relative to the panel's top. */
    public static int upgradesSlotsBottom(IoLayout io) {
        int upgradeRows = (io.upgrades() + 3) / 4;
        int moduleRows = (io.modules() + 3) / 4;
        return PANEL_HEADER + (upgradeRows + moduleRows) * SLOT + (upgradeRows > 0 && moduleRows > 0 ? 4 : 0);
    }

    /**
     * Places {@code count} touching slots in columns of {@code rows}, each column starting at the top
     * row (2026-11-05); returns the x after the last column.
     */
    private static int placeSlots(Box[] slots, int first, int count, int rows, int x) {
        for (int i = 0; i < count; i++) {
            slots[first + i] = new Box(x + (i / rows) * SLOT, TOP + (i % rows) * SLOT, SLOT, SLOT);
        }
        return x + columns(count, rows) * SLOT;
    }
}
