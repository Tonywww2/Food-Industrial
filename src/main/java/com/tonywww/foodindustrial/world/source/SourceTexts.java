package com.tonywww.foodindustrial.world.source;

import com.tonywww.foodindustrial.gui.RecipeInfo;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JEI information pages saying where each I44 product comes from (additives/10 §5 原料来源), built from the
 * generation list. Texts are drafts (I44 待审).
 */
final class SourceTexts {
    private SourceTexts() {
    }

    static void bootstrap() {
        SourceTable.Data data = SourceTable.get();
        Set<String> done = new LinkedHashSet<>();
        data.info().forEach((item, info) -> page(done, item, info.zh(), info.en()));
        for (SourceTable.Crop crop : data.crops()) {
            String plant = crop.plant();
            page(done, plant, "种在耕地上长成" + crop.zh() + "；农民出售，村庄的箱子里也有",
                "Plant it on farmland to grow " + crop.en() + "; farmers sell it and village chests hold it");
            for (SourceTable.Drop drop : crop.drops()) {
                if (!drop.item().equals(plant)) {
                    page(done, drop.item(), "收获成熟的" + crop.zh() + "时一并得到", "Comes with a ripe " + crop.en() + " harvest");
                }
            }
        }
        for (SourceTable.Wild wild : data.wild()) {
            SourceTable.Biomes biomes = data.biomes().get(wild.biome());
            for (SourceTable.Drop drop : wild.drops()) {
                page(done, drop.item(), wild.zh() + "野生于" + biomes.zh() + "，长成后右键采集；用剪刀可连株挖走移栽",
                    wild.en() + " grows wild in " + biomes.en() + "; pick it when grown, or take the plant with shears to replant it");
            }
        }
        for (SourceTable.Tree tree : data.trees()) {
            SourceTable.Biomes biomes = data.biomes().get(tree.biome());
            String whereZh = (tree.bush() ? tree.zh() + "丛" : tree.zh().endsWith("树") ? tree.zh() : tree.zh() + "树") + "生长在" + biomes.zh() + "，";
            String whereEn = tree.en() + (tree.bush() ? " bushes grow" : " trees grow") + " in " + biomes.en() + "; ";
            String partZh = tree.bush() ? "" : "树叶";
            String partEn = tree.bush() ? "the bush" : "the leaves";
            for (SourceTable.Drop drop : tree.flower()) {
                page(done, drop.item(), whereZh + "开花时空手右键" + partZh + "采摘", whereEn + "pick from " + partEn + " in bloom");
            }
            for (SourceTable.Drop drop : tree.fruit()) {
                page(done, drop.item(), whereZh + "结果时空手右键" + partZh + "采摘", whereEn + "pick from " + partEn + " when it bears fruit");
            }
            for (SourceTable.Drop drop : tree.leaf()) {
                page(done, drop.item(), whereZh + "长成后用剪刀右键" + partZh + "采剪", whereEn + "shear " + partEn + " when grown");
            }
            for (SourceTable.Drop drop : tree.strip()) {
                page(done, drop.item(), whereZh + "用斧给它的原木去皮得到", whereEn + "strip its log with an axe");
            }
            for (SourceTable.Drop drop : tree.wood()) {
                page(done, drop.item(), whereZh + "砍下它的原木得到", whereEn + "chop its log");
            }
            if (tree.tap() != null) {
                boolean bottle = !tree.tap().kind().equals("item");
                String result = tree.tap().kind().equals("fluid") ? tree.tap().result() + "_bucket" : tree.tap().result();
                page(done, result, whereZh + (bottle ? "主手持刀、副手拿玻璃瓶右键它的原木割取" : "手持刀右键它的原木割取"),
                    whereEn + (bottle ? "tap its log with a knife, a glass bottle in the other hand" : "tap its log with a knife"));
            }
        }
        for (SourceTable.Drop drop : data.gardeniaFlower()) {
            page(done, drop.item(), "丛林中的栀子丛开花时右键采摘", "Pick it from gardenia bushes in jungles when they bloom");
        }
        for (SourceTable.VanillaDrop drop : data.vanillaDrops()) {
            page(done, drop.item(), drop.zh() + "有时掉落", "Sometimes dropped by " + drop.en());
        }
        data.smoked().forEach((input, result) -> page(done, result, "梅子放进烟熏炉熏制", "Smoke ume plums in a smoker"));
        for (SourceTable.Scrape scrape : data.scrape()) {
            page(done, scrape.item(), "手持刀右键" + scrape.zh() + "刮取", "Scrape it off " + scrape.en() + " with a knife");
        }
        for (SourceTable.Loot loot : data.loot()) {
            if (loot.zh() == null) {
                page(done, loot.item(), "钓鱼与沉船的箱子里偶尔找到", "Now and then fished up or found in shipwreck chests");
            } else if (!loot.zh().isEmpty()) {
                page(done, loot.item(), loot.zh(), loot.en());
            }
        }
    }

    /** One page per item: the first source written (an item with several sources keeps the first text). */
    private static void page(Set<String> done, String item, String zh, String en) {
        if (done.add(item)) {
            RecipeInfo.page(item, zh, en);
        }
    }
}
