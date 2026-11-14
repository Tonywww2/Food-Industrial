package com.tonywww.foodindustrial.core.data;

import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import net.minecraft.world.item.ItemStack;

/**
 * Version-independent writer for block entity data, sync data and packet payloads. Backed by
 * {@code CompoundTag} on 1.20.1 / 1.21.1 and by {@code ValueOutput} on 26.1; obtained only from
 * {@code platform}.
 */
public interface DataWriter {
    void putBoolean(String key, boolean value);

    void putByte(String key, byte value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putString(String key, String value);

    void putIntArray(String key, int[] value);

    /** Writes a double. */
    default void putDouble(String key, double value) {
        putCodec(key, Codec.DOUBLE, value);
    }

    /** Writes an item stack; empty stacks are written as absent. */
    void putItem(String key, ItemStack stack);

    /** Writes a fluid amount; empty amounts are written as absent. */
    default void putFluid(String key, FluidAmount fluid) {
        if (!fluid.isEmpty()) {
            putCodec(key, FluidAmount.CODEC, fluid);
        }
    }

    /** Writes any value with a codec (registry-aware where the node supports it). */
    <T> void putCodec(String key, Codec<T> codec, T value);

    /** Nested compound under {@code key}. */
    DataWriter child(String key);

    /** List of nested compounds under {@code key}. */
    ListWriter list(String key);

    interface ListWriter {
        /** Appends a new compound to the list and returns its writer. */
        DataWriter add();
    }
}
