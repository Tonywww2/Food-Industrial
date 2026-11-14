package com.tonywww.foodindustrial.food.metabolism;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.platform.network.PlatformNetwork;

import java.util.Arrays;

/**
 * What a player's client knows of its metabolism (foods/02 §8): displayed amounts, tiers, and the server's
 * configuration of the parts and of unconsciousness. Sent by the server to its own player only.
 */
public final class MetabolismView implements PlatformNetwork.Payload {
    /** Amounts by substance ordinal: mg, %, g in the window, units, and reserve days for the nutrients. */
    final double[] amounts = new double[Substance.all().length];
    final int[] tiers = new int[Substance.all().length];
    /** Ticks until each substance leaves its tier downwards (−1: no change ahead), as of the sync. */
    final long[] next = new long[Substance.all().length];
    /** Ticks since each nutrient's reserve ran out (−1: not out), as of the sync. */
    final long[] deficient = new long[Substance.all().length];
    boolean unconscious;
    /** The player's metabolic rate: {@link #next} and {@link #deficient} are in the body's time, which runs this many times as fast. */
    double rate = 1.0;
    boolean impurityOn = true;
    boolean nutrientsOn = true;
    /** Unconsciousness settings, by {@link Unconscious} ordinal. */
    final boolean[] unconsciousRules = new boolean[Unconscious.values().length];

    /** The configurable parts of unconsciousness (foods/03 §3.1). */
    public enum Unconscious {
        SCREEN, MOVEMENT, ATTACK, BLOCKS, USE_ITEMS, CONTAINERS, WAKE_ON_DAMAGE
    }

    public double amount(Substance substance) {
        return amounts[substance.ordinal()];
    }

    public int tier(Substance substance) {
        return tiers[substance.ordinal()];
    }

    /** Ticks until {@code substance} leaves its tier downwards as of the sync, or −1. */
    public long next(Substance substance) {
        return next[substance.ordinal()];
    }

    /** Ticks since a nutrient's reserve ran out as of the sync, or −1 while it has some. */
    public long deficient(Substance substance) {
        return deficient[substance.ordinal()];
    }

    public boolean unconscious() {
        return unconscious;
    }

    /** The player's metabolic rate as of the sync (1: normal). */
    public double rate() {
        return rate;
    }

    /** Whether unconsciousness, when the player is unconscious, applies {@code rule}. */
    public boolean applies(Unconscious rule) {
        return unconscious && unconsciousRules[rule.ordinal()];
    }

    /** Whether the server's configuration has unconsciousness apply {@code rule} (whether or not unconscious now). */
    public boolean rule(Unconscious rule) {
        return unconsciousRules[rule.ordinal()];
    }

    /** Whether a substance is shown and active under the server's configuration. */
    public boolean shown(Substance substance) {
        return substance == Substance.IMPURITY ? impurityOn : !substance.nutrient() || nutrientsOn;
    }

    /** Copies {@code other} into this view. */
    void copy(MetabolismView other) {
        System.arraycopy(other.amounts, 0, amounts, 0, amounts.length);
        System.arraycopy(other.tiers, 0, tiers, 0, tiers.length);
        System.arraycopy(other.next, 0, next, 0, next.length);
        System.arraycopy(other.deficient, 0, deficient, 0, deficient.length);
        System.arraycopy(other.unconsciousRules, 0, unconsciousRules, 0, unconsciousRules.length);
        unconscious = other.unconscious;
        rate = other.rate;
        impurityOn = other.impurityOn;
        nutrientsOn = other.nutrientsOn;
    }

    /** Whether the client would show something different from {@code other}. Allocation-free. */
    boolean differs(MetabolismView other) {
        if (!Arrays.equals(tiers, other.tiers) || unconscious != other.unconscious || impurityOn != other.impurityOn
            || nutrientsOn != other.nutrientsOn || rate != other.rate || !Arrays.equals(unconsciousRules, other.unconsciousRules)) {
            return true;
        }
        for (Substance substance : Substance.all()) {
            if (MetabolismText.displayed(substance, amounts[substance.ordinal()])
                != MetabolismText.displayed(substance, other.amounts[substance.ordinal()])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void write(DataWriter out) {
        for (Substance substance : Substance.all()) {
            out.putDouble(substance.id(), amounts[substance.ordinal()]);
            out.putInt(substance.id() + "_tier", tiers[substance.ordinal()]);
            out.putLong(substance.id() + "_next", next[substance.ordinal()]);
            out.putLong(substance.id() + "_deficient", deficient[substance.ordinal()]);
        }
        out.putBoolean("unconscious", unconscious);
        out.putDouble("rate", rate);
        out.putBoolean("impurity_on", impurityOn);
        out.putBoolean("nutrients_on", nutrientsOn);
        for (Unconscious rule : Unconscious.values()) {
            out.putBoolean("rule_" + rule.ordinal(), unconsciousRules[rule.ordinal()]);
        }
    }

    static MetabolismView read(DataReader in) {
        MetabolismView view = new MetabolismView();
        for (Substance substance : Substance.all()) {
            view.amounts[substance.ordinal()] = in.getDouble(substance.id(), 0);
            view.tiers[substance.ordinal()] = in.getInt(substance.id() + "_tier", 0);
            view.next[substance.ordinal()] = in.getLong(substance.id() + "_next", -1);
            view.deficient[substance.ordinal()] = in.getLong(substance.id() + "_deficient", -1);
        }
        view.unconscious = in.getBoolean("unconscious", false);
        view.rate = in.getDouble("rate", 1.0);
        view.impurityOn = in.getBoolean("impurity_on", true);
        view.nutrientsOn = in.getBoolean("nutrients_on", true);
        for (Unconscious rule : Unconscious.values()) {
            view.unconsciousRules[rule.ordinal()] = in.getBoolean("rule_" + rule.ordinal(), false);
        }
        return view;
    }
}
