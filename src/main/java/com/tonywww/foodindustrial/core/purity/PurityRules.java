package com.tonywww.foodindustrial.core.purity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.network.PlatformNetwork;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

/**
 * External purity rules (additives/10 §1.6): data pack files
 * {@code data/<ns>/foodindustrial/purity_rules/<name>.json} that set the purity of vanilla and
 * other mods' items and fluids, matched by tag or id.
 * <pre>
 * { "items": ["#c:dusts/salt", "othermod:sea_salt"], "fluids": ["#c:brine"], "purity": "crude" }
 * </pre>
 * {@code purity} is a percentage, or a grade name standing for that grade's lower bound by the bounds
 * of the matched item's material ({@code poor}: the external default). A grade name on an item that
 * is in no material tag, or whose material lacks the grade, is logged and ignored for that item; so
 * is any rule matching an item of a material without purity (additives/10 §1.1).
 * Without a matching rule the external default applies; with several, the lowest wins. The loader
 * does not cap the value; the mod's own rules are checked at data generation ({@link #checkBundled}).
 * Rules are synced to clients on login and after {@code /reload}, so client displays match server
 * checks.
 */
public final class PurityRules {
    private static final Codec<List<String>> STRINGS = Codec.STRING.listOf();
    private static final PlatformNetwork.Channel<SyncPayload> SYNC =
        PlatformNetwork.toClient("purity_rules", SyncPayload::read, payload -> apply(payload.rules()));

    /** Rules with their lookup caches; replaced as a whole so stale cache entries never survive a reload. */
    private static volatile State state = new State(List.of());
    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();

    private PurityRules() {
    }

    /**
     * The {@code purity} of a rule as written: a grade name or a percentage.
     *
     * @param grade   grade name, or {@code null} for a percentage
     * @param percent percentage, used when {@code grade} is {@code null}
     */
    public record Value(@Nullable PurityGrade grade, double percent) {
        /** {@code b} for an item or fluid of {@code material}; {@link Purity#NONE} when it cannot be resolved. */
        public int resolve(@Nullable String material) {
            if (grade == null) {
                return PurityScale.ofPercent(percent);
            }
            PurityData.Material data = PurityData.current().material(material);
            return data == null ? Purity.NONE : data.gradeValue(grade);
        }

        /** Text as written in the file. */
        public String text() {
            return grade != null ? grade.id() : (percent == Math.rint(percent) ? Long.toString((long) percent) : Double.toString(percent));
        }

        static Value parse(String text) {
            PurityGrade grade = PurityGrade.byId(text);
            if (grade != null) {
                return new Value(grade, 0.0);
            }
            double percent = Double.parseDouble(text);
            PurityScale.ofPercent(percent);
            return new Value(null, percent);
        }
    }

    /** One rule file. Entries are {@code #tag} or ids, as written in the file. */
    public record Rule(String source, List<String> items, List<String> fluids, Value value) {
        public Rule {
            items = List.copyOf(items);
            fluids = List.copyOf(fluids);
        }
    }

    private record State(List<Rule> rules, Map<Item, Integer> items, Map<Fluid, Integer> fluids) {
        State(List<Rule> rules) {
            this(List.copyOf(rules), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
        }
    }

    /** Hooks loading, syncing and cache invalidation. Call during mod construction. */
    public static void bootstrap() {
        PlatformEvents.onDataReload("purity_rules", "foodindustrial/purity_rules", PurityRules::load);
        PlatformEvents.onDatapackSync(players -> {
            SyncPayload payload = new SyncPayload(state.rules());
            players.forEach(player -> PlatformNetwork.sendToPlayer(player, SYNC, payload));
        });
        PlatformEvents.onTagsUpdated(PurityRules::invalidate);
        PurityData.onChanged(PurityRules::invalidate);
    }

    /**
     * Runs {@code listener} whenever the rules change on this side (server reload, or a sync received
     * by the client); for caches that depend on effective purity, such as ingredient item lists.
     */
    public static void onChanged(Runnable listener) {
        LISTENERS.add(listener);
    }

    /** Rules currently in effect on this side. */
    public static List<Rule> rules() {
        return state.rules();
    }

    /**
     * Rule value of an item of vanilla or another mod: the lowest matching rule, or {@link Purity#NONE}
     * when no rule applies.
     *
     * @param material the item's material (by tag), for grade names
     */
    static int value(Item item, @Nullable String material) {
        State current = state;
        if (current.rules().isEmpty()) {
            return Purity.NONE;
        }
        return current.items().computeIfAbsent(item, key -> computeItem(current.rules(), key, material));
    }

    /** Rule value of a fluid of vanilla or another mod, as {@link #value(Item, String)}. */
    static int value(Fluid fluid, @Nullable String material) {
        State current = state;
        if (current.rules().isEmpty()) {
            return Purity.NONE;
        }
        return current.fluids().computeIfAbsent(fluid, key -> computeFluid(current.rules(), key, material));
    }

