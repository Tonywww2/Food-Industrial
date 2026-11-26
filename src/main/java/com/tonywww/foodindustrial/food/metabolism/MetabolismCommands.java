package com.tonywww.foodindustrial.food.metabolism;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.PlatformCommands;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@code /foodindustrial metabolism show [player]} (I46, permission level 2): every substance's amount or reserve
 * and tier, settled to now. Parts switched off in the configuration are left out.
 */
final class MetabolismCommands {
    private static final String KEY = "commands.foodindustrial.metabolism.";

    private MetabolismCommands() {
    }

    static void bootstrap() {
        Lang.add(KEY + "title", "%s 的体内状况", "%s's body");
        Lang.add(KEY + "line", "%s：%s（%s）", "%s: %s (%s)");
        PlatformEvents.onRegisterCommands(MetabolismCommands::register);
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(FoodIndustrial.MOD_ID)
            .requires(PlatformCommands.gameMasters())
            .then(Commands.literal("metabolism")
                .then(Commands.literal("show")
                    .executes(command -> show(command, command.getSource().getPlayerOrException()))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(command -> show(command, EntityArgument.getPlayer(command, "player")))))));
    }

    private static int show(CommandContext<CommandSourceStack> command, ServerPlayer player) {
        MetabolismState state = Metabolism.state(player);
        Metabolism.settle(player, state, true);
        reply(command, Component.translatable(KEY + "title", player.getDisplayName()));
        for (Substance substance : Substance.all()) {
            if (substance == Substance.IMPURITY && !state.impurityOn
                || substance.nutrient() && !MetabolismState.on(state.nutrientsOn, substance.nutrientIndex())) {
                continue;
            }
            double amount = switch (substance) {
                case CAFFEINE -> state.caffeine();
                case ALCOHOL -> state.alcohol();
                case FAST_SUGAR -> state.sugarInWindow();
                case NITRITE -> state.methemoglobin();
                case IMPURITY -> state.impurity();
                default -> state.reserve(substance);
            };
            reply(command, Component.translatable(KEY + "line", MetabolismText.name(substance), MetabolismText.amount(substance, amount),
                MetabolismText.tier(substance, state.appliedTier(substance))));
        }
        return 1;
    }

    private static void reply(CommandContext<CommandSourceStack> command, MutableComponent text) {
        command.getSource().sendSuccess(() -> text, false);
    }
}
