package com.tonywww.foodindustrial.platform;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
//? if >=26.1 {
/*import net.minecraft.world.item.ItemUseAnimation;
*///?} else {
import net.minecraft.world.item.UseAnim;
//?}
//? if >=1.21 {
/*import net.minecraft.core.component.DataComponents;
*///?}

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

    /** Wears the tool in {@code hand} by {@code amount} (breaks it at zero; none in creative). */
    public static void damage(ItemStack stack, int amount, Player player, InteractionHand hand) {
        //? if >=1.21 {
        /*stack.hurtAndBreak(amount, player, hand == InteractionHand.MAIN_HAND
            ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        *///?} else {
        stack.hurtAndBreak(amount, player, owner -> owner.broadcastBreakEvent(hand));
        //?}
    }

    /** {@code properties} made edible: {@code nutrition} hunger points, saturation modifier {@code saturation}. */
    public static Item.Properties food(Item.Properties properties, int nutrition, float saturation) {
        //? if >=1.21 {
        /*FoodProperties food = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
        *///?} else {
        FoodProperties food = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build();
        //?}
        return properties.food(food);
    }

    /** How an item is consumed, by its use animation. */
    public enum ConsumeKind {
        EAT, DRINK, NONE
    }

    /** Whether using {@code stack} eats it, drinks it, or neither (by its use animation). */
    public static ConsumeKind consumeKind(ItemStack stack) {
        //? if >=26.1 {
        /*ItemUseAnimation animation = stack.getUseAnimation();
        return animation == ItemUseAnimation.EAT ? ConsumeKind.EAT : animation == ItemUseAnimation.DRINK ? ConsumeKind.DRINK : ConsumeKind.NONE;
        *///?} else {
        UseAnim animation = stack.getUseAnimation();
        return animation == UseAnim.EAT ? ConsumeKind.EAT : animation == UseAnim.DRINK ? ConsumeKind.DRINK : ConsumeKind.NONE;
        //?}
    }

    /**
     * Food values of an item.
     *
     * @param nutrition  hunger points restored
     * @param saturation saturation points restored (not the modifier)
     */
    public record FoodValues(int nutrition, float saturation) {
    }

    /** Food values of {@code stack}, or {@code null} when it is not food. */
    @Nullable
    public static FoodValues foodValues(ItemStack stack) {
        //? if >=1.21 {
        /*FoodProperties food = stack.get(DataComponents.FOOD);
        return food == null ? null : new FoodValues(food.nutrition(), food.saturation());
        *///?} else {
        FoodProperties food = stack.getFoodProperties(null);
        return food == null ? null : new FoodValues(food.getNutrition(), food.getNutrition() * food.getSaturationModifier() * 2.0F);
        //?}
    }

    /**
     * Whether {@code stack} is eaten or drunk from the hand: 26.1 asks the consumable component (fish buckets carry a
     * food component without it); before, food items and drinkable items count, thrown potions do not.
     */
    public static boolean consumable(ItemStack stack) {
        //? if >=26.1 {
        /*return stack.has(DataComponents.CONSUMABLE);
        *///?} else {
        if (stack.getItem() instanceof net.minecraft.world.item.ThrowablePotionItem) {
            return false;
        }
        return foodValues(stack) != null || consumeKind(stack) != ConsumeKind.NONE;
        //?}
    }

    /** Whether {@code item} places a block that is eaten by bites: the cake, Farmer's Delight's pies. */
    public static boolean eatenByBites(Item item) {
        if (!(item instanceof net.minecraft.world.item.BlockItem blockItem)) {
            return false;
        }
        net.minecraft.world.level.block.Block block = blockItem.getBlock();
        if (block instanceof net.minecraft.world.level.block.CakeBlock) {
            return true;
        }
        for (Class<?> type = block.getClass(); type != null; type = type.getSuperclass()) {
            if (type.getName().equals("vectorwing.farmersdelight.common.block.PieBlock")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Food values of an item's default stack, or {@code null} when it is not food or (26.1 data generation) its
     * components are not bound yet.
     */
    @Nullable
    public static FoodValues foodValues(Item item) {
        //? if >=26.1 {
        /*if (!item.builtInRegistryHolder().areComponentsBound()) {
            return null;
        }
        *///?}
        return foodValues(new ItemStack(item));
    }

    /** Translation key of an item's name. */
    public static String descriptionKey(Item item) {
        return item.getDescriptionId();
    }
}
