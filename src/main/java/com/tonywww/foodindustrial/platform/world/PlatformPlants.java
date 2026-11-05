package com.tonywww.foodindustrial.platform.world;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.function.Function;
import java.util.function.Supplier;
//? if forge {
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
//?}
//? if neoforge && <26.1 {
/*import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;
*///?}
//? if >=26.1 {
/*import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;
*///?}

/**
 * Plant, crop and tree blocks whose vanilla base classes or constructors differ between versions.
 * Each method returns a block factory for {@code PlatformRegistry.block}; the properties are usually
 * a copy of the matching vanilla block ({@code PlatformBlocks.copy}).
 */
public final class PlatformPlants {
    private PlatformPlants() {
    }

    /** A farmland crop (age 0–7) planted with its own produce, like carrots. */
    public static Function<BlockBehaviour.Properties, Block> crop(Supplier<? extends ItemLike> produce) {
        return properties -> new ModCropBlock(properties, produce);
    }

    /** A wild plant growing on grass or dirt; drops come from its loot table. */
    public static Function<BlockBehaviour.Properties, Block> wildPlant() {
        return WildPlantBlock::new;
    }

    /** A shrub like the sweet berry bush that gives {@code fruit} when picked and does not hurt. */
    public static Function<BlockBehaviour.Properties, Block> fruitBush(Supplier<? extends ItemLike> fruit) {
        return properties -> new FruitBushBlock(properties, fruit);
    }

    /** A pod growing on the side of jungle logs, like cocoa (vanilla {@code CocoaBlock}). */
    public static Function<BlockBehaviour.Properties, Block> podOnJungleLog() {
        return CocoaBlock::new;
    }

    /** An underwater plant like seagrass that bone meal spreads to nearby water. */
    public static Function<BlockBehaviour.Properties, Block> seaPlant() {
        return SeaPlantBlock::new;
    }

    /** A sapling growing the configured feature {@code foodindustrial:<treeFeature>}. */
    public static Function<BlockBehaviour.Properties, Block> sapling(String treeFeature) {
        ResourceKey<ConfiguredFeature<?, ?>> key = PlatformIdentifier.key(Registries.CONFIGURED_FEATURE, treeFeature);
        //? if forge {
        AbstractTreeGrower grower = new AbstractTreeGrower() {
            @Override
            protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean flowers) {
                return key;
            }
        };
        //?} else {
        /*TreeGrower grower = new TreeGrower(FoodIndustrial.MOD_ID + ":" + treeFeature, Optional.empty(), Optional.of(key), Optional.empty());
        *///?}
        return properties -> new SaplingBlock(grower, properties);
    }

    /** Leaves (untinted textures; 26.1 leaf particles take the biome foliage colour). */
    public static Function<BlockBehaviour.Properties, Block> leaves() {
        //? if >=26.1 {
        /*return properties -> new TintedParticleLeavesBlock(0.01F, properties);
        *///?} else {
        return LeavesBlock::new;
        //?}
    }

    /** A log or wood block that an axe strips into {@code stripped}. */
    public static Function<BlockBehaviour.Properties, Block> strippableLog(Supplier<? extends Block> stripped) {
        return properties -> new StrippableLogBlock(properties, stripped);
    }

    /** Whether {@code block} is one of the mod's strippable logs. */
    public static boolean isStrippableLog(Block block) {
        return block instanceof StrippableLogBlock;
    }
}
