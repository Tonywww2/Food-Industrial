package com.tonywww.foodindustrial.food;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformPackagedFluids;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import com.tonywww.foodindustrial.registry.SharedTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

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
import java.util.Set;
import java.util.function.Supplier;

/**
 * The finished products of additives/12 (I21, 2026-11-11): items with their names, the tag
 * {@code foodindustrial:foods/<id>} and the category tag {@code foodindustrial:food_categories/<category>}.
 * The table is read from {@code /foodindustrial/foods.json} ({@code tools/foods/export_foods.py}). Hunger and
 * saturation come from the products' nutrition (I45, {@link FoodNutrition}); products without it are not eaten
 * (seasonings, ingredients, raw noodles and the like, foods/13–22 “食用”).
 * <p>
 * Liquid products (I47, foods/08 §1 rule 5) also register their bulk fluid under the product's ID, with a bucket and
 * the fluid tag {@code foodindustrial:<id>}; a packaged one pours its {@link #PACKAGE} mB out of its container
 * ({@link PlatformPackagedFluids}), into that fluid or into the existing fluid it names ({@code pours}).
 */
public final class FinishedFoods {
    public static final String RESOURCE = "/foodindustrial/foods.json";
    private static final String NS = FoodIndustrial.MOD_ID;
    /** Products registered earlier as food by the T0 devices (I12); here they only get their tags. */
    private static final Set<String> REGISTERED_ELSEWHERE = Set.of("dried_apple", "dried_berries");

    /**
     * One row of the table. {@code stack} is the stack size of the product's form (foods/01 §3.2: loose fresh foods and
     * soups 16, packaged 64); {@code container} the packaging path ({@code glass_jar}, I40). The batches fill both
     * in; rows without them stack to 64 and have no container.
     */
    public record Food(String id, String zh, String en, String category, int stack, String container, @Nullable BulkFluid fluid,
                       @Nullable String pours) {
    }

    /** The bulk fluid of a liquid product: tint and texture kind ({@code liquid} or {@code viscous}). */
    public record BulkFluid(int color, String texture) {
    }

    /** mB in one bottle or jar of a liquid product (foods/09 §1). */
    public static final int PACKAGE = 250;

    private static final List<Food> FOODS = load();
    private static final List<Supplier<? extends Item>> ITEMS = new ArrayList<>();
    private static final Map<String, Supplier<? extends Item>> BY_ID = new LinkedHashMap<>();
    private static final Map<String, PlatformFluids.Registered> FLUIDS = new LinkedHashMap<>();

    static {
        for (Food food : FOODS) {
            SharedTags.item(NS + ":foods/" + food.id(), NS + ":" + food.id());
            SharedTags.item(NS + ":food_categories/" + food.category(), NS + ":" + food.id());
            if (REGISTERED_ELSEWHERE.contains(food.id())) {
                continue;
            }
            Supplier<? extends Item> item = PlatformRegistry.item(food.id(), properties -> new FoodLine.FoodItem(properties, food.container()),
                properties(food));
            Lang.add(Lang.item(food.id()), food.zh(), food.en());
            ITEMS.add(item);
            BY_ID.put(food.id(), item);
        }
        for (Food food : FOODS) {
            if (food.fluid() != null) {
                registerFluid(food);
            }
        }
        for (Food food : FOODS) {
            Supplier<? extends Fluid> poured = food.pours() != null ? pouredMaterial(food.pours())
                : FLUIDS.containsKey(food.id()) ? FLUIDS.get(food.id()).source() : null;
            if (poured != null && !food.container().isEmpty() && BY_ID.containsKey(food.id())) {
                String container = food.container();
                Supplier<? extends Item> back = com.tonywww.foodindustrial.food.tool.FoodTools.returnable(container)
                    ? () -> com.tonywww.foodindustrial.food.tool.FoodTools.item(container) : null;
                PlatformPackagedFluids.register(BY_ID.get(food.id()), poured, PACKAGE, back);
            }
        }
        ModCreativeTabs.add(ModCreativeTabs.Tab.PRODUCTS, output -> {
            ITEMS.forEach(item -> output.accept(new ItemStack(item.get())));
            FLUIDS.values().forEach(fluid -> output.accept(new ItemStack(fluid.bucket().get())));
            ModMaterials.foodIntermediates(output);
        });
        PlatformDatagen.register("product fluids", FinishedFoods::generate);
    }

