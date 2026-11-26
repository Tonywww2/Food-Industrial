package com.tonywww.foodindustrial.food.flavor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlavoringsTest {
    /** foods/12: the groups in the table's order; an aux-only material has none. */
    @Test
    void groupsFollowTheTable() {
        assertEquals(List.of(FlavorType.SPICY, FlavorType.SMOKY, FlavorType.SAVORY), Flavorings.groups("clove_leaf_oil"));
        assertTrue(Flavorings.groups("patchouli_oil").isEmpty());
        assertTrue(Flavorings.isNote("patchouli_oil"));
        // "78 号" stands for both: propylene glycol and glycerol are secondary notes
        assertTrue(Flavorings.isNote("propylene_glycol") && Flavorings.isNote("glycerol"));
        assertEquals(List.of(FlavorType.COOLING), Flavorings.groups("menthol"));
    }

    /** The crafting table and "automatic": the first group of the first material that has one (I48, 2026-11-19). */
    @Test
    void automaticTypeIsTheFirstGroupOfTheFirstNote() {
        assertEquals(FlavorType.SPICY, Flavorings.autoType(List.of("patchouli_oil", "clove_leaf_oil", "menthol")));
        assertEquals(FlavorType.COOLING, Flavorings.autoType(List.of("menthol", "clove_leaf_oil")));
        assertNull(Flavorings.autoType(List.of("patchouli_oil", "sucrose")));
    }

    @Test
    void idsOfTheFlavourings() {
        assertEquals(FlavorType.NUTTY_ROASTED, Flavorings.typeOfId("foodindustrial:nutty_roasted_flavoring"));
        assertEquals(FlavorType.FRUITY, Flavorings.typeOfId("fruity_flavoring"));
        assertNull(Flavorings.typeOfId("hawthorn_kernel_smoke_flavoring_1"));
        assertEquals("cooling_flavoring_premix", FlavorType.COOLING.premixId());
    }
}
