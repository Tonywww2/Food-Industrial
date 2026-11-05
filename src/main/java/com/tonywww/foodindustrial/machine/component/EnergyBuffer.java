package com.tonywww.foodindustrial.machine.component;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.EnergyStore;

/**
 * FE buffer of a T2+ machine (machines/03 §2): accepts energy from automation, never gives it out.
 * Receiving energy reports to the owner, which wakes a machine waiting for power.
 */
public final class EnergyBuffer implements EnergyStore {
    private final long capacity;
    private final long maxReceive;
    private final Runnable onReceive;
    private long energy;

    public EnergyBuffer(long capacity, long maxReceive, Runnable onReceive) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.onReceive = onReceive;
    }

    @Override
    public long getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(long value) {
        boolean gained = value > energy;
        energy = Math.max(0, Math.min(capacity, value));
        if (gained) {
            onReceive.run();
        }
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long maxReceive() {
        return maxReceive;
    }

    @Override
    public long maxExtract() {
        return 0;
    }

    /** Takes {@code amount} FE if all of it is there. */
    public boolean consume(long amount) {
        if (energy < amount) {
            return false;
        }
        energy -= amount;
        return true;
    }

    public void save(DataWriter out) {
        if (energy > 0) {
            out.putLong("Energy", energy);
        }
    }

    public void load(DataReader in) {
        energy = Math.max(0, Math.min(capacity, in.getLong("Energy", 0)));
    }
}
