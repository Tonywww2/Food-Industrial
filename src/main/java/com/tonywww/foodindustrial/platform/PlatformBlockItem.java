package com.tonywww.foodindustrial.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.Level;
//? if >=26.1 {
/*import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
*///?} else {
import net.minecraft.world.InteractionResultHolder;
//?}
//? if <1.21 {
import org.jetbrains.annotations.Nullable;
//?}

/**
 * Block item with the stable tooltip hook of {@link PlatformItem#appendTooltip}. Create it through
 * {@link PlatformRegistry#blockItem(String, java.util.function.Supplier, java.util.function.BiFunction, Item.Properties)}
 * so the version-specific block item properties are applied.
 */
public class PlatformBlockItem extends BlockItem {
    public PlatformBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    /** Adds lines below the item name; {@code shiftDown} as in {@link PlatformItem}. */
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
    }

    /** Right click while not using the item on a block; as {@link PlatformItem#onUseInAir}. */
    protected boolean onUseInAir(Level level, Player player, InteractionHand hand) {
        return false;
    }

    //? if >=26.1 {
    /*@Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return onUseInAir(level, player, hand) ? InteractionResult.SUCCESS : super.use(level, player, hand);
    }
    *///?} else {
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (onUseInAir(level, player, hand)) {
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
        return super.use(level, player, hand);
    }
    //?}

    private void collect(ItemStack stack, List<Component> lines) {
        appendTooltip(stack, lines, Platform.isShiftDown());
    }

    //? if >=26.1 {
    /*@Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        List<Component> lines = new ArrayList<>(2);
        collect(stack, lines);
        lines.forEach(builder);
    }
    *///?}

    //? if >=1.21 && <26.1 {
    /*@Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        collect(stack, lines);
    }
    *///?}

    //? if <1.21 {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        collect(stack, lines);
    }
    //?}
}
