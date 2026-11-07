package com.tonywww.foodindustrial.gui.client;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.GuiLayout;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.gui.MachineMenu;
import com.tonywww.foodindustrial.gui.MachineView;
import com.tonywww.foodindustrial.gui.MenuActions;
import com.tonywww.foodindustrial.machine.ModMachines;
import com.tonywww.foodindustrial.machine.block.MachineBlock;
import com.tonywww.foodindustrial.machine.component.RedstoneControl;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.component.VentMode;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;
import com.tonywww.foodindustrial.platform.client.gui.PlatformScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The machine screen (machines/04 §1–§5): main window with slots, tanks, energy or fuel, progress
 * arrow with the product purity and the status line; side panels (left: info, upgrades and modules;
 * right: input/output, redstone), one open per side, remembered per machine type for this game session;
 * the structure page of multiblock controllers.
 */
public class MachineScreen extends PlatformScreen<MachineMenu> {
    private static final int OVERLAP = 3;
    /** Face cells of the input/output panel and their pitch (half size, 2026-11-05). */
    private static final int CELL = 12;
    private static final int CELL_PITCH = 13;
    /** Pitch of the redstone buttons. */
    private static final int SMALL_PITCH = Widgets.SMALL + 2;
    /** Height of the text buttons in the advanced input/output options and the pitch of their rows. */
    private static final int TEXT_BUTTON_H = 10;
    private static final int ROW_PITCH = TEXT_BUTTON_H + 2;
    /** Side of the coloured squares of a face cell's item and fluid channels. */
    private static final int CHANNEL = 4;
    private static final long DUMP_CONFIRM_MS = 2000;
    /** Open panel per side and machine type, for this game session (2026-11-05). */
    private static final Map<String, Panel> OPEN_LEFT = new HashMap<>();
    private static final Map<String, Panel> OPEN_RIGHT = new HashMap<>();
    private static final Map<String, Boolean> STRUCTURE_PAGE = new HashMap<>();

    private enum Panel {
        INFO(true, Widgets.Color.BLUE, Widgets.Icon.INFO, null, "info"),
        UPGRADES(true, Widgets.Color.GREEN, Widgets.Icon.UPGRADES, null, "upgrades"),
        /** Tab icon: the device wrench (2026-11-05). */
        IO(false, Widgets.Color.ORANGE, null, () -> new ItemStack(ModMachines.DEVICE_WRENCH.get()), "io"),
        /** Tab icon: redstone dust (2026-11-05). */
        REDSTONE(false, Widgets.Color.RED, null, () -> new ItemStack(Items.REDSTONE), "redstone");

        final boolean left;
        final Widgets.Color color;
        @Nullable
        final Widgets.Icon icon;
        @Nullable
        final Supplier<ItemStack> item;
        final String key;

        Panel(boolean left, Widgets.Color color, @Nullable Widgets.Icon icon, @Nullable Supplier<ItemStack> item, String key) {
            this.left = left;
            this.color = color;
            this.icon = icon;
            this.item = item;
            this.key = key;
        }
    }

    /** A panel's rectangle relative to the window. */
    private record PanelBox(Panel panel, boolean open, int x, int y, int w, int h) {
        boolean contains(double px, double py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }

        /** Left edge of the visible part (the window hides {@link #OVERLAP} pixels). */
        int inner() {
            return panel.left ? x : x + OVERLAP;
        }

        boolean onHeader(double px, double py) {
            return contains(px, py) && py < y + (open ? GuiLayout.PANEL_HEADER : GuiLayout.TAB);
        }
    }

    private final MachineDefinition definition;
    private final GuiLayout layout;
    private final List<Panel> panels = new ArrayList<>();
    /** Face shown in the advanced options of the input/output panel, or -1. */
    private int advancedFace = -1;
    private int dumpTank = -1;
    private long dumpUntil;

