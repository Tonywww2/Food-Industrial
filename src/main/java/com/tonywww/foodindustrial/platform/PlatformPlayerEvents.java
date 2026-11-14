package com.tonywww.foodindustrial.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
//? if forge {
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
//?} else {
/*import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
*///?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.common.util.TriState;
*///?}
//? if >=26.1 {
/*import net.minecraft.util.TriState;
*///?}

/**
 * Player life-cycle, eating and interaction hooks (I35) without loader event types. Register during mod
 * construction. Unless a hook says otherwise it runs on the server for server players only.
 */
public final class PlatformPlayerEvents {
    private static final List<Consumer<ServerPlayer>> LOGIN = new ArrayList<>();
    private static final List<Consumer<ServerPlayer>> LOGOUT = new ArrayList<>();
    private static final List<Consumer<ServerPlayer>> RESPAWN = new ArrayList<>();
    private static final List<Consumer<ServerPlayer>> DIMENSION = new ArrayList<>();
    private static final List<Consumer<ServerPlayer>> TICK = new ArrayList<>();
    private static final List<Consumer<ServerPlayer>> DAMAGED = new ArrayList<>();
    private static final List<CloneListener> CLONE = new ArrayList<>();
    private static final List<BiConsumer<ServerPlayer, ItemStack>> CONSUMED = new ArrayList<>();
    private static final List<SleepCheck> SLEEP = new ArrayList<>();
    @Nullable
    private static ExhaustionScale exhaustion;
    @Nullable
    private static InteractionGate gate;

    private PlatformPlayerEvents() {
    }

    /** After a player joins the server. */
    public static void onLogin(Consumer<ServerPlayer> listener) {
        LOGIN.add(listener);
    }

    /** Before a player leaves the server (still holds its data). */
    public static void onLogout(Consumer<ServerPlayer> listener) {
        LOGOUT.add(listener);
    }

    /** After a player respawns (after death or leaving the End): the new player entity. */
    public static void onRespawn(Consumer<ServerPlayer> listener) {
        RESPAWN.add(listener);
    }

    /** After a player moved to another dimension. */
    public static void onChangedDimension(Consumer<ServerPlayer> listener) {
        DIMENSION.add(listener);
    }

    /** At the end of every tick of every server player. */
    public static void onTick(Consumer<ServerPlayer> listener) {
        TICK.add(listener);
    }

    /** After a server player took damage (health was lost). */
    public static void onDamaged(Consumer<ServerPlayer> listener) {
        DAMAGED.add(listener);
    }

    /** Receives the old and the new entity when a player respawns. */
    @FunctionalInterface
    public interface CloneListener {
        /**
         * @param death {@code true} after death; {@code false} when returning from the End, in which case the
         *              player data ({@link PlatformPlayerData}) is already copied
         */
        void cloned(ServerPlayer original, ServerPlayer fresh, boolean death);
    }

    /** When a respawning player's new entity is created, before it enters the world. */
    public static void onClone(CloneListener listener) {
        CLONE.add(listener);
    }

    /**
     * After a player finished eating or drinking an item ({@code stack} as it was before), or ate one bite of a
     * cake or a Farmer's Delight pie ({@code stack}: one of that block's item).
     */
    public static void onConsumed(BiConsumer<ServerPlayer, ItemStack> listener) {
        CONSUMED.add(listener);
    }

    /** Scales the food exhaustion a player is about to gain. */
    @FunctionalInterface
    public interface ExhaustionScale {
        float scale(Player player, float amount);
    }

    /** Sets the exhaustion scale (one per mod). */
    public static void setExhaustionScale(ExhaustionScale scale) {
        exhaustion = scale;
    }

    /** Decides whether a player may sleep. */
    @FunctionalInterface
    public interface SleepCheck {
        /** The reason shown in the action bar when the player cannot sleep, or {@code null} when it may. */
        @Nullable
        Component problem(ServerPlayer player);
    }

    /** Checks players who lie down in a bed. */
    public static void onSleepCheck(SleepCheck check) {
        SLEEP.add(check);
    }

    /** What a player tries to do. */
    public enum Interaction {
        ATTACK, BREAK_BLOCK, PLACE_BLOCK, USE_ITEM, OPEN_CONTAINER
    }

    /** Blocks interactions; asked on both sides (clients must know the state to stop the action early). */
    @FunctionalInterface
    public interface InteractionGate {
        boolean blocked(Player player, Interaction interaction);
    }

    /** Sets the interaction gate (one per mod). */
    public static void setInteractionGate(InteractionGate value) {
        gate = value;
    }

    private static boolean blocked(Player player, Interaction interaction) {
        return gate != null && gate.blocked(player, interaction);
    }

