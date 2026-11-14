package com.tonywww.foodindustrial.integration;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * One of this mod's recipes as another mod's machine runs it (I25): inputs as written (item ingredients
 * already in the node's JSON form), outputs with the fixed purity of the integration's tier.
 *
 * @param recipeType   this mod's recipe type id ({@code boiling}, …)
 * @param items        item inputs
 * @param fluids       fluid inputs (no purity threshold: other mods cannot check one)
 * @param itemOutputs  item outputs made at this tier
 * @param fluidOutputs fluid outputs made at this tier
 * @param time         ticks of the recipe
 * @param energy       FE of one run (FE per tick × ticks)
 * @param heated       the recipe needs heat ({@code temperature.heated})
 * @param cooled       the recipe needs cooling ({@code temperature.cooling})
 */
public record ExternalRecipe(String recipeType, List<ItemIn> items, List<FluidIn> fluids, List<ItemOut> itemOutputs, List<FluidOut> fluidOutputs,
                             int time, long energy, boolean heated, boolean cooled) {
    /** Item input: the ingredient JSON, the count and whether the ingredient checks purity. */
    public record ItemIn(JsonElement ingredient, int count, boolean purity) {
    }

    /** Fluid input: a fluid tag or a fluid id and the amount in mB. */
    public record FluidIn(@Nullable String tag, @Nullable String fluid, int amount) {
    }

    /** Item output: id, count, chance per run and purity byte ({@code Purity.NONE} for none). */
    public record ItemOut(String id, int count, double chance, int purity) {
    }

    /** Fluid output: id, amount in mB and purity byte ({@code Purity.NONE} for none). */
    public record FluidOut(String id, int amount, int purity) {
    }

    /** This recipe with other item inputs. */
    public ExternalRecipe withItems(List<ItemIn> newItems) {
        return new ExternalRecipe(recipeType, newItems, fluids, itemOutputs, fluidOutputs, time, energy, heated, cooled);
    }

    /** This recipe with other fluid inputs. */
    public ExternalRecipe withFluids(List<FluidIn> newFluids) {
        return new ExternalRecipe(recipeType, items, newFluids, itemOutputs, fluidOutputs, time, energy, heated, cooled);
    }

    /** Total count of item inputs, each counted as often as it is used. */
    public int itemCount() {
        return items.stream().mapToInt(ItemIn::count).sum();
    }
}
