package com.tonywww.foodindustrial.food.metabolism;

import org.junit.jupiter.api.Test;

import static com.tonywww.foodindustrial.food.metabolism.MetabolismRules.DAY;
import static com.tonywww.foodindustrial.food.metabolism.MetabolismState.ALL_NUTRIENTS;
import static com.tonywww.foodindustrial.food.metabolism.MetabolismRules.HOUR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The metabolism's arithmetic (foods/02, I35 §六). */
class MetabolismStateTest {
    private static final int ALL = ALL_NUTRIENTS;

    private static void eat(MetabolismState state, double caffeine, double alcohol, double sugar, double impurity) {
        state.intake(caffeine, alcohol, sugar, 0, null, 0, impurity, true, ALL);
    }

    /** One serving of one nutrient, in its data unit. */
    private static void eat(MetabolismState state, Substance nutrient, double amount) {
        double[] nutrients = new double[MetabolismState.NUTRIENTS];
        nutrients[nutrient.nutrientIndex()] = amount;
        state.intake(0, 0, 0, 0, nutrients, 0, 0, true, ALL);
    }

    @Test
    void caffeineHalvesIn4000Ticks() {
        MetabolismState state = new MetabolismState();
        eat(state, 200, 0, 0, 0);
        state.advance(4_000, true, ALL);
        assertEquals(100.0, state.caffeine(), 1e-9);
        state.advance(2_000, true, ALL);
        state.advance(2_000, true, ALL);
        assertEquals(50.0, state.caffeine(), 1e-9);
    }

    @Test
    void metabolicRateScalesTheBodysTime() {
        MetabolismState slow = new MetabolismState();
        eat(slow, 200, 0, 0, 0);
        slow.advance(8_000, 0.5, true, ALL);
        assertEquals(100.0, slow.caffeine(), 1e-9);
        assertEquals(4_000, slow.clock());
        MetabolismState fast = new MetabolismState();
        fast.set(Substance.ALCOHOL, 0.10);
        fast.advance(HOUR / 2, 2.0, true, ALL);
        assertEquals(0.085, fast.alcohol(), 1e-12);
        double reserve = fast.reserve(Substance.VITAMIN_C);
        fast.advance(DAY, 2.0, true, ALL);
        assertEquals(reserve - 2, fast.reserve(Substance.VITAMIN_C), 1e-9);
        MetabolismState stopped = new MetabolismState();
        eat(stopped, 200, 0, 0, 0);
        stopped.advance(DAY, 0.0, true, ALL);
        assertEquals(200.0, stopped.caffeine(), 1e-9);
        assertEquals(0, stopped.clock());
    }

    @Test
    void fractionsOfABodyTickCarryOver() {
        MetabolismState state = new MetabolismState();
        for (int i = 0; i < 20; i++) {
            state.advance(1, 0.25, true, ALL);
        }
        assertEquals(5, state.clock());
        for (int i = 0; i < 3; i++) {
            state.advance(20, 1.05, true, ALL);
        }
        assertEquals(68, state.clock());
    }

    @Test
    void halfALitreOfBeerIsAbout0033Percent() {
        double grams = 500 * 0.04 * 0.789;
        assertEquals(15.8, grams, 0.05);
        assertEquals(0.033, MetabolismRules.bloodAlcohol(grams), 0.0005);
    }

    @Test
    void alcoholFalls0015PointsPerHourAndStopsAtZero() {
        MetabolismState state = new MetabolismState();
        state.set(Substance.ALCOHOL, 0.10);
        state.advance(HOUR, true, ALL);
        assertEquals(0.085, state.alcohol(), 1e-12);
        state.advance(10 * HOUR, true, ALL);
        assertEquals(0.0, state.alcohol());
    }

