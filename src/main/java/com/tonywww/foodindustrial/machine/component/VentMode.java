package com.tonywww.foodindustrial.machine.component;

import java.util.Locale;

/**
 * Venting switch of an output tank (machines/03 §4, 2026-11-05): nothing, vent what blocks the machine
 * (overflow) or vent everything. At most 100 mB per tick (400 mB per batch), gases only.
 */
public enum VentMode {
    OFF, OVERFLOW, ALL;

    /** mB vented per batch of 4 ticks. */
    public static final int PER_BATCH = 400;

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public VentMode cycle(boolean forwards) {
        VentMode[] values = values();
        return values[Math.floorMod(ordinal() + (forwards ? 1 : -1), values.length)];
    }

    public static VentMode byIndex(int index) {
        VentMode[] values = values();
        return values[Math.floorMod(index, values.length)];
    }
}
