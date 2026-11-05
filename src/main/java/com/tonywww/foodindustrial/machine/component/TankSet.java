package com.tonywww.foodindustrial.machine.component;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformFluids;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Fluid tanks of a machine (machines/02 §4.1, machines/03 §2, §4): input tanks (FI) first, then output
 * tanks (FO). A tank takes liquids, gases or both, can be locked to a fluid (with purity), and merges
 * the same fluid at another purity into the mB-weighted average, rounded down (additives/10 §2.2
 * rule 4).
 */
public final class TankSet {
    /** What a tank accepts. */
    public enum Phase {
        LIQUID, GAS, BOTH;

        boolean accepts(FluidAmount fluid) {
            boolean gas = PlatformFluids.isGas(fluid.fluid());
            return this == BOTH || (this == GAS) == gas;
        }
    }

    private final FluidAmount[] tanks;
    private final FluidAmount[] locks;
    private final Phase[] phases;
    private final VentMode[] vents;
    private final int inputs;
    private final int capacity;
    private final Consumer<SlotRole> onChange;

    public TankSet(int inputs, int outputs, int capacity, Consumer<SlotRole> onChange) {
        this.inputs = inputs;
        this.capacity = capacity;
        this.onChange = onChange;
        tanks = new FluidAmount[inputs + outputs];
        locks = new FluidAmount[inputs + outputs];
        phases = new Phase[inputs + outputs];
        vents = new VentMode[inputs + outputs];
        Arrays.fill(vents, VentMode.OFF);
        Arrays.fill(tanks, FluidAmount.EMPTY);
        Arrays.fill(locks, FluidAmount.EMPTY);
        Arrays.fill(phases, Phase.BOTH);
    }

    public int size() {
        return tanks.length;
    }

    public int inputs() {
        return inputs;
    }

    public int outputs() {
        return tanks.length - inputs;
    }

    public SlotRole role(int tank) {
        return tank < inputs ? SlotRole.INPUT : SlotRole.OUTPUT;
    }

    public int capacity() {
        return capacity;
    }

    public FluidAmount get(int tank) {
        return tanks[tank];
    }

    public void set(int tank, FluidAmount fluid) {
        tanks[tank] = fluid.isEmpty() ? FluidAmount.EMPTY : fluid;
        onChange.accept(role(tank));
    }

    public Phase phase(int tank) {
        return phases[tank];
    }

    public void setPhase(int tank, Phase phase) {
        phases[tank] = phase;
        onChange.accept(role(tank));
    }

    public FluidAmount lock(int tank) {
        return locks[tank];
    }

    /** Locks a tank to a fluid kind (amount ignored); empty unlocks. */
    public void setLock(int tank, FluidAmount fluid) {
        locks[tank] = fluid.isEmpty() ? FluidAmount.EMPTY : fluid.withAmount(1);
        onChange.accept(role(tank));
    }

    /** Whether the tank's phase and lock allow this fluid (amount ignored). */
    public boolean allows(int tank, FluidAmount fluid) {
        if (fluid.isEmpty() || !phases[tank].accepts(fluid)) {
            return false;
        }
        FluidAmount lock = locks[tank];
        return lock.isEmpty() || lock.isSameKind(fluid);
    }

    /** Whether {@code fluid} can share the tank with its current contents. */
    public boolean joins(int tank, FluidAmount fluid) {
        FluidAmount current = tanks[tank];
        return current.isEmpty() || current.fluid() == fluid.fluid();
    }

    /** Contents after {@code amount} mB of {@code fluid} joined the tank. */
    public FluidAmount joined(int tank, FluidAmount fluid, int amount) {
        return Purity.merge(tanks[tank], fluid.withAmount(amount));
    }

    /** Free space for {@code fluid} in a tank (0 when it cannot go there). */
    public int space(int tank, FluidAmount fluid) {
        return joins(tank, fluid) ? capacity - tanks[tank].amount() : 0;
    }

    /**
     * Puts a product into the output tanks: the tank already holding this fluid first, then an empty
     * one. A product goes into a single tank.
     *
     * @return the amount that did not fit
     */
    public int fillOutput(FluidAmount fluid, boolean simulate) {
        int target = outputTankFor(fluid);
        if (target < 0) {
            return fluid.amount();
        }
        int moved = Math.min(space(target, fluid), fluid.amount());
        if (!simulate && moved > 0) {
            set(target, joined(target, fluid, moved));
        }
        return fluid.amount() - moved;
    }

