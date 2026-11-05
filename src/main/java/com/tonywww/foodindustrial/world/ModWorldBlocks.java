package com.tonywww.foodindustrial.world;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.platform.PlatformBlocks;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.world.PlatformPlants;
import com.tonywww.foodindustrial.platform.world.PlatformWood;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The I07 world blocks (additives/08 §C): mineral ores and crusts, crops with their wild plants, the
 * gardenia bush, vanilla pods, sea plants, nitre soil and the rubber wood set, plus the fallback
 * sulfur and apatite ores and paddy rice.
 */
public final class ModWorldBlocks {
    /** How a block looks and drops; data generation keys off it. */
    public enum Kind {
        ORE, CRUST, SOIL, CROP, WILD_PLANT, BUSH, POD, SEA_PLANT,
        LOG, WOOD, PLANKS, STAIRS, SLAB, FENCE, FENCE_GATE, DOOR, TRAPDOOR, PRESSURE_PLATE, BUTTON, LEAVES, SAPLING
    }

    /**
     * A registered block. {@code item} is the block's own item (null for crops, wild plants and the
     * bush and pod, which are placed with their produce); {@code produce} is what they drop.
     */
    public record Entry(String id, Kind kind, Supplier<Block> block, @Nullable Supplier<? extends Item> item,
                        @Nullable Supplier<? extends Item> produce, String nameZh, String nameEn) {
    }

    /** A crop: produce item, crop block and wild plant. */
    public record Crop(String id, Supplier<Item> produce, Entry crop, Entry wild) {
    }

    private static final Map<String, Entry> BY_ID = new LinkedHashMap<>();
    private static final List<Crop> CROPS = new ArrayList<>();

    public static final Entry GYPSUM_ORE = block("gypsum_ore", Kind.ORE, "石膏矿石", "Gypsum Ore", Block::new, Blocks.COAL_ORE);
    public static final Entry ALUNITE_ORE = block("alunite_ore", Kind.ORE, "明矾石矿石", "Alunite Ore", Block::new, Blocks.COAL_ORE);
    public static final Entry MAGNESITE_ORE = block("magnesite_ore", Kind.ORE, "菱镁矿石", "Magnesite Ore", Block::new, Blocks.COAL_ORE);
    public static final Entry ROCK_SALT = block("rock_salt", Kind.ORE, "岩盐", "Rock Salt", Block::new, Blocks.STONE);
    public static final Entry APATITE_ORE = block("apatite_ore", Kind.ORE, "磷灰石矿石", "Apatite Ore", Block::new, Blocks.COAL_ORE);
    public static final Entry SULFUR_ORE = block("sulfur_ore", Kind.ORE, "硫矿石", "Sulfur Ore", PlatformBlocks.experienceDropping(0, 2), Blocks.COAL_ORE);
    public static final Entry TRONA_ORE = block("trona_ore", Kind.CRUST, "天然碱矿", "Trona Ore", Block::new, Blocks.SAND);
    public static final Entry NITRATINE = block("nitratine", Kind.CRUST, "钠硝石矿", "Nitratine Ore", Block::new, Blocks.SAND);
    public static final Entry NITRE_SOIL = block("nitre_soil", Kind.SOIL, "硝土", "Nitre Soil", Block::new, Blocks.DIRT);

    public static final Crop STEVIA = crop("stevia", "甜菊", "Stevia");
    public static final Crop MINT = crop("mint", "薄荷", "Mint");
    public static final Crop ROSEMARY = crop("rosemary", "迷迭香", "Rosemary");
    public static final Crop INDIGO_PLANT = crop("indigo_plant", "蓼蓝", "Indigo Plant");
    public static final Crop RICE = crop("rice", "稻谷", "Paddy Rice");

    private static final Supplier<Item>[] GARDENIA_FRUIT = holder();
    public static final Entry GARDENIA_BUSH = plant("gardenia_bush", Kind.BUSH, "栀子丛", "Gardenia Bush",
        PlatformPlants.fruitBush(() -> GARDENIA_FRUIT[0].get()), Blocks.SWEET_BERRY_BUSH, "gardenia_fruit", "栀子果", "Gardenia Fruit", GARDENIA_FRUIT);
    private static final Supplier<Item>[] VANILLA_POD = holder();
    public static final Entry VANILLA = plant("vanilla", Kind.POD, "香荚兰", "Vanilla",
        PlatformPlants.podOnJungleLog(), Blocks.COCOA, "vanilla_pod", "香荚兰豆", "Vanilla Pod", VANILLA_POD);

