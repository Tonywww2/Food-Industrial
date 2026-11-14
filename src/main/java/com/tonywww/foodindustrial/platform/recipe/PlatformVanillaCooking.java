package com.tonywww.foodindustrial.platform.recipe;

import com.mojang.serialization.MapCodec;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
//? if >=26.1 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
*///?}
//? if >=1.21 && <26.1 {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
*///?}
//? if <1.21 {
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
//?}

/**
 * Bridge recipes of the vanilla cooking blocks (machines/01 §5, machines/02 §3): recipes of the
 * vanilla smelting, smoking, blasting and campfire types, read from data pack files of this mod's
 * serializers ({@code foodindustrial:vanilla_smelting} and so on). They extend the vanilla recipe
 * classes, so furnaces and campfires use them as they are; matching and the result (with purity)
 * come from the business {@link Bridge}. An input that another recipe of the same block already
 * takes is left to that recipe (vanilla first).
 */
public final class PlatformVanillaCooking {
    private PlatformVanillaCooking() {
    }

    /** The vanilla blocks and the serializer of each. */
    public enum Block {
        CAMPFIRE("vanilla_campfire_cooking"),
        FURNACE("vanilla_smelting"),
        SMOKER("vanilla_smoking"),
        BLAST_FURNACE("vanilla_blasting");

        private final String name;

        Block(String name) {
            this.name = name;
        }

        /** Serializer id, the {@code type} of the bridge recipe files. */
        public String serializerId() {
            return "foodindustrial:" + name;
        }
    }

    /** Business side of the bridge recipes. */
    public interface Bridge<D> {
        /** Fields of a bridge recipe file besides {@code type}. */
        MapCodec<D> codec();

        /** Input shown in the recipe book. */
        Ingredient ingredient(D data);

        /** Result item shown in the recipe book (without purity; no stack is made while recipes load). */
        Item resultItem(D data);

        int resultCount(D data);

        int cookingTime(D data, Block block);

        float experience(D data);

        /** Whether one item of {@code input} can be cooked. */
        boolean matches(D data, ItemStack input);

        /** The result of cooking one item of {@code input} on {@code block}, purity included. */
        ItemStack assemble(D data, Block block, ItemStack input);
    }

    private static final Map<Block, Supplier<RecipeSerializer<?>>> SERIALIZERS = new EnumMap<>(Block.class);

    /** Registers the serializers of the four blocks. Called once during mod construction. */
    @SuppressWarnings("unchecked")
    public static <D> void register(Bridge<D> bridge) {
        Bridge<Object> any = (Bridge<Object>) bridge;
        for (Block block : Block.values()) {
            SERIALIZERS.put(block, PlatformRegistry.register(Registries.RECIPE_SERIALIZER, block.name, () -> serializer(any, block)));
        }
    }

    /** Business data and caches of one bridge recipe. */
    private static final class Core {
        private final Bridge<Object> bridge;
        private final Object data;
        private final Block block;
        /** Whether another recipe of the block takes the item (vanilla first); rebuilt with the recipes. */
        private final Map<Item, Boolean> claimed = new ConcurrentHashMap<>();

        Core(Bridge<Object> bridge, Object data, Block block) {
            this.bridge = bridge;
            this.data = data;
            this.block = block;
        }

        Ingredient ingredient() {
            return bridge.ingredient(data);
        }

        Item resultItem() {
            return bridge.resultItem(data);
        }

        int resultCount() {
            return bridge.resultCount(data);
        }

        ItemStack result() {
            return new ItemStack(resultItem(), resultCount());
        }

        float experience() {
            return bridge.experience(data);
        }

        int cookingTime() {
            return bridge.cookingTime(data, block);
        }

        boolean matches(ItemStack input, Level level, RecipeType<?> type) {
            if (input.isEmpty() || !bridge.matches(data, input)) {
                return false;
            }
            return !claimed.computeIfAbsent(input.getItem(), item -> claimedByOthers(level, type, input));
        }

        ItemStack assemble(ItemStack input) {
            return bridge.assemble(data, block, input);
        }

