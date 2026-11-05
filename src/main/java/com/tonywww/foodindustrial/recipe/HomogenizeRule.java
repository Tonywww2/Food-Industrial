package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Built-in rules of the M21 mixer (additives/10 §2.2 rule 3, §6), used by the machine and by the
 * recipes generated for JEI:
 * <ul>
 *   <li>homogenize: the same material at different purities mixes into the weighted average, rounded
 *       down;</li>
 *   <li>normalize: any item of a material's tag turns 1:1 into this mod's item of that material, at
 *       its effective purity (external rule or external default).</li>
 * </ul>
 */
public final class HomogenizeRule {
    private HomogenizeRule() {
    }

    /**
     * Purity of a homogenized batch.
     *
     * @param values  purity bytes of the parts
     * @param amounts weights (items × 1,000, fluids in mB)
     */
    public static int homogenize(int[] values, long[] amounts) {
        return PurityScale.floorOf(PurityScale.weightedAverage(values, amounts));
    }

    /**
     * Purity a stack keeps when normalized into the mod item of {@code material}, or {@link Purity#NONE}
     * when it has none.
     */
    public static int normalizedPurity(ItemStack stack, @Nullable String material) {
        return Purity.get(stack, material);
    }
}
