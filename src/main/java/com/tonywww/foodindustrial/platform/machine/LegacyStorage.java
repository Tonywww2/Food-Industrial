package com.tonywww.foodindustrial.platform.machine;

//? if forge {
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
//?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
*///?}
//? if <26.1 {
import com.tonywww.foodindustrial.core.storage.EnergyStore;
import com.tonywww.foodindustrial.core.storage.EnergyTransfer;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.ItemSlots;
import com.tonywww.foodindustrial.core.storage.ItemTransfer;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformItems;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * Adapters for the pre-26.1 handler interfaces ({@code IItemHandler}, {@code IFluidHandler},
 * {@code IEnergyStorage}); the method names are identical on Forge 1.20.1 and NeoForge 1.21.1.
 */
final class LegacyStorage {
    private LegacyStorage() {
    }

    /** Business slots exposed as a loader item handler. */
    static final class Items implements IItemHandler {
        private final ItemSlots slots;

        Items(ItemSlots slots) {
            this.slots = slots;
        }

        @Override
        public int getSlots() {
            return slots.slots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slots.getStack(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || slot < 0 || slot >= slots.slots() || !slots.canInsert(slot, stack)) {
                return stack;
            }
            ItemStack existing = slots.getStack(slot);
            int limit = Math.min(slots.getSlotLimit(slot), stack.getMaxStackSize());
            if (!existing.isEmpty()) {
                if (!PlatformItems.sameItemSameData(existing, stack)) {
                    return stack;
                }
                limit -= existing.getCount();
            }
            if (limit <= 0) {
                return stack;
            }
            int moved = Math.min(limit, stack.getCount());
            if (!simulate) {
                slots.setStack(slot, existing.isEmpty() ? stack.copyWithCount(moved) : existing.copyWithCount(existing.getCount() + moved));
            }
            return moved == stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - moved);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0 || slot < 0 || slot >= slots.slots() || !slots.canExtract(slot)) {
                return ItemStack.EMPTY;
            }
            ItemStack existing = slots.getStack(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int moved = Math.min(amount, Math.min(existing.getCount(), existing.getMaxStackSize()));
            if (!simulate) {
                slots.setStack(slot, moved == existing.getCount() ? ItemStack.EMPTY : existing.copyWithCount(existing.getCount() - moved));
            }
            return existing.copyWithCount(moved);
        }

