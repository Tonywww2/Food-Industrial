package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.entity.PortBlockEntity;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

/** Menu types of the machine screens and how blocks open them. */
public final class ModMenus {
    public static final Supplier<MenuType<MachineMenu>> MACHINE = PlatformMenus.register("machine", MachineMenu::new);
    public static final Supplier<MenuType<PortMenu>> PORT = PlatformMenus.register("port", PortMenu::new);

    private ModMenus() {
    }

    /** Registers the menus, packets, language entries and, on the client, the screens. */
    public static void bootstrap() {
        MenuNetwork.bootstrap();
        GuiText.bootstrap();
        if (Platform.isPhysicalClient()) {
            com.tonywww.foodindustrial.gui.client.ClientGui.bootstrap();
        }
    }

    /** Opens a machine's screen for {@code player} (server side). */
    public static void open(ServerPlayer player, MachineBlockEntity machine) {
        PlatformMenus.open(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return machine.getBlockState().getBlock().getName();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player opener) {
                return new MachineMenu(containerId, inventory, machine);
            }
        }, out -> MachineMenu.writeOpenData(machine, out));
    }

    /** Opens a port's screen for {@code player} (server side). */
    public static void open(ServerPlayer player, PortBlockEntity port) {
        PlatformMenus.open(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return port.getBlockState().getBlock().getName();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player opener) {
                return new PortMenu(containerId, inventory, port);
            }
        }, out -> PortMenu.writeOpenData(port, out));
    }
}
