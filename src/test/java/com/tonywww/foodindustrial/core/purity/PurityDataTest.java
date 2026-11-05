package com.tonywww.foodindustrial.core.purity;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurityDataTest {
    private static final String SALT = "foodindustrial:salt";
    private static final String SUGAR = "foodindustrial:sucrose";
    private static final String ACETIC_ACID = "foodindustrial:acetic_acid";
    private static final String BITTERN = "foodindustrial:bittern";

    @BeforeAll
    static void registerMaterials() {
        if (PurityData.builtin().materials().isEmpty()) {
            for (MaterialDef def : MaterialTable.load()) {
                if (def.purity() != null) {
                    PurityData.registerBuiltin("foodindustrial:" + def.id(), def.purity());
                }
            }
        }
    }

    private static PurityData.Material material(String id) {
        PurityData.Material material = PurityData.current().material(id);
        assertTrue(material != null, id);
        return material;
    }

    private static String display(int b) {
        return PurityScale.format(b);
    }

    @Test
    void builtinDataLoadsWithoutErrors() {
        List<String> errors = new ArrayList<>();
        Map<String, JsonElement> materials = new LinkedHashMap<>();
        int withData = 0;
        for (MaterialDef def : MaterialTable.load()) {
            if (def.purity() != null) {
                materials.put("foodindustrial:" + def.id(), def.purity());
                withData++;
            }
        }
        PurityData data = PurityData.build(PurityData.builtinCategories(), materials, errors::add);
        assertEquals(List.of(), errors);
        assertEquals(12, data.categories().size());
        assertEquals(withData, data.materials().size());
        for (MaterialDef def : MaterialTable.load()) {
            PurityData.Material material = data.material("foodindustrial:" + def.id());
            boolean bearer = material != null && (material.graded() || material.sourceByte() != PurityData.NONE);
            assertEquals(def.bearer(), bearer, "bearer of " + def.id());
        }
    }

    @Test
    void externalDefaults() {
        // additives/10 §1.6
        assertEquals("74", display(material(SUGAR).missing()));
        assertEquals("82", display(material(SALT).missing()));
        assertEquals("1.8", display(material(ACETIC_ACID).missing()), "lowest grade is food 3.5%");
    }

    @Test
    void ruleGradeNames() {
        assertEquals("85", display(new PurityRules.Value(PurityGrade.CRUDE, 0).resolve(SUGAR)));
        assertEquals("74", display(new PurityRules.Value(PurityGrade.POOR, 0).resolve(SUGAR)), "poor = external default");
        assertEquals(PurityData.NONE, new PurityRules.Value(PurityGrade.CRUDE, 0).resolve(ACETIC_ACID), "no crude grade");
        assertEquals(PurityData.NONE, new PurityRules.Value(PurityGrade.CRUDE, 0).resolve(null), "no material");
        assertEquals(PurityScale.ofPercent(33), new PurityRules.Value(null, 33).resolve(null));
    }

    @Test
    void gradesFollowMaterialBounds() {
        PurityData.Material salt = material(SALT);
        // salt overrides (10 §1.5): 90 / 95 / 97 / 99.8
        assertEquals(Optional.of(PurityGrade.POOR), salt.gradeOf(PurityScale.ofPercent(83)));
        assertEquals(Optional.of(PurityGrade.CRUDE), salt.gradeOf(25));
        assertEquals(Optional.of(PurityGrade.REFINED), salt.gradeOf(PurityScale.ofPercent(95)));
        assertEquals(Optional.of(PurityGrade.FOOD), salt.gradeOf(39));
        assertEquals(Optional.of(PurityGrade.LAB), salt.gradeOf(PurityScale.ofPercent(99.8)));
        assertEquals(Optional.of(PurityGrade.REFINED), salt.gradeOf(PurityScale.ofPercent(97) - 1));
        assertEquals("83", display(salt.sourceByte()));

        PurityData.Material acid = material(ACETIC_ACID);
        assertTrue(acid.lowerBound(PurityGrade.CRUDE).isEmpty());
        assertEquals(Optional.of(PurityGrade.POOR), acid.gradeOf(PurityScale.ofPercent(3)));
        assertEquals(Optional.of(PurityGrade.FOOD), acid.gradeOf(PurityScale.ofPercent(4)), "vinegar is food grade");

        PurityData.Material bittern = material(BITTERN);
        assertFalse(bittern.graded());
        assertEquals(Optional.empty(), bittern.gradeOf(10));
        assertEquals("7.3", display(bittern.missing()), "K0 content is the source purity");
        assertEquals(List.of(bittern.sourceByte()), bittern.listedValues());
    }

    @Test
    void listedValuesAreDefaultThenBounds() {
        PurityData.Material salt = material(SALT);
        List<Integer> values = salt.listedValues();
        assertEquals(List.of(salt.missing(), PurityScale.ofPercent(90), PurityScale.ofPercent(95), PurityScale.ofPercent(97),
            PurityScale.ofPercent(99.8)), values);
        assertEquals(List.of(material(ACETIC_ACID).missing(), PurityScale.ofPercent(3.5), PurityScale.ofPercent(99.8)),
            material(ACETIC_ACID).listedValues());
    }

    @Test
    void thresholds() {
        PurityThreshold refined = PurityThreshold.resolve(Either.left("refined"), null, SALT);
        assertEquals(PurityScale.ofPercent(95), refined.value());
        assertEquals(SALT, refined.material());
        assertNull(refined.explicitMaterial());
        PurityThreshold explicit = PurityThreshold.resolve(Either.left("food"), "salt", null);
        assertEquals(PurityScale.ofPercent(97), explicit.value(), "material without namespace");
        assertEquals(SALT, explicit.explicitMaterial());
        assertEquals(PurityScale.ofPercent(33.3), PurityThreshold.resolve(Either.right(33.3), null, null).value(), "any percentage");
        assertEquals(PurityScale.ofPercent(12), PurityThreshold.resolve(JsonParser.parseString("12"), null, null).value());
        assertThrows(IllegalArgumentException.class, () -> PurityThreshold.resolve(Either.left("crude"), null, ACETIC_ACID), "no crude");
        assertThrows(IllegalArgumentException.class, () -> PurityThreshold.resolve(Either.left("food"), null, null), "no material");
        assertThrows(IllegalArgumentException.class, () -> PurityThreshold.resolve(Either.left("food"), null, BITTERN), "K0 has no grades");
        assertThrows(IllegalArgumentException.class, () -> PurityThreshold.resolve(Either.left("poor"), null, SALT));
        assertThrows(IllegalArgumentException.class, () -> PurityThreshold.resolve(Either.right(101.0), null, null));
    }

    @Test
    void validation() {
        Map<String, JsonElement> categories = new LinkedHashMap<>();
        categories.put("test:ok", JsonParser.parseString("{\"grades\":{\"crude\":50,\"refined\":60,\"food\":70,\"lab\":99.9}}"));
        categories.put("test:decreasing", JsonParser.parseString("{\"grades\":{\"crude\":50,\"refined\":40}}"));
        categories.put("test:decimals", JsonParser.parseString("{\"grades\":{\"crude\":50.5}}"));
        categories.put("test:grade", JsonParser.parseString("{\"grades\":{\"poor\":10}}"));
        categories.put("test:id", JsonParser.parseString("{\"id\":\"test:other\",\"grades\":{}}"));
        Map<String, JsonElement> materials = new LinkedHashMap<>();
        materials.put("test:a", JsonParser.parseString("{\"category\":\"test:ok\",\"grades\":{\"crude\":null,\"lab\":99.99},\"source_purity\":0.5}"));
        materials.put("test:b", JsonParser.parseString("{\"category\":\"test:missing\"}"));
        materials.put("test:c", JsonParser.parseString("{\"category\":\"test:ok\",\"grades\":{\"food\":55}}"));
        materials.put("test:d", JsonParser.parseString("{\"grades\":{}}"));
        List<String> errors = new ArrayList<>();
        PurityData data = PurityData.build(categories, materials, errors::add);
        assertEquals(List.of("test:ok"), new ArrayList<>(data.categories().keySet()));
        assertEquals(List.of("test:a"), new ArrayList<>(data.materials().keySet()));
        assertEquals(7, errors.size(), String.join("\n", errors));
        PurityData.Material a = data.material("test:a");
        assertEquals(PurityScale.ofPercent(60), a.lowestBound());
        assertEquals(PurityScale.externalDefault(PurityScale.ofPercent(60)), a.missing());
        assertEquals(PurityScale.ofPercent(99.99), a.lowerBound(PurityGrade.LAB).getAsInt());
    }
}
