package com.tonywww.foodindustrial.api.purity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityMatching;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Fluid ingredient {@code foodindustrial:fluid} (additives/10 §4.2): a fluid tag or fluid id, an amount
 * in mB and an optional purity threshold. The same type on every node; recipes of this mod read it
 * with {@link #CODEC}.
 * <pre>
 * { "type": "foodindustrial:fluid", "tag": "foodindustrial:sulfuric_acid", "amount": 100, "min_purity": "refined" }
 * </pre>
 * A grade name in {@code min_purity} stands for that grade's lower bound by the bounds of the material
 * named in {@code material}, or else of the material whose tag or fluid this is. K0 materials have no
 * grades and take percentages only.
 *
 * @param tag       fluid tag, or empty when {@code fluid} is set
 * @param fluid     single fluid, or empty when {@code tag} is set
 * @param amount    amount in mB, {@code > 0}
 * @param threshold purity threshold, or empty for none
 */
public record FluidPurityIngredient(Optional<TagKey<Fluid>> tag, Optional<Fluid> fluid, int amount, Optional<PurityThreshold> threshold) {
    /** Value of the {@code type} field. */
    public static final String TYPE = "foodindustrial:fluid";

    private static final Codec<String> TYPE_CODEC = Codec.STRING.comapFlatMap(
        value -> TYPE.equals(value) ? DataResult.success(value) : DataResult.error(() -> "Expected type " + TYPE + ", got " + value),
        value -> value);

    /** Decoded fields before the checks and the threshold resolution. */
    private record Raw(Optional<TagKey<Fluid>> tag, Optional<Fluid> fluid, int amount,
                       Optional<Either<String, Double>> minPurity, Optional<String> material) {
    }

    public static final MapCodec<FluidPurityIngredient> MAP_CODEC = RecordCodecBuilder.<Raw>mapCodec(instance -> instance.group(
        TYPE_CODEC.fieldOf("type").forGetter(raw -> TYPE),
        StrictCodecs.optional("tag", TagKey.codec(Registries.FLUID)).forGetter(Raw::tag),
        StrictCodecs.optional("fluid", BuiltInRegistries.FLUID.byNameCodec()).forGetter(Raw::fluid),
        Codec.INT.fieldOf("amount").forGetter(Raw::amount),
        StrictCodecs.optional("min_purity", PurityThreshold.WRITTEN_CODEC).forGetter(Raw::minPurity),
        StrictCodecs.optional("material", Codec.STRING).forGetter(Raw::material)
    ).apply(instance, (type, tag, fluid, amount, minPurity, material) -> new Raw(tag, fluid, amount, minPurity, material)))
        .flatXmap(FluidPurityIngredient::fromRaw, ingredient -> DataResult.success(new Raw(ingredient.tag, ingredient.fluid, ingredient.amount,
            ingredient.threshold.map(PurityThreshold::written), ingredient.threshold.map(PurityThreshold::explicitMaterial))));

    public static final Codec<FluidPurityIngredient> CODEC = MAP_CODEC.codec();

    public FluidPurityIngredient {
        if (tag.isPresent() == fluid.isPresent()) {
            throw new IllegalArgumentException("Exactly one of tag or fluid must be set");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Fluid amount must be positive: " + amount);
        }
    }

    private static DataResult<FluidPurityIngredient> fromRaw(Raw raw) {
        if (raw.tag.isPresent() == raw.fluid.isPresent()) {
            return DataResult.error(() -> "Fluid ingredient needs exactly one of \"tag\" or \"fluid\"");
        }
        if (raw.amount <= 0) {
            return DataResult.error(() -> "Fluid amount must be positive: " + raw.amount);
        }
        Optional<PurityThreshold> threshold;
        try {
            threshold = raw.minPurity.map(written -> PurityThreshold.resolve(written, raw.material.orElse(null), hint(raw.tag, raw.fluid)));
        } catch (IllegalArgumentException exception) {
            return DataResult.error(exception::getMessage);
        }
        return DataResult.success(new FluidPurityIngredient(raw.tag, raw.fluid, raw.amount, threshold));
    }

    @Nullable
    private static String hint(Optional<TagKey<Fluid>> tag, Optional<Fluid> fluid) {
        return tag.map(t -> Purity.materialOfTag(PlatformTags.tagId(t), true)).orElseGet(() -> fluid.map(Purity::materialOf).orElse(null));
    }

    private static FluidPurityIngredient of(Optional<TagKey<Fluid>> tag, Optional<Fluid> fluid, int amount, PurityGrade min) {
        return new FluidPurityIngredient(tag, fluid, amount, Optional.of(PurityThreshold.resolve(Either.left(min.id()), null, hint(tag, fluid))));
    }

    /**
     * Fluid ingredient with purity at least the lower bound of {@code min} by the bounds of the tag's material.
     *
     * @throws IllegalArgumentException when the tag is not a material tag or the material lacks the grade
     */
    public static FluidPurityIngredient of(TagKey<Fluid> tag, int amount, PurityGrade min) {
        return of(Optional.of(tag), Optional.empty(), amount, min);
    }

    public static FluidPurityIngredient of(TagKey<Fluid> tag, int amount) {
        return new FluidPurityIngredient(Optional.of(tag), Optional.empty(), amount, Optional.empty());
    }

    public static FluidPurityIngredient of(Fluid fluid, int amount, PurityGrade min) {
        return of(Optional.empty(), Optional.of(fluid), amount, min);
    }

    /** Threshold byte, or empty for none. */
    public Optional<Integer> minPurity() {
        return threshold.map(PurityThreshold::value);
    }

    /** Whether the fluid type and effective purity match; the amount is not checked. */
    public boolean test(FluidAmount candidate) {
        if (candidate.isEmpty()) {
            return false;
        }
        boolean kind = tag.map(t -> candidate.fluid().defaultFluidState().is(t)).orElseGet(() -> candidate.fluid() == fluid.get());
        return kind && threshold.map(value -> Purity.meets(candidate, value.value(), value.material())).orElse(true);
    }

    /** {@link #test} and at least {@link #amount()} mB. */
    public boolean testWithAmount(FluidAmount candidate) {
        return candidate.amount() >= amount && test(candidate);
    }

    /** Source fluids the ingredient can match. */
    public List<Fluid> fluids() {
        return tag.map(PlatformTags::sourceFluidsIn).orElseGet(() -> List.of(fluid.get()));
    }

    /** Fluids shown for this ingredient (JEI): bearers at the threshold and each higher grade bound. */
    public List<FluidAmount> displayFluids() {
        return PurityMatching.displayFluids(fluids(), amount, threshold.orElse(null));
    }
}
