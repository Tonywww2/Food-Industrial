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
 * @param handHeld     hand tool used in the inventory (H01, H02): one input, a fluid only as one
 *                     bucket, a fluid product only back into that bucket (additives/07 §操作与实现规格)
 * @param configurations what the device offers at one time, one per machine mode; empty when it has no
 *                     modes (everything in {@code capabilities} at once)
 */
public record DeviceSpec(String id, DeviceProfile profile, Set<ProcessType> types, Capabilities capabilities, Slots slots,
                         boolean manual, boolean timed, @Nullable VanillaDevice vanilla, boolean handHeld,
                         List<Configuration> configurations) {
    /** Fluid in one bucket, the only fluid amount hand tools handle. */
    public static final int BUCKET = 1000;

    private static final Map<String, DeviceSpec> REGISTRY = new LinkedHashMap<>();

    /** Item input (I), item output (O), fluid input (FI), fluid output (FO) and catalyst (C) slot counts. */
    public record Slots(int itemIn, int itemOut, int fluidIn, int fluidOut, int catalyst) {
        public Slots(int itemIn, int itemOut, int fluidIn, int fluidOut) {
            this(itemIn, itemOut, fluidIn, fluidOut, 0);
        }
    }

    /**
     * What a machine offers in one mode (machines/01 §3.2): the capabilities it has then, modules included,
     * and the recipe types the mode allows (empty = all).
     */
    public record Configuration(Capabilities capabilities, Set<ProcessType> types) {
        public Configuration {
            types = Set.copyOf(types);
        }

        boolean runs(ProcessRecipe recipe) {
            return (types.isEmpty() || types.contains(recipe.type())) && capabilities.covers(recipe.required());
        }
    }

    public DeviceSpec {
        types = Set.copyOf(types);
        configurations = List.copyOf(configurations);
    }

    public DeviceSpec(String id, DeviceProfile profile, Set<ProcessType> types, Capabilities capabilities, Slots slots,
                      boolean manual, boolean timed, @Nullable VanillaDevice vanilla, boolean handHeld) {
        this(id, profile, types, capabilities, slots, manual, timed, vanilla, handHeld, List.of());
    }

    public DeviceSpec(String id, DeviceProfile profile, Set<ProcessType> types, Capabilities capabilities, Slots slots,
                      boolean manual, boolean timed, @Nullable VanillaDevice vanilla) {
        this(id, profile, types, capabilities, slots, manual, timed, vanilla, false);
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

    /**
     * Whether the device can run both recipes at one time, in one mode (machines/01 §3.2): a machine in
     * another mode offers other capabilities, so recipes told apart by a mode's condition do not meet.
     */
    public boolean runsTogether(ProcessRecipe a, ProcessRecipe b) {
        if (!canRun(a) || !canRun(b)) {
            return false;
        }
        return configurations.isEmpty() || configurations.stream().anyMatch(configuration -> configuration.runs(a) && configuration.runs(b));
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
        if (handHeld) {
            String problem = handHeldProblem(recipe);
            if (problem != null) {
                return problem;
            }
        }
        if (vanilla != null) {
            String problem = vanillaProblem(recipe);
            if (problem != null) {
                return problem;
            }
        }
        if (recipe.plate().isPresent()) {
            // a culture plate gives the one colony the player picks (M30)
            itemOut = Math.min(itemOut, 1);
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

    @Nullable
    private static String handHeldProblem(ProcessRecipe recipe) {
        if (recipe.inputs().size() != 1) {
            return "hand tools take one input";
        }
        boolean fluidIn = recipe.inputs().get(0) instanceof ProcessInput.FluidInput;
        if (fluidIn && ((ProcessInput.FluidInput) recipe.inputs().get(0)).amount() != BUCKET) {
            return "hand tools take fluids by the bucket (1000 mB)";
        }
        int fluidOut = 0;
        for (ProcessOutput output : recipe.outputs()) {
            if (output.isFluid()) {
                fluidOut++;
                if (output.amount() != BUCKET) {
                    return "hand tools return fluids by the bucket (1000 mB)";
                }
            }
        }
        if (fluidOut > (fluidIn ? 1 : 0)) {
            return "hand tools put a fluid product only back into the input bucket";
        }
        return null;
    }

    /**
     * Vanilla blocks (machines/01 §5): one item in (count 1) to one item out, no fluid, no chance, no
     * purity range.
     */
    @Nullable
    private static String vanillaProblem(ProcessRecipe recipe) {
        if (recipe.inputs().size() != 1 || !(recipe.inputs().get(0) instanceof ProcessInput.ItemInput item) || item.count() != 1) {
            return "vanilla blocks take one item";
        }
        if (recipe.outputs().size() != 1 || recipe.outputs().get(0).isFluid() || recipe.outputs().get(0).isChanced()) {
            return "vanilla blocks make one item, no by-products";
        }
        if (recipe.purityRange().isPresent()) {
            return "vanilla blocks give one result per input (no purity_range)";
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
