package com.tonywww.foodindustrial.machine.workshop;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.platform.PlatformBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** Item of a machine attachment with a Shift description (text standard §3.2): hand crank, condenser. */
public class DescribedBlockItem extends PlatformBlockItem {
    private final String id;

    public DescribedBlockItem(Block block, Properties properties, String id) {
        super(block, properties);
        this.id = id;
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        Tooltips.machine(Lang.description(id), lines, shiftDown);
    }
}
