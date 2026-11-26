package com.tonywww.foodindustrial.platform.compat.jei;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//? if forge {
import mezz.jei.api.forge.ForgeTypes;
import net.minecraftforge.fluids.FluidStack;
//?} else {
/*import mezz.jei.api.neoforge.NeoForgeTypes;
import net.neoforged.neoforge.fluids.FluidStack;
*///?}
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}

/** JEI calls that differ between the nodes (JEI 15.74, 19.39, 29.53; I22). */
final class JeiSupport {
    /** Role of a recipe's catalyst slots: never encoded into patterns, found by uses lookups. */
    //? if >=26.1 {
    /*static final RecipeIngredientRole CATALYST = RecipeIngredientRole.CRAFTING_STATION;
    *///?} else {
    static final RecipeIngredientRole CATALYST = RecipeIngredientRole.CATALYST;
    //?}

    private JeiSupport() {
    }

    static IIngredientType<FluidStack> fluidType() {
        //? if forge {
        return ForgeTypes.FLUID_STACK;
        //?} else {
        /*return NeoForgeTypes.FLUID_STACK;
        *///?}
    }

    //? if >=26.1 {
    /*static Canvas canvas(GuiGraphicsExtractor graphics) {
        return new Canvas(graphics, Minecraft.getInstance().font);
    }
    *///?} else {
    static Canvas canvas(GuiGraphics graphics) {
        return new Canvas(graphics, Minecraft.getInstance().font);
    }
    //?}

    /** Adds fluids to a slot drawn as a 16 × 16 square filled to the largest amount. */
    static IRecipeSlotBuilder fluids(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y, List<FluidAmount> fluids) {
        List<FluidStack> stacks = new ArrayList<>();
        long capacity = 1;
        for (FluidAmount fluid : fluids) {
            stacks.add(PlatformFluids.toStack(fluid));
            capacity = Math.max(capacity, fluid.amount());
        }
        return builder.addSlot(role, x, y).addIngredients(fluidType(), stacks).setFluidRenderer(capacity, false, 16, 16);
    }

    /**
     * Gives {@code item} subtypes by {@code key} (an empty key: no subtype), for the ingredient list and for recipe
     * lookups alike (2026-11-20).
     */
    static void subtypes(mezz.jei.api.registration.ISubtypeRegistration registration, net.minecraft.world.item.Item item,
                         java.util.function.Function<ItemStack, String> key) {
        //? if >=26.1 {
        /*registration.registerSubtypeInterpreter(item, (stack, context) -> {
            String value = key.apply(stack);
            return value.isEmpty() ? null : value;
        });
        *///?} else if >=1.21 {
        /*registration.registerSubtypeInterpreter(item, new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>() {
            @Override
            public Object getSubtypeData(ItemStack stack, mezz.jei.api.ingredients.subtypes.UidContext context) {
                String value = key.apply(stack);
                return value.isEmpty() ? null : value;
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack stack, mezz.jei.api.ingredients.subtypes.UidContext context) {
                return key.apply(stack);
            }
        });
        *///?} else {
        registration.registerSubtypeInterpreter(item, (stack, context) -> key.apply(stack));
        //?}
    }

    static IRecipeSlotBuilder items(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y, List<ItemStack> items) {
        return builder.addSlot(role, x, y).addItemStacks(items);
    }

    /** Fluid shown in a slot, as the mod's amount type. */
    static Optional<FluidAmount> displayedFluid(IRecipeSlotView view) {
        return view.getDisplayedIngredient(fluidType()).map(PlatformFluids::fromStack);
    }

    /** Replaces what a slot shows (the output of the selected device). */
    static void showItem(IRecipeSlotDrawable slot, ItemStack stack) {
        slot.createDisplayOverrides().addIngredient(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, stack);
    }

    static void showFluid(IRecipeSlotDrawable slot, FluidAmount fluid) {
        slot.createDisplayOverrides().addIngredient(fluidType(), PlatformFluids.toStack(fluid));
    }
}
