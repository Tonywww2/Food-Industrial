package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.Catalyst;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.EnvironmentFactor;
import com.tonywww.foodindustrial.recipe.OutcomeResolver;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * One process unit of a T0 device (additives/07 §操作与实现规格, 2026-11-10): a group of item slots
 * and tanks whose contents change in place. Products go back into the same slots and tanks, so a
 * product left in the device starts the next recipe that fits it (failure products). When several
 * recipes fit, the most specific one runs ({@link ProcessRecipe#specificity()}), ties by id. Passive
 * devices call {@link #tick} once per 20-tick batch; the pot calls {@link #act} once per stir.
 */
public final class InPlaceProcess {
    /** Item slots and tanks of the unit within the device's contents; catalyst slots are shared. */
    public record Unit(int[] slots, int[] tanks) {
    }

    /** Outcome of one batch or action. */
    public enum Step {
        /** No recipe can run now. */
        IDLE,
        /** Progress advanced. */
        PROGRESS,
        /** A recipe completed; the contents changed. */
        DONE
    }

    private final Unit unit;
    @Nullable
    private RecipeIndex.Entry entry;
    private long progress;
    @Nullable
    private String savedRecipe;
    @Nullable
    private Set<String> missing;
    /**
     * The running (or saved) recipe still fits the contents but lacks a condition for now (night over
     * the salt pan): it pauses with its progress (I14, additives/06 §T1 作坊).
     */
    @Nullable
    private RecipeIndex.Entry held;

    public InPlaceProcess(Unit unit) {
        this.unit = unit;
    }

    public Unit unit() {
        return unit;
    }

    /** Recipe running now, or {@code null}. */
    @Nullable
    public RecipeIndex.Entry current() {
        return entry;
    }

    /** Progress in ticks (passive) or actions (manual). */
    public long progress() {
        return progress;
    }

    /** Conditions missing for the most specific recipe that would fit the contents, or {@code null}. */
    @Nullable
    public Set<String> missing() {
        return missing;
    }

    /** Clears the progress (the salt pan in the rain). */
    public void resetProgress() {
        progress = 0;
    }

    /** Ticks of the current recipe under {@code current}, after the environment factors. */
    public long goalTicks(Capabilities current) {
        return entry == null ? 0 : goalTicks(entry, current);
    }

    /** Ticks of {@code entry} under {@code current}, after the environment factors. */
    public static long goalTicks(RecipeIndex.Entry entry, Capabilities current) {
        ProcessRecipe recipe = entry.recipe();
        return Math.max(4, Math.round(recipe.batchTime() * EnvironmentFactor.time(recipe.environmentFactors(), current)));
    }

    /**
     * The recipe that would run now, for a status line; updates {@link #missing()} and leaves the
     * progress alone.
     */
    @Nullable
    public RecipeIndex.Entry preview(Level level, DeviceSpec spec, DeviceContents contents, Capabilities current) {
        Choice choice = select(level, spec, contents, current);
        return choice == null ? null : choice.entry();
    }

    /** One batch of {@code delta} ticks of a passive device. */
    public Step tick(Level level, DeviceSpec spec, DeviceContents contents, Capabilities current, int delta) {
        Choice choice = select(level, spec, contents, current);
        if (!adopt(choice)) {
            return Step.IDLE;
        }
        progress += delta;
        if (progress < goalTicks(current)) {
            return Step.PROGRESS;
        }
        progress = 0;
        complete(level, spec, contents, current, choice);
        entry = null;
        return Step.DONE;
    }

    /** One hand action (a stir of the pot). */
    public Step act(Level level, DeviceSpec spec, DeviceContents contents, Capabilities current) {
        Choice choice = select(level, spec, contents, current);
        if (!adopt(choice)) {
            return Step.IDLE;
        }
        progress++;
        if (progress < entry.recipe().manualActions()) {
            return Step.PROGRESS;
        }
        progress = 0;
        complete(level, spec, contents, current, choice);
        entry = null;
        return Step.DONE;
    }

    /**
     * Takes the chosen recipe; progress carries on only when the recipe stays the same. Without a
     * choice, a running recipe that only lacks a condition keeps its progress.
     */
    private boolean adopt(@Nullable Choice choice) {
        if (choice == null) {
            if (held != null) {
                entry = held;
                savedRecipe = null;
                return false;
            }
            entry = null;
            progress = 0;
            return false;
        }
        if (entry == null && savedRecipe != null && savedRecipe.equals(choice.entry().id())) {
            entry = choice.entry();
        }
        savedRecipe = null;
        if (entry != choice.entry()) {
            entry = choice.entry();
            progress = 0;
        }
        return true;
    }

    // ---- selection ----

    /** A recipe that can run on the contents now. */
    record Choice(RecipeIndex.Entry entry, ProcessRecipe.Match match, int[] catalystSlots) {
    }

    @Nullable
    private Choice select(Level level, DeviceSpec spec, DeviceContents contents, Capabilities current) {
        List<ItemStack> items = items(contents);
        List<FluidAmount> fluids = fluids(contents);
        missing = null;
        held = null;
        List<Choice> runnable = new ArrayList<>();
        RecipeIndex.Entry blocked = null;
        for (RecipeIndex.Entry candidate : candidates(RecipeIndex.get(level), spec, items, fluids)) {
            ProcessRecipe recipe = candidate.recipe();
            // the products stay in the device's slots: they do not stop the next run (full coverage, 2026-11-12)
            ProcessRecipe.Match match = recipe.match(items, fluids, stack -> producedItem(recipe, stack), fluid -> producedFluid(recipe, fluid));
            if (match == null) {
                continue;
            }
            int[] catalysts = catalystSlots(contents, recipe.catalysts());
            if (catalysts == null) {
                continue;
            }
            if (!current.covers(recipe.required())) {
                if (blocked == null || recipe.specificity() > blocked.recipe().specificity()) {
                    blocked = candidate;
                }
                if (candidate == entry || (entry == null && candidate.id().equals(savedRecipe))) {
                    held = candidate;
                }
                continue;
            }
            runnable.add(new Choice(candidate, match, catalysts));
        }
        runnable.sort(Comparator.<Choice>comparingInt(choice -> -choice.entry().recipe().specificity())
            .thenComparing(choice -> choice.entry().id()));
        for (Choice choice : runnable) {
            if (fits(level, spec, contents, current, choice)) {
                return choice;
            }
        }
        if (!runnable.isEmpty()) {
            // another recipe could run but its products do not fit: no pause
            held = null;
        }
        if (held != null) {
            missing = current.missing(held.recipe().required());
        } else if (runnable.isEmpty() && blocked != null) {
            missing = current.missing(blocked.recipe().required());
        }
        return null;
    }

    /** Recipes the device runs whose first input accepts something in the unit. */
    private static Set<RecipeIndex.Entry> candidates(RecipeIndex index, DeviceSpec spec, List<ItemStack> items, List<FluidAmount> fluids) {
        Set<RecipeIndex.Entry> found = new LinkedHashSet<>();
        for (ProcessType type : spec.types()) {
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    addRunnable(found, index.candidates(type, stack.getItem()), spec);
                }
            }
            for (FluidAmount fluid : fluids) {
                if (!fluid.isEmpty()) {
                    addRunnable(found, index.candidates(type, fluid.fluid()), spec);
                }
            }
        }
        return found;
    }

    private static void addRunnable(Set<RecipeIndex.Entry> found, List<RecipeIndex.Entry> entries, DeviceSpec spec) {
        for (RecipeIndex.Entry entry : entries) {
            if (entry.runsOn(spec)) {
                found.add(entry);
            }
        }
    }

    /** A distinct catalyst slot for each catalyst of the recipe, or {@code null}. */
    @Nullable
    private static int[] catalystSlots(DeviceContents contents, List<Catalyst> needed) {
        int[] slots = new int[needed.size()];
        boolean[] used = new boolean[contents.catalystSlots()];
        for (int i = 0; i < needed.size(); i++) {
            slots[i] = -1;
            for (int c = 0; c < used.length && slots[i] < 0; c++) {
                if (!used[c] && needed.get(i).test(contents.catalyst(c))) {
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

    private static boolean producedItem(ProcessRecipe recipe, ItemStack stack) {
        for (ProcessOutput output : recipe.outputs()) {
            if (!output.isFluid() && output.stack(1).getItem() == stack.getItem()) {
                return true;
            }
        }
        return false;
    }

    private static boolean producedFluid(ProcessRecipe recipe, FluidAmount fluid) {
        for (ProcessOutput output : recipe.outputs()) {
            if (output.isFluid() && output.fluidAmount(1).fluid() == fluid.fluid()) {
                return true;
            }
        }
        return false;
    }

    private List<ItemStack> items(DeviceContents contents) {
        List<ItemStack> items = new ArrayList<>(unit.slots().length);
        for (int slot : unit.slots()) {
            items.add(contents.item(slot));
        }
        return items;
    }

    private List<FluidAmount> fluids(DeviceContents contents) {
        List<FluidAmount> fluids = new ArrayList<>(unit.tanks().length);
        for (int tank : unit.tanks()) {
            fluids.add(contents.tank(tank));
        }
        return fluids;
    }

    // ---- running ----

    /** Whether the products of one run (largest amounts) fit once the inputs are taken. */
    private boolean fits(Level level, DeviceSpec spec, DeviceContents contents, Capabilities current, Choice choice) {
        DeviceContents trial = contents.copy();
        Products products = products(spec, trial, choice, current, RandomSource.create(0L), true);
        consume(trial, choice);
        return place(trial, products, choice.entry().recipe().purityRange().isPresent());
    }

    private void complete(Level level, DeviceSpec spec, DeviceContents contents, Capabilities current, Choice choice) {
        Products products = products(spec, contents, choice, current, level.getRandom(), false);
        consume(contents, choice);
        place(contents, products, false);
        ProcessRecipe recipe = choice.entry().recipe();
        for (int i = 0; i < recipe.catalysts().size(); i++) {
            contents.wearCatalyst(choice.catalystSlots()[i], recipe.catalysts().get(i).runs());
        }
    }

    /** Products of one run, items and fluids, with their purity. */
    public record Products(List<ItemStack> items, List<FluidAmount> fluids) {
    }

    /** Products of {@code choice} from the unit's current inputs. */
    private Products products(DeviceSpec spec, DeviceContents contents, Choice choice, Capabilities current, RandomSource random, boolean largest) {
        ProcessRecipe recipe = choice.entry().recipe();
        int[] values = new int[recipe.inputs().size()];
        long[] amounts = new long[recipe.inputs().size()];
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput input = recipe.inputs().get(i);
            if (input instanceof ProcessInput.ItemInput item) {
                values[i] = item.purityOf(contents.item(unit.slots()[choice.match().slots()[i]]));
            } else {
                values[i] = ((ProcessInput.FluidInput) input).purityOf(contents.tank(unit.tanks()[choice.match().tanks()[i]]));
            }
            amounts[i] = input.weight();
        }
        return products(spec, choice.entry(), values, amounts, current, random, largest);
    }

    /**
     * Products of one run of {@code entry} on a T0 device with their purity (additives/10 §2.1).
     * {@code largest}: every output at its largest amount (the space check); otherwise amounts and
     * chances are rolled. The environment factors scale the main product (machines/01 §3.1).
     *
     * @param values  effective purity of each input
     * @param amounts weight of each input in the start purity
     */
    public static Products products(DeviceSpec spec, RecipeIndex.Entry entry, int[] values, long[] amounts, Capabilities current,
                                    RandomSource random, boolean largest) {
        ProcessRecipe recipe = entry.recipe();
        Tier base = entry.baseTier() != null ? entry.baseTier() : Tier.T0;
        double yield = EnvironmentFactor.yield(recipe.environmentFactors(), current);
        List<ItemStack> items = new ArrayList<>();
        List<FluidAmount> fluids = new ArrayList<>();
        for (int index = 0; index < recipe.outputs().size(); index++) {
            ProcessOutput output = recipe.outputs().get(index);
            int amount;
            if (largest) {
                amount = output.producedAt(spec.tier()) ? output.amount() : 0;
                if (index == 0) {
                    amount = (int) Math.ceil(amount * yield);
                }
            } else {
                amount = OutcomeResolver.amount(recipe, index, spec.profile(), random);
                if (index == 0 && yield != 1.0 && amount > 0) {
                    double scaled = amount * yield;
                    amount = (int) Math.floor(scaled) + (random.nextDouble() < scaled - Math.floor(scaled) ? 1 : 0);
                }
            }
            if (amount <= 0) {
                continue;
            }
            int b = OutcomeResolver.purity(recipe, index, spec.profile(), base, values, amounts, random);
            if (output.isFluid()) {
                FluidAmount fluid = output.fluidAmount(amount);
                fluids.add(b == OutcomeResolver.NONE ? fluid : fluid.withPurity(b));
            } else {
                ItemStack stack = output.stack(amount);
                items.add(b == OutcomeResolver.NONE ? stack : Purity.with(stack, b));
            }
        }
        return new Products(items, fluids);
    }

    private void consume(DeviceContents contents, Choice choice) {
        ProcessRecipe recipe = choice.entry().recipe();
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput input = recipe.inputs().get(i);
            if (input instanceof ProcessInput.ItemInput item) {
                int slot = unit.slots()[choice.match().slots()[i]];
                ItemStack stack = contents.item(slot).copy();
                stack.shrink(item.count());
                contents.setItem(slot, stack);
            } else {
                int tank = unit.tanks()[choice.match().tanks()[i]];
                FluidAmount fluid = contents.tank(tank);
                contents.setTank(tank, fluid.withAmount(fluid.amount() - ((ProcessInput.FluidInput) input).amount()));
            }
        }
    }

    /**
     * Puts the products into the unit. {@code ownSlot}: the main product needs an empty slot of its
     * own (its purity is drawn from a range, so it may not stack).
     */
    private boolean place(DeviceContents contents, Products products, boolean ownSlot) {
        for (int i = 0; i < products.items().size(); i++) {
            ItemStack stack = products.items().get(i);
            if (i == 0 && ownSlot) {
                boolean empty = false;
                for (int slot : unit.slots()) {
                    if (contents.item(slot).isEmpty()) {
                        contents.setItem(slot, stack.copy());
                        empty = true;
                        break;
                    }
                }
                if (!empty) {
                    return false;
                }
            } else if (!contents.insertItem(unit.slots(), stack).isEmpty()) {
                return false;
            }
        }
        for (FluidAmount fluid : products.fluids()) {
            if (!contents.insertFluid(unit.tanks(), fluid)) {
                return false;
            }
        }
        return true;
    }

    // ---- persistence ----

    public void save(DataWriter out, String prefix) {
        String id = entry != null ? entry.id() : savedRecipe;
        if (id != null && progress > 0) {
            out.putString(prefix + "Recipe", id);
            out.putLong(prefix + "Progress", progress);
        }
    }

    public void load(DataReader in, String prefix) {
        String id = in.getString(prefix + "Recipe", "");
        savedRecipe = id.isEmpty() ? null : id;
        progress = savedRecipe == null ? 0 : Math.max(0, in.getLong(prefix + "Progress", 0));
        entry = null;
    }
}
