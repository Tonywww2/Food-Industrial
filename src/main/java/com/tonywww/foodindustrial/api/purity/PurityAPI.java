package com.tonywww.foodindustrial.api.purity;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Public purity API (additives/10 §4.5). Purity is exchanged as the stored byte of the two-ended
 * logarithmic scale ({@code -128} = 0%, {@code 127} = 100%, see {@link PurityScale}), which compares
 * directly. Grades depend on the material's bounds.
 */
public final class PurityAPI {
    /** Purity of items and fluids that carry none. */
    public static final byte NONE = Byte.MIN_VALUE;

    private PurityAPI() {
    }

    /**
     * Effective purity byte of an item, with the rules for foreign items applied; {@link #NONE}
     * (same as 0%) for items without purity.
     */
    public static byte getPurity(ItemStack stack) {
        int b = Purity.get(stack);
        return b == Purity.NONE ? NONE : (byte) b;
    }

    /** Effective purity byte of a fluid, as {@link #getPurity(ItemStack)}. */
    public static byte getPurity(FluidAmount fluid) {
        int b = Purity.get(fluid);
        return b == Purity.NONE ? NONE : (byte) b;
    }

    /** Grade by the material's bounds; empty for K0 materials and items without purity. */
    public static Optional<PurityGrade> gradeOf(ItemStack stack) {
        return Purity.grade(stack);
    }

    public static Optional<PurityGrade> gradeOf(FluidAmount fluid) {
        return Purity.grade(fluid);
    }

    /** Whether stacks of this item carry a stored purity. */
    public static boolean carriesPurity(ItemStack stack) {
        return !stack.isEmpty() && Purity.bears(stack.getItem());
    }

    /**
     * Copy of a purity-bearing stack at the lower bound of {@code grade} by its material's bounds
     * ({@link PurityGrade#POOR}: the external default).
     *
     * @throws IllegalArgumentException when the material lacks the grade
     */
    public static ItemStack withPurity(ItemStack stack, PurityGrade grade) {
        return Purity.with(stack, grade);
    }

    /** Copy of a purity-bearing stack at {@code percent}, rounded up to the scale. */
    public static ItemStack withPurity(ItemStack stack, double percent) {
        return Purity.with(stack, PurityScale.ofPercent(percent));
    }

    public static FluidAmount withPurity(FluidAmount fluid, PurityGrade grade) {
        return Purity.with(fluid, grade);
    }

    public static FluidAmount withPurity(FluidAmount fluid, double percent) {
        return Purity.with(fluid, PurityScale.ofPercent(percent));
    }
}
