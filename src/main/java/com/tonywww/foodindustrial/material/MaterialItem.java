package com.tonywww.foodindustrial.material;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.platform.PlatformItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Item of a solid material: material tooltip (formula, purity, Shift description). */
public class MaterialItem extends PlatformItem {
    private final MaterialDef material;

    public MaterialItem(Properties properties, MaterialDef material) {
        super(properties);
        this.material = material;
    }

    public MaterialDef material() {
        return material;
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        Tooltips.material(stack, material.formula(), Lang.description(material.id()), lines, shiftDown);
    }
}
