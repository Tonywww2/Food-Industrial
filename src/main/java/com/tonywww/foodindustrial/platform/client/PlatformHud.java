package com.tonywww.foodindustrial.platform.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
//? if forge {
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
//?} else {
/*import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}

/**
 * Client drawing hooks of the metabolism (I35, foods/02 §8): camera sway, a full-screen tint above the HUD, and a
 * side panel to the right of the survival inventory with a toggle button inside the inventory (above the offhand
 * slot, clear of Apothic Attributes' button in the top right of the player model's frame). The panel is a stack of
 * small dark translucent pills, one per entry with an 8 × 8 icon and a thin bar; hovering a pill shows its card. Only
 * used on the physical client; register during client setup.
 */
public final class PlatformHud {
    private static final List<CameraHook> CAMERA = new ArrayList<>();
    private static final List<IntSupplier> TINTS = new ArrayList<>();
    @Nullable
    private static InventoryPanel panel;
    private static boolean open;
    /** Button position inside the inventory (centred 2 pixels above the offhand slot at 77, 62), and its size. */
    private static final int BUTTON_X = 80;
    private static final int BUTTON_Y = 49;
    private static final int BUTTON = 10;
    /** Panel layout (I35 mock-up F, “floating pills”, chosen on 2026-11-13). */
    private static final int GAP = 2;
    private static final int WIDTH = 44;
    private static final int PILL = 10;
    private static final int PILL_GAP = 2;
    private static final int ICON_X = 3;
    private static final int ICON_Y = 1;
    private static final int ICON = 8;
    private static final int BAR_X = 14;
    private static final int BAR_Y = 4;
    private static final int BAR_WIDTH = 26;
    private static final int BAR_HEIGHT = 2;
    private static final int PILL_COLOR = 0xCC101116;

    private PlatformHud() {
    }

    /** Changes the camera angles for this frame (degrees). */
    public static final class CameraAngles {
        public float yaw;
        public float pitch;
        public float roll;
    }

    @FunctionalInterface
    public interface CameraHook {
        /** @param partialTick the frame's partial tick */
        void adjust(CameraAngles angles, double partialTick);
    }

    /** Adjusts the camera every frame. */
    public static void onCameraAngles(CameraHook hook) {
        CAMERA.add(hook);
    }

    /** Fills the whole screen above the HUD with the ARGB colour {@code tint} returns (0: nothing). */
    public static void onScreenTint(IntSupplier tint) {
        TINTS.add(tint);
    }

    /** One pill of the panel: an icon ({@code namespace:textures/....png}, a 16 × 16 texture drawn 8 × 8) and a bar. */
    public record Row(String icon, GuiBar bar) {
    }

    /** Contents of the inventory side panel. */
    public interface InventoryPanel {
        /** Whether the button and the panel show at all. */
        boolean available();

        /** Rows, read every frame while the panel is open. */
        List<Row> rows();

        /** The card shown while the mouse is over row {@code index}, or {@code null}. */
        @Nullable
        TooltipRows tooltip(int index);

        /** The button's 8 × 8 icon ({@code namespace:textures/....png}). */
        String buttonIcon();

        /** The button's tooltip. */
        Component buttonTooltip();
    }

    /**
     * Shows icons for the mod's attribute modifiers in Apothic Attributes' attribute panel when it is installed
     * ({@code iconOf}: a declared modifier id's 16 × 16 icon, or {@code null}).
     */
    public static void showModifierIcons(java.util.function.Function<String, String> iconOf) {
        //? if forge {
        boolean loaded = com.tonywww.foodindustrial.platform.Platform.isModLoaded("attributeslib");
        //?} else {
        /*boolean loaded = com.tonywww.foodindustrial.platform.Platform.isModLoaded("apothic_attributes");
        *///?}
        if (loaded) {
            com.tonywww.foodindustrial.platform.PlatformEvents.onClientSetup(() ->
                com.tonywww.foodindustrial.platform.compat.apothic_attributes.ApothicAttributesCompat.register(iconOf));
        }
    }

    /** Sets the inventory side panel (one per mod). */
    public static void registerInventoryPanel(InventoryPanel value) {
        panel = value;
    }

    /**
     * The panel's area on {@code screen} as {x, y, width, height} for JEI to keep clear, or {@code null} when it is
     * closed or {@code screen} is not the survival inventory.
     */
    @Nullable
    public static int[] inventoryPanelArea(Screen screen) {
        if (!(screen instanceof InventoryScreen inventory) || panel == null || !open || !panel.available()) {
            return null;
        }
        return new int[] {inventory.getGuiLeft() + inventory.getXSize() + GAP, inventory.getGuiTop(), WIDTH, height(panel.rows().size())};
    }

    private static int height(int rows) {
        return Math.max(0, rows * (PILL + PILL_GAP) - PILL_GAP);
    }

    private static boolean overButton(InventoryScreen screen, double mouseX, double mouseY) {
        int x = screen.getGuiLeft() + BUTTON_X;
        int y = screen.getGuiTop() + BUTTON_Y;
        return mouseX >= x && mouseX < x + BUTTON && mouseY >= y && mouseY < y + BUTTON;
    }

    private static boolean showsButton(Screen screen) {
        return screen instanceof InventoryScreen && panel != null && panel.available();
    }

    private static void click(Screen screen, double mouseX, double mouseY, Runnable cancel) {
        if (showsButton(screen) && overButton((InventoryScreen) screen, mouseX, mouseY)) {
            open = !open;
            cancel.run();
        }
    }

