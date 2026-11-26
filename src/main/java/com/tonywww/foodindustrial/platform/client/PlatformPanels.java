package com.tonywww.foodindustrial.platform.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.client.event.EntityRenderersEvent;
//?} else {
/*import com.tonywww.foodindustrial.platform.machine.PlatformBlockEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
*///?}
//? if <26.1 {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;
//?} else {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.FormattedCharSequence;
*///?}

/**
 * Flat panels that float at a block and face the camera (I37): rectangles of one texture, item icons,
 * fluid squares and small texts, all at full brightness. Business code fills a {@link Panel} each frame;
 * the renderer here draws it on the three nodes. Register on the physical client during mod construction.
 */
public final class PlatformPanels {
    private static final List<Entry<?>> ENTRIES = new ArrayList<>();
    /** Full brightness: a panel reads the same by day and by night. */
    private static final int LIGHT = 0xF000F0;
    /** Panel pixels between two depth layers. */
    private static final float LAYER = 0.35F;
    /** Depth of the item icons and texts in front of the panel, in pixels. */
    private static final float ITEM_DEPTH = 3.0F;
    private static final float TEXT_DEPTH = 6.5F;
    /** Item icons keep this much of their depth: blocks stay recognisable without poking through the panel. */
    private static final float FLATTEN = 0.3F;
    /** Steepest tilt towards a camera above or below, in radians. */
    private static final float MAX_PITCH = 1.05F;
    /**
     * Turns a panel's normals so that its front faces up: the level's two lights then shade every panel the
     * same whichever way it faces, the front fully lit, like items in a screen.
     */
    private static final Matrix3f FRONT_UP = new Matrix3f(1, 0, 0, 0, 0, -1, 0, 1, 0);
    private static final Matrix3f NORMALS = new Matrix3f();

    private PlatformPanels() {
    }

    /**
     * What one panel shows in a frame. Positions are panel pixels: the origin is the panel's centre, x
     * grows to the viewer's right and y upwards. A panel is owned by business code, one per block
     * entity, and refilled every frame: {@link #clear()}, then rectangles, items, fluids and texts.
     */
    public static final class Panel {
        public static final int MAX_RECTS = 64;
        public static final int MAX_ITEMS = 24;
        public static final int MAX_FLUIDS = 16;
        public static final int MAX_TEXTS = 24;

        /** Centre of the panel in the block's coordinates (0 to 1 inside the block). */
        public float x;
        public float y;
        public float z;
        /** Blocks per panel pixel. */
        public float unit = 1 / 96F;

        private int rects;
        private final float[] rect = new float[MAX_RECTS * 9];
        private final int[] rectColor = new int[MAX_RECTS];
        private int items;
        private final ItemStack[] itemStacks = new ItemStack[MAX_ITEMS];
        private final float[] item = new float[MAX_ITEMS * 6];
        private int fluids;
        private final FluidAmount[] fluidStacks = new FluidAmount[MAX_FLUIDS];
        private final float[] fluid = new float[MAX_FLUIDS * 4];
        private int texts;
        private final String[] textStrings = new String[MAX_TEXTS];
        private final float[] text = new float[MAX_TEXTS * 3];
        private final int[] textColor = new int[MAX_TEXTS];
        private final boolean[] textRight = new boolean[MAX_TEXTS];
        /** Width of each text and, on 26.1, its laid-out form: kept while the string stays the same object. */
        private final String[] measured = new String[MAX_TEXTS];
        private final int[] textWidth = new int[MAX_TEXTS];
        private final Object[] textLaidOut = new Object[MAX_TEXTS];
        /** 26.1: the resolved model of each item, kept while the stack stays the same object. */
        private final ItemStack[] resolved = new ItemStack[MAX_ITEMS];
        private final Object[] itemModels = new Object[MAX_ITEMS];
        private float yaw;
        private float pitch;

        public void clear() {
            rects = 0;
            items = 0;
            fluids = 0;
            texts = 0;
        }

