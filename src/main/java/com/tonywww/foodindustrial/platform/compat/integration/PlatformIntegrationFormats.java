package com.tonywww.foodindustrial.platform.compat.integration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.integration.ExternalRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Recipe files of other mods' machines (I25), in the format each mod reads on this node: 1.20.1 writes
 * {@code item}/{@code nbt} (purity as the SNBT string {@code {Purity:<b>b}}), 1.21.1 writes
 * {@code id}/{@code components}. The formats were read from the mods' own recipe files (Create 6.0.8 /
 * 6.0.10, Thermal Expansion 11.0, Mekanism 10.4 / 10.7, PneumaticCraft 6.0 / 8.2, NuclearCraft 1.2.37).
 */
public final class PlatformIntegrationFormats {
    //? if forge {
    private static final Set<String> AVAILABLE = Set.of("farmersdelight", "create", "thermal", "mekanism", "pneumaticcraft", "nuclearcraft");
    //?} else if <26.1 {
    /*private static final Set<String> AVAILABLE = Set.of("farmersdelight", "create", "mekanism", "pneumaticcraft");
    *///?} else {
    /*private static final Set<String> AVAILABLE = Set.of();
    *///?}

    private PlatformIntegrationFormats() {
    }

    /** Whether the integration has a release of its mod for this node (I25 node table). */
    public static boolean available(String integration) {
        return AVAILABLE.contains(integration);
    }

    /**
     * The loader's difference ingredient: what {@code base} matches except what {@code subtracted} matches
     * (Forge {@code forge:difference}, NeoForge {@code neoforge:difference}); the other mods' machines read
     * standard ingredients, so they take it like any other (I25, 2026-11-11).
     */
    public static JsonObject difference(JsonElement base, JsonElement subtracted) {
        JsonObject json = new JsonObject();
        //? if forge {
        json.addProperty("type", "forge:difference");
        //?} else {
        /*json.addProperty("type", "neoforge:difference");
        *///?}
        json.add("base", base.deepCopy());
        json.add("subtracted", subtracted.deepCopy());
        return json;
    }

    /** The recipe file of {@code recipe} for machine {@code type}, or {@code null} when its format cannot hold it. */
    @Nullable
    public static JsonObject write(String type, ExternalRecipe recipe) {
        JsonObject json = switch (type) {
            case "create:milling", "create:crushing" -> createProcessing(recipe);
            case "create:compacting", "create:mixing" -> createBasin(type, recipe);
            case "thermal:pulverizer", "thermal:press", "thermal:centrifuge", "thermal:pyrolyzer" -> thermalItem(recipe);
            case "thermal:crystallizer" -> thermalCrystallizer(recipe);
            case "thermal:refinery" -> thermalRefinery(recipe);
            case "mekanism:crushing", "mekanism:enriching" -> mekanismItem(recipe);
            case "mekanism:evaporating" -> mekanismEvaporating(recipe);
            case "pneumaticcraft:pressure_chamber" -> pressureChamber(recipe);
            case "pneumaticcraft:thermo_plant" -> thermoPlant(recipe);
            case "pneumaticcraft:heat_frame_cooling" -> heatFrame(recipe);
            case "pneumaticcraft:refinery" -> pncRefinery(recipe);
            case "nuclearcraft:rock_crusher", "nuclearcraft:crystallizer", "nuclearcraft:electrolyzer" -> nuclearcraft(recipe);
            default -> null;
        };
        if (json == null) {
            return null;
        }
        JsonObject result = new JsonObject();
        result.addProperty("type", type);
        json.entrySet().forEach(entry -> result.add(entry.getKey(), entry.getValue()));
        return result;
    }

    // ---- shared pieces ----

    private static String snbt(int purity) {
        return "{" + com.tonywww.foodindustrial.platform.purity.PlatformPurity.TAG_KEY + ":" + purity + "b}";
    }

    /** An item stack result: id, count and purity in the node's stack format. */
    private static JsonObject itemStack(ExternalRecipe.ItemOut output) {
        JsonObject json = new JsonObject();
        //? if forge {
        json.addProperty("item", output.id());
        json.addProperty("count", output.count());
        if (output.purity() != Purity.NONE) {
            json.addProperty("nbt", snbt(output.purity()));
        }
        //?} else {
        /*json.addProperty("id", output.id());
        json.addProperty("count", output.count());
        if (output.purity() != Purity.NONE) {
            JsonObject components = new JsonObject();
            components.addProperty("foodindustrial:purity", output.purity());
            json.add("components", components);
        }
        *///?}
        return json;
    }

