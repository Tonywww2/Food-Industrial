package com.tonywww.foodindustrial.core.purity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.network.PlatformNetwork;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Material categories and per-material purity data (additives/10 §1.3–1.5, §4.7): the grade bounds of
 * each category, and for each material its category, source purity and grade overrides.
 * <pre>
 * data/foodindustrial/purity_categories/k1_inorganic.json
 * { "id": "foodindustrial:k1_inorganic", "grades": { "crude": 90, "refined": 95, "food": 98, "lab": 99.8 } }
 * data/foodindustrial/purity_materials/salt.json
 * { "id": "foodindustrial:salt", "category": "foodindustrial:k1_inorganic", "source_purity": 83,
 *   "grades": { "crude": 90, "refined": 95, "food": 97, "lab": 99.8 } }
 * </pre>
 * Category files give all four bounds ({@code null} = no such grade; K0 has none). Material files
 * override single bounds ({@code null} = the material has no such grade). Bounds must increase
 * strictly and have no more decimals than the display shows in their range.
 * <p>
 * The built-in data comes from the material master table ({@code purity_categories.json} and the
 * materials registered through {@link Purity#registerMaterial}); data generation writes it as the
 * files above. Server start and {@code /reload} load the files before recipes, so recipes can turn
 * grade names into bytes while they load; the result is synced to clients with the external rules.
 * An instance is immutable.
 */
public final class PurityData {
    public static final String CATEGORY_DIRECTORY = "purity_categories";
    public static final String MATERIAL_DIRECTORY = "purity_materials";
    /** Returned for "no value" (no such grade, no source purity, no purity at all). */
    public static final int NONE = Integer.MIN_VALUE;

    private static final String CATEGORIES_RESOURCE = "/foodindustrial/purity_categories.json";
    private static final Codec<List<String>> STRINGS = Codec.STRING.listOf();

    /** Material purity JSON registered from the master table, by material id. */
    private static final Map<String, JsonObject> BUILTIN_MATERIALS = new LinkedHashMap<>();
    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();
    @Nullable
    private static volatile PurityData current;
    private static volatile boolean loaded;

    /** Category bounds as written, CRUDE..LAB; {@code null} = no such grade. */
    public record Category(String id, List<Optional<BigDecimal>> bounds) {
        public Category {
            bounds = List.copyOf(bounds);
        }
    }

    /**
     * A material with its effective bounds (category merged with overrides).
     *
     * @param bounds     written bounds CRUDE..LAB, empty = no such grade
     * @param boundBytes {@code b} of each bound CRUDE..LAB, {@link #NONE} = no such grade
     * @param source     written source purity, or {@code null}
     * @param sourceByte {@code b} of the source purity, or {@link #NONE}
     * @param missing    value of items and fluids without stored purity: the external default
     *                   (graded), the source purity (K0) or {@link #NONE}
     */
    public record Material(String id, String category, List<Optional<BigDecimal>> bounds, int[] boundBytes,
                           @Nullable BigDecimal source, int sourceByte, int missing, JsonObject json) {
        /** Whether the material has at least one grade bound; K0 materials show "content" instead. */
        public boolean graded() {
            for (int b : boundBytes) {
                if (b != NONE) {
                    return true;
                }
            }
            return false;
        }

        /** Grade of {@code b}; empty for ungraded materials. */
        public Optional<PurityGrade> gradeOf(int b) {
            if (!graded()) {
                return Optional.empty();
            }
            for (int i = PurityGrade.BOUNDED.length - 1; i >= 0; i--) {
                if (boundBytes[i] != NONE && b >= boundBytes[i]) {
                    return Optional.of(PurityGrade.BOUNDED[i]);
                }
            }
            return Optional.of(PurityGrade.POOR);
        }

        /** Lower bound of {@code grade} ({@code -128} for poor); empty when the material lacks it. */
        public OptionalInt lowerBound(PurityGrade grade) {
            if (grade == PurityGrade.POOR) {
                return graded() ? OptionalInt.of(PurityScale.MIN) : OptionalInt.empty();
            }
            int b = boundBytes[grade.ordinal() - 1];
            return b == NONE ? OptionalInt.empty() : OptionalInt.of(b);
        }

        /**
         * Value a grade name stands for in external rules and {@code withPurity}: the lower bound, and
         * the external default for poor. {@link #NONE} when the material lacks the grade.
         */
        public int gradeValue(PurityGrade grade) {
            if (grade == PurityGrade.POOR) {
                return missing;
            }
            return boundBytes[grade.ordinal() - 1];
        }

        /** Lowest bound, or {@link #NONE} for ungraded materials. */
        public int lowestBound() {
            return lowest(boundBytes);
        }

        /**
         * Values listed in the creative tab (additives/10 §5): the external default and each grade's
         * lower bound for graded materials, the source purity for K0.
         */
        public List<Integer> listedValues() {
            List<Integer> values = new ArrayList<>();
            if (graded()) {
                values.add(missing);
                for (int b : boundBytes) {
                    if (b != NONE && b > values.get(values.size() - 1)) {
                        values.add(b);
                    }
                }
            } else if (sourceByte != NONE) {
                values.add(sourceByte);
            }
            return values;
        }
    }

    private final Map<String, Category> categories;
    private final Map<String, Material> materials;

    private PurityData(Map<String, Category> categories, Map<String, Material> materials) {
        this.categories = Collections.unmodifiableMap(categories);
        this.materials = Collections.unmodifiableMap(materials);
    }

    public Map<String, Category> categories() {
        return categories;
    }

    public Map<String, Material> materials() {
        return materials;
    }

    @Nullable
    public Material material(@Nullable String id) {
        return id == null ? null : materials.get(id);
    }

    // ---- building ----

    /**
     * Builds data from category and material files (keyed by {@code namespace:path}). Files with
     * errors are reported to {@code errors} and skipped; materials of a skipped category too.
     */
    public static PurityData build(Map<String, JsonElement> categoryFiles, Map<String, JsonElement> materialFiles, Consumer<String> errors) {
        Map<String, Category> categories = new TreeMap<>();
        categoryFiles.forEach((id, json) -> {
            try {
                categories.put(id, parseCategory(id, json));
            } catch (RuntimeException exception) {
                errors.accept("purity category " + id + ": " + exception.getMessage());
            }
        });
        Map<String, Material> materials = new LinkedHashMap<>();
        materialFiles.forEach((id, json) -> {
            try {
                materials.put(id, parseMaterial(id, json, categories));
            } catch (RuntimeException exception) {
                errors.accept("purity material " + id + ": " + exception.getMessage());
            }
        });
        return new PurityData(categories, materials);
    }

    /**
     * Parses a category file.
     *
     * @throws IllegalArgumentException when malformed
     */
    public static Category parseCategory(String id, JsonElement json) {
        JsonObject object = object(json);
        checkId(id, object);
        JsonObject grades = object.has("grades") ? object(object.get("grades")) : new JsonObject();
        checkGradeKeys(grades);
        List<Optional<BigDecimal>> bounds = new ArrayList<>();
        for (PurityGrade grade : PurityGrade.BOUNDED) {
            bounds.add(grades.has(grade.id()) ? percent(grades.get(grade.id()), grade.id()) : Optional.empty());
        }
        checkIncreasing(bounds);
        return new Category(id, bounds);
    }

    /**
     * Parses a material file against the given categories.
     *
     * @throws IllegalArgumentException when malformed or the category is unknown
     */
    public static Material parseMaterial(String id, JsonElement json, Map<String, Category> categories) {
        JsonObject object = object(json);
        checkId(id, object);
        if (!object.has("category") || !object.get("category").isJsonPrimitive()) {
            throw new IllegalArgumentException("missing \"category\"");
        }
        String categoryId = object.get("category").getAsString();
        Category category = categories.get(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("unknown category " + categoryId);
        }
        List<Optional<BigDecimal>> bounds = new ArrayList<>(category.bounds());
        if (object.has("grades")) {
            JsonObject grades = object(object.get("grades"));
            checkGradeKeys(grades);
            for (PurityGrade grade : PurityGrade.BOUNDED) {
                if (grades.has(grade.id())) {
                    bounds.set(grade.ordinal() - 1, percent(grades.get(grade.id()), grade.id()));
                }
            }
        }
        checkIncreasing(bounds);
        int[] bytes = new int[bounds.size()];
        int previous = NONE;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = bounds.get(i).map(value -> PurityScale.ofPercent(value.doubleValue())).orElse(NONE);
            if (bytes[i] != NONE) {
                if (previous != NONE && bytes[i] <= previous) {
                    throw new IllegalArgumentException("grade bounds fall on the same scale step");
                }
                previous = bytes[i];
            }
        }
        BigDecimal source = null;
        if (object.has("source_purity") && !object.get("source_purity").isJsonNull()) {
            source = percent(object.get("source_purity"), "source_purity").orElseThrow();
        }
        int sourceByte = source == null ? NONE : PurityScale.ofPercent(source.doubleValue());
        int missing = previous != NONE ? PurityScale.externalDefault(lowest(bytes)) : sourceByte;
        return new Material(id, categoryId, List.copyOf(bounds), bytes, source, sourceByte, missing, object.deepCopy());
    }

    private static int lowest(int[] bytes) {
        for (int b : bytes) {
            if (b != NONE) {
                return b;
            }
        }
        return NONE;
    }

    private static JsonObject object(JsonElement json) {
        if (json == null || !json.isJsonObject()) {
            throw new IllegalArgumentException("not a JSON object");
        }
        return json.getAsJsonObject();
    }

    private static void checkId(String id, JsonObject object) {
        if (object.has("id") && !id.equals(object.get("id").getAsString())) {
            throw new IllegalArgumentException("\"id\" " + object.get("id").getAsString() + " does not match the file");
        }
    }

    private static void checkGradeKeys(JsonObject grades) {
        for (String key : grades.keySet()) {
            PurityGrade grade = PurityGrade.byId(key);
            if (grade == null || grade == PurityGrade.POOR) {
                throw new IllegalArgumentException("unknown grade \"" + key + "\" (crude, refined, food, lab)");
            }
        }
    }

    private static Optional<BigDecimal> percent(JsonElement value, String field) {
        if (value.isJsonNull()) {
            return Optional.empty();
        }
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException("\"" + field + "\" must be a percentage or null");
        }
        BigDecimal percent = value.getAsBigDecimal();
        PurityScale.checkWritten(percent);
        return Optional.of(percent);
    }

    private static void checkIncreasing(List<Optional<BigDecimal>> bounds) {
        BigDecimal previous = null;
        for (Optional<BigDecimal> bound : bounds) {
            if (bound.isPresent()) {
                if (previous != null && bound.get().compareTo(previous) <= 0) {
                    throw new IllegalArgumentException("grade bounds must increase strictly");
                }
                previous = bound.get();
            }
        }
    }

    // ---- state ----

    /** Data in effect on this side: the last loaded or synced data, or the built-in data. */
    public static PurityData current() {
        PurityData data = current;
        if (data == null) {
            data = builtin();
            current = data;
        }
        return data;
    }

    /** Runs {@code listener} whenever the data in effect changes on this side. */
    public static void onChanged(Runnable listener) {
        LISTENERS.add(listener);
    }

    /** Called by {@link Purity#registerMaterial} during mod construction. */
    static synchronized void registerBuiltin(String id, JsonObject json) {
        if (BUILTIN_MATERIALS.putIfAbsent(id, json.deepCopy()) != null) {
            throw new IllegalArgumentException("Duplicate purity material " + id);
        }
        if (!loaded) {
            current = null;
        }
    }

    /** Built-in data: the exported category bounds and the registered materials. */
    public static synchronized PurityData builtin() {
        Map<String, JsonElement> materials = new LinkedHashMap<>(BUILTIN_MATERIALS);
        return build(builtinCategories(), materials, error -> {
            throw new IllegalStateException("Built-in " + error);
        });
    }

    /** Category files of {@code purity_categories.json}, keyed by id. */
    public static Map<String, JsonElement> builtinCategories() {
        try (InputStream in = PurityData.class.getResourceAsStream(CATEGORIES_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + CATEGORIES_RESOURCE + "; run tools/materials/export_materials.py");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                Map<String, JsonElement> result = new LinkedHashMap<>();
                for (JsonElement element : JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("categories")) {
                    JsonObject category = element.getAsJsonObject();
                    result.put(category.get("id").getAsString(), category);
                }
                return result;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + CATEGORIES_RESOURCE, exception);
        }
    }

    /** Hooks loading, syncing and data generation. Call once during mod construction. */
    static void bootstrap() {
        PlatformEvents.onDataReloadEarly("purity_data", List.of(CATEGORY_DIRECTORY, MATERIAL_DIRECTORY), PurityData::load);
        PlatformNetwork.Channel<SyncPayload> sync = Sync.CHANNEL;
        PlatformEvents.onDatapackSync(players -> {
            SyncPayload payload = SyncPayload.of(current());
            players.forEach(player -> PlatformNetwork.sendToPlayer(player, sync, payload));
        });
        PlatformDatagen.register("purity data", PurityData::generate);
    }

    private static void apply(PurityData data) {
        current = data;
        loaded = true;
        LISTENERS.forEach(Runnable::run);
    }

    private static void load(Map<String, Map<String, JsonElement>> files) {
        PurityData data = build(files.getOrDefault(CATEGORY_DIRECTORY, Map.of()), files.getOrDefault(MATERIAL_DIRECTORY, Map.of()),
            error -> FoodIndustrial.LOGGER.error("Skipping {}", error));
        for (String id : BUILTIN_MATERIALS.keySet()) {
            if (!data.materials.containsKey(id)) {
                FoodIndustrial.LOGGER.warn("No purity data for material {}; its items show no purity", id);
            }
        }
        apply(data);
        FoodIndustrial.LOGGER.info("Loaded {} purity categories and {} purity materials", data.categories.size(), data.materials.size());
    }

    private static void generate(PlatformDatagen.Sink sink) {
        PurityData data = builtin();
        // null bounds mean "no such grade", so these files keep their nulls
        builtinCategories().forEach((id, json) ->
            sink.jsonKeepingNulls(PlatformDatagen.Kind.DATA, namespace(id), CATEGORY_DIRECTORY + "/" + path(id), json));
        data.materials.forEach((id, material) -> {
            JsonObject json = new JsonObject();
            json.addProperty("id", id);
            material.json().entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
            sink.jsonKeepingNulls(PlatformDatagen.Kind.DATA, namespace(id), MATERIAL_DIRECTORY + "/" + path(id), json);
        });
    }

    private static String namespace(String id) {
        return id.substring(0, id.indexOf(':'));
    }

    private static String path(String id) {
        return id.substring(id.indexOf(':') + 1);
    }

    /** Category file JSON of a parsed category (for syncing). */
    private static JsonObject categoryJson(Category category) {
        JsonObject grades = new JsonObject();
        for (PurityGrade grade : PurityGrade.BOUNDED) {
            Optional<BigDecimal> bound = category.bounds().get(grade.ordinal() - 1);
            grades.add(grade.id(), bound.<JsonElement>map(JsonPrimitive::new).orElse(JsonNull.INSTANCE));
        }
        JsonObject json = new JsonObject();
        json.add("grades", grades);
        return json;
    }

    /** Holds the channel so the data classes load without the network (unit tests). */
    private static final class Sync {
        static final PlatformNetwork.Channel<SyncPayload> CHANNEL =
            PlatformNetwork.toClient("purity_data", SyncPayload::read, SyncPayload::apply);
    }

    /** The loaded files as JSON text; the client rebuilds the data with the same parser. */
    private record SyncPayload(List<String> categoryIds, List<String> categories, List<String> materialIds, List<String> materials)
        implements PlatformNetwork.Payload {
        static SyncPayload of(PurityData data) {
            List<String> categoryIds = new ArrayList<>(data.categories.keySet());
            List<String> categories = data.categories.values().stream().map(category -> categoryJson(category).toString()).toList();
            List<String> materialIds = new ArrayList<>(data.materials.keySet());
            List<String> materials = data.materials.values().stream().map(material -> material.json().toString()).toList();
            return new SyncPayload(categoryIds, categories, materialIds, materials);
        }

        @Override
        public void write(DataWriter out) {
            out.putCodec("category_ids", STRINGS, categoryIds);
            out.putCodec("categories", STRINGS, categories);
            out.putCodec("material_ids", STRINGS, materialIds);
            out.putCodec("materials", STRINGS, materials);
        }

        static SyncPayload read(DataReader in) {
            return new SyncPayload(
                in.getCodec("category_ids", STRINGS).orElse(List.of()), in.getCodec("categories", STRINGS).orElse(List.of()),
                in.getCodec("material_ids", STRINGS).orElse(List.of()), in.getCodec("materials", STRINGS).orElse(List.of()));
        }

        void apply() {
            Map<String, JsonElement> categoryFiles = new LinkedHashMap<>();
            for (int i = 0; i < Math.min(categoryIds.size(), categories.size()); i++) {
                categoryFiles.put(categoryIds.get(i), JsonParser.parseString(categories.get(i)));
            }
            Map<String, JsonElement> materialFiles = new LinkedHashMap<>();
            for (int i = 0; i < Math.min(materialIds.size(), materials.size()); i++) {
                materialFiles.put(materialIds.get(i), JsonParser.parseString(materials.get(i)));
            }
            PurityData.apply(build(categoryFiles, materialFiles, error -> FoodIndustrial.LOGGER.error("Synced {}", error)));
        }
    }
}