        /**
         * A rectangle of the panel texture: its lower left corner and size in panel pixels, the upper left
         * corner and size of the texture region in texture pixels, the depth layer (0 is the back) and a
         * colour that multiplies the texture.
         */
        public void rect(float x, float y, float w, float h, float u, float v, float uw, float vh, int layer, int argb) {
            if (rects >= MAX_RECTS) {
                return;
            }
            int i = rects * 9;
            rect[i] = x;
            rect[i + 1] = y;
            rect[i + 2] = w;
            rect[i + 3] = h;
            rect[i + 4] = u;
            rect[i + 5] = v;
            rect[i + 6] = uw;
            rect[i + 7] = vh;
            rect[i + 8] = layer;
            rectColor[rects++] = argb;
        }

        /**
         * An item icon as screens draw it: centre, size in pixels (16 is a slot's icon), stretch along x and
         * y, and a turn in radians (anticlockwise).
         */
        public void item(ItemStack stack, float centreX, float centreY, float size, float stretchX, float stretchY, float turn) {
            if (items >= MAX_ITEMS || stack.isEmpty()) {
                return;
            }
            int i = items * 6;
            item[i] = centreX;
            item[i + 1] = centreY;
            item[i + 2] = size;
            item[i + 3] = stretchX;
            item[i + 4] = stretchY;
            item[i + 5] = turn;
            itemStacks[items++] = stack;
        }

        /** A square of a fluid's still texture in its colour: lower left corner and size. */
        public void fluid(FluidAmount amount, float x, float y, float w, float h) {
            if (fluids >= MAX_FLUIDS || amount.isEmpty()) {
                return;
            }
            int i = fluids * 4;
            fluid[i] = x;
            fluid[i + 1] = y;
            fluid[i + 2] = w;
            fluid[i + 3] = h;
            fluidStacks[fluids++] = amount;
        }

        /**
         * A text with a shadow: its upper left corner, or its upper right corner when {@code right}; a
         * {@code scale} of 1 makes a letter 8 pixels high. Pass the same string object while the text stays
         * the same, so its width is measured once.
         */
        public void text(String string, float x, float y, float scale, boolean right, int argb) {
            if (texts >= MAX_TEXTS) {
                return;
            }
            int i = texts * 3;
            text[i] = x;
            text[i + 1] = y;
            text[i + 2] = scale;
            textRight[texts] = right;
            textColor[texts] = argb;
            textStrings[texts++] = string;
        }
    }

    /** The panel of a block entity in this frame, or {@code null} for none. */
    @FunctionalInterface
    public interface Provider<T extends BlockEntity> {
        /**
         * @param cameraX the camera's position in the block's coordinates (the block's corner is 0, 0, 0)
         */
        @Nullable
        Panel panel(T blockEntity, float partialTick, double cameraX, double cameraY, double cameraZ);
    }

    private record Entry<T extends BlockEntity>(Supplier<? extends BlockEntityType<? extends T>> type, String texture, int textureSize,
                                                 IntSupplier viewDistance, Provider<T> provider) {
    }

    /**
     * Draws panels at the blocks of {@code type}.
     *
     * @param texture      path of the square panel texture, e.g. {@code textures/gui/immersive_panel.png}
     * @param textureSize  its width in pixels
     * @param viewDistance blocks from the camera within which {@code provider} is asked at all, read every
     *                     frame; 0 switches the panels of this type off at no cost per block entity
     */
    public static <T extends BlockEntity> void register(Supplier<? extends BlockEntityType<? extends T>> type, String texture, int textureSize,
                                                         IntSupplier viewDistance, Provider<T> provider) {
        ENTRIES.add(new Entry<>(type, texture, textureSize, viewDistance, provider));
    }

    /** Called by {@link PlatformBlockRenderers#bind}. */
    static void register(EntityRenderersEvent.RegisterRenderers event) {
        for (Entry<?> entry : ENTRIES) {
            registerEntry(event, entry);
        }
    }

    private static <T extends BlockEntity> void registerEntry(EntityRenderersEvent.RegisterRenderers event, Entry<T> entry) {
        //? if >=26.1 {
        /*event.registerBlockEntityRenderer(entry.type().get(), context -> new PanelRenderer<>(entry, context.itemModelResolver()));
        *///?} else {
        event.registerBlockEntityRenderer(entry.type().get(), context -> new PanelRenderer<>(entry));
        //?}
    }

