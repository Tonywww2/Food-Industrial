package com.tonywww.foodindustrial.platform.machine;

import com.tonywww.foodindustrial.core.storage.EnergyTransfer;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.core.storage.ItemTransfer;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
//? if forge {
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
//?} else {
/*import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
*///?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
*///?}
//? if >=26.1 {
/*import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
*///?}

/**
 * Cached item / fluid / energy access to the six neighbours of a block entity (server side only).
 * Forge 1.20.1 caches the neighbour's {@code LazyOptional} and drops it on invalidation; NeoForge uses
 * {@code BlockCapabilityCache}, which tracks block entity replacement by itself. Obtain through
 * {@link PlatformBlockEntity#neighbors()}; never query capabilities directly in a tick.
 */
public final class PlatformNeighborCache {
    private static final int ITEMS = 0;
    private static final int FLUIDS = 1;
    private static final int ENERGY = 2;

    private final PlatformBlockEntity owner;
    /** Business-facing wrappers, index kind * 6 + direction. */
    private final Object[] wrappers = new Object[18];

    PlatformNeighborCache(PlatformBlockEntity owner) {
        this.owner = owner;
    }

    /** Items of the block on {@code side}, or {@code null} if it has no item access on the facing face. */
    @Nullable
    public ItemTransfer items(Direction side) {
        return (ItemTransfer) get(ITEMS, side);
    }

    /** Fluids of the block on {@code side}, or {@code null}. */
    @Nullable
    public FluidTransfer fluids(Direction side) {
        return (FluidTransfer) get(FLUIDS, side);
    }

    /** Energy of the block on {@code side}, or {@code null}. */
    @Nullable
    public EnergyTransfer energy(Direction side) {
        return (EnergyTransfer) get(ENERGY, side);
    }

    //? if forge {
    void onNeighborChanged() {
        java.util.Arrays.fill(wrappers, null);
    }

    @Nullable
    private Object get(int kind, Direction side) {
        int index = kind * 6 + side.ordinal();
        Object wrapper = wrappers[index];
        if (wrapper != null) {
            return wrapper;
        }
        Level level = owner.getLevel();
        if (level == null || level.isClientSide()) {
            return null;
        }
        BlockEntity neighbor = level.getBlockEntity(owner.getBlockPos().relative(side));
        if (neighbor == null) {
            return null;
        }
        LazyOptional<?> optional = neighbor.getCapability(capability(kind), side.getOpposite());
        Object handler = optional.orElse(null);
        if (handler == null) {
            return null;
        }
        wrapper = switch (kind) {
            case ITEMS -> new LegacyStorage.ItemTarget((IItemHandler) handler);
            case FLUIDS -> new LegacyStorage.FluidTarget((IFluidHandler) handler);
            default -> new LegacyStorage.EnergyTarget((IEnergyStorage) handler);
        };
        wrappers[index] = wrapper;
        optional.addListener(invalidated -> wrappers[index] = null);
        return wrapper;
    }

    private static Capability<?> capability(int kind) {
        return switch (kind) {
            case ITEMS -> ForgeCapabilities.ITEM_HANDLER;
            case FLUIDS -> ForgeCapabilities.FLUID_HANDLER;
            default -> ForgeCapabilities.ENERGY;
        };
    }
    //?} else {
    /*private final BlockCapabilityCache<?, ?>[] caches = new BlockCapabilityCache<?, ?>[18];
    private final Object[] handlers = new Object[18];

    void onNeighborChanged() {
        // BlockCapabilityCache is notified by NeoForge itself.
    }

    @Nullable
    private Object get(int kind, Direction side) {
        if (!(owner.getLevel() instanceof ServerLevel level)) {
            return null;
        }
        int index = kind * 6 + side.ordinal();
        BlockCapabilityCache<?, ?> cache = caches[index];
        if (cache == null) {
            cache = BlockCapabilityCache.create(capability(kind), level, owner.getBlockPos().relative(side), side.getOpposite(),
                () -> !owner.isRemoved(), () -> wrappers[index] = null);
            caches[index] = cache;
        }
        Object handler = cache.getCapability();
        if (handler == null) {
            return null;
        }
        if (wrappers[index] == null || handlers[index] != handler) {
            handlers[index] = handler;
            wrappers[index] = wrap(kind, handler);
        }
        return wrappers[index];
    }
    *///?}

    //? if neoforge && <26.1 {
    /*private static BlockCapability<?, Direction> capability(int kind) {
        return switch (kind) {
            case ITEMS -> Capabilities.ItemHandler.BLOCK;
            case FLUIDS -> Capabilities.FluidHandler.BLOCK;
            default -> Capabilities.EnergyStorage.BLOCK;
        };
    }

    private static Object wrap(int kind, Object handler) {
        return switch (kind) {
            case ITEMS -> new LegacyStorage.ItemTarget((IItemHandler) handler);
            case FLUIDS -> new LegacyStorage.FluidTarget((IFluidHandler) handler);
            default -> new LegacyStorage.EnergyTarget((IEnergyStorage) handler);
        };
    }
    *///?}

    //? if >=26.1 {
    /*private static BlockCapability<?, Direction> capability(int kind) {
        return switch (kind) {
            case ITEMS -> Capabilities.Item.BLOCK;
            case FLUIDS -> Capabilities.Fluid.BLOCK;
            default -> Capabilities.Energy.BLOCK;
        };
    }

    @SuppressWarnings("unchecked")
    private static Object wrap(int kind, Object handler) {
        return switch (kind) {
            case ITEMS -> new TransferStorage.ItemTarget((ResourceHandler<ItemResource>) handler);
            case FLUIDS -> new TransferStorage.FluidTarget((ResourceHandler<FluidResource>) handler);
            default -> new TransferStorage.EnergyTarget((EnergyHandler) handler);
        };
    }
    *///?}
}
