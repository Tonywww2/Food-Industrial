package com.tonywww.foodindustrial.platform;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.world.PlatformPlants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
//? if forge {
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
*///?}
//? if neoforge && <26.1 {
/*import net.neoforged.neoforge.event.AddReloadListenerEvent;
*///?}
//? if >=26.1 {
/*import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
*///?}

/**
 * Lifecycle, reload and sync hooks without loader event types. Register handlers during mod
 * construction.
 * <p>
 * Add further semantic hooks here (for example "on server starting", "on register commands") when
 * business code needs them, instead of importing loader events outside {@code platform}.
 */
public final class PlatformEvents {
    private static final List<Runnable> COMMON_SETUP = new ArrayList<>();
    private static final List<Runnable> CLIENT_SETUP = new ArrayList<>();
    private static final List<JsonLoader> JSON_LOADERS = new ArrayList<>();
    private static final List<Consumer<List<ServerPlayer>>> DATAPACK_SYNC = new ArrayList<>();
    private static final List<Runnable> TAGS_UPDATED = new ArrayList<>();
    private static final List<Consumer<CommandDispatcher<CommandSourceStack>>> COMMANDS = new ArrayList<>();
    private static final List<AxeStripListener> AXE_STRIP = new ArrayList<>();

    private PlatformEvents() {
    }

    /** Runs once during common setup on both sides. */
    public static void onCommonSetup(Runnable task) {
        COMMON_SETUP.add(task);
    }

    /** Runs once during client setup; never fires on a dedicated server. */
    public static void onClientSetup(Runnable task) {
        CLIENT_SETUP.add(task);
    }

    /**
     * Loads every {@code data/<namespace>/<directory>/**.json} on server start and {@code /reload}.
     * The map is keyed by {@code namespace:path} (path without directory and extension), sorted.
     */
    public static void onDataReload(String name, String directory, Consumer<Map<String, JsonElement>> apply) {
        JSON_LOADERS.add(new JsonLoader(name, List.of(directory), false, files -> apply.accept(files.get(directory))));
    }

    /**
     * Like {@link #onDataReload}, for data that recipes need while they decode: {@code apply} receives
     * every {@code data/<namespace>/<directory>/**.json} of each directory (keyed by directory, then by
     * {@code namespace:path}) before any recipe decodes — up to 1.21.1 at the end of the preparation
     * stage (recipes decode when they apply), from 26.1 before any preparation starts (recipes decode
     * while they prepare). It may run off the main thread and must only publish immutable state.
     */
    public static void onDataReloadEarly(String name, List<String> directories, Consumer<Map<String, Map<String, JsonElement>>> apply) {
        JSON_LOADERS.add(new JsonLoader(name, List.copyOf(directories), true, apply));
    }

    /**
     * Server side: called when players need synced data — one player when they log in, all players
     * after a datapack reload.
     */
    public static void onDatapackSync(Consumer<List<ServerPlayer>> sender) {
        DATAPACK_SYNC.add(sender);
    }

    /** Both sides: called after tags are (re)loaded or received. */
    public static void onTagsUpdated(Runnable task) {
        TAGS_UPDATED.add(task);
    }

    /** Receives axe stripping on the server. */
    @FunctionalInterface
    public interface AxeStripListener {
        void stripped(ServerLevel level, BlockPos pos, BlockState original);
    }

    /**
     * Server side: called when an axe strips a block that has a stripped form (vanilla logs and wood,
     * and the mod's logs), before the block changes.
     */
    public static void onAxeStrip(AxeStripListener listener) {
        AXE_STRIP.add(listener);
    }

    /** Server side: registers commands whenever the command tree is built. */
    public static void onRegisterCommands(Consumer<CommandDispatcher<CommandSourceStack>> registrar) {
        COMMANDS.add(registrar);
    }

    private static final class JsonLoader extends SimplePreparableReloadListener<Map<String, Map<String, JsonElement>>> {
        private final String name;
        private final List<String> directories;
        private final boolean early;
        private final Consumer<Map<String, Map<String, JsonElement>>> apply;

        private JsonLoader(String name, List<String> directories, boolean early, Consumer<Map<String, Map<String, JsonElement>>> apply) {
            this.name = name;
            this.directories = directories;
            this.early = early;
            this.apply = apply;
        }

        //? if >=26.1 {
        /*/^* Recipes decode during their preparation from 1.21.2 on; this runs before any preparation. ^/
        @Override
        public void prepareSharedState(PreparableReloadListener.SharedState state) {
            if (early) {
                apply.accept(readAll(state.resourceManager()));
            }
        }
        *///?}

