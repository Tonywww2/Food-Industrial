package com.tonywww.foodindustrial.platform.compat.jei;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.gui.RecipeInfo;
import com.tonywww.foodindustrial.gui.client.MachineScreen;
import com.tonywww.foodindustrial.gui.client.recipe.RecipeDevices;
import com.tonywww.foodindustrial.gui.client.recipe.RecipePage;
import com.tonywww.foodindustrial.gui.client.recipe.WorldPage;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import com.tonywww.foodindustrial.platform.recipe.PlatformVanillaCooking;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.world.interaction.BlockTransformRecipe;
import com.tonywww.foodindustrial.world.interaction.HarvestRecipe;
import com.tonywww.foodindustrial.world.interaction.WorldInteractions;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//? if >=26.1 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

/**
 * JEI integration (I22, additives/10 §5): one category per recipe type R01–R19, the two world
 * interactions and the multiblock structures; every device that runs a type is its catalyst; machine
 * screens get the transfer button, the arrow click area, extra areas and drop-to-lock; information
 * pages say where raw materials come from; the bridge recipes on vanilla blocks are hidden from the
 * vanilla categories (they show in R07–R09 with the vanilla blocks as catalysts).
 */
@JeiPlugin
public final class FoodIndustrialJeiPlugin implements IModPlugin {
    /*
     * JEI 15 on Forge 1.20.1 starts when the recipes arrive, before the tags; the mod's recipes name
     * tags, so they are added at run time once the tags are in (I22, 2026-11-11). From 1.20.2 on tags
     * come first and registration adds them directly.
     */
    private static volatile IJeiRuntime runtime;
    private static volatile FoodIndustrialJeiPlugin pending;

    static {
        com.tonywww.foodindustrial.platform.PlatformEvents.onClientTagsReceived(() -> {
            FoodIndustrialJeiPlugin plugin = pending;
            IJeiRuntime current = runtime;
            if (plugin != null && current != null) {
                pending = null;
                Minecraft.getInstance().execute(() -> plugin.addRecipes(current.getRecipeManager()));
            }
        });
    }

    private final Map<ProcessType, ProcessCategory> categories = new LinkedHashMap<>();
    private WorldCategory transforms;
    private WorldCategory harvests;

    //? if >=26.1 {
    /*@Override
    public Identifier getPluginUid() {
        return PlatformIdentifier.id("jei");
    }
    *///?} else {
    @Override
    public ResourceLocation getPluginUid() {
        return PlatformIdentifier.id("jei");
    }
    //?}

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    /** The JEI runtime, or {@code null} before it is available. */
    static IJeiRuntime runtime() {
        return runtime;
    }

    /** Whether the client has bound tags: the mod's chemicals tag is never empty once tags are in. */
    private static boolean tagsBound() {
        return !com.tonywww.foodindustrial.platform.PlatformTags.itemsIn(
            com.tonywww.foodindustrial.platform.PlatformTags.itemTag(FoodIndustrial.MOD_ID + ":chemicals")).isEmpty();
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        categories.clear();
        for (ProcessType type : ProcessTypes.recipeTypes()) {
            ProcessCategory category = new ProcessCategory(type, gui);
            categories.put(type, category);
            registration.addRecipeCategories(category);
        }
        transforms = new WorldCategory("block_transform", new ItemStack(Items.DIRT), gui);
        harvests = new WorldCategory("harvesting", new ItemStack(WorldInteractions.FLINT_KNIFE.get()), gui);
        registration.addRecipeCategories(transforms, harvests);
        MachineDefinition icon = null;
        for (MachineDefinition machine : MachineDefinition.all()) {
            if (machine.isMultiblock() && !machine.development()) {
                icon = machine;
                break;
            }
        }
        registration.addRecipeCategories(new StructureCategory(gui, icon == null ? new ItemStack(Items.BRICKS) : new ItemStack(icon.item())));
    }