    public static final Entry GELIDIUM = block("gelidium", Kind.SEA_PLANT, "石花菜", "Gelidium", PlatformPlants.seaPlant(), Blocks.SEAGRASS);
    public static final Entry EUCHEUMA = block("eucheuma", Kind.SEA_PLANT, "麒麟菜", "Eucheuma", PlatformPlants.seaPlant(), Blocks.SEAGRASS);

    public static final Entry STRIPPED_RUBBER_LOG = block("stripped_rubber_log", Kind.LOG, "去皮橡胶木原木", "Stripped Rubber Log", RotatedPillarBlock::new, Blocks.STRIPPED_OAK_LOG);
    public static final Entry STRIPPED_RUBBER_WOOD = block("stripped_rubber_wood", Kind.WOOD, "去皮橡胶木", "Stripped Rubber Wood", RotatedPillarBlock::new, Blocks.STRIPPED_OAK_WOOD);
    public static final Entry RUBBER_LOG = block("rubber_log", Kind.LOG, "橡胶木原木", "Rubber Log", PlatformPlants.strippableLog(STRIPPED_RUBBER_LOG.block()), Blocks.OAK_LOG);
    public static final Entry RUBBER_WOOD = block("rubber_wood", Kind.WOOD, "橡胶木", "Rubber Wood", PlatformPlants.strippableLog(STRIPPED_RUBBER_WOOD.block()), Blocks.OAK_WOOD);
    public static final Entry RUBBER_PLANKS = block("rubber_planks", Kind.PLANKS, "橡胶木木板", "Rubber Planks", Block::new, Blocks.OAK_PLANKS);
    public static final Entry RUBBER_STAIRS = block("rubber_stairs", Kind.STAIRS, "橡胶木楼梯", "Rubber Stairs", PlatformWood.stairs(RUBBER_PLANKS.block()), Blocks.OAK_STAIRS);
    public static final Entry RUBBER_SLAB = block("rubber_slab", Kind.SLAB, "橡胶木台阶", "Rubber Slab", SlabBlock::new, Blocks.OAK_SLAB);
    public static final Entry RUBBER_FENCE = block("rubber_fence", Kind.FENCE, "橡胶木栅栏", "Rubber Fence", FenceBlock::new, Blocks.OAK_FENCE);
    public static final Entry RUBBER_FENCE_GATE = block("rubber_fence_gate", Kind.FENCE_GATE, "橡胶木栅栏门", "Rubber Fence Gate", PlatformWood.fenceGate(), Blocks.OAK_FENCE_GATE);
    public static final Entry RUBBER_DOOR = block("rubber_door", Kind.DOOR, "橡胶木门", "Rubber Door", PlatformWood.door(), Blocks.OAK_DOOR);
    public static final Entry RUBBER_TRAPDOOR = block("rubber_trapdoor", Kind.TRAPDOOR, "橡胶木活板门", "Rubber Trapdoor", PlatformWood.trapdoor(), Blocks.OAK_TRAPDOOR);
    public static final Entry RUBBER_PRESSURE_PLATE = block("rubber_pressure_plate", Kind.PRESSURE_PLATE, "橡胶木压力板", "Rubber Pressure Plate", PlatformWood.pressurePlate(), Blocks.OAK_PRESSURE_PLATE);
    public static final Entry RUBBER_BUTTON = block("rubber_button", Kind.BUTTON, "橡胶木按钮", "Rubber Button", PlatformWood.button(), Blocks.OAK_BUTTON);
    public static final Entry RUBBER_LEAVES = block("rubber_leaves", Kind.LEAVES, "橡胶树树叶", "Rubber Leaves", PlatformPlants.leaves(), Blocks.OAK_LEAVES);
    public static final Entry RUBBER_SAPLING = block("rubber_sapling", Kind.SAPLING, "橡胶树树苗", "Rubber Sapling", PlatformPlants.sapling(ModWorldgen.RUBBER_TREE), Blocks.OAK_SAPLING);

    private ModWorldBlocks() {
    }

    @SuppressWarnings("unchecked")
    private static Supplier<Item>[] holder() {
        return new Supplier[1];
    }

