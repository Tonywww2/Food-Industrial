package com.tonywww.foodindustrial.food.metabolism;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.platform.PlatformPlayerData;

import static com.tonywww.foodindustrial.food.metabolism.MetabolismRules.*;

/**
 * What one player has in the body (foods/02 §3–5, I35) and the arithmetic of it, without the game: elimination is
 * computed in one step for any elapsed time (first order: × 0.5^(Δt / half-life); zero order: − rate × Δt), never
 * tick by tick. Times are counted on the state's own clock, which only moves forward. Settling allocates nothing.
 */
public final class MetabolismState implements PlatformPlayerData.Stored {
    private static final int SUGAR_SLOTS = 32;
    /** Number of nutrients (C, B1, D, A, calcium, iron, zinc). */
    public static final int NUTRIENTS = 7;
    /** Mask with every nutrient on (bit {@code 1 << nutrientIndex}). */
    public static final int ALL_NUTRIENTS = (1 << NUTRIENTS) - 1;
    private static final long NEVER = Long.MIN_VALUE / 4;

    double caffeine;
    double alcohol;
    /** Methemoglobin rise from nitrite (percentage points). */
    double methemoglobin;
    /** Nitrite eaten on the current day of the body's time (mg), and that day ({@code clock / DAY}). */
    double nitriteToday;
    long nitriteDay;
    double impurity;
    final double[] reserve = new double[NUTRIENTS];
    /** Clock time a reserve reached 0, or −1 while it is above 0. */
    final long[] emptySince = new long[NUTRIENTS];
    /**
     * Each nutrient eaten on the current day of the body's time, against its upper level (vitamin A: preformed only),
     * and that day ({@code clock / DAY}).
     */
    final double[] today = new double[NUTRIENTS];
    long intakeDay;
    private final double[] sugarGrams = new double[SUGAR_SLOTS];
    private final long[] sugarTime = new long[SUGAR_SLOTS];
    private int sugarCount;
    double sunTicks;
    /** The state's own time: advanced by every settlement. */
    long clock;
    private long lastDayTime;
    private long lastGameTime;
    long lastVomit = NEVER;
    long lastPoison = NEVER;
    long lastNitritePoison = NEVER;
    /** Tiers in effect (what the effects were last applied for), by {@link Substance} ordinal. */
    final int[] tiers = new int[Substance.all().length];
    boolean unconscious;
    /** Whether the impurity part was on, and which nutrients were on (bit per nutrient index), at the last settlement. */
    boolean impurityOn = true;
    int nutrientsOn = ALL_NUTRIENTS;
    // Not saved: what the server applied and sent, recomputed after loading.
    /** Whether walking and jumping are locked (unconscious). */
    boolean movementLocked;
    /** Factor of food exhaustion (caffeine and sugar endurance). */
    double exhaustionFactor = 1.0;
    /** The player's metabolic rate at the last settlement (attribute {@code foodindustrial:metabolic_rate}). */
    double rate = 1.0;
    /** Fraction of a tick of the body's time not yet advanced (rates other than 1). */
    private double carry;
    /** What the client was sent last, and a reusable view to compare with it. */
    MetabolismView sent;
    final MetabolismView scratch = new MetabolismView();

    public MetabolismState() {
        fillReserves();
    }

    /** A new player: nutrient reserves full, everything else 0. */
    public void reset() {
        caffeine = 0;
        alcohol = 0;
        methemoglobin = 0;
        nitriteToday = 0;
        nitriteDay = 0;
        java.util.Arrays.fill(today, 0);
        intakeDay = 0;
        impurity = 0;
        sugarCount = 0;
        sunTicks = 0;
        lastVomit = NEVER;
        lastPoison = NEVER;
        lastNitritePoison = NEVER;
        java.util.Arrays.fill(tiers, 0);
        unconscious = false;
        exhaustionFactor = 1.0;
        fillReserves();
    }

    private void fillReserves() {
        for (int i = 0; i < NUTRIENTS; i++) {
            reserve[i] = RESERVE_CAP[i];
            emptySince[i] = -1;
        }
    }

    /** Clears impurity (its part turned on or off). */
    public void resetImpurity() {
        impurity = 0;
    }

