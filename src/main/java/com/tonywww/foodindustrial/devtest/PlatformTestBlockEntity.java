package com.tonywww.foodindustrial.devtest;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.EnergyStore;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.ItemSlots;
import com.tonywww.foodindustrial.core.storage.ItemTransfer;
import com.tonywww.foodindustrial.core.storage.StorageHost;
import com.tonywww.foodindustrial.platform.machine.PlatformBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Platform test block entity: slot 0 accepts items, slot 1 can be extracted; once per second one
 * item moves from slot 0 to slot 1 and slot 1 is pushed into the block below. The tank accepts any
 * fluid and pushes 250 mB per second into the block above; the energy buffer pushes 100 FE up.
 */
public final class PlatformTestBlockEntity extends PlatformBlockEntity implements StorageHost {
    private final ItemStack[] stacks = {ItemStack.EMPTY, ItemStack.EMPTY};
    private FluidAmount tank = FluidAmount.EMPTY;
    private long energy;
    private int ticks;

    private final ItemSlots slots = new ItemSlots() {
        @Override public int slots() { return 2; }
        @Override public ItemStack getStack(int slot) { return stacks[slot]; }
        @Override public void setStack(int slot, ItemStack stack) { stacks[slot] = stack; setChanged(); }
        @Override public boolean canInsert(int slot, ItemStack stack) { return slot == 0; }
        @Override public boolean canExtract(int slot) { return slot == 1; }
    };

    private final FluidTanks tanks = new FluidTanks() {
        @Override public int tanks() { return 1; }
        @Override public FluidAmount getFluid(int index) { return tank; }
        @Override public void setFluid(int index, FluidAmount fluid) { tank = fluid; setChanged(); syncToClient(); }
        @Override public int getCapacity(int index) { return 4000; }
        @Override public boolean canFill(int index, FluidAmount fluid) { return true; }
        @Override public boolean canDrain(int index) { return true; }
    };

    private final EnergyStore energyStore = new EnergyStore() {
        @Override public long getEnergy() { return energy; }
        @Override public void setEnergy(long value) { energy = value; setChanged(); }
        @Override public long getCapacity() { return 10_000; }
        @Override public long maxReceive() { return 1_000; }
        @Override public long maxExtract() { return 1_000; }
    };

    public PlatformTestBlockEntity(BlockPos pos, BlockState state) {
        super(DevTestContent.PLATFORM_TEST.get(), pos, state);
    }

    @Override
    public @Nullable ItemSlots itemsFor(@Nullable Direction side) {
        return slots;
    }

    @Override
    public @Nullable FluidTanks fluidsFor(@Nullable Direction side) {
        return tanks;
    }

    @Override
    public @Nullable EnergyStore energyFor(@Nullable Direction side) {
        return energyStore;
    }

    @Override
    public void serverTick() {
        if (++ticks % 20 != 0) {
            return;
        }
        if (!stacks[0].isEmpty() && (stacks[1].isEmpty() || (stacks[1].getItem() == stacks[0].getItem() && stacks[1].getCount() < stacks[1].getMaxStackSize()))) {
            ItemStack moved = stacks[0].copyWithCount(1);
            slots.setStack(0, stacks[0].copyWithCount(stacks[0].getCount() - 1));
            slots.setStack(1, stacks[1].isEmpty() ? moved : stacks[1].copyWithCount(stacks[1].getCount() + 1));
        }
        ItemTransfer below = neighbors().items(Direction.DOWN);
        if (below != null && !stacks[1].isEmpty()) {
            ItemStack remainder = below.insert(stacks[1], false);
            slots.setStack(1, remainder);
        }
        FluidTransfer above = neighbors().fluids(Direction.UP);
        if (above != null && !tank.isEmpty()) {
            int filled = above.fill(tank.withAmount(Math.min(250, tank.amount())), false);
            if (filled > 0) {
                tanks.setFluid(0, tank.withAmount(tank.amount() - filled));
            }
        }
        var aboveEnergy = neighbors().energy(Direction.UP);
        if (aboveEnergy != null && energy > 0) {
            long moved = aboveEnergy.insert(Math.min(100, energy), false);
            energyStore.setEnergy(energy - moved);
        }
    }

    @Override
    protected void saveData(DataWriter out) {
        out.putItem("Input", stacks[0]);
        out.putItem("Output", stacks[1]);
        out.putFluid("Tank", tank);
        out.putLong("Energy", energy);
    }

    @Override
    protected void loadData(DataReader in) {
        stacks[0] = in.getItem("Input");
        stacks[1] = in.getItem("Output");
        tank = in.getFluid("Tank");
        energy = in.getLong("Energy", 0);
    }

    @Override
    protected void saveSyncData(DataWriter out) {
        out.putFluid("Tank", tank);
    }

    @Override
    protected void loadSyncData(DataReader in) {
        tank = in.getFluid("Tank");
        if (level != null && level.isClientSide()) {
            FoodIndustrial.LOGGER.info("[be sync] {} tank={} {} mB", getBlockPos().toShortString(),
                tank.isEmpty() ? "empty" : PlatformTags.fluidId(tank.fluid()), tank.amount());
        }
    }
}