    /** A fluid stack result: id, amount and purity in the node's stack format. */
    private static JsonObject fluidStack(ExternalRecipe.FluidOut output) {
        JsonObject json = new JsonObject();
        //? if forge {
        json.addProperty("fluid", output.id());
        json.addProperty("amount", output.amount());
        if (output.purity() != Purity.NONE) {
            json.addProperty("nbt", snbt(output.purity()));
        }
        //?} else {
        /*json.addProperty("id", output.id());
        json.addProperty("amount", output.amount());
        if (output.purity() != Purity.NONE) {
            JsonObject components = new JsonObject();
            components.addProperty("foodindustrial:purity", output.purity());
            json.add("components", components);
        }
        *///?}
        return json;
    }

    /** {@code ingredient} with a count field added, or {@code null} when it is not an object. */
    @Nullable
    private static JsonObject counted(JsonElement ingredient, String key, int count) {
        if (!ingredient.isJsonObject()) {
            return null;
        }
        JsonObject json = ingredient.getAsJsonObject().deepCopy();
        if (count != 1) {
            json.addProperty(key, count);
        }
        return json;
    }

    /** Fluid ingredient {@code {<tagKey>: tag, amount}} or {@code {<fluidKey>: id, amount}}. */
    private static JsonObject fluidIngredient(ExternalRecipe.FluidIn input, String tagKey, String fluidKey) {
        JsonObject json = new JsonObject();
        if (input.tag() != null) {
            json.addProperty(tagKey, input.tag());
        } else {
            json.addProperty(fluidKey, input.fluid());
        }
        json.addProperty("amount", input.amount());
        return json;
    }

    private static boolean anyPurityInput(ExternalRecipe recipe) {
        return recipe.items().stream().anyMatch(ExternalRecipe.ItemIn::purity);
    }

    // ---- Create ----

    private static String createTime() {
        //? if forge {
        return "processingTime";
        //?} else {
        /*return "processing_time";
        *///?}
    }

    private static JsonArray createResults(ExternalRecipe recipe) {
        JsonArray results = new JsonArray();
        for (ExternalRecipe.ItemOut output : recipe.itemOutputs()) {
            JsonObject json = itemStack(output);
            if (output.chance() < 1) {
                json.addProperty("chance", (float) output.chance());
            }
            results.add(json);
        }
        for (ExternalRecipe.FluidOut output : recipe.fluidOutputs()) {
            results.add(fluidStack(output));
        }
        return results;
    }

    private static JsonArray createIngredients(ExternalRecipe recipe) {
        JsonArray ingredients = new JsonArray();
        for (ExternalRecipe.ItemIn input : recipe.items()) {
            for (int i = 0; i < input.count(); i++) {
                ingredients.add(input.ingredient().deepCopy());
            }
        }
        for (ExternalRecipe.FluidIn input : recipe.fluids()) {
            //? if forge {
            ingredients.add(fluidIngredient(input, "fluidTag", "fluid"));
            //?} else {
            /*JsonObject json = fluidIngredient(input, "tag", "fluid");
            json.addProperty("type", input.tag() != null ? "neoforge:tag" : "neoforge:single");
            ingredients.add(json);
            *///?}
        }
        return ingredients;
    }

    private static JsonObject createProcessing(ExternalRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add("ingredients", createIngredients(recipe));
        json.add("results", createResults(recipe));
        json.addProperty(createTime(), Math.max(recipe.time(), 20));
        return json;
    }

    @Nullable
    private static JsonObject createBasin(String type, ExternalRecipe recipe) {
        if (recipe.itemCount() > 9) {
            return null;
        }
        JsonObject json = new JsonObject();
        json.add("ingredients", createIngredients(recipe));
        json.add("results", createResults(recipe));
        // Concentrating in a basin needs a heated one (P11, P28).
        if (recipe.heated() || recipe.recipeType().equals("boiling")) {
            //? if forge {
            json.addProperty("heatRequirement", "heated");
            //?} else {
            /*json.addProperty("heat_requirement", "heated");
            *///?}
        }
        return json;
    }

    // ---- Thermal Expansion (1.20.1) ----

    private static JsonArray thermalResults(ExternalRecipe recipe) {
        JsonArray results = new JsonArray();
        for (ExternalRecipe.ItemOut output : recipe.itemOutputs()) {
            JsonObject json = itemStack(output);
            if (output.chance() < 1) {
                json.addProperty("chance", (float) output.chance());
            }
            results.add(json);
        }
        for (ExternalRecipe.FluidOut output : recipe.fluidOutputs()) {
            results.add(fluidStack(output));
        }
        return results;
    }

    private static int thermalEnergy(ExternalRecipe recipe) {
        return (int) Math.max(400, Math.min(Integer.MAX_VALUE, recipe.energy()));
    }

