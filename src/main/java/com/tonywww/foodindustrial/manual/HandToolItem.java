package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.platform.PlatformItem;
import com.tonywww.foodindustrial.platform.PlatformItemData;
import com.tonywww.foodindustrial.platform.PlatformPlayers;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * H01 mortar, H02 sieve and cloth bag (additives/07 §操作与实现规格, 2026-11-10). Used in the inventory
 * by stacking with a right click: the tool on the cursor onto an ingredient, or an ingredient on the
 * cursor onto the tool. Each click is one action; after {@code manual.actions} actions one input is
 * used and the products go to the inventory. A bucket of liquid counts as a fluid input of 1,000 mB;
 * a fluid product goes back into that bucket. The progress is kept on the tool and restarts when the
 * ingredient or the recipe changes. Tools do not wear out.
 */
public class HandToolItem extends PlatformItem {
    private static final String DATA = "foodindustrial:manual";

    private final HandTool tool;

    public HandToolItem(Properties properties, HandTool tool) {
        super(properties);
        this.tool = tool;
    }

    public HandTool tool() {
        return tool;
    }

    /** The tool on the cursor, right-clicked onto {@code slot}. */
    @Override
    public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY || self.getCount() != 1) {
            return false;
        }
        ItemStack target = slot.getItem();
        if (target.isEmpty() || target.getItem() instanceof HandToolItem) {
            return false;
        }
        if (!player.level().isClientSide()) {
            work(player, self, target, replacement -> {
                slot.set(replacement);
                slot.setChanged();
            });
        }
        return true;
    }

    /** An ingredient on the cursor, right-clicked onto the tool in {@code slot}. */
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess carried) {
        if (action != ClickAction.SECONDARY || other.isEmpty() || other.getItem() instanceof HandToolItem || self.getCount() != 1) {
            return false;
        }
        if (!player.level().isClientSide()) {
            work(player, self, other, carried::set);
            slot.setChanged();
        }
        return true;
    }

    /** One action of the tool on {@code target}; {@code replace} sets the target's new stack. */
    private void work(Player player, ItemStack toolStack, ItemStack target, Consumer<ItemStack> replace) {
        Level level = player.level();
        FluidAmount fluid = PlatformFluidContainers.contained(target);
        Found found = find(level, target, fluid);
        if (found == null) {
            clear(toolStack);
            PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", "no_recipe")));
            return;
        }
        String inputId = PlatformTags.itemId(target.getItem());
        String recipeId = PlatformItemData.getString(toolStack, DATA, "Recipe");
        int progress = recipeId.equals(found.entry().id()) && PlatformItemData.getString(toolStack, DATA, "Input").equals(inputId)
            ? PlatformItemData.getInt(toolStack, DATA, "Progress", 0) : 0;
        progress++;
        int goal = found.entry().recipe().manualActions();
        level.playSound(null, player.blockPosition(), SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 0.4F,
            0.9F + level.getRandom().nextFloat() * 0.2F);
        if (progress < goal) {
            int done = progress;
            PlatformItemData.write(toolStack, DATA, level.registryAccess(), out -> {
                out.putString("Recipe", found.entry().id());
                out.putString("Input", inputId);
                out.putInt("Progress", done);
            });
            PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", tool.verb()), done, goal));
            return;
        }
        clear(toolStack);
        complete(player, found, target, fluid, replace);
    }

    private record Found(RecipeIndex.Entry entry, boolean fluidInput) {
    }

    /** The most specific recipe of the tool for one item or one bucket of fluid. */
    @Nullable
    private Found find(Level level, ItemStack target, FluidAmount fluid) {
        RecipeIndex index = RecipeIndex.get(level);
        Set<RecipeIndex.Entry> candidates = new LinkedHashSet<>();
        for (ProcessType type : tool.spec().types()) {
            if (!fluid.isEmpty()) {
                candidates.addAll(index.candidates(type, fluid.fluid()));
            }
            candidates.addAll(index.candidates(type, target.getItem()));
        }
        List<Found> runnable = new ArrayList<>();
        for (RecipeIndex.Entry entry : candidates) {
            if (!entry.runsOn(tool.spec())) {
                continue;
            }
            ProcessRecipe recipe = entry.recipe();
            ProcessInput input = recipe.inputs().get(0);
            if (input instanceof ProcessInput.FluidInput fluidInput) {
                if (!fluid.isEmpty() && target.getCount() == 1 && fluidInput.test(fluid)) {
                    runnable.add(new Found(entry, true));
                }
            } else if (((ProcessInput.ItemInput) input).test(target)) {
                runnable.add(new Found(entry, false));
            }
        }
        return runnable.stream().min(Comparator.<Found>comparingInt(found -> -found.entry().recipe().specificity())
            .thenComparing(found -> found.entry().id())).orElse(null);
    }

    private void complete(Player player, Found found, ItemStack target, FluidAmount fluid, Consumer<ItemStack> replace) {
        ProcessRecipe recipe = found.entry().recipe();
        ProcessInput input = recipe.inputs().get(0);
        int value = input instanceof ProcessInput.ItemInput item ? item.purityOf(target) : ((ProcessInput.FluidInput) input).purityOf(fluid);
        InPlaceProcess.Products products = InPlaceProcess.products(tool.spec(), found.entry(), new int[]{value}, new long[]{input.weight()},
            tool.spec().capabilities(), player.level().getRandom(), false);
        if (found.fluidInput()) {
            ItemStack container = PlatformFluidContainers.drained(target);
            if (!products.fluids().isEmpty()) {
                ItemStack filled = PlatformFluidContainers.filled(container, products.fluids().get(0));
                container = filled.isEmpty() ? container : filled;
            }
            replace.accept(container);
        } else {
            ItemStack rest = target.copy();
            rest.shrink(((ProcessInput.ItemInput) input).count());
            replace.accept(rest.isEmpty() ? ItemStack.EMPTY : rest);
        }
        for (ItemStack product : products.items()) {
            if (!player.getInventory().add(product.copy())) {
                player.drop(product, false);
            }
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.3F, 1.2F);
        PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", "done")));
    }

    private static void clear(ItemStack toolStack) {
        PlatformItemData.remove(toolStack, DATA);
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        int progress = PlatformItemData.getInt(stack, DATA, "Progress", 0);
        if (progress > 0) {
            lines.add(Component.translatable(GuiText.key("manual", "in_progress"), progress));
        }
        Tooltips.machine(Lang.description(tool.id()), lines, shiftDown);
    }
}
