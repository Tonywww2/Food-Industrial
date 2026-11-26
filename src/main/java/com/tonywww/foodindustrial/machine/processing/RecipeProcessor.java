package com.tonywww.foodindustrial.machine.processing;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.food.FoodLine;
import com.tonywww.foodindustrial.food.quality.FoodQuality;
import com.tonywww.foodindustrial.food.quality.QualityRules;
import com.tonywww.foodindustrial.machine.component.MachineInventory;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.component.TankSet;
import com.tonywww.foodindustrial.machine.definition.GasSupply;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.Catalyst;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.OutcomeResolver;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Processing state machine of a recipe-driven machine (machines/02 §5). Each batch:
 * <ol>
 *   <li>redstone forbids → sleep;</li>
 *   <li>without a recipe, look one up only if the inputs changed (index by first input); none → sleep;</li>
 *   <li>products of this run (largest amount) do not fit → sleep until outputs are taken; with parallel
 *       upgrades the run makes as many copies as the inputs allow and the products fit (I15);</li>
 *   <li>take the batch's energy ({@code FE/t × 4}), keep the fire lit or take the hand crank's
 *       stored power; missing → sleep;</li>
 *   <li>advance progress by {@code 4 × speed}; at the recipe time consume inputs, settle purity and
 *       amounts, store products, wear catalysts and keep the remainder of the progress.</li>
 * </ol>
 * Progress is counted in thousandths of a tick so speed multipliers stay integer. When the running
 * recipe only lacks a condition for now (night over a sun mode, a module taken out, another mode) and no
 * other recipe can take over, it pauses with its progress (I14, additives/06 §T1 作坊).
 */
public final class RecipeProcessor {
    @Nullable
    private RecipeIndex.Entry entry;
    @Nullable
    private ProcessRecipe.Match match;
    private int[] catalystSlots = new int[0];
    private long progress;
    private boolean inputsDirty = true;
    private boolean outputsDirty = true;
    private boolean fits;
    private boolean fluidsFit = true;
    @Nullable
    private String savedRecipe;
    @Nullable
    private String missing;
    /** The current recipe lacks a condition for now: keep it and its progress, do not run. */
    private boolean paused;
    private ItemStack[] plannedItems = new ItemStack[0];
    private FluidAmount[] plannedFluids = new FluidAmount[0];
    /** Index of the main product in {@link #plannedItems}, or -1 when it is a fluid or not produced. */
    private int plannedMain = -1;
    /** Copies the inputs allow at most (parallel upgrades, machines/01 §1 rule 4). */
    private int maxRuns = 1;
    /** Copies the next completion makes: the most the inputs allow whose products fit. */
    private int runs = 1;
    /** Setup ticks of the machine in front of every run (M29 sterilization), taken at planning. */
    private int setup;
    /** Food quality of the planned products (I38), {@code 0} for none. */
    private int plannedQuality;
    /** Items one run takes from each additive slot (the ingredient step, foods/05 §3). */
    private int[] additiveTake = new int[0];

    public void inputsChanged() {
        inputsDirty = true;
    }

    public void outputsChanged() {
        outputsDirty = true;
    }

    @Nullable
    public RecipeIndex.Entry current() {
        return entry;
    }

    /** Progress in ticks (at speed ×1). */
    public int progressTicks() {
        return (int) (progress / 1000);
    }

    /** Whether the planned fluid products do not fit the output tanks (venting “overflow”). */
    public boolean fluidsBlocked() {
        return entry != null && !fluidsFit;
    }

    /** Ticks of one run of the current recipe at speed ×1 (with the machine's setup ticks), or 0 without a recipe. */
    public int totalTicks() {
        return entry == null ? 0 : entry.recipe().batchTime() + setup;
    }

    /** Main product of the next completion (base amount, planned purity), or empty. */
    public ItemStack plannedItem() {
        return plannedMain >= 0 && plannedMain < plannedItems.length ? plannedItems[plannedMain] : ItemStack.EMPTY;
    }

