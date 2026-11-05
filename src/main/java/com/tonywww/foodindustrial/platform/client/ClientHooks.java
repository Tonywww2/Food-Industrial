package com.tonywww.foodindustrial.platform.client;

import net.minecraft.client.Minecraft;
//? if <26.1 {
import net.minecraft.client.gui.screens.Screen;
//?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.network.PacketDistributor;
*///?}
//? if >=26.1 {
/*import net.neoforged.neoforge.client.network.ClientPacketDistributor;
*///?}

/**
 * Client-only helpers. Only reference this class behind a physical-client check.
 */
public final class ClientHooks {
    private ClientHooks() {
    }

    public static boolean isShiftDown() {
        //? if >=26.1 {
        /*return Minecraft.getInstance().hasShiftDown();
        *///?} else {
        return Screen.hasShiftDown();
        //?}
    }

    public static boolean isAltDown() {
        //? if >=26.1 {
        /*return Minecraft.getInstance().hasAltDown();
        *///?} else {
        return Screen.hasAltDown();
        //?}
    }

    public static boolean isControlDown() {
        //? if >=26.1 {
        /*return Minecraft.getInstance().hasControlDown();
        *///?} else {
        return Screen.hasControlDown();
        //?}
    }

    /** A small red marker particle at a position in the client world (structure highlights). */
    public static void markerParticle(double x, double y, double z) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        //? if >=26.1 {
        /*minecraft.level.addParticle(new net.minecraft.core.particles.DustParticleOptions(0xFF3030, 1.0F), x, y, z, 0, 0, 0);
        *///?} else {
        minecraft.level.addParticle(new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(1.0F, 0.19F, 0.19F), 1.0F), x, y, z, 0, 0, 0);
        //?}
    }

    /** The local player's open menu, or {@code null}. */
    @org.jetbrains.annotations.Nullable
    public static net.minecraft.world.inventory.AbstractContainerMenu openMenu() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player == null ? null : minecraft.player.containerMenu;
    }

    /** The client's registry access, or {@code null} outside a world. */
    @org.jetbrains.annotations.Nullable
    public static net.minecraft.core.RegistryAccess registryAccess() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? null : minecraft.level.registryAccess();
    }

    //? if neoforge && <26.1 {
    /*public static void sendToServer(net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
    *///?}

    //? if >=26.1 {
    /*public static void sendToServer(net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
    *///?}
}
