package com.tonywww.foodindustrial.gui.client;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.gui.GuiLayout;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.gui.MachineView;
import com.tonywww.foodindustrial.machine.bio.ColonyLook;
import com.tonywww.foodindustrial.machine.bio.CultureBenchBlockEntity;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The culture plate of the M30 window (I17, additives/06 §T4 生物 / 精细：操作与实现规格): a petri dish
 * in the processing zone with the colonies, and four buttons beside it (inoculate, identify, pick,
 * discard). Colonies show their fixed look (colour and patches); names only once identified.
 */
final class PlateWidget {
    static final int BUTTONS = 4;
    private static final int BUTTON = 12;
    private static final int COLONY = 10;
    private static final int RIM = 0xFFB4BCC4;
    private static final int AGAR = 0xFFE6D9A2;
    private static final int GROWING = 0xC0FFFFFF;
    private static final String[] BUTTON_KEYS = {"inoculate", "identify", "pick", "discard"};

    private final GuiLayout.Box zone;

    /** @param frame the processing zone's frame */
    PlateWidget(GuiLayout.Box frame) {
        int pad = GuiLayout.ZONE_PAD;
        this.zone = new GuiLayout.Box(frame.x() + pad, frame.y() + pad, frame.w() - 2 * pad, frame.h() - 2 * pad);
    }

    /** The dish: the largest circle left of the buttons. */
    GuiLayout.Box dish() {
        int size = Math.min(zone.h(), zone.w() - BUTTON - 2);
        int x = zone.x() + (zone.w() - BUTTON - 2 - size) / 2;
        int y = zone.y() + (zone.h() - size) / 2;
        return new GuiLayout.Box(x, y, size, size);
    }

    GuiLayout.Box button(int index) {
        int gap = Math.max(1, (zone.h() - BUTTONS * BUTTON) / (BUTTONS - 1));
        int total = BUTTONS * BUTTON + (BUTTONS - 1) * gap;
        return new GuiLayout.Box(zone.x() + zone.w() - BUTTON, zone.y() + (zone.h() - total) / 2 + index * (BUTTON + gap), BUTTON, BUTTON);
    }

    /** Colony {@code index} of {@code count}: on a ring around the dish centre (one colony in the middle). */
    GuiLayout.Box colony(int index, int count) {
        GuiLayout.Box dish = dish();
        int cx = dish.x() + dish.w() / 2;
        int cy = dish.y() + dish.h() / 2;
        if (count <= 1) {
            return new GuiLayout.Box(cx - COLONY / 2, cy - COLONY / 2, COLONY, COLONY);
        }
        double radius = dish.w() * 0.27;
        double angle = -Math.PI / 2 + 2 * Math.PI * index / count + Math.PI / count;
        int x = (int) Math.round(cx + radius * Math.cos(angle)) - COLONY / 2;
        int y = (int) Math.round(cy + radius * Math.sin(angle)) - COLONY / 2;
        return new GuiLayout.Box(x, y, COLONY, COLONY);
    }

    /** Whether button {@code index} can be used now. */
    static boolean enabled(MachineView view, int index, int selected) {
        boolean plate = view.plateStage == CultureBenchBlockEntity.Stage.PLATE.ordinal();
        return switch (index) {
            case CultureBenchBlockEntity.INOCULATE -> view.canInoculate;
            case CultureBenchBlockEntity.IDENTIFY -> plate && !view.identified && view.colonyLooks.length > 0;
            case CultureBenchBlockEntity.PICK -> plate && selected >= 0 && selected < view.colonyLooks.length;
            default -> plate;
        };
    }

    void draw(Canvas canvas, MachineView view, int selected, int mx, int my) {
        GuiLayout.Box dish = dish();
        disc(canvas, dish.x(), dish.y(), dish.w(), RIM);
        disc(canvas, dish.x() + 2, dish.y() + 2, dish.w() - 4, AGAR);
        int stage = view.plateStage;
        if (stage == CultureBenchBlockEntity.Stage.INCUBATING.ordinal()) {
            // colonies growing: pale dots that swell with the progress
            int size = 2 + (view.total > 0 ? (int) Math.min(COLONY - 2, (long) view.progress * (COLONY - 2) / view.total) : 0);
            for (int i = 0; i < 4; i++) {
                GuiLayout.Box box = colony(i, 4);
                disc(canvas, box.x() + (COLONY - size) / 2, box.y() + (COLONY - size) / 2, size, GROWING);
            }
        } else {
            for (int i = 0; i < view.colonyLooks.length; i++) {
                GuiLayout.Box box = colony(i, view.colonyLooks.length);
                drawColony(canvas, box.x(), box.y(), view.colonyLooks[i]);
                if (i == selected) {
                    canvas.outline(box.x() - 2, box.y() - 2, COLONY + 4, COLONY + 4, 0xFFFFFFFF);
                }
            }
        }
        for (int i = 0; i < BUTTONS; i++) {
            GuiLayout.Box box = button(i);
            boolean on = enabled(view, i, selected);
            Widgets.button(canvas, box.x(), box.y(), box.w(), box.h(), on && box.contains(mx, my), false);
            glyph(canvas, i, box.x() + 2, box.y() + 2, on ? 0xFF202020 : 0xFF6E6E6E);
        }
    }

