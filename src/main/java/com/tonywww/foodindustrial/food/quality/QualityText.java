package com.tonywww.foodindustrial.food.quality;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * The quality lines of finished products (text-standard §3.3, foods/04 §4): under the name "grade · label", with Shift
 * one more line "item grade · item grade"; the declaration lines of aspartame and its acesulfame salt before the
 * nutrition part. Grade colours ordinary grey, good green, excellent gold (foods/04 §8 rule 8); the label colours are
 * the implementation's proposal (premium light purple, homemade yellow, poor red), to be reviewed.
 */
public final class QualityText {
    private static final String KEY = "quality." + FoodIndustrial.MOD_ID + ".";
    private static final String[] GRADES = {"ordinary", "good", "excellent"};
    private static final ChatFormatting[] GRADE_COLORS = {ChatFormatting.GRAY, ChatFormatting.GREEN, ChatFormatting.GOLD};
    private static final String[] LABELS = {"", "premium", "homemade", "poor"};
    private static final ChatFormatting[] LABEL_COLORS = {ChatFormatting.GRAY, ChatFormatting.LIGHT_PURPLE, ChatFormatting.YELLOW, ChatFormatting.RED};

    private QualityText() {
    }

    public static void bootstrap() {
        Lang.add(KEY + "grade.ordinary", "一般", "Ordinary");
        Lang.add(KEY + "grade.good", "良好", "Good");
        Lang.add(KEY + "grade.excellent", "优", "Excellent");
        Lang.add(KEY + "label.premium", "特级", "Premium");
        Lang.add(KEY + "label.homemade", "土法", "Homemade");
        Lang.add(KEY + "label.poor", "劣质", "Poor");
        for (QualityItem item : QualityItem.values()) {
            Lang.add(KEY + "item." + item.id(), item.zh(), item.en());
        }
        Lang.add(KEY + "declaration.aspartame", "阿斯巴甜（含苯丙氨酸）", "Aspartame (contains phenylalanine)");
        Lang.add(KEY + "declaration.aspartame_acesulfame", "天门冬酰苯丙氨酸甲酯乙酰磺胺酸（含苯丙氨酸）",
            "Aspartame-acesulfame salt (contains phenylalanine)");
    }

    public static Component grade(int grade) {
        return Component.translatable(KEY + "grade." + GRADES[grade]).withStyle(GRADE_COLORS[grade]);
    }

    public static Component label(int label) {
        return Component.translatable(KEY + "label." + LABELS[label]).withStyle(LABEL_COLORS[label]);
    }

    public static Component item(QualityItem item) {
        return Component.translatable(KEY + "item." + item.id()).withStyle(ChatFormatting.GRAY);
    }

    /** The quality line and, with Shift, the item line of {@code code}; nothing when it shows nothing. */
    public static List<Component> lines(int code, boolean shiftDown) {
        List<Component> lines = new ArrayList<>(2);
        if (!FoodQuality.shown(code)) {
            return lines;
        }
        int grade = FoodQuality.grade(code);
        int label = FoodQuality.label(code);
        MutableComponent line = Component.empty();
        if (grade != FoodQuality.ORDINARY) {
            line.append(grade(grade));
        }
        if (label != FoodQuality.LABEL_NONE) {
            if (grade != FoodQuality.ORDINARY) {
                line.append(Component.literal(" · ").withStyle(ChatFormatting.GRAY));
            }
            line.append(label(label));
        }
        lines.add(line);
        if (shiftDown) {
            MutableComponent items = Component.empty();
            boolean first = true;
            for (QualityItem item : QualityItem.values()) {
                int value = FoodQuality.item(code, item);
                if (value < 0) {
                    continue;
                }
                if (!first) {
                    items.append(Component.literal(" · ").withStyle(ChatFormatting.GRAY));
                }
                items.append(item(item)).append(Component.literal(" ")).append(grade(value));
                first = false;
            }
            if (!first) {
                lines.add(items);
            }
        }
        return lines;
    }

    /** Appends the quality lines of a fluid or item code after its name line. */
    public static void append(int code, boolean shiftDown, List<Component> lines) {
        lines.addAll(lines(code, shiftDown));
        lines.addAll(declarations(code));
    }

    /** The declaration lines (text-standard §3.3), in table order. */
    public static List<Component> declarations(int code) {
        List<Component> lines = new ArrayList<>(2);
        if (FoodQuality.has(code, FoodQuality.ASPARTAME)) {
            lines.add(Component.translatable(KEY + "declaration.aspartame").withStyle(ChatFormatting.GRAY));
        }
        if (FoodQuality.has(code, FoodQuality.ASPARTAME_ACESULFAME)) {
            lines.add(Component.translatable(KEY + "declaration.aspartame_acesulfame").withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }
}
