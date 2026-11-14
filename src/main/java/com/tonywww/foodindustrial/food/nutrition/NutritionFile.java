package com.tonywww.foodindustrial.food.nutrition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * One nutrition data file (foods/03 §2): {@code data/<ns>/foodindustrial/nutrition/<name>.json}, or one data
 * generation input ({@code tools/nutrition/<mod>.json}, I34), which may also carry {@code only_on}.
 * <pre>
 * { "mod": "minecraft", "priority": 0, "entries": [ { "items": ["minecraft:apple"], "serving_g": 182,
 *   "per_serving": { "caffeine_mg": 0, … }, "impurity": 0, "energy_kcal": 95, "basis": { … } } ] }
 * </pre>
 * A missing component key counts as 0 and is recorded as missing; an unknown key, {@code serving_g} ≤ 0 or a
 * negative amount make the whole file malformed. {@code impurity} (units per serving, foods/02 §5.1) is optional
 * and defaults to 0; it is not a component and never counts as missing.
 *
 * @param source   file id ({@code namespace:path}), for messages
 * @param mod      mod the data is for; the file is skipped when it is not loaded
 * @param priority higher wins when several files name one item
 */
public record NutritionFile(String source, String mod, int priority, List<Entry> entries) {
    public NutritionFile {
        entries = List.copyOf(entries);
    }

    /**
     * One entry: the items sharing one serving's data.
     *
     * @param servingG   grams in one serving (one bite of a cake)
     * @param amounts    amount per serving of each {@link Component}, by ordinal
     * @param missing    mask of the components the entry did not name ({@link Component#bit()})
     * @param impurity   impurity units per serving (0 for none)
     * @param energyKcal energy per serving, for the report only
     * @param basis      where the numbers come from, as written; the game never reads it
     * @param onlyOn     generation inputs only: the Minecraft versions to generate the entry for ({@code null}:
     *                   all nodes)
     */
    public record Entry(List<String> items, double servingG, double[] amounts, int missing, double impurity,
                        Optional<Double> energyKcal, Optional<JsonElement> basis, Optional<List<String>> onlyOn) {
        public Entry {
            items = List.copyOf(items);
        }

        public double amount(Component component) {
            return amounts[component.ordinal()];
        }

        /** Whether the entry is generated for Minecraft {@code version} ({@code only_on}). */
        public boolean appliesTo(String version) {
            return onlyOn.map(versions -> versions.contains(version)).orElse(true);
        }
    }

    private record RawEntry(List<String> items, double servingG, Map<String, Double> perServing, double impurity,
                            Optional<Double> energyKcal, Optional<Dynamic<?>> basis, Optional<List<String>> onlyOn) {
    }

    private record RawFile(String mod, int priority, List<RawEntry> entries) {
    }

