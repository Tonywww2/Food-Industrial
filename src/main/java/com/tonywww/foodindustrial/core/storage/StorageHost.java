package com.tonywww.foodindustrial.core.storage;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Implemented by block entities whose storage is exposed to automation. The platform layer asks for
 * the view of each face and wraps it as the loader's item / fluid / energy capability.
 * <p>
 * Return {@code null} to expose nothing on that face. The returned views must stay the same object
 * until {@code PlatformBlockEntity#invalidateStorage()} is called (for example after a side
 * configuration change), because loaders cache them.
 *
 * @see com.tonywww.foodindustrial.platform.machine.PlatformMachineCapabilities
 */
public interface StorageHost {
    @Nullable
    default ItemSlots itemsFor(@Nullable Direction side) {
        return null;
    }

    @Nullable
    default FluidTanks fluidsFor(@Nullable Direction side) {
        return null;
    }

    @Nullable
    default EnergyStore energyFor(@Nullable Direction side) {
        return null;
    }
}
