package com.tonywww.foodindustrial.recipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Content checks of recipes, run by the data generation (errors fail the run) and by the development
 * command on loaded recipes:
 * <ul>
 *   <li>the structure is valid; time is a multiple of 4 (warning, machines/02 §6.1 rule 1);</li>
 *   <li>at least one registered device can run the recipe (machines/03 §3);</li>
 *   <li>every product with purity has a rule, written or default, that gives a value; rules and
 *       {@code purity_range} only on products with purity (additives/10 §2.1); {@code t0_max} grade
 *       names exist for the product's material;</li>
 *   <li>{@code min_purity} and {@code purity_sources} only on inputs that carry purity (additives/10 §1.1);</li>
 *   <li>a recipe with a compound product (K12) has a food-grade {@code min_purity} on every input that
 *       carries purity (additives/10 §1.1); {@link #checkCraftingJson} checks the same for crafting
 *       recipes at data generation;</li>
 *   <li>inputs refer to this mod's tags only (additives/10 §3).</li>
 * </ul>
 */
public final class RecipeChecks {
    private static final String TAG_PREFIX = "#" + FoodIndustrial.MOD_ID + ":";

    private RecipeChecks() {
    }

    /** A finding; warnings do not fail the data run. */
    public record Problem(String recipe, boolean error, String message) {
        @Override
        public String toString() {
            return (error ? "ERROR " : "WARN ") + recipe + ": " + message;
        }
    }

    public static List<Problem> check(String id, ProcessRecipe recipe, List<DeviceSpec> devices) {
        List<Problem> problems = new ArrayList<>();
        for (String message : recipe.structureProblems()) {
            problems.add(new Problem(id, true, message));
        }
        if (recipe.time() % 4 != 0) {
            problems.add(new Problem(id, false, "time " + recipe.time() + " is not a multiple of 4; devices round it up"));
        }
        boolean runnable = false;
        for (DeviceSpec device : devices) {
            // the development devices (devtest) are not in a released game
            if (!device.id().contains(":dev_") && device.canRun(recipe)) {
                runnable = true;
                break;
            }
        }
        if (!runnable) {
            problems.add(new Problem(id, true, "no registered device can run it"));
        }
        for (VanillaDevice device : recipe.vanillaDevices()) {
            String reason = device.spec() == null ? "not registered" : device.spec().whyNot(recipe);
            if (reason != null) {
                problems.add(new Problem(id, true, "vanilla device " + device.id() + " cannot run it: " + reason));
            }
        }
        checkPurity(id, recipe, problems);
        checkPurityInputs(id, recipe, problems);
        checkCompound(id, recipe, problems);
        checkInputs(id, recipe, problems);
        return problems;
    }

    private static void checkPurity(String id, ProcessRecipe recipe, List<Problem> problems) {
        boolean participants = recipe.hasParticipants();
        for (int i = 0; i < recipe.outputs().size(); i++) {
            ProcessOutput output = recipe.outputs().get(i);
            if (!output.bearsPurity()) {
                if (output.purity() != null) {
                    problems.add(new Problem(id, true, "output " + output.id() + " has a purity rule but carries no purity"));
                }
                if (i == 0 && recipe.purityRange().isPresent()) {
                    problems.add(new Problem(id, true, "purity_range on " + output.id() + ", which carries no purity"));
                }
                continue;
            }
            if (i == 0 && recipe.purityRange().isPresent()) {
                continue;
            }
            PurityRule rule = recipe.ruleOf(i);
            PurityData.Material material = PurityData.current().material(output.material());
            PurityGrade t0Grade = rule.t0MaxGrade();
            if (t0Grade != null && (material == null || material.gradeValue(t0Grade) == PurityData.NONE)) {
                problems.add(new Problem(id, true, "output " + output.id() + ": t0_max \"" + t0Grade.id() + "\" is not a grade of "
                    + output.material()));
            }
            if (rule.readsInputs() && !participants) {
                problems.add(new Problem(id, true, "output " + output.id() + " uses " + rule.mode().id()
                    + " but no input takes part in the start purity"));
            }
            boolean sourceFallback = rule.mode() == PurityRule.Mode.SOURCE || !participants;
            if (sourceFallback && !rule.hasValue() && OutcomeResolver.apply(PurityRule.SOURCE_DEFAULT, material, Double.NaN, 0) == OutcomeResolver.NONE) {
                problems.add(new Problem(id, true, "output " + output.id() + " has no purity: write a source value"
                    + " (its material has neither source_purity nor a crude bound)"));
            }
        }
    }

    private static void checkPurityInputs(String id, ProcessRecipe recipe, List<Problem> problems) {
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput input = recipe.inputs().get(i);
            int index = i;
            boolean threshold = input.threshold().isPresent();
            boolean source = recipe.puritySources().map(sources -> sources.contains(index)).orElse(false);
            if ((threshold || source) && !bears(input.material())) {
                String fields = threshold && source ? "min_purity and purity_sources" : threshold ? "min_purity" : "purity_sources";
                problems.add(new Problem(id, true, "input " + i + " (" + describe(input) + ") carries no purity, so it cannot have " + fields));
            }
        }
    }

    private static void checkCompound(String id, ProcessRecipe recipe, List<Problem> problems) {
        String product = null;
        for (ProcessOutput output : recipe.outputs()) {
            if (output.bearsPurity() && isCompound(output.material())) {
                product = output.material();
                break;
            }
        }
        if (product == null) {
            return;
        }
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput input = recipe.inputs().get(i);
            String problem = componentProblem(input.material(), input.threshold());
            if (problem != null) {
                problems.add(new Problem(id, true, "input " + i + " of compound product " + product + ": " + problem));
            }
        }
    }

    /**
     * Data generation check of a vanilla-format recipe file ({@link PlatformDatagen#addCheck}): a crafting
     * recipe whose result is a compound product (K12) needs a food-grade {@code min_purity} on every
     * ingredient that carries purity (additives/10 §1.1, §4.3). Process recipes are checked by {@link #check}.
     *
     * @throws IllegalStateException on a missing or too low threshold
     */
    public static void checkCraftingJson(String id, JsonElement json, DynamicOps<JsonElement> ops) {
        // Only vanilla-format recipes: this mod's process recipes and the integration files for other
        // mods' machines (I25) have their own checks.
        if (!json.isJsonObject() || json.getAsJsonObject().has("type")
            && !json.getAsJsonObject().get("type").getAsString().startsWith("minecraft:")) {
            return;
        }
        String product = PlatformDatagen.recipeResultItem(json).flatMap(PlatformTags::item)
            .filter(Purity::bears).map(Purity::materialOf).filter(RecipeChecks::isCompound).orElse(null);
        if (product == null) {
            return;
        }
        List<String> problems = new ArrayList<>();
        for (JsonElement element : PlatformDatagen.recipeIngredients(json)) {
            Ingredient ingredient = PlatformIngredients.codec().parse(ops, element).result().orElse(null);
            if (ingredient == null) {
                problems.add("unreadable ingredient " + element);
                continue;
            }
            Optional<PurityThreshold> threshold = PlatformIngredients.purityThreshold(ingredient);
            String material = threshold.map(PurityThreshold::material)
                .orElseGet(() -> Purity.materialHint(PlatformIngredients.references(PlatformIngredients.unwrap(ingredient)), false));
            String problem = componentProblem(material, threshold);
            if (problem != null) {
                problems.add(problem);
            }
        }
        if (!problems.isEmpty()) {
            throw new IllegalStateException("Recipe " + id + " makes compound product " + product + ": " + String.join("; ", problems));
        }
    }

    /** Whether {@code material} is a compound product (K12). */
    public static boolean isCompound(@Nullable String material) {
        PurityData.Material data = PurityData.current().material(material);
        return data != null && data.compound();
    }

    /**
     * Problem of one component of a compound product, or {@code null}: an input that carries purity
     * needs {@code min_purity} at least its material's food bound.
     */
    @Nullable
    static String componentProblem(@Nullable String material, Optional<PurityThreshold> threshold) {
        PurityData.Material data = PurityData.current().material(material);
        if (data == null || !data.bears()) {
            return null;
        }
        int food = data.gradeValue(PurityGrade.FOOD);
        if (food == PurityData.NONE) {
            return material + " has no food grade, so it cannot be a component";
        }
        if (threshold.isEmpty() || threshold.get().value() < food) {
            return material + " needs min_purity \"food\" (at least " + PurityScale.format(food) + "%)";
        }
        return null;
    }

    private static boolean bears(@Nullable String material) {
        PurityData.Material data = PurityData.current().material(material);
        return data != null && data.bears();
    }

    private static String describe(ProcessInput input) {
        if (input.material() != null) {
            return input.material();
        }
        String plain = input.plainMaterial();
        return plain != null ? plain : "no purity material";
    }

    private static void checkInputs(String id, ProcessRecipe recipe, List<Problem> problems) {
        for (int i = 0; i < recipe.catalysts().size(); i++) {
            JsonElement references = PlatformIngredients.references(PlatformIngredients.unwrap(recipe.catalysts().get(i).ingredient()));
            if (!onlyOwnTags(references)) {
                problems.add(new Problem(id, true, "catalyst " + i + " must refer to " + TAG_PREFIX + "… tags only: " + references));
            }
        }
        for (int i = 0; i < recipe.inputs().size(); i++) {
            ProcessInput input = recipe.inputs().get(i);
            if (input instanceof ProcessInput.ItemInput item) {
                JsonElement references = PlatformIngredients.references(PlatformIngredients.unwrap(item.ingredient()));
                if (!onlyOwnTags(references)) {
                    problems.add(new Problem(id, true, "input " + i + " must refer to " + TAG_PREFIX + "… tags only: " + references));
                }
            } else {
                ProcessInput.FluidInput fluid = (ProcessInput.FluidInput) input;
                boolean own = fluid.fluid().tag().map(tag -> PlatformTags.tagId(tag).startsWith(FoodIndustrial.MOD_ID + ":")).orElse(false);
                if (!own) {
                    problems.add(new Problem(id, true, "fluid input " + i + " must use a " + TAG_PREFIX + "… fluid tag"));
                }
            }
        }
    }

    private static boolean onlyOwnTags(JsonElement json) {
        if (json == null) {
            return false;
        }
        if (json.isJsonArray()) {
            if (json.getAsJsonArray().isEmpty()) {
                return false;
            }
            for (JsonElement element : json.getAsJsonArray()) {
                if (!onlyOwnTags(element)) {
                    return false;
                }
            }
            return true;
        }
        if (json.isJsonObject()) {
            JsonElement tag = json.getAsJsonObject().get("tag");
            return tag != null && tag.getAsString().startsWith(FoodIndustrial.MOD_ID + ":");
        }
        return json.isJsonPrimitive() && json.getAsString().startsWith(TAG_PREFIX);
    }
}
