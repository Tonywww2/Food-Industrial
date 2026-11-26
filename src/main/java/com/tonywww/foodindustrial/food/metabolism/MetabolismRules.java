package com.tonywww.foodindustrial.food.metabolism;

/**
 * Every number of the metabolism (foods/02, I35 “实现约定”); nothing else in the code writes them down. Times are
 * in ticks of the body's time, which is world time at a metabolic rate of 1 (1 game hour = 1,000 ticks, 1 game day
 * = 24,000 ticks).
 */
public final class MetabolismRules {
    public static final long HOUR = 1_000;
    public static final long DAY = 24_000;
    /** Settlement interval of a player, and the longest stretch of sunlight one settlement counts. */
    public static final int SETTLE_INTERVAL = 20;
    /** Player attribute scaling the body's time (2026-11-13 user request): default 1, from 0 (stopped) to 100. */
    public static final String RATE_ATTRIBUTE = "metabolic_rate";
    public static final double RATE_MIN = 0.0;
    public static final double RATE_MAX = 100.0;
    public static final int SUN_STEP_LIMIT = 2 * SETTLE_INTERVAL;

    /** Body (foods/02 §2): 70 kg, Widmark water ratio 0.68. */
    public static final double BODY_WEIGHT_G = 70_000;
    public static final double WATER_RATIO = 0.68;

    /** Caffeine (§4.1): first-order, half-life 4 h; tiers by mg. */
    public static final double CAFFEINE_HALF_LIFE = 4_000;
    public static final double CAFFEINE_ALERT = 75;
    public static final double CAFFEINE_NO_SLEEP = 100;
    public static final double CAFFEINE_ENDURANCE = 210;
    /** Above this (strictly) the boosts end and attack damage drops. */
    public static final double CAFFEINE_EXCESS = 400;
    public static final double CAFFEINE_MINING = 0.10;
    public static final double CAFFEINE_ATTACK_SPEED = 0.05;
    public static final double CAFFEINE_EXHAUSTION = 0.15;
    public static final double CAFFEINE_EXCESS_ATTACK_DAMAGE = -0.10;

    /** Alcohol (§4.2): blood alcohol in %, zero-order 0.015 points per hour. */
    public static final double ALCOHOL_ELIMINATION_PER_TICK = 0.015 / HOUR;
    public static final double ALCOHOL_TIPSY = 0.01;
    public static final double ALCOHOL_DRUNK = 0.06;
    /** Strictly above. */
    public static final double ALCOHOL_VOMIT = 0.20;
    /** Strictly above. */
    public static final double ALCOHOL_UNCONSCIOUS = 0.30;
    public static final double ALCOHOL_POISONING = 0.40;
    public static final double ALCOHOL_TIPSY_ATTACK_SPEED = -0.05;
    public static final double ALCOHOL_DRUNK_ATTACK_SPEED = -0.15;
    public static final double ALCOHOL_DRUNK_MOVEMENT = -0.15;
    public static final int VOMIT_FOOD = 3;
    public static final float POISON_DAMAGE = 2.0F;

    /** Fast sugar (§4.3): each serving counts for 2 h; at 30 g exhaustion −20 %, with caffeine at most −30 %. */
    public static final long SUGAR_WINDOW = 2_000;
    public static final double SUGAR_ENDURANCE = 30;
    public static final double SUGAR_EXHAUSTION = 0.20;
    public static final double EXHAUSTION_CAP = 0.30;

    /**
     * Nitrite (§4.4, I45): methemoglobin rise in percentage points, 0.033 per mg of sodium nitrite (Kortboyer 1998),
     * at most 100; first-order, half-life 1.2 h. Intake is also summed per day of the body's time against the
     * acceptable daily intake (7.35 mg sodium nitrite for 70 kg), shown only.
     */
    public static final double NITRITE_PER_MG = 0.033;
    public static final double METHEMOGLOBIN_CAP = 100;
    public static final double NITRITE_HALF_LIFE = 1_200;
    public static final double NITRITE_ADI_MG = 7.35;
    public static final double NITRITE_MILD = 10;
    public static final double NITRITE_MODERATE = 30;
    /** Strictly above. */
    public static final double NITRITE_SEVERE = 50;
    public static final double NITRITE_LETHAL = 70;
    public static final double NITRITE_MILD_ATTACK_DAMAGE = -0.10;
    public static final double NITRITE_MILD_MOVEMENT = -0.10;
    public static final double NITRITE_MODERATE_MOVEMENT = -0.30;
    public static final double NITRITE_MODERATE_ATTACK_SPEED = -0.20;
    /** Damage every tenth of a game hour at the lethal tier. */
    public static final long NITRITE_POISON_INTERVAL = HOUR / 10;
    public static final float NITRITE_POISON_DAMAGE = 2.0F;

