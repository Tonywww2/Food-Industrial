package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

/**
 * Settles one completion of a recipe (additives/10 §2.1, machines/01 §1):
 * <ol>
 *   <li>start purity: weighted average of the participating inputs (items count × 1,000, fluids by mB);</li>
 *   <li>the product's rule: source, removal, keep or degrade;</li>
 *   <li>tier bonus for source and removal: the remaining impurities × 0.75 per tier above the recipe's
 *       base tier;</li>
 *   <li>caps: {@code max} on every device, {@code t0_max} on T0 devices; a cap never lowers the result
 *       below the start purity; the result rounds down to the scale;</li>
 *   <li>standardisation: a device with the standardisation module lowers every graded product to its
 *       grade's lower bound (poor products and K0 materials unchanged; 2026-11-05).</li>
 * </ol>
 * Purity is a pure function of recipe, input purities and tiers; conversions use {@link PurityScale}
 * only. Yield (physical recipes) and {@code chance} are random with the expected value unchanged; the
 * device passes its random source.
 */
public final class OutcomeResolver {
    public static final int NONE = PurityData.NONE;

    private OutcomeResolver() {
    }

    /**
     * Purity of output {@code index} for one completion, or {@link #NONE} when the product carries none.
     *
     * @param device  profile of the executing device (tier, standardisation module)
     * @param base    base tier of the recipe (lowest tier of the devices that can run it)
     * @param values  effective purity of each recipe input as consumed ({@link #NONE} for none)
     * @param amounts weight of each recipe input ({@link ProcessInput#weight()}, × parallel runs)
     */
    public static int purity(ProcessRecipe recipe, int index, DeviceProfile device, Tier base, int[] values, long[] amounts, RandomSource random) {
        ProcessOutput output = recipe.outputs().get(index);
        if (!output.bearsPurity()) {
            return NONE;
        }
        PurityData.Material material = PurityData.current().material(output.material());
        if (index == 0 && recipe.purityRange().isPresent()) {
            int low = recipe.rangeLow();
            int high = recipe.rangeHigh();
            int b = low + random.nextInt(high - low + 1);
            return device.normalizing() ? normalize(b, material) : b;
        }
        return apply(recipe.ruleOf(index), material, start(recipe, values, amounts), device.tier().ordinal() - base.ordinal(),
            device.tier() == Tier.T0, device.normalizing());
    }

