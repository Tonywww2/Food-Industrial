package com.tonywww.foodindustrial.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

/**
 * Purity rule of one product (additives/10 §2.1): how the product's purity follows from the start
 * purity (the weighted average of the participating inputs).
 * <pre>
 * { "mode": "source", "value": 90 }     written value, inputs ignored
 * { "mode": "removal", "value": 50 }    50% of the impurities removed by mass
 * { "mode": "keep" }                    start purity
 * { "mode": "degrade", "value": 10 }    10% of the target substance lost by mass
 * </pre>
 * Every mode may add caps: {@code "max": 97} (percent, all devices) and {@code "t0_max": "refined"}
 * (only on T0 devices; a grade name stands for the product material's lower bound of that grade, or a
 * percentage; 2026-11-05). A cap never lowers the result below the start purity. The source mode
 * without {@code value} takes the product material's source purity, else its crude bound; the R19
 * compounding default takes its food bound instead ({@link #SOURCE_FOOD}, not writable in JSON).
 *
 * @param mode       calculation
 * @param value      written percentage, {@code NaN} when absent
 * @param max        cap in percent, {@code NaN} when absent
 * @param t0Max      cap on T0 devices as written (grade name or percentage), or {@code null}
 * @param valueGrade source value given as the product material's lower bound of this grade, or
 *                   {@code null} (built-in defaults only)
 */
public record PurityRule(Mode mode, double value, double max, @Nullable Either<String, Double> t0Max, @Nullable PurityGrade valueGrade) {
    /** The four calculations. */
    public enum Mode {
        SOURCE, REMOVAL, KEEP, DEGRADE;

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        static DataResult<Mode> parse(String id) {
            for (Mode mode : values()) {
                if (mode.id().equals(id)) {
                    return DataResult.success(mode);
                }
            }
            return DataResult.error(() -> "Unknown purity mode " + id + " (source, removal, keep, degrade)");
        }
    }

    public PurityRule(Mode mode, double value, double max) {
        this(mode, value, max, null, null);
    }

    /** Source mode with the product material's source purity (or crude bound). */
    public static final PurityRule SOURCE_DEFAULT = new PurityRule(Mode.SOURCE, Double.NaN, Double.NaN);
    /** Source mode with the product material's food bound: default of R19 compounding (machines/01 §1.1). */
    public static final PurityRule SOURCE_FOOD = new PurityRule(Mode.SOURCE, Double.NaN, Double.NaN, null, PurityGrade.FOOD);
    public static final PurityRule KEEP = new PurityRule(Mode.KEEP, Double.NaN, Double.NaN);

    /** A percentage; whole numbers are written without decimals. */
    public static final Codec<Double> PERCENT = Codec.of(new Encoder<Double>() {
        @Override
        public <T> DataResult<T> encode(Double input, DynamicOps<T> ops, T prefix) {
            double number = input;
            return DataResult.success(ops.createNumeric(number == Math.rint(number) && Math.abs(number) < 1e9 ? (Number) (int) number : (Number) number));
        }
    }, Codec.DOUBLE);

    public static final Codec<PurityRule> CODEC = RecordCodecBuilder.<PurityRule>create(instance -> instance.group(
        Codec.STRING.comapFlatMap(Mode::parse, Mode::id).fieldOf("mode").forGetter(PurityRule::mode),
        StrictCodecs.optional("value", PERCENT).forGetter(rule -> optional(rule.value)),
        StrictCodecs.optional("max", PERCENT).forGetter(rule -> optional(rule.max)),
        StrictCodecs.optional("t0_max", PurityThreshold.WRITTEN_CODEC).forGetter(rule -> Optional.ofNullable(rule.t0Max))
    ).apply(instance, (mode, value, max, t0Max) -> new PurityRule(mode, value.orElse(Double.NaN), max.orElse(Double.NaN), t0Max.orElse(null), null)))
        .flatXmap(PurityRule::validate, PurityRule::validate);

    public static PurityRule source(double percent) {
        return new PurityRule(Mode.SOURCE, percent, Double.NaN);
    }