    private static int tint() {
        int result = 0;
        for (IntSupplier tint : TINTS) {
            int value = tint.getAsInt();
            if (value != 0) {
                result = value;
            }
        }
        return result;
    }

    private static final CameraAngles ANGLES = new CameraAngles();

    /** Called by {@link PlatformClient#bind}. */
    static void bind() {
        //? if forge {
        var bus = MinecraftForge.EVENT_BUS;
        //?} else {
        /*var bus = NeoForge.EVENT_BUS;
        *///?}
        bus.addListener((ViewportEvent.ComputeCameraAngles event) -> {
            if (CAMERA.isEmpty()) {
                return;
            }
            ANGLES.yaw = event.getYaw();
            ANGLES.pitch = event.getPitch();
            ANGLES.roll = event.getRoll();
            for (CameraHook hook : CAMERA) {
                hook.adjust(ANGLES, event.getPartialTick());
            }
            event.setYaw(ANGLES.yaw);
            event.setPitch(ANGLES.pitch);
            event.setRoll(ANGLES.roll);
        });
        bus.addListener((RenderGuiEvent.Post event) -> {
            int color = tint();
            if (color != 0) {
                tint(event.getGuiGraphics(), color);
            }
        });
        //? if <26.1 {
        bus.addListener((ScreenEvent.Render.Post event) -> {
            if (showsButton(event.getScreen())) {
                draw(GuiCanvas.of(event.getGuiGraphics()), (InventoryScreen) event.getScreen(), event.getMouseX(), event.getMouseY());
            }
        });
        //?} else {
        /*// 26.1 draws tooltips deferred at the end of the screen's own pass, before Render.Post: drawing in the
        // foreground pass keeps the carried item and item tooltips above the panel and shows the row cards
        bus.addListener((ScreenEvent.Render.Foreground event) -> {
            if (showsButton(event.getScreen())) {
                draw(GuiCanvas.of(event.getGuiGraphics()), (InventoryScreen) event.getScreen(), event.getMouseX(), event.getMouseY());
            }
        });
        *///?}
        bus.addListener((ScreenEvent.MouseButtonPressed.Pre event) ->
            click(event.getScreen(), event.getMouseX(), event.getMouseY(), () -> event.setCanceled(true)));
    }

    private static void draw(GuiCanvas canvas, InventoryScreen screen, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        int bx = screen.getGuiLeft() + BUTTON_X;
        int by = screen.getGuiTop() + BUTTON_Y;
        boolean hover = overButton(screen, mouseX, mouseY);
        frame(canvas, bx, by, BUTTON, BUTTON, hover || open);
        canvas.icon(panel.buttonIcon(), bx + 1, by + 1, 8, 8);
        int hovered = -1;
        if (open) {
            List<Row> rows = panel.rows();
            int x = screen.getGuiLeft() + screen.getXSize() + GAP;
            int y = screen.getGuiTop();
            for (int i = 0; i < rows.size(); i++) {
                Row row = rows.get(i);
                int rowY = y + i * (PILL + PILL_GAP);
                pill(canvas, x, rowY, WIDTH, PILL, PILL_COLOR);
                canvas.icon(row.icon(), x + ICON_X, rowY + ICON_Y, ICON, 16);
                canvas.bar(row.bar(), x + BAR_X, rowY + BAR_Y, BAR_WIDTH, BAR_HEIGHT);
                if (mouseX >= x && mouseX < x + WIDTH && mouseY >= rowY && mouseY < rowY + PILL) {
                    hovered = i;
                }
            }
        }
        if (hover) {
            canvas.tooltip(font, panel.buttonTooltip(), mouseX, mouseY);
        } else if (hovered >= 0) {
            TooltipRows card = panel.tooltip(hovered);
            if (card != null) {
                canvas.tooltip(font, card, mouseX, mouseY);
            }
        }
    }

    /** A rectangle with its corners cut by two pixels. */
    private static void pill(GuiCanvas canvas, int x, int y, int width, int height, int color) {
        canvas.fill(x + 2, y, x + width - 2, y + 1, color);
        canvas.fill(x + 1, y + 1, x + width - 1, y + 2, color);
        canvas.fill(x, y + 2, x + width, y + height - 2, color);
        canvas.fill(x + 1, y + height - 2, x + width - 1, y + height - 1, color);
        canvas.fill(x + 2, y + height - 1, x + width - 2, y + height, color);
    }

    /** A vanilla-style raised button frame. */
    private static void frame(GuiCanvas canvas, int x, int y, int width, int height, boolean pressed) {
        canvas.fill(x, y, x + width, y + height, 0xFF000000);
        canvas.fill(x + 1, y + 1, x + width - 1, y + height - 1, pressed ? 0xFF8B8B8B : 0xFFFFFFFF);
        canvas.fill(x + 2, y + 2, x + width - 1, y + height - 1, 0xFF555555);
        canvas.fill(x + 2, y + 2, x + width - 2, y + height - 2, pressed ? 0xFFA0A0A0 : 0xFFC6C6C6);
    }

    //? if <26.1 {
    /** Fills the screen above every HUD layer (the layers sit at rising depths up to 1.21.1). */
    private static void tint(GuiGraphics graphics, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 2000.0F);
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), color);
        graphics.pose().popPose();
    }
    //?} else {
    /*private static void tint(GuiGraphicsExtractor graphics, int color) {
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), color);
    }
    *///?}
}
