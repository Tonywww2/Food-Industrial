package com.tonywww.foodindustrial.devtest;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.MachinePerf;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.machine.entity.PortBlockEntity;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Development commands for the machine framework (I09): {@code /fidev machine <pos>} (state),
 * {@code /fidev machine <pos> energy <FE>}, {@code /fidev machine <pos> side <face> <item|fluid> <mode>},
 * {@code /fidev machine <pos> auto <in|out> <item|fluid> <on|off>}, {@code /fidev machine <pos> port <mode> <auto>}
 * and {@code /foodindustrial perf <start|stop|report>} (machines/02 §6.4 rule 15). Screens (I10):
 * {@code set gui <player>} opens the machine's or port's screen, {@code set action <player> <code> <a> <b>}
 * applies a menu action like the client would, {@code set carried <player> <item> <count>} puts a stack on
 * the cursor, {@code set tool <player> <wrench|redprint> <plain|shift>} uses a tool on the machine, {@code set use <player>} and
 * {@code set useair <player> [shift]} right-click with the held item on {@code <pos>} or in the air.
 */
final class DevMachineCommands {
    private DevMachineCommands() {
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fidev").then(Commands.literal("machine")
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(DevMachineCommands::show)
                .then(Commands.literal("energy").then(Commands.argument("fe", IntegerArgumentType.integer(0)).executes(DevMachineCommands::energy)))
                .then(Commands.literal("set").then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevMachineCommands::set))))));
        dispatcher.register(Commands.literal("foodindustrial").then(Commands.literal("perf")
            .then(Commands.literal("start").executes(context -> {
                MachinePerf.start(context.getSource().getLevel().getGameTime());
                reply(context, "machine perf started");
                return 1;
            }))
            .then(Commands.literal("stop").executes(context -> {
                MachinePerf.stop();
                reply(context, "machine perf stopped");
                return 1;
            }))
            .then(Commands.literal("report").executes(context -> {
                for (String line : MachinePerf.report(context.getSource().getLevel().getGameTime())) {
                    reply(context, line);
                }
                return 1;
            }))));
    }

    private static BlockEntity at(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        return context.getSource().getLevel().getBlockEntity(pos);
    }

    private static int show(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockEntity blockEntity = at(context);
        if (blockEntity instanceof com.tonywww.foodindustrial.logistics.TankBlockEntity tank) {
            StringBuilder line = new StringBuilder(tank.kind().id() + " own=" + describe(tank.fluid()) + " draining=" + tank.draining() + " column=");
            for (com.tonywww.foodindustrial.logistics.TankBlockEntity member : tank.column()) {
                line.append('[').append(member.getBlockPos().getY()).append(':').append(describe(member.fluid())).append(']');
            }
            reply(context, line.toString());
            return 1;
        }
        if (blockEntity instanceof PortBlockEntity port) {
            reply(context, "port " + port.kind() + " mode=" + port.mode() + " auto=" + port.autoOutput()
                + " controller=" + (port.controller() == null ? "none" : port.controller().getBlockPos().toShortString()));
            return 1;
        }
        if (!(blockEntity instanceof MachineBlockEntity machine)) {
            reply(context, "no machine here");
            return 0;
        }
        StringBuilder line = new StringBuilder(machine.definition().id() + " status=" + machine.status().id() + " facing=" + machine.facing()
            + " active=" + machine.getBlockState().getValue(com.tonywww.foodindustrial.machine.block.MachineBlock.ACTIVE));
        if (machine instanceof ProcessingMachineBlockEntity processing) {
            var current = processing.processor().current();
            line.append(" recipe=").append(current == null ? "none" : current.id()).append(" progress=").append(processing.processor().progressTicks());
            if (processing.processor().missing() != null) {
                line.append(" missing=").append(processing.processor().missing());
            }
        }
        if (machine instanceof MultiblockControllerBlockEntity controller) {
            line.append(" formed=").append(controller.formed()).append(" ports=").append(controller.ports().size())
                .append(" wrong=").append(controller.wrongBlocks().size());
        }
        if (machine.energy() != null) {
            line.append(" energy=").append(machine.energy().getEnergy());
        }
        if (machine.heat() != null) {
            line.append(" burn=").append(machine.heat().burnLeft()).append('/').append(machine.heat().burnTotal());
        }
        reply(context, line.toString());
        StringBuilder config = new StringBuilder("  config default=" + machine.hasDefaultConfig() + " redstone=" + machine.redstone().id()
            + " mode=" + machine.modeIndex() + " sides=");
        for (SideConfig.Face face : SideConfig.Face.values()) {
            config.append(face.name().charAt(0)).append(':').append(machine.sides().items(face).ordinal()).append(machine.sides().fluids(face).ordinal())
                .append(machine.sides().itemScope(face).ordinal()).append(' ');
        }
        config.append("auto=").append(machine.sides().autoInputItems() ? "i" : "-").append(machine.sides().autoInputFluids() ? "I" : "-")
            .append(machine.sides().autoOutputItems() ? "o" : "-").append(machine.sides().autoOutputFluids() ? "O" : "-");
        reply(context, config.toString());
        for (int slot = 0; slot < machine.inventory().size(); slot++) {
            if (!machine.inventory().lock(slot).isEmpty()) {
                reply(context, "  lock " + slot + ": " + PlatformTags.itemId(machine.inventory().lock(slot).getItem()));
            }
        }
        for (int tank = 0; tank < machine.tanks().size(); tank++) {
            if (!machine.tanks().lock(tank).isEmpty()) {
                reply(context, "  tank lock " + tank + ": " + PlatformTags.fluidId(machine.tanks().lock(tank).fluid()));
            }
        }
        for (int slot = 0; slot < machine.inventory().size(); slot++) {
            ItemStack stack = machine.inventory().get(slot);
            if (!stack.isEmpty()) {
                int b = Purity.get(stack);
                reply(context, "  " + machine.inventory().role(slot) + " " + slot + ": " + stack.getCount() + " " + PlatformTags.itemId(stack.getItem())
                    + (b == Purity.NONE ? "" : " " + PurityScale.format(b) + "%"));
            }
        }
        for (int tank = 0; tank < machine.tanks().size(); tank++) {
            FluidAmount fluid = machine.tanks().get(tank);
            if (!fluid.isEmpty()) {
                reply(context, "  tank " + tank + " " + machine.tanks().role(tank) + ": " + fluid.amount() + " mB " + PlatformTags.fluidId(fluid.fluid())
                    + (fluid.hasPurity() ? " " + PurityScale.format(fluid.purity()) + "%" : ""));
            }
        }
        return 1;
    }

    private static int energy(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (at(context) instanceof MachineBlockEntity machine && machine.energy() != null) {
            machine.energy().setEnergy(IntegerArgumentType.getInteger(context, "fe"));
            reply(context, "energy " + machine.energy().getEnergy());
            return 1;
        }
        reply(context, "no FE machine here");
        return 0;
    }

    /** {@code side <face> <item|fluid> <off|input|output|both>}, {@code auto <in|out> <item|fluid> <on|off>}, {@code port <mode> <on|off>}, {@code redstone <mode>}. */
    private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String[] args = StringArgumentType.getString(context, "args").trim().split("\\s+");
        BlockEntity blockEntity = at(context);
        if (args[0].equals("gui") || args[0].equals("action") || args[0].equals("carried") || args[0].equals("tool") || args[0].equals("use") || args[0].equals("useair")) {
            return screen(context, blockEntity, args);
        }
        if (args[0].equals("port") && blockEntity instanceof PortBlockEntity port) {
            port.configure(SideConfig.Mode.valueOf(args[1].toUpperCase()), args.length > 2 && args[2].equals("on"));
            reply(context, "port " + port.mode() + " auto=" + port.autoOutput());
            return 1;
        }
        if (!(blockEntity instanceof MachineBlockEntity machine)) {
            reply(context, "no machine here");
            return 0;
        }
        switch (args[0]) {
            case "side" -> {
                SideConfig.Face face = SideConfig.Face.valueOf(args[1].toUpperCase());
                SideConfig.Mode mode = SideConfig.Mode.valueOf(args[3].toUpperCase());
                if (args[2].equals("fluid")) {
                    machine.sides().setFluids(face, mode);
                } else {
                    machine.sides().setItems(face, mode);
                }
            }
            case "auto" -> machine.sides().setAuto(args[1].equals("in"), args[2].equals("fluid"), args[3].equals("on"));
            case "redstone" -> machine.setRedstone(com.tonywww.foodindustrial.machine.component.RedstoneControl.valueOf(args[1].toUpperCase()));
            case "item" -> {
                ItemStack stack = new ItemStack(PlatformTags.item(args[2]).orElseThrow(), Integer.parseInt(args[3]));
                if (args.length > 4) {
                    stack = Purity.with(stack, PurityScale.ofPercent(Double.parseDouble(args[4])));
                }
                machine.inventory().set(Integer.parseInt(args[1]), stack);
                reply(context, "slot " + args[1] + " set");
                return 1;
            }
            case "fluid" -> {
                FluidAmount fluid = FluidAmount.of(PlatformTags.fluid(args[2]).orElseThrow(), Integer.parseInt(args[3]));
                if (args.length > 4) {
                    fluid = Purity.with(fluid, PurityScale.ofPercent(Double.parseDouble(args[4])));
                }
                machine.tanks().set(Integer.parseInt(args[1]), fluid);
                reply(context, "tank " + args[1] + " set");
                return 1;
            }
            case "mode" -> machine.setMode(Integer.parseInt(args[1]));
            default -> {
                reply(context, "unknown setting " + args[0]);
                return 0;
            }
        }
        machine.sidesChanged();
        reply(context, "ok");
        return 1;
    }

    private static int screen(CommandContext<CommandSourceStack> context, BlockEntity blockEntity, String[] args) throws CommandSyntaxException {
        net.minecraft.server.level.ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayerByName(args[1]);
        if (player == null) {
            reply(context, "no player " + args[1]);
            return 0;
        }
        switch (args[0]) {
            case "gui" -> {
                if (blockEntity instanceof MachineBlockEntity machine) {
                    com.tonywww.foodindustrial.gui.ModMenus.open(player, machine);
                } else if (blockEntity instanceof PortBlockEntity port) {
                    com.tonywww.foodindustrial.gui.ModMenus.open(player, port);
                }
            }
            case "action" -> {
                if (player.containerMenu instanceof com.tonywww.foodindustrial.gui.FIMenu menu) {
                    menu.runAction(player, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                    menu.broadcastChanges();
                } else {
                    reply(context, "no machine screen open");
                    return 0;
                }
            }
            case "carried" -> player.containerMenu.setCarried(args[2].equals("empty") ? ItemStack.EMPTY
                : new ItemStack(PlatformTags.item(args[2]).orElseThrow(), Integer.parseInt(args[3])));
            case "use" -> {
                // a right click with the main hand item on the top face of <pos>, as the server handles it
                BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                net.minecraft.world.phys.BlockHitResult hit = new net.minecraft.world.phys.BlockHitResult(
                    net.minecraft.world.phys.Vec3.atCenterOf(pos).add(0, 0.5, 0), net.minecraft.core.Direction.UP, pos, false);
                player.setShiftKeyDown(args.length > 2 && args[2].equals("shift"));
                reply(context, "use " + player.gameMode.useItemOn(player, player.level(), player.getMainHandItem(),
                    net.minecraft.world.InteractionHand.MAIN_HAND, hit));
                player.setShiftKeyDown(false);
            }
            case "useair" -> {
                // a right click in the air with the main hand item, sneaking when "shift" is given
                player.setShiftKeyDown(args.length > 2 && args[2].equals("shift"));
                reply(context, "use " + player.gameMode.useItem(player, player.level(), player.getMainHandItem(), net.minecraft.world.InteractionHand.MAIN_HAND));
                player.setShiftKeyDown(false);
            }
            case "tool" -> {
                if (!(blockEntity instanceof MachineBlockEntity machine)) {
                    reply(context, "no machine here");
                    return 0;
                }
                ItemStack held = player.getMainHandItem();
                if (!(held.getItem() instanceof com.tonywww.foodindustrial.machine.tool.MachineTool tool)) {
                    reply(context, "hold a machine tool");
                    return 0;
                }
                reply(context, "tool " + tool.useOnMachine(player.level(), machine, player, held, args.length > 3 && args[3].equals("shift")));
            }
            default -> {
            }
        }
        reply(context, "ok " + args[0]);
        return 1;
    }

    private static String describe(FluidAmount fluid) {
        return fluid.isEmpty() ? "empty" : fluid.amount() + " " + PlatformTags.fluidId(fluid.fluid())
            + (fluid.hasPurity() ? " " + PurityScale.format(fluid.purity()) + "%" : "");
    }

    private static void reply(CommandContext<CommandSourceStack> context, String text) {
        context.getSource().sendSuccess(() -> Component.literal(text), false);
    }
}