    /**
     * An item of a material without purity: rules matching it are logged once per reload and ignored
     * (additives/10 §1.1).
     */
    static void ignoredFor(Item item, String plainMaterial) {
        State current = state;
        if (!current.rules().isEmpty()) {
            current.items().computeIfAbsent(item, key -> {
                ItemStack probe = new ItemStack(item);
                String id = PlatformTags.itemId(item);
                for (Rule rule : current.rules()) {
                    if (matchesItem(rule.items(), probe, id)) {
                        warnPlain(rule, id, plainMaterial);
                    }
                }
                return Purity.NONE;
            });
        }
    }

    /** A fluid of a material without purity, as {@link #ignoredFor(Item, String)}. */
    static void ignoredFor(Fluid fluid, String plainMaterial) {
        State current = state;
        if (!current.rules().isEmpty()) {
            current.fluids().computeIfAbsent(fluid, key -> {
                String id = PlatformTags.fluidId(fluid);
                for (Rule rule : current.rules()) {
                    if (matchesFluid(rule.fluids(), fluid, id)) {
                        warnPlain(rule, id, plainMaterial);
                    }
                }
                return Purity.NONE;
            });
        }
    }

    private static void warnPlain(Rule rule, String id, String plainMaterial) {
        FoodIndustrial.LOGGER.warn("Purity rule {}: {} belongs to {}, which carries no purity; ignored", rule.source(), id, plainMaterial);
    }

    private static int computeItem(List<Rule> rules, Item item, @Nullable String material) {
        ItemStack probe = new ItemStack(item);
        String id = PlatformTags.itemId(item);
        int result = Purity.NONE;
        for (Rule rule : rules) {
            if (matchesItem(rule.items(), probe, id)) {
                result = lowest(result, rule, id, material);
            }
        }
        return result;
    }

    private static int lowest(int current, Rule rule, String id, @Nullable String material) {
        int b = rule.value().resolve(material);
        if (b == Purity.NONE) {
            FoodIndustrial.LOGGER.warn("Purity rule {}: {} {} for {}, ignored", rule.source(), rule.value().text(),
                material == null ? "needs a material, but it is in no material tag" : "is not a grade of " + material, id);
            return current;
        }
        return current == Purity.NONE ? b : Math.min(current, b);
    }

