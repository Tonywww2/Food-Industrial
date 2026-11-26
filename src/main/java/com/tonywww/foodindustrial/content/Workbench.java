package com.tonywww.foodindustrial.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.food.FoodCrafting;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeGen;

/**
 * Builder of the food line's crafting-table steps ({@link FoodCrafting}, {@code foodindustrial:food_crafting}): the T0
 * way of R19, R20, R26, R27 and R29 (machines/05 §2.1). Ingredients are written like {@link ContentRecipe}'s
 * (a path of this mod's tag, or an id or {@code #tag} with {@code :}; {@code |grade} adds a purity threshold); each
 * recipe is decoded once at data generation, so a malformed one fails the run.
 */
public final class Workbench {
    private static final String NS = FoodIndustrial.MOD_ID;

    private final PlatformDatagen.Sink sink;
    private final String prefix;

    public Workbench(PlatformDatagen.Sink sink, String prefix) {
        this.sink = sink;
        this.prefix = prefix;
    }

    public Recipe of(ProcessType process) {
        return new Recipe(process);
    }

    public final class Recipe {
        private final ProcessType process;
        private final JsonArray ingredients = new JsonArray();
        private final JsonArray tools = new JsonArray();
        private final java.util.List<java.util.List<String>> needs = new java.util.ArrayList<>();
        private String makes;
        private JsonObject result;
        private JsonObject quality;
        private String condition;

        private Recipe(ProcessType process) {
            this.process = process;
        }

        private JsonElement ingredient(String written) {
            int bar = written.indexOf('|');
            String base = bar < 0 ? written : written.substring(0, bar);
            String ref = base.contains(":") ? base : "#" + NS + ":" + base;
            String full = bar < 0 ? ref : ref + written.substring(bar);
            net.minecraft.world.item.crafting.Ingredient ingredient = RecipeGen.ingredient(sink, full);
            needs.add(FoodTiers.refs(ingredient));
            return PlatformIngredients.codec().encodeStart(sink.ops(), ingredient).result()
                .orElseThrow(() -> new IllegalStateException("Cannot write ingredient " + written));
        }

        /** {@code count} slots of tag {@code tag} (or an item id), optionally with a purity threshold. */
        public Recipe in(String tag, int count) {
            JsonObject input = new JsonObject();
            input.add("ingredient", ingredient(tag));
            if (count != 1) {
                input.addProperty("count", count);
            }
            ingredients.add(input);
            return this;
        }

        public Recipe in(String tag, int count, String min) {
            return in(tag + "|" + min, count);
        }

        /**
         * A fluid of fluid tag {@code tag} (this mod's path or a full id), at least {@code amount} mB, in one container that
         * comes back empty (I48).
         */
        public Recipe fin(String tag, int amount) {
            String full = tag.contains(":") ? tag : NS + ":" + tag;
            needs.add(java.util.List.of("fluid:#" + full));
            JsonObject fluid = new JsonObject();
            fluid.addProperty("type", NS + ":fluid");
            fluid.addProperty("tag", full);
            fluid.addProperty("amount", amount);
            JsonObject input = new JsonObject();
            input.add("fluid", fluid);
            ingredients.add(input);
            return this;
        }

        public Recipe semi(String key, int count) {
            needs.add(java.util.List.of("semi:" + key));
            JsonObject input = new JsonObject();
            input.addProperty("semi_finished", key);
            if (count != 1) {
                input.addProperty("count", count);
            }
            ingredients.add(input);
            return this;
        }

        /** A knife or mold that stays in the grid. */
        public Recipe tool(String itemOrTag) {
            tools.add(ingredient(itemOrTag));
            return this;
        }

        public Recipe out(String item, int count) {
            result = new JsonObject();
            makes = item.contains(":") ? item : NS + ":" + item;
            result.addProperty("item", makes);
            if (count != 1) {
                result.addProperty("count", count);
            }
            return this;
        }

