package com.tonywww.foodindustrial.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Vanilla processing blocks that run R07–R09 recipes listing them in {@code vanilla_devices}
 * (machines/01 §5): tier, recipe types, capabilities and the factor on the recipe time. A device of
 * high temperature offers the heated grade too. The recipes reach the blocks through bridge recipes
 * of the vanilla cooking types ({@link VanillaCookingBridge}).
 */
public enum VanillaDevice {
    CAMPFIRE(Tier.T0, List.of(ProcessTypes.DRYING, ProcessTypes.KILN), Capabilities.of("temperature.heated"), 3.0),
    FURNACE(Tier.T0, List.of(ProcessTypes.DRYING, ProcessTypes.KILN), Capabilities.of("temperature.heated", "temperature.high"), 1.0),
    SMOKER(Tier.T0, List.of(ProcessTypes.DRYING, ProcessTypes.FUMIGATING), Capabilities.of("temperature.heated"), 0.5),
    BLAST_FURNACE(Tier.T1, List.of(ProcessTypes.KILN), Capabilities.of("temperature.heated", "temperature.high"), 0.5);

    public static final Codec<VanillaDevice> CODEC = Codec.STRING.comapFlatMap(
        id -> {
            VanillaDevice device = byId(id);
            return device != null ? DataResult.success(device)
                : DataResult.error(() -> "Unknown vanilla device " + id + " (campfire, furnace, smoker, blast_furnace)");
        },
        VanillaDevice::id);

    private final Tier tier;
    private final List<ProcessType> types;
    private final Capabilities capabilities;
    private final double timeFactor;
    private DeviceSpec spec;

    VanillaDevice(Tier tier, List<ProcessType> types, Capabilities capabilities, double timeFactor) {
        this.tier = tier;
        this.types = types;
        this.capabilities = capabilities;
        this.timeFactor = timeFactor;
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

    /** Cooking time of a recipe on this block: the recipe time times the block's factor, at least 1 tick. */
    public int cookingTime(ProcessRecipe recipe) {
        return Math.max(1, (int) Math.round(recipe.time() * timeFactor));
    }

    /** The registered device of this block. */
    public DeviceSpec spec() {
        return spec;
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

    /** Registers the four devices: one item in, one item out, on time. Called once during mod construction. */
    static void registerSpecs() {
        for (VanillaDevice device : values()) {
            device.spec = DeviceSpec.register(new DeviceSpec(FoodIndustrial.MOD_ID + ":vanilla/" + device.id(), DeviceProfile.standard(device.tier),
                Set.copyOf(device.types), device.capabilities, new DeviceSpec.Slots(1, 1, 0, 0), false, true, device));
        }
    }
}
