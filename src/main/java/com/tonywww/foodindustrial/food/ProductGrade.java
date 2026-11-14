package com.tonywww.foodindustrial.food;

import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * Grade label of a finished product (foods/01 §5): from low to high poor, homemade, unmarked and premium. Only
 * read here (the metabolism counts impurity from poor and homemade products, I35); how the label is written to a
 * stack is set by the product batches, which install the reader with {@link #setReader}. Until then every stack
 * is unmarked.
 */
public enum ProductGrade {
    POOR, HOMEMADE, UNMARKED, PREMIUM;

    private static Function<ItemStack, ProductGrade> reader = stack -> UNMARKED;

    /** The grade of {@code stack}. */
    public static ProductGrade of(ItemStack stack) {
        return reader.apply(stack);
    }

    /** Installs how grades are read from stacks. */
    public static void setReader(Function<ItemStack, ProductGrade> value) {
        reader = value;
    }
}