    private static Codec<RawEntry> entryCodec(boolean input) {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("items").forGetter(RawEntry::items),
            Codec.DOUBLE.fieldOf("serving_g").forGetter(RawEntry::servingG),
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("per_serving").forGetter(RawEntry::perServing),
            StrictCodecs.optional("impurity", Codec.DOUBLE, 0.0).forGetter(RawEntry::impurity),
            StrictCodecs.optional("energy_kcal", Codec.DOUBLE).forGetter(RawEntry::energyKcal),
            StrictCodecs.optional("basis", Codec.PASSTHROUGH).forGetter(RawEntry::basis),
            (input ? StrictCodecs.optional("only_on", Codec.STRING.listOf())
                : MapCodec.<Optional<List<String>>>unit(Optional.empty())).forGetter(RawEntry::onlyOn)
        ).apply(instance, RawEntry::new));
    }

    private static Codec<RawFile> fileCodec(boolean input) {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("mod").forGetter(RawFile::mod),
            StrictCodecs.optional("priority", Codec.INT, 0).forGetter(RawFile::priority),
            entryCodec(input).listOf().fieldOf("entries").forGetter(RawFile::entries)
        ).apply(instance, RawFile::new));
    }

    private static final Codec<RawFile> FILE = fileCodec(false);
    private static final Codec<RawFile> INPUT = fileCodec(true);

    /**
     * Parses a data pack file.
     *
     * @throws IllegalArgumentException when the file is malformed
     */
    public static NutritionFile parse(String source, JsonElement json) {
        return parse(source, json, FILE);
    }

    /**
     * Parses a data generation input, which may carry {@code only_on} on entries.
     *
     * @throws IllegalArgumentException when the file is malformed
     */
    public static NutritionFile parseInput(String source, JsonElement json) {
        return parse(source, json, INPUT);
    }

    private static NutritionFile parse(String source, JsonElement json, Codec<RawFile> codec) {
        var result = codec.parse(JsonOps.INSTANCE, json);
        Optional<RawFile> raw = result.result();
        if (raw.isEmpty()) {
            throw new IllegalArgumentException(result.error().map(error -> error.message()).orElse("malformed"));
        }
        List<Entry> entries = raw.get().entries().stream().map(NutritionFile::entry).toList();
        return new NutritionFile(source, raw.get().mod(), raw.get().priority(), entries);
    }

    private static Entry entry(RawEntry raw) {
        if (raw.items().isEmpty()) {
            throw new IllegalArgumentException("an entry names no items");
        }
        if (!(raw.servingG() > 0)) {
            throw new IllegalArgumentException("serving_g must be above 0 (" + raw.items().get(0) + ")");
        }
        double[] amounts = new double[Component.all().length];
        int named = 0;
        for (Map.Entry<String, Double> amount : raw.perServing().entrySet()) {
            Component component = Component.byKey(amount.getKey());
            if (component == null) {
                throw new IllegalArgumentException("unknown component \"" + amount.getKey() + "\" (" + raw.items().get(0) + ")");
            }
            if (!(amount.getValue() >= 0)) {
                throw new IllegalArgumentException(amount.getKey() + " must not be negative (" + raw.items().get(0) + ")");
            }
            amounts[component.ordinal()] = amount.getValue();
            named |= component.bit();
        }
        if (!(raw.impurity() >= 0)) {
            throw new IllegalArgumentException("impurity must not be negative (" + raw.items().get(0) + ")");
        }
        if (raw.energyKcal().isPresent() && !(raw.energyKcal().get() >= 0)) {
            throw new IllegalArgumentException("energy_kcal must not be negative (" + raw.items().get(0) + ")");
        }
        Optional<JsonElement> basis = raw.basis().map(dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue());
        return new Entry(raw.items(), raw.servingG(), amounts, Component.ALL & ~named, raw.impurity(), raw.energyKcal(), basis, raw.onlyOn());
    }

    /** The file as written to a data pack: generation-only fields left out, every component key written. */
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.addProperty("mod", mod);
        if (priority != 0) {
            root.addProperty("priority", priority);
        }
        JsonArray list = new JsonArray();
        for (Entry entry : entries) {
            JsonObject object = new JsonObject();
            JsonArray items = new JsonArray();
            entry.items().forEach(items::add);
            object.add("items", items);
            object.addProperty("serving_g", number(entry.servingG()));
            JsonObject perServing = new JsonObject();
            for (Component component : Component.all()) {
                perServing.addProperty(component.key(), number(entry.amount(component)));
            }
            object.add("per_serving", perServing);
            if (entry.impurity() > 0) {
                object.addProperty("impurity", number(entry.impurity()));
            }
            entry.energyKcal().ifPresent(energy -> object.addProperty("energy_kcal", number(energy)));
            entry.basis().ifPresent(basis -> object.add("basis", basis));
            list.add(object);
        }
        root.add("entries", list);
        return root;
    }

    /** Whole numbers without a decimal point, so generated files read like hand-written ones. */
    private static Number number(double value) {
        return value == Math.rint(value) && Math.abs(value) < 1e15 ? (Number) (long) value : (Number) value;
    }
}
