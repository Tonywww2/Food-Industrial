package com.tonywww.foodindustrial.material;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.TextChecksAccess;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialTableTest {
    private static final List<MaterialDef> MATERIALS = MaterialTable.load();

    @Test
    void masterTableIsComplete() {
        Set<String> ids = new HashSet<>();
        for (MaterialDef def : MATERIALS) {
            assertTrue(ids.add(def.id()), "duplicate " + def.id());
            assertTrue(def.id().matches("[a-z0-9_]+"), def.id());
            if (def.isFluid()) {
                assertNotEquals(0, def.color(), "fluid colour of " + def.id());
            }
            // additives/10 §1.1, §1.4 (2026-11-05): cultures are K11 and only cultures; enzymes and consumables carry no purity
            if (def.category().equals("culture")) {
                assertEquals("foodindustrial:k11_culture", def.purity().get("category").getAsString(), "cultures are K11: " + def.id());
            }
            boolean k11 = def.purity() != null && def.purity().get("category").getAsString().equals("foodindustrial:k11_culture");
            assertEquals(def.category().equals("culture"), k11, "K11 holds only cultures: " + def.id());
            if (!Set.of("chemical", "culture").contains(def.category())) {
                assertNull(def.purity(), "only chemicals and cultures have a purity category: " + def.id());
            }
            boolean k0 = def.purity() != null && def.purity().get("category").getAsString().equals("foodindustrial:k0_raw");
            assertEquals(def.purity() != null && (!k0 || def.purity().has("source_purity")), def.bearer(), "bearer of " + def.id());
        }
        assertFalse(ids.contains("water"), "water maps to vanilla");
        assertTrue(ids.containsAll(List.of("salt", "sulfuric_acid", "carbon_dioxide", "latex", "acetobacter", "beta_amylase", "iron_catalyst")));
    }

    @Test
    void knownRows() {
        MaterialDef salt = find("salt");
        assertEquals("氯化钠（盐）", salt.nameZh());
        assertEquals("氯化钠", salt.professionalZh());
        assertEquals("盐", salt.commonZh());
        assertEquals("NaCl", salt.formula());
        assertEquals("chemicals/salt", salt.tag());
        assertEquals(MaterialDef.State.GAS, find("carbon_dioxide").state());
        assertEquals("(C6H10O5)n", find("starch").formula());
        assertEquals(null, find("pectin").formula());
        assertFalse(find("latex").bearer());
        assertFalse(find("nickel_catalyst").bearer());
        assertTrue(find("gum_base").bearer(), "compound products are K12");
        assertEquals("foodindustrial:k12_compound", find("gum_base").purity().get("category").getAsString());
        assertFalse(find("brine").bearer(), "K0 without source purity");
        // carry no purity (additives/10 §1.1): mixtures, enzymes and enzyme sources, distilled water, consumables
        for (String id : List.of("bittern", "resin", "soda_lye", "fish_liver_oil", "enzymes", "lysozyme", "pancreatic_extract",
            "beta_amylase", "distilled_water", "expression_vector")) {
            assertNull(find(id).purity(), id);
            assertFalse(find(id).bearer(), id);
        }
        assertEquals("consumable", find("expression_vector").category());
        assertEquals("foodindustrial:k1_inorganic", find("sodium_bisulfite").purity().get("category").getAsString());
        assertEquals(83, find("salt").purity().get("source_purity").getAsInt());
        assertNotNull(find("vpo_catalyst").formula());
    }

    @Test
    void everyNamePassesTheTextStandard() {
        List<String> problems = new java.util.ArrayList<>();
        for (MaterialDef def : MATERIALS) {
            TextChecksAccess.check(Lang.item(def.id()), def.nameZh(), def.nameEn(), problems);
            TextChecksAccess.check(Lang.description(def.id()), def.professionalZh(), def.professionalEn(), problems);
        }
        assertEquals(List.of(), problems);
    }

    private static MaterialDef find(String id) {
        return MATERIALS.stream().filter(def -> def.id().equals(id)).findFirst().orElseThrow();
    }
}
