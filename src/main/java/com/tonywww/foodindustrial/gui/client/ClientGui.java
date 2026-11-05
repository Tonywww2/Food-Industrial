package com.tonywww.foodindustrial.gui.client;

import com.tonywww.foodindustrial.gui.ModMenus;
import com.tonywww.foodindustrial.platform.client.PlatformClient;

/** Client side of the machine screens: screen classes and the structure highlight. */
public final class ClientGui {
    private ClientGui() {
    }

    /** Called during mod construction on the physical client. */
    public static void bootstrap() {
        PlatformClient.registerScreen(ModMenus.MACHINE, MachineScreen::new);
        PlatformClient.registerScreen(ModMenus.PORT, PortScreen::new);
        PlatformClient.onClientTick(StructureHighlight::tick);
    }
}
