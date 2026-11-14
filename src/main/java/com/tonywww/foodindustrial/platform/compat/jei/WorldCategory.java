package com.tonywww.foodindustrial.platform.compat.jei;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.client.recipe.WorldPage;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}

/** The two world interaction categories, W01 block transformation and W02 harvesting (I22). */
final class WorldCategory implements IRecipeCategory<WorldPage> {
    private final String id;
    private final RecipeType<WorldPage> recipeType;
    private final IDrawable icon;

    WorldCategory(String id, ItemStack icon, IGuiHelper guiHelper) {
        this.id = id;
        this.recipeType = RecipeType.create(FoodIndustrial.MOD_ID, id, WorldPage.class);
        this.icon = guiHelper.createDrawableItemStack(icon);
    }

    @Override
    public RecipeType<WorldPage> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Lang.jeiCategory(id));
    }

    @Override
    public int getWidth() {
        return WorldPage.WIDTH;
    }

    @Override
    public int getHeight() {
        return WorldPage.HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WorldPage page, IFocusGroup focuses) {
        JeiSupport.items(builder, RecipeIngredientRole.INPUT, WorldPage.BLOCK_X + 1, 1, page.blocks());
        if (page.harvesting()) {
            JeiSupport.items(builder, JeiSupport.CATALYST, WorldPage.TOOL_X + 1, 1, page.tools())
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(WorldPage.toolLine()));
            if (!page.containers().isEmpty()) {
                JeiSupport.items(builder, RecipeIngredientRole.INPUT, WorldPage.CONTAINER_X + 1, 1, page.containers())
                    .addRichTooltipCallback((view, tooltip) -> tooltip.add(WorldPage.containerLine()));
            }
            FluidAmount fluid = page.resultFluid();
            if (!fluid.isEmpty()) {
                JeiSupport.fluids(builder, RecipeIngredientRole.OUTPUT, WorldPage.RESULT_X + 1, 1, List.of(fluid))
                    .addRichTooltipCallback((view, tooltip) -> JeiSupport.displayedFluid(view).ifPresent(shown -> {
                        List<Component> lines = new ArrayList<>();
                        Tooltips.purityLine(Purity.materialOf(shown.fluid()), Purity.get(shown), lines);
                        tooltip.addAll(lines);
                    }));
            } else {
                JeiSupport.items(builder, RecipeIngredientRole.OUTPUT, WorldPage.RESULT_X + 1, 1, List.of(page.resultItem()));
            }
        } else {
            JeiSupport.items(builder, RecipeIngredientRole.OUTPUT, WorldPage.RESULT_X + 1, 1, List.of(page.resultBlock()));
        }
    }

    //? if >=26.1 {
    /*@Override
    public void draw(WorldPage page, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        page.draw(JeiSupport.canvas(graphics), mouseX, mouseY);
    }
    *///?} else {
    @Override
    public void draw(WorldPage page, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        page.draw(JeiSupport.canvas(graphics), mouseX, mouseY);
    }
    //?}

    @Override
    public void getTooltip(ITooltipBuilder tooltip, WorldPage page, IRecipeSlotsView slots, double mouseX, double mouseY) {
        tooltip.addAll(page.tooltip(mouseX, mouseY));
    }
}
