package com.tonywww.foodindustrial.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entries that several content groups add to the same vanilla tags (for example
 * {@code minecraft:mineable/pickaxe}). One data generator ({@code ModWorldData}) writes each tag file,
 * merging these in, so two generators never write the same file.
 */
public final class SharedTags {
    private static final Map<String, List<String>> BLOCKS = new LinkedHashMap<>();
    private static final Map<String, List<String>> ITEMS = new LinkedHashMap<>();

    private SharedTags() {
    }

    public static void block(String tag, String value) {
        BLOCKS.computeIfAbsent(tag, key -> new ArrayList<>()).add(value);
    }

    public static void item(String tag, String value) {
        ITEMS.computeIfAbsent(tag, key -> new ArrayList<>()).add(value);
    }

    public static Map<String, List<String>> blocks() {
        return BLOCKS;
    }

    public static Map<String, List<String>> items() {
        return ITEMS;
    }
}
