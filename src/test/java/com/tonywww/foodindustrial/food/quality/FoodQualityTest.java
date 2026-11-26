package com.tonywww.foodindustrial.food.quality;

import com.tonywww.foodindustrial.api.purity.PurityGrade;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodQualityTest {
    private static int items(int color, int taste, int state, int keeping) {
        int code = 0;
        code = FoodQuality.withItem(code, QualityItem.COLOR, color);
        code = FoodQuality.withItem(code, QualityItem.TASTE, taste);
        code = FoodQuality.withItem(code, QualityItem.STATE, state);
        return FoodQuality.withItem(code, QualityItem.PRESERVATION, keeping);
    }

    @Test
    void itemsAndLabelsPackAndUnpack() {
        int code = FoodQuality.withLabel(items(-1, 2, 0, 1), FoodQuality.LABEL_HOMEMADE) | FoodQuality.ASPARTAME;
        assertEquals(-1, FoodQuality.item(code, QualityItem.COLOR));
        assertEquals(2, FoodQuality.item(code, QualityItem.TASTE));
        assertEquals(0, FoodQuality.item(code, QualityItem.STATE));
        assertEquals(1, FoodQuality.item(code, QualityItem.PRESERVATION));
        assertEquals(FoodQuality.LABEL_HOMEMADE, FoodQuality.label(code));
        assertTrue(FoodQuality.has(code, FoodQuality.ASPARTAME));
        assertFalse(FoodQuality.has(code, FoodQuality.NITRITE_MISUSE));
        assertEquals(FoodQuality.NONE, FoodQuality.withItem(items(0, -1, -1, -1), QualityItem.COLOR, -1));
    }

    @Test
    void misuseOnAStepWithoutQualityIsAHiddenFlag() {
        // I47: nitrite in the salt position of a step with no ingredient segment marks the product with the flag alone
        int code = FoodQuality.merge(FoodQuality.NONE, FoodQuality.NITRITE_MISUSE);
        assertTrue(FoodQuality.has(code, FoodQuality.NITRITE_MISUSE));
        assertFalse(FoodQuality.shown(code), "no quality line for the flag alone");
        assertEquals(FoodQuality.ORDINARY, FoodQuality.grade(code));
        int carried = FoodQuality.merge(items(-1, 2, -1, -1), code);
        assertTrue(FoodQuality.has(carried, FoodQuality.NITRITE_MISUSE), "carried to later steps");
        assertEquals(2, FoodQuality.item(carried, QualityItem.TASTE));
        assertFalse(QualityRules.isNitrite(null));
        assertTrue(QualityRules.isNitrite("potassium_nitrite"));
        assertFalse(QualityRules.isNitrite("salt"));
    }

    @Test
    void theOverallGradeAveragesTheEvaluatedItems() {
        // foods/04 §4: ordinary 0, good 1, excellent 2; ≥ 1.5 excellent, ≥ 0.5 good
        assertEquals(FoodQuality.EXCELLENT, FoodQuality.grade(items(-1, 2, 1, -1)));
        assertEquals(FoodQuality.GOOD, FoodQuality.grade(items(-1, 1, 0, -1)));
        assertEquals(FoodQuality.ORDINARY, FoodQuality.grade(items(0, 0, 1, 0)));
        assertEquals(FoodQuality.GOOD, FoodQuality.grade(items(0, 0, 2, 0)));
        assertEquals(FoodQuality.ORDINARY, FoodQuality.grade(FoodQuality.NONE));
        // premium raises one grade, not past excellent
        assertEquals(FoodQuality.GOOD, FoodQuality.grade(FoodQuality.withLabel(items(0, -1, -1, -1), FoodQuality.LABEL_PREMIUM)));
        assertEquals(FoodQuality.EXCELLENT, FoodQuality.grade(FoodQuality.withLabel(items(2, -1, -1, -1), FoodQuality.LABEL_PREMIUM)));
        // the line shows only when better than ordinary or labelled
        assertFalse(FoodQuality.shown(items(0, 0, -1, -1)));
        assertTrue(FoodQuality.shown(FoodQuality.withLabel(items(0, -1, -1, -1), FoodQuality.LABEL_POOR)));
    }

    @Test
    void processingKeepsTheLowerGradeAndTheWorseLabel() {
        int dough = FoodQuality.withLabel(items(-1, 2, 1, -1), FoodQuality.LABEL_PREMIUM);
        int filling = FoodQuality.withLabel(items(-1, 1, -1, 2), FoodQuality.LABEL_PREMIUM) | FoodQuality.NITRITE_MISUSE;
        int dumpling = FoodQuality.merge(dough, filling);
        assertEquals(1, FoodQuality.item(dumpling, QualityItem.TASTE));
        assertEquals(1, FoodQuality.item(dumpling, QualityItem.STATE));
        assertEquals(2, FoodQuality.item(dumpling, QualityItem.PRESERVATION));
        assertEquals(-1, FoodQuality.item(dumpling, QualityItem.COLOR));
        assertEquals(FoodQuality.LABEL_PREMIUM, FoodQuality.label(dumpling));
        assertTrue(FoodQuality.has(dumpling, FoodQuality.NITRITE_MISUSE));
        assertEquals(FoodQuality.LABEL_NONE, FoodQuality.label(FoodQuality.merge(dough, items(-1, 1, -1, -1))));
        assertEquals(FoodQuality.LABEL_POOR, FoodQuality.label(FoodQuality.merge(FoodQuality.withLabel(dough, FoodQuality.LABEL_HOMEMADE),
            FoodQuality.withLabel(filling, FoodQuality.LABEL_POOR))));
        // inputs without quality take no part
        assertEquals(dough, FoodQuality.merge(FoodQuality.NONE, dough));
        assertEquals(dough, QualityRules.carried(new int[]{0, dough, 0}));
    }

    @Test
    void theLabelComparesEveryGradeWithTheMinimum() {
        PurityGrade food = PurityGrade.FOOD;
        assertEquals(FoodQuality.LABEL_NONE, QualityRules.label(List.of(), food));
        assertEquals(FoodQuality.LABEL_NONE, QualityRules.label(List.of(PurityGrade.FOOD, PurityGrade.LAB), food));
        assertEquals(FoodQuality.LABEL_PREMIUM, QualityRules.label(List.of(PurityGrade.LAB, PurityGrade.LAB), food));
        assertEquals(FoodQuality.LABEL_HOMEMADE, QualityRules.label(List.of(PurityGrade.LAB, PurityGrade.REFINED), food));
        assertEquals(FoodQuality.LABEL_POOR, QualityRules.label(List.of(PurityGrade.CRUDE, PurityGrade.REFINED), food));
        // a traditional product asks less (foods/01 §5)
        assertEquals(FoodQuality.LABEL_PREMIUM, QualityRules.label(List.of(PurityGrade.REFINED), PurityGrade.CRUDE));
    }

    @Test
    void synergiesAndDefaultClassesFollowFoods04() {
        assertTrue(Synergies.pair(QualityItem.STATE, "locust_bean_gum", "xanthan_gum"));
        assertFalse(Synergies.pair(QualityItem.TASTE, "locust_bean_gum", "xanthan_gum"));
        assertTrue(Synergies.pair(QualityItem.COLOR, "sodium_nitrite", "sodium_ascorbate"));
        assertEquals("sweetener", AdditiveFunctions.classOf("aspartame"));
        assertEquals(QualityItem.TASTE, AdditiveFunctions.itemOf("sweetener"));
        assertEquals(QualityItem.COLOR, AdditiveFunctions.itemOf(AdditiveFunctions.classOf("sodium_nitrite")));
        QualitySpec.Slot keeping = new QualitySpec.Slot(QualityItem.PRESERVATION, List.of(), 1);
        // citric acid is an acidity regulator, accepted on keeping as a chelator of synergy row 5
        assertTrue(keeping.accepts("citric_acid"));
        assertTrue(keeping.accepts("tbhq"));
        assertFalse(keeping.accepts("tartrazine"));
        QualitySpec.Slot listed = new QualitySpec.Slot(QualityItem.TASTE, List.of("caffeine"), 1);
        assertTrue(listed.accepts("caffeine"));
        assertFalse(listed.accepts("aspartame"));
    }

    /** I48 (foods/11 §6): a flavouring goes on taste only, whatever the item's list; the list names the matching types. */
    @Test
    void flavoringsGoOnTasteOnly() {
        QualitySpec.Slot taste = new QualitySpec.Slot(QualityItem.TASTE, List.of("dairy_flavoring"), 1);
        assertTrue(taste.accepts("dairy_flavoring"));
        assertTrue(taste.accepts("fruity_flavoring"), "another type counts as one good additive");
        QualitySpec.Slot state = new QualitySpec.Slot(QualityItem.STATE, List.of(), 1);
        assertFalse(state.accepts("dairy_flavoring"));
        QualitySpec.Slot plainTaste = new QualitySpec.Slot(QualityItem.TASTE, List.of(), 1);
        assertTrue(plainTaste.accepts("smoky_flavoring"));
    }
}
