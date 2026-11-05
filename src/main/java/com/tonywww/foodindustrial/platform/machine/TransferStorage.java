package com.tonywww.foodindustrial.platform.machine;

//? if >=26.1 {
/*import com.tonywww.foodindustrial.core.storage.EnergyStore;
import com.tonywww.foodindustrial.core.storage.EnergyTransfer;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.ItemSlots;
import com.tonywww.foodindustrial.core.storage.ItemTransfer;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.Predicate;

/^*
 * Adapters for the 26.1 transactional transfer API. Every change made inside a transaction is
 * journaled per slot / tank and reverted when the transaction is aborted.
 ^/
final class TransferStorage {
    private TransferStorage() {
    }

    static final class Items implements ResourceHandler<ItemResource> {
        private final ItemSlots slots;
        private final SlotJournal[] journals;

        Items(ItemSlots slots) {
            this.slots = slots;
            this.journals = new SlotJournal[slots.slots()];
        }

        private void snapshot(int index, TransactionContext transaction) {
            SlotJournal journal = journals[index];
            if (journal == null) {
                journal = journals[index] = new SlotJournal(slots, index);
            }
            journal.updateSnapshots(transaction);
        }

        @Override public int size() { return slots.slots(); }
        @Override public ItemResource getResource(int index) { return ItemResource.of(slots.getStack(index)); }
        @Override public long getAmountAsLong(int index) { return slots.getStack(index).getCount(); }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            int limit = slots.getSlotLimit(index);
            return resource.isEmpty() ? limit : Math.min(limit, resource.getMaxStackSize());
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return !resource.isEmpty() && slots.canInsert(index, resource.toStack());
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (index < 0 || index >= slots.slots() || amount == 0) {
                return 0;
            }
            ItemStack existing = slots.getStack(index);
            if (!existing.isEmpty() && !resource.matches(existing)) {
                return 0;
            }
            if (!slots.canInsert(index, resource.toStack())) {
                return 0;
            }
            int space = (int) getCapacityAsLong(index, resource) - existing.getCount();
            int moved = Math.min(space, amount);
            if (moved <= 0) {
                return 0;
            }
            snapshot(index, transaction);
            slots.setStack(index, resource.toStack(existing.getCount() + moved));
            return moved;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (index < 0 || index >= slots.slots() || amount == 0 || !slots.canExtract(index)) {
                return 0;
            }
            ItemStack existing = slots.getStack(index);
            if (existing.isEmpty() || !resource.matches(existing)) {
                return 0;
            }
            int moved = Math.min(amount, existing.getCount());
            snapshot(index, transaction);
            slots.setStack(index, moved == existing.getCount() ? ItemStack.EMPTY : existing.copyWithCount(existing.getCount() - moved));
            return moved;
        }
    }

    private static final class SlotJournal extends SnapshotJournal<ItemStack> {
        private final ItemSlots slots;
        private final int index;

        SlotJournal(ItemSlots slots, int index) {
            this.slots = slots;
            this.index = index;
        }

        @Override protected ItemStack createSnapshot() { return slots.getStack(index).copy(); }
        @Override protected void revertToSnapshot(ItemStack snapshot) { slots.setStack(index, snapshot); }
    }

    static final class Fluids implements ResourceHandler<FluidResource> {
        private final FluidTanks tanks;
        private final TankJournal[] journals;

        Fluids(FluidTanks tanks) {
            this.tanks = tanks;
            this.journals = new TankJournal[tanks.tanks()];
        }

        private void snapshot(int index, TransactionContext transaction) {
            TankJournal journal = journals[index];
            if (journal == null) {
                journal = journals[index] = new TankJournal(tanks, index);
            }
            journal.updateSnapshots(transaction);
        }

        @Override public int size() { return tanks.tanks(); }
        @Override public FluidResource getResource(int index) { return PlatformFluids.toResource(tanks.getFluid(index)); }
        @Override public long getAmountAsLong(int index) { return tanks.getFluid(index).amount(); }
        @Override public long getCapacityAsLong(int index, FluidResource resource) { return tanks.getCapacity(index); }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return !resource.isEmpty() && tanks.canFill(index, PlatformFluids.fromResource(resource, 1));
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (index < 0 || index >= tanks.tanks() || amount == 0) {
                return 0;
            }
            FluidAmount offered = PlatformFluids.fromResource(resource, amount);
            FluidAmount current = tanks.getFluid(index);
            if ((!current.isEmpty() && !tanks.joins(index, current, offered)) || !tanks.canFill(index, offered)) {
                return 0;
            }
            int filled = Math.min(tanks.getCapacity(index) - current.amount(), amount);
            if (filled <= 0) {
                return 0;
            }
            snapshot(index, transaction);
            tanks.setFluid(index, tanks.joined(index, current, offered, filled));
            return filled;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (index < 0 || index >= tanks.tanks() || amount == 0 || !tanks.canDrain(index)) {
                return 0;
            }
            FluidAmount current = tanks.getFluid(index);
            if (current.isEmpty() || !current.isSameKind(PlatformFluids.fromResource(resource, 1))) {
                return 0;
            }
            int drained = Math.min(amount, current.amount());
            snapshot(index, transaction);
            tanks.setFluid(index, current.withAmount(current.amount() - drained));
            return drained;
        }
    }

    private static final class TankJournal extends SnapshotJournal<FluidAmount> {
        private final FluidTanks tanks;
        private final int index;

        TankJournal(FluidTanks tanks, int index) {
            this.tanks = tanks;
            this.index = index;
        }

        @Override protected FluidAmount createSnapshot() { return tanks.getFluid(index); }
        @Override protected void revertToSnapshot(FluidAmount snapshot) { tanks.setFluid(index, snapshot); }
    }

    static final class Energy extends SnapshotJournal<Long> implements EnergyHandler {
        private final EnergyStore store;

        Energy(EnergyStore store) {
            this.store = store;
        }

        @Override protected Long createSnapshot() { return store.getEnergy(); }
        @Override protected void revertToSnapshot(Long snapshot) { store.setEnergy(snapshot); }
        @Override public long getAmountAsLong() { return store.getEnergy(); }
        @Override public long getCapacityAsLong() { return store.getCapacity(); }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            long accepted = Math.min(Math.min(amount, store.maxReceive()), store.getCapacity() - store.getEnergy());
            if (accepted <= 0) {
                return 0;
            }
            updateSnapshots(transaction);
            store.setEnergy(store.getEnergy() + accepted);
            return (int) accepted;
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            long extracted = Math.min(Math.min(amount, store.maxExtract()), store.getEnergy());
            if (extracted <= 0) {
                return 0;
            }
            updateSnapshots(transaction);
            store.setEnergy(store.getEnergy() - extracted);
            return (int) extracted;
        }
    }

    /^* A foreign item handler seen through the business {@link ItemTransfer} interface. ^/
    record ItemTarget(ResourceHandler<ItemResource> handler) implements ItemTransfer {
        @Override
        public ItemStack insert(ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) {
                return stack;
            }
            try (Transaction transaction = Transaction.openRoot()) {
                int inserted = handler.insert(ItemResource.of(stack), stack.getCount(), transaction);
                if (!simulate) {
                    transaction.commit();
                }
                return inserted == stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - inserted);
            }
        }

        @Override
        public ItemStack extract(int maxCount, Predicate<ItemStack> filter, boolean simulate) {
            for (int i = 0; i < handler.size(); i++) {
                ItemResource resource = handler.getResource(i);
                if (resource.isEmpty() || !filter.test(resource.toStack())) {
                    continue;
                }
                try (Transaction transaction = Transaction.openRoot()) {
                    int extracted = handler.extract(i, resource, maxCount, transaction);
                    if (extracted > 0) {
                        if (!simulate) {
                            transaction.commit();
                        }
                        return resource.toStack(extracted);
                    }
                }
            }
            return ItemStack.EMPTY;
        }
    }

    /^* A foreign fluid handler seen through the business {@link FluidTransfer} interface. ^/
    record FluidTarget(ResourceHandler<FluidResource> handler) implements FluidTransfer {
        @Override
        public int fill(FluidAmount fluid, boolean simulate) {
            if (fluid.isEmpty()) {
                return 0;
            }
            try (Transaction transaction = Transaction.openRoot()) {
                int filled = handler.insert(PlatformFluids.toResource(fluid), fluid.amount(), transaction);
                if (!simulate) {
                    transaction.commit();
                }
                return filled;
            }
        }

        @Override
        public FluidAmount drain(int maxAmount, boolean simulate) {
            for (int i = 0; i < handler.size(); i++) {
                FluidResource resource = handler.getResource(i);
                if (!resource.isEmpty()) {
                    FluidAmount drained = drainResource(resource, maxAmount, simulate);
                    if (!drained.isEmpty()) {
                        return drained;
                    }
                }
            }
            return FluidAmount.EMPTY;
        }

        @Override
        public FluidAmount drain(FluidAmount fluid, boolean simulate) {
            return fluid.isEmpty() ? FluidAmount.EMPTY : drainResource(PlatformFluids.toResource(fluid), fluid.amount(), simulate);
        }

        private FluidAmount drainResource(FluidResource resource, int maxAmount, boolean simulate) {
            try (Transaction transaction = Transaction.openRoot()) {
                int drained = handler.extract(resource, maxAmount, transaction);
                if (!simulate) {
                    transaction.commit();
                }
                return PlatformFluids.fromResource(resource, drained);
            }
        }
    }

    /^* A foreign energy handler seen through the business {@link EnergyTransfer} interface. ^/
    record EnergyTarget(EnergyHandler handler) implements EnergyTransfer {
        @Override
        public long insert(long amount, boolean simulate) {
            try (Transaction transaction = Transaction.openRoot()) {
                int inserted = handler.insert((int) Math.min(Integer.MAX_VALUE, amount), transaction);
                if (!simulate) {
                    transaction.commit();
                }
                return inserted;
            }
        }

        @Override
        public long extract(long amount, boolean simulate) {
            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = handler.extract((int) Math.min(Integer.MAX_VALUE, amount), transaction);
                if (!simulate) {
                    transaction.commit();
                }
                return extracted;
            }
        }
    }
}
*///?}
