package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.core.Tier;

/**
 * Performance of a device (machines/01 §1): how fast, how much and how hungry it runs a recipe. The
 * purity bonus follows from the tier alone. Multipliers are kept as integers (per mille) so the
 * batch code does integer arithmetic only (machines/02 §6.3 rule 10).
 *
 * @param tier           tier T0–T4
 * @param speed          progress units per tick (T1 ×1, T2 ×2, T3 ×4, T4 ×4; hand tools count actions)
 * @param yieldPermille  main product multiplier of physical recipes, 1000 = ×1
 * @param energyPermille recipe energy multiplier, 1000 = ×1
 */
public record DeviceProfile(Tier tier, int speed, int yieldPermille, int energyPermille) {
    public DeviceProfile {
        if (speed <= 0 || yieldPermille < 1000 || energyPermille < 0) {
            throw new IllegalArgumentException("Invalid device profile " + speed + "/" + yieldPermille + "/" + energyPermille);
        }
    }

    /** Default values of a tier (machines/01 §1). */
    public static DeviceProfile standard(Tier tier) {
        return switch (tier) {
            case T0, T1 -> new DeviceProfile(tier, 1, 1000, 1000);
            case T2 -> new DeviceProfile(tier, 2, 1100, 1000);
            case T3 -> new DeviceProfile(tier, 4, 1200, 1500);
            case T4 -> new DeviceProfile(tier, 4, 1250, 2000);
        };
    }

    /** FE per tick while running {@code recipe}. */
    public long energyPerTick(ProcessRecipe recipe) {
        return (long) recipe.energy() * energyPermille / 1000L;
    }

    /** Ticks a run of {@code recipe} takes, in whole 4-tick batches. */
    public int ticks(ProcessRecipe recipe) {
        int batches = (recipe.batchTime() / 4 + speed - 1) / speed;
        return batches * 4;
    }
}
