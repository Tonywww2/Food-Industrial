package com.tonywww.foodindustrial.machine;

import java.util.Map;
import java.util.TreeMap;

/**
 * Batch timing per machine type for {@code /foodindustrial perf} (machines/02 §6.4 rule 15). Off by
 * default; when off, the ticker pays one static field read.
 */
public final class MachinePerf {
    private static volatile boolean enabled;
    private static final Map<String, long[]> STATS = new TreeMap<>();
    private static long startTick;

    private MachinePerf() {
    }

    public static boolean enabled() {
        return enabled;
    }

    public static synchronized void start(long gameTime) {
        STATS.clear();
        startTick = gameTime;
        enabled = true;
    }

    public static void stop() {
        enabled = false;
    }

    /** Records one batch: {@code [batches, nanos, batches that ended asleep]}. */
    public static synchronized void record(String type, long nanos, boolean asleep) {
        long[] stats = STATS.computeIfAbsent(type, key -> new long[3]);
        stats[0]++;
        stats[1] += nanos;
        if (asleep) {
            stats[2]++;
        }
    }

    /** Lines of the report: per type batches, average µs per batch and total ms per tick. */
    public static synchronized String[] report(long gameTime) {
        long ticks = Math.max(1, gameTime - startTick);
        String[] lines = new String[STATS.size() + 1];
        lines[0] = "machine perf over " + ticks + " ticks";
        int i = 1;
        for (Map.Entry<String, long[]> entry : STATS.entrySet()) {
            long[] s = entry.getValue();
            lines[i++] = String.format("%s: %d batches (%d asleep), %.2f µs/batch, %.4f ms/tick", entry.getKey(), s[0], s[2],
                s[1] / 1000.0 / Math.max(1, s[0]), s[1] / 1_000_000.0 / ticks);
        }
        return lines;
    }
}
