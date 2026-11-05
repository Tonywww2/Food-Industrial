package com.tonywww.foodindustrial.machine.processing;

import java.util.Locale;

/**
 * State of a machine as shown in the status line (machines/04 §1, §7): the key
 * {@code gui.foodindustrial.status.<id>}.
 */
public enum MachineStatus {
    /** Processing a recipe. */
    RUNNING(false),
    /** No recipe for the inputs. */
    WAITING(true),
    /** The products do not fit. */
    OUTPUT_FULL(true),
    /** Not enough energy or fuel. */
    NO_POWER(true),
    /** A condition of the recipe is missing right now (mode, module, air). */
    MISSING(true),
    /** Redstone control forbids running. */
    REDSTONE(true),
    /** Multiblock structure not formed. */
    UNFORMED(true);

    private final boolean sleeps;

    MachineStatus(boolean sleeps) {
        this.sleeps = sleeps;
    }

    /** Whether the machine sleeps in this state until an event wakes it (machines/02 §6.2). */
    public boolean sleeps() {
        return sleeps;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }
}
