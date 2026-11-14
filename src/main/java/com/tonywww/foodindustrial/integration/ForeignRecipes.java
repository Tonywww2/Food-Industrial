package com.tonywww.foodindustrial.integration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.platform.compat.integration.PlatformIntegrationFormats;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The other mods' own recipes, read from their jars during data generation (I25, 2026-11-11, user): an
 * integration recipe leaves out the inputs a recipe of the machine's own mod already takes, so the other
 * mod's recipe is the one that runs for those. The build passes the jar list in the system property
 * {@code foodindustrial.externalJars} (a file, one path per line); without it nothing is left out.
 *
 * <p>Inputs are compared by what they can match: item and fluid ids and tags, tags expanded through
 * the jars' tag files and this mod's own planned tags. Two recipes overlap when they take the same number
 * of item and fluid inputs and every input of each shares something with an input of the other; ours
 * then loses the overlapping part ({@link #narrow}).
 */
final class ForeignRecipes {
    /**
     * A recipe of another mod: its id, inputs (each the set of ids, {@code item:}/{@code fluid:}, and tags it
     * names) and the mods its {@code mod_loaded} conditions ask for (Create's compat recipes for Thermal ores).
     */
    record Foreign(String id, List<Set<String>> items, List<Set<String>> fluids, Set<String> mods) {
    }

    private static final Set<String> OUTPUT_WORDS = Set.of("result", "output", "product");

    private final Map<String, List<Foreign>> byType = new HashMap<>();
    private final Map<String, Set<String>> itemTags = new HashMap<>();
    private final Map<String, Set<String>> fluidTags = new HashMap<>();
    private final boolean loaded;

    private ForeignRecipes(boolean loaded) {
        this.loaded = loaded;
    }

    /** Reads the jars named by the build, or an empty index without them. */
    static ForeignRecipes load(Set<String> types) {
        String list = System.getProperty("foodindustrial.externalJars");
        if (list == null || !Files.isRegularFile(Path.of(list))) {
            FoodIndustrial.LOGGER.warn("Integration: no jar list of the other mods; inputs that overlap their recipes are not left out");
            return new ForeignRecipes(false);
        }
        ForeignRecipes index = new ForeignRecipes(true);
        try {
            for (String line : Files.readAllLines(Path.of(list), StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    index.read(Path.of(line.trim()), types);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read the jar list " + list, e);
        }
        index.ownTags();
        return index;
    }

    boolean loaded() {
        return loaded;
    }

    private void read(Path jar, Set<String> types) throws IOException {
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith("data/") || !name.endsWith(".json")) {
                    continue;
                }
                String[] parts = name.split("/", 4);
                if (parts.length < 4) {
                    continue;
                }
                String namespace = parts[1];
                String rest = parts[2] + "/" + parts[3];
                JsonElement json;
                try (InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8)) {
                    json = JsonParser.parseReader(reader);
                } catch (RuntimeException e) {
                    continue;
                }
                if (!json.isJsonObject()) {
                    continue;
                }
                String path = rest.substring(rest.indexOf('/') + 1, rest.length() - ".json".length());
                if (rest.startsWith("recipes/") || rest.startsWith("recipe/")) {
                    recipe(namespace + ":" + path, json.getAsJsonObject(), types);
                } else if (rest.startsWith("tags/items/") || rest.startsWith("tags/item/")) {
                    tag(itemTags, namespace + ":" + path.substring(path.indexOf('/') + 1), json.getAsJsonObject(), "item:");
                } else if (rest.startsWith("tags/fluids/") || rest.startsWith("tags/fluid/")) {
                    tag(fluidTags, namespace + ":" + path.substring(path.indexOf('/') + 1), json.getAsJsonObject(), "fluid:");
                }
            }
        }
    }

    private static void tag(Map<String, Set<String>> tags, String id, JsonObject json, String prefix) {
        if (!json.has("values")) {
            return;
        }
        Set<String> values = tags.computeIfAbsent(id, key -> new LinkedHashSet<>());
        for (JsonElement value : json.getAsJsonArray("values")) {
            String text = value.isJsonObject() ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();
            values.add(text.startsWith("#") ? "tag:" + text.substring(1) : prefix + text);
        }
    }

    /** This mod's planned tags join the jars' (a common tag may hold this mod's items). */
    private void ownTags() {
        ModTags.planned(false).forEach((id, values) -> values.forEach(value -> itemTags.computeIfAbsent(id, key -> new LinkedHashSet<>())
            .add(value.id().startsWith("#") ? "tag:" + value.id().substring(1) : "item:" + value.id())));
        ModTags.planned(true).forEach((id, values) -> values.forEach(value -> fluidTags.computeIfAbsent(id, key -> new LinkedHashSet<>())
            .add(value.id().startsWith("#") ? "tag:" + value.id().substring(1) : "fluid:" + value.id())));
    }

    private void recipe(String id, JsonObject json, Set<String> types) {
        JsonElement type = json.get("type");
        if (type == null || !type.isJsonPrimitive() || !types.contains(type.getAsString())) {
            return;
        }
        List<Set<String>> items = new ArrayList<>();
        List<Set<String>> fluids = new ArrayList<>();
        inputs(json, items, fluids);
        byType.computeIfAbsent(type.getAsString(), key -> new ArrayList<>()).add(new Foreign(id, items, fluids, mods(json)));
    }

    /** The mods named by the top-level {@code mod_loaded} conditions of a recipe; other conditions are not read. */
    private static Set<String> mods(JsonObject json) {
        Set<String> mods = new LinkedHashSet<>();
        for (String key : List.of("conditions", "neoforge:conditions")) {
            if (json.has(key) && json.get(key).isJsonArray()) {
                for (JsonElement condition : json.getAsJsonArray(key)) {
                    if (condition.isJsonObject() && condition.getAsJsonObject().has("type") && condition.getAsJsonObject().has("modid")
                        && condition.getAsJsonObject().get("type").getAsString().endsWith(":mod_loaded")) {
                        mods.add(condition.getAsJsonObject().get("modid").getAsString());
                    }
                }
            }
        }
        return mods;
    }

    /**
     * Collects the ingredients under {@code element}, skipping every key that names an output. An object
     * with an amount and a fluid or tag is a fluid; one with an item or tag is an item.
     */
    private static void inputs(JsonElement element, List<Set<String>> items, List<Set<String>> fluids) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                inputs(child, items, fluids);
            }
            return;
        }
        if (!element.isJsonObject()) {
            return;
        }
        JsonObject object = element.getAsJsonObject();
        Set<String> fluid = fluidKeys(object);
        if (fluid != null) {
            fluids.add(fluid);
            return;
        }
        Set<String> item = itemKeys(object);
        if (item != null) {
            items.add(item);
            return;
        }
        for (Map.Entry<String, JsonElement> field : object.entrySet()) {
            String key = field.getKey().toLowerCase(java.util.Locale.ROOT);
            if (OUTPUT_WORDS.stream().anyMatch(key::contains) || key.equals("type") || key.equals("conditions")
                || key.equals("neoforge:conditions")) {
                continue;
            }
            // a list under a singular key ("ingredient", "input", Thermal's "value") is one ingredient that
            // takes any of its items; under a plural key ("ingredients", "itemInputs") each is its own
            if (field.getValue().isJsonArray() && !key.endsWith("s") && isAlternatives(field.getValue().getAsJsonArray())) {
                Set<String> any = new LinkedHashSet<>();
                for (JsonElement child : field.getValue().getAsJsonArray()) {
                    any.addAll(itemKeys(child.getAsJsonObject()));
                }
                items.add(any);
                continue;
            }
            inputs(field.getValue(), items, fluids);
        }
    }

    private static boolean isAlternatives(JsonArray array) {
        for (JsonElement child : array) {
            if (!child.isJsonObject() || itemKeys(child.getAsJsonObject()) == null) {
                return false;
            }
        }
        return !array.isEmpty();
    }

    @Nullable
    private static Set<String> fluidKeys(JsonObject object) {
        if (!object.has("amount") || object.has("item") || object.has("ingredient")) {
            return null;
        }
        for (String key : List.of("fluid", "fluid_tag", "fluidTag", "tag")) {
            if (object.has(key) && object.get(key).isJsonPrimitive()) {
                String value = object.get(key).getAsString();
                return Set.of(key.equals("fluid") ? "fluid:" + value : "tag:" + value);
            }
        }
        return null;
    }

    @Nullable
    private static Set<String> itemKeys(JsonObject object) {
        if (object.has("item") && object.get("item").isJsonPrimitive()) {
            return Set.of("item:" + object.get("item").getAsString());
        }
        if (object.has("tag") && object.get("tag").isJsonPrimitive()) {
            return Set.of("tag:" + object.get("tag").getAsString());
        }
        return null;
    }

    /** Everything {@code keys} can match: the ids and tags themselves and the contents of the tags, all levels. */
    Set<String> expand(Set<String> keys, boolean fluid) {
        Map<String, Set<String>> tags = fluid ? fluidTags : itemTags;
        Set<String> result = new HashSet<>();
        List<String> queue = new ArrayList<>(keys);
        while (!queue.isEmpty()) {
            String key = queue.remove(queue.size() - 1);
            if (!result.add(key) || !key.startsWith("tag:")) {
                continue;
            }
            queue.addAll(tags.getOrDefault(key.substring(4), Set.of()));
        }
        return result;
    }

    /**
     * What becomes of one of our recipes on a machine whose own recipes take some of the same inputs:
     * {@code recipe} is ours without those inputs, or {@code null} when nothing is left or the overlap
     * cannot be cut out; {@code foreign} names the machine's recipes met and {@code kept} what stays.
     */
    record Narrowing(@Nullable ExternalRecipe recipe, List<String> foreign, String kept) {
    }

    /**
     * The machine's recipes that overlap ours: as many item and fluid inputs, and every input of each
     * shares something with an input of the other.
     */
    private List<Foreign> overlapping(String type, ExternalRecipe ours) {
        List<Set<String>> items = new ArrayList<>();
        for (ExternalRecipe.ItemIn item : ours.items()) {
            items.add(expand(keys(item.ingredient()), false));
        }
        List<Set<String>> fluids = new ArrayList<>();
        for (ExternalRecipe.FluidIn fluid : ours.fluids()) {
            fluids.add(expand(Set.of(fluidKey(fluid)), true));
        }
        List<Foreign> met = new ArrayList<>();
        for (Foreign foreign : byType.getOrDefault(type, List.of())) {
            if (foreign.items().size() != items.size() || foreign.fluids().size() != fluids.size()) {
                continue;
            }
            List<Set<String>> theirItems = foreign.items().stream().map(keys -> expand(keys, false)).toList();
            List<Set<String>> theirFluids = foreign.fluids().stream().map(keys -> expand(keys, true)).toList();
            if (covers(items, theirItems) && covers(theirItems, items) && covers(fluids, theirFluids) && covers(theirFluids, fluids)) {
                met.add(foreign);
            }
        }
        return met;
    }

    /** The mods that the machine's recipes overlapping ours need loaded (besides the machine's own). */
    Set<String> requiredMods(String type, ExternalRecipe ours) {
        Set<String> mods = new java.util.TreeSet<>();
        overlapping(type, ours).forEach(foreign -> mods.addAll(foreign.mods()));
        return mods;
    }

    /**
     * Our recipe without the inputs that the machine's own recipes already take (2026-11-11, user: the
     * other mod's recipe runs for those, ours for the rest), or {@code null} when none of them overlaps.
     * A recipe of the machine overlaps when it takes as many item and fluid inputs as ours and every input
     * of each shares something with an input of the other. Only a recipe with a single input can be cut:
     * an item input keeps its other items and tags (a tag that only partly overlaps stays, minus the
     * machine's inputs, through the loader's difference ingredient); a fluid input keeps what is left when
     * that is one fluid or one whole tag. Only the machine's recipes whose mods are all in {@code present}
     * count (a recipe for another mod's ore loads only with that mod).
     */
    @Nullable
    Narrowing narrow(String type, ExternalRecipe ours, Set<String> present) {
        List<Foreign> met = overlapping(type, ours).stream().filter(foreign -> present.containsAll(foreign.mods())).toList();
        if (met.isEmpty()) {
            return null;
        }
        List<String> ids = met.stream().map(Foreign::id).toList();
        List<ExternalRecipe.ItemIn> ourItems = ours.items();
        List<ExternalRecipe.FluidIn> ourFluids = ours.fluids();
        if (ourItems.size() + ourFluids.size() != 1) {
            return new Narrowing(null, ids, "");
        }
        boolean fluid = ourItems.isEmpty();
        Set<String> theirKeys = new LinkedHashSet<>();
        Set<String> theirs = new HashSet<>();
        for (Foreign foreign : met) {
            for (Set<String> keys : fluid ? foreign.fluids() : foreign.items()) {
                theirKeys.addAll(keys);
                theirs.addAll(expand(keys, fluid));
            }
        }
        List<String> kept = new ArrayList<>();
        boolean partial = false;
        boolean removed = false;
        for (String entry : flatten(fluid ? Set.of(fluidKey(ours.fluids().get(0))) : keys(ours.items().get(0).ingredient()), fluid)) {
            if (theirs.contains(entry)) {
                removed = true;
            } else {
                kept.add(entry);
                if (entry.startsWith("tag:") && !java.util.Collections.disjoint(expand(Set.of(entry), fluid), theirs)) {
                    partial = true;
                }
            }
        }
        String keptText = String.join("、", kept.stream().map(entry -> entry.startsWith("tag:") ? "#" + entry.substring(4) : entry.substring(entry.indexOf(':') + 1)).toList());
        if (kept.isEmpty() || !removed && !partial) {
            return new Narrowing(kept.isEmpty() ? null : ours, ids, keptText);
        }
        if (fluid) {
            List<String> left = kept.stream().filter(entry -> !entry.contains(":flowing_")).toList();
            if (partial || left.size() != 1) {
                return new Narrowing(null, ids, "");
            }
            String entry = left.get(0);
            ExternalRecipe.FluidIn in = ours.fluids().get(0);
            ExternalRecipe.FluidIn narrowed = entry.startsWith("tag:")
                ? new ExternalRecipe.FluidIn(entry.substring(4), null, in.amount()) : new ExternalRecipe.FluidIn(null, entry.substring(6), in.amount());
            return new Narrowing(ours.withFluids(List.of(narrowed)), ids, keptText);
        }
        ExternalRecipe.ItemIn in = ours.items().get(0);
        JsonElement narrowed;
        if (kept.size() == 1 && !partial) {
            narrowed = keyJson(kept.get(0));
        } else {
            JsonArray subtracted = new JsonArray();
            theirKeys.forEach(key -> subtracted.add(keyJson(key)));
            narrowed = PlatformIntegrationFormats.difference(base(in.ingredient()), subtracted.size() == 1 ? subtracted.get(0) : subtracted);
            keptText = keptText + "（除去" + String.join("、", theirKeys.stream().map(key -> key.startsWith("tag:") ? "#" + key.substring(4) : key.substring(5)).toList()) + "）";
        }
        return new Narrowing(ours.withItems(List.of(new ExternalRecipe.ItemIn(rebase(in.ingredient(), narrowed), in.count(), in.purity()))), ids, keptText);
    }

    /** The ids and tags an ingredient names, through a purity ingredient to its base. */
    private static Set<String> keys(JsonElement ingredient) {
        List<Set<String>> found = new ArrayList<>();
        inputs(base(ingredient), found, new ArrayList<>());
        Set<String> keys = new LinkedHashSet<>();
        found.forEach(keys::addAll);
        return keys;
    }

    private static String fluidKey(ExternalRecipe.FluidIn fluid) {
        return fluid.tag() != null ? "tag:" + fluid.tag() : "fluid:" + fluid.fluid();
    }

    /** This mod's own tags opened up into their values (other tags stay whole); ids and other mods' tags as they are. */
    private Set<String> flatten(Set<String> keys, boolean fluid) {
        Map<String, Set<String>> tags = fluid ? fluidTags : itemTags;
        Set<String> result = new LinkedHashSet<>();
        for (String key : keys) {
            if (key.startsWith("tag:" + FoodIndustrial.MOD_ID + ":") && tags.containsKey(key.substring(4))) {
                result.addAll(flatten(tags.get(key.substring(4)), fluid));
            } else {
                result.add(key);
            }
        }
        return result;
    }

    private static JsonObject keyJson(String key) {
        JsonObject json = new JsonObject();
        if (key.startsWith("tag:")) {
            json.addProperty("tag", key.substring(4));
        } else {
            json.addProperty("item", key.substring(key.indexOf(':') + 1));
        }
        return json;
    }

    /** The ingredient a purity ingredient wraps, or the ingredient itself. */
    private static JsonElement base(JsonElement ingredient) {
        return isPurity(ingredient) ? ingredient.getAsJsonObject().get("base") : ingredient;
    }

    /** {@code ingredient} with {@code base} in place of what it matches (inside a purity ingredient, its base). */
    private static JsonElement rebase(JsonElement ingredient, JsonElement base) {
        if (!isPurity(ingredient)) {
            return base;
        }
        JsonObject copy = ingredient.getAsJsonObject().deepCopy();
        copy.add("base", base);
        return copy;
    }

    private static boolean isPurity(JsonElement ingredient) {
        return ingredient.isJsonObject() && ingredient.getAsJsonObject().has("base")
            && ingredient.getAsJsonObject().has("type") && ingredient.getAsJsonObject().get("type").getAsString().equals("foodindustrial:purity");
    }

    /** Every set of {@code a} shares something with a set of {@code b}. */
    private static boolean covers(List<Set<String>> a, List<Set<String>> b) {
        for (Set<String> x : a) {
            boolean met = false;
            for (Set<String> y : b) {
                if (!java.util.Collections.disjoint(x, y)) {
                    met = true;
                    break;
                }
            }
            if (!met) {
                return false;
            }
        }
        return true;
    }
}
