package com.tonywww.foodindustrial.core.storage;

import net.minecraft.world.item.ItemStack;

/**
 * Item slots seen by automation (hoppers, pipes, AE2). Implemented by business components such as
 * {@code MachineInventory}; {@code platform} wraps it as each loader's item capability and performs
 * insertion / extraction on top of these primitives.
 * <p>
 * Implementations must not mutate the stack returned by {@link #getStack} in place; the platform
 * adapters always write a new stack through {@link #setStack}.
 */
public interface ItemSlots {
    int slots();

    ItemStack getStack(int slot);

    /** Replaces the stack without validation and notifies the owner of the change. */
    void setStack(int slot, ItemStack stack);

    /** Maximum count for this slot (further limited by the item's own max stack size). */
    default int getSlotLimit(int slot) {
        return 64;
    }

    /** Whether automation may insert {@code stack} into {@code slot} (role and item checks only). */
    boolean canInsert(int slot, ItemStack stack);

    /** Whether automation may extract from {@code slot}. */
    boolean canExtract(int slot);
}
