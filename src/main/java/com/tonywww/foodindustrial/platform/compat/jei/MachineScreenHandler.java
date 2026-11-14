package com.tonywww.foodindustrial.platform.compat.jei;

import com.tonywww.foodindustrial.gui.MenuActions;
import com.tonywww.foodindustrial.gui.client.MachineScreen;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.ProcessType;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Machine screens with JEI (I22, machines/04): the side panels are extra areas JEI keeps clear, the
 * progress arrow opens the machine's recipe types, and dropping an item or fluid from JEI on an input
 * slot or tank locks it (the JEI form of Alt + click).
 */
final class MachineScreenHandler implements IGuiContainerHandler<MachineScreen>, IGhostIngredientHandler<MachineScreen> {
    private final Map<ProcessType, ProcessCategory> categories;

    MachineScreenHandler(Map<ProcessType, ProcessCategory> categories) {
        this.categories = categories;
    }

    @Override
    public List<Rect2i> getGuiExtraAreas(MachineScreen screen) {
        List<Rect2i> areas = new ArrayList<>();
        for (int[] area : screen.extraAreas()) {
            areas.add(new Rect2i(area[0], area[1], area[2], area[3]));
        }
        return areas;
    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(MachineScreen screen, double mouseX, double mouseY) {
        int[] arrow = screen.arrowArea();
        if (arrow == null) {
            return List.of();
        }
        List<RecipeType<?>> types = new ArrayList<>();
        for (ProcessType type : screen.getMenu().definition().types()) {
            ProcessCategory category = categories.get(type);
            if (category != null) {
                types.add(category.getRecipeType());
            }
        }
        if (types.isEmpty()) {
            return List.of();
        }
        return List.of(IGuiClickableArea.createBasic(arrow[0], arrow[1], arrow[2], arrow[3], types.toArray(new RecipeType<?>[0])));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I> List<Target<I>> getTargetsTyped(MachineScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
            for (int[] slot : screen.lockSlots()) {
                Rect2i area = new Rect2i(screen.left() + slot[1], screen.top() + slot[2], 18, 18);
                targets.add((Target<I>) new Target<ItemStack>() {
                    @Override
                    public Rect2i getArea() {
                        return area;
                    }

                    @Override
                    public void accept(ItemStack stack) {
                        screen.getMenu().send(MenuActions.LOCK_SLOT_ITEM, slot[0], PlatformTags.itemRawId(stack.getItem()));
                    }
                });
            }
        } else if (ingredient.getType() == JeiSupport.fluidType()) {
            for (int[] tank : screen.lockTanks()) {
                Rect2i area = new Rect2i(screen.left() + tank[1], screen.top() + tank[2], tank[3], tank[4]);
                targets.add(new Target<I>() {
                    @Override
                    public Rect2i getArea() {
                        return area;
                    }

                    @Override
                    public void accept(I fluid) {
                        int id = PlatformTags.fluidRawId(PlatformFluids.fromStack(JeiSupport.fluidType().getIngredientClass().cast(fluid)).fluid());
                        screen.getMenu().send(MenuActions.LOCK_TANK_FLUID, tank[0], id);
                    }
                });
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {
    }
}
