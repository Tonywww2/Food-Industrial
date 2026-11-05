package com.tonywww.foodindustrial.core.storage;

/**
 * An energy buffer in FE seen by automation. Implemented by business components such as
 * {@code EnergyBuffer}; {@code platform} wraps it as each loader's energy capability.
 */
public interface EnergyStore {
    long getEnergy();

    /** Sets the stored energy without validation and notifies the owner of the change. */
    void setEnergy(long energy);

    long getCapacity();

    /** Maximum FE accepted per transfer operation; 0 disables insertion. */
    long maxReceive();

    /** Maximum FE extracted per transfer operation; 0 disables extraction. */
    long maxExtract();
}
