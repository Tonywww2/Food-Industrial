package com.tonywww.foodindustrial.core.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Names of the item and fluid tags the mod generates, shown by EMI and other viewers (docs/tag-names.md, 2026-11-25,
 * user: tag names for EMI). Language key {@code tag.<item|fluid>.<namespace>.<path with dots>}. A tag is named by the
 * table of tag-names §2 ({@code tag_names.json}, {@code tools/text/export_tag_names.py}), else by the one name its own
 * entries have (items, fluids without the flowing ones, or this mod's tags it includes). Every tag of this mod's namespace
 * needs a name; the data run fails otherwise. Common tags ({@code c}, {@code forge}) get a name only from their entries.
 */
public final class TagNames {
    private static final String NS = FoodIndustrial.MOD_ID;
    private static final String RESOURCE = "/foodindustrial/tag_names.json";
    private static final List<String> KINDS = List.of("item", "fluid");
    /** Tag files written in this data run: kind → tag id → entries ({@code #tag} or id). */
    private static final Map<String, Map<String, List<String>>> WRITTEN = new LinkedHashMap<>();

    private TagNames() {
    }

    /** Records the tag files every generator writes. Called by {@link Lang#bootstrap()}. */
    static void bootstrap() {
        PlatformDatagen.addCheck(PlatformDatagen.Kind.ITEM_TAG, (id, json, ops) -> record("item", id, json));
        PlatformDatagen.addCheck(PlatformDatagen.Kind.FLUID_TAG, (id, json, ops) -> record("fluid", id, json));
    }

    private static synchronized void record(String kind, String id, JsonElement json) {
        List<String> entries = WRITTEN.computeIfAbsent(kind, key -> new TreeMap<>()).computeIfAbsent(id, key -> new ArrayList<>());
        for (JsonElement value : json.getAsJsonObject().getAsJsonArray("values")) {
            entries.add(value.isJsonPrimitive() ? value.getAsString() : value.getAsJsonObject().get("id").getAsString());
        }
    }

    /** Language key of a tag. */
    public static String key(String kind, String tag) {
        int colon = tag.indexOf(':');
        return "tag." + kind + "." + tag.substring(0, colon) + "." + tag.substring(colon + 1).replace('/', '.');
    }

    /** Adds the names of the recorded tags to the language entries (data run, before the language files are written). */
    static synchronized void addNames() {
        Map<String, Map<String, Name>> table = loadTable();
        List<String> missing = new ArrayList<>();
        int named = 0;
        for (String kind : KINDS) {
            Map<String, List<String>> tags = WRITTEN.getOrDefault(kind, Map.of());
            Resolver resolver = new Resolver(kind, tags, table.getOrDefault(kind, Map.of()));
            for (String tag : tags.keySet()) {
                String namespace = tag.substring(0, tag.indexOf(':'));
                if (!namespace.equals(NS) && !namespace.equals("c") && !namespace.equals("forge")) {
                    continue;
                }
                Name name = resolver.name(tag);
                if (name == null) {
                    if (namespace.equals(NS)) {
                        missing.add(kind + " " + tag);
                    }
                    continue;
                }
                if (name.chemical()) {
                    Lang.addName(key(kind, tag), name.zh(), name.en());
                } else {
                    Lang.add(key(kind, tag), name.zh(), name.en());
                }
                named++;
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Tags without a name (add them to docs/tag-names.md §2 and run tools/text/export_tag_names.py):\n  "
                + String.join("\n  ", missing));
        }
        FoodIndustrial.LOGGER.info("Tag names: {}", named);
    }

    /** A tag name; {@code chemical} when copied from a material's standard name (checked by the chemical name rules). */
    private record Name(String zh, String en, boolean chemical) {
    }

    /** Names the tags of one kind: the table first, else the single name of the tag's own entries. */
    private static final class Resolver {
        private final String kind;
        private final Map<String, List<String>> tags;
        private final Map<String, Name> table;
        private final Map<String, Name> done = new LinkedHashMap<>();
        private final Set<String> unnamed = new HashSet<>();
        private final Set<String> visiting = new HashSet<>();

        Resolver(String kind, Map<String, List<String>> tags, Map<String, Name> table) {
            this.kind = kind;
            this.tags = tags;
            this.table = table;
        }

        Name name(String tag) {
            if (done.containsKey(tag)) {
                return done.get(tag);
            }
            if (unnamed.contains(tag) || !visiting.add(tag)) {
                return null;
            }
            Name name = tag.startsWith(NS + ":") ? table.get(tag.substring(NS.length() + 1)) : null;
            if (name == null) {
                name = fromEntries(tags.getOrDefault(tag, List.of()));
            }
            visiting.remove(tag);
            if (name == null) {
                unnamed.add(tag);
            } else {
                done.put(tag, name);
            }
            return name;
        }

        /** The one name of the entries of this mod (null when there are none, several, or one has no name). */
        private Name fromEntries(List<String> entries) {
            Set<Name> names = new LinkedHashSet<>();
            for (String entry : entries) {
                boolean isTag = entry.startsWith("#");
                String id = isTag ? entry.substring(1) : entry;
                if (!id.startsWith(NS + ":")) {
                    continue;
                }
                Name name;
                if (isTag) {
                    name = name(id);
                } else {
                    String path = id.substring(NS.length() + 1);
                    if (kind.equals("fluid") && path.startsWith("flowing_")) {
                        continue;
                    }
                    name = kind.equals("fluid") ? langName(Lang.fluid(path)) : langName(Lang.item(path), Lang.block(path));
                }
                if (name == null) {
                    return null;
                }
                names.add(name);
            }
            return names.size() == 1 ? names.iterator().next() : null;
        }

        private static Name langName(String... keys) {
            for (String key : keys) {
                String zh = Lang.zh().get(key);
                if (zh != null) {
                    return new Name(zh, Lang.en().get(key), Lang.isChemicalName(key));
                }
            }
            return null;
        }
    }

    private static Map<String, Map<String, Name>> loadTable() {
        Map<String, Map<String, Name>> table = new LinkedHashMap<>();
        try (InputStream in = TagNames.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE);
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            for (String kind : KINDS) {
                Map<String, Name> names = new LinkedHashMap<>();
                JsonObject entries = root.has(kind) ? root.getAsJsonObject(kind) : new JsonObject();
                entries.entrySet().forEach(entry -> {
                    JsonObject name = entry.getValue().getAsJsonObject();
                    names.put(entry.getKey(), new Name(name.get("zh").getAsString(), name.get("en").getAsString(), false));
                });
                table.put(kind, names);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        return table;
    }
}
