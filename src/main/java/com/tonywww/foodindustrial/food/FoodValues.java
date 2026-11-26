package com.tonywww.foodindustrial.food;

/**
 * Hunger and saturation of the mod's products from the energy of one serving (foods/04 §5.2, §5.3). The quality factor
 * (1.0 / 1.2 / 1.4) is applied per stack by {@link FoodLine#saturationFactor(int)}; the values here are the base ones
 * the item is registered with. The per-product energies come with the nutrition data (I45).
 */
public final class FoodValues {
    /** Reference daily energy of GB 28050 (8,400 kJ): one full hunger bar. */
    public static final double DAILY_KCAL = 2000.0;
    public static final int MAX_HUNGER = 20;

    private FoodValues() {
    }

    /** {@code round(20 × √(kcal / 2,000))}, 0–20; 0 means the item is eaten any time (foods/04 §5.2). */
    public static int hunger(double kcal) {
        if (!(kcal > 0)) {
            return 0;
        }
        long value = Math.round(MAX_HUNGER * Math.sqrt(kcal / DAILY_KCAL));
        return (int) Math.min(MAX_HUNGER, value);
    }

    /**
     * The saturation coefficient by the energy shares of one serving (foods/04 §5.3), checked in order: free sugars
     * ≥ 25 % → 0.1, protein ≥ 30 % → 0.8, protein ≥ 10 % → 0.6, otherwise 0.3.
     */
    public static float coefficient(double proteinShare, double freeSugarShare) {
        if (freeSugarShare >= 0.25) {
            return 0.1F;
        }
        if (proteinShare >= 0.30) {
            return 0.8F;
        }
        if (proteinShare >= 0.10) {
            return 0.6F;
        }
        return 0.3F;
    }

    /** Saturation points: {@code hunger × coefficient × 2 × quality factor}. */
    public static float saturation(int hunger, float coefficient, float qualityFactor) {
        return hunger * coefficient * 2.0F * qualityFactor;
    }
}