    // ---- shared drawing ----

    /** Turns the panel towards the camera and remembers the angles. */
    private static void face(Panel panel, double cameraX, double cameraY, double cameraZ) {
        float dx = (float) cameraX - panel.x;
        float dy = (float) cameraY - panel.y;
        float dz = (float) cameraZ - panel.z;
        panel.yaw = (float) Math.atan2(dx, dz);
        float pitch = (float) Math.atan2(dy, Math.sqrt(dx * dx + dz * dz));
        panel.pitch = Math.max(-MAX_PITCH, Math.min(MAX_PITCH, pitch));
    }

    /** Moves the pose to the panel: pixels as units, facing the camera, normals lit from the front. */
    private static void enter(PoseStack poseStack, Panel panel) {
        NORMALS.set(poseStack.last().normal()).mul(FRONT_UP);
        poseStack.translate(panel.x, panel.y, panel.z);
        poseStack.mulPose(Axis.YP.rotation(panel.yaw));
        poseStack.mulPose(Axis.XP.rotation(-panel.pitch));
        poseStack.scale(panel.unit, panel.unit, panel.unit);
        poseStack.last().normal().set(NORMALS);
    }

    /** The rectangles of the layers from {@code fromLayer} to {@code toLayer}. */
    private static void rects(PoseStack.Pose pose, VertexConsumer buffer, Panel panel, int fromLayer, int toLayer, float textureSize) {
        for (int n = 0; n < panel.rects; n++) {
            int i = n * 9;
            int layer = (int) panel.rect[i + 8];
            if (layer < fromLayer || layer > toLayer) {
                continue;
            }
            float x0 = panel.rect[i];
            float y0 = panel.rect[i + 1];
            float x1 = x0 + panel.rect[i + 2];
            float y1 = y0 + panel.rect[i + 3];
            float u0 = panel.rect[i + 4] / textureSize;
            float v0 = panel.rect[i + 5] / textureSize;
            float u1 = u0 + panel.rect[i + 6] / textureSize;
            float v1 = v0 + panel.rect[i + 7] / textureSize;
            float z = layer * LAYER;
            int argb = panel.rectColor[n];
            PlatformBlockRenderers.vertex(pose, buffer, x0, y1, z, u0, v0, argb, LIGHT, 0, 0, 1);
            PlatformBlockRenderers.vertex(pose, buffer, x0, y0, z, u0, v1, argb, LIGHT, 0, 0, 1);
            PlatformBlockRenderers.vertex(pose, buffer, x1, y0, z, u1, v1, argb, LIGHT, 0, 0, 1);
            PlatformBlockRenderers.vertex(pose, buffer, x1, y1, z, u1, v0, argb, LIGHT, 0, 0, 1);
        }
    }

    /** One fluid square with the fluid's sprite and colour. */
    private static void fluidQuad(PoseStack.Pose pose, VertexConsumer buffer, Panel panel, int n, TextureAtlasSprite sprite, int argb) {
        int i = n * 4;
        float x0 = panel.fluid[i];
        float y0 = panel.fluid[i + 1];
        float x1 = x0 + panel.fluid[i + 2];
        float y1 = y0 + panel.fluid[i + 3];
        float scale = PlatformBlockRenderers.spriteScale();
        float u0 = sprite.getU(0);
        float u1 = sprite.getU(scale);
        float v0 = sprite.getV(0);
        float v1 = sprite.getV(scale);
        float z = 2 * LAYER;
        PlatformBlockRenderers.vertex(pose, buffer, x0, y1, z, u0, v0, argb, LIGHT, 0, 0, 1);
        PlatformBlockRenderers.vertex(pose, buffer, x0, y0, z, u0, v1, argb, LIGHT, 0, 0, 1);
        PlatformBlockRenderers.vertex(pose, buffer, x1, y0, z, u1, v1, argb, LIGHT, 0, 0, 1);
        PlatformBlockRenderers.vertex(pose, buffer, x1, y1, z, u1, v0, argb, LIGHT, 0, 0, 1);
    }

