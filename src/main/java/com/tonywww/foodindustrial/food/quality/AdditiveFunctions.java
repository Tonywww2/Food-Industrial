package com.tonywww.foodindustrial.food.quality;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The GB 2760 function class of each additive material and the quality item each class improves (foods/04 §1, I39).
 * The classes come from {@code /foodindustrial/additive_functions.json} ({@code tools/foods/additive_functions.py},
 * read from additives/02); the class → item table is foods/04 §1. Classes outside the table (defoamers, gum bases,
 * enzymes, nutrients, processing aids, "other") improve no item by default: a recipe lists them by variety.
 */
public final class AdditiveFunctions {
    public static final String RESOURCE = "/foodindustrial/additive_functions.json";

    /** foods/04 §1: the classes counted for each item. Flavourings reach taste only as blended flavourings (I48). */
    private static final Map<String, QualityItem> ITEM_OF_CLASS = Map.ofEntries(
        Map.entry("acidity_regulator", QualityItem.TASTE), Map.entry("flavor_enhancer", QualityItem.TASTE),
        Map.entry("sweetener", QualityItem.TASTE),
        Map.entry("anticaking", QualityItem.STATE), Map.entry("leavening", QualityItem.STATE), Map.entry("emulsifier", QualityItem.STATE),
        Map.entry("flour_treatment", QualityItem.STATE), Map.entry("humectant", QualityItem.STATE), Map.entry("stabilizer", QualityItem.STATE),
        Map.entry("thickener", QualityItem.STATE),
        Map.entry("antioxidant", QualityItem.PRESERVATION), Map.entry("preservative", QualityItem.PRESERVATION),
        Map.entry("bleaching", QualityItem.COLOR), Map.entry("colorant", QualityItem.COLOR), Map.entry("color_retention", QualityItem.COLOR),
        Map.entry("glazing", QualityItem.COLOR));

    private static final Map<String, List<String>> CLASSES = load();
    private static final Map<String, String> CLASS_OF = invert(CLASSES);

    private AdditiveFunctions() {
    }

    /** Class names in table order. */
    public static Set<String> classes() {
        return CLASSES.keySet();
    }

    public static boolean isClass(String name) {
        return CLASSES.containsKey(name);
    }

    /** Materials of {@code className}. */
    public static List<String> members(String className) {
        return CLASSES.getOrDefault(className, List.of());
    }

    /** Function class of an additive material, or {@code null} when it is not a GB 2760 table entry. */
    @Nullable
    public static String classOf(@Nullable String material) {
        return material == null ? null : CLASS_OF.get(material);
    }

    /** The item a class improves by default (foods/04 §1), or {@code null}. */
    @Nullable
    public static QualityItem itemOf(@Nullable String className) {
        return className == null ? null : ITEM_OF_CLASS.get(className);
    }

    /** Classes improving {@code item} by default. */
    public static List<String> classesOf(QualityItem item) {
        return ITEM_OF_CLASS.entrySet().stream().filter(entry -> entry.getValue() == item).map(Map.Entry::getKey).sorted().toList();
    }

    /** The class → item table, for checks and pages. */
    public static Map<QualityItem, List<String>> table() {
        Map<QualityItem, List<String>> table = new EnumMap<>(QualityItem.class);
        for (QualityItem item : QualityItem.values()) {
            table.put(item, classesOf(item));
        }
        return table;
    }

    private static Map<String, List<String>> load() {
        try (InputStream in = AdditiveFunctions.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE + "; run tools/foods/additive_functions.py");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                JsonObject classes = JsonParser.parseReader(reader).getAsJsonObject().getAsJsonObject("classes");
                Map<String, List<String>> result = new LinkedHashMap<>();
                for (Map.Entry<String, JsonElement> entry : classes.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList());
                }
                return Collections.unmodifiableMap(result);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + RESOURCE, exception);
        }
    }

    private static Map<String, String> invert(Map<String, List<String>> classes) {
        Map<String, String> result = new HashMap<>();
        classes.forEach((name, members) -> members.forEach(material -> result.putIfAbsent(material, name)));
        return Collections.unmodifiableMap(result);
    }
}
