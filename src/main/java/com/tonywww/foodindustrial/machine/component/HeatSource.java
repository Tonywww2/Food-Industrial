package com.tonywww.foodindustrial.machine.component;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;

/**
 * Fuel fire of a T1 machine, like a vanilla furnace (2026-11-04): a new item is lit only when a recipe
 * can run; once lit it burns to the end, running or not. Burn times are the vanilla furnace values.
 */
public final class HeatSource {
    private int burnLeft;
    private int burnTotal;

    public boolean lit() {
        return burnLeft > 0;
    }

    public int burnLeft() {
        return burnLeft;
    }

    public int burnTotal() {
        return burnTotal;
    }

    /** Lights an item that burns {@code ticks}. */
    public void light(int ticks) {
        burnLeft = ticks;
        burnTotal = ticks;
    }

    /** Burns {@code ticks}; returns whether the fire is still lit. */
    public boolean burn(int ticks) {
        burnLeft = Math.max(0, burnLeft - ticks);
        return burnLeft > 0;
    }

    public void save(DataWriter out) {
        if (burnLeft > 0) {
            out.putInt("BurnLeft", burnLeft);
            out.putInt("BurnTotal", burnTotal);
        }
    }

    public void load(DataReader in) {
        burnLeft = Math.max(0, in.getInt("BurnLeft", 0));
        burnTotal = Math.max(burnLeft, in.getInt("BurnTotal", 0));
    }
}
