package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.platform.client.ClientHooks;
import com.tonywww.foodindustrial.platform.network.PlatformNetwork;
import net.minecraft.server.level.ServerPlayer;

/**
 * Packets of the machine screens (machines/04 §6): views from the server while a menu is open, and
 * actions from the client, which the server accepts only for the sender's open, still valid menu.
 */
final class MenuNetwork {
    record ViewPayload(int containerId, int groups, MachineView view) implements PlatformNetwork.Payload {
        @Override
        public void write(DataWriter out) {
            out.putInt("Id", containerId);
            out.putInt("Groups", groups);
            view.write(out.child("View"), groups);
        }

        static ViewPayload read(DataReader in) {
            int groups = in.getInt("Groups", 0);
            return new ViewPayload(in.getInt("Id", -1), groups, MachineView.read(in.child("View"), groups));
        }
    }

    record ActionPayload(int containerId, int action, int a, int b) implements PlatformNetwork.Payload {
        @Override
        public void write(DataWriter out) {
            out.putInt("Id", containerId);
            out.putInt("Action", action);
            out.putInt("A", a);
            out.putInt("B", b);
        }

        static ActionPayload read(DataReader in) {
            return new ActionPayload(in.getInt("Id", -1), in.getInt("Action", -1), in.getInt("A", 0), in.getInt("B", 0));
        }
    }

    private static final PlatformNetwork.Channel<ViewPayload> VIEW =
        PlatformNetwork.toClient("machine_view", ViewPayload::read, MenuNetwork::onView);
    private static final PlatformNetwork.Channel<ActionPayload> ACTION =
        PlatformNetwork.toServer("machine_action", ActionPayload::read, MenuNetwork::onAction);

    private MenuNetwork() {
    }

    /** Declares the channels. Called during mod construction. */
    static void bootstrap() {
    }

    static void sendView(ServerPlayer player, int containerId, int groups, MachineView view) {
        PlatformNetwork.sendToPlayer(player, VIEW, new ViewPayload(containerId, groups, view));
    }

    static void sendAction(int containerId, int action, int a, int b) {
        PlatformNetwork.sendToServer(ACTION, new ActionPayload(containerId, action, a, b));
    }

    private static void onView(ViewPayload payload) {
        if (ClientHooks.openMenu() instanceof FIMenu menu && menu.containerId == payload.containerId()) {
            menu.receive(payload.groups(), payload.view());
        }
    }

    private static void onAction(ActionPayload payload, ServerPlayer sender) {
        if (sender.containerMenu instanceof FIMenu menu && menu.containerId == payload.containerId()) {
            menu.runAction(sender, payload.action(), payload.a(), payload.b());
        }
    }
}
