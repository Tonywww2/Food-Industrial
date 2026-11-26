package com.tonywww.foodindustrial.food;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.food.nutrition.Component;
import com.tonywww.foodindustrial.food.nutrition.NutritionFile;
import com.tonywww.foodindustrial.food.nutrition.NutritionGen;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Per-serving nutrition of the finished products (I45), read from {@code /foodindustrial/food_nutrition.json}
 * ({@code tools/foods/nutrition.py} builds it from the batch inputs and the composition sources). It gives the
 * products their hunger and saturation at registration (foods/04 §5.2, §5.3; products without data are not eaten)
 * and the entries of the generated nutrition data file {@code foodindustrial.json} (I34). Keys the composition
 * source does not give stay out of the data (they are never written as 0); keys of components the mod does not
 * know are skipped.
 */
public final class FoodNutrition {
    public static final String RESOURCE = "/foodindustrial/food_nutrition.json";
    private static final String NS = FoodIndustrial.MOD_ID;

    /**
     * One product.
     *
     * @param hunger      hunger points (0: eaten any time)
     * @param coefficient saturation coefficient (0.1, 0.3, 0.6, 0.8): the item's saturation modifier
     * @param energyKcal  energy of one serving
     * @param proteinG    protein of one serving, for the coefficient check
     * @param servingG    grams of one serving
     * @param perServing  per-serving amounts by data key ({@code vitamin_c_mg}), only the ones known
     * @param curingNitrite nitrite added by a colour-retention agent (mg)
     * @param misuseNitrite nitrite when misused for salt (mg)
     * @param basis       where the numbers come from (food, card, sources, processing, assumptions)
     */
    public record Values(int hunger, float coefficient, double energyKcal, double proteinG, double servingG, Map<String, Double> perServing,
                         double curingNitrite, double misuseNitrite, JsonObject basis) {
    }

    private static final Map<String, Values> VALUES = load();

    private FoodNutrition() {
    }

    /** The product's values, or {@code null} when it is not eaten. */
    public static Values of(String id) {
        return VALUES.get(id);
    }

    /** All products with values, by id. */
    public static Map<String, Values> all() {
        return Collections.unmodifiableMap(VALUES);
    }

    /** Item properties with the product's food values, or plain properties when it is not eaten. */
    public static Item.Properties properties(String id) {
        Values values = VALUES.get(id);
        if (values == null) {
            return new Item.Properties();
        }
        return PlatformItems.food(new Item.Properties(), values.hunger(), values.coefficient(), values.hunger() == 0);
    }

    /** Registers the products' nutrition entries and the data generation checks. Called during mod construction. */
    static void bootstrap() {
        NutritionGen.addProducts(FoodNutrition::entries);
        PlatformDatagen.register("food_nutrition", sink -> check());
    }

    private static List<NutritionFile.Entry> entries() {
        List<NutritionFile.Entry> entries = new ArrayList<>();
        VALUES.forEach((id, values) -> {
            double[] amounts = new double[Component.all().length];
            int missing = 0;
            for (Component component : Component.all()) {
                Double amount = values.perServing().get(component.key());
                if (amount == null) {
                    missing |= component.bit();
                } else {
                    amounts[component.ordinal()] = amount;
                }
            }
            entries.add(new NutritionFile.Entry(List.of(NS + ":" + id), values.servingG(), amounts, missing, 0.0, values.curingNitrite(),
                values.misuseNitrite(), Optional.of(values.energyKcal()), Optional.of(values.basis()), Optional.empty()));
        });
        return entries;
    }

    /**
     * Data generation check (I39, I45): every product with values is a finished product and has the hunger and
     * saturation coefficient the algorithm gives for its energy and composition.
     */
    private static void check() {
        for (Map.Entry<String, Values> entry : VALUES.entrySet()) {
            Values values = entry.getValue();
            if (values.hunger() != FoodValues.hunger(values.energyKcal())) {
                throw new IllegalStateException("Food nutrition " + entry.getKey() + ": hunger " + values.hunger() + " is not "
                    + FoodValues.hunger(values.energyKcal()) + " for " + values.energyKcal() + " kcal");
            }
            double kcal = values.energyKcal();
            double sugar = values.perServing().getOrDefault(Component.FAST_SUGAR.key(), 0.0);
            float coefficient = kcal > 0 ? FoodValues.coefficient(values.proteinG() * 4 / kcal, sugar * 4 / kcal) : 0.3F;
            if (Math.abs(coefficient - values.coefficient()) > 1e-6) {
                throw new IllegalStateException("Food nutrition " + entry.getKey() + ": saturation coefficient " + values.coefficient()
                    + " is not " + coefficient);
            }
        }
        java.util.Set<String> products = new java.util.HashSet<>();
        FinishedFoods.foods().forEach(food -> products.add(food.id()));
        for (String id : VALUES.keySet()) {
            if (!products.contains(id)) {
                throw new IllegalStateException("Food nutrition names " + id + ", which is not a finished product");
            }
        }
    }

    private static Map<String, Values> load() {
        try (InputStream in = FoodNutrition.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE + "; run tools/foods/nutrition.py");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                Map<String, Values> result = new LinkedHashMap<>();
                JsonObject foods = JsonParser.parseReader(reader).getAsJsonObject().getAsJsonObject("foods");
                for (Map.Entry<String, JsonElement> entry : foods.entrySet()) {
                    JsonObject json = entry.getValue().getAsJsonObject();
                    Map<String, Double> perServing = new LinkedHashMap<>();
                    for (Map.Entry<String, JsonElement> amount : json.getAsJsonObject("per_serving").entrySet()) {
                        // keys the mod does not know are skipped
                        if (Component.byKey(amount.getKey()) != null) {
                            perServing.put(amount.getKey(), amount.getValue().getAsDouble());
                        }
                    }
                    result.put(entry.getKey(), new Values(json.get("hunger").getAsInt(), json.get("saturation_coefficient").getAsFloat(),
                        json.get("energy_kcal").getAsDouble(), json.get("protein_g").getAsDouble(), json.get("serving_g").getAsDouble(),
                        Collections.unmodifiableMap(perServing),
                        json.has("curing_nitrite_mg") ? json.get("curing_nitrite_mg").getAsDouble() : 0.0,
                        json.has("misuse_nitrite_mg") ? json.get("misuse_nitrite_mg").getAsDouble() : 0.0,
                        json.getAsJsonObject("basis")));
                }
                return Collections.unmodifiableMap(result);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + RESOURCE, exception);
        }
    }
}
