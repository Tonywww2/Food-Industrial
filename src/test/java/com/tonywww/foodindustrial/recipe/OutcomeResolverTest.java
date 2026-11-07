package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.purity.PurityTestData;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OutcomeResolverTest {
    @BeforeAll
    static void materials() {
        PurityTestData.registerMaterials();
    }

    private static PurityData.Material material(String id) {
        return PurityData.current().material("foodindustrial:" + id);
    }

    private static double start(int b, long amount) {
        return PurityScale.weightedAverage(new int[]{b}, new long[]{amount});
    }

    private static String format(int b) {
        return PurityScale.format(b);
    }

    @Test
    void seaSaltRecrystallization() {
        // additives/10 §2.1: 4 × 83% sea salt → 2, 50% of the impurities removed, base tier T0
        PurityData.Material salt = material("salt");
        PurityRule rule = PurityRule.removal(50);
        double start = start(PurityScale.ofPercent(83), 4 * PurityScale.ITEM_AMOUNT);
        assertEquals("90.6", format(OutcomeResolver.apply(rule, salt, start, 0)), "T0");
        assertEquals("92.6", format(OutcomeResolver.apply(rule, salt, start, 1)), "T1");
        assertEquals("94.3", format(OutcomeResolver.apply(rule, salt, start, 2)), "T2");
        int b = PurityScale.ofPercent(83);
        String[] expected = {"90.6", "94.7", "97.1"};
        for (String value : expected) {
            b = OutcomeResolver.apply(rule, salt, start(b, 4 * PurityScale.ITEM_AMOUNT), 0);
            assertEquals(value, format(b));
        }
    }

    @Test
    void sourceModeWithTierBonus() {
        // machines/01 §3: gypsum, source 80, base T0 → mortar 81%, stone mill 84%, crusher 88%
        PurityData.Material gypsum = material("gypsum");
        PurityRule rule = PurityRule.source(80);
        assertEquals("81", format(OutcomeResolver.apply(rule, gypsum, Double.NaN, 0)));
        assertEquals("84", format(OutcomeResolver.apply(rule, gypsum, Double.NaN, 1)));
        assertEquals("88", format(OutcomeResolver.apply(rule, gypsum, Double.NaN, 2)));
        // machines/01 §5: kelp ash, source 8% (K0), campfire T0 8.6%, earthen kiln T1 11%
        PurityData.Material ash = material("kelp_ash");
        assertEquals("8.6", format(OutcomeResolver.apply(PurityRule.source(8), ash, Double.NaN, 0)));
        assertEquals("11", format(OutcomeResolver.apply(PurityRule.source(8), ash, Double.NaN, 1)));
    }

    @Test
    void sourceDefaultsToMaterialData() {
        assertEquals(PurityScale.ofPercent(83), OutcomeResolver.apply(PurityRule.SOURCE_DEFAULT, material("salt"), Double.NaN, 0));
        // K0 with a source purity (no such row in the table now; additives/10 §1.4)
        assertEquals(PurityScale.ofPercent(7), OutcomeResolver.apply(PurityRule.SOURCE_DEFAULT, PurityTestData.k0Material(7), Double.NaN, 0));
        // no source purity: the crude bound
        PurityData.Material ethanol = material("ethanol");
        assertEquals(ethanol.boundBytes()[0], OutcomeResolver.apply(PurityRule.SOURCE_DEFAULT, ethanol, Double.NaN, 0));
        // K0 without source purity: none
        assertEquals(OutcomeResolver.NONE, OutcomeResolver.apply(PurityRule.SOURCE_DEFAULT, material("brine"), Double.NaN, 0));
        // a rule reading inputs without a start purity falls back to the source mode
        assertEquals(PurityScale.ofPercent(83), OutcomeResolver.apply(PurityRule.removal(50), material("salt"), Double.NaN, 0));
    }

    @Test
    void keepDegradeAndMax() {
        PurityData.Material salt = material("salt");
        for (int b = PurityScale.MIN; b <= PurityScale.MAX; b++) {
            assertEquals(b, OutcomeResolver.apply(PurityRule.KEEP, salt, start(b, 3000), 2), "keep " + b);
        }
        int b = PurityScale.ofPercent(90);
        assertEquals(PurityScale.floorOf(PurityScale.fraction(b) * 0.9), OutcomeResolver.apply(PurityRule.degrade(10), salt, start(b, 1000), 3));
        int capped = OutcomeResolver.apply(PurityRule.removal(90).withMax(97), salt, start(PurityScale.ofPercent(95), 1000), 2);
        assertEquals(PurityScale.ofPercent(97), capped);
        // keep and degrade take no tier bonus
        assertEquals(OutcomeResolver.apply(PurityRule.degrade(10), salt, start(b, 1000), 0),
            OutcomeResolver.apply(PurityRule.degrade(10), salt, start(b, 1000), 4));
    }

    /** additives/10 §2.1 step 4: t0_max = refined on the H03 pot, then the same recipe on T2 (I29 acceptance). */
    @Test
    void t0MaxCapsRepeatedHandRecrystallization() {
        PurityData.Material salt = material("salt");
        PurityRule rule = PurityRule.removal(50).withT0Max(PurityGrade.REFINED);
        String[] expected = {"90.6", "94.7", "95.2", "95.2", "95.2"};
        int b = PurityScale.ofPercent(83);
        for (String step : expected) {
            b = OutcomeResolver.apply(rule, salt, start(b, 4 * PurityScale.ITEM_AMOUNT), 0, true, false);
            assertEquals(step, format(b), "T0");
        }
        assertEquals("98.4", format(OutcomeResolver.apply(rule, salt, start(b, 4 * PurityScale.ITEM_AMOUNT), 2, false, false)), "T2");
        // above T0 the cap does not apply
        double from = start(PurityScale.ofPercent(94.7), 4000);
        assertEquals(OutcomeResolver.apply(PurityRule.removal(50), salt, from, 0), OutcomeResolver.apply(rule, salt, from, 0, false, false));
        assertEquals("97.1", format(OutcomeResolver.apply(rule, salt, from, 0, false, false)));
    }

    /** A cap never lowers the start purity: 98% sea salt on T0 stays at least 98%. */
    @Test
    void capsDoNotLowerTheStart() {
        PurityData.Material salt = material("salt");
        int start = PurityScale.ofPercent(98);
        int t0 = OutcomeResolver.apply(PurityRule.removal(50).withT0Max(PurityGrade.REFINED), salt, start(start, 4000), 0, true, false);
        assertTrue(t0 >= start, "t0_max: " + format(t0));
        int max = OutcomeResolver.apply(PurityRule.removal(50).withMax(95), salt, start(start, 4000), 3, false, false);
        assertTrue(max >= start, "max: " + format(max));
        assertEquals(PurityScale.ofPercent(95), OutcomeResolver.apply(PurityRule.removal(50).withMax(95), salt, start(PurityScale.ofPercent(90), 4000), 3, false, false));
    }

    /** R19 compounding: the K12 product takes its food bound (95.2%); a crafting result of 20b shows 85% (crude). */
    @Test
    void compoundProducts() {
        PurityData.Material gumBase = material("gum_base");
        assertEquals("foodindustrial:k12_compound", gumBase.category());
        int b = OutcomeResolver.apply(PurityRule.SOURCE_FOOD, gumBase, start(PurityScale.ofPercent(99), 3000), 0, false, false);
        assertEquals("95.2", format(b));
        assertEquals(PurityGrade.FOOD, gumBase.gradeOf(b).orElseThrow());
        assertEquals("85", format(20));
        assertEquals(PurityGrade.CRUDE, gumBase.gradeOf(20).orElseThrow());
        assertTrue(gumBase.lowerBound(PurityGrade.LAB).isEmpty(), "K12 has no lab grade");
    }

    /** additives/10 §2.1 step 6: the standardisation module lowers to the grade's lower bound (I29 acceptance). */
    @Test
    void standardisation() {
        PurityData.Material salt = material("salt");
        int food = PurityScale.ofPercent(98.4);
        assertEquals(46, food);
        assertEquals(39, OutcomeResolver.normalize(food, salt));
        assertEquals("97.1", format(OutcomeResolver.normalize(food, salt)));
        assertEquals(33, OutcomeResolver.normalize(33, salt), "refined 95.2% is the bound");
        assertEquals(20, OutcomeResolver.normalize(20, salt), "poor 85% unchanged");
        PurityData.Material brine = material("brine");
        int content = PurityScale.ofPercent(26);
        assertEquals(content, OutcomeResolver.normalize(content, brine), "K0 unchanged");
        assertEquals(OutcomeResolver.NONE, OutcomeResolver.normalize(OutcomeResolver.NONE, salt));
        // through apply: the T2 result 98.4% drops to 97.1% on a standardising device
        int b = OutcomeResolver.apply(PurityRule.removal(50).withT0Max(PurityGrade.REFINED), salt, start(33, 4000), 2, false, true);
        assertEquals("97.1", format(b));
    }

    @Test
    void weightedStartPurity() {
        // 3 items at 90% and 1000 mB at 80%: weights 3000 and 1000
        int high = PurityScale.ofPercent(90);
        int low = PurityScale.ofPercent(80);
        double expected = (PurityScale.fraction(high) * 3000 + PurityScale.fraction(low) * 1000) / 4000;
        assertEquals(expected, PurityScale.weightedAverage(new int[]{high, low}, new long[]{3000, 1000}), 1e-15);
        assertEquals(PurityScale.floorOf(expected), HomogenizeRule.homogenize(new int[]{high, low}, new long[]{3000, 1000}));
    }

    @Test
    void yieldIsProbabilisticWithUnchangedExpectation() {
        // machines/01 §1 rule 3: T2 ×1.1
        assertEquals(100, hits(1, 1100));
        assertEquals(400, hits(4, 1100));
        for (int roll = 0; roll < 1000; roll++) {
            assertEquals(25, OutcomeResolver.extra(250, 1100, roll), "fluid 250 mB");
            assertEquals(0, OutcomeResolver.extra(5, 1000, roll), "T1");
        }
        // T3 ×1.2 with 6 items: 1 always, 20% one more
        assertEquals(1000 + 200, totalExtra(6, 1200));
        assertEquals(250, hits(1, 1250));
    }

    private static int hits(int amount, int yield) {
        int hits = 0;
        for (int roll = 0; roll < 1000; roll++) {
            hits += OutcomeResolver.extra(amount, yield, roll);
        }
        return hits;
    }

    private static int totalExtra(int amount, int yield) {
        return hits(amount, yield);
    }

    @Test
    void profiles() {
        DeviceProfile t2 = DeviceProfile.standard(com.tonywww.foodindustrial.core.Tier.T2);
        assertEquals(2, t2.speed());
        assertEquals(1100, t2.yieldPermille());
        assertEquals(1500, DeviceProfile.standard(com.tonywww.foodindustrial.core.Tier.T3).energyPermille());
        assertEquals(1250, DeviceProfile.standard(com.tonywww.foodindustrial.core.Tier.T4).yieldPermille());
        assertEquals(1000, DeviceProfile.standard(com.tonywww.foodindustrial.core.Tier.T1).yieldPermille());
    }
}
