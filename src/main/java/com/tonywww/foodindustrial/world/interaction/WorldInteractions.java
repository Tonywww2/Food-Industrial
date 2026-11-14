package com.tonywww.foodindustrial.world.interaction;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.platform.PlatformItem;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.PlatformSavedData;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import com.tonywww.foodindustrial.platform.world.PlatformFluidPickup;
import com.tonywww.foodindustrial.platform.world.PlatformRandomTicks;
import com.tonywww.foodindustrial.recipe.Environment;
import com.tonywww.foodindustrial.recipe.OutcomeResolver;
import com.tonywww.foodindustrial.recipe.PurityRule;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * World interactions W01 and W02 (machines/01 §3.2, additives/07 H11, H15, 2026-11-10): block
 * transformation driven by random ticks and nearby entities, and harvesting with a tool. Also the
 * flint knife and the liquid bottle.
 */
public final class WorldInteractions {
    /** Item tag of knives (H15): the common knife tag and the flint knife. */
    public static final String KNIVES = "foodindustrial:knives";
    /** Ticks between checks of each driving entity (W01). */
    public static final int ENTITY_INTERVAL = 1200;
    /** Horizontal reach of a driving entity's check (W01). */
    public static final int ENTITY_RADIUS = 4;
    /** Ticks between passes over the entities of a level; each entity is due every {@link #ENTITY_INTERVAL}. */
    private static final int ENTITY_PASS = 100;

    public static final PlatformRecipes.Kind<BlockTransformRecipe> BLOCK_TRANSFORM = PlatformRecipes.register("block_transform", BlockTransformRecipe.CODEC);
    public static final PlatformRecipes.Kind<HarvestRecipe> HARVESTING = PlatformRecipes.register("harvesting", HarvestRecipe.CODEC);

    public static final Supplier<PlatformItem> FLINT_KNIFE = PlatformRegistry.item("flint_knife", props -> new PlatformItem(props) {
        @Override
        protected void appendTooltip(ItemStack stack, List<net.minecraft.network.chat.Component> lines, boolean shiftDown) {
            com.tonywww.foodindustrial.core.text.Tooltips.machine(Lang.description("flint_knife"), lines, shiftDown);
        }
    }, new Item.Properties().durability(64));
    public static final Supplier<FluidBottleItem> FLUID_BOTTLE = PlatformRegistry.item(FluidBottleItem.ID, FluidBottleItem::new,
        new Item.Properties().stacksTo(1));

    /** Loaded recipes; rebuilt on the first level tick after a reload. */
    private static volatile Loaded loaded;

    private record Loaded(List<BlockTransformRecipe> transforms, List<Ticked> ticked, List<BlockTransformRecipe> driven, List<HarvestRecipe> harvests) {
    }

    /**
     * A transform driven by random ticks with its block or tag looked up once per reload, so that a
     * random tick matches without allocating (machines/02 §6.3 rule 10; I27, 2026-11-11).
     */
    private record Ticked(BlockTransformRecipe recipe, @Nullable TagKey<Block> tag, @Nullable Block block) {
        static Ticked of(BlockTransformRecipe recipe) {
            String id = recipe.block();
            return id.startsWith("#") ? new Ticked(recipe, PlatformTags.blockTag(id.substring(1)), null)
                : new Ticked(recipe, null, PlatformTags.block(id).orElse(null));
        }

        boolean matches(BlockState state) {
            return tag != null ? state.is(tag) : block != null && state.is(block);
        }
    }

    private WorldInteractions() {
    }

