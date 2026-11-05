package com.tonywww.foodindustrial.recipe;

import com.google.gson.JsonElement;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;

import java.util.ArrayList;
import java.util.List;

/**
 * Content checks of recipes, run by the data generation (errors fail the run) and by the development
 * command on loaded recipes:
 * <ul>
 *   <li>the structure is valid; time is a multiple of 4 (warning, machines/02 §6.1 rule 1);</li>
 *   <li>at least one registered device can run the recipe (machines/03 §3);</li>
 *   <li>every product with purity has a rule, written or default, that gives a value; rules and
 *       {@code purity_range} only on products with purity (additives/10 §2.1);</li>
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
            if (device.canRun(recipe)) {
                runnable = true;
                break;
            }
        }
        if (!runnable) {
            problems.add(new Problem(id, true, "no registered device can run it"));
        }
        checkPurity(id, recipe, problems);
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
            if (rule.readsInputs() && !participants) {
                problems.add(new Problem(id, true, "output " + output.id() + " uses " + rule.mode().id()
                    + " but no input takes part in the start purity"));
            }
            PurityData.Material material = PurityData.current().material(output.material());
            boolean sourceFallback = rule.mode() == PurityRule.Mode.SOURCE || !participants;
            if (sourceFallback && !rule.hasValue() && OutcomeResolver.apply(PurityRule.SOURCE_DEFAULT, material, Double.NaN, 0) == OutcomeResolver.NONE) {
                problems.add(new Problem(id, true, "output " + output.id() + " has no purity: write a source value"
                    + " (its material has neither source_purity nor a crude bound)"));
            }
        }
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
