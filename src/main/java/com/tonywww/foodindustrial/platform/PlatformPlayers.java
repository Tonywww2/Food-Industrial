package com.tonywww.foodindustrial.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Player helpers whose vanilla signatures differ between versions. */
public final class PlatformPlayers {
    private PlatformPlayers() {
    }

    /** Shows {@code message} above the hotbar (the action bar). */
    public static void actionBar(Player player, Component message) {
        //? if >=26.1 {
        /*player.sendOverlayMessage(message);
        *///?} else {
        player.displayClientMessage(message, true);
        //?}
    }

    /** How far the player reaches blocks. */
    public static double blockReach(Player player) {
        //? if >=1.21 {
        /*return player.blockInteractionRange();
        *///?} else {
        return player.getBlockReach();
        //?}
    }

    /** How far the player reaches entities. */
    public static double entityReach(Player player) {
        //? if >=1.21 {
        /*return player.entityInteractionRange();
        *///?} else {
        return player.getEntityReach();
        //?}
    }

    /**
     * Whether the player aims at nothing within reach: no block, no fluid and no entity (using an item
     * "in the air"). Works on both sides.
     */
    public static boolean aimsAtNothing(Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 blockEnd = eye.add(look.scale(blockReach(player)));
        HitResult block = player.level().clip(new ClipContext(eye, blockEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
        if (block.getType() != HitResult.Type.MISS) {
            return false;
        }
        double reach = entityReach(player);
        Vec3 entityEnd = eye.add(look.scale(reach));
        AABB area = player.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0);
        return ProjectileUtil.getEntityHitResult(player, eye, entityEnd, area, entity -> !entity.isSpectator() && entity.isPickable(), reach * reach) == null;
    }
}
