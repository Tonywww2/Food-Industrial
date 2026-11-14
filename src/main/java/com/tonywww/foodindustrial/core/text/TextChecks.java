package com.tonywww.foodindustrial.core.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Automated checks of the text standard (§1, §3, §6), run by the data generator before the language
 * files are written. Any violation fails the data run with the full list of problems.
 */
public final class TextChecks {
    /** Forbidden words of text standard §1.1 (Chinese). */
    static final List<String> FORBIDDEN_ZH = List.of("可以", "能够", "一种", "各种", "非常", "十分", "重要的", "神奇的", "你", "我们", "请");
    /** Longest Shift description, in Han characters (§3.1, §3.2). */
    static final int MAX_DESCRIPTION_HAN = 24;
    /** Longest English tooltip line (§1.9). */
    static final int MAX_EN_TOOLTIP = 60;

    private static final Pattern EMOJI = Pattern.compile("[\\x{1F000}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\x{FE0F}]");
    private static final Pattern HAN = Pattern.compile("\\p{IsHan}");
    /** Han next to a Latin letter or digit without a space (§1.5). */
    private static final Pattern MISSING_SPACE = Pattern.compile("\\p{IsHan}[A-Za-z0-9]|[A-Za-z0-9]\\p{IsHan}");
    /**
     * Half-width punctuation in Chinese text (§1.4). Chemical names keep their half-width locants and
     * descriptors: commas between letters or digits (1,2-丙二醇、N,N-二甲基甲酰胺), parentheses
     * inside a descriptor (L(+)-酒石酸、3(2H)-呋喃酮) and the groups of {@link #CHEMICAL_GROUP}.
     */
    private static final Pattern HALF_WIDTH = Pattern.compile("(?<![A-Za-z0-9]),|,(?![A-Za-z0-9])|[;:?]|(?<=\\p{IsHan})\\(|\\)(?=\\p{IsHan})|^\\(|\\)$");
    /**
     * Half-width parenthesised groups that belong to a chemical name and are skipped by
     * {@link #HALF_WIDTH} (2026-11-05): a degree of polymerisation as GB 2760 writes it (聚氧乙烯(20)山梨醇酐…)
     * and a substituent group after a locant or descriptor (3-(3-羟基-4-甲氧基苯基)丙醛).
     */
    private static final Pattern CHEMICAL_GROUP = Pattern.compile("\\(\\d+\\)|(?<=[-0-9A-Za-z,])\\([^()（）]*\\)");
    /** Hyphen used for a numeric range instead of the en dash (§1.4). */
    private static final Pattern RANGE_HYPHEN = Pattern.compile("\\d%?\\s*-\\s*\\d");
    /** Space between a number and % (§1.5). */
    private static final Pattern PERCENT_SPACE = Pattern.compile("\\d\\s+%");
    /** Number glued to a unit, or a unit in the wrong case (§1.5, §1.7). */
    private static final Pattern UNIT = Pattern.compile("\\d(mB|FE|°C|MPa|tick)|\\b\\d+\\s*(mb|MB|fe|Fe|°c|mpa|Mpa)\\b");
    /**
     * Word of an English name: letters start upper case, except chemical prefixes (l-, dl-, n-, …) and the
     * short words English title case keeps lower case (Plaster of Paris, Palladium on Carbon).
     */
    private static final Pattern EN_NAME_WORD_OK = Pattern.compile("^(?:[^A-Za-z]|[A-Z]|(?:l|d|dl|n|o|m|p|cis|trans|sec|tert)-|(?:of|and|on|in|for|with|the|or|from)$).*");
    /**
     * Chemical names as GB 2760 writes them (I31, 2026-11-11): a half-width group inside a name (a substituent
     * 双(2-甲基-3-呋喃基)、二(甲硫基)甲烷, a descriptor (+/-)-、(E)-, an alternative locant 3,5(或6)-), i.e. one whose
     * content has a digit, Latin letter or sign, or that starts with 或, or that is followed by more of the name.
     * Only a trailing group of Chinese words is a qualifier and must be full-width (丁香花蕾酊（提取物）).
     */
    private static final Pattern CHEMICAL_NAME_GROUP = Pattern.compile(
        "\\((?:[^()（）]*[0-9A-Za-z+±/,αβγδεω-][^()（）]*|或[^()（）]*)\\)|\\([^()（）]*\\)(?=.)");
    /** Locants written against Chinese text in GB names (丁酸3-丁酮-2-醇酯、3,5(或6)-), masked before the space check. */
    private static final Pattern LOCANT = Pattern.compile("[A-Za-z0-9]+(?:[(),][A-Za-z0-9]+\\)?)*(?=-)|\\d+(?=[)或和])|(?<=[(或和])\\d+");
    /** Commas of a chemical name between descriptors (对,α-、反式,反式-、(48%),亚麻酸), masked before the punctuation check. */
    private static final Pattern NAME_COMMA = Pattern.compile("(?<=\\S),(?=\\S)");
    /**
     * Configuration prefixes (text-standard §2.1, 2026-11-12): Fischer configuration of amino acids, sugars and
     * hydroxy acids in capitals ({@code L-Lysine}, {@code D-Ribose}), optical rotation of terpenes and tocopherols
     * in lower case ({@code d-Limonene}, {@code l-Menthol}). The stems tell which a name is.
     */
    private static final Pattern FISCHER_LOWER = Pattern.compile("(?<![A-Za-z])(?:d|l|dl)-(?i:alanin|arginin|asparagin|aspart|"
        + "cystein|cystin|glutam|glycer|histidin|isoleucin|leucin|lysin|methionin|phenylalanin|prolin|serin|threon|tryptophan|tyrosin|"
        + "valin|carnitin|theanin|ornithin|citrullin|selenocystein|methylselenocystein|homocystein|ribos|rhamnos|xylos|arabinos|glucos|"
        + "fructos|galactos|mannos|tagatos|allulos|psicos|fucos|sorbos|glucon|glucuron|lact|malic|malate|tartar|tartrat|pantothen|"
        + "pantolacton|pantoic|ascorb|sorbit|mannit|xylit|erythrit)");
    private static final Pattern OPTICAL_UPPER = Pattern.compile("(?<![A-Za-z])(?:D|L|DL)-(?i:limonen|menthol|menthon|isomenthon|"
        + "carvon|camphor|borne|pinen|linalo|citronell|fench|terpine|piperiton|pulegon|carvyl|isopulegol|nootkaton|camphen|tocopher|"
        + "alpha-tocopher)");
    /** Chemical prefixes of English names: l-, dl-, alpha-, p,alpha-, trans,trans-, and isomer lists (o, m, p). */
    private static final String CHEM_PREFIX = "(?:l|d|dl|n|o|m|p|cis|trans|sec|tert|alpha|beta|gamma|delta|epsilon|omega|[αβγδεω])";
    private static final Pattern EN_CHEMICAL_WORD = Pattern.compile(
        "^(?:" + CHEM_PREFIX + "(?:[,/]" + CHEM_PREFIX + ")*-.*|" + CHEM_PREFIX + "(?:[,/]" + CHEM_PREFIX + ")*[,)]?)$");

