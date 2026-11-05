package com.tonywww.foodindustrial.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.tonywww.foodindustrial.core.Tier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Conditions of a recipe (machines/01 §3, §3.1), written by vocabulary category in the
 * {@code requirements} field ({@code conditions} is Forge's load-condition array on 1.20.1):
 * <pre>
 * "requirements": { "min_tier": "t3", "temperature": "high", "methods": [ "centrifuge" ],
 *                 "sterile": true, "environment": [ "open_sky", "clear_weather" ] }
 * </pre>
 * A string or list names vocabulary values of that category ({@code methods} is the {@code method}
 * category), {@code true} names the category's own word ({@code sterile.sterile}). Every value is a
 * capability the device must have; leaving a category out means no requirement. Categories and values
 * come from the condition vocabulary, so new conditions need no code here.
 */
public final class Conditions {
    /** A written value: {@code true}/{@code false}, a value name, or a list of value names. */
    private static final Codec<Either<Boolean, Either<String, List<String>>>> VALUE =
        Codec.either(Codec.BOOL, Codec.either(Codec.STRING, Codec.STRING.listOf()));

    public static final Codec<Conditions> CODEC = Codec.unboundedMap(Codec.STRING, VALUE)
        .comapFlatMap(Conditions::parse, conditions -> conditions.written);

    public static final Conditions NONE = new Conditions(Collections.emptyMap(), null, Capabilities.NONE);

    private static final String MIN_TIER = "min_tier";

    private final Map<String, Either<Boolean, Either<String, List<String>>>> written;
    @Nullable
    private final Tier minTier;
    private final Capabilities required;

    private Conditions(Map<String, Either<Boolean, Either<String, List<String>>>> written, @Nullable Tier minTier, Capabilities required) {
        this.written = written;
        this.minTier = minTier;
        this.required = required;
    }

    private static DataResult<Conditions> parse(Map<String, Either<Boolean, Either<String, List<String>>>> written) {
        Tier minTier = null;
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, Either<Boolean, Either<String, List<String>>>> entry : written.entrySet()) {
            String name = entry.getKey();
            Either<Boolean, Either<String, List<String>>> value = entry.getValue();
            if (name.equals(MIN_TIER)) {
                String id = value.right().flatMap(names -> names.left()).orElse(null);
                Tier tier = id == null ? null : Tier.byId(id);
                if (tier == null) {
                    return DataResult.error(() -> "min_tier must be t0–t4");
                }
                minTier = tier;
                continue;
            }
            String category = categoryOf(name);
            List<String> values = new ArrayList<>();
            value.ifLeft(flag -> {
                if (flag) {
                    values.add(category);
                }
            }).ifRight(names -> names.ifLeft(values::add).ifRight(values::addAll));
            for (String item : values) {
                String key = category + "." + item;
                try {
                    Capabilities.check(key);
                } catch (IllegalArgumentException exception) {
                    return DataResult.error(exception::getMessage);
                }
                keys.add(key);
            }
        }
        return DataResult.success(new Conditions(Collections.unmodifiableMap(new TreeMap<>(written)), minTier, Capabilities.of(keys)));
    }

    /** Vocabulary category of a written key ({@code methods} → {@code method}). */
    private static String categoryOf(String name) {
        return name.equals("methods") ? "method" : name;
    }

    /** Lowest device tier allowed, or {@code null}. */
    @Nullable
    public Tier minTier() {
        return minTier;
    }

    /** Capabilities the device must have. */
    public Capabilities required() {
        return required;
    }

    public boolean isEmpty() {
        return written.isEmpty();
    }

    /** Whether the recipe requires {@code category.value}. */
    public boolean requires(String key) {
        return required.keys().contains(key);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Conditions conditions && minTier == conditions.minTier && required.equals(conditions.required);
    }

    @Override
    public int hashCode() {
        return required.hashCode() * 31 + (minTier == null ? 0 : minTier.hashCode());
    }

    @Override
    public String toString() {
        return (minTier == null ? "" : "min " + minTier.id() + " ") + required;
    }
}
