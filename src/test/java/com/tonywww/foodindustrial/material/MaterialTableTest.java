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
            if (Set.of("culture", "enzyme").contains(def.category())) {
                assertEquals("foodindustrial:k11_culture", def.purity().get("category").getAsString(), "cultures and enzymes are K11");
            }
            if (!Set.of("chemical", "culture", "enzyme").contains(def.category())) {
                assertNull(def.purity(), "only chemicals, cultures and enzymes have a purity category: " + def.id());
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
        assertFalse(find("gum_base").bearer(), "compound products carry no purity");
        assertTrue(find("bittern").bearer(), "K0 with source purity shows its content");
        assertTrue(find("resin").bearer());
        assertFalse(find("soda_lye").bearer(), "K0 without source purity");
        assertTrue(find("fish_liver_oil").bearer());
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
