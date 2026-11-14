package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.purity.PlatformPurity;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
//? if forge {
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
*///?}
//? if >=26.1 {
/*import net.neoforged.neoforge.transfer.fluid.FluidResource;
*///?}

/**
 * Fluid registration and conversion between {@link FluidAmount} and loader fluid stacks.
 * <p>
 * Every registered fluid gets a fluid type, a source and a flowing fluid. Liquids also get a liquid
 * block and a bucket; gases get neither and report {@link #isGas} (negative density, the loaders'
 * "lighter than air" convention, so other mods recognise them as gases too).
 */
public final class PlatformFluids {
    private static final List<Registered> REGISTERED = new ArrayList<>();

    private PlatformFluids() {
    }

    /**
     * Appearance and physical properties of a fluid.
     *
     * @param pickupPurity purity byte stamped on buckets filled from the world, read at pickup
     *                     ({@link PlatformPurity#NONE} stamps nothing), or {@code null} for fluids
     *                     without purity
     */
    public record Spec(int color, boolean gas, int temperature, int viscosity, String stillTexture, String flowingTexture,
                       @Nullable IntSupplier pickupPurity) {
        /** Default liquid: placeholder textures tinted with {@code color} (ARGB). */
        public static Spec liquid(int color) {
            return new Spec(color, false, 300, 1000, "block/fluid_still", "block/fluid_flowing", null);
        }

        /** Default gas: no bucket, no block. */
        public static Spec gas(int color) {
            return new Spec(color, true, 300, 200, "block/fluid_still", "block/fluid_flowing", null);
        }

        /** Same fluid drawn with other textures (paths under {@code textures/}, greyscale, tinted with the colour). */
        public Spec withTextures(String still, String flowing) {
            return new Spec(color, gas, temperature, viscosity, still, flowing, pickupPurity);
        }

        /** Same fluid flowing slower in the world (water 1000). */
        public Spec withViscosity(int value) {
            return new Spec(color, gas, temperature, value, stillTexture, flowingTexture, pickupPurity);
        }

        /** Same fluid carrying purity: buckets keep it, buckets filled from the world get {@code pickup}. */
        public Spec withPurity(IntSupplier pickup) {
            return new Spec(color, gas, temperature, viscosity, stillTexture, flowingTexture, pickup);
        }

        public boolean carriesPurity() {
            return pickupPurity != null;
        }
    }

    /** Tooltip lines of mod buckets (a bucket is not a {@link PlatformItem}). */
    @FunctionalInterface
    public interface BucketTooltip {
        void append(ItemStack stack, Fluid fluid, List<Component> lines, boolean shiftDown);
    }

    /** Sets the tooltip hook of every mod bucket. */
    public static void setBucketTooltip(BucketTooltip tooltip) {
        FluidContainers.bucketTooltip = tooltip;
    }

    /** A registered fluid. {@link #bucket()} and {@link #block()} are {@code null} for gases. */
    public interface Registered {
        String name();

        Spec spec();

        Supplier<? extends Fluid> source();

        Supplier<? extends Fluid> flowing();

        @Nullable
        Supplier<? extends Item> bucket();

        @Nullable
        Supplier<? extends Block> block();
    }

    /** All fluids registered through {@link #register}, in registration order. */
    public static List<Registered> registered() {
        return Collections.unmodifiableList(REGISTERED);
    }

    /** Whether the fluid is a gas (stored in tanks, no bucket, cannot be placed). */
    public static boolean isGas(Fluid fluid) {
        return fluid.getFluidType().isLighterThanAir();
    }

