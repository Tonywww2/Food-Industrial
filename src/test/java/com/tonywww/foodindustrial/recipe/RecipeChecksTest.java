package com.tonywww.foodindustrial.recipe;

import com.mojang.datafixers.util.Either;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityTestData;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Purity checks of I29 that need no game registries (additives/10 §1.1). */
class RecipeChecksTest {
    private static final String SALT = "foodindustrial:salt";

    @BeforeAll
    static void materials() {
        PurityTestData.registerMaterials();
    }

    private static Optional<PurityThreshold> min(String grade) {
        return Optional.of(PurityThreshold.resolve(Either.left(grade), null, SALT));
    }

    @Test
    void materialsWithoutPurity() {
        // enzymes, distilled water and mixtures have no purity data, so their items never store Purity
        for (String id : List.of("enzymes", "beta_amylase", "distilled_water", "bittern", "expression_vector")) {
            assertNull(PurityData.current().material("foodindustrial:" + id), id);
        }
        assertFalse(PurityData.current().material("foodindustrial:brine").bears(), "K0 without source purity");
        assertTrue(PurityData.current().material(SALT).bears());
        assertTrue(PurityData.current().material("foodindustrial:acetobacter").bears(), "cultures keep purity");
    }

    @Test
    void compoundComponents() {
        assertTrue(RecipeChecks.isCompound("foodindustrial:gum_base"));
        assertFalse(RecipeChecks.isCompound(SALT));
        assertFalse(RecipeChecks.isCompound(null));
        assertNotNull(RecipeChecks.componentProblem(SALT, Optional.empty()), "no threshold");
        assertNotNull(RecipeChecks.componentProblem(SALT, min("refined")), "below food");
        assertNull(RecipeChecks.componentProblem(SALT, min("food")));
        assertNull(RecipeChecks.componentProblem(SALT, min("lab")));
        assertNull(RecipeChecks.componentProblem(SALT, Optional.of(PurityThreshold.resolve(Either.right(97.0), null, SALT))),
            "a percentage at the food bound");
        // inputs without purity need no threshold
        assertNull(RecipeChecks.componentProblem("foodindustrial:distilled_water", Optional.empty()));
        assertNull(RecipeChecks.componentProblem("foodindustrial:brine", Optional.empty()));
        assertNull(RecipeChecks.componentProblem(null, Optional.empty()));
    }
}