    @Test
    void sugarCountsForTwoHoursAndEnduranceIsCapped() {
        MetabolismState state = new MetabolismState();
        eat(state, 0, 0, 20, 0);
        state.advance(1_500, true, ALL);
        eat(state, 0, 0, 15, 0);
        assertEquals(35.0, state.sugarInWindow(), 1e-9);
        assertEquals(1, state.tier(Substance.FAST_SUGAR, true, ALL));
        state.advance(500, true, ALL);
        assertEquals(15.0, state.sugarInWindow(), 1e-9);
        assertEquals(0, state.tier(Substance.FAST_SUGAR, true, ALL));
        assertEquals(0.20, MetabolismRules.exhaustionCut(0, 1), 1e-12);
        assertEquals(0.15, MetabolismRules.exhaustionCut(3, 0), 1e-12);
        assertEquals(0.30, MetabolismRules.exhaustionCut(3, 1), 1e-12);
        assertEquals(0.0, MetabolismRules.exhaustionCut(4, 0), 1e-12);
    }

    @Test
    void threeServingsADaySettleAt26And260() {
        assertEquals(26, steadyImpurity(MetabolismRules.IMPURITY_HOMEMADE), 1.0);
        assertEquals(260, steadyImpurity(MetabolismRules.IMPURITY_POOR), 10.0);
        assertEquals(1, MetabolismRules.impurityTier(26));
        assertEquals(3, MetabolismRules.impurityTier(260));
    }

    private static double steadyImpurity(double perServing) {
        MetabolismState state = new MetabolismState();
        double peak = 0;
        for (int day = 0; day < 2_000; day++) {
            for (int meal = 0; meal < 3; meal++) {
                eat(state, 0, 0, 0, perServing);
                peak = state.impurity();
                state.advance(DAY / 3, true, ALL);
            }
        }
        return peak;
    }

    @Test
    void nutrientsDrainCapAndReachBothTiers() {
        MetabolismState state = new MetabolismState();
        assertEquals(30.0, state.reserve(Substance.VITAMIN_C));
        assertEquals(14.0, state.reserve(Substance.VITAMIN_B1));
        state.advance(10 * DAY, true, ALL);
        assertEquals(20.0, state.reserve(Substance.VITAMIN_C), 1e-9);
        // 100 mg of vitamin C is one day; the reserve never exceeds 30 days
        eat(state, Substance.VITAMIN_C, 5_000);
        assertEquals(30.0, state.reserve(Substance.VITAMIN_C), 1e-9);
        state.advance(30 * DAY, true, ALL);
        assertEquals(0.0, state.reserve(Substance.VITAMIN_C));
        assertEquals(1, state.tier(Substance.VITAMIN_C, true, ALL));
        state.advance(30 * DAY - 1, true, ALL);
        assertEquals(1, state.tier(Substance.VITAMIN_C, true, ALL));
        state.advance(1, true, ALL);
        assertEquals(2, state.tier(Substance.VITAMIN_C, true, ALL));
        eat(state, Substance.VITAMIN_C, 50);
        assertEquals(0, state.tier(Substance.VITAMIN_C, true, ALL));
        // B1 ran out after 14 days, so its second tier started 14 days later
        assertEquals(2, state.tier(Substance.VITAMIN_B1, true, ALL));
        // a switched-off part has no tier
        assertEquals(0, state.tier(Substance.VITAMIN_B1, true, 0));
    }

    @Test
    void sunlightAddsOneDayOfVitaminDPer1000Ticks() {
        MetabolismState state = new MetabolismState();
        state.advance(20 * DAY, true, ALL);
        assertEquals(10.0, state.reserve(Substance.VITAMIN_D), 1e-9);
        for (int i = 0; i < 50; i++) {
            state.addSunlight(40);
        }
        assertEquals(12.0, state.reserve(Substance.VITAMIN_D), 1e-9);
    }

    @Test
    void elapsedTakesTheLargerOfDayAndGameTime() {
        MetabolismState state = new MetabolismState();
        state.startTiming(1_000, 50_000);
        // sleeping: the time of day jumps 12,000 while the game time moves on 100
        assertEquals(12_000, state.elapsed(13_000, 50_100));
        // daylight cycle off: the time of day stands still
        assertEquals(500, state.elapsed(13_000, 50_600));
        // time set back
        assertEquals(20, state.elapsed(8_000, 50_620));
        // the first settlement after login counts nothing from before it
        state.startTiming(0, 90_000);
        assertEquals(20, state.elapsed(20, 90_020));
    }

