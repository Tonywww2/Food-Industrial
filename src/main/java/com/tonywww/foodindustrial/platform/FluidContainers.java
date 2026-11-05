package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.purity.PlatformPurity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
//? if forge {
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
//?}
//? if neoforge && <26.1 {
/*import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
*///?}
//? if >=26.1 {
/*import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.Consumer;
*///?}

/**
 * Buckets and liquid blocks of this mod's fluids. Buckets carry the fluid's purity (stored byte) in
 * and out of fluid handlers; a liquid block placed in the world keeps no purity, so picking it up
 * stamps the fluid's pickup purity onto the bucket (machines/03 §4).
 */
final class FluidContainers {
    private FluidContainers() {
    }

    /** Tooltip hook of the buckets; see {@link PlatformFluids#setBucketTooltip}. */
    @Nullable
    static PlatformFluids.BucketTooltip bucketTooltip;

    /** Bucket of a mod fluid. Fluid handlers are attached by {@link PlatformFluids} (loaders only attach them to plain buckets). */
    static final class Bucket extends BucketItem {
        //? if forge {
        Bucket(Supplier<? extends Fluid> fluid, Properties properties) {
            super(fluid, properties);
        }

        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return new Wrapper(stack);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
            super.appendHoverText(stack, level, lines, flag);
            collect(stack, lines);
        }
        //?}
        //? if neoforge && <26.1 {
        /*Bucket(Supplier<? extends Fluid> fluid, Properties properties) {
            super(fluid.get(), properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
            super.appendHoverText(stack, context, lines, flag);
            collect(stack, lines);
        }
        *///?}
        //? if >=26.1 {
        /*Bucket(Supplier<? extends Fluid> fluid, Properties properties) {
            super(fluid.get(), properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
            super.appendHoverText(stack, context, display, builder, flag);
            List<Component> lines = new ArrayList<>(3);
            collect(stack, lines);
            lines.forEach(builder);
        }
        *///?}

        private void collect(ItemStack stack, List<Component> lines) {
            PlatformFluids.BucketTooltip hook = bucketTooltip;
            if (hook != null) {
                hook.append(stack, fluidOf(this), lines, Platform.isShiftDown());
            }
        }
    }

    private static Fluid fluidOf(BucketItem bucket) {
        //? if forge {
        return bucket.getFluid();
        //?}
        //? if neoforge && <26.1 {
        /*return bucket.content;
        *///?}
        //? if >=26.1 {
        /*return bucket.content;
        *///?}
    }

    /** Liquid block that stamps {@code pickupPurity} on buckets filled from the world. */
    static final class Liquid extends LiquidBlock {
        @Nullable
        private final IntSupplier pickupPurity;

        //? if forge {
        Liquid(Supplier<? extends FlowingFluid> fluid, Properties properties, @Nullable IntSupplier pickupPurity) {
            super(fluid, properties);
            this.pickupPurity = pickupPurity;
        }

        @Override
        public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
            return stamp(super.pickupBlock(level, pos, state));
        }
        //?}
        //? if neoforge && <26.1 {
        /*Liquid(Supplier<? extends FlowingFluid> fluid, Properties properties, @Nullable IntSupplier pickupPurity) {
            super(fluid.get(), properties);
            this.pickupPurity = pickupPurity;
        }

        @Override
        public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
            return stamp(super.pickupBlock(player, level, pos, state));
        }
        *///?}
        //? if >=26.1 {
        /*Liquid(Supplier<? extends FlowingFluid> fluid, Properties properties, @Nullable IntSupplier pickupPurity) {
            super(fluid.get(), properties);
            this.pickupPurity = pickupPurity;
        }

        @Override
        public ItemStack pickupBlock(@Nullable LivingEntity user, LevelAccessor level, BlockPos pos, BlockState state) {
            return stamp(super.pickupBlock(user, level, pos, state));
        }
        *///?}

        private ItemStack stamp(ItemStack stack) {
            int purity = pickupPurity == null ? PlatformPurity.NONE : pickupPurity.getAsInt();
            if (!stack.isEmpty() && purity != PlatformPurity.NONE) {
                PlatformPurity.set(stack, (byte) purity);
            }
            return stack;
        }
    }

    //? if <26.1 {
    /** Bucket fluid handler that keeps the purity between the bucket stack and the fluid stack. */
    static final class Wrapper extends FluidBucketWrapper {
        Wrapper(ItemStack container) {
            super(container);
        }

        @Override
        public FluidStack getFluid() {
            FluidStack stack = super.getFluid();
            int purity = PlatformPurity.get(container);
            if (!stack.isEmpty() && purity != PlatformPurity.NONE) {
                PlatformFluids.setStackPurity(stack, (byte) purity);
            }
            return stack;
        }

        @Override
        protected void setFluid(FluidStack stack) {
            super.setFluid(stack);
            int purity = PlatformFluids.stackPurity(stack);
            if (!stack.isEmpty() && purity != FluidAmount.NO_PURITY) {
                PlatformPurity.set(container, (byte) purity);
            }
        }
    }
    //?} else {
    /*/^* Bucket resource handler (26.1 transfer API) that keeps the purity between bucket and fluid. ^/
    static final class Handler extends ItemAccessResourceHandler<FluidResource> {
        Handler(ItemAccess access) {
            super(access, 1);
        }

        @Override
        protected FluidResource getResourceFrom(ItemResource access, int index) {
            if (!(access.getItem() instanceof BucketItem bucket)) {
                return FluidResource.EMPTY;
            }
            FluidResource resource = FluidResource.of(bucket.content);
            Byte purity = access.get(PlatformPurity.COMPONENT.get());
            return purity == null ? resource : resource.with(PlatformPurity.COMPONENT.get(), purity);
        }

        @Override
        protected int getAmountFrom(ItemResource access, int index) {
            return getResourceFrom(access, index).isEmpty() ? 0 : FluidType.BUCKET_VOLUME;
        }

        @Override
        protected ItemResource update(ItemResource access, int index, FluidResource resource, int amount) {
            if (amount == 0) {
                return ItemResource.of(Items.BUCKET);
            }
            if (amount != FluidType.BUCKET_VOLUME) {
                return ItemResource.EMPTY;
            }
            FluidStack stack = resource.toStack(amount);
            ItemResource bucket = ItemResource.of(stack.getFluidType().getBucket(stack));
            Byte purity = resource.get(PlatformPurity.COMPONENT.get());
            return purity == null ? bucket : bucket.with(PlatformPurity.COMPONENT.get(), purity);
        }

        @Override
        protected int getCapacity(int index, FluidResource resource) {
            return FluidType.BUCKET_VOLUME;
        }
    }
    *///?}
}
