package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.purity.PurityTestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(PurityScale.ofPercent(7), OutcomeResolver.apply(PurityRule.SOURCE_DEFAULT, material("bittern"), Double.NaN, 0));
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
