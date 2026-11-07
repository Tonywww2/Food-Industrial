package com.tonywww.foodindustrial.machine.definition;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Effect of an upgrade item in an upgrade slot (U) (machines/01 §1 rule 4): speed (with more energy),
 * energy efficiency, parallel runs, standardisation (machines/01 §1.2). Upgrade items and their values
 * are content of I15; the framework only adds the effects up.
 *
 * @param speedPermille  speed multiplier, 1000 = unchanged
 * @param energyPermille energy multiplier, 1000 = unchanged
 * @param parallel       extra parallel runs
 * @param normalizing    standardisation module: products drop to their grade's lower bound; several do
 *                       not stack
 */
public record Upgrade(int speedPermille, int energyPermille, int parallel, boolean normalizing) {
    public Upgrade(int speedPermille, int energyPermille, int parallel) {
        this(speedPermille, energyPermille, parallel, false);
    }

    /** The standardisation module (machines/01 §1.2): changes purity only. */
    public static Upgrade standardisation() {
        return new Upgrade(1000, 1000, 0, true);
    }

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
