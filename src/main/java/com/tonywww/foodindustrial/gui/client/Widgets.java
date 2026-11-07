package com.tonywww.foodindustrial.gui.client;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.gui.GuiLayout;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;
import net.minecraft.network.chat.Component;

/**
 * Drawing of the screen elements from the placeholder textures of {@code tools/gui/gui_textures.py}
 * (machines/04, 2026-11-05): windows and panels are cut from 256 × 256 frames into four corners, so
 * any size up to 256 works; coordinates of {@code elements.png} are mirrored from the script.
 */
final class Widgets {
    static final String WINDOW = FoodIndustrial.MOD_ID + ":textures/gui/window.png";
    static final String ELEMENTS = FoodIndustrial.MOD_ID + ":textures/gui/elements.png";
    private static final int SIZE = 256;

    /** Panel colours (frame textures). */
    enum Color {
        BLUE, GREEN, ORANGE, RED;

        String texture() {
            return FoodIndustrial.MOD_ID + ":textures/gui/panel_" + name().toLowerCase(java.util.Locale.ROOT) + ".png";
        }
    }

    /** 16 × 16 icons in {@code elements.png} row 100: left panels and the top buttons (the right panels use item icons). */
    enum Icon {
        INFO(0, 100), UPGRADES(16, 100), STRUCTURE(64, 100), MODE(80, 100), HIGHLIGHT(96, 100);

        final int u;
        final int v;

