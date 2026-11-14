package com.tonywww.foodindustrial.food.nutrition;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformCommands;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * {@code /foodindustrial nutrition show <item>} and {@code /foodindustrial nutrition missing} (foods/03 §3.2,
 * permission level 2).
 */
public final class NutritionCommands {
    private static final String KEY = "commands.foodindustrial.nutrition.";

    private NutritionCommands() {
    }

    static void bootstrap() {
        Lang.add(KEY + "none", "%s：无数据", "%s: no data");
        Lang.add(KEY + "serving", "%s：一份 %s g（%s，优先级 %s）", "%s: %s g per serving (%s, priority %s)");
        Lang.add(KEY + "missing_keys", "缺键：%s", "Missing keys: %s");
        Lang.add(KEY + "energy", "能量 %s kcal", "Energy %s kcal");
        Lang.add(KEY + "impurity", "杂质 %s", "Impurity %s");
        Lang.add(KEY + "missing", "缺营养数据的食物 %s 种，清单已写入 %s", "%s foods lack nutrition data; list written to %s");
        Lang.add(KEY + "missing_mod", "%s：%s", "%s: %s");
        Lang.add(KEY + "missing_none", "可吃、可喝的物品都有完整的营养数据", "Every edible or drinkable item has complete nutrition data");
        Lang.add(KEY + "write_failed", "清单写入失败：%s", "Could not write the list: %s");
        PlatformEvents.onRegisterCommands(NutritionCommands::register);
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(FoodIndustrial.MOD_ID)
            .requires(PlatformCommands.gameMasters())
            .then(Commands.literal("nutrition")
                .then(Commands.literal("show")
                    .then(Commands.argument("item", PlatformCommands.itemArgument(context))
                        .executes(command -> show(command, PlatformCommands.item(command, "item")))))
                .then(Commands.literal("missing").executes(NutritionCommands::missing))));
    }

    private static int show(CommandContext<CommandSourceStack> command, Item item) {
        String id = PlatformTags.itemId(item);
        NutritionData data = Nutrition.of(item);
        if (data == null) {
            reply(command, net.minecraft.network.chat.Component.translatable(KEY + "none", id));
            return 0;
        }
        reply(command, net.minecraft.network.chat.Component.translatable(KEY + "serving", id, plain(data.servingG()),
            String.valueOf(data.source()), data.priority()));
        List<String> parts = new ArrayList<>();
        for (Component component : Component.all()) {
            parts.add(component.key() + " " + plain(data.amount(component)));
        }
        reply(command, net.minecraft.network.chat.Component.literal(String.join(" · ", parts)));
        if (data.missing() != 0) {
            reply(command, net.minecraft.network.chat.Component.translatable(KEY + "missing_keys", missingKeys(data.missing())));
        }
        if (data.impurity() > 0) {
            reply(command, net.minecraft.network.chat.Component.translatable(KEY + "impurity", plain(data.impurity())));
        }
        if (data.energyKcal() != null) {
            reply(command, net.minecraft.network.chat.Component.translatable(KEY + "energy", plain(data.energyKcal())));
        }
        return 1;
    }

    private static int missing(CommandContext<CommandSourceStack> command) {
        JsonArray list = new JsonArray();
        Map<String, Integer> byMod = new TreeMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(item);
            boolean bites = PlatformItems.eatenByBites(item);
            if (!bites && !PlatformItems.consumable(stack)) {
                continue;
            }
            PlatformItems.FoodValues food = PlatformItems.foodValues(stack);
            PlatformItems.ConsumeKind kind = PlatformItems.consumeKind(stack);
            NutritionData data = Nutrition.of(item);
            if (data != null && data.missing() == 0) {
                continue;
            }
            String id = PlatformTags.itemId(item);
            String mod = id.substring(0, id.indexOf(':'));
            JsonObject entry = new JsonObject();
            entry.addProperty("item", id);
            entry.addProperty("translation_key", PlatformItems.descriptionKey(item));
            if (food != null) {
                entry.addProperty("nutrition", food.nutrition());
                entry.addProperty("saturation", Math.round(food.saturation() * 100) / 100.0);
            }
            entry.addProperty("use_animation", bites ? "bites" : kind.name().toLowerCase(java.util.Locale.ROOT));
            entry.addProperty("mod", mod);
            if (data == null) {
                entry.addProperty("problem", "no_data");
            } else {
                JsonArray keys = new JsonArray();
                for (Component component : Component.all()) {
                    if ((data.missing() & component.bit()) != 0) {
                        keys.add(component.key());
                    }
                }
                entry.add("missing_keys", keys);
            }
            list.add(entry);
            byMod.merge(mod, 1, Integer::sum);
        }
        if (list.isEmpty()) {
            reply(command, net.minecraft.network.chat.Component.translatable(KEY + "missing_none"));
            return 0;
        }
        Path file = Platform.gameDirectory().resolve(FoodIndustrial.MOD_ID).resolve("nutrition_missing.json");
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(list) + "\n",
                StandardCharsets.UTF_8);
        } catch (IOException exception) {
            command.getSource().sendFailure(net.minecraft.network.chat.Component.translatable(KEY + "write_failed", exception.getMessage()));
            return 0;
        }
        reply(command, net.minecraft.network.chat.Component.translatable(KEY + "missing", list.size(), file.toString()));
        byMod.forEach((mod, count) -> reply(command, net.minecraft.network.chat.Component.translatable(KEY + "missing_mod", mod, count)));
        return list.size();
    }

    private static String missingKeys(int mask) {
        List<String> keys = new ArrayList<>();
        for (Component component : Component.all()) {
            if ((mask & component.bit()) != 0) {
                keys.add(component.key());
            }
        }
        return String.join(", ", keys);
    }

    /** A number as written in a data file: no trailing zeros. */
    static String plain(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private static void reply(CommandContext<CommandSourceStack> command, MutableComponent text) {
        command.getSource().sendSuccess(() -> text, false);
    }
}
