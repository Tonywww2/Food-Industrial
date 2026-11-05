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
 * sets more than the crude bound of a material its entries belong to (additives/10 §1.6). None are
 * shipped yet (2026-11-04): external items count at the external default.
 */
public final class BundledPurityRules {
    private static final Map<String, Supplier<JsonObject>> RULES = new LinkedHashMap<>();
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
     * Sets how data generation finds the materials of a rule entry ({@code #tag} or id; {@code true}
     * for fluids): the materials whose tags list the entry on the running node.
     */
    public static void setEntryMaterials(BiFunction<String, Boolean, List<String>> resolver) {
        entryMaterials = resolver;
    }

    static void bootstrap() {
        PlatformDatagen.register("purity rules", sink -> RULES.forEach((name, rule) -> {
            JsonObject json = rule.get();
            PurityRules.checkBundled(PurityRules.parse(FoodIndustrial.MOD_ID + ":" + name, json), entryMaterials);
            sink.json(PlatformDatagen.Kind.DATA, FoodIndustrial.MOD_ID, "foodindustrial/purity_rules/" + name, json);
        }));
    }
}
