package com.tonywww.foodindustrial.core.purity;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import org.jetbrains.annotations.Nullable;

/**
 * Purity threshold of a recipe ingredient ({@code min_purity}, additives/10 §4.1): a grade name, which
 * stands for that grade's lower bound by the bounds of the checked material, or a percentage. Turned
 * into a byte when the recipe loads (written values round up).
 *
 * @param grade            grade written in the recipe, or {@code null} for a percentage
 * @param percent          percentage written in the recipe ({@code NaN} for a grade)
 * @param explicitMaterial {@code material} field as written, or {@code null}
 * @param value            the threshold byte
 * @param material         material the ingredient checks for (explicit, or found from the base), or
 *                         {@code null}; gives the external default of foreign items
 */
public record PurityThreshold(@Nullable PurityGrade grade, double percent, @Nullable String explicitMaterial,
                              int value, @Nullable String material) {
    /** {@code min_purity} as written: a grade name or a number. */
    public static final Codec<Either<String, Double>> WRITTEN_CODEC = Codec.either(Codec.STRING, Codec.of(
        new Encoder<Double>() {
            @Override
            public <T> DataResult<T> encode(Double input, DynamicOps<T> ops, T prefix) {
                double number = input;
                return DataResult.success(ops.createNumeric(number == Math.rint(number) && Math.abs(number) < 1e9 ? (Number) (int) number : (Number) number));
            }
        }, Codec.DOUBLE));

    /**
     * Resolves a threshold as written.
     *
     * @param written  {@code min_purity}: a grade name ({@code crude}, {@code refined}, {@code food}, {@code lab}) or a percentage
     * @param material {@code material} field, or {@code null}
     * @param hint     material found from the ingredient's base, or {@code null}
     * @throws IllegalArgumentException for unknown grades, percentages outside 0..100, or a grade name
     *                                  whose material cannot be found or lacks that grade
     */
    public static PurityThreshold resolve(Either<String, Double> written, @Nullable String material, @Nullable String hint) {
        String explicit = material == null ? null : normalize(material);
        String target = explicit != null ? explicit : hint;
        return written.map(name -> {
            PurityGrade grade = PurityGrade.byId(name);
            if (grade == null || grade == PurityGrade.POOR) {
                try {
                    return ofPercent(Double.parseDouble(name.trim()), explicit, target);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException("min_purity must be crude, refined, food, lab or a percentage: " + name);
                }
            }
            if (target == null) {
                throw new IllegalArgumentException("min_purity \"" + grade.id()
                    + "\" needs a material: add \"material\" or use a material tag as the base");
            }
            PurityData.Material data = PurityData.current().material(target);
            if (data == null) {
                throw new IllegalArgumentException("unknown purity material " + target);
            }
            int bound = data.gradeValue(grade);
            if (bound == PurityData.NONE) {
                throw new IllegalArgumentException("material " + target + " has no grade " + grade.id());
            }
            return new PurityThreshold(grade, Double.NaN, explicit, bound, target);
        }, percent -> ofPercent(percent, explicit, target));
    }

    /** Resolves a threshold read from JSON ({@code min_purity} element and optional {@code material}). */
    public static PurityThreshold resolve(JsonElement written, @Nullable String material, @Nullable String hint) {
        if (!written.isJsonPrimitive()) {
            throw new IllegalArgumentException("min_purity must be a grade name or a percentage");
        }
        JsonPrimitive primitive = written.getAsJsonPrimitive();
        return resolve(primitive.isNumber() ? Either.right(primitive.getAsDouble()) : Either.left(primitive.getAsString()), material, hint);
    }

    private static PurityThreshold ofPercent(double percent, @Nullable String explicit, @Nullable String target) {
        return new PurityThreshold(null, percent, explicit, PurityScale.ofPercent(percent), target);
    }

    /** Threshold received over the network: only the byte and the material are needed. */
    public static PurityThreshold received(int value, @Nullable String material) {
        return new PurityThreshold(null, PurityScale.percent(value), null, PurityScale.clamp(value), material == null || material.isEmpty() ? null : material);
    }

    /** {@code min_purity} as written (a resolved network copy writes its percentage). */
    public Either<String, Double> written() {
        return grade != null ? Either.left(grade.id()) : Either.right(percent);
    }

    /** {@code min_purity} as JSON. */
    public JsonElement writtenJson() {
        if (grade != null) {
            return new JsonPrimitive(grade.id());
        }
        return percent == Math.rint(percent) ? new JsonPrimitive((int) percent) : new JsonPrimitive(percent);
    }

    /** Material ids may omit the mod namespace. */
    private static String normalize(String material) {
        String trimmed = material.trim();
        return trimmed.indexOf(':') >= 0 ? trimmed : FoodIndustrial.MOD_ID + ":" + trimmed;
    }
}
