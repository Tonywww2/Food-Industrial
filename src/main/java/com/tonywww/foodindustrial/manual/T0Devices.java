package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.text.ConditionVocabulary;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.machine.PlatformMachineCapabilities;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * T0 hand tools and placed devices (I12, additives/07 §操作与实现规格): registration, the heat sources
 * of the pots and the shared helpers.
 */
public final class T0Devices {
    /** Blocks that heat a pot placed right above them when lit (lit campfires by default). */
    public static final String HEAT_SOURCES = "foodindustrial:pot_heat_sources";

    /** A registered device: block, item and block entity type. */
    public record Device(T0Kind kind, Supplier<T0DeviceBlock> block, Supplier<T0BlockItem> item,
                         Supplier<BlockEntityType<T0DeviceBlockEntity>> type) {
    }

    private static final Map<HandTool, Supplier<HandToolItem>> TOOLS = new EnumMap<>(HandTool.class);
    private static final Map<T0Kind, Device> DEVICES = new EnumMap<>(T0Kind.class);

    /**
     * Dried fruit from the drying rack, the food of the fumigation box (07 #25). Finished products of
     * I21, registered early so the chain can be made; food values are game settings (2026-11-10).
     */
    public static final Supplier<Item> DRIED_APPLE = food("dried_apple", "苹果干", "Dried Apple", 4, 0.6F);
    public static final Supplier<Item> DRIED_BERRIES = food("dried_berries", "浆果干", "Dried Berries", 4, 0.4F);

    static {
        for (HandTool tool : HandTool.values()) {
            Supplier<HandToolItem> item = PlatformRegistry.item(tool.id(), props -> new HandToolItem(props, tool), new Item.Properties().stacksTo(1));
            TOOLS.put(tool, item);
            Lang.add(Lang.item(tool.id()), tool.zh(), tool.en());
            Lang.add(Lang.description(tool.id()), tool.descriptionZh(), tool.descriptionEn());
            ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        }
        for (T0Kind kind : T0Kind.values()) {
            DEVICES.put(kind, device(kind));
        }
    }

    private T0Devices() {
    }

    private static Supplier<Item> food(String id, String zh, String en, int nutrition, float saturation) {
        Supplier<Item> item = PlatformRegistry.item(id, PlatformItems.food(new Item.Properties(), nutrition, saturation));
        Lang.add(Lang.item(id), zh, en);
        ModCreativeTabs.add(ModCreativeTabs.Tab.PRODUCTS, output -> output.accept(new ItemStack(item.get())));
        return item;
    }

    private static Device device(T0Kind kind) {
        @SuppressWarnings("unchecked")
        Supplier<BlockEntityType<T0DeviceBlockEntity>>[] type = new Supplier[1];
        Supplier<T0DeviceBlock> block = PlatformRegistry.block(kind.id(), props -> T0DeviceBlock.create(props, kind, () -> type[0].get()),
            properties(kind));
        type[0] = PlatformRegistry.blockEntity(kind.id(), (pos, state) -> new T0DeviceBlockEntity(type[0].get(), kind, pos, state), block);
        Supplier<T0BlockItem> item = PlatformRegistry.blockItem(kind.id(), block, (b, props) -> new T0BlockItem(b, props, kind.id()),
            new Item.Properties());
        PlatformMachineCapabilities.register(type[0]);
        Lang.add(Lang.block(kind.id()), kind.zh(), kind.en());
        Lang.add(Lang.description(kind.id()), kind.descriptionZh(), kind.descriptionEn());
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return new Device(kind, block, item, type[0]);
    }

    private static BlockBehaviour.Properties properties(T0Kind kind) {
        return switch (kind) {
            case IRON_POT -> BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.METAL).noOcclusion();
            case CLAY_POT, CROCK, SALT_PAN -> BlockBehaviour.Properties.of().strength(1.5F).sound(SoundType.DECORATED_POT).noOcclusion();
            case KOJI_TRAY -> BlockBehaviour.Properties.of().strength(0.8F).sound(SoundType.BAMBOO_WOOD).noOcclusion();
            default -> BlockBehaviour.Properties.of().strength(1.5F).sound(SoundType.WOOD).noOcclusion();
        };
    }

    public static Supplier<HandToolItem> tool(HandTool tool) {
        return TOOLS.get(tool);
    }

    public static Map<T0Kind, Device> devices() {
        return DEVICES;
    }

    /** Whether {@code state} heats a pot above it: in {@link #HEAT_SOURCES} and lit if it can be lit. */
    public static boolean isHeatSource(BlockState state) {
        if (!state.is(PlatformTags.blockTag(HEAT_SOURCES))) {
            return false;
        }
        return !state.hasProperty(BlockStateProperties.LIT) || state.getValue(BlockStateProperties.LIT);
    }

    /** Condition words joined for the action bar ({@code 露天、晴天}). */
    public static Component conditionNames(Set<String> keys) {
        MutableComponent names = Component.empty();
        boolean first = true;
        for (String key : keys) {
            int dot = key.indexOf('.');
            String category = dot < 0 ? key : key.substring(0, dot);
            String value = dot < 0 ? "" : key.substring(dot + 1);
            if (!first) {
                names.append(Component.translatable(GuiText.key("manual", "separator")));
            }
            names.append(ConditionVocabulary.find(category, value).map(word -> Component.translatable(word.translationKey()))
                .orElse(Component.literal(key)));
            first = false;
        }
        return names;
    }

    /** Registers the device specs, data generation and the client parts. Called during mod construction. */
    public static void bootstrap() {
        HandTool.registerSpecs();
        T0Kind.registerSpecs();
        ManualData.bootstrap();
        ManualRecipes.bootstrap();
        if (Platform.isPhysicalClient()) {
            com.tonywww.foodindustrial.manual.client.ManualClient.bootstrap();
        }
    }
}
