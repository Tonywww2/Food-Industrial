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
            "multi_fraction", "多馏分", "Multiple fractions",
            // M08 essential oil separator module (I14, 2026-11-10)
            "oil_separation", "油水分层", "Oil separation",
            // T2 modules (I15, additives/06 §T2 机械化：操作与实现规格, 2026-11-11)
            "lead_chamber", "铅室", "Lead chamber",
            "emulsifying", "乳化", "Emulsifying",
            // M22 module (I16, additives/06 §T3 化工：操作与实现规格, 2026-11-11)
            "molecular_distillation", "分子蒸馏", "Molecular distillation");
        category("heat_sensitivity",
            "normal", "普通", "Normal",
            "sensitive", "热敏", "Heat sensitive",
            "very_sensitive", "极热敏", "Highly heat sensitive");
        category("environment",
            "open_sky", "露天", "Open sky",
            "clear_weather", "晴天", "Clear weather",
            "cold_biome", "寒冷群系", "Cold biome",
            "hot_biome", "炎热群系", "Hot biome",
            "ocean_biome", "海洋群系", "Ocean biome",
            // T0 devices (I12, additives/07 §操作与实现规格, 2026-11-10)
            "sheltered", "不露天", "Sheltered",
            "rain", "淋雨", "In the rain",
            "daytime", "白天", "Daytime",
            "non_cold_biome", "非寒冷群系", "Non-cold biome",
            "jungle_biome", "丛林群系", "Jungle biome",
            "bamboo_biome", "竹林", "Bamboo jungle");
        // machine modes (I14, additives/06 §T1 作坊：操作与实现规格, 2026-11-10); recipes name them with "mode"
        category("mode",
            "dry_grinding", "干磨", "Dry grinding",
            "wet_grinding", "湿磨", "Wet grinding",
            "open", "开口", "Open",
            "sealed", "密闭", "Sealed",
            "anaerobic", "厌氧消化", "Anaerobic digestion",
            "koji_tray", "曲盘", "Koji trays",
            "shallow_tray", "浅盘", "Shallow trays",
            "calcining", "煅烧", "Calcining",
            "ashing", "烧灰", "Ashing",
            "dry_distillation", "干馏", "Dry distillation",
            "distillation", "蒸馏", "Distillation",
            "steam_distillation", "水蒸气蒸馏", "Steam distillation",
            "sublimation", "升华", "Sublimation",
            "cooling_crystallization", "冷却结晶", "Cooling crystallization",
            "sun_crystallization", "晾晒结晶", "Sun crystallization",
            "drying", "烘干", "Drying",
            "smoking", "烟熏", "Smoking",
            "sulfur_burning", "燃硫", "Sulfur burning",
            // T2 machine modes (I15, additives/06 §T2 机械化：操作与实现规格, 2026-11-11)
            "reaction", "常压反应", "Reaction",
            "polymerization", "聚合", "Polymerization",
            "concentration", "浓缩", "Concentration",
            "evaporative_crystallization", "蒸发结晶", "Evaporative crystallization",
            "isoelectric", "等电点", "Isoelectric precipitation",
            "freezing", "冷冻", "Freezing",
            "drum_drying", "滚筒", "Drum drying",
            "flash_drying", "气流", "Flash drying",
            "solvent_extraction", "溶剂浸提", "Solvent extraction",
            "liquid_extraction", "液液萃取", "Liquid-liquid extraction",
            "absorption", "吸收", "Absorption",
            "carbonation", "碳化", "Carbonation",
            "combustion", "燃烧除氧", "Combustion",
            "roasting", "焙烧", "Roasting",
            "condensation", "缩合", "Condensation",
            "melting", "熔融", "Melting",
            "alkali_fusion", "碱熔", "Alkali fusion",
            "activation", "活化", "Activation",
            "chlorination", "氯化", "Chlorination",
            "reduction", "还原", "Reduction",
            "blending", "配料", "Blending",
            "homogenizing", "均质", "Homogenizing",
            "normalizing", "归一", "Normalizing",
            // T3 machine modes (I16, additives/06 §T3 化工：操作与实现规格, 2026-11-11)
            "atmospheric_distillation", "常压精馏", "Atmospheric distillation",
            "vacuum_distillation", "减压精馏", "Vacuum distillation",
            "cryogenic_separation", "深冷分离", "Cryogenic separation",
            "stripping", "汽提", "Stripping",
            "hydrogenation", "加氢", "Hydrogenation",
            "carbonylation", "羰基化", "Carbonylation",
            "ammonia_synthesis", "合成氨", "Ammonia synthesis",
            "pressure_oxidation", "加压氧化", "Pressure oxidation",
            "epoxidation", "环氧化", "Epoxidation",
            "ethoxylation", "乙氧基化", "Ethoxylation",
            "chlor_alkali", "氯碱", "Chlor-alkali",
            "electroreduction", "电解还原", "Electroreduction",
            "electrooxidation", "电解氧化", "Electrooxidation",
            "water_electrolysis", "电解水", "Water electrolysis",
            "molten_salt", "熔盐电解", "Molten salt electrolysis",
            "adsorption", "吸附", "Adsorption",
            "fractionation", "分级", "Fractionation",
            "regeneration", "再生", "Regeneration",
            "burning", "燃烧", "Burning",
            "gas_catalysis", "气相催化", "Gas-phase catalysis",
            "cracking", "裂解", "Cracking",
            "fluidized_bed", "流化床", "Fluidized bed",
            // T4 machine modes (I17, additives/06 §T4 生物 / 精细：操作与实现规格, 2026-11-11)
            "aerated", "通气发酵", "Aerated fermentation",
            "isolation", "平板分离", "Plate isolation",
            "purification", "纯化", "Purification",
            "expansion", "扩培", "Expansion",
            "mutagenesis", "诱变", "Mutagenesis",
            "recombination", "基因重组", "Genetic recombination",
            "ultrafiltration", "超滤", "Ultrafiltration",
            "nanofiltration", "纳滤", "Nanofiltration",
            "reverse_osmosis", "反渗透", "Reverse osmosis",
            "bipolar_electrodialysis", "双极膜电渗析", "Bipolar membrane electrodialysis");
        category("manual_purity",
            "fixed", "定值", "Fixed",
            "refine", "提纯", "Refine",
            "keep", "保持", "Keep");
        category("purity_mode",
            "source", "来源", "Source",
            "removal", "除杂", "Impurity removal",
            "keep", "保持", "Keep",
            "degrade", "劣化", "Degradation");
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
    private static final Set<String> DISPLAY_ONLY = Set.of("manual_purity", "purity_mode", "random");

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
