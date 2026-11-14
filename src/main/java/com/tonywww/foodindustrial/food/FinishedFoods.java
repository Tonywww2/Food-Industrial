package com.tonywww.foodindustrial.food;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import com.tonywww.foodindustrial.registry.SharedTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The finished products of additives/12 (I21, 2026-11-11): items with their names, the tag
 * {@code foodindustrial:foods/<id>} and the category tag {@code foodindustrial:food_categories/<category>}.
 * The table is read from {@code /foodindustrial/foods.json} ({@code tools/foods/export_foods.py}). Effects, food
 * values and recipes come with the product design; until then the products carry placeholder food values
 * (additives/12, 11 问题 43): dishes 4 hunger and 0.3 saturation, drinks 2 and 0.1, seasonings and ingredients none.
 */
public final class FinishedFoods {
    public static final String RESOURCE = "/foodindustrial/foods.json";
    private static final String NS = FoodIndustrial.MOD_ID;
    /** Products registered earlier as food by the T0 devices (I12); here they only get their tags. */
    private static final Set<String> REGISTERED_ELSEWHERE = Set.of("dried_apple", "dried_berries");
    /** Categories of seasonings and cooking ingredients: not eaten on their own (placeholder, 11 问题 43). */
    private static final Set<String> NOT_EATEN = Set.of("fats_oils", "sweeteners", "condiments");
    /**
     * Products in the other categories not eaten as they are: ingredients (flour, egg white powder, fish sauce, shrimp
     * paste, casing, yeast; 11 问题 43), noodles and doughs to be cooked first, and powders to be mixed (11 问题 44).
     */
    private static final Set<String> INGREDIENTS = Set.of("self_rising_flour", "egg_white_powder", "fish_sauce", "shrimp_paste",
        "collagen_casing", "dry_yeast", "dried_noodles", "rice_vermicelli", "hor_fun", "glass_noodles", "starch_noodles",
        "tapioca_pearls", "frozen_dumplings", "frozen_tangyuan", "infant_rice_cereal", "protein_powder", "meal_replacement_powder");
    /** Drinks: 2 hunger, 0.1 saturation. */
    private static final Set<String> DRINKS = Set.of("beverages", "alcoholic");

    /** One row of the table. */
    public record Food(String id, String zh, String en, String category) {
    }

    private static final List<Food> FOODS = load();
    private static final List<Supplier<Item>> ITEMS = new ArrayList<>();

    static {
        for (Food food : FOODS) {
            SharedTags.item(NS + ":foods/" + food.id(), NS + ":" + food.id());
            SharedTags.item(NS + ":food_categories/" + food.category(), NS + ":" + food.id());
            if (REGISTERED_ELSEWHERE.contains(food.id())) {
                continue;
            }
            Supplier<Item> item = PlatformRegistry.item(food.id(), properties(food));
            Lang.add(Lang.item(food.id()), food.zh(), food.en());
            ITEMS.add(item);
        }
        ModCreativeTabs.add(ModCreativeTabs.Tab.PRODUCTS, output -> ITEMS.forEach(item -> output.accept(new ItemStack(item.get()))));
    }

    private FinishedFoods() {
    }

    /** Placeholder food values until the product design (additives/12 opening, 2026-11-12). */
    private static Item.Properties properties(Food food) {
        if (NOT_EATEN.contains(food.category()) || INGREDIENTS.contains(food.id())) {
            return new Item.Properties();
        }
        if (DRINKS.contains(food.category()) || food.id().equals("flavored_tea")) {
            return PlatformItems.food(new Item.Properties(), 2, 0.1F);
        }
        return PlatformItems.food(new Item.Properties(), 4, 0.3F);
    }

    /** The table rows in order. */
    public static List<Food> foods() {
        return Collections.unmodifiableList(FOODS);
    }

    /** Ids whose item model and texture this group generates (all but the ones registered elsewhere). */
    public static List<String> ownIds() {
        return FOODS.stream().map(Food::id).filter(id -> !REGISTERED_ELSEWHERE.contains(id)).toList();
    }

    /** Registers the items. Called during mod construction. */
    public static void bootstrap() {
        FoodData.bootstrap();
    }

    private static List<Food> load() {
        try (InputStream in = FinishedFoods.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE + "; run tools/foods/export_foods.py");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                List<Food> result = new ArrayList<>();
                for (JsonElement element : JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("foods")) {
                    JsonObject json = element.getAsJsonObject();
                    JsonObject name = json.getAsJsonObject("name");
                    result.add(new Food(json.get("id").getAsString(), name.get("zh").getAsString(), name.get("en").getAsString(),
                        json.get("category").getAsString()));
                }
                return result;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + RESOURCE, exception);
        }
    }
}
