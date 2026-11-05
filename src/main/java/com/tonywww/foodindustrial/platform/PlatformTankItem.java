package com.tonywww.foodindustrial.platform;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
//? if forge {
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;
//?} else {
/*import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.function.Supplier;
*///?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
*///?}
//? if >=26.1 {
/*import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;
*///?}

/**
 * An item that holds up to {@link #capacity()} mB of one fluid (gas cylinders), exposed through the
 * loaders' item fluid handler so machines, tanks and other mods can fill and empty it. Contents live
 * in the stack (NBT on 1.20.1, the {@code foodindustrial:fluid_content} component from 1.21 on); read
 * them with {@link PlatformFluidContainers#contained}.
 */
public class PlatformTankItem extends PlatformItem {
    private static final List<PlatformTankItem> ITEMS = new ArrayList<>();

    //? if neoforge {
    /*public static final Supplier<DataComponentType<SimpleFluidContent>> CONTENT = PlatformRegistry.register(
        Registries.DATA_COMPONENT_TYPE, "fluid_content",
        () -> DataComponentType.<SimpleFluidContent>builder().persistent(SimpleFluidContent.CODEC)
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());
    *///?}

    private final int capacity;
    private final Predicate<Fluid> accepts;

    public PlatformTankItem(Properties properties, int capacity, Predicate<Fluid> accepts) {
        super(properties);
        this.capacity = capacity;
        this.accepts = accepts;
        ITEMS.add(this);
    }

    public int capacity() {
        return capacity;
    }

    /** Whether the item takes {@code fluid}. */
    public boolean accepts(Fluid fluid) {
        return accepts.test(fluid);
    }

    //? if forge {
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemStack(stack, capacity) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return accepts(fluid.getFluid());
            }
        };
    }

    /** Registers the item fluid handlers. Called by the mod entrypoint. */
    public static void bind(IEventBus modBus) {
        // Forge: the handler comes from initCapabilities.
    }
    //?} else {
    /*/^* Registers the item fluid handlers. Called by the mod entrypoint. ^/
    public static void bind(IEventBus modBus) {
        modBus.addListener((RegisterCapabilitiesEvent event) -> {
            for (PlatformTankItem item : ITEMS) {
                register(event, item);
            }
        });
    }
    *///?}

    //? if neoforge && <26.1 {
    /*private static void register(RegisterCapabilitiesEvent event, PlatformTankItem item) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new FluidHandlerItemStack(CONTENT, stack, item.capacity) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return item.accepts(fluid.getFluid());
            }
        }, item);
    }
    *///?}

    //? if >=26.1 {
    /*private static void register(RegisterCapabilitiesEvent event, PlatformTankItem item) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new ItemAccessFluidHandler(access, CONTENT.get(), item.capacity) {
            @Override
            public boolean isValid(int index, FluidResource resource) {
                return super.isValid(index, resource) && (resource.isEmpty() || item.accepts(resource.getFluid()));
            }
        }, item);
    }
    *///?}
}
