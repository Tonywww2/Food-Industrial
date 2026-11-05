package com.tonywww.foodindustrial.core.storage;

/**
 * Fluid tanks seen by automation. Implemented by business components such as {@code TankSet};
 * {@code platform} wraps it as each loader's fluid capability.
 * <p>
 * A tank holds a single {@link FluidAmount#isSameKind kind} (fluid + purity) at a time.
 */
public interface FluidTanks {
    int tanks();

    FluidAmount getFluid(int tank);

    /** Replaces the contents without validation and notifies the owner of the change. */
    void setFluid(int tank, FluidAmount fluid);

    int getCapacity(int tank);

    /** Whether automation may fill this kind of fluid into the tank (amount is ignored). */
    boolean canFill(int tank, FluidAmount fluid);

    /** Whether automation may drain from the tank. */
    boolean canDrain(int tank);

    /**
     * Whether {@code offered} may join the non-empty contents {@code current} of a tank. Default: the
     * same kind (fluid and purity); this mod's tanks also take the same fluid at another purity.
     */
    default boolean joins(int tank, FluidAmount current, FluidAmount offered) {
        return current.isSameKind(offered);
    }

    /**
     * Contents after {@code filled} mB of {@code offered} joined {@code current} (empty or accepted by
     * {@link #joins}). Default: the offered kind; this mod's tanks average the purity by mB.
     */
    default FluidAmount joined(int tank, FluidAmount current, FluidAmount offered, int filled) {
        return offered.withAmount(current.amount() + filled);
    }
}
