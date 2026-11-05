package com.tonywww.foodindustrial.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Codec helpers that behave the same on every node. DataFixerUpper 6 (Minecraft 1.20.1) makes
 * {@code optionalFieldOf} lenient: a field that is present but invalid silently becomes the default.
 * Data written by people must fail loudly instead, so data formats use {@link #optional}.
 */
public final class StrictCodecs {
    private StrictCodecs() {
    }

    /** Optional field: absent gives empty; present but invalid is an error. */
    public static <A> MapCodec<Optional<A>> optional(String name, Codec<A> codec) {
        return new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString(name));
            }

            @Override
            public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
                T value = input.get(name);
                if (value == null) {
                    return DataResult.success(Optional.empty());
                }
                return codec.parse(ops, value).map(Optional::of).mapError(message -> name + ": " + message);
            }

            @Override
            public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return input.isPresent() ? prefix.add(name, codec.encodeStart(ops, input.get())) : prefix;
            }

            @Override
            public String toString() {
                return "StrictOptional[" + name + "]";
            }
        };
    }

    /** Optional field with a default, which is left out when encoding; present but invalid is an error. */
    public static <A> MapCodec<A> optional(String name, Codec<A> codec, A defaultValue) {
        return optional(name, codec).xmap(value -> value.orElse(defaultValue),
            value -> Objects.equals(value, defaultValue) ? Optional.empty() : Optional.of(value));
    }
}
