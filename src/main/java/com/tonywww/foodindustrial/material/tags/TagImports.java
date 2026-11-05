package com.tonywww.foodindustrial.material.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads {@code /foodindustrial/tag_imports.json}: the tag contents listed in additives/08 §A/§B and the
 * catalyst tags of 09 chemicals (08 §C), exported by {@code tools/materials/export_materials.py}.
 */
public final class TagImports {
    public static final String RESOURCE = "/foodindustrial/tag_imports.json";

    /**
     * One tag of the mod's namespace and its listed entries per common-tag convention.
     *
     * @param tag   path in the {@code foodindustrial} namespace, e.g. {@code materials/kelp}
     * @param fluid whether it is a fluid tag
     * @param forge entries on nodes with {@code forge:} common tags (1.20.1)
     * @param c     entries on nodes with {@code c:} common tags (1.21 on)
     */
    public record Entry(String tag, boolean fluid, List<String> forge, List<String> c) {
        public Entry {
            forge = List.copyOf(forge);
            c = List.copyOf(c);
        }

        /** Entries for the common-tag namespace of the running node ({@code forge} or {@code c}). */
        public List<String> entries(String commonNamespace) {
            return switch (commonNamespace) {
                case "forge" -> forge;
                case "c" -> c;
                default -> throw new IllegalArgumentException("Unknown common tag namespace " + commonNamespace);
            };
        }
    }

    private TagImports() {
    }

    public static List<Entry> load() {
        try (InputStream in = TagImports.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE + "; run tools/materials/export_materials.py");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return parse(JsonParser.parseReader(reader).getAsJsonObject());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + RESOURCE, exception);
        }
    }

    static List<Entry> parse(JsonObject root) {
        List<Entry> result = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("tags")) {
            JsonObject object = element.getAsJsonObject();
            String kind = object.get("kind").getAsString();
            if (!kind.equals("item") && !kind.equals("fluid")) {
                throw new IllegalStateException("Unknown tag kind " + kind + " in " + RESOURCE);
            }
            result.add(new Entry(object.get("tag").getAsString(), kind.equals("fluid"),
                    strings(object.getAsJsonArray("forge")), strings(object.getAsJsonArray("c"))));
        }
        return Collections.unmodifiableList(result);
    }

    private static List<String> strings(JsonArray array) {
        List<String> result = new ArrayList<>();
        for (JsonElement element : array) {
            result.add(element.getAsString());
        }
        return result;
    }
}
