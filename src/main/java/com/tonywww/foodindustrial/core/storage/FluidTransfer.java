package com.tonywww.foodindustrial.core.storage;

/**
 * Fluid access to a neighbouring block (any mod's tank), obtained from
 * {@code PlatformBlockEntity#neighbors()}.
 */
public interface FluidTransfer {
    /** Fills up to {@code fluid.amount()}; returns the amount accepted. */
    int fill(FluidAmount fluid, boolean simulate);

    /** Drains up to {@code maxAmount} of whatever the target offers first. */
    FluidAmount drain(int maxAmount, boolean simulate);

    /** Drains up to {@code fluid.amount()} of exactly this kind (fluid and purity). */
    FluidAmount drain(FluidAmount fluid, boolean simulate);
}
