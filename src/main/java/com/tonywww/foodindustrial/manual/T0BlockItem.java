package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.platform.PlatformBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** Item of a T0 device block: the Shift description (text standard §3.2). */
public class T0BlockItem extends PlatformBlockItem {
    private final String id;

    public T0BlockItem(Block block, Properties properties, String id) {
        super(block, properties);
        this.id = id;
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        Tooltips.machine(Lang.description(id), lines, shiftDown);
    }
}