    /** Refills one nutrient's reserve and forgets its intake today (the nutrient turned on or off). */
    public void resetNutrient(int index) {
        reserve[index] = RESERVE_CAP[index];
        emptySince[index] = -1;
        today[index] = 0;
        if (index == Substance.VITAMIN_D.nutrientIndex()) {
            sunTicks = 0;
        }
    }

    /** Whether nutrient {@code index} is on in mask {@code nutrients}. */
    public static boolean on(int nutrients, int index) {
        return (nutrients & 1 << index) != 0;
    }

    /** Game time of the last settlement. */
    long lastGameTime() {
        return lastGameTime;
    }

    /** Starts timing from the given world times without settling the time before (login: offline time is not counted). */
    public void startTiming(long dayTime, long gameTime) {
        lastDayTime = dayTime;
        lastGameTime = gameTime;
    }

    /**
     * Elapsed time since the last call: the larger of the change of the time of day and of the game time (sleeping
     * skips count, a stopped daylight cycle still counts, time set back counts as normal time), never negative.
     */
    public long elapsed(long dayTime, long gameTime) {
        long byDay = dayTime - lastDayTime;
        long byGame = gameTime - lastGameTime;
        lastDayTime = dayTime;
        lastGameTime = gameTime;
        return Math.max(0, Math.max(byDay, byGame));
    }

    /**
     * Lets {@code ticks} of world time pass at a metabolic rate: the body's time moves {@code rate} times as fast
     * (0.5 half as fast, 0 not at all); fractions of a tick are carried to the next call.
     */
    public void advance(long ticks, double rate, boolean impurityPart, int nutrients) {
        if (ticks <= 0 || rate <= 0) {
            return;
        }
        double body = ticks * rate + carry;
        long whole = (long) Math.floor(body);
        carry = body - whole;
        advance(whole, impurityPart, nutrients);
    }

    /** Lets {@code ticks} of the body's time pass; only the nutrients on in {@code nutrients} are used. */
    public void advance(long ticks, boolean impurityPart, int nutrients) {
        if (ticks <= 0) {
            return;
        }
        clock += ticks;
        caffeine = decay(caffeine, ticks, CAFFEINE_HALF_LIFE);
        alcohol = Math.max(0.0, alcohol - ALCOHOL_ELIMINATION_PER_TICK * ticks);
        methemoglobin = decay(methemoglobin, ticks, NITRITE_HALF_LIFE);
        expireSugar();
        if (impurityPart) {
            impurity = decay(impurity, ticks, IMPURITY_HALF_LIFE);
        }
        for (int i = 0; i < NUTRIENTS; i++) {
            if (on(nutrients, i)) {
                double before = reserve[i];
                double after = before - (double) ticks / DAY;
                if (after <= 0) {
                    if (before > 0) {
                        // the moment the reserve ran out
                        emptySince[i] = clock - Math.round(-after * DAY);
                    }
                    after = 0;
                }
                reserve[i] = after;
            }
        }
    }

    private static double decay(double amount, long ticks, double halfLife) {
        if (amount == 0) {
            return 0;
        }
        double result = amount * Math.pow(0.5, ticks / halfLife);
        return result < 1e-6 ? 0 : result;
    }

    private void expireSugar() {
        int expired = 0;
        while (expired < sugarCount && sugarTime[expired] + SUGAR_WINDOW <= clock) {
            expired++;
        }
        if (expired > 0) {
            System.arraycopy(sugarGrams, expired, sugarGrams, 0, sugarCount - expired);
            System.arraycopy(sugarTime, expired, sugarTime, 0, sugarCount - expired);
            sugarCount -= expired;
        }
    }

    /** {@code ticks} spent in direct sunlight: every 1,000 add one day of vitamin D. */
    public void addSunlight(long ticks) {
        sunTicks += ticks;
        if (sunTicks >= SUN_TICKS_PER_DAY) {
            double days = Math.floor(sunTicks / SUN_TICKS_PER_DAY);
            sunTicks -= days * SUN_TICKS_PER_DAY;
            addReserve(Substance.VITAMIN_D.nutrientIndex(), days, false);
        }
    }

