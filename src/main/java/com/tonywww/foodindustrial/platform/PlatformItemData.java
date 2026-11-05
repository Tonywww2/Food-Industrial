package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
//? if >=1.21 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
*///?}

/**
 * Mod data on item stacks (machine settings, redprints): a compound under {@code key} in the stack's
 * NBT on 1.20.1 and in the {@code minecraft:custom_data} component from 1.21 on. Stacks with different
 * data do not stack, so write only what differs from the default.
 */
public final class PlatformItemData {
    private PlatformItemData() {
    }

    /** Whether the stack holds data under {@code key}. */
    public static boolean has(ItemStack stack, String key) {
        CompoundTag root = root(stack);
        return root != null && root.contains(key);
    }

    /**
     * The data under {@code key}, or {@code null}. {@code registries} may be {@code null} on 1.20.1;
     * from 1.21 on it is needed to read item stacks inside the data.
     */
    @Nullable
    public static DataReader read(ItemStack stack, String key, @Nullable HolderLookup.Provider registries) {
        CompoundTag root = root(stack);
        if (root == null || !root.contains(key)) {
            return null;
        }
        //? if >=26.1 {
        /*CompoundTag data = root.getCompoundOrEmpty(key);
        *///?} else {
        CompoundTag data = root.getCompound(key);
        //?}
        return PlatformData.readCompound(registries, data);
    }

    /** A string field of the data under {@code key} without registry access (tooltips), or empty. */
    public static String getString(ItemStack stack, String key, String field) {
        CompoundTag root = root(stack);
        if (root == null || !root.contains(key)) {
            return "";
        }
        //? if >=26.1 {
        /*return root.getCompoundOrEmpty(key).getStringOr(field, "");
        *///?} else {
        return root.getCompound(key).getString(field);
        //?}
    }

    /** An int field of the data under {@code key} without registry access, or {@code defaultValue}. */
    public static int getInt(ItemStack stack, String key, String field, int defaultValue) {
        CompoundTag root = root(stack);
        if (root == null || !root.contains(key)) {
            return defaultValue;
        }
        //? if >=26.1 {
        /*return root.getCompoundOrEmpty(key).getIntOr(field, defaultValue);
        *///?} else {
        CompoundTag data = root.getCompound(key);
        return data.contains(field) ? data.getInt(field) : defaultValue;
        //?}
    }

    /** Replaces the data under {@code key}; when {@code body} writes nothing the key is removed. */
    public static void write(ItemStack stack, String key, @Nullable HolderLookup.Provider registries, Consumer<DataWriter> body) {
        CompoundTag data = PlatformData.writeCompound(registries, body);
        if (data.isEmpty()) {
            remove(stack, key);
            return;
        }
        //? if >=1.21 {
        /*CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(key, data));
        *///?} else {
        stack.getOrCreateTag().put(key, data);
        //?}
    }

    public static void remove(ItemStack stack, String key) {
        //? if >=1.21 {
        /*if (has(stack, key)) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(key));
        }
        *///?} else {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(key);
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
        //?}
    }

    @Nullable
    private static CompoundTag root(ItemStack stack) {
        //? if >=1.21 {
        /*CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null || data.isEmpty() ? null : data.copyTag();
        *///?} else {
        CompoundTag tag = stack.getTag();
        return tag != null && !tag.isEmpty() ? tag : null;
        //?}
    }
}