        RecipeSerializer<?> serializer() {
            return SERIALIZERS.get(block).get();
        }
    }

    /**
     * Whether {@code recipe} (a cooking recipe, or a holder of one from 1.21 on) is a bridge recipe of
     * the mod (JEI hides them from the vanilla categories, I22).
     */
    public static boolean isBridge(Object recipe) {
        //? if >=1.21 {
        /*if (recipe instanceof net.minecraft.world.item.crafting.RecipeHolder<?> holder) {
            return holder.value() instanceof Bridged;
        }
        *///?}
        return recipe instanceof Bridged;
    }

    /** A recipe made by the bridge. */
    private interface Bridged {
        Core core();
    }

    //? if >=26.1 {
    /*@SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean claimedByOthers(Level level, RecipeType<?> type, ItemStack input) {
        if (!(level instanceof ServerLevel server)) {
            return false;
        }
        SingleRecipeInput single = new SingleRecipeInput(input.copyWithCount(1));
        for (Object holder : server.recipeAccess().recipeMap().byType((RecipeType) type)) {
            Recipe<SingleRecipeInput> recipe = (Recipe<SingleRecipeInput>) ((RecipeHolder<?>) holder).value();
            if (!(recipe instanceof Bridged) && recipe.matches(single, level)) {
                return true;
            }
        }
        return false;
    }

    private static Recipe<?> create(Core core) {
        return switch (core.block) {
            case CAMPFIRE -> new BridgedCampfire(core);
            case FURNACE -> new BridgedSmelting(core);
            case SMOKER -> new BridgedSmoking(core);
            case BLAST_FURNACE -> new BridgedBlasting(core);
        };
    }

    private static ItemStackTemplate template(Core core) {
        return new ItemStackTemplate(core.resultItem(), core.resultCount());
    }

    private static Recipe.CommonInfo common() {
        return new Recipe.CommonInfo(false);
    }

    private static AbstractCookingRecipe.CookingBookInfo book() {
        return new AbstractCookingRecipe.CookingBookInfo(CookingBookCategory.MISC, "");
    }

    private static RecipeSerializer<?> serializer(Bridge<Object> bridge, Block block) {
        MapCodec<AbstractCookingRecipe> codec = bridge.codec().xmap(data -> (AbstractCookingRecipe) create(new Core(bridge, data, block)),
            recipe -> ((Bridged) recipe).core().data);
        StreamCodec<RegistryFriendlyByteBuf, AbstractCookingRecipe> stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        return new RecipeSerializer<>(codec, stream);
    }

    static final class BridgedSmelting extends SmeltingRecipe implements Bridged {
        private final Core core;

        BridgedSmelting(Core core) {
            super(common(), book(), core.ingredient(), template(core), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(SingleRecipeInput input, Level level) { return core.matches(input.item(), level, getType()); }
        @Override public ItemStack assemble(SingleRecipeInput input) { return core.assemble(input.item()); }
        @SuppressWarnings("unchecked")
        @Override public RecipeSerializer<SmeltingRecipe> getSerializer() { return (RecipeSerializer<SmeltingRecipe>) core.serializer(); }
    }

    static final class BridgedSmoking extends SmokingRecipe implements Bridged {
        private final Core core;

        BridgedSmoking(Core core) {
            super(common(), book(), core.ingredient(), template(core), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(SingleRecipeInput input, Level level) { return core.matches(input.item(), level, getType()); }
        @Override public ItemStack assemble(SingleRecipeInput input) { return core.assemble(input.item()); }
        @SuppressWarnings("unchecked")
        @Override public RecipeSerializer<SmokingRecipe> getSerializer() { return (RecipeSerializer<SmokingRecipe>) core.serializer(); }
    }

    static final class BridgedBlasting extends BlastingRecipe implements Bridged {
        private final Core core;

        BridgedBlasting(Core core) {
            super(common(), book(), core.ingredient(), template(core), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(SingleRecipeInput input, Level level) { return core.matches(input.item(), level, getType()); }
        @Override public ItemStack assemble(SingleRecipeInput input) { return core.assemble(input.item()); }
        @SuppressWarnings("unchecked")
        @Override public RecipeSerializer<BlastingRecipe> getSerializer() { return (RecipeSerializer<BlastingRecipe>) core.serializer(); }
    }

    static final class BridgedCampfire extends CampfireCookingRecipe implements Bridged {
        private final Core core;

        BridgedCampfire(Core core) {
            super(common(), book(), core.ingredient(), template(core), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(SingleRecipeInput input, Level level) { return core.matches(input.item(), level, getType()); }
        @Override public ItemStack assemble(SingleRecipeInput input) { return core.assemble(input.item()); }
        @SuppressWarnings("unchecked")
        @Override public RecipeSerializer<CampfireCookingRecipe> getSerializer() { return (RecipeSerializer<CampfireCookingRecipe>) core.serializer(); }
    }
    *///?}

