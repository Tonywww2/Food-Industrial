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

    /**
     * Colour of the tier wherever it is named: tooltips, JEI and the stage names of the handbook
     * (docs/text-standard.md §3.4; 2026-11-11). Mid tones, readable on dark tooltips and light pages.
     */
    public int color() {
        return switch (this) {
            case T0 -> 0xA67C52;
            case T1 -> 0x5E9E3A;
            case T2 -> 0x3F7FC4;
            case T3 -> 0xD0703A;
            case T4 -> 0xA35FC4;
        };
    }

    public boolean atLeast(Tier other) {
        return ordinal() >= other.ordinal();
    }
}
