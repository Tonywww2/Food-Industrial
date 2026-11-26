package com.tonywww.foodindustrial.platform.food;

import net.minecraft.world.item.ItemStack;
//? if >=1.21 {
/*import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;
*///?} else {
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
//?}

/**
 * Food data on item stacks (I38, foods/04 §7, foods/08 §3): the packed quality code (int tag {@code FoodQuality} on
 * 1.20.1, data component {@code foodindustrial:food_quality} from 1.21 on) and the semi-finished key
 * {@code product|stage} (string tag {@code SemiFinished}, component {@code foodindustrial:semi_finished}). Use the
 * business classes in {@code food.quality} rather than calling this directly.
 */
public final class PlatformFoodData {
    public static final String QUALITY_TAG = "FoodQuality";
    public static final String SEMI_TAG = "SemiFinished";

    //? if >=1.21 {
    /*public static final Supplier<DataComponentType<Integer>> QUALITY = PlatformRegistry.register(
        Registries.DATA_COMPONENT_TYPE, "food_quality",
        () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final Supplier<DataComponentType<String>> SEMI = PlatformRegistry.register(
        Registries.DATA_COMPONENT_TYPE, "semi_finished",
        () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
    *///?}

    private PlatformFoodData() {
    }

    /** Registers the data components during mod construction. */
    public static void bootstrap() {
    }

    /** Quality code of the stack, {@code 0} when it has none. */
    public static int quality(ItemStack stack) {
        //? if >=1.21 {
        /*Integer value = stack.get(QUALITY.get());
        return value == null ? 0 : value;
        *///?} else {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(QUALITY_TAG, Tag.TAG_INT) ? tag.getInt(QUALITY_TAG) : 0;
        //?}
    }

    /** Writes the quality code; {@code 0} removes it. */
    public static void setQuality(ItemStack stack, int code) {
        //? if >=1.21 {
        /*if (code == 0) {
            stack.remove(QUALITY.get());
        } else {
            stack.set(QUALITY.get(), code);
        }
        *///?} else {
        if (code == 0) {
            remove(stack, QUALITY_TAG);
        } else {
            stack.getOrCreateTag().putInt(QUALITY_TAG, code);
        }
        //?}
    }

    /**
     * Multiplies the saturation of a food stack by {@code factor} (I39, foods/04 §5.3) from 1.21 on, where the food
     * component is the stack's own; nothing on 1.20.1, where the item answers per stack
     * ({@code PlatformItem#saturationFactor}). Scales the item's own value, so calling it again replaces the factor.
     */
    public static void scaleSaturation(ItemStack stack, float factor) {
        //? if >=26.1 {
        /*net.minecraft.world.food.FoodProperties food = stack.getItem().components().get(net.minecraft.core.component.DataComponents.FOOD);
        if (food != null) { // factor 1 equals the prototype, which clears the patch
            stack.set(net.minecraft.core.component.DataComponents.FOOD,
                new net.minecraft.world.food.FoodProperties(food.nutrition(), food.saturation() * factor, food.canAlwaysEat()));
        }
        *///?}
        //? if >=1.21 && <26.1 {
        /*net.minecraft.world.food.FoodProperties food = stack.getItem().components().get(net.minecraft.core.component.DataComponents.FOOD);
        if (food != null) { // factor 1 equals the prototype, which clears the patch
            stack.set(net.minecraft.core.component.DataComponents.FOOD, new net.minecraft.world.food.FoodProperties(food.nutrition(),
                food.saturation() * factor, food.canAlwaysEat(), food.eatSeconds(), food.usingConvertsTo(), food.effects()));
        }
        *///?}
    }

    /** Semi-finished key {@code product|stage}, or empty. */
    public static String semiFinished(ItemStack stack) {
        //? if >=1.21 {
        /*String value = stack.get(SEMI.get());
        return value == null ? "" : value;
        *///?} else {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(SEMI_TAG, Tag.TAG_STRING) ? tag.getString(SEMI_TAG) : "";
        //?}
    }

    public static void setSemiFinished(ItemStack stack, String key) {
        //? if >=1.21 {
        /*if (key.isEmpty()) {
            stack.remove(SEMI.get());
        } else {
            stack.set(SEMI.get(), key);
        }
        *///?} else {
        if (key.isEmpty()) {
            remove(stack, SEMI_TAG);
        } else {
            stack.getOrCreateTag().putString(SEMI_TAG, key);
        }
        //?}
    }

    //? if <1.21 {
    private static void remove(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(key);
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
    }
    //?}
}
