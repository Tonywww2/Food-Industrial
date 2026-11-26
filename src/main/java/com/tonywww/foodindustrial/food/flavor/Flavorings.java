package com.tonywww.foodindustrial.food.flavor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformItemData;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import com.tonywww.foodindustrial.registry.SharedTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The flavourings of the second batch (I48, foods/11): flavourings ({@code <type>_flavoring}, one item per flavour type)
 * with the dosage form as item data, and the flavour groups of the 1,885 flavouring materials (foods/12, read from
 * {@code /foodindustrial/flavor_notes.json}, {@code tools/foods/export_flavor_notes.py}) as the tags
 * {@code flavor_notes/<type>} (main notes) and {@code flavor_notes/all} (secondary notes), each an item and a fluid tag.
 * <p>
 * A material in several groups makes the flavouring of the type its device is set to (I48, 2026-11-19, user); the crafting
 * table and a device left on "automatic" take the first group the table lists for the first flavouring material among
 * the inputs ({@link #autoType}).
 */
public final class Flavorings {
    private static final String NS = FoodIndustrial.MOD_ID;
    public static final String RESOURCE = "/foodindustrial/flavor_notes.json";
    /** Item data key of the dosage form. */
    public static final String DATA = "flavoring";
    /** The secondary-note tags: every flavouring material. */
    public static final String ALL_NOTES = "flavor_notes/all";

    private static final Map<FlavorType, Supplier<Item>> ITEMS = new EnumMap<>(FlavorType.class);
    /** Material (plain id) → its flavour groups in table order; materials without a group map to an empty list. */
    private static final Map<String, List<FlavorType>> GROUPS = load();

    private Flavorings() {
    }

    /** Registers the items, names, tags and generated files. Called during mod construction, after the materials. */
    public static void bootstrap() {
        for (FlavorType type : FlavorType.values()) {
            ITEMS.put(type, PlatformRegistry.item(type.itemId(), new Item.Properties()));
            Lang.add(Lang.item(type.itemId()), type.zh() + "香精", type.en() + " Flavoring");
            Lang.add(type.key(), type.zh(), type.en());
            SharedTags.item(NS + ":flavorings", NS + ":" + type.itemId());
            SharedTags.item(NS + ":flavorings/" + type.id(), NS + ":" + type.itemId());
        }
        for (DosageForm form : DosageForm.values()) {
            Lang.add(form.key(), form.zh(), form.en());
        }
        Lang.add("tooltip.foodindustrial.flavoring.type", "风味类型：%s", "Flavor type: %s");
        Lang.add("tooltip.foodindustrial.flavoring.form", "剂型：%s", "Dosage form: %s");
        tags();
        ModCreativeTabs.add(ModCreativeTabs.Tab.FLAVORS, output -> {
            for (FlavorType type : FlavorType.values()) {
                for (DosageForm form : DosageForm.values()) {
                    output.accept(stack(type, form, 1));
                }
            }
        });
        PlatformDatagen.register("flavorings", Flavorings::generate);
        if (Platform.isPhysicalClient()) {
            com.tonywww.foodindustrial.food.flavor.client.FlavoringClient.bootstrap();
        }
    }

    /** {@code flavor_notes/<type>} and {@code flavor_notes/all}: items by their material tag, fluids by theirs. */
    private static void tags() {
        for (FlavorType type : FlavorType.values()) {
            ModTags.declare(type.notesTag());
        }
        ModTags.declare(ALL_NOTES);
        for (Map.Entry<String, List<FlavorType>> entry : GROUPS.entrySet()) {
            ModMaterials.Entry material = ModMaterials.get(entry.getKey());
            if (material == null) {
                throw new IllegalStateException("Flavouring material " + entry.getKey() + " is not registered");
            }
            String member = "#" + NS + ":" + material.def().tag();
            List<String> tags = new ArrayList<>();
            tags.add(ALL_NOTES);
            entry.getValue().forEach(type -> tags.add(type.notesTag()));
            for (String tag : tags) {
                if (material.fluid() != null) {
                    ModTags.extraFluid(tag, member);
                }
                if (material.item() != null) {
                    SharedTags.item(NS + ":" + tag, member);
                }
            }
        }
    }

    private static Map<String, List<FlavorType>> load() {
        try (InputStream in = Flavorings.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE + "; run tools/foods/export_flavor_notes.py");
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            Map<String, List<FlavorType>> groups = new LinkedHashMap<>();
            for (JsonElement element : root.getAsJsonArray("flavors")) {
                JsonObject flavor = element.getAsJsonObject();
                List<FlavorType> types = new ArrayList<>();
                for (JsonElement group : flavor.getAsJsonArray("groups")) {
                    FlavorType type = FlavorType.byId(group.getAsString());
                    if (type == null) {
                        throw new IllegalStateException("Unknown flavour type " + group + " in " + RESOURCE);
                    }
                    types.add(type);
                }
                groups.put(flavor.get("id").getAsString(), List.copyOf(types));
            }
            return Collections.unmodifiableMap(groups);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + RESOURCE, e);
        }
    }

    // ---- items ----

    public static Item item(FlavorType type) {
        return ITEMS.get(type).get();
    }

    /** The type of a flavouring item, or {@code null}. */
    @Nullable
    public static FlavorType type(Item item) {
        for (Map.Entry<FlavorType, Supplier<Item>> entry : ITEMS.entrySet()) {
            if (entry.getValue().get() == item) {
                return entry.getKey();
            }
        }
        return null;
    }

    /** The type of a flavouring by its id ({@code <type>_flavoring}, with or without the namespace), or {@code null}. */
    @Nullable
    public static FlavorType typeOfId(@Nullable String id) {
        if (id == null) {
            return null;
        }
        String plain = id.startsWith(NS + ":") ? id.substring(NS.length() + 1) : id;
        return plain.endsWith("_flavoring") ? FlavorType.byId(plain.substring(0, plain.length() - "_flavoring".length())) : null;
    }

    /** The type of the coarse emulsion fluid of a type ({@link FlavorType#premixId}), or {@code null}. */
    @Nullable
    public static FlavorType typeOfPremix(Fluid fluid) {
        String id = PlatformTags.fluidId(fluid);
        String plain = id.startsWith(NS + ":") ? id.substring(NS.length() + 1) : id;
        return plain.endsWith("_flavoring_premix") ? FlavorType.byId(plain.substring(0, plain.length() - "_flavoring_premix".length())) : null;
    }

    /** The dosage form of a flavouring stack, or {@code null}. */
    @Nullable
    public static DosageForm form(ItemStack stack) {
        if (stack.isEmpty() || type(stack.getItem()) == null) {
            return null;
        }
        return DosageForm.byId(PlatformItemData.getString(stack, DATA, "form"));
    }

    /** {@code count} flavourings of {@code type} in {@code form}. */
    public static ItemStack stack(FlavorType type, DosageForm form, int count) {
        return withForm(new ItemStack(item(type), count), form);
    }

    /** Writes the dosage form onto a flavouring stack (and picks its model); returns the stack. */
    public static ItemStack withForm(ItemStack stack, DosageForm form) {
        PlatformItemData.putString(stack, DATA, "form", form.id());
        PlatformItems.setModelVariant(stack, form.variant());
        return stack;
    }

    // ---- flavour groups ----

    /** The flavour groups of a flavouring material (plain id), in the table's order; empty when it is only a secondary note. */
    public static List<FlavorType> groups(@Nullable String material) {
        return material == null ? List.of() : GROUPS.getOrDefault(material, List.of());
    }

    /** Whether {@code material} (plain id) is one of the flavouring materials of tables B.2 and B.3. */
    public static boolean isNote(@Nullable String material) {
        return material != null && GROUPS.containsKey(material);
    }

    /**
     * The type made on "automatic" and on the crafting table: the first group of the first flavouring material with a
     * group among {@code materials} (in input order), or {@code null}.
     */
    @Nullable
    public static FlavorType autoType(List<String> materials) {
        for (String material : materials) {
            List<FlavorType> groups = groups(material);
            if (!groups.isEmpty()) {
                return groups.get(0);
            }
        }
        return null;
    }

    /**
     * Whether {@code material} (plain id) is a flavouring material of its own (category {@code flavor}, foods/11 §1): one
     * a finished product's recipe must not take, only a flavouring (I48). The additives that are also listed as
     * flavourings (acids, sugars, polyols) are chemicals and stay allowed.
     */
    public static boolean isFlavorMaterial(@Nullable String material) {
        if (material == null) {
            return false;
        }
        ModMaterials.Entry entry = ModMaterials.get(material);
        return entry != null && "flavor".equals(entry.def().category());
    }

    private static Map<String, String> materialOfTag;

    /** The flavouring material (plain id) whose tag is {@code tag} (a full id, with or without {@code #}), or {@code null}. */
    @Nullable
    public static String flavorMaterialOfTag(String tag) {
        if (materialOfTag == null) {
            Map<String, String> map = new java.util.HashMap<>();
            for (ModMaterials.Entry entry : ModMaterials.all()) {
                if ("flavor".equals(entry.def().category())) {
                    map.put(NS + ":" + entry.def().tag(), entry.def().id());
                }
            }
            materialOfTag = map;
        }
        return materialOfTag.get(tag.startsWith("#") ? tag.substring(1) : tag);
    }

    // ---- generated files ----

    private static void generate(PlatformDatagen.Sink sink) {
        for (FlavorType type : FlavorType.values()) {
            String id = type.itemId();
            JsonObject base = model(id);
            JsonArray overrides = new JsonArray();
            for (int variant = 1; variant < DosageForm.VARIANTS.length; variant++) {
                String name = id + "_" + DosageForm.VARIANTS[variant];
                sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + name, model(name));
                JsonObject predicate = new JsonObject();
                predicate.addProperty("custom_model_data", variant);
                JsonObject override = new JsonObject();
                override.add("predicate", predicate);
                override.addProperty("model", NS + ":item/" + name);
                overrides.add(override);
            }
            base.add("overrides", overrides);
            sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + id, base);
            // 26.1: the variant from the first float of the custom model data
            JsonObject dispatch = new JsonObject();
            dispatch.addProperty("type", "minecraft:range_dispatch");
            dispatch.addProperty("property", "minecraft:custom_model_data");
            dispatch.addProperty("index", 0);
            JsonArray entries = new JsonArray();
            for (int variant = 1; variant < DosageForm.VARIANTS.length; variant++) {
                JsonObject entry = new JsonObject();
                entry.addProperty("threshold", (float) variant);
                entry.add("model", plain(id + "_" + DosageForm.VARIANTS[variant]));
                entries.add(entry);
            }
            dispatch.add("entries", entries);
            dispatch.add("fallback", plain(id));
            JsonObject definition = new JsonObject();
            definition.add("model", dispatch);
            sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, id, definition);
        }
    }

    private static JsonObject model(String texture) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", NS + ":item/" + texture);
        model.add("textures", textures);
        return model;
    }

    private static JsonObject plain(String model) {
        JsonObject inner = new JsonObject();
        inner.addProperty("type", "minecraft:model");
        inner.addProperty("model", NS + ":item/" + model);
        return inner;
    }
}
