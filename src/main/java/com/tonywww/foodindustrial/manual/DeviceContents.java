package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformItems;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

/**
 * What a T0 device holds (additives/07 §操作与实现规格): item slots and tanks that inputs and products
 * share (the contents change in place), and catalyst slots with their use counts. Each tank holds one
 * fluid; a fluid joins a tank only when the tank is empty or holds the same fluid (purities merge by
 * weighted average, additives/10 §2.2 rule 4).
 */
public final class DeviceContents {
    private final ItemStack[] items;
    private final FluidAmount[] tanks;
    private final int tankCapacity;
    private final ItemStack[] catalysts;
    private final int[] catalystUses;

    public DeviceContents(int itemSlots, int tanks, int tankCapacity, int catalystSlots) {
        this.items = new ItemStack[itemSlots];
        this.tanks = new FluidAmount[tanks];
        this.tankCapacity = tankCapacity;
        this.catalysts = new ItemStack[catalystSlots];
        this.catalystUses = new int[catalystSlots];
        Arrays.fill(items, ItemStack.EMPTY);
        Arrays.fill(this.tanks, FluidAmount.EMPTY);
        Arrays.fill(catalysts, ItemStack.EMPTY);
    }

    /** Deep copy, for trying a recipe before running it. */
    public DeviceContents copy() {
        DeviceContents copy = new DeviceContents(items.length, tanks.length, tankCapacity, catalysts.length);
        for (int i = 0; i < items.length; i++) {
            copy.items[i] = items[i].copy();
        }
        System.arraycopy(tanks, 0, copy.tanks, 0, tanks.length);
        for (int i = 0; i < catalysts.length; i++) {
            copy.catalysts[i] = catalysts[i].copy();
        }
        System.arraycopy(catalystUses, 0, copy.catalystUses, 0, catalystUses.length);
        return copy;
    }

    public int itemSlots() {
        return items.length;
    }

    public int tankCount() {
        return tanks.length;
    }

    public int tankCapacity() {
        return tankCapacity;
    }

    public int catalystSlots() {
        return catalysts.length;
    }

    public ItemStack item(int slot) {
        return items[slot];
    }

    public void setItem(int slot, ItemStack stack) {
        items[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    public FluidAmount tank(int tank) {
        return tanks[tank];
    }

    public void setTank(int tank, FluidAmount fluid) {
        tanks[tank] = fluid.amount() <= 0 ? FluidAmount.EMPTY : fluid;
    }

    public ItemStack catalyst(int slot) {
        return catalysts[slot];
    }

    public void setCatalyst(int slot, ItemStack stack) {
        if (!PlatformItems.sameItemSameData(catalysts[slot], stack)) {
            catalystUses[slot] = 0;
        }
        catalysts[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    /** Counts one use of the catalyst in {@code slot}; uses the item up after {@code runs} uses. */
    public void wearCatalyst(int slot, int runs) {
        if (++catalystUses[slot] >= runs) {
            catalystUses[slot] = 0;
            catalysts[slot].shrink(1);
            if (catalysts[slot].isEmpty()) {
                catalysts[slot] = ItemStack.EMPTY;
            }
        }
    }

    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        for (FluidAmount fluid : tanks) {
            if (!fluid.isEmpty()) {
                return false;
            }
        }
        for (ItemStack stack : catalysts) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // ---- in-place insertion ----

    /**
     * Puts {@code stack} into the slots among {@code slots}: first onto stacks of the same item and
     * data, then into empty slots. Returns what did not fit.
     */
    public ItemStack insertItem(int[] slots, ItemStack stack) {
        ItemStack rest = stack.copy();
        for (int slot : slots) {
            ItemStack here = items[slot];
            if (!here.isEmpty() && PlatformItems.sameItemSameData(here, rest)) {
                int moved = Math.min(rest.getCount(), here.getMaxStackSize() - here.getCount());
                if (moved > 0) {
                    here.grow(moved);
                    rest.shrink(moved);
                }
            }
            if (rest.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        for (int slot : slots) {
            if (items[slot].isEmpty()) {
                int moved = Math.min(rest.getCount(), rest.getMaxStackSize());
                items[slot] = rest.copyWithCount(moved);
                rest.shrink(moved);
            }
            if (rest.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return rest;
    }

    /**
     * Puts {@code fluid} into a tank among {@code tanks} that is empty or holds the same fluid; whole
     * or nothing. Returns whether it fit.
     */
    public boolean insertFluid(int[] tankIndices, FluidAmount fluid) {
        if (fluid.isEmpty()) {
            return true;
        }
        for (int tank : tankIndices) {
            FluidAmount here = tanks[tank];
            if (!here.isEmpty() && here.fluid() == fluid.fluid() && here.amount() + fluid.amount() <= tankCapacity) {
                tanks[tank] = Purity.merge(here, fluid);
                return true;
            }
        }
        for (int tank : tankIndices) {
            if (tanks[tank].isEmpty() && fluid.amount() <= tankCapacity) {
                tanks[tank] = fluid;
                return true;
            }
        }
        return false;
    }

    // ---- persistence ----

    public void save(DataWriter out) {
        for (int i = 0; i < items.length; i++) {
            out.putItem("Item" + i, items[i]);
        }
        for (int i = 0; i < tanks.length; i++) {
            out.putFluid("Tank" + i, tanks[i]);
        }
        for (int i = 0; i < catalysts.length; i++) {
            out.putItem("Catalyst" + i, catalysts[i]);
            if (catalystUses[i] > 0) {
                out.putInt("CatalystUses" + i, catalystUses[i]);
            }
        }
    }

    public void load(DataReader in) {
        for (int i = 0; i < items.length; i++) {
            items[i] = in.getItem("Item" + i);
        }
        for (int i = 0; i < tanks.length; i++) {
            tanks[i] = in.getFluid("Tank" + i);
        }
        for (int i = 0; i < catalysts.length; i++) {
            catalysts[i] = in.getItem("Catalyst" + i);
            catalystUses[i] = in.getInt("CatalystUses" + i, 0);
        }
    }
}
