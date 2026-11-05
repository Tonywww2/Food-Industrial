package com.tonywww.foodindustrial.platform;

import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.FoodIndustrial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
//? if >=1.21 {
/*import com.mojang.serialization.MapCodec;
*///?}

/**
 * World generation hooks. Registers the placement filter {@code foodindustrial:enabled}
 * ({@code {"type": "foodindustrial:enabled", "switch": "<name>"}}), which keeps a placed feature only
 * while the business switch {@code <name>} is on (for example fallback ores that only generate when
 * the providing mod is absent).
 */
public final class PlatformWorldgen {
    /** Type id of the switch filter, for generated placed features. */
    public static final String ENABLED_FILTER = FoodIndustrial.MOD_ID + ":enabled";

    private static volatile Predicate<String> switches = name -> true;
    private static Supplier<PlacementModifierType<EnabledFilter>> type;

    private PlatformWorldgen() {
    }

    /** Sets how switch names are evaluated; checked each time a filtered feature is placed. */
    public static void setSwitches(Predicate<String> predicate) {
        switches = predicate;
    }

    /**
     * Development check: places the placed feature {@code id} at {@code pos} as world generation
     * would (all placement modifiers apply, including the biome check). Returns whether anything was placed.
     */
    public static boolean placeFeature(ServerLevel level, String id, BlockPos pos) {
        //? if >=26.1 {
        /*Optional<PlacedFeature> feature = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).getOptional(PlatformIdentifier.parse(id));
        *///?} else {
        Optional<PlacedFeature> feature = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getOptional(PlatformIdentifier.parse(id));
        //?}
        return feature.orElseThrow(() -> new IllegalArgumentException("unknown placed feature " + id))
            .placeWithBiomeCheck(level, level.getChunkSource().getGenerator(), level.getRandom(), pos);
    }

    /**
     * Development check: generates the chunks within {@code radius} of chunk ({@code chunkX},
     * {@code chunkZ}) and counts the blocks of {@code namespace} in them, by block id.
     */
    public static Map<String, Integer> countBlocks(ServerLevel level, int chunkX, int chunkZ, int radius, String namespace) {
        Map<String, Integer> counts = new TreeMap<>();
        //? if >=26.1 {
        /*int minY = level.getMinY();
        *///?} else {
        int minY = level.getMinBuildHeight();
        //?}
        int maxY = minY + level.getHeight();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int cx = chunkX - radius; cx <= chunkX + radius; cx++) {
            for (int cz = chunkZ - radius; cz <= chunkZ + radius; cz++) {
                ChunkAccess chunk = level.getChunk(cx, cz);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            BlockState state = chunk.getBlockState(pos.set((cx << 4) + x, y, (cz << 4) + z));
                            String block = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
                            if (block.startsWith(namespace + ":")) {
                                counts.merge(block, 1, Integer::sum);
                            }
                        }
                    }
                }
            }
        }
        return counts;
    }

    static void bind() {
        type = PlatformRegistry.register(Registries.PLACEMENT_MODIFIER_TYPE, "enabled", () -> (PlacementModifierType<EnabledFilter>) () -> EnabledFilter.CODEC);
    }

    private static final class EnabledFilter extends PlacementFilter {
        //? if >=1.21 {
        /*private static final MapCodec<EnabledFilter> CODEC = Codec.STRING.fieldOf("switch").xmap(EnabledFilter::new, filter -> filter.name);
        *///?} else {
        private static final Codec<EnabledFilter> CODEC = Codec.STRING.fieldOf("switch").xmap(EnabledFilter::new, filter -> filter.name).codec();
        //?}

        private final String name;

        private EnabledFilter(String name) {
            this.name = name;
        }

        @Override
        protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
            return switches.test(name);
        }

        @Override
        public PlacementModifierType<?> type() {
            return type.get();
        }
    }
}
