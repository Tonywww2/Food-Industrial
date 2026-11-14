package com.tonywww.foodindustrial.food.nutrition;

import com.google.gson.JsonElement;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.network.PlatformNetwork;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Nutrition data in effect (foods/03 §2, I34): the data pack files {@code data/<ns>/foodindustrial/nutrition/*.json},
 * loaded on server start and {@code /reload} and synced to clients (items, serving grams and the components, not
 * {@code basis}). The server and the client keep separate tables, so a single-player client receiving the sync
 * does not replace what the integrated server resolved.
 */
public final class Nutrition {
    private static final PlatformNetwork.Channel<SyncPayload> SYNC =
        PlatformNetwork.toClient("nutrition", SyncPayload::read, payload -> client = payload.table());

    private static volatile Map<Item, NutritionData> server = Map.of();
    private static volatile Map<Item, NutritionData> client = Map.of();

    private Nutrition() {
    }

    /** Hooks loading and syncing. Call during mod construction. */
    public static void bootstrap() {
        PlatformEvents.onDataReload("nutrition", "foodindustrial/nutrition", Nutrition::load);
        PlatformEvents.onDatapackSync(players -> {
            SyncPayload payload = new SyncPayload(server);
            players.forEach(player -> PlatformNetwork.sendToPlayer(player, SYNC, payload));
        });
        NutritionCommands.bootstrap();
        NutritionGen.bootstrap();
    }

    /** Server data of one serving of {@code stack}, or {@code null} when it has none. Allocation-free. */
    @Nullable
    public static NutritionData of(ItemStack stack) {
        return server.get(stack.getItem());
    }

    /** Server data of {@code item}, or {@code null}. */
    @Nullable
    public static NutritionData of(Item item) {
        return server.get(item);
    }

    /** Data the client received for {@code stack} (tooltips, panels), or {@code null}. */
    @Nullable
    public static NutritionData client(ItemStack stack) {
        return client.get(stack.getItem());
    }

    /** All server data, for commands. */
    public static Map<Item, NutritionData> all() {
        return server;
    }

    private static void load(Map<String, JsonElement> files) {
        List<NutritionFile> parsed = new ArrayList<>();
        files.forEach((source, json) -> {
            try {
                parsed.add(NutritionFile.parse(source, json));
            } catch (RuntimeException exception) {
                FoodIndustrial.LOGGER.error("Skipping nutrition data {}: {}", source, exception.getMessage());
            }
        });
        Map<Item, NutritionData> resolved = NutritionData.resolve(parsed, Platform::isModLoaded, PlatformTags::item,
            FoodIndustrial.LOGGER::warn);
        server = new IdentityHashMap<>(resolved);
        FoodIndustrial.LOGGER.info("Loaded nutrition data for {} items from {} files", resolved.size(), parsed.size());
    }

    private record SyncPayload(Map<Item, NutritionData> table) implements PlatformNetwork.Payload {
        @Override
        public void write(DataWriter out) {
            DataWriter.ListWriter list = out.list("items");
            table.forEach((item, data) -> {
                DataWriter entry = list.add();
                entry.putString("item", PlatformTags.itemId(item));
                entry.putDouble("serving_g", data.servingG());
                entry.putDouble("impurity", data.impurity());
                for (Component component : Component.all()) {
                    entry.putDouble(component.key(), data.amount(component));
                }
            });
        }

        static SyncPayload read(DataReader in) {
            Map<Item, NutritionData> table = new IdentityHashMap<>();
            for (DataReader entry : in.list("items")) {
                Optional<Item> item = PlatformTags.item(entry.getString("item", ""));
                if (item.isEmpty()) {
                    continue;
                }
                double[] amounts = new double[Component.all().length];
                for (Component component : Component.all()) {
                    amounts[component.ordinal()] = entry.getDouble(component.key(), 0.0);
                }
                table.put(item.get(), new NutritionData(entry.getDouble("serving_g", 0.0), amounts, 0, entry.getDouble("impurity", 0.0),
                    null, null, 0));
            }
            return new SyncPayload(table);
        }
    }
}
