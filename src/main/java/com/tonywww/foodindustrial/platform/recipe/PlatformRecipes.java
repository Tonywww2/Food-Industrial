package com.tonywww.foodindustrial.platform.recipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.RegistryOps;
import io.netty.buffer.Unpooled;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
//? if >=26.1 {
/*import net.minecraft.resources.ResourceKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
*///?}
//? if >=1.21 && <26.1 {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
*///?}
//? if <1.21 {
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
//?}

/**
 * Data-driven recipe types described by a business {@link MapCodec}. Each recipe is stored in the
 * vanilla recipe manager as a {@link DataRecipe} wrapping the decoded business object; the mod's
 * machines read them through {@link #all} and never use vanilla matching or the recipe book.
 * <p>
 * JSON format: the business codec's fields plus {@code "type": "foodindustrial:<name>"}.
 */
public final class PlatformRecipes {
    private PlatformRecipes() {
    }

    /** A registered recipe kind (type + serializer). */
    public static final class Kind<D> {
        private final String name;
        private final MapCodec<D> codec;
        private Supplier<RecipeType<DataRecipe<D>>> type;
        private Supplier<RecipeSerializer<DataRecipe<D>>> serializer;

        private Kind(String name, MapCodec<D> codec) {
            this.name = name;
            this.codec = codec;
        }

        public String name() {
            return name;
        }

        public MapCodec<D> codec() {
            return codec;
        }
    }

    /** Registers recipe type and serializer {@code foodindustrial:<name>}. */
    public static <D> Kind<D> register(String name, MapCodec<D> codec) {
        Kind<D> kind = new Kind<>(name, codec);
        kind.type = PlatformRegistry.register(Registries.RECIPE_TYPE, name, () -> RecipeType.simple(PlatformIdentifier.id(name)));
        kind.serializer = PlatformRegistry.register(Registries.RECIPE_SERIALIZER, name, () -> createSerializer(kind));
        return kind;
    }

    /** All loaded recipes of {@code kind} (server side; empty on clients from 26.1 on). */
    public static <D> List<D> all(Level level, Kind<D> kind) {
        List<D> result = new ArrayList<>();
        //? if >=26.1 {
        /*if (level instanceof ServerLevel serverLevel) {
            for (RecipeHolder<DataRecipe<D>> holder : serverLevel.recipeAccess().recipeMap().byType(kind.type.get())) {
                result.add(holder.value().data());
            }
        }
        *///?}
        //? if >=1.21 && <26.1 {
        /*for (RecipeHolder<DataRecipe<D>> holder : level.getRecipeManager().getAllRecipesFor(kind.type.get())) {
            result.add(holder.value().data());
        }
        *///?}
        //? if <1.21 {
        for (DataRecipe<D> recipe : level.getRecipeManager().getAllRecipesFor(kind.type.get())) {
            result.add(recipe.data());
        }
        //?}
        return result;
    }

    /** JSON ops that know the level's registries (tag ingredients encode as tags on every node). */
    public static DynamicOps<JsonElement> jsonOps(Level level) {
        //? if >=1.21 {
        /*return level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        *///?} else {
        return RegistryOps.create(JsonOps.INSTANCE, level.registryAccess());
        //?}
    }

    /** A loaded recipe with its id. */
    public record Loaded<D>(String id, D data) {
    }

    /** All loaded recipes of {@code kind} with their ids (server side; empty on clients from 26.1 on). */
    public static <D> List<Loaded<D>> loaded(Level level, Kind<D> kind) {
        List<Loaded<D>> result = new ArrayList<>();
        //? if >=26.1 {
        /*if (level instanceof ServerLevel serverLevel) {
            for (RecipeHolder<DataRecipe<D>> holder : serverLevel.recipeAccess().recipeMap().byType(kind.type.get())) {
                result.add(new Loaded<>(holder.id().identifier().toString(), holder.value().data()));
            }
        }
        *///?}
        //? if >=1.21 && <26.1 {
        /*for (RecipeHolder<DataRecipe<D>> holder : level.getRecipeManager().getAllRecipesFor(kind.type.get())) {
            result.add(new Loaded<>(holder.id().toString(), holder.value().data()));
        }
        *///?}
        //? if <1.21 {
        for (DataRecipe<D> recipe : level.getRecipeManager().getAllRecipesFor(kind.type.get())) {
            result.add(new Loaded<>(recipe.id().toString(), recipe.data()));
        }
        //?}
        return result;
    }

