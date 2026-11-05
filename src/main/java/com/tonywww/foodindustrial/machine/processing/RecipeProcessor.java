package com.tonywww.foodindustrial.machine.processing;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.component.MachineInventory;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.component.TankSet;
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
import java.util.Set;

/**
 * Processing state machine of a recipe-driven machine (machines/02 §5). Each batch:
 * <ol>
 *   <li>redstone forbids → sleep;</li>
 *   <li>without a recipe, look one up only if the inputs changed (index by first input); none → sleep;</li>
 *   <li>products of this run (largest amount) do not fit → sleep until outputs are taken;</li>
 *   <li>take the batch's energy ({@code FE/t × 4}) or keep the fire lit; missing → sleep;</li>
 *   <li>advance progress by {@code 4 × speed}; at the recipe time consume inputs, settle purity and
 *       amounts, store products, wear catalysts and keep the remainder of the progress.</li>
 * </ol>
 * Progress is counted in thousandths of a tick so speed multipliers stay integer.
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
    private ItemStack[] plannedItems = new ItemStack[0];
    private FluidAmount[] plannedFluids = new FluidAmount[0];
    /** Index of the main product in {@link #plannedItems}, or -1 when it is a fluid or not produced. */
    private int plannedMain = -1;

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

    /** Ticks of one run of the current recipe at speed ×1, or 0 without a recipe. */
    public int totalTicks() {
        return entry == null ? 0 : entry.recipe().batchTime();
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
        if (outputsDirty) {
            outputsDirty = false;
            fluidsFit = machine.tanks().outputsFit(plannedFluids, plannedFluids.length);
            fits = machine.inventory().outputsFit(plannedItems, plannedItems.length, plannedMain) && fluidsFit;
        }
        if (!fits) {
            return MachineStatus.OUTPUT_FULL;
        }
        ProcessRecipe recipe = entry.recipe();
        DeviceProfile profile = machine.definition().profile();
        PowerKind power = machine.definition().power();
        if (power == PowerKind.ENERGY) {
            long need = profile.energyPerTick(recipe) * machine.upgrades().energyPermille() / 1000L * delta;
            if (!machine.energy().consume(need)) {
                return MachineStatus.NO_POWER;
            }
        } else if (power == PowerKind.FUEL && !machine.fireForRecipe()) {
            return MachineStatus.NO_POWER;
        }
        progress += (long) delta * profile.speed() * machine.upgrades().speedPermille();
        long goal = recipe.batchTime() * 1000L;
        if (progress >= goal) {
            progress -= goal;
            complete(machine);
        }
        machine.markUnsaved();
        return MachineStatus.RUNNING;
    }

    // ---- matching ----

    private void rematch(MachineBlockEntity machine) {
        missing = null;
        List<ItemStack> items = inputs(machine.inventory());
        List<FluidAmount> fluids = inputs(machine.tanks());
        RecipeIndex index = RecipeIndex.get(machine.getLevel());
        if (entry == null && savedRecipe != null) {
            entry = index.byId(savedRecipe);
            savedRecipe = null;
        }
        if (entry != null && accept(machine, entry, items, fluids, machine.capabilities())) {
            plan(machine);
            return;
        }
        entry = null;
        match = null;
        progress = 0;
        plannedMain = -1;
        plannedItems = new ItemStack[0];
        plannedFluids = new FluidAmount[0];
        RecipeIndex.Entry found = find(machine, index, items, fluids, machine.capabilities());
        if (found == null) {
            RecipeIndex.Entry possible = find(machine, index, items, fluids, machine.definition().spec().capabilities());
            if (possible != null) {
                Set<String> lacking = machine.capabilities().missing(possible.recipe().required());
                missing = lacking.isEmpty() ? "catalyst" : String.join(",", lacking);
            }
            match = null;
            return;
        }
        accept(machine, found, items, fluids, machine.capabilities());
        plan(machine);
    }

    @Nullable
    private RecipeIndex.Entry find(MachineBlockEntity machine, RecipeIndex index, List<ItemStack> items, List<FluidAmount> fluids, Capabilities capabilities) {
        DeviceSpec spec = machine.definition().spec();
        for (ProcessType type : ProcessTypes.recipeTypes()) {
            if (!spec.types().contains(type) || !machine.allows(type)) {
                continue;
            }
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    RecipeIndex.Entry found = first(machine, index.candidates(type, stack.getItem()), items, fluids, capabilities);
                    if (found != null) {
                        return found;
                    }
                }
            }
            for (FluidAmount fluid : fluids) {
                if (!fluid.isEmpty()) {
                    RecipeIndex.Entry found = first(machine, index.candidates(type, fluid.fluid()), items, fluids, capabilities);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private RecipeIndex.Entry first(MachineBlockEntity machine, List<RecipeIndex.Entry> entries, List<ItemStack> items, List<FluidAmount> fluids, Capabilities capabilities) {
        for (RecipeIndex.Entry candidate : entries) {
            if (accept(machine, candidate, items, fluids, capabilities)) {
                return candidate;
            }
        }
        return null;
    }

    /** Whether the machine can run {@code candidate} now; on success the match and catalyst slots are kept. */
    private boolean accept(MachineBlockEntity machine, RecipeIndex.Entry candidate, List<ItemStack> items, List<FluidAmount> fluids, Capabilities capabilities) {
        ProcessRecipe recipe = candidate.recipe();
        if (!candidate.runsOn(machine.definition().spec()) || !machine.allows(recipe.type()) || !capabilities.covers(recipe.required())) {
            return false;
        }
        ProcessRecipe.Match found = recipe.match(items, fluids);
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
        for (int index = 0; index < recipe.outputs().size(); index++) {
            ProcessOutput output = recipe.outputs().get(index);
            if (!output.producedAt(tier)) {
                continue;
            }
            int amount = output.amount();
            if (index == 0 && recipe.type().yieldScalable()) {
                amount += OutcomeResolver.extra(amount, machine.definition().profile().yieldPermille(), 0);
            }
            int b = index == 0 && recipe.purityRange().isPresent() ? OutcomeResolver.NONE
                : OutcomeResolver.purity(recipe, index, tier, baseTier(), values, amounts, fixed);
            if (output.isFluid()) {
                FluidAmount fluid = output.fluidAmount(amount);
                fluids.add(b == OutcomeResolver.NONE ? fluid : fluid.withPurity(b));
            } else {
                ItemStack stack = output.stack(amount);
                if (index == 0) {
                    main = items.size();
                }
                items.add(b == OutcomeResolver.NONE ? stack : Purity.with(stack, b));
            }
        }
        plannedMain = main;
        plannedItems = items.toArray(ItemStack[]::new);
        plannedFluids = fluids.toArray(FluidAmount[]::new);
        outputsDirty = true;
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

    private void complete(MachineBlockEntity machine) {
        ProcessRecipe recipe = entry.recipe();
        Tier tier = machine.definition().tier();
        DeviceProfile profile = machine.definition().profile();
        RandomSource random = machine.getLevel().getRandom();
        int[] values = new int[recipe.inputs().size()];
        long[] amounts = new long[recipe.inputs().size()];
        inputPurities(machine, recipe, values, amounts);
        Tier base = baseTier();
        ProcessRecipe.Match used = match;
        int[] usedCatalysts = catalystSlots;
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput input = recipe.inputs().get(i);
            if (input instanceof ProcessInput.ItemInput item) {
                machine.inventory().shrink(machine.inventory().slot(SlotRole.INPUT, used.slots()[i]), item.count());
            } else {
                machine.tanks().drain(used.tanks()[i], ((ProcessInput.FluidInput) input).amount());
            }
        }
        for (int index = 0; index < recipe.outputs().size(); index++) {
            int amount = OutcomeResolver.amount(recipe, index, profile, random);
            if (amount <= 0) {
                continue;
            }
            ProcessOutput output = recipe.outputs().get(index);
            int b = OutcomeResolver.purity(recipe, index, tier, base, values, amounts, random);
            if (output.isFluid()) {
                FluidAmount fluid = output.fluidAmount(amount);
                machine.tanks().fillOutput(b == OutcomeResolver.NONE ? fluid : fluid.withPurity(b), false);
            } else {
                ItemStack stack = output.stack(amount);
                machine.inventory().insertOutput(b == OutcomeResolver.NONE ? stack : Purity.with(stack, b), index == 0, false);
            }
        }
        for (int i = 0; i < recipe.catalysts().size(); i++) {
            int slot = usedCatalysts[i];
            if (machine.catalysts().wear(slot, recipe.catalysts().get(i).runs())) {
                machine.inventory().shrink(machine.inventory().slot(SlotRole.CATALYST, slot), 1);
            }
        }
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
