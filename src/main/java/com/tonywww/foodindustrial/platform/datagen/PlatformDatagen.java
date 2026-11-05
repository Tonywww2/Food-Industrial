package com.tonywww.foodindustrial.platform.datagen;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
//? if forge {
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
*///?}

/**
 * Data generation without version-specific providers. Business generators describe <em>what</em>
 * to write ({@link Kind} + namespace + path + JSON); this class maps each kind to the running node's
 * directory layout (plural folders on 1.20.1, singular from 1.21 on; {@code forge/} vs
 * {@code neoforge/} biome modifiers; item definitions only on 26.1) and writes the files.
 * <p>
 * A generator that throws fails the data run; use that for content checks.
 */
public final class PlatformDatagen {
    private static final List<Generator> GENERATORS = new ArrayList<>();

    private PlatformDatagen() {
    }

    /** Logical kinds of generated files. */
    public enum Kind {
        /** {@code assets/<ns>/lang/<path>.json}. */
        LANG,
        /** {@code assets/<ns>/blockstates/<path>.json}. */
        BLOCKSTATE,
        /** {@code assets/<ns>/models/<path>.json} ({@code path} starts with {@code item/} or {@code block/}). */
        MODEL,
        /** {@code assets/<ns>/items/<path>.json}; written on 26.1 only. */
        ITEM_DEFINITION,
        /** Item tag. */
        ITEM_TAG,
        /** Block tag. */
        BLOCK_TAG,
        /** Fluid tag. */
        FLUID_TAG,
        /** Block loot table ({@code path} without {@code blocks/}). */
        BLOCK_LOOT,
        /** Recipe. */
        RECIPE,
        /** {@code data/<ns>/worldgen/<path>.json} ({@code path} starts with the worldgen registry, e.g. {@code placed_feature/}). */
        WORLDGEN,
        /** Loader biome modifier ({@code forge/} or {@code neoforge/} folder). */
        BIOME_MODIFIER,
        /** {@code data/<ns>/<path>.json}, same on every node. */
        DATA,
        /** {@code assets/<ns>/<path>.json}, same on every node. */
        ASSET
    }

    /** Receives generated files. */
    public interface Sink {
        /** Writes a file the way vanilla data providers do (sorted keys, {@code null} members left out). */
        void json(Kind kind, String namespace, String path, JsonElement json);

        /** Writes a file whose {@code null} members carry meaning, keeping them (members in insertion order). */
        void jsonKeepingNulls(Kind kind, String namespace, String path, JsonElement json);

        /**
         * Writes a text report for the designers to {@code build/reports/foodindustrial/<name>} of the
         * node project; it is not part of the resources.
         */
        void report(String name, String text);

        /**
         * JSON ops that know the game's registries: encode and decode recipe parts with them so tag
         * ingredients stay tags on every node.
         */
        DynamicOps<JsonElement> ops();

        /** Item ingredient of an item id or {@code #tag}, built against the data run's registries. */
        Ingredient ingredient(String itemOrTag);
    }

    private static final Gson KEEPING_NULLS = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();

    private record Generator(String name, Consumer<Sink> body) {
    }

    /** Registers a generator; it runs once per data run. */
    public static void register(String name, Consumer<Sink> generator) {
        GENERATORS.add(new Generator(name, generator));
    }

    /** Namespace of the common tags: {@code forge} on 1.20.1, {@code c} from 1.21 on. */
    public static String commonTagNamespace() {
        //? if <1.21 {
        return "forge";
        //?} else {
        /*return "c";
        *///?}
    }

    /**
     * The loader's common fluid tag for gases: NeoForge's {@code c:gaseous} from 1.21 on. Forge 1.20.1
     * has no such tag, so the result is empty there.
     */
    public static Optional<String> gaseousFluidTag() {
        //? if <1.21 {
        return Optional.empty();
        //?} else {
        /*return Optional.of("c:gaseous");
        *///?}
    }