    /** One serving without nutrients: caffeine (mg), alcohol (g), fast sugar (g), nitrite (mg as sodium nitrite), impurity units. */
    public void intake(double caffeineMg, double alcoholG, double sugarG, double nitriteMg, double impurityUnits, boolean impurityPart) {
        intake(caffeineMg, alcoholG, sugarG, nitriteMg, null, 0, impurityUnits, impurityPart, 0);
    }

    /**
     * One serving: caffeine (mg), alcohol (g), fast sugar (g), nitrite (mg as sodium nitrite), the nutrients by nutrient
     * index in their data units ({@code null}: none; vitamin A preformed only), provitamin A (µg RAE, which fills the
     * vitamin A reserve up to its cap but does not count against the upper level), impurity units; only the nutrients
     * on in {@code on} are counted.
     */
    public void intake(double caffeineMg, double alcoholG, double sugarG, double nitriteMg, double[] nutrients, double provitaminAUg,
                       double impurityUnits, boolean impurityPart, int on) {
        caffeine += caffeineMg;
        alcohol += bloodAlcohol(alcoholG);
        if (nitriteMg > 0) {
            methemoglobin = Math.min(METHEMOGLOBIN_CAP, methemoglobin + MetabolismRules.methemoglobin(nitriteMg));
            nitriteToday = nitriteToday() + nitriteMg;
            nitriteDay = clock / DAY;
        }
        if (sugarG > 0) {
            if (sugarCount == SUGAR_SLOTS) {
                sugarGrams[sugarCount - 1] += sugarG;
                sugarTime[sugarCount - 1] = clock;
            } else {
                sugarGrams[sugarCount] = sugarG;
                sugarTime[sugarCount] = clock;
                sugarCount++;
            }
        }
        if (impurityPart) {
            impurity += impurityUnits;
        }
        int vitaminA = Substance.VITAMIN_A.nutrientIndex();
        if (on(on, vitaminA)) {
            addReserve(vitaminA, provitaminAUg / DAILY_NEED[vitaminA], false);
        }
        if (nutrients != null && on != 0) {
            rollDay();
            for (int i = 0; i < NUTRIENTS; i++) {
                double amount = nutrients[i];
                if (amount > 0 && on(on, i)) {
                    addReserve(i, amount / DAILY_NEED[i], OVERFLOW[i]);
                    today[i] += amount;
                }
            }
        }
    }

    /** Adds days to a reserve: up to its cap, or beyond it when {@code overflow}; a reserve above its cap stays. */
    private void addReserve(int index, double days, boolean overflow) {
        if (days <= 0) {
            return;
        }
        double before = reserve[index];
        reserve[index] = overflow ? before + days : Math.max(before, Math.min(RESERVE_CAP[index], before + days));
        if (reserve[index] > 0) {
            emptySince[index] = -1;
        }
    }

    /** Starts a new day's intake when the body's time has moved into another day. */
    private void rollDay() {
        long day = clock / DAY;
        if (day != intakeDay) {
            java.util.Arrays.fill(today, 0);
            intakeDay = day;
        }
    }

    /** A nutrient eaten on the current day of the body's time, in its data unit (vitamin A: preformed only). */
    public double today(Substance nutrient) {
        return intakeDay == clock / DAY ? today[nutrient.nutrientIndex()] : 0;
    }

    /** Fast sugar eaten within the last two hours (g). */
    public double sugarInWindow() {
        double total = 0;
        for (int i = 0; i < sugarCount; i++) {
            total += sugarGrams[i];
        }
        return total;
    }

    /** Ticks until the fast sugar in the window falls below the endurance tier, or −1 when it is not in it. */
    public long sugarEnduranceLeft() {
        double total = sugarInWindow();
        if (sugarTier(total) == 0) {
            return -1;
        }
        for (int i = 0; i < sugarCount; i++) {
            total -= sugarGrams[i];
            if (sugarTier(total) == 0) {
                return Math.max(0, sugarTime[i] + SUGAR_WINDOW - clock);
            }
        }
        return -1;
    }