        Icon(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    /** 8 × 8 icons of the small panel buttons in {@code elements.png} row 132 (2026-11-05). */
    enum SmallIcon {
        IGNORE, LOW, HIGH, DISABLED, IN_ITEMS, IN_FLUIDS, OUT_ITEMS, OUT_FLUIDS, BACK;

        int u() {
            return ordinal() * 8;
        }
    }

    /** Size of the small panel buttons (redstone and input/output panels). */
    static final int SMALL = 10;

    /** Slot frame variants in {@code elements.png} row 0. */
    enum SlotKind {
        NORMAL(0), CATALYST(18), MAIN(36), UPGRADE(54);

        final int u;

        SlotKind(int u) {
            this.u = u;
        }
    }

    static final int TEXT = 0xFF404040;
    static final int TEXT_LIGHT = 0xFFFFFFFF;
    static final int CHANNEL_OFF = 0xFF8B8B8B;
    static final int CHANNEL_INPUT = 0xFF3F76E4;
    static final int CHANNEL_OUTPUT = 0xFFE07B27;
    static final int CHANNEL_NONE = 0xFF555555;

    private Widgets() {
    }

    /** A 256 × 256 frame texture drawn at any size up to 256: four corners. */
    static void frame(Canvas canvas, String texture, int x, int y, int w, int h) {
        int corner = 4;
        canvas.blit(texture, x, y, 0, 0, w - corner, h - corner, SIZE, SIZE);
        canvas.blit(texture, x + w - corner, y, SIZE - corner, 0, corner, h - corner, SIZE, SIZE);
        canvas.blit(texture, x, y + h - corner, 0, SIZE - corner, w - corner, corner, SIZE, SIZE);
        canvas.blit(texture, x + w - corner, y + h - corner, SIZE - corner, SIZE - corner, corner, corner, SIZE, SIZE);
    }

    static void window(Canvas canvas, int x, int y, int w, int h) {
        frame(canvas, WINDOW, x, y, w, h);
    }

    static void panel(Canvas canvas, Color color, int x, int y, int w, int h) {
        frame(canvas, color.texture(), x, y, w, h);
    }

    static void slot(Canvas canvas, SlotKind kind, int x, int y) {
        canvas.blit(ELEMENTS, x, y, kind.u, 0, 18, 18, SIZE, SIZE);
    }

    static void icon(Canvas canvas, Icon icon, int x, int y) {
        canvas.blit(ELEMENTS, x, y, icon.u, icon.v, 16, 16, SIZE, SIZE);
    }

    static void lock(Canvas canvas, int x, int y) {
        canvas.blit(ELEMENTS, x, y, 160, 0, 7, 9, SIZE, SIZE);
    }

    /** 8 × 8 icon of a venting switch ({@code VentMode} ordinal). */
    static void vent(Canvas canvas, int mode, int x, int y) {
        canvas.blit(ELEMENTS, x, y, 176 + 8 * Math.floorMod(mode, 3), 0, 8, 8, SIZE, SIZE);
    }

    static void gas(Canvas canvas, int x, int y) {
        canvas.blit(ELEMENTS, x, y, 168, 0, 8, 8, SIZE, SIZE);
    }

    /** Progress arrow, {@code done} of 24 pixels filled. */
    static void arrow(Canvas canvas, int x, int y, int done) {
        canvas.blit(ELEMENTS, x, y, 80, 0, GuiLayout.ARROW_W, GuiLayout.ARROW_H, SIZE, SIZE);
        if (done > 0) {
            canvas.blit(ELEMENTS, x, y, 104, 0, Math.min(GuiLayout.ARROW_W, done), GuiLayout.ARROW_H, SIZE, SIZE);
        }
    }

    /** Fuel flame, {@code lit} of 14 pixels from the bottom. */
    static void flame(Canvas canvas, int x, int y, int lit) {
        canvas.blit(ELEMENTS, x, y, 128, 0, 14, 14, SIZE, SIZE);
        if (lit > 0) {
            int h = Math.min(14, lit);
            canvas.blit(ELEMENTS, x, y + 14 - h, 142, 14 - h, 14, h, SIZE, SIZE);
        }
    }

    /** Recessed frame of a tank or bar (cut from the 18 × 64 or 14 × 64 frames). */
    private static void inset(Canvas canvas, int u, int frameW, int x, int y, int w, int h) {
        int corner = 2;
        canvas.blit(ELEMENTS, x, y, u, 32, w - corner, h - corner, SIZE, SIZE);
        canvas.blit(ELEMENTS, x + w - corner, y, u + frameW - corner, 32, corner, h - corner, SIZE, SIZE);
        canvas.blit(ELEMENTS, x, y + h - corner, u, 32 + 64 - corner, w - corner, corner, SIZE, SIZE);
        canvas.blit(ELEMENTS, x + w - corner, y + h - corner, u + frameW - corner, 32 + 64 - corner, corner, corner, SIZE, SIZE);
    }

    /** Tank with its fluid from the bottom and gauge marks. */
    static void tank(Canvas canvas, int x, int y, int w, int h, FluidAmount fluid, int capacity) {
        inset(canvas, 0, 18, x, y, w, h);
        if (!fluid.isEmpty() && capacity > 0) {
            int inner = h - 2;
            int filled = (int) Math.max(1, Math.min(inner, (long) inner * fluid.amount() / capacity));
            canvas.fluid(fluid, x + 1, y + 1 + inner - filled, w - 2, filled);
        }
        for (int i = 1; i < 4; i++) {
            int my = y + 1 + (h - 2) * i / 4;
            canvas.fill(x + 1, my, x + 1 + (i == 2 ? 5 : 3), my + 1, 0x90FFFFFF);
        }
    }

    /** Energy bar filled {@code value} of {@code max} from the bottom. */
    static void energy(Canvas canvas, int x, int y, int w, int h, long value, long max) {
        inset(canvas, 36, 14, x, y, w, h);
        if (value > 0 && max > 0) {
            int inner = h - 2;
            int filled = (int) Math.max(1, Math.min(inner, inner * value / max));
            canvas.blit(ELEMENTS, x + 1, y + 1 + inner - filled, 50, 32 + 64 - filled, w - 2, filled, SIZE, SIZE);
        }
    }

    /** Colour of the zone frames of the machine area. */
    static final int ZONE_FRAME = 0xFF8B8B8B;

    /** 1-pixel frame around a zone of the machine area (machines/04 §1, 2026-11-05). */
    static void zoneFrame(Canvas canvas, GuiLayout.Box frame) {
        canvas.outline(frame.x(), frame.y(), frame.w(), frame.h(), ZONE_FRAME);
    }

    /** Button background (normal, hovered or selected) cut from the 120 × 20 frames. */
    static void button(Canvas canvas, int x, int y, int w, int h, boolean hovered, boolean selected) {
        int u = selected ? 0 : hovered ? 120 : 0;
        int v = selected ? 180 : 160;
        int corner = 2;
        canvas.blit(ELEMENTS, x, y, u, v, w - corner, h - corner, SIZE, SIZE);
        canvas.blit(ELEMENTS, x + w - corner, y, u + 120 - corner, v, corner, h - corner, SIZE, SIZE);
        canvas.blit(ELEMENTS, x, y + h - corner, u, v + 20 - corner, w - corner, corner, SIZE, SIZE);
        canvas.blit(ELEMENTS, x + w - corner, y + h - corner, u + 120 - corner, v + 20 - corner, corner, corner, SIZE, SIZE);
    }

    /** A button with centred text. */
    static void textButton(Canvas canvas, int x, int y, int w, int h, Component text, boolean hovered, boolean selected) {
        button(canvas, x, y, w, h, hovered, selected);
        canvas.text(text, x + (w - canvas.width(text)) / 2, y + (h - 8) / 2, TEXT_LIGHT, true);
    }

    /** A button with a centred 16 × 16 icon. */
    static void iconButton(Canvas canvas, int x, int y, int size, Icon icon, boolean hovered, boolean selected) {
        button(canvas, x, y, size, size, hovered, selected);
        icon(canvas, icon, x + (size - 16) / 2, y + (size - 16) / 2);
    }

    /** A {@link #SMALL} button with a centred 8 × 8 icon. */
    static void smallButton(Canvas canvas, int x, int y, SmallIcon icon, boolean hovered, boolean selected) {
        button(canvas, x, y, SMALL, SMALL, hovered, selected);
        canvas.blit(ELEMENTS, x + 1, y + 1, icon.u(), 132, 8, 8, SIZE, SIZE);
    }

    /** Text shrunk to {@code maxWidth} when longer. */
    static void fitText(Canvas canvas, Component text, int x, int y, int maxWidth, int color, boolean shadow) {
        int width = canvas.width(text);
        if (width <= maxWidth) {
            canvas.text(text, x, y, color, shadow);
            return;
        }
        float scale = (float) maxWidth / width;
        canvas.push();
        canvas.translate(x, y + (1 - scale) * 4);
        canvas.scale(scale);
        canvas.text(text, 0, 0, color, shadow);
        canvas.pop();
    }
}
