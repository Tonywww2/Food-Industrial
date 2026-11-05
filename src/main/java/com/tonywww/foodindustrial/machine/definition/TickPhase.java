package com.tonywww.foodindustrial.machine.definition;

/**
 * Scheduling phase of a machine type (machines/02 §6.1 rule 2): machines run every 4 ticks, only on
 * the tick where {@code (gameTime & 3) == phase}; one phase per family of machines so that the same
 * kind of work runs together and the families spread over the four ticks.
 */
public enum TickPhase {
    /** Mechanical and separation: M01, M02, M04, M11, M12, M18, M19, M21. */
    MECHANICAL(0),
    /** Heating and kilns: M03, M07, M08, M10, M20, M22, M26, M27. */
    HEATING(1),
    /** Crystallizing, drying, refining: M09, M15, M17, M24, M25, M28, M31. */
    REFINING(2),
    /** Reactions and biology: M05, M06, M14, M23, M29, M30. */
    REACTION(3);

    /** Batch length of machines, ticks. */
    public static final int BATCH = 4;
    /** Batch length of T0 passive devices, ticks (machines/02 §6.1 rule 3). */
    public static final int PASSIVE_BATCH = 20;

    private final int tick;

    TickPhase(int tick) {
        this.tick = tick;
    }

    /** Tick within the 4-tick cycle, 0–3. */
    public int tick() {
        return tick;
    }
}
