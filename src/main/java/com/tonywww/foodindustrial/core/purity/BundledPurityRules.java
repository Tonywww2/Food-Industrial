package com.tonywww.foodindustrial.core.purity;

import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * The external purity rules shipped with the mod ({@code data/foodindustrial/foodindustrial/purity_rules/}).
 * Integrations add their rules here (I25, I26); data generation parses each one and fails when a rule
 * sets more than the crude bound of a material its entries belong to (additives/10 §1.6); entries of
 * mods missing from the data run cannot be checked there, so their generators check the materials
 * themselves (I25). The integrations ship one rule each for the chemicals their mods make (2026-11-11).
 */
public final class BundledPurityRules {
    private static final Map<String, Supplier<JsonObject>> RULES = new LinkedHashMap<>();
    /** Materials of entries the data run cannot place by its tags (other mods' items and fluids, I25). */
    private static final Map<String, List<String>> KNOWN = new LinkedHashMap<>();
    private static BiFunction<String, Boolean, List<String>> entryMaterials = (entry, fluid) -> List.of();

    private BundledPurityRules() {
    }

    /**
     * Adds a rule file. The supplier runs during data generation, so it may use node conventions such
     * as {@link PlatformDatagen#commonTagNamespace()}.
     */
    public static void add(String name, Supplier<JsonObject> rule) {
        if (RULES.putIfAbsent(name, rule) != null) {
            throw new IllegalArgumentException("Duplicate bundled purity rule " + name);
        }
    }

    /**
     * Declares the material of another mod's entry; the check of the crude bound uses it when the data
     * run's tags do not list the entry ({@code fluid}: a fluid entry; I25, 2026-11-11).
     */
    public static void knownEntry(String entry, boolean fluid, String material) {
        KNOWN.computeIfAbsent((fluid ? "fluid " : "item ") + entry, key -> new java.util.ArrayList<>()).add(material);
    }

    /**
     * Sets how data generation finds the materials of a rule entry ({@code #tag} or id; {@code true}
     * for fluids): the materials whose tags list the entry on the running node.
     */
    public static void setEntryMaterials(BiFunction<String, Boolean, List<String>> resolver) {
        entryMaterials = resolver;
    }

    static void bootstrap() {
        PlatformDatagen.register("purity rules", sink -> RULES.forEach((name, rule) -> {
            JsonObject json = rule.get();
            PurityRules.checkBundled(PurityRules.parse(FoodIndustrial.MOD_ID + ":" + name, json), (entry, fluid) -> {
                List<String> found = entryMaterials.apply(entry, fluid);
                return found.isEmpty() ? KNOWN.getOrDefault((fluid ? "fluid " : "item ") + entry, List.of()) : found;
            });
            sink.json(PlatformDatagen.Kind.DATA, FoodIndustrial.MOD_ID, "foodindustrial/purity_rules/" + name, json);
        }));
    }
}
