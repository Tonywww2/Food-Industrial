package com.tonywww.foodindustrial.machine.tool;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformItem;
import com.tonywww.foodindustrial.platform.PlatformItemData;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Device wrench (machines/04 §4, 2026-11-05): right click turns a machine 90° clockwise; Shift + right
 * click dismantles it. The machine's contents drop; settings that are not default stay on the item and
 * come back when it is placed.
 */
public class DeviceWrenchItem extends PlatformItem implements MachineTool {
    public static final String ID = "device_wrench";

    public DeviceWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return MachineTool.useOn(this, context);
    }

    @Override
    public PlatformEntityBlock.UseResult useOnMachine(Level level, MachineBlockEntity machine, Player player, ItemStack stack, boolean secondary) {
        if (!MachineTool.mayEdit(player)) {
            return PlatformEntityBlock.UseResult.PASS;
        }
        if (level.isClientSide()) {
            return PlatformEntityBlock.UseResult.SUCCESS;
        }
        if (secondary) {
            ItemStack drop = new ItemStack(machine.definition().item());
            if (!machine.hasDefaultConfig()) {
                PlatformItemData.write(drop, MachineBlockEntity.CONFIG_ITEM_KEY, level.registryAccess(), machine::saveConfig);
            }
            level.removeBlock(machine.getBlockPos(), false);
            player.getInventory().placeItemBackInInventory(drop);
        } else {
            machine.rotate();
        }
        return PlatformEntityBlock.UseResult.SUCCESS;
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        Tooltips.machine(Lang.description(ID), lines, shiftDown);
    }
}
