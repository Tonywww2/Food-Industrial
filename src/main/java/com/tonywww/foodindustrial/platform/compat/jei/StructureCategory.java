package com.tonywww.foodindustrial.platform.compat.jei;

import com.mojang.blaze3d.platform.InputConstants;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.gui.RecipeText;
import com.tonywww.foodindustrial.gui.client.recipe.StructurePage;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}

/** Multiblock structure pages, one per multiblock machine, layer by layer (I22, 2026-11-11). */
final class StructureCategory implements IRecipeCategory<MachineDefinition> {
    static final RecipeType<MachineDefinition> TYPE = RecipeType.create(FoodIndustrial.MOD_ID, RecipeText.STRUCTURE_CATEGORY, MachineDefinition.class);
    private final IDrawable icon;

    StructureCategory(IGuiHelper guiHelper, ItemStack icon) {
        this.icon = guiHelper.createDrawableItemStack(icon);
    }

    @Override
    public RecipeType<MachineDefinition> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Lang.jeiCategory(RecipeText.STRUCTURE_CATEGORY));
    }

    @Override
    public int getWidth() {
        return StructurePage.WIDTH;
    }

    @Override
    public int getHeight() {
        return StructurePage.HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineDefinition machine, IFocusGroup focuses) {
        StructurePage page = StructurePage.of(machine);
        List<StructurePage.Need> needs = page.needs();
        for (int i = 0; i < needs.size(); i++) {
            StructurePage.Need need = needs.get(i);
            JeiSupport.items(builder, i < page.casingKinds() ? RecipeIngredientRole.INPUT : JeiSupport.CATALYST,
                need.x() + 1, need.y() + 1, List.of(need.stack()));
        }
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(new ItemStack(machine.item()));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MachineDefinition machine, IFocusGroup focuses) {
        StructurePage page = StructurePage.of(machine);
        builder.addInputHandler(new IJeiInputHandler() {
            @Override
            public ScreenRectangle getArea() {
                return new ScreenRectangle(0, 0, StructurePage.WIDTH, StructurePage.HEIGHT);
            }

            @Override
            public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
                InputConstants.Key key = input.getKey();
                if (key.getType() != InputConstants.Type.MOUSE || key.getValue() != 0) {
                    return false;
                }
                if (input.isSimulate()) {
                    return mouseY >= 0 && mouseY < 11 && (mouseX < 11 || mouseX >= StructurePage.NEXT_X && mouseX < StructurePage.NEXT_X + 11);
                }
                return page.click(mouseX, mouseY);
            }
        });
    }

    //? if >=26.1 {
    /*@Override
    public void draw(MachineDefinition machine, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        StructurePage.of(machine).draw(JeiSupport.canvas(graphics), mouseX, mouseY);
    }
    *///?} else {
    @Override
    public void draw(MachineDefinition machine, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        StructurePage.of(machine).draw(JeiSupport.canvas(graphics), mouseX, mouseY);
    }
    //?}

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MachineDefinition machine, IRecipeSlotsView slots, double mouseX, double mouseY) {
        tooltip.addAll(StructurePage.of(machine).tooltip(mouseX, mouseY));
    }
}
