package com.tonywww.foodindustrial.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Data generation of this mod's process recipes. Content registers generators here; the data run
 * writes their recipes in the node's format, runs {@link RecipeChecks} (errors fail the run) and writes
 * the purity report ({@link PurityReport}) to {@code build/reports/foodindustrial/purity_report.md}.
 */
public final class RecipeGen {
    private static final List<Consumer<Output>> GENERATORS = new ArrayList<>();
    private static final List<Function<Map<String, ProcessRecipe>, List<String>>> SET_CHECKS = new ArrayList<>();
    private static final List<java.util.function.BiConsumer<PlatformDatagen.Sink, Map<String, ProcessRecipe>>> FOLLOWERS = new ArrayList<>();

    private RecipeGen() {
    }

    /** What a recipe generator writes to. */
    public interface Output {
        /** Adds recipe {@code foodindustrial:<path>}. */
        void add(String path, ProcessRecipe recipe);

        /** Ingredient of an item id or {@code #tag}, built against the data run's registries. */
        Ingredient ingredient(String itemOrTag);

        /**
         * Decodes a recipe written as JSON (without {@code type}), its item ingredients (inputs and
         * catalysts) written as plain strings, an item id or {@code #tag}, which are built like
         * {@link #ingredient}. Fails the data run on errors.
         */
        ProcessRecipe parse(ProcessType type, JsonObject body);
    }

    /**
     * Registers a check over all generated recipes (by path); the problems it returns fail the data run
     * (2026-11-11, the M30 slot tags).
     */
    public static void addSetCheck(Function<Map<String, ProcessRecipe>, List<String>> check) {
        SET_CHECKS.add(check);
    }

    /**
     * Registers a generator that writes files derived from all generated recipes (by path), such as the
     * recipes for other mods' machines (I25); it runs after the checks passed.
     */
    public static void addFollower(java.util.function.BiConsumer<PlatformDatagen.Sink, Map<String, ProcessRecipe>> follower) {
        FOLLOWERS.add(follower);
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

    /**
     * What makes two recipes candidates for a conflict (machines/01 §3.2, I31 2026-11-12): recipe type, mode,
     * catalysts and the set of input materials, amounts and purity thresholds left out; the conditions are checked
     * against the devices.
     */
    public static String uniquenessKey(JsonObject json) {
        List<String> inputs = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("inputs")) {
            JsonObject input = element.getAsJsonObject();
            if (input.has("ingredient")) {
                inputs.add("item " + canonical(base(input.get("ingredient"))));
            } else {
                JsonObject fluid = input.getAsJsonObject("fluid").deepCopy();
                fluid.remove("amount");
                fluid.remove("min_purity");
                inputs.add("fluid " + canonical(fluid));
            }
        }
        List<String> catalysts = new ArrayList<>();
        if (json.has("catalyst")) {
            JsonElement written = json.get("catalyst");
            List<JsonElement> entries = new ArrayList<>();
            if (written.isJsonArray()) {
                written.getAsJsonArray().forEach(entries::add);
            } else {
                entries.add(written);
            }
            entries.forEach(entry -> catalysts.add(canonical(base(entry.getAsJsonObject().get("ingredient")))));
        }
        return json.get("type").getAsString() + "|" + (json.has("mode") ? json.get("mode").getAsString() : "") + "|"
            + new java.util.TreeSet<>(catalysts) + "|" + new java.util.TreeSet<>(inputs);
    }

    /** The material of an ingredient written with a purity threshold. */
    private static JsonElement base(JsonElement ingredient) {
        if (ingredient.isJsonObject() && ingredient.getAsJsonObject().has("base")) {
            return base(ingredient.getAsJsonObject().get("base"));
        }
        return ingredient;
    }

    /** JSON with its object keys sorted. */
    private static String canonical(JsonElement element) {
        if (element.isJsonObject()) {
            java.util.TreeMap<String, String> sorted = new java.util.TreeMap<>();
            element.getAsJsonObject().entrySet().forEach(entry -> sorted.put(entry.getKey(), canonical(entry.getValue())));
            return sorted.toString();
        }
        if (element.isJsonArray()) {
            List<String> items = new ArrayList<>();
            element.getAsJsonArray().forEach(item -> items.add(canonical(item)));
            return items.toString();
        }
        return element.toString();
    }

