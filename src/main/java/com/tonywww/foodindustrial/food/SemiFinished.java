package com.tonywww.foodindustrial.food;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.PlatformItem;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.food.PlatformFoodData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * The semi-finished item (foods/08 §3, I38): one item for every solid state that belongs to a single product, told
 * apart by its data, the key {@code product|stage} (the product's id without the namespace, the stage key). Its name is "product (stage)"; recipes match the key exactly; it
 * stacks to 64 and is not eaten. Stage names come with the product batches ({@link #stage}).
 */
public final class SemiFinished {
    private static final String NS = FoodIndustrial.MOD_ID;
    private static final Map<String, String[]> STAGES = new TreeMap<>();

    public static final Supplier<Item> ITEM = PlatformRegistry.item("semi_finished", SemiFinishedItem::new, new Item.Properties());

    static {
        Lang.add(Lang.item("semi_finished"), "半成品", "Semi-finished Product");
        Lang.add("item." + NS + ".semi_finished.of", "%s（%s）", "%s (%s)");
    }

    private SemiFinished() {
    }

    /** Registers the item. Called during mod construction. */
    public static void bootstrap() {
    }

    /** Declares a stage name (foods/08 §3: named in the batch documents). */
    public static void stage(String key, String zh, String en) {
        if (STAGES.putIfAbsent(key, new String[]{zh, en}) == null) {
            Lang.add(stageKey(key), zh, en);
        }
    }

    public static String stageKey(String stage) {
        return "semi_finished." + NS + ".stage." + stage;
    }

    /** The data key of a product's stage. */
    public static String key(String product, String stage) {
        return product + "|" + stage;
    }

    /** {@code count} semi-finished items of {@code product} at {@code stage}. */
    public static ItemStack stack(String key, int count) {
        ItemStack stack = new ItemStack(ITEM.get(), count);
        PlatformFoodData.setSemiFinished(stack, key);
        return stack;
    }

    /** Key of the stack, or empty for other items. */
    public static String keyOf(ItemStack stack) {
        return stack.getItem() == ITEM.get() ? PlatformFoodData.semiFinished(stack) : "";
    }

    /** Stage names declared so far, by key. */
    public static Map<String, String[]> stages() {
        return java.util.Collections.unmodifiableMap(STAGES);
    }

    private static final class SemiFinishedItem extends PlatformItem {
        SemiFinishedItem(Properties properties) {
            super(properties);
        }

        @Override
        public Component getName(ItemStack stack) {
            String key = PlatformFoodData.semiFinished(stack);
            int bar = key.indexOf('|');
            if (bar <= 0) {
                return super.getName(stack);
            }
            return Component.translatable("item." + NS + ".semi_finished.of",
                Component.translatable(Lang.item(key.substring(0, bar))), Component.translatable(stageKey(key.substring(bar + 1))));
        }
    }
}
