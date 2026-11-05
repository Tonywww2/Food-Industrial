package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.FoodIndustrial;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if forge {
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Deferred registration without loader types. Every entry returns a plain {@link Supplier}
 * ({@code RegistryObject} on Forge, {@code DeferredHolder} on NeoForge).
 * <p>
 * Call these from static initializers of business holder classes (for example {@code ModItems}) and
 * trigger those classes from {@link FoodIndustrial#init()} with an explicit {@code bootstrap()} call,
 * so all entries are queued during mod construction.
 */
public final class PlatformRegistry {
    private static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTERS = new LinkedHashMap<>();
    private static IEventBus modBus;

    private PlatformRegistry() {
    }

    /** Registers {@code foodindustrial:<name>} in any vanilla or modded registry. */
    public static <T, I extends T> Supplier<I> register(ResourceKey<? extends Registry<T>> registry, String name, Supplier<? extends I> factory) {
        return registerFor(registry).register(name, factory);
    }

    /**
     * Registers an item. The factory receives the given properties already prepared for the running
     * Minecraft version (1.21.2+ requires the item id to be set before construction).
     */
    public static <I extends Item> Supplier<I> item(String name, Function<Item.Properties, ? extends I> factory, Item.Properties properties) {
        return register(Registries.ITEM, name, () -> factory.apply(itemProperties(name, properties)));
    }

    /** Registers a plain {@link Item}. */
    public static Supplier<Item> item(String name, Item.Properties properties) {
        return item(name, Item::new, properties);
    }

    /** Registers a block; properties are prepared for the running Minecraft version like {@link #item}. */
    public static <B extends Block> Supplier<B> block(String name, Function<BlockBehaviour.Properties, ? extends B> factory, BlockBehaviour.Properties properties) {
        return register(Registries.BLOCK, name, () -> factory.apply(blockProperties(name, properties)));
    }

    /** Registers the {@link BlockItem} for a block under the same name. */
    public static Supplier<BlockItem> blockItem(String name, Supplier<? extends Block> block, Item.Properties properties) {
        return item(name, props -> new BlockItem(block.get(), blockItemProperties(props)), properties);
    }

    /** Block item of a custom class, for example a {@link PlatformBlockItem} with tooltips. */
    public static <I extends BlockItem> Supplier<I> blockItem(String name, Supplier<? extends Block> block,
                                                            java.util.function.BiFunction<Block, Item.Properties, I> factory, Item.Properties properties) {
        return item(name, props -> factory.apply(block.get(), blockItemProperties(props)), properties);
    }

    /**
     * Registers an item with its own name that places {@code block}, like carrots or cocoa beans
     * ({@code ItemNameBlockItem} before 1.21.2, a plain {@link BlockItem} after).
     */
    public static Supplier<Item> placingItem(String name, Supplier<? extends Block> block, Item.Properties properties) {
        //? if >=1.21.2 {
        /*return item(name, props -> new BlockItem(block.get(), props), properties);
        *///?} else {
        return item(name, props -> new net.minecraft.world.item.ItemNameBlockItem(block.get(), props), properties);
        //?}
    }

    /** Registers the item of a two-block-high block (doors) under the block's name. */
    public static Supplier<Item> doubleHighBlockItem(String name, Supplier<? extends Block> block, Item.Properties properties) {
        return item(name, props -> new net.minecraft.world.item.DoubleHighBlockItem(block.get(), blockItemProperties(props)), properties);
    }

    /**
     * Registers a creative mode tab. Tabs without entries are hidden by the game.
     *
     * @param titleKey language key of the tab title
     * @param icon     icon stack, resolved when the tab is first drawn
     * @param entries  adds the tab's stacks, in order
     */
    public static Supplier<CreativeModeTab> creativeTab(String name, String titleKey, Supplier<ItemStack> icon, Consumer<Consumer<ItemStack>> entries) {
        return register(Registries.CREATIVE_MODE_TAB, name, () -> CreativeModeTab.builder()
            .title(Component.translatable(titleKey))
            .icon(icon)
            .displayItems((parameters, output) -> entries.accept(output::accept))
            .build());
    }

    /** Creates block entities for {@link #blockEntity}. */
    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    /** Registers a block entity type valid for the given blocks. */
    @SafeVarargs
    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> blockEntity(String name, BlockEntityFactory<T> factory, Supplier<? extends Block>... blocks) {
        return register(Registries.BLOCK_ENTITY_TYPE, name, () -> {
            Block[] valid = new Block[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                valid[i] = blocks[i].get();
            }
            return createBlockEntityType(factory, valid);
        });
    }

    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block[] blocks) {
        //? if >=26.1 {
        /*return new BlockEntityType<>(factory::create, blocks);
        *///?} else {
        return BlockEntityType.Builder.of(factory::create, blocks).build(null);
        //?}
    }

    private static Item.Properties itemProperties(String name, Item.Properties properties) {
        //? if >=1.21.2 {
        /*return properties.setId(PlatformIdentifier.key(Registries.ITEM, name));
        *///?} else {
        return properties;
        //?}
    }

    private static Item.Properties blockItemProperties(Item.Properties properties) {
        //? if >=1.21.2 {
        /*return properties.useBlockDescriptionPrefix();
        *///?} else {
        return properties;
        //?}
    }

    private static BlockBehaviour.Properties blockProperties(String name, BlockBehaviour.Properties properties) {
        //? if >=1.21.2 {
        /*return properties.setId(PlatformIdentifier.key(Registries.BLOCK, name));
        *///?} else {
        return properties;
        //?}
    }

    @SuppressWarnings("unchecked")
    private static <T> DeferredRegister<T> registerFor(ResourceKey<? extends Registry<T>> registry) {
        return (DeferredRegister<T>) REGISTERS.computeIfAbsent(registry, key -> {
            DeferredRegister<T> created = DeferredRegister.create(registry, FoodIndustrial.MOD_ID);
            if (modBus != null) {
                created.register(modBus);
            }
            return created;
        });
    }

    /** Called by {@link ModEntrypoint} after {@link FoodIndustrial#init()}. */
    static void bind(IEventBus bus) {
        modBus = bus;
        REGISTERS.values().forEach(register -> register.register(bus));
    }
}
