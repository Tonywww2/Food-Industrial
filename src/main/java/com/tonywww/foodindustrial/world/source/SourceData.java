package com.tonywww.foodindustrial.world.source;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.PlatformTrades;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;

import java.util.List;

/**
 * Data of the I44 plants: loot tables, 26.1 item definitions, tags (through {@link SharedTags}, written by
 * {@code ModWorldData}), the W02 tapping recipes of the own logs, the zongzi leaf recipe and the 26.1 villager trades.
 */
final class SourceData {
    private static final String NS = FoodIndustrial.MOD_ID;

    private SourceData() {
    }

    static void bootstrap() {
        for (SourceBlocks.CropBlocks crop : SourceBlocks.crops()) {
            String id = NS + ":" + crop.crop().id() + "_crop";
            SharedTags.block("minecraft:crops", id);
            SharedTags.block("minecraft:maintains_farmland", id);
            SharedTags.block("minecraft:bee_growables", id);
        }
        for (SourceBlocks.WildBlocks wild : SourceBlocks.wild()) {
            SharedTags.block("minecraft:bee_growables", NS + ":wild_" + wild.wild().id());
            SharedTags.block("minecraft:sword_efficient", NS + ":wild_" + wild.wild().id());
        }
        for (SourceBlocks.TreeBlocks tree : SourceBlocks.trees()) {
            String id = NS + ":" + tree.tree().id();
            if (tree.bush() != null) {
                SharedTags.block("minecraft:bee_growables", id + "_bush");
                SharedTags.block("minecraft:sword_efficient", id + "_bush");
                continue;
            }
            SharedTags.block("minecraft:saplings", id + "_sapling");
            SharedTags.item("minecraft:saplings", id + "_sapling");
            SharedTags.block("minecraft:leaves", id + "_leaves");
            SharedTags.item("minecraft:leaves", id + "_leaves");
            SharedTags.block("minecraft:mineable/hoe", id + "_leaves");
            SharedTags.block("minecraft:replaceable_by_trees", id + "_leaves");
            if (tree.log() != null) {
                SharedTags.block("minecraft:logs_that_burn", id + "_log");
                SharedTags.item("minecraft:logs_that_burn", id + "_log");
                SharedTags.block("minecraft:mineable/axe", id + "_log");
            }
        }
        for (SourceTable.Scrape scrape : SourceTable.get().scrape()) {
            scrape.blocks().forEach(block -> SharedTags.block(NS + ":harvest/" + scrape.item(), block));
        }
        SharedTags.block("minecraft:mineable/axe", NS + ":" + SourceBlocks.NEST_ID);
        PlatformDatagen.register("world sources", SourceData::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        for (SourceBlocks.CropBlocks crop : SourceBlocks.crops()) {
            SourceTable.Crop c = crop.crop();
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, c.id() + "_crop", cropLoot(c));
            if (crop.wild() != null) {
                sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, "wild_" + c.id(), table("wild_" + c.id(),
                    pool(array(counted(c.plant(), 1, 2)), null)));
            }
        }
        for (SourceTable.NewItem item : SourceTable.get().items().values()) {
            itemDefinition(sink, item.id());
        }
        for (SourceBlocks.WildBlocks wild : SourceBlocks.wild()) {
            String id = "wild_" + wild.wild().id();
            itemDefinition(sink, id);
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, id, plantLoot(id, wild.wild().drops(), List.of()));
        }
        for (SourceBlocks.TreeBlocks tree : SourceBlocks.trees()) {
            SourceTable.Tree t = tree.tree();
            if (tree.bush() != null) {
                itemDefinition(sink, t.id() + "_bush");
                List<SourceTable.Drop> flowers = t.fruit().isEmpty() ? List.of() : t.flower();
                List<SourceTable.Drop> ripe = t.fruit().isEmpty() ? t.flower() : t.fruit();
                sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, t.id() + "_bush", bushLoot(t.id() + "_bush", ripe, flowers));
                continue;
            }
            itemDefinition(sink, t.id() + "_sapling");
            itemDefinition(sink, t.id() + "_leaves");
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, t.id() + "_sapling", table(t.id() + "_sapling", selfPool(t.id() + "_sapling")));
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, t.id() + "_leaves", leavesLoot(t.id()));
            if (tree.log() != null) {
                itemDefinition(sink, t.id() + "_log");
                sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, t.id() + "_log", logLoot(t));
                if (t.tap() != null) {
                    sink.json(PlatformDatagen.Kind.RECIPE, NS, "world/tap_" + t.id(), tapRecipe(t));
                }
            }
        }
        for (SourceTable.Scrape scrape : SourceTable.get().scrape()) {
            JsonObject recipe = new JsonObject();
            recipe.addProperty("type", NS + ":harvesting");
            recipe.addProperty("block", "#" + NS + ":harvest/" + scrape.item());
            recipe.add("tool", PlatformDatagen.ingredient("#" + NS + ":knives"));
            JsonObject result = new JsonObject();
            result.addProperty("item", NS + ":" + scrape.item());
            recipe.add("result", result);
            recipe.addProperty("cooldown", 6000);
            sink.json(PlatformDatagen.Kind.RECIPE, NS, "world/scrape_" + scrape.item(), recipe);
        }
        // the swiftlet nest: swallowroot when broken (Silk Touch keeps it), or scraped with a knife, resting 12,000 ticks
        itemDefinition(sink, SourceBlocks.NEST_ID);
        JsonObject nestBlock = item(NS + ":" + SourceBlocks.NEST_ID);
        nestBlock.add("conditions", array(matchTool(PlatformDatagen.toolHasSilkTouch())));
        JsonObject alternatives = new JsonObject();
        alternatives.addProperty("type", "minecraft:alternatives");
        alternatives.add("children", array(nestBlock, counted("swallowroot", 1, 2)));
        sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, SourceBlocks.NEST_ID, table(SourceBlocks.NEST_ID, pool(array(alternatives), null)));
        JsonObject scrapeNest = new JsonObject();
        scrapeNest.addProperty("type", NS + ":harvesting");
        scrapeNest.addProperty("block", NS + ":" + SourceBlocks.NEST_ID);
        scrapeNest.add("tool", PlatformDatagen.ingredient("#" + NS + ":knives"));
        JsonObject root = new JsonObject();
        root.addProperty("item", NS + ":swallowroot");
        scrapeNest.add("result", root);
        scrapeNest.addProperty("cooldown", 12000);
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "world/scrape_swallowroot", scrapeNest);
        JsonObject leaf = new JsonObject();
        leaf.addProperty("type", "minecraft:crafting_shapeless");
        leaf.addProperty("category", "misc");
        JsonArray ingredients = new JsonArray();
        ingredients.add(PlatformDatagen.ingredient("minecraft:bamboo"));
        leaf.add("ingredients", ingredients);
        leaf.add("result", PlatformDatagen.recipeResult(NS + ":zongzi_leaf", 2));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, "zongzi_leaf", leaf);
        PlatformTrades.generate(sink);
        com.tonywww.foodindustrial.platform.PlatformEnchantments.generate(sink);
    }

    // ------------------------------------------------------------------ loot

    /** Ripe: every drop, fortune adding to the first; unripe: the planted item back. */
    private static JsonObject cropLoot(SourceTable.Crop crop) {
        String self = NS + ":" + crop.id() + "_crop";
        JsonArray pools = new JsonArray();
        JsonObject seed = item(NS + ":" + crop.plant());
        JsonObject notRipe = new JsonObject();
        notRipe.addProperty("condition", "minecraft:inverted");
        notRipe.add("term", stateIs(self, "age", "7"));
        pools.add(pool(array(seed), array(notRipe)));
        boolean first = true;
        for (SourceTable.Drop drop : crop.drops()) {
            JsonObject entry = counted(drop.item(), Math.max(first ? 1 : 0, drop.min()), drop.max());
            if (first) {
                entry.getAsJsonArray("functions").add(fortuneBonus());
            }
            pools.add(pool(array(entry), array(stateIs(self, "age", "7"))));
            first = false;
        }
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:block");
        table.add("pools", pools);
        table.add("functions", array(function("minecraft:explosion_decay")));
        table.addProperty("random_sequence", NS + ":blocks/" + crop.id() + "_crop");
        return table;
    }

    /** Wild herb: shears (or Silk Touch) take the plant; otherwise the products when grown (age 3). */
    private static JsonObject plantLoot(String id, List<SourceTable.Drop> ripe, List<SourceTable.Drop> flowers) {
        String self = NS + ":" + id;
        JsonObject anyOf = shearsOrSilk();
        JsonObject plant = item(self);
        plant.add("conditions", array(anyOf));
        JsonArray pools = new JsonArray();
        pools.add(pool(array(plant), null));
        JsonObject notShears = new JsonObject();
        notShears.addProperty("condition", "minecraft:inverted");
        notShears.add("term", anyOf.deepCopy());
        for (SourceTable.Drop drop : ripe) {
            pools.add(pool(array(counted(drop.item(), Math.max(1, drop.min()), drop.max())), array(notShears, stateIs(self, "age", "3"))));
        }
        for (SourceTable.Drop drop : flowers) {
            pools.add(pool(array(counted(drop.item(), Math.max(1, drop.min()), drop.max())), array(notShears, stateIs(self, "age", "2"))));
        }
        return table(id, pools);
    }

    /** Bush: always itself (to replant), plus what is ripe on it. */
    private static JsonObject bushLoot(String id, List<SourceTable.Drop> ripe, List<SourceTable.Drop> flowers) {
        String self = NS + ":" + id;
        JsonArray pools = new JsonArray();
        pools.add(selfPool(id));
        for (SourceTable.Drop drop : ripe) {
            pools.add(pool(array(counted(drop.item(), Math.max(1, drop.min()), drop.max())), array(stateIs(self, "age", "3"))));
        }
        for (SourceTable.Drop drop : flowers) {
            pools.add(pool(array(counted(drop.item(), Math.max(1, drop.min()), drop.max())), array(stateIs(self, "age", "2"))));
        }
        return table(id, pools);
    }

    /** Vanilla oak leaves: the leaves with shears or Silk Touch, else a sapling now and then and sticks. */
    private static JsonObject leavesLoot(String tree) {
        String self = NS + ":" + tree + "_leaves";
        JsonObject anyOf = shearsOrSilk();
        JsonObject leaves = item(self);
        leaves.add("conditions", array(anyOf));
        JsonObject sapling = item(NS + ":" + tree + "_sapling");
        sapling.add("conditions", array(condition("minecraft:survives_explosion"), tableBonus(0.05, 0.0625, 0.083333336, 0.1)));
        JsonObject alternatives = new JsonObject();
        alternatives.addProperty("type", "minecraft:alternatives");
        alternatives.add("children", array(leaves, sapling));
        JsonArray pools = new JsonArray();
        pools.add(pool(array(alternatives), null));
        JsonObject notShears = new JsonObject();
        notShears.addProperty("condition", "minecraft:inverted");
        notShears.add("term", anyOf.deepCopy());
        JsonObject stick = counted("minecraft:stick", 1, 2);
        stick.add("conditions", array(tableBonus(0.02, 0.022222223, 0.025, 0.033333335, 0.1)));
        pools.add(pool(array(stick), array(notShears)));
        return table(tree + "_leaves", pools);
    }

    /** Own log: the wood product (Silk Touch keeps the log), or the log itself. */
    private static JsonObject logLoot(SourceTable.Tree tree) {
        String id = tree.id() + "_log";
        if (tree.wood().isEmpty()) {
            return table(id, selfPool(id));
        }
        JsonObject log = item(NS + ":" + id);
        log.add("conditions", array(matchTool(PlatformDatagen.toolHasSilkTouch())));
        SourceTable.Drop drop = tree.wood().get(0);
        JsonObject wood = counted(drop.item(), Math.max(1, drop.min()), drop.max());
        JsonObject alternatives = new JsonObject();
        alternatives.addProperty("type", "minecraft:alternatives");
        alternatives.add("children", array(log, wood));
        return table(id, pool(array(alternatives), array(condition("minecraft:survives_explosion"))));
    }

    // ------------------------------------------------------------------ recipes

    /** W02 tapping of an own log with a knife (H15), a glass bottle in the off hand for fluids and bottled syrups. */
    private static JsonObject tapRecipe(SourceTable.Tree tree) {
        SourceTable.Tap tap = tree.tap();
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", NS + ":harvesting");
        recipe.addProperty("block", NS + ":" + tree.id() + "_log");
        recipe.add("tool", PlatformDatagen.ingredient("#" + NS + ":knives"));
        if (!tap.kind().equals("item")) {
            recipe.add("container", PlatformDatagen.ingredient("minecraft:glass_bottle"));
        }
        JsonObject result = new JsonObject();
        if (tap.kind().equals("fluid")) {
            result.addProperty("fluid", NS + ":" + tap.result());
            result.addProperty("amount", tap.amount());
        } else {
            result.addProperty("item", NS + ":" + tap.result());
            result.addProperty("count", tap.amount());
        }
        recipe.add("result", result);
        recipe.addProperty("cooldown", 6000);
        return recipe;
    }

    // ------------------------------------------------------------------ helpers

    private static void itemDefinition(PlatformDatagen.Sink sink, String id) {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", NS + ":item/" + id);
        JsonObject definition = new JsonObject();
        definition.add("model", model);
        sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, id, definition);
    }

    private static JsonObject table(String id, JsonObject pool) {
        return table(id, array(pool));
    }

    private static JsonObject table(String id, JsonArray pools) {
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:block");
        table.add("pools", pools);
        table.addProperty("random_sequence", NS + ":blocks/" + id);
        return table;
    }

    private static JsonObject selfPool(String id) {
        return pool(array(item(NS + ":" + id)), array(condition("minecraft:survives_explosion")));
    }

    private static JsonObject pool(JsonArray entries, JsonArray conditions) {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);
        if (conditions != null) {
            pool.add("conditions", conditions);
        }
        pool.add("entries", entries);
        return pool;
    }

    private static JsonObject item(String id) {
        JsonObject item = new JsonObject();
        item.addProperty("type", "minecraft:item");
        item.addProperty("name", id);
        return item;
    }

    /** An item of this mod (or {@code minecraft:} when given) with a uniform count and explosion decay. */
    private static JsonObject counted(String item, int min, int max) {
        JsonObject entry = item(item.contains(":") ? item : NS + ":" + item);
        JsonObject count = new JsonObject();
        if (min == max) {
            count.addProperty("count", (double) min);
        } else {
            JsonObject uniform = new JsonObject();
            uniform.addProperty("type", "minecraft:uniform");
            uniform.addProperty("min", (double) min);
            uniform.addProperty("max", (double) max);
            count.add("count", uniform);
        }
        count.addProperty("function", "minecraft:set_count");
        count.addProperty("add", false);
        entry.add("functions", array(count, function("minecraft:explosion_decay")));
        return entry;
    }

    private static JsonObject fortuneBonus() {
        JsonObject bonus = function("minecraft:apply_bonus");
        bonus.addProperty("enchantment", "minecraft:fortune");
        bonus.addProperty("formula", "minecraft:uniform_bonus_count");
        JsonObject parameters = new JsonObject();
        parameters.addProperty("bonusMultiplier", 1);
        bonus.add("parameters", parameters);
        return bonus;
    }

    private static JsonObject shearsOrSilk() {
        JsonObject anyOf = new JsonObject();
        anyOf.addProperty("condition", "minecraft:any_of");
        anyOf.add("terms", array(matchTool(PlatformDatagen.toolIsShears()), matchTool(PlatformDatagen.toolHasSilkTouch())));
        return anyOf;
    }

    private static JsonObject condition(String type) {
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", type);
        return condition;
    }

    private static JsonObject function(String type) {
        JsonObject function = new JsonObject();
        function.addProperty("function", type);
        return function;
    }

    private static JsonObject matchTool(JsonObject predicate) {
        JsonObject condition = condition("minecraft:match_tool");
        condition.add("predicate", predicate);
        return condition;
    }

    private static JsonObject stateIs(String block, String property, String value) {
        JsonObject condition = condition("minecraft:block_state_property");
        condition.addProperty("block", block);
        JsonObject properties = new JsonObject();
        properties.addProperty(property, value);
        condition.add("properties", properties);
        return condition;
    }

    private static JsonObject tableBonus(double... chances) {
        JsonObject condition = condition("minecraft:table_bonus");
        condition.addProperty("enchantment", "minecraft:fortune");
        JsonArray list = new JsonArray();
        for (double chance : chances) {
            list.add(chance);
        }
        condition.add("chances", list);
        return condition;
    }

    private static JsonArray array(JsonElement... elements) {
        JsonArray array = new JsonArray();
        for (JsonElement element : elements) {
            array.add(element);
        }
        return array;
    }
}
