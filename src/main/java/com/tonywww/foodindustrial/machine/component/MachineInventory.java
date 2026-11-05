package com.tonywww.foodindustrial.machine.component;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.platform.PlatformItems;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * Item slots of a machine grouped by role (machines/02 §4.1, machines/03 §1), in the order I, O, C, F,
 * U, X. Input slots can be locked to an item (with purity): emptied, they still only take that item
 * (machines/03 §2, machines/04 §1). Every change reports the slot's role to the owner, which wakes the
 * machine (machines/02 §6.2).
 */
public final class MachineInventory {
    private final ItemStack[] stacks;
    private final SlotRole[] roles;
    private final ItemStack[] locks;
    private final int[] start = new int[SlotRole.values().length];
    private final int[] count = new int[SlotRole.values().length];
    private final Consumer<SlotRole> onChange;

    public MachineInventory(IoLayout layout, Consumer<SlotRole> onChange) {
        this.onChange = onChange;
        int[] sizes = {layout.itemIn(), layout.itemOut(), layout.catalyst(), layout.fuel(), layout.upgrades(), layout.modules()};
        int total = 0;
        for (SlotRole role : SlotRole.values()) {
            start[role.ordinal()] = total;
            count[role.ordinal()] = sizes[role.ordinal()];
            total += sizes[role.ordinal()];
        }
        stacks = new ItemStack[total];
        roles = new SlotRole[total];
        locks = new ItemStack[total];
        for (SlotRole role : SlotRole.values()) {
            for (int i = 0; i < count[role.ordinal()]; i++) {
                roles[start[role.ordinal()] + i] = role;
            }
        }
        java.util.Arrays.fill(stacks, ItemStack.EMPTY);
        java.util.Arrays.fill(locks, ItemStack.EMPTY);
    }

    public int size() {
        return stacks.length;
    }

    public SlotRole role(int slot) {
        return roles[slot];
    }

    /** First slot of {@code role}. */
    public int start(SlotRole role) {
        return start[role.ordinal()];
    }

    public int count(SlotRole role) {
        return count[role.ordinal()];
    }

    /** The {@code index}-th slot of {@code role}. */
    public int slot(SlotRole role, int index) {
        return start[role.ordinal()] + index;
    }

    /** The stack in a slot; never modify it in place, write a new one with {@link #set}. */
    public ItemStack get(int slot) {
        return stacks[slot];
    }

    public ItemStack get(SlotRole role, int index) {
        return stacks[slot(role, index)];
    }

