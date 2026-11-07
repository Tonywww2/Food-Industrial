package com.tonywww.foodindustrial.platform.client;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
*///?}
//? if <26.1 {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}
//? if neoforge && <26.1 {
/*import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
*///?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSources;
*///?}

/**
 * Client-side registration: menu screens, fluid rendering, bucket tints and slot labels. Only used on the
 * physical client.
 */
public final class PlatformClient {
    private static final List<ScreenEntry<?, ?>> SCREEN_ENTRIES = new ArrayList<>();

    private PlatformClient() {
    }

    private record ScreenEntry<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(
        Supplier<MenuType<M>> type, MenuScreens.ScreenConstructor<M, U> constructor) {
    }

    /** A run of slot label text in one colour (ARGB); small runs are drawn smaller and lower (subscripts). */
    public record LabelRun(String text, boolean small, int color) {
    }

    private record SlotLabel(Supplier<? extends Item> item, List<LabelRun> runs) {
    }

    private static final List<SlotLabel> SLOT_LABELS = new ArrayList<>();
    /** Largest label size relative to the font, and the size of small runs relative to normal ones. */
    private static final float LABEL_MAX_SCALE = 0.5F;
    private static final float LABEL_SMALL = 0.7F;
    private static final float LABEL_SMALL_DROP = 3.5F;
    private static final int SLOT = 16;

    /** Binds the screen class of a menu type. Call during mod construction from client-only code. */
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(Supplier<MenuType<M>> type, MenuScreens.ScreenConstructor<M, U> constructor) {
        SCREEN_ENTRIES.add(new ScreenEntry<>(type, constructor));
    }

    /**
     * Draws a one-line label along the top of the item's icon wherever it is drawn in a GUI slot
     * (inventories, hotbar, JEI): centred, at most half the font size and shrunk to fit the slot
     * width. Call during mod construction from client-only code.
     */
    public static void registerSlotLabel(Supplier<? extends Item> item, List<LabelRun> runs) {
        if (!runs.isEmpty()) {
            SLOT_LABELS.add(new SlotLabel(item, List.copyOf(runs)));
        }
    }

