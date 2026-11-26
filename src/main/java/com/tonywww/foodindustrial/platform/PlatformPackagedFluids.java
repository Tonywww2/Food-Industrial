package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.platform.food.PlatformFoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
//? if forge {
import com.tonywww.foodindustrial.FoodIndustrial;
import net.minecraft.core.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
//?}
//? if neoforge && <26.1 {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
*///?}
//? if >=26.1 {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
*///?}

/**
 * Packaged liquid products (foods/08 §1 rule 5, I47): a bottle or jar of a product holds a fixed amount of its bulk
 * fluid. Through the loaders' item fluid handler, tanks and machines can pour it out (all of it at once, like a bucket);
 * the fluid takes the product's quality and the item becomes its container, or nothing when the container is not
 * given back. Nothing can be filled in this way: products are filled by their packaging recipes.
 */
public final class PlatformPackagedFluids {
    /**
     * @param item      the packaged product
     * @param fluid     the fluid it pours out
     * @param amount    mB in one item
     * @param container what is left after pouring, or {@code null} for nothing
     */
    public record Entry(Supplier<? extends Item> item, Supplier<? extends Fluid> fluid, int amount,
                        @Nullable Supplier<? extends Item> container) {
    }

    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static volatile Map<Item, Entry> byItem;

    private PlatformPackagedFluids() {
    }

    /** Declares a packaged product. Call during mod construction. */
    public static void register(Supplier<? extends Item> item, Supplier<? extends Fluid> fluid, int amount,
                                @Nullable Supplier<? extends Item> container) {
        ENTRIES.add(new Entry(item, fluid, amount, container));
    }

    /** The packaged product of {@code item}, if it is one. */
    @Nullable
    public static Entry of(Item item) {
        Map<Item, Entry> map = byItem;
        if (map == null) {
            map = new IdentityHashMap<>();
            for (Entry entry : ENTRIES) {
                map.put(entry.item().get(), entry);
            }
            byItem = map;
        }
        return map.get(item);
    }

    private static ItemStack emptied(Entry entry) {
        return entry.container() == null ? ItemStack.EMPTY : new ItemStack(entry.container().get());
    }

    //? if <26.1 {
    /** The item fluid handler of one packaged product stack. */
    static final class Handler implements IFluidHandlerItem {
        private final Entry entry;
        private ItemStack container;

        Handler(ItemStack container, Entry entry) {
            this.container = container;
            this.entry = entry;
        }

        private FluidStack contents() {
            if (container.isEmpty() || container.getItem() != entry.item().get()) {
                return FluidStack.EMPTY;
            }
            FluidStack stack = new FluidStack(entry.fluid().get(), entry.amount());
            int quality = PlatformFoodData.quality(container);
            if (quality != 0) {
                PlatformFluids.setStackQuality(stack, quality);
            }
            return stack;
        }

        @Override
        public ItemStack getContainer() {
            return container;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return contents();
        }

        @Override
        public int getTankCapacity(int tank) {
            return entry.amount();
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidStack contents = contents();
            return contents.isEmpty() || resource.getFluid() != contents.getFluid() ? FluidStack.EMPTY : drain(resource.getAmount(), action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack contents = contents();
            if (contents.isEmpty() || maxDrain < entry.amount()) {
                return FluidStack.EMPTY;
            }
            if (action.execute()) {
                container = emptied(entry);
            }
            return contents;
        }
    }
    //?}

    //? if forge {
    /** Attaches the handlers to packaged product stacks. Called by the mod entrypoint. */
    public static void bind(IEventBus modBus) {
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, (AttachCapabilitiesEvent<ItemStack> event) -> {
            if (ENTRIES.isEmpty()) {
                return;
            }
            Entry entry = of(event.getObject().getItem());
            if (entry != null) {
                event.addCapability(PlatformIdentifier.id(FoodIndustrial.MOD_ID, "packaged_fluid"), new Provider(new Handler(event.getObject(), entry)));
            }
        });
    }

    private static final class Provider implements ICapabilityProvider {
        private final LazyOptional<IFluidHandlerItem> holder;

        Provider(Handler handler) {
            holder = LazyOptional.of(() -> handler);
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
            return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, holder);
        }
    }
    //?}

    //? if neoforge && <26.1 {
    /*/^* Registers the handlers of the packaged products. Called by the mod entrypoint. ^/
    public static void bind(IEventBus modBus) {
        modBus.addListener((RegisterCapabilitiesEvent event) -> {
            for (Entry entry : ENTRIES) {
                event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new Handler(stack, entry), entry.item().get());
            }
        });
    }
    *///?}

    //? if >=26.1 {
    /*/^* Registers the handlers of the packaged products. Called by the mod entrypoint. ^/
    public static void bind(IEventBus modBus) {
        modBus.addListener((RegisterCapabilitiesEvent event) -> {
            for (Entry entry : ENTRIES) {
                event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new Handler(access, entry), entry.item().get());
            }
        });
    }

    /^* Resource handler (26.1 transfer API) of one packaged product: all of it pours out at once. ^/
    static final class Handler extends ItemAccessResourceHandler<FluidResource> {
        private final Entry entry;

        Handler(ItemAccess access, Entry entry) {
            super(access, 1);
            this.entry = entry;
        }

        @Override
        protected FluidResource getResourceFrom(ItemResource access, int index) {
            if (access.getItem() != entry.item().get()) {
                return FluidResource.EMPTY;
            }
            FluidResource resource = FluidResource.of(entry.fluid().get());
            Integer quality = access.get(PlatformFoodData.QUALITY.get());
            return quality == null || quality == 0 ? resource : resource.with(PlatformFoodData.QUALITY.get(), quality);
        }

        @Override
        protected int getAmountFrom(ItemResource access, int index) {
            return getResourceFrom(access, index).isEmpty() ? 0 : entry.amount();
        }

        @Override
        protected ItemResource update(ItemResource access, int index, FluidResource resource, int amount) {
            if (amount == 0) {
                return entry.container() == null ? ItemResource.EMPTY : ItemResource.of(entry.container().get());
            }
            return amount == entry.amount() ? access : ItemResource.EMPTY;
        }

        @Override
        protected int getCapacity(int index, FluidResource resource) {
            return entry.amount();
        }
    }
    *///?}
}
