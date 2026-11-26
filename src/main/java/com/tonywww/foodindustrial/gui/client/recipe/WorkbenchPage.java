package com.tonywww.foodindustrial.gui.client.recipe;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.food.FoodCrafting;
import com.tonywww.foodindustrial.gui.RecipeText;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A food-line crafting-table step ({@link FoodCrafting}, I38) as JEI's crafting page shows it (2026-11-25, the
 * vanilla crafting category, so AE2 and other mods treat it as a crafting recipe): one grid slot per unit, row by row
 * (an ingredient by its count; a fluid as the bottles and buckets that hold enough of it, with the fluid itself; then
 * the knives and molds), and the result. The process, the second product and the additive slots are told in tooltips.
 * Loader- and JEI-independent, like {@link RecipePage}.
 */
public final class WorkbenchPage {
    /** What a grid slot holds. */
    public enum Kind {
        ITEM, FLUID, TOOL
    }

    /**
     * A grid slot, in grid order.
     *
     * @param index the recipe input (or tool) it stands for
     */
    public record Slot(Kind kind, int index) {
    }

    private final String id;
    private final FoodCrafting recipe;
    private final List<Slot> slots = new ArrayList<>();

    private WorkbenchPage(String id, FoodCrafting recipe) {
        this.id = id;
        this.recipe = recipe;
        for (int i = 0; i < recipe.ingredients().size(); i++) {
            ProcessInput input = recipe.ingredients().get(i);
            if (input instanceof ProcessInput.ItemInput item) {
                for (int n = 0; n < item.count(); n++) {
                    slots.add(new Slot(Kind.ITEM, i));
                }
            } else {
                slots.add(new Slot(Kind.FLUID, i));
            }
        }
        for (int i = 0; i < recipe.tools().size(); i++) {
            slots.add(new Slot(Kind.TOOL, i));
        }
    }

    public static WorkbenchPage of(String id, FoodCrafting recipe) {
        return new WorkbenchPage(id, recipe);
    }

    public String id() {
        return id;
    }

    /** The grid slots in use, row by row from the top left (at most nine). */
    public List<Slot> slots() {
        return slots;
    }

    /** Whether every slot has something to show (as {@link RecipePage#complete}). */
    public boolean complete() {
        for (Slot slot : slots) {
            if (items(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** Stacks shown in a grid slot: one item each; for a fluid, the bottles and buckets that hold enough of it. */
    public List<ItemStack> items(Slot slot) {
        return switch (slot.kind) {
            case ITEM, FLUID -> FoodCrafting.unitStacks(recipe.ingredients().get(slot.index));
            case TOOL -> PlatformIngredients.displayStacks(recipe.tools().get(slot.index));
        };
    }

    /** The fluids a container slot also shows, so looking the fluid up finds the step; empty for other slots. */
    public List<FluidAmount> fluids(Slot slot) {
        return slot.kind == Kind.FLUID ? ((ProcessInput.FluidInput) recipe.ingredients().get(slot.index)).fluid().displayFluids() : List.of();
    }

    public ItemStack result() {
        return recipe.result().stack(recipe.result().amount());
    }

    // ---- tooltips ----

    /** Lines JEI adds below the ingredient tooltip of a grid slot. */
    public List<Component> slotLines(Slot slot) {
        List<Component> lines = new ArrayList<>();
        switch (slot.kind) {
            case ITEM -> RecipePage.inputLines(recipe.ingredients().get(slot.index), lines);
            case FLUID -> {
                ProcessInput input = recipe.ingredients().get(slot.index);
                RecipePage.inputLines(input, lines);
                lines.add(Component.translatable(RecipeText.key("container_back"),
                    String.valueOf(((ProcessInput.FluidInput) input).amount())).withStyle(ChatFormatting.GRAY));
            }
            case TOOL -> lines.add(Component.translatable(RecipeText.key("tool_kept")).withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    /** Lines below the result's tooltip: the process, the second product, the additive slots. */
    public List<Component> resultLines() {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(RecipeText.key("workbench_process"), Component.translatable(Lang.jeiCategory(recipe.process().id())))
            .withStyle(ChatFormatting.GRAY));
        recipe.byproduct().ifPresent(output -> {
            ItemStack second = output.stack(output.amount());
            lines.add(Component.translatable(RecipeText.key("second_kept"), second.getHoverName(), String.valueOf(second.getCount()))
                .withStyle(ChatFormatting.GRAY));
        });
        if (recipe.quality().isPresent()) {
            lines.add(Component.translatable(RecipeText.key("additives")).withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }
}
