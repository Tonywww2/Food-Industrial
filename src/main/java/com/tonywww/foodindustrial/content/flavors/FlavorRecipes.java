package com.tonywww.foodindustrial.content.flavors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.content.ContentRecipe;
import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.BOILING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GRINDING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.MIXING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The flavourings of batch 5 (I31, additives/18 and 19): every route row is a template plus its inputs, read
 * from {@code flavors.json} ({@code tools/flavors/export_flavors.py}); each template writes the core recipe of
 * 18 §1.4 (the step that changes the material, its reagents as inputs and what it recovers as byproducts), and
 * every flavour that carries a purity gets one optional refining recipe (distilled when liquid, recrystallized
 * when solid). Rows of several templates are one recipe on the device 19 §6 names (11 问题 40).
 * <p>
 * Game settings of the templates: plant raw materials by the tier of their real oil yield (18 §1.4 原料用量,
 * 11 问题 42): essential oils the tier, concretes and oleoresins half of it, tinctures and extracts 2, balsams 1;
 * synthesis inputs and products by the coefficients of the key reaction (items one each, fluids 1000 mB a unit);
 * the products of a synthesis come out at their source purity; recipes with the same inputs on the same device
 * are one recipe with all products (18 §1.1). Rows the exporter cannot read as tokens, and routes too wide for
 * their device, are written by hand ({@link FlavorManual}).
 */
public final class FlavorRecipes extends Batch {
    public static final String RESOURCE = "/foodindustrial/flavors.json";
    private static final int METAL_RUNS = 64;
    /** Solvents recovered by the extraction templates (three quarters back). */
    private static final Set<String> SOLVENTS = Set.of("ethanol", "n_hexane", "acetone", "petroleum_ether", "ethyl_acetate", "toluene",
        "methanol", "dichloromethane", "carbon_dioxide", "diethyl_ether", "vegetable_oil_extraction_solvent");
    /** Acid given back by an anhydride acylation (ST02). */
    private static final Map<String, String> ANHYDRIDE_ACIDS = Map.of("acetic_anhydride", "acetic_acid", "propionic_anhydride",
        "propionic_acid", "butyric_anhydride", "butyric_acid", "isobutyric_anhydride", "isobutyric_acid", "hexanoic_anhydride",
        "hexanoic_acid", "benzoic_anhydride", "benzoic_acid");
    /** Raw materials outside the tier table, by count (18 §1.4, 11 问题 44): the rum of the rum concrete, four of it. */
    private static final Map<String, Integer> FIXED_RAW = Map.of("foods/rum", 4);
    /** Dried powders (NT14) of fresh leaves and whole herbs: 4; the others 2 (18 §1.4). */
    private static final Set<String> LEAVES_AND_HERBS = Set.of("materials/damiana_leaf", "materials/dittany_of_crete",
        "materials/wormwood", "materials/lemon_balm");
    /** Largest amounts a reaction's coefficients may give before the recipe is written per unit of product. */
    private static final int MAX_FLUID_UNITS = 8;
    private static final int MAX_ITEMS = 16;
    /** Templates run under nitrogen. */
    private static final Set<String> INERT = Set.of("ST16", "ST28", "ST29", "ST43", "ST60");

    /** Devices by their port counts (machines/06), to check that a core recipe fits. */
    enum Device {
        M02(1, 1, 0, 1, 0), M03(2, 2, 2, 1, 0), M04(2, 2, 2, 1, 0), M05(2, 1, 1, 2, 1), M07(2, 3, 0, 2, 0), M08(1, 1, 1, 2, 0),
        M11(1, 2, 0, 0, 0), M14(3, 2, 6, 3, 1), M15(1, 2, 2, 2, 0), M18(2, 2, 2, 2, 0), M20(3, 3, 0, 1, 0), M21(6, 2, 3, 1, 0),
        M22(0, 1, 2, 6, 0), M23(2, 2, 5, 2, 1), M27(1, 1, 3, 3, 1), M29(2, 1, 3, 2, 2);

        final int itemIn;
        final int itemOut;
        final int fluidIn;
        final int fluidOut;
        final int catalysts;

        Device(int itemIn, int itemOut, int fluidIn, int fluidOut, int catalysts) {
            this.itemIn = itemIn;
            this.itemOut = itemOut;
            this.fluidIn = fluidIn;
            this.fluidOut = fluidOut;
            this.catalysts = catalysts;
        }
    }

    record Input(String ref, boolean fluid, boolean catalyst, String id, boolean bearer) {
        boolean raw() {
            return ref.startsWith("materials/") || ref.startsWith("foods/");
        }
    }

    /**
     * {@code coefficients}: amounts of the inputs by material id, {@code products}: amounts of what the reaction gives
     * by formula, both by the key reaction (empty when it names none).
     */
    record Variant(boolean alternatives, String label, List<String> templates, List<Input> inputs, String note, List<String> noteIds,
                   String device, String mode, Map<String, Integer> coefficients, Map<String, Integer> products) {
    }

    record Route(String code, String id, boolean fluid, boolean gas, boolean bearer, String category, List<Variant> variants) {
        String key() {
            return code != null ? code : id;
        }
    }