        @Override
        public int getSlotLimit(int slot) {
            return slots.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slots.canInsert(slot, stack);
        }
    }

    /** Business tanks exposed as a loader fluid handler. A fill goes into a single tank. */
    static final class Fluids implements IFluidHandler {
        private final FluidTanks tanks;

        Fluids(FluidTanks tanks) {
            this.tanks = tanks;
        }

        @Override
        public int getTanks() {
            return tanks.tanks();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return PlatformFluids.toStack(tanks.getFluid(tank));
        }

        @Override
        public int getTankCapacity(int tank) {
            return tanks.getCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return tanks.canFill(tank, PlatformFluids.fromStack(stack));
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            FluidAmount offered = PlatformFluids.fromStack(resource);
            if (offered.isEmpty()) {
                return 0;
            }
            int target = -1;
            for (int i = 0; i < tanks.tanks() && target < 0; i++) {
                FluidAmount current = tanks.getFluid(i);
                if (!current.isEmpty() && tanks.joins(i, current, offered) && current.amount() < tanks.getCapacity(i) && tanks.canFill(i, offered)) {
                    target = i;
                }
            }
            for (int i = 0; i < tanks.tanks() && target < 0; i++) {
                if (tanks.getFluid(i).isEmpty() && tanks.canFill(i, offered)) {
                    target = i;
                }
            }
            if (target < 0) {
                return 0;
            }
            FluidAmount current = tanks.getFluid(target);
            int filled = Math.min(tanks.getCapacity(target) - current.amount(), offered.amount());
            if (filled > 0 && action.execute()) {
                tanks.setFluid(target, tanks.joined(target, current, offered, filled));
            }
            return Math.max(filled, 0);
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidAmount wanted = PlatformFluids.fromStack(resource);
            if (wanted.isEmpty()) {
                return FluidStack.EMPTY;
            }
            for (int i = 0; i < tanks.tanks(); i++) {
                FluidAmount current = tanks.getFluid(i);
                if (!current.isEmpty() && current.isSameKind(wanted) && tanks.canDrain(i)) {
                    return PlatformFluids.toStack(drainTank(i, current, wanted.amount(), action.execute()));
                }
            }
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (maxDrain <= 0) {
                return FluidStack.EMPTY;
            }
            for (int i = 0; i < tanks.tanks(); i++) {
                FluidAmount current = tanks.getFluid(i);
                if (!current.isEmpty() && tanks.canDrain(i)) {
                    return PlatformFluids.toStack(drainTank(i, current, maxDrain, action.execute()));
                }
            }
            return FluidStack.EMPTY;
        }

        private FluidAmount drainTank(int tank, FluidAmount current, int max, boolean execute) {
            int drained = Math.min(max, current.amount());
            if (execute) {
                tanks.setFluid(tank, current.withAmount(current.amount() - drained));
            }
            return current.withAmount(drained);
        }
    }

    /** Business energy buffer exposed as a loader energy storage (int-based). */
    static final class Energy implements IEnergyStorage {
        private final EnergyStore store;

        Energy(EnergyStore store) {
            this.store = store;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            long accepted = Math.min(Math.min(maxReceive, store.maxReceive()), store.getCapacity() - store.getEnergy());
            if (accepted <= 0) {
                return 0;
            }
            if (!simulate) {
                store.setEnergy(store.getEnergy() + accepted);
            }
            return (int) accepted;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            long extracted = Math.min(Math.min(maxExtract, store.maxExtract()), store.getEnergy());
            if (extracted <= 0) {
                return 0;
            }
            if (!simulate) {
                store.setEnergy(store.getEnergy() - extracted);
            }
            return (int) extracted;
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(Integer.MAX_VALUE, store.getEnergy());
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(Integer.MAX_VALUE, store.getCapacity());
        }

        @Override
        public boolean canExtract() {
            return store.maxExtract() > 0;
        }

        @Override
        public boolean canReceive() {
            return store.maxReceive() > 0;
        }
    }

    /** A foreign item handler seen through the business {@link ItemTransfer} interface. */
    record ItemTarget(IItemHandler handler) implements ItemTransfer {
        @Override
        public ItemStack insert(ItemStack stack, boolean simulate) {
            ItemStack remainder = stack;
            for (int i = 0; i < handler.getSlots() && !remainder.isEmpty(); i++) {
                remainder = handler.insertItem(i, remainder, simulate);
            }
            return remainder;
        }

        @Override
        public ItemStack extract(int maxCount, Predicate<ItemStack> filter, boolean simulate) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack candidate = handler.getStackInSlot(i);
                if (!candidate.isEmpty() && filter.test(candidate)) {
                    ItemStack extracted = handler.extractItem(i, maxCount, simulate);
                    if (!extracted.isEmpty()) {
                        return extracted;
                    }
                }
            }
            return ItemStack.EMPTY;
        }
    }

    /** A foreign fluid handler seen through the business {@link FluidTransfer} interface. */
    record FluidTarget(IFluidHandler handler) implements FluidTransfer {
        @Override
        public int fill(FluidAmount fluid, boolean simulate) {
            return fluid.isEmpty() ? 0 : handler.fill(PlatformFluids.toStack(fluid), action(simulate));
        }

        @Override
        public FluidAmount drain(int maxAmount, boolean simulate) {
            return PlatformFluids.fromStack(handler.drain(maxAmount, action(simulate)));
        }

        @Override
        public FluidAmount drain(FluidAmount fluid, boolean simulate) {
            return fluid.isEmpty() ? FluidAmount.EMPTY
                : PlatformFluids.fromStack(handler.drain(PlatformFluids.toStack(fluid), action(simulate)));
        }

        private static IFluidHandler.FluidAction action(boolean simulate) {
            return simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
        }
    }

    /** A foreign energy storage seen through the business {@link EnergyTransfer} interface. */
    record EnergyTarget(IEnergyStorage storage) implements EnergyTransfer {
        @Override
        public long insert(long amount, boolean simulate) {
            return storage.receiveEnergy((int) Math.min(Integer.MAX_VALUE, amount), simulate);
        }

        @Override
        public long extract(long amount, boolean simulate) {
            return storage.extractEnergy((int) Math.min(Integer.MAX_VALUE, amount), simulate);
        }
    }
}
//?}
