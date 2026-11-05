package com.tonywww.foodindustrial.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.tonywww.foodindustrial.core.Tier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Vanilla processing blocks that run R07–R09 recipes listing them in {@code vanilla_devices}
 * (machines/01 §5). The bridge itself comes with I13.
 */
public enum VanillaDevice {
    CAMPFIRE(Tier.T0, List.of(ProcessTypes.DRYING, ProcessTypes.KILN)),
    FURNACE(Tier.T0, List.of(ProcessTypes.DRYING, ProcessTypes.KILN)),
    SMOKER(Tier.T0, List.of(ProcessTypes.DRYING, ProcessTypes.FUMIGATING)),
    BLAST_FURNACE(Tier.T1, List.of(ProcessTypes.KILN));

    public static final Codec<VanillaDevice> CODEC = Codec.STRING.comapFlatMap(
        id -> {
            VanillaDevice device = byId(id);
            return device != null ? DataResult.success(device)
                : DataResult.error(() -> "Unknown vanilla device " + id + " (campfire, furnace, smoker, blast_furnace)");
        },
        VanillaDevice::id);

    private final Tier tier;
    private final List<ProcessType> types;

    VanillaDevice(Tier tier, List<ProcessType> types) {
        this.tier = tier;
        this.types = types;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Tier tier() {
        return tier;
    }

    /** Whether the block can run recipes of {@code type}. */
    public boolean runs(ProcessType type) {
        return types.contains(type);
    }

    @Nullable
    public static VanillaDevice byId(String id) {
        for (VanillaDevice device : values()) {
            if (device.id().equals(id)) {
                return device;
            }
        }
        return null;
    }
}
