package com.tonywww.foodindustrial.world.source;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.platform.PlatformBlocks;
import com.tonywww.foodindustrial.platform.PlatformCompost;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.world.PlatformPlants;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The I44 plants (additives/08 §C 来源规则; user 2026-11-19: trees grow as trees on vanilla trunks or their own log,
 * shrubs and woody vines as one-block bushes, crops planted with their own product): registered from
 * {@link SourceTable} at mod construction. Block ids: crops {@code <id>_crop} (and {@code wild_<id>} where the crop
 * also grows wild), wild herbs {@code wild_<id>}, trees {@code <id>_sapling}, {@code <id>_leaves} and {@code <id>_log},
 * bushes {@code <id>_bush}.
 */
public final class SourceBlocks {
    private static final String NS = FoodIndustrial.MOD_ID;

    public record CropBlocks(SourceTable.Crop crop, Supplier<Block> block, @Nullable Supplier<Block> wild) {
    }

    public record WildBlocks(SourceTable.Wild wild, Supplier<Block> block) {
    }

    /** A tree's sapling, leaves and own log (null when it uses a vanilla trunk), or a bush. */
    public record TreeBlocks(SourceTable.Tree tree, @Nullable Supplier<Block> sapling, @Nullable Supplier<Block> leaves,
                             @Nullable Supplier<Block> log, @Nullable Supplier<Block> bush) {
    }

    /** Biome groups on sand: their saplings and herbs also grow on sand (tools/world/sources.py SANDY). */
    static final java.util.Set<String> SANDY = java.util.Set.of("desert", "arid", "beach");
    private static final List<Supplier<? extends Item>> NEW_ITEMS = new ArrayList<>();
    private static final List<CropBlocks> CROPS = new ArrayList<>();
    private static final List<WildBlocks> WILD = new ArrayList<>();
    private static final List<TreeBlocks> TREES = new ArrayList<>();
    /** Plant item id → crop block (planted on farmland with a right click). */
    private static final Map<String, Supplier<Block>> PLANTING = new LinkedHashMap<>();
    private static final List<Supplier<? extends Item>> TAB = new ArrayList<>();
    private static boolean registered;
    /** The swiftlet nest block (燕巢). */
    static final String NEST_ID = "swiftlet_nest";
    static Supplier<Block> NEST;

    private SourceBlocks() {
    }