    private int outputTankFor(FluidAmount fluid) {
        for (int tank = inputs; tank < tanks.length; tank++) {
            if (!tanks[tank].isEmpty() && tanks[tank].fluid() == fluid.fluid() && allows(tank, fluid)) {
                return tank;
            }
        }
        for (int tank = inputs; tank < tanks.length; tank++) {
            if (tanks[tank].isEmpty() && allows(tank, fluid)) {
                return tank;
            }
        }
        return -1;
    }

    /** Whether all fluids fit into the output tanks together (each into one tank). */
    public boolean outputsFit(FluidAmount[] products, int length) {
        int[] used = new int[tanks.length];
        net.minecraft.world.level.material.Fluid[] claimed = new net.minecraft.world.level.material.Fluid[tanks.length];
        for (int i = 0; i < length; i++) {
            FluidAmount fluid = products[i];
            if (fluid.isEmpty()) {
                continue;
            }
            int target = -1;
            for (int tank = inputs; tank < tanks.length && target < 0; tank++) {
                net.minecraft.world.level.material.Fluid held = !tanks[tank].isEmpty() ? tanks[tank].fluid() : claimed[tank];
                if (held == fluid.fluid() && allows(tank, fluid)) {
                    target = tank;
                }
            }
            for (int tank = inputs; tank < tanks.length && target < 0; tank++) {
                if (tanks[tank].isEmpty() && claimed[tank] == null && allows(tank, fluid)) {
                    target = tank;
                }
            }
            if (target < 0 || tanks[target].amount() + used[target] + fluid.amount() > capacity) {
                return false;
            }
            used[target] += fluid.amount();
            claimed[target] = fluid.fluid();
        }
        return true;
    }

    /** Drains {@code amount} mB from a tank. */
    public void drain(int tank, int amount) {
        FluidAmount current = tanks[tank];
        set(tank, current.withAmount(Math.max(0, current.amount() - amount)));
    }

    public boolean any(SlotRole role) {
        int from = role == SlotRole.INPUT ? 0 : inputs;
        int to = role == SlotRole.INPUT ? inputs : tanks.length;
        for (int tank = from; tank < to; tank++) {
            if (!tanks[tank].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** Venting switch of an output tank (machines/03 §4); always {@link VentMode#OFF} for input tanks. */
    public VentMode vent(int tank) {
        return vents[tank];
    }

    public void setVent(int tank, VentMode mode) {
        if (role(tank) == SlotRole.OUTPUT) {
            vents[tank] = mode;
        }
    }

    /** Whether any venting switch is on. */
    public boolean hasVents() {
        for (VentMode vent : vents) {
            if (vent != VentMode.OFF) {
                return true;
            }
        }
        return false;
    }

    /** Venting switches are settings: saved with the configuration. */
    public void saveVents(DataWriter out) {
        for (int i = 0; i < vents.length; i++) {
            if (vents[i] != VentMode.OFF) {
                out.putByte("TankVent" + i, (byte) vents[i].ordinal());
            }
        }
    }

    public void loadVents(DataReader in) {
        for (int i = 0; i < vents.length; i++) {
            vents[i] = role(i) == SlotRole.OUTPUT ? VentMode.byIndex(in.getByte("TankVent" + i, (byte) 0)) : VentMode.OFF;
        }
    }

    /** Whether any tank is locked. */
    public boolean hasLocks() {
        for (FluidAmount lock : locks) {
            if (!lock.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** Locks are settings (machines/04 §4): saved with the configuration. */
    public void saveLocks(DataWriter out) {
        for (int i = 0; i < locks.length; i++) {
            if (!locks[i].isEmpty()) {
                out.putFluid("TankLock" + i, locks[i]);
            }
        }
    }

    public void loadLocks(DataReader in) {
        for (int i = 0; i < locks.length; i++) {
            FluidAmount lock = in.getFluid("TankLock" + i);
            locks[i] = lock.isEmpty() ? FluidAmount.EMPTY : lock.withAmount(1);
        }
    }

    public void save(DataWriter out) {
        for (int i = 0; i < tanks.length; i++) {
            if (!tanks[i].isEmpty()) {
                out.putFluid("Tank" + i, tanks[i]);
            }
            if (phases[i] != Phase.BOTH) {
                out.putByte("TankPhase" + i, (byte) phases[i].ordinal());
            }
        }
    }

    public void load(DataReader in) {
        for (int i = 0; i < tanks.length; i++) {
            tanks[i] = in.getFluid("Tank" + i);
            int phase = in.getByte("TankPhase" + i, (byte) Phase.BOTH.ordinal());
            phases[i] = Phase.values()[Math.max(0, Math.min(Phase.values().length - 1, phase))];
        }
    }
}