    /** Ticks since a nutrient's reserve ran out, or −1 while it has some. */
    public long deficientFor(Substance nutrient) {
        int index = nutrient.nutrientIndex();
        if (reserve[index] > 0) {
            return -1;
        }
        return emptySince[index] >= 0 ? Math.max(0, clock - emptySince[index]) : 0;
    }

    public double caffeine() {
        return caffeine;
    }

    public double alcohol() {
        return alcohol;
    }

    public double impurity() {
        return impurity;
    }

    /** Methemoglobin rise from nitrite (%). */
    public double methemoglobin() {
        return methemoglobin;
    }

    /** Nitrite eaten on the current day of the body's time (mg). */
    public double nitriteToday() {
        return nitriteDay == clock / DAY ? nitriteToday : 0;
    }

    /** Reserve of a nutrient in days. */
    public double reserve(Substance nutrient) {
        return reserve[nutrient.nutrientIndex()];
    }

    public long clock() {
        return clock;
    }

    /** The tier a substance is at now (not yet applied). */
    public int tier(Substance substance, boolean impurityPart, int nutrients) {
        return switch (substance) {
            case CAFFEINE -> caffeineTier(caffeine);
            case ALCOHOL -> alcoholTier(alcohol);
            case FAST_SUGAR -> sugarTier(sugarInWindow());
            case NITRITE -> nitriteTier(methemoglobin);
            case IMPURITY -> impurityPart ? impurityTier(impurity) : 0;
            default -> on(nutrients, substance.nutrientIndex()) ? nutrientTier(substance) : 0;
        };
    }

    /**
     * Deficiency: 1 when the reserve is out, 2 once it has been out for a cap's length. Excess (foods/02 §5.2): 3 when
     * the day's intake reaches the upper level or a reserve that keeps its overflow reaches three times its cap, 4 (for
     * nutrients with a second excess tier) when the day's intake reaches three times the upper level.
     */
    private int nutrientTier(Substance nutrient) {
        int index = nutrient.nutrientIndex();
        if (reserve[index] <= 0) {
            return emptySince[index] >= 0 && clock - emptySince[index] >= RESERVE_CAP[index] * DAY ? 2 : 1;
        }
        if (!nutrient.hasExcess()) {
            return 0;
        }
        int tier = 0;
        double eaten = today(nutrient);
        double upper = UPPER_LEVEL[index];
        if (upper > 0 && nutrient.tiers() > Substance.DEFICIENCY_TIERS + 1 && eaten >= upper * EXCESS_MULTIPLE - EPSILON) {
            tier = Substance.DEFICIENCY_TIERS + 2;
        } else if (upper > 0 && eaten >= upper - EPSILON) {
            tier = Substance.DEFICIENCY_TIERS + 1;
        }
        if (tier == 0 && OVERFLOW[index] && reserve[index] >= RESERVE_CAP[index] * EXCESS_MULTIPLE - EPSILON) {
            tier = Substance.DEFICIENCY_TIERS + 1;
        }
        return tier;
    }

    /**
     * Ticks of the body's time until a nutrient leaves its excess tier downwards: the day's intake restarts at the end
     * of the day; a reserve at three times its cap drains a day per day. −1 when not in excess.
     */
    public long excessLeft(Substance nutrient, int tier) {
        if (!nutrient.excessTier(tier)) {
            return -1;
        }
        int index = nutrient.nutrientIndex();
        long dayEnd = DAY - Math.floorMod(clock, DAY);
        double upper = UPPER_LEVEL[index];
        boolean byIntake = upper > 0 && today(nutrient) >= upper - EPSILON;
        double over = reserve[index] - RESERVE_CAP[index] * EXCESS_MULTIPLE;
        long byReserve = OVERFLOW[index] && over >= -EPSILON ? (long) Math.ceil(Math.max(0, over) * DAY) : -1;
        if (tier > Substance.DEFICIENCY_TIERS + 1) {
            // the second tier ends with the day; the first may go on after it by the reserve
            return dayEnd;
        }
        return byIntake ? Math.max(dayEnd, byReserve) : byReserve;
    }

    /** Tier applied last for {@code substance}. */
    public int appliedTier(Substance substance) {
        return tiers[substance.ordinal()];
    }

