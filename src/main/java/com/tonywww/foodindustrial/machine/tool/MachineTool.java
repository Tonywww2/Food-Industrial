package com.tonywww.foodindustrial.machine.tool;

import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * An item that acts on machines (machines/04 §4). Right click reaches it through the machine block
 * (which gives tools priority over opening the screen); Shift + right click skips the block and
 * reaches the item's {@code useOn}. Both call {@link #useOnMachine}.
 */
public interface MachineTool {
    /** Runs on both sides; changes happen on the server. */
    PlatformEntityBlock.UseResult useOnMachine(Level level, MachineBlockEntity machine, Player player, ItemStack stack, boolean secondary);

    /** {@code useOn} of a tool item: machines only. */
    static InteractionResult useOn(MachineTool tool, UseOnContext context) {
        if (context.getPlayer() == null || !(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof MachineBlockEntity machine)) {
            return InteractionResult.PASS;
        }
        return switch (tool.useOnMachine(context.getLevel(), machine, context.getPlayer(), context.getItemInHand(), context.isSecondaryUseActive())) {
            case SUCCESS -> InteractionResult.SUCCESS;
            case CONSUME -> InteractionResult.CONSUME;
            case FAIL -> InteractionResult.FAIL;
            case PASS, PASS_TO_ITEM -> InteractionResult.PASS;
        };
    }

    /** Whether the player may change blocks (not in adventure or spectator mode). */
    static boolean mayEdit(Player player) {
        return player.getAbilities().mayBuild;
    }
}
