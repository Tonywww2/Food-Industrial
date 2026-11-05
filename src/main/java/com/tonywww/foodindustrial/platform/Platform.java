package com.tonywww.foodindustrial.platform;

//? if forge {
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
//?} else {
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
*///?}

/**
 * Loader / environment queries with a stable signature on every node.
 */
public final class Platform {
    private Platform() {
    }

    /** {@code "forge"} or {@code "neoforge"}. */
    public static String loaderName() {
        //? if forge {
        return "forge";
        //?} else {
        /*return "neoforge";
        *///?}
    }

    /** Minecraft version this jar was built for, for example {@code "1.20.1"}. */
    public static String minecraftVersion() {
        return ModList.get().getModContainerById("minecraft")
            .map(container -> container.getModInfo().getVersion().toString())
            .orElse("unknown");
    }

    /** Whether another mod is present (use before touching an optional integration). */
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    /** {@code true} on the physical client (also true for the integrated server's JVM). */
    public static boolean isPhysicalClient() {
        //? if >=26.1 {
        /*return FMLEnvironment.getDist().isClient();
        *///?} else {
        return FMLEnvironment.dist.isClient();
        //?}
    }

    /** Whether Shift is held on the client; always {@code false} on a dedicated server. */
    public static boolean isShiftDown() {
        return isPhysicalClient() && com.tonywww.foodindustrial.platform.client.ClientHooks.isShiftDown();
    }

    /** Whether Alt is held on the client; always {@code false} on a dedicated server. */
    public static boolean isAltDown() {
        return isPhysicalClient() && com.tonywww.foodindustrial.platform.client.ClientHooks.isAltDown();
    }

    /** Whether Ctrl (Cmd on macOS) is held on the client; always {@code false} on a dedicated server. */
    public static boolean isControlDown() {
        return isPhysicalClient() && com.tonywww.foodindustrial.platform.client.ClientHooks.isControlDown();
    }

    /** {@code true} when running from a development environment (runClient / runServer). */
    public static boolean isDevelopment() {
        //? if >=26.1 {
        /*return !FMLEnvironment.isProduction();
        *///?} else {
        return !FMLLoader.isProduction();
        //?}
    }
}
