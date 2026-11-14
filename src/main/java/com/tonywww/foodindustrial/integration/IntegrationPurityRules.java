package com.tonywww.foodindustrial.integration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.purity.BundledPurityRules;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.platform.compat.integration.PlatformIntegrationFormats;

import java.util.List;

/**
 * External purity rules of the integrations (additives/10 §1.6, I25, 2026-11-11): the chemicals another
 * mod makes (Mekanism's salt and sulfuric acid, NuclearCraft's acids and gases, …) count as crude, the
 * highest a bundled rule may set, instead of the poor external default. Only entries whose material
 * bears purity and has a crude grade are written; the data run has none of these mods, so the materials
 * are listed here rather than read from tags.
 */
final class IntegrationPurityRules {
    /** Entry of another mod: id, fluid or item, and the material it belongs to. */
    private record Entry(String id, boolean fluid, String material) {
    }

    private static Entry item(String id, String material) {
        return new Entry(id, false, material);
    }

    private static Entry fluid(String id, String material) {
        return new Entry(id, true, material);
    }

    private IntegrationPurityRules() {
    }

    static void bootstrap() {
        add(Integrations.MEKANISM, List.of(
            item("mekanism:salt", "salt"), item("mekanism:dust_sulfur", "sulfur"),
            fluid("mekanism:brine", "brine"), fluid("mekanism:chlorine", "chlorine"), fluid("mekanism:hydrogen", "hydrogen"),
            fluid("mekanism:hydrogen_chloride", "hydrogen_chloride"), fluid("mekanism:oxygen", "oxygen"),
            fluid("mekanism:sulfur_dioxide", "sulfur_dioxide"), fluid("mekanism:sulfur_trioxide", "sulfur_trioxide"),
            fluid("mekanism:sulfuric_acid", "sulfuric_acid")));
        add(Integrations.NUCLEARCRAFT, List.of(
            item("nuclearcraft:salt", "salt"), item("nuclearcraft:sulfur_dust", "sulfur"), item("nuclearcraft:iodine_dust", "iodine"),
            item("nuclearcraft:manganese_dioxide_dust", "manganese_dioxide"), item("nuclearcraft:potassium_hydroxide_dust", "potassium_hydroxide"),
            item("nuclearcraft:potassium_iodide_dust", "potassium_iodide"), item("nuclearcraft:sodium_dust", "sodium"),
            item("nuclearcraft:sodium_hydroxide_dust", "sodium_hydroxide"), item("nuclearcraft:silicon_gem", "silicon"),
            fluid("nuclearcraft:ammonia", "ammonia"), fluid("nuclearcraft:carbon_dioxide", "carbon_dioxide"),
            fluid("nuclearcraft:carbon_monoxide", "carbon_monoxide"), fluid("nuclearcraft:chlorine", "chlorine"),
            fluid("nuclearcraft:ethanol", "ethanol"), fluid("nuclearcraft:hydrochloric_acid", "hydrochloric_acid"),
            fluid("nuclearcraft:hydrogen", "hydrogen"), fluid("nuclearcraft:hydrogen_chloride", "hydrogen_chloride"),
            fluid("nuclearcraft:methanol", "methanol"), fluid("nuclearcraft:nitric_acid", "nitric_acid"),
            fluid("nuclearcraft:nitric_oxide", "nitric_oxide"), fluid("nuclearcraft:nitrogen", "nitrogen"),
            fluid("nuclearcraft:nitrogen_dioxide", "nitrogen_dioxide"), fluid("nuclearcraft:oxygen", "oxygen"),
            fluid("nuclearcraft:sulfur_dioxide", "sulfur_dioxide"), fluid("nuclearcraft:sulfur_trioxide", "sulfur_trioxide"),
            fluid("nuclearcraft:sulfuric_acid", "sulfuric_acid")));
        add(Integrations.PNEUMATICCRAFT, List.of(
            fluid("pneumaticcraft:oil", "crude_oil"), fluid("pneumaticcraft:ethanol", "ethanol")));
    }

    private static void add(Integrations.Mod mod, List<Entry> entries) {
        if (!PlatformIntegrationFormats.available(mod.id())) {
            return;
        }
        BundledPurityRules.add("integration/" + mod.id(), () -> {
            JsonArray items = new JsonArray();
            JsonArray fluids = new JsonArray();
            for (Entry entry : entries) {
                PurityData.Material material = PurityData.current().material(com.tonywww.foodindustrial.FoodIndustrial.MOD_ID + ":" + entry.material());
                if (material == null || !material.bears() || !material.graded() || material.gradeValue(PurityGrade.CRUDE) == Purity.NONE) {
                    continue;
                }
                (entry.fluid() ? fluids : items).add(entry.id());
                BundledPurityRules.knownEntry(entry.id(), entry.fluid(), com.tonywww.foodindustrial.FoodIndustrial.MOD_ID + ":" + entry.material());
            }
            JsonObject rule = new JsonObject();
            rule.add("items", items);
            rule.add("fluids", fluids);
            rule.addProperty("purity", "crude");
            return rule;
        });
    }
}
