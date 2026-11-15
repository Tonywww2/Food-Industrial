package com.tonywww.foodindustrial.platform.compat.create05;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.tonywww.foodindustrial.platform.compat.create.CreateTransmission;
import com.tonywww.foodindustrial.platform.compat.create.TransmissionInterfaceBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Client side of the transmission interface on Create 0.5.1: a Flywheel 0.6 instance of the half shaft
 * pointing up, as Create registers its own kinetic blocks (the block renderer is skipped while Flywheel
 * instances), and Create's kinetic renderer for when Flywheel's backend is off.
 */
final class LegacyCreateClient {
    private LegacyCreateClient() {
    }

    static void bind(IEventBus modBus) {
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() ->
            InstancedRenderRegistry.configure(CreateTransmission.blockEntityType())
                .factory(ShaftInstance::new)
                .alwaysSkipRender()
                .apply()));
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
            event.registerBlockEntityRenderer(CreateTransmission.blockEntityType(), Renderer::new));
    }

    private static final class ShaftInstance extends HalfShaftInstance<TransmissionInterfaceBlockEntity> {
        ShaftInstance(MaterialManager materials, TransmissionInterfaceBlockEntity be) {
            super(materials, be);
        }

        @Override
        protected Direction getShaftDirection() {
            return Direction.UP;
        }
    }

    private static final class Renderer extends KineticBlockEntityRenderer<TransmissionInterfaceBlockEntity> {
        Renderer(BlockEntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        protected SuperByteBuffer getRotatedModel(TransmissionInterfaceBlockEntity be, BlockState state) {
            return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, Direction.UP);
        }
    }
}
