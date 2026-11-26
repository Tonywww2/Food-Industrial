package com.tonywww.foodindustrial.material.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.purity.BundledPurityRules;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Generates the mod's item and fluid tags and the common tags it exports to (I06). */
public final class ModTags {
    private ModTags() {
    }

    private static TagPlan.Result plan;
    /** Tags whose content other code adds (I44: the world code's raw items), merged with tag_imports.json: forge, c entries. */
    private static final java.util.Map<String, List<List<String>>> EXTRA = new java.util.LinkedHashMap<>();

    /**
     * Adds {@code entry} (an item id or {@code #tag}) to the mod's item tag {@code tag} (path, e.g.
     * {@code materials/barley}) on every node. Call during mod construction.
     */
    public static void extra(String tag, String entry) {
        List<List<String>> lists = EXTRA.computeIfAbsent(tag, key -> List.of(new ArrayList<>(), new ArrayList<>()));
        lists.get(0).add(entry);
        lists.get(1).add(entry);
    }

    /** Fluid tags whose content other code adds (I47: the bulk fluids of the products and their groups). */
    private static final java.util.Map<String, List<String>> EXTRA_FLUIDS = new java.util.LinkedHashMap<>();

    /** Adds {@code entry} (a fluid id or {@code #tag}) to the mod's fluid tag {@code tag} (path) on every node. */
    public static void extraFluid(String tag, String entry) {
        EXTRA_FLUIDS.computeIfAbsent(tag, key -> new ArrayList<>()).add(entry);
    }

    /** Declares the mod's item tag {@code tag} (path), empty unless something adds to it: a raw material with no source yet. */
    public static void declare(String tag) {
        EXTRA.computeIfAbsent(tag, key -> List.of(new ArrayList<>(), new ArrayList<>()));
    }

    /** Adds {@code forgeEntry} on 1.20.1 and {@code cEntry} from 1.21 on to {@code tag}. */
    public static void extra(String tag, String forgeEntry, String cEntry) {
        List<List<String>> lists = EXTRA.computeIfAbsent(tag, key -> List.of(new ArrayList<>(), new ArrayList<>()));
        if (forgeEntry != null) {
            lists.get(0).add(forgeEntry);
        }
        if (cEntry != null) {
            lists.get(1).add(cEntry);
        }
    }

    /** Adds the common tag {@code #forge:<path>} (1.20.1) or {@code #c:<path>} (1.21 on) to {@code tag}, optional. */
    public static void extraCommon(String tag, String path) {
        List<List<String>> lists = EXTRA.computeIfAbsent(tag, key -> List.of(new ArrayList<>(), new ArrayList<>()));
        lists.get(0).add("#forge:" + path);
        lists.get(1).add("#c:" + path);
    }

    public static void bootstrap() {
        PlatformDatagen.register("tags", ModTags::generate);
        BundledPurityRules.setEntryMaterials(ModTags::materialsOf);
    }

    /**
     * Data generation: materials whose tags list {@code entry} directly ({@code #tag} or id), or whose
     * tag {@code entry} is.
     */
    private static List<String> materialsOf(String entry, boolean fluid) {
        List<String> result = new ArrayList<>();
        if (entry.startsWith("#")) {
            String own = materialOfTag(entry.substring(1), fluid);
            if (own != null) {
                result.add(own);
            }
        }
        Map<String, List<TagPlan.Value>> tags = fluid ? plan().fluids() : plan().items();
        tags.forEach((tag, values) -> {
            String material = materialOfTag(tag, fluid);
            if (material != null && !result.contains(material) && values.stream().anyMatch(value -> value.id().equals(entry))) {
                result.add(material);
            }
        });
        return result;
    }

    /** Material (with or without purity) whose recipe tag is {@code tag}. */
    private static String materialOfTag(String tag, boolean fluid) {
        String material = Purity.materialOfTag(tag, fluid);
        return material != null ? material : Purity.plainMaterialOfTag(tag, fluid);
    }

    private static void generate(PlatformDatagen.Sink sink) {
        TagPlan.Result result = plan();
        write(sink, PlatformDatagen.Kind.ITEM_TAG, result.items());
        write(sink, PlatformDatagen.Kind.FLUID_TAG, result.fluids());
        FoodIndustrial.LOGGER.info("Tags: {} item tags, {} fluid tags", result.items().size(), result.fluids().size());
        for (String dropped : result.dropped()) {
            FoodIndustrial.LOGGER.info("Tags: left out (mod absent on this node): {}", dropped);
        }
        for (String tag : result.externalOnly()) {
            FoodIndustrial.LOGGER.info("Tags: only other mods' tags on this node: {}", tag);
        }
    }

    /** Planned item or fluid tags on the running node by id (data generation only). */
    public static Map<String, List<TagPlan.Value>> planned(boolean fluid) {
        return fluid ? plan().fluids() : plan().items();
    }

    /** Tag contents on the running node (data generation only). */
    private static TagPlan.Result plan() {
        if (plan != null) {
            return plan;
        }
        List<TagPlan.Registered> materials = new ArrayList<>();
        for (ModMaterials.Entry entry : ModMaterials.all()) {
            PlatformFluids.Registered fluid = entry.fluid();
            if (fluid == null) {
                materials.add(new TagPlan.Registered(entry.def(), PlatformTags.itemId(entry.item().get()), null, null, null));
            } else {
                materials.add(new TagPlan.Registered(entry.def(), null,
                        PlatformTags.fluidId(fluid.source().get()), PlatformTags.fluidId(fluid.flowing().get()),
                        fluid.bucket() == null ? null : PlatformTags.itemId(fluid.bucket().get())));
            }
        }
        TagPlan.Conventions conventions = new TagPlan.Conventions(PlatformDatagen.commonTagNamespace());
        List<TagImports.Entry> imports = new ArrayList<>(TagImports.load());
        EXTRA.forEach((tag, entries) -> imports.add(new TagImports.Entry(tag, false, entries.get(0), entries.get(1))));
        EXTRA_FLUIDS.forEach((tag, entries) -> imports.add(new TagImports.Entry(tag, true, entries, entries)));
        plan = TagPlan.build(materials, imports, conventions,
                id -> PlatformTags.item(id).isPresent(), id -> PlatformTags.fluid(id).isPresent());
        return plan;
    }

    private static void write(PlatformDatagen.Sink sink, PlatformDatagen.Kind kind, Map<String, List<TagPlan.Value>> tags) {
        tags.forEach((tag, values) -> {
            int colon = tag.indexOf(':');
            JsonArray array = new JsonArray();
            for (TagPlan.Value value : values) {
                if (value.required()) {
                    array.add(value.id());
                } else {
                    JsonObject optional = new JsonObject();
                    optional.addProperty("id", value.id());
                    optional.addProperty("required", false);
                    array.add(optional);
                }
            }
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            json.add("values", array);
            sink.json(kind, tag.substring(0, colon), tag.substring(colon + 1), json);
        });
    }
}
