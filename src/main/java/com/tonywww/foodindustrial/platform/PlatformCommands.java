package com.tonywww.foodindustrial.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.world.item.Item;

import java.util.function.Predicate;
//? if >=26.1 {
/*import net.minecraft.commands.Commands;
*///?}

/** Command arguments and permissions whose vanilla API differs between versions. */
public final class PlatformCommands {
    private PlatformCommands() {
    }

    /** An item id argument ({@code minecraft:apple}); read it with {@link #item}. */
    public static ArgumentType<?> itemArgument(CommandBuildContext context) {
        return ItemArgument.item(context);
    }

    /** The item of an argument made by {@link #itemArgument}. */
    public static Item item(CommandContext<CommandSourceStack> context, String name) {
        //? if >=26.1 {
        /*return ItemArgument.getItem(context, name).item().value();
        *///?} else {
        return ItemArgument.getItem(context, name).getItem();
        //?}
    }

    /** Permission of operators at level 2 (game masters), as vanilla's {@code /give}. */
    public static Predicate<CommandSourceStack> gameMasters() {
        //? if >=26.1 {
        /*return Commands.hasPermission(Commands.LEVEL_GAMEMASTERS);
        *///?} else {
        return source -> source.hasPermission(2);
        //?}
    }
}
