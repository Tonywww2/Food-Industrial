package com.tonywww.foodindustrial.machine.bio;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.OutcomeResolver;
import com.tonywww.foodindustrial.recipe.Plate;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * M30 culture bench (I17, additives/06 §T4 生物 / 精细：操作与实现规格, 2026-11-11): its own window.
 * Isolation, purification, mutagenesis and recombination work a plate: the player inoculates (the
 * inputs are used and the products and colonies are worked out), the plate incubates for the recipe's
 * time on FE, colonies grow, an optional identification shows their names, and the player picks one
 * colony into the output or discards the plate. Expansion runs as a normal machine (automatable).
 * The plate waits for the player; no new plate is inoculated before it is picked or discarded.
 */
public class CultureBenchBlockEntity extends ProcessingMachineBlockEntity {
    public static final String ID = "culture_bench";
    public static final String EXPANSION = "expansion";
    public static final int IDENTIFY_TICKS = 200;
    public static final int IDENTIFY_ENERGY = 40;
    /** Actions of the window ({@code MenuActions.CULTURE}). */
    public static final int INOCULATE = 0;
    public static final int IDENTIFY = 1;
    public static final int PICK = 2;
    public static final int DISCARD = 3;

    /** Where the plate stands. */
    public enum Stage {
        IDLE, INCUBATING, PLATE, IDENTIFYING
    }

    private Stage stage = Stage.IDLE;
    /** Progress of incubation or identification, thousandths of a tick at speed ×1. */
    private long plateProgress;
    private int plateTotal;
    private long plateEnergy;
    private List<ItemStack> colonies = List.of();
    private boolean identified;
    /** The plate recipe the inputs allow now, looked up when they change. */
    @Nullable
    private RecipeIndex.Entry plateRecipe;
    private boolean plateDirty = true;

    public CultureBenchBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    public Stage stage() {
        return stage;
    }

    /** The colonies of the plate (empty before it has grown). */
    public List<ItemStack> colonies() {
        return stage == Stage.PLATE || stage == Stage.IDENTIFYING ? colonies : List.of();
    }

    public boolean identified() {
        return identified;
    }

    /** Whether the inputs allow inoculating a plate now. */
    public boolean canInoculate() {
        return stage == Stage.IDLE && plateMode() && plateRecipe != null;
    }

    /** Whether the current operation works a plate (all but expansion). */
    public boolean plateMode() {
        MachineMode current = mode();
        return current != null && !current.id().equals(EXPANSION);
    }

    private long identifyEnergy() {
        return (long) IDENTIFY_ENERGY * definition.profile().energyPermille() / 1000L * upgrades().energyPermille() / 1000L;
    }

    @Override
    protected MachineStatus tickBatch(int delta, long gameTime) {
        lacking = null;
        switch (stage) {
            case PLATE -> {
                return MachineStatus.PLATE;
            }
            case INCUBATING, IDENTIFYING -> {
                if (!redstone().allows(isPowered())) {
                    return MachineStatus.REDSTONE;
                }
                long perTick = stage == Stage.INCUBATING ? plateEnergy : identifyEnergy();
                if (!energy().consume(perTick * delta)) {
                    return MachineStatus.NO_POWER;
                }
                plateProgress += (long) delta * definition.profile().speed() * speedPermille();
                if (plateProgress >= plateTotal * 1000L) {
                    plateProgress = 0;
                    if (stage == Stage.IDENTIFYING) {
                        identified = true;
                    }
                    stage = Stage.PLATE;
                    markDirty();
                    return MachineStatus.PLATE;
                }
                markUnsaved();
                return MachineStatus.RUNNING;
            }
            default -> {
                if (!plateMode()) {
                    return super.tickBatch(delta, gameTime);
                }
                if (plateDirty) {
                    plateDirty = false;
                    RecipeIndex.Entry found = findPlateRecipe();
                    if (found != plateRecipe) {
                        plateRecipe = found;
                        markDirty();
                    }
                }
                return plateRecipe != null ? MachineStatus.INOCULATE : MachineStatus.WAITING;
            }
        }
    }

    // ---- actions ----

