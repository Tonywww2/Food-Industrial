package com.tonywww.foodindustrial.material.tags;

import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TagPlanTest {
    private static final List<MaterialDef> MATERIALS = MaterialTable.load();
    private static final TagPlan.Conventions FORGE = new TagPlan.Conventions("forge", Optional.empty(), name -> Optional.empty());
    private static final TagPlan.Conventions C = new TagPlan.Conventions("c", Optional.of("c:gaseous"), name -> Optional.of("c:buckets/" + name));

    private static List<TagPlan.Registered> registered() {
        List<TagPlan.Registered> result = new ArrayList<>();
        for (MaterialDef def : MATERIALS) {
            String id = "foodindustrial:" + def.id();
            if (def.isFluid()) {
                String bucket = def.state() == MaterialDef.State.GAS ? null : id + "_bucket";
                result.add(new TagPlan.Registered(def, null, id, "foodindustrial:flowing_" + def.id(), bucket));
            } else {
                result.add(new TagPlan.Registered(def, id, null, null, null));
            }
        }
        return result;
    }

    private static TagPlan.Result build(TagPlan.Conventions conventions, Set<String> absent) {
        return TagPlan.build(registered(), TagImports.load(), conventions, id -> !absent.contains(id), id -> !absent.contains(id));
    }

    private static List<String> ids(List<TagPlan.Value> values) {
        return values.stream().map(TagPlan.Value::id).toList();
    }

    @Test
    void ownTagsImportsAndExports() {
        TagPlan.Result forge = build(FORGE, Set.of());
        assertEquals(List.of("foodindustrial:salt", "#forge:dusts/salt", "#forge:gems/salt"), ids(forge.items().get("foodindustrial:chemicals/salt")));
        assertEquals(List.of("foodindustrial:salt"), ids(forge.items().get("forge:dusts/salt")));
        assertEquals(List.of("foodindustrial:sucrose", "#forge:dusts/sucrose", "#forge:gems/sucrose", "minecraft:sugar", "#forge:sugar"),
                ids(forge.items().get("foodindustrial:chemicals/sucrose")));
        assertEquals(List.of("foodindustrial:sulfuric_acid", "foodindustrial:flowing_sulfuric_acid", "#forge:sulfuric_acid"),
                ids(forge.fluids().get("foodindustrial:sulfuric_acid")));
        assertFalse(forge.fluids().containsKey("c:gaseous"));
        // Catalysts in both tags; cultures, catalysts and raw materials are not exported.
        assertEquals(List.of("foodindustrial:nickel_catalyst"), ids(forge.items().get("foodindustrial:catalysts/nickel")));
        assertTrue(forge.items().containsKey("foodindustrial:chemicals/nickel_catalyst"));
        assertFalse(forge.items().containsKey("forge:dusts/nickel_catalyst"));
        assertFalse(forge.items().containsKey("forge:dusts/acetobacter"));
        assertFalse(forge.fluids().containsKey("forge:latex"));
        // Every value is optional except the mod's own ids.
        forge.items().values().forEach(values -> values.forEach(value ->
                assertEquals(value.id().startsWith("foodindustrial:"), value.required(), value.id())));
    }

    @Test
    void modernConventions() {
        TagPlan.Result c = build(C, Set.of());
        assertTrue(ids(c.fluids().get("c:gaseous")).contains("foodindustrial:carbon_dioxide"));
        assertFalse(ids(c.fluids().get("c:gaseous")).contains("foodindustrial:sulfuric_acid"));
        assertEquals(List.of("foodindustrial:sulfuric_acid_bucket"), ids(c.items().get("c:buckets/sulfuric_acid")));
        assertFalse(c.items().containsKey("c:buckets/carbon_dioxide"));
        assertEquals(List.of("minecraft:short_grass", "minecraft:fern", "minecraft:tall_grass"), ids(c.items().get("foodindustrial:materials/grass")));
        assertEquals(List.of("minecraft:leather", "minecraft:rabbit_hide", "#c:leathers"), ids(c.items().get("foodindustrial:materials/hide")));
    }

    @Test
    void absentEntries() {
        TagPlan.Result c = build(C, Set.of("farmersdelight:straw", "farmersdelight:rice", "pneumaticcraft:vegetable_oil"));
        assertFalse(c.items().containsKey("foodindustrial:materials/straw"), "empty tags are not written");
        assertTrue(c.dropped().contains("foodindustrial:materials/straw <- farmersdelight:straw"));
        assertEquals(List.of("foodindustrial:rice", "#c:crops/rice", "#c:grain/rice"), ids(c.items().get("foodindustrial:materials/rice")));
        assertTrue(c.externalOnly().contains("foodindustrial:materials/nickel"));
        assertTrue(c.externalOnly().contains("foodindustrial:materials/lemon"));
        assertFalse(c.externalOnly().contains("foodindustrial:materials/kelp"));
        assertThrows(IllegalStateException.class, () -> build(C, Set.of("minecraft:kelp")));
    }
}
