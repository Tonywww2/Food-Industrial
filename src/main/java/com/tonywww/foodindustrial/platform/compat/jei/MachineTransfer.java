package com.tonywww.foodindustrial.platform.compat.jei;

import com.tonywww.foodindustrial.gui.MachineMenu;
import com.tonywww.foodindustrial.gui.MenuActions;
import com.tonywww.foodindustrial.gui.ModMenus;
import com.tonywww.foodindustrial.gui.RecipeText;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The JEI “+” button on machine screens (additives/10 §5 一键转移): picks inventory items for each
 * item input with the recipe ingredient's own {@code test} (so purity thresholds hold), then asks the
 * server to move them into the machine's input slots ({@link MenuActions#TRANSFER}); fluids and
 * catalysts stay with the player. Shift-click moves as many full sets as fit.
 */
final class MachineTransfer implements IRecipeTransferHandler<MachineMenu, ProcessCategory.Entry> {
    private final ProcessCategory category;
    private final IRecipeTransferHandlerHelper helper;

    MachineTransfer(ProcessCategory category, IRecipeTransferHandlerHelper helper) {
        this.category = category;
        this.helper = helper;
    }

    @Override
    public Class<? extends MachineMenu> getContainerClass() {
        return MachineMenu.class;
    }

    @Override
    public Optional<MenuType<MachineMenu>> getMenuType() {
        return Optional.of(ModMenus.MACHINE.get());
    }

    @Override
    public RecipeType<ProcessCategory.Entry> getRecipeType() {
        return category.getRecipeType();
    }

    /** One planned move: inventory menu slot → machine slot. */
    private record Move(int from, int to, int count) {
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(MachineMenu menu, ProcessCategory.Entry entry, IRecipeSlotsView slots, Player player,
                                               boolean maxTransfer, boolean doTransfer) {
        ProcessRecipe recipe = entry.recipe();
        if (!menu.definition().types().contains(category.type()) || !menu.definition().spec().canRun(recipe)) {
            return helper.createUserErrorWithTooltip(Component.translatable(RecipeText.key("transfer_device")));
        }
        int machineSlots = menu.machineSlots();
        List<Integer> targets = new ArrayList<>();
        for (int i = 0; i < machineSlots; i++) {
            if (menu.definition().layout().roleOf(i) == SlotRole.INPUT && menu.slots.get(i).isActive()) {
                targets.add(i);
            }
        }
        int[] left = new int[menu.slots.size()];
        for (int i = machineSlots; i < menu.slots.size(); i++) {
            left[i] = menu.slots.get(i).getItem().getCount();
        }
        List<IRecipeSlotView> inputViews = slots.getSlotViews(RecipeIngredientRole.INPUT);
        List<IRecipeSlotView> missing = new ArrayList<>();
        List<ProcessInput.ItemInput> items = new ArrayList<>();
        List<IRecipeSlotView> itemViews = new ArrayList<>();
        for (int i = 0; i < recipe.inputs().size(); i++) {
            if (recipe.inputs().get(i) instanceof ProcessInput.ItemInput item) {
                items.add(item);
                itemViews.add(i < inputViews.size() ? inputViews.get(i) : null);
            }
        }
        int sets = 1;
        if (maxTransfer) {
            sets = 64;
            for (ProcessInput.ItemInput item : items) {
                sets = Math.min(sets, Math.max(1, available(menu, item, left) / item.count()));
                sets = Math.min(sets, Math.max(1, 64 / item.count()));
            }
        }
        List<Move> moves = new ArrayList<>();
        List<Integer> used = new ArrayList<>();
        for (int n = 0; n < items.size(); n++) {
            ProcessInput.ItemInput item = items.get(n);
            int need = item.count() * sets;
            Integer target = null;
            for (int slot : targets) {
                if (!used.contains(slot)) {
                    target = slot;
                    break;
                }
            }
            if (target == null) {
                return helper.createUserErrorWithTooltip(Component.translatable(RecipeText.key("transfer_device")));
            }
            used.add(target);
            ItemStack first = null;
            List<Move> planned = new ArrayList<>();
            for (int i = machineSlots; i < menu.slots.size() && need > 0; i++) {
                Slot from = menu.slots.get(i);
                ItemStack stack = from.getItem();
                if (stack.isEmpty() || left[i] <= 0 || !com.tonywww.foodindustrial.platform.recipe.PlatformIngredients.fresh(item.ingredient()).test(stack)
                    || !menu.definition().slotAccepts(SlotRole.INPUT, target, stack)) {
                    continue;
                }
                if (first != null && !PlatformItems.sameItemSameData(first, stack)) {
                    continue;
                }
                first = stack;
                int take = Math.min(need, left[i]);
                planned.add(new Move(i, target, take));
                left[i] -= take;
                need -= take;
            }
            if (need > 0) {
                IRecipeSlotView view = itemViews.get(n);
                if (view != null) {
                    missing.add(view);
                }
                continue;
            }
            moves.addAll(planned);
        }
        if (!missing.isEmpty()) {
            return helper.createUserErrorForMissingSlots(Component.translatable(RecipeText.key("transfer_missing")), missing);
        }
        if (doTransfer) {
            for (int slot : used) {
                if (!menu.slots.get(slot).getItem().isEmpty()) {
                    menu.send(MenuActions.CLEAR_SLOT, slot, 0);
                }
            }
            for (Move move : moves) {
                menu.send(MenuActions.TRANSFER, move.from, (move.to << 8) | move.count);
            }
        }
        return null;
    }

    private static int available(MachineMenu menu, ProcessInput.ItemInput item, int[] left) {
        int total = 0;
        for (int i = menu.machineSlots(); i < menu.slots.size(); i++) {
            ItemStack stack = menu.slots.get(i).getItem();
            if (!stack.isEmpty() && com.tonywww.foodindustrial.platform.recipe.PlatformIngredients.fresh(item.ingredient()).test(stack)) {
                total += left[i];
            }
        }
        return total;
    }
}