    //? if >=1.21 && <26.1 {
    /*@SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean claimedByOthers(Level level, RecipeType<?> type, ItemStack input) {
        SingleRecipeInput single = new SingleRecipeInput(input.copyWithCount(1));
        for (Object holder : level.getRecipeManager().getAllRecipesFor((RecipeType) type)) {
            Recipe<SingleRecipeInput> recipe = (Recipe<SingleRecipeInput>) ((RecipeHolder<?>) holder).value();
            if (!(recipe instanceof Bridged) && recipe.matches(single, level)) {
                return true;
            }
        }
        return false;
    }

    private static Recipe<?> create(Core core) {
        return switch (core.block) {
            case CAMPFIRE -> new BridgedCampfire(core);
            case FURNACE -> new BridgedSmelting(core);
            case SMOKER -> new BridgedSmoking(core);
            case BLAST_FURNACE -> new BridgedBlasting(core);
        };
    }

    private static RecipeSerializer<?> serializer(Bridge<Object> bridge, Block block) {
        MapCodec<Recipe<?>> codec = bridge.codec().xmap(data -> create(new Core(bridge, data, block)), recipe -> ((Bridged) recipe).core().data);
        StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        return new RecipeSerializer<Recipe<?>>() {
            @Override public MapCodec<Recipe<?>> codec() { return codec; }
            @Override public StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> streamCodec() { return stream; }
        };
    }

    static final class BridgedSmelting extends SmeltingRecipe implements Bridged {
        private final Core core;

        BridgedSmelting(Core core) {
            super("", CookingBookCategory.MISC, core.ingredient(), core.result(), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(SingleRecipeInput input, Level level) { return core.matches(input.item(), level, getType()); }
        @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) { return core.assemble(input.item()); }
        @Override public RecipeSerializer<?> getSerializer() { return core.serializer(); }
    }

    static final class BridgedSmoking extends SmokingRecipe implements Bridged {
        private final Core core;

        BridgedSmoking(Core core) {
            super("", CookingBookCategory.MISC, core.ingredient(), core.result(), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(SingleRecipeInput input, Level level) { return core.matches(input.item(), level, getType()); }
        @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) { return core.assemble(input.item()); }
        @Override public RecipeSerializer<?> getSerializer() { return core.serializer(); }
    }

    static final class BridgedBlasting extends BlastingRecipe implements Bridged {
        private final Core core;

        BridgedBlasting(Core core) {
            super("", CookingBookCategory.MISC, core.ingredient(), core.result(), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(SingleRecipeInput input, Level level) { return core.matches(input.item(), level, getType()); }
        @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) { return core.assemble(input.item()); }
        @Override public RecipeSerializer<?> getSerializer() { return core.serializer(); }
    }

    static final class BridgedCampfire extends CampfireCookingRecipe implements Bridged {
        private final Core core;

        BridgedCampfire(Core core) {
            super("", CookingBookCategory.MISC, core.ingredient(), core.result(), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(SingleRecipeInput input, Level level) { return core.matches(input.item(), level, getType()); }
        @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) { return core.assemble(input.item()); }
        @Override public RecipeSerializer<?> getSerializer() { return core.serializer(); }
    }
    *///?}