    /** The mod's recipes by JEI recipe type (empty without a level). */
    private Map<RecipeType<?>, List<?>> recipes() {
        Map<RecipeType<?>, List<?>> result = new LinkedHashMap<>();
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return result;
        }
        List<String> incomplete = new ArrayList<>();
        for (Map.Entry<ProcessType, ProcessCategory> entry : categories.entrySet()) {
            List<ProcessCategory.Entry> recipes = new ArrayList<>();
            for (PlatformRecipes.Loaded<ProcessRecipe> loaded : PlatformRecipes.loaded(level, entry.getKey().kind())) {
                if (RecipePage.of(loaded.id(), loaded.data()).complete()) {
                    recipes.add(new ProcessCategory.Entry(loaded.id(), loaded.data()));
                } else {
                    incomplete.add(loaded.id());
                }
            }
            recipes.sort((a, b) -> a.id().compareTo(b.id()));
            result.put(entry.getValue().getRecipeType(), recipes);
        }
        List<WorldPage> transformPages = new ArrayList<>();
        for (BlockTransformRecipe recipe : PlatformRecipes.all(level, WorldInteractions.BLOCK_TRANSFORM)) {
            transformPages.add(WorldPage.of(recipe));
        }
        result.put(transforms.getRecipeType(), transformPages);
        List<WorldPage> harvestPages = new ArrayList<>();
        for (HarvestRecipe recipe : PlatformRecipes.all(level, WorldInteractions.HARVESTING)) {
            harvestPages.add(WorldPage.of(recipe));
        }
        result.put(harvests.getRecipeType(), harvestPages);
        FoodIndustrial.LOGGER.debug("JEI: left out {} recipes with an input no item or fluid fills: {}", incomplete.size(), incomplete);
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addRecipes(IRecipeManager manager) {
        recipes().forEach((type, list) -> manager.addRecipes((RecipeType) type, (List) list));
        FoodIndustrial.LOGGER.info("JEI: added the mod's recipes after the tags arrived");
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void registerRecipes(IRecipeRegistration registration) {
        if (tagsBound()) {
            pending = null;
            recipes().forEach((type, list) -> registration.addRecipes((RecipeType) type, (List) list));
        } else {
            pending = this;
        }
        List<MachineDefinition> structures = new ArrayList<>();
        for (MachineDefinition machine : MachineDefinition.all()) {
            if (machine.isMultiblock() && !machine.development()) {
                structures.add(machine);
            }
        }
        registration.addRecipes(StructureCategory.TYPE, structures);
        for (Map.Entry<ItemStack, String> page : RecipeInfo.pages()) {
            registration.addItemStackInfo(page.getKey(), Component.translatable(page.getValue()));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (Map.Entry<ProcessType, ProcessCategory> entry : categories.entrySet()) {
            for (DeviceSpec device : RecipeDevices.of(entry.getKey())) {
                for (ItemStack icon : RecipeDevices.icons(device)) {
                    registration.addRecipeCatalyst(icon, entry.getValue().getRecipeType());
                }
            }
        }
        registration.addRecipeCatalyst(new ItemStack(WorldInteractions.FLINT_KNIFE.get()), harvests.getRecipeType());
        for (MachineDefinition machine : MachineDefinition.all()) {
            if (machine.isMultiblock() && !machine.development()) {
                registration.addRecipeCatalyst(new ItemStack(machine.item()), StructureCategory.TYPE);
            }
        }
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        for (ProcessCategory category : categories.values()) {
            registration.addRecipeTransferHandler(new MachineTransfer(category, registration.getTransferHelper()), category.getRecipeType());
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        MachineScreenHandler handler = new MachineScreenHandler(categories);
        registration.addGuiContainerHandler(MachineScreen.class, handler);
        registration.addGhostIngredientHandler(MachineScreen.class, handler);
        registration.addGuiContainerHandler(net.minecraft.client.gui.screens.inventory.InventoryScreen.class, new InventoryPanelHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        IRecipeManager manager = jeiRuntime.getRecipeManager();
        if (pending == this && tagsBound()) {
            pending = null;
            addRecipes(manager);
        }
        hideBridges(manager, RecipeTypes.SMELTING);
        hideBridges(manager, RecipeTypes.SMOKING);
        hideBridges(manager, RecipeTypes.BLASTING);
        hideBridges(manager, RecipeTypes.CAMPFIRE_COOKING);
    }

    /** Hides the recipes the bridge put into a vanilla cooking type (machines/01 §5 rule 5). */
    //? if >=26.1 {
    /*private static <T> void hideBridges(IRecipeManager manager, mezz.jei.api.recipe.types.IRecipeType<T> type) {
    *///?} else {
    private static <T> void hideBridges(IRecipeManager manager, RecipeType<T> type) {
    //?}
        List<T> bridges = new ArrayList<>();
        manager.createRecipeLookup(type).includeHidden().get().forEach(recipe -> {
            if (PlatformVanillaCooking.isBridge(recipe)) {
                bridges.add(recipe);
            }
        });
        if (!bridges.isEmpty()) {
            manager.hideRecipes(type, bridges);
        }
        FoodIndustrial.LOGGER.debug("JEI: hid {} bridge recipes of {}", bridges.size(), type);
    }
}
