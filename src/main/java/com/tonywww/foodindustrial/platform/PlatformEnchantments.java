package com.tonywww.foodindustrial.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
//? if forge {
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;
//?}

/**
 * The mod's enchantment (I44, foods/17 §1.1, additives/11 问题 69): “肢解” {@code foodindustrial:dismemberment}, a
 * single-level treasure enchantment for melee weapons that only comes from loot chests and librarians. 1.20.1
 * registers it in code (treasure only, so never from the enchanting table); from 1.21 enchantments are data, written by
 * {@link #generate} with the vanilla tags that keep it out of the table and put it in loot and trades.
 */
public final class PlatformEnchantments {
    public static final String DISMEMBERMENT = "dismemberment";
    //? if forge {
    private static Supplier<Enchantment> dismemberment;
    //?}

    private PlatformEnchantments() {
    }

    /** Registers the enchantment where it is code (1.20.1). Call during mod construction. */
    public static void bootstrap() {
        //? if forge {
        dismemberment = PlatformRegistry.register(Registries.ENCHANTMENT, DISMEMBERMENT, () -> new Enchantment(Enchantment.Rarity.RARE,
            EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}) {
            @Override
            public boolean isTreasureOnly() {
                return true;
            }

            @Override
            public boolean canEnchant(ItemStack stack) {
                return stack.getItem() instanceof AxeItem || super.canEnchant(stack);
            }

            @Override
            public int getMinCost(int level) {
                return 25;
            }

            @Override
            public int getMaxCost(int level) {
                return 75;
            }
        });
        //?}
    }

    /** The dismemberment level of {@code weapon} (0 without it). */
    public static int dismemberment(ItemStack weapon, Level level) {
        if (weapon.isEmpty()) {
            return 0;
        }
        //? if forge {
        return EnchantmentHelper.getItemEnchantmentLevel(dismemberment.get(), weapon);
        //?}
        //? if >=26.1 {
        /*return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(PlatformIdentifier.key(Registries.ENCHANTMENT, DISMEMBERMENT))
            .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, weapon)).orElse(0);
        *///?}
        //? if neoforge && <26.1 {
        /*return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(PlatformIdentifier.key(Registries.ENCHANTMENT, DISMEMBERMENT))
            .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, weapon)).orElse(0);
        *///?}
    }

    /** From 1.21: the enchantment file and the tags (treasure, tradeable at double price, random loot); nothing on 1.20.1. */
    public static void generate(PlatformDatagen.Sink sink) {
        //? if >=1.21 {
        /*JsonObject enchantment = new JsonObject();
        JsonObject description = new JsonObject();
        description.addProperty("translate", "enchantment." + FoodIndustrial.MOD_ID + "." + DISMEMBERMENT);
        enchantment.add("description", description);
        enchantment.addProperty("supported_items", "#minecraft:enchantable/weapon");
        enchantment.addProperty("weight", 2);
        enchantment.addProperty("max_level", 1);
        enchantment.add("min_cost", cost(25));
        enchantment.add("max_cost", cost(75));
        enchantment.addProperty("anvil_cost", 4);
        JsonArray slots = new JsonArray();
        slots.add("mainhand");
        enchantment.add("slots", slots);
        sink.json(PlatformDatagen.Kind.DATA, FoodIndustrial.MOD_ID, "enchantment/" + DISMEMBERMENT, enchantment);
        for (String tag : new String[]{"treasure", "tradeable", "double_trade_price", "on_random_loot"}) {
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            JsonArray values = new JsonArray();
            values.add(FoodIndustrial.MOD_ID + ":" + DISMEMBERMENT);
            json.add("values", values);
            sink.json(PlatformDatagen.Kind.DATA, "minecraft", "tags/enchantment/" + tag, json);
        }
        *///?}
    }

    private static JsonObject cost(int base) {
        JsonObject cost = new JsonObject();
        cost.addProperty("base", base);
        cost.addProperty("per_level_above_first", 25);
        return cost;
    }
}