    private TextChecks() {
    }

    /**
     * Checks every entry and the zh / en key sets.
     *
     * @throws IllegalStateException listing all problems
     */
    public static void checkAll(Map<String, String> zh, Map<String, String> en, Map<String, String> commonNames, Set<String> chemical) {
        List<String> problems = new ArrayList<>();
        if (!zh.keySet().equals(en.keySet())) {
            problems.add("zh_cn and en_us keys differ");
        }
        zh.forEach((key, value) -> checkZh(key, plain(key, value), chemical.contains(key), problems));
        en.forEach((key, value) -> checkEn(key, plain(key, value), chemical.contains(key), problems));
        Map<String, String> names = new TreeMap<>();
        zh.forEach((key, value) -> {
            if (isName(key)) {
                names.put(key, value);
            }
        });
        problems.addAll(checkNaming(names, commonNames));
        if (!problems.isEmpty()) {
            throw new IllegalStateException("Text standard violations:\n  " + String.join("\n  ", problems));
        }
    }

    /** Patchouli formatting codes ({@code $(br2)}, {@code $(l)} …) in book texts are markup, not text (I23). */
    private static final Pattern BOOK_CODE = Pattern.compile("\\$\\([^)]*\\)");

    /** The text a reader sees: book texts without their formatting codes, line breaks as spaces. */
    static String plain(String key, String value) {
        return key.startsWith("book.") ? BOOK_CODE.matcher(value).replaceAll(" ").replaceAll(" {2,}", " ").trim() : value;
    }

    static void checkZh(String key, String value, List<String> problems) {
        checkZh(key, value, false, problems);
    }

    /** {@code chemical}: the entry is the name of a material, written as its standard name (I31). */
    static void checkZh(String key, String value, boolean chemical, List<String> problems) {
        if (value.isBlank()) {
            problems.add(key + ": empty");
            return;
        }
        for (String word : FORBIDDEN_ZH) {
            if (value.contains(word)) {
                problems.add(key + ": forbidden word \"" + word + "\" in \"" + value + "\"");
            }
        }
        if (value.contains("！") || value.contains("!") || EMOJI.matcher(value).find()) {
            problems.add(key + ": exclamation mark or emoji in \"" + value + "\"");
        }
        find(MISSING_SPACE, key, chemical ? LOCANT.matcher(value).replaceAll("-") : value,
            "missing space between Chinese and Latin / digits", problems);
        String groups = chemical ? NAME_COMMA.matcher(CHEMICAL_NAME_GROUP.matcher(value).replaceAll("0")).replaceAll("0") : value;
        find(HALF_WIDTH, key, CHEMICAL_GROUP.matcher(groups).replaceAll(""), "half-width punctuation", problems);
        find(RANGE_HYPHEN, key, value, "range written with '-' instead of '–'", problems);
        find(PERCENT_SPACE, key, value, "space before %", problems);
        find(UNIT, key, value, "unit spacing or case", problems);
        if (isLine(key) && value.endsWith("。")) {
            problems.add(key + ": single line ends with \"。\"");
        }
        if (isDescription(key)) {
            int han = (int) HAN.matcher(value).results().count();
            if (han > MAX_DESCRIPTION_HAN) {
                problems.add(key + ": description has " + han + " Han characters (max " + MAX_DESCRIPTION_HAN + ")");
            }
            if (value.contains("。")) {
                problems.add(key + ": description contains \"。\"");
            }
        }
    }

