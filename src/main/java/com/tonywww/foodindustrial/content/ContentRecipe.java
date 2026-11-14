package com.tonywww.foodindustrial.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import java.util.Map;

/**
 * Builder of the content batches' process recipes (I18–I20), written as recipe JSON and parsed by
 * {@link RecipeGen.Output#parse}. Conventions (additives/04 §实现约定, game settings, 2026-11-11):
 * <ul>
 *   <li>Item inputs name tags of this mod by path ({@code chemicals/soda_ash}); a path with {@code :} is
 *       written as is ({@code minecraft:charcoal}, {@code #c:...}). {@code min} adds a purity threshold.</li>
 *   <li>Outputs name ids of this mod by path; the purity rule ({@link #src()}, {@link #rm}, {@link #keep()},
 *       {@link #degrade}) and {@link #chance}, {@link #minTier} apply to the last output.</li>
 *   <li>Without {@link #time} the type's base time applies ({@link #BASE_TIME}); energy follows the type's
 *       default.</li>
 *   <li>The batches start at T1 (the T0 chains are the manual recipes of I12): without {@link #tier} a
 *       recipe needs a T1 device.</li>
 * </ul>
 */
public final class ContentRecipe {
    private static final String NS = FoodIndustrial.MOD_ID;
    /** Base time by recipe type at speed ×1 (T1); faster tiers divide it (machines/01 §1). */
    public static final Map<ProcessType, Integer> BASE_TIME = Map.ofEntries(
        Map.entry(ProcessTypes.GRINDING, 100), Map.entry(ProcessTypes.PRESSING, 100), Map.entry(ProcessTypes.SEPARATING, 100),
        Map.entry(ProcessTypes.EXTRACTING, 200), Map.entry(ProcessTypes.BOILING, 200), Map.entry(ProcessTypes.CRYSTALLIZING, 200),
        Map.entry(ProcessTypes.DRYING, 200), Map.entry(ProcessTypes.FUMIGATING, 200), Map.entry(ProcessTypes.KILN, 400),
        Map.entry(ProcessTypes.DISTILLING, 200), Map.entry(ProcessTypes.REACTING, 200), Map.entry(ProcessTypes.ABSORBING, 200),
        Map.entry(ProcessTypes.GAS_REACTING, 200), Map.entry(ProcessTypes.ELECTROLYZING, 200), Map.entry(ProcessTypes.PURIFYING, 200),
        Map.entry(ProcessTypes.FERMENTING, 12000), Map.entry(ProcessTypes.SOLID_FERMENTING, 24000), Map.entry(ProcessTypes.CULTURING, 1200),
        Map.entry(ProcessTypes.MIXING, 100));

    private final RecipeGen.Output out;
    private final String prefix;
    private final ProcessType type;
    private final JsonArray inputs = new JsonArray();
    private final JsonArray outputs = new JsonArray();
    private final JsonObject body = new JsonObject();
    private final JsonObject requirements = new JsonObject();
    private final JsonArray catalysts = new JsonArray();

    private ContentRecipe(RecipeGen.Output out, String prefix, ProcessType type) {
        this.out = out;
        this.prefix = prefix;
        this.type = type;
    }

    /** A recipe of {@code type} added as {@code foodindustrial:<prefix>/<name>}. */
    public static ContentRecipe of(RecipeGen.Output out, String prefix, ProcessType type) {
        return new ContentRecipe(out, prefix, type);
    }

    private static String itemRef(String path) {
        return path.contains(":") ? path : "#" + NS + ":" + path;
    }

    private static String id(String path) {
        return path.contains(":") ? path : NS + ":" + path;
    }

    // ---- inputs ----

    public ContentRecipe in(String tag, int count) {
        return in(tag, count, null);
    }

    /** Item input with a purity threshold ({@code crude}, {@code refined}, {@code food}, {@code lab} or a percentage). */
    public ContentRecipe in(String tag, int count, String min) {
        JsonObject input = new JsonObject();
        input.addProperty("ingredient", itemRef(tag) + (min == null ? "" : "|" + min));
        if (count != 1) {
            input.addProperty("count", count);
        }
        inputs.add(input);
        return this;
    }

    public ContentRecipe fin(String tag, int amount) {
        return fin(tag, amount, null);
    }

    /** Fluid input of fluid tag {@code foodindustrial:<tag>}, optionally with a threshold. */
    public ContentRecipe fin(String tag, int amount, String min) {
        JsonObject fluid = new JsonObject();
        fluid.addProperty("type", NS + ":fluid");
        fluid.addProperty("tag", tag.contains(":") ? tag : NS + ":" + tag);
        fluid.addProperty("amount", amount);
        if (min != null) {
            try {
                fluid.addProperty("min_purity", Double.parseDouble(min));
            } catch (NumberFormatException exception) {
                fluid.addProperty("min_purity", min);
            }
        }
        JsonObject input = new JsonObject();
        input.add("fluid", fluid);
        inputs.add(input);
        return this;
    }

    // ---- outputs ----

    public ContentRecipe out(String item, int count) {
        JsonObject output = new JsonObject();
        output.addProperty("item", id(item));
        if (count != 1) {
            output.addProperty("count", count);
        }
        outputs.add(output);
        return this;
    }

    public ContentRecipe fout(String fluid, int amount) {
        JsonObject output = new JsonObject();
        output.addProperty("fluid", id(fluid));
        output.addProperty("amount", amount);
        outputs.add(output);
        return this;
    }