    /** Moves the pose to an item icon; the normals stay those of the panel, whatever the stretch. */
    private static void placeItem(PoseStack poseStack, Panel panel, int n) {
        int i = n * 6;
        float size = panel.item[i + 2];
        poseStack.translate(panel.item[i], panel.item[i + 1], ITEM_DEPTH);
        if (panel.item[i + 5] != 0) {
            poseStack.mulPose(Axis.ZP.rotation(panel.item[i + 5]));
        }
        poseStack.scale(size * panel.item[i + 3], size * panel.item[i + 4], size * FLATTEN);
        poseStack.last().normal().set(NORMALS);
    }

    /** Width of text {@code n} in font pixels, measured when its string changes. */
    private static int width(Panel panel, int n, Font font) {
        String string = panel.textStrings[n];
        if (panel.measured[n] != string) {
            panel.measured[n] = string;
            panel.textWidth[n] = font.width(string);
            panel.textLaidOut[n] = null;
        }
        return panel.textWidth[n];
    }

    //? if <26.1 {
    private static final class PanelRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
        private final Entry<T> entry;
        private final RenderType renderType;

        PanelRenderer(Entry<T> entry) {
            this.entry = entry;
            this.renderType = RenderType.entityTranslucentCull(PlatformIdentifier.id(entry.texture()));
        }

        @Override
        public int getViewDistance() {
            return entry.viewDistance().getAsInt();
        }

        //? if neoforge {
        /*@Override
        public net.minecraft.world.phys.AABB getRenderBoundingBox(T blockEntity) {
            return blockEntity instanceof PlatformBlockEntity own ? own.renderBounds() : BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
        }
        *///?}

