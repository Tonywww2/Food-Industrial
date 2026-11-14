package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A recipe type (machines/01 §2): one unit operation shared by hand tools, workshop machines and
 * industrial machines. Immutable descriptor; there are no subclasses per type. Slot limits and allowed
 * conditions are not declared here: a recipe must fit at least one registered device (machines/01
 * §3.1).
 */
public final class ProcessType {
    private final String id;
    private final String code;
    private final boolean yieldScalable;
    private final boolean sourceOnly;
    private final Function<Conditions, PurityRule> defaultRule;
    @Nullable
    private PlatformRecipes.Kind<ProcessRecipe> kind;

    ProcessType(String id, String code, boolean yieldScalable, boolean sourceOnly, Function<Conditions, PurityRule> defaultRule) {
        this.id = id;
        this.code = code;
        this.yieldScalable = yieldScalable;
        this.sourceOnly = sourceOnly;
        this.defaultRule = defaultRule;
    }

    /** Recipe type id without namespace ({@code grinding}). */
    public String id() {
        return id;
    }

    /** Design code ({@code R01}–{@code R19}, {@code W01}, {@code W02}). */
    public String code() {
        return code;
    }

    /** Whether device yield multiplies the main product (physical operations, machines/01 §1 rule 2). */
    public boolean yieldScalable() {
        return yieldScalable;
    }

    /**
     * FE/t of a recipe of this type that writes no {@code energy}, on FE machines before the tier's
     * energy factor (machines/01 §2, 2026-11-11); 0 for world interactions.
     */
    public int defaultEnergy() {
        return switch (code) {
            case "R01", "R02", "R03", "R08", "R18", "R19" -> 10;
            case "R04", "R06", "R12", "R15" -> 15;
            case "R05", "R07", "R11" -> 20;
            case "R10" -> 30;
            case "R09", "R13" -> 40;
            case "R14" -> 80;
            case "R16", "R17" -> 5;
            default -> 0;
        };
    }

    /** World interaction types (W01, W02); their recipe format comes with I12. */
    public boolean isWorldInteraction() {
        return code.startsWith("W");
    }

    /**
     * Purity rule of a product whose recipe writes none (machines/01 §1.1): the source mode when no
     * input takes part in the start purity or the type ignores inputs (fermentation, world
     * interaction), else the type's rule for these conditions.
     */
    public PurityRule defaultRule(Conditions conditions, boolean hasParticipants) {
        if (sourceOnly || !hasParticipants) {
            return PurityRule.SOURCE_DEFAULT;
        }
        return defaultRule.apply(conditions);
    }

    /** Vanilla recipe kind of this type; {@code null} for the world interaction types. */
    @Nullable
    public PlatformRecipes.Kind<ProcessRecipe> kind() {
        return kind;
    }

    void bind(PlatformRecipes.Kind<ProcessRecipe> recipeKind) {
        this.kind = recipeKind;
    }

    @Override
    public String toString() {
        return code + " " + id;
    }
}