    /**
     * The common item tag for buckets of the fluid {@code name}: {@code c:buckets/<name>} from 1.21 on
     * (the convention of NeoForge and its mods). 1.20.1 has no bucket tags, so the result is empty there.
     */
    public static Optional<String> bucketTag(String name) {
        //? if <1.21 {
        return Optional.empty();
        //?} else {
        /*return Optional.of("c:buckets/" + name);
        *///?}
    }

    /** A uniform int provider ({@code minecraft:uniform}); 1.20.1 nests the bounds under {@code value}. */
    public static JsonObject uniformInt(int min, int max) {
        JsonObject bounds = new JsonObject();
        bounds.addProperty("min_inclusive", min);
        bounds.addProperty("max_inclusive", max);
        JsonObject provider = new JsonObject();
        provider.addProperty("type", "minecraft:uniform");
        //? if forge {
        provider.add("value", bounds);
        //?} else {
        /*bounds.entrySet().forEach(entry -> provider.add(entry.getKey(), entry.getValue()));
        *///?}
        return provider;
    }

    /**
     * Adds what a {@code minecraft:tree} config places below the trunk, like vanilla oak: dirt via
     * {@code dirt_provider} and {@code force_dirt} before 26.1, {@code below_trunk_provider} from 26.1 on.
     */
    public static void treeBelowTrunk(JsonObject treeConfig) {
        JsonObject state = new JsonObject();
        state.addProperty("Name", "minecraft:dirt");
        JsonObject dirt = new JsonObject();
        dirt.addProperty("type", "minecraft:simple_state_provider");
        dirt.add("state", state);
        //? if >=26.1 {
        /*JsonObject keep = new JsonObject();
        keep.addProperty("type", "minecraft:matching_block_tag");
        keep.addProperty("tag", "minecraft:cannot_replace_below_tree_trunk");
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "minecraft:not");
        condition.add("predicate", keep);
        JsonObject rule = new JsonObject();
        rule.add("if_true", condition);
        rule.add("then", dirt);
        JsonArray rules = new JsonArray();
        rules.add(rule);
        JsonObject provider = new JsonObject();
        provider.addProperty("type", "minecraft:rule_based_state_provider");
        provider.add("rules", rules);
        treeConfig.add("below_trunk_provider", provider);
        *///?} else {
        treeConfig.add("dirt_provider", dirt);
        treeConfig.addProperty("force_dirt", false);
        //?}
    }

    /** A recipe ingredient for an item id or a {@code #tag}. */
    public static JsonElement ingredient(String itemOrTag) {
        //? if >=26.1 {
        /*return new JsonPrimitive(itemOrTag);
        *///?} else {
        JsonObject ingredient = new JsonObject();
        if (itemOrTag.startsWith("#")) {
            ingredient.addProperty("tag", itemOrTag.substring(1));
        } else {
            ingredient.addProperty("item", itemOrTag);
        }
        return ingredient;
        //?}
    }

    /** A crafting recipe result ({@code item} on 1.20.1, {@code id} from 1.20.5 on). */
    public static JsonObject recipeResult(String item, int count) {
        JsonObject result = new JsonObject();
        //? if forge {
        result.addProperty("item", item);
        //?} else {
        /*result.addProperty("id", item);
        *///?}
        result.addProperty("count", count);
        return result;
    }

    /** {@code match_tool} predicate: the tool has Silk Touch. */
    public static JsonObject toolHasSilkTouch() {
        JsonObject levels = new JsonObject();
        levels.addProperty("min", 1);
        JsonObject enchantment = new JsonObject();
        enchantment.add("levels", levels);
        JsonArray list = new JsonArray();
        list.add(enchantment);
        JsonObject predicate = new JsonObject();
        //? if forge {
        enchantment.addProperty("enchantment", "minecraft:silk_touch");
        predicate.add("enchantments", list);
        //?} else {
        /*enchantment.addProperty("enchantments", "minecraft:silk_touch");
        JsonObject components = new JsonObject();
        components.add("minecraft:enchantments", list);
        predicate.add("predicates", components);
        *///?}
        return predicate;
    }

