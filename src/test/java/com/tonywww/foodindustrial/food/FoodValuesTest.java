package com.tonywww.foodindustrial.food;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** foods/04 §5.5: the comparison table against vanilla (energy per serving → hunger, shares → coefficient). */
class FoodValuesTest {
    @Test
    void hungerMatchesTable() {
        int[][] rows = {{15, 2}, {25, 2}, {35, 3}, {46, 3}, {49, 3}, {60, 3}, {89, 4}, {95, 4}, {133, 5}, {148, 5}, {161, 6},
            {167, 6}, {178, 6}, {183, 6}};
        for (int[] row : rows) {
            assertEquals(row[1], FoodValues.hunger(row[0]), "kcal " + row[0]);
        }
        assertEquals(0, FoodValues.hunger(1.0));
        assertEquals(0, FoodValues.hunger(0));
        assertEquals(20, FoodValues.hunger(2000));
        assertEquals(20, FoodValues.hunger(9000));
    }

    @Test
    void coefficientMatchesTable() {
        assertEquals(0.3F, FoodValues.coefficient(0.08, 0));     // watermelon
        assertEquals(0.6F, FoodValues.coefficient(0.15, 0));     // beetroot
        assertEquals(0.8F, FoodValues.coefficient(0.37, 0));     // mushroom stew
        assertEquals(0.1F, FoodValues.coefficient(0.04, 0.26));  // cookie
        assertEquals(0.6F, FoodValues.coefficient(0.19, 0.07));  // bread
        assertEquals(0.8F, FoodValues.coefficient(0.87, 0));     // cooked cod
    }

    @Test
    void saturationAppliesQuality() {
        assertEquals(7.2F, FoodValues.saturation(6, 0.6F, 1.0F), 1e-4);
        assertEquals(10.08F, FoodValues.saturation(6, 0.6F, 1.4F), 1e-4);
    }
}
