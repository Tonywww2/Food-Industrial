package com.tonywww.foodindustrial.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Base of the machine and port menus (machines/04 §6): on the server it captures a {@link MachineView}
 * every tick and sends the groups that changed; the client merges them into {@link #view()}. Settings
 * change only through {@link #send} actions, which the server checks before applying.
 */
public abstract class FIMenu extends AbstractContainerMenu {
    protected final Player player;
    /** Server: the last view sent; client: the merged view. */
    private MachineView view = new MachineView();
    private boolean synced;

    protected FIMenu(MenuType<?> type, int containerId, Inventory inventory) {
        super(type, containerId);
        this.player = inventory.player;
    }

    /** Groups of {@link MachineView} this menu shows. */
    protected abstract int syncGroups();

    /** Server: the current state, or {@code null} when there is nothing to show. */
    @Nullable
    protected abstract MachineView capture(int groups);

    /** Server: an action from the client of this menu; the menu is still valid for {@code sender}. */
    protected abstract void handleAction(ServerPlayer sender, int action, int a, int b);

    /** Server: applies an action as if it came from the client (packets and development commands). */
    public final void runAction(ServerPlayer sender, int action, int a, int b) {
        if (stillValid(sender)) {
            handleAction(sender, action, a, b);
        }
    }

    /** Client: what the screen shows. */
    public MachineView view() {
        return view;
    }

    /** Client: asks the server to apply an action. */
    public void send(int action, int a, int b) {
        MenuNetwork.sendAction(containerId, action, a, b);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) {
            int groups = syncGroups();
            MachineView now = capture(groups);
            if (now == null) {
                return;
            }
            int changed = synced ? now.diff(view, groups) : groups;
            if (changed != 0) {
                synced = true;
                view = now;
                MenuNetwork.sendView(serverPlayer, containerId, changed, now);
            }
        }
    }

    /** Client: merges a received view. */
    void receive(int groups, MachineView received) {
        view.merge(received, groups);
    }

    /** Adds the player's inventory: three rows from {@code y}, the hotbar 58 pixels lower. */
    protected final void addPlayerInventory(Inventory inventory, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, y + 58));
        }
    }

    /**
     * Shift-click from the player's inventory when no machine slot takes the stack: moves it between
     * the main inventory and the hotbar.
     */
    protected final boolean moveWithinPlayer(ItemStack stack, int index, int playerStart) {
        int hotbar = playerStart + 27;
        if (index < hotbar) {
            return moveItemStackTo(stack, hotbar, hotbar + 9, false);
        }
        return moveItemStackTo(stack, playerStart, hotbar, false);
    }

    /** Puts {@code stack} into the player's inventory or drops it at their feet. */
    protected final void give(Player target, ItemStack stack) {
        if (!stack.isEmpty()) {
            target.getInventory().placeItemBackInInventory(stack);
        }
    }

    /** Replaces one item of the carried stack with {@code result} (fluid containers filled or emptied). */
    protected final void replaceOneCarried(Player target, ItemStack result) {
        ItemStack carried = getCarried();
        if (carried.getCount() <= 1) {
            setCarried(result);
        } else {
            setCarried(carried.copyWithCount(carried.getCount() - 1));
            give(target, result);
        }
    }
}
