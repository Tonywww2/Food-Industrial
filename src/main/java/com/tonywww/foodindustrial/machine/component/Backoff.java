package com.tonywww.foodindustrial.machine.component;

/**
 * Back-off of automatic input or output (machines/02 §6.3 rule 7): at most one attempt per batch; when
 * the target cannot take anything the interval doubles 4 → 8 → 16 … up to 80 ticks; one success
 * resets it to 4.
 */
public final class Backoff {
    public static final int MIN = 4;
    public static final int MAX = 80;

    private int interval = MIN;
    private long next;

    /** Whether an attempt is due at {@code gameTime}. */
    public boolean due(long gameTime) {
        return gameTime >= next;
    }

    /** Game time of the next attempt. */
    public long next() {
        return next;
    }

    public void succeeded(long gameTime) {
        interval = MIN;
        next = gameTime + MIN;
    }

    public void failed(long gameTime) {
        interval = Math.min(MAX, interval * 2);
        next = gameTime + interval;
    }

    /** Tries again at the next batch (something changed). */
    public void reset() {
        interval = MIN;
        next = 0;
    }
}
