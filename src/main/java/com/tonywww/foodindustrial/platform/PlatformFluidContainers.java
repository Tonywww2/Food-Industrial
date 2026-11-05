package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import net.minecraft.world.item.ItemStack;
//? if forge {
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
//?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
*///?}
//? if >=26.1 {
/*import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.BiPredicate;
*///?}

/**
 * Fluid item containers (buckets and other mods' cells or bottles) through the loaders' item fluid
 * handlers. Purity travels with the fluid (mod buckets keep it, see {@code FluidContainers}).
 */
public final class PlatformFluidContainers {
    private PlatformFluidContainers() {
    }

    //? if <26.1 {
    /** Fluid held by an item container, or {@link FluidAmount#EMPTY}. */
    public static FluidAmount contained(ItemStack container) {
        return FluidUtil.getFluidContained(container).map(PlatformFluids::fromStack).orElse(FluidAmount.EMPTY);
    }

    /** One {@code container} filled with exactly {@code fluid}, or {@link ItemStack#EMPTY} if it cannot take all of it. */
    public static ItemStack filled(ItemStack container, FluidAmount fluid) {
        if (container.isEmpty() || fluid.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return FluidUtil.getFluidHandler(container.copyWithCount(1)).map(handler -> {
            FluidStack stack = PlatformFluids.toStack(fluid);
            return handler.fill(stack, IFluidHandler.FluidAction.EXECUTE) == stack.getAmount() ? handler.getContainer() : ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }

    /** One {@code container} after its fluid is drained, or {@link ItemStack#EMPTY} if nothing can be drained. */
    public static ItemStack drained(ItemStack container) {
        if (container.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return FluidUtil.getFluidHandler(container.copyWithCount(1)).map(handler ->
            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE).isEmpty() ? ItemStack.EMPTY : handler.getContainer()
        ).orElse(ItemStack.EMPTY);
    }
    //?} else {
    /*/^* Fluid held by an item container, or {@link FluidAmount#EMPTY}. ^/
    public static FluidAmount contained(ItemStack container) {
        return PlatformFluids.fromStack(FluidUtil.getFirstStackContained(container));
    }

    /^* One {@code container} filled with exactly {@code fluid}, or {@link ItemStack#EMPTY} if it cannot take all of it. ^/
    public static ItemStack filled(ItemStack container, FluidAmount fluid) {
        if (container.isEmpty() || fluid.isEmpty()) {
            return ItemStack.EMPTY;
        }
        FluidResource resource = PlatformFluids.toResource(fluid);
        return transact(container, (handler, transaction) -> handler.insert(resource, fluid.amount(), transaction) == fluid.amount());
    }

    /^* One {@code container} after its fluid is drained, or {@link ItemStack#EMPTY} if nothing can be drained. ^/
    public static ItemStack drained(ItemStack container) {
        if (container.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return transact(container, (handler, transaction) -> {
            for (int index = 0; index < handler.size(); index++) {
                FluidResource resource = handler.getResource(index);
                if (!resource.isEmpty() && handler.extract(index, resource, (int) handler.getAmountAsLong(index), transaction) > 0) {
                    return true;
                }
            }
            return false;
        });
    }

    private static ItemStack transact(ItemStack container, BiPredicate<ResourceHandler<FluidResource>, TransactionContext> action) {
        ItemStacksResourceHandler slot = new ItemStacksResourceHandler(NonNullList.of(ItemStack.EMPTY, container.copyWithCount(1)));
        ResourceHandler<FluidResource> fluids = ItemAccess.forHandlerIndex(slot, 0).getCapability(Capabilities.Fluid.ITEM);
        if (fluids == null) {
            return ItemStack.EMPTY;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            if (!action.test(fluids, transaction)) {
                return ItemStack.EMPTY;
            }
            transaction.commit();
        }
        return slot.getResource(0).toStack((int) slot.getAmountAsLong(0));
    }
    *///?}
}
