package com.tonywww.foodindustrial.material;

import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Registers every row of the material master table (I05): solids as {@link MaterialItem}s, liquids,
 * solutions and gases as fluids (liquids with block and bucket, gases without). Materials with a purity
 * category register their purity data, the others register as plain materials (additives/10 §1.1);
 * purity bearers register their items, fluids and buckets.
 * Buckets keep the purity, and buckets filled from the world get the material's value for missing
 * purity (external default, or source purity for K0; additives/10 §1.6).
 */
public final class ModMaterials {
    /** A registered material. Exactly one of {@code item} and {@code fluid} is set. */
    public record Entry(MaterialDef def, @Nullable Supplier<? extends Item> item, @Nullable PlatformFluids.Registered fluid) {
    }

    private static final Map<String, Entry> BY_ID = new LinkedHashMap<>();
    private static volatile Map<Fluid, MaterialDef> byFluid;

    static {
        for (MaterialDef def : MaterialTable.load()) {
            String material = materialId(def);
            String tag = FoodIndustrial.MOD_ID + ":" + def.tag();
            if (def.purity() != null) {
                Purity.registerMaterial(material, def.isFluid() ? null : tag, def.isFluid() ? tag : null, def.purity());
            } else {
                Purity.registerPlainMaterial(material, def.isFluid() ? null : tag, def.isFluid() ? tag : null);
            }
            if (def.isFluid()) {
                PlatformFluids.Spec spec = def.state() == MaterialDef.State.GAS ? PlatformFluids.Spec.gas(def.color()) : PlatformFluids.Spec.liquid(def.color());
                if (def.bearer()) {
                    spec = spec.withPurity(() -> Purity.missingValue(material));
                }
                PlatformFluids.Registered fluid = PlatformFluids.register(def.id(), spec);
                if (def.bearer()) {
                    Purity.registerFluid(fluid.source(), material);
                    if (fluid.bucket() != null) {
                        Purity.registerItem(fluid.bucket(), material);
                    }
                }
                BY_ID.put(def.id(), new Entry(def, null, fluid));
            } else {
                Supplier<MaterialItem> item = PlatformRegistry.item(def.id(), properties -> new MaterialItem(properties, def), new Item.Properties());
                if (def.bearer()) {
                    Purity.registerItem(item, material);
                }
                BY_ID.put(def.id(), new Entry(def, item, null));
            }
        }
    }

    private ModMaterials() {
    }

    static {
        long fluids = BY_ID.values().stream().filter(entry -> entry.fluid() != null).count();
        FoodIndustrial.LOGGER.info("Registered {} materials ({} items, {} fluids)", BY_ID.size(), BY_ID.size() - fluids, fluids);
    }

    public static Collection<Entry> all() {
        return Collections.unmodifiableCollection(BY_ID.values());
    }

    @Nullable
    public static Entry get(String id) {
        return BY_ID.get(id);
    }

    /** Material of a mod fluid (source or flowing), if any. */
    @Nullable
    public static MaterialDef byFluid(Fluid fluid) {
        Map<Fluid, MaterialDef> map = byFluid;
        if (map == null) {
            map = new IdentityHashMap<>();
            for (Entry entry : BY_ID.values()) {
                if (entry.fluid() != null) {
                    map.put(entry.fluid().source().get(), entry.def());
                    map.put(entry.fluid().flowing().get(), entry.def());
                }
            }
            byFluid = map;
        }
        return map.get(fluid);
    }

    /** Registers texts, creative tab entries, the bucket tooltip and data generation. */
    public static void bootstrap() {
        for (Entry entry : BY_ID.values()) {
            addLang(entry.def());
        }
        PlatformFluids.setBucketTooltip((stack, fluid, lines, shiftDown) -> {
            MaterialDef def = byFluid(fluid);
            if (def != null) {
                Tooltips.material(stack, def.formula(), Lang.description(def.id()), lines, shiftDown);
            }
        });
        ModCreativeTabs.add(ModCreativeTabs.Tab.CHEMICALS, output -> tabEntries(output, "chemical", "catalyst"));
        ModCreativeTabs.add(ModCreativeTabs.Tab.RAW_MATERIALS, output -> tabEntries(output, "raw"));
        ModCreativeTabs.add(ModCreativeTabs.Tab.CULTURES, output -> tabEntries(output, "culture", "enzyme", "consumable"));
        ModCreativeTabs.icon(ModCreativeTabs.Tab.CHEMICALS, () -> icon("salt"));
        ModCreativeTabs.icon(ModCreativeTabs.Tab.RAW_MATERIALS, () -> icon("tomato_pomace"));
        ModCreativeTabs.icon(ModCreativeTabs.Tab.CULTURES, () -> icon("acetobacter"));
        PlatformDatagen.register("materials", ModMaterials::generate);
        if (Platform.isPhysicalClient()) {
            MaterialLabels.register(BY_ID.values());
        }
    }