    /** Registers everything (once). Called during mod construction by {@code ModWorldBlocks}. */
    public static void bootstrap() {
        if (registered) {
            return;
        }
        registered = true;
        SourceTable.Data data = SourceTable.get();
        for (SourceTable.NewItem item : data.items().values()) {
            Supplier<Item> registered = PlatformRegistry.item(item.id(), new Item.Properties());
            NEW_ITEMS.add(registered);
            TAB.add(registered);
            Lang.add(Lang.item(item.id()), item.zh(), item.en());
            ModTags.extra("materials/" + item.tag(), NS + ":" + item.id());
        }
        data.extraTags().forEach((tag, items) -> items.forEach(item -> ModTags.extra("materials/" + tag, NS + ":" + item)));
        for (SourceTable.Linked linked : data.linked()) {
            String tag = "materials/" + linked.tag();
            linked.common().forEach(path -> ModTags.extraCommon(tag, path));
            linked.items().forEach(item -> ModTags.extra(tag, item));
            linked.own().forEach(own -> ModTags.extra(tag, "#" + NS + ":" + own));
            for (int i = 0; i < Math.max(linked.forge().size(), linked.c().size()); i++) {
                ModTags.extra(tag, i < linked.forge().size() ? linked.forge().get(i) : null, i < linked.c().size() ? linked.c().get(i) : null);
            }
        }
        for (SourceTable.Crop crop : data.crops()) {
            registerCrop(crop);
        }
        for (SourceTable.Wild wild : data.wild()) {
            registerWild(wild);
        }
        for (SourceTable.Tree tree : data.trees()) {
            registerTree(tree);
        }
        // 燕根 (swallowroot, 08 §C: 结构生成：崖壁燕巢方块): a swiftlet nest on mountain rock faces
        NEST = PlatformRegistry.block(NEST_ID, com.tonywww.foodindustrial.platform.world.WallNestBlock::new,
            PlatformBlocks.copy(Blocks.BEE_NEST).noOcclusion());
        TAB.add(PlatformRegistry.blockItem(NEST_ID, NEST, new Item.Properties()));
        Lang.add(Lang.block(NEST_ID), "燕巢", "Swiftlet Nest");
        com.tonywww.foodindustrial.gui.RecipeInfo.page("swallowroot", "山地崖壁上的燕巢，手持刀右键刮取或挖下燕巢得到",
            "Scrape swiftlet nests on mountain cliffs with a knife, or break them");
        ModCreativeTabs.add(ModCreativeTabs.Tab.PLANTS, output -> TAB.forEach(item -> output.accept(new ItemStack(item.get()))));
        ModCreativeTabs.icon(ModCreativeTabs.Tab.PLANTS, () -> new ItemStack(item("clove_sapling")));
        PlatformEvents.onRightClickBlock((level, player, hand, pos) -> {
            ItemStack held = player.getItemInHand(hand);
            Supplier<Block> crop = held.isEmpty() ? null : PLANTING.get(PlatformTags.itemId(held.getItem()));
            if (crop == null || !level.getBlockState(pos).is(Blocks.FARMLAND)) {
                return false;
            }
            BlockPos above = pos.above();
            BlockState state = crop.get().defaultBlockState();
            if (!level.isEmptyBlock(above) || !state.canSurvive(level, above)) {
                return false;
            }
            level.setBlock(above, state, Block.UPDATE_ALL);
            level.playSound(null, above, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            return true;
        });
        PlatformEvents.onAxeStrip((level, pos, original) -> {
            for (TreeBlocks tree : TREES) {
                if (tree.log() != null && original.is(tree.log().get()) && !tree.tree().strip().isEmpty()) {
                    SourceHarvest.roll(tree.tree().strip(), level.getRandom()).forEach(stack -> Block.popResource(level, pos, stack));
                }
            }
        });
        SourceVillages.bootstrap();
        SourceOrgans.bootstrap();
        SourceAnimals.bootstrap();
        SourceTexts.bootstrap();
        SourceData.bootstrap();
        SourceWorldgen.bootstrap();
    }

    private static void registerCrop(SourceTable.Crop crop) {
        String plant = crop.plant();
        Supplier<Block> block = PlatformRegistry.block(crop.id() + "_crop", PlatformPlants.crop(() -> item(plant)),
            PlatformBlocks.copy(Blocks.WHEAT));
        Lang.add(Lang.block(crop.id() + "_crop"), crop.zh(), crop.en());
        Supplier<Block> wild = null;
        if (crop.wild() != null) {
            wild = PlatformRegistry.block("wild_" + crop.id(), PlatformPlants.wildPlant(), PlatformBlocks.copy(Blocks.DANDELION));
            Lang.add(Lang.block("wild_" + crop.id()), "野生" + crop.zh(), "Wild " + crop.en());
        }
        PLANTING.put(NS + ":" + plant, block);
        CROPS.add(new CropBlocks(crop, block, wild));
    }

    private static void registerWild(SourceTable.Wild wild) {
        String id = "wild_" + wild.id();
        Supplier<Block>[] self = holder();
        Supplier<Block> block = PlatformRegistry.block(id,
            PlatformPlants.harvestPlant(new SourceHarvest(List.of(), wild.drops(), List.of(), 1, () -> new ItemStack(self[0].get())), true,
                wild.sand()), PlatformBlocks.noCollision(PlatformBlocks.copy(Blocks.SWEET_BERRY_BUSH)));
        self[0] = block;
        TAB.add(PlatformRegistry.blockItem(id, block, new Item.Properties()));
        Lang.add(Lang.block(id), wild.zh(), wild.en());
        WILD.add(new WildBlocks(wild, block));
    }

    private static void registerTree(SourceTable.Tree tree) {
        String id = tree.id();
        if (tree.bush()) {
            Supplier<Block>[] self = holder();
            Supplier<Block> bush = PlatformRegistry.block(id + "_bush",
                PlatformPlants.harvestPlant(new SourceHarvest(tree.flower(), tree.fruit(), tree.leaf(), 1, () -> new ItemStack(self[0].get())),
                    false, false), PlatformBlocks.copy(Blocks.SWEET_BERRY_BUSH));
            self[0] = bush;
            Supplier<? extends Item> item = PlatformRegistry.blockItem(id + "_bush", bush, new Item.Properties());
            TAB.add(item);
            PlatformCompost.register(item, 0.3F);
            Lang.add(Lang.block(id + "_bush"), tree.zh() + "丛", tree.en() + " Bush");
            TREES.add(new TreeBlocks(tree, null, null, null, bush));
            return;
        }
        boolean sandy = SourceTable.get().biomes().get(tree.biome()) != null && SANDY.contains(tree.biome());
        Supplier<Block> sapling = PlatformRegistry.block(id + "_sapling", PlatformPlants.sapling(treeFeature(tree), sandy),
            PlatformBlocks.copy(Blocks.OAK_SAPLING));
        Supplier<Block>[] self = holder();
        Supplier<Block> leaves = PlatformRegistry.block(id + "_leaves",
            PlatformPlants.fruitLeaves(new SourceHarvest(tree.flower(), tree.fruit(), tree.leaf(), 0, () -> new ItemStack(self[0].get()))),
            PlatformBlocks.copy(Blocks.OAK_LEAVES));
        self[0] = leaves;
        Supplier<Block> log = null;
        if (tree.ownLog()) {
            log = PlatformRegistry.block(id + "_log", tree.strip().isEmpty() ? PlatformPlants.log()
                : PlatformPlants.strippableLog(() -> PlatformTags.block("minecraft:stripped_" + strippedWood(tree) + "_log").orElseThrow()),
                PlatformBlocks.copy(Blocks.OAK_LOG));
            TAB.add(PlatformRegistry.blockItem(id + "_log", log, new Item.Properties()));
            Lang.add(Lang.block(id + "_log"), tree.zh() + "原木", tree.en() + " Log");
        }
        Supplier<? extends Item> saplingItem = PlatformRegistry.blockItem(id + "_sapling", sapling, new Item.Properties());
        Supplier<? extends Item> leavesItem = PlatformRegistry.blockItem(id + "_leaves", leaves, new Item.Properties());
        TAB.add(saplingItem);
        TAB.add(leavesItem);
        PlatformCompost.register(saplingItem, 0.3F);
        PlatformCompost.register(leavesItem, 0.3F);
        Lang.add(Lang.block(id + "_sapling"), tree.zh() + "树苗", tree.en() + " Sapling");
        Lang.add(Lang.block(id + "_leaves"), tree.zh() + "树叶", tree.en() + " Leaves");
        TREES.add(new TreeBlocks(tree, sapling, leaves, log, null));
    }

    /** Configured feature that the tree's sapling grows (and world generation places). */
    static String treeFeature(SourceTable.Tree tree) {
        return tree.id() + "_tree";
    }

    /** The vanilla stripped log an own log strips into, by climate. */
    static String strippedWood(SourceTable.Tree tree) {
        return switch (tree.biome()) {
            case "jungle" -> "jungle";
            case "taiga" -> "spruce";
            case "savanna", "desert", "arid" -> "acacia";
            default -> "oak";
        };
    }

    @SuppressWarnings("unchecked")
    private static Supplier<Block>[] holder() {
        return new Supplier[1];
    }

    /** The I07 gardenia bush's picking (08 §C: 栀子灌木加花期采花): flowers at age 2, fruit at age 3. */
    public static com.tonywww.foodindustrial.platform.world.PlantHarvest gardenia(Supplier<ItemStack> pickBlock) {
        SourceTable.Data data = SourceTable.get();
        return new SourceHarvest(data.gardeniaFlower(), data.gardeniaFruit(), List.of(), 1, pickBlock);
    }

    /** The item {@code foodindustrial:<id>} (materials, the new raw items and the I07 ones). */
    public static Item item(String id) {
        return PlatformTags.item(NS + ":" + id).orElseThrow(() -> new IllegalStateException("Unknown item " + NS + ":" + id));
    }

    public static List<CropBlocks> crops() {
        return List.copyOf(CROPS);
    }

    public static List<WildBlocks> wild() {
        return List.copyOf(WILD);
    }

    public static List<TreeBlocks> trees() {
        return List.copyOf(TREES);
    }

    /** The new raw items (no material entry). */
    public static List<Supplier<? extends Item>> newItems() {
        return List.copyOf(NEW_ITEMS);
    }
}
