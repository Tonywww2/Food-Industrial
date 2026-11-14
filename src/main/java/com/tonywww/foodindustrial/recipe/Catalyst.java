package com.tonywww.foodindustrial.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * A catalyst, culture, adsorbent, membrane or electrode a recipe needs in the device's catalyst slots
 * (machines/03 §1, additives/08 §催化剂与助剂). The item is not consumed per run: the device counts
 * completions and uses up one item after {@code runs} of them (2026-11-04).
 * <pre>
 * "catalyst": { "ingredient": { "tag": "foodindustrial:catalysts/nickel" }, "runs": 64 }
 * "catalyst": [ { "ingredient": … }, { "ingredient": … } ]      two cultures
 * </pre>
 *
 * @param ingredient accepted items (this mod's tags)
 * @param runs       completions one item lasts, default {@value #DEFAULT_RUNS}
 */
public record Catalyst(Ingredient ingredient, int runs) {
    public static final int DEFAULT_RUNS = 64;

    public static final Codec<Catalyst> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        PlatformIngredients.codec().fieldOf("ingredient").forGetter(Catalyst::ingredient),
        StrictCodecs.optional("runs", Codec.intRange(1, 1_000_000), DEFAULT_RUNS).forGetter(Catalyst::runs)
    ).apply(instance, Catalyst::new));

    /** One catalyst as an object, several as a list. */
    public static final Codec<List<Catalyst>> ONE_OR_MORE = Codec.either(CODEC, CODEC.listOf()).xmap(
        either -> either.map(List::of, list -> list),
        list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list));

    public boolean test(ItemStack stack) {
        return !stack.isEmpty() && PlatformIngredients.fresh(ingredient).test(stack);
    }
}
