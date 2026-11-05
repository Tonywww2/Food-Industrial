package com.tonywww.foodindustrial.core.text;

import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Language keys (text standard §4) and the zh_cn / en_us entries of the mod. Features add their
 * entries during mod construction with {@link #add}; the data run writes both files after
 * {@link TextChecks} has accepted every entry.
 */
public final class Lang {
    private static final Map<String, String> ZH = new TreeMap<>();
    private static final Map<String, String> EN = new TreeMap<>();
    /** Professional name → common name (text standard §2.1), from the material table. */
    private static final Map<String, String> COMMON_NAMES = new TreeMap<>();

    private Lang() {
    }

    /** Adds one key in both languages. A key may be added only once. */
    public static void add(String key, String zh, String en) {
        if (ZH.containsKey(key)) {
            throw new IllegalStateException("Duplicate language key " + key);
        }
        ZH.put(key, zh);
        EN.put(key, en);
    }

    /**
     * Declares that {@code professional} has the common name {@code common}: its name must be written
     * {@code 专业名称（日常名称）} and the common name never stands alone (checked at data generation).
     */
    public static void addCommonName(String professional, String common) {
        COMMON_NAMES.put(professional, common);
    }

    public static Map<String, String> zh() {
        return Collections.unmodifiableMap(ZH);
    }

    public static Map<String, String> en() {
        return Collections.unmodifiableMap(EN);
    }

    public static String item(String id) {
        return "item." + FoodIndustrial.MOD_ID + "." + id;
    }

    public static String block(String id) {
        return "block." + FoodIndustrial.MOD_ID + "." + id;
    }

    public static String fluid(String id) {
        return "fluid_type." + FoodIndustrial.MOD_ID + "." + id;
    }

    /** Shift description of an item, block or fluid. */
    public static String description(String id) {
        return "tooltip." + FoodIndustrial.MOD_ID + "." + id + ".desc";
    }

    public static String jeiCategory(String recipeType) {
        return "jei." + FoodIndustrial.MOD_ID + ".category." + recipeType;
    }

    public static String condition(String key) {
        return "condition." + FoodIndustrial.MOD_ID + "." + key;
    }

    /** GUI keys of machines/04 §7, e.g. {@code gui("status", "output_full")}. */
    public static String gui(String group, String key) {
        return "gui." + FoodIndustrial.MOD_ID + "." + group + "." + key;
    }

    public static String creativeTab(String id) {
        return "itemGroup." + FoodIndustrial.MOD_ID + "." + id;
    }

    /** Registers the language generator. Called once from {@link FoodIndustrial#init()}. */
    public static void bootstrap() {
        PlatformDatagen.register("lang", sink -> {
            TextChecks.checkAll(ZH, EN, COMMON_NAMES);
            sink.json(PlatformDatagen.Kind.LANG, FoodIndustrial.MOD_ID, "zh_cn", toJson(ZH));
            sink.json(PlatformDatagen.Kind.LANG, FoodIndustrial.MOD_ID, "en_us", toJson(EN));
        });
    }

    private static JsonObject toJson(Map<String, String> entries) {
        JsonObject json = new JsonObject();
        entries.forEach(json::addProperty);
        return json;
    }
}
