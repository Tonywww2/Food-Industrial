package com.tonywww.foodindustrial.food.tool;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.platform.PlatformItem;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import com.tonywww.foodindustrial.registry.SharedTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Molds, tools and packaging of the finished-food line (I40, machines/05 §3, §4). Molds are catalysts: kept in the
 * crafting grid ({@code tools} of {@code food_crafting}), counted runs in machines (I38). Containers are inputs of
 * R29 filling and sealing; the glass jar and the glass bottle come back when a product in them is poured out or
 * eaten ({@link #returnable}).
 */
public final class FoodTools {
    private static final String NS = FoodIndustrial.MOD_ID;
    /** Edible casings (foods/17 §1.4): natural casing (batch 5) and collagen casing. */
    public static final String EDIBLE_CASINGS = NS + ":casings/edible";
    /** Molds: stack of 16, like buckets (I40, to review). */
    public static final int MOLD_STACK = 16;

    /** One mold, tool or container: ID, names and the Shift description (molds only, text-standard §3.2). */
    public record Entry(String id, String zh, String en, String descriptionZh, String descriptionEn) {
    }

    public static final List<Entry> MOLDS = List.of(
        new Entry("granulating_screen", "筛网", "Granulating Screen", "湿料过筛，制成颗粒", "Rubs wet mixes into granules"),
        new Entry("tablet_die", "压片冲模", "Tablet Die", "把粉料压成片或块", "Presses powders into tablets or cubes"),
        new Entry("filling_mold", "包馅模", "Filling Mold", "包饺子、包子等带馅面点", "Wraps dumplings, buns and other filled dough"),
        new Entry("stretching_mold", "拉伸模", "Stretching Mold", "把面团拉成面条或麻花", "Pulls dough into noodles or twists"),
        new Entry("extrusion_die", "挤丝模", "Extrusion Die", "把米浆或淀粉挤成丝", "Extrudes rice or starch into strands"),
        new Entry("cake_mold", "蛋糕模", "Cake Mold", "烘烤或冷藏蛋糕", "Bakes or chills cakes"),
        new Entry("loaf_pan", "吐司模", "Loaf Pan", "烤吐司", "Bakes sandwich loaves"),
        new Entry("tart_pan", "挞派模", "Tart Pan", "烤蛋挞和派", "Bakes tarts and pies"),
        new Entry("baking_iron", "夹板", "Baking Iron", "夹烤华夫饼、威化和蛋卷", "Bakes waffles, wafers and egg rolls"),
        new Entry("mooncake_mold", "月饼模", "Mooncake Mold", "压出月饼的花纹", "Presses the pattern of mooncakes"),
        new Entry("cheese_mold", "奶酪模", "Cheese Mold", "把凝乳压成奶酪块", "Presses curd into cheese"),
        new Entry("popsicle_mold", "冰棍模", "Popsicle Mold", "冻冰棍和雪糕", "Freezes ice pops and ice cream bars"),
        new Entry("stuffing_nozzle", "灌肠嘴", "Stuffing Nozzle", "把肉馅灌进肠衣", "Stuffs meat into casings"),
        new Entry("ham_mold", "火腿模", "Ham Mold", "煮西式火腿时定形", "Shapes ham while it cooks"),
        new Entry("tofu_mold", "豆腐模", "Tofu Mold", "压制豆腐和豆干", "Presses tofu and dried tofu"),
        new Entry("chocolate_mold", "巧克力模", "Chocolate Mold", "浇注巧克力", "Casts chocolate"),
        new Entry("hard_candy_mold", "硬糖模", "Hard Candy Mold", "浇注硬糖", "Casts hard candy"),
        new Entry("lollipop_mold", "棒棒糖模", "Lollipop Mold", "浇注棒棒糖", "Casts lollipops"),
        new Entry("starch_mogul", "淀粉模", "Starch Mogul", "在淀粉印模里浇注软糖", "Casts gummies in starch impressions"),
        new Entry("panning_drum", "糖衣锅", "Panning Drum", "滚动上糖衣", "Tumbles centres to coat them"),
        new Entry("blow_mold", "吹瓶模", "Blow Mold", "吹制玻璃瓶和塑料瓶", "Blows glass and plastic bottles"),
        new Entry("oak_barrel", "橡木桶", "Oak Barrel", "陈酿威士忌等烈酒", "Ages whisky and other spirits"));

    public static final List<Entry> CONTAINERS = List.of(
        new Entry("paper_bag", "纸袋", "Paper Bag", "", ""),
        new Entry("glass_jar", "玻璃罐", "Glass Jar", "", ""),
        new Entry("plastic_bag", "塑料袋", "Plastic Bag", "", ""),
        new Entry("paper_cup", "纸杯", "Paper Cup", "", ""),
        new Entry("tin_can", "马口铁罐", "Tin Can", "", ""),
        new Entry("plastic_casing", "塑料肠衣", "Plastic Casing", "", ""),
        new Entry("glass_bottle", "灌装玻璃瓶", "Filling Glass Bottle", "", ""),
        new Entry("aluminum_can", "铝罐", "Aluminum Can", "", ""),
        new Entry("pet_bottle", "PET 瓶", "PET Bottle", "", ""),
        new Entry("tea_bag", "滤纸袋", "Tea Bag", "", ""));

    /** Containers given back when the product in them is poured out or eaten (machines/05 §4). */
    public static final Set<String> RETURNABLE = Set.of("glass_jar", "glass_bottle");

    private static final Map<String, Supplier<? extends Item>> ITEMS = new LinkedHashMap<>();

    static {
        for (Entry mold : MOLDS) {
            ITEMS.put(mold.id(), PlatformRegistry.item(mold.id(), properties -> new MoldItem(properties, mold.id()),
                new Item.Properties().stacksTo(MOLD_STACK)));
            Lang.add(Lang.item(mold.id()), mold.zh(), mold.en());
            Lang.add(Lang.description(mold.id()), mold.descriptionZh(), mold.descriptionEn());
            SharedTags.item(NS + ":molds", NS + ":" + mold.id());
            // one tag per mold and container, for recipes (which name tags of this mod only, I47)
            SharedTags.item(NS + ":molds/" + mold.id(), NS + ":" + mold.id());
        }
        for (Entry container : CONTAINERS) {
            ITEMS.put(container.id(), PlatformRegistry.item(container.id(), PlatformItem::new, new Item.Properties()));
            Lang.add(Lang.item(container.id()), container.zh(), container.en());
            SharedTags.item(NS + ":containers", NS + ":" + container.id());
            SharedTags.item(NS + ":containers/" + container.id(), NS + ":" + container.id());
        }
        SharedTags.item(EDIBLE_CASINGS, NS + ":collagen_casing");
        ModCreativeTabs.add(ModCreativeTabs.Tab.MOLDS, output -> MOLDS.forEach(mold -> output.accept(stack(mold.id()))));
        ModCreativeTabs.add(ModCreativeTabs.Tab.CONTAINERS, output -> CONTAINERS.forEach(container -> output.accept(stack(container.id()))));
        ModCreativeTabs.icon(ModCreativeTabs.Tab.MOLDS, () -> stack("cake_mold"));
        ModCreativeTabs.icon(ModCreativeTabs.Tab.CONTAINERS, () -> stack("glass_jar"));
    }

    private FoodTools() {
    }

    /** Registers the items. Called during mod construction. */
    public static void bootstrap() {
        FoodToolData.bootstrap();
    }

    public static Item item(String id) {
        Supplier<? extends Item> item = ITEMS.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Unknown mold or container " + id);
        }
        return item.get();
    }

    public static ItemStack stack(String id) {
        return new ItemStack(item(id));
    }

    /** Whether {@code id} (path or full ID) is a container given back when emptied. */
    public static boolean returnable(String id) {
        return RETURNABLE.contains(id.startsWith(NS + ":") ? id.substring(NS.length() + 1) : id);
    }

    /** A mold or tool: the one-line description under Shift. */
    static final class MoldItem extends PlatformItem {
        private final String id;

        MoldItem(Properties properties, String id) {
            super(properties);
            this.id = id;
        }

        @Override
        protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
            Tooltips.machine(Lang.description(id), lines, shiftDown);
        }
    }
}
