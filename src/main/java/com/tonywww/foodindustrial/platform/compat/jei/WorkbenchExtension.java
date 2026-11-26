package com.tonywww.foodindustrial.platform.compat.jei;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.food.FoodCrafting;
import com.tonywww.foodindustrial.gui.client.recipe.WorkbenchPage;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.recipe.PlatformCrafting;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
//? if forge {
import net.minecraftforge.fluids.FluidStack;
//?} else {
/*import net.neoforged.neoforge.fluids.FluidStack;
*///?}
//? if >=1.21 {
/*import net.minecraft.world.item.crafting.RecipeHolder;
*///?}

/**
 * The food line's crafting-table steps ({@code foodindustrial:food_crafting}, I38) on JEI's own crafting page
 * (2026-11-25, user: so AE2's terminals and patterns, and other mods that work with crafting recipes, treat them as
 * crafting recipes). The recipes are special, which JEI's crafting page shows only through an extension like this.
 * Slots: the 3 × 3 grid in order, unit by unit ({@link WorkbenchPage}); a fluid's slot shows the bottles and buckets
 * that hold enough of it and the fluid itself, so looking up the fluid finds the step.
 */
//? if <1.21 {
final class WorkbenchExtension implements ICraftingCategoryExtension {
    private final PlatformCrafting.GridRecipe<?> recipe;

    WorkbenchExtension(PlatformCrafting.GridRecipe<?> recipe) {
        this.recipe = recipe;
    }

    /** Whether JEI may show {@code recipe} this way now: a food-line step every slot of which has items, once {@code ready}. */
    static boolean handles(PlatformCrafting.GridRecipe<?> recipe, BooleanSupplier ready) {
        return recipe.data() instanceof FoodCrafting step && ready.getAsBoolean() && WorkbenchPage.of(recipe.id().toString(), step).complete();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper grid, IFocusGroup focuses) {
        layout(WorkbenchPage.of(recipe.id().toString(), (FoodCrafting) recipe.data()), builder, grid);
    }

    @Override
    public net.minecraft.resources.ResourceLocation getRegistryName() {
        return recipe.id();
    }
//?}
//? if >=1.21 && <26.1 {
/*@SuppressWarnings({"rawtypes", "unchecked"})
final class WorkbenchExtension implements ICraftingCategoryExtension<PlatformCrafting.GridRecipe<?>> {
    private final BooleanSupplier ready;

    WorkbenchExtension(BooleanSupplier ready) {
        this.ready = ready;
    }

    @Override
    public boolean isHandled(RecipeHolder<PlatformCrafting.GridRecipe<?>> holder) {
        return holder.value().data() instanceof FoodCrafting step && ready.getAsBoolean() && WorkbenchPage.of("", step).complete();
    }

    @Override
    public void setRecipe(RecipeHolder<PlatformCrafting.GridRecipe<?>> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper grid,
                          IFocusGroup focuses) {
        layout(WorkbenchPage.of(holder.id().toString(), (FoodCrafting) holder.value().data()), builder, grid);
    }

    @Override
    public java.util.Optional<net.minecraft.resources.ResourceLocation> getRegistryName(RecipeHolder<PlatformCrafting.GridRecipe<?>> holder) {
        return java.util.Optional.of(holder.id());
    }
*///?}
//? if >=26.1 {
/*@SuppressWarnings({"rawtypes", "unchecked"})
final class WorkbenchExtension implements ICraftingCategoryExtension<PlatformCrafting.GridRecipe<?>> {
    private final BooleanSupplier ready;

    WorkbenchExtension(BooleanSupplier ready) {
        this.ready = ready;
    }

    @Override
    public boolean isHandled(RecipeHolder<PlatformCrafting.GridRecipe<?>> holder) {
        return holder.value().data() instanceof FoodCrafting step && ready.getAsBoolean() && WorkbenchPage.of("", step).complete();
    }

    @Override
    public void setRecipe(RecipeHolder<PlatformCrafting.GridRecipe<?>> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper grid,
                          IFocusGroup focuses) {
        layout(WorkbenchPage.of(holder.id().identifier().toString(), (FoodCrafting) holder.value().data()), builder, grid);
    }

    @Override
    public List<net.minecraft.world.item.crafting.display.SlotDisplay> getIngredients(RecipeHolder<PlatformCrafting.GridRecipe<?>> holder) {
        List<net.minecraft.world.item.crafting.display.SlotDisplay> displays = new ArrayList<>();
        for (net.minecraft.world.item.crafting.Ingredient ingredient : holder.value().data().slotIngredients()) {
            displays.add(ingredient.display());
        }
        return displays;
    }
*///?}

    /** Whether a recipe object ({@link PlatformCrafting#recipeObjects}) is a step JEI can show: every slot has items. */
    static boolean handles(Object recipe) {
        //? if >=1.21 {
        /*Object value = recipe instanceof RecipeHolder<?> holder ? holder.value() : recipe;
        *///?} else {
        Object value = recipe;
        //?}
        return value instanceof PlatformCrafting.GridRecipe<?> grid && grid.data() instanceof FoodCrafting step
            && WorkbenchPage.of("", step).complete();
    }

    /** Nine grid slots in JEI's crafting positions (the units first, row by row), then the result. */
    private static void layout(WorkbenchPage page, IRecipeLayoutBuilder builder, ICraftingGridHelper grid) {
        List<WorkbenchPage.Slot> slots = page.slots();
        for (int i = 0; i < 9; i++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, (i % 3) * 18 + 1, (i / 3) * 18 + 1);
            if (i >= slots.size()) {
                continue;
            }
            WorkbenchPage.Slot unit = slots.get(i);
            slot.addItemStacks(page.items(unit));
            List<FluidAmount> fluids = page.fluids(unit);
            if (!fluids.isEmpty()) {
                List<FluidStack> stacks = new ArrayList<>();
                long capacity = 1;
                for (FluidAmount fluid : fluids) {
                    stacks.add(PlatformFluids.toStack(fluid));
                    capacity = Math.max(capacity, fluid.amount());
                }
                slot.addIngredients(JeiSupport.fluidType(), stacks).setFluidRenderer(capacity, false, 16, 16);
            }
            slot.addRichTooltipCallback((view, tooltip) -> tooltip.addAll(page.slotLines(unit)));
        }
        grid.createAndSetOutputs(builder, List.of(page.result()))
            .addRichTooltipCallback((view, tooltip) -> tooltip.addAll(page.resultLines()));
        builder.setShapeless();
    }
}
