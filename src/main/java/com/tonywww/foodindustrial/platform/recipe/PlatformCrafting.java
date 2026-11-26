package com.tonywww.foodindustrial.platform.recipe;

import com.mojang.serialization.MapCodec;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
//? if >=26.1 {
/*import com.mojang.serialization.JsonOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.PlacementInfo;
*///?}
//? if >=1.21 && <26.1 {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingInput;
*///?}
//? if <1.21 {
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
//?}

/**
 * Crafting-table recipes whose matching and result the business code decides from the grid (I38: the food line's
 * workbench steps, foods/05 §2–§3). The business object sees the grid as a list of stacks; this class wraps it into
 * each node's {@code CraftingRecipe}. The recipes are special (no recipe book); JEI shows them on its own page ({@link #loaded}).
 */
public final class PlatformCrafting {
    private PlatformCrafting() {
    }

    /** A grid recipe as the business code writes it. */
    public interface Grid {
        /** Whether the grid's stacks (empty ones included, row by row) make this recipe. */
        boolean matches(List<ItemStack> grid);

        /** The result for these stacks. */
        ItemStack assemble(List<ItemStack> grid);

        /** What stays in each slot after crafting (same size as the grid), before the slot itself loses one item. */
        List<ItemStack> remaining(List<ItemStack> grid);

        /** The result shown without a grid. */
        ItemStack example();

        /**
         * One ingredient per grid slot the recipe fills (shapeless, at most nine), for other mods that read a crafting
         * recipe's ingredients: AE2 patterns and terminals, Create's crafters and mixer (2026-11-25). Matching itself
         * stays with {@link #matches}.
         */
        List<Ingredient> slotIngredients();
    }

    /** Registers serializer {@code foodindustrial:<name>} for grid recipes of {@code codec}. */
    public static <D extends Grid> Supplier<RecipeSerializer<GridRecipe<D>>> register(String name, MapCodec<D> codec) {
        Holder<D> holder = new Holder<>(codec);
        holder.serializer = PlatformRegistry.register(Registries.RECIPE_SERIALIZER, name, () -> createSerializer(holder));
        return holder.serializer;
    }

    public static final class Holder<D extends Grid> {
        private final MapCodec<D> codec;
        private Supplier<RecipeSerializer<GridRecipe<D>>> serializer;

        Holder(MapCodec<D> codec) {
            this.codec = codec;
        }
    }

    /**
     * The loaded grid recipes whose data is a {@code type}, with their ids. Clients from 26.1 on hold only the crafting
     * recipes the server sent: JEI has them sent; without it the list is empty there.
     */
    public static <D extends Grid> List<PlatformRecipes.Loaded<D>> loaded(Level level, Class<D> type) {
        List<PlatformRecipes.Loaded<D>> result = new ArrayList<>();
        //? if >=26.1 {
        /*net.minecraft.world.item.crafting.RecipeMap map = level instanceof net.minecraft.server.level.ServerLevel server
            ? server.recipeAccess().recipeMap() : PlatformRecipes.clientRecipes;
        if (map != null) {
            for (net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> holder : map.byType(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
                if (holder.value() instanceof GridRecipe<?> grid && type.isInstance(grid.data())) {
                    result.add(new PlatformRecipes.Loaded<>(holder.id().identifier().toString(), type.cast(grid.data())));
                }
            }
        }
        *///?}
        //? if >=1.21 && <26.1 {
        /*for (net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> holder
            : level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
            if (holder.value() instanceof GridRecipe<?> grid && type.isInstance(grid.data())) {
                result.add(new PlatformRecipes.Loaded<>(holder.id().toString(), type.cast(grid.data())));
            }
        }
        *///?}
        //? if <1.21 {
        for (CraftingRecipe recipe : level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
            if (recipe instanceof GridRecipe<?> grid && type.isInstance(grid.data())) {
                result.add(new PlatformRecipes.Loaded<>(recipe.getId().toString(), type.cast(grid.data())));
            }
        }
        //?}
        return result;
    }

