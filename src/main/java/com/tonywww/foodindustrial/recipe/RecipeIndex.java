package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recipe lookup (machines/02 §3, §6.3 rule 8, §6.4 rule 13). Built once after each reload, on the
 * first query: recipes by type and by every item or fluid their first input accepts, each with its
 * base tier and the devices that can run it. Cleared when tags are reloaded (recipes always reload
 * with them); never scans all recipes at run time.
 */
public final class RecipeIndex {
    @Nullable
    private static volatile RecipeIndex current;

    /**
     * A loaded recipe.
     *
     * @param baseTier lowest tier among the devices that can run it (additives/10 §2.1 step 3), or
     *                 {@code null} when no registered device can
     * @param runners  devices that can run it, lowest tier first
     */
    public record Entry(String id, ProcessRecipe recipe, @Nullable Tier baseTier, List<DeviceSpec> runners) {
        public boolean runnable() {
            return baseTier != null;
        }

        /** Whether {@code device} is one of the runners. */
        public boolean runsOn(DeviceSpec device) {
            for (DeviceSpec runner : runners) {
                if (runner == device) {
                    return true;
                }
            }
            return false;
        }
    }

    /** A recipe found for a device's contents. */
    public record Found(Entry entry, ProcessRecipe.Match match) {
    }

    private final Map<String, Entry> byId = new LinkedHashMap<>();
    private final Map<ProcessType, List<Entry>> byType = new IdentityHashMap<>();
    private final Map<ProcessType, Map<Item, List<Entry>>> byItem = new IdentityHashMap<>();
    private final Map<ProcessType, Map<Fluid, List<Entry>>> byFluid = new IdentityHashMap<>();
    private final List<String> unrunnable = new ArrayList<>();
    private final Map<ProcessType, java.util.Set<Item>> inputItems = new IdentityHashMap<>();
    private final Map<ProcessType, java.util.Set<Fluid>> inputFluids = new IdentityHashMap<>();

    private RecipeIndex() {
    }

    static void bootstrap() {
        PlatformEvents.onTagsUpdated(RecipeIndex::invalidate);
    }

    /** Drops the index; the next query rebuilds it. */
    public static void invalidate() {
        current = null;
    }

    /** The index of the loaded recipes (server side). */
    public static RecipeIndex get(Level level) {
        RecipeIndex index = current;
        if (index == null) {
            index = build(level);
            current = index;
        }
        return index;
    }

    private static RecipeIndex build(Level level) {
        RecipeIndex index = new RecipeIndex();
        for (ProcessType type : ProcessTypes.recipeTypes()) {
            List<Entry> entries = new ArrayList<>();
            Map<Item, List<Entry>> items = new HashMap<>();
            Map<Fluid, List<Entry>> fluids = new HashMap<>();
            java.util.Set<Item> anyItems = new java.util.HashSet<>();
            java.util.Set<Fluid> anyFluids = new java.util.HashSet<>();
            for (PlatformRecipes.Loaded<ProcessRecipe> loaded : PlatformRecipes.loaded(level, type.kind())) {
                ProcessRecipe recipe = loaded.data();
                List<DeviceSpec> runners = DeviceSpec.runnersOf(recipe);
                Entry entry = new Entry(loaded.id(), recipe, runners.isEmpty() ? null : runners.get(0).tier(), List.copyOf(runners));
                entries.add(entry);
                index.byId.put(entry.id, entry);
                if (!entry.runnable()) {
                    index.unrunnable.add(entry.id);
                }
                for (ProcessInput input : recipe.inputs()) {
                    if (input instanceof ProcessInput.ItemInput item) {
                        anyItems.addAll(PlatformIngredients.candidateItems(item.ingredient()));
                    } else {
                        anyFluids.addAll(((ProcessInput.FluidInput) input).fluid().fluids());
                    }
                }
                ProcessInput first = recipe.inputs().get(0);
                if (first instanceof ProcessInput.ItemInput item) {
                    for (Item candidate : PlatformIngredients.candidateItems(item.ingredient())) {
                        items.computeIfAbsent(candidate, key -> new ArrayList<>()).add(entry);
                    }
                } else {
                    for (Fluid candidate : ((ProcessInput.FluidInput) first).fluid().fluids()) {
                        fluids.computeIfAbsent(candidate, key -> new ArrayList<>()).add(entry);
                    }
                }
            }
            index.byType.put(type, List.copyOf(entries));
            index.byItem.put(type, freeze(items));
            index.byFluid.put(type, freeze(fluids));
            index.inputItems.put(type, java.util.Set.copyOf(anyItems));
            index.inputFluids.put(type, java.util.Set.copyOf(anyFluids));
        }
        if (!index.unrunnable.isEmpty()) {
            FoodIndustrial.LOGGER.warn("{} recipes have no device that can run them: {}", index.unrunnable.size(),
                index.unrunnable.size() > 20 ? index.unrunnable.subList(0, 20) + " …" : index.unrunnable);
        }
        return index;
    }

