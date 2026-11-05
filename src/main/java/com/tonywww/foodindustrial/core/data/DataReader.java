package com.tonywww.foodindustrial.core.data;

import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * Version-independent reader matching {@link DataWriter}. Missing keys yield the given default,
 * an empty stack or an empty optional.
 */
public interface DataReader {
    boolean getBoolean(String key, boolean defaultValue);

    byte getByte(String key, byte defaultValue);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    String getString(String key, String defaultValue);

    /** The stored array, or an empty array. */
    int[] getIntArray(String key);

    /** The stored stack, or {@link ItemStack#EMPTY}. */
    ItemStack getItem(String key);

    /** The stored fluid amount, or {@link FluidAmount#EMPTY}. */
    default FluidAmount getFluid(String key) {
        return getCodec(key, FluidAmount.CODEC).orElse(FluidAmount.EMPTY);
    }

    <T> Optional<T> getCodec(String key, Codec<T> codec);

    /** Nested compound, or an empty reader when absent. */
    DataReader child(String key);

    /** Compounds of a list written with {@link DataWriter#list}; empty when absent. */
    List<DataReader> list(String key);
}
