package com.tonywww.foodindustrial.core.purity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The purity scale (additives/10 §1.2): purity is the wet-basis mass fraction of the target substance,
 * stored as a byte {@code b} on a two-ended logarithmic scale. {@code b = -128} is 0%, {@code b = 127}
 * is 100%, every other value is {@code p = 1 / (1 + 10^(-b / 25.4))}.
 * <p>
 * The 256 values are computed once with {@link StrictMath} and only looked up afterwards, so every
 * node and platform gets bit-identical results. Values written in recipes and data (source purity,
 * grade bounds, thresholds, external rules) round up with {@link #ceilOf}; computed results (removal,
 * bonus, weighted average) are calculated in doubles and round down with {@link #floorOf} at the end.
 */
public final class PurityScale {
    public static final int MIN = -128;
    public static final int MAX = 127;

    /** Weight of one item in weighted averages: 1 item = 1,000 mB (additives/10 §2.1). */
    public static final long ITEM_AMOUNT = 1000;

    /** Remaining impurities are multiplied by this for every tier above the recipe's base tier. */
    public static final double TIER_FACTOR = 0.75;

    private static final double[] TABLE = new double[256];

    static {
        for (int b = MIN; b <= MAX; b++) {
            TABLE[b - MIN] = b == MIN ? 0.0 : b == MAX ? 1.0 : 1.0 / (1.0 + StrictMath.pow(10.0, -b / 25.4));
        }
    }

    private PurityScale() {
    }

    public static int clamp(int b) {
        return Math.max(MIN, Math.min(MAX, b));
    }

    /** Mass fraction (0..1) of {@code b}; out-of-range values clamp. */
    public static double fraction(int b) {
        return TABLE[clamp(b) - MIN];
    }

    /** Percentage (0..100) of {@code b}. */
    public static double percent(int b) {
        return fraction(b) * 100.0;
    }

    /** Smallest {@code b} with {@code fraction(b) >= x}: for values written in recipes and data. */
    public static int ceilOf(double x) {
        if (!(x > TABLE[0])) {
            return MIN;
        }
        if (x >= TABLE[255]) {
            return MAX;
        }
        int low = MIN;
        int high = MAX;
        // TABLE[low - MIN] < x <= TABLE[high - MIN]
        while (high - low > 1) {
            int mid = Math.floorDiv(low + high, 2);
            if (TABLE[mid - MIN] >= x) {
                high = mid;
            } else {
                low = mid;
            }
        }
        return high;
    }

    /** Largest {@code b} with {@code fraction(b) <= x}: for computed results. */
    public static int floorOf(double x) {
        if (!(x < TABLE[255])) {
            return MAX;
        }
        if (x <= TABLE[0]) {
            return MIN;
        }
        int low = MIN;
        int high = MAX;
        // TABLE[low - MIN] <= x < TABLE[high - MIN]
        while (high - low > 1) {
            int mid = Math.floorDiv(low + high, 2);
            if (TABLE[mid - MIN] <= x) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return low;
    }

    /**
     * {@code b} of a percentage written in recipes or data ({@code 83}, {@code 3.5}, {@code 99.8}),
     * rounded up.
     *
     * @throws IllegalArgumentException outside 0..100
     */
    public static int ofPercent(double percent) {
        if (!(percent >= 0.0 && percent <= 100.0)) {
            throw new IllegalArgumentException("Purity must be a percentage between 0 and 100: " + percent);
        }
        return ceilOf(percent / 100.0);
    }

    /**
     * Impurity removal (additives/10 §2.1): removes {@code removedPercent} of the impurities by mass,
     * then another 25% of what remains for every tier above the base tier.
     *
     * @param p              start purity as a fraction
     * @param removedPercent removed share of the impurities, 0..100 (0 for the source mode)
     * @param tierBonus      equipment tier minus the recipe's base tier, {@code >= 0}
     * @return the resulting fraction, not yet rounded
     */
    public static double removal(double p, double removedPercent, int tierBonus) {
        double impurity = (1.0 - p) * (1.0 - removedPercent / 100.0) * StrictMath.pow(TIER_FACTOR, Math.max(0, tierBonus));
        double total = p + impurity;
        return total <= 0.0 ? 0.0 : p / total;
    }

    /**
     * Source mode: the written value (rounded up to the scale) with the tier bonus applied as a
     * removal of nothing (additives/10 §2.1, step 3).
     */
    public static int source(double writtenPercent, int tierBonus) {
        return floorOf(removal(fraction(ofPercent(writtenPercent)), 0.0, tierBonus));
    }

    /** Degradation: the target substance loses {@code lostPercent} of its mass. Not rounded. */
    public static double degrade(double p, double lostPercent) {
        return p * (1.0 - lostPercent / 100.0);
    }

    /**
     * Weighted average of purities (items by count × {@link #ITEM_AMOUNT}, fluids by mB). Not rounded;
     * when every weighted value is the same, that value exactly (so keeping a purity never loses a step).
     *
     * @param values  purity bytes
     * @param amounts weights, same length, {@code >= 0}
     */
    public static double weightedAverage(int[] values, long[] amounts) {
        return weightedAverage(values, amounts, values.length);
    }

    /** {@link #weightedAverage(int[], long[])} over the first {@code count} entries. */
    public static double weightedAverage(int[] values, long[] amounts, int count) {
        if (count > values.length || count > amounts.length) {
            throw new IllegalArgumentException("values and amounts are shorter than " + count);
        }
        double sum = 0.0;
        double weight = 0.0;
        int first = Integer.MIN_VALUE;
        boolean same = true;
        for (int i = 0; i < count; i++) {
            if (amounts[i] <= 0) {
                continue;
            }
            int b = clamp(values[i]);
            if (first == Integer.MIN_VALUE) {
                first = b;
            } else if (b != first) {
                same = false;
            }
            sum += fraction(b) * amounts[i];
            weight += amounts[i];
        }
        if (weight <= 0.0) {
            return 0.0;
        }
        return same ? fraction(first) : sum / weight;
    }

    /**
     * Purity of foreign items and of mod items without stored purity (additives/10 §1.6): the
     * impurities of the lowest grade bound doubled, {@code c / (2 - c)}, rounded down.
     *
     * @param lowestBound {@code b} of the material's lowest grade bound (normally crude)
     */
    public static int externalDefault(int lowestBound) {
        double c = fraction(lowestBound);
        return floorOf(c / (2.0 - c));
    }

    /**
     * Display text without the percent sign (text standard §1 rule 6), truncated so it never exceeds
     * the actual value: below 1% two decimals ({@code "<0.01"} below 0.01%), 1%–10% one decimal,
     * 10%–90% whole numbers, 90%–99.9% one decimal, from 99.9% two decimals. Only −128 and 127 show
     * {@code "0"} and {@code "100"}.
     */
    public static String format(int b) {
        int value = clamp(b);
        if (value == MIN) {
            return "0";
        }
        if (value == MAX) {
            return "100";
        }
        double percent = percent(value);
        if (percent < 0.01) {
            return "<0.01";
        }
        return new BigDecimal(fraction(value)).movePointRight(2)
            .setScale(displayDecimals(percent), RoundingMode.FLOOR).toPlainString();
    }

    /** Decimals shown for a percentage in the range it falls into (text standard §1 rule 6). */
    public static int displayDecimals(double percent) {
        if (percent < 1.0) {
            return 2;
        }
        if (percent < 10.0) {
            return 1;
        }
        if (percent < 90.0) {
            return 0;
        }
        return percent < 99.9 ? 1 : 2;
    }

    /**
     * Checks a percentage as written in data: within 0..100 and with no more decimals than the display
     * rule shows in its range, so displayed values and grades always agree (additives/10 §1.2).
     *
     * @throws IllegalArgumentException when out of range or too precise
     */
    public static void checkWritten(BigDecimal percent) {
        if (percent.signum() < 0 || percent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Purity must be a percentage between 0 and 100: " + percent.toPlainString());
        }
        int decimals = Math.max(0, percent.stripTrailingZeros().scale());
        int allowed = displayDecimals(percent.doubleValue());
        if (decimals > allowed) {
            throw new IllegalArgumentException("Purity " + percent.toPlainString() + "% has " + decimals
                + " decimals; at most " + allowed + " in this range");
        }
    }
}