    // ---- called by mixins ----

    /** {@code Player#causeFoodExhaustion}: the amount after scaling. */
    public static float scaleExhaustion(Player player, float amount) {
        return exhaustion == null ? amount : exhaustion.scale(player, amount);
    }

    /** A cake or pie block lost a bite to {@code player}. */
    public static void ateBite(Player player, BlockState state) {
        if (player instanceof ServerPlayer server && !CONSUMED.isEmpty()) {
            ItemStack stack = new ItemStack(state.getBlock().asItem());
            CONSUMED.forEach(listener -> listener.accept(server, stack));
        }
    }

    // ---- event wiring ----

    private static void consumed(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer server) {
            CONSUMED.forEach(listener -> listener.accept(server, stack));
        }
    }

    private static void clone(Player original, Player fresh, boolean death) {
        if (!(original instanceof ServerPlayer old) || !(fresh instanceof ServerPlayer created)) {
            return;
        }
        PlatformPlayerData.revive(original);
        try {
            if (!death) {
                PlatformPlayerData.copyAll(original, fresh);
            }
            CLONE.forEach(listener -> listener.cloned(old, created, death));
        } finally {
            PlatformPlayerData.invalidate(original);
        }
    }

    @Nullable
    private static Component sleepProblem(Player player) {
        if (player instanceof ServerPlayer server) {
            for (SleepCheck check : SLEEP) {
                Component problem = check.problem(server);
                if (problem != null) {
                    return problem;
                }
            }
        }
        return null;
    }

    /** Whether a right click on a block would open a container there. */
    private static boolean opensContainer(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getMenuProvider(level, pos) != null || level.getBlockEntity(pos) instanceof Container;
    }

    private static boolean placesBlock(ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    /** Called by the mod entrypoint. */
    static void bind() {
        //? if forge {
        var bus = MinecraftForge.EVENT_BUS;
        bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> each(LOGIN, event.getEntity()));
        bus.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> each(LOGOUT, event.getEntity()));
        bus.addListener((PlayerEvent.PlayerRespawnEvent event) -> each(RESPAWN, event.getEntity()));
        bus.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> each(DIMENSION, event.getEntity()));
        bus.addListener((PlayerEvent.Clone event) -> clone(event.getOriginal(), event.getEntity(), event.isWasDeath()));
        bus.addListener((TickEvent.PlayerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                each(TICK, event.player);
            }
        });
        bus.addListener((LivingDamageEvent event) -> {
            if (event.getAmount() > 0 && event.getEntity() instanceof ServerPlayer player) {
                each(DAMAGED, player);
            }
        });
        bus.addListener((LivingEntityUseItemEvent.Finish event) -> {
            if (event.getEntity() instanceof Player player) {
                consumed(player, event.getItem());
            }
        });
        bus.addListener((PlayerSleepInBedEvent event) -> {
            Component problem = sleepProblem(event.getEntity());
            if (problem != null) {
                event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
                PlatformPlayers.actionBar(event.getEntity(), problem);
            }
        });
        bus.addListener((AttackEntityEvent event) -> {
            if (blocked(event.getEntity(), Interaction.ATTACK)) {
                event.setCanceled(true);
            }
        });
        bus.addListener((PlayerInteractEvent.LeftClickBlock event) -> {
            if (blocked(event.getEntity(), Interaction.BREAK_BLOCK)) {
                event.setCanceled(true);
            }
        });
        bus.addListener((BlockEvent.BreakEvent event) -> {
            if (blocked(event.getPlayer(), Interaction.BREAK_BLOCK)) {
                event.setCanceled(true);
            }
        });
        bus.addListener((BlockEvent.EntityPlaceEvent event) -> {
            if (event.getEntity() instanceof Player player && blocked(player, Interaction.PLACE_BLOCK)) {
                event.setCanceled(true);
            }
        });
        bus.addListener((PlayerInteractEvent.RightClickBlock event) -> {
            Player player = event.getEntity();
            if (blocked(player, Interaction.OPEN_CONTAINER) && opensContainer(event.getLevel(), event.getPos())) {
                event.setUseBlock(Event.Result.DENY);
            }
            if (blocked(player, placesBlock(event.getItemStack()) ? Interaction.PLACE_BLOCK : Interaction.USE_ITEM)) {
                event.setUseItem(Event.Result.DENY);
            }
        });
        bus.addListener((PlayerInteractEvent.RightClickItem event) -> {
            if (blocked(event.getEntity(), Interaction.USE_ITEM)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        });
        bus.addListener((PlayerInteractEvent.EntityInteract event) -> {
            if (blocked(event.getEntity(), Interaction.OPEN_CONTAINER)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        });
        bus.addListener((PlayerInteractEvent.EntityInteractSpecific event) -> {
            if (blocked(event.getEntity(), Interaction.OPEN_CONTAINER)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        });
        bus.addListener((LivingEntityUseItemEvent.Start event) -> {
            if (event.getEntity() instanceof Player player && blocked(player, Interaction.USE_ITEM)) {
                event.setCanceled(true);
            }
        });
        //?} else {
        /*var bus = NeoForge.EVENT_BUS;
        bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> each(LOGIN, event.getEntity()));
        bus.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> each(LOGOUT, event.getEntity()));
        bus.addListener((PlayerEvent.PlayerRespawnEvent event) -> each(RESPAWN, event.getEntity()));
        bus.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> each(DIMENSION, event.getEntity()));
        bus.addListener((PlayerEvent.Clone event) -> clone(event.getOriginal(), event.getEntity(), event.isWasDeath()));
        bus.addListener((PlayerTickEvent.Post event) -> each(TICK, event.getEntity()));
        bindDamageAndBreak(bus);
        bus.addListener((LivingEntityUseItemEvent.Finish event) -> {
            if (event.getEntity() instanceof Player player) {
                consumed(player, event.getItem());
            }
        });
        bus.addListener((CanPlayerSleepEvent event) -> {
            Component problem = sleepProblem(event.getEntity());
            if (problem != null) {
                event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);
                PlatformPlayers.actionBar(event.getEntity(), problem);
            }
        });
        bus.addListener((AttackEntityEvent event) -> {
            if (blocked(event.getEntity(), Interaction.ATTACK)) {
                event.setCanceled(true);
            }
        });
        bus.addListener((PlayerInteractEvent.LeftClickBlock event) -> {
            if (blocked(event.getEntity(), Interaction.BREAK_BLOCK)) {
                event.setCanceled(true);
            }
        });
        bus.addListener((BlockEvent.EntityPlaceEvent event) -> {
            if (event.getEntity() instanceof Player player && blocked(player, Interaction.PLACE_BLOCK)) {
                event.setCanceled(true);
            }
        });
        bus.addListener((PlayerInteractEvent.RightClickBlock event) -> {
            Player player = event.getEntity();
            if (blocked(player, Interaction.OPEN_CONTAINER) && opensContainer(event.getLevel(), event.getPos())) {
                event.setUseBlock(TriState.FALSE);
            }
            if (blocked(player, placesBlock(event.getItemStack()) ? Interaction.PLACE_BLOCK : Interaction.USE_ITEM)) {
                event.setUseItem(TriState.FALSE);
            }
        });
        bus.addListener((PlayerInteractEvent.RightClickItem event) -> {
            if (blocked(event.getEntity(), Interaction.USE_ITEM)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        });
        bus.addListener((PlayerInteractEvent.EntityInteract event) -> {
            if (blocked(event.getEntity(), Interaction.OPEN_CONTAINER)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        });
        bus.addListener((PlayerInteractEvent.EntityInteractSpecific event) -> {
            if (blocked(event.getEntity(), Interaction.OPEN_CONTAINER)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        });
        bus.addListener((LivingEntityUseItemEvent.Start event) -> {
            if (event.getEntity() instanceof Player player && blocked(player, Interaction.USE_ITEM)) {
                event.setCanceled(true);
            }
        });
        *///?}
    }

    //? if neoforge && <26.1 {
    /*private static void bindDamageAndBreak(net.neoforged.bus.api.IEventBus bus) {
        bus.addListener((LivingDamageEvent.Post event) -> {
            if (event.getNewDamage() > 0 && event.getEntity() instanceof ServerPlayer player) {
                each(DAMAGED, player);
            }
        });
        bus.addListener((BlockEvent.BreakEvent event) -> {
            if (blocked(event.getPlayer(), Interaction.BREAK_BLOCK)) {
                event.setCanceled(true);
            }
        });
    }
    *///?}

    //? if >=26.1 {
    /*private static void bindDamageAndBreak(net.neoforged.bus.api.IEventBus bus) {
        bus.addListener((LivingDamageEvent.Post event) -> {
            if (event.getHealthDamage() > 0 && event.getEntity() instanceof ServerPlayer player) {
                each(DAMAGED, player);
            }
        });
        bus.addListener((net.neoforged.neoforge.event.level.block.BreakBlockEvent event) -> {
            if (blocked(event.getPlayer(), Interaction.BREAK_BLOCK)) {
                event.setCanceled(true);
            }
        });
    }
    *///?}

    private static void each(List<Consumer<ServerPlayer>> listeners, Player player) {
        if (player instanceof ServerPlayer server) {
            for (Consumer<ServerPlayer> listener : listeners) {
                listener.accept(server);
            }
        }
    }
}
