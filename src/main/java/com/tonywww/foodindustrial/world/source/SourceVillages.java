package com.tonywww.foodindustrial.world.source;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.PlatformLoot;
import com.tonywww.foodindustrial.platform.PlatformTrades;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Where crop produce comes from (additives/08 §C: 种子由村民交易或战利品给出), and the other loot sources of I44:
 * farmers sell the produce, village house chests hold the produce of their climate, oak leaves drop acorn shells,
 * ripe carrots carrot seeds, fishing and shipwrecks ambergris. Numbers are drafts (I44 待审).
 */
final class SourceVillages {
    private static final String NS = FoodIndustrial.MOD_ID;
    /** Crop "level" 1–5 of the list → items per offer, emeralds, villager level (farmers keep their level 1 offers). */
    private static final int[] COUNT = {0, 6, 4, 3, 2, 1};
    private static final int[] EMERALDS = {0, 1, 1, 2, 3, 4};
    private static final int[] XP = {0, 2, 5, 10, 15, 30};

    private SourceVillages() {
    }

    static void bootstrap() {
        SourceTable.Data data = SourceTable.get();
        Map<String, List<String>> chests = new LinkedHashMap<>();
        for (SourceTable.Crop crop : data.crops()) {
            int level = Math.max(1, Math.min(5, crop.level()));
            int villagerLevel = villagerLevel(crop);
            String plant = crop.plant();
            PlatformTrades.sells("emerald_" + plant, "farmer", villagerLevel, () -> SourceBlocks.item(plant), COUNT[level], EMERALDS[level],
                12, XP[villagerLevel]);
            for (String table : data.villages().getOrDefault(crop.climate(), List.of())) {
                chests.computeIfAbsent("minecraft:" + table, key -> new ArrayList<>()).add(plant);
            }
        }
        chests.forEach((table, items) -> PlatformLoot.addPool(table, () -> villagePool(items)));
        for (SourceTable.VanillaDrop drop : data.vanillaDrops()) {
            PlatformLoot.addPool("minecraft:" + drop.table(), () -> chancePool(drop.item(), drop.chance(), drop.block(), drop.age()));
        }
        for (SourceTable.Loot loot : data.loot()) {
            PlatformLoot.addPool("minecraft:" + loot.table(), () -> chancePool(loot.item(), loot.chance(), null, null));
        }
    }

    /** Farmers sell the commoner produce from level 2 (their level 1 offers stay vanilla's), the rarest at level 5. */
    static int villagerLevel(SourceTable.Crop crop) {
        return Math.max(2, Math.min(5, crop.level() + 1));
    }

    /** Up to two stacks of 1–3 produce of the climate's crops. */
    private static JsonObject villagePool(List<String> items) {
        JsonObject pool = new JsonObject();
        JsonObject rolls = new JsonObject();
        rolls.addProperty("type", "minecraft:uniform");
        rolls.addProperty("min", 0.0);
        rolls.addProperty("max", 2.0);
        pool.add("rolls", rolls);
        pool.addProperty("bonus_rolls", 0.0);
        JsonArray entries = new JsonArray();
        for (String item : items) {
            JsonObject entry = itemEntry(item);
            JsonObject count = new JsonObject();
            count.addProperty("type", "minecraft:uniform");
            count.addProperty("min", 1.0);
            count.addProperty("max", 3.0);
            JsonObject function = new JsonObject();
            function.addProperty("function", "minecraft:set_count");
            function.add("count", count);
            JsonArray functions = new JsonArray();
            functions.add(function);
            entry.add("functions", functions);
            entries.add(entry);
        }
        pool.add("entries", entries);
        return pool;
    }

    /** One item with {@code chance}; for a block table, only when the block is {@code age} (ripe crops). */
    static JsonObject chancePool(String item, double chance, String block, Integer age) {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);
        JsonArray conditions = new JsonArray();
        JsonObject random = new JsonObject();
        random.addProperty("condition", "minecraft:random_chance");
        random.addProperty("chance", chance);
        conditions.add(random);
        if (block != null && age != null) {
            JsonObject state = new JsonObject();
            state.addProperty("condition", "minecraft:block_state_property");
            state.addProperty("block", block);
            JsonObject properties = new JsonObject();
            properties.addProperty("age", String.valueOf(age));
            state.add("properties", properties);
            conditions.add(state);
        }
        pool.add("conditions", conditions);
        JsonArray entries = new JsonArray();
        entries.add(itemEntry(item));
        pool.add("entries", entries);
        return pool;
    }

    private static JsonObject itemEntry(String item) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", NS + ":" + item);
        return entry;
    }
}
