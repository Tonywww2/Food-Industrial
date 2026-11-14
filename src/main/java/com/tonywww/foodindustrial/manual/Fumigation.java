package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.platform.PlatformItemData;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

/**
 * H09 sulfur fumigation (additives/07 §操作与实现规格, 2026-11-10): food in the item tag
 * {@code foodindustrial:fumigatable} gets a “sulfured” mark; each fumigation burns one sulfur. The
 * effect of the mark is decided with the finished products.
 */
public final class Fumigation {
    /** Ticks of one fumigation (game setting, 2026-11-10). */
    public static final int TIME = 1200;
    /** Item data key of the mark. */
    public static final String MARK = "foodindustrial:sulfured";
    public static final String FOOD_TAG = "foodindustrial:fumigatable";
    public static final String FUEL_TAG = "foodindustrial:chemicals/sulfur";

    private Fumigation() {
    }

    /** Food that can be fumigated and is not yet. */
    public static boolean canFumigate(ItemStack stack) {
        return !stack.isEmpty() && stack.is(PlatformTags.itemTag(FOOD_TAG)) && !isMarked(stack);
    }

    public static boolean isMarked(ItemStack stack) {
        return !stack.isEmpty() && PlatformItemData.has(stack, MARK);
    }

    public static boolean isFuel(ItemStack stack) {
        return !stack.isEmpty() && stack.is(PlatformTags.itemTag(FUEL_TAG));
    }

    /** A copy of {@code stack} with the mark. */
    public static ItemStack mark(ItemStack stack, HolderLookup.Provider registries) {
        ItemStack copy = stack.copy();
        PlatformItemData.write(copy, MARK, registries, out -> out.putBoolean("Done", true));
        return copy;
    }
}
