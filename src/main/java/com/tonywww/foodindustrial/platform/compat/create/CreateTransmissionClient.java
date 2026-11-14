package com.tonywww.foodindustrial.platform.compat.create;
//? if <26.1 {

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
//? if forge {
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
*///?}

/** Client side of the transmission interface (I25): the half shaft on top turns, with or without Flywheel. */
final class CreateTransmissionClient {
    private CreateTransmissionClient() {
    }

    static void bind(IEventBus modBus) {
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> SimpleBlockEntityVisualizer.builder(CreateTransmission.type.get())
            .factory((context, be, partialTick) -> new OrientedRotatingVisual<>(context, be, partialTick, Direction.SOUTH, Direction.UP,
                Models.partial(AllPartialModels.SHAFT_HALF)))
            .skipVanillaRender(be -> true)
            .apply()));
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
            event.registerBlockEntityRenderer(CreateTransmission.type.get(), Renderer::new));
    }

    /** Without Flywheel: the half shaft drawn by the block entity renderer. */
    private static final class Renderer extends KineticBlockEntityRenderer<TransmissionInterfaceBlockEntity> {
        Renderer(BlockEntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        protected SuperByteBuffer getRotatedModel(TransmissionInterfaceBlockEntity be, BlockState state) {
            return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, Direction.UP);
        }
    }
}
//?}
