package com.tonywww.foodindustrial.core.purity;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Matching and display rules of the purity ingredients (additives/10 §4.1, §4.4), shared by every
 * node's ingredient implementation.
 */
public final class PurityMatching {
    private PurityMatching() {
    }

    /** {@code base} accepts the stack and its effective purity reaches the threshold. */
    public static boolean test(Predicate<ItemStack> base, PurityThreshold threshold, ItemStack stack) {
        return base.test(stack) && Purity.meets(stack, threshold.value(), threshold.material());
    }

    /**
     * Stacks shown for a purity ingredient (additives/10 §4.4): each purity bearer at the threshold and
     * at every higher grade bound of its material (lowest first); other items only when their effective
     * purity (external rules or external default) reaches the threshold.
     */
    public static List<ItemStack> displayStacks(Collection<ItemStack> baseStacks, PurityThreshold threshold) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : baseStacks) {
            if (stack.isEmpty()) {
                continue;
            }
            Item item = stack.getItem();
            if (Purity.bears(item)) {
                for (int b : shownValues(Purity.materialOf(item), threshold.value())) {
                    result.add(Purity.with(stack, b));
                }
            } else if (Purity.meets(stack, threshold.value(), threshold.material())) {
                result.add(stack.copy());
            }
        }
        return result;
    }

    /** Fluids shown for a fluid ingredient, by the same rules as {@link #displayStacks}. */
    public static List<FluidAmount> displayFluids(Collection<Fluid> fluids, int amount, @Nullable PurityThreshold threshold) {
        List<FluidAmount> result = new ArrayList<>();
        for (Fluid fluid : fluids) {
            FluidAmount plain = FluidAmount.of(fluid, amount);
            if (plain.isEmpty()) {
                continue;
            }
            if (threshold == null) {
                int missing = Purity.bears(fluid) ? Purity.missingValue(Purity.materialOf(fluid)) : Purity.NONE;
                result.add(missing == Purity.NONE ? plain : Purity.with(plain, missing));
            } else if (Purity.bears(fluid)) {
                for (int b : shownValues(Purity.materialOf(fluid), threshold.value())) {
                    result.add(Purity.with(plain, b));
                }
            } else if (Purity.meets(plain, threshold.value(), threshold.material())) {
                result.add(plain);
            }
        }
        return result;
    }

    /** The threshold, then each grade bound of {@code material} above it. */
    private static List<Integer> shownValues(@Nullable String material, int threshold) {
        List<Integer> values = new ArrayList<>();
        values.add(threshold);
        PurityData.Material data = PurityData.current().material(material);
        if (data != null) {
            for (int b : data.boundBytes()) {
                if (b != PurityData.NONE && b > values.get(values.size() - 1)) {
                    values.add(b);
                }
            }
        }
        return values;
    }
}
