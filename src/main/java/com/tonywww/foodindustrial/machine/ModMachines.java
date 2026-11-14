package com.tonywww.foodindustrial.machine;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.block.PortBlock;
import com.tonywww.foodindustrial.machine.entity.PortBlockEntity;
import com.tonywww.foodindustrial.machine.tool.DeviceWrenchItem;
import com.tonywww.foodindustrial.machine.tool.RedprintItem;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.machine.PlatformMachineCapabilities;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Machines and the shared multiblock ports. I09 registers the ports; I14 the T1 workshop
 * ({@code machine.workshop}); I15 the T2 machines with M11, the I09 sample ({@code machine.mechanical});
 * I16 the T3 machines ({@code machine.chemical}); I17 the T4 machines ({@code machine.bio}).
 */
public final class ModMachines {
    /** A registered port: block, item and block entity type. */
    public record Port(PortBlockEntity.Kind kind, Supplier<PortBlock> block, Supplier<BlockItem> item,
                       Supplier<BlockEntityType<PortBlockEntity>> type) {
    }

    public static final Port ITEM_PORT = port("item_port", PortBlockEntity.Kind.ITEM, "物品仓", "Item Port");
    public static final Port FLUID_PORT = port("fluid_port", PortBlockEntity.Kind.FLUID, "流体仓", "Fluid Port");
    public static final Port ENERGY_PORT = port("energy_port", PortBlockEntity.Kind.ENERGY, "能量仓", "Energy Port");

    /** Device wrench and redprint (machines/04 §4, 2026-11-05; English names pending review). */
    public static final Supplier<DeviceWrenchItem> DEVICE_WRENCH = tool(DeviceWrenchItem.ID, DeviceWrenchItem::new,
        "设备扳手", "Device Wrench", "右键旋转机器，Shift + 右键拆下并保留设置", "Rotates machines; Shift + right-click dismantles");
    public static final Supplier<RedprintItem> REDPRINT = tool(RedprintItem.ID, RedprintItem::new,
        "红图", "Redprint", "Shift + 右键复制机器设置，右键粘贴到同类机器", "Shift + right-click copies settings; right-click pastes");

    private ModMachines() {
    }

    private static <I extends Item> Supplier<I> tool(String id, Function<Item.Properties, I> factory, String zh, String en, String descZh, String descEn) {
        Supplier<I> item = PlatformRegistry.item(id, factory, new Item.Properties().stacksTo(1));
        Lang.add(Lang.item(id), zh, en);
        Lang.add(Lang.description(id), descZh, descEn);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return item;
    }

    /** Ids of the tool items (generated item models and recipes). */
    static List<String> tools() {
        return List.of(DeviceWrenchItem.ID, RedprintItem.ID);
    }

    private static Port port(String id, PortBlockEntity.Kind kind, String zh, String en) {
        @SuppressWarnings("unchecked")
        Supplier<BlockEntityType<PortBlockEntity>>[] type = new Supplier[1];
        Supplier<PortBlock> block = PlatformRegistry.block(id, properties -> new PortBlock(properties, () -> type[0].get()),
            BlockBehaviour.Properties.of().strength(3.5F).requiresCorrectToolForDrops());
        type[0] = PlatformRegistry.blockEntity(id, (pos, state) -> new PortBlockEntity(type[0].get(), kind, pos, state), block);
        Supplier<BlockItem> item = PlatformRegistry.blockItem(id, block, new Item.Properties());
        PlatformMachineCapabilities.register(type[0]);
        Lang.add(Lang.block(id), zh, en);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return new Port(kind, block, item, type[0]);
    }

    public static List<Port> ports() {
        return List.of(ITEM_PORT, FLUID_PORT, ENERGY_PORT);
    }

    /** Loads the class (registration happens in the static fields). Called during mod construction. */
    public static void bootstrap() {
        com.tonywww.foodindustrial.machine.workshop.WorkshopMachines.bootstrap();
        com.tonywww.foodindustrial.machine.mechanical.MechanicalMachines.bootstrap();
        com.tonywww.foodindustrial.machine.chemical.ChemicalMachines.bootstrap();
        com.tonywww.foodindustrial.machine.bio.BioMachines.bootstrap();
        MachineData.bootstrap();
    }
}
