package com.tonywww.foodindustrial.machine.chemical;

/**
 * Main product of M27's cracking mode by furnace tube segments (I16, additives/06 §T3 化工：操作与实现规格):
 * ×0.7 with one segment, 0.1 more per further segment, ×1.0 with four.
 */
public final class CrackingYield {
    public static final int MAX_SEGMENTS = 4;

    private CrackingYield() {
    }

    /** Main product per mille with {@code segments} tube segments. */
    public static int permille(int segments) {
        int clamped = Math.max(1, Math.min(MAX_SEGMENTS, segments));
        return 700 + 100 * (clamped - 1);
    }
}