    /** {@code match_tool} predicate: the tool is shears. */
    public static JsonObject toolIsShears() {
        JsonObject predicate = new JsonObject();
        //? if forge {
        JsonArray items = new JsonArray();
        items.add("minecraft:shears");
        predicate.add("items", items);
        //?} else {
        /*predicate.addProperty("items", "minecraft:shears");
        *///?}
        return predicate;
    }

    /**
     * The {@code render_type} a block model needs for cut-out textures ({@code minecraft:cutout} or
     * {@code minecraft:cutout_mipped}) before 26.1; empty from 26.1 on, where the layer follows the
     * texture's transparency.
     */
    public static Optional<String> cutoutRenderType(boolean mipped) {
        //? if >=26.1 {
        /*return Optional.empty();
        *///?} else {
        return Optional.of(mipped ? "minecraft:cutout_mipped" : "minecraft:cutout");
        //?}
    }

    /** Loader namespace for loader-specific data ({@code forge} / {@code neoforge}). */
    public static String loaderNamespace() {
        //? if forge {
        return "forge";
        //?} else {
        /*return "neoforge";
        *///?}
    }

    /**
     * Model of a mod bucket: the loader's dynamic bucket (vanilla bucket with the fluid's still texture
     * in its opening, tinted like the fluid). Writes {@code models/item/<bucket>.json} up to 1.21.1
     * and the item definition {@code items/<bucket>.json} from 26.1.
     *
     * @param bucket bucket item path, e.g. {@code bittern_bucket}
     * @param fluid  source fluid id, e.g. {@code foodindustrial:bittern}
     */
    public static void bucketModel(Sink sink, String namespace, String bucket, String fluid) {
        JsonObject json = new JsonObject();
        //? if >=26.1 {
        /*JsonObject textures = new JsonObject();
        textures.addProperty("base", "minecraft:item/bucket");
        textures.addProperty("fluid", "neoforge:item/mask/bucket_fluid");
        JsonObject model = new JsonObject();
        model.addProperty("type", "neoforge:fluid_container");
        model.add("textures", textures);
        model.addProperty("fluid", fluid);
        json.add("model", model);
        sink.json(Kind.ITEM_DEFINITION, namespace, bucket, json);
        *///?} else {
        json.addProperty("parent", loaderNamespace() + ":item/bucket");
        json.addProperty("loader", loaderNamespace() + ":fluid_container");
        json.addProperty("fluid", fluid);
        sink.json(Kind.MODEL, namespace, "item/" + bucket, json);
        //?}
    }

    /** Whether the node uses item definition files ({@code assets/<ns>/items}). */
    public static boolean usesItemDefinitions() {
        //? if >=26.1 {
        /*return true;
        *///?} else {
        return false;
        //?}
    }

    /** Path of a file of {@code kind} relative to the pack root, or {@code null} if this node does not use it. */
    public static String relativePath(Kind kind, String namespace, String path) {
        boolean plural = commonTagNamespace().equals("forge");
        return switch (kind) {
            case LANG -> "assets/" + namespace + "/lang/" + path + ".json";
            case BLOCKSTATE -> "assets/" + namespace + "/blockstates/" + path + ".json";
            case MODEL -> "assets/" + namespace + "/models/" + path + ".json";
            case ITEM_DEFINITION -> usesItemDefinitions() ? "assets/" + namespace + "/items/" + path + ".json" : null;
            case ITEM_TAG -> "data/" + namespace + "/tags/" + (plural ? "items/" : "item/") + path + ".json";
            case BLOCK_TAG -> "data/" + namespace + "/tags/" + (plural ? "blocks/" : "block/") + path + ".json";
            case FLUID_TAG -> "data/" + namespace + "/tags/" + (plural ? "fluids/" : "fluid/") + path + ".json";
            case BLOCK_LOOT -> "data/" + namespace + "/" + (plural ? "loot_tables" : "loot_table") + "/blocks/" + path + ".json";
            case RECIPE -> "data/" + namespace + "/" + (plural ? "recipes/" : "recipe/") + path + ".json";
            case WORLDGEN -> "data/" + namespace + "/worldgen/" + path + ".json";
            case BIOME_MODIFIER -> "data/" + namespace + "/" + loaderNamespace() + "/biome_modifier/" + path + ".json";
            case DATA -> "data/" + namespace + "/" + path + ".json";
            case ASSET -> "assets/" + namespace + "/" + path + ".json";
        };
    }

