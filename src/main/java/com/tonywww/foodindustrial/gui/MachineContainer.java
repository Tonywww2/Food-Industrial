package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.machine.component.MachineInventory;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A machine's item slots as a vanilla container for the menu's slots (server side). Every change goes
 * through {@link MachineInventory#set}; {@link #setChanged} also catches stacks vanilla changed in place.
 */
final class MachineContainer implements Container {
    private final MachineBlockEntity machine;
    private final MachineInventory inventory;

    MachineContainer(MachineBlockEntity machine) {
        this.machine = machine;
        this.inventory = machine.inventory();
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack current = inventory.get(slot);
        if (current.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack taken = current.copyWithCount(Math.min(amount, current.getCount()));
        inventory.shrink(slot, taken.getCount());
        return taken;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack current = inventory.get(slot);
        inventory.set(slot, ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        machine.contentsEdited();
    }

    @Override
    public boolean stillValid(Player player) {
        return !machine.isRemoved() && Container.stillValidBlockEntity(machine, player);
    }

    @Override
    public void clearContent() {
        for (int slot = 0; slot < inventory.size(); slot++) {
            inventory.set(slot, ItemStack.EMPTY);
        }
    }
}