    /** Start purity of a completion as a fraction, or {@code NaN} when no participating input carries purity. */
    public static double start(ProcessRecipe recipe, int[] values, long[] amounts) {
        int size = recipe.inputs().size();
        int[] used = new int[size];
        long[] weights = new long[size];
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (values[i] != NONE && amounts[i] > 0 && recipe.participates(i)) {
                used[count] = values[i];
                weights[count] = amounts[i];
                count++;
            }
        }
        return count == 0 ? Double.NaN : PurityScale.weightedAverage(used, weights, count);
    }

    /** Applies a rule on a device above T0 without the standardisation module. */
    public static int apply(PurityRule rule, @Nullable PurityData.Material material, double start, int tierBonus) {
        return apply(rule, material, start, tierBonus, false, false);
    }

    /**
     * Applies a rule.
     *
     * @param material  product material, or {@code null}
     * @param start     start purity as a fraction, {@code NaN} when none; a rule that reads inputs
     *                  falls back to the source mode without one
     * @param tierBonus device tier minus base tier (negative counts as 0); only source and removal use it
     * @param t0        the device is T0 ({@code t0_max} applies)
     * @param normalize the device has the standardisation module
     * @return the purity byte, or {@link #NONE} when the source mode finds no value
     */
    public static int apply(PurityRule rule, @Nullable PurityData.Material material, double start, int tierBonus, boolean t0, boolean normalize) {
        int bonus = Math.max(0, tierBonus);
        int b;
        if (rule.mode() == PurityRule.Mode.SOURCE || Double.isNaN(start)) {
            b = source(rule.mode() == PurityRule.Mode.SOURCE ? rule : PurityRule.SOURCE_DEFAULT, material, bonus);
        } else {
            b = switch (rule.mode()) {
                case REMOVAL -> PurityScale.floorOf(PurityScale.removal(start, rule.value(), bonus));
                case DEGRADE -> PurityScale.floorOf(PurityScale.degrade(start, rule.value()));
                default -> PurityScale.floorOf(start);
            };
        }
        if (b == NONE) {
            return NONE;
        }
        int floor = Double.isNaN(start) ? PurityScale.MIN : PurityScale.floorOf(start);
        if (rule.hasMax()) {
            b = cap(b, rule.maxByte(), floor);
        }
        if (t0 && rule.hasT0Max()) {
            int cap = rule.t0MaxByte(material);
            if (cap != NONE) {
                b = cap(b, cap, floor);
            }
        }
        return normalize ? normalize(b, material) : b;
    }

    /** min(result, max(cap, start)): a cap does not lower the start purity (additives/10 §2.1 step 4). */
    private static int cap(int b, int cap, int floor) {
        return Math.min(b, Math.max(cap, floor));
    }

    /**
     * Standardisation (additives/10 §2.1 step 6): a graded product drops to its grade's lower bound;
     * poor products, K0 materials and products without purity keep {@code b}.
     */
    public static int normalize(int b, @Nullable PurityData.Material material) {
        if (b == NONE || material == null || !material.graded()) {
            return b;
        }
        PurityGrade grade = material.gradeOf(b).orElse(PurityGrade.POOR);
        if (grade == PurityGrade.POOR) {
            return b;
        }
        return Math.min(b, material.lowerBound(grade).orElse(b));
    }

    /**
     * Source mode: the written value, else the bound of the rule's value grade (R19 default), else the
     * material's {@code source_purity}, else its crude bound;
     * {@link #NONE} when none exists (K0 without source purity). The tier bonus applies.
     */
    private static int source(PurityRule rule, @Nullable PurityData.Material material, int bonus) {
        if (rule.hasValue()) {
            return PurityScale.source(rule.value(), bonus);
        }
        if (material == null) {
            return NONE;
        }
        if (rule.valueGrade() != null) {
            int bound = material.gradeValue(rule.valueGrade());
            if (bound != NONE) {
                return PurityScale.floorOf(PurityScale.removal(PurityScale.fraction(bound), 0.0, bonus));
            }
        }
        int b = material.sourceByte() != NONE ? material.sourceByte() : material.gradeValue(PurityGrade.CRUDE);
        return b == NONE ? NONE : PurityScale.floorOf(PurityScale.removal(PurityScale.fraction(b), 0.0, bonus));
    }

    /**
     * Amount of output {@code index} for one completion (items or mB): {@code 0} when the device's
     * tier is below the output's {@code min_tier} or its {@code chance} misses; the main product of
     * physical recipes gets the device yield.
     */
    public static int amount(ProcessRecipe recipe, int index, DeviceProfile profile, RandomSource random) {
        ProcessOutput output = recipe.outputs().get(index);
        if (!output.producedAt(profile.tier())) {
            return 0;
        }
        if (output.isChanced() && !(random.nextDouble() < output.chance())) {
            return 0;
        }
        if (index == 0 && recipe.type().yieldScalable()) {
            return output.amount() + extra(output.amount(), profile.yieldPermille(), random.nextInt(1000));
        }
        return output.amount();
    }

    /**
     * Extra main product from the yield (machines/01 §1 rule 3): {@code amount × (yield − 1)}; the whole
     * part always, one more when {@code roll} (uniform in 0..999) falls below the fraction.
     */
    public static int extra(int amount, int yieldPermille, int roll) {
        long scaled = (long) amount * (yieldPermille - 1000);
        int whole = (int) (scaled / 1000L);
        int rest = (int) (scaled % 1000L);
        return whole + (roll < rest ? 1 : 0);
    }
}
