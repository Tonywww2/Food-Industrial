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

import java.util.Optional;

/**
 * One recipe input (machines/01 §3.1): an item ingredient with a count, or a fluid ingredient
 * {@code foodindustrial:fluid} with its amount inside.
 * <pre>
 * { "ingredient": { "tag": "foodindustrial:chemicals/salt" }, "count": 4 }
 * { "fluid": { "type": "foodindustrial:fluid", "tag": "foodindustrial:water", "amount": 1000 } }
 * { "semi_finished": "steamed_bun|dough", "count": 2 }      the semi-finished item of a product's stage (foods/08 §3)
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

    /** The {@code min_purity} threshold, if written. */
    Optional<PurityThreshold> threshold();

    /** Material without purity (additives/10 §1.1) the input's tag refers to, or {@code null}; for checks. */
    @Nullable
    String plainMaterial();

    /**
     * Item input.
     *
     * @param semi the semi-finished key {@code product|stage} the stack must carry (foods/08 §3), or empty
     * @param form the dosage form a flavouring must have (I48: the emulsion flavouring for spray drying), or empty
     */
    record ItemInput(Ingredient ingredient, int count, @Nullable String material, String semi, String form) implements ProcessInput {
        static final MapCodec<ItemInput> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlatformIngredients.codec().fieldOf("ingredient").forGetter(ItemInput::ingredient),
            StrictCodecs.optional("count", Codec.intRange(1, 64), 1).forGetter(ItemInput::count),
            StrictCodecs.optional("form", Codec.STRING, "").forGetter(ItemInput::form)
        ).apply(instance, (ingredient, count, form) -> of(ingredient, count).withForm(form)));

        static final MapCodec<ItemInput> SEMI_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("semi_finished").forGetter(ItemInput::semi),
            StrictCodecs.optional("count", Codec.intRange(1, 64), 1).forGetter(ItemInput::count)
        ).apply(instance, ItemInput::semi));

        public ItemInput(Ingredient ingredient, int count, @Nullable String material) {
            this(ingredient, count, material, "", "");
        }

        public ItemInput(Ingredient ingredient, int count, @Nullable String material, String semi) {
            this(ingredient, count, material, semi, "");
        }

        /** This input taking only flavourings of the dosage form {@code dosage} ({@code ""}: any). */
        public ItemInput withForm(String dosage) {
            return new ItemInput(ingredient, count, material, semi, dosage);
        }

        public static ItemInput of(Ingredient ingredient, int count) {
            String material = PlatformIngredients.purityThreshold(ingredient).map(PurityThreshold::material)
                .orElseGet(() -> Purity.materialHint(PlatformIngredients.references(ingredient), false));
            return new ItemInput(ingredient, count, material);
        }

        /** {@code count} semi-finished items with the key {@code product|stage}. */
        public static ItemInput semi(String key, int count) {
            return new ItemInput(Ingredient.of(com.tonywww.foodindustrial.food.SemiFinished.ITEM.get()), count, null, key);
        }

        public boolean isSemi() {
            return !semi.isEmpty();
        }

        /** The stack matches the ingredient (purity threshold included) and holds enough items. */
        public boolean test(ItemStack stack) {
            return !stack.isEmpty() && stack.getCount() >= count && accepts(stack);
        }

        /** The stack is of the kind this input takes (any count): the ingredient, or the semi-finished key. */
        public boolean accepts(ItemStack stack) {
            if (isSemi()) {
                return semi.equals(com.tonywww.foodindustrial.food.SemiFinished.keyOf(stack));
            }
            if (!form.isEmpty()) {
                com.tonywww.foodindustrial.food.flavor.DosageForm dosage = com.tonywww.foodindustrial.food.flavor.Flavorings.form(stack);
                if (dosage == null || !dosage.id().equals(form)) {
                    return false;
                }
            }
            return PlatformIngredients.fresh(ingredient).test(stack);
        }

        /** Stacks standing for the input in recipe pages. */
        public java.util.List<ItemStack> displayStacks() {
            if (isSemi()) {
                return java.util.List.of(com.tonywww.foodindustrial.food.SemiFinished.stack(semi, count));
            }
            com.tonywww.foodindustrial.food.flavor.DosageForm dosage = form.isEmpty() ? null : com.tonywww.foodindustrial.food.flavor.DosageForm.byId(form);
            if (dosage != null) {
                return PlatformIngredients.displayStacks(ingredient).stream()
                    .map(stack -> com.tonywww.foodindustrial.food.flavor.Flavorings.withForm(stack.copy(), dosage)).toList();
            }
            // a flavouring in any form shows each form: recipe pages tell the forms apart (JEI subtypes, 2026-11-20)
            java.util.List<ItemStack> stacks = new java.util.ArrayList<>();
            for (ItemStack stack : PlatformIngredients.displayStacks(ingredient)) {
                if (com.tonywww.foodindustrial.food.flavor.Flavorings.type(stack.getItem()) != null
                    && com.tonywww.foodindustrial.food.flavor.Flavorings.form(stack) == null) {
                    for (com.tonywww.foodindustrial.food.flavor.DosageForm each : com.tonywww.foodindustrial.food.flavor.DosageForm.values()) {
                        stacks.add(com.tonywww.foodindustrial.food.flavor.Flavorings.withForm(stack.copy(), each));
                    }
                } else {
                    stacks.add(stack);
                }
            }
            return stacks;
        }

        /** Effective purity of a consumed stack, or {@link Purity#NONE}. */
        public int purityOf(ItemStack stack) {
            return Purity.get(stack, material);
        }

        @Override
        public long weight() {
            return count * PurityScale.ITEM_AMOUNT;
        }

        @Override
        public Optional<PurityThreshold> threshold() {
            return PlatformIngredients.purityThreshold(ingredient);
        }

        @Override
        @Nullable
        public String plainMaterial() {
            return Purity.plainMaterialHint(PlatformIngredients.references(PlatformIngredients.unwrap(ingredient)), false);
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

        @Override
        public Optional<PurityThreshold> threshold() {
            return fluid.threshold();
        }

        @Override
        @Nullable
        public String plainMaterial() {
            return fluid.tag().map(tag -> Purity.plainMaterialOfTag(PlatformTags.tagId(tag), true)).orElse(null);
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
            if (map.get("semi_finished") != null) {
                return ItemInput.SEMI_CODEC.decode(ops, map).map(value -> value);
            }
            return DataResult.error(() -> "Recipe input needs \"ingredient\" (items), \"semi_finished\" or \"fluid\" (fluids)");
        }

        @Override
        public <T> DataResult<T> encode(ProcessInput input, DynamicOps<T> ops, T prefix) {
            if (input instanceof ItemInput item) {
                return (item.isSemi() ? ItemInput.SEMI_CODEC : ItemInput.MAP_CODEC).codec().encode(item, ops, prefix);
            }
            return FluidInput.MAP_CODEC.codec().encode((FluidInput) input, ops, prefix);
        }
    };
}
