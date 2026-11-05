package com.tonywww.foodindustrial.logistics;

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
 * Gas cylinder (machines/03 §4–5): 1,000 mB of one gas, does not stack; filled and emptied at machine
 * tanks, tank blocks and other mods' fluid handlers. Gases cannot be placed in the world.
 */
public class GasCylinderItem extends PlatformTankItem {
    public static final String ID = "gas_cylinder";
    public static final int CAPACITY = 1000;

    public GasCylinderItem(Properties properties) {
        super(properties, CAPACITY, PlatformFluids::isGas);
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
