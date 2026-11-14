package com.tonywww.foodindustrial.devtest;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.logistics.TankBlockEntity;
import com.tonywww.foodindustrial.manual.DeviceContents;
import com.tonywww.foodindustrial.manual.Fumigation;
import com.tonywww.foodindustrial.manual.HandToolItem;
import com.tonywww.foodindustrial.manual.T0DeviceBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.Environment;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Development commands for the T0 tools and devices (I12), run as a player:
 * <ul>
 *   <li>{@code /fidev t0 show x y z} — contents with purity, capabilities and the action-bar line;</li>
 *   <li>{@code /fidev t0 use x y z [sneak]} — a right click on the device with the main-hand item;</li>
 *   <li>{@code /fidev t0 advance x y z ticks} — passive batches covering {@code ticks} (1,200 at a time);</li>
 *   <li>{@code /fidev tool toolSlot targetSlot [times]} — the hand tool in an inventory slot right-clicked
 *       onto another slot (0–8 hotbar, 9–35 main);</li>
 *   <li>{@code /fidev env x y z} — environment capabilities of a position;</li>
 *   <li>{@code /fidev tartar x y z ticks} — tartar growth of a wooden tank.</li>
 * </ul>
 */
final class DevManualCommands {
    private static final int CHUNK = 1200;

    private DevManualCommands() {
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fidev")
            .then(Commands.literal("t0")
                .then(Commands.literal("show").then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevManualCommands::show)))
                .then(Commands.literal("use").then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevManualCommands::use)))
                .then(Commands.literal("advance").then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevManualCommands::advance))))
            .then(Commands.literal("tool").then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevManualCommands::tool)))
            .then(Commands.literal("env").then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevManualCommands::env)))
            .then(Commands.literal("tartar").then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevManualCommands::tartar))));
    }

    private static String[] args(CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, "args").trim().split("\\s+");
    }

    private static BlockPos pos(String[] args) {
        return new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    private static T0DeviceBlockEntity device(CommandContext<CommandSourceStack> context, BlockPos pos) {
        if (context.getSource().getLevel().getBlockEntity(pos) instanceof T0DeviceBlockEntity device) {
            return device;
        }
        throw new IllegalArgumentException("No T0 device at " + pos.toShortString());
    }

    private static int show(CommandContext<CommandSourceStack> context) {
        T0DeviceBlockEntity device = device(context, pos(args(context)));
        report(context, device);
        return 1;
    }

    private static void report(CommandContext<CommandSourceStack> context, T0DeviceBlockEntity device) {
        DeviceContents contents = device.contents();
        reply(context, device.kind().id() + " caps=" + device.capabilities().keys());
        for (int i = 0; i < contents.itemSlots(); i++) {
            if (!contents.item(i).isEmpty()) {
                reply(context, "  item" + i + " " + stack(contents.item(i)));
            }
        }
        for (int i = 0; i < contents.tankCount(); i++) {
            if (!contents.tank(i).isEmpty()) {
                reply(context, "  tank" + i + " " + fluid(contents.tank(i)));
            }
        }
        for (int i = 0; i < contents.catalystSlots(); i++) {
            if (!contents.catalyst(i).isEmpty()) {
                reply(context, "  catalyst" + i + " " + stack(contents.catalyst(i)));
            }
        }
        reply(context, "  bar: " + device.describe().getString());
    }

    static String stack(ItemStack stack) {
        int b = Purity.bears(stack.getItem()) ? Purity.get(stack) : Purity.NONE;
        return PlatformTags.itemId(stack.getItem()) + " x" + stack.getCount() + (b == Purity.NONE ? "" : " @" + PurityScale.format(b) + "%")
            + (Fumigation.isMarked(stack) ? " [sulfured]" : "");
    }

    private static String fluid(FluidAmount fluid) {
        int b = Purity.bears(fluid.fluid()) ? Purity.get(fluid) : Purity.NONE;
        return PlatformTags.fluidId(fluid.fluid()) + " " + fluid.amount() + "mB" + (b == Purity.NONE ? "" : " @" + PurityScale.format(b) + "%");
    }

    private static int use(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String[] args = args(context);
        BlockPos pos = pos(args);
        ServerPlayer player = context.getSource().getPlayerOrException();
        T0DeviceBlockEntity device = device(context, pos);
        boolean sneak = args.length > 3 && args[3].equals("sneak");
        player.setShiftKeyDown(sneak);
        try {
            ItemStack held = player.getMainHandItem();
            var result = device.use(player, InteractionHand.MAIN_HAND, held, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
            reply(context, "use -> " + result + ", hand now " + stack(player.getMainHandItem()));
        } finally {
            player.setShiftKeyDown(false);
        }
        report(context, device(context, pos));
        return 1;
    }

    private static int advance(CommandContext<CommandSourceStack> context) {
        String[] args = args(context);
        BlockPos pos = pos(args);
        int ticks = Integer.parseInt(args[3]);
        for (int done = 0; done < ticks; done += CHUNK) {
            device(context, pos).runBatch(Math.min(CHUNK, ticks - done));
        }
        report(context, device(context, pos));
        return 1;
    }

    private static int tool(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String[] args = args(context);
        ServerPlayer player = context.getSource().getPlayerOrException();
        Inventory inventory = player.getInventory();
        int toolSlot = Integer.parseInt(args[0]);
        int targetSlot = Integer.parseInt(args[1]);
        int times = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        ItemStack tool = inventory.getItem(toolSlot);
        if (!(tool.getItem() instanceof HandToolItem item)) {
            throw new IllegalArgumentException("No hand tool in slot " + toolSlot);
        }
        // inventory menu: main inventory at 9–35, hotbar at 36–44
        Slot slot = player.inventoryMenu.getSlot(targetSlot < 9 ? 36 + targetSlot : targetSlot);
        for (int i = 0; i < times; i++) {
            item.overrideStackedOnOther(tool, slot, ClickAction.SECONDARY, player);
        }
        reply(context, "target now " + stack(slot.getItem()));
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                reply(context, "  inv" + i + " " + stack(inventory.getItem(i)));
            }
        }
        return 1;
    }

    private static int env(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        reply(context, "env " + Environment.at(level, pos(args(context))).keys());
        return 1;
    }

    private static int tartar(CommandContext<CommandSourceStack> context) {
        String[] args = args(context);
        BlockPos pos = pos(args);
        if (!(context.getSource().getLevel().getBlockEntity(pos) instanceof TankBlockEntity tank)) {
            throw new IllegalArgumentException("No tank at " + pos.toShortString());
        }
        int ticks = Integer.parseInt(args[3]);
        for (int done = 0; done < ticks; done += CHUNK) {
            tank.growTartar(Math.min(CHUNK, ticks - done));
        }
        reply(context, "tartar " + tank.tartar());
        return 1;
    }

    private static void reply(CommandContext<CommandSourceStack> context, String text) {
        context.getSource().sendSuccess(() -> Component.literal(text), false);
    }
}
