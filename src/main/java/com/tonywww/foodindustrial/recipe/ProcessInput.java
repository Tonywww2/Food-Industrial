package com.tonywww.foodindustrial.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.api.purity.FluidPurityIngredient;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

/**
 * One recipe input (machines/01 §3.1): an item ingredient with a count, or a fluid ingredient
 * {@code foodindustrial:fluid} with its amount inside.
 * <pre>
 * { "ingredient": { "tag": "foodindustrial:chemicals/salt" }, "count": 4 }
 * { "fluid": { "type": "foodindustrial:fluid", "tag": "foodindustrial:water", "amount": 1000 } }
 * </pre>
 */
public sealed interface ProcessInput permits ProcessInput.ItemInput, ProcessInput.FluidInput {
    /**
     * Material the input refers to (the threshold's material, else the material of its tag or item),
     * or {@code null}; decides whether the input takes part in the start purity and gives foreign
     * items their external default.
     */
    @Nullable
    String material();

    /** Weight in the start purity: items count × 1,000, fluids by mB (additives/10 §2.1). */
    long weight();

    /** Item input. */
    record ItemInput(Ingredient ingredient, int count, @Nullable String material) implements ProcessInput {
        static final MapCodec<ItemInput> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlatformIngredients.codec().fieldOf("ingredient").forGetter(ItemInput::ingredient),
            StrictCodecs.optional("count", Codec.intRange(1, 64), 1).forGetter(ItemInput::count)
        ).apply(instance, ItemInput::of));

        public static ItemInput of(Ingredient ingredient, int count) {
            String material = PlatformIngredients.purityThreshold(ingredient).map(PurityThreshold::material)
                .orElseGet(() -> Purity.materialHint(PlatformIngredients.references(ingredient), false));
            return new ItemInput(ingredient, count, material);
        }

        /** The stack matches the ingredient (purity threshold included) and holds enough items. */
        public boolean test(ItemStack stack) {
            return !stack.isEmpty() && stack.getCount() >= count && ingredient.test(stack);
        }

        /** Effective purity of a consumed stack, or {@link Purity#NONE}. */
        public int purityOf(ItemStack stack) {
            return Purity.get(stack, material);
        }

        @Override
        public long weight() {
            return count * PurityScale.ITEM_AMOUNT;
        }
    }

    /** Fluid input. */
    record FluidInput(FluidPurityIngredient fluid, @Nullable String material) implements ProcessInput {
        static final MapCodec<FluidInput> MAP_CODEC = FluidPurityIngredient.CODEC.fieldOf("fluid").xmap(FluidInput::of, FluidInput::fluid);

        public static FluidInput of(FluidPurityIngredient fluid) {
            String material = fluid.threshold().map(PurityThreshold::material).orElseGet(() -> fluid.tag()
                .map(tag -> Purity.materialOfTag(PlatformTags.tagId(tag), true))
                .orElseGet(() -> fluid.fluid().map(Purity::materialOf).orElse(null)));
            return new FluidInput(fluid, material);
        }

        /** The fluid matches (purity threshold included) and is at least the amount. */
        public boolean test(FluidAmount candidate) {
            return fluid.testWithAmount(candidate);
        }

        /** Effective purity of a consumed fluid, or {@link Purity#NONE}. */
        public int purityOf(FluidAmount candidate) {
            return Purity.get(candidate, material);
        }

        public int amount() {
            return fluid.amount();
        }

        @Override
        public long weight() {
            return fluid.amount();
        }
    }

    /** Item inputs have {@code ingredient}, fluid inputs {@code fluid}. */
    Codec<ProcessInput> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<ProcessInput, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(map -> decodeMap(ops, map)).map(value -> Pair.of(value, input));
        }

        private <T> DataResult<ProcessInput> decodeMap(DynamicOps<T> ops, MapLike<T> map) {
            if (map.get("fluid") != null) {
                return FluidInput.MAP_CODEC.decode(ops, map).map(value -> value);
            }
            if (map.get("ingredient") != null) {
                return ItemInput.MAP_CODEC.decode(ops, map).map(value -> value);
            }
            return DataResult.error(() -> "Recipe input needs \"ingredient\" (items) or \"fluid\" (fluids)");
        }

        @Override
        public <T> DataResult<T> encode(ProcessInput input, DynamicOps<T> ops, T prefix) {
            if (input instanceof ItemInput item) {
                return ItemInput.MAP_CODEC.codec().encode(item, ops, prefix);
            }
            return FluidInput.MAP_CODEC.codec().encode((FluidInput) input, ops, prefix);
        }
    };
}