    private JsonObject last() {
        return outputs.get(outputs.size() - 1).getAsJsonObject();
    }

    /** Chance (0–1) of the last output, a byproduct. */
    public ContentRecipe chance(double value) {
        last().addProperty("chance", value);
        return this;
    }

    /** The last output only on devices of {@code tier} and above. */
    public ContentRecipe minTier(int tier) {
        last().addProperty("min_tier", "t" + tier);
        return this;
    }

    /** Source rule with the material's source purity (or its crude bound). */
    public ContentRecipe src() {
        return purity("source", Double.NaN);
    }

    public ContentRecipe src(double percent) {
        return purity("source", percent);
    }

    public ContentRecipe rm(double percent) {
        return purity("removal", percent);
    }

    public ContentRecipe keep() {
        return purity("keep", Double.NaN);
    }

    public ContentRecipe degrade(double percent) {
        return purity("degrade", percent);
    }

    private ContentRecipe purity(String mode, double value) {
        JsonObject rule = new JsonObject();
        rule.addProperty("mode", mode);
        if (!Double.isNaN(value)) {
            rule.addProperty("value", value);
        }
        last().add("purity", rule);
        return this;
    }

    /** Inputs (by index) that make the start purity. */
    public ContentRecipe sources(int... indices) {
        JsonArray list = new JsonArray();
        for (int index : indices) {
            list.add(index);
        }
        body.add("purity_sources", list);
        return this;
    }

    /** Random purity of the main product (red yeast rice only, additives/10 §2.2). */
    public ContentRecipe purityRange(double low, double high) {
        JsonArray range = new JsonArray();
        range.add(low);
        range.add(high);
        body.add("purity_range", range);
        return this;
    }

    /** Culture plate (M30): each colony is the first product with the input culture's purity as chance, else the second. */
    public ContentRecipe plate(boolean byPurity) {
        JsonObject plate = new JsonObject();
        plate.addProperty("by_purity", byPurity);
        body.add("plate", plate);
        return this;
    }

    /** Culture plate (M30) of four colonies drawn by the products' weights. */
    public ContentRecipe plateWeights(int... weights) {
        JsonObject plate = new JsonObject();
        JsonArray list = new JsonArray();
        for (int weight : weights) {
            list.add(weight);
        }
        plate.add("weights", list);
        body.add("plate", plate);
        return this;
    }

    // ---- conditions ----

    public ContentRecipe mode(String mode) {
        body.addProperty("mode", mode);
        return this;
    }

    public ContentRecipe tier(int tier) {
        requirements.addProperty("min_tier", "t" + tier);
        return this;
    }

    /** Temperature: {@code heated}, {@code high}, {@code extreme}, {@code cooling} or {@code freezing}. */
    public ContentRecipe temp(String value) {
        requirements.addProperty("temperature", value);
        return this;
    }

    /** Pressure: {@code pressurized}, {@code high} or {@code vacuum}. */
    public ContentRecipe pressure(String value) {
        requirements.addProperty("pressure", value);
        return this;
    }

    public ContentRecipe air() {
        requirements.addProperty("atmosphere", "air");
        return this;
    }

    public ContentRecipe airtight() {
        requirements.addProperty("atmosphere", "airtight");
        return this;
    }

    public ContentRecipe inert() {
        requirements.addProperty("atmosphere", "inert");
        return this;
    }

    public ContentRecipe sterile() {
        requirements.addProperty("sterile", true);
        return this;
    }

    public ContentRecipe method(String... methods) {
        JsonArray list = requirements.has("methods") ? requirements.getAsJsonArray("methods") : new JsonArray();
        for (String method : methods) {
            list.add(method);
        }
        requirements.add("methods", list);
        return this;
    }

    /** Heat sensitivity: {@code sensitive} or {@code very_sensitive}. */
    public ContentRecipe heat(String value) {
        requirements.addProperty("heat_sensitivity", value);
        return this;
    }

    public ContentRecipe cat(String tag, int runs) {
        return cat(tag, runs, null);
    }

    /** Catalyst or culture in the C slot, used {@code runs} times; optionally with a purity threshold. */
    public ContentRecipe cat(String tag, int runs, String min) {
        JsonObject catalyst = new JsonObject();
        catalyst.addProperty("ingredient", itemRef(tag) + (min == null ? "" : "|" + min));
        catalyst.addProperty("runs", runs);
        catalysts.add(catalyst);
        return this;
    }

    public ContentRecipe time(int ticks) {
        body.addProperty("time", ticks);
        return this;
    }

    public ContentRecipe energy(int perTick) {
        body.addProperty("energy", perTick);
        return this;
    }

    /** Adds the recipe as {@code foodindustrial:<prefix>/<name>}. */
    public void add(String name) {
        JsonObject json = new JsonObject();
        json.add("inputs", inputs);
        json.add("outputs", outputs);
        if (!body.has("time")) {
            body.addProperty("time", BASE_TIME.get(type));
        }
        if (!requirements.has("min_tier")) {
            requirements.addProperty("min_tier", "t1");
        }
        body.entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
        if (requirements.size() > 0) {
            json.add("requirements", requirements);
        }
        if (!catalysts.isEmpty()) {
            json.add("catalyst", catalysts.size() == 1 ? catalysts.get(0) : catalysts);
        }
        out.add(prefix + "/" + name, out.parse(type, json));
    }
}
