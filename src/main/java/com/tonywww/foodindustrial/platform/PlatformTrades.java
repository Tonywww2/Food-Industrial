package com.tonywww.foodindustrial.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
//? if forge {
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
//?}
//? if neoforge && <26.1 {
/*import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
*///?}

/**
 * Villager offers added by the mod (I44: farmers sell crop produce). Before 26.1 the offers join the profession's
 * pool in the loader's trade event; 26.1 trades are data ({@code villager_trade} files listed in the vanilla
 * {@code #minecraft:<profession>/level_<n>} tags), written by {@link #generate}.
 */
public final class PlatformTrades {
    /** One offer: {@code count} of {@code item} for {@code emeralds} emeralds. */
    public record Sale(String id, String profession, int level, Supplier<? extends Item> item, int count, int emeralds, int maxUses, int xp) {
    }

    private static final List<Sale> SALES = new ArrayList<>();

    private PlatformTrades() {
    }

    /** Adds an offer of a vanilla profession ({@code farmer}, ...) at {@code level} 1–5; {@code id} names the 26.1 file. */
    public static void sells(String id, String profession, int level, Supplier<? extends Item> item, int count, int emeralds, int maxUses, int xp) {
        SALES.add(new Sale(id, profession, level, item, count, emeralds, maxUses, xp));
    }

    public static List<Sale> sales() {
        return List.copyOf(SALES);
    }

    /** 26.1: the {@code villager_trade} files and the level tags; nothing on the other nodes. */
    public static void generate(PlatformDatagen.Sink sink) {
        //? if >=26.1 {
        /*Map<String, JsonArray> tags = new TreeMap<>();
        for (Sale sale : SALES) {
            JsonObject trade = new JsonObject();
            JsonObject wants = new JsonObject();
            wants.addProperty("id", "minecraft:emerald");
            wants.addProperty("count", (double) sale.emeralds());
            trade.add("wants", wants);
            JsonObject gives = new JsonObject();
            gives.addProperty("id", PlatformTags.itemId(sale.item().get()));
            gives.addProperty("count", sale.count());
            trade.add("gives", gives);
            trade.addProperty("max_uses", (double) sale.maxUses());
            trade.addProperty("xp", (double) sale.xp());
            trade.addProperty("reputation_discount", 0.05);
            String path = sale.profession() + "/" + sale.level() + "/" + sale.id();
            sink.json(PlatformDatagen.Kind.DATA, FoodIndustrial.MOD_ID, "villager_trade/" + path, trade);
            tags.computeIfAbsent(sale.profession() + "/level_" + sale.level(), key -> new JsonArray()).add(FoodIndustrial.MOD_ID + ":" + path);
        }
        tags.forEach((tag, values) -> {
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            json.add("values", values);
            sink.json(PlatformDatagen.Kind.DATA, "minecraft", "tags/villager_trade/" + tag, json);
        });
        *///?}
    }

    //? if forge {
    static void onTrades(VillagerTradesEvent event) {
        String profession = event.getType().name();
        for (Sale sale : SALES) {
            if (sale.profession().equals(profession)) {
                event.getTrades().get(sale.level()).add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, sale.emeralds()),
                    new ItemStack(sale.item().get(), sale.count()), sale.maxUses(), sale.xp(), 0.05F));
            }
        }
    }
    //?}
    //? if neoforge && <26.1 {
    /*static void onTrades(VillagerTradesEvent event) {
        String profession = event.getType().name();
        for (Sale sale : SALES) {
            if (sale.profession().equals(profession)) {
                event.getTrades().get(sale.level()).add((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, sale.emeralds()),
                    new ItemStack(sale.item().get(), sale.count()), sale.maxUses(), sale.xp(), 0.05F));
            }
        }
    }
    *///?}
}
