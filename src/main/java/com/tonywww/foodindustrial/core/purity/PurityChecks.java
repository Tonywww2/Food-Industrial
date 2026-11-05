package com.tonywww.foodindustrial.core.purity;

import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.world.item.ItemStack;

/**
 * Data generation checks (additives/10 §1): every output path of this mod — machine and crafting
 * results, loot, outputs registered into other mods' recipes — writes a purity on bearer items, so
 * the same purity never exists both with and without the stored value. Generators call these while
 * building output JSON; a failure aborts the data run.
 */
public final class PurityChecks {
    private PurityChecks() {
    }

    /**
     * Throws when {@code output} is a purity bearer without stored purity.
     *
     * @param where recipe or loot table id, for the error message
     */
    public static void requireOnOutput(ItemStack output, String where) {
        if (!output.isEmpty() && Purity.bears(output.getItem()) && Purity.stored(output).isEmpty()) {
            throw new IllegalStateException("Output " + PlatformTags.itemId(output.getItem()) + " of " + where
                + " carries no purity; every output path must write one (additives/10 §1)");
        }
    }
}
