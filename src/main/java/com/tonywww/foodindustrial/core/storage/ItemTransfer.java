package com.tonywww.foodindustrial.core.storage;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * Item access to a neighbouring block (any mod's inventory), obtained from
 * {@code PlatformBlockEntity#neighbors()}.
 */
public interface ItemTransfer {
    /** Inserts as much of {@code stack} as possible; returns the remainder (never mutates the argument). */
    ItemStack insert(ItemStack stack, boolean simulate);

    /** Extracts up to {@code maxCount} items of a single kind accepted by {@code filter}. */
    ItemStack extract(int maxCount, Predicate<ItemStack> filter, boolean simulate);
}