    /** One recipe being assembled. */
    static final class Step {
        final ProcessType type;
        final Device device;
        String mode;
        final List<String> methods = new ArrayList<>();
        String temp;
        String pressure;
        boolean inert;
        boolean sterile;
        int tier;
        final List<In> ins = new ArrayList<>();
        final List<Cat> cats = new ArrayList<>();
        final List<Out> outs = new ArrayList<>();
        String name;
        String where;
        /** Reaction coefficients of the inputs by material id, and the material id of each input. */
        Map<String, Integer> coefficients = Map.of();
        final Map<In, String> ids = new LinkedHashMap<>();
        final List<String> dropped = new ArrayList<>();
        final Set<String> productIds = new LinkedHashSet<>();
        /** What the key reaction gives, by formula (units), and the products of separate reactions merged here. */
        Map<String, Integer> made = Map.of();
        final List<Share> shares = new ArrayList<>();

        Step(ProcessType type, Device device) {
            this.type = type;
            this.device = device;
        }

        String key() {
            // amounts are no way to tell recipes apart (11 问题 41): same reactants in another ratio meet here
            return type.id() + "|" + device + "|" + mode + "|" + methods + "|" + temp + "|" + pressure + "|" + inert + "|"
                + ins.stream().map(in -> in.ref() + (in.fluid() ? "~" : "") + "@" + in.min()).toList() + "|" + cats;
        }

        boolean has(String id) {
            return outs.stream().anyMatch(out -> out.id.equals(id));
        }
    }

    record In(String ref, boolean fluid, int amount, String min) {
    }

    /** A product as its own reaction makes it: its amount and the amounts of the inputs (by ref). */
    record Share(String id, int amount, Map<String, Integer> ins) {
    }

    record Cat(String ref, int runs, String min) {
    }

    /** {@code src}: null no purity rule, negative the material's source value, else that value. */
    record Out(String id, boolean fluid, int amount, Double src) {
    }

    private final List<String> problems = new ArrayList<>();
    private final Map<String, Step> steps = new LinkedHashMap<>();
    private final Map<String, Route> products = new LinkedHashMap<>();
    private final Map<String, Integer> tiers = new LinkedHashMap<>();
    private final Map<String, MaterialDef> materials = new HashMap<>();