    private static DataProvider provider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, Generator generator) {
        Path root = output.getOutputFolder();
        return new DataProvider() {
            @Override
            public CompletableFuture<?> run(CachedOutput cache) {
                return lookup.thenCompose(registries -> write(cache, registries));
            }

            private CompletableFuture<?> write(CachedOutput cache, HolderLookup.Provider registries) {
                //? if >=1.21 {
                /*DynamicOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
                *///?} else {
                DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
                //?}
                List<CompletableFuture<?>> writes = new ArrayList<>();
                generator.body().accept(new Sink() {
                    @Override
                    public DynamicOps<JsonElement> ops() {
                        return ops;
                    }

                    @Override
                    public Ingredient ingredient(String itemOrTag) {
                        return datagenIngredient(registries, itemOrTag);
                    }

                    @Override
                    public void json(Kind kind, String namespace, String path, JsonElement json) {
                        String relative = relativePath(kind, namespace, path);
                        if (relative != null) {
                            writes.add(DataProvider.saveStable(cache, json, root.resolve(relative)));
                        }
                    }

                    @Override
                    public void jsonKeepingNulls(Kind kind, String namespace, String path, JsonElement json) {
                        String relative = relativePath(kind, namespace, path);
                        if (relative != null) {
                            writes.add(saveKeepingNulls(cache, json, root.resolve(relative)));
                        }
                    }

                    @Override
                    public void report(String name, String text) {
                        // root is <node project>/src/generated/resources
                        Path file = root.resolve("../../../build/reports/foodindustrial").normalize().resolve(name);
                        try {
                            Files.createDirectories(file.getParent());
                            Files.writeString(file, text, StandardCharsets.UTF_8);
                        } catch (IOException exception) {
                            throw new UncheckedIOException(exception);
                        }
                    }
                });
                return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
            }

            @Override
            public String getName() {
                return "Food Industrial: " + generator.name();
            }
        };
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    private static CompletableFuture<?> saveKeepingNulls(CachedOutput cache, JsonElement json, Path path) {
        return CompletableFuture.runAsync(() -> {
            byte[] bytes = (KEEPING_NULLS.toJson(json) + "\n").getBytes(StandardCharsets.UTF_8);
            try {
                cache.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        });
    }

    /**
     * Ingredient built the way the node's own recipe providers do, so it encodes with the data run's
     * registry ops (26.1 checks that a tag belongs to the registries it is written with).
     */
    private static Ingredient datagenIngredient(HolderLookup.Provider registries, String itemOrTag) {
        if (itemOrTag.startsWith("#")) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, PlatformIdentifier.parse(itemOrTag.substring(1)));
            //? if >=26.1 {
            /*return Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(tag));
            *///?} else {
            return Ingredient.of(tag);
            //?}
        }
        Item item = BuiltInRegistries.ITEM.getOptional(PlatformIdentifier.parse(itemOrTag))
            .orElseThrow(() -> new IllegalArgumentException("Unknown item " + itemOrTag));
        return Ingredient.of(item);
    }

    /** Hooks the data run. Called by the mod entrypoint. */
    public static void bind(IEventBus modBus) {
        //? if >=26.1 {
        /*modBus.addListener((GatherDataEvent.Client event) -> addProviders(event));
        *///?} else {
        modBus.addListener(PlatformDatagen::addProviders);
        //?}
    }

    private static void addProviders(GatherDataEvent event) {
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        for (Generator generator : GENERATORS) {
            event.getGenerator().addProvider(true, provider(event.getGenerator().getPackOutput(), lookup, generator));
        }
    }
}
