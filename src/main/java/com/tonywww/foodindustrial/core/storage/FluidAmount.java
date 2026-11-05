package com.tonywww.foodindustrial.core.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * A quantity of one fluid with an optional purity, independent of the loaders' {@code FluidStack}
 * types. Conversion to and from loader stacks happens only in {@code platform}.
 *
 * @param fluid   the fluid (never a flowing variant for stored fluids)
 * @param amount  amount in mB, {@code >= 0}
 * @param purity  stored purity byte ({@code b = k - 128}), or {@link #NO_PURITY}
 */
public record FluidAmount(Fluid fluid, int amount, int purity) {
    /** Marker for fluids that carry no purity (external fluids, water). */
    public static final int NO_PURITY = Integer.MIN_VALUE;
    public static final FluidAmount EMPTY = new FluidAmount(Fluids.EMPTY, 0, NO_PURITY);

    public static final Codec<FluidAmount> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidAmount::fluid),
        Codec.INT.fieldOf("amount").forGetter(FluidAmount::amount),
        Codec.BYTE.optionalFieldOf("purity").forGetter(f -> f.hasPurity()
            ? java.util.Optional.of((byte) f.purity) : java.util.Optional.empty())
    ).apply(instance, (fluid, amount, purity) -> of(fluid, amount, purity.map(b -> (int) b).orElse(NO_PURITY))));

    public FluidAmount {
        if (amount < 0) {
            throw new IllegalArgumentException("Negative fluid amount: " + amount);
        }
        if (purity != NO_PURITY && (purity < Byte.MIN_VALUE || purity > Byte.MAX_VALUE)) {
            throw new IllegalArgumentException("Purity byte out of range: " + purity);
        }
    }

    public static FluidAmount of(Fluid fluid, int amount) {
        return of(fluid, amount, NO_PURITY);
    }

    public static FluidAmount of(Fluid fluid, int amount, int purity) {
        if (fluid == Fluids.EMPTY || amount <= 0) {
            return EMPTY;
        }
        return new FluidAmount(fluid, amount, purity);
    }

    public boolean isEmpty() {
        return fluid == Fluids.EMPTY || amount <= 0;
    }

    public boolean hasPurity() {
        return purity != NO_PURITY;
    }

    /** Same fluid and same purity: the two amounts can share one tank. */
    public boolean isSameKind(FluidAmount other) {
        return fluid == other.fluid && purity == other.purity;
    }

    public FluidAmount withAmount(int newAmount) {
        return of(fluid, newAmount, purity);
    }

    public FluidAmount withPurity(int newPurity) {
        return isEmpty() ? EMPTY : new FluidAmount(fluid, amount, newPurity);
    }
}
