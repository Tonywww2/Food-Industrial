package com.tonywww.foodindustrial.devtest;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.platform.PlatformItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Development-only purity bearer with a material tooltip. */
public class PurityTestItem extends PlatformItem {
    public PurityTestItem(Properties properties) {
        super(properties);
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        Tooltips.material(stack, "CaSO4·2H2O", Lang.description("purity_test"), lines, shiftDown);
    }
}