    public MachineScreen(MachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, GuiLayout.WIDTH, menu.layout().height());
        this.definition = menu.definition();
        this.layout = menu.layout();
        panels.add(Panel.INFO);
        if (definition.layout().upgrades() + definition.layout().modules() > 0) {
            panels.add(Panel.UPGRADES);
        }
        if (!definition.isMultiblock()) {
            panels.add(Panel.IO);
        }
        panels.add(Panel.REDSTONE);
        syncMenuState();
    }

    private MachineView view() {
        return menu.view();
    }

    @Nullable
    private Panel open(boolean left) {
        Panel panel = (left ? OPEN_LEFT : OPEN_RIGHT).get(definition.id());
        return panel != null && panels.contains(panel) ? panel : null;
    }

    private void toggle(Panel panel) {
        Map<String, Panel> map = panel.left ? OPEN_LEFT : OPEN_RIGHT;
        if (map.get(definition.id()) == panel) {
            map.remove(definition.id());
        } else {
            map.put(definition.id(), panel);
        }
        if (!panel.left) {
            advancedFace = -1;
        }
        syncMenuState();
    }

    private boolean structurePage() {
        return definition.isMultiblock() && STRUCTURE_PAGE.getOrDefault(definition.id(), false);
    }

    private void syncMenuState() {
        menu.setUpgradesOpen(open(true) == Panel.UPGRADES && !structurePage());
        menu.setStructurePage(structurePage());
    }

    // ---- panel geometry ----

    private List<PanelBox> panelBoxes() {
        List<PanelBox> boxes = new ArrayList<>();
        int leftY = GuiLayout.PANEL_TOP;
        int rightY = GuiLayout.PANEL_TOP;
        for (Panel panel : panels) {
            boolean isOpen = open(panel.left) == panel;
            int w = (isOpen ? width(panel) : GuiLayout.TAB) + OVERLAP;
            int h = isOpen ? height(panel) : GuiLayout.TAB;
            int y = panel.left ? leftY : rightY;
            int x = panel.left ? -w + OVERLAP : GuiLayout.WIDTH - OVERLAP;
            boxes.add(new PanelBox(panel, isOpen, x, y, w, h));
            if (panel.left) {
                leftY += h + GuiLayout.PANEL_GAP;
            } else {
                rightY += h + GuiLayout.PANEL_GAP;
            }
        }
        return boxes;
    }

    private int width(Panel panel) {
        return switch (panel) {
            case INFO -> 118;
            case UPGRADES -> GuiLayout.UPGRADES_WIDTH - OVERLAP;
            case IO -> ioContentWidth() + 10;
            case REDSTONE -> redstoneContentWidth() + 10;
        };
    }

    private int height(Panel panel) {
        return switch (panel) {
            case INFO -> GuiLayout.PANEL_HEADER + infoLines().size() * 10 + 4;
            case UPGRADES -> GuiLayout.upgradesSlotsBottom(definition.layout()) + 4 + upgradeLines().size() * 10 + 4;
            case IO -> GuiLayout.PANEL_HEADER + (advancedFace >= 0 ? advancedHeight() : 3 * CELL_PITCH - 1) + 5;
            case REDSTONE -> GuiLayout.PANEL_HEADER + Widgets.SMALL + 4 + 10 + 3;
        };
    }

    /**
     * Content width the header title of a right panel needs: it starts 1 pixel left of the content and
     * ends 4 pixels before the panel edge (no icon on an open right panel), and on the advanced page
     * follows the back button.
     */
    private int titleRoom(Component title, boolean back) {
        return font.width(title) - 2 + (back ? Widgets.SMALL + 2 : 0);
    }

    private Component panelTitle(Panel panel) {
        if (panel == Panel.IO && advancedFace >= 0) {
            return advancedTitle(SideConfig.Face.values()[advancedFace]);
        }
        return Component.translatable(GuiText.key("panel", panel.key));
    }

    private static Component advancedTitle(SideConfig.Face face) {
        return Component.translatable(GuiText.key("io", "advanced_title"), Component.translatable(GuiText.key("io", face.name().toLowerCase(Locale.ROOT))));
    }

    /** Content width of the input/output panel: face grid, titles of both pages, advanced rows. */
    private int ioContentWidth() {
        int width = Math.max(4 * CELL_PITCH - 1, titleRoom(Component.translatable(GuiText.key("panel", Panel.IO.key)), false));
        if (!advancedRows().isEmpty()) {
            for (SideConfig.Face face : SideConfig.Face.values()) {
                width = Math.max(width, titleRoom(advancedTitle(face), true));
            }
            for (String key : new String[]{"scope_out", "scope_in"}) {
                width = Math.max(width, font.width(Component.translatable(GuiText.key("io", key))));
            }
            int widest = 0;
            for (String key : new String[]{"scope_all", "scope_main", "scope_byproduct"}) {
                widest = Math.max(widest, font.width(Component.translatable(GuiText.key("io", key))));
            }
            widest = Math.max(widest, font.width(Component.translatable(GuiText.key("io", "scope_tank"), "9")));
            width = Math.max(width, rowLabelWidth() + 2 + widest + 6);
        }
        return width;
    }

    /** Width of the row labels of the advanced options ("物品：", "流体："). */
    private int rowLabelWidth() {
        return Math.max(font.width(Component.translatable(GuiText.key("io", "label_items"))),
            font.width(Component.translatable(GuiText.key("io", "label_fluids"))));
    }

    /** Content width of the redstone panel: the buttons, every mode name, the title. */
    private int redstoneContentWidth() {
        int width = Math.max(4 * SMALL_PITCH - 2, titleRoom(Component.translatable(GuiText.key("panel", Panel.REDSTONE.key)), false));
        for (RedstoneControl control : RedstoneControl.values()) {
            width = Math.max(width, font.width(Component.translatable(GuiText.key("redstone", control.id()))));
        }
        return width;
    }

    @Override
    protected boolean isInsideExtras(double mouseX, double mouseY) {
        double mx = mouseX - leftPos;
        double my = mouseY - topPos;
        for (PanelBox box : panelBoxes()) {
            if (box.contains(mx, my)) {
                return true;
            }
        }
        return false;
    }

    // ---- drawing ----

    @Override
    protected void drawBackground(Canvas canvas, int mouseX, int mouseY) {
        int mx = mouseX - leftPos;
        int my = mouseY - topPos;
        canvas.push();
        canvas.translate(leftPos, topPos);
        for (PanelBox box : panelBoxes()) {
            drawPanel(canvas, box, mx, my);
        }
        Widgets.window(canvas, 0, 0, imageWidth, imageHeight);
        for (int i = menu.machineSlots(); i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            Widgets.slot(canvas, Widgets.SlotKind.NORMAL, slot.x - 1, slot.y - 1);
        }
        if (structurePage()) {
            drawStructure(canvas, mx, my);
        } else {
            drawMain(canvas, mx, my);
        }
        drawTopButtons(canvas, mx, my);
        canvas.pop();
    }

    private void drawMain(Canvas canvas, int mx, int my) {
        MachineView view = view();
        IoLayout io = definition.layout();
        for (GuiLayout.Zone zone : layout.zones()) {
            Widgets.zoneFrame(canvas, zone.frame());
        }
        for (int i = 0; i < menu.machineSlots(); i++) {
            SlotRole role = io.roleOf(i);
            if (role == SlotRole.UPGRADE || role == SlotRole.MODULE) {
                continue;
            }
            GuiLayout.Box box = layout.slot(i);
            Widgets.SlotKind kind = switch (role) {
                case CATALYST -> Widgets.SlotKind.CATALYST;
                case OUTPUT -> io.itemOut() > 1 && i == io.start(SlotRole.OUTPUT) ? Widgets.SlotKind.MAIN : Widgets.SlotKind.NORMAL;
                default -> Widgets.SlotKind.NORMAL;
            };
            Widgets.slot(canvas, kind, box.x(), box.y());
        }
        GuiLayout.Box energy = layout.energy();
        if (energy != null) {
            Widgets.energy(canvas, energy.x(), energy.y(), energy.w(), energy.h(), view.energy, view.energyCapacity);
        }
        GuiLayout.Box flame = layout.flame();
        if (flame != null) {
            int lit = view.burnTotal > 0 && view.burnLeft > 0 ? Math.max(1, view.burnLeft * 14 / view.burnTotal) : 0;
            Widgets.flame(canvas, flame.x(), flame.y(), lit);
        }
        for (int i = 0; i < io.tanks(); i++) {
            GuiLayout.Box box = layout.tank(i);
            FluidAmount fluid = i < view.tanks.length ? view.tanks[i] : FluidAmount.EMPTY;
            Widgets.tank(canvas, box.x(), box.y(), box.w(), box.h(), fluid, view.tankCapacity);
            if (i < view.tankLocks.length && !view.tankLocks[i].isEmpty()) {
                Widgets.lock(canvas, box.x() + box.w() - 8, box.y() + 1);
            }
            if (i < view.tankPhases.length && view.tankPhases[i] == 1) {
                Widgets.gas(canvas, box.x() + 1, box.y() + 1);
            }
            if (i == dumpTank && System.currentTimeMillis() < dumpUntil) {
                canvas.outline(box.x(), box.y(), box.w(), box.h(), 0xFFFF4040);
            }
            GuiLayout.Box vent = layout.vent(i);
            if (vent != null) {
                int mode = i < view.tankVents.length ? view.tankVents[i] : 0;
                Widgets.button(canvas, vent.x(), vent.y(), vent.w(), vent.h(), vent.contains(mx, my), mode != 0);
                Widgets.vent(canvas, mode, vent.x() + (vent.w() - 8) / 2, vent.y() + (vent.h() - 8) / 2);
            }
        }
        GuiLayout.Box arrow = layout.arrow();
        int done = view.total > 0 ? (int) Math.min(GuiLayout.ARROW_W, (long) view.progress * GuiLayout.ARROW_W / view.total) : 0;
        Widgets.arrow(canvas, arrow.x(), arrow.y(), done);
        GuiLayout.Box purityBox = layout.purity();
        Widgets.zoneFrame(canvas, purityBox);
        Component purity = productPurity(true);
        if (purity != null) {
            int room = purityBox.w() - 4;
            int width = Math.min(room, canvas.width(purity));
            Widgets.fitText(canvas, purity, purityBox.x() + (purityBox.w() - width) / 2, purityBox.y() + 2, room, 0xFFFFFFFF, true);
        }
        Widgets.fitText(canvas, statusText(), 8, layout.statusY(), GuiLayout.WIDTH - 16, statusColor(), false);
    }

    private void drawTopButtons(Canvas canvas, int mx, int my) {
        if (!definition.modes().isEmpty()) {
            GuiLayout.Box box = modeButton();
            Widgets.button(canvas, box.x(), box.y(), box.w(), box.h(), box.contains(mx, my), false);
            smallIcon(canvas, Widgets.Icon.MODE, box.x() + 1, box.y() + 1);
        }
        if (definition.isMultiblock()) {
            GuiLayout.Box box = structureButton();
            Widgets.button(canvas, box.x(), box.y(), box.w(), box.h(), box.contains(mx, my), structurePage());
            smallIcon(canvas, Widgets.Icon.STRUCTURE, box.x() + 1, box.y() + 1);
        }
    }

    private static void smallIcon(Canvas canvas, Widgets.Icon icon, int x, int y) {
        canvas.push();
        canvas.translate(x, y);
        canvas.scale(0.75F);
        Widgets.icon(canvas, icon, 0, 0);
        canvas.pop();
    }

    private GuiLayout.Box modeButton() {
        return new GuiLayout.Box(GuiLayout.WIDTH - 22, 2, 14, 14);
    }

    private GuiLayout.Box structureButton() {
        return new GuiLayout.Box(GuiLayout.WIDTH - 22 - (definition.modes().isEmpty() ? 0 : 16), 2, 14, 14);
    }

    @Override
    protected void drawForeground(Canvas canvas, int mouseX, int mouseY) {
        if (structurePage()) {
            return;
        }
        MachineView view = view();
        IoLayout io = definition.layout();
        for (int i = 0; i < io.itemIn(); i++) {
            int index = io.start(SlotRole.INPUT) + i;
            ItemStack lock = index < view.slotLocks.length ? view.slotLocks[index] : ItemStack.EMPTY;
            if (lock.isEmpty()) {
                continue;
            }
            Slot slot = menu.slots.get(index);
            if (!slot.hasItem()) {
                canvas.item(lock, slot.x, slot.y);
                canvas.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x9A8B8B8B);
            }
            Widgets.lock(canvas, slot.x + 10, slot.y - 1);
        }
    }

    private void drawPanel(Canvas canvas, PanelBox box, int mx, int my) {
        Widgets.panel(canvas, box.panel.color, box.x, box.y, box.w, box.h);
        // right panels show their icon only on the collapsed tab (2026-11-05)
        if (box.panel.left || !box.open) {
            int iconX = box.panel.left ? box.x + 4 : box.x + box.w - 20;
            if (box.panel.item != null) {
                canvas.item(box.panel.item.get(), iconX, box.y + 4);
            } else if (box.panel.icon != null) {
                Widgets.icon(canvas, box.panel.icon, iconX, box.y + 4);
            }
        }
        if (!box.open) {
            return;
        }
        Component title = panelTitle(box.panel);
        int titleX = box.panel.left ? box.x + 22 : box.inner() + 4;
        int titleWidth = box.panel.left ? box.w - OVERLAP - 26 : box.w - OVERLAP - 8;
        if (box.panel == Panel.IO && advancedFace >= 0) {
            GuiLayout.Box back = backButton(box);
            Widgets.smallButton(canvas, back.x(), back.y(), Widgets.SmallIcon.BACK, back.contains(mx, my), false);
            titleX += Widgets.SMALL + 2;
            titleWidth -= Widgets.SMALL + 2;
        }
        Widgets.fitText(canvas, title, titleX, box.y + 8, titleWidth, 0xFFFFFFFF, true);
        int cx = box.inner() + 5;
        int cy = box.y + GuiLayout.PANEL_HEADER;
        switch (box.panel) {
            case INFO -> {
                int y = cy;
                for (Component line : infoLines()) {
                    Widgets.fitText(canvas, line, cx, y, box.w - OVERLAP - 10, 0xFFFFFFFF, true);
                    y += 10;
                }
            }
            case UPGRADES -> drawUpgrades(canvas, box, cx);
            case IO -> drawIo(canvas, cx, cy, mx, my);
            case REDSTONE -> drawRedstone(canvas, cx, cy, mx, my);
        }
    }

    private void drawUpgrades(Canvas canvas, PanelBox box, int cx) {
        IoLayout io = definition.layout();
        for (int i = 0; i < io.upgrades() + io.modules(); i++) {
            GuiLayout.Box slot = layout.slot(io.start(SlotRole.UPGRADE) + i);
            Widgets.slot(canvas, Widgets.SlotKind.UPGRADE, slot.x(), slot.y());
        }
        int y = box.y + GuiLayout.upgradesSlotsBottom(io) + 4;
        for (Component line : upgradeLines()) {
            Widgets.fitText(canvas, line, cx, y, box.w - OVERLAP - 10, 0xFFFFFFFF, true);
            y += 10;
        }
    }

    // ---- input / output panel ----

    private static final int[][] FACE_CELLS = {
        {1, 1}, // FRONT
        {3, 1}, // BACK
        {0, 1}, // LEFT
        {2, 1}, // RIGHT
        {1, 0}, // TOP
        {1, 2}, // BOTTOM
    };
    /** Automation switches in the corners: input items, input fluids, output items, output fluids. */
    private static final int[][] AUTO_CELLS = {{0, 0}, {2, 0}, {0, 2}, {2, 2}};
    private static final Widgets.SmallIcon[] AUTO_ICONS = {Widgets.SmallIcon.IN_ITEMS, Widgets.SmallIcon.IN_FLUIDS, Widgets.SmallIcon.OUT_ITEMS, Widgets.SmallIcon.OUT_FLUIDS};
    private static final String[] AUTO_KEYS = {"auto_input_items", "auto_input_fluids", "auto_output_items", "auto_output_fluids"};

    private boolean hasItems() {
        return definition.layout().itemIn() + definition.layout().itemOut() + definition.layout().catalyst() + definition.layout().fuel() > 0;
    }

    private boolean hasFluids() {
        return definition.layout().tanks() > 0;
    }

    private void drawIo(Canvas canvas, int cx, int cy, int mx, int my) {
        if (advancedFace >= 0) {
            drawAdvanced(canvas, cx, cy, mx, my);
            return;
        }
        MachineView view = view();
        for (SideConfig.Face face : SideConfig.Face.values()) {
            drawFace(canvas, face, faceCell(cx, cy, face), view, mx, my);
        }
        for (int i = 0; i < 4; i++) {
            if (!hasAuto(i)) {
                continue;
            }
            GuiLayout.Box button = autoButton(cx, cy, i);
            boolean on = (view.auto & (1 << i)) != 0;
            Widgets.smallButton(canvas, button.x(), button.y(), AUTO_ICONS[i], button.contains(mx, my), on);
        }
    }

    private static GuiLayout.Box faceCell(int cx, int cy, SideConfig.Face face) {
        return new GuiLayout.Box(cx + FACE_CELLS[face.ordinal()][0] * CELL_PITCH, cy + FACE_CELLS[face.ordinal()][1] * CELL_PITCH, CELL, CELL);
    }

    /** Automation switch {@code i} in a corner cell of the face grid. */
    private static GuiLayout.Box autoButton(int cx, int cy, int i) {
        int inset = (CELL - Widgets.SMALL) / 2;
        return new GuiLayout.Box(cx + AUTO_CELLS[i][0] * CELL_PITCH + inset, cy + AUTO_CELLS[i][1] * CELL_PITCH + inset, Widgets.SMALL, Widgets.SMALL);
    }

    private boolean hasAuto(int i) {
        return (i & 1) == 1 ? hasFluids() : hasItems();
    }

    /**
     * A face cell: a centred square for the item channel on top and one for the fluid channel below,
     * coloured by mode; no letters, the tooltip names the modes (2026-11-05).
     */
    private void drawFace(Canvas canvas, SideConfig.Face face, GuiLayout.Box cell, MachineView view, int mx, int my) {
        int x = cell.x();
        int y = cell.y();
        int inset = (CELL - CHANNEL) / 2;
        int gap = CELL - 2 * CHANNEL - 2;
        canvas.fill(x, y, x + CELL, y + CELL, 0xFF202020);
        drawChannel(canvas, x + inset, y + 1, CHANNEL, CHANNEL, hasItems() ? view.sideItems[face.ordinal()] : -1);
        drawChannel(canvas, x + inset, y + 1 + CHANNEL + gap, CHANNEL, CHANNEL, hasFluids() ? view.sideFluids[face.ordinal()] : -1);
        if (cell.contains(mx, my)) {
            canvas.outline(x - 1, y - 1, CELL + 2, CELL + 2, 0xFFFFFFFF);
        }
    }

    private void drawChannel(Canvas canvas, int x, int y, int w, int h, int mode) {
        if (mode < 0) {
            canvas.fill(x, y, x + w, y + h, Widgets.CHANNEL_NONE);
            return;
        }
        SideConfig.Mode value = SideConfig.Mode.values()[Math.floorMod(mode, 4)];
        switch (value) {
            case OFF -> canvas.fill(x, y, x + w, y + h, Widgets.CHANNEL_OFF);
            case INPUT -> canvas.fill(x, y, x + w, y + h, Widgets.CHANNEL_INPUT);
            case OUTPUT -> canvas.fill(x, y, x + w, y + h, Widgets.CHANNEL_OUTPUT);
            case BOTH -> {
                for (int row = 0; row < h; row++) {
                    int split = x + w - (row + 1) * w / h;
                    canvas.fill(x, y + row, split, y + row + 1, Widgets.CHANNEL_INPUT);
                    canvas.fill(split, y + row, x + w, y + row + 1, Widgets.CHANNEL_OUTPUT);
                }
            }
        }
    }

    private ItemStack neighbour(SideConfig.Face face) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return ItemStack.EMPTY;
        }
        BlockState own = minecraft.level.getBlockState(menu.pos());
        Direction direction = face.toDirection(MachineBlock.facing(own));
        BlockPos pos = menu.pos().relative(direction);
        BlockState state = minecraft.level.getBlockState(pos);
        return state.isAir() ? ItemStack.EMPTY : new ItemStack(state.getBlock().asItem());
    }

    /** Rows of the advanced options: 0 item output scope, 1 fluid output tank, 2 fluid input tank. */
    private List<Integer> advancedRows() {
        List<Integer> rows = new ArrayList<>();
        if (definition.layout().itemOut() > 0) {
            rows.add(0);
        }
        if (definition.layout().fluidOut() > 0) {
            rows.add(1);
        }
        if (definition.layout().fluidIn() > 0) {
            rows.add(2);
        }
        return rows;
    }

    /** A row of the advanced options: option 0 item output, 1 fluid output, 2 fluid input. */
    private record AdvancedRow(int option, int y, boolean items) {
    }

    /** Section headings ("输出范围", "输入范围") and rows of the advanced options, from the top of the content. */
    private List<int[]> advancedSections() {
        List<int[]> sections = new ArrayList<>();
        int y = 0;
        List<Integer> options = advancedRows();
        if (options.contains(0) || options.contains(1)) {
            sections.add(new int[]{0, y});
            y += 10 + (options.contains(0) ? ROW_PITCH : 0) + (options.contains(1) ? ROW_PITCH : 0);
        }
        if (options.contains(2)) {
            sections.add(new int[]{1, y + (y > 0 ? 2 : 0)});
        }
        return sections;
    }

    private List<AdvancedRow> advancedLayout() {
        List<AdvancedRow> rows = new ArrayList<>();
        List<Integer> options = advancedRows();
        for (int[] section : advancedSections()) {
            int y = section[1] + 10;
            for (int option : section[0] == 0 ? new int[]{0, 1} : new int[]{2}) {
                if (options.contains(option)) {
                    rows.add(new AdvancedRow(option, y, option == 0));
                    y += ROW_PITCH;
                }
            }
        }
        return rows;
    }

    private int advancedHeight() {
        List<AdvancedRow> rows = advancedLayout();
        return rows.isEmpty() ? 0 : rows.get(rows.size() - 1).y() + TEXT_BUTTON_H;
    }

    /**
     * Advanced options of one face (2026-11-05): "输出范围" with an item row and a fluid row, then
     * "输入范围" with a fluid row; each row is a label followed by its button.
     */
    private void drawAdvanced(Canvas canvas, int cx, int cy, int mx, int my) {
        SideConfig.Face face = SideConfig.Face.values()[advancedFace];
        for (int[] section : advancedSections()) {
            canvas.text(Component.translatable(GuiText.key("io", section[0] == 0 ? "scope_out" : "scope_in")), cx, cy + section[1], 0xFFFFFFFF, true);
        }
        for (AdvancedRow row : advancedLayout()) {
            Component label = Component.translatable(GuiText.key("io", row.items() ? "label_items" : "label_fluids"));
            canvas.text(label, cx, cy + row.y() + 1, 0xFFE0E0E0, true);
            GuiLayout.Box button = scopeButton(cx, cy, row);
            Widgets.textButton(canvas, button.x(), button.y(), button.w(), button.h(), scopeText(face, row.option()), button.contains(mx, my), false);
        }
    }

    private GuiLayout.Box scopeButton(int cx, int cy, AdvancedRow row) {
        int x = cx + rowLabelWidth() + 2;
        return new GuiLayout.Box(x, cy + row.y(), cx + ioContentWidth() - x, TEXT_BUTTON_H);
    }

    /** Back button of the advanced options: top left of the panel, before the title (2026-11-05). */
    private static GuiLayout.Box backButton(PanelBox box) {
        return new GuiLayout.Box(box.inner() + 4, box.y + (GuiLayout.PANEL_HEADER - Widgets.SMALL) / 2, Widgets.SMALL, Widgets.SMALL);
    }

    private Component scopeText(SideConfig.Face face, int row) {
        MachineView view = view();
        if (row == 0) {
            String key = switch (SideConfig.OutputScope.values()[Math.floorMod(view.itemScopes[face.ordinal()], 3)]) {
                case ALL -> "scope_all";
                case MAIN -> "scope_main";
                case BYPRODUCT -> "scope_byproduct";
            };
            return Component.translatable(GuiText.key("io", key));
        }
        int tank = row == 1 ? view.fluidOutTanks[face.ordinal()] : view.fluidInTanks[face.ordinal()];
        return tank < 0 ? Component.translatable(GuiText.key("io", "scope_all"))
            : Component.translatable(GuiText.key("io", "scope_tank"), String.valueOf(tank + 1));
    }

    // ---- redstone panel ----

    private static final Widgets.SmallIcon[] REDSTONE_ICONS = {Widgets.SmallIcon.IGNORE, Widgets.SmallIcon.LOW, Widgets.SmallIcon.HIGH, Widgets.SmallIcon.DISABLED};

    private void drawRedstone(Canvas canvas, int cx, int cy, int mx, int my) {
        for (int i = 0; i < 4; i++) {
            GuiLayout.Box button = redstoneButton(cx, cy, i);
            Widgets.smallButton(canvas, button.x(), button.y(), REDSTONE_ICONS[i], button.contains(mx, my), view().redstone == i);
        }
        RedstoneControl current = RedstoneControl.byIndex(view().redstone);
        canvas.text(Component.translatable(GuiText.key("redstone", current.id())), cx, cy + Widgets.SMALL + 4, 0xFFFFFFFF, true);
    }

    private static GuiLayout.Box redstoneButton(int cx, int cy, int i) {
        return new GuiLayout.Box(cx + i * SMALL_PITCH, cy, Widgets.SMALL, Widgets.SMALL);
    }

    // ---- structure page ----

    private void drawStructure(Canvas canvas, int mx, int my) {
        MachineView view = view();
        int y = 20;
        canvas.text(Component.translatable(GuiText.key("structure", view.formed ? "formed" : "unformed")), 8, y,
            view.formed ? 0xFF2E7D32 : 0xFFB71C1C, false);
        y += 11;
        int rows = Math.max(1, (layout.statusY() - 6 - y) / 10);
        int shown = view.wrong.size() <= rows ? view.wrong.size() : rows - 1;
        for (int i = 0; i < shown; i++) {
            BlockPos pos = view.wrong.get(i);
            String expected = i < view.expected.size() ? view.expected.get(i) : "";
            Component line = Component.translatable(GuiText.key("structure", "wrong"),
                pos.getX() + ", " + pos.getY() + ", " + pos.getZ(), Component.translatable(expected));
            Widgets.fitText(canvas, line, 8, y, 140, Widgets.TEXT, false);
            y += 10;
        }
        if (view.wrong.size() > shown) {
            canvas.text(Component.translatable(GuiText.key("structure", "more"), String.valueOf(view.wrong.size() - shown)), 8, y, Widgets.TEXT, false);
        }
        canvas.text(Component.translatable(GuiText.key("structure", "ports"), String.valueOf(view.ports.size())), 8, layout.statusY(), Widgets.TEXT, false);
        if (!view.wrong.isEmpty()) {
            GuiLayout.Box box = highlightButton();
            Widgets.textButton(canvas, box.x(), box.y(), box.w(), box.h(),
                Component.translatable(GuiText.key("structure", "highlight")), box.contains(mx, my), false);
        }
    }

    private GuiLayout.Box highlightButton() {
        return new GuiLayout.Box(GuiLayout.WIDTH - 8 - 44, layout.statusY() - 4, 44, 14);
    }

    // ---- texts ----

    private Component statusText() {
        MachineView view = view();
        MachineStatus status = view.status;
        if (status == MachineStatus.MISSING) {
            MutableComponent conditions = Component.empty();
            String[] keys = view.missing.isEmpty() ? new String[0] : view.missing.split(",");
            for (int i = 0; i < keys.length; i++) {
                if (i > 0) {
                    conditions.append("、");
                }
                conditions.append(keys[i].equals("catalyst") ? Component.translatable(GuiText.key("status", "catalyst"))
                    : Component.translatable(Lang.condition(keys[i])));
            }
            return Component.translatable(GuiText.key("status", "missing"), conditions);
        }
        if (status == MachineStatus.NO_POWER && definition.power() == PowerKind.FUEL) {
            return Component.translatable(GuiText.key("status", "no_fuel"));
        }
        return Component.translatable(GuiText.key("status", status.id()));
    }

    private int statusColor() {
        return switch (view().status) {
            case RUNNING -> 0xFF2E7D32;
            case WAITING -> 0xFF505050;
            default -> 0xFFB71C1C;
        };
    }

    /** Purity of the planned main product: the percentage (short) or the full purity line. */
    @Nullable
    private Component productPurity(boolean shortForm) {
        MachineView view = view();
        String material;
        int b;
        if (!view.productItem.isEmpty() && Purity.bears(view.productItem.getItem()) && Purity.stored(view.productItem).isPresent()) {
            material = Purity.materialOf(view.productItem.getItem());
            b = Purity.stored(view.productItem).getAsInt();
        } else if (!view.productFluid.isEmpty() && view.productFluid.hasPurity() && Purity.bears(view.productFluid.fluid())) {
            material = Purity.materialOf(view.productFluid.fluid());
            b = view.productFluid.purity();
        } else {
            return null;
        }
        List<Component> lines = new ArrayList<>(1);
        Tooltips.purityLine(material, b, lines);
        if (lines.isEmpty()) {
            return null;
        }
        if (!shortForm) {
            return lines.get(0);
        }
        Integer color = lines.get(0).getStyle().getColor() == null ? null : lines.get(0).getStyle().getColor().getValue();
        MutableComponent percent = Component.literal(PurityScale.format(b) + "%");
        return color == null ? percent.withStyle(ChatFormatting.GRAY) : percent.withStyle(style -> style.withColor(color));
    }

    private List<Component> infoLines() {
        MachineView view = view();
        List<Component> lines = new ArrayList<>();
        Component product = !view.productItem.isEmpty() ? view.productItem.getHoverName()
            : !view.productFluid.isEmpty() ? PlatformFluids.displayName(view.productFluid) : null;
        if (product == null) {
            lines.add(Component.translatable(GuiText.key("info", "recipe"), Component.translatable(GuiText.key("info", "none"))));
        } else {
            lines.add(Component.translatable(GuiText.key("info", "recipe"), ""));
            lines.add(Component.literal("  ").append(product));
        }
        if (view.total > 0) {
            lines.add(Component.translatable(GuiText.key("info", "progress"), String.valueOf(view.progress), String.valueOf(view.total)));
            if (view.rate > 0) {
                double seconds = (view.total - view.progress) * 1000.0 / view.rate / 20.0;
                lines.add(Component.translatable(GuiText.key("info", "remaining"), String.format(Locale.ROOT, "%.1f", Math.max(0, seconds))));
            }
        }
        lines.add(Component.translatable(GuiText.key("info", "speed"), String.format(Locale.ROOT, "%.2f", view.rate / 1000.0)));
        if (definition.power() == PowerKind.ENERGY && view.total > 0) {
            lines.add(Component.translatable(GuiText.key("info", "energy"), String.valueOf(view.energyPerTick)));
        }
        Component purity = productPurity(false);
        if (purity != null) {
            lines.add(Component.translatable(GuiText.key("info", "purity"), ""));
            lines.add(Component.literal("  ").append(purity));
        }
        if (view.total > 0) {
            lines.add(Component.translatable(GuiText.key("info", "yield"), String.valueOf(view.yieldPermille / 10)));
        }
        List<Component> capabilities = capabilityNames(view.capabilities);
        if (!capabilities.isEmpty()) {
            lines.add(Component.translatable(GuiText.key("info", "capabilities")));
            lines.addAll(capabilities);
        }
        return lines;
    }

    private List<Component> upgradeLines() {
        MachineView view = view();
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(GuiText.key("upgrades", "speed"), String.format(Locale.ROOT, "%.2f", view.speedPermille / 1000.0)));
        lines.add(Component.translatable(GuiText.key("upgrades", "energy"), String.format(Locale.ROOT, "%.2f", view.energyPermille / 1000.0)));
        lines.add(Component.translatable(GuiText.key("upgrades", "parallel"), String.valueOf(view.parallel)));
        List<Component> added = capabilityNames(view.moduleCapabilities);
        if (!added.isEmpty()) {
            lines.add(Component.translatable(GuiText.key("upgrades", "adds")));
            lines.addAll(added);
        }
        return lines;
    }

    private static List<Component> capabilityNames(List<String> keys) {
        List<Component> names = new ArrayList<>();
        for (String key : keys) {
            if (!key.startsWith("mode.")) {
                names.add(Component.literal("· ").append(Component.translatable(Lang.condition(key))));
            }
        }
        return names;
    }

    /** Side panels in screen coordinates (x, y, w, h), for other mods' overlays (JEI extra areas, I22). */
    @Override
    public List<int[]> extraAreas() {
        List<int[]> areas = new ArrayList<>();
        for (PanelBox box : panelBoxes()) {
            areas.add(new int[]{leftPos + box.x, topPos + box.y, box.w, box.h});
        }
        return areas;
    }

    // ---- tooltips ----

    @Override
    protected void drawOverlay(Canvas canvas, int mouseX, int mouseY) {
        int mx = mouseX - leftPos;
        int my = mouseY - topPos;
        List<Component> lines = tooltip(mx, my);
        if (!lines.isEmpty()) {
            canvas.tooltip(lines, mouseX, mouseY);
        }
    }

    private List<Component> tooltip(int mx, int my) {
        List<Component> lines = new ArrayList<>();
        MachineView view = view();
        for (PanelBox box : panelBoxes()) {
            if (box.contains(mx, my)) {
                panelTooltip(box, mx, my, lines);
                return lines;
            }
        }
        if (!definition.modes().isEmpty() && modeButton().contains(mx, my)) {
            MachineMode mode = definition.modes().get(Math.floorMod(view.mode, definition.modes().size()));
            lines.add(Component.translatable(GuiText.key("mode", "current"), Component.translatable(Lang.condition("mode." + mode.id()))));
            return lines;
        }
        if (definition.isMultiblock() && structureButton().contains(mx, my)) {
            lines.add(Component.translatable(GuiText.key("structure", structurePage() ? "main" : "tab")));
            return lines;
        }
        if (structurePage()) {
            return lines;
        }
        GuiLayout.Box energy = layout.energy();
        if (energy != null && energy.contains(mx, my)) {
            lines.add(Component.translatable(GuiText.key("energy", "amount"), String.valueOf(view.energy), String.valueOf(view.energyCapacity)));
            return lines;
        }
        GuiLayout.Box flame = layout.flame();
        if (flame != null && flame.contains(mx, my)) {
            lines.add(view.burnLeft > 0
                ? Component.translatable(GuiText.key("fuel", "burning"), String.format(Locale.ROOT, "%.1f", view.burnLeft / 20.0))
                : Component.translatable(GuiText.key("fuel", "out")));
            for (String key : view.capabilities) {
                if (key.startsWith("temperature.")) {
                    lines.add(Component.translatable(Lang.condition(key)).withStyle(ChatFormatting.GRAY));
                }
            }
            return lines;
        }
        for (int i = 0; i < definition.layout().tanks(); i++) {
            if (layout.tank(i).contains(mx, my)) {
                tankTooltip(i, lines);
                return lines;
            }
            GuiLayout.Box vent = layout.vent(i);
            if (vent != null && vent.contains(mx, my)) {
                int mode = i < view.tankVents.length ? view.tankVents[i] : 0;
                lines.add(Component.translatable(GuiText.key("vent", "title"),
                    Component.translatable(GuiText.key("vent", VentMode.byIndex(mode).id()))));
                lines.add(Component.translatable(GuiText.key("vent", "gas_only")).withStyle(ChatFormatting.GRAY));
                return lines;
            }
        }
        GuiLayout.Box arrow = layout.arrow();
        if (arrow.contains(mx, my) && view.total > 0) {
            lines.add(Component.translatable(GuiText.key("info", "progress"), String.valueOf(view.progress), String.valueOf(view.total)));
            return lines;
        }
        Component purity = layout.purity().contains(mx, my) ? productPurity(false) : null;
        if (purity != null) {
            lines.add(Component.translatable(GuiText.key("info", "purity"), ""));
            lines.add(Component.literal("  ").append(purity));
            return lines;
        }
        Slot slot = hoveredSlot();
        if (slot != null && !slot.hasItem() && slot.index < definition.layout().itemSlots()) {
            ItemStack lock = slot.index < view.slotLocks.length ? view.slotLocks[slot.index] : ItemStack.EMPTY;
            if (!lock.isEmpty()) {
                lines.add(Component.translatable(GuiText.key("slot", "locked"), lock.getHoverName()));
            }
        }
        return lines;
    }

    private void tankTooltip(int tank, List<Component> lines) {
        MachineView view = view();
        FluidAmount fluid = tank < view.tanks.length ? view.tanks[tank] : FluidAmount.EMPTY;
        if (fluid.isEmpty()) {
            lines.add(Component.translatable(GuiText.key("tank", "empty")));
        } else {
            lines.add(PlatformFluids.displayName(fluid));
            if (Purity.bears(fluid.fluid())) {
                Tooltips.purityLine(Purity.materialOf(fluid.fluid()), Purity.get(fluid), lines);
            }
        }
        lines.add(Component.translatable(GuiText.key("tank", "amount"), String.valueOf(fluid.amount()), String.valueOf(view.tankCapacity))
            .withStyle(ChatFormatting.GRAY));
        FluidAmount lock = tank < view.tankLocks.length ? view.tankLocks[tank] : FluidAmount.EMPTY;
        if (!lock.isEmpty()) {
            lines.add(Component.translatable(GuiText.key("tank", "locked"), PlatformFluids.displayName(lock)).withStyle(ChatFormatting.GOLD));
        }
        if (tank == dumpTank && System.currentTimeMillis() < dumpUntil) {
            lines.add(Component.translatable(GuiText.key("tank", "dump")).withStyle(ChatFormatting.RED));
        }
    }

    private void panelTooltip(PanelBox box, int mx, int my, List<Component> lines) {
        if (box.panel == Panel.IO && box.open && advancedFace >= 0 && backButton(box).contains(mx, my)) {
            lines.add(Component.translatable(GuiText.key("io", "back_to_faces")));
            return;
        }
        if (box.onHeader(mx, my)) {
            lines.add(Component.translatable(GuiText.key("panel", box.panel.key)));
            return;
        }
        int cx = box.inner() + 5;
        int cy = box.y + GuiLayout.PANEL_HEADER;
        MachineView view = view();
        if (box.panel == Panel.IO && advancedFace < 0) {
            for (SideConfig.Face face : SideConfig.Face.values()) {
                if (faceCell(cx, cy, face).contains(mx, my)) {
                    lines.add(Component.translatable(GuiText.key("io", face.name().toLowerCase(Locale.ROOT))));
                    ItemStack neighbour = neighbour(face);
                    if (!neighbour.isEmpty()) {
                        lines.add(Component.translatable(GuiText.key("io", "neighbour"), neighbour.getHoverName()).withStyle(ChatFormatting.GRAY));
                    }
                    if (hasItems()) {
                        lines.add(Component.translatable(GuiText.key("io", "items"), modeName(view.sideItems[face.ordinal()])).withStyle(ChatFormatting.GRAY));
                    }
                    if (hasFluids()) {
                        lines.add(Component.translatable(GuiText.key("io", "fluids"), modeName(view.sideFluids[face.ordinal()])).withStyle(ChatFormatting.GRAY));
                    }
                    if (!advancedRows().isEmpty()) {
                        lines.add(Component.translatable(GuiText.key("io", "advanced")).withStyle(ChatFormatting.DARK_GRAY));
                    }
                    return;
                }
            }
            for (int i = 0; i < 4; i++) {
                if (hasAuto(i) && autoButton(cx, cy, i).contains(mx, my)) {
                    boolean on = (view.auto & (1 << i)) != 0;
                    lines.add(Component.translatable(GuiText.key("io", AUTO_KEYS[i]), Component.translatable(GuiText.key("io", on ? "on" : "switched_off"))));
                    return;
                }
            }
        }
        if (box.panel == Panel.REDSTONE) {
            for (int i = 0; i < 4; i++) {
                if (redstoneButton(cx, cy, i).contains(mx, my)) {
                    lines.add(Component.translatable(GuiText.key("redstone", RedstoneControl.byIndex(i).id())));
                    return;
                }
            }
        }
    }

    private static Component modeName(int mode) {
        String key = switch (SideConfig.Mode.values()[Math.floorMod(mode, 4)]) {
            case OFF -> "off";
            case INPUT -> "input";
            case OUTPUT -> "output";
            case BOTH -> "both";
        };
        return Component.translatable(GuiText.key("io", key));
    }

    @Override
    protected void appendSlotTooltip(Slot slot, List<Component> lines) {
        if (slot.index >= menu.machineSlots()) {
            return;
        }
        IoLayout io = definition.layout();
        SlotRole role = io.roleOf(slot.index);
        MachineView view = view();
        if (role == SlotRole.CATALYST) {
            int index = slot.index - io.start(SlotRole.CATALYST);
            if (index < view.catalystLeft.length && view.catalystLeft[index] > 0) {
                lines.add(Component.translatable(GuiText.key("slot", "uses"), String.valueOf(view.catalystLeft[index])).withStyle(ChatFormatting.GRAY));
            }
        } else if (role == SlotRole.INPUT && slot.index < view.slotLocks.length && !view.slotLocks[slot.index].isEmpty()) {
            lines.add(Component.translatable(GuiText.key("slot", "locked"), view.slotLocks[slot.index].getHoverName()).withStyle(ChatFormatting.GOLD));
        }
    }

    // ---- input ----

    @Override
    protected boolean onClick(double mouseX, double mouseY, int button) {
        double mx = mouseX - leftPos;
        double my = mouseY - topPos;
        for (PanelBox box : panelBoxes()) {
            if (box.contains(mx, my)) {
                return clickPanel(box, mx, my, button);
            }
        }
        if (!definition.modes().isEmpty() && modeButton().contains(mx, my)) {
            int size = definition.modes().size();
            menu.send(MenuActions.MODE, Math.floorMod(view().mode + (button == 1 ? -1 : 1), size), 0);
            return true;
        }
        if (definition.isMultiblock() && structureButton().contains(mx, my)) {
            STRUCTURE_PAGE.put(definition.id(), !structurePage());
            syncMenuState();
            return true;
        }
        if (structurePage()) {
            if (!view().wrong.isEmpty() && highlightButton().contains(mx, my)) {
                StructureHighlight.show(view().wrong);
                return true;
            }
            return false;
        }
        for (int i = 0; i < definition.layout().tanks(); i++) {
            if (layout.tank(i).contains(mx, my)) {
                clickTank(i, button);
                return true;
            }
            GuiLayout.Box vent = layout.vent(i);
            if (vent != null && vent.contains(mx, my)) {
                int mode = i < view().tankVents.length ? view().tankVents[i] : 0;
                menu.send(MenuActions.VENT, i, VentMode.byIndex(mode).cycle(button != 1).ordinal());
                return true;
            }
        }
        if (Platform.isAltDown()) {
            Slot slot = hoveredSlot();
            if (slot != null && slot.index < menu.machineSlots() && definition.layout().roleOf(slot.index) == SlotRole.INPUT) {
                menu.send(MenuActions.LOCK_SLOT, slot.index, 0);
                return true;
            }
        }
        return false;
    }

    private void clickTank(int tank, int button) {
        if (Platform.isAltDown()) {
            menu.send(MenuActions.LOCK_TANK, tank, 0);
        } else if (button == 1 && Platform.isShiftDown()) {
            long now = System.currentTimeMillis();
            if (dumpTank == tank && now < dumpUntil) {
                menu.send(MenuActions.TANK_DUMP, tank, 0);
                dumpTank = -1;
            } else {
                dumpTank = tank;
                dumpUntil = now + DUMP_CONFIRM_MS;
            }
        } else if (!menu.getCarried().isEmpty()) {
            menu.send(MenuActions.TANK_CLICK, tank, 0);
        }
    }

    private boolean clickPanel(PanelBox box, double mx, double my, int button) {
        if (box.panel == Panel.IO && box.open && advancedFace >= 0 && backButton(box).contains(mx, my)) {
            advancedFace = -1;
            return true;
        }
        if (box.onHeader(mx, my)) {
            toggle(box.panel);
            return true;
        }
        int cx = box.inner() + 5;
        int cy = box.y + GuiLayout.PANEL_HEADER;
        switch (box.panel) {
            case IO -> clickIo(cx, cy, mx, my, button);
            case REDSTONE -> {
                for (int i = 0; i < 4; i++) {
                    if (redstoneButton(cx, cy, i).contains(mx, my)) {
                        menu.send(MenuActions.REDSTONE, i, 0);
                    }
                }
            }
            case UPGRADES -> {
                return false;
            }
            default -> {
            }
        }
        return true;
    }

    private void clickIo(int cx, int cy, double mx, double my, int button) {
        MachineView view = view();
        if (advancedFace >= 0) {
            SideConfig.Face face = SideConfig.Face.values()[advancedFace];
            for (AdvancedRow row : advancedLayout()) {
                if (scopeButton(cx, cy, row).contains(mx, my)) {
                    boolean forwards = button != 1;
                    if (row.option() == 0) {
                        int next = Math.floorMod(view.itemScopes[face.ordinal()] + (forwards ? 1 : -1), 3);
                        menu.send(MenuActions.SCOPE_ITEMS, face.ordinal(), next);
                    } else {
                        boolean out = row.option() == 1;
                        int tanks = out ? definition.layout().fluidOut() : definition.layout().fluidIn();
                        int current = out ? view.fluidOutTanks[face.ordinal()] : view.fluidInTanks[face.ordinal()];
                        int next = Math.floorMod(current + 1 + (forwards ? 1 : -1), tanks + 1) - 1;
                        menu.send(out ? MenuActions.SCOPE_FLUID_OUT : MenuActions.SCOPE_FLUID_IN, face.ordinal(), next);
                    }
                }
            }
            return;
        }
        for (SideConfig.Face face : SideConfig.Face.values()) {
            GuiLayout.Box cell = faceCell(cx, cy, face);
            if (cell.contains(mx, my)) {
                if (Platform.isAltDown()) {
                    if (!advancedRows().isEmpty()) {
                        advancedFace = face.ordinal();
                    }
                    return;
                }
                boolean fluid = my >= cell.y() + CELL / 2;
                if (fluid ? !hasFluids() : !hasItems()) {
                    return;
                }
                int current = fluid ? view.sideFluids[face.ordinal()] : view.sideItems[face.ordinal()];
                SideConfig.Mode next = Platform.isShiftDown() && button == 0 ? SideConfig.Mode.OFF
                    : SideConfig.Mode.values()[Math.floorMod(current, 4)].cycle(button != 1);
                menu.send(MenuActions.SIDE, face.ordinal(), (fluid ? 16 : 0) | next.ordinal());
                return;
            }
        }
        for (int i = 0; i < 4; i++) {
            if (hasAuto(i) && autoButton(cx, cy, i).contains(mx, my)) {
                boolean on = (view.auto & (1 << i)) != 0;
                menu.send(MenuActions.AUTO, i, on ? 0 : 1);
                return;
            }
        }
    }
}
