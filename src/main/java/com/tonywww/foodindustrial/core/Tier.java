package com.tonywww.foodindustrial.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Equipment tier T0–T4 (machines/01 §1, additives/10 §2.3): hand tools, passive devices, world
 * interaction and the vanilla campfire, furnace and smoker are T0; workshop machines and the blast
 * furnace T1; then T2–T4. Written as {@code t0}–{@code t4}.
 */
public enum Tier {
    T0, T1, T2, T3, T4;

    public static final Codec<Tier> CODEC = Codec.STRING.comapFlatMap(
        id -> {
            Tier tier = byId(id);
            return tier != null ? DataResult.success(tier) : DataResult.error(() -> "Unknown tier " + id + " (t0–t4)");
        },
        Tier::id);

    /** Serialized name, {@code t0}–{@code t4}. */
    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    /** Tier by serialized name, or {@code null}. */
    @Nullable
    public static Tier byId(String id) {
        for (Tier tier : values()) {
            if (tier.id().equals(id.trim().toLowerCase(Locale.ROOT))) {
                return tier;
            }
        }
        return null;
    }

    public boolean atLeast(Tier other) {
        return ordinal() >= other.ordinal();
    }
}
