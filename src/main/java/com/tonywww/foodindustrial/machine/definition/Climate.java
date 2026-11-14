package com.tonywww.foodindustrial.machine.definition;

/**
 * Climate of a fermentation room (M06 曲房, I14, additives/06 §T1 作坊): burning fuel makes it warm,
 * water in the input tank makes it humid; while it runs, {@code water} mB are used every
 * {@code interval} ticks (2026-11-10: 10 mB every 40 ticks). Warm and humid together put the purity of
 * a {@code purity_range} recipe in the upper half of its range. The climate never stops the machine.
 *
 * @param water    water used each interval while humid, mB
 * @param interval running ticks between two uses of water
 */
public record Climate(int water, int interval) {
    public Climate {
        if (water <= 0 || interval <= 0) {
            throw new IllegalArgumentException("Climate water use and interval must be positive");
        }
    }
}
