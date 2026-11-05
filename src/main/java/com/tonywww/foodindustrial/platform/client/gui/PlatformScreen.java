package com.tonywww.foodindustrial.platform.client.gui;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}

/**
 * Container screen with stable hooks (machines/04): {@link #drawBackground} (screen coordinates),
 * {@link #drawForeground} (relative to the window's corner), {@link #drawOverlay} (after slots and
 * item tooltips, for own tooltips), {@link #onClick} before the vanilla slot handling, and
 * {@link #isInsideExtras} for side panels outside the window. The version-specific render and input
 * methods are implemented here.
 */
public abstract class PlatformScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {
    protected PlatformScreen(M menu, Inventory inventory, Component title, int width, int height) {
        //? if >=26.1 {
        /*super(menu, inventory, title, width, height);
        *///?} else {
        super(menu, inventory, title);
        this.imageWidth = width;
        this.imageHeight = height;
        //?}
        this.inventoryLabelY = height - 94;
    }

    /** Draws the window and everything behind the slots; screen coordinates. */
    protected abstract void drawBackground(Canvas canvas, int mouseX, int mouseY);

    /** Draws labels and overlays on the slots; coordinates relative to the window's top-left corner. */
    protected void drawForeground(Canvas canvas, int mouseX, int mouseY) {
    }

    /** Draws last (tooltips of own widgets); screen coordinates. */
    protected void drawOverlay(Canvas canvas, int mouseX, int mouseY) {
    }

    /** A click before slots handle it; return {@code true} to consume it. Screen coordinates. */
    protected boolean onClick(double mouseX, double mouseY, int button) {
        return false;
    }

    /** Areas outside the window that belong to the screen (side panels): clicks there do not drop items. */
    protected boolean isInsideExtras(double mouseX, double mouseY) {
        return false;
    }

    /** Areas outside the window that the screen draws on, in screen coordinates {x, y, w, h}. */
    public List<int[]> extraAreas() {
        return List.of();
    }

    /** Adds lines to the tooltip of the item in {@code slot} (the hovered slot). */
    protected void appendSlotTooltip(Slot slot, List<Component> lines) {
    }

    /** The slot under the mouse, or {@code null}. */
    @Nullable
    public Slot hoveredSlot() {
        return getSlotUnderMouse();
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        List<Component> lines = new ArrayList<>(super.getTooltipFromContainerItem(stack));
        Slot slot = getSlotUnderMouse();
        if (slot != null) {
            appendSlotTooltip(slot, lines);
        }
        return lines;
    }

    public int left() {
        return leftPos;
    }

    public int top() {
        return topPos;
    }

    public int windowWidth() {
        return imageWidth;
    }

    public int windowHeight() {
        return imageHeight;
    }

    //? if >=26.1 {
    /*@Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        drawBackground(new Canvas(graphics, font), mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        drawForeground(new Canvas(graphics, font), mouseX, mouseY);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        drawOverlay(new Canvas(graphics, font), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (onClick(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top) {
        return super.hasClickedOutside(mouseX, mouseY, left, top) && !isInsideExtras(mouseX, mouseY);
    }
    *///?} else {
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawBackground(new Canvas(graphics, font), mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        drawForeground(new Canvas(graphics, font), mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        //? if <1.21 {
        renderBackground(graphics);
        //?}
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        drawOverlay(new Canvas(graphics, font), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onClick(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return super.hasClickedOutside(mouseX, mouseY, left, top, button) && !isInsideExtras(mouseX, mouseY);
    }
    //?}
}