    public void set(int slot, ItemStack stack) {
        stacks[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack;
        onChange.accept(roles[slot]);
    }

    /** Lock of an input slot, or empty. */
    public ItemStack lock(int slot) {
        return locks[slot];
    }

    /** Locks an input slot to {@code template} (count ignored); empty unlocks. */
    public void setLock(int slot, ItemStack template) {
        locks[slot] = template.isEmpty() ? ItemStack.EMPTY : template.copyWithCount(1);
        onChange.accept(roles[slot]);
    }

    /** Whether a stack may go into an input slot under its lock. */
    public boolean allowedByLock(int slot, ItemStack stack) {
        ItemStack lock = locks[slot];
        return lock.isEmpty() || PlatformItems.sameItemSameData(lock, stack);
    }

    /** Whether any input slot is locked. */
    public boolean hasLocks() {
        for (ItemStack lock : locks) {
            if (!lock.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * First output slot for main products ({@code main}) or byproducts (machines/03 §1: main product
     * first): the main product goes into the first output slot, byproducts into the others; a machine
     * with one output slot puts both there.
     */
    public int outputFrom(boolean main) {
        int first = start(SlotRole.OUTPUT);
        return main || count(SlotRole.OUTPUT) <= 1 ? first : first + 1;
    }

    /** End (exclusive) of the output slots for main products or byproducts. */
    public int outputTo(boolean main) {
        int first = start(SlotRole.OUTPUT);
        return main && count(SlotRole.OUTPUT) > 1 ? first + 1 : first + count(SlotRole.OUTPUT);
    }

    /** Whether an output slot belongs to the main product's slots or the byproducts'. */
    public boolean outputServes(int slot, boolean main) {
        return slot >= outputFrom(main) && slot < outputTo(main);
    }

    /**
     * Puts {@code stack} into its output slots ({@link #outputFrom}): onto equal stacks first, then into
     * empty slots.
     *
     * @return what did not fit
     */
    public ItemStack insertOutput(ItemStack stack, boolean main, boolean simulate) {
        ItemStack rest = stack;
        int first = outputFrom(main);
        int last = outputTo(main);
        for (int pass = 0; pass < 2 && !rest.isEmpty(); pass++) {
            for (int slot = first; slot < last && !rest.isEmpty(); slot++) {
                ItemStack current = stacks[slot];
                boolean empty = current.isEmpty();
                if (pass == 0 ? empty || !PlatformItems.sameItemSameData(current, rest) : !empty) {
                    continue;
                }
                int limit = Math.min(64, rest.getMaxStackSize());
                int moved = Math.min(limit - current.getCount(), rest.getCount());
                if (moved <= 0) {
                    continue;
                }
                if (!simulate) {
                    set(slot, empty ? rest.copyWithCount(moved) : current.copyWithCount(current.getCount() + moved));
                }
                rest = rest.getCount() == moved ? ItemStack.EMPTY : rest.copyWithCount(rest.getCount() - moved);
            }
        }
        return rest;
    }

    /**
     * Whether all stacks fit into the output slots together.
     *
     * @param mainIndex index of the main product in {@code products}, or -1
     */
    public boolean outputsFit(ItemStack[] products, int length, int mainIndex) {
        ItemStack[] saved = new ItemStack[count(SlotRole.OUTPUT)];
        int first = start(SlotRole.OUTPUT);
        System.arraycopy(stacks, first, saved, 0, saved.length);
        boolean fits = true;
        for (int i = 0; i < length && fits; i++) {
            if (!products[i].isEmpty()) {
                fits = insertOutputQuiet(products[i], i == mainIndex).isEmpty();
            }
        }
        System.arraycopy(saved, 0, stacks, first, saved.length);
        return fits;
    }

    private ItemStack insertOutputQuiet(ItemStack stack, boolean main) {
        ItemStack rest = stack;
        int first = outputFrom(main);
        int last = outputTo(main);
        for (int pass = 0; pass < 2 && !rest.isEmpty(); pass++) {
            for (int slot = first; slot < last && !rest.isEmpty(); slot++) {
                ItemStack current = stacks[slot];
                boolean empty = current.isEmpty();
                if (pass == 0 ? empty || !PlatformItems.sameItemSameData(current, rest) : !empty) {
                    continue;
                }
                int moved = Math.min(Math.min(64, rest.getMaxStackSize()) - current.getCount(), rest.getCount());
                if (moved > 0) {
                    stacks[slot] = empty ? rest.copyWithCount(moved) : current.copyWithCount(current.getCount() + moved);
                    rest = rest.getCount() == moved ? ItemStack.EMPTY : rest.copyWithCount(rest.getCount() - moved);
                }
            }
        }
        return rest;
    }

    /** Removes {@code amount} items from a slot. */
    public void shrink(int slot, int amount) {
        ItemStack current = stacks[slot];
        set(slot, current.getCount() <= amount ? ItemStack.EMPTY : current.copyWithCount(current.getCount() - amount));
    }

    /** Whether any slot of {@code role} holds something. */
    public boolean any(SlotRole role) {
        for (int i = start(role), end = i + count(role); i < end; i++) {
            if (!stacks[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** Whether every item slot, locks aside, is empty. */
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void save(DataWriter out) {
        for (int i = 0; i < stacks.length; i++) {
            if (!stacks[i].isEmpty()) {
                out.putItem("Slot" + i, stacks[i]);
            }
        }
    }

    public void load(DataReader in) {
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = in.getItem("Slot" + i);
        }
    }

    /** Locks are settings (machines/04 §4): saved with the configuration. */
    public void saveLocks(DataWriter out) {
        for (int i = 0; i < locks.length; i++) {
            if (!locks[i].isEmpty()) {
                out.putItem("Lock" + i, locks[i]);
            }
        }
    }

    public void loadLocks(DataReader in) {
        for (int i = 0; i < locks.length; i++) {
            ItemStack lock = roles[i] == SlotRole.INPUT ? in.getItem("Lock" + i) : ItemStack.EMPTY;
            locks[i] = lock.isEmpty() ? ItemStack.EMPTY : lock.copyWithCount(1);
        }
    }
}
