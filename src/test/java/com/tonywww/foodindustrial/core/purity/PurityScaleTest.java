package com.tonywww.foodindustrial.core.purity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurityScaleTest {
    /** SHA-256 of the 256 table values (big-endian IEEE 754 bits); identical on every node and platform. */
    private static final String TABLE_HASH = "22ffe4af6f27bd827beebb6068de5e9ec4a518012abd7d12423784ddaac3b6c0";

    @Test
    void tableEnds() {
        assertEquals(0.0, PurityScale.fraction(-128));
        assertEquals(1.0, PurityScale.fraction(127));
        assertEquals(0.5, PurityScale.fraction(0));
        assertEquals(0.0, PurityScale.fraction(-500), "out of range clamps");
        assertEquals(1.0, PurityScale.fraction(500), "out of range clamps");
    }

    @Test
    void tableIsIdenticalEverywhere() throws NoSuchAlgorithmException {
        ByteBuffer buffer = ByteBuffer.allocate(256 * Long.BYTES);
        for (int b = -128; b <= 127; b++) {
            buffer.putLong(Double.doubleToLongBits(PurityScale.fraction(b)));
        }
        String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(buffer.array()));
        assertEquals(TABLE_HASH, hash);
    }

    @Test
    void everyValueRoundTrips() {
        for (int b = -128; b <= 127; b++) {
            double p = PurityScale.fraction(b);
            assertEquals(b, PurityScale.ceilOf(p), "ceilOf(p(" + b + "))");
            assertEquals(b, PurityScale.floorOf(p), "floorOf(p(" + b + "))");
            if (b > -128) {
                assertTrue(p > PurityScale.fraction(b - 1), "strictly increasing at " + b);
                double between = (p + PurityScale.fraction(b - 1)) / 2;
                assertEquals(b, PurityScale.ceilOf(between), "ceilOf between " + (b - 1) + " and " + b);
                assertEquals(b - 1, PurityScale.floorOf(between), "floorOf between " + (b - 1) + " and " + b);
            }
        }
        assertEquals(-128, PurityScale.ceilOf(-1.0));
        assertEquals(127, PurityScale.floorOf(2.0));
    }

    @Test
    void referenceTable() {
        // additives/10 §1.2
        Map<Integer, String> table = Map.ofEntries(
            Map.entry(-128, "0"), Map.entry(-127, "<0.01"), Map.entry(-76, "0.10"), Map.entry(-51, "0.97"),
            Map.entry(-32, "5.2"), Map.entry(-24, "10"), Map.entry(-15, "20"), Map.entry(0, "50"),
            Map.entry(16, "81"), Map.entry(20, "85"), Map.entry(25, "90.6"), Map.entry(33, "95.2"),
            Map.entry(39, "97.1"), Map.entry(43, "98.0"), Map.entry(51, "99.0"), Map.entry(59, "99.5"),
            Map.entry(69, "99.8"), Map.entry(77, "99.90"), Map.entry(102, "99.99"), Map.entry(127, "100"));
        table.forEach((b, text) -> assertEquals(text, PurityScale.format(b), "display of " + b));
        assertEquals("99.99", PurityScale.format(126), "only 127 shows 100%");
    }

    @Test
    void displayTruncates() {
        for (int b = -127; b <= 126; b++) {
            String text = PurityScale.format(b);
            if (!text.startsWith("<")) {
                assertTrue(new BigDecimal(text).doubleValue() <= PurityScale.percent(b), "display of " + b + " above the value");
            }
        }
    }

    @Test
    void writtenValuesRoundUp() {
        assertEquals(-128, PurityScale.ofPercent(0));
        assertEquals(127, PurityScale.ofPercent(100));
        assertEquals(0, PurityScale.ofPercent(50));
        assertEquals("83", PurityScale.format(PurityScale.ofPercent(83)));
        assertEquals("7.3", PurityScale.format(PurityScale.ofPercent(7)), "bittern content");
        assertEquals("71", PurityScale.format(PurityScale.ofPercent(70)), "resin content");
        assertThrows(IllegalArgumentException.class, () -> PurityScale.ofPercent(100.5));
        assertThrows(IllegalArgumentException.class, () -> PurityScale.ofPercent(-1));
    }

    @Test
    void writtenDecimals() {
        for (String ok : new String[]{"0.25", "0.2", "3.5", "33", "92.4", "99.8", "99.99", "100", "0"}) {
            assertDoesNotThrow(() -> PurityScale.checkWritten(new BigDecimal(ok)), ok);
        }
        for (String bad : new String[]{"0.255", "3.55", "33.5", "92.45", "99.995", "100.5", "-1"}) {
            assertThrows(IllegalArgumentException.class, () -> PurityScale.checkWritten(new BigDecimal(bad)), bad);
        }
    }

    @Test
    void seaSaltRecrystallization() {
        // additives/10 §2.1: 83% sea salt, 50% of the impurities removed, base tier T0
        double start = PurityScale.fraction(PurityScale.ofPercent(83));
        assertEquals("90.6", PurityScale.format(PurityScale.floorOf(PurityScale.removal(start, 50, 0))), "T0");
        assertEquals("92.6", PurityScale.format(PurityScale.floorOf(PurityScale.removal(start, 50, 1))), "T1");
        assertEquals("94.3", PurityScale.format(PurityScale.floorOf(PurityScale.removal(start, 50, 2))), "T2");
        int b = PurityScale.ofPercent(83);
        String[] expected = {"90.6", "94.7", "97.1"};
        for (String text : expected) {
            b = PurityScale.floorOf(PurityScale.removal(PurityScale.fraction(b), 50, 0));
            assertEquals(text, PurityScale.format(b));
        }
    }

    @Test
    void sourceMode() {
        // machines/01: kelp ash, source 8%, base tier T0
        assertEquals("8.6", PurityScale.format(PurityScale.source(8, 0)));
        assertEquals("11", PurityScale.format(PurityScale.source(8, 1)));
        assertEquals(PurityScale.ofPercent(83), PurityScale.source(83, 0));
    }

    @Test
    void externalDefaults() {
        // additives/10 §1.6
        assertEquals("74", PurityScale.format(PurityScale.externalDefault(PurityScale.ofPercent(85))), "sugar");
        assertEquals("82", PurityScale.format(PurityScale.externalDefault(PurityScale.ofPercent(90))), "salt");
        assertEquals("1.8", PurityScale.format(PurityScale.externalDefault(PurityScale.ofPercent(3.5))), "acetic acid");
    }

    @Test
    void weightedAverageCountsItemsAsOneBucket() {
        int low = PurityScale.ofPercent(20);
        int high = PurityScale.ofPercent(90);
        double expected = (PurityScale.fraction(low) + PurityScale.fraction(high)) / 2;
        assertEquals(expected, PurityScale.weightedAverage(new int[]{low, high}, new long[]{PurityScale.ITEM_AMOUNT, 1000}), 1e-15);
        double weighted = (2 * PurityScale.fraction(low) + 0.5 * PurityScale.fraction(high)) / 2.5;
        assertEquals(weighted, PurityScale.weightedAverage(new int[]{low, high}, new long[]{2 * PurityScale.ITEM_AMOUNT, 500}), 1e-15);
    }

    @Test
    void degradation() {
        double p = PurityScale.fraction(PurityScale.ofPercent(90));
        assertEquals(p * 0.9, PurityScale.degrade(p, 10), 1e-15);
    }
}
