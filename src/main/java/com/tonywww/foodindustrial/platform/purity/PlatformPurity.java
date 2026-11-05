package com.tonywww.foodindustrial.platform.purity;

import net.minecraft.world.item.ItemStack;
//? if >=1.21 {
/*import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;
*///?} else {
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
//?}

/**
 * Raw purity storage on item stacks: the byte tag {@code Purity} on 1.20.1, the byte data component
 * {@code foodindustrial:purity} from 1.21 on. Values are the stored byte ({@code b = k - 128}); use the
 * business {@code Purity} API rather than calling this directly.
 * <p>
 * Fluid purity is carried by {@code FluidAmount}; conversion to loader fluid stacks happens in
 * {@link com.tonywww.foodindustrial.platform.PlatformFluids}.
 */
public final class PlatformPurity {
    /** Returned when a stack carries no purity. Same value as {@code FluidAmount.NO_PURITY}. */
    public static final int NONE = Integer.MIN_VALUE;
    /** NBT key used on 1.20.1 item and fluid stacks. */
    public static final String TAG_KEY = "Purity";

    // The foodindustrial:purity data component (1.21+).
    //? if >=1.21 {
    /*public static final Supplier<DataComponentType<Byte>> COMPONENT = PlatformRegistry.register(
        Registries.DATA_COMPONENT_TYPE, "purity",
        () -> DataComponentType.<Byte>builder().persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE).build());
    *///?}

    private PlatformPurity() {
    }

    /** Registers the storage (data component) during mod construction. */
    public static void bootstrap() {
    }

    /** Stored purity byte, or {@link #NONE}. */
    public static int get(ItemStack stack) {
        //? if >=1.21 {
        /*Byte value = stack.get(COMPONENT.get());
        return value == null ? NONE : value;
        *///?} else {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(TAG_KEY, Tag.TAG_BYTE) ? tag.getByte(TAG_KEY) : NONE;
        //?}
    }

    /** Writes the purity byte onto the stack (in place). */
    public static void set(ItemStack stack, byte purity) {
        //? if >=1.21 {
        /*stack.set(COMPONENT.get(), purity);
        *///?} else {
        stack.getOrCreateTag().putByte(TAG_KEY, purity);
        //?}
    }

    /** Removes the purity from the stack (in place). */
    public static void clear(ItemStack stack) {
        //? if >=1.21 {
        /*stack.remove(COMPONENT.get());
        *///?} else {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(TAG_KEY);
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
        //?}
    }
}
