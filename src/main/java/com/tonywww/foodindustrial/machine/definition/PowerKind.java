package com.tonywww.foodindustrial.machine.definition;

/** How a machine is powered (machines/03 §1). */
public enum PowerKind {
    /**
     * No power: passive devices and machines driven by time alone. A fuel slot on such a machine is
     * optional fuel that burns while it runs (M06's brazier, I14).
     */
    NONE,
    /** Furnace fuel in the fuel slot (T1 heating machines). */
    FUEL,
    /** FE from any face that is not closed (T2 and up). */
    ENERGY,
    /**
     * Human power (M01, M02; additives/06 §T1 作坊, 2026-11-10): a hand crank on top stores up to
     * {@link #MANUAL_MAX} ticks of running, {@link #MANUAL_PER_TURN} per turn.
     */
    MANUAL;

    /** Running ticks one turn of the hand crank gives. */
    public static final int MANUAL_PER_TURN = 20;
    /** Running ticks a manual machine stores at most. */
    public static final int MANUAL_MAX = 100;
}
