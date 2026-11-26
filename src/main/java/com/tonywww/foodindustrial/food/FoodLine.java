package com.tonywww.foodindustrial.food;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.food.quality.FoodQuality;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.food.PlatformFoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * The finished-food line's shared pieces (I38): which items are finished products, which items and fluids carry
 * quality, and reading and writing the quality of stacks and fluids.
 * <p>
 * Quality goes onto the main product of a step whenever the step has some, and onto other products only when they
 * carry quality: finished products, semi-finished items, and the intermediates and liquid states the product batches
 * register with {@link #carrier} (foods/08 §1 rules 3–5).
 */
public final class FoodLine {
    private static final String NS = FoodIndustrial.MOD_ID;
    private static final Set<String> PRODUCTS = new HashSet<>();
    private static final Set<String> CARRIERS = new HashSet<>();

    private FoodLine() {
    }

    /** Registers the food line's items. Called during mod construction, after the finished products. */
    public static void bootstrap() {
        for (FinishedFoods.Food food : FinishedFoods.foods()) {
            PRODUCTS.add(NS + ":" + food.id());
            CARRIERS.add(NS + ":" + food.id());
        }
        CARRIERS.add(NS + ":semi_finished");
        SemiFinished.bootstrap();
        FoodCrafting.bootstrap();
        com.tonywww.foodindustrial.food.flavor.Flavorings.bootstrap();
        com.tonywww.foodindustrial.food.quality.QualityText.bootstrap();
        com.tonywww.foodindustrial.food.quality.QualityCommands.bootstrap();
        if (com.tonywww.foodindustrial.platform.Platform.isPhysicalClient()) {
            com.tonywww.foodindustrial.food.quality.client.QualityClient.bootstrap();
        }
        for (String member : new String[]{"chemicals/salt", "chemicals/sodium_nitrite", "chemicals/potassium_nitrite"}) {
            com.tonywww.foodindustrial.registry.SharedTags.item(SALT_POSITION, "#" + NS + ":" + member);
        }
    }

    /**
     * The item tag of a salt position (foods/02 §4.4): recipes write their salt as this tag, which also holds the
     * nitrites, so that nitrite mistaken for salt goes in and marks the product.
     */
    public static final String SALT_POSITION = NS + ":salt_position";

    /** Whether a recipe input is a salt position. */
    public static boolean isSaltPosition(com.tonywww.foodindustrial.recipe.ProcessInput.ItemInput input) {
        return com.tonywww.foodindustrial.platform.recipe.PlatformIngredients.references(input.ingredient()).toString().contains(SALT_POSITION);
    }

    /** Declares an item or fluid id (this mod's path or a full id) that carries quality as a by-product too. */
    public static void carrier(String id) {
        CARRIERS.add(id.contains(":") ? id : NS + ":" + id);
    }

    /** Whether {@code item} is one of the finished products (additives/12, foods/01). */
    public static boolean isProduct(@Nullable Item item) {
        return item != null && PRODUCTS.contains(PlatformTags.itemId(item));
    }

    /** Whether {@code fluid} is a finished product in its fluid form (same ID as the product item, foods/10 opening). */
    public static boolean isProduct(@Nullable Fluid fluid) {
        return fluid != null && PRODUCTS.contains(PlatformTags.fluidId(fluid));
    }

    public static boolean carries(Item item) {
        return CARRIERS.contains(PlatformTags.itemId(item));
    }

    public static boolean carries(Fluid fluid) {
        return CARRIERS.contains(PlatformTags.fluidId(fluid));
    }

    /** Quality code of a stack ({@link FoodQuality}), {@code 0} for none. */
    public static int quality(ItemStack stack) {
        return stack.isEmpty() ? FoodQuality.NONE : PlatformFoodData.quality(stack);
    }

    /** Saturation factor of a quality grade (foods/04 §4, §5.3): ×1.0, ×1.2, ×1.4. */
    public static float saturationFactor(int code) {
        return switch (FoodQuality.grade(code)) {
            case FoodQuality.EXCELLENT -> 1.4F;
            case FoodQuality.GOOD -> 1.2F;
            default -> 1F;
        };
    }

    /** {@code stack} with quality {@code code} (in place); its saturation follows the grade (foods/04 §5.3). */
    public static ItemStack withQuality(ItemStack stack, int code) {
        if (!stack.isEmpty()) {
            PlatformFoodData.setQuality(stack, code);
            PlatformFoodData.scaleSaturation(stack, saturationFactor(code));
        }
        return stack;
    }

    /** Item of a finished product: its saturation follows its quality (1.20.1, foods/04 §5.3). */
    public static final class FoodItem extends com.tonywww.foodindustrial.platform.PlatformItem {
        /** Container path ({@code glass_jar}), or empty; returnable ones come back (I40, machines/05 §4). */
        private final String container;

        public FoodItem(Properties properties) {
            this(properties, "");
        }

        public FoodItem(Properties properties, String container) {
            super(properties);
            this.container = container;
        }

        @Override
        protected float saturationFactor(ItemStack stack) {
            return FoodLine.saturationFactor(quality(stack));
        }

        /** The container given back, or {@code null}. */
        @Nullable
        public Item returnedContainer() {
            return !container.isEmpty() && com.tonywww.foodindustrial.food.tool.FoodTools.returnable(container)
                ? com.tonywww.foodindustrial.food.tool.FoodTools.item(container) : null;
        }

        @Override
        protected Item craftingRemainderItem() {
            return returnedContainer();
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, net.minecraft.world.level.Level level, net.minecraft.world.entity.LivingEntity entity) {
            Item back = returnedContainer();
            boolean creative = entity instanceof net.minecraft.world.entity.player.Player player && player.getAbilities().instabuild;
            ItemStack rest = super.finishUsingItem(stack, level, entity);
            if (back == null || creative) {
                return rest;
            }
            if (rest.isEmpty()) {
                return new ItemStack(back);
            }
            if (entity instanceof net.minecraft.world.entity.player.Player player && !level.isClientSide()) {
                ItemStack container = new ItemStack(back);
                if (!player.getInventory().add(container)) {
                    player.drop(container, false);
                }
            }
            return rest;
        }
    }

    public static int quality(FluidAmount fluid) {
        return fluid.quality();
    }
}
