package com.tonywww.foodindustrial.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
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
 * Base item class with stable hooks: {@link #appendTooltip} and {@link #onUseInAir}; the
 * version-specific {@code appendHoverText} and {@code use} signatures are handled here. Use on a
 * block goes through the vanilla {@code useOn(UseOnContext)}, which is the same on every node.
 */
public class PlatformItem extends Item {
    public PlatformItem(Properties properties) {
        super(properties);
    }

    /**
     * Adds lines below the item name. {@code shiftDown} is {@code true} while Shift is held on the
     * client (always {@code false} elsewhere).
     */
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
    }

    /**
     * Right click while not using the item on a block. Runs on both sides; return {@code true} when
     * handled (the arm swings and the stack stays).
     */
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