    private static boolean matchesItem(List<String> entries, ItemStack probe, String id) {
        for (String entry : entries) {
            if (entry.startsWith("#") ? probe.is(PlatformTags.itemTag(entry.substring(1))) : entry.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static int computeFluid(List<Rule> rules, Fluid fluid, @Nullable String material) {
        String id = PlatformTags.fluidId(fluid);
        int result = Purity.NONE;
        for (Rule rule : rules) {
            if (matchesFluid(rule.fluids(), fluid, id)) {
                result = lowest(result, rule, id, material);
            }
        }
        return result;
    }

    private static boolean matchesFluid(List<String> entries, Fluid fluid, String id) {
        for (String entry : entries) {
            if (entry.startsWith("#") ? fluid.defaultFluidState().is(PlatformTags.fluidTag(entry.substring(1))) : entry.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static void invalidate() {
        state = new State(state.rules());
    }

    private static void apply(List<Rule> loaded) {
        state = new State(loaded);
        LISTENERS.forEach(Runnable::run);
    }

    private static void load(Map<String, JsonElement> files) {
        List<Rule> loaded = new ArrayList<>();
        files.forEach((source, json) -> {
            try {
                loaded.add(parse(source, json));
            } catch (RuntimeException exception) {
                FoodIndustrial.LOGGER.error("Skipping purity rule {}: {}", source, exception.getMessage());
            }
        });
        apply(loaded);
        FoodIndustrial.LOGGER.info("Loaded {} external purity rules", loaded.size());
    }

    /**
     * Parses one rule file. Unknown ids are logged and dropped; anything malformed throws.
     *
     * @throws IllegalArgumentException for malformed files
     */
    public static Rule parse(String source, JsonElement json) {
        if (!json.isJsonObject()) {
            throw new IllegalArgumentException("not a JSON object");
        }
        JsonObject object = json.getAsJsonObject();
        if (!object.has("purity")) {
            throw new IllegalArgumentException("missing \"purity\"");
        }
        Value value = parsePurity(object.get("purity"));
        if (!object.has("items") && !object.has("fluids")) {
            throw new IllegalArgumentException("needs \"items\" or \"fluids\"");
        }
        return new Rule(source, entries(source, object, "items", true), entries(source, object, "fluids", false), value);
    }

    private static Value parsePurity(JsonElement value) {
        if (!value.isJsonPrimitive()) {
            throw new IllegalArgumentException("\"purity\" must be a number or a grade name");
        }
        JsonPrimitive primitive = value.getAsJsonPrimitive();
        if (primitive.isNumber()) {
            double percent = primitive.getAsDouble();
            PurityScale.ofPercent(percent);
            return new Value(null, percent);
        }
        PurityGrade grade = PurityGrade.byId(primitive.getAsString());
        if (grade == null) {
            throw new IllegalArgumentException("unknown grade \"" + primitive.getAsString() + "\"");
        }
        return new Value(grade, 0.0);
    }

    private static List<String> entries(String source, JsonObject object, String key, boolean items) {
        if (!object.has(key)) {
            return List.of();
        }
        if (!object.get(key).isJsonArray()) {
            throw new IllegalArgumentException("\"" + key + "\" must be a list");
        }
        JsonArray array = object.getAsJsonArray(key);
        List<String> result = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                throw new IllegalArgumentException("\"" + key + "\" entries must be strings");
            }
            String entry = element.getAsString();
            String id = entry.startsWith("#") ? entry.substring(1) : entry;
            if (!PlatformTags.isValidId(id)) {
                throw new IllegalArgumentException("invalid id \"" + entry + "\"");
            }
            if (!entry.startsWith("#") && !exists(id, items)) {
                // Entries of mods that are not installed (the integrations' rules, I25) are skipped quietly.
                if (com.tonywww.foodindustrial.platform.Platform.isModLoaded(id.contains(":") ? id.substring(0, id.indexOf(':')) : "minecraft")) {
                    FoodIndustrial.LOGGER.warn("Purity rule {}: unknown {} {}, ignored", source, items ? "item" : "fluid", id);
                }
                continue;
            }
            result.add(entry);
        }
        return result;
    }

    private static boolean exists(String id, boolean item) {
        Optional<?> found = item ? PlatformTags.item(id) : PlatformTags.fluid(id);
        return found.isPresent();
    }

    /**
     * Data generation check for the mod's own rule files: for every material an entry belongs to, the
     * value must not exceed that material's crude bound (its lowest bound when it has no crude grade,
     * its source purity for K0).
     *
     * @param materialsOf materials whose tags hold an entry ({@code #tag} or id; {@code true} for fluids)
     * @throws IllegalStateException when an entry has no material or a value is too high
     */
    public static void checkBundled(Rule rule, BiFunction<String, Boolean, List<String>> materialsOf) {
        for (String item : rule.items()) {
            checkBundled(rule, item, materialsOf.apply(item, false));
        }
        for (String fluid : rule.fluids()) {
            checkBundled(rule, fluid, materialsOf.apply(fluid, true));
        }
    }

    private static void checkBundled(Rule rule, String entry, List<String> materials) {
        if (materials.isEmpty()) {
            throw new IllegalStateException("Bundled purity rule " + rule.source() + ": " + entry + " is in no material tag");
        }
        for (String material : materials) {
            PurityData.Material data = PurityData.current().material(material);
            if (data == null) {
                throw new IllegalStateException("Bundled purity rule " + rule.source() + ": " + entry + " belongs to "
                    + material + ", which carries no purity");
            }
            int cap = Purity.NONE;
            if (data != null) {
                cap = !data.graded() ? data.sourceByte()
                    : data.gradeValue(PurityGrade.CRUDE) != Purity.NONE ? data.gradeValue(PurityGrade.CRUDE) : data.lowestBound();
            }
            int b = rule.value().resolve(material);
            if (cap == Purity.NONE || b == Purity.NONE || b > cap) {
                throw new IllegalStateException("Bundled purity rule " + rule.source() + " sets " + rule.value().text()
                    + " for " + entry + " (" + material + "), above its crude bound");
            }
        }
    }

    private record SyncPayload(List<Rule> rules) implements PlatformNetwork.Payload {
        @Override
        public void write(DataWriter out) {
            DataWriter.ListWriter list = out.list("rules");
            for (Rule rule : rules) {
                DataWriter entry = list.add();
                entry.putString("source", rule.source());
                entry.putCodec("items", STRINGS, rule.items());
                entry.putCodec("fluids", STRINGS, rule.fluids());
                entry.putString("purity", rule.value().text());
            }
        }

        static SyncPayload read(DataReader in) {
            List<Rule> rules = new ArrayList<>();
            for (DataReader entry : in.list("rules")) {
                rules.add(new Rule(
                    entry.getString("source", ""),
                    entry.getCodec("items", STRINGS).orElse(List.of()),
                    entry.getCodec("fluids", STRINGS).orElse(List.of()),
                    Value.parse(entry.getString("purity", "poor"))));
            }
            return new SyncPayload(rules);
        }
    }
}
