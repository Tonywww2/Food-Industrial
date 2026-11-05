package com.tonywww.foodindustrial.api.purity;

import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The five purity grades, lowest first. Their bounds depend on the material: each material belongs to
 * a category with its own lower bounds, and single materials may override them (additives/10 §1.3–1.5).
 * A purity at or above a grade's bound belongs to that grade; below the crude bound it is poor.
 */
public enum PurityGrade {
    POOR("poor", ChatFormatting.RED),
    CRUDE("crude", ChatFormatting.GOLD),
    REFINED("refined", ChatFormatting.AQUA),
    FOOD("food", ChatFormatting.GREEN),
    LAB("lab", ChatFormatting.LIGHT_PURPLE);

    /** Grades with a lower bound in the material data, lowest first. */
    public static final PurityGrade[] BOUNDED = {CRUDE, REFINED, FOOD, LAB};

    private final String id;
    private final ChatFormatting color;

    PurityGrade(String id, ChatFormatting color) {
        this.id = id;
        this.color = color;
    }

    /** Serialized name ({@code poor}, {@code crude}, {@code refined}, {@code food}, {@code lab}). */
    public String id() {
        return id;
    }

    /** Colour of the tooltip purity line (text standard §3.1). */
    public ChatFormatting color() {
        return color;
    }

    /** Language key of the grade name, {@code purity.foodindustrial.<id>}. */
    public String translationKey() {
        return "purity.foodindustrial." + id;
    }

    /** Grade by serialized name, or {@code null}. */
    @Nullable
    public static PurityGrade byId(String id) {
        String key = id.trim().toLowerCase(Locale.ROOT);
        for (PurityGrade grade : values()) {
            if (grade.id.equals(key)) {
                return grade;
            }
        }
        return null;
    }
}
