package com.tonywww.foodindustrial.platform;

import net.minecraft.world.item.ItemStack;

/**
 * Item stack helpers whose vanilla names differ between versions.
 */
public final class PlatformItems {
    private PlatformItems() {
    }

    /** Same item and same NBT (1.20.1) / data components (1.21+); counts are ignored. */
    public static boolean sameItemSameData(ItemStack a, ItemStack b) {
        //? if >=1.21 {
        /*return ItemStack.isSameItemSameComponents(a, b);
        *///?} else {
        return ItemStack.isSameItemSameTags(a, b);
        //?}
    }
}