    private static void addLang(MaterialDef def) {
        if (def.commonZh() != null) {
            Lang.addCommonName(def.professionalZh(), def.commonZh());
        }
        Lang.add(Lang.description(def.id()), def.professionalZh(), def.professionalEn());
        if (!def.isFluid()) {
            Lang.add(Lang.item(def.id()), def.nameZh(), def.nameEn());
            return;
        }
        Lang.add(Lang.fluid(def.id()), def.nameZh(), def.nameEn());
        if (def.state() != MaterialDef.State.GAS) {
            Lang.add(Lang.block(def.id()), def.nameZh(), def.nameEn());
            Lang.add(Lang.item(def.id() + "_bucket"), def.nameZh() + "桶", def.nameEn() + " Bucket");
        }
    }

    /** Purity id of a material: {@code foodindustrial:<id>}. */
    public static String materialId(MaterialDef def) {
        return FoodIndustrial.MOD_ID + ":" + def.id();
    }

    private static ItemStack icon(String id) {
        Entry entry = BY_ID.get(id);
        if (entry == null || entry.item() == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(entry.item().get());
        return entry.def().bearer() ? Purity.with(stack, PurityGrade.FOOD) : stack;
    }

    /**
     * Stacks of the given categories in table order; purity bearers once per grade: at the external
     * default and at each grade's lower bound, K0 at the source purity (additives/10 §5).
     */
    private static void tabEntries(Consumer<ItemStack> output, String... categories) {
        for (Entry entry : BY_ID.values()) {
            MaterialDef def = entry.def();
            if (!matches(def.category(), categories)) {
                continue;
            }
            Supplier<? extends Item> item = entry.item() != null ? entry.item() : entry.fluid().bucket();
            if (item == null) {
                continue;
            }
            ItemStack stack = new ItemStack(item.get());
            List<Integer> values = def.bearer() ? Purity.listedValues(materialId(def)) : List.of();
            if (values.isEmpty()) {
                output.accept(stack);
            }
            for (int b : values) {
                output.accept(Purity.with(stack, b));
            }
        }
    }

    private static boolean matches(String category, String[] categories) {
        for (String candidate : categories) {
            if (candidate.equals(category)) {
                return true;
            }
        }
        return false;
    }

    /** Item models (and 26.1 item definitions), fluid block states and models. */
    private static void generate(PlatformDatagen.Sink sink) {
        String ns = FoodIndustrial.MOD_ID;
        for (Entry entry : BY_ID.values()) {
            String id = entry.def().id();
            if (entry.item() != null) {
                itemModel(sink, id);
            } else if (entry.fluid().block() != null) {
                PlatformDatagen.bucketModel(sink, ns, id + "_bucket", ns + ":" + id);
                JsonObject model = new JsonObject();
                JsonObject textures = new JsonObject();
                textures.addProperty("particle", ns + ":block/fluid_still");
                model.add("textures", textures);
                sink.json(PlatformDatagen.Kind.MODEL, ns, "block/" + id, model);
                JsonObject variant = new JsonObject();
                variant.addProperty("model", ns + ":block/" + id);
                JsonObject variants = new JsonObject();
                variants.add("", variant);
                JsonObject state = new JsonObject();
                state.add("variants", variants);
                sink.json(PlatformDatagen.Kind.BLOCKSTATE, ns, id, state);
            }
        }
    }

    private static void itemModel(PlatformDatagen.Sink sink, String id) {
        String ns = FoodIndustrial.MOD_ID;
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", ns + ":item/" + id);
        model.add("textures", textures);
        sink.json(PlatformDatagen.Kind.MODEL, ns, "item/" + id, model);
        JsonObject inner = new JsonObject();
        inner.addProperty("type", "minecraft:model");
        inner.addProperty("model", ns + ":item/" + id);
        JsonObject definition = new JsonObject();
        definition.add("model", inner);
        sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, ns, id, definition);
    }
}
