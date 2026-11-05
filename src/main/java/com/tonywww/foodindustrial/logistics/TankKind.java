package com.tonywww.foodindustrial.logistics;

import com.tonywww.foodindustrial.platform.PlatformFluids;
import net.minecraft.world.level.material.Fluid;

/** The two fluid tank blocks (machines/03 §5, 2026-11-05). */
public enum TankKind {
    /** T1 wooden tank: 4,000 mB, liquids only, open top. */
    WOODEN("wooden_tank", 4_000, false, 1),
    /** T2 metal tank: 32,000 mB, liquids or gases; vertical columns of up to 8 share their storage. */
    METAL("metal_tank", 32_000, true, 8);

    /** mB moved down per batch of 4 ticks in drain mode. */
    public static final int DRAIN_PER_BATCH = 400;

    private final String id;
    private final int capacity;
    private final boolean gases;
    private final int maxColumn;

    TankKind(String id, int capacity, boolean gases, int maxColumn) {
        this.id = id;
        this.capacity = capacity;
        this.gases = gases;
        this.maxColumn = maxColumn;
    }

    public String id() {
        return id;
    }

    /** Capacity of one block. */
    public int capacity() {
        return capacity;
    }

    /** Longest column of tanks sharing their storage. */
    public int maxColumn() {
        return maxColumn;
    }

    public boolean stacks() {
        return maxColumn > 1;
    }

    public boolean accepts(Fluid fluid) {
        return gases || !PlatformFluids.isGas(fluid);
    }
}
