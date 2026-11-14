package com.tonywww.foodindustrial.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * One recipe of any general type (machines/01 §3, §3.1; machines/02 §3). Every type uses this class;
 * fields a type does not need stay empty.
 * <pre>
 * {
 *   "type": "foodindustrial:crystallizing",
 *   "inputs": [ { "ingredient": { "tag": "foodindustrial:chemicals/salt" }, "count": 4 },
 *               { "fluid": { "type": "foodindustrial:fluid", "tag": "foodindustrial:water", "amount": 1000 } } ],
 *   "purity_sources": [ 0 ],
 *   "outputs": [ { "item": "foodindustrial:salt", "count": 2, "purity": { "mode": "removal", "value": 50 } } ],
 *   "time": 200,
 *   "manual": { "actions": 4 }
 * }
 * </pre>
 */
public final class ProcessRecipe {
    private static final Codec<Integer> MANUAL = RecordCodecBuilder.<Integer>create(instance -> instance.group(
        Codec.intRange(1, 100000).fieldOf("actions").forGetter(actions -> actions)
    ).apply(instance, actions -> actions));

    /** A machine mode: a word of the {@code mode} condition category. */
    private static final Codec<String> MODE = Codec.STRING.comapFlatMap(mode -> {
        try {
            Capabilities.check("mode." + mode);
            return DataResult.success(mode);
        } catch (IllegalArgumentException exception) {
            return DataResult.error(exception::getMessage);
        }
    }, mode -> mode);

    private final ProcessType type;
    private final List<ProcessInput> inputs;
    private final List<ProcessOutput> outputs;
    private final Optional<List<Integer>> puritySources;
    private final Optional<List<Double>> purityRange;
    private final int time;
    private final int energy;
    private final Conditions conditions;
    private final Optional<String> mode;
    private final int manualActions;
    private final List<Catalyst> catalysts;
    private final List<VanillaDevice> vanillaDevices;
    private final Map<String, EnvironmentFactor> environmentFactors;
    private final Optional<Plate> plate;

    private final Capabilities required;
    private final int itemInputs;
    private final int fluidInputs;

    public ProcessRecipe(ProcessType type, List<ProcessInput> inputs, List<ProcessOutput> outputs, Optional<List<Integer>> puritySources,
                         Optional<List<Double>> purityRange, int time, int energy, Conditions conditions, Optional<String> mode,
                         int manualActions, List<VanillaDevice> vanillaDevices, List<Catalyst> catalysts) {
        this(type, inputs, outputs, puritySources, purityRange, time, energy, conditions, mode, manualActions, vanillaDevices, catalysts, Map.of());
    }

    public ProcessRecipe(ProcessType type, List<ProcessInput> inputs, List<ProcessOutput> outputs, Optional<List<Integer>> puritySources,
                         Optional<List<Double>> purityRange, int time, int energy, Conditions conditions, Optional<String> mode,
                         int manualActions, List<VanillaDevice> vanillaDevices, List<Catalyst> catalysts,
                         Map<String, EnvironmentFactor> environmentFactors) {
        this(type, inputs, outputs, puritySources, purityRange, time, energy, conditions, mode, manualActions, vanillaDevices, catalysts,
            environmentFactors, Optional.empty());
    }

    public ProcessRecipe(ProcessType type, List<ProcessInput> inputs, List<ProcessOutput> outputs, Optional<List<Integer>> puritySources,
                         Optional<List<Double>> purityRange, int time, int energy, Conditions conditions, Optional<String> mode,
                         int manualActions, List<VanillaDevice> vanillaDevices, List<Catalyst> catalysts,
                         Map<String, EnvironmentFactor> environmentFactors, Optional<Plate> plate) {
        this.type = type;
        this.inputs = List.copyOf(inputs);
        this.outputs = List.copyOf(outputs);
        this.puritySources = puritySources.map(List::copyOf);
        this.purityRange = purityRange.map(List::copyOf);
        this.time = time;
        this.energy = energy;
        this.conditions = conditions;
        this.mode = mode;
        this.manualActions = manualActions;
        this.vanillaDevices = List.copyOf(vanillaDevices);
        this.catalysts = List.copyOf(catalysts);
        this.environmentFactors = Map.copyOf(environmentFactors);
        this.plate = plate;
        this.required = mode.map(m -> conditions.required().with(Capabilities.of("mode." + m))).orElse(conditions.required());
        int items = 0;
        for (ProcessInput input : inputs) {
            if (input instanceof ProcessInput.ItemInput) {
                items++;
            }
        }
        this.itemInputs = items;
        this.fluidInputs = inputs.size() - items;
    }

