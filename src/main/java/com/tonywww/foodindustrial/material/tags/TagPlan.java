package com.tonywww.foodindustrial.material.tags;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.material.MaterialDef;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Works out the mod's item and fluid tags (I06, additives/08 §标签体系) without touching the game:
 * the caller passes the registered ids, the node's tag conventions and registry lookups.
 * Every node writes both common-tag namespaces, {@code forge:} and {@code c:} (2026-11-12, user: mods of
 * either convention are found on any version).
 * <ul>
 *   <li>Own tags: every material joins {@code foodindustrial:<tag>} from the master table; solids under
 *       {@code chemicals/} import {@code #<common>:dusts/<id>} and {@code #<common>:gems/<id>}, fluids
 *       import {@code #<common>:<id>} (rule 3).</li>
 *   <li>Exports (rule 2, 2026-11-04): chemical solids join {@code <common>:dusts/<id>}, chemical fluids
 *       {@code <common>:<id>}; gases also join the gaseous tag and buckets the bucket tag.</li>
 *   <li>Every chemical item joins {@code foodindustrial:chemicals}, every chemical fluid the fluid tag of the
 *       same name (2026-11-11).</li>
 *   <li>Listed contents from 08 §A/§B and the catalyst tags of 09 chemicals ({@link TagImports}): the direct
 *       ids of the node's column, the tags of both columns, each common tag also in the other namespace.</li>
 * </ul>
 * Checks (rule 7): direct ids must exist on the node (vanilla and own ids fail the run, other mods'
 * ids are left out when absent), references to the mod's own tags must resolve, and empty tags are
 * not written.
 */
public final class TagPlan {
    private static final String OWN = FoodIndustrial.MOD_ID + ":";
    /**
     * Materials whose GTCEu name (additives/09 GT column) is not imported (I26, 2026-11-11): GTCEu's sugar
     * is vanilla sugar (sucrose here, not glucose), its dissolved calcium acetate is a fluid, its nickel
     * is the metal, and the tags of its hydrochloric acid and apatite already belong to this mod's
     * hydrochloric acid and raw apatite.
     */
    private static final Set<String> GTCEU_NOT_IMPORTED = Set.of("glucose", "calcium_acetate", "nickel_catalyst", "hydrogen_chloride", "fluorapatite");
    /** Every chemical, items and fluids, for searching (2026-11-11, user). */
    public static final String CHEMICALS = OWN + "chemicals";

    /** A tag value: an item or fluid id, or {@code #tag}; non-required values may be missing at load. */
    public record Value(String id, boolean required) {
    }

    /** A registered material and the ids of what it registered ({@code null} where not applicable). */
    public record Registered(MaterialDef def, @Nullable String item, @Nullable String source,
                             @Nullable String flowing, @Nullable String bucket) {
    }

    /** The common-tag namespaces written on every node. */
    public static final List<String> COMMON = List.of("forge", "c");

    /**
     * Tag conventions of the running node.
     *
     * @param common the node's own namespace ({@code forge} on 1.20.1, {@code c} from 1.21 on): which column of
     *               08 §A/§B gives the direct ids
     */
    public record Conventions(String common) {
    }

    /**
     * @param items        item tags by id
     * @param fluids       fluid tags by id
     * @param dropped      entries left out because their mod is absent on this node, as {@code tag <- id}
     * @param externalOnly own tags whose contents all come from other mods' tags on this node
     */
    public record Result(Map<String, List<Value>> items, Map<String, List<Value>> fluids,
                         List<String> dropped, List<String> externalOnly) {
    }

    private TagPlan() {
    }

    public static Result build(List<Registered> materials, List<TagImports.Entry> imports, Conventions conventions,
                               Predicate<String> itemExists, Predicate<String> fluidExists) {
        Map<String, Set<Value>> items = new LinkedHashMap<>();
        Map<String, Set<Value>> fluids = new LinkedHashMap<>();
        for (Registered material : materials) {
            MaterialDef def = material.def();
            String own = OWN + def.tag();
            boolean chemical = def.category().equals("chemical");
            if (material.item() != null) {
                add(items, own, material.item(), true);
                for (String namespace : COMMON) {
                    String common = namespace + ":";
                    if (def.tag().startsWith("chemicals/")) {
                        add(items, own, "#" + common + "dusts/" + def.id(), false);
                        add(items, own, "#" + common + "gems/" + def.id(), false);
                        gtceuName(def).ifPresent(name -> {
                            add(items, own, "#" + common + "dusts/" + name, false);
                            add(items, own, "#" + common + "gems/" + name, false);
                        });
                    }
                    if (chemical) {
                        add(items, common + "dusts/" + def.id(), material.item(), true);
                    }
                }
                if (chemical) {
                    add(items, CHEMICALS, material.item(), true);
                }
            } else {
                String source = require(material.source(), def);
                String flowing = require(material.flowing(), def);
                add(fluids, own, source, true);
                add(fluids, own, flowing, true);
                for (String namespace : COMMON) {
                    String common = namespace + ":";
                    add(fluids, own, "#" + common + def.id(), false);
                    gtceuName(def).ifPresent(name -> add(fluids, own, "#" + common + name, false));
                }
                if (chemical) {
                    add(fluids, CHEMICALS, source, true);
                    add(fluids, CHEMICALS, flowing, true);
                    for (String namespace : COMMON) {
                        String common = namespace + ":";
                        add(fluids, common + def.id(), source, true);
                        add(fluids, common + def.id(), flowing, true);
                        // the loaders' gas tag (Forge's forge:gaseous, NeoForge's c:gaseous) and bucket tags
                        if (def.state() == MaterialDef.State.GAS) {
                            add(fluids, common + "gaseous", source, true);
                            add(fluids, common + "gaseous", flowing, true);
                        }
                        if (material.bucket() != null) {
                            add(items, common + "buckets/" + def.id(), material.bucket(), true);
                        }
                    }
                }
            }
        }

        List<String> problems = new ArrayList<>();
        List<String> dropped = new ArrayList<>();
        Set<String> declared = new HashSet<>();
        for (TagImports.Entry entry : imports) {
            Map<String, Set<Value>> target = entry.fluid() ? fluids : items;
            Predicate<String> exists = entry.fluid() ? fluidExists : itemExists;
            String tag = OWN + entry.tag();
            List<String> listed = listed(entry, conventions.common());
            if (listed.isEmpty()) {
                // a crop or ore with no source yet (08 §C, I30): an empty tag that recipes can already name
                target.computeIfAbsent(tag, key -> new LinkedHashSet<>());
                declared.add(tag);
            }
            for (String id : listed) {
                if (id.startsWith("#")) {
                    add(target, tag, id, false);
                } else if (exists.test(id)) {
                    add(target, tag, id, id.startsWith(OWN));
                } else if (id.startsWith("minecraft:") || id.startsWith(OWN)) {
                    problems.add(tag + " lists " + id + ", which does not exist");
                } else {
                    dropped.add(tag + " <- " + id);
                }
            }
        }

        checkOwnReferences(items, problems);
        checkOwnReferences(fluids, problems);
        if (!problems.isEmpty()) {
            throw new IllegalStateException("Tag check failed:\n  " + String.join("\n  ", problems));
        }
        List<String> externalOnly = new ArrayList<>();
        collectExternalOnly(items, externalOnly);
        collectExternalOnly(fluids, externalOnly);
        return new Result(freeze(items, declared), freeze(fluids, declared), Collections.unmodifiableList(dropped),
                Collections.unmodifiableList(externalOnly));
    }

    /**
     * What a listed tag holds on a node: its column's entries (direct ids exist per node), then the tags of
     * the other column, then each common tag in the other namespace as well ({@code #forge:x} and {@code #c:x}).
     */
    static List<String> listed(TagImports.Entry entry, String common) {
        Set<String> result = new LinkedHashSet<>(entry.entries(common));
        for (String namespace : COMMON) {
            for (String id : entry.entries(namespace)) {
                if (id.startsWith("#")) {
                    result.add(id);
                }
            }
        }
        for (String id : List.copyOf(result)) {
            for (String namespace : COMMON) {
                if (id.startsWith("#" + namespace + ":")) {
                    String path = id.substring(namespace.length() + 2);
                    COMMON.forEach(other -> result.add("#" + other + ":" + path));
                }
            }
        }
        return List.copyOf(result);
    }

    /**
     * The GTCEu material name to import when it differs from this mod's id (I26): GTCEu puts its dusts
     * and fluids in the common tags of its own names, e.g. potassium chloride is its {@code rock_salt}.
     */
    private static Optional<String> gtceuName(MaterialDef def) {
        String name = def.gtceu();
        return name == null || name.equals(def.id()) || GTCEU_NOT_IMPORTED.contains(def.id()) ? Optional.empty() : Optional.of(name);
    }

    private static String require(@Nullable String id, MaterialDef def) {
        if (id == null) {
            throw new IllegalArgumentException("Fluid material " + def.id() + " without fluid ids");
        }
        return id;
    }

    private static void add(Map<String, Set<Value>> tags, String tag, String id, boolean required) {
        Set<Value> values = tags.computeIfAbsent(tag, key -> new LinkedHashSet<>());
        if (!values.contains(new Value(id, true)) && !values.contains(new Value(id, false))) {
            values.add(new Value(id, required));
        }
    }

    private static void checkOwnReferences(Map<String, Set<Value>> tags, List<String> problems) {
        for (Map.Entry<String, Set<Value>> tag : tags.entrySet()) {
            for (Value value : tag.getValue()) {
                if (value.id().startsWith("#" + OWN) && !tags.containsKey(value.id().substring(1))) {
                    problems.add(tag.getKey() + " refers to " + value.id() + ", which is not generated");
                }
            }
        }
    }

    private static void collectExternalOnly(Map<String, Set<Value>> tags, List<String> out) {
        for (Map.Entry<String, Set<Value>> tag : tags.entrySet()) {
            if (!tag.getKey().startsWith(OWN)) {
                continue;
            }
            boolean guaranteed = tag.getValue().stream()
                    .anyMatch(value -> !value.id().startsWith("#") || value.id().startsWith("#minecraft:") || value.id().startsWith("#" + OWN));
            if (!guaranteed) {
                out.add(tag.getKey());
            }
        }
    }

    private static Map<String, List<Value>> freeze(Map<String, Set<Value>> tags, Set<String> declared) {
        Map<String, List<Value>> result = new LinkedHashMap<>();
        tags.forEach((tag, values) -> {
            if (!values.isEmpty() || declared.contains(tag)) {
                result.put(tag, List.copyOf(values));
            }
        });
        return Collections.unmodifiableMap(result);
    }
}