    public static void bootstrap() {
        Lang.add(Lang.item("flint_knife"), "燧石小刀", "Flint Knife");
        Lang.add(Lang.description("flint_knife"), "割取橡胶、松脂，刮下酒石", "Taps latex and resin, scrapes off tartar");
        Lang.add(Lang.item(FluidBottleItem.ID), "液体瓶", "Liquid Bottle");
        Lang.add(Lang.description(FluidBottleItem.ID), "装 250 mB 液体，可倒进锅、陶坛和储罐", "Holds 250 mB of a liquid for pots, crocks and tanks");
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> {
            output.accept(new ItemStack(FLINT_KNIFE.get()));
            output.accept(new ItemStack(FLUID_BOTTLE.get()));
        });
        PlatformEvents.onTagsUpdated(() -> loaded = null);
        PlatformEvents.onServerLevelTick(WorldInteractions::levelTick);
        PlatformEvents.onRightClickBlock(WorldInteractions::harvest);
        PlatformRandomTicks.setHook(WorldInteractions::randomTick);
        PlatformFluidPickup.setHook(WorldInteractions::seawater);
        WorldInteractionData.bootstrap();
    }

    private static Loaded loaded(ServerLevel level) {
        Loaded current = loaded;
        if (current == null) {
            List<BlockTransformRecipe> transforms = PlatformRecipes.all(level, BLOCK_TRANSFORM);
            List<BlockTransformRecipe> driven = transforms.stream().filter(r -> r.entities().isPresent() && r.entityChance() > 0).toList();
            List<Ticked> ticked = new ArrayList<>();
            for (BlockTransformRecipe recipe : transforms) {
                if (recipe.randomTickChance() > 0) {
                    ticked.add(Ticked.of(recipe));
                }
            }
            current = new Loaded(List.copyOf(transforms), List.copyOf(ticked), driven, List.copyOf(PlatformRecipes.all(level, HARVESTING)));
            Set<Block> hooked = new HashSet<>();
            for (Block block : net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
                BlockState state = block.defaultBlockState();
                for (BlockTransformRecipe recipe : transforms) {
                    if (recipe.randomTickChance() > 0 && recipe.matches(state)) {
                        hooked.add(block);
                    }
                }
            }
            PlatformRandomTicks.setHooked(hooked);
            loaded = current;
            FoodIndustrial.LOGGER.debug("World interactions: {} transforms, {} harvests", transforms.size(), current.harvests.size());
        }
        return current;
    }

    // ---- W01 ----

    private static void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Loaded current = loaded;
        if (current == null) {
            return;
        }
        List<Ticked> ticked = current.ticked();
        for (int i = 0; i < ticked.size(); i++) {
            Ticked transform = ticked.get(i);
            if (transform.matches(state) && random.nextDouble() < transform.recipe().randomTickChance()) {
                transform(level, pos, transform.recipe());
                return;
            }
        }
    }

    private static void levelTick(ServerLevel level) {
        long time = level.getGameTime();
        if (time % ENTITY_PASS != 0) {
            return;
        }
        Loaded current = loaded(level);
        if (current.driven().isEmpty()) {
            return;
        }
        int slot = (int) ((time / ENTITY_PASS) % (ENTITY_INTERVAL / ENTITY_PASS));
        List<Entity> due = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) {
            if (Math.floorMod(entity.getId(), ENTITY_INTERVAL / ENTITY_PASS) == slot) {
                due.add(entity);
            }
        }
        RandomSource random = level.getRandom();
        for (Entity entity : due) {
            for (BlockTransformRecipe recipe : current.driven()) {
                if (!PlatformTags.entityMatches(entity, recipe.entities().get())) {
                    continue;
                }
                BlockPos pos = entity.blockPosition().offset(random.nextInt(2 * ENTITY_RADIUS + 1) - ENTITY_RADIUS, -1,
                    random.nextInt(2 * ENTITY_RADIUS + 1) - ENTITY_RADIUS);
                if (level.isLoaded(pos) && recipe.matches(level.getBlockState(pos)) && random.nextDouble() < recipe.entityChance()) {
                    transform(level, pos, recipe);
                }
            }
        }
    }

    private static void transform(ServerLevel level, BlockPos pos, BlockTransformRecipe recipe) {
        if (!Environment.at(level, pos).covers(recipe.requirements().required())) {
            return;
        }
        recipe.resultBlock().ifPresent(block -> level.setBlockAndUpdate(pos, block.defaultBlockState()));
    }

    // ---- seawater (additives/08 §C) ----

    /** A water bucket filled in an ocean or beach biome holds seawater. */
    private static ItemStack seawater(net.minecraft.world.level.LevelAccessor level, BlockPos pos, ItemStack taken) {
        if (!taken.is(net.minecraft.world.item.Items.WATER_BUCKET)) {
            return taken;
        }
        var biome = level.getBiome(pos);
        if (!biome.is(net.minecraft.tags.BiomeTags.IS_OCEAN) && !biome.is(net.minecraft.tags.BiomeTags.IS_BEACH)) {
            return taken;
        }
        return PlatformTags.item(FoodIndustrial.MOD_ID + ":seawater_bucket").map(ItemStack::new).orElse(taken);
    }

    // ---- W02 ----

    private static boolean harvest(ServerLevel level, Player player, InteractionHand hand, BlockPos pos) {
        if (hand != InteractionHand.MAIN_HAND) {
            return false;
        }
        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        Loaded current = loaded(level);
        for (HarvestRecipe recipe : current.harvests()) {
            if (!recipe.tool().test(tool) || !recipe.matches(state)) {
                continue;
            }
            ItemStack container = player.getOffhandItem();
            if (recipe.container().isPresent() && !recipe.container().get().test(container)) {
                return false;
            }
            if (!Environment.at(level, pos).covers(recipe.requirements().required())) {
                return false;
            }
            Cooldowns cooldowns = PlatformSavedData.get(level, "harvest_cooldowns", Cooldowns::new).value();
            long time = level.getGameTime();
            if (cooldowns.until(pos) > time) {
                level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.5F, 0.8F);
                return true;
            }
            ItemStack result = result(recipe, level.getRandom());
            if (result.isEmpty()) {
                return false;
            }
            cooldowns.set(pos, time + recipe.cooldown());
            PlatformSavedData.get(level, "harvest_cooldowns", Cooldowns::new).setDirty();
            if (recipe.container().isPresent() && !player.getAbilities().instabuild) {
                container.shrink(1);
            }
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
            PlatformItems.damage(tool, 1, player, InteractionHand.MAIN_HAND);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1.0F);
            return true;
        }
        return false;
    }

    /** The harvested item, or a liquid bottle with the fluid; purity by the result's rule (source by default). */
    private static ItemStack result(HarvestRecipe recipe, RandomSource random) {
        int b = OutcomeResolver.NONE;
        if (recipe.result().bearsPurity()) {
            PurityRule rule = recipe.result().purity() != null ? recipe.result().purity() : PurityRule.SOURCE_DEFAULT;
            b = OutcomeResolver.apply(rule, com.tonywww.foodindustrial.core.purity.PurityData.current().material(recipe.result().material()),
                Double.NaN, 0);
        }
        if (recipe.result().isFluid()) {
            FluidAmount fluid = recipe.result().fluidAmount(recipe.result().amount());
            return PlatformFluidContainers.filled(new ItemStack(FLUID_BOTTLE.get()), b == OutcomeResolver.NONE ? fluid : fluid.withPurity(b));
        }
        ItemStack stack = recipe.result().stack(recipe.result().amount());
        return b == OutcomeResolver.NONE ? stack : Purity.with(stack, b);
    }

    /** Harvest cooldowns of a level by block position (game time when the block can be harvested again). */
    static final class Cooldowns implements PlatformSavedData.Stored {
        private final Map<Long, Long> until = new HashMap<>();

        long until(BlockPos pos) {
            return until.getOrDefault(pos.asLong(), 0L);
        }

        void set(BlockPos pos, long time) {
            until.put(pos.asLong(), time);
            if (until.size() > 4096) {
                long now = time - 1_000_000L;
                until.values().removeIf(value -> value < now);
            }
        }

        @Override
        public void save(DataWriter out) {
            DataWriter.ListWriter list = out.list("Cooldowns");
            until.forEach((pos, time) -> {
                DataWriter entry = list.add();
                entry.putLong("Pos", pos);
                entry.putLong("Until", time);
            });
        }

        @Override
        public void load(DataReader in) {
            until.clear();
            for (DataReader entry : in.list("Cooldowns")) {
                until.put(entry.getLong("Pos", 0L), entry.getLong("Until", 0L));
            }
        }
    }
}
