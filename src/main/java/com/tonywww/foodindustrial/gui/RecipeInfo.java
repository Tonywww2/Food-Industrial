package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.world.ModWorldBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Where raw materials come from, as JEI information pages (additives/10 §5 原料来源, I22): ores, crust
 * blocks, wild crops, sea plants, the rubber tree and seawater. Places follow the world generation of
 * I07 ({@code world.ModWorldgen}); texts are drafts awaiting review (2026-11-11).
 */
public final class RecipeInfo {
    /** Item (resolved when JEI asks) → language key of its information page. */
    private static final Map<Supplier<? extends Item>, String> PAGES = new LinkedHashMap<>();

    private RecipeInfo() {
    }

    /** Adds a page for item {@code name} (path in the mod namespace) or the item {@code item} supplies. */
    private static void add(String name, Supplier<? extends Item> item, String zh, String en) {
        String key = "jei." + FoodIndustrial.MOD_ID + ".info." + name;
        Lang.add(key, zh, en);
        PAGES.put(item, key);
    }

    private static void add(String name, String zh, String en) {
        add(name, () -> PlatformTags.item(FoodIndustrial.MOD_ID + ":" + name).orElse(null), zh, en);
    }

    static void bootstrap() {
        add(ModWorldBlocks.GYPSUM_ORE.id(), "生成在沙漠地下 0–80 层，用镐采集", "Found under deserts at Y 0–80; mine it with a pickaxe");
        add(ModWorldBlocks.ALUNITE_ORE.id(), "生成在山地 32–160 层", "Found in mountains at Y 32–160");
        add(ModWorldBlocks.ROCK_SALT.id(), "在主世界 0–48 层成大块生成", "Large veins all over the Overworld at Y 0–48");
        add(ModWorldBlocks.MAGNESITE_ORE.id(), "生成在主世界 0–64 层", "Found all over the Overworld at Y 0–64");
        add(ModWorldBlocks.SELENIUM_ORE.id(), "生成在主世界 0–48 层", "Found all over the Overworld at Y 0–48");
        add(ModWorldBlocks.CHROMITE_ORE.id(), "生成在山地与风袭丘陵 0–64 层", "Found in mountains and windswept hills at Y 0–64");
        add(ModWorldBlocks.APATITE_ORE.id(), "未装热力时生成在主世界 0–64 层", "Found at Y 0–64 when Thermal is not installed");
        add(ModWorldBlocks.SULFUR_ORE.id(), "未装热力时生成在主世界 0–32 层，挖掘掉落硫", "Found at Y 0–32 when Thermal is not installed; drops sulfur");
        add(ModWorldBlocks.TRONA_ORE.id(), "生成在沙漠地表的沙层里", "Found in the sand of desert surfaces");
        add(ModWorldBlocks.NITRATINE.id(), "生成在沙漠地表的沙层里", "Found in the sand of desert surfaces");
        add(ModWorldBlocks.NITRE_SOIL.id(), "不露天的泥土在牲畜附近或随时间变成硝土", "Sheltered dirt turns into it near livestock or over time");
        crop(ModWorldBlocks.STEVIA, "野生于平原与热带草原，种下可栽培", "Grows wild in plains and savannas; can be planted");
        crop(ModWorldBlocks.MINT, "野生于平原与草甸，种下可栽培", "Grows wild in plains and meadows; can be planted");
        crop(ModWorldBlocks.TURMERIC, "野生于丛林与热带草原，种下可栽培", "Grows wild in jungles and savannas; can be planted");
        crop(ModWorldBlocks.CHILI, "野生于平原与热带草原，种下可栽培", "Grows wild in plains and savannas; can be planted");
        crop(ModWorldBlocks.ROSEMARY, "野生于草甸与风袭丘陵，种下可栽培", "Grows wild in meadows and windswept hills; can be planted");
        crop(ModWorldBlocks.INDIGO_PLANT, "野生于河流与沼泽，种下可栽培", "Grows wild by rivers and in swamps; can be planted");
        crop(ModWorldBlocks.PADDY, "野生于沼泽与河流，种下可栽培；碾米得大米与米糠", "Grows wild in swamps and by rivers; can be planted; milled into rice and rice bran");
        produce(ModWorldBlocks.GARDENIA_BUSH, "丛林中的栀子丛结果，右键采摘", "Gardenia bushes in jungles bear fruit; right-click to pick");
        produce(ModWorldBlocks.VANILLA, "长在丛林树干上，成熟后采摘", "Grows on jungle tree trunks; pick when ripe");
        block(ModWorldBlocks.GELIDIUM, "生成在暖水与温水海洋的海底", "Grows on the floor of warm and lukewarm oceans");
        block(ModWorldBlocks.EUCHEUMA, "生成在暖水海洋的海底", "Grows on the floor of warm oceans");
        block(ModWorldBlocks.RUBBER_LOG, "丛林中的橡胶树；用刀割取可得乳胶", "Rubber trees grow in jungles; cut with a knife for latex");
        block(ModWorldBlocks.RUBBER_SAPLING, "丛林中的橡胶树树叶掉落", "Dropped by rubber tree leaves in jungles");
        add("potassium_bitartrate", "木桶罐装葡萄酒放在寒冷处或冰旁，罐壁结出酒石，用刀刮下", "Wine in a wooden tank kept cold or by ice deposits it; scrape it off with a knife");
        add("seawater_bucket", "在海洋或沙滩群系用水桶装水得到海水", "A water bucket filled in an ocean or beach biome holds seawater");
        // I31: materials that are also GB 2760 flavorings (18 ST26, 一物一号): the flavoring code is only an alias
        for (com.google.gson.JsonElement element : com.tonywww.foodindustrial.content.flavors.FlavorRecipes.read().getAsJsonArray("aliases")) {
            com.google.gson.JsonObject alias = element.getAsJsonObject();
            String id = alias.get("id").getAsString();
            String code = alias.get("code").getAsString();
            add(alias.get("fluid").getAsBoolean() ? id + "_bucket" : id, "也是食品用香料，GB 2760 编码 " + code,
                "Also a food flavoring, GB 2760 code " + code);
        }
    }

    private static void crop(ModWorldBlocks.Crop crop, String zh, String en) {
        add(crop.id(), crop.produce(), zh, en);
    }

    private static void produce(ModWorldBlocks.Entry entry, String zh, String en) {
        add(entry.id(), entry.produce() != null ? entry.produce() : entry.item(), zh, en);
    }

    private static void block(ModWorldBlocks.Entry entry, String zh, String en) {
        add(entry.id(), zh, en);
    }

    /** Pages to register: item stacks with the key of their text (items that do not exist are left out). */
    public static List<Map.Entry<ItemStack, String>> pages() {
        List<Map.Entry<ItemStack, String>> result = new ArrayList<>();
        PAGES.forEach((item, key) -> {
            Item found = item == null ? null : item.get();
            if (found != null) {
                result.add(Map.entry(new ItemStack(found), key));
            }
        });
        return result;
    }
}