    private FlavorRecipes(RecipeGen.Output out) {
        super(out, "flavors");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new FlavorRecipes(out).generate());
    }

    public static JsonObject read() {
        try (InputStream in = FlavorRecipes.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE + "; run tools/flavors/export_flavors.py");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + RESOURCE, exception);
        }
    }

    static List<Route> routes(JsonObject root) {
        List<Route> result = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("routes")) {
            JsonObject json = element.getAsJsonObject();
            JsonObject product = json.getAsJsonObject("product");
            List<Variant> variants = new ArrayList<>();
            for (JsonElement v : json.getAsJsonArray("variants")) {
                JsonObject variant = v.getAsJsonObject();
                List<Input> inputs = new ArrayList<>();
                for (JsonElement i : variant.getAsJsonArray("inputs")) {
                    JsonObject input = i.getAsJsonObject();
                    String kind = input.get("kind").getAsString();
                    inputs.add(new Input(input.get("ref").getAsString(), kind.equals("fluid"), kind.equals("catalyst"),
                        string(input, "id"), input.has("bearer") && input.get("bearer").getAsBoolean()));
                }
                variants.add(new Variant(variant.has("alternatives") && variant.get("alternatives").getAsBoolean(), string(variant, "label"),
                    strings(variant.getAsJsonArray("templates")), inputs, string(variant, "note"),
                    variant.has("note_ids") ? strings(variant.getAsJsonArray("note_ids")) : List.of(), string(variant, "device"),
                    string(variant, "mode"), counts(variant, "coefficients"), counts(variant, "products")));
            }
            result.add(new Route(string(json, "code"), json.get("id").getAsString(), product.get("kind").getAsString().equals("fluid"),
                "gas".equals(string(product, "state")), product.get("bearer").getAsBoolean(), string(product, "category"), variants));
        }
        return result;
    }

    private static Map<String, Integer> counts(JsonObject json, String key) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (json.has(key)) {
            json.getAsJsonObject(key).entrySet().forEach(entry -> result.put(entry.getKey(), (int) Math.ceil(entry.getValue().getAsDouble())));
        }
        return result;
    }

    private static String string(JsonObject json, String key) {
        return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsString() : null;
    }

    private static List<String> strings(JsonArray array) {
        List<String> result = new ArrayList<>();
        array.forEach(element -> result.add(element.getAsString()));
        return result;
    }

    private void generate() {
        JsonObject root = read();
        root.getAsJsonObject("raw_tiers").entrySet().forEach(entry -> tiers.put(entry.getKey(), entry.getValue().getAsInt()));
        for (Route route : routes(root)) {
            products.putIfAbsent(route.id(), route);
            if (FlavorManual.HAND_WRITTEN.contains(route.key())) {
                continue;
            }
            for (Variant variant : route.variants()) {
                String suffix = route.variants().size() > 1 && variant.label() != null ? "_" + variant.label().toLowerCase(Locale.ROOT) : "";
                if (FlavorManual.HAND_WRITTEN.contains(route.key() + suffix)) {
                    continue;
                }
                if (variant.alternatives()) {
                    for (String template : variant.templates()) {
                        core(route, variant, template, null, suffix + "_" + template.toLowerCase(Locale.ROOT));
                    }
                } else if (variant.templates().size() > 1 && variant.device() != null) {
                    core(route, variant, null, Device.valueOf(variant.device()), suffix);
                } else {
                    core(route, variant, variant.templates().get(variant.templates().size() - 1), null, suffix);
                }
            }
        }
        steps.values().forEach(this::share);
        steps.values().forEach(this::check);
        steps.values().forEach(this::emit);
        products.values().forEach(this::refining);
        manualRefining();
        new FlavorManual(out).generate();
        if (!problems.isEmpty()) {
            throw new IllegalStateException("Flavour routes that do not fit the template devices (write them in FlavorManual):\n  "
                + String.join("\n  ", problems));
        }
    }

    // ---- core recipes (18 §1.4) ----

    /**
     * The core recipe of one variant: by {@code template}, or for a row of several templates on {@code merged}
     * (19 §6), its conditions and byproducts taken from all the templates.
     */
    private void core(Route route, Variant variant, String template, Device merged, String suffix) {
        String where = route.key() + suffix;
        List<String> templates = template != null ? List.of(template) : variant.templates();
        Step step;
        // raw material count: a fixed number, or OIL (the tier) or CONCRETE (half the tier) of 18 §1.4
        int raw = 1;
        int amount = route.fluid() ? B : 1;
        boolean synthesis = templates.stream().anyMatch(t -> t.startsWith("ST"));
        Double src = route.bearer() ? -1.0 : null;
        if (merged != null) {
            step = merged == Device.M23 ? stepM23(variant.mode()) : merged == Device.M18 ? extraction(true) : stepM14();
        } else {
            switch (template) {
                case "NT1", "NT12" -> {
                    step = new Step(DISTILLING, Device.M08);
                    step.mode = "steam_distillation";
                    step.methods.add("oil_separation");
                    raw = OIL;
                    amount = route.fluid() ? 100 : 1;
                }
                case "NT2" -> {
                    step = new Step(PRESSING, Device.M02);
                    raw = OIL;
                    amount = 100;
                }
                case "NT3" -> {
                    step = new Step(EXTRACTING, Device.M04);
                    raw = 2;
                }
                case "NT4", "NT6", "NT17", "ST21" -> {
                    // a resin named with the extraction: adsorbed in the stirred reactor (04 实现约定)
                    step = variant.inputs().stream().anyMatch(Input::catalyst) ? stepM14() : extraction(true);
                    raw = template.equals("NT4") || template.equals("NT6") ? CONCRETE : 4;
                    amount = route.fluid() ? 250 : 1;
                }
                case "NT5" -> {
                    // a row naming the flowers makes the concrete and the absolute in one go
                    step = extraction(true);
                    raw = CONCRETE;
                    amount = route.fluid() ? 250 : 1;
                }
                case "NT7" -> {
                    boolean solvent = variant.inputs().stream().anyMatch(i -> i.fluid() && !i.ref().equals("water"));
                    step = variant.inputs().stream().anyMatch(Input::catalyst) ? stepM14()
                        : solvent ? extraction(true) : new Step(EXTRACTING, Device.M04);
                    raw = 2;
                    amount = route.fluid() ? 500 : 1;
                }
                case "NT8" -> {
                    step = new Step(EXTRACTING, Device.M03);
                    step.temp = "heated";
                    raw = 1;
                    amount = route.fluid() ? 250 : 1;
                }
                case "NT9", "ST13", "ST51" -> {
                    if (variant.inputs().stream().anyMatch(i -> !i.fluid() && !i.catalyst())) {
                        step = new Step(DISTILLING, Device.M08);
                        step.mode = "steam_distillation";
                        step.methods.add("oil_separation");
                        raw = 8;
                        amount = route.fluid() ? 100 : 1;
                    } else if (!route.fluid() && template.equals("NT9")) {
                        step = new Step(CRYSTALLIZING, Device.M15);
                        step.temp = "cooling";
                    } else {
                        step = new Step(DISTILLING, template.equals("ST51") ? Device.M22 : Device.M08);
                        amount = route.fluid() ? 250 : 1;
                        if (template.equals("ST51")) {
                            step.methods.add("multi_fraction");
                        }
                    }
                }
                case "NT10", "ST18" -> {
                    step = new Step(FERMENTING, Device.M29);
                    step.sterile = true;
                    step.mode = "aerated";
                }
                case "NT11" -> {
                    step = new Step(KILN, Device.M07);
                    step.mode = "dry_distillation";
                    raw = 4;
                    amount = route.fluid() ? 250 : 1;
                }
                case "NT13" -> {
                    step = new Step(DISTILLING, Device.M08);
                    amount = route.fluid() ? 250 : 1;
                }
                case "NT14" -> {
                    step = new Step(GRINDING, Device.M11);
                    raw = variant.inputs().stream().anyMatch(i -> LEAVES_AND_HERBS.contains(i.ref())) ? 4 : 2;
                }
                case "NT18" -> {
                    step = new Step(BOILING, Device.M15);
                    step.mode = "concentration";
                    amount = 100;
                }
                case "NT15", "NT16" -> step = stepM14();
                case "ST05", "ST40", "ST41" -> step = stepM23("hydrogenation");
                case "ST19" -> step = stepM23("carbonylation");
                case "ST52", "ST59", "ST61" -> step = stepM23(null);
                case "ST14" -> step = variant.inputs().stream().anyMatch(i -> "hydrogen".equals(i.id())) ? stepM23("hydrogenation") : stepM14();
                case "ST33" -> step = gasPhase(variant) ? stepM27("gas_catalysis")
                    : stepM23(variant.inputs().stream().anyMatch(i -> "hydrogen".equals(i.id())) ? "hydrogenation" : null);
                case "ST04" -> step = gasPhase(variant) || (variant.inputs().size() == 1 && variant.inputs().get(0).fluid())
                    ? stepM27("gas_catalysis") : stepM14();
                // ST11 rows noted gas phase (2-picoline, thiophene, pyrazine, pyrrole) on M27, as 19 says: 2-picoline then
                // differs from the 3-picoline of 110 (11 问题 41, 2026-11-12)
                case "ST11" -> step = gasPhase(variant) ? stepM27("gas_catalysis") : stepM14();
                case "ST10", "ST30", "ST54" -> step = gasPhase(variant)
                    || variant.inputs().stream().anyMatch(i -> i.ref().equals("catalysts/alumina")) ? stepM27("gas_catalysis") : stepM14();
                case "ST27" -> {
                    boolean ring = variant.inputs().size() == 1 && !variant.inputs().get(0).fluid();
                    step = ring ? new Step(KILN, Device.M20) : stepM27("gas_catalysis");
                    if (ring) {
                        step.mode = "condensation";
                    }
                }
                case "ST34", "ST44", "ST48" -> step = stepM27("gas_catalysis");
                case "ST39" -> step = stepM27("cracking");
                case "ST38" -> {
                    // R09 alkali fusion, but castor oil is a fluid the rotary kiln cannot take: at high temperature in M23
                    step = new Step(REACTING, Device.M23);
                    step.temp = "high";
                }
                case "ST22" -> {
                    step = new Step(MIXING, Device.M21);
                    step.mode = "blending";
                }
                case "ST50" -> {
                    step = stepM14();
                    step.mode = "polymerization";
                    step.temp = "cooling";
                }
                case "ST42", "ST49" -> {
                    step = stepM14();
                    step.methods.add("ultraviolet");
                    step.temp = "cooling";
                }
                case "ST53" -> {
                    if (variant.inputs().stream().anyMatch(Input::raw)) {
                        step = new Step(DISTILLING, Device.M08);
                        step.mode = "steam_distillation";
                        step.methods.add("oil_separation");
                        raw = 8;
                        amount = 100;
                    } else {
                        step = stepM14();
                    }
                }
                default -> {
                    if (!template.startsWith("ST") || template.equals("ST26")) {
                        problems.add(where + ": template " + template + " has no core recipe");
                        return;
                    }
                    step = stepM14();
                }
            }
        }
        step.where = where;
        step.name = route.id() + suffix;
        // conditions named in the note tell routes of the same reactants apart (18 §1.5: deep hydrogenation hot)
        if (variant.note() != null && variant.note().contains("高温")) {
            step.temp = "high";
        }
        if (variant.note() != null && variant.note().contains("高压")) {
            step.pressure = "high";
        }
        if (templates.stream().anyMatch(INERT::contains) && step.device == Device.M14) {
            step.inert = true;
        }
        if ((templates.contains("ST32") || templates.contains("ST47") || templates.contains("ST54")) && step.device == Device.M14) {
            step.methods.add("corrosion_resistant");
        }
        if (templates.contains("ST12")) {
            src = route.bearer() ? 30.0 : null;
        }
        // inputs
        boolean hasCatalyst = false;
        List<Input> inputs = new ArrayList<>(FlavorManual.INPUTS.getOrDefault(route.key() + suffix, variant.inputs()));
        if (templates.contains("ST06") || templates.contains("ST14")) {
            // the acid that cyclizes or isomerizes, named in the note, tells routes of the same reactants apart (18 §1.1)
            for (String acid : List.of("sulfuric_acid", "phosphoric_acid")) {
                if (variant.noteIds().contains(acid) && inputs.stream().noneMatch(i -> acid.equals(i.id()))) {
                    inputs.add(new Input(acid, true, false, acid, true));
                }
            }
        }
        Map<String, Integer> units = synthesis ? amounts(variant, route, where, templates.contains("ST18")) : Map.of();
        if (synthesis && units.containsKey(PRODUCT)) {
            amount = units.get(PRODUCT) * (route.fluid() ? B : 1);
        }
        for (Input input : inputs) {
            if (input.catalyst()) {
                step.cats.add(new Cat(input.ref(), runs(input.ref(), step), step.device == Device.M29 && input.ref().startsWith("cultures/") ? PURE : null));
                hasCatalyst = true;
            } else if (input.fluid()) {
                if (step.type == KILN && input.ref().equals("water")) {
                    continue;
                }
                In in = new In(input.ref(), true, synthesis ? units.getOrDefault(input.id() != null ? input.id() : input.ref(), 1) * B : B,
                    step.type == MIXING && input.bearer() ? PURE : null);
                step.ins.add(in);
                step.ids.put(in, input.id());
            } else {
                if (step.device == Device.M08 && !input.raw() && inputs.stream().anyMatch(Input::raw)) {
                    // the pot still takes one item: a treatment after the distillation is a step of its own (FlavorManual)
                    problems.add(where + ": " + input.ref() + " is a second item for the pot still");
                    continue;
                }
                int count = input.raw() ? rawCount(raw, input.ref(), where) : synthesis ? units.getOrDefault(input.id(), 1) : 1;
                In in = new In(input.ref(), false, count, step.type == MIXING && input.bearer() ? PURE : null);
                step.ins.add(in);
                step.ids.put(in, input.id());
            }
        }
        step.coefficients = variant.coefficients();
        if (templates.contains("ST27")) {
            for (String id : variant.noteIds()) {
                if (id.equals("manganese_dioxide")) {
                    step.cats.add(new Cat("chemicals/manganese_dioxide", ENZYME_RUNS, null));
                    hasCatalyst = true;
                }
            }
        }
        if (!hasCatalyst) {
            String catalyst = defaultCatalyst(templates, step);
            if (catalyst != null) {
                step.cats.add(new Cat(catalyst, runs(catalyst, step), null));
            }
        }
        if (step.device == Device.M08 && step.mode != null && step.ins.stream().noneMatch(In::fluid)) {
            step.ins.add(new In("water", true, B, null));
        }
        if (step.type == FERMENTING && step.ins.stream().noneMatch(In::fluid)) {
            step.ins.add(new In("water", true, 2 * B, null));
        }
        // outputs: the product, then what the merged steps give back
        step.outs.add(new Out(route.id(), route.fluid(), amount, src));
        step.productIds.add(route.id());
        Map<String, Integer> own = new LinkedHashMap<>();
        step.ins.forEach(in -> own.put(in.ref(), in.amount()));
        step.shares.add(new Share(route.id(), amount, own));
        Map<String, Integer> made = new LinkedHashMap<>();
        units.forEach((key, count) -> {
            if (key.startsWith("=")) {
                made.put(key.substring(1), count);
            }
        });
        step.made = made;
        for (String extra : FlavorManual.COPRODUCTS.getOrDefault(route.key() + suffix, List.of())) {
            step.outs.add(new Out(extra, false, 1, -1.0));
            step.productIds.add(extra);
        }
        byproducts(step, templates, variant, synthesis ? units : Map.of());
        fit(step);
        put(step);
    }

    private static final int OIL = -1;
    private static final int CONCRETE = -2;
    /** Key of the product's amount in {@link #amounts}; what the reaction gives is under "=" and its formula. */
    private static final String PRODUCT = "";

    /** Raw material count of the rule {@code raw} (a count, {@link #OIL} or {@link #CONCRETE}) for {@code ref}. */
    private int rawCount(int raw, String ref, String where) {
        if (raw > 0) {
            return raw;
        }
        if (FIXED_RAW.containsKey(ref)) {
            return FIXED_RAW.get(ref);
        }
        Integer tier = tiers.get(ref);
        if (tier == null) {
            problems.add(where + ": " + ref + " has no tier in 18 §1.4");
            return 1;
        }
        return raw == OIL ? tier : Math.max(1, tier / 2);
    }

    /**
     * Amounts of a synthesis by its key reaction (11 问题 42): inputs by material id, the product under
     * {@link #PRODUCT} and byproducts by material id, in units. Fermentations (ST18), and reactions whose amounts
     * would not fit the tanks, are written per unit of product, rounded (11 问题 44).
     */
    private Map<String, Integer> amounts(Variant variant, Route route, String where, boolean fermentation) {
        Map<String, Integer> result = new LinkedHashMap<>(variant.coefficients());
        variant.products().forEach((made, count) -> result.put("=" + made, count));
        String formula = formula(route.id());
        Integer product = formula == null ? null : variant.products().get(formula);
        if (product != null) {
            result.put(PRODUCT, product);
        }
        boolean tooMuch = variant.coefficients().entrySet().stream().anyMatch(entry -> entry.getValue() > (fluid(entry.getKey()) ? MAX_FLUID_UNITS : MAX_ITEMS));
        // fermentation and biotransformation per unit of product, the nitrogen source by the product's nitrogen (11 问题 44)
        if ((tooMuch || fermentation) && product != null) {
            result.replaceAll((id, count) -> Math.max(1, Math.round((float) count / product)));
            com.tonywww.foodindustrial.FoodIndustrial.LOGGER.info("I31 flavour {}: amounts per unit of product {}", where, result);
        }
        return result;
    }

    @org.jetbrains.annotations.Nullable
    private String formula(String id) {
        MaterialDef def = materials().get(id);
        return def == null ? null : def.formula();
    }

    private boolean fluid(String id) {
        MaterialDef def = materials().get(id);
        return id.equals("water") || def != null && def.isFluid();
    }

    private Map<String, MaterialDef> materials() {
        if (materials.isEmpty()) {
            MaterialTable.load().forEach(def -> materials.put(def.id(), def));
        }
        return materials;
    }

    /**
     * Adds a core recipe. Recipes of the same reactants on the same device are one recipe with all the products
     * (联产, 18 §1.5); a ratio does not tell recipes apart (11 问题 41), so reactants in another ratio for another
     * product are listed as a problem unless 18 §1.5 names the products as coproducts of one step.
     */
    private void put(Step step) {
        Step same = steps.get(step.key());
        if (same == null) {
            steps.put(step.key(), step);
            return;
        }
        if (!same.coefficients.isEmpty() && !step.coefficients.isEmpty() && !same.coefficients.equals(step.coefficients)
            && !(FlavorManual.COPRODUCTS_IN_RATIO.containsAll(same.productIds) && FlavorManual.COPRODUCTS_IN_RATIO.containsAll(step.productIds))) {
            problems.add(step.where + ": the same reactants as " + same.where + " in another ratio (11 问题 41)");
            return;
        }
        Set<String> sameFormulas = formulas(same.productIds);
        Set<String> newFormulas = formulas(step.productIds);
        // isomers (one formula) come from separate reactions even when an equation names their formula
        boolean isomers = !java.util.Collections.disjoint(sameFormulas, newFormulas);
        if (!isomers && !newFormulas.isEmpty() && same.made.keySet().containsAll(newFormulas)) {
            // the first reaction already gives this product (one equation, two products): in its amount
            for (Out out : step.outs) {
                if (step.productIds.contains(out.id()) && !same.has(out.id())) {
                    same.outs.add(0, made(out, same.made));
                    same.productIds.add(out.id());
                }
            }
        } else if (!isomers && !sameFormulas.isEmpty() && step.made.keySet().containsAll(sameFormulas)) {
            // this reaction gives the products of the first too: its inputs and amounts for all of them
            same.ins.replaceAll(in -> step.ins.stream().filter(other -> other.ref().equals(in.ref())).findFirst().orElse(in));
            same.outs.replaceAll(out -> same.productIds.contains(out.id()) ? made(out, step.made) : out);
            for (Out out : step.outs) {
                if (step.productIds.contains(out.id()) && !same.has(out.id())) {
                    same.outs.add(0, made(out, step.made));
                    same.productIds.add(out.id());
                }
            }
            same.made = step.made;
            same.coefficients = step.coefficients;
        } else {
            // products of separate reactions of the same inputs: shared out (11 问题 44, see share)
            for (Out out : step.outs) {
                if (step.productIds.contains(out.id()) && !same.has(out.id())) {
                    same.outs.add(0, out);
                    same.productIds.add(out.id());
                }
            }
            same.shares.addAll(step.shares);
        }
        if (same.type == DISTILLING && same.outs.stream().filter(Out::fluid).count() > 1 && !same.methods.contains("multi_fraction")) {
            same.methods.add("multi_fraction");
        }
        same.where = same.where + "+" + step.where;
        fit(same);
    }

    private Set<String> formulas(Set<String> ids) {
        Set<String> result = new LinkedHashSet<>();
        for (String id : ids) {
            String formula = formula(id);
            if (formula == null) {
                return Set.of();
            }
            result.add(formula);
        }
        return result;
    }

    private Out made(Out out, Map<String, Integer> made) {
        String formula = formula(out.id());
        Integer count = formula == null ? null : made.get(formula);
        return count == null ? out : new Out(out.id(), out.fluid(), count * (out.fluid() ? B : 1), out.src());
    }

    /**
     * Products of separate reactions merged into one recipe of the same inputs share the inputs (11 问题 44): each
     * gets an equal part, at the yield of its own reaction (acetone 3 → isophorone 500 mB, of 3 each, and mesityl
     * oxide 750 mB, of 2 each). Cuts of one batch (distilling, extracting, crystallizing, dry distillation) keep
     * their amounts.
     */
    private void share(Step step) {
        if (step.shares.size() < 2 || step.type == DISTILLING || step.type == EXTRACTING || step.type == CRYSTALLIZING || step.type == KILN) {
            return;
        }
        int parts = step.shares.size();
        for (Share share : step.shares) {
            double factor = Double.NaN;
            for (In in : step.ins) {
                Integer own = share.ins().get(in.ref());
                if (own != null && own > 0) {
                    factor = (double) in.amount() / own;
                    break;
                }
            }
            if (Double.isNaN(factor)) {
                problems.add(step.where + ": " + share.id() + " shares no input with the merged recipe");
                continue;
            }
            double amount = share.amount() * factor / parts;
            for (int i = 0; i < step.outs.size(); i++) {
                Out out = step.outs.get(i);
                if (out.id().equals(share.id())) {
                    long rounded = Math.round(amount);
                    if (!out.fluid() && Math.abs(amount - rounded) > 1e-6 || rounded < 1) {
                        problems.add(step.where + ": " + share.id() + " shares to " + amount + " items");
                    }
                    step.outs.set(i, new Out(out.id(), out.fluid(), (int) Math.max(1, rounded), out.src()));
                }
            }
        }
        com.tonywww.foodindustrial.FoodIndustrial.LOGGER.info("I31 flavour {}: coproducts shared {}", step.where, step.outs);
    }

    /**
     * Makes a recipe fit its device: a merged recipe (联产) gives up its recovered byproducts, a recipe naming more
     * catalysts than slots keeps the metal one; what still does not fit is listed for hand-writing.
     */
    private void fit(Step step) {
        Device device = step.type == DISTILLING && step.methods.contains("multi_fraction") ? Device.M22 : step.device;
        while ((step.outs.stream().filter(Out::fluid).count() > device.fluidOut || step.outs.stream().filter(out -> !out.fluid()).count() > device.itemOut)
            && step.outs.size() > 1 && !step.productIds.contains(step.outs.get(step.outs.size() - 1).id())) {
            step.dropped.add(step.outs.remove(step.outs.size() - 1).id());
        }
        while (step.cats.size() > device.catalysts && !step.cats.isEmpty()) {
            Cat drop = step.cats.stream().filter(cat -> !cat.ref().startsWith("catalysts/") || cat.ref().contains("resin")).findFirst()
                .orElse(step.cats.get(step.cats.size() - 1));
            step.cats.remove(drop);
            step.dropped.add(drop.ref());
        }
        if (!step.dropped.isEmpty()) {
            com.tonywww.foodindustrial.FoodIndustrial.LOGGER.info("I31 flavour {} on {} leaves out {}", step.where, device, step.dropped);
        }
    }

    private static boolean gasPhase(Variant variant) {
        return variant.note() != null && (variant.note().contains("M27") || variant.note().contains("气相"));
    }

    private Step stepM14() {
        Step step = new Step(REACTING, Device.M14);
        step.temp = "heated";
        step.tier = 2;
        return step;
    }

    private Step stepM23(String mode) {
        Step step = new Step(REACTING, Device.M23);
        step.mode = mode;
        step.temp = "heated";
        step.pressure = "pressurized";
        return step;
    }

    private Step stepM27(String mode) {
        Step step = new Step(GAS_REACTING, Device.M27);
        step.mode = mode;
        return step;
    }

    private Step extraction(boolean solvent) {
        Step step = new Step(EXTRACTING, Device.M18);
        if (solvent) {
            step.mode = "solvent_extraction";
        }
        return step;
    }

    private static String defaultCatalyst(List<String> templates, Step step) {
        if (step.device == Device.M23 && "hydrogenation".equals(step.mode)) {
            if (templates.contains("ST40")) {
                return "catalysts/copper";
            }
            return templates.contains("ST41") ? "catalysts/palladium" : "catalysts/nickel";
        }
        if (step.device == Device.M23 && "carbonylation".equals(step.mode)) {
            return "catalysts/cobalt";
        }
        if (step.device == Device.M27 && "gas_catalysis".equals(step.mode)) {
            if (templates.contains("ST48")) {
                return "catalysts/palladium";
            }
            if (templates.contains("ST33")) {
                return "catalysts/nickel";
            }
            if (templates.contains("ST27")) {
                return "chemicals/manganese_dioxide";
            }
            return templates.contains("ST04") && !templates.contains("ST30") ? "catalysts/copper" : "catalysts/alumina";
        }
        if (step.device == Device.M14) {
            if (templates.contains("ST15") || templates.contains("ST50")) {
                return "catalysts/aluminium_chloride";
            }
            if (templates.contains("ST60")) {
                return "catalysts/metathesis";
            }
            if (templates.contains("ST31")) {
                return "chemicals/aluminium_isopropoxide";
            }
        }
        return null;
    }

    private static int runs(String ref, Step step) {
        if (ref.startsWith("cultures/")) {
            return CULTURE_RUNS;
        }
        if (ref.startsWith("enzymes/") || ref.startsWith("chemicals/") || ref.contains("resin")) {
            return ENZYME_RUNS;
        }
        return ref.contains("immobilized") ? IMMOBILIZED_RUNS : METAL_RUNS;
    }

    /** What the merged steps give back (18 §1.4 并入): recovered solvent, the acid of an anhydride, and so on. */
    private void byproducts(Step step, List<String> templates, Variant variant, Map<String, Integer> units) {
        Set<String> extra = new LinkedHashSet<>();
        int before = step.outs.size();
        if (step.type == EXTRACTING && step.mode != null) {
            step.ins.stream().filter(in -> in.fluid() && SOLVENTS.contains(in.ref())).findFirst()
                .ifPresent(in -> step.outs.add(new Out(in.ref(), true, 750, -1.0)));
        }
        if (templates.contains("ST02")) {
            for (Input input : variant.inputs()) {
                String acid = input.id() == null ? null : ANHYDRIDE_ACIDS.get(input.id());
                if (acid != null && extra.add(acid)) {
                    boolean fluid = !acid.equals("benzoic_acid");
                    step.outs.add(new Out(acid, fluid, fluid ? 500 : 1, -1.0));
                }
            }
        }
        if (templates.contains("ST29")) {
            step.outs.add(new Out("triphenylphosphine_oxide", false, 1, -1.0));
        }
        if (templates.contains("ST31")) {
            step.outs.add(new Out("acetone", true, 500, -1.0));
        }
        if (templates.contains("ST32")) {
            step.outs.add(new Out("hydrochloric_acid", true, 500, -1.0));
        }
        if (templates.contains("ST38")) {
            // the still residue is the disodium salt, acidified in a recipe of its own (FlavorManual, 11 问题 42)
            step.outs.add(new Out("disodium_sebacate", false, 1, -1.0));
        }
        if (templates.contains("NT15")) {
            step.outs.add(new Out("glycerol", true, 100, -1.0));
        }
        // what the key reaction gives back comes out in its amount (11 问题 42)
        for (int i = before; i < step.outs.size(); i++) {
            Out out = step.outs.get(i);
            String formula = formula(out.id());
            Integer count = formula == null ? null : units.get("=" + formula);
            if (count != null) {
                step.outs.set(i, new Out(out.id(), out.fluid(), count * (out.fluid() ? B : 1), out.src()));
            }
        }
    }

    /** Port counts of the device; routes that do not fit are listed for hand-writing. */
    private void check(Step step) {
        long items = step.ins.stream().filter(in -> !in.fluid()).count();
        long fluids = step.ins.stream().filter(In::fluid).count() + (step.inert ? 1 : 0);
        long itemsOut = step.outs.stream().filter(out -> !out.fluid()).count();
        long fluidsOut = step.outs.stream().filter(Out::fluid).count();
        Device device = step.type == DISTILLING && step.methods.contains("multi_fraction") ? Device.M22 : step.device;
        if (items > device.itemIn || fluids > device.fluidIn || itemsOut > device.itemOut || fluidsOut > device.fluidOut
            || step.cats.size() > device.catalysts) {
            problems.add(step.where + " on " + device + ": " + items + " items, " + fluids + " fluids in, " + itemsOut + " items, "
                + fluidsOut + " fluids out, " + step.cats.size() + " catalysts " + step.ins + " " + step.cats);
        }
    }

    private void emit(Step step) {
        ContentRecipe recipe = r(step.type);
        for (In in : step.ins) {
            if (in.fluid()) {
                recipe.fin(in.ref(), in.amount(), in.min());
            } else {
                recipe.in(in.ref(), in.amount(), in.min());
            }
        }
        for (Cat cat : step.cats) {
            recipe.cat(cat.ref(), cat.runs(), cat.min());
        }
        for (Out out : step.outs) {
            if (out.fluid()) {
                recipe.fout(out.id(), out.amount());
            } else {
                recipe.out(out.id(), out.amount());
            }
            if (out.src() != null) {
                if (out.src() < 0) {
                    recipe.src();
                } else {
                    recipe.src(out.src());
                }
            }
        }
        if (step.mode != null) {
            recipe.mode(step.mode);
        }
        if (!step.methods.isEmpty()) {
            recipe.method(step.methods.toArray(String[]::new));
        }
        if (step.temp != null) {
            recipe.temp(step.temp);
        }
        if (step.pressure != null) {
            recipe.pressure(step.pressure);
        }
        if (step.inert) {
            recipe.inert();
        }
        if (step.sterile) {
            recipe.sterile();
        }
        if (step.tier > 1) {
            recipe.tier(step.tier);
        }
        recipe.add(step.name);
    }

    // ---- optional refining (18 §1.4 可选精制) ----

    /** The flavours written by hand ({@link FlavorManual}, flavors.json "manual") get the same refining. */
    private void manualRefining() {
        Map<String, MaterialDef> table = new HashMap<>();
        MaterialTable.load().forEach(def -> table.put(def.id(), def));
        for (JsonElement element : read().getAsJsonArray("manual")) {
            String id = element.getAsJsonObject().get("id").getAsString();
            MaterialDef def = table.get(id);
            if (def == null || products.containsKey(id)) {
                continue;
            }
            String category = def.purity() == null || !def.purity().has("category") ? null
                : "K" + def.purity().get("category").getAsString().replaceAll(".*:k(\\d+)_.*", "$1");
            Route route = new Route(null, id, def.isFluid(), def.state() == MaterialDef.State.GAS,
                def.bearer(), category, List.of());
            products.put(id, route);
            refining(route);
        }
    }

    /**
     * One refining recipe per flavour that carries a purity: a liquid distilled (R10, the pot still takes the
     * main fraction, the column more), a solid recrystallized from ethanol (R06), a gas rectified on the column.
     */
    private void refining(Route route) {
        if (!route.bearer() || FlavorManual.NOT_REFINED.contains(route.id())) {
            return;
        }
        if (route.gas()) {
            r(DISTILLING).fin(route.id(), B).fout(route.id(), 900).method("multi_fraction").add(route.id() + "_refining");
        } else if (route.fluid()) {
            r(DISTILLING).fin(route.id(), B).fout(route.id(), 750).add(route.id() + "_refining");
        } else if ("K1".equals(route.category()) || "K2".equals(route.category())) {
            r(CRYSTALLIZING).in("chemicals/" + route.id(), 4).fin("water", B).out(route.id(), 3).add(route.id() + "_refining");
        } else {
            r(CRYSTALLIZING).in("chemicals/" + route.id(), 4).fin("ethanol", B).out(route.id(), 3).fout("ethanol", 800).src()
                .add(route.id() + "_refining");
        }
    }
}
