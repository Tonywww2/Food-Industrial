package com.tonywww.foodindustrial.platform;

import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import net.minecraft.world.level.ItemLike;
//? if <1.21 {
import net.minecraft.world.level.block.ComposterBlock;
//?}

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Items the vanilla composter takes and their chance of adding a layer (I07). Forge 1.20.1 fills the
 * composter's table at common setup; NeoForge (1.21 on) reads the data map {@code neoforge:compostables},
 * which data generation writes.
 */
public final class PlatformCompost {
    private static final Map<Supplier<? extends ItemLike>, Float> CHANCES = new LinkedHashMap<>();

    static {
        //? if <1.21 {
        PlatformEvents.onCommonSetup(() -> CHANCES.forEach((item, chance) -> ComposterBlock.COMPOSTABLES.put(item.get().asItem(), (float) chance)));
        //?} else {
        /*PlatformDatagen.register("compostables", PlatformCompost::generate);
        *///?}
    }

    private PlatformCompost() {
    }

    /** Lets the composter take {@code item} with {@code chance} (0–1) per item, as the vanilla table. */
    public static void register(Supplier<? extends ItemLike> item, float chance) {
        if (chance <= 0 || chance > 1) {
            throw new IllegalArgumentException("Compost chance " + chance + " out of (0, 1]");
        }
        CHANCES.put(item, chance);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        JsonObject values = new JsonObject();
        CHANCES.forEach((item, chance) -> {
            JsonObject entry = new JsonObject();
            entry.addProperty("chance", chance);
            values.add(PlatformTags.itemId(item.get().asItem()), entry);
        });
        JsonObject json = new JsonObject();
        json.add("values", values);
        sink.json(PlatformDatagen.Kind.DATA, "neoforge", "data_maps/item/compostables", json);
    }
}
