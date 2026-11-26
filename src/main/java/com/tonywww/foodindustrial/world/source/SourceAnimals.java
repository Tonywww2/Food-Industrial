package com.tonywww.foodindustrial.world.source;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.gui.RecipeInfo;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.world.PlatformAnimals;

/**
 * The animals of I44 (user 2026-11-19: 兽类新增生物): the civet on the ocelot's body (jungles and savannas, drops
 * civet musk), the beaver on the fox's body (swamps and rivers, drops castoreum) and the duck on the chicken's body
 * (swamps and rivers, drops raw duck, lays duck eggs; foods/23). Their loot tables and biome spawns are data; spawn
 * numbers are drafts (I44 待审).
 */
final class SourceAnimals {
    private static final String NS = FoodIndustrial.MOD_ID;

    private SourceAnimals() {
    }

    static void bootstrap() {
        for (SourceTable.Animal animal : SourceTable.get().animals()) {
            String lays = animal.lays();
            PlatformAnimals.register(animal.id(), PlatformAnimals.Body.valueOf(animal.body()), lays == null ? null : () -> SourceBlocks.item(lays));
            Lang.add("entity." + NS + "." + animal.id(), animal.zh(), animal.en());
            SourceTable.Biomes first = SourceTable.get().biomes().get(animal.biomes().get(0));
            String name = animal.en().toLowerCase(java.util.Locale.ROOT);
            RecipeInfo.page(animal.drop().item(), animal.zh() + "生活在" + first.zh() + "等地，击杀掉落", "Dropped by " + name
                + "s, found in " + first.en() + " and elsewhere");
            if (lays != null) {
                RecipeInfo.page(lays, animal.zh() + "生活在" + first.zh() + "等地，成年后定时下蛋", "Laid by grown " + name
                    + "s, found in " + first.en() + " and elsewhere");
            }
        }
        PlatformDatagen.register("animals", SourceAnimals::generate);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        for (SourceTable.Animal animal : SourceTable.get().animals()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("type", "minecraft:item");
            entry.addProperty("name", NS + ":" + animal.drop().item());
            JsonObject pool = new JsonObject();
            pool.addProperty("rolls", 1.0);
            pool.addProperty("bonus_rolls", 0.0);
            JsonArray entries = new JsonArray();
            entries.add(entry);
            pool.add("entries", entries);
            JsonArray pools = new JsonArray();
            pools.add(pool);
            JsonObject table = new JsonObject();
            table.addProperty("type", "minecraft:entity");
            table.add("pools", pools);
            table.addProperty("random_sequence", NS + ":entities/" + animal.id());
            sink.json(PlatformDatagen.Kind.ENTITY_LOOT, NS, animal.id(), table);

            JsonObject modifier = new JsonObject();
            modifier.addProperty("type", PlatformDatagen.loaderNamespace() + ":add_spawns");
            JsonArray biomes = new JsonArray();
            for (String group : animal.biomes()) {
                SourceTable.get().biomes().get(group).biomes().forEach(biomes::add);
            }
            modifier.add("biomes", biomes);
            JsonObject spawner = new JsonObject();
            spawner.addProperty("type", NS + ":" + animal.id());
            spawner.addProperty("weight", animal.weight());
            spawner.addProperty("minCount", animal.min());
            spawner.addProperty("maxCount", animal.max());
            JsonArray spawners = new JsonArray();
            spawners.add(spawner);
            modifier.add("spawners", spawners);
            sink.json(PlatformDatagen.Kind.BIOME_MODIFIER, NS, "spawn_" + animal.id(), modifier);
        }
    }
}