    /** A button of the window (server side). */
    public void action(int action, int colony) {
        switch (action) {
            case INOCULATE -> inoculate();
            case IDENTIFY -> {
                if (stage == Stage.PLATE && !identified && !colonies.isEmpty()) {
                    stage = Stage.IDENTIFYING;
                    plateTotal = IDENTIFY_TICKS;
                    plateProgress = 0;
                }
            }
            case PICK -> {
                if (stage == Stage.PLATE && colony >= 0 && colony < colonies.size()) {
                    // the picked culture goes to whichever output slot takes it
                    ItemStack picked = colonies.get(colony);
                    boolean main = inventory().insertOutput(picked, true, true).isEmpty();
                    if (!main && !inventory().insertOutput(picked, false, true).isEmpty()) {
                        return;
                    }
                    inventory().insertOutput(picked, main, false);
                    clearPlate();
                }
            }
            case DISCARD -> {
                if (stage == Stage.PLATE) {
                    clearPlate();
                }
            }
            default -> {
                return;
            }
        }
        markDirty();
        wake();
    }

    private void clearPlate() {
        stage = Stage.IDLE;
        colonies = List.of();
        identified = false;
        plateProgress = 0;
        plateDirty = true;
    }

    private void inoculate() {
        if (!canInoculate()) {
            return;
        }
        RecipeIndex.Entry entry = plateRecipe;
        ProcessRecipe recipe = entry.recipe();
        List<ItemStack> items = inputs();
        ProcessRecipe.Match match = recipe.match(items, List.of());
        if (match == null) {
            plateDirty = true;
            return;
        }
        RandomSource random = level.getRandom();
        DeviceProfile profile = definition.profile().withNormalizing(upgrades().normalizing());
        Tier base = entry.baseTier() != null ? entry.baseTier() : Tier.T0;
        int[] values = new int[recipe.inputs().size()];
        long[] amounts = new long[recipe.inputs().size()];
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput.ItemInput input = (ProcessInput.ItemInput) recipe.inputs().get(i);
            values[i] = input.purityOf(items.get(match.slots()[i]));
            amounts[i] = input.weight();
        }
        ItemStack[] products = new ItemStack[recipe.outputs().size()];
        for (int index = 0; index < products.length; index++) {
            ProcessOutput output = recipe.outputs().get(index);
            int amount = Math.max(1, OutcomeResolver.amount(recipe, index, profile, random));
            int b = OutcomeResolver.purity(recipe, index, profile, base, values, amounts, random);
            ItemStack stack = output.stack(amount);
            products[index] = b == OutcomeResolver.NONE || stack.isEmpty() ? stack : Purity.with(stack, b);
        }
        Plate plate = recipe.plate().orElseThrow();
        List<ItemStack> drawn = new ArrayList<>();
        for (int i = 0; i < plate.colonies(); i++) {
            int index = plate.byPurity() ? targetOrWild(values[0], random) : weighted(recipe, plate, random);
            if (index >= 0 && !products[index].isEmpty()) {
                drawn.add(products[index].copy());
            }
        }
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput.ItemInput input = (ProcessInput.ItemInput) recipe.inputs().get(i);
            inventory().shrink(inventory().slot(SlotRole.INPUT, match.slots()[i]), input.count());
        }
        colonies = List.copyOf(drawn);
        identified = false;
        stage = Stage.INCUBATING;
        plateTotal = recipe.batchTime();
        plateEnergy = profile.energyPerTick(recipe) * upgrades().energyPermille() / 1000L;
        plateProgress = 0;
        plateDirty = true;
    }

    /** Purification: the target (output 0) with the input culture's purity as chance, else output 1. */
    private static int targetOrWild(int purity, RandomSource random) {
        double chance = purity == Purity.NONE ? 0.0 : PurityScale.fraction(purity);
        return random.nextDouble() < chance ? 0 : 1;
    }

    private int weighted(ProcessRecipe recipe, Plate plate, RandomSource random) {
        int total = 0;
        for (int i = 0; i < recipe.outputs().size(); i++) {
            if (recipe.outputs().get(i).producedAt(definition.tier())) {
                total += plate.weight(i);
            }
        }
        if (total <= 0) {
            return -1;
        }
        int roll = random.nextInt(total);
        for (int i = 0; i < recipe.outputs().size(); i++) {
            if (recipe.outputs().get(i).producedAt(definition.tier())) {
                roll -= plate.weight(i);
                if (roll < 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private List<ItemStack> inputs() {
        List<ItemStack> list = new ArrayList<>(inventory().count(SlotRole.INPUT));
        for (int i = 0; i < inventory().count(SlotRole.INPUT); i++) {
            list.add(inventory().get(SlotRole.INPUT, i));
        }
        return list;
    }

    /**
     * The plate recipe of the current operation the inputs allow: of the culturing recipes with a plate
     * the bench can run, the one with the most conditions, ties by id.
     */
    @Nullable
    private RecipeIndex.Entry findPlateRecipe() {
        if (level == null) {
            return null;
        }
        RecipeIndex index = RecipeIndex.get(level);
        List<ItemStack> items = inputs();
        RecipeIndex.Entry best = null;
        for (ItemStack stack : items) {
            if (stack.isEmpty()) {
                continue;
            }
            for (RecipeIndex.Entry candidate : index.candidates(ProcessTypes.CULTURING, stack.getItem())) {
                ProcessRecipe recipe = candidate.recipe();
                if (recipe.plate().isEmpty() || !candidate.runsOn(definition.spec()) || !capabilities().covers(recipe.required())
                    || recipe.fluidInputCount() > 0 || recipe.match(items, List.of()) == null) {
                    continue;
                }
                if (best == null || recipe.specificity() > best.recipe().specificity()
                    || recipe.specificity() == best.recipe().specificity() && candidate.id().compareTo(best.id()) < 0) {
                    best = candidate;
                }
            }
        }
        return best;
    }

    // ---- shown in the window ----

    @Override
    public @Nullable Job job() {
        if (stage == Stage.INCUBATING || stage == Stage.IDENTIFYING) {
            return new Job((int) (plateProgress / 1000), plateTotal, stage == Stage.INCUBATING ? plateEnergy : identifyEnergy(),
                ItemStack.EMPTY, com.tonywww.foodindustrial.core.storage.FluidAmount.EMPTY, 1000);
        }
        return stage == Stage.IDLE && !plateMode() ? super.job() : null;
    }

    @Override
    protected void inputsChanged() {
        super.inputsChanged();
        plateDirty = true;
    }

    @Override
    protected void modeChanged() {
        super.modeChanged();
        plateDirty = true;
    }

    // ---- persistence ----

    @Override
    protected void saveData(DataWriter out) {
        super.saveData(out);
        if (stage == Stage.IDLE) {
            return;
        }
        out.putInt("PlateStage", stage.ordinal());
        out.putLong("PlateProgress", plateProgress);
        out.putInt("PlateTotal", plateTotal);
        out.putLong("PlateEnergy", plateEnergy);
        out.putBoolean("Identified", identified);
        out.putInt("Colonies", colonies.size());
        for (int i = 0; i < colonies.size(); i++) {
            out.putItem("Colony" + i, colonies.get(i));
        }
    }

    @Override
    protected void loadData(DataReader in) {
        super.loadData(in);
        int ordinal = in.getInt("PlateStage", 0);
        stage = ordinal > 0 && ordinal < Stage.values().length ? Stage.values()[ordinal] : Stage.IDLE;
        plateProgress = Math.max(0, in.getLong("PlateProgress", 0));
        plateTotal = Math.max(0, in.getInt("PlateTotal", 0));
        plateEnergy = Math.max(0, in.getLong("PlateEnergy", 0));
        identified = in.getBoolean("Identified", false);
        List<ItemStack> loaded = new ArrayList<>();
        for (int i = 0; i < Math.min(8, in.getInt("Colonies", 0)); i++) {
            ItemStack stack = in.getItem("Colony" + i);
            if (!stack.isEmpty()) {
                loaded.add(stack);
            }
        }
        colonies = List.copyOf(loaded);
        plateDirty = true;
    }
}
