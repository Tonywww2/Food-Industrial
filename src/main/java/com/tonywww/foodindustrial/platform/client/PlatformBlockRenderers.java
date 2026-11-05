package com.tonywww.foodindustrial.platform.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
*///?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
*///?}
//? if <26.1 {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.inventory.InventoryMenu;
//?} else {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.phys.Vec3;
*///?}

/**
 * Block entity renderers drawn by the platform: a fluid cuboid inside a block (tank contents, machines/03
 * §5) with the fluid's still texture, tint and the block's light. Register on the physical client during
 * mod construction.
 */
public final class PlatformBlockRenderers {
    private static final List<Entry<?>> ENTRIES = new ArrayList<>();

    private PlatformBlockRenderers() {
    }

    /** A fluid cuboid in block coordinates (0 to 1). */
    public record FluidBox(FluidAmount fluid, float x0, float y0, float z0, float x1, float y1, float z1) {
    }

    /** The cuboid to draw for a block entity, or {@code null} for nothing. */
    @FunctionalInterface
    public interface FluidContent<T extends BlockEntity> {
        @Nullable
        FluidBox box(T blockEntity);
    }

    private record Entry<T extends BlockEntity>(Supplier<? extends BlockEntityType<? extends T>> type, FluidContent<T> content) {
    }

    public static <T extends BlockEntity> void registerFluidBox(Supplier<? extends BlockEntityType<? extends T>> type, FluidContent<T> content) {
        ENTRIES.add(new Entry<>(type, content));
    }

    /** Called by {@link PlatformClient#bind}. */
    static void bind(IEventBus modBus) {
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            for (Entry<?> entry : ENTRIES) {
                register(event, entry);
            }
        });
    }

    private static <T extends BlockEntity> void register(EntityRenderersEvent.RegisterRenderers event, Entry<T> entry) {
        event.registerBlockEntityRenderer(entry.type().get(), context -> new FluidBoxRenderer<>(entry.content()));
    }

    // ---- drawing ----

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
        return model.fluidTintSource() == null ? 0xFFFFFFFF : model.fluidTintSource().colorAsStack(PlatformFluids.toStack(fluid)) | 0xFF000000;
        *///?} else {
        return IClientFluidTypeExtensions.of(fluid.fluid()).getTintColor(PlatformFluids.toStack(fluid)) | 0xFF000000;
        //?}
    }

    /** The six faces of {@code box}, each with the sprite cropped to the face's size. */
    private static void cuboid(PoseStack.Pose pose, VertexConsumer buffer, FluidBox box, TextureAtlasSprite sprite, int argb, int light) {
        float x0 = box.x0(), y0 = box.y0(), z0 = box.z0(), x1 = box.x1(), y1 = box.y1(), z1 = box.z1();
        // top and bottom
        quad(pose, buffer, sprite, argb, light, 0, 1, 0, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, z0, x1, z1);
        quad(pose, buffer, sprite, argb, light, 0, -1, 0, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, z0, x1, z1);
        // north (-z) and south (+z)
        quad(pose, buffer, sprite, argb, light, 0, 0, -1, x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0, x0, 1 - y1, x1, 1 - y0);
        quad(pose, buffer, sprite, argb, light, 0, 0, 1, x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, 1 - y1, x1, 1 - y0);
        // west (-x) and east (+x)
        quad(pose, buffer, sprite, argb, light, -1, 0, 0, x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1, z0, 1 - y1, z1, 1 - y0);
        quad(pose, buffer, sprite, argb, light, 1, 0, 0, x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0, z0, 1 - y1, z1, 1 - y0);
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer buffer, TextureAtlasSprite sprite, int argb, int light,
                             float nx, float ny, float nz,
                             float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz,
                             float u0, float v0, float u1, float v1) {
        float su0 = sprite.getU(u0 * spriteScale());
        float su1 = sprite.getU(u1 * spriteScale());
        float sv0 = sprite.getV(v0 * spriteScale());
        float sv1 = sprite.getV(v1 * spriteScale());
        vertex(pose, buffer, ax, ay, az, su0, sv0, argb, light, nx, ny, nz);
        vertex(pose, buffer, bx, by, bz, su0, sv1, argb, light, nx, ny, nz);
        vertex(pose, buffer, cx, cy, cz, su1, sv1, argb, light, nx, ny, nz);
        vertex(pose, buffer, dx, dy, dz, su1, sv0, argb, light, nx, ny, nz);
    }

    /** {@code TextureAtlasSprite.getU} takes 0–16 before 1.21 and 0–1 from 1.21 on. */
    private static float spriteScale() {
        //? if <1.21 {
        return 16.0F;
        //?} else {
        /*return 1.0F;
        *///?}
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float z, float u, float v, int argb, int light,
                               float nx, float ny, float nz) {
        //? if <1.21 {
        buffer.vertex(pose.pose(), x, y, z).color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, (argb >>> 24) & 255)
            .uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
        //?} else {
        /*buffer.addVertex(pose, x, y, z).setColor(argb).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
            .setNormal(pose, nx, ny, nz);
        *///?}
    }

    //? if <26.1 {
    private static final class FluidBoxRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
        private final FluidContent<T> content;

        FluidBoxRenderer(FluidContent<T> content) {
            this.content = content;
        }

        @Override
        public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
            FluidBox box = content.box(blockEntity);
            if (box == null || box.fluid().isEmpty()) {
                return;
            }
            VertexConsumer buffer = buffers.getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));
            cuboid(poseStack.last(), buffer, box, sprite(box.fluid()), tint(box.fluid()), light);
        }
    }
    //?} else {
    /*private static final class FluidBoxState extends BlockEntityRenderState {
        @Nullable
        FluidBox box;
        @Nullable
        TextureAtlasSprite sprite;
        int tint;
    }

    private static final class FluidBoxRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, FluidBoxState> {
        private final FluidContent<T> content;

        FluidBoxRenderer(FluidContent<T> content) {
            this.content = content;
        }

        @Override
        public FluidBoxState createRenderState() {
            return new FluidBoxState();
        }

        @Override
        public void extractRenderState(T blockEntity, FluidBoxState state, float partialTicks, Vec3 cameraPosition,
                                       ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
            BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
            FluidBox box = content.box(blockEntity);
            state.box = box == null || box.fluid().isEmpty() ? null : box;
            if (state.box != null) {
                state.sprite = sprite(box.fluid());
                state.tint = tint(box.fluid());
            }
        }

        @Override
        public void submit(FluidBoxState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
            if (state.box == null || state.sprite == null) {
                return;
            }
            FluidBox box = state.box;
            TextureAtlasSprite sprite = state.sprite;
            int tint = state.tint;
            int light = state.lightCoords;
            collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS),
                (pose, buffer) -> cuboid(pose, buffer, box, sprite, tint, light));
        }
    }
    *///?}
}