        /** Dosage form of the result, a flavouring (I48). */
        public Recipe form(String dosage) {
            result.addProperty("form", dosage);
            return this;
        }

        public Recipe sout(String key, int count) {
            makes = "semi:" + key;
            result = new JsonObject();
            result.addProperty("semi_finished", key);
            if (count != 1) {
                result.addProperty("count", count);
            }
            return this;
        }

        /** The ingredient step's quality items, written as for {@link ContentRecipe#quality}. */
        public Recipe quality(String minGrade, String... items) {
            quality = new JsonObject();
            JsonArray list = new JsonArray();
            for (String item : items) {
                int colon = item.indexOf(':');
                if (colon < 0) {
                    list.add(item);
                    continue;
                }
                JsonObject slot = new JsonObject();
                slot.addProperty("item", item.substring(0, colon));
                JsonArray accepts = new JsonArray();
                for (String accepted : item.substring(colon + 1).split(",")) {
                    accepts.add(accepted);
                }
                slot.add("accepts", accepts);
                list.add(slot);
            }
            quality.add("items", list);
            if (minGrade != null && !minGrade.equals("food")) {
                quality.addProperty("min_grade", minGrade);
            }
            return this;
        }

        /** Loads the recipe only while the item tag {@code tag} (this mod's path) has entries (I49, as {@link ContentRecipe#whenTag}). */
        public Recipe whenTag(String tag) {
            condition = tag.contains(":") ? tag : NS + ":" + tag;
            return this;
        }

        /** Writes the recipe as {@code foodindustrial:<prefix>/workbench/<name>}. */
        public void add(String name) {
            JsonObject body = new JsonObject();
            body.addProperty("process", process.id());
            body.add("ingredients", ingredients);
            if (!tools.isEmpty()) {
                body.add("tools", tools);
            }
            body.add("result", result);
            if (quality != null) {
                body.add("quality", quality);
            }
            // decoded with placeholder ingredients: tags are not bound during data generation (26.1 needs them bound)
            JsonObject check = body.deepCopy();
            check.getAsJsonArray("ingredients").forEach(element -> {
                if (element.getAsJsonObject().has("ingredient")) {
                    element.getAsJsonObject().add("ingredient", PlatformDatagen.ingredient("minecraft:stone"));
                }
            });
            if (check.has("tools")) {
                JsonArray placeholders = new JsonArray();
                check.getAsJsonArray("tools").forEach(element -> placeholders.add(PlatformDatagen.ingredient("minecraft:stone")));
                check.add("tools", placeholders);
            }
            // a finished product takes flavourings, never the flavouring materials themselves (I48, foods/11)
            if (!prefix.equals("food2")) {
                for (java.util.List<String> need : needs) {
                    for (String ref : need) {
                        String note = com.tonywww.foodindustrial.food.flavor.Flavorings.flavorMaterialOfTag(
                            ref.startsWith("fluid:") ? ref.substring("fluid:".length()) : ref);
                        if (note != null) {
                            throw new IllegalStateException("Workbench recipe " + name + " takes the flavouring material " + note
                                + "; products take a flavouring (foods/11 §6)");
                        }
                    }
                }
            }
            DataResult<FoodCrafting> parsed = FoodCrafting.CODEC.codec().parse(sink.ops(), check);
            if (parsed.result().isEmpty()) {
                throw new IllegalStateException("Bad workbench recipe " + name + ": " + parsed.error().map(error -> error.message()).orElse("?"));
            }
            JsonObject json = new JsonObject();
            json.addProperty("type", NS + ":food_crafting");
            body.entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
            sink.json(PlatformDatagen.Kind.RECIPE, NS, prefix + "/workbench/" + name, condition == null ? json
                : com.tonywww.foodindustrial.platform.recipe.PlatformRecipeConditions.whenTagNotEmpty(json, condition));
            FoodTiers.workbench(NS + ":" + prefix + "/workbench/" + name, needs, makes);
        }
    }
}