    /**
     * Sends {@code data} through the recipe serializer's network encoding and back, as the recipe sync
     * to clients does (development checks of the network format).
     */
    public static <D> D networkRoundTrip(Level level, Kind<D> kind, D data) {
        //? if >=1.21 {
        /*RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
        try {
            StreamCodec<RegistryFriendlyByteBuf, DataRecipe<D>> stream = streamCodecOf(kind);
            stream.encode(buffer, new DataRecipe<>(kind, data));
            return stream.decode(buffer).data();
        } finally {
            buffer.release();
        }
        *///?} else {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            ResourceLocation id = PlatformIdentifier.id("network_check");
            RecipeSerializer<DataRecipe<D>> serializer = kind.serializer.get();
            serializer.toNetwork(buffer, new DataRecipe<>(id, kind, data));
            return serializer.fromNetwork(id, buffer).data();
        } finally {
            buffer.release();
        }
        //?}
    }

    /** Ingredients of any loaded recipe (development checks); empty when the recipe is unknown. */
    public static List<Ingredient> ingredientsOf(Level level, String id) {
        //? if >=26.1 {
        /*if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, PlatformIdentifier.parse(id)))
                .map(holder -> holder.value().placementInfo().ingredients()).orElse(List.of());
        }
        return List.of();
        *///?}
        //? if >=1.21 && <26.1 {
        /*return level.getRecipeManager().byKey(PlatformIdentifier.parse(id)).<List<Ingredient>>map(holder -> holder.value().getIngredients()).orElse(List.of());
        *///?}
        //? if <1.21 {
        return level.getRecipeManager().byKey(PlatformIdentifier.parse(id)).<List<Ingredient>>map(Recipe::getIngredients).orElse(List.of());
        //?}
    }

    //? if >=26.1 {
    /*/^* Vanilla recipe wrapper around a business object. ^/
    public record DataRecipe<D>(Kind<D> kind, D data) implements Recipe<RecipeInput> {
        @Override public boolean matches(RecipeInput input, Level level) { return false; }
        @Override public ItemStack assemble(RecipeInput input) { return ItemStack.EMPTY; }
        @Override public boolean isSpecial() { return true; }
        @Override public boolean showNotification() { return false; }
        @Override public String group() { return ""; }
        @Override public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() { return kind.serializer.get(); }
        @Override public RecipeType<? extends Recipe<RecipeInput>> getType() { return kind.type.get(); }
        @Override public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }
        @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
    }

    private static <D> RecipeSerializer<DataRecipe<D>> createSerializer(Kind<D> kind) {
        MapCodec<DataRecipe<D>> codec = kind.codec.xmap(data -> new DataRecipe<>(kind, data), DataRecipe::data);
        StreamCodec<RegistryFriendlyByteBuf, DataRecipe<D>> stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        return new RecipeSerializer<>(codec, stream);
    }

    @SuppressWarnings("deprecation")
    private static <D> StreamCodec<RegistryFriendlyByteBuf, DataRecipe<D>> streamCodecOf(Kind<D> kind) {
        return kind.serializer.get().streamCodec();
    }
    *///?}

    //? if >=1.21 && <26.1 {
    /*/^* Vanilla recipe wrapper around a business object. ^/
    public record DataRecipe<D>(Kind<D> kind, D data) implements Recipe<RecipeInput> {
        @Override public boolean matches(RecipeInput input, Level level) { return false; }
        @Override public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) { return ItemStack.EMPTY; }
        @Override public boolean canCraftInDimensions(int width, int height) { return false; }
        @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return ItemStack.EMPTY; }
        @Override public boolean isSpecial() { return true; }
        @Override public RecipeSerializer<?> getSerializer() { return kind.serializer.get(); }
        @Override public RecipeType<?> getType() { return kind.type.get(); }
    }

    private static <D> RecipeSerializer<DataRecipe<D>> createSerializer(Kind<D> kind) {
        MapCodec<DataRecipe<D>> codec = kind.codec.xmap(data -> new DataRecipe<>(kind, data), DataRecipe::data);
        StreamCodec<RegistryFriendlyByteBuf, DataRecipe<D>> stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        return new RecipeSerializer<>() {
            @Override public MapCodec<DataRecipe<D>> codec() { return codec; }
            @Override public StreamCodec<RegistryFriendlyByteBuf, DataRecipe<D>> streamCodec() { return stream; }
        };
    }

    private static <D> StreamCodec<RegistryFriendlyByteBuf, DataRecipe<D>> streamCodecOf(Kind<D> kind) {
        return kind.serializer.get().streamCodec();
    }
    *///?}

    //? if <1.21 {
    /** Vanilla recipe wrapper around a business object. */
    public record DataRecipe<D>(ResourceLocation id, Kind<D> kind, D data) implements Recipe<Container> {
        @Override public boolean matches(Container container, Level level) { return false; }
        @Override public ItemStack assemble(Container container, RegistryAccess registries) { return ItemStack.EMPTY; }
        @Override public boolean canCraftInDimensions(int width, int height) { return false; }
        @Override public ItemStack getResultItem(RegistryAccess registries) { return ItemStack.EMPTY; }
        @Override public ResourceLocation getId() { return id; }
        @Override public boolean isSpecial() { return true; }
        @Override public RecipeSerializer<?> getSerializer() { return kind.serializer.get(); }
        @Override public RecipeType<?> getType() { return kind.type.get(); }
    }

    private static <D> RecipeSerializer<DataRecipe<D>> createSerializer(Kind<D> kind) {
        return new RecipeSerializer<>() {
            @Override
            public DataRecipe<D> fromJson(ResourceLocation id, JsonObject json) {
                D data = kind.codec.codec().parse(JsonOps.INSTANCE, json)
                    .getOrThrow(false, message -> { throw new IllegalArgumentException("Recipe " + id + ": " + message); });
                return new DataRecipe<>(id, kind, data);
            }

            @Override
            public DataRecipe<D> fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                return fromJson(id, JsonParser.parseString(buf.readUtf(262144)).getAsJsonObject());
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, DataRecipe<D> recipe) {
                buf.writeUtf(kind.codec.codec().encodeStart(JsonOps.INSTANCE, recipe.data()).getOrThrow(false, message -> {
                    throw new IllegalStateException("Recipe " + recipe.id() + ": " + message);
                }).toString(), 262144);
            }
        };
    }
    //?}
}