    /** Index of the colony at the mouse, or -1. */
    int colonyAt(MachineView view, double mx, double my) {
        if (view.plateStage != CultureBenchBlockEntity.Stage.PLATE.ordinal() && view.plateStage != CultureBenchBlockEntity.Stage.IDENTIFYING.ordinal()) {
            return -1;
        }
        for (int i = 0; i < view.colonyLooks.length; i++) {
            GuiLayout.Box box = colony(i, view.colonyLooks.length);
            if (mx >= box.x() - 1 && mx < box.x() + COLONY + 1 && my >= box.y() - 1 && my < box.y() + COLONY + 1) {
                return i;
            }
        }
        return -1;
    }

    int buttonAt(double mx, double my) {
        for (int i = 0; i < BUTTONS; i++) {
            if (button(i).contains(mx, my)) {
                return i;
            }
        }
        return -1;
    }

    static void buttonTooltip(int index, List<Component> lines) {
        lines.add(Component.translatable(GuiText.key("plate", BUTTON_KEYS[index])));
    }

    static void colonyTooltip(MachineView view, int index, List<Component> lines) {
        ItemStack stack = index < view.colonyItems.length ? view.colonyItems[index] : ItemStack.EMPTY;
        if (!view.identified || stack.isEmpty()) {
            lines.add(Component.translatable(GuiText.key("plate", "unidentified")));
            return;
        }
        lines.add(stack.getHoverName());
        int b = Purity.stored(stack).orElse(Purity.NONE);
        if (b != Purity.NONE) {
            lines.add(Component.literal(PurityScale.format(b) + "%").withStyle(ChatFormatting.GRAY));
        }
    }

    // ---- drawing ----

    /** A filled circle of diameter {@code size} with its box at ({@code x}, {@code y}). */
    static void disc(Canvas canvas, int x, int y, int size, int argb) {
        double r = size / 2.0;
        for (int row = 0; row < size; row++) {
            double dy = row + 0.5 - r;
            int half = (int) Math.round(Math.sqrt(Math.max(0, r * r - dy * dy)));
            if (half > 0) {
                canvas.fill(x + (int) Math.round(r) - half, y + row, x + (int) Math.round(r) + half, y + row + 1, argb);
            }
        }
    }

    private static int shade(int argb, double factor) {
        int r = (int) Math.min(255, ((argb >> 16) & 0xFF) * factor);
        int g = (int) Math.min(255, ((argb >> 8) & 0xFF) * factor);
        int b = (int) Math.min(255, (argb & 0xFF) * factor);
        return (argb & 0xFF000000) | r << 16 | g << 8 | b;
    }

    private static void dot(Canvas canvas, int x, int y, int argb) {
        canvas.fill(x, y, x + 1, y + 1, argb);
    }

    /** A colony of look {@code look}: its colour and one of eight patch patterns ({@link ColonyLook}). */
    static void drawColony(Canvas canvas, int x, int y, int look) {
        int color = ColonyLook.color(look);
        int dark = shade(color, 0.6);
        int light = shade(color, 1.3);
        if (ColonyLook.pattern(look) == 6) {
            // fuzzy edge: hairs around the colony
            for (int[] p : new int[][]{{4, -1}, {9, 1}, {10, 5}, {8, 9}, {4, 10}, {0, 8}, {-1, 4}, {1, 1}}) {
                dot(canvas, x + p[0], y + p[1], dark);
            }
        }
        disc(canvas, x, y, COLONY, color);
        switch (ColonyLook.pattern(look)) {
            case 1 -> canvas.fill(x + 4, y + 4, x + 6, y + 6, dark);
            case 2 -> {
                disc(canvas, x + 2, y + 2, 6, dark);
                disc(canvas, x + 3, y + 3, 4, light);
            }
            case 3 -> {
                for (int[] p : new int[][]{{2, 3}, {6, 2}, {3, 6}, {7, 6}, {5, 4}}) {
                    dot(canvas, x + p[0], y + p[1], dark);
                }
            }
            case 4 -> {
                canvas.fill(x + 2, y + 4, x + 8, y + 5, dark);
                canvas.fill(x + 4, y + 2, x + 5, y + 8, dark);
                dot(canvas, x + 6, y + 6, dark);
            }
            case 5 -> {
                for (int i = 1; i < 9; i += 2) {
                    dot(canvas, x + i, y + i, light);
                    dot(canvas, x + 9 - i, y + i, light);
                }
            }
            case 7 -> {
                disc(canvas, x + 2, y + 2, 6, light);
                disc(canvas, x + 4, y + 4, 2, dark);
            }
            default -> {
            }
        }
    }

    /** The 8 × 8 glyph of button {@code index}: plus, magnifier, tweezers (up arrow), cross. */
    private static void glyph(Canvas canvas, int index, int x, int y, int argb) {
        switch (index) {
            case CultureBenchBlockEntity.INOCULATE -> {
                canvas.fill(x + 3, y, x + 5, y + 8, argb);
                canvas.fill(x, y + 3, x + 8, y + 5, argb);
            }
            case CultureBenchBlockEntity.IDENTIFY -> {
                canvas.outline(x, y, 6, 6, argb);
                canvas.fill(x + 5, y + 5, x + 8, y + 8, argb);
            }
            case CultureBenchBlockEntity.PICK -> {
                canvas.fill(x + 3, y + 2, x + 5, y + 8, argb);
                canvas.fill(x + 1, y + 3, x + 7, y + 4, argb);
                canvas.fill(x + 2, y + 2, x + 6, y + 3, argb);
                canvas.fill(x + 3, y + 1, x + 5, y + 2, argb);
            }
            default -> {
                for (int i = 0; i < 8; i++) {
                    canvas.fill(x + i, y + i, x + i + 1, y + i + 1, argb);
                    canvas.fill(x + 7 - i, y + i, x + 8 - i, y + i + 1, argb);
                }
            }
        }
    }
}