    @Test
    void tierBoundaries() {
        assertEquals(0, MetabolismRules.caffeineTier(74.9));
        assertEquals(1, MetabolismRules.caffeineTier(75));
        assertEquals(2, MetabolismRules.caffeineTier(100));
        assertEquals(3, MetabolismRules.caffeineTier(210));
        assertEquals(3, MetabolismRules.caffeineTier(400));
        assertEquals(4, MetabolismRules.caffeineTier(400.1));
        assertEquals(0, MetabolismRules.alcoholTier(0.0099));
        assertEquals(1, MetabolismRules.alcoholTier(0.01));
        assertEquals(1, MetabolismRules.alcoholTier(0.055));
        assertEquals(2, MetabolismRules.alcoholTier(0.06));
        assertEquals(2, MetabolismRules.alcoholTier(0.20));
        assertEquals(3, MetabolismRules.alcoholTier(0.2001));
        assertEquals(3, MetabolismRules.alcoholTier(0.30));
        assertEquals(4, MetabolismRules.alcoholTier(0.3001));
        assertEquals(5, MetabolismRules.alcoholTier(0.40));
        assertEquals(0, MetabolismRules.sugarTier(29.9));
        assertEquals(1, MetabolismRules.sugarTier(30));
        assertEquals(0, MetabolismRules.impurityTier(9.99));
        assertEquals(1, MetabolismRules.impurityTier(10));
        assertEquals(2, MetabolismRules.impurityTier(30));
        assertEquals(3, MetabolismRules.impurityTier(60));
    }

    @Test
    void countdownsToTheNextTierDown() {
        // caffeine 120 mg halves to 100 mg in 4000 * log2(1.2) ticks
        assertEquals((long) Math.ceil(4_000 * Math.log(1.2) / Math.log(2)), MetabolismRules.ticksToDrop(Substance.CAFFEINE, 120, 2));
        assertEquals(-1, MetabolismRules.ticksToDrop(Substance.CAFFEINE, 50, 0));
        // alcohol 0.142 % falls to 0.06 % at 0.015 points per 1000 ticks
        assertEquals(5_467, MetabolismRules.ticksToDrop(Substance.ALCOHOL, 0.142, 2), 1);
        assertEquals(2_000, MetabolismRules.ticksToDrop(Substance.ALCOHOL, 0.33, 4), 1);
        // impurity 120 halves to 60 in 30 days
        assertEquals(30 * DAY, MetabolismRules.ticksToDrop(Substance.IMPURITY, 120, 3), 1);
        assertEquals(-1, MetabolismRules.ticksToDrop(Substance.FAST_SUGAR, 40, 1));
    }

    @Test
    void sugarEnduranceEndsWhenEnoughOfTheWindowExpires() {
        MetabolismState state = new MetabolismState();
        eat(state, 0, 0, 20, 0);
        assertEquals(-1, state.sugarEnduranceLeft());
        state.advance(500, true, ALL);
        eat(state, 0, 0, 15, 0);
        // 35 g; once the first 20 g leave (1500 ticks from now) only 15 g remain
        assertEquals(1_500, state.sugarEnduranceLeft());
        state.advance(300, true, ALL);
        eat(state, 0, 0, 20, 0);
        // 55 g; without the first 20 g, 35 g remain; without the next 15 g, 20 g: that is when it ends
        assertEquals(1_700, state.sugarEnduranceLeft());
    }

    @Test
    void deficiencyIsCountedFromTheMomentTheReserveRanOut() {
        MetabolismState state = new MetabolismState();
        assertEquals(-1, state.deficientFor(Substance.VITAMIN_B1));
        state.advance(20 * DAY, true, ALL);
        // B1 lasts 14 days, so it has been out for 6
        assertEquals(6 * DAY, state.deficientFor(Substance.VITAMIN_B1));
        assertEquals(-1, state.deficientFor(Substance.VITAMIN_C));
    }

