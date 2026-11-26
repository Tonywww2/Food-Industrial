package com.tonywww.foodindustrial.world.source;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The I44 generation list ({@code world_sources.json}, exported by {@code tools/world/sources.py}): crops, wild
 * herbs, trees and bushes with the parts they give, their biomes and the new raw items (additives/08 §C 来源规则).
 */
public final class SourceTable {
    public static final String RESOURCE = "/foodindustrial/world_sources.json";

    /** {@code count} of an item, uniform between {@code min} and {@code max}. */
    public record Drop(String item, int min, int max) {
    }

    /** A group of vanilla biomes with its names for texts. */
    public record Biomes(String id, List<String> biomes, String zh, String en) {
    }

    /** A raw item registered by the world code (no material entry): names, tag under {@code materials/}, texture kind, colour. */
    public record NewItem(String id, String zh, String en, String tag, String texture, String colour) {
    }

    /** A farmland crop planted with its first drop (2026-11-19: 产物即种子). */
    public record Crop(String id, String zh, String en, List<Drop> drops, String leaf, String colour, int level, String climate,
                       @Nullable String wild) {
        public String plant() {
            return drops.get(0).item();
        }
    }

    /** A wild herb: products when grown; {@code sand}: also grows on sand. */
    public record Wild(String id, String zh, String en, List<Drop> drops, String biome, String leaf, String colour, int rarity,
                       boolean sand) {
    }

    /**
     * A tree or bush. {@code trunk}: a vanilla wood type or {@code own}; {@code form}: tree, conifer, palm or bush.
     * {@code tap}: what tapping the own log with a knife gives ({@code item}, {@code bottled} item, or {@code fluid} in mB).
     */
    public record Tree(String id, String zh, String en, String biome, String trunk, String form, List<Drop> flower, List<Drop> fruit,
                       List<Drop> leaf, List<Drop> strip, List<Drop> wood, @Nullable Tap tap, String leaves, String bloom, String colour,
                       @Nullable String bark, int rarity) {
        public boolean bush() {
            return form.equals("bush");
        }

        public boolean ownLog() {
            return trunk.equals("own");
        }
    }

    public record Tap(String result, String kind, int amount) {
    }

    /** Extra loot of a vanilla block of the same species (acorns from oak leaves, carrot seeds). */
    public record VanillaDrop(String table, String item, double chance, @Nullable Integer age, @Nullable String block, String zh, String en) {
    }

    /**
     * An item added to a vanilla loot table: a weighted entry of a fishing table, or a pool with a chance. {@code zh},
     * {@code en}: its JEI info text, empty when another source writes it, null for the default (fished up or in shipwrecks).
     */
    public record Loot(String table, String item, int weight, double chance, @Nullable String zh, @Nullable String en) {
    }

    /** An insect scraped off {@code blocks} with a knife (W02). */
    public record Scrape(String item, List<String> blocks, String zh, String en) {
    }

    /** A mod animal on a vanilla body: its drop, biome groups and spawn (weight, pack size); {@code lays}: the egg it lays, if any. */
    public record Animal(String id, String body, String zh, String en, Drop drop, List<String> biomes, int weight, int min, int max,
                         String colour, String marks, @Nullable String lays) {
    }

    /** A tag linked to other mods: common tag paths, item ids, own tags, convention-specific entries. */
    public record Linked(String tag, List<String> common, List<String> items, List<String> own, List<String> forge, List<String> c) {
    }

    /** An information page written in the list instead of from the entries (an item with sources outside it too). */
    public record Info(String zh, String en) {
    }

    public record Data(Map<String, Biomes> biomes, Map<String, List<String>> villages, Map<String, NewItem> items,
                       Map<String, List<String>> extraTags, List<Crop> crops, List<Wild> wild, List<Tree> trees,
                       List<Drop> gardeniaFlower, List<Drop> gardeniaFruit, List<VanillaDrop> vanillaDrops, Map<String, String> smoked,
                       List<Loot> loot, List<Linked> linked, List<Scrape> scrape, Map<String, List<Drop>> organs, List<Animal> animals,
                       Map<String, Info> info) {
    }