    static void checkEn(String key, String value, List<String> problems) {
        checkEn(key, value, false, problems);
    }

    /** {@code chemical}: a material name; chemical prefixes may start it and its length is not limited (I31). */
    static void checkEn(String key, String value, boolean chemical, List<String> problems) {
        if (value.isBlank()) {
            problems.add(key + ": empty");
            return;
        }
        if (value.contains("!") || EMOJI.matcher(value).find()) {
            problems.add(key + ": exclamation mark or emoji in \"" + value + "\"");
        }
        find(PERCENT_SPACE, key, value, "space before %", problems);
        find(UNIT, key, value, "unit spacing or case", problems);
        if (isLine(key) && value.endsWith(".")) {
            problems.add(key + ": single line ends with \".\"");
        }
        if (isLine(key) && startsInLowerCase(value) && !(chemical && EN_CHEMICAL_WORD.matcher(value.split(" ", 2)[0]).matches())) {
            problems.add(key + ": English line starts in lower case");
        }
        if (key.startsWith("tooltip.") && value.length() > MAX_EN_TOOLTIP && !chemical) {
            problems.add(key + ": English tooltip has " + value.length() + " characters (max " + MAX_EN_TOOLTIP + ")");
        }
        if (isName(key)) {
            find(FISCHER_LOWER, key, value, "Fischer configuration prefix in lower case (D-, L-, DL-)", problems);
            find(OPTICAL_UPPER, key, value, "optical rotation prefix in capitals (d-, l-, dl-)", problems);
            for (String word : value.split(" ")) {
                String bare = word.startsWith("(") ? word.substring(1) : word;
                if (!bare.isEmpty() && !EN_NAME_WORD_OK.matcher(bare).matches()
                    && !(chemical && EN_CHEMICAL_WORD.matcher(bare).matches())) {
                    problems.add(key + ": English name word \"" + word + "\" not capitalised");
                }
            }
        }
    }

    /**
     * Naming check of §2.1: a material with a common name must be named {@code 专业名称（日常名称）},
     * and no item, block or fluid may be named by the common name alone.
     *
     * @param names       name key → Chinese name
     * @param commonNames professional name → common name, from the material table
     */
    public static List<String> checkNaming(Map<String, String> names, Map<String, String> commonNames) {
        List<String> problems = new ArrayList<>();
        names.forEach((key, value) -> {
            String base = value.contains("（") ? value.substring(0, value.indexOf('（')) : value;
            String common = commonNames.get(base);
            if (common != null && !value.startsWith(base + "（" + common + "）")) {
                problems.add(key + ": \"" + value + "\" should be \"" + base + "（" + common + "）\"");
            }
            if (commonNames.containsValue(value)) {
                problems.add(key + ": common name \"" + value + "\" used alone");
            }
        });
        return problems;
    }

    /** Lower-case ASCII start, except chemical prefixes such as {@code l-Menthol} or {@code tert-Butyl}. */
    private static boolean startsInLowerCase(String value) {
        char first = value.charAt(0);
        if (first < 'a' || first > 'z') {
            return false;
        }
        String word = value.split(" ", 2)[0];
        return !word.matches("(?:l|d|dl|n|o|m|p|cis|trans|sec|tert)-.*");
    }

    private static boolean isName(String key) {
        return key.startsWith("item.") || key.startsWith("block.") || key.startsWith("fluid_type.");
    }

    /** Keys shown as single lines (tooltips, JEI, GUI, conditions, grades). */
    private static boolean isLine(String key) {
        return key.startsWith("tooltip.") || key.startsWith("jei.") || key.startsWith("gui.")
            || key.startsWith("condition.") || key.startsWith("purity.");
    }

    private static boolean isDescription(String key) {
        return key.startsWith("tooltip.") && key.endsWith(".desc");
    }

    private static void find(Pattern pattern, String key, String value, String what, List<String> problems) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            problems.add(key + ": " + what + " at \"" + matcher.group() + "\" in \"" + value + "\"");
        }
    }
}
