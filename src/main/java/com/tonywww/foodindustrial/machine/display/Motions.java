package com.tonywww.foodindustrial.machine.display;

/**
 * The small movements on a panel (machines/04 §9.3), as numbers: everything is a function of the time
 * in ticks, so the client computes them each frame without any data from the server.
 */
public final class Motions {
    /** Ticks a product swells after it arrives. */
    public static final float POP_TICKS = 8;
    /** Index of each value {@link #input} writes. */
    public static final int DX = 0;
    public static final int DY = 1;
    public static final int STRETCH_X = 2;
    public static final int STRETCH_Y = 3;
    public static final int TURN = 4;
    private static final float TAU = (float) (Math.PI * 2);

    private Motions() {
    }

    /**
     * How input {@code index} of {@code count} moves at {@code time}: an offset in panel pixels, a
     * stretch along x and y and a turn in radians, written to {@code out}.
     */
    public static void input(Motion motion, float time, int index, int count, float[] out) {
        out[DX] = 0;
        out[DY] = 0;
        out[STRETCH_X] = 1;
        out[STRETCH_Y] = 1;
        out[TURN] = 0;
        switch (motion) {
            case GRIND -> {
                out[DX] = 0.7F * sin(2.9F * time + 1.7F * index);
                out[DY] = 0.7F * sin(3.7F * time + 2.3F * index + 1);
                out[TURN] = 0.12F * sin(2.3F * time + index);
            }
            case PRESS -> {
                float squeeze = Math.max(0, sin(0.35F * time + 0.9F * index));
                squeeze *= squeeze;
                out[STRETCH_Y] = 1 - 0.28F * squeeze;
                out[STRETCH_X] = 1 + 0.14F * squeeze;
                // the icon is 16 pixels high: keep its lower edge where it was
                out[DY] = -8 * (1 - out[STRETCH_Y]);
            }
            case HEAT -> out[DY] = 0.6F * sin(0.2F * time + index);
            case MIX -> {
                float angle = 0.22F * time + TAU * index / Math.max(1, count);
                out[DX] = 1.6F * (float) Math.cos(angle);
                out[DY] = 1.6F * sin(angle);
                out[TURN] = 0.15F * sin(angle);
            }
            case FERMENT -> {
                float swell = 1 + 0.07F * sin(0.14F * time + 1.3F * index);
                out[STRETCH_X] = swell;
                out[STRETCH_Y] = swell;
            }
            default -> {
            }
        }
    }

    /** Size of a product {@code age} ticks after it arrived: it swells and settles back to 1. */
    public static float pop(float age) {
        if (age < 0 || age >= POP_TICKS) {
            return 1;
        }
        float left = 1 - age / POP_TICKS;
        return 1 + 0.4F * left * left;
    }

    /**
     * How far the work is, 0 to 1: the progress the server last reported, moved on by the ticks since
     * while the machine runs ({@code rate} is progress per tick in thousandths).
     */
    public static float progress(int progress, int total, int rate, float ticksSince, boolean running) {
        if (total <= 0) {
            return 0;
        }
        float now = progress + (running ? Math.max(0, ticksSince) * rate / 1000F : 0);
        return Math.max(0, Math.min(1, now / total));
    }

    /** Where along its way (0 to 1) puff or bubble {@code index} of {@code count} is, each a third apart. */
    public static float phase(float time, float period, int index, int count) {
        float phase = time / period + (float) index / count;
        return phase - (float) Math.floor(phase);
    }

    private static float sin(float angle) {
        return (float) Math.sin(angle);
    }
}
