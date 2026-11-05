package com.tonywww.foodindustrial.machine.component;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;

/**
 * Wear of the items in the catalyst slots (machines/03 §1, additives/08 §催化剂与助剂): a counter per
 * slot counts completions; when it reaches the recipe's {@code runs}, one item is used up and the count
 * starts again. The items themselves never change, so they stack and stay one AE2 type.
 */
public final class CatalystCounters {
    private final int[] used;

    public CatalystCounters(int slots) {
        used = new int[slots];
    }

    public int used(int index) {
        return used[index];
    }

    /** Counts one completion; returns whether one item is now used up. */
    public boolean wear(int index, int runs) {
        used[index]++;
        if (used[index] >= runs) {
            used[index] = 0;
            return true;
        }
        return false;
    }

    /** Starts over (the item in the slot was taken out). */
    public void reset(int index) {
        used[index] = 0;
    }

    public void save(DataWriter out) {
        for (int value : used) {
            if (value != 0) {
                out.putIntArray("CatalystUse", used);
                return;
            }
        }
    }

    public void load(DataReader in) {
        int[] values = in.getIntArray("CatalystUse");
        for (int i = 0; i < used.length; i++) {
            used[i] = i < values.length ? Math.max(0, values[i]) : 0;
        }
    }
}
