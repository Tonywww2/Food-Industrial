package com.tonywww.foodindustrial.food.quality;

/**
 * The quality data of a food stack or fluid, packed into one int (foods/04 §4, §7; foods/05 §2): the grade of each
 * evaluated quality item, the purity label and the flags later systems read (declarations, nitrite). {@code 0} means
 * no quality data: products of recipes without an ingredient segment, and everything outside the food line.
 * <ul>
 *   <li>bits 0–7: two bits per {@link QualityItem}: 0 not evaluated, 1 ordinary, 2 good, 3 excellent;</li>
 *   <li>bits 8–9: label: 0 none, 1 premium, 2 homemade, 3 poor (foods/01 §5);</li>
 *   <li>bits 10–13: {@link #ASPARTAME}, {@link #ASPARTAME_ACESULFAME} (declaration lines, text-standard §3.3),
 *       {@link #CURING_AGENT} (a colour-retention agent on the colour item), {@link #NITRITE_MISUSE} (nitrite taken
 *       as salt, foods/02 §4.4).</li>
 * </ul>
 * Stacks with different codes do not stack; the same recipe with the same additives always writes the same code.
 */
public final class FoodQuality {
    public static final int NONE = 0;

    public static final int ORDINARY = 0;
    public static final int GOOD = 1;
    public static final int EXCELLENT = 2;

    public static final int LABEL_NONE = 0;
    public static final int LABEL_PREMIUM = 1;
    public static final int LABEL_HOMEMADE = 2;
    public static final int LABEL_POOR = 3;

    public static final int ASPARTAME = 1 << 10;
    public static final int ASPARTAME_ACESULFAME = 1 << 11;
    public static final int CURING_AGENT = 1 << 12;
    public static final int NITRITE_MISUSE = 1 << 13;
    private static final int FLAGS = ASPARTAME | ASPARTAME_ACESULFAME | CURING_AGENT | NITRITE_MISUSE;
    private static final int LABEL_SHIFT = 8;

    private FoodQuality() {
    }

    /**
     * Grade of {@code item}: {@code -1} when the product does not evaluate it, else {@link #ORDINARY}, {@link #GOOD}
     * or {@link #EXCELLENT}.
     */
    public static int item(int code, QualityItem item) {
        return ((code >>> (item.ordinal() * 2)) & 3) - 1;
    }

    /** {@code code} with {@code item} at {@code grade} ({@code -1}: not evaluated). */
    public static int withItem(int code, QualityItem item, int grade) {
        int shift = item.ordinal() * 2;
        return (code & ~(3 << shift)) | ((Math.max(-1, Math.min(EXCELLENT, grade)) + 1) << shift);
    }

    public static boolean evaluates(int code, QualityItem item) {
        return item(code, item) >= 0;
    }

    public static int label(int code) {
        return (code >>> LABEL_SHIFT) & 3;
    }

    public static int withLabel(int code, int label) {
        return (code & ~(3 << LABEL_SHIFT)) | ((label & 3) << LABEL_SHIFT);
    }

    public static boolean has(int code, int flag) {
        return (code & flag) != 0;
    }

    public static int flags(int code) {
        return code & FLAGS;
    }

    /**
     * Overall grade (foods/04 §4): the evaluated items score 0, 1, 2; the average {@code ≥ 1.5} is excellent,
     * {@code ≥ 0.5} good, else ordinary; premium raises it one grade, at most excellent. Ordinary without items.
     */
    public static int grade(int code) {
        int sum = 0;
        int count = 0;
        for (QualityItem item : QualityItem.values()) {
            int grade = item(code, item);
            if (grade >= 0) {
                sum += grade;
                count++;
            }
        }
        // average ≥ 1.5 ⇔ 2·sum ≥ 3·count; ≥ 0.5 ⇔ 2·sum ≥ count
        int grade = count == 0 ? ORDINARY : 2 * sum >= 3 * count ? EXCELLENT : 2 * sum >= count ? GOOD : ORDINARY;
        if (label(code) == LABEL_PREMIUM) {
            grade = Math.min(EXCELLENT, grade + 1);
        }
        return grade;
    }

    /** Whether the stack shows a quality line: not ordinary, or labelled (text-standard §3.3). */
    public static boolean shown(int code) {
        return code != NONE && (grade(code) != ORDINARY || label(code) != LABEL_NONE);
    }

    /**
     * Quality of a product made from inputs carrying {@code a} and {@code b} in a processing step (foods/05 §2):
     * each item the lower grade of the inputs evaluating it; the worst label (poor > homemade > none), premium only
     * when both are premium; the flags of both. {@link #NONE} inputs take no part.
     */
    public static int merge(int a, int b) {
        if (a == NONE) {
            return b;
        }
        if (b == NONE) {
            return a;
        }
        int code = 0;
        for (QualityItem item : QualityItem.values()) {
            int ga = item(a, item);
            int gb = item(b, item);
            int grade = ga < 0 ? gb : gb < 0 ? ga : Math.min(ga, gb);
            code = withItem(code, item, grade);
        }
        int la = label(a);
        int lb = label(b);
        int label;
        if (la == LABEL_POOR || lb == LABEL_POOR) {
            label = LABEL_POOR;
        } else if (la == LABEL_HOMEMADE || lb == LABEL_HOMEMADE) {
            label = LABEL_HOMEMADE;
        } else {
            label = la == LABEL_PREMIUM && lb == LABEL_PREMIUM ? LABEL_PREMIUM : LABEL_NONE;
        }
        return withLabel(code, label) | flags(a) | flags(b);
    }
}