    /** The business codec of recipes of {@code type} (the {@code type} field is handled by the recipe system). */
    public static MapCodec<ProcessRecipe> mapCodec(ProcessType type) {
        return RecordCodecBuilder.<ProcessRecipe>mapCodec(instance -> instance.group(
            ProcessInput.CODEC.listOf().fieldOf("inputs").forGetter(ProcessRecipe::inputs),
            ProcessOutput.CODEC.listOf().fieldOf("outputs").forGetter(ProcessRecipe::outputs),
            StrictCodecs.optional("purity_sources", Codec.INT.listOf()).forGetter(ProcessRecipe::puritySources),
            StrictCodecs.optional("purity_range", PurityRule.PERCENT.listOf()).forGetter(ProcessRecipe::purityRange),
            StrictCodecs.optional("time", Codec.INT, 0).forGetter(ProcessRecipe::time),
            StrictCodecs.optional("energy", Codec.INT, 0).forGetter(ProcessRecipe::energy),
            StrictCodecs.optional("requirements", Conditions.CODEC, Conditions.NONE).forGetter(ProcessRecipe::conditions),
            StrictCodecs.optional("mode", MODE).forGetter(ProcessRecipe::mode),
            StrictCodecs.optional("manual", MANUAL, 0).forGetter(ProcessRecipe::manualActions),
            StrictCodecs.optional("vanilla_devices", VanillaDevice.CODEC.listOf(), List.of()).forGetter(ProcessRecipe::vanillaDevices),
            StrictCodecs.optional("catalyst", Catalyst.ONE_OR_MORE, List.of()).forGetter(ProcessRecipe::catalysts),
            StrictCodecs.optional("environment_factors", EnvironmentFactor.MAP, Map.of()).forGetter(ProcessRecipe::environmentFactors),
            StrictCodecs.optional("plate", Plate.CODEC).forGetter(ProcessRecipe::plate)
        ).apply(instance, (inputs, outputs, sources, range, time, energy, conditions, mode, manual, vanilla, catalysts, factors, plate) ->
            new ProcessRecipe(type, inputs, outputs, sources, range, time, energy, conditions, mode, manual, vanilla, catalysts, factors, plate)))
            .flatXmap(ProcessRecipe::validate, DataResult::success);
    }

