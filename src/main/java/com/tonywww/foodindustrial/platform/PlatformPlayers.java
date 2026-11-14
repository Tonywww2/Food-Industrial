package com.tonywww.foodindustrial.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
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

    /**
     * The overworld's time of day in ticks, counting on across days (advanced by sleeping, stopped by
     * {@code doDaylightCycle false}); the 26.1 overworld clock.
     */
    public static long overworldDayTime(ServerPlayer player) {
        ServerLevel overworld = ((ServerLevel) player.level()).getServer().overworld();
        //? if >=26.1 {
        /*return overworld.getOverworldClockTime();
        *///?} else {
        return overworld.getDayTime();
        //?}
    }

    /** Ticks the world has run (the game time), which no command or sleeping changes. */
    public static long gameTime(ServerPlayer player) {
        return ((ServerLevel) player.level()).getServer().overworld().getGameTime();
    }

    /**
     * Whether the player stands in direct sunlight: daytime in a dimension with a sky, nothing above the block
     * of the player's head, full sky light there.
     */
    public static boolean inDirectSunlight(Player player) {
        Level level = player.level();
        BlockPos head = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        //? if >=26.1 {
        /*boolean day = level.isBrightOutside();
        *///?} else {
        boolean day = level.isDay();
        //?}
        return day && level.canSeeSky(head) && level.getBrightness(LightLayer.SKY, head) >= 15;
    }

    /** Takes {@code food} hunger points and all saturation (vomiting). */
    public static void loseFood(Player player, int food) {
        FoodData data = player.getFoodData();
        data.setFoodLevel(Math.max(0, data.getFoodLevel() - food));
        data.setSaturation(0.0F);
    }

    /** Damages the player with a damage type of a data pack ({@code namespace:path}). */
    public static void hurt(ServerPlayer player, String damageType, float amount) {
        var key = PlatformIdentifier.key(Registries.DAMAGE_TYPE, damageType.substring(0, damageType.indexOf(':')),
            damageType.substring(damageType.indexOf(':') + 1));
        ServerLevel level = (ServerLevel) player.level();
        //? if >=26.1 {
        /*DamageSource source = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
        player.hurtServer(level, source, amount);
        *///?} else {
        DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
        player.hurt(source, amount);
        //?}
    }
}
