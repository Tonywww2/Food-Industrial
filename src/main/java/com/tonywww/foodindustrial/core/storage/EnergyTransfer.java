package com.tonywww.foodindustrial.core.storage;

/**
 * Energy access (FE) to a neighbouring block, obtained from {@code PlatformBlockEntity#neighbors()}.
 */
public interface EnergyTransfer {
    /** Inserts up to {@code amount}; returns the amount accepted. */
    long insert(long amount, boolean simulate);

    /** Extracts up to {@code amount}; returns the amount extracted. */
    long extract(long amount, boolean simulate);
}
