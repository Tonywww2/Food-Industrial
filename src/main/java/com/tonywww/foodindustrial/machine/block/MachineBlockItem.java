package com.tonywww.foodindustrial.machine.block;

import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformBlockItem;
import com.tonywww.foodindustrial.platform.PlatformItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** Item of a machine: says when it carries settings from a dismantled machine (machines/04 §4). */
public class MachineBlockItem extends PlatformBlockItem {
    public MachineBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        if (PlatformItemData.has(stack, MachineBlockEntity.CONFIG_ITEM_KEY)) {
            lines.add(Component.translatable(GuiText.key("machine", "configured")).withStyle(ChatFormatting.GRAY));
        }
    }
}