    /** Impurity (§5.1): per serving of a homemade or poor product; first-order, half-life 30 days. */
    public static final double IMPURITY_HOMEMADE = 0.2;
    public static final double IMPURITY_POOR = 2.0;
    public static final double IMPURITY_HALF_LIFE = 30 * DAY;
    public static final double IMPURITY_FATIGUE = 10;
    public static final double IMPURITY_WEAKNESS = 30;
    public static final double IMPURITY_ANEMIA = 60;
    public static final double IMPURITY_ATTACK_DAMAGE = -0.10;
    public static final double IMPURITY_MINING = -0.10;
    public static final double IMPURITY_MOVEMENT = -0.10;
    public static final double IMPURITY_MAX_HEALTH = -4;

    /**
     * Nutrients (§5.2–5.6), by nutrient index (C, B1, D, A, calcium, iron, zinc): daily need (GB 28050 NRV; vitamin D
     * 15 µg, §5.5), reserve cap in days, tolerable upper intake level per day (NASEM adults; 0: none), and whether a
     * reserve keeps what goes above its cap (fat-soluble vitamins and minerals, §5.2; vitamin D stays as in I35).
     * Vitamin A's upper level counts preformed vitamin A only, and only preformed vitamin A goes above the cap.
     */
    public static final double[] DAILY_NEED = {100, 1.4, 15, 800, 800, 15, 15};
    public static final double[] RESERVE_CAP = {30, 14, 30, 365, 365, 180, 30};
    public static final double[] UPPER_LEVEL = {0, 0, 0, 3_000, 2_500, 45, 40};
    public static final boolean[] OVERFLOW = {false, false, false, true, true, true, true};
    /**
     * Excess (§5.2): the day's intake at the upper level is the first excess tier, at this many times it the second;
     * a reserve at this many times its cap is the first excess tier too.
     */
    public static final double EXCESS_MULTIPLE = 3;
    /** Sunlight: every 1,000 ticks in the sun add one day of vitamin D. */
    public static final double SUN_TICKS_PER_DAY = 1_000;
    public static final double VITAMIN_C_ATTACK_DAMAGE = -0.10;
    public static final double VITAMIN_C_HEALING = -0.50;
    public static final double VITAMIN_C_MAX_HEALTH = -4;
    public static final double VITAMIN_B1_MOVEMENT = -0.15;
    public static final double VITAMIN_B1_MOVEMENT_SEVERE = -0.30;
    public static final double VITAMIN_B1_JUMP = -0.20;
    public static final double VITAMIN_D_FALL_DAMAGE = 0.50;
    public static final double VITAMIN_D_ATTACK_DAMAGE = -0.10;
    /** Vitamin A (§5.6.1): deficiency darkens the view (night blindness); excess slows mining. */
    public static final double VITAMIN_A_MAX_HEALTH = -4;
    public static final double VITAMIN_A_EXCESS_MINING = -0.20;
    public static final double VITAMIN_A_EXCESS_MAX_HEALTH = -4;
    /** Calcium (§5.6.2). */
    public static final double CALCIUM_FALL_DAMAGE = 0.50;
    public static final double CALCIUM_MAX_HEALTH = -4;
    public static final double CALCIUM_EXCESS_MOVEMENT = -0.10;
    /** Iron (§5.6.2). */
    public static final double IRON_MAX_HEALTH = -4;
    public static final double IRON_ATTACK_DAMAGE = -0.10;
    public static final double IRON_MOVEMENT = -0.20;
    public static final double IRON_EXCESS_DAMAGE_TAKEN = 0.10;
    public static final double IRON_EXCESS_MAX_HEALTH = -4;
    /** Zinc (§5.6.2). */
    public static final double ZINC_HEALING = -0.25;
    public static final double ZINC_ATTACK_DAMAGE = -0.10;
    public static final double ZINC_EXCESS_MAX_HEALTH = -2;

    /** Threshold slack for values that come out of floating-point decay. */
    static final double EPSILON = 1e-9;

    private MetabolismRules() {
    }

    /** Blood alcohol (%) added by {@code grams} of ethanol (Widmark). */
    public static double bloodAlcohol(double grams) {
        return grams / (BODY_WEIGHT_G * WATER_RATIO) * 100.0;
    }

    /** Tier of a caffeine amount (mg): 0 none, 1 alert, 2 sleepless, 3 endurance, 4 excess. */
    public static int caffeineTier(double mg) {
        if (mg > CAFFEINE_EXCESS + EPSILON) {
            return 4;
        }
        if (mg >= CAFFEINE_ENDURANCE - EPSILON) {
            return 3;
        }
        if (mg >= CAFFEINE_NO_SLEEP - EPSILON) {
            return 2;
        }
        return mg >= CAFFEINE_ALERT - EPSILON ? 1 : 0;
    }

