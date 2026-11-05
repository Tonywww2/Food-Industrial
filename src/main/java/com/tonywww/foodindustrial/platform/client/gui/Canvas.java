package com.tonywww.foodindustrial.platform.client.gui;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.InventoryMenu;
//?}
//? if forge {
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
//?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
*///?}

/**
 * Version-independent drawing for screens (machines/04): rectangles, texture regions, text, items,
 * fluids and tooltips. Wraps {@code GuiGraphics} (1.20.1, 1.21.1) or {@code GuiGraphicsExtractor}
 * (26.1). Colours are ARGB.
 */
public final class Canvas {
    //? if >=26.1 {
    /*private final GuiGraphicsExtractor graphics;

    public Canvas(GuiGraphicsExtractor graphics, Font font) {
        this.graphics = graphics;
        this.font = font;
    }
    *///?} else {
    private final GuiGraphics graphics;

    public Canvas(GuiGraphics graphics, Font font) {
        this.graphics = graphics;
        this.font = font;
    }
    //?}

    private final Font font;

    public Font font() {
        return font;
    }

    public void fill(int x0, int y0, int x1, int y1, int argb) {
        graphics.fill(x0, y0, x1, y1, argb);
    }

    /** One-pixel frame inside {@code (x, y, w, h)}. */
    public void outline(int x, int y, int w, int h, int argb) {
        fill(x, y, x + w, y + 1, argb);
        fill(x, y + h - 1, x + w, y + h, argb);
        fill(x, y + 1, x + 1, y + h - 1, argb);
        fill(x + w - 1, y + 1, x + w, y + h - 1, argb);
    }

    /** A region of a texture ({@code namespace:textures/…png}) at its own size. */
    public void blit(String texture, int x, int y, int u, int v, int w, int h, int textureWidth, int textureHeight) {
        //? if >=26.1 {
        /*graphics.blit(RenderPipelines.GUI_TEXTURED, PlatformIdentifier.parse(texture), x, y, (float) u, (float) v, w, h, textureWidth, textureHeight);
        *///?} else {
        graphics.blit(PlatformIdentifier.parse(texture), x, y, (float) u, (float) v, w, h, textureWidth, textureHeight);
        //?}
    }

    public void text(Component text, int x, int y, int argb, boolean shadow) {
        //? if >=26.1 {
        /*graphics.text(font, text, x, y, argb, shadow);
        *///?} else {
        graphics.drawString(font, text, x, y, argb, shadow);
        //?}
    }

    public void text(String text, int x, int y, int argb, boolean shadow) {
        text(Component.literal(text), x, y, argb, shadow);
    }

    public int width(Component text) {
        return font.width(text);
    }

    public int width(String text) {
        return font.width(text);
    }

    /** An item with its count and bar. */
    public void item(ItemStack stack, int x, int y) {
        //? if >=26.1 {
        /*graphics.item(stack, x, y);
        graphics.itemDecorations(font, stack, x, y);
        *///?} else {
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(font, stack, x, y);
        //?}
    }

    /** {@code fluid} filling {@code (x, y, w, h)} from the bottom, tiled with its still texture and tint. */
    public void fluid(FluidAmount fluid, int x, int y, int w, int h) {
        if (fluid.isEmpty() || w <= 0 || h <= 0) {
            return;
        }
        TextureAtlasSprite sprite = sprite(fluid);
        int tint = tint(fluid);
        for (int dy = 0; dy < h; dy += 16) {
            int th = Math.min(16, h - dy);
            for (int dx = 0; dx < w; dx += 16) {
                int tw = Math.min(16, w - dx);
                int px = x + dx;
                int py = y + h - dy - th;
                //? if >=26.1 {
                /*graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, px, py, tw, th, tint | 0xFF000000);
                *///?} else {
                graphics.blit(px, py, 0, tw, th, sprite, ((tint >> 16) & 255) / 255F, ((tint >> 8) & 255) / 255F, (tint & 255) / 255F, 1F);
                //?}
            }
        }
    }

    private static TextureAtlasSprite sprite(FluidAmount fluid) {
        //? if >=26.1 {
        /*FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.fluid().defaultFluidState());
        return model.stillMaterial().sprite();
        *///?} else {
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.fluid());
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture(PlatformFluids.toStack(fluid)));
        //?}
    }

    private static int tint(FluidAmount fluid) {
        //? if >=26.1 {
        /*FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.fluid().defaultFluidState());
        return model.fluidTintSource() == null ? 0xFFFFFF : model.fluidTintSource().colorAsStack(PlatformFluids.toStack(fluid)) & 0xFFFFFF;
        *///?} else {
        return IClientFluidTypeExtensions.of(fluid.fluid()).getTintColor(PlatformFluids.toStack(fluid)) & 0xFFFFFF;
        //?}
    }

    /** A tooltip at the mouse; draw it after everything else. */
    public void tooltip(List<Component> lines, int x, int y) {
        if (lines.isEmpty()) {
            return;
        }
        //? if >=26.1 {
        /*graphics.setComponentTooltipForNextFrame(font, lines, x, y);
        *///?} else {
        graphics.renderComponentTooltip(font, lines, x, y);
        //?}
    }

    public void push() {
        //? if >=26.1 {
        /*graphics.pose().pushMatrix();
        *///?} else {
        graphics.pose().pushPose();
        //?}
    }

    public void translate(float x, float y) {
        //? if >=26.1 {
        /*graphics.pose().translate(x, y);
        *///?} else {
        graphics.pose().translate(x, y, 0);
        //?}
    }

    public void scale(float factor) {
        //? if >=26.1 {
        /*graphics.pose().scale(factor, factor);
        *///?} else {
        graphics.pose().scale(factor, factor, 1);
        //?}
    }

    public void pop() {
        //? if >=26.1 {
        /*graphics.pose().popMatrix();
        *///?} else {
        graphics.pose().popPose();
        //?}
    }
}
