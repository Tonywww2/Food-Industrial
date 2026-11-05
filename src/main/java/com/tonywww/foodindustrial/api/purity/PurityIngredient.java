package com.tonywww.foodindustrial.api.purity;

import com.mojang.datafixers.util.Either;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Item ingredient {@code foodindustrial:purity} (additives/10 §4.1): wraps any ingredient and adds a
 * purity threshold. Registered with the loader's ingredient system, so every recipe that reads
 * standard ingredients (vanilla crafting, Farmer's Delight, Create, ...) understands it.
 * <pre>
 * { "type": "foodindustrial:purity", "base": { "tag": "foodindustrial:chemicals/soda_ash" }, "min_purity": "refined" }
 * </pre>
 * A grade name stands for that grade's lower bound by the bounds of the material named in
 * {@code material}, or else of the material whose tag the base is.
 */
public final class PurityIngredient {
    /** Value of the {@code type} field. */
    public static final String TYPE = "foodindustrial:purity";

    private PurityIngredient() {
    }

    /**
     * {@code base} with purity at least the lower bound of {@code min}; the material is found from the
     * base's tag or items.
     *
     * @throws IllegalArgumentException when no material can be found or it lacks the grade
     */
    public static Ingredient of(Ingredient base, PurityGrade min) {
        return of(base, min, null);
    }

    /** {@code base} with purity at least the lower bound of {@code min} by the bounds of {@code material}. */
    public static Ingredient of(Ingredient base, PurityGrade min, @Nullable String material) {
        String hint = Purity.materialHint(PlatformIngredients.references(base), false);
        return PlatformIngredients.purity(base, PurityThreshold.resolve(Either.left(min.id()), material, hint));
    }

    /** {@code base} with purity at least {@code minPercent} (rounded up to the scale). */
    public static Ingredient of(Ingredient base, double minPercent) {
        String hint = Purity.materialHint(PlatformIngredients.references(base), false);
        return PlatformIngredients.purity(base, PurityThreshold.resolve(Either.right(minPercent), null, hint));
    }

    /** Threshold byte of a purity ingredient, or empty for other ingredients. */
    public static OptionalInt minPurity(Ingredient ingredient) {
        Optional<PurityThreshold> threshold = PlatformIngredients.purityThreshold(ingredient);
        return threshold.map(value -> OptionalInt.of(value.value())).orElse(OptionalInt.empty());
    }
}
