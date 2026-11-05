package com.tonywww.foodindustrial.core.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Fixed process condition vocabulary (text standard §5, following machines/01 §3). Recipes store the
 * value keys; JEI, Patchouli and configs show the words through
 * {@code condition.foodindustrial.<category>.<value>}.
 */
public final class ConditionVocabulary {
    /** One word of the vocabulary. */
    public record Word(String category, String value, String zh, String en) {
        public String key() {
            return category + "." + value;
        }

        public String translationKey() {
            return Lang.condition(key());
        }
    }

    private static final Map<String, Word> WORDS = new LinkedHashMap<>();

    static {
        category("temperature",
            "ambient", "常温", "Ambient",
            "heated", "加热", "Heated",
            "high", "高温", "High temperature",
            "extreme", "极高温", "Extreme temperature",
            "cooling", "冷却", "Cooling",
            "freezing", "冷冻", "Freezing");
        category("pressure",
            "atmospheric", "常压", "Atmospheric pressure",
            "pressurized", "加压", "Pressurized",
            "high", "高压", "High pressure",
            "vacuum", "真空", "Vacuum");
        category("atmosphere",
            "air", "空气", "Air",
            "airtight", "隔绝空气", "Airtight",
            "inert", "惰性", "Inert",
            "gas", "指定气体", "Specified gas");
        category("sterile",
            "sterile", "无菌", "Sterile");
        category("method",
            "filtration", "过滤", "Filtration",
            "centrifuge", "离心", "Centrifugation",
            "settling", "沉降", "Settling",
            "open_flame", "明火", "Open flame",
            "solvent_recovery", "溶剂回收", "Solvent recovery",
            "supercritical", "超临界", "Supercritical",
            "vacuum", "真空", "Vacuum",
            "corrosion_resistant", "耐腐蚀", "Corrosion resistant",
            "ultraviolet", "紫外", "Ultraviolet",
            "polymerization", "聚合", "Polymerization",
            "internal_mixing", "密炼", "Internal mixing",
            "ion_exchange", "离子交换", "Ion exchange",
            "membrane", "膜", "Membrane",
            "electrodialysis", "电渗析", "Electrodialysis",
            "multi_fraction", "多馏分", "Multiple fractions");
        category("heat_sensitivity",
            "normal", "普通", "Normal",
            "sensitive", "热敏", "Heat sensitive",
            "very_sensitive", "极热敏", "Highly heat sensitive");
        category("environment",
            "open_sky", "露天", "Open sky",
            "clear_weather", "晴天", "Clear weather",
            "cold_biome", "寒冷群系", "Cold biome",
            "hot_biome", "炎热群系", "Hot biome",
            "ocean_biome", "海洋群系", "Ocean biome");
        category("manual_purity",
            "fixed", "定值", "Fixed",
            "refine", "提纯", "Refine",
            "keep", "保持", "Keep");
        category("random",
            "purity", "纯度随机（只用于红曲米）", "Random purity",
            "chance", "概率产出", "Chance output");
    }

    private ConditionVocabulary() {
    }

    private static void category(String category, String... triples) {
        for (int i = 0; i < triples.length; i += 3) {
            Word word = new Word(category, triples[i], triples[i + 1], triples[i + 2]);
            WORDS.put(word.key(), word);
        }
    }

    /** Categories that only label JEI and book entries and are never recipe conditions. */
    private static final Set<String> DISPLAY_ONLY = Set.of("manual_purity", "random");

    /**
     * Whether recipes may require values of {@code category} and devices may offer them
     * (machines/01 §3.1): every category of the table except the display-only ones. New conditions
     * only need new words here and in the device capabilities.
     */
    public static boolean isConditionCategory(String category) {
        if (DISPLAY_ONLY.contains(category)) {
            return false;
        }
        for (Word word : WORDS.values()) {
            if (word.category().equals(category)) {
                return true;
            }
        }
        return false;
    }

    /** Word for {@code category} and {@code value}, if it belongs to the vocabulary. */
    public static Optional<Word> find(String category, String value) {
        return Optional.ofNullable(WORDS.get(category + "." + value));
    }

    /** Values of one category, in table order. */
    public static List<Word> category(String category) {
        List<Word> result = new ArrayList<>();
        for (Word word : WORDS.values()) {
            if (word.category().equals(category)) {
                result.add(word);
            }
        }
        return result;
    }

    public static Map<String, Word> all() {
        return Collections.unmodifiableMap(WORDS);
    }

    /** Adds the language entries. Called once during mod construction. */
    public static void bootstrap() {
        for (Word word : WORDS.values()) {
            Lang.add(word.translationKey(), word.zh(), word.en());
        }
    }
}