    @Test
    void cardNumbers() {
        assertEquals("0:53", MetabolismText.clock(1_052));
        assertEquals("0:01", MetabolismText.clock(3));
        assertEquals("4:34", MetabolismText.clock(5_467));
        assertEquals("1:00:00", MetabolismText.clock(72_000));
        assertEquals("+10", MetabolismText.signed(0.10 * 100));
        assertEquals("-4", MetabolismText.signed(-4));
        assertEquals("-30", MetabolismText.signed(-0.30 * 100));
        assertEquals("+50", MetabolismText.signed(0.50 * 100));
        assertEquals("12", MetabolismText.reserveDays(12.9));
        assertEquals("<1", MetabolismText.reserveDays(0.4));
    }

    @Test
    void savedStateLoadsBackAndANewPlayerStartsFull() {
        MetabolismState fresh = new MetabolismState();
        assertTrue(fresh.reserve(Substance.VITAMIN_D) == 30.0 && fresh.caffeine() == 0);
    }

    @Test
    void nitriteRaisesMethemoglobinAndHalvesIn1200Ticks() {
        MetabolismState state = new MetabolismState();
        // 0.46 g of sodium nitrite taken for salt (dumplings, foods/13 §8): +15.18 points, mild tier
        state.intake(0, 0, 0, 460, null, 0, 0, true, ALL);
        assertEquals(15.18, state.methemoglobin(), 1e-9);
        assertEquals(1, state.tier(Substance.NITRITE, true, ALL));
        assertEquals(460.0, state.nitriteToday(), 1e-9);
        state.advance(1_200, true, ALL);
        assertEquals(7.59, state.methemoglobin(), 1e-9);
        assertEquals(0, state.tier(Substance.NITRITE, true, ALL));
        // the rise is capped at 100; above 50 is tier 3 (strictly), 70 tier 4
        state.intake(0, 0, 0, 5_000, null, 0, 0, true, ALL);
        assertEquals(100.0, state.methemoglobin(), 1e-9);
        assertEquals(4, state.tier(Substance.NITRITE, true, ALL));
        state.set(Substance.NITRITE, 50);
        assertEquals(2, state.tier(Substance.NITRITE, true, ALL));
        state.set(Substance.NITRITE, 50.01);
        assertEquals(3, state.tier(Substance.NITRITE, true, ALL));
        // the day's sum restarts on the next day of the body's time
        state.advance(DAY, true, ALL);
        assertEquals(0.0, state.nitriteToday(), 1e-9);
    }

    @Test
    void newNutrientsStartFullAndDrainLikeTheOthers() {
        MetabolismState state = new MetabolismState();
        assertEquals(365.0, state.reserve(Substance.VITAMIN_A));
        assertEquals(365.0, state.reserve(Substance.CALCIUM));
        assertEquals(180.0, state.reserve(Substance.IRON));
        assertEquals(30.0, state.reserve(Substance.ZINC));
        state.advance(31 * DAY, true, ALL);
        assertEquals(0.0, state.reserve(Substance.ZINC));
        assertEquals(1, state.tier(Substance.ZINC, true, ALL));
        assertEquals(DAY, state.deficientFor(Substance.ZINC));
        // 15 mg of zinc is one day
        eat(state, Substance.ZINC, 30);
        assertEquals(2.0, state.reserve(Substance.ZINC), 1e-9);
        assertEquals(0, state.tier(Substance.ZINC, true, ALL));
    }

    @Test
    void mineralsKeepWhatGoesAboveTheCapAndThreeCapsIsExcess() {
        MetabolismState state = new MetabolismState();
        // 30 days of zinc at 15 mg a day, eaten over many days below the upper level (40 mg)
        for (int day = 0; day < 60; day++) {
            eat(state, Substance.ZINC, 39);
            state.advance(DAY, true, ALL);
        }
        // 2.6 days a day, 1 used: +1.6 a day for 60 days on top of 30
        assertEquals(30 + 60 * 1.6, state.reserve(Substance.ZINC), 1e-6);
        assertEquals(3, state.tier(Substance.ZINC, true, ALL));
        // vitamin C (water-soluble) never goes above its cap
        eat(state, Substance.VITAMIN_C, 100_000);
        assertEquals(30.0, state.reserve(Substance.VITAMIN_C), 1e-9);
        // set() keeps an overflowing reserve
        state.set(Substance.IRON, 600);
        assertEquals(600.0, state.reserve(Substance.IRON), 1e-9);
        assertEquals(3, state.tier(Substance.IRON, true, ALL));
        assertEquals((long) Math.ceil((600 - 540) * (double) DAY), state.excessLeft(Substance.IRON, 3));
    }

