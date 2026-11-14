package com.tonywww.foodindustrial.platform.compat.jei;

import com.tonywww.foodindustrial.platform.client.PlatformHud;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

/** Keeps JEI's item list clear of the metabolism panel beside the survival inventory (I35). */
final class InventoryPanelHandler implements IGuiContainerHandler<InventoryScreen> {
    @Override
    public List<Rect2i> getGuiExtraAreas(InventoryScreen screen) {
        int[] area = PlatformHud.inventoryPanelArea(screen);
        return area == null ? List.of() : List.of(new Rect2i(area[0], area[1], area[2], area[3]));
    }
}