    /** The bulk fluid of a liquid product: same ID, a bucket, names, the fluid tag {@code foodindustrial:<id>}. */
    private static void registerFluid(Food food) {
        PlatformFluids.Spec spec = PlatformFluids.Spec.liquid(food.fluid().color());
        if ("viscous".equals(food.fluid().texture())) {
            spec = spec.withTextures("block/fluid_viscous_still", "block/fluid_viscous_flowing").withViscosity(4000);
        }
        FLUIDS.put(food.id(), PlatformFluids.register(food.id(), spec));
        Lang.addName(Lang.fluid(food.id()), food.zh(), food.en());
        Lang.addName(Lang.block(food.id()), food.zh(), food.en());
        Lang.addName(Lang.item(food.id() + "_bucket"), food.zh() + "桶", food.en() + " Bucket");
        ModTags.extraFluid(food.id(), NS + ":" + food.id());
        ModTags.extraFluid(food.id(), NS + ":flowing_" + food.id());
    }

    private static Supplier<? extends Fluid> pouredMaterial(String material) {
        ModMaterials.Entry entry = ModMaterials.get(material);
        if (entry == null || entry.fluid() == null) {
            throw new IllegalStateException("Product pours into unknown fluid " + material);
        }
        return entry.fluid().source();
    }

    /** The product item of {@code id}, if registered here. */
    @Nullable
    public static Supplier<? extends Item> item(String id) {
        return BY_ID.get(id);
    }

    /** The bulk fluid of a liquid product, if it has one. */
    @Nullable
    public static PlatformFluids.Registered fluid(String id) {
        return FLUIDS.get(id);
    }

    /** Bucket models and liquid block states of the bulk fluids. */
    private static void generate(PlatformDatagen.Sink sink) {
        for (String id : FLUIDS.keySet()) {
            PlatformDatagen.bucketModel(sink, NS, id + "_bucket", NS + ":" + id);
            com.google.gson.JsonObject model = new com.google.gson.JsonObject();
            com.google.gson.JsonObject textures = new com.google.gson.JsonObject();
            textures.addProperty("particle", NS + ":block/fluid_still");
            model.add("textures", textures);
            sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, model);
            com.google.gson.JsonObject variant = new com.google.gson.JsonObject();
            variant.addProperty("model", NS + ":block/" + id);
            com.google.gson.JsonObject variants = new com.google.gson.JsonObject();
            variants.add("", variant);
            com.google.gson.JsonObject state = new com.google.gson.JsonObject();
            state.add("variants", variants);
            sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
        }
    }

    private FinishedFoods() {
    }

    /** Food values from the nutrition (I45), and the stack size of the product's form. */
    private static Item.Properties properties(Food food) {
        return FoodNutrition.properties(food.id()).stacksTo(food.stack());
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
        FoodNutrition.bootstrap();
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
                    int stack = json.has("stack") ? json.get("stack").getAsInt() : 64;
                    String container = json.has("container") ? json.get("container").getAsString() : "";
                    BulkFluid fluid = null;
                    if (json.has("fluid")) {
                        JsonObject bulk = json.getAsJsonObject("fluid");
                        fluid = new BulkFluid(0xFF000000 | Integer.parseInt(bulk.get("color").getAsString().substring(1), 16),
                            bulk.get("texture").getAsString());
                    }
                    String pours = json.has("pours") ? json.get("pours").getAsString() : null;
                    result.add(new Food(json.get("id").getAsString(), name.get("zh").getAsString(), name.get("en").getAsString(),
                        json.get("category").getAsString(), stack, container, fluid, pours));
                }
                return result;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + RESOURCE, exception);
        }
    }
}
