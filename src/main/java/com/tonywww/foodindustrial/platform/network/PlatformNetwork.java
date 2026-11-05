package com.tonywww.foodindustrial.platform.network;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.platform.PlatformData;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
//? if forge {
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;
//?} else {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
*///?}

/**
 * Packets between client and server. A payload writes itself with a {@link DataWriter}; the wire
 * format is a compound tag on every node. Declare channels during mod construction (static
 * initializers triggered from {@code FoodIndustrial.init()}).
 * <p>
 * Server handlers run on the server thread with the sending player; client handlers run on the
 * client thread and may reference client classes.
 */
public final class PlatformNetwork {
    private static final String PROTOCOL = "1";
    private static final List<Channel<?>> CHANNELS = new ArrayList<>();

    private PlatformNetwork() {
    }

    /** A packet body. */
    public interface Payload {
        void write(DataWriter out);
    }

    @FunctionalInterface
    public interface Decoder<T> {
        T read(DataReader in);
    }

    @FunctionalInterface
    public interface ServerHandler<T> {
        void handle(T payload, ServerPlayer sender);
    }

    @FunctionalInterface
    public interface ClientHandler<T> {
        void handle(T payload);
    }

    /** A declared packet type. */
    public static final class Channel<T extends Payload> {
        private final String name;
        private final int index;
        private final Decoder<T> decoder;
        @Nullable
        private final ServerHandler<T> serverHandler;
        @Nullable
        private final ClientHandler<T> clientHandler;
        //? if neoforge {
        /*private final CustomPacketPayload.Type<Envelope> type;
        *///?}

        private Channel(String name, int index, Decoder<T> decoder, @Nullable ServerHandler<T> serverHandler, @Nullable ClientHandler<T> clientHandler) {
            this.name = name;
            this.index = index;
            this.decoder = decoder;
            this.serverHandler = serverHandler;
            this.clientHandler = clientHandler;
            //? if neoforge {
            /*this.type = new CustomPacketPayload.Type<>(PlatformIdentifier.id(name));
            *///?}
        }

        public String name() {
            return name;
        }

        @SuppressWarnings("unchecked")
        private void dispatchServer(Object payload, ServerPlayer sender) {
            if (serverHandler != null) {
                serverHandler.handle((T) payload, sender);
            }
        }

        @SuppressWarnings("unchecked")
        private void dispatchClient(Object payload) {
            if (clientHandler != null) {
                clientHandler.handle((T) payload);
            }
        }
    }

    /** Declares a client → server packet. */
    public static <T extends Payload> Channel<T> toServer(String name, Decoder<T> decoder, ServerHandler<T> handler) {
        return add(new Channel<>(name, CHANNELS.size(), decoder, handler, null));
    }

    /** Declares a server → client packet. */
    public static <T extends Payload> Channel<T> toClient(String name, Decoder<T> decoder, ClientHandler<T> handler) {
        return add(new Channel<>(name, CHANNELS.size(), decoder, null, handler));
    }

    private static <T extends Payload> Channel<T> add(Channel<T> channel) {
        CHANNELS.add(channel);
        return channel;
    }

    //? if forge {
    /** Forge: one SimpleChannel carrying an envelope (channel index + compound). */
    private record Envelope(int index, CompoundTag data) {
    }

    private static final SimpleChannel SIMPLE_CHANNEL = NetworkRegistry.ChannelBuilder
        .named(PlatformIdentifier.id("main"))
        .networkProtocolVersion(() -> PROTOCOL)
        .clientAcceptedVersions(PROTOCOL::equals)
        .serverAcceptedVersions(PROTOCOL::equals)
        .simpleChannel();

    /** Registers the envelope message. Called by the mod entrypoint. */
    public static void bind(IEventBus modBus) {
        SIMPLE_CHANNEL.messageBuilder(Envelope.class, 0)
            .encoder((envelope, buf) -> {
                buf.writeVarInt(envelope.index());
                buf.writeNbt(envelope.data());
            })
            .decoder(buf -> new Envelope(buf.readVarInt(), buf.readNbt()))
            .consumerMainThread(PlatformNetwork::handle)
            .add();
    }

    private static void handle(Envelope envelope, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (envelope.index() < 0 || envelope.index() >= CHANNELS.size() || envelope.data() == null) {
            return;
        }
        Channel<?> channel = CHANNELS.get(envelope.index());
        Object payload = channel.decoder.read(PlatformData.readCompound(null, envelope.data()));
        if (context.getDirection().getReceptionSide().isServer()) {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                channel.dispatchServer(payload, sender);
            }
        } else {
            channel.dispatchClient(payload);
        }
    }

    private static <T extends Payload> Envelope envelope(Channel<T> channel, T payload) {
        return new Envelope(channel.index, PlatformData.writeCompound(null, payload::write));
    }

    public static <T extends Payload> void sendToServer(Channel<T> channel, T payload) {
        SIMPLE_CHANNEL.sendToServer(envelope(channel, payload));
    }

    public static <T extends Payload> void sendToPlayer(ServerPlayer player, Channel<T> channel, T payload) {
        SIMPLE_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), envelope(channel, payload));
    }

    public static <T extends Payload> void sendToAll(Channel<T> channel, T payload) {
        SIMPLE_CHANNEL.send(PacketDistributor.ALL.noArg(), envelope(channel, payload));
    }
    //?} else {
    /*/^* NeoForge: one payload type per channel, wrapping the decoded business payload. ^/
    private record Envelope(CustomPacketPayload.Type<Envelope> type, Channel<?> channel, Object value) implements CustomPacketPayload {
    }

    /^* Registers the payload types on the mod bus. Called by the mod entrypoint. ^/
    public static void bind(IEventBus modBus) {
        modBus.addListener(PlatformNetwork::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL);
        for (Channel<?> channel : CHANNELS) {
            StreamCodec<RegistryFriendlyByteBuf, Envelope> codec = StreamCodec.of(
                (buf, envelope) -> buf.writeNbt(PlatformData.writeCompound(buf.registryAccess(), out -> ((Payload) envelope.value()).write(out))),
                buf -> new Envelope(channel.type, channel, channel.decoder.read(PlatformData.readCompound(buf.registryAccess(), buf.readNbt()))));
            if (channel.serverHandler != null) {
                registrar.playToServer(channel.type, codec, PlatformNetwork::handleServer);
            } else {
                registrar.playToClient(channel.type, codec, PlatformNetwork::handleClient);
            }
        }
    }

    private static void handleServer(Envelope envelope, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sender) {
                envelope.channel().dispatchServer(envelope.value(), sender);
            }
        });
    }

    private static void handleClient(Envelope envelope, IPayloadContext context) {
        context.enqueueWork(() -> envelope.channel().dispatchClient(envelope.value()));
    }

    public static <T extends Payload> void sendToServer(Channel<T> channel, T payload) {
        com.tonywww.foodindustrial.platform.client.ClientHooks.sendToServer(new Envelope(channel.type, channel, payload));
    }

    public static <T extends Payload> void sendToPlayer(ServerPlayer player, Channel<T> channel, T payload) {
        PacketDistributor.sendToPlayer(player, new Envelope(channel.type, channel, payload));
    }

    public static <T extends Payload> void sendToAll(Channel<T> channel, T payload) {
        PacketDistributor.sendToAllPlayers(new Envelope(channel.type, channel, payload));
    }
    *///?}
}