    @Nullable
    private static JsonObject thermalItem(ExternalRecipe recipe) {
        ExternalRecipe.ItemIn input = recipe.items().get(0);
        JsonObject ingredient = counted(input.ingredient(), "count", input.count());
        if (ingredient == null) {
            return null;
        }
        JsonObject json = new JsonObject();
        json.add("ingredient", ingredient);
        json.add("result", thermalResults(recipe));
        json.addProperty("energy", thermalEnergy(recipe));
        return json;
    }

    @Nullable
    private static JsonObject thermalCrystallizer(ExternalRecipe recipe) {
        JsonArray ingredients = new JsonArray();
        ingredients.add(fluidIngredient(recipe.fluids().get(0), "fluid_tag", "fluid"));
        for (ExternalRecipe.ItemIn input : recipe.items()) {
            JsonObject ingredient = counted(input.ingredient(), "count", input.count());
            if (ingredient == null) {
                return null;
            }
            ingredients.add(ingredient);
        }
        JsonObject json = new JsonObject();
        json.add("ingredients", ingredients);
        json.add("result", thermalResults(recipe));
        json.addProperty("energy", thermalEnergy(recipe));
        return json;
    }

    private static JsonObject thermalRefinery(ExternalRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add("ingredient", fluidIngredient(recipe.fluids().get(0), "fluid_tag", "fluid"));
        json.add("result", thermalResults(recipe));
        json.addProperty("energy", thermalEnergy(recipe));
        return json;
    }

    // ---- Mekanism ----

    @Nullable
    private static JsonObject mekanismItem(ExternalRecipe recipe) {
        ExternalRecipe.ItemIn input = recipe.items().get(0);
        JsonObject json = new JsonObject();
        //? if forge {
        JsonObject sized = new JsonObject();
        sized.add("ingredient", input.ingredient().deepCopy());
        if (input.count() != 1) {
            sized.addProperty("amount", input.count());
        }
        //?} else {
        /*JsonObject sized = counted(input.ingredient(), "count", input.count());
        if (sized == null) {
            return null;
        }
        if (!sized.has("count")) {
            sized.addProperty("count", 1);
        }
        *///?}
        json.add("input", sized);
        json.add("output", itemStack(recipe.itemOutputs().get(0)));
        return json;
    }

    private static JsonObject mekanismEvaporating(ExternalRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add("input", fluidIngredient(recipe.fluids().get(0), "tag", "fluid"));
        json.add("output", fluidStack(recipe.fluidOutputs().get(0)));
        return json;
    }

    // ---- PneumaticCraft: Repressurized ----

    @Nullable
    private static JsonObject pressureChamber(ExternalRecipe recipe) {
        JsonArray inputs = new JsonArray();
        for (ExternalRecipe.ItemIn input : recipe.items()) {
            //? if forge {
            if (input.count() == 1) {
                inputs.add(input.ingredient().deepCopy());
                continue;
            }
            if (input.purity() || !input.ingredient().isJsonObject()) {
                return null;
            }
            JsonObject stacked = input.ingredient().getAsJsonObject().deepCopy();
            if (!stacked.has("tag") && !stacked.has("item")) {
                return null;
            }
            stacked.addProperty("type", "pneumaticcraft:stacked_item");
            stacked.addProperty("count", input.count());
            inputs.add(stacked);
            //?} else {
            /*JsonObject sized = counted(input.ingredient(), "count", input.count());
            if (sized == null) {
                return null;
            }
            inputs.add(sized);
            *///?}
        }
        JsonArray results = new JsonArray();
        recipe.itemOutputs().forEach(output -> results.add(itemStack(output)));
        JsonObject json = new JsonObject();
        json.add("inputs", inputs);
        json.addProperty("pressure", 2.0);
        json.add("results", results);
        return json;
    }

    private static JsonObject pncFluid(ExternalRecipe.FluidIn input) {
        //? if forge {
        JsonObject json = fluidIngredient(input, "tag", "fluid");
        json.addProperty("type", "pneumaticcraft:fluid");
        return json;
        //?} else {
        /*return fluidIngredient(input, "tag", "fluid");
        *///?}
    }

    private static JsonObject pncHot() {
        JsonObject temperature = new JsonObject();
        //? if forge {
        temperature.addProperty("min_temp", 373);
        //?} else {
        /*temperature.addProperty("min", 373);
        *///?}
        return temperature;
    }

