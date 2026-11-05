package com.tonywww.foodindustrial.machine;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.SharedTags;

/**
 * Generated assets and data of machines, ports and the machine tools: block states (facing × active),
 * orientable block models, item models, loot tables (the block drops itself), the pickaxe tag and the
 * tools' crafting recipes. Textures are
 * {@code block/<id>_front}, {@code _front_active}, {@code _side}, {@code _top}; ports use one
 * {@code block/<id>} texture.
 */
final class MachineData {
    private static final String NS = FoodIndustrial.MOD_ID;
    private static final String[] FACINGS = {"north", "east", "south", "west"};

    private MachineData() {
    }

    static void bootstrap() {
        for (MachineDefinition definition : MachineDefinition.all()) {
            if (definition.development()) {
                continue;
            }
            SharedTags.block("minecraft:mineable/pickaxe", NS + ":" + definition.id());
        }
        for (ModMachines.Port port : ModMachines.ports()) {
            SharedTags.block("minecraft:mineable/pickaxe", NS + ":" + portId(port));
        }
        PlatformDatagen.register("machines", MachineData::generate);
    }

    private static String portId(ModMachines.Port port) {
        return port.kind().name().toLowerCase(java.util.Locale.ROOT) + "_port";
    }

    private static void generate(PlatformDatagen.Sink sink) {
        tools(sink);
        for (MachineDefinition definition : MachineDefinition.all()) {
            if (definition.development()) {
                continue;
            }
            String id = definition.id();
            JsonObject variants = new JsonObject();
            for (int i = 0; i < FACINGS.length; i++) {
                for (boolean active : new boolean[]{false, true}) {
                    JsonObject variant = new JsonObject();
                    variant.addProperty("model", NS + ":block/" + id + (active ? "_active" : ""));
                    int rotation = ((i + 2) % 4) * 90 - 180;
                    if (Math.floorMod(rotation, 360) != 0) {
                        variant.addProperty("y", Math.floorMod(rotation, 360));
                    }
                    variants.add("active=" + active + ",facing=" + FACINGS[i], variant);
                }
            }
            JsonObject state = new JsonObject();
            state.add("variants", variants);
            sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
            sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, orientable(id, false));
            sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id + "_active", orientable(id, true));
            blockItem(sink, id);
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, id, dropSelf(NS + ":" + id));
        }
        for (ModMachines.Port port : ModMachines.ports()) {
            String id = portId(port);
            JsonObject variants = new JsonObject();
            JsonObject variant = new JsonObject();
            variant.addProperty("model", NS + ":block/" + id);
            variants.add("", variant);
            JsonObject state = new JsonObject();
            state.add("variants", variants);
            sink.json(PlatformDatagen.Kind.BLOCKSTATE, NS, id, state);
            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:block/cube_all");
            JsonObject textures = new JsonObject();
            textures.addProperty("all", NS + ":block/" + id);
            model.add("textures", textures);
            sink.json(PlatformDatagen.Kind.MODEL, NS, "block/" + id, model);
            blockItem(sink, id);
            sink.json(PlatformDatagen.Kind.BLOCK_LOOT, NS, id, dropSelf(NS + ":" + id));
        }
    }

    /** Item models and crafting recipes of the device wrench and redprint (machines/04 §4). */
    private static void tools(PlatformDatagen.Sink sink) {
        for (String id : ModMachines.tools()) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:item/handheld");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", NS + ":item/" + id);
            model.add("textures", textures);
            sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, model);
            JsonObject inner = new JsonObject();
            inner.addProperty("type", "minecraft:model");
            inner.addProperty("model", NS + ":item/" + id);
            JsonObject definition = new JsonObject();
            definition.add("model", inner);
            sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, id, definition);
        }
        String common = PlatformDatagen.commonTagNamespace();
        JsonObject wrench = new JsonObject();
        wrench.addProperty("type", "minecraft:crafting_shaped");
        wrench.addProperty("category", "equipment");
        JsonObject key = new JsonObject();
        key.add("I", PlatformDatagen.ingredient("#" + common + ":ingots/iron"));
        key.add("C", PlatformDatagen.ingredient("#" + common + ":ingots/copper"));
        wrench.add("key", key);
        JsonArray pattern = new JsonArray();
        pattern.add("I I");
        pattern.add(" C ");
        pattern.add(" I ");
        wrench.add("pattern", pattern);
        wrench.add("result", PlatformDatagen.recipeResult(NS + ":" + com.tonywww.foodindustrial.machine.tool.DeviceWrenchItem.ID, 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, com.tonywww.foodindustrial.machine.tool.DeviceWrenchItem.ID, wrench);
        JsonObject redprint = new JsonObject();
        redprint.addProperty("type", "minecraft:crafting_shapeless");
        redprint.addProperty("category", "misc");
        JsonArray ingredients = new JsonArray();
        ingredients.add(PlatformDatagen.ingredient("#" + common + ":dusts/redstone"));
        ingredients.add(PlatformDatagen.ingredient("minecraft:paper"));
        ingredients.add(PlatformDatagen.ingredient("minecraft:map"));
        redprint.add("ingredients", ingredients);
        redprint.add("result", PlatformDatagen.recipeResult(NS + ":" + com.tonywww.foodindustrial.machine.tool.RedprintItem.ID, 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, com.tonywww.foodindustrial.machine.tool.RedprintItem.ID, redprint);
    }

    private static JsonObject orientable(String id, boolean active) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/orientable");
        JsonObject textures = new JsonObject();
        textures.addProperty("front", NS + ":block/" + id + (active ? "_front_active" : "_front"));
        textures.addProperty("side", NS + ":block/" + id + "_side");
        textures.addProperty("top", NS + ":block/" + id + "_top");
        model.add("textures", textures);
        return model;
    }

    private static void blockItem(PlatformDatagen.Sink sink, String id) {
        JsonObject item = new JsonObject();
        item.addProperty("parent", NS + ":block/" + id);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, item);
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", NS + ":block/" + id);
        JsonObject definition = new JsonObject();
        definition.add("model", model);
        sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, id, definition);
    }

    private static JsonObject dropSelf(String id) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", id);
        JsonArray entries = new JsonArray();
        entries.add(entry);
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:survives_explosion");
        JsonArray conditions = new JsonArray();
        conditions.add(condition);
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);
        pool.add("entries", entries);
        pool.add("conditions", conditions);
        JsonArray pools = new JsonArray();
        pools.add(pool);
        JsonObject loot = new JsonObject();
        loot.addProperty("type", "minecraft:block");
        loot.add("pools", pools);
        return loot;
    }
}
