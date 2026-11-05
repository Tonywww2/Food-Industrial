package com.tonywww.foodindustrial.machine.definition;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Effect of an upgrade item in an upgrade slot (U) (machines/01 §1 rule 4): speed (with more energy),
 * energy efficiency, parallel runs. Upgrade items and their values are content of I15; the framework
 * only adds the effects up.
 *
 * @param speedPermille  speed multiplier, 1000 = unchanged
 * @param energyPermille energy multiplier, 1000 = unchanged
 * @param parallel       extra parallel runs
 */
public record Upgrade(int speedPermille, int energyPermille, int parallel) {
    private static final Map<Supplier<? extends Item>, Upgrade> ITEMS = new ConcurrentHashMap<>();

    public static void register(Supplier<? extends Item> item, Upgrade upgrade) {
        ITEMS.put(item, upgrade);
    }

    /** Effect of an item in an upgrade slot, or {@code null}. */
    @Nullable
    public static Upgrade of(Item item) {
        for (Map.Entry<Supplier<? extends Item>, Upgrade> entry : ITEMS.entrySet()) {
            if (entry.getKey().get() == item) {
                return entry.getValue();
            }
        }
        return null;
    }
}
