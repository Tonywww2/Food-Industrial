package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.core.Tier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The vanilla blocks of machines/01 §5: time factors, tiers and recipe types. */
class VanillaDeviceTest {
    private static ProcessRecipe timed(ProcessType type, int time) {
        return new ProcessRecipe(type, List.of(), List.of(), Optional.empty(), Optional.empty(), time, 0, Conditions.NONE,
            Optional.empty(), 0, List.of(), List.of());
    }

    @Test
    void cookingTimes() {
        ProcessRecipe kiln = timed(ProcessTypes.KILN, 200);
        assertEquals(600, VanillaDevice.CAMPFIRE.cookingTime(kiln));
        assertEquals(200, VanillaDevice.FURNACE.cookingTime(kiln));
        assertEquals(100, VanillaDevice.SMOKER.cookingTime(timed(ProcessTypes.DRYING, 200)));
        assertEquals(100, VanillaDevice.BLAST_FURNACE.cookingTime(kiln));
        assertEquals(1, VanillaDevice.SMOKER.cookingTime(timed(ProcessTypes.DRYING, 1)), "at least one tick");
    }

    @Test
    void tiersAndTypes() {
        assertEquals(Tier.T0, VanillaDevice.FURNACE.tier());
        assertEquals(Tier.T1, VanillaDevice.BLAST_FURNACE.tier());
        assertTrue(VanillaDevice.CAMPFIRE.runs(ProcessTypes.KILN));
        assertTrue(VanillaDevice.SMOKER.runs(ProcessTypes.FUMIGATING));
        assertFalse(VanillaDevice.SMOKER.runs(ProcessTypes.KILN));
        assertFalse(VanillaDevice.BLAST_FURNACE.runs(ProcessTypes.DRYING));
    }
}
