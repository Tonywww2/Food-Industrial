package com.tonywww.foodindustrial.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.core.purity.PurityScale;

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
 * Every mode may add {@code "max": 97} (percent). The source mode without {@code value} takes the
 * product material's source purity, else its crude bound.
 *
 * @param mode  calculation
 * @param value written percentage, {@code NaN} when absent
 * @param max   cap in percent, {@code NaN} when absent
 */
public record PurityRule(Mode mode, double value, double max) {
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

    /** Source mode with the product material's source purity (or crude bound). */
    public static final PurityRule SOURCE_DEFAULT = new PurityRule(Mode.SOURCE, Double.NaN, Double.NaN);
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
        StrictCodecs.optional("max", PERCENT).forGetter(rule -> optional(rule.max))
    ).apply(instance, (mode, value, max) -> new PurityRule(mode, value.orElse(Double.NaN), max.orElse(Double.NaN))))
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
        return new PurityRule(mode, value, percent);
    }

    public boolean hasValue() {
        return !Double.isNaN(value);
    }

    public boolean hasMax() {
        return !Double.isNaN(max);
    }

    /** {@code b} of the cap (written values round up), or {@link PurityScale#MAX} without one. */
    public int maxByte() {
        return hasMax() ? PurityScale.ofPercent(max) : PurityScale.MAX;
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
        } catch (IllegalArgumentException exception) {
            return DataResult.error(exception::getMessage);
        }
        return DataResult.success(rule);
    }
}
