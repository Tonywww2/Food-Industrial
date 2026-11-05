package com.tonywww.foodindustrial.logistics;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.platform.PlatformBlockItem;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformItemData;
import com.tonywww.foodindustrial.platform.PlatformPlayers;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.PlatformWorldFluids;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * Item of a tank (machines/03 §5, 2026-11-05). Keeps the fluid of a broken tank; Shift + right click
 * while aiming at nothing (no block, fluid or entity in reach) switches between place mode (a normal
 * block item) and use mode, in which it works like a bucket in
 * steps of 1,000 mB: right click on a fluid source takes it (an empty tank takes any fluid, a filled one
 * only the same; wooden tanks take no gases), right click on a block face with air in front places the
 * fluid there.
 */
public class TankBlockItem extends PlatformBlockItem {
    private static final String KEY = "FoodIndustrialTank";

    private final TankKind kind;

    public TankBlockItem(Block block, Properties properties) {
        super(block, properties);
        this.kind = ((TankBlock) block).kind();
    }

    public TankKind kind() {
        return kind;
    }

    // ---- data ----

    /** Fluid carried by a tank item, or empty. */
    public static FluidAmount fluid(ItemStack stack) {
        String id = PlatformItemData.getString(stack, KEY, "Fluid");
        int amount = PlatformItemData.getInt(stack, KEY, "Amount", 0);
        if (id.isEmpty() || amount <= 0) {
            return FluidAmount.EMPTY;
        }
        Fluid fluid = PlatformTags.fluid(id).orElse(null);
        if (fluid == null) {
            return FluidAmount.EMPTY;
        }
        int purity = PlatformItemData.getInt(stack, KEY, "Purity", FluidAmount.NO_PURITY);
        return FluidAmount.of(fluid, amount, purity);
    }

    /** Whether the item is in use mode. */
    public static boolean useMode(ItemStack stack) {
        return PlatformItemData.getInt(stack, KEY, "Use", 0) != 0;
    }

    public static void setFluid(ItemStack stack, FluidAmount fluid, HolderLookup.Provider registries) {
        write(stack, fluid, useMode(stack), registries);
    }

    private static void write(ItemStack stack, FluidAmount fluid, boolean use, HolderLookup.Provider registries) {
        PlatformItemData.write(stack, KEY, registries, out -> {
            if (!fluid.isEmpty()) {
                out.putString("Fluid", PlatformTags.fluidId(fluid.fluid()));
                out.putInt("Amount", fluid.amount());
                if (fluid.hasPurity()) {
                    out.putInt("Purity", fluid.purity());
                }
            }
            if (use) {
                out.putInt("Use", 1);
            }
        });
    }

    // ---- use ----

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!useMode(context.getItemInHand())) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean onUseInAir(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive() && PlatformPlayers.aimsAtNothing(player)) {
            toggle(level, player, hand);
            return true;
        }
        if (!useMode(stack)) {
            return false;
        }
        BlockHitResult hit = PlatformWorldFluids.lookAt(level, player, true);
        if (hit == null) {
            return false;
        }
        FluidAmount contents = fluid(stack);
        BlockPos pos = hit.getBlockPos();
        Fluid source = PlatformWorldFluids.sourceAt(level, pos);
        if (source != null) {
            if (!kind.accepts(source) || (!contents.isEmpty() && contents.fluid() != source)
                || contents.amount() + PlatformWorldFluids.UNIT > kind.capacity()) {
                return false;
            }
            if (!level.isClientSide()) {
                FluidAmount taken = PlatformWorldFluids.pickup(level, pos, player);
                if (!taken.isEmpty()) {
                    update(level, player, hand, contents.isEmpty() ? taken : Purity.merge(contents, taken));
                }
            }
            return true;
        }
        BlockPos target = pos.relative(hit.getDirection());
        if (contents.amount() < PlatformWorldFluids.UNIT || !level.isEmptyBlock(target) || !PlatformWorldFluids.placeable(contents.fluid())) {
            return false;
        }
        if (!level.isClientSide() && PlatformWorldFluids.place(level, target, contents.fluid())) {
            update(level, player, hand, contents.withAmount(contents.amount() - PlatformWorldFluids.UNIT));
        }
        return true;
    }

    /** Writes new contents to one item of the held stack (splitting it off a larger stack). */
    private static void update(Level level, Player player, InteractionHand hand, FluidAmount contents) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getCount() == 1) {
            setFluid(stack, contents, level.registryAccess());
            return;
        }
        ItemStack one = stack.copyWithCount(1);
        stack.shrink(1);
        setFluid(one, contents, level.registryAccess());
        player.getInventory().placeItemBackInInventory(one);
    }

    private static void toggle(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return;
        }
        ItemStack stack = player.getItemInHand(hand);
        boolean use = !useMode(stack);
        write(stack, fluid(stack), use, level.registryAccess());
        PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("tank", use ? "mode_use" : "mode_place")));
    }

    // ---- tooltip ----

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        FluidAmount contents = fluid(stack);
        if (!contents.isEmpty()) {
            lines.add(PlatformFluids.displayName(contents).copy().append(" ").append(Component.translatable(GuiText.key("tank", "amount"),
                String.valueOf(contents.amount()), String.valueOf(kind.capacity()))).withStyle(ChatFormatting.GRAY));
            if (Purity.bears(contents.fluid())) {
                Tooltips.purityLine(Purity.materialOf(contents.fluid()), Purity.get(contents), lines);
            }
        }
        lines.add(Component.translatable(GuiText.key("tank", useMode(stack) ? "mode_use" : "mode_place")).withStyle(ChatFormatting.DARK_GRAY));
        Tooltips.machine(Lang.description(kind.id()), lines, shiftDown);
    }
}