    /**
     * The loaded grid recipes whose data is a {@code type}, as the node's recipe objects ({@code RecipeHolder} from 1.21,
     * the recipe itself before), for recipe viewers that take them (JEI's crafting page). Clients from 26.1 on: as
     * {@link #loaded}.
     */
    public static List<Object> recipeObjects(Level level, Class<? extends Grid> type) {
        List<Object> result = new ArrayList<>();
        //? if >=26.1 {
        /*net.minecraft.world.item.crafting.RecipeMap map = level instanceof net.minecraft.server.level.ServerLevel server
            ? server.recipeAccess().recipeMap() : PlatformRecipes.clientRecipes;
        if (map != null) {
            for (net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> holder : map.byType(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
                if (holder.value() instanceof GridRecipe<?> grid && type.isInstance(grid.data())) {
                    result.add(holder);
                }
            }
        }
        *///?}
        //? if >=1.21 && <26.1 {
        /*for (net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> holder
            : level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
            if (holder.value() instanceof GridRecipe<?> grid && type.isInstance(grid.data())) {
                result.add(holder);
            }
        }
        *///?}
        //? if <1.21 {
        for (CraftingRecipe recipe : level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
            if (recipe instanceof GridRecipe<?> grid && type.isInstance(grid.data())) {
                result.add(recipe);
            }
        }
        //?}
        return result;
    }

    /** The remainder vanilla leaves for one item of {@code stack} (a bucket for a water bucket), or empty. */
    public static ItemStack remainder(ItemStack stack) {
        //? if >=26.1 {
        /*var remainder = stack.getCraftingRemainder();
        return remainder != null ? remainder.create() : ItemStack.EMPTY;
        *///?} else {
        return stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
        //?}
    }

    private static NonNullList<Ingredient> nonNullIngredients(List<Ingredient> list) {
        NonNullList<Ingredient> result = NonNullList.create();
        result.addAll(list);
        return result;
    }

