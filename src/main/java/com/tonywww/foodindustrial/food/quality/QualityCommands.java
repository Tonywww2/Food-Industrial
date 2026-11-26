package com.tonywww.foodindustrial.food.quality;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.food.FoodLine;
import com.tonywww.foodindustrial.food.SemiFinished;
import com.tonywww.foodindustrial.platform.PlatformCommands;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code /foodindustrial quality show} (I39): the quality data of the item in the main hand, for checking recipes:
 * overall grade, each evaluated item, the label, the flags and the stored code.
 */
public final class QualityCommands {
    private static final String KEY = "command." + FoodIndustrial.MOD_ID + ".quality.";

    private QualityCommands() {
    }

    public static void bootstrap() {
        Lang.add(KEY + "none", "手上的物品不带品质数据", "The item in hand carries no quality data");
        Lang.add(KEY + "summary", "%s：%s（代码 %s）", "%s: %s (code %s)");
        Lang.add(KEY + "items", "评价项：%s", "Quality items: %s");
        Lang.add(KEY + "flags", "标记：%s", "Flags: %s");
        PlatformEvents.onRegisterCommands(QualityCommands::register);
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(FoodIndustrial.MOD_ID)
            .requires(PlatformCommands.gameMasters())
            .then(Commands.literal("quality").then(Commands.literal("show").executes(QualityCommands::show))));
    }

    private static int show(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof ServerPlayer player)) {
            return 0;
        }
        ItemStack stack = player.getMainHandItem();
        int code = FoodLine.quality(stack);
        if (code == FoodQuality.NONE) {
            reply(command, Component.translatable(KEY + "none"));
            return 0;
        }
        String id = PlatformTags.itemId(stack.getItem());
        String semi = SemiFinished.keyOf(stack);
        MutableComponent grade = QualityText.grade(FoodQuality.grade(code)).copy();
        if (FoodQuality.label(code) != FoodQuality.LABEL_NONE) {
            grade.append(" · ").append(QualityText.label(FoodQuality.label(code)));
        }
        reply(command, Component.translatable(KEY + "summary", semi.isEmpty() ? id : id + " " + semi, grade, code));
        MutableComponent items = Component.empty();
        for (QualityItem item : QualityItem.values()) {
            int value = FoodQuality.item(code, item);
            if (value >= 0) {
                items.append(QualityText.item(item)).append(" ").append(QualityText.grade(value)).append("  ");
            }
        }
        reply(command, Component.translatable(KEY + "items", items));
        List<String> flags = new ArrayList<>();
        if (FoodQuality.has(code, FoodQuality.ASPARTAME)) {
            flags.add("aspartame");
        }
        if (FoodQuality.has(code, FoodQuality.ASPARTAME_ACESULFAME)) {
            flags.add("aspartame_acesulfame");
        }
        if (FoodQuality.has(code, FoodQuality.CURING_AGENT)) {
            flags.add("curing_agent");
        }
        if (FoodQuality.has(code, FoodQuality.NITRITE_MISUSE)) {
            flags.add("nitrite_misuse");
        }
        reply(command, Component.translatable(KEY + "flags", flags.isEmpty() ? "-" : String.join(", ", flags)));
        return 1;
    }

    private static void reply(CommandContext<CommandSourceStack> command, Component text) {
        command.getSource().sendSuccess(() -> text, false);
    }
}
