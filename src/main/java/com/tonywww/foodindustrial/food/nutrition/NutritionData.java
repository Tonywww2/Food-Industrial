package com.tonywww.foodindustrial.food.nutrition;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The data in effect for one item: one serving's grams and components (foods/03 §2).
 *
 * @param servingG   grams in one serving
 * @param amounts    per serving, by {@link Component} ordinal (do not modify)
 * @param missing    mask of the components the file did not name (counted as 0)
 * @param impurity   impurity units per serving (foods/02 §5.1), 0 for none
 * @param energyKcal energy per serving, or {@code null}; for the report only
 * @param source     file id the data comes from ({@code null} on the client)
 * @param priority   priority of that file
 */
public record NutritionData(double servingG, double[] amounts, int missing, double impurity, @Nullable Double energyKcal,
                            @Nullable String source, int priority) {
    public double amount(Component component) {
        return amounts[component.ordinal()];
    }

    /** Whether any component is above 0. */
    public boolean anyAmount() {
        for (double amount : amounts) {
            if (amount > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NutritionData data && servingG == data.servingG && Arrays.equals(amounts, data.amounts)
            && missing == data.missing && impurity == data.impurity && java.util.Objects.equals(energyKcal, data.energyKcal)
            && java.util.Objects.equals(source, data.source) && priority == data.priority;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(servingG, Arrays.hashCode(amounts), missing, source, priority);
    }

    /**
     * Resolves loaded files into the data per item (foods/03 §2): files of mods that are not loaded are skipped
     * quietly; unknown item ids are warned about and ignored; when several files name one item, the higher
     * {@code priority} wins, and on equal priority the file whose id sorts last wins, with a warning.
     *
     * @param files     the files, in any order
     * @param modLoaded whether a mod is loaded
     * @param items     the item of an id, if it exists
     * @param warn      receives warnings
     */
    public static <I> Map<I, NutritionData> resolve(List<NutritionFile> files, Predicate<String> modLoaded,
                                                    Function<String, Optional<I>> items, Consumer<String> warn) {
        TreeMap<String, NutritionFile> sorted = new TreeMap<>();
        files.forEach(file -> sorted.put(file.source(), file));
        Map<I, NutritionData> result = new java.util.LinkedHashMap<>();
        for (NutritionFile file : sorted.values()) {
            if (!modLoaded.test(file.mod())) {
                continue;
            }
            for (NutritionFile.Entry entry : file.entries()) {
                NutritionData data = new NutritionData(entry.servingG(), entry.amounts(), entry.missing(), entry.impurity(),
                    entry.energyKcal().orElse(null), file.source(), file.priority());
                for (String id : entry.items()) {
                    Optional<I> item = items.apply(id);
                    if (item.isEmpty()) {
                        warn.accept("Nutrition data " + file.source() + ": unknown item " + id + ", ignored");
                        continue;
                    }
                    NutritionData current = result.get(item.get());
                    if (current != null) {
                        if (current.priority() > file.priority()) {
                            continue;
                        }
                        if (current.priority() == file.priority()) {
                            warn.accept("Nutrition data for " + id + " in both " + current.source() + " and " + file.source()
                                + " at priority " + file.priority() + "; " + file.source() + " is used");
                        }
                    }
                    result.put(item.get(), data);
                }
            }
        }
        return result;
    }
}