    private static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        for (SlotLabel label : SLOT_LABELS) {
            event.register(label.item().get(), (graphics, font, stack, x, y) -> {
                drawLabel(graphics, font, label, x, y);
                return false;
            });
        }
    }

    private static float labelWidth(Font font, List<LabelRun> runs) {
        float width = 0;
        for (LabelRun run : runs) {
            width += font.width(run.text()) * (run.small() ? LABEL_SMALL : 1.0F);
        }
        return width;
    }

    //? if <26.1 {
    private static void drawLabel(GuiGraphics graphics, Font font, SlotLabel label, int x, int y) {
        float width = labelWidth(font, label.runs());
        float scale = Math.min(LABEL_MAX_SCALE, SLOT / width);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        // z 200: above the item, like the stack count
        pose.translate(x + (SLOT - width * scale) / 2.0F, y, 200.0F);
        pose.scale(scale, scale, 1.0F);
        float cursor = 0;
        for (LabelRun run : label.runs()) {
            pose.pushPose();
            pose.translate(cursor, run.small() ? LABEL_SMALL_DROP : 0.0F, 0.0F);
            if (run.small()) {
                pose.scale(LABEL_SMALL, LABEL_SMALL, 1.0F);
            }
            graphics.drawString(font, run.text(), 0, 0, run.color(), true);
            pose.popPose();
            cursor += font.width(run.text()) * (run.small() ? LABEL_SMALL : 1.0F);
        }
        pose.popPose();
    }
    //?} else {
    /*private static void drawLabel(GuiGraphicsExtractor graphics, Font font, SlotLabel label, int x, int y) {
        float width = labelWidth(font, label.runs());
        float scale = Math.min(LABEL_MAX_SCALE, SLOT / width);
        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x + (SLOT - width * scale) / 2.0F, y);
        pose.scale(scale, scale);
        float cursor = 0;
        for (LabelRun run : label.runs()) {
            pose.pushMatrix();
            pose.translate(cursor, run.small() ? LABEL_SMALL_DROP : 0.0F);
            if (run.small()) {
                pose.scale(LABEL_SMALL, LABEL_SMALL);
            }
            graphics.text(font, run.text(), 0, 0, run.color(), true);
            pose.popMatrix();
            cursor += font.width(run.text()) * (run.small() ? LABEL_SMALL : 1.0F);
        }
        pose.popMatrix();
    }
    *///?}

    private static final List<Runnable> CLIENT_TICK = new ArrayList<>();

    /** Runs at the end of every client tick. Call during mod construction from client-only code. */
    public static void onClientTick(Runnable task) {
        CLIENT_TICK.add(task);
    }

    private static void clientTick() {
        for (Runnable task : CLIENT_TICK) {
            task.run();
        }
    }

    /** Called by the mod entrypoint on the physical client. */
    public static void bind(IEventBus modBus) {
        //? if forge {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.TickEvent.ClientTickEvent event) -> {
            if (event.phase == net.minecraftforge.event.TickEvent.Phase.END) {
                clientTick();
            }
        });
        //?} else {
        /*net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.client.event.ClientTickEvent.Post event) -> clientTick());
        *///?}
        //? if forge {
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> SCREEN_ENTRIES.forEach(PlatformClient::registerVanilla)));
        //?} else {
        /*modBus.addListener((RegisterMenuScreensEvent event) -> SCREEN_ENTRIES.forEach(entry -> registerEvent(event, entry)));
        *///?}
        //? if neoforge && <26.1 {
        /*modBus.addListener(PlatformClient::registerFluidExtensions);
        *///?}
        //? if <26.1 {
        modBus.addListener(PlatformClient::registerBucketColors);
        //?}
        modBus.addListener(PlatformClient::registerItemDecorations);
        PlatformBlockRenderers.bind(modBus);
        //? if >=26.1 {
        /*modBus.addListener(PlatformClient::registerFluidModels);
        *///?}
    }

    //? if forge {
    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerVanilla(ScreenEntry<M, U> entry) {
        MenuScreens.register(entry.type().get(), entry.constructor());
    }
    //?} else {
    /*private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerEvent(RegisterMenuScreensEvent event, ScreenEntry<M, U> entry) {
        event.register(entry.type().get(), entry.constructor());
    }
    *///?}

    //? if <26.1 {
    /** Tints the fluid layer of the mod's buckets (the loader's dynamic bucket model); from 26.1 the model tints itself. */
    private static void registerBucketColors(RegisterColorHandlersEvent.Item event) {
        DynamicFluidContainerModel.Colors colors = new DynamicFluidContainerModel.Colors();
        for (PlatformFluids.Registered fluid : PlatformFluids.registered()) {
            if (fluid.bucket() != null) {
                event.register(colors, fluid.bucket().get());
            }
        }
    }
    //?}

    //? if neoforge && <26.1 {
    /*private static void registerFluidExtensions(RegisterClientExtensionsEvent event) {
        for (PlatformFluids.Registered fluid : PlatformFluids.registered()) {
            PlatformFluids.Spec spec = fluid.spec();
            ResourceLocation still = PlatformIdentifier.id(FoodIndustrial.MOD_ID, spec.stillTexture());
            ResourceLocation flowing = PlatformIdentifier.id(FoodIndustrial.MOD_ID, spec.flowingTexture());
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override public int getTintColor() { return spec.color(); }
                @Override public ResourceLocation getStillTexture() { return still; }
                @Override public ResourceLocation getFlowingTexture() { return flowing; }
            }, fluid.source().get().getFluidType());
        }
    }
    *///?}

    //? if >=26.1 {
    /*private static void registerFluidModels(RegisterFluidModelsEvent event) {
        for (PlatformFluids.Registered fluid : PlatformFluids.registered()) {
            PlatformFluids.Spec spec = fluid.spec();
            event.register(new FluidModel.Unbaked(
                new Material(PlatformIdentifier.id(FoodIndustrial.MOD_ID, spec.stillTexture())),
                new Material(PlatformIdentifier.id(FoodIndustrial.MOD_ID, spec.flowingTexture())),
                null,
                FluidTintSources.constant(spec.color())), fluid.source().get(), fluid.flowing().get());
        }
    }
    *///?}
}
