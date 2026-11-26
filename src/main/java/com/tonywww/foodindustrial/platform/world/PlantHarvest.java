package com.tonywww.foodindustrial.platform.world;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * What picking a plant by hand or with shears gives (I44): bushes, wild herbs and fruit leaves ask it on every
 * right click. The business code (world.source) decides by species, age (0–3) and the item in hand.
 */
public interface PlantHarvest {
    /**
     * The products a right click with {@code held} picks at {@code age} and the age the plant drops back to, or
     * {@code null} when there is nothing to pick (the click then does what it would otherwise do).
     */
    @Nullable
    Picked pick(int age, ItemStack held, RandomSource random);

    /** The item the pick-block key gives. */
    ItemStack pickBlock();

    /** Products and the age after picking. {@code shears}: the click used shears (they wear by 1). */
    record Picked(List<ItemStack> items, int age, boolean shears) {
    }
}
