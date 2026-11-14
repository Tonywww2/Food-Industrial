package com.tonywww.foodindustrial.platform.mixin;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
//? if <1.21 {
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//?}

/**
 * On 1.20.1 a furnace stacks a result onto its output slot when only the items match, so results of
 * different purity would merge into one stack with one purity. From 1.20.5 vanilla requires the
 * same components; this makes 1.20.1 require the same tags (bridge recipes, machines/01 §5). Empty
 * on later nodes.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    //? if <1.21 {
    @Redirect(method = "canBurn", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean foodindustrial$sameTags(ItemStack result, ItemStack burnt) {
        return ItemStack.isSameItemSameTags(result, burnt);
    }
    //?}
}