    private static Data data;

    private SourceTable() {
    }

    public static Data get() {
        if (data == null) {
            data = load();
        }
        return data;
    }

    private static Data load() {
        try (InputStream in = SourceTable.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE + "; run tools/world/sources.py");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return parse(JsonParser.parseReader(reader).getAsJsonObject());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + RESOURCE, exception);
        }
    }

    private static Data parse(JsonObject root) {
        Map<String, Biomes> biomes = new LinkedHashMap<>();
        root.getAsJsonObject("biomes").entrySet().forEach(entry -> {
            JsonObject group = entry.getValue().getAsJsonObject();
            biomes.put(entry.getKey(), new Biomes(entry.getKey(), strings(group.getAsJsonArray("biomes")),
                group.get("zh").getAsString(), group.get("en").getAsString()));
        });
        Map<String, List<String>> villages = new LinkedHashMap<>();
        root.getAsJsonObject("villages").entrySet().forEach(entry -> villages.put(entry.getKey(), strings(entry.getValue().getAsJsonArray())));
        Map<String, NewItem> items = new LinkedHashMap<>();
        root.getAsJsonObject("items").entrySet().forEach(entry -> {
            JsonObject item = entry.getValue().getAsJsonObject();
            items.put(entry.getKey(), new NewItem(entry.getKey(), text(item, "zh"), text(item, "en"), text(item, "tag"), text(item, "texture"),
                text(item, "colour")));
        });
        Map<String, List<String>> extraTags = new LinkedHashMap<>();
        root.getAsJsonObject("extra_tags").entrySet().forEach(entry -> extraTags.put(entry.getKey(), strings(entry.getValue().getAsJsonArray())));
        List<Crop> crops = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("crops")) {
            JsonObject c = element.getAsJsonObject();
            crops.add(new Crop(text(c, "id"), text(c, "zh"), text(c, "en"), drops(c.getAsJsonArray("drops")), text(c, "leaf"), text(c, "colour"),
                c.get("level").getAsInt(), text(c, "climate"), optional(c, "wild")));
        }
        List<Wild> wild = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("wild")) {
            JsonObject w = element.getAsJsonObject();
            wild.add(new Wild(text(w, "id"), text(w, "zh"), text(w, "en"), drops(w.getAsJsonArray("drops")), text(w, "biome"), text(w, "leaf"),
                text(w, "colour"), w.get("rarity").getAsInt(), w.get("sand").getAsBoolean()));
        }
        List<Tree> trees = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("trees")) {
            JsonObject t = element.getAsJsonObject();
            Tap tap = null;
            if (t.has("tap") && t.get("tap").isJsonArray()) {
                JsonArray array = t.getAsJsonArray("tap");
                tap = new Tap(array.get(0).getAsString(), array.get(1).getAsString(), array.get(2).getAsInt());
            }
            trees.add(new Tree(text(t, "id"), text(t, "zh"), text(t, "en"), text(t, "biome"), text(t, "trunk"), text(t, "form"),
                drops(t.getAsJsonArray("flower")), drops(t.getAsJsonArray("fruit")), drops(t.getAsJsonArray("leaf")),
                drops(t.getAsJsonArray("strip")), drops(t.getAsJsonArray("wood")), tap, text(t, "leaves"), text(t, "bloom"),
                text(t, "colour"), optional(t, "bark"), t.get("rarity").getAsInt()));
        }
        JsonObject gardenia = root.getAsJsonObject("gardenia");
        List<VanillaDrop> vanillaDrops = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("vanilla_drops")) {
            JsonObject d = element.getAsJsonObject();
            vanillaDrops.add(new VanillaDrop(text(d, "table"), text(d, "item"), d.get("chance").getAsDouble(),
                d.has("age") ? d.get("age").getAsInt() : null, optional(d, "block"), text(d, "zh"), text(d, "en")));
        }
        Map<String, String> smoked = new LinkedHashMap<>();
        for (JsonElement element : root.getAsJsonArray("smoked")) {
            smoked.put(text(element.getAsJsonObject(), "input"), text(element.getAsJsonObject(), "result"));
        }
        List<Loot> loot = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("loot")) {
            JsonObject l = element.getAsJsonObject();
            loot.add(new Loot(text(l, "table"), text(l, "item"), l.has("weight") ? l.get("weight").getAsInt() : 0,
                l.has("chance") ? l.get("chance").getAsDouble() : 0, l.has("zh") ? text(l, "zh") : null, l.has("en") ? text(l, "en") : null));
        }
        List<Linked> linked = new ArrayList<>();
        root.getAsJsonObject("linked").entrySet().forEach(entry -> {
            JsonObject l = entry.getValue().getAsJsonObject();
            linked.add(new Linked(entry.getKey(), list(l, "common"), list(l, "items"), list(l, "own"), list(l, "forge"), list(l, "c")));
        });
        List<Scrape> scrape = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("scrape")) {
            JsonObject sc = element.getAsJsonObject();
            scrape.add(new Scrape(text(sc, "item"), strings(sc.getAsJsonArray("blocks")), text(sc, "zh"), text(sc, "en")));
        }
        Map<String, Info> info = new LinkedHashMap<>();
        root.getAsJsonObject("info").entrySet().forEach(entry ->
            info.put(entry.getKey(), new Info(text(entry.getValue().getAsJsonObject(), "zh"), text(entry.getValue().getAsJsonObject(), "en"))));
        return new Data(biomes, villages, items, extraTags, List.copyOf(crops), List.copyOf(wild), List.copyOf(trees),
            drops(gardenia.getAsJsonArray("flower")), drops(gardenia.getAsJsonArray("fruit")), List.copyOf(vanillaDrops), smoked,
            List.copyOf(loot), List.copyOf(linked), List.copyOf(scrape), organs(root.getAsJsonObject("organs")), animals(root.getAsJsonArray("animals")),
            info);
    }

    private static List<Animal> animals(JsonArray array) {
        List<Animal> animals = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject a = element.getAsJsonObject();
            JsonArray drop = a.getAsJsonArray("drop");
            animals.add(new Animal(text(a, "id"), text(a, "body"), text(a, "zh"), text(a, "en"),
                new Drop(drop.get(0).getAsString(), drop.get(1).getAsInt(), drop.get(2).getAsInt()), strings(a.getAsJsonArray("biomes")),
                a.get("weight").getAsInt(), a.get("min").getAsInt(), a.get("max").getAsInt(), text(a, "colour"), text(a, "marks"),
                a.has("lays") ? text(a, "lays") : null));
        }
        return List.copyOf(animals);
    }

    private static Map<String, List<Drop>> organs(JsonObject json) {
        Map<String, List<Drop>> organs = new LinkedHashMap<>();
        json.entrySet().forEach(entry -> {
            List<Drop> parts = new ArrayList<>();
            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                JsonArray part = element.getAsJsonArray();
                parts.add(new Drop(part.get(0).getAsString(), part.get(1).getAsInt(), part.get(1).getAsInt()));
            }
            organs.put(entry.getKey(), List.copyOf(parts));
        });
        return organs;
    }

    private static List<String> list(JsonObject object, String key) {
        return object.has(key) ? strings(object.getAsJsonArray(key)) : List.of();
    }

    private static List<Drop> drops(JsonArray array) {
        List<Drop> result = new ArrayList<>();
        for (JsonElement element : array) {
            JsonArray drop = element.getAsJsonArray();
            result.add(new Drop(drop.get(0).getAsString(), drop.get(1).getAsInt(), drop.get(2).getAsInt()));
        }
        return List.copyOf(result);
    }

    private static List<String> strings(JsonArray array) {
        List<String> result = new ArrayList<>();
        array.forEach(element -> result.add(element.getAsString()));
        return List.copyOf(result);
    }

    private static String text(JsonObject object, String key) {
        return object.get(key).getAsString();
    }

    @Nullable
    private static String optional(JsonObject object, String key) {
        return object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsString() : null;
    }
}
