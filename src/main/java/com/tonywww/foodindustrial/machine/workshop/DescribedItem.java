package com.tonywww.foodindustrial.machine.workshop;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.platform.PlatformItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** A plain item with a Shift description (text standard §3.2): machine modules. */
public class DescribedItem extends PlatformItem {
    private final String id;

    public DescribedItem(Properties properties, String id) {
        super(properties);
        this.id = id;
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        Tooltips.machine(Lang.description(id), lines, shiftDown);
    }
}
