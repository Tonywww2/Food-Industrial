package com.tonywww.foodindustrial.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Data generation of this mod's process recipes. Content registers generators here; the data run
 * writes their recipes in the node's format, runs {@link RecipeChecks} (errors fail the run) and writes
 * the purity report ({@link PurityReport}) to {@code build/reports/foodindustrial/purity_report.md}.
 */
public final class RecipeGen {
    private static final List<Consumer<Output>> GENERATORS = new ArrayList<>();

    private RecipeGen() {
    }

    /** What a recipe generator writes to. */
    public interface Output {
        /** Adds recipe {@code foodindustrial:<path>}. */
        void add(String path, ProcessRecipe recipe);

        /** Ingredient of an item id or {@code #tag}, built against the data run's registries. */
        Ingredient ingredient(String itemOrTag);
    }

    /** Registers a recipe generator; it runs once per data run. */
    public static void register(Consumer<Output> generator) {
        GENERATORS.add(generator);
    }

    /**
     * Recipe JSON as a data pack writes it, {@code type} included.
     *
     * @param ops JSON ops with registry access ({@link PlatformRecipes#jsonOps}, {@link PlatformDatagen.Sink#ops})
     */
    public static JsonObject toJson(ProcessRecipe recipe, DynamicOps<JsonElement> ops) {
        DataResult<JsonElement> result = ProcessRecipe.mapCodec(recipe.type()).codec().encodeStart(ops, recipe);
        JsonElement body = result.result().orElseThrow(() -> new IllegalStateException("Cannot encode recipe: "
            + result.error().map(error -> error.message()).orElse("?")));
        JsonObject json = new JsonObject();
        json.addProperty("type", FoodIndustrial.MOD_ID + ":" + recipe.type().id());
        body.getAsJsonObject().entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
        return json;
    }

    static void bootstrap() {
        PlatformDatagen.register("process recipes", RecipeGen::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        Map<String, ProcessRecipe> recipes = new LinkedHashMap<>();
        Output output = new Output() {
            @Override
            public void add(String path, ProcessRecipe recipe) {
                if (recipes.putIfAbsent(path, recipe) != null) {
                    throw new IllegalStateException("Duplicate recipe " + path);
                }
            }

            @Override
            public Ingredient ingredient(String itemOrTag) {
                return sink.ingredient(itemOrTag);
            }
        };
        GENERATORS.forEach(generator -> generator.accept(output));

        List<PlatformRecipes.Loaded<ProcessRecipe>> loaded = new ArrayList<>();
        List<RecipeChecks.Problem> errors = new ArrayList<>();
        List<DeviceSpec> devices = DeviceSpec.all();
        recipes.forEach((path, recipe) -> {
            String id = FoodIndustrial.MOD_ID + ":" + path;
            sink.json(PlatformDatagen.Kind.RECIPE, FoodIndustrial.MOD_ID, path, toJson(recipe, sink.ops()));
            loaded.add(new PlatformRecipes.Loaded<>(id, recipe));
            for (RecipeChecks.Problem problem : RecipeChecks.check(id, recipe, devices)) {
                if (problem.error()) {
                    errors.add(problem);
                } else {
                    FoodIndustrial.LOGGER.warn("{}", problem);
                }
            }
        });
        sink.report("purity_report.md", PurityReport.markdown(PurityReport.compute(loaded, devices)));
        if (!errors.isEmpty()) {
            errors.forEach(problem -> FoodIndustrial.LOGGER.error("{}", problem));
            throw new IllegalStateException(errors.size() + " recipe errors, first: " + errors.get(0));
        }
    }
}