    private static NonNullList<ItemStack> nonNull(List<ItemStack> list) {
        NonNullList<ItemStack> result = NonNullList.withSize(list.size(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            result.set(i, list.get(i));
        }
        return result;
    }

    //? if >=26.1 {
    /*public record GridRecipe<D extends Grid>(Holder<D> holder, D data) implements CraftingRecipe {
        private static List<ItemStack> grid(CraftingInput input) {
            List<ItemStack> grid = new ArrayList<>(input.size());
            for (int i = 0; i < input.size(); i++) {
                grid.add(input.getItem(i));
            }
            return grid;
        }

        @Override public boolean matches(CraftingInput input, Level level) { return data.matches(grid(input)); }
        @Override public ItemStack assemble(CraftingInput input) { return data.assemble(grid(input)); }
        @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) { return nonNull(data.remaining(grid(input))); }
        @Override public boolean isSpecial() { return true; }
        @Override public boolean showNotification() { return false; }
        @Override public String group() { return ""; }
        @Override public CraftingBookCategory category() { return CraftingBookCategory.MISC; }
        @Override public PlacementInfo placementInfo() {
            List<Ingredient> ingredients = data.slotIngredients();
            return ingredients.isEmpty() ? PlacementInfo.NOT_PLACEABLE : PlacementInfo.create(ingredients);
        }
        @Override public RecipeSerializer<GridRecipe<D>> getSerializer() { return holder.serializer.get(); }
    }

    private static <D extends Grid> RecipeSerializer<GridRecipe<D>> createSerializer(Holder<D> holder) {
        MapCodec<GridRecipe<D>> codec = holder.codec.xmap(data -> new GridRecipe<>(holder, data), GridRecipe::data);
        StreamCodec<RegistryFriendlyByteBuf, GridRecipe<D>> stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        return new RecipeSerializer<>(codec, stream);
    }
    *///?}

    //? if >=1.21 && <26.1 {
    /*public record GridRecipe<D extends Grid>(Holder<D> holder, D data) implements CraftingRecipe {
        private static List<ItemStack> grid(CraftingInput input) {
            List<ItemStack> grid = new ArrayList<>(input.size());
            for (int i = 0; i < input.size(); i++) {
                grid.add(input.getItem(i));
            }
            return grid;
        }

        @Override public boolean matches(CraftingInput input, Level level) { return data.matches(grid(input)); }
        @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) { return data.assemble(grid(input)); }
        @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) { return nonNull(data.remaining(grid(input))); }
        @Override public boolean canCraftInDimensions(int width, int height) { return true; }
        @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return data.example(); }
        @Override public NonNullList<Ingredient> getIngredients() { return nonNullIngredients(data.slotIngredients()); }
        @Override public boolean isSpecial() { return true; }
        @Override public CraftingBookCategory category() { return CraftingBookCategory.MISC; }
        @Override public RecipeSerializer<?> getSerializer() { return holder.serializer.get(); }
    }

    private static <D extends Grid> RecipeSerializer<GridRecipe<D>> createSerializer(Holder<D> holder) {
        MapCodec<GridRecipe<D>> codec = holder.codec.xmap(data -> new GridRecipe<>(holder, data), GridRecipe::data);
        StreamCodec<RegistryFriendlyByteBuf, GridRecipe<D>> stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        return new RecipeSerializer<>() {
            @Override public MapCodec<GridRecipe<D>> codec() { return codec; }
            @Override public StreamCodec<RegistryFriendlyByteBuf, GridRecipe<D>> streamCodec() { return stream; }
        };
    }
    *///?}

    //? if <1.21 {
    public record GridRecipe<D extends Grid>(ResourceLocation id, Holder<D> holder, D data) implements CraftingRecipe {
        private static List<ItemStack> grid(CraftingContainer container) {
            List<ItemStack> grid = new ArrayList<>(container.getContainerSize());
            for (int i = 0; i < container.getContainerSize(); i++) {
                grid.add(container.getItem(i));
            }
            return grid;
        }

        @Override public boolean matches(CraftingContainer container, Level level) { return data.matches(grid(container)); }
        @Override public ItemStack assemble(CraftingContainer container, RegistryAccess registries) { return data.assemble(grid(container)); }
        @Override public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) { return nonNull(data.remaining(grid(container))); }
        @Override public boolean canCraftInDimensions(int width, int height) { return true; }
        @Override public ItemStack getResultItem(RegistryAccess registries) { return data.example(); }
        @Override public NonNullList<Ingredient> getIngredients() { return nonNullIngredients(data.slotIngredients()); }
        @Override public ResourceLocation getId() { return id; }
        @Override public boolean isSpecial() { return true; }
        @Override public CraftingBookCategory category() { return CraftingBookCategory.MISC; }
        @Override public RecipeSerializer<?> getSerializer() { return holder.serializer.get(); }
    }

    private static <D extends Grid> RecipeSerializer<GridRecipe<D>> createSerializer(Holder<D> holder) {
        return new RecipeSerializer<>() {
            @Override
            public GridRecipe<D> fromJson(ResourceLocation id, JsonObject json) {
                D data = holder.codec.codec().parse(JsonOps.INSTANCE, json)
                    .getOrThrow(false, message -> { throw new IllegalArgumentException("Recipe " + id + ": " + message); });
                return new GridRecipe<>(id, holder, data);
            }

            @Override
            public GridRecipe<D> fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                return fromJson(id, JsonParser.parseString(buf.readUtf(262144)).getAsJsonObject());
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, GridRecipe<D> recipe) {
                buf.writeUtf(holder.codec.codec().encodeStart(JsonOps.INSTANCE, recipe.data()).getOrThrow(false, message -> {
                    throw new IllegalStateException("Recipe " + recipe.id() + ": " + message);
                }).toString(), 262144);
            }
        };
    }
    //?}
}