    /**
     * A recipe with its {@code type} field ({@code "type": "foodindustrial:kiln"}) as a data pack file
     * writes it, for formats that embed one (vanilla device bridges, machines/01 §5).
     */
    public static final Codec<ProcessRecipe> TYPED_CODEC = new MapCodec<ProcessRecipe>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString("type"));
        }

        @Override
        public <T> DataResult<ProcessRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
            T written = input.get("type");
            if (written == null) {
                return DataResult.error(() -> "Missing recipe type");
            }
            return Codec.STRING.parse(ops, written).flatMap(id -> {
                ProcessType type = id.startsWith("foodindustrial:") ? ProcessTypes.byId(id) : null;
                if (type == null || type.isWorldInteraction()) {
                    return DataResult.error(() -> "Unknown process recipe type " + id);
                }
                return mapCodec(type).decode(ops, input);
            });
        }

        @Override
        public <T> RecordBuilder<T> encode(ProcessRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            prefix.add("type", ops.createString("foodindustrial:" + input.type().id()));
            return mapCodec(input.type()).encode(input, ops, prefix);
        }
    }.codec();

    private static DataResult<ProcessRecipe> validate(ProcessRecipe recipe) {
        List<String> problems = recipe.structureProblems();
        if (!problems.isEmpty()) {
            return DataResult.error(() -> String.join("; ", problems));
        }
        return DataResult.success(recipe);
    }

    /** Errors that make the recipe unusable on any device; empty when it is well formed. */
    public List<String> structureProblems() {
        List<String> problems = new ArrayList<>();
        if (inputs.isEmpty()) {
            problems.add("needs at least one input");
        }
        if (outputs.isEmpty()) {
            problems.add("needs at least one output");
        } else if (outputs.get(0).isChanced()) {
            problems.add("the main product (first output) cannot have a chance");
        }
        puritySources.ifPresent(sources -> {
            Set<Integer> seen = new HashSet<>();
            for (int index : sources) {
                if (index < 0 || index >= inputs.size() || !seen.add(index)) {
                    problems.add("purity_sources has an invalid or repeated input index " + index);
                }
            }
        });
        purityRange.ifPresent(range -> {
            if (range.size() != 2 || range.get(0) > range.get(1)) {
                problems.add("purity_range must be [low, high]");
            } else {
                for (double value : range) {
                    try {
                        PurityScale.checkWritten(BigDecimal.valueOf(value));
                    } catch (IllegalArgumentException exception) {
                        problems.add("purity_range: " + exception.getMessage());
                    }
                }
            }
        });
        if (time < 0 || energy < 0) {
            problems.add("time and energy must not be negative");
        }
        if (time == 0 && manualActions == 0) {
            problems.add("needs \"time\" (devices) or \"manual\" (hand tools)");
        }
        for (VanillaDevice device : vanillaDevices) {
            if (!device.runs(type)) {
                problems.add("vanilla device " + device.id() + " does not run " + type.id() + " recipes");
            }
        }
        plate.ifPresent(written -> {
            if (type != ProcessTypes.CULTURING || mode.isEmpty()) {
                problems.add("a plate needs a culturing recipe with a mode (M30)");
            }
            if (!written.weights().isEmpty() && (written.byPurity() || written.weights().size() != outputs.size())) {
                problems.add("plate weights need one weight per output and no by_purity");
            }
            if (written.byPurity() && (outputs.size() < 2 || inputs.isEmpty())) {
                problems.add("a by_purity plate needs an input culture, the target and the wild colony outputs");
            }
            if (fluidInputs > 0 || outputs.stream().anyMatch(ProcessOutput::isFluid)) {
                problems.add("a plate takes and gives items only");
            }
        });
        return problems;
    }

    // ---- matching ----

    /**
     * Inputs assigned to distinct item slots and tanks.
     *
     * @param slots slot index per input ({@code -1} for fluid inputs)
     * @param tanks tank index per input ({@code -1} for item inputs)
     */
    public record Match(int[] slots, int[] tanks) {
    }

    /**
     * Assigns every input to its own slot or tank whose contents satisfy it (purity thresholds
     * included), or returns {@code null}. Call only when the inputs changed (machines/02 §6.3 rule 8).
     */
    @Nullable
    public Match match(List<ItemStack> items, List<FluidAmount> fluids) {
        return match(items, fluids, fluid -> false);
    }

    /**
     * The slot and tank each input takes, or {@code null}. Matching covers the contents fully (machines/01
     * §3.2, 2026-11-12): every non-empty slot and tank must be taken by an input, except tanks {@code spare}
     * accepts (the machine's own gas supply, such as the nitrogen of an inert reactor).
     */
    @Nullable
    public Match match(List<ItemStack> items, List<FluidAmount> fluids, java.util.function.Predicate<FluidAmount> spare) {
        return match(items, fluids, stack -> false, spare);
    }

    /**
     * As {@link #match(List, List, java.util.function.Predicate)}, with slots that need not be taken either (a T0
     * device whose products stay in its slots: they do not stop the next run).
     */
    @Nullable
    public Match match(List<ItemStack> items, List<FluidAmount> fluids, java.util.function.Predicate<ItemStack> spareItems,
                       java.util.function.Predicate<FluidAmount> spare) {
        int[] slots = new int[inputs.size()];
        int[] tanks = new int[inputs.size()];
        boolean[] usedSlots = new boolean[items.size()];
        boolean[] usedTanks = new boolean[fluids.size()];
        return assign(0, items, fluids, slots, tanks, usedSlots, usedTanks, spareItems, spare) ? new Match(slots, tanks) : null;
    }

    private boolean assign(int index, List<ItemStack> items, List<FluidAmount> fluids, int[] slots, int[] tanks,
                           boolean[] usedSlots, boolean[] usedTanks, java.util.function.Predicate<ItemStack> spareItems,
                           java.util.function.Predicate<FluidAmount> spare) {
        if (index == inputs.size()) {
            for (int slot = 0; slot < items.size(); slot++) {
                if (!usedSlots[slot] && !items.get(slot).isEmpty() && !spareItems.test(items.get(slot))) {
                    return false;
                }
            }
            for (int tank = 0; tank < fluids.size(); tank++) {
                if (!usedTanks[tank] && !fluids.get(tank).isEmpty() && !spare.test(fluids.get(tank))) {
                    return false;
                }
            }
            return true;
        }
        ProcessInput input = inputs.get(index);
        if (input instanceof ProcessInput.ItemInput item) {
            tanks[index] = -1;
            for (int slot = 0; slot < items.size(); slot++) {
                if (!usedSlots[slot] && item.test(items.get(slot))) {
                    usedSlots[slot] = true;
                    slots[index] = slot;
                    if (assign(index + 1, items, fluids, slots, tanks, usedSlots, usedTanks, spareItems, spare)) {
                        return true;
                    }
                    usedSlots[slot] = false;
                }
            }
            return false;
        }
        ProcessInput.FluidInput fluid = (ProcessInput.FluidInput) input;
        slots[index] = -1;
        for (int tank = 0; tank < fluids.size(); tank++) {
            if (!usedTanks[tank] && fluid.test(fluids.get(tank))) {
                usedTanks[tank] = true;
                tanks[index] = tank;
                if (assign(index + 1, items, fluids, slots, tanks, usedSlots, usedTanks, spareItems, spare)) {
                    return true;
                }
                usedTanks[tank] = false;
            }
        }
        return false;
    }

    // ---- purity ----

    /**
     * Whether input {@code index} takes part in the start purity (additives/10 §2.1 step 1): the
     * inputs named in {@code purity_sources}; without it, inputs of graded materials (K1–K11).
     * Reads the current purity data.
     */
    public boolean participates(int index) {
        if (puritySources.isPresent()) {
            return puritySources.get().contains(index);
        }
        PurityData.Material material = PurityData.current().material(inputs.get(index).material());
        return material != null && material.graded();
    }

    /** Whether any input takes part in the start purity. */
    public boolean hasParticipants() {
        for (int i = 0; i < inputs.size(); i++) {
            if (participates(i)) {
                return true;
            }
        }
        return false;
    }

    /** The rule of output {@code index}: as written, else the type's default (machines/01 §1.1). */
    public PurityRule ruleOf(int index) {
        PurityRule written = outputs.get(index).purity();
        return written != null ? written : type.defaultRule(conditions, hasParticipants());
    }

    /** {@code b} of the low end of {@code purity_range} (written values round up), or {@link PurityData#NONE}. */
    public int rangeLow() {
        return purityRange.map(range -> PurityScale.ofPercent(range.get(0))).orElse(PurityData.NONE);
    }

    /** {@code b} of the high end of {@code purity_range}, or {@link PurityData#NONE}. */
    public int rangeHigh() {
        return purityRange.map(range -> PurityScale.ofPercent(range.get(1))).orElse(PurityData.NONE);
    }

    // ---- accessors ----

    public ProcessType type() {
        return type;
    }

    public List<ProcessInput> inputs() {
        return inputs;
    }

    public List<ProcessOutput> outputs() {
        return outputs;
    }

    public Optional<List<Integer>> puritySources() {
        return puritySources;
    }

    public Optional<List<Double>> purityRange() {
        return purityRange;
    }

    /** Time in ticks as written; {@code 0} when only hand tools run the recipe. */
    public int time() {
        return time;
    }

    /** Time rounded up to whole 4-tick batches (machines/02 §6.1 rule 1). */
    public int batchTime() {
        return (time + 3) & ~3;
    }

    /** FE per tick at speed ×1. */
    public int energy() {
        return energy;
    }

    public Conditions conditions() {
        return conditions;
    }

    /** Machine mode the device must be in, if any. */
    public Optional<String> mode() {
        return mode;
    }

    /** Hand actions to complete the recipe; {@code 0} when it cannot be made by hand. */
    public int manualActions() {
        return manualActions;
    }

    /** Catalysts or cultures that must sit in the device's catalyst slots; worn by completions. */
    public List<Catalyst> catalysts() {
        return catalysts;
    }

    public List<VanillaDevice> vanillaDevices() {
        return vanillaDevices;
    }

    /** Capabilities a device needs: the conditions plus the mode. */
    public Capabilities required() {
        return required;
    }

    /** Effects of environment capabilities on time and yield (machines/01 §3.1). */
    public Map<String, EnvironmentFactor> environmentFactors() {
        return environmentFactors;
    }

    /** The culture plate of an M30 recipe (I17), if any. */
    public Optional<Plate> plate() {
        return plate;
    }

    /**
     * How specific the recipe is, for choosing among the recipes the same contents could run: the
     * conditions and catalysts first, then, between equals, the number of inputs (2026-11-11: a recipe
     * using more of the contents wins over one using a part of them); ties go by id.
     */
    public int specificity() {
        return (required.keys().size() + catalysts.size()) * 16 + inputs.size();
    }

    public int itemInputCount() {
        return itemInputs;
    }

    public int fluidInputCount() {
        return fluidInputs;
    }
}
