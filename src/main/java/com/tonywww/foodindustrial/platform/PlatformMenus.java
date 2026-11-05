package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;
import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;
//?} else {
/*import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
*///?}

/**
 * Menu types that receive extra data when opened (block position, mode, configuration). The extra
 * data is written with a {@link DataWriter} on the server and read back on the client.
 */
public final class PlatformMenus {
    private PlatformMenus() {
    }

    @FunctionalInterface
    public interface Factory<M extends AbstractContainerMenu> {
        /** Creates the menu; on the client {@code extra} holds what {@link #open} wrote. */
        M create(int containerId, Inventory inventory, DataReader extra);
    }

    /** Registers {@code foodindustrial:<name>}. */
    public static <M extends AbstractContainerMenu> Supplier<MenuType<M>> register(String name, Factory<M> factory) {
        //? if forge {
        return PlatformRegistry.register(Registries.MENU, name, () -> IForgeMenuType.create(
            (containerId, inventory, buf) -> factory.create(containerId, inventory, PlatformData.readCompound(null, readTag(buf == null ? null : buf.readNbt())))));
        //?} else {
        /*return PlatformRegistry.register(Registries.MENU, name, () -> IMenuTypeExtension.create(
            (containerId, inventory, buf) -> factory.create(containerId, inventory, PlatformData.readCompound(buf.registryAccess(), readTag(buf.readNbt())))));
        *///?}
    }

    /** Opens {@code provider}'s menu for {@code player}, sending {@code extra} to the client. */
    public static void open(ServerPlayer player, MenuProvider provider, Consumer<DataWriter> extra) {
        //? if forge {
        NetworkHooks.openScreen(player, provider, buf -> buf.writeNbt(PlatformData.writeCompound(null, extra)));
        //?} else {
        /*player.openMenu(provider, buf -> buf.writeNbt(PlatformData.writeCompound(buf.registryAccess(), extra)));
        *///?}
    }

    private static CompoundTag readTag(CompoundTag tag) {
        return tag == null ? new CompoundTag() : tag;
    }
}
