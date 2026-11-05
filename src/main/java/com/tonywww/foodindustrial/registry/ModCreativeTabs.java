package com.tonywww.foodindustrial.registry;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The mod's creative mode tabs (I05): chemicals, minerals and raw materials, cultures and enzymes,
 * machines, products. Content plans add their stacks with {@link #add}; empty tabs stay hidden.
 */
public final class ModCreativeTabs {
    /** Tabs in display order. */
    public enum Tab {
        CHEMICALS("chemicals", "化学品", "Chemicals"),
        RAW_MATERIALS("raw_materials", "矿物与原料", "Minerals and Raw Materials"),
        CULTURES("cultures", "菌种与酶", "Cultures and Enzymes"),
        MACHINES("machines", "机器", "Machines"),
        PRODUCTS("products", "成品", "Products");

        final String id;
        final String zh;
        final String en;

        Tab(String id, String zh, String en) {
            this.id = id;
            this.zh = zh;
            this.en = en;
        }
    }

    private static final Map<Tab, List<Consumer<Consumer<ItemStack>>>> ENTRIES = new EnumMap<>(Tab.class);
    private static final Map<Tab, Supplier<ItemStack>> ICONS = new EnumMap<>(Tab.class);

    private ModCreativeTabs() {
    }

    /** Adds stacks to a tab; {@code entries} runs whenever the tab contents are rebuilt. */
    public static void add(Tab tab, Consumer<Consumer<ItemStack>> entries) {
        ENTRIES.computeIfAbsent(tab, key -> new ArrayList<>()).add(entries);
    }

    /** Sets the icon of a tab. */
    public static void icon(Tab tab, Supplier<ItemStack> icon) {
        ICONS.put(tab, icon);
    }

    /** Registers the tabs and their titles. Call once during mod construction. */
    public static void bootstrap() {
        for (Tab tab : Tab.values()) {
            String key = Lang.creativeTab(tab.id);
            Lang.add(key, tab.zh, tab.en);
            PlatformRegistry.creativeTab(tab.id, key,
                () -> ICONS.getOrDefault(tab, () -> ItemStack.EMPTY).get(),
                output -> ENTRIES.getOrDefault(tab, List.of()).forEach(entries -> entries.accept(output)));
        }
    }
}