    /** Registers {@code foodindustrial:<name>} (source) and {@code foodindustrial:flowing_<name>}. */
    public static Registered register(String name, Spec spec) {
        Holder holder = new Holder(name, spec);
        holder.type = PlatformRegistry.register(fluidTypeRegistry(), name, () -> createType(spec));
        holder.sourceFluid = PlatformRegistry.register(Registries.FLUID, name, () -> createSource(holder));
        holder.flowingFluid = PlatformRegistry.register(Registries.FLUID, "flowing_" + name, () -> createFlowing(holder));
        if (!spec.gas()) {
            holder.liquidBlock = PlatformRegistry.block(name,
                properties -> new FluidContainers.Liquid(() -> (FlowingFluid) holder.sourceFluid.get(), properties, spec.pickupPurity()),
                liquidBlockProperties());
            holder.bucketItem = PlatformRegistry.item(name + "_bucket",
                properties -> new FluidContainers.Bucket(holder.sourceFluid, properties),
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        }
        REGISTERED.add(holder);
        return holder;
    }

    /**
     * Copies the fluid's purity onto a filled bucket. Every loader path that fills an empty bucket
     * (vanilla bucket handlers, other mods' tanks) asks the fluid type for the bucket.
     */
    private static ItemStack bucketWithPurity(ItemStack bucket, FluidStack stack) {
        int purity = stackPurity(stack);
        if (!bucket.isEmpty() && purity != FluidAmount.NO_PURITY) {
            PlatformPurity.set(bucket, (byte) purity);
        }
        return bucket;
    }

    /** Converts a loader stack; purity is read from the stack's NBT / data component. */
    public static FluidAmount fromStack(FluidStack stack) {
        if (stack.isEmpty()) {
            return FluidAmount.EMPTY;
        }
        return FluidAmount.of(stack.getFluid(), stack.getAmount(), stackPurity(stack));
    }

    /** Converts to a loader stack carrying the purity. */
    public static FluidStack toStack(FluidAmount fluid) {
        if (fluid.isEmpty()) {
            return FluidStack.EMPTY;
        }
        FluidStack stack = new FluidStack(fluid.fluid(), fluid.amount());
        if (fluid.hasPurity()) {
            setStackPurity(stack, (byte) fluid.purity());
        }
        return stack;
    }

    /** Display name of a fluid (the loader's fluid type description). */
    public static net.minecraft.network.chat.Component displayName(FluidAmount fluid) {
        FluidStack stack = toStack(fluid.isEmpty() ? fluid : fluid.withAmount(Math.max(1, fluid.amount())));
        //? if forge {
        return stack.getDisplayName();
        //?} else {
        /*return net.minecraft.network.chat.Component.translatable(stack.getDescriptionId());
        *///?}
    }

    private static final class Holder implements Registered {
        private final String name;
        private final Spec spec;
        private Supplier<FluidType> type;
        private Supplier<Fluid> sourceFluid;
        private Supplier<Fluid> flowingFluid;
        @Nullable
        private Supplier<FluidContainers.Liquid> liquidBlock;
        @Nullable
        private Supplier<FluidContainers.Bucket> bucketItem;

        private Holder(String name, Spec spec) {
            this.name = name;
            this.spec = spec;
        }

        @Override public String name() { return name; }
        @Override public Spec spec() { return spec; }
        @Override public Supplier<? extends Fluid> source() { return sourceFluid; }
        @Override public Supplier<? extends Fluid> flowing() { return flowingFluid; }
        @Override public @Nullable Supplier<? extends Item> bucket() { return bucketItem; }
        @Override public @Nullable Supplier<? extends Block> block() { return liquidBlock; }
    }

    private static BlockBehaviour.Properties liquidBlockProperties() {
        return PlatformBlocks.noCollision(BlockBehaviour.Properties.of()).replaceable().strength(100.0F)
            .pushReaction(PushReaction.DESTROY).noLootTable().liquid();
    }

    private static FluidType.Properties typeProperties(Spec spec) {
        FluidType.Properties properties = FluidType.Properties.create()
            .temperature(spec.temperature())
            .viscosity(spec.viscosity());
        if (spec.gas()) {
            properties.density(-1000).canSwim(false).canDrown(false).canPushEntity(false).canExtinguish(false);
        } else {
            properties.density(1000)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
        }
        return properties;
    }

    //? if forge {
    private static net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<FluidType>> fluidTypeRegistry() {
        return ForgeRegistries.Keys.FLUID_TYPES;
    }

    private static FluidType createType(Spec spec) {
        return new FluidType(typeProperties(spec)) {
            @Override
            public ItemStack getBucket(FluidStack stack) {
                return bucketWithPurity(super.getBucket(stack), stack);
            }

            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override public int getTintColor() { return spec.color(); }
                    @Override public net.minecraft.resources.ResourceLocation getStillTexture() { return PlatformIdentifier.id(FoodIndustrial.MOD_ID, spec.stillTexture()); }
                    @Override public net.minecraft.resources.ResourceLocation getFlowingTexture() { return PlatformIdentifier.id(FoodIndustrial.MOD_ID, spec.flowingTexture()); }
                });
            }
        };
    }

    private static ForgeFlowingFluid.Properties fluidProperties(Holder holder) {
        ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(holder.type, holder.sourceFluid, holder.flowingFluid);
        if (holder.bucketItem != null) {
            properties.bucket(holder.bucketItem);
        }
        if (holder.liquidBlock != null) {
            properties.block(holder.liquidBlock);
        }
        return properties;
    }

    private static Fluid createSource(Holder holder) {
        return new ForgeFlowingFluid.Source(fluidProperties(holder));
    }

    private static Fluid createFlowing(Holder holder) {
        return new ForgeFlowingFluid.Flowing(fluidProperties(holder));
    }

    static int stackPurity(FluidStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(PlatformPurity.TAG_KEY, net.minecraft.nbt.Tag.TAG_BYTE) ? tag.getByte(PlatformPurity.TAG_KEY) : FluidAmount.NO_PURITY;
    }

    static void setStackPurity(FluidStack stack, byte purity) {
        stack.getOrCreateTag().putByte(PlatformPurity.TAG_KEY, purity);
    }

    /** Attaches fluid handlers to the mod buckets. Called by the mod entrypoint. */
    public static void bind(IEventBus modBus) {
        // Forge: buckets provide their handler through Item#initCapabilities.
    }
    //?} else {
    /*private static net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<FluidType>> fluidTypeRegistry() {
        return NeoForgeRegistries.Keys.FLUID_TYPES;
    }

    private static FluidType createType(Spec spec) {
        return new FluidType(typeProperties(spec)) {
            @Override
            public ItemStack getBucket(FluidStack stack) {
                return bucketWithPurity(super.getBucket(stack), stack);
            }
        };
    }

    private static BaseFlowingFluid.Properties fluidProperties(Holder holder) {
        BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(holder.type, holder.sourceFluid, holder.flowingFluid);
        if (holder.bucketItem != null) {
            properties.bucket(holder.bucketItem);
        }
        if (holder.liquidBlock != null) {
            properties.block(holder.liquidBlock);
        }
        return properties;
    }

    private static Fluid createSource(Holder holder) {
        return new BaseFlowingFluid.Source(fluidProperties(holder));
    }

    private static Fluid createFlowing(Holder holder) {
        return new BaseFlowingFluid.Flowing(fluidProperties(holder));
    }

    static int stackPurity(FluidStack stack) {
        Byte value = stack.get(PlatformPurity.COMPONENT.get());
        return value == null ? FluidAmount.NO_PURITY : value;
    }

    static void setStackPurity(FluidStack stack, byte purity) {
        stack.set(PlatformPurity.COMPONENT.get(), purity);
    }

    /^* Attaches fluid handlers to the mod buckets. Called by the mod entrypoint. ^/
    public static void bind(IEventBus modBus) {
        modBus.addListener((RegisterCapabilitiesEvent event) -> {
            for (Registered fluid : REGISTERED) {
                if (fluid.bucket() != null) {
                    registerBucket(event, fluid.bucket().get());
                }
            }
        });
    }
    *///?}

    //? if neoforge && <26.1 {
    /*private static void registerBucket(RegisterCapabilitiesEvent event, Item bucket) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new FluidContainers.Wrapper(stack), bucket);
    }
    *///?}

    //? if >=26.1 {
    /*private static void registerBucket(RegisterCapabilitiesEvent event, Item bucket) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FluidContainers.Handler(access), bucket);
    }
    *///?}

    //? if >=26.1 {
    /*public static FluidResource toResource(FluidAmount fluid) {
        if (fluid.isEmpty()) {
            return FluidResource.EMPTY;
        }
        FluidResource resource = FluidResource.of(fluid.fluid());
        return fluid.hasPurity() ? resource.with(PlatformPurity.COMPONENT.get(), (byte) fluid.purity()) : resource;
    }

    public static FluidAmount fromResource(FluidResource resource, int amount) {
        if (resource.isEmpty() || amount <= 0) {
            return FluidAmount.EMPTY;
        }
        Byte value = resource.getComponents().get(PlatformPurity.COMPONENT.get());
        return FluidAmount.of(resource.getFluid(), amount, value == null ? FluidAmount.NO_PURITY : value);
    }
    *///?}
}
