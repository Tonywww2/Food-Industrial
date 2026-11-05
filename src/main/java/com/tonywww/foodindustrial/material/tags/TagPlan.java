package com.tonywww.foodindustrial.material.tags;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.material.MaterialDef;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Works out the mod's item and fluid tags (I06, additives/08 §标签体系) without touching the game:
 * the caller passes the registered ids, the node's tag conventions and registry lookups.
 * <ul>
 *   <li>Own tags: every material joins {@code foodindustrial:<tag>} from the master table; solids under
 *       {@code chemicals/} import {@code #<common>:dusts/<id>} and {@code #<common>:gems/<id>}, fluids
 *       import {@code #<common>:<id>} (rule 3).</li>
 *   <li>Exports (rule 2, 2026-11-04): chemical solids join {@code <common>:dusts/<id>}, chemical fluids
 *       {@code <common>:<id>}; from 1.21 on gases also join the gaseous tag and buckets the bucket tag.</li>
 *   <li>Listed contents from 08 §A/§B and the catalyst tags of 09 chemicals ({@link TagImports}).</li>
 * </ul>
 * Checks (rule 7): direct ids must exist on the node (vanilla and own ids fail the run, other mods'
 * ids are left out when absent), references to the mod's own tags must resolve, and empty tags are
 * not written.
 */
public final class TagPlan {
    private static final String OWN = FoodIndustrial.MOD_ID + ":";

    /** A tag value: an item or fluid id, or {@code #tag}; non-required values may be missing at load. */
    public record Value(String id, boolean required) {
    }

    /** A registered material and the ids of what it registered ({@code null} where not applicable). */
    public record Registered(MaterialDef def, @Nullable String item, @Nullable String source,
                             @Nullable String flowing, @Nullable String bucket) {
    }

    /** Tag conventions of the running node. */
    public record Conventions(String common, Optional<String> gaseous, Function<String, Optional<String>> bucketTag) {
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
        String common = conventions.common() + ":";
        for (Registered material : materials) {
            MaterialDef def = material.def();
            String own = OWN + def.tag();
            boolean chemical = def.category().equals("chemical");
            if (material.item() != null) {
                add(items, own, material.item(), true);
                if (def.tag().startsWith("chemicals/")) {
                    add(items, own, "#" + common + "dusts/" + def.id(), false);
                    add(items, own, "#" + common + "gems/" + def.id(), false);
                }
                if (chemical) {
                    add(items, common + "dusts/" + def.id(), material.item(), true);
                }
            } else {
                String source = require(material.source(), def);
                String flowing = require(material.flowing(), def);
                add(fluids, own, source, true);
                add(fluids, own, flowing, true);
                add(fluids, own, "#" + common + def.id(), false);
                if (chemical) {
                    add(fluids, common + def.id(), source, true);
                    add(fluids, common + def.id(), flowing, true);
                    if (def.state() == MaterialDef.State.GAS) {
                        conventions.gaseous().ifPresent(tag -> {
                            add(fluids, tag, source, true);
                            add(fluids, tag, flowing, true);
                        });
                    }
                    if (material.bucket() != null) {
                        String bucket = material.bucket();
                        conventions.bucketTag().apply(def.id()).ifPresent(tag -> add(items, tag, bucket, true));
                    }
                }
            }
        }

        List<String> problems = new ArrayList<>();
        List<String> dropped = new ArrayList<>();
        for (TagImports.Entry entry : imports) {
            Map<String, Set<Value>> target = entry.fluid() ? fluids : items;
            Predicate<String> exists = entry.fluid() ? fluidExists : itemExists;
            String tag = OWN + entry.tag();
            for (String id : entry.entries(conventions.common())) {
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
        return new Result(freeze(items), freeze(fluids), Collections.unmodifiableList(dropped),
                Collections.unmodifiableList(externalOnly));
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

    private static Map<String, List<Value>> freeze(Map<String, Set<Value>> tags) {
        Map<String, List<Value>> result = new LinkedHashMap<>();
        tags.forEach((tag, values) -> {
            if (!values.isEmpty()) {
                result.put(tag, List.copyOf(values));
            }
        });
        return Collections.unmodifiableMap(result);
    }
}
