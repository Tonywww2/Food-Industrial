package com.tonywww.foodindustrial.platform.compat.jei;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.client.recipe.RecipeDevices;
import com.tonywww.foodindustrial.gui.client.recipe.RecipePage;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
//?}

/** One JEI category per recipe type R01–R19 (I22, additives/10 §5); the page itself is {@link RecipePage}. */
final class ProcessCategory implements IRecipeCategory<ProcessCategory.Entry> {
    /** A loaded recipe with its id. */
    record Entry(String id, ProcessRecipe recipe) {
        RecipePage page() {
            return RecipePage.of(id, recipe);
        }
    }

    private final ProcessType type;
    private final RecipeType<Entry> recipeType;
    private final IDrawable icon;

    ProcessCategory(ProcessType type, IGuiHelper guiHelper) {
        this.type = type;
        this.recipeType = RecipeType.create(FoodIndustrial.MOD_ID, type.id(), Entry.class);
        List<DeviceSpec> devices = RecipeDevices.of(type);
        ItemStack iconStack = devices.isEmpty() ? ItemStack.EMPTY : RecipeDevices.icon(devices.get(devices.size() - 1));
        this.icon = guiHelper.createDrawableItemStack(iconStack);
    }

    ProcessType type() {
        return type;
    }

    @Override
    public RecipeType<Entry> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Lang.jeiCategory(type.id()));
    }

    @Override
    public int getWidth() {
        return RecipePage.WIDTH;
    }

    @Override
    public int getHeight() {
        return RecipePage.HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Entry entry, IFocusGroup focuses) {
        RecipePage page = entry.page();
        for (RecipePage.Slot slot : page.slots()) {
            int x = slot.x() + 1;
            int y = slot.y() + 1;
            IRecipeSlotBuilder added = switch (slot.role()) {
                case INPUT -> slot.fluid()
                    ? JeiSupport.fluids(builder, RecipeIngredientRole.INPUT, x, y, page.inputFluids(slot.index()))
                    : JeiSupport.items(builder, RecipeIngredientRole.INPUT, x, y, page.inputItems(slot.index()));
                case CATALYST -> JeiSupport.items(builder, JeiSupport.CATALYST, x, y, page.catalystItems(slot.index()));
                case OUTPUT -> slot.fluid()
                    ? JeiSupport.fluids(builder, RecipeIngredientRole.OUTPUT, x, y, List.of(page.outputFluid(slot.index())))
                    : JeiSupport.items(builder, RecipeIngredientRole.OUTPUT, x, y, List.of(page.outputItem(slot.index())));
            };
            added.addRichTooltipCallback((view, tooltip) -> {
                if (slot.fluid()) {
                    JeiSupport.displayedFluid(view).ifPresent(fluid -> {
                        List<Component> lines = new ArrayList<>();
                        Tooltips.purityLine(Purity.materialOf(fluid.fluid()), Purity.get(fluid), lines);
                        tooltip.addAll(lines);
                    });
                }
                tooltip.addAll(page.slotLines(slot));
            });
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, Entry entry, IFocusGroup focuses) {
        RecipePage page = entry.page();
        List<IRecipeSlotDrawable> outputs = builder.getRecipeSlots().getSlots(RecipeIngredientRole.OUTPUT);
        builder.addInputHandler(new IJeiInputHandler() {
            @Override
            public ScreenRectangle getArea() {
                return new ScreenRectangle(0, 0, RecipePage.WIDTH, RecipePage.HEIGHT);
            }

            @Override
            public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
                InputConstants.Key key = input.getKey();
                if (key.getType() != InputConstants.Type.MOUSE || key.getValue() != 0) {
                    return false;
                }
                if (input.isSimulate()) {
                    return page.devices().size() > 1 && page.deviceAt(mouseX, mouseY) >= 0;
                }
                DeviceSpec before = page.selectedDevice();
                if (!page.click(mouseX, mouseY)) {
                    return false;
                }
                refresh(page, outputs);
                if (page.selectedDevice() != before) {
                    reshow(entry, focuses);
                }
                return true;
            }
        });
    }

    /**
     * Shows the recipe again after the device changed, so JEI builds its slots anew: transfers such
     * as AE2's pattern encoding read the slots as built, not the outputs shown over them (I24,
     * 2026-11-11).
     */
    private void reshow(Entry entry, IFocusGroup focuses) {
        mezz.jei.api.runtime.IJeiRuntime runtime = FoodIndustrialJeiPlugin.runtime();
        if (runtime == null) {
            return;
        }
        List<mezz.jei.api.recipe.IFocus<?>> list = new ArrayList<>(focuses.getAllFocuses());
        net.minecraft.client.Minecraft.getInstance().execute(() -> runtime.getRecipesGui().showRecipes(this, List.of(entry), list));
    }

    /**
     * JEI clears display overrides whenever the slots cycle and then asks the category again: the
     * outputs of the selected device are shown again each time.
     */
    @Override
    public void onDisplayedIngredientsUpdate(Entry entry, List<IRecipeSlotDrawable> slots, IFocusGroup focuses) {
        List<IRecipeSlotDrawable> outputs = new ArrayList<>();
        for (IRecipeSlotDrawable slot : slots) {
            if (slot.getRole() == RecipeIngredientRole.OUTPUT) {
                outputs.add(slot);
            }
        }
        refresh(entry.page(), outputs);
    }

    /** Shows the outputs of the selected device. */
    private static void refresh(RecipePage page, List<IRecipeSlotDrawable> outputs) {
        int index = 0;
        for (RecipePage.Slot slot : page.slots()) {
            if (slot.role() != RecipePage.Role.OUTPUT || index >= outputs.size()) {
                continue;
            }
            IRecipeSlotDrawable drawable = outputs.get(index++);
            drawable.clearDisplayOverrides();
            if (slot.fluid()) {
                FluidAmount fluid = page.outputFluid(slot.index());
                JeiSupport.showFluid(drawable, fluid);
            } else {
                JeiSupport.showItem(drawable, page.outputItem(slot.index()));
            }
        }
    }

    //? if >=26.1 {
    /*@Override
    public void draw(Entry entry, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        entry.page().draw(JeiSupport.canvas(graphics), mouseX, mouseY);
    }

    @Override
    public Identifier getRegistryName(Entry entry) {
        return com.tonywww.foodindustrial.platform.PlatformIdentifier.parse(entry.id());
    }
    *///?} else {
    @Override
    public void draw(Entry entry, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        entry.page().draw(JeiSupport.canvas(graphics), mouseX, mouseY);
    }

    @Override
    public ResourceLocation getRegistryName(Entry entry) {
        return com.tonywww.foodindustrial.platform.PlatformIdentifier.parse(entry.id());
    }
    //?}

    @Override
    public void getTooltip(ITooltipBuilder tooltip, Entry entry, IRecipeSlotsView slots, double mouseX, double mouseY) {
        tooltip.addAll(entry.page().tooltip(mouseX, mouseY));
    }
}
