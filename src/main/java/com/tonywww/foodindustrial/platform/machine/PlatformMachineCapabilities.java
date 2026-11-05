package com.tonywww.foodindustrial.platform.machine;

import com.tonywww.foodindustrial.core.storage.EnergyStore;
import com.tonywww.foodindustrial.core.storage.FluidTanks;
import com.tonywww.foodindustrial.core.storage.ItemSlots;
import com.tonywww.foodindustrial.core.storage.StorageHost;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
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
 * Exposes {@link StorageHost} block entities as each loader's item, fluid and energy capability:
 * Forge 1.20.1 capabilities with {@code LazyOptional}, NeoForge 1.21.1 {@code IItemHandler} /
 * {@code IFluidHandler} / {@code IEnergyStorage}, NeoForge 26.1 transactional
 * {@code ResourceHandler} / {@code EnergyHandler}.
 * <p>
 * Call {@link #register} once per block entity type during mod construction. Adapters are created on
 * first query, cached per face and dropped by {@link PlatformBlockEntity#invalidateStorage()}.
 */
public final class PlatformMachineCapabilities {
    private static final int ITEMS = 0;
    private static final int FLUIDS = 1;
    private static final int ENERGY = 2;
    private static final Object NONE = new Object();
    private static final List<Supplier<? extends BlockEntityType<? extends PlatformBlockEntity>>> TYPES = new ArrayList<>();

    private PlatformMachineCapabilities() {
    }

    /** Exposes the storage of every block entity of {@code type} that implements {@link StorageHost}. */
    public static void register(Supplier<? extends BlockEntityType<? extends PlatformBlockEntity>> type) {
        TYPES.add(type);
    }

    private static int sideIndex(@Nullable Direction side) {
        return side == null ? 6 : side.ordinal();
    }

    /** Per block entity cache: 3 kinds x 7 faces (6 directions + null). */
    private static Object[] cache(PlatformBlockEntity blockEntity) {
        if (blockEntity.capabilityState == null) {
            blockEntity.capabilityState = new Object[21];
        }
        return (Object[]) blockEntity.capabilityState;
    }

    /** The adapter for one kind and face, or {@code null} when the host exposes nothing there. */
    @Nullable
    private static Object adapter(PlatformBlockEntity blockEntity, int kind, @Nullable Direction side) {
        if (!(blockEntity instanceof StorageHost host) || blockEntity.isRemoved()) {
            return null;
        }
        Object[] cache = cache(blockEntity);
        int index = kind * 7 + sideIndex(side);
        Object cached = cache[index];
        if (cached == null) {
            cached = switch (kind) {
                case ITEMS -> {
                    ItemSlots view = host.itemsFor(side);
                    yield view == null ? NONE : itemAdapter(view);
                }
                case FLUIDS -> {
                    FluidTanks view = host.fluidsFor(side);
                    yield view == null ? NONE : fluidAdapter(view);
                }
                default -> {
                    EnergyStore view = host.energyFor(side);
                    yield view == null ? NONE : energyAdapter(view);
                }
            };
            cache[index] = cached;
        }
        return cached == NONE ? null : cached;
    }

    //? if <26.1 {
    private static Object itemAdapter(ItemSlots view) { return new LegacyStorage.Items(view); }
    private static Object fluidAdapter(FluidTanks view) { return new LegacyStorage.Fluids(view); }
    private static Object energyAdapter(EnergyStore view) { return new LegacyStorage.Energy(view); }
    //?} else {
    /*private static Object itemAdapter(ItemSlots view) { return new TransferStorage.Items(view); }
    private static Object fluidAdapter(FluidTanks view) { return new TransferStorage.Fluids(view); }
    private static Object energyAdapter(EnergyStore view) { return new TransferStorage.Energy(view); }
    *///?}

    //? if forge {
    /** Forge capability lookup, called from {@link PlatformBlockEntity#getCapability}. */
    @Nullable
    @SuppressWarnings("unchecked")
    static <T> LazyOptional<T> forgeCapability(PlatformBlockEntity blockEntity, Capability<T> capability, @Nullable Direction side) {
        int kind;
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            kind = ITEMS;
        } else if (capability == ForgeCapabilities.FLUID_HANDLER) {
            kind = FLUIDS;
        } else if (capability == ForgeCapabilities.ENERGY) {
            kind = ENERGY;
        } else {
            return null;
        }
        Object adapter = adapter(blockEntity, kind, side);
        if (adapter == null) {
            return null;
        }
        Object[] optionals = forgeOptionals(blockEntity);
        int index = kind * 7 + sideIndex(side);
        LazyOptional<?> optional = (LazyOptional<?>) optionals[index];
        if (optional == null) {
            optional = LazyOptional.of(() -> adapter);
            optionals[index] = optional;
        }
        return (LazyOptional<T>) optional;
    }

    private static Object[] forgeOptionals(PlatformBlockEntity blockEntity) {
        if (blockEntity.forgeOptionals == null) {
            blockEntity.forgeOptionals = new Object[21];
        }
        return blockEntity.forgeOptionals;
    }

    static void invalidate(PlatformBlockEntity blockEntity) {
        Object[] optionals = blockEntity.forgeOptionals;
        if (optionals != null) {
            for (Object optional : optionals) {
                if (optional != null) {
                    ((LazyOptional<?>) optional).invalidate();
                }
            }
        }
        blockEntity.forgeOptionals = null;
        blockEntity.capabilityState = null;
    }

    /** No event registration on Forge: capabilities come from {@code getCapability}. */
    public static void bind(IEventBus modBus) {
    }
    //?} else {
    /*static void invalidate(PlatformBlockEntity blockEntity) {
        blockEntity.capabilityState = null;
        blockEntity.invalidateCapabilities();
    }

    /^* Registers the capability providers on the mod bus. Called by the mod entrypoint. ^/
    public static void bind(IEventBus modBus) {
        modBus.addListener(PlatformMachineCapabilities::onRegisterCapabilities);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        for (Supplier<? extends BlockEntityType<? extends PlatformBlockEntity>> type : TYPES) {
            registerType(event, (BlockEntityType) type.get());
        }
    }
    *///?}

    //? if neoforge && <26.1 {
    /*private static <B extends PlatformBlockEntity> void registerType(RegisterCapabilitiesEvent event, BlockEntityType<B> type) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (blockEntity, side) -> (IItemHandler) adapter(blockEntity, ITEMS, side));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type, (blockEntity, side) -> (IFluidHandler) adapter(blockEntity, FLUIDS, side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, (blockEntity, side) -> (IEnergyStorage) adapter(blockEntity, ENERGY, side));
    }
    *///?}

    //? if >=26.1 {
    /*@SuppressWarnings("unchecked")
    private static <B extends PlatformBlockEntity> void registerType(RegisterCapabilitiesEvent event, BlockEntityType<B> type) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, type, (blockEntity, side) -> (ResourceHandler<ItemResource>) adapter(blockEntity, ITEMS, side));
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, type, (blockEntity, side) -> (ResourceHandler<FluidResource>) adapter(blockEntity, FLUIDS, side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, type, (blockEntity, side) -> (EnergyHandler) adapter(blockEntity, ENERGY, side));
    }
    *///?}
}
