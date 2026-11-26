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

    /**
     * Factor on the saturation of this stack (I39: finished products by quality grade, foods/04 §5.3). 1.20.1 asks
     * the item per stack; from 1.21 on the food component of the stack itself is written
     * ({@link com.tonywww.foodindustrial.platform.food.PlatformFoodData#scaleSaturation}).
     */
    protected float saturationFactor(ItemStack stack) {
        return 1F;
    }

    /**
     * The item left in the crafting grid after this item is used (I40: a glass jar or bottle given back when the
     * product in it is poured out), or {@code null} for the vanilla behaviour. Asked lazily, so it may name an item
     * registered after this one.
     */
    protected Item craftingRemainderItem() {
        return null;
    }

    //? if >=26.1 {
    /*@Override
    public net.minecraft.world.item.ItemStackTemplate getCraftingRemainder(net.minecraft.world.item.ItemInstance instance) {
        Item own = craftingRemainderItem();
        return own != null ? new net.minecraft.world.item.ItemStackTemplate(own) : super.getCraftingRemainder(instance);
    }
    *///?} else {
    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return craftingRemainderItem() != null || super.hasCraftingRemainingItem(stack);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        Item own = craftingRemainderItem();
        return own != null ? new ItemStack(own) : super.getCraftingRemainingItem(stack);
    }
    //?}

    //? if <1.21 {
    @Override
    @Nullable
    public net.minecraft.world.food.FoodProperties getFoodProperties(ItemStack stack, @Nullable net.minecraft.world.entity.LivingEntity entity) {
        net.minecraft.world.food.FoodProperties base = super.getFoodProperties(stack, entity);
        float factor = saturationFactor(stack);
        if (base == null || factor == 1F) {
            return base;
        }
        net.minecraft.world.food.FoodProperties.Builder builder = new net.minecraft.world.food.FoodProperties.Builder()
            .nutrition(base.getNutrition()).saturationMod(base.getSaturationModifier() * factor);
        if (base.isMeat()) {
            builder.meat();
        }
        if (base.canAlwaysEat()) {
            builder.alwaysEat();
        }
        if (base.isFastFood()) {
            builder.fast();
        }
        for (com.mojang.datafixers.util.Pair<net.minecraft.world.effect.MobEffectInstance, Float> effect : base.getEffects()) {
            builder.effect(effect::getFirst, effect.getSecond());
        }
        return builder.build();
    }
    //?}

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