        @Override
        public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
            Minecraft minecraft = Minecraft.getInstance();
            Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
            BlockPos pos = blockEntity.getBlockPos();
            double cameraX = camera.x - pos.getX();
            double cameraY = camera.y - pos.getY();
            double cameraZ = camera.z - pos.getZ();
            Panel panel = entry.provider().panel(blockEntity, partialTick, cameraX, cameraY, cameraZ);
            if (panel == null) {
                return;
            }
            face(panel, cameraX, cameraY, cameraZ);
            poseStack.pushPose();
            enter(poseStack, panel);
            float size = entry.textureSize();
            if (panel.rects > 0) {
                rects(poseStack.last(), buffers.getBuffer(renderType), panel, 0, 3, size);
            }
            if (panel.fluids > 0) {
                VertexConsumer buffer = buffers.getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));
                for (int n = 0; n < panel.fluids; n++) {
                    FluidAmount amount = panel.fluidStacks[n];
                    fluidQuad(poseStack.last(), buffer, panel, n, PlatformBlockRenderers.sprite(amount), PlatformBlockRenderers.tint(amount));
                }
            }
            int seed = (int) pos.asLong();
            for (int n = 0; n < panel.items; n++) {
                poseStack.pushPose();
                placeItem(poseStack, panel, n);
                minecraft.getItemRenderer().renderStatic(panel.itemStacks[n], ItemDisplayContext.GUI, LIGHT, OverlayTexture.NO_OVERLAY,
                    poseStack, buffers, blockEntity.getLevel(), seed + n);
                poseStack.popPose();
            }
            if (panel.rects > 0) {
                rects(poseStack.last(), buffers.getBuffer(renderType), panel, 4, Integer.MAX_VALUE, size);
            }
            Font font = minecraft.font;
            for (int n = 0; n < panel.texts; n++) {
                int i = n * 3;
                float scale = panel.text[i + 2];
                poseStack.pushPose();
                poseStack.translate(panel.text[i], panel.text[i + 1], TEXT_DEPTH);
                poseStack.scale(scale, -scale, scale);
                font.drawInBatch(panel.textStrings[n], panel.textRight[n] ? -width(panel, n, font) : 0, 0, panel.textColor[n], true,
                    poseStack.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, LIGHT);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
    }
    //?} else {
    /*private static final class PanelState extends BlockEntityRenderState {
        @Nullable
        Panel panel;
    }

    private static final class PanelRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, PanelState> {
        private final Entry<T> entry;
        private final ItemModelResolver resolver;
        private final net.minecraft.client.renderer.rendertype.RenderType renderType;

        PanelRenderer(Entry<T> entry, ItemModelResolver resolver) {
            this.entry = entry;
            this.resolver = resolver;
            this.renderType = RenderTypes.entityTranslucent(PlatformIdentifier.id(entry.texture()));
        }

        @Override
        public int getViewDistance() {
            return entry.viewDistance().getAsInt();
        }

        @Override
        public net.minecraft.world.phys.AABB getRenderBoundingBox(T blockEntity) {
            return blockEntity instanceof PlatformBlockEntity own ? own.renderBounds() : BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
        }

        @Override
        public PanelState createRenderState() {
            return new PanelState();
        }

        @Override
        public void extractRenderState(T blockEntity, PanelState state, float partialTicks, Vec3 cameraPosition,
                                       ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
            BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
            BlockPos pos = blockEntity.getBlockPos();
            double cameraX = cameraPosition.x - pos.getX();
            double cameraY = cameraPosition.y - pos.getY();
            double cameraZ = cameraPosition.z - pos.getZ();
            Panel panel = entry.provider().panel(blockEntity, partialTicks, cameraX, cameraY, cameraZ);
            state.panel = panel;
            if (panel == null) {
                return;
            }
            face(panel, cameraX, cameraY, cameraZ);
            int seed = (int) pos.asLong();
            for (int n = 0; n < panel.items; n++) {
                if (panel.resolved[n] != panel.itemStacks[n] || panel.itemModels[n] == null) {
                    ItemStackRenderState model = new ItemStackRenderState();
                    resolver.updateForTopItem(model, panel.itemStacks[n], ItemDisplayContext.GUI, blockEntity.getLevel(), null, seed + n);
                    panel.resolved[n] = panel.itemStacks[n];
                    panel.itemModels[n] = model;
                }
            }
            Font font = Minecraft.getInstance().font;
            for (int n = 0; n < panel.texts; n++) {
                width(panel, n, font);
                if (panel.textLaidOut[n] == null) {
                    panel.textLaidOut[n] = FormattedCharSequence.forward(panel.textStrings[n], net.minecraft.network.chat.Style.EMPTY);
                }
            }
        }

        @Override
        public void submit(PanelState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
            Panel panel = state.panel;
            if (panel == null) {
                return;
            }
            poseStack.pushPose();
            enter(poseStack, panel);
            float size = entry.textureSize();
            if (panel.rects > 0) {
                collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> rects(pose, buffer, panel, 0, 3, size));
            }
            if (panel.fluids > 0) {
                collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, buffer) -> {
                    for (int n = 0; n < panel.fluids; n++) {
                        FluidAmount amount = panel.fluidStacks[n];
                        fluidQuad(pose, buffer, panel, n, PlatformBlockRenderers.sprite(amount), PlatformBlockRenderers.tint(amount));
                    }
                });
            }
            for (int n = 0; n < panel.items; n++) {
                poseStack.pushPose();
                placeItem(poseStack, panel, n);
                ((ItemStackRenderState) panel.itemModels[n]).submit(poseStack, collector, LIGHT, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
            if (panel.rects > 0) {
                collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> rects(pose, buffer, panel, 4, Integer.MAX_VALUE, size));
            }
            for (int n = 0; n < panel.texts; n++) {
                int i = n * 3;
                float scale = panel.text[i + 2];
                poseStack.pushPose();
                poseStack.translate(panel.text[i], panel.text[i + 1], TEXT_DEPTH);
                poseStack.scale(scale, -scale, scale);
                collector.submitText(poseStack, panel.textRight[n] ? -panel.textWidth[n] : 0, 0, (FormattedCharSequence) panel.textLaidOut[n], true,
                    Font.DisplayMode.NORMAL, LIGHT, panel.textColor[n], 0, 0);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
    }
    *///?}
}
