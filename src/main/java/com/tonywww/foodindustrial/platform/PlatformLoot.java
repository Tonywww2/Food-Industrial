package com.tonywww.foodindustrial.platform;

import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.event.LootTableLoadEvent;
//?} else {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import com.mojang.serialization.JsonOps;
import net.neoforged.neoforge.event.LootTableLoadEvent;
*///?}

/**
 * Pools added to existing loot tables when they load (I44: village chests, oak leaves, carrots, fishing,
 * shipwrecks). Pools are vanilla loot JSON built by the business code, parsed with the node's own loot
 * deserializer so the same JSON works on every node.
 */
public final class PlatformLoot {
    private static final Map<String, List<Supplier<JsonObject>>> POOLS = new LinkedHashMap<>();

    private PlatformLoot() {
    }

    /** Adds a pool to the loot table {@code table} ({@code namespace:path}, e.g. {@code minecraft:chests/village/village_plains_house}). */
    public static void addPool(String table, Supplier<JsonObject> pool) {
        POOLS.computeIfAbsent(table, key -> new ArrayList<>()).add(pool);
    }

    /** The pools added to {@code table}, as JSON (for reports and tests). */
    public static List<JsonObject> pools(String table) {
        return POOLS.getOrDefault(table, List.of()).stream().map(Supplier::get).toList();
    }

    /** Every table that gets pools. */
    public static java.util.Set<String> tables() {
        return java.util.Collections.unmodifiableSet(POOLS.keySet());
    }

    static void onLoad(LootTableLoadEvent event) {
        List<Supplier<JsonObject>> pools = POOLS.get(event.getName().toString());
        if (pools == null) {
            return;
        }
        int index = 0;
        for (Supplier<JsonObject> supplier : pools) {
            JsonObject json = supplier.get().deepCopy();
            json.addProperty("name", FoodIndustrial.MOD_ID + "_" + index++);
            try {
                event.getTable().addPool(parse(event, json));
                FoodIndustrial.LOGGER.debug("Loot: added a pool to {}", event.getName());
            } catch (RuntimeException exception) {
                // never lose the vanilla table over an added pool
                FoodIndustrial.LOGGER.error("Could not add a pool to loot table {}", event.getName(), exception);
            }
        }
    }

    //? if forge {
    /**
     * Forge 1.20.1 reads pool names from a JSON context that only exists while a whole table loads, so the pool is
     * built from the JSON subset the mod uses: constant or uniform rolls; item entries with {@code set_count};
     * {@code random_chance} and {@code block_state_property} conditions.
     */
    private static LootPool parse(LootTableLoadEvent event, JsonObject json) {
        LootPool.Builder pool = LootPool.lootPool().name(json.get("name").getAsString()).setRolls(number(json.get("rolls")));
        if (json.has("conditions")) {
            for (com.google.gson.JsonElement element : json.getAsJsonArray("conditions")) {
                pool.when(condition(element.getAsJsonObject(), event));
            }
        }
        for (com.google.gson.JsonElement element : json.getAsJsonArray("entries")) {
            JsonObject entry = element.getAsJsonObject();
            net.minecraft.world.item.Item item = PlatformTags.item(entry.get("name").getAsString())
                .orElseThrow(() -> new IllegalStateException("Unknown item in a loot pool for " + event.getName() + ": " + entry.get("name")));
            var builder = net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem(item);
            if (entry.has("weight")) {
                builder.setWeight(entry.get("weight").getAsInt());
            }
            if (entry.has("functions")) {
                for (com.google.gson.JsonElement function : entry.getAsJsonArray("functions")) {
                    JsonObject f = function.getAsJsonObject();
                    if (!f.get("function").getAsString().equals("minecraft:set_count")) {
                        throw new IllegalStateException("Unsupported loot function " + f.get("function") + " for " + event.getName());
                    }
                    builder.apply(net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount(number(f.get("count"))));
                }
            }
            pool.add(builder);
        }
        return pool.build();
    }

    private static net.minecraft.world.level.storage.loot.providers.number.NumberProvider number(com.google.gson.JsonElement json) {
        if (json.isJsonPrimitive()) {
            return net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly(json.getAsFloat());
        }
        JsonObject uniform = json.getAsJsonObject();
        return net.minecraft.world.level.storage.loot.providers.number.UniformGenerator.between(uniform.get("min").getAsFloat(),
            uniform.get("max").getAsFloat());
    }

    private static net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder condition(JsonObject json, LootTableLoadEvent event) {
        String type = json.get("condition").getAsString();
        if (type.equals("minecraft:random_chance")) {
            return net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(json.get("chance").getAsFloat());
        }
        if (type.equals("minecraft:block_state_property")) {
            net.minecraft.world.level.block.Block block = PlatformTags.block(json.get("block").getAsString()).orElseThrow();
            var properties = net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties();
            for (var property : json.getAsJsonObject("properties").entrySet()) {
                properties.hasProperty(block.getStateDefinition().getProperty(property.getKey()), property.getValue().getAsString());
            }
            return net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                .setProperties(properties);
        }
        throw new IllegalStateException("Unsupported loot condition " + type + " for " + event.getName());
    }
    //?} else {
    /*private static LootPool parse(LootTableLoadEvent event, JsonObject json) {
        HolderLookup.Provider registries = event.getRegistries();
        return LootPool.CODEC.parse(RegistryOps.create(JsonOps.INSTANCE, registries), json)
            .getOrThrow(message -> new IllegalStateException("Bad loot pool for " + event.getName() + ": " + message));
    }
    *///?}
}
