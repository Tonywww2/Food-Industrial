package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The 19 general recipe types and the 2 world interaction types (machines/01 §2) with their default
 * purity rules (machines/01 §1.1). Removal shares are game settings in four steps: ordinary 50%,
 * strong 75%, very strong 90%, pure culture 99%.
 */
public final class ProcessTypes {
    private static final Map<String, ProcessType> BY_ID = new LinkedHashMap<>();

    private static final Function<Conditions, PurityRule> KEEP = conditions -> PurityRule.KEEP;
    private static final Function<Conditions, PurityRule> REMOVE_50 = conditions -> PurityRule.removal(50);
    private static final Function<Conditions, PurityRule> REMOVE_75 = conditions -> PurityRule.removal(75);
    private static final Function<Conditions, PurityRule> REMOVE_90 = conditions -> PurityRule.removal(90);

    public static final ProcessType GRINDING = add("grinding", "R01", true, false, KEEP);
    public static final ProcessType PRESSING = add("pressing", "R02", true, false, REMOVE_50);
    public static final ProcessType SEPARATING = add("separating", "R03", true, false, REMOVE_50);
    public static final ProcessType EXTRACTING = add("extracting", "R04", true, false, REMOVE_75);
    public static final ProcessType BOILING = add("boiling", "R05", false, false, REMOVE_50);
    public static final ProcessType CRYSTALLIZING = add("crystallizing", "R06", false, false, REMOVE_75);
    public static final ProcessType DRYING = add("drying", "R07", true, false, REMOVE_50);
    public static final ProcessType FUMIGATING = add("fumigating", "R08", false, false, KEEP);
    public static final ProcessType KILN = add("kiln", "R09", false, false, REMOVE_50);
    /** Multiple fractions (M22) separate better: 90% when the recipe requires them. */
    public static final ProcessType DISTILLING = add("distilling", "R10", false, false,
        conditions -> PurityRule.removal(conditions.requires("method.multi_fraction") ? 90 : 75));
    public static final ProcessType REACTING = add("reacting", "R11", false, false, REMOVE_50);
    public static final ProcessType ABSORBING = add("absorbing", "R12", false, false, REMOVE_50);
    public static final ProcessType GAS_REACTING = add("gas_reacting", "R13", false, false, REMOVE_50);
    public static final ProcessType ELECTROLYZING = add("electrolyzing", "R14", false, false, REMOVE_90);
    /** Adsorption 75%; ion exchange, membranes and electrodialysis 90% when the recipe requires them. */
    public static final ProcessType PURIFYING = add("purifying", "R15", false, false,
        conditions -> PurityRule.removal(conditions.requires("method.ion_exchange") || conditions.requires("method.membrane")
            || conditions.requires("method.electrodialysis") ? 90 : 75));
    public static final ProcessType FERMENTING = add("fermenting", "R16", false, true, KEEP);
    public static final ProcessType SOLID_FERMENTING = add("solid_fermenting", "R17", true, true, KEEP);
    public static final ProcessType CULTURING = add("culturing", "R18", false, false, conditions -> PurityRule.removal(99));
    /** Compound products carry no purity; the same material mixed keeps the weighted average. */
    public static final ProcessType MIXING = add("mixing", "R19", false, false, KEEP);
    public static final ProcessType BLOCK_TRANSFORM = add("block_transform", "W01", false, true, KEEP);
    public static final ProcessType HARVESTING = add("harvesting", "W02", false, true, KEEP);

    private static final List<ProcessType> RECIPE_TYPES = BY_ID.values().stream().filter(type -> !type.isWorldInteraction()).toList();

    private ProcessTypes() {
    }

    private static ProcessType add(String id, String code, boolean yieldScalable, boolean sourceOnly, Function<Conditions, PurityRule> rule) {
        ProcessType type = new ProcessType(id, code, yieldScalable, sourceOnly, rule);
        BY_ID.put(id, type);
        return type;
    }

    /** All types, R01 first. */
    public static List<ProcessType> all() {
        return Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
    }

    /** Types with a datapack recipe format (R01–R19), R01 first. */
    public static List<ProcessType> recipeTypes() {
        return RECIPE_TYPES;
    }

    /** Type by id without namespace, or {@code null}. */
    @Nullable
    public static ProcessType byId(String id) {
        return BY_ID.get(id.startsWith("foodindustrial:") ? id.substring("foodindustrial:".length()) : id);
    }

    /** Registers the recipe types and serializers of R01–R19. Called once during mod construction. */
    public static void bootstrap() {
        for (ProcessType type : recipeTypes()) {
            type.bind(PlatformRecipes.register(type.id(), ProcessRecipe.mapCodec(type)));
        }
        RecipeIndex.bootstrap();
        RecipeGen.bootstrap();
    }
}
