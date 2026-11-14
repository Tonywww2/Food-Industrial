package com.tonywww.foodindustrial.devtest;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tonywww.foodindustrial.food.ProductGrade;
import com.tonywww.foodindustrial.food.metabolism.Metabolism;
import com.tonywww.foodindustrial.food.metabolism.MetabolismState;
import com.tonywww.foodindustrial.food.metabolism.Substance;
import com.tonywww.foodindustrial.platform.PlatformCommands;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/**
 * Development commands of the metabolism (I35): {@code /fidev metabolism show [player]}, {@code set <substance>
 * <value>}, {@code eat <item> [servings]}, {@code grade <homemade|poor> [servings]}, {@code advance <ticks>},
 * {@code reset}.
 */
final class DevMetabolismCommands {
    private DevMetabolismCommands() {
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("fidev").then(Commands.literal("metabolism")
            .then(Commands.literal("show")
                .executes(command -> show(command, command.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(command -> show(command, EntityArgument.getPlayer(command, "player")))))
            .then(Commands.literal("set")
                .then(Commands.argument("substance", StringArgumentType.word())
                    .suggests((command, builder) -> {
                        for (Substance substance : Substance.all()) {
                            builder.suggest(substance.id());
                        }
                        return builder.buildFuture();
                    })
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0))
                        .executes(DevMetabolismCommands::set))))
            .then(Commands.literal("eat")
                .then(Commands.argument("item", PlatformCommands.itemArgument(context))
                    .executes(command -> eat(command, 1))
                    .then(Commands.argument("servings", IntegerArgumentType.integer(1, 1000))
                        .executes(command -> eat(command, IntegerArgumentType.getInteger(command, "servings"))))))
            .then(Commands.literal("grade")
                .then(Commands.argument("grade", StringArgumentType.word())
                    .suggests((command, builder) -> builder.suggest("homemade").suggest("poor").buildFuture())
                    .executes(command -> grade(command, 1))
                    .then(Commands.argument("servings", IntegerArgumentType.integer(1, 1000))
                        .executes(command -> grade(command, IntegerArgumentType.getInteger(command, "servings"))))))
            .then(Commands.literal("advance")
                .then(Commands.argument("ticks", LongArgumentType.longArg(1))
                    .executes(command -> {
                        ServerPlayer player = command.getSource().getPlayerOrException();
                        Metabolism.advance(player, LongArgumentType.getLong(command, "ticks"));
                        return show(command, player);
                    })))
            .then(Commands.literal("reset").executes(command -> {
                ServerPlayer player = command.getSource().getPlayerOrException();
                Metabolism.reset(player);
                return show(command, player);
            }))));
    }

    private static int show(CommandContext<CommandSourceStack> command, ServerPlayer player) {
        MetabolismState state = Metabolism.state(player);
        Metabolism.settle(player, state, true);
        StringBuilder text = new StringBuilder(player.getScoreboardName()).append(" clock ").append(state.clock());
        text.append(String.format(Locale.ROOT, "%ncaffeine %.1f mg [%d] · alcohol %.4f %% [%d] · sugar %.1f g [%d]",
            state.caffeine(), state.appliedTier(Substance.CAFFEINE), state.alcohol(), state.appliedTier(Substance.ALCOHOL),
            state.sugarInWindow(), state.appliedTier(Substance.FAST_SUGAR)));
        text.append(String.format(Locale.ROOT, "%nimpurity %.2f [%d] · C %.2f d [%d] · B1 %.2f d [%d] · D %.2f d [%d]",
            state.impurity(), state.appliedTier(Substance.IMPURITY),
            state.reserve(Substance.VITAMIN_C), state.appliedTier(Substance.VITAMIN_C),
            state.reserve(Substance.VITAMIN_B1), state.appliedTier(Substance.VITAMIN_B1),
            state.reserve(Substance.VITAMIN_D), state.appliedTier(Substance.VITAMIN_D)));
        reply(command, text.toString());
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        ServerPlayer player = command.getSource().getPlayerOrException();
        Substance substance = Substance.byId(StringArgumentType.getString(command, "substance"));
        if (substance == null) {
            reply(command, "unknown substance");
            return 0;
        }
        MetabolismState state = Metabolism.state(player);
        Metabolism.settle(player, state, true);
        state.set(substance, DoubleArgumentType.getDouble(command, "value"));
        Metabolism.changed(player);
        return show(command, player);
    }

    private static int eat(CommandContext<CommandSourceStack> command, int servings) throws CommandSyntaxException {
        ServerPlayer player = command.getSource().getPlayerOrException();
        ItemStack stack = new ItemStack(PlatformCommands.item(command, "item"));
        for (int i = 0; i < servings; i++) {
            Metabolism.eat(player, stack, ProductGrade.of(stack));
        }
        return show(command, player);
    }

    private static int grade(CommandContext<CommandSourceStack> command, int servings) throws CommandSyntaxException {
        ServerPlayer player = command.getSource().getPlayerOrException();
        String name = StringArgumentType.getString(command, "grade");
        ProductGrade grade = name.equals("homemade") ? ProductGrade.HOMEMADE : name.equals("poor") ? ProductGrade.POOR : null;
        if (grade == null) {
            reply(command, "grade is homemade or poor");
            return 0;
        }
        for (int i = 0; i < servings; i++) {
            Metabolism.eat(player, ItemStack.EMPTY, grade);
        }
        return show(command, player);
    }

    private static void reply(CommandContext<CommandSourceStack> command, String text) {
        command.getSource().sendSuccess(() -> Component.literal(text), false);
    }
}
