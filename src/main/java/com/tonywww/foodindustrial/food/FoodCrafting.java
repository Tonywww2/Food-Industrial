package com.tonywww.foodindustrial.food;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.food.flavor.FlavorType;
import com.tonywww.foodindustrial.food.flavor.Flavorings;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.food.quality.FoodQuality;
import com.tonywww.foodindustrial.food.quality.QualityRules;
import com.tonywww.foodindustrial.food.quality.QualitySpec;
import com.tonywww.foodindustrial.platform.recipe.PlatformCrafting;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A food-line step on the crafting table (machines/05 §2.1, §2.3; foods/05 §2–§3; I38): the T0 way of R19
 * compounding (the ingredient step), R20, R26, R27 and R29. Shapeless; knives and molds stay in the grid
 * (machines/05 §3); an ingredient step takes additives in the free slots, judged as on the machines; a second product
 * stays in the grid (machines/05 §2.3: R26 cutting).
 * <pre>
 * {
 *   "type": "foodindustrial:food_crafting",
 *   "process": "forming",
 *   "ingredients": [ { "semi_finished": "steamed_bun|dough" } ],
 *   "tools": [ { "tag": "foodindustrial:knives" } ],
 *   "result": { "item": "foodindustrial:steamed_bun", "count": 2 },
 *   "byproduct": { "item": "foodindustrial:fish_skin" },
 *   "quality": { "items": [ "state" ] }
 * }
 * </pre>
 * Each item ingredient takes {@code count} slots of one item each; a fluid ingredient ({@code "fluid"}, I48) takes one slot:
 * a bucket, bottle or other container holding at least the amount, which comes back empty (the whole content is used).
 * A flavouring made from flavouring materials is of the first flavour group of the first of them in the grid (I48,
 * {@link Flavorings#autoType}); the recipes of the other types do not match.
 */
public record FoodCrafting(ProcessType process, List<ProcessInput> ingredients, List<Ingredient> tools, ProcessOutput result,
                           Optional<ProcessOutput> byproduct, Optional<QualitySpec> quality) implements PlatformCrafting.Grid {
    private static final Codec<ProcessType> PROCESS = Codec.STRING.comapFlatMap(id -> {
        ProcessType type = ProcessTypes.byId(id);
        return type == null || type.isWorldInteraction() ? DataResult.error(() -> "Unknown process " + id) : DataResult.success(type);
    }, ProcessType::id);

    public static final MapCodec<FoodCrafting> CODEC = RecordCodecBuilder.<FoodCrafting>mapCodec(instance -> instance.group(
        PROCESS.fieldOf("process").forGetter(FoodCrafting::process),
        ProcessInput.CODEC.listOf().fieldOf("ingredients").forGetter(FoodCrafting::ingredients),
        StrictCodecs.optional("tools", PlatformIngredients.codec().listOf(), List.of()).forGetter(FoodCrafting::tools),
        ProcessOutput.CODEC.fieldOf("result").forGetter(FoodCrafting::result),
        StrictCodecs.optional("byproduct", ProcessOutput.CODEC).forGetter(FoodCrafting::byproduct),
        StrictCodecs.optional("quality", QualitySpec.CODEC).forGetter(FoodCrafting::quality)
    ).apply(instance, FoodCrafting::new)).flatXmap(FoodCrafting::validate, DataResult::success);

    public FoodCrafting {
        ingredients = List.copyOf(ingredients);
        tools = List.copyOf(tools);
    }

    private static DataResult<FoodCrafting> validate(FoodCrafting recipe) {
        int slots = recipe.tools.size();
        for (ProcessInput input : recipe.ingredients) {
            slots += units(input);
        }
        if (slots == 0 || slots > 9) {
            return DataResult.error(() -> "A crafting step needs 1–9 slots, not " + recipe.ingredients.size());
        }
        if (recipe.result.isFluid() || recipe.byproduct.map(ProcessOutput::isFluid).orElse(false)) {
            return DataResult.error(() -> "A crafting step makes items");
        }
        return DataResult.success(recipe);
    }

    /** Grid slots an ingredient takes: its count, one for a fluid. */
    private static int units(ProcessInput input) {
        return input instanceof ProcessInput.ItemInput item ? item.count() : 1;
    }

    /** Whether a grid stack can stand for one unit of {@code input}: the item, or a container holding the fluid. */
    private static boolean accepts(ProcessInput input, ItemStack stack) {
        if (input instanceof ProcessInput.ItemInput item) {
            return item.accepts(stack);
        }
        FluidAmount held = PlatformFluidContainers.contained(stack);
        return !held.isEmpty() && ((ProcessInput.FluidInput) input).test(held);
    }

    /** Which grid slot each unit (ingredients by count, then tools) takes; the rest are additives. */
    private record Assignment(int[] units, boolean[] additive) {
    }

    @Nullable
    private Assignment assign(List<ItemStack> grid) {
        List<java.util.function.Predicate<ItemStack>> units = new ArrayList<>();
        for (ProcessInput input : ingredients) {
            for (int i = 0; i < units(input); i++) {
                units.add(stack -> accepts(input, stack));
            }
        }
        for (Ingredient tool : tools) {
            Ingredient fresh = PlatformIngredients.fresh(tool);
            units.add(fresh::test);
        }
        int[] taken = new int[units.size()];
        boolean[] used = new boolean[grid.size()];
        if (!assign(0, units, grid, taken, used)) {
            return null;
        }
        boolean[] additive = new boolean[grid.size()];
        for (int slot = 0; slot < grid.size(); slot++) {
            additive[slot] = !used[slot] && !grid.get(slot).isEmpty();
        }
        return new Assignment(taken, additive);
    }

    private static boolean assign(int unit, List<java.util.function.Predicate<ItemStack>> units, List<ItemStack> grid, int[] taken, boolean[] used) {
        if (unit == units.size()) {
            return true;
        }
        for (int slot = 0; slot < grid.size(); slot++) {
            if (!used[slot] && !grid.get(slot).isEmpty() && units.get(unit).test(grid.get(slot))) {
                used[slot] = true;
                taken[unit] = slot;
                if (assign(unit + 1, units, grid, taken, used)) {
                    return true;
                }
                used[slot] = false;
            }
        }
        return false;
    }

    private static List<ItemStack> additives(List<ItemStack> grid, Assignment assignment) {
        List<ItemStack> list = new ArrayList<>();
        for (int slot = 0; slot < grid.size(); slot++) {
            // one item of each additive slot goes into a run
            list.add(assignment.additive[slot] ? grid.get(slot).copyWithCount(1) : ItemStack.EMPTY);
        }
        return list;
    }

    @Override
    public boolean matches(List<ItemStack> grid) {
        Assignment assignment = assign(grid);
        if (assignment == null) {
            return false;
        }
        FlavorType made = Flavorings.type(result.item());
        if (made != null && made != Flavorings.autoType(materials(grid))) {
            return false;
        }
        boolean extras = false;
        for (boolean additive : assignment.additive) {
            extras |= additive;
        }
        if (!extras) {
            return true;
        }
        // only an ingredient step takes more than its ingredients: the additives (foods/05 §3)
        return quality.isPresent() && !QualityRules.ingredient(quality.get(), result.id(), additives(grid, assignment), List.of()).refused();
    }

    @Override
    public ItemStack assemble(List<ItemStack> grid) {
        Assignment assignment = assign(grid);
        ItemStack stack = result.stack(result.amount());
        if (assignment == null) {
            return stack;
        }
        int code = code(grid, assignment);
        return code == FoodQuality.NONE ? stack : FoodLine.withQuality(stack, code);
    }

    /** Materials of the grid's stacks in slot order (bottled and bucketed fluids by their fluid). */
    private static List<String> materials(List<ItemStack> grid) {
        List<String> materials = new ArrayList<>();
        for (ItemStack stack : grid) {
            String material = QualityRules.material(stack);
            if (material != null) {
                materials.add(material);
            }
        }
        return materials;
    }

    private int code(List<ItemStack> grid, Assignment assignment) {
        if (quality.isPresent()) {
            List<QualityRules.Input> inputs = new ArrayList<>();
            int unit = 0;
            for (ProcessInput input : ingredients) {
                for (int i = 0; i < units(input); i++, unit++) {
                    ItemStack stack = grid.get(assignment.units[unit]);
                    String material = QualityRules.material(stack);
                    boolean asSalt = material != null && QualityRules.NITRITES.contains(material)
                        && input instanceof ProcessInput.ItemInput item && FoodLine.isSaltPosition(item);
                    inputs.add(new QualityRules.Input(QualityRules.grade(stack), FoodLine.quality(stack), material, asSalt));
                }
            }
            QualityRules.Outcome outcome = QualityRules.ingredient(quality.get(), result.id(), additives(grid, assignment), inputs);
            return outcome.refused() ? FoodQuality.NONE : outcome.code();
        }
        int code = FoodQuality.NONE;
        int unit = 0;
        for (ProcessInput input : ingredients) {
            for (int i = 0; i < units(input); i++, unit++) {
                ItemStack stack = grid.get(assignment.units[unit]);
                int value = FoodLine.quality(stack);
                // nitrite in a salt position is noted on any step (foods/02 §4.4, I47)
                if (input instanceof ProcessInput.ItemInput item && FoodLine.isSaltPosition(item) && QualityRules.isNitrite(QualityRules.material(stack))) {
                    value |= FoodQuality.NITRITE_MISUSE;
                }
                code = FoodQuality.merge(code, value);
            }
        }
        return code;
    }

    @Override
    public List<ItemStack> remaining(List<ItemStack> grid) {
        List<ItemStack> remaining = new ArrayList<>(grid.size());
        for (ItemStack stack : grid) {
            remaining.add(stack.isEmpty() ? ItemStack.EMPTY : PlatformCrafting.remainder(stack));
        }
        Assignment assignment = assign(grid);
        if (assignment == null) {
            return remaining;
        }
        int ingredientUnits = assignment.units.length - tools.size();
        // fluid containers come back empty
        int unit = 0;
        for (ProcessInput input : ingredients) {
            for (int i = 0; i < units(input); i++, unit++) {
                if (input instanceof ProcessInput.FluidInput) {
                    int slot = assignment.units[unit];
                    remaining.set(slot, PlatformFluidContainers.drained(grid.get(slot)));
                }
            }
        }
        // knives and molds stay (machines/05 §3)
        for (int t = 0; t < tools.size(); t++) {
            int slot = assignment.units[ingredientUnits + t];
            remaining.set(slot, grid.get(slot).copyWithCount(1));
        }
        // the second product stays in the slot of the first ingredient (machines/05 §2.3)
        if (byproduct.isPresent() && ingredientUnits > 0) {
            ItemStack second = byproduct.get().stack(byproduct.get().amount());
            int code = code(grid, assignment);
            if (code != FoodQuality.NONE && FoodLine.carries(second.getItem())) {
                FoodLine.withQuality(second, code);
            }
            int slot = assignment.units[0];
            if (remaining.get(slot).isEmpty()) {
                remaining.set(slot, second);
            }
        }
        return remaining;
    }

    @Override
    public ItemStack example() {
        return result.stack(result.amount());
    }

    /**
     * One ingredient per grid unit (ingredients by count, then tools), for other mods reading the recipe (AE2, Create;
     * 2026-11-25): a plain item or tag input as written; a semi-finished stage, a flavouring form or a fluid as an
     * ingredient testing exactly what {@link #matches} accepts.
     */
    @Override
    public List<Ingredient> slotIngredients() {
        List<Ingredient> list = new ArrayList<>();
        for (ProcessInput input : ingredients) {
            Ingredient ingredient;
            if (input instanceof ProcessInput.ItemInput item && !item.isSemi() && item.form().isEmpty()) {
                ingredient = item.ingredient();
            } else {
                ingredient = PlatformIngredients.matching(stack -> accepts(input, stack), () -> unitStacks(input));
            }
            for (int i = 0; i < units(input); i++) {
                list.add(ingredient);
            }
        }
        list.addAll(tools);
        return list.size() <= 9 ? list : List.of();
    }

    /** Stacks shown for one unit of an input: one item each; a fluid as the bottles and buckets holding enough of it. */
    public static List<ItemStack> unitStacks(ProcessInput input) {
        if (input instanceof ProcessInput.ItemInput item) {
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack stack : item.displayStacks()) {
                stacks.add(stack.copyWithCount(1));
            }
            return stacks;
        }
        ProcessInput.FluidInput fluidInput = (ProcessInput.FluidInput) input;
        List<ItemStack> stacks = new ArrayList<>();
        int amount = fluidInput.amount();
        for (FluidAmount fluid : fluidInput.fluid().displayFluids()) {
            if (amount <= com.tonywww.foodindustrial.world.interaction.FluidBottleItem.CAPACITY) {
                addFilled(stacks, new ItemStack(com.tonywww.foodindustrial.world.interaction.WorldInteractions.FLUID_BOTTLE.get()),
                    fluid.withAmount(com.tonywww.foodindustrial.world.interaction.FluidBottleItem.CAPACITY));
            }
            if (amount <= 1000) {
                addFilled(stacks, new ItemStack(net.minecraft.world.item.Items.BUCKET), fluid.withAmount(1000));
            }
        }
        return stacks;
    }

    private static void addFilled(List<ItemStack> stacks, ItemStack container, FluidAmount fluid) {
        ItemStack filled = PlatformFluidContainers.filled(container, fluid);
        if (!filled.isEmpty()) {
            stacks.add(filled);
        }
    }

    /** Registers the recipe serializer {@code foodindustrial:food_crafting}. */
    static void bootstrap() {
        PlatformCrafting.register("food_crafting", CODEC);
    }
}
