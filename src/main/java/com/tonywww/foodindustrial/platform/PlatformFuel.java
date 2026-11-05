package com.tonywww.foodindustrial.platform;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
//? if forge {
import net.minecraftforge.common.ForgeHooks;
//?}

/** Furnace fuel values of the running node (vanilla furnace burn times, modded fuels included). */
public final class PlatformFuel {
    private PlatformFuel() {
    }

    /** Burn time of one item in ticks, as in a vanilla furnace; {@code 0} for non-fuels. */
    public static int burnTime(Level level, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        //? if >=26.1 {
        /*return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues());
        *///?}
        //? if neoforge && <26.1 {
        /*return stack.getBurnTime(RecipeType.SMELTING);
        *///?}
        //? if forge {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
        //?}
    }

    /**
     * The fuel slot after one item of {@code stack} was burnt, as a furnace leaves it: one fewer, or
     * the container (a lava bucket leaves a bucket) when it was the last. Never mutates {@code stack}.
     */
    public static ItemStack afterBurning(ItemStack stack) {
        if (stack.getCount() > 1) {
            return stack.copyWithCount(stack.getCount() - 1);
        }
        //? if >=26.1 {
        /*var remainder = stack.getCraftingRemainder();
        return remainder != null ? remainder.create() : ItemStack.EMPTY;
        *///?} else {
        return stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
        //?}
    }
}