    private static <K> Map<K, List<Entry>> freeze(Map<K, List<Entry>> map) {
        Map<K, List<Entry>> frozen = new HashMap<>();
        map.forEach((key, list) -> frozen.put(key, List.copyOf(list)));
        return frozen;
    }

    @Nullable
    public Entry byId(String id) {
        return byId.get(id);
    }

    /** Recipes of {@code type} in load order. */
    public List<Entry> all(ProcessType type) {
        return byType.getOrDefault(type, List.of());
    }

    /** Every loaded recipe. */
    public Iterable<Entry> all() {
        return Collections.unmodifiableCollection(byId.values());
    }

    /** Recipes of {@code type} whose first input accepts {@code item}. */
    public List<Entry> candidates(ProcessType type, Item item) {
        Map<Item, List<Entry>> map = byItem.get(type);
        return map == null ? List.of() : map.getOrDefault(item, List.of());
    }

    /** Recipes of {@code type} whose first input accepts {@code fluid}. */
    public List<Entry> candidates(ProcessType type, Fluid fluid) {
        Map<Fluid, List<Entry>> map = byFluid.get(type);
        return map == null ? List.of() : map.getOrDefault(fluid, List.of());
    }

    /** Whether any recipe of {@code type} has an input that accepts {@code item} (automatic input). */
    public boolean usesItem(ProcessType type, Item item) {
        java.util.Set<Item> set = inputItems.get(type);
        return set != null && set.contains(item);
    }

    /** Whether any recipe of {@code type} has an input that accepts {@code fluid}. */
    public boolean usesFluid(ProcessType type, Fluid fluid) {
        java.util.Set<Fluid> set = inputFluids.get(type);
        return set != null && set.contains(fluid);
    }

    /** Ids of recipes no registered device can run. */
    public List<String> unrunnable() {
        return Collections.unmodifiableList(unrunnable);
    }

    /**
     * First recipe the device can run with its current capabilities whose inputs are all present.
     * Call only when the contents changed (machines/02 §6.3 rule 8).
     *
     * @param current capabilities right now (mode and modules), within {@link DeviceSpec#capabilities()}
     */
    @Nullable
    public Found find(DeviceSpec device, Capabilities current, List<ItemStack> items, List<FluidAmount> fluids) {
        for (ProcessType type : ProcessTypes.recipeTypes()) {
            if (!device.types().contains(type)) {
                continue;
            }
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    Found found = first(candidates(type, stack.getItem()), device, current, items, fluids);
                    if (found != null) {
                        return found;
                    }
                }
            }
            for (FluidAmount fluid : fluids) {
                if (!fluid.isEmpty()) {
                    Found found = first(candidates(type, fluid.fluid()), device, current, items, fluids);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static Found first(List<Entry> entries, DeviceSpec device, Capabilities current, List<ItemStack> items, List<FluidAmount> fluids) {
        for (Entry entry : entries) {
            if (entry.runsOn(device) && current.covers(entry.recipe.required())) {
                ProcessRecipe.Match match = entry.recipe.match(items, fluids);
                if (match != null) {
                    return new Found(entry, match);
                }
            }
        }
        return null;
    }
}