    /** Sets a substance's amount directly (development command): mg, %, g, % methemoglobin, units or days. */
    public void set(Substance substance, double value) {
        switch (substance) {
            case CAFFEINE -> caffeine = Math.max(0, value);
            case ALCOHOL -> alcohol = Math.max(0, value);
            case FAST_SUGAR -> {
                sugarCount = 0;
                intake(0, 0, Math.max(0, value), 0, 0, false);
            }
            case NITRITE -> methemoglobin = Math.max(0, Math.min(METHEMOGLOBIN_CAP, value));
            case IMPURITY -> impurity = Math.max(0, value);
            default -> {
                int index = substance.nutrientIndex();
                reserve[index] = Math.max(0, OVERFLOW[index] ? value : Math.min(RESERVE_CAP[index], value));
                emptySince[index] = reserve[index] > 0 ? -1 : clock;
            }
        }
    }

    @Override
    public void save(DataWriter out) {
        out.putDouble("caffeine", caffeine);
        out.putDouble("alcohol", alcohol);
        out.putDouble("methemoglobin", methemoglobin);
        out.putDouble("nitrite_today", nitriteToday);
        out.putLong("nitrite_day", nitriteDay);
        out.putDouble("impurity", impurity);
        for (int i = 0; i < NUTRIENTS; i++) {
            out.putDouble("reserve" + i, reserve[i]);
            out.putLong("empty_since" + i, emptySince[i]);
            out.putDouble("today" + i, today[i]);
        }
        out.putLong("intake_day", intakeDay);
        DataWriter.ListWriter sugar = out.list("sugar");
        for (int i = 0; i < sugarCount; i++) {
            DataWriter entry = sugar.add();
            entry.putDouble("g", sugarGrams[i]);
            entry.putLong("t", sugarTime[i]);
        }
        out.putDouble("sun", sunTicks);
        out.putLong("clock", clock);
        out.putLong("day_time", lastDayTime);
        out.putLong("game_time", lastGameTime);
        out.putLong("last_vomit", lastVomit);
        out.putLong("last_poison", lastPoison);
        out.putLong("last_nitrite_poison", lastNitritePoison);
        out.putBoolean("impurity_on", impurityOn);
        out.putInt("nutrient_mask", nutrientsOn);
    }

    @Override
    public void load(DataReader in) {
        caffeine = in.getDouble("caffeine", 0);
        alcohol = in.getDouble("alcohol", 0);
        methemoglobin = in.getDouble("methemoglobin", 0);
        nitriteToday = in.getDouble("nitrite_today", 0);
        nitriteDay = in.getLong("nitrite_day", 0);
        impurity = in.getDouble("impurity", 0);
        for (int i = 0; i < NUTRIENTS; i++) {
            reserve[i] = in.getDouble("reserve" + i, RESERVE_CAP[i]);
            emptySince[i] = in.getLong("empty_since" + i, -1);
            today[i] = in.getDouble("today" + i, 0);
        }
        intakeDay = in.getLong("intake_day", 0);
        sugarCount = 0;
        for (DataReader entry : in.list("sugar")) {
            if (sugarCount < SUGAR_SLOTS) {
                sugarGrams[sugarCount] = entry.getDouble("g", 0);
                sugarTime[sugarCount] = entry.getLong("t", 0);
                sugarCount++;
            }
        }
        sunTicks = in.getDouble("sun", 0);
        clock = in.getLong("clock", 0);
        lastDayTime = in.getLong("day_time", 0);
        lastGameTime = in.getLong("game_time", 0);
        lastVomit = in.getLong("last_vomit", NEVER);
        lastPoison = in.getLong("last_poison", NEVER);
        lastNitritePoison = in.getLong("last_nitrite_poison", NEVER);
        impurityOn = in.getBoolean("impurity_on", true);
        // before I46's per-nutrient switches the whole part was one boolean
        nutrientsOn = in.getInt("nutrient_mask", in.getBoolean("nutrients_on", true) ? ALL_NUTRIENTS : 0);
        // Tiers are not saved: effects are applied again after loading.
        java.util.Arrays.fill(tiers, 0);
        unconscious = false;
        movementLocked = false;
        exhaustionFactor = 1.0;
        sent = null;
    }
}