        @Override
        protected Map<String, Map<String, JsonElement>> prepare(ResourceManager manager, ProfilerFiller profiler) {
            //? if >=26.1 {
            /*if (early) {
                return Map.of();
            }
            *///?}
            Map<String, Map<String, JsonElement>> result = readAll(manager);
            if (early) {
                // Up to 1.21.1 recipes decode when they apply; every preparation finishes before any applies.
                apply.accept(result);
            }
            return result;
        }

        private Map<String, Map<String, JsonElement>> readAll(ResourceManager manager) {
            Map<String, Map<String, JsonElement>> result = new TreeMap<>();
            for (String directory : directories) {
                result.put(directory, read(manager, directory));
            }
            return result;
        }

        private Map<String, JsonElement> read(ResourceManager manager, String directory) {
            Map<String, JsonElement> result = new TreeMap<>();
            String prefix = directory + "/";
            for (var entry : manager.listResources(directory, id -> id.getPath().endsWith(".json")).entrySet()) {
                String path = entry.getKey().getPath();
                String key = entry.getKey().getNamespace() + ":" + path.substring(prefix.length(), path.length() - ".json".length());
                Resource resource = entry.getValue();
                try (Reader reader = resource.openAsReader()) {
                    result.put(key, JsonParser.parseReader(reader));
                } catch (Exception exception) {
                    FoodIndustrial.LOGGER.error("Failed to read {} file {}", name, entry.getKey(), exception);
                }
            }
            return result;
        }

        @Override
        protected void apply(Map<String, Map<String, JsonElement>> data, ResourceManager manager, ProfilerFiller profiler) {
            if (!early) {
                apply.accept(data);
            }
        }
    }

    /** Called by {@link ModEntrypoint}. */
    static void bind(IEventBus modBus) {
        modBus.addListener(PlatformEvents::commonSetup);
        modBus.addListener(PlatformEvents::clientSetup);
        gameBus().addListener(PlatformEvents::addReloadListeners);
        gameBus().addListener(PlatformEvents::datapackSync);
        gameBus().addListener(PlatformEvents::tagsUpdated);
        gameBus().addListener(PlatformEvents::registerCommands);
        gameBus().addListener(PlatformEvents::toolModification);
    }

    private static void toolModification(BlockEvent.BlockToolModificationEvent event) {
        //? if forge {
        boolean strip = event.getToolAction() == ToolActions.AXE_STRIP;
        //?} else {
        /*boolean strip = event.getItemAbility() == ItemAbilities.AXE_STRIP;
        *///?}
        if (!strip || event.isSimulated() || AXE_STRIP.isEmpty() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockState state = event.getState();
        if (AxeItem.getAxeStrippingState(state) != null || PlatformPlants.isStrippableLog(state.getBlock())) {
            AXE_STRIP.forEach(listener -> listener.stripped(level, event.getPos(), state));
        }
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        COMMANDS.forEach(registrar -> registrar.accept(event.getDispatcher()));
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        COMMON_SETUP.forEach(event::enqueueWork);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        CLIENT_SETUP.forEach(event::enqueueWork);
    }

    private static void tagsUpdated(TagsUpdatedEvent event) {
        TAGS_UPDATED.forEach(Runnable::run);
    }

    //? if forge {
    private static IEventBus gameBus() {
        return MinecraftForge.EVENT_BUS;
    }

    private static void addReloadListeners(AddReloadListenerEvent event) {
        JSON_LOADERS.forEach(event::addListener);
    }

    private static void datapackSync(OnDatapackSyncEvent event) {
        List<ServerPlayer> players = event.getPlayer() != null ? List.of(event.getPlayer()) : event.getPlayerList().getPlayers();
        DATAPACK_SYNC.forEach(sender -> sender.accept(players));
    }
    //?} else {
    /*private static IEventBus gameBus() {
        return NeoForge.EVENT_BUS;
    }

    private static void datapackSync(OnDatapackSyncEvent event) {
        List<ServerPlayer> players = event.getRelevantPlayers().toList();
        DATAPACK_SYNC.forEach(sender -> sender.accept(players));
    }
    *///?}

    //? if neoforge && <26.1 {
    /*private static void addReloadListeners(AddReloadListenerEvent event) {
        JSON_LOADERS.forEach(event::addListener);
    }
    *///?}

    //? if >=26.1 {
    /*private static void addReloadListeners(AddServerReloadListenersEvent event) {
        for (JsonLoader loader : JSON_LOADERS) {
            event.addListener(PlatformIdentifier.id(loader.name), loader);
        }
    }
    *///?}
}
