package com.tonywww.foodindustrial.platform.client;

import com.mojang.datafixers.util.Either;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
*///?}

/**
 * Version-neutral drawing for the mod's own GUI widgets (I35): rectangles, icons, text, bars and tooltips. One
 * shared instance wraps the frame's graphics; render thread only.
 */
final class GuiCanvas {
    private static final GuiCanvas INSTANCE = new GuiCanvas();
    //? if <26.1 {
    private static final Map<String, ResourceLocation> IDS = new HashMap<>();
    private GuiGraphics graphics;

    static GuiCanvas of(GuiGraphics graphics) {
        INSTANCE.graphics = graphics;
        return INSTANCE;
    }
    //?} else {
    /*private static final Map<String, Identifier> IDS = new HashMap<>();
    private GuiGraphicsExtractor graphics;

    static GuiCanvas of(GuiGraphicsExtractor graphics) {
        INSTANCE.graphics = graphics;
        return INSTANCE;
    }
    *///?}

    private GuiCanvas() {
    }

    /** Fills a rectangle (corners inclusive–exclusive) with an ARGB colour. */
    void fill(int x0, int y0, int x1, int y1, int argb) {
        if (x1 > x0 && y1 > y0) {
            graphics.fill(x0, y0, x1, y1, argb);
        }
    }

    /** Draws a {@code textureSize} square texture ({@code namespace:textures/....png}) {@code size} square. */
    void icon(String texture, int x, int y, int size, int textureSize) {
        //? if <26.1 {
        graphics.blit(IDS.computeIfAbsent(texture, PlatformIdentifier::parse), x, y, size, size, 0, 0, textureSize, textureSize, textureSize, textureSize);
        //?} else {
        /*graphics.blit(RenderPipelines.GUI_TEXTURED, IDS.computeIfAbsent(texture, PlatformIdentifier::parse), x, y, 0, 0, size, size,
            textureSize, textureSize, textureSize, textureSize);
        *///?}
    }

    /** Draws text in an ARGB colour (the text's own colours win). */
    void text(Font font, Component text, int x, int y, int argb, boolean shadow) {
        //? if <26.1 {
        graphics.drawString(font, text, x, y, argb, shadow);
        //?} else {
        /*graphics.text(font, text, x, y, argb, shadow);
        *///?}
    }

    /** Draws a bar inside {@code x, y, width, height}; its edge (if any) lies one pixel outside. */
    void bar(GuiBar bar, int x, int y, int width, int height) {
        if (bar.edgeColor() != 0) {
            fill(x - 1, y - 1, x + width + 1, y + height + 1, bar.edgeColor());
        }
        fill(x, y, x + width, y + height, bar.trackColor());
        if (bar.stripeColor() != 0) {
            for (int i = 0; i < width; i += 2) {
                fill(x + i, y, x + i + 1, y + height, bar.stripeColor());
            }
        }
        float[] zones = bar.zones();
        for (int i = 0; i + 1 < zones.length; i += 2) {
            fill(x + Math.round(zones[i] * width), y, x + Math.round(zones[i + 1] * width), y + height, bar.zoneColors()[i / 2]);
        }
        if (bar.fill() > 0) {
            int filled = Math.max(1, Math.round(Math.min(1.0F, bar.fill()) * width));
            fill(x, y, x + filled, y + height, bar.fillColor());
        }
        for (float tick : bar.ticks()) {
            int at = x + Math.round(tick * width);
            fill(at, y, at + 1, y + height, bar.tickColor());
        }
    }

    /** Shows a tooltip made of one part at the mouse, above everything else. */
    void tooltip(Font font, TooltipComponent part, int mouseX, int mouseY) {
        List<Either<FormattedText, TooltipComponent>> elements = List.of(Either.right(part));
        //? if <26.1 {
        graphics.renderComponentTooltipFromElements(font, elements, mouseX, mouseY, ItemStack.EMPTY);
        //?} else {
        /*graphics.setComponentTooltipFromElementsForNextFrame(font, elements, mouseX, mouseY, ItemStack.EMPTY);
        *///?}
    }

    /** Shows a one-line tooltip at the mouse. */
    void tooltip(Font font, Component text, int mouseX, int mouseY) {
        //? if <26.1 {
        graphics.renderTooltip(font, text, mouseX, mouseY);
        //?} else {
        /*graphics.setTooltipForNextFrame(font, text, mouseX, mouseY);
        *///?}
    }
}