    /** Main product when it is a fluid, or empty. */
    public FluidAmount plannedFluid() {
        if (entry == null || plannedMain >= 0 || plannedFluids.length == 0) {
            return FluidAmount.EMPTY;
        }
        ProcessOutput first = entry.recipe().outputs().get(0);
        return first.isFluid() ? plannedFluids[0] : FluidAmount.EMPTY;
    }

    /**
     * Uses of the recipe's catalyst in catalyst slot {@code index} before one item is used up
     * (machines/04 §1), or 0 when the current recipe does not use that slot.
     */
    public int catalystRuns(int index) {
        if (entry == null) {
            return 0;
        }
        for (int i = 0; i < catalystSlots.length && i < entry.recipe().catalysts().size(); i++) {
            if (catalystSlots[i] == index) {
                return entry.recipe().catalysts().get(i).runs();
            }
        }
        return 0;
    }

    /** The condition missing right now (vocabulary keys), for the status line; {@code null} otherwise. */
    @Nullable
    public String missing() {
        return missing;
    }

    public MachineStatus tick(MachineBlockEntity machine, int delta) {
        if (!machine.redstone().allows(machine.isPowered())) {
            return MachineStatus.REDSTONE;
        }
        if (inputsDirty) {
            inputsDirty = false;
            rematch(machine);
        }
        if (entry == null) {
            progress = 0;
            return missing != null ? MachineStatus.MISSING : MachineStatus.WAITING;
        }
        if (paused) {
            return MachineStatus.MISSING;
        }
        if (outputsDirty) {
            outputsDirty = false;
            fluidsFit = machine.tanks().outputsFit(plannedFluids, plannedFluids.length);
            runs = 0;
            for (int n = maxRuns; n >= 1 && runs == 0; n--) {
                FluidAmount[] fluids = scaled(plannedFluids, n);
                ItemStack[] items = scaled(plannedItems, n);
                if ((n == 1 ? fluidsFit : machine.tanks().outputsFit(fluids, fluids.length))
                    && machine.inventory().outputsFit(items, items.length, plannedMain)) {
                    runs = n;
                }
            }
            fits = runs > 0;
            runs = Math.max(1, runs);
        }
        if (!fits) {
            return MachineStatus.OUTPUT_FULL;
        }
        ProcessRecipe recipe = entry.recipe();
        DeviceProfile profile = machine.definition().profile();
        PowerKind power = machine.definition().power();
        int run = delta;
        int drive = 0;
        if (power == PowerKind.ENERGY) {
            long need = profile.energyPerTick(recipe) * machine.upgrades().energyPermille() / 1000L * delta * runs;
            if (!machine.energy().consume(need)) {
                return MachineStatus.NO_POWER;
            }
        } else if (power == PowerKind.FUEL && !machine.fireForRecipe()) {
            return MachineStatus.NO_POWER;
        } else if (power == PowerKind.MANUAL) {
            // A turning Create transmission interface drives the machine instead of the hand crank (I25).
            drive = machine.drivePermille();
            if (drive == 0) {
                run = machine.takeManualPower(delta);
                if (run == 0) {
                    return MachineStatus.NO_POWER;
                }
            }
        }
        boolean climate = false;
        if (machine.definition().climate() != null) {
            climate = machine.climateBatch(run);
        } else if (power == PowerKind.NONE && machine.heat() != null) {
            machine.fireForRecipe();
        }
        GasSupply gas = machine.definition().gas();
        if (gas != null && recipe.required().keys().contains(gas.capability())) {
            machine.gasBatch(run);
        }
        long gain = (long) run * profile.speed() * machine.speedPermille();
        progress += drive > 0 ? gain * drive / 1000L : gain;
        long goal = (recipe.batchTime() + setup) * 1000L;
        if (progress >= goal) {
            progress -= goal;
            complete(machine, climate);
        }
        machine.markUnsaved();
        return MachineStatus.RUNNING;
    }

    // ---- matching ----

