package com.tonywww.foodindustrial.world.interaction;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformTankItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Liquid bottle (additives/07 H15, 2026-11-10): 250 mB of one liquid, what harvesting collects. Pours
 * into pots, crocks and tanks like a bucket (4 bottles = 1 bucket) and can be filled again.
 */
public class FluidBottleItem extends PlatformTankItem {
    public static final String ID = "fluid_bottle";
    public static final int CAPACITY = 250;

    public FluidBottleItem(Properties properties) {
        super(properties, CAPACITY, fluid -> !PlatformFluids.isGas(fluid));
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        FluidAmount contents = PlatformFluidContainers.contained(stack);
        if (!contents.isEmpty()) {
            lines.add(PlatformFluids.displayName(contents).copy().append(" ").append(Component.translatable(GuiText.key("tank", "amount"),
                String.valueOf(contents.amount()), String.valueOf(CAPACITY))).withStyle(ChatFormatting.GRAY));
            if (Purity.bears(contents.fluid())) {
                Tooltips.purityLine(Purity.materialOf(contents.fluid()), Purity.get(contents), lines);
            }
        }
        Tooltips.machine(Lang.description(ID), lines, shiftDown);
    }
}
