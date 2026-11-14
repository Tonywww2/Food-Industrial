package com.tonywww.foodindustrial.food.nutrition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Generates the mod's nutrition data files (foods/03 §1, I34): {@code data/foodindustrial/foodindustrial/nutrition/<mod>.json}
 * from the hand-made inputs {@code tools/nutrition/<mod>.json} (vanilla, Farmer's Delight and other bundled mods'
 * foods; the build passes the directory in the system property {@code foodindustrial.nutritionInputs}) and from the
 * entries the finished-product batches register ({@link #addProducts}). Generated files are never edited by hand.
 * <p>
 * Inputs may restrict entries to some Minecraft versions with {@code only_on}; a mod that is not on the node
 * produces no file. Every written file is checked: all six component keys, {@code basis} with {@code food},
 * {@code sources} and {@code confirmed} on hand-made entries, items that exist on the node, no item in two
 * entries; a failed check fails the data run. A report lists every item with its food values for comparison.
 */
public final class NutritionGen {
    /** System property naming the input directory. */
    public static final String INPUTS = "foodindustrial.nutritionInputs";
    private static final String NS = FoodIndustrial.MOD_ID;
    private static final String DIRECTORY = "foodindustrial/nutrition/";
    /** File of the mod's own products, whose basis is the algorithm rather than hand-made sources. */
    private static final String PRODUCTS = FoodIndustrial.MOD_ID;
    private static final List<Supplier<List<NutritionFile.Entry>>> PRODUCT_SOURCES = new ArrayList<>();

    private NutritionGen() {
    }

    static void bootstrap() {
        PlatformDatagen.register("nutrition", NutritionGen::generate);
        PlatformDatagen.addCheck(PlatformDatagen.Kind.DATA, (id, json, ops) -> {
            String path = id.substring(id.indexOf(':') + 1);
            if (id.startsWith(NS + ":") && path.startsWith(DIRECTORY)) {
                check(id, path.substring(DIRECTORY.length()), json);
            }
        });
    }

    /**
     * Registers entries of the mod's finished products (foods/01 §4), computed by a product batch; their
     * {@code basis} names the algorithm and the recipe. Call during mod construction.
     */
    public static void addProducts(Supplier<List<NutritionFile.Entry>> entries) {
        PRODUCT_SOURCES.add(entries);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        StringBuilder report = new StringBuilder("# Nutrition data (generated)\n\nNode: Minecraft ")
            .append(Platform.minecraftVersion()).append(". Saturation is in points; amounts are per serving.\n");
        Set<String> seen = new HashSet<>();
        for (NutritionFile file : inputs()) {
            if (!Platform.isModLoaded(file.mod())) {
                FoodIndustrial.LOGGER.info("Nutrition data: {} is not on this node, {} not generated", file.mod(), file.source());
                continue;
            }
            List<NutritionFile.Entry> entries = file.entries().stream().filter(entry -> entry.appliesTo(Platform.minecraftVersion())).toList();
            write(sink, new NutritionFile(file.mod(), file.mod(), file.priority(), entries), seen, report);
        }
        List<NutritionFile.Entry> products = new ArrayList<>();
        PRODUCT_SOURCES.forEach(source -> products.addAll(source.get()));
        if (!products.isEmpty()) {
            write(sink, new NutritionFile(PRODUCTS, PRODUCTS, 0, products), seen, report);
        }
        sink.report("nutrition_report.md", report.toString());
    }

    private static void write(PlatformDatagen.Sink sink, NutritionFile file, Set<String> seen, StringBuilder report) {
        for (NutritionFile.Entry entry : file.entries()) {
            for (String item : entry.items()) {
                if (!seen.add(item)) {
                    throw new IllegalStateException("Nutrition data: " + item + " appears twice in the generated files");
                }
            }
        }
        report.append("\n## ").append(file.mod()).append("\n\n| Item | Nutrition | Saturation | Serving (g) | Energy (kcal) |");
        for (Component component : Component.all()) {
            report.append(' ').append(component.key()).append(" |");
        }
        report.append(" impurity | Source |\n|---|---|---|---|---|");
        report.append("---|".repeat(Component.all().length + 1)).append("---|\n");
        for (NutritionFile.Entry entry : file.entries()) {
            for (String id : entry.items()) {
                Optional<Item> item = PlatformTags.item(id);
                PlatformItems.FoodValues food = item.map(PlatformItems::foodValues).orElse(null);
                report.append("| ").append(id).append(" | ")
                    .append(food == null ? "–" : String.valueOf(food.nutrition())).append(" | ")
                    .append(food == null ? "–" : NutritionCommands.plain(Math.round(food.saturation() * 100) / 100.0)).append(" | ")
                    .append(NutritionCommands.plain(entry.servingG())).append(" | ")
                    .append(entry.energyKcal().map(NutritionCommands::plain).orElse("–")).append(" |");
                for (Component component : Component.all()) {
                    report.append(' ').append(NutritionCommands.plain(entry.amount(component))).append(" |");
                }
                report.append(' ').append(NutritionCommands.plain(entry.impurity())).append(" |");
                report.append(' ').append(source(entry)).append(" |\n");
            }
        }
        sink.json(PlatformDatagen.Kind.DATA, NS, DIRECTORY + file.mod(), file.toJson());
    }

    private static String source(NutritionFile.Entry entry) {
        if (entry.basis().isEmpty() || !entry.basis().get().isJsonObject()) {
            return "–";
        }
        JsonObject basis = entry.basis().get().getAsJsonObject();
        List<String> parts = new ArrayList<>();
        if (basis.has("food")) {
            parts.add(basis.get("food").getAsString());
        }
        if (basis.has("sources") && basis.get("sources").isJsonArray()) {
            basis.getAsJsonArray("sources").forEach(source -> parts.add(source.getAsString()));
        }
        if (basis.has("processing")) {
            parts.add(basis.get("processing").getAsString());
        }
        return String.join("; ", parts).replace("|", "\\|");
    }

    /** The input files, sorted by mod id. */
    private static List<NutritionFile> inputs() {
        String directory = System.getProperty(INPUTS);
        if (directory == null) {
            throw new IllegalStateException("Nutrition data: the system property " + INPUTS + " is not set (the build sets it)");
        }
        Path root = Path.of(directory);
        if (!Files.isDirectory(root)) {
            throw new IllegalStateException("Nutrition data: input directory " + root + " does not exist");
        }
        TreeMap<String, NutritionFile> files = new TreeMap<>();
        try (Stream<Path> paths = Files.list(root)) {
            for (Path path : paths.filter(path -> path.getFileName().toString().endsWith(".json")).toList()) {
                String name = path.getFileName().toString();
                String mod = name.substring(0, name.length() - ".json".length());
                JsonElement json;
                try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    json = JsonParser.parseReader(reader);
                }
                NutritionFile file;
                try {
                    file = NutritionFile.parseInput(name, json);
                } catch (IllegalArgumentException exception) {
                    throw new IllegalStateException("Nutrition input " + name + ": " + exception.getMessage(), exception);
                }
                if (!file.mod().equals(mod)) {
                    throw new IllegalStateException("Nutrition input " + name + " is for mod " + file.mod() + "; name it " + file.mod() + ".json");
                }
                checkHandEntries(name, file);
                files.put(mod, file);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        return List.copyOf(files.values());
    }

    /** Hand-made entries name all six keys and carry a basis with food, sources and confirmation. */
    private static void checkHandEntries(String source, NutritionFile file) {
        for (NutritionFile.Entry entry : file.entries()) {
            String first = entry.items().get(0);
            if (entry.missing() != 0) {
                throw new IllegalStateException("Nutrition data " + source + ": " + first + " lacks " + keys(entry.missing()));
            }
            Optional<JsonElement> basis = entry.basis();
            if (basis.isEmpty() || !basis.get().isJsonObject()) {
                throw new IllegalStateException("Nutrition data " + source + ": " + first + " has no basis");
            }
            JsonObject object = basis.get().getAsJsonObject();
            if (!nonEmptyString(object, "food") || !nonEmptyString(object, "confirmed")) {
                throw new IllegalStateException("Nutrition data " + source + ": the basis of " + first + " needs food and confirmed");
            }
            if (!object.has("sources") || !object.get("sources").isJsonArray() || object.getAsJsonArray("sources").isEmpty()) {
                throw new IllegalStateException("Nutrition data " + source + ": the basis of " + first + " needs sources");
            }
        }
    }

    private static boolean nonEmptyString(JsonObject object, String key) {
        return object.has(key) && object.get(key).isJsonPrimitive() && !object.get(key).getAsString().isBlank();
    }

    /** Check of every written nutrition file. */
    private static void check(String id, String name, JsonElement json) {
        NutritionFile file;
        try {
            file = NutritionFile.parse(id, json);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Generated nutrition data " + id + ": " + exception.getMessage(), exception);
        }
        Set<String> items = new HashSet<>();
        for (NutritionFile.Entry entry : file.entries()) {
            if (entry.missing() != 0) {
                throw new IllegalStateException("Generated nutrition data " + id + ": " + entry.items().get(0) + " lacks " + keys(entry.missing()));
            }
            for (String item : entry.items()) {
                if (PlatformTags.item(item).isEmpty()) {
                    throw new IllegalStateException("Generated nutrition data " + id + ": unknown item " + item);
                }
                if (!items.add(item)) {
                    throw new IllegalStateException("Generated nutrition data " + id + ": " + item + " is in two entries");
                }
            }
        }
        if (!name.equals(PRODUCTS)) {
            checkHandEntries(id, file);
        }
    }

    private static String keys(int mask) {
        List<String> keys = new ArrayList<>();
        for (Component component : Component.all()) {
            if ((mask & component.bit()) != 0) {
                keys.add(component.key());
            }
        }
        return String.join(", ", keys);
    }
}