    public static PurityRule removal(double percent) {
        return new PurityRule(Mode.REMOVAL, percent, Double.NaN);
    }

    public static PurityRule degrade(double percent) {
        return new PurityRule(Mode.DEGRADE, percent, Double.NaN);
    }

    public PurityRule withMax(double percent) {
        return new PurityRule(mode, value, percent, t0Max, valueGrade);
    }

    /** Cap on T0 devices: a grade name of the product material. */
    public PurityRule withT0Max(PurityGrade grade) {
        return new PurityRule(mode, value, max, Either.left(grade.id()), valueGrade);
    }

    /** Cap on T0 devices: a percentage. */
    public PurityRule withT0Max(double percent) {
        return new PurityRule(mode, value, max, Either.right(percent), valueGrade);
    }

    public boolean hasValue() {
        return !Double.isNaN(value);
    }

    public boolean hasMax() {
        return !Double.isNaN(max);
    }

    public boolean hasT0Max() {
        return t0Max != null;
    }

    /** {@code b} of the cap (written values round up), or {@link PurityScale#MAX} without one. */
    public int maxByte() {
        return hasMax() ? PurityScale.ofPercent(max) : PurityScale.MAX;
    }

    /** Grade of {@code t0_max} when written as a grade name, else {@code null}. */
    @Nullable
    public PurityGrade t0MaxGrade() {
        return t0Max == null ? null : t0Max.map(PurityGrade::byId, percent -> null);
    }

    /**
     * {@code b} of the T0 cap for a product of {@code material} (a grade name takes its lower bound,
     * percentages round up); {@link PurityScale#MAX} without a cap, {@link PurityData#NONE} when the
     * grade cannot be resolved (the data checks reject such recipes).
     */
    public int t0MaxByte(@Nullable PurityData.Material material) {
        if (t0Max == null) {
            return PurityScale.MAX;
        }
        return t0Max.map(name -> {
            PurityGrade grade = PurityGrade.byId(name);
            return grade == null || material == null ? PurityData.NONE : material.gradeValue(grade);
        }, PurityScale::ofPercent);
    }

    /** Whether the result depends on the inputs' purity. */
    public boolean readsInputs() {
        return mode != Mode.SOURCE;
    }

    private static Optional<Double> optional(double value) {
        return Double.isNaN(value) ? Optional.empty() : Optional.of(value);
    }

    private static DataResult<PurityRule> validate(PurityRule rule) {
        try {
            switch (rule.mode) {
                case SOURCE -> {
                    if (rule.hasValue()) {
                        PurityScale.checkWritten(BigDecimal.valueOf(rule.value));
                    }
                }
                case REMOVAL, DEGRADE -> {
                    if (!rule.hasValue()) {
                        return DataResult.error(() -> "Purity mode " + rule.mode.id() + " needs \"value\"");
                    }
                    if (!(rule.value >= 0.0 && rule.value <= 100.0)) {
                        return DataResult.error(() -> "Purity " + rule.mode.id() + " value must be 0–100: " + rule.value);
                    }
                }
                case KEEP -> {
                    if (rule.hasValue()) {
                        return DataResult.error(() -> "Purity mode keep takes no \"value\"");
                    }
                }
            }
            if (rule.hasMax()) {
                PurityScale.checkWritten(BigDecimal.valueOf(rule.max));
            }
            if (rule.t0Max != null) {
                String problem = rule.t0Max.map(name -> {
                    PurityGrade grade = PurityGrade.byId(name);
                    return grade == null || grade == PurityGrade.POOR ? "t0_max must be crude, refined, food, lab or a percentage: " + name : null;
                }, percent -> {
                    PurityScale.checkWritten(BigDecimal.valueOf(percent));
                    return null;
                });
                if (problem != null) {
                    return DataResult.error(() -> problem);
                }
            }
        } catch (IllegalArgumentException exception) {
            return DataResult.error(exception::getMessage);
        }
        return DataResult.success(rule);
    }
}
