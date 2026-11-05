package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.FoodIndustrial;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
//? if >=26.1 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

/**
 * Identifier construction. The type is {@code ResourceLocation} before 26.1 and {@code Identifier} from
 * 26.1 on, so business code should not name it: keep results in {@code var} locals, pass them straight
 * into Minecraft APIs, or use {@link #key} to get a version-independent {@link ResourceKey}.
 */
public final class PlatformIdentifier {
    private PlatformIdentifier() {
    }

    /** {@code foodindustrial:<path>} as a {@link ResourceKey} of the given registry. */
    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String path) {
        return ResourceKey.create(registry, id(path));
    }

    /** {@code <namespace>:<path>} as a {@link ResourceKey} of the given registry. */
    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String namespace, String path) {
        return ResourceKey.create(registry, id(namespace, path));
    }

    //? if >=26.1 {
    /*public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(FoodIndustrial.MOD_ID, path);
    }

    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier vanilla(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    public static Identifier parse(String value) {
        return Identifier.parse(value);
    }
    *///?} else {
    // Forge 47.4.x backports the 1.21 factory methods and deprecates the constructors.
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(FoodIndustrial.MOD_ID, path);
    }

    public static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation parse(String value) {
        return ResourceLocation.parse(value);
    }
    //?}
}