    private static Entry block(String id, Kind kind, String zh, String en, Function<BlockBehaviour.Properties, ? extends Block> factory, Block like) {
        Supplier<Block> block = PlatformRegistry.block(id, factory, PlatformBlocks.copy(like));
        Supplier<? extends Item> item = kind == Kind.DOOR
            ? PlatformRegistry.doubleHighBlockItem(id, block, new Item.Properties())
            : PlatformRegistry.blockItem(id, block, new Item.Properties());
        return put(new Entry(id, kind, block, item, null, zh, en));
    }

    /** A plant placed with a separate, separately named item (bush fruit, vanilla pod). */
    private static Entry plant(String id, Kind kind, String zh, String en, Function<BlockBehaviour.Properties, ? extends Block> factory, Block like,
                               String itemId, String itemZh, String itemEn, Supplier<Item>[] itemHolder) {
        Supplier<Block> block = PlatformRegistry.block(id, factory, PlatformBlocks.copy(like));
        Supplier<Item> item = PlatformRegistry.placingItem(itemId, block, new Item.Properties());
        itemHolder[0] = item;
        Lang.add(Lang.item(itemId), itemZh, itemEn);
        return put(new Entry(id, kind, block, null, item, zh, en));
    }

    private static Crop crop(String id, String zh, String en) {
        Supplier<Item>[] produce = holder();
        Supplier<Block> cropBlock = PlatformRegistry.block(id + "_crop", PlatformPlants.crop(() -> produce[0].get()), PlatformBlocks.copy(Blocks.WHEAT));
        produce[0] = PlatformRegistry.placingItem(id, cropBlock, new Item.Properties());
        Supplier<Block> wildBlock = PlatformRegistry.block("wild_" + id, PlatformPlants.wildPlant(), PlatformBlocks.copy(Blocks.DANDELION));
        Lang.add(Lang.item(id), zh, en);
        Entry crop = put(new Entry(id + "_crop", Kind.CROP, cropBlock, null, produce[0], zh, en));
        Entry wild = put(new Entry("wild_" + id, Kind.WILD_PLANT, wildBlock, null, produce[0], "野生" + zh, "Wild " + en));
        Crop result = new Crop(id, produce[0], crop, wild);
        CROPS.add(result);
        return result;
    }

    private static Entry put(Entry entry) {
        BY_ID.put(entry.id(), entry);
        return entry;
    }

    public static List<Entry> all() {
        return List.copyOf(BY_ID.values());
    }

    public static List<Crop> crops() {
        return Collections.unmodifiableList(CROPS);
    }

    /** Texts, creative tab entries, fire odds, fallback bark and data generation. */
    public static void bootstrap() {
        Fallbacks.bootstrap();
        for (Entry entry : BY_ID.values()) {
            Lang.add(Lang.block(entry.id()), entry.nameZh(), entry.nameEn());
        }
        ModCreativeTabs.add(ModCreativeTabs.Tab.RAW_MATERIALS, output -> {
            for (Entry entry : BY_ID.values()) {
                if (entry.item() != null) {
                    output.accept(new ItemStack(entry.item().get()));
                } else if (entry.kind() != Kind.WILD_PLANT && entry.produce() != null) {
                    output.accept(new ItemStack(entry.produce().get()));
                }
            }
        });
        PlatformEvents.onCommonSetup(ModWorldBlocks::setFlammability);
        PlatformEvents.onAxeStrip((level, pos, original) -> {
            if (Fallbacks.TREE_BARK.enabled()) {
                Block.popResource(level, pos, new ItemStack(ModMaterials.get("tree_bark").item().get()));
            }
        });
        ModWorldData.bootstrap();
        ModWorldgen.bootstrap();
    }

    /** Vanilla's oak odds ({@code FireBlock.bootStrap}). */
    private static void setFlammability() {
        for (Entry entry : BY_ID.values()) {
            switch (entry.kind()) {
                case PLANKS, STAIRS, SLAB, FENCE, FENCE_GATE -> PlatformWood.setFlammable(entry.block().get(), 5, 20);
                case LOG, WOOD -> PlatformWood.setFlammable(entry.block().get(), 5, 5);
                case LEAVES -> PlatformWood.setFlammable(entry.block().get(), 30, 60);
                default -> {
                }
            }
        }
    }
}
