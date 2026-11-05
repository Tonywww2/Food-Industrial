package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.core.Tier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * What a device can run (machines/01 §3, machines/03 §3): its tier and profile, recipe types,
 * capabilities (every value it can offer, modes and modules included), slot counts and how it makes
 * progress. Machines, hand tools, passive devices and the vanilla blocks register one each; the
 * registry gives every recipe its base tier and the devices JEI lists.
 *
 * @param id           device id ({@code foodindustrial:stone_mill})
 * @param profile      tier, speed, yield and energy factor
 * @param types        recipe types the device runs
 * @param capabilities every capability the device can offer
 * @param slots        item and fluid slot counts by role
 * @param manual       hand tool: runs only recipes with {@code manual}
 * @param timed        runs on time: only recipes with {@code time}
 * @param vanilla      vanilla block: runs only recipes listing it in {@code vanilla_devices}
 */
public record DeviceSpec(String id, DeviceProfile profile, Set<ProcessType> types, Capabilities capabilities, Slots slots,
                         boolean manual, boolean timed, @Nullable VanillaDevice vanilla) {
    private static final Map<String, DeviceSpec> REGISTRY = new LinkedHashMap<>();

    /** Item input (I), item output (O), fluid input (FI), fluid output (FO) and catalyst (C) slot counts. */
    public record Slots(int itemIn, int itemOut, int fluidIn, int fluidOut, int catalyst) {
        public Slots(int itemIn, int itemOut, int fluidIn, int fluidOut) {
            this(itemIn, itemOut, fluidIn, fluidOut, 0);
        }
    }

    public DeviceSpec {
        types = Set.copyOf(types);
    }

    public Tier tier() {
        return profile.tier();
    }

    /** Registers a device; ids are unique. */
    public static synchronized DeviceSpec register(DeviceSpec spec) {
        if (REGISTRY.putIfAbsent(spec.id, spec) != null) {
            throw new IllegalStateException("Duplicate device " + spec.id);
        }
        RecipeIndex.invalidate();
        return spec;
    }

    /** All registered devices in registration order. */
    public static synchronized List<DeviceSpec> all() {
        return Collections.unmodifiableList(new ArrayList<>(REGISTRY.values()));
    }

    @Nullable
    public static synchronized DeviceSpec byId(String id) {
        return REGISTRY.get(id);
    }

    /** Whether the device can run {@code recipe}; {@link #whyNot} names the first reason. */
    public boolean canRun(ProcessRecipe recipe) {
        return whyNot(recipe) == null;
    }

    /** First reason the device cannot run {@code recipe}, or {@code null} when it can. */
    @Nullable
    public String whyNot(ProcessRecipe recipe) {
        if (!types.contains(recipe.type())) {
            return "type " + recipe.type().id();
        }
        if (vanilla != null && !recipe.vanillaDevices().contains(vanilla)) {
            return "not listed in vanilla_devices";
        }
        if (manual && recipe.manualActions() == 0) {
            return "no manual section";
        }
        if (timed && recipe.time() == 0) {
            return "no time";
        }
        Tier minTier = recipe.conditions().minTier();
        if (minTier != null && !tier().atLeast(minTier)) {
            return "min_tier " + minTier.id();
        }
        if (!capabilities.covers(recipe.required())) {
            return "missing " + capabilities.missing(recipe.required());
        }
        int itemOut = 0;
        int fluidOut = 0;
        for (ProcessOutput output : recipe.outputs()) {
            if (output.producedAt(tier())) {
                if (output.isFluid()) {
                    fluidOut++;
                } else {
                    itemOut++;
                }
            }
        }
        if (recipe.catalysts().size() > slots.catalyst) {
            return "catalyst slots " + recipe.catalysts().size() + " > " + slots.catalyst;
        }
        if (recipe.itemInputCount() > slots.itemIn || recipe.fluidInputCount() > slots.fluidIn
            || itemOut > slots.itemOut || fluidOut > slots.fluidOut) {
            return "slots " + recipe.itemInputCount() + "/" + itemOut + "/" + recipe.fluidInputCount() + "/" + fluidOut
                + " > " + slots.itemIn + "/" + slots.itemOut + "/" + slots.fluidIn + "/" + slots.fluidOut;
        }
        return null;
    }

    /** Devices that can run {@code recipe}, lowest tier first. */
    public static List<DeviceSpec> runnersOf(ProcessRecipe recipe) {
        List<DeviceSpec> result = new ArrayList<>();
        for (DeviceSpec spec : all()) {
            if (spec.canRun(recipe)) {
                result.add(spec);
            }
        }
        result.sort((a, b) -> Integer.compare(a.tier().ordinal(), b.tier().ordinal()));
        return result;
    }
}