    /** Item that stands in for the written ingredients while decoding: tags are not bound in a data run (26.1). */
    private static final String PLACEHOLDER = "minecraft:stone";

    /**
     * {@link Output#parse}: the string ingredients are swapped for a placeholder item, the rest is
     * decoded, then the ingredients are put back built against the data run's registries.
     */
    private static ProcessRecipe parse(PlatformDatagen.Sink sink, ProcessType type, JsonObject body) {
        JsonObject json = body.deepCopy();
        List<String> inputs = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("inputs")) {
            JsonObject input = element.getAsJsonObject();
            if (input.has("ingredient")) {
                inputs.add(input.get("ingredient").getAsString());
                input.add("ingredient", PlatformDatagen.ingredient(PLACEHOLDER));
            }
        }
        List<String> catalysts = new ArrayList<>();
        if (json.has("catalyst")) {
            JsonElement written = json.get("catalyst");
            List<JsonElement> entries = new ArrayList<>();
            if (written.isJsonArray()) {
                written.getAsJsonArray().forEach(entries::add);
            } else {
                entries.add(written);
            }
            for (JsonElement element : entries) {
                JsonObject catalyst = element.getAsJsonObject();
                catalysts.add(catalyst.get("ingredient").getAsString());
                catalyst.add("ingredient", PlatformDatagen.ingredient(PLACEHOLDER));
            }
        }
        DataResult<ProcessRecipe> result = ProcessRecipe.mapCodec(type).codec().parse(sink.ops(), json);
        ProcessRecipe parsed = result.result().orElseThrow(() -> new IllegalStateException("Bad recipe " + body + ": "
            + result.error().map(error -> error.message()).orElse("?")));
        List<ProcessInput> rebuilt = new ArrayList<>();
        int item = 0;
        for (ProcessInput input : parsed.inputs()) {
            rebuilt.add(input instanceof ProcessInput.ItemInput itemInput
                ? ProcessInput.ItemInput.of(ingredient(sink, inputs.get(item++)), itemInput.count()) : input);
        }
        List<Catalyst> rebuiltCatalysts = new ArrayList<>();
        for (int i = 0; i < parsed.catalysts().size(); i++) {
            rebuiltCatalysts.add(new Catalyst(ingredient(sink, catalysts.get(i)), parsed.catalysts().get(i).runs()));
        }
        return new ProcessRecipe(type, rebuilt, parsed.outputs(), parsed.puritySources(), parsed.purityRange(), parsed.time(),
            parsed.energy(), parsed.conditions(), parsed.mode(), parsed.manualActions(), parsed.vanillaDevices(), rebuiltCatalysts,
            parsed.environmentFactors(), parsed.plate());
    }

    /**
     * A written item ingredient: an item id or {@code #tag}, optionally followed by {@code |} and a
     * {@code min_purity} (grade name or percentage) that wraps it in a purity ingredient (additives/10 §4.1).
     */
    private static Ingredient ingredient(PlatformDatagen.Sink sink, String written) {
        int bar = written.indexOf('|');
        if (bar < 0) {
            return sink.ingredient(written);
        }
        Ingredient base = sink.ingredient(written.substring(0, bar));
        String minimum = written.substring(bar + 1);
        String hint = Purity.materialHint(PlatformIngredients.references(base), false);
        Either<String, Double> threshold;
        try {
            threshold = Either.right(Double.parseDouble(minimum));
        } catch (NumberFormatException exception) {
            threshold = Either.left(minimum);
        }
        return PlatformIngredients.purity(base, PurityThreshold.resolve(threshold, null, hint));
    }

    /**
     * Two recipes told apart by where the device stands (additives/07: the T0 devices take the environment of
     * their position, the recipe with the most conditions wins): the player chooses the place, as a mode.
     */
    private static boolean byPlace(ProcessRecipe a, ProcessRecipe b) {
        return !environment(a).equals(environment(b));
    }

    private static Set<String> environment(ProcessRecipe recipe) {
        Set<String> keys = new java.util.TreeSet<>();
        for (String key : recipe.required().keys()) {
            if (key.startsWith("environment.")) {
                keys.add(key);
            }
        }
        return keys;
    }

    /** Whichever runs, the same: the same inputs give the same products in the same proportion (purity and time aside). */
    static boolean sameProducts(JsonObject a, JsonObject b) {
        List<String> keysA = new ArrayList<>();
        List<Double> amountsA = new ArrayList<>();
        List<String> keysB = new ArrayList<>();
        List<Double> amountsB = new ArrayList<>();
        amounts(a, keysA, amountsA);
        amounts(b, keysB, amountsB);
        if (!keysA.equals(keysB) || amountsA.isEmpty()) {
            return false;
        }
        double ratio = amountsB.get(0) / amountsA.get(0);
        for (int i = 0; i < amountsA.size(); i++) {
            if (Math.abs(amountsA.get(i) * ratio - amountsB.get(i)) > 1e-6) {
                return false;
            }
        }
        return true;
    }

    private static void amounts(JsonObject json, List<String> keys, List<Double> amounts) {
        for (JsonElement element : json.getAsJsonArray("inputs")) {
            JsonObject input = element.getAsJsonObject();
            if (input.has("ingredient")) {
                keys.add("in " + canonical(base(input.get("ingredient"))));
                amounts.add(input.has("count") ? input.get("count").getAsDouble() : 1);
            } else {
                JsonObject fluid = input.getAsJsonObject("fluid").deepCopy();
                double amount = fluid.remove("amount").getAsDouble();
                fluid.remove("min_purity");
                keys.add("in " + canonical(fluid));
                amounts.add(amount);
            }
        }
        for (JsonElement element : json.getAsJsonArray("outputs")) {
            JsonObject output = element.getAsJsonObject();
            String chance = output.has("chance") ? " " + output.get("chance").getAsString() : "";
            if (output.has("fluid")) {
                keys.add("out fluid " + output.get("fluid").getAsString() + chance);
                amounts.add(output.get("amount").getAsDouble());
            } else {
                keys.add("out " + output.get("item").getAsString() + chance);
                amounts.add(output.has("count") ? output.get("count").getAsDouble() : 1);
            }
        }
    }

    /**
     * A hand (T0) recipe and a machine recipe for the same step, the devices different (I31: slaked lime, mogroside):
     * only one runs on the T0 devices, the other on none, and every machine that could run both picks the machine
     * recipe (more conditions, or as many and the smaller id: the rule of {@code RecipeProcessor}).
     */
    private static boolean handAndMachine(String pathA, ProcessRecipe a, String pathB, ProcessRecipe b, List<DeviceSpec> devices,
                                          List<DeviceSpec> shared) {
        boolean handA = devices.stream().anyMatch(device -> device.tier() == Tier.T0 && device.canRun(a));
        boolean handB = devices.stream().anyMatch(device -> device.tier() == Tier.T0 && device.canRun(b));
        if (handA == handB) {
            return false;
        }
        String hand = handA ? pathA : pathB;
        String machine = handA ? pathB : pathA;
        ProcessRecipe handRecipe = handA ? a : b;
        ProcessRecipe machineRecipe = handA ? b : a;
        int order = Integer.compare(machineRecipe.specificity(), handRecipe.specificity());
        return !shared.isEmpty() && (order > 0 || (order == 0 && machine.compareTo(hand) < 0));
    }

    static void bootstrap() {
        PlatformDatagen.register("process recipes", RecipeGen::generate);
        PlatformDatagen.addCheck(PlatformDatagen.Kind.RECIPE, RecipeChecks::checkCraftingJson);
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

            @Override
            public ProcessRecipe parse(ProcessType type, JsonObject body) {
                return RecipeGen.parse(sink, type, body);
            }
        };
        GENERATORS.forEach(generator -> generator.accept(output));

        List<PlatformRecipes.Loaded<ProcessRecipe>> loaded = new ArrayList<>();
        List<RecipeChecks.Problem> errors = new ArrayList<>();
        List<DeviceSpec> devices = DeviceSpec.all();
        Map<String, List<String>> byKey = new java.util.TreeMap<>();
        Map<String, JsonObject> writtenByPath = new java.util.HashMap<>();
        recipes.forEach((path, recipe) -> {
            String id = FoodIndustrial.MOD_ID + ":" + path;
            JsonObject written = toJson(recipe, sink.ops());
            writtenByPath.put(path, written);
            byKey.computeIfAbsent(uniquenessKey(written), key -> new ArrayList<>()).add(path);
            sink.json(PlatformDatagen.Kind.RECIPE, FoodIndustrial.MOD_ID, path, written);
            for (VanillaDevice device : recipe.vanillaDevices()) {
                sink.json(PlatformDatagen.Kind.RECIPE, FoodIndustrial.MOD_ID, "vanilla/" + device.id() + "/" + path,
                    VanillaCookingBridge.bridgeJson(recipe, device, sink.ops()));
            }
            loaded.add(new PlatformRecipes.Loaded<>(id, recipe));
            for (RecipeChecks.Problem problem : RecipeChecks.check(id, recipe, devices)) {
                if (problem.error()) {
                    errors.add(problem);
                } else {
                    FoodIndustrial.LOGGER.warn("{}", problem);
                }
            }
        });
        // recipes of the same type, mode, catalysts and input materials conflict when one device can run both at one time,
        // in one mode: conditions only tell them apart when they change which devices can (machines/01 §3.2, 2026-11-12)
        List<DeviceSpec> runners = devices.stream().filter(device -> !device.id().contains(":dev_")).toList();
        for (List<String> paths : byKey.values()) {
            for (int i = 0; i < paths.size(); i++) {
                for (int j = i + 1; j < paths.size(); j++) {
                    String pathA = paths.get(i);
                    String pathB = paths.get(j);
                    ProcessRecipe a = recipes.get(pathA);
                    ProcessRecipe b = recipes.get(pathB);
                    List<DeviceSpec> shared = runners.stream().filter(device -> device.runsTogether(a, b)).toList();
                    if (shared.isEmpty()) {
                        continue;
                    }
                    String allowed = byPlace(a, b) ? "told apart by the place" : sameProducts(writtenByPath.get(pathA), writtenByPath.get(pathB))
                        ? "the same products" : handAndMachine(pathA, a, pathB, b, runners, shared) ? "hand recipe and machine recipe" : null;
                    if (allowed != null) {
                        FoodIndustrial.LOGGER.info("Same inputs on {}, allowed ({}): {} / {}", shared.get(0).id(), allowed, pathA, pathB);
                        continue;
                    }
                    List<String> pair = List.of(pathA, pathB);
                    errors.add(new RecipeChecks.Problem("(all)", true, "same type, mode, catalysts and input materials, both run on "
                        + shared.get(0).id() + " (machines/01 §3.2): " + pair));
                }
            }
        }
        for (Function<Map<String, ProcessRecipe>, List<String>> check : SET_CHECKS) {
            for (String message : check.apply(Collections.unmodifiableMap(recipes))) {
                errors.add(new RecipeChecks.Problem("(all)", true, message));
            }
        }
        sink.report("purity_report.md", PurityReport.markdown(PurityReport.compute(loaded, devices), PurityReport.earliest(loaded, devices),
            PurityReport.pendingRoutes(loaded)));
        if (!errors.isEmpty()) {
            errors.forEach(problem -> FoodIndustrial.LOGGER.error("{}", problem));
            throw new IllegalStateException(errors.size() + " recipe errors, first: " + errors.get(0));
        }
        Map<String, ProcessRecipe> all = Collections.unmodifiableMap(recipes);
        FOLLOWERS.forEach(follower -> follower.accept(sink, all));
    }
}
