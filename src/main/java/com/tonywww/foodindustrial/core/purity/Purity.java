package com.tonywww.foodindustrial.core.purity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.purity.PlatformPurity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Reading and writing purity (the byte {@code b} of {@link PurityScale}) on items and fluids.
 * <p>
 * Materials with a purity category register here with their recipe tags and purity data. Purity
 * bearers — the items and fluids of graded materials, and of K0 materials with a source purity —
 * carry a stored purity. Everything else has an effective purity used by recipe checks
 * (additives/10 §1.6):
 * <ul>
 *     <li>a bearer without stored purity ({@code /give}, old saves, foreign loot): the material's
 *     external default, or its source purity for K0;</li>
 *     <li>items and fluids of vanilla and other mods: the external purity rules, else the external
 *     default of the material whose tag holds them (or of the material a recipe checks for);</li>
 *     <li>other items and fluids of this mod (crops, ores, seawater): no purity.</li>
 * </ul>
 * Materials of the master table without purity (enzymes, consumables, multi-component mixtures;
 * additives/10 §1.1) register as plain materials: foreign items in their tags have no purity, and
 * external rules that match them are logged and ignored.
 * {@link #NONE} stands for "no purity".
 */
public final class Purity {
    public static final int NONE = PurityData.NONE;

    /** A material known to the purity system, with the tag recipes use for it. */
    private record MaterialTag(String id, @Nullable String itemTag, @Nullable String fluidTag) {
    }

    private static final Map<String, MaterialTag> MATERIALS = new LinkedHashMap<>();
    private static final Map<String, MaterialTag> PLAIN = new LinkedHashMap<>();
    private static final List<Map.Entry<Supplier<? extends Item>, String>> ITEM_BEARERS = new ArrayList<>();
    private static final List<Map.Entry<Supplier<? extends Fluid>, String>> FLUID_BEARERS = new ArrayList<>();
    private static volatile Map<Item, String> itemBearers;
    private static volatile Map<Fluid, String> fluidBearers;
    /** Materials of foreign items and fluids by tag membership; replaced when tags or data change. */
    private static volatile Map<Object, Optional<String>> foreignMaterials = new ConcurrentHashMap<>();
    /** Plain materials of foreign items and fluids by tag membership. */
    private static volatile Map<Object, Optional<String>> foreignPlain = new ConcurrentHashMap<>();

    private Purity() {
    }

    /** Registers the purity texts, data, rules and caches. Call once during mod construction. */
    public static void bootstrap() {
        Lang.add(Tooltips.PURITY_KEY, "%s（%s%%）", "%s (%s%%)");
        Lang.add(Tooltips.CONTENT_KEY, "含量 %s%%", "Content %s%%");
        Lang.add(PurityGrade.POOR.translationKey(), "劣质", "Poor");
        Lang.add(PurityGrade.CRUDE.translationKey(), "粗制", "Crude");
        Lang.add(PurityGrade.REFINED.translationKey(), "精制", "Refined");
        Lang.add(PurityGrade.FOOD.translationKey(), "食品级", "Food Grade");
        Lang.add(PurityGrade.LAB.translationKey(), "实验室级", "Lab Grade");
        PurityData.bootstrap();
        PurityRules.bootstrap();
        BundledPurityRules.bootstrap();
        PlatformEvents.onTagsUpdated(Purity::invalidate);
        PurityData.onChanged(Purity::invalidate);
    }

    private static void invalidate() {
        foreignMaterials = new ConcurrentHashMap<>();
        foreignPlain = new ConcurrentHashMap<>();
    }

    // ---- registration (mod construction) ----

    /**
     * Registers a material with a purity category.
     *
     * @param id       material id, {@code foodindustrial:salt}
     * @param itemTag  item tag recipes use for it ({@code foodindustrial:chemicals/salt}), or {@code null}
     * @param fluidTag fluid tag recipes use for it, or {@code null}
     * @param data     purity data as in the material files: {@code category}, {@code source_purity}, {@code grades}
     */
    public static void registerMaterial(String id, @Nullable String itemTag, @Nullable String fluidTag, JsonObject data) {
        if (PLAIN.containsKey(id) || MATERIALS.putIfAbsent(id, new MaterialTag(id, itemTag, fluidTag)) != null) {
            throw new IllegalArgumentException("Duplicate purity material " + id);
        }
        PurityData.registerBuiltin(id, data);
    }

    /**
     * Registers a material without purity (additives/10 §1.1), with the tag recipes use for it.
     * Parameters as in {@link #registerMaterial}.
     */
    public static void registerPlainMaterial(String id, @Nullable String itemTag, @Nullable String fluidTag) {
        if (MATERIALS.containsKey(id) || PLAIN.putIfAbsent(id, new MaterialTag(id, itemTag, fluidTag)) != null) {
            throw new IllegalArgumentException("Duplicate material " + id);
        }
    }

    /** Whether {@code id} is a registered material without purity. */
    public static boolean isPlain(@Nullable String id) {
        return id != null && PLAIN.containsKey(id);
    }

    /** Marks an item as a purity bearer of {@code material}. Call during mod construction. */
    public static void registerItem(Supplier<? extends Item> item, String material) {
        ITEM_BEARERS.add(Map.entry(item, material));
        itemBearers = null;
    }

    /** Marks a (source) fluid as a purity bearer of {@code material}. Call during mod construction. */
    public static void registerFluid(Supplier<? extends Fluid> fluid, String material) {
        FLUID_BEARERS.add(Map.entry(fluid, material));
        fluidBearers = null;
    }

    private static Map<Item, String> itemBearers() {
        Map<Item, String> map = itemBearers;
        if (map == null) {
            map = new IdentityHashMap<>();
            for (Map.Entry<Supplier<? extends Item>, String> entry : ITEM_BEARERS) {
                map.put(entry.getKey().get(), entry.getValue());
            }
            itemBearers = map;
        }
        return map;
    }

    private static Map<Fluid, String> fluidBearers() {
        Map<Fluid, String> map = fluidBearers;
        if (map == null) {
            map = new IdentityHashMap<>();
            for (Map.Entry<Supplier<? extends Fluid>, String> entry : FLUID_BEARERS) {
                map.put(entry.getKey().get(), entry.getValue());
            }
            fluidBearers = map;
        }
        return map;
    }

    // ---- materials ----

    /**
     * This mod's item of {@code material}: the first registered bearer, or {@code null} (M21
     * normalizing turns foreign items of the material's tag into it, I15).
     */
    @Nullable
    public static Item itemOf(@Nullable String material) {
        if (material == null) {
            return null;
        }
        for (Map.Entry<Supplier<? extends Item>, String> entry : ITEM_BEARERS) {
            if (entry.getValue().equals(material)) {
                return entry.getKey().get();
            }
        }
        return null;
    }

    /** Whether {@code item} belongs to this mod. */
    public static boolean isOwn(Item item) {
        return isOwn(PlatformTags.itemId(item));
    }

    /** Whether stacks of {@code item} carry a stored purity. */
    public static boolean bears(Item item) {
        return itemBearers().containsKey(item);
    }

    /** Whether {@code fluid} carries a stored purity. */
    public static boolean bears(Fluid fluid) {
        return fluidBearers().containsKey(fluid);
    }

    /**
     * Material of an item: the registered material of a bearer, else the first material (in table
     * order) whose recipe tag holds the item; {@code null} when none does.
     */
    @Nullable
    public static String materialOf(Item item) {
        String registered = itemBearers().get(item);
        if (registered != null) {
            return registered;
        }
        Map<Object, Optional<String>> cache = foreignMaterials;
        return cache.computeIfAbsent(item, key -> {
            ItemStack probe = new ItemStack(item);
            for (MaterialTag material : MATERIALS.values()) {
                if (material.itemTag() != null && probe.is(PlatformTags.itemTag(material.itemTag()))) {
                    return Optional.of(material.id());
                }
            }
            return Optional.empty();
        }).orElse(null);
    }

    /** Material of a fluid, by the same rules as {@link #materialOf(Item)}. */
    @Nullable
    public static String materialOf(Fluid fluid) {
        String registered = fluidBearers().get(fluid);
        if (registered != null) {
            return registered;
        }
        Map<Object, Optional<String>> cache = foreignMaterials;
        return cache.computeIfAbsent(fluid, key -> {
            for (MaterialTag material : MATERIALS.values()) {
                if (material.fluidTag() != null && fluid.defaultFluidState().is(PlatformTags.fluidTag(material.fluidTag()))) {
                    return Optional.of(material.id());
                }
            }
            return Optional.empty();
        }).orElse(null);
    }

    /** Material whose recipe tag is {@code tagId}, or {@code null}. */
    @Nullable
    public static String materialOfTag(String tagId, boolean fluid) {
        return ofTag(MATERIALS, tagId, fluid);
    }

    /** Plain material (without purity) whose recipe tag is {@code tagId}, or {@code null}. */
    @Nullable
    public static String plainMaterialOfTag(String tagId, boolean fluid) {
        return ofTag(PLAIN, tagId, fluid);
    }

    @Nullable
    private static String ofTag(Map<String, MaterialTag> materials, String tagId, boolean fluid) {
        for (MaterialTag material : materials.values()) {
            if (tagId.equals(fluid ? material.fluidTag() : material.itemTag())) {
                return material.id();
            }
        }
        return null;
    }

    /**
     * Plain material whose recipe tag holds {@code item} (in table order), or {@code null}. Only
     * consulted when the item has no purity material.
     */
    @Nullable
    public static String plainMaterialOf(Item item) {
        return foreignPlain.computeIfAbsent(item, key -> {
            ItemStack probe = new ItemStack(item);
            for (MaterialTag material : PLAIN.values()) {
                if (material.itemTag() != null && probe.is(PlatformTags.itemTag(material.itemTag()))) {
                    return Optional.of(material.id());
                }
            }
            return Optional.empty();
        }).orElse(null);
    }

    /** Plain material of a fluid, as {@link #plainMaterialOf(Item)}. */
    @Nullable
    public static String plainMaterialOf(Fluid fluid) {
        return foreignPlain.computeIfAbsent(fluid, key -> {
            for (MaterialTag material : PLAIN.values()) {
                if (material.fluidTag() != null && fluid.defaultFluidState().is(PlatformTags.fluidTag(material.fluidTag()))) {
                    return Optional.of(material.id());
                }
            }
            return Optional.empty();
        }).orElse(null);
    }

    /**
     * Material a recipe ingredient refers to, read from its JSON: tag and item / fluid references in
     * any node's format ({@code {"tag": …}}, {@code {"item": …}}, {@code "#tag"}, {@code "id"}, lists).
     * {@code null} unless every reference resolves to the same material.
     */
    @Nullable
    public static String materialHint(@Nullable JsonElement json, boolean fluid) {
        Set<String> found = new LinkedHashSet<>();
        if (!collectHints(json, fluid, false, found) || found.size() != 1) {
            return null;
        }
        return found.iterator().next();
    }

    /**
     * Material without purity a recipe ingredient refers to, read as {@link #materialHint}: {@code null}
     * unless every reference resolves to the same plain material.
     */
    @Nullable
    public static String plainMaterialHint(@Nullable JsonElement json, boolean fluid) {
        Set<String> found = new LinkedHashSet<>();
        if (!collectHints(json, fluid, true, found) || found.size() != 1) {
            return null;
        }
        return found.iterator().next();
    }

    private static boolean collectHints(@Nullable JsonElement json, boolean fluid, boolean plain, Set<String> found) {
        if (json == null || json.isJsonNull()) {
            return false;
        }
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            for (JsonElement element : array) {
                if (!collectHints(element, fluid, plain, found)) {
                    return false;
                }
            }
            return !array.isEmpty();
        }
        String tag = null;
        String id = null;
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            String value = json.getAsString();
            if (value.startsWith("#")) {
                tag = value.substring(1);
            } else {
                id = value;
            }
        } else if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            if (object.has("tag")) {
                tag = object.get("tag").getAsString();
            } else if (object.has(fluid ? "fluid" : "item")) {
                id = object.get(fluid ? "fluid" : "item").getAsString();
            }
        }
        String material = null;
        if (tag != null) {
            material = plain ? plainMaterialOfTag(tag, fluid) : materialOfTag(tag, fluid);
        } else if (plain) {
            return false;
        } else if (id != null && PlatformTags.isValidId(id)) {
            material = fluid
                ? PlatformTags.fluid(id).map(f -> fluidBearers().get(f)).orElse(null)
                : PlatformTags.item(id).map(i -> itemBearers().get(i)).orElse(null);
        }
        if (material == null) {
            return false;
        }
        found.add(material);
        return true;
    }

    /** Value of a material's items and fluids without stored purity; {@link #NONE} when unknown. */
    public static int missingValue(@Nullable String material) {
        PurityData.Material data = PurityData.current().material(material);
        return data == null ? NONE : data.missing();
    }

    // ---- reading ----

    /** Stored purity of a bearer stack, if present. Non-bearers report empty. */
    public static OptionalInt stored(ItemStack stack) {
        if (stack.isEmpty() || !bears(stack.getItem())) {
            return OptionalInt.empty();
        }
        int b = PlatformPurity.get(stack);
        return b == PlatformPurity.NONE ? OptionalInt.empty() : OptionalInt.of(b);
    }

    /** Stored purity of a bearer fluid, if present. Non-bearers report empty. */
    public static OptionalInt stored(FluidAmount fluid) {
        if (fluid.isEmpty() || !fluid.hasPurity() || !bears(fluid.fluid())) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(fluid.purity());
    }

    /** Effective purity of a stack, or {@link #NONE}. */
    public static int get(ItemStack stack) {
        return get(stack, null);
    }

    /**
     * Effective purity of a stack, or {@link #NONE}.
     *
     * @param hint material a recipe checks for; gives the external default of foreign items whose
     *             own material is unknown
     */
    public static int get(ItemStack stack, @Nullable String hint) {
        if (stack.isEmpty()) {
            return NONE;
        }
        Item item = stack.getItem();
        String bearer = itemBearers().get(item);
        if (bearer != null) {
            OptionalInt stored = stored(stack);
            return stored.isPresent() ? stored.getAsInt() : missingValue(bearer);
        }
        if (isOwn(PlatformTags.itemId(item))) {
            return NONE;
        }
        String material = materialOf(item);
        if (material == null && plainMaterialOf(item) != null) {
            PurityRules.ignoredFor(item, plainMaterialOf(item));
            return NONE;
        }
        int ruled = PurityRules.value(item, material);
        return ruled != NONE ? ruled : missingValue(material != null ? material : hint);
    }

    /** Effective purity of a fluid, or {@link #NONE}. */
    public static int get(FluidAmount fluid) {
        return get(fluid, null);
    }

    /** Effective purity of a fluid, or {@link #NONE}; {@code hint} as in {@link #get(ItemStack, String)}. */
    public static int get(FluidAmount fluid, @Nullable String hint) {
        if (fluid.isEmpty()) {
            return NONE;
        }
        Fluid type = fluid.fluid();
        String bearer = fluidBearers().get(type);
        if (bearer != null) {
            OptionalInt stored = stored(fluid);
            return stored.isPresent() ? stored.getAsInt() : missingValue(bearer);
        }
        if (isOwn(PlatformTags.fluidId(type))) {
            return NONE;
        }
        String material = materialOf(type);
        if (material == null && plainMaterialOf(type) != null) {
            PurityRules.ignoredFor(type, plainMaterialOf(type));
            return NONE;
        }
        int ruled = PurityRules.value(type, material);
        return ruled != NONE ? ruled : missingValue(material != null ? material : hint);
    }

    /**
     * Two portions of the same fluid in one tank: purity is the mB-weighted average of their effective
     * purities, rounded down (additives/10 §2.2 rule 4). Fluids without purity stay without.
     */
    public static FluidAmount merge(FluidAmount current, FluidAmount added) {
        if (current.isEmpty()) {
            return added;
        }
        if (added.isEmpty()) {
            return current;
        }
        int total = current.amount() + added.amount();
        if (current.purity() == added.purity()) {
            return current.withAmount(total);
        }
        int a = get(current);
        int b = get(added);
        if (a == NONE || b == NONE) {
            return current.withAmount(total).withPurity(a != NONE ? a : b == NONE ? FluidAmount.NO_PURITY : b);
        }
        int merged = PurityScale.floorOf(PurityScale.weightedAverage(new int[]{a, b}, new long[]{current.amount(), added.amount()}));
        return FluidAmount.of(current.fluid(), total, merged);
    }

    /** Grade of the effective purity by the material's bounds; empty for K0 and items without purity. */
    public static Optional<PurityGrade> grade(ItemStack stack) {
        return stack.isEmpty() ? Optional.empty() : grade(materialOf(stack.getItem()), get(stack));
    }

    public static Optional<PurityGrade> grade(FluidAmount fluid) {
        return fluid.isEmpty() ? Optional.empty() : grade(materialOf(fluid.fluid()), get(fluid));
    }

    /** Grade of {@code b} by the bounds of {@code material}; empty for K0, unknown materials and {@link #NONE}. */
    public static Optional<PurityGrade> grade(@Nullable String material, int b) {
        PurityData.Material data = PurityData.current().material(material);
        return data == null || b == NONE ? Optional.empty() : data.gradeOf(b);
    }

    /** Whether the effective purity reaches {@code min}; stacks without purity never do. */
    public static boolean meets(ItemStack stack, int min, @Nullable String hint) {
        int b = get(stack, hint);
        return b != NONE && b >= min;
    }

    public static boolean meets(FluidAmount fluid, int min, @Nullable String hint) {
        int b = get(fluid, hint);
        return b != NONE && b >= min;
    }

    // ---- writing ----

    /** Writes purity {@code b} onto a bearer stack (in place). */
    public static void set(ItemStack stack, int b) {
        requireBearer(stack);
        PlatformPurity.set(stack, (byte) PurityScale.clamp(b));
    }

    /** Copy of a bearer stack with purity {@code b}. */
    public static ItemStack with(ItemStack stack, int b) {
        ItemStack copy = stack.copy();
        set(copy, b);
        return copy;
    }

    /**
     * Copy of a bearer stack at the lower bound of {@code grade} by its material's bounds (the external
     * default for poor).
     *
     * @throws IllegalArgumentException when the material lacks the grade
     */
    public static ItemStack with(ItemStack stack, PurityGrade grade) {
        requireBearer(stack);
        return with(stack, gradeValue(itemBearers().get(stack.getItem()), grade));
    }

    /** Bearer fluid with purity {@code b}. */
    public static FluidAmount with(FluidAmount fluid, int b) {
        if (fluid.isEmpty()) {
            return FluidAmount.EMPTY;
        }
        if (!bears(fluid.fluid())) {
            throw new IllegalArgumentException("Fluid carries no purity: " + PlatformTags.fluidId(fluid.fluid()));
        }
        return fluid.withPurity(PurityScale.clamp(b));
    }

    /** Bearer fluid at the lower bound of {@code grade}, as {@link #with(ItemStack, PurityGrade)}. */
    public static FluidAmount with(FluidAmount fluid, PurityGrade grade) {
        if (fluid.isEmpty()) {
            return FluidAmount.EMPTY;
        }
        return with(fluid, gradeValue(fluidBearers().get(fluid.fluid()), grade));
    }

    private static int gradeValue(@Nullable String material, PurityGrade grade) {
        PurityData.Material data = PurityData.current().material(material);
        int b = data == null ? NONE : data.gradeValue(grade);
        if (b == NONE) {
            throw new IllegalArgumentException("Material " + material + " has no grade " + grade.id());
        }
        return b;
    }

    /** Values listed for a bearer of {@code material} in the creative tab (additives/10 §5). */
    public static List<Integer> listedValues(String material) {
        PurityData.Material data = PurityData.current().material(material);
        return data == null ? List.of() : data.listedValues();
    }

    private static void requireBearer(ItemStack stack) {
        if (stack.isEmpty() || !bears(stack.getItem())) {
            throw new IllegalArgumentException("Item carries no purity: " + (stack.isEmpty() ? "empty" : PlatformTags.itemId(stack.getItem())));
        }
    }

    static boolean isOwn(String id) {
        return id.startsWith(FoodIndustrial.MOD_ID + ":");
    }
}