    @Test
    void theDaysIntakeAgainstTheUpperLevelEndsWithTheDay() {
        MetabolismState state = new MetabolismState();
        state.advance(DAY / 4, true, ALL);
        // iron: upper level 45 mg, second excess tier at 135 mg
        eat(state, Substance.IRON, 30);
        assertEquals(0, state.tier(Substance.IRON, true, ALL));
        eat(state, Substance.IRON, 15);
        assertEquals(45.0, state.today(Substance.IRON), 1e-9);
        assertEquals(3, state.tier(Substance.IRON, true, ALL));
        assertEquals(DAY * 3 / 4, state.excessLeft(Substance.IRON, 3));
        eat(state, Substance.IRON, 90);
        assertEquals(4, state.tier(Substance.IRON, true, ALL));
        // calcium has one excess tier: three times the upper level stays at tier 3
        eat(state, Substance.CALCIUM, 8_000);
        assertEquals(3, state.tier(Substance.CALCIUM, true, ALL));
        state.advance(DAY * 3 / 4, true, ALL);
        assertEquals(0.0, state.today(Substance.IRON), 1e-9);
        assertEquals(0, state.tier(Substance.IRON, true, ALL));
        assertEquals(0, state.tier(Substance.CALCIUM, true, ALL));
        // a switched-off part has no excess
        eat(state, Substance.IRON, 50);
        assertEquals(0, state.tier(Substance.IRON, true, 0));
    }

    @Test
    void provitaminAFillsTheReserveToItsCapButIsNotCountedAgainstTheUpperLevel() {
        MetabolismState state = new MetabolismState();
        state.advance(10 * DAY, true, ALL);
        assertEquals(355.0, state.reserve(Substance.VITAMIN_A), 1e-9);
        // 80,000 µg RAE of carotenoids would be 100 days, but carotenoids stop at the cap
        state.intake(0, 0, 0, 0, new double[MetabolismState.NUTRIENTS], 80_000, 0, true, ALL);
        assertEquals(365.0, state.reserve(Substance.VITAMIN_A), 1e-9);
        assertEquals(0.0, state.today(Substance.VITAMIN_A), 1e-9);
        assertEquals(0, state.tier(Substance.VITAMIN_A, true, ALL));
        // preformed vitamin A goes above the cap and counts against 3,000 µg
        eat(state, Substance.VITAMIN_A, 3_200);
        assertEquals(369.0, state.reserve(Substance.VITAMIN_A), 1e-9);
        assertEquals(3, state.tier(Substance.VITAMIN_A, true, ALL));
    }

    @Test
    void aSwitchedOffNutrientIsNotUsedCountedOrJudged() {
        MetabolismState state = new MetabolismState();
        int noZinc = ALL & ~(1 << Substance.ZINC.nutrientIndex());
        state.advance(40 * DAY, true, noZinc);
        assertEquals(30.0, state.reserve(Substance.ZINC), 1e-9);
        assertEquals(0.0, state.reserve(Substance.VITAMIN_C), 1e-9);
        double[] nutrients = new double[MetabolismState.NUTRIENTS];
        nutrients[Substance.ZINC.nutrientIndex()] = 100;
        state.intake(0, 0, 0, 0, nutrients, 0, 0, true, noZinc);
        assertEquals(0.0, state.today(Substance.ZINC), 1e-9);
        assertEquals(0, state.tier(Substance.ZINC, true, noZinc));
        // once it is on again it is judged as it stands
        state.set(Substance.ZINC, 0);
        assertEquals(1, state.tier(Substance.ZINC, true, ALL));
        state.resetNutrient(Substance.ZINC.nutrientIndex());
        assertEquals(30.0, state.reserve(Substance.ZINC), 1e-9);
    }
}
