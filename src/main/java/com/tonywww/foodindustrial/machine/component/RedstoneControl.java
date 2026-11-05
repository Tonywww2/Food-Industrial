package com.tonywww.foodindustrial.machine.component;

import java.util.Locale;

/** Redstone control of a machine (machines/04 §2): ignore, run on low, run on high, disabled. */
public enum RedstoneControl {
    IGNORE, LOW, HIGH, DISABLED;

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    /** Whether the machine may run with this signal. */
    public boolean allows(boolean powered) {
        return switch (this) {
            case IGNORE -> true;
            case LOW -> !powered;
            case HIGH -> powered;
            case DISABLED -> false;
        };
    }

    public static RedstoneControl byIndex(int index) {
        RedstoneControl[] values = values();
        return values[Math.floorMod(index, values.length)];
    }
}