    //? if <1.21 {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean claimedByOthers(Level level, RecipeType<?> type, ItemStack input) {
        SimpleContainer container = new SimpleContainer(input.copyWithCount(1));
        for (Object candidate : level.getRecipeManager().getAllRecipesFor((RecipeType) type)) {
            Recipe<Container> recipe = (Recipe<Container>) candidate;
            if (!(recipe instanceof Bridged) && recipe.matches(container, level)) {
                return true;
            }
        }
        return false;
    }

    private static Recipe<?> create(Core core, ResourceLocation id) {
        return switch (core.block) {
            case CAMPFIRE -> new BridgedCampfire(id, core);
            case FURNACE -> new BridgedSmelting(id, core);
            case SMOKER -> new BridgedSmoking(id, core);
            case BLAST_FURNACE -> new BridgedBlasting(id, core);
        };
    }

    private static RecipeSerializer<?> serializer(Bridge<Object> bridge, Block block) {
        return new RecipeSerializer<Recipe<?>>() {
            @Override
            public Recipe<?> fromJson(ResourceLocation id, JsonObject json) {
                Object data = bridge.codec().codec().parse(JsonOps.INSTANCE, json)
                    .getOrThrow(false, message -> { throw new IllegalArgumentException("Recipe " + id + ": " + message); });
                return create(new Core(bridge, data, block), id);
            }

            @Override
            public Recipe<?> fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                return fromJson(id, JsonParser.parseString(buf.readUtf(262144)).getAsJsonObject());
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, Recipe<?> recipe) {
                buf.writeUtf(bridge.codec().codec().encodeStart(JsonOps.INSTANCE, ((Bridged) recipe).core().data).getOrThrow(false, message -> {
                    throw new IllegalStateException("Recipe " + recipe.getId() + ": " + message);
                }).toString(), 262144);
            }
        };
    }

    static final class BridgedSmelting extends SmeltingRecipe implements Bridged {
        private final Core core;

        BridgedSmelting(ResourceLocation id, Core core) {
            super(id, "", CookingBookCategory.MISC, core.ingredient(), core.result(), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(Container container, Level level) { return core.matches(container.getItem(0), level, getType()); }
        @Override public ItemStack assemble(Container container, RegistryAccess registries) { return core.assemble(container.getItem(0)); }
        @Override public RecipeSerializer<?> getSerializer() { return core.serializer(); }
    }

    static final class BridgedSmoking extends SmokingRecipe implements Bridged {
        private final Core core;

        BridgedSmoking(ResourceLocation id, Core core) {
            super(id, "", CookingBookCategory.MISC, core.ingredient(), core.result(), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(Container container, Level level) { return core.matches(container.getItem(0), level, getType()); }
        @Override public ItemStack assemble(Container container, RegistryAccess registries) { return core.assemble(container.getItem(0)); }
        @Override public RecipeSerializer<?> getSerializer() { return core.serializer(); }
    }

    static final class BridgedBlasting extends BlastingRecipe implements Bridged {
        private final Core core;

        BridgedBlasting(ResourceLocation id, Core core) {
            super(id, "", CookingBookCategory.MISC, core.ingredient(), core.result(), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(Container container, Level level) { return core.matches(container.getItem(0), level, getType()); }
        @Override public ItemStack assemble(Container container, RegistryAccess registries) { return core.assemble(container.getItem(0)); }
        @Override public RecipeSerializer<?> getSerializer() { return core.serializer(); }
    }

    static final class BridgedCampfire extends CampfireCookingRecipe implements Bridged {
        private final Core core;

        BridgedCampfire(ResourceLocation id, Core core) {
            super(id, "", CookingBookCategory.MISC, core.ingredient(), core.result(), core.experience(), core.cookingTime());
            this.core = core;
        }

        @Override public Core core() { return core; }
        @Override public boolean matches(Container container, Level level) { return core.matches(container.getItem(0), level, getType()); }
        @Override public ItemStack assemble(Container container, RegistryAccess registries) { return core.assemble(container.getItem(0)); }
        @Override public RecipeSerializer<?> getSerializer() { return core.serializer(); }
    }
    //?}
}
