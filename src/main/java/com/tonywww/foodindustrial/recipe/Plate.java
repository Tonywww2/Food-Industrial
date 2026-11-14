package com.tonywww.foodindustrial.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;

import java.util.List;

/**
 * A culture plate of an M30 recipe (R18, I17, additives/06 §T4 生物 / 精细：操作与实现规格): after
 * incubation {@code colonies} colonies grow, each one of the recipe's outputs; the player identifies and
 * picks one.
 * <pre>
 * "plate": { "colonies": 4, "weights": [ 3, 1 ] }      each colony drawn from the outputs by weight
 * "plate": { "by_purity": true }                          purification: the first output with the
 *                                                         purity of the first input as its chance,
 *                                                         else the second output (wild colonies)
 * </pre>
 *
 * @param colonies colonies on the plate, 1–8
 * @param weights  weight per output (empty: all 1)
 * @param byPurity colonies are the target with the input culture's purity as chance
 */
public record Plate(int colonies, List<Integer> weights, boolean byPurity) {
    public static final int DEFAULT_COLONIES = 4;

    public static final Codec<Plate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        StrictCodecs.optional("colonies", Codec.intRange(1, 8), DEFAULT_COLONIES).forGetter(Plate::colonies),
        StrictCodecs.optional("weights", Codec.intRange(1, 10000).listOf(), List.of()).forGetter(Plate::weights),
        StrictCodecs.optional("by_purity", Codec.BOOL, false).forGetter(Plate::byPurity)
    ).apply(instance, Plate::new));

    public Plate {
        weights = List.copyOf(weights);
    }

    /** Weight of output {@code index}. */
    public int weight(int index) {
        return weights.isEmpty() ? 1 : weights.get(index);
    }
}