    private void rematch(MachineBlockEntity machine) {
        missing = null;
        paused = false;
        List<ItemStack> items = inputs(machine.inventory());
        List<FluidAmount> fluids = inputs(machine.tanks());
        RecipeIndex index = RecipeIndex.get(machine.getLevel());
        if (entry == null && savedRecipe != null) {
            entry = index.byId(savedRecipe);
            savedRecipe = null;
        }
        RecipeIndex.Entry previous = entry;
        // find() keeps the entry, match and catalyst slots of the recipe it takes; the progress carries
        // on only when it is the same recipe
        RecipeIndex.Entry found = find(machine, index, items, fluids, machine.capabilities(), true);
        if (found != null) {
            if (found != previous) {
                progress = 0;
            }
            plan(machine);
            return;
        }
        Capabilities possible = machine.definition().spec().capabilities();
        if (previous != null && accept(machine, previous, items, fluids, possible, false)) {
            missing = missing(machine, previous.recipe());
            paused = true;
            plan(machine);
            return;
        }
        RecipeIndex.Entry blocked = find(machine, index, items, fluids, possible, false);
        if (blocked != null) {
            missing = missing(machine, blocked.recipe());
        }
        entry = null;
        match = null;
        progress = 0;
        plannedMain = -1;
        plannedItems = new ItemStack[0];
        plannedFluids = new FluidAmount[0];
    }

    /**
     * The recipe the machine runs on these contents: of the recipes it can run, the one with the most
     * conditions (catalysts count), ties by id — the rule of the T0 devices (additives/07, applied to
     * machines in I14 so that a heated recrystallization wins over plain brine on the boiling stove).
     * The winner is accepted (its match and catalyst slots kept); {@code null} when none can run.
     */
    /**
     * What keeps the machine from running {@code recipe}: the missing capabilities (vocabulary keys), or
     * {@code @} and the translation key of what the machine lacks for it (M22 trays).
     */
    private static String missing(MachineBlockEntity machine, ProcessRecipe recipe) {
        String keys = String.join(",", machine.capabilities().missing(recipe.required()));
        String lacking = lacking(machine, recipe);
        if (lacking == null) {
            return keys;
        }
        return keys.isEmpty() ? "@" + lacking : keys + ",@" + lacking;
    }