    /** Tier of a blood alcohol (%): 0 none, 1 tipsy, 2 drunk, 3 vomiting, 4 unconscious, 5 poisoning. */
    public static int alcoholTier(double percent) {
        if (percent >= ALCOHOL_POISONING - EPSILON) {
            return 5;
        }
        if (percent > ALCOHOL_UNCONSCIOUS + EPSILON) {
            return 4;
        }
        if (percent > ALCOHOL_VOMIT + EPSILON) {
            return 3;
        }
        if (percent >= ALCOHOL_DRUNK - EPSILON) {
            return 2;
        }
        return percent >= ALCOHOL_TIPSY - EPSILON ? 1 : 0;
    }

    /** Methemoglobin rise (percentage points) from {@code mg} of sodium nitrite. */
    public static double methemoglobin(double mg) {
        return mg * NITRITE_PER_MG;
    }

    /** Tier of a methemoglobin rise (%): 0 none, 1 mild, 2 moderate, 3 severe (unconscious), 4 lethal. */
    public static int nitriteTier(double percent) {
        if (percent >= NITRITE_LETHAL - EPSILON) {
            return 4;
        }
        if (percent > NITRITE_SEVERE + EPSILON) {
            return 3;
        }
        if (percent >= NITRITE_MODERATE - EPSILON) {
            return 2;
        }
        return percent >= NITRITE_MILD - EPSILON ? 1 : 0;
    }

    /** Tier of the fast sugar in the window (g): 0 or 1 (endurance). */
    public static int sugarTier(double grams) {
        return grams >= SUGAR_ENDURANCE - EPSILON ? 1 : 0;
    }

    /** Tier of impurity: 0 none, 1 fatigue, 2 weakness, 3 anemia. */
    public static int impurityTier(double units) {
        if (units >= IMPURITY_ANEMIA - EPSILON) {
            return 3;
        }
        if (units >= IMPURITY_WEAKNESS - EPSILON) {
            return 2;
        }
        return units >= IMPURITY_FATIGUE - EPSILON ? 1 : 0;
    }

    /**
     * Lower bound of tier {@code tier} of caffeine (mg), alcohol (%), nitrite (methemoglobin %) or impurity (units); 0
     * for tier 0 or another substance. Caffeine's tier 4, alcohol's tiers 3 and 4 and nitrite's tier 3 start strictly
     * above it.
     */
    public static double bound(Substance substance, int tier) {
        if (tier <= 0 || tier > substance.tiers()) {
            return 0;
        }
        return switch (substance) {
            case CAFFEINE -> new double[] {CAFFEINE_ALERT, CAFFEINE_NO_SLEEP, CAFFEINE_ENDURANCE, CAFFEINE_EXCESS}[tier - 1];
            case ALCOHOL -> new double[] {ALCOHOL_TIPSY, ALCOHOL_DRUNK, ALCOHOL_VOMIT, ALCOHOL_UNCONSCIOUS, ALCOHOL_POISONING}[tier - 1];
            case FAST_SUGAR -> SUGAR_ENDURANCE;
            case NITRITE -> new double[] {NITRITE_MILD, NITRITE_MODERATE, NITRITE_SEVERE, NITRITE_LETHAL}[tier - 1];
            case IMPURITY -> new double[] {IMPURITY_FATIGUE, IMPURITY_WEAKNESS, IMPURITY_ANEMIA}[tier - 1];
            default -> 0;
        };
    }

    /**
     * Ticks until caffeine (mg), alcohol (%), nitrite (%) or impurity (units) at {@code amount} falls out of {@code tier} into
     * the one below; −1 for tier 0 and for the other substances (their timing lives in the state).
     */
    public static long ticksToDrop(Substance substance, double amount, int tier) {
        if (tier <= 0) {
            return -1;
        }
        double bound = bound(substance, tier);
        return switch (substance) {
            case CAFFEINE -> halfLives(amount, bound, CAFFEINE_HALF_LIFE);
            case ALCOHOL -> amount <= bound ? 0 : (long) Math.ceil((amount - bound) / ALCOHOL_ELIMINATION_PER_TICK);
            case NITRITE -> halfLives(amount, bound, NITRITE_HALF_LIFE);
            case IMPURITY -> halfLives(amount, bound, IMPURITY_HALF_LIFE);
            default -> -1;
        };
    }

    private static long halfLives(double amount, double bound, double halfLife) {
        return amount <= bound ? 0 : (long) Math.ceil(halfLife * Math.log(amount / bound) / Math.log(2));
    }

    /** Share of food exhaustion removed at the given caffeine and sugar tiers. */
    public static double exhaustionCut(int caffeineTier, int sugarTier) {
        double cut = (caffeineTier == 3 ? CAFFEINE_EXHAUSTION : 0.0) + (sugarTier == 1 ? SUGAR_EXHAUSTION : 0.0);
        return Math.min(EXHAUSTION_CAP, cut);
    }
}
