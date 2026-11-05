package com.tonywww.foodindustrial.gui;

/** Action codes the screens send with {@link FIMenu#send}; the server validates every argument. */
public final class MenuActions {
    /** {@code a} face; {@code b} channel (0 items, 1 fluids) × 16 + mode. */
    public static final int SIDE = 1;
    /** {@code a} face; {@code b} item output scope. */
    public static final int SCOPE_ITEMS = 2;
    /** {@code a} face; {@code b} output tank (0-based among output tanks) or -1 for all. */
    public static final int SCOPE_FLUID_OUT = 3;
    /** {@code a} face; {@code b} input tank or -1 for all. */
    public static final int SCOPE_FLUID_IN = 4;
    /** {@code a} switch (0 input items, 1 input fluids, 2 output items, 3 output fluids); {@code b} on. */
    public static final int AUTO = 5;
    /** {@code a} redstone control. */
    public static final int REDSTONE = 6;
    /** {@code a} mode index. */
    public static final int MODE = 7;
    /** {@code a} inventory slot. */
    public static final int LOCK_SLOT = 8;
    /** {@code a} tank. */
    public static final int LOCK_TANK = 9;
    /** {@code a} tank: fill from or into the carried fluid container. */
    public static final int TANK_CLICK = 10;
    /** {@code a} tank: empty it (confirmed on the client). */
    public static final int TANK_DUMP = 11;
    /** {@code a} output tank; {@code b} venting switch ({@code VentMode} ordinal). */
    public static final int VENT = 12;
    /** Port: {@code a} direction (input, output, off). */
    public static final int PORT_MODE = 20;
    /** Port: {@code b} automatic output on. */
    public static final int PORT_AUTO = 21;

    private MenuActions() {
    }
}