    /**
     * @param strict also require what the machine itself checks ({@link MachineBlockEntity#recipeLacking});
     *               off to find the recipe a message is about
     */
    @Nullable
    private RecipeIndex.Entry find(MachineBlockEntity machine, RecipeIndex index, List<ItemStack> items, List<FluidAmount> fluids, Capabilities capabilities,
                                   boolean strict) {
        DeviceSpec spec = machine.definition().spec();
        RecipeIndex.Entry best = null;
        for (ProcessType type : ProcessTypes.recipeTypes()) {
            if (!spec.types().contains(type) || !machine.allows(type)) {
                continue;
            }
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    best = better(best, machine, index.candidates(type, stack.getItem()), items, fluids, capabilities, strict);
                }
            }
            for (FluidAmount fluid : fluids) {
                if (!fluid.isEmpty()) {
                    best = better(best, machine, index.candidates(type, fluid.fluid()), items, fluids, capabilities, strict);
                }
            }
        }
        if (best != null) {
            accept(machine, best, items, fluids, capabilities, strict);
        }
        return best;
    }

    /** {@code best} or a runnable entry of {@code entries} that beats it. */
    @Nullable
    private RecipeIndex.Entry better(@Nullable RecipeIndex.Entry best, MachineBlockEntity machine, List<RecipeIndex.Entry> entries,
                                     List<ItemStack> items, List<FluidAmount> fluids, Capabilities capabilities, boolean strict) {
        for (RecipeIndex.Entry candidate : entries) {
            if (best != null) {
                int order = Integer.compare(candidate.recipe().specificity(), best.recipe().specificity());
                if (order < 0 || (order == 0 && candidate.id().compareTo(best.id()) >= 0)) {
                    continue;
                }
            }
            if (runnable(machine, candidate, items, fluids, capabilities, strict)) {
                best = candidate;
            }
        }
        return best;
    }

    /** Whether the machine can run {@code candidate} now, without keeping anything. */
    private boolean runnable(MachineBlockEntity machine, RecipeIndex.Entry candidate, List<ItemStack> items, List<FluidAmount> fluids, Capabilities capabilities,
                             boolean strict) {
        ProcessRecipe recipe = candidate.recipe();
        return takes(machine, candidate, capabilities, strict) && recipe.match(items, fluids, spare(machine)) != null
            && catalystSlots(machine.inventory(), recipe.catalysts()) != null;
    }

    private static boolean takes(MachineBlockEntity machine, RecipeIndex.Entry candidate, Capabilities capabilities, boolean strict) {
        ProcessRecipe recipe = candidate.recipe();
        return candidate.runsOn(machine.definition().spec()) && machine.allows(recipe.type()) && capabilities.covers(recipe.required())
            && machine.offers(recipe) && (!strict || lacking(machine, recipe) == null);
    }

    /** What the machine lacks for {@code recipe}: its own checks, then the additives of an ingredient step. */
    @Nullable
    private static String lacking(MachineBlockEntity machine, ProcessRecipe recipe) {
        String own = machine.recipeLacking(recipe);
        if (own != null || recipe.quality().isEmpty()) {
            return own;
        }
        List<ItemStack> additives = additives(machine.inventory());
        if (additives.stream().allMatch(ItemStack::isEmpty)) {
            return null;
        }
        QualityRules.Outcome outcome = QualityRules.ingredient(recipe.quality().get(), recipe.outputs().get(0).id(), additives, List.of());
        return outcome.refused() ? "gui.foodindustrial.need." + outcome.refusal() : null;
    }

    private static List<ItemStack> additives(MachineInventory inventory) {
        int count = inventory.count(SlotRole.ADDITIVE);
        List<ItemStack> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(inventory.get(SlotRole.ADDITIVE, i));
        }
        return list;
    }

    /**
     * Quality of the products of the next run (I38, foods/05 §2): an ingredient step fixes it from the additives and
     * the inputs and notes in {@link #additiveTake} what it takes; any other step carries the inputs' quality over.
     */
    private int quality(MachineBlockEntity machine, ProcessRecipe recipe) {
        additiveTake = new int[0];
        if (recipe.quality().isPresent()) {
            List<QualityRules.Input> inputs = new ArrayList<>(recipe.inputs().size());
            for (int i = 0; i < recipe.inputs().size(); i++) {
                ProcessInput input = recipe.inputs().get(i);
                if (input instanceof ProcessInput.ItemInput item) {
                    ItemStack stack = machine.inventory().get(SlotRole.INPUT, match.slots()[i]);
                    String material = QualityRules.material(stack);
                    boolean asSalt = material != null && QualityRules.NITRITES.contains(material) && FoodLine.isSaltPosition(item);
                    inputs.add(new QualityRules.Input(Purity.grade(stack).orElse(null), FoodLine.quality(stack), material, asSalt));
                } else {
                    FluidAmount fluid = machine.tanks().get(match.tanks()[i]);
                    inputs.add(new QualityRules.Input(Purity.grade(fluid).orElse(null), fluid.quality(),
                        QualityRules.plain(Purity.materialOf(fluid.fluid())), false));
                }
            }
            QualityRules.Outcome outcome = QualityRules.ingredient(recipe.quality().get(), recipe.outputs().get(0).id(),
                additives(machine.inventory()), inputs);
            if (outcome.refused()) {
                return 0;
            }
            additiveTake = outcome.take();
            return outcome.code();
        }
        int code = 0;
        for (int i = 0; i < recipe.inputs().size(); i++) {
            int value;
            if (recipe.inputs().get(i) instanceof ProcessInput.ItemInput item) {
                ItemStack stack = machine.inventory().get(SlotRole.INPUT, match.slots()[i]);
                value = FoodLine.quality(stack);
                // nitrite in any product recipe's salt position is noted, ingredient step or not (foods/02 §4.4, I47)
                if (FoodLine.isSaltPosition(item) && QualityRules.isNitrite(QualityRules.material(stack))) {
                    value |= FoodQuality.NITRITE_MISUSE;
                }
            } else {
                value = machine.tanks().get(match.tanks()[i]).quality();
            }
            code = FoodQuality.merge(code, value);
        }
        return code;
    }

    /** The product with the run's quality: the main product always, others when they carry quality (I38). */
    private static ItemStack stamped(ItemStack stack, int code, boolean main) {
        return code != 0 && (main || FoodLine.carries(stack.getItem())) ? FoodLine.withQuality(stack, code) : stack;
    }

    private static FluidAmount stamped(FluidAmount fluid, int code, boolean main) {
        return code != 0 && (main || FoodLine.carries(fluid.fluid())) ? fluid.withQuality(code) : fluid;
    }

    /** Tanks a recipe need not take: the machine's own gas supply (the nitrogen of the inert stirred reactor). */
    private static java.util.function.Predicate<FluidAmount> spare(MachineBlockEntity machine) {
        GasSupply gas = machine.definition().gas();
        return gas == null ? fluid -> false
            : fluid -> com.tonywww.foodindustrial.platform.PlatformTags.fluidId(fluid.fluid()).equals(gas.fluid());
    }

    /** Whether the machine can run {@code candidate} now; on success the match and catalyst slots are kept. */
    private boolean accept(MachineBlockEntity machine, RecipeIndex.Entry candidate, List<ItemStack> items, List<FluidAmount> fluids, Capabilities capabilities,
                           boolean strict) {
        ProcessRecipe recipe = candidate.recipe();
        if (!takes(machine, candidate, capabilities, strict)) {
            return false;
        }
        ProcessRecipe.Match found = recipe.match(items, fluids, spare(machine));
        if (found == null) {
            return false;
        }
        int[] slots = catalystSlots(machine.inventory(), recipe.catalysts());
        if (slots == null) {
            return false;
        }
        entry = candidate;
        match = found;
        catalystSlots = slots;
        return true;
    }

    /** A distinct catalyst slot for each catalyst of the recipe, or {@code null}. */
    @Nullable
    private static int[] catalystSlots(MachineInventory inventory, List<Catalyst> needed) {
        int[] slots = new int[needed.size()];
        boolean[] used = new boolean[inventory.count(SlotRole.CATALYST)];
        for (int i = 0; i < needed.size(); i++) {
            slots[i] = -1;
            for (int c = 0; c < used.length && slots[i] < 0; c++) {
                if (!used[c] && needed.get(i).test(inventory.get(SlotRole.CATALYST, c))) {
                    used[c] = true;
                    slots[i] = c;
                }
            }
            if (slots[i] < 0) {
                return null;
            }
        }
        return slots;
    }

    private static List<ItemStack> inputs(MachineInventory inventory) {
        List<ItemStack> list = new ArrayList<>(inventory.count(SlotRole.INPUT));
        for (int i = 0; i < inventory.count(SlotRole.INPUT); i++) {
            list.add(inventory.get(SlotRole.INPUT, i));
        }
        return list;
    }

    private static List<FluidAmount> inputs(TankSet tanks) {
        List<FluidAmount> list = new ArrayList<>(tanks.inputs());
        for (int i = 0; i < tanks.inputs(); i++) {
            list.add(tanks.get(i));
        }
        return list;
    }

    // ---- products ----

    /** Products of the next completion at their largest amount, with their purity, for the space check. */
    private void plan(MachineBlockEntity machine) {
        ProcessRecipe recipe = entry.recipe();
        Tier tier = machine.definition().tier();
        int[] values = new int[recipe.inputs().size()];
        long[] amounts = new long[recipe.inputs().size()];
        inputPurities(machine, recipe, values, amounts);
        List<ItemStack> items = new ArrayList<>();
        List<FluidAmount> fluids = new ArrayList<>();
        int main = -1;
        RandomSource fixed = RandomSource.create(0L);
        plannedQuality = quality(machine, recipe);
        for (int index = 0; index < recipe.outputs().size(); index++) {
            ProcessOutput output = recipe.outputs().get(index);
            if (!output.producedAt(tier)) {
                continue;
            }
            int amount = output.amount();
            if (index == 0 && recipe.scalesYield()) {
                amount += OutcomeResolver.extra(amount, machine.definition().profile().yieldPermille(), 0);
            }
            int b = index == 0 && recipe.purityRange().isPresent() ? OutcomeResolver.NONE
                : OutcomeResolver.purity(recipe, index, profile(machine), baseTier(), values, amounts, fixed);
            if (output.isFluid()) {
                FluidAmount fluid = output.fluidAmount(amount);
                fluids.add(stamped(b == OutcomeResolver.NONE ? fluid : fluid.withPurity(b), plannedQuality, index == 0));
            } else {
                ItemStack stack = output.stack(amount);
                if (index == 0) {
                    main = items.size();
                }
                items.add(stamped(b == OutcomeResolver.NONE ? stack : Purity.with(stack, b), plannedQuality, index == 0));
            }
        }
        plannedMain = main;
        plannedItems = items.toArray(ItemStack[]::new);
        plannedFluids = fluids.toArray(FluidAmount[]::new);
        maxRuns = parallelLimit(machine, recipe);
        setup = machine.setupTicks();
        outputsDirty = true;
    }

    /**
     * Copies one completion can make (machines/01 §1 rule 4, 2026-11-11): up to one plus the parallel
     * upgrades, as many as the matched slots and tanks hold inputs for.
     */
    private int parallelLimit(MachineBlockEntity machine, ProcessRecipe recipe) {
        int limit = machine.upgrades().parallel() * Math.max(1, machine.structureParallel());
        for (int slot = 0; slot < additiveTake.length && limit > 1; slot++) {
            if (additiveTake[slot] > 0) {
                limit = Math.min(limit, machine.inventory().get(SlotRole.ADDITIVE, slot).getCount() / additiveTake[slot]);
            }
        }
        for (int i = 0; i < recipe.inputs().size() && limit > 1; i++) {
            ProcessInput input = recipe.inputs().get(i);
            if (input instanceof ProcessInput.ItemInput item) {
                limit = Math.min(limit, machine.inventory().get(SlotRole.INPUT, match.slots()[i]).getCount() / Math.max(1, item.count()));
            } else {
                int amount = ((ProcessInput.FluidInput) input).amount();
                limit = Math.min(limit, machine.tanks().get(match.tanks()[i]).amount() / Math.max(1, amount));
            }
        }
        return Math.max(1, limit);
    }

    private static ItemStack[] scaled(ItemStack[] stacks, int n) {
        if (n == 1) {
            return stacks;
        }
        ItemStack[] result = new ItemStack[stacks.length];
        for (int i = 0; i < stacks.length; i++) {
            result[i] = stacks[i].isEmpty() ? stacks[i] : stacks[i].copyWithCount(stacks[i].getCount() * n);
        }
        return result;
    }

    private static FluidAmount[] scaled(FluidAmount[] fluids, int n) {
        if (n == 1) {
            return fluids;
        }
        FluidAmount[] result = new FluidAmount[fluids.length];
        for (int i = 0; i < fluids.length; i++) {
            result[i] = fluids[i].isEmpty() ? fluids[i] : fluids[i].withAmount(fluids[i].amount() * n);
        }
        return result;
    }

    /** Copies the next completion makes (for the screen). */
    public int runs() {
        return entry == null ? 0 : runs;
    }

    /** Profile of the machine with its standardisation upgrade (machines/01 §1.2). */
    private static DeviceProfile profile(MachineBlockEntity machine) {
        return machine.definition().profile().withNormalizing(machine.upgrades().normalizing());
    }

    private Tier baseTier() {
        Tier base = entry.baseTier();
        return base != null ? base : Tier.T0;
    }

    private void inputPurities(MachineBlockEntity machine, ProcessRecipe recipe, int[] values, long[] amounts) {
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput input = recipe.inputs().get(i);
            if (input instanceof ProcessInput.ItemInput item) {
                values[i] = item.purityOf(machine.inventory().get(SlotRole.INPUT, match.slots()[i]));
            } else {
                values[i] = ((ProcessInput.FluidInput) input).purityOf(machine.tanks().get(match.tanks()[i]));
            }
            amounts[i] = input.weight();
        }
    }

    /** One completion; {@code climate}: a fermentation room is warm and humid (upper half of a purity range). */
    private void complete(MachineBlockEntity machine, boolean climate) {
        ProcessRecipe recipe = entry.recipe();
        DeviceProfile profile = profile(machine);
        RandomSource random = machine.getLevel().getRandom();
        int[] values = new int[recipe.inputs().size()];
        long[] amounts = new long[recipe.inputs().size()];
        inputPurities(machine, recipe, values, amounts);
        Tier base = baseTier();
        ProcessRecipe.Match used = match;
        int[] usedCatalysts = catalystSlots;
        int copies = Math.max(1, runs);
        int code = quality(machine, recipe);
        int[] take = additiveTake;
        machine.completing(recipe);
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput input = recipe.inputs().get(i);
            if (input instanceof ProcessInput.ItemInput item) {
                machine.inventory().shrink(machine.inventory().slot(SlotRole.INPUT, used.slots()[i]), item.count() * copies);
            } else {
                machine.tanks().drain(used.tanks()[i], ((ProcessInput.FluidInput) input).amount() * copies);
            }
        }
        for (int index = 0; index < recipe.outputs().size(); index++) {
            int amount = 0;
            for (int copy = 0; copy < copies; copy++) {
                amount += OutcomeResolver.amount(recipe, index, profile, random);
            }
            int permille = machine.outputPermille(recipe, index);
            if (permille != 1000) {
                long scaled = (long) amount * permille;
                amount = (int) (scaled / 1000) + (random.nextInt(1000) < scaled % 1000 ? 1 : 0);
            }
            if (amount <= 0) {
                continue;
            }
            ProcessOutput output = recipe.outputs().get(index);
            int b = OutcomeResolver.purity(recipe, index, profile, base, values, amounts, random, climate);
            if (output.isFluid()) {
                FluidAmount fluid = output.fluidAmount(amount);
                machine.tanks().fillOutput(stamped(b == OutcomeResolver.NONE ? fluid : fluid.withPurity(b), code, index == 0), false);
            } else {
                ItemStack stack = output.stack(amount);
                machine.inventory().insertOutput(stamped(b == OutcomeResolver.NONE ? stack : Purity.with(stack, b), code, index == 0),
                    index == 0, false);
            }
        }
        for (int slot = 0; slot < take.length; slot++) {
            if (take[slot] > 0) {
                machine.inventory().shrink(machine.inventory().slot(SlotRole.ADDITIVE, slot), take[slot] * copies);
            }
        }
        for (int i = 0; i < recipe.catalysts().size(); i++) {
            int slot = usedCatalysts[i];
            for (int copy = 0; copy < copies && !machine.inventory().get(SlotRole.CATALYST, slot).isEmpty(); copy++) {
                machine.wearCatalyst(slot, recipe.catalysts().get(i).runs());
            }
        }
        machine.completed(recipe, usedCatalysts);
        inputsDirty = true;
        outputsDirty = true;
    }

    // ---- persistence ----

    public void save(DataWriter out) {
        if (entry != null) {
            out.putString("Recipe", entry.id());
            out.putLong("Progress", progress);
        }
    }

    public void load(DataReader in) {
        String id = in.getString("Recipe", "");
        savedRecipe = id.isEmpty() ? null : id;
        progress = savedRecipe == null ? 0 : Math.max(0, in.getLong("Progress", 0));
        entry = null;
        inputsDirty = true;
        outputsDirty = true;
    }
}