    @Nullable
    private static JsonObject thermoPlant(ExternalRecipe recipe) {
        if (recipe.items().isEmpty() && recipe.fluids().isEmpty() || anyPurityInput(recipe) && recipe.items().get(0).count() != 1) {
            return null;
        }
        JsonObject json = new JsonObject();
        //? if forge {
        if (!recipe.fluids().isEmpty()) {
            json.add("fluid_input", pncFluid(recipe.fluids().get(0)));
        }
        if (!recipe.items().isEmpty()) {
            json.add("item_input", recipe.items().get(0).ingredient().deepCopy());
        }
        if (!recipe.fluidOutputs().isEmpty()) {
            json.add("fluid_output", fluidStack(recipe.fluidOutputs().get(0)));
        }
        if (!recipe.itemOutputs().isEmpty()) {
            json.add("item_output", itemStack(recipe.itemOutputs().get(0)));
        }
        json.addProperty("exothermic", false);
        json.addProperty("air_use_multiplier", 1.0);
        json.addProperty("pressure", 0.0);
        json.addProperty("speed", 1.0);
        //?} else {
        /*JsonObject inputs = new JsonObject();
        if (!recipe.fluids().isEmpty()) {
            inputs.add("fluid", pncFluid(recipe.fluids().get(0)));
        }
        if (!recipe.items().isEmpty()) {
            inputs.add("item", recipe.items().get(0).ingredient().deepCopy());
        }
        JsonObject outputs = new JsonObject();
        if (!recipe.fluidOutputs().isEmpty()) {
            outputs.add("fluid_output", fluidStack(recipe.fluidOutputs().get(0)));
        }
        if (!recipe.itemOutputs().isEmpty()) {
            outputs.add("item_output", itemStack(recipe.itemOutputs().get(0)));
        }
        json.add("inputs", inputs);
        json.add("outputs", outputs);
        *///?}
        if (recipe.heated()) {
            json.add("temperature", pncHot());
        }
        return json;
    }

    private static JsonObject heatFrame(ExternalRecipe recipe) {
        JsonObject json = new JsonObject();
        //? if forge {
        json.add("input", pncFluid(recipe.fluids().get(0)));
        json.addProperty("max_temp", 273);
        json.add("result", itemStack(recipe.itemOutputs().get(0)));
        //?} else {
        /*JsonObject input = new JsonObject();
        input.add("fluid", pncFluid(recipe.fluids().get(0)));
        json.add("input", input);
        json.add("output", itemStack(recipe.itemOutputs().get(0)));
        json.addProperty("temperature", 273);
        *///?}
        return json;
    }

    private static JsonObject pncRefinery(ExternalRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add("input", pncFluid(recipe.fluids().get(0)));
        JsonArray results = new JsonArray();
        recipe.fluidOutputs().forEach(output -> results.add(fluidStack(output)));
        //? if forge {
        json.add("results", results);
        //?} else {
        /*json.add("outputs", results);
        *///?}
        json.add("temperature", pncHot());
        return json;
    }

    // ---- NuclearCraft (1.20.1) ----

    @Nullable
    private static JsonObject nuclearcraft(ExternalRecipe recipe) {
        JsonObject json = new JsonObject();
        if (!recipe.items().isEmpty()) {
            JsonArray inputs = new JsonArray();
            for (ExternalRecipe.ItemIn input : recipe.items()) {
                JsonObject ingredient = counted(input.ingredient(), "count", input.count());
                if (ingredient == null) {
                    return null;
                }
                inputs.add(ingredient);
            }
            json.add("input", inputs);
        }
        if (!recipe.fluids().isEmpty()) {
            JsonArray inputs = new JsonArray();
            recipe.fluids().forEach(input -> inputs.add(fluidIngredient(input, "tag", "fluid")));
            json.add("inputFluids", inputs);
        }
        if (!recipe.itemOutputs().isEmpty()) {
            // Outputs are ingredients in NuclearCraft; an NBT ingredient keeps the purity on the product.
            JsonArray outputs = new JsonArray();
            for (ExternalRecipe.ItemOut output : recipe.itemOutputs()) {
                JsonObject json1 = new JsonObject();
                if (output.purity() != Purity.NONE) {
                    json1.addProperty("type", "forge:nbt");
                    json1.addProperty("nbt", snbt(output.purity()));
                }
                json1.addProperty("item", output.id());
                if (output.count() != 1) {
                    json1.addProperty("count", output.count());
                }
                outputs.add(json1);
            }
            json.add("output", outputs);
        }
        if (!recipe.fluidOutputs().isEmpty()) {
            JsonArray outputs = new JsonArray();
            for (ExternalRecipe.FluidOut output : recipe.fluidOutputs()) {
                JsonObject json1 = new JsonObject();
                json1.addProperty("fluid", output.id());
                json1.addProperty("amount", output.amount());
                outputs.add(json1);
            }
            json.add("outputFluids", outputs);
        }
        json.addProperty("powerModifier", 1.0);
        json.addProperty("radiation", 1.0);
        json.addProperty("timeModifier", 1.0);
        return json;
    }
}
