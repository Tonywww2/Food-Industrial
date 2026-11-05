package com.tonywww.foodindustrial.platform.recipe;

//? if neoforge {
/*import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityMatching;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
*///?}
//? if neoforge && <26.1 {
/*import java.util.Arrays;
*///?}
//? if >=26.1 {
/*import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
*///?}

//? if neoforge {
/*/^*
 * NeoForge implementation of {@code foodindustrial:purity} (1.21.1 and 26.1). Not simple: matching
 * reads the stack's purity. The wrapped ingredient decides emptiness, so serializers that drop empty
 * ingredients keep it. The threshold is resolved when the recipe loads (a grade name by the bounds of
 * the material found from the base as written); the network carries the resolved byte and material.
 ^/
record NeoPurityIngredient(Ingredient base, PurityThreshold threshold) implements ICustomIngredient {
    static final MapCodec<NeoPurityIngredient> CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of("base", "min_purity", "material").map(ops::createString);
        }

        /^* Decodes the base, and reads it as written to find its material. ^/
        @Override
        public <T> DataResult<NeoPurityIngredient> decode(DynamicOps<T> ops, MapLike<T> input) {
            T base = input.get("base");
            T minPurity = input.get("min_purity");
            if (base == null || minPurity == null) {
                return DataResult.error(() -> "foodindustrial:purity needs \"base\" and \"min_purity\"");
            }
            T material = input.get("material");
            DataResult<Optional<String>> materialResult = material == null
                ? DataResult.success(Optional.empty()) : Codec.STRING.parse(ops, material).map(Optional::of);
            return ingredientCodec().parse(ops, base).flatMap(ingredient -> PurityThreshold.WRITTEN_CODEC.parse(ops, minPurity)
                .flatMap(written -> materialResult.flatMap(explicit -> {
                    JsonElement json = new Dynamic<>(ops, base).convert(JsonOps.INSTANCE).getValue();
                    try {
                        return DataResult.success(new NeoPurityIngredient(ingredient,
                            PurityThreshold.resolve(written, explicit.orElse(null), Purity.materialHint(json, false))));
                    } catch (IllegalArgumentException exception) {
                        return DataResult.error(exception::getMessage);
                    }
                })));
        }

        /^* Encodes with the caller's ops, so tags stay tags. ^/
        @Override
        public <T> RecordBuilder<T> encode(NeoPurityIngredient input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            prefix.add("base", ingredientCodec().encodeStart(ops, input.base()));
            prefix.add("min_purity", PurityThreshold.WRITTEN_CODEC.encodeStart(ops, input.threshold().written()));
            if (input.threshold().explicitMaterial() != null) {
                prefix.add("material", ops.createString(input.threshold().explicitMaterial()));
            }
            return prefix;
        }
    };
    static final StreamCodec<RegistryFriendlyByteBuf, NeoPurityIngredient> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, NeoPurityIngredient::base,
        ByteBufCodecs.VAR_INT, ingredient -> ingredient.threshold().value(),
        ByteBufCodecs.STRING_UTF8, ingredient -> ingredient.threshold().material() == null ? "" : ingredient.threshold().material(),
        (base, value, material) -> new NeoPurityIngredient(base, PurityThreshold.received(value, material)));
    static final Supplier<IngredientType<NeoPurityIngredient>> TYPE = PlatformRegistry.register(
        NeoForgeRegistries.Keys.INGREDIENT_TYPES, "purity", () -> new IngredientType<>(CODEC, STREAM_CODEC));

    /^* Triggers the type registration during mod construction. ^/
    static void bootstrap() {
    }

    @Override
    public boolean test(ItemStack stack) {
        return PurityMatching.test(base, threshold, stack);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return TYPE.get();
    }
*///?}
//? if neoforge && <26.1 {
/*
    static Codec<Ingredient> ingredientCodec() {
        return Ingredient.CODEC_NONEMPTY;
    }

    @Override
    public Stream<ItemStack> getItems() {
        return PurityMatching.displayStacks(Arrays.asList(base.getItems()), threshold).stream();
    }

    static List<ItemStack> displayStacks(Ingredient ingredient) {
        return Arrays.asList(ingredient.getItems());
    }
*///?}
//? if >=26.1 {
/*
    static Codec<Ingredient> ingredientCodec() {
        return Ingredient.CODEC;
    }

    /^* Every item the base accepts; the purity check happens in {@link #test}. ^/
    @Override
    @SuppressWarnings("deprecation")
    public Stream<Holder<Item>> items() {
        return base.items();
    }

    /^* Qualifying purity variants of the base items. ^/
    @Override
    public SlotDisplay display() {
        return new SlotDisplay.Composite(qualifyingStacks().stream()
            .<SlotDisplay>map(stack -> new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack)))
            .toList());
    }

    @SuppressWarnings("deprecation")
    private List<ItemStack> qualifyingStacks() {
        return PurityMatching.displayStacks(base.items().map(ItemStack::new).toList(), threshold);
    }

    @SuppressWarnings("deprecation")
    static List<ItemStack> displayStacks(Ingredient ingredient) {
        if (ingredient.getCustomIngredient() instanceof NeoPurityIngredient purity) {
            return purity.qualifyingStacks();
        }
        return ingredient.items().map(ItemStack::new).toList();
    }
*///?}
//? if neoforge {
/*}
*///?}
