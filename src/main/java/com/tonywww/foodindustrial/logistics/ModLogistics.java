package com.tonywww.foodindustrial.logistics;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.machine.PlatformMachineCapabilities;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Supplier;

/**
 * Fluid logistics of the mod (machines/03 §4–5, I11): wooden and metal tanks and the gas cylinder. No
 * pipes (2026-11-05): fluids move between neighbouring machines by automatic input and output.
 */
public final class ModLogistics {
    /** A registered tank: block, item and block entity type. */
    public record Tank(TankKind kind, Supplier<TankBlock> block, Supplier<TankBlockItem> item, Supplier<BlockEntityType<TankBlockEntity>> type) {
    }

    public static final Tank WOODEN_TANK = tank(TankKind.WOODEN, "木桶罐", "Wooden Tank",
        "存放液体，空手 Shift + 右键切换向下排出", "Stores liquids; Shift + right-click toggles draining",
        BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD).noOcclusion());
    public static final Tank METAL_TANK = tank(TankKind.METAL, "金属储罐", "Metal Tank",
        "存放液体或气体，竖直相连共用容量", "Stores liquids or gases; stacked tanks share storage",
        BlockBehaviour.Properties.of().strength(3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion());

    public static final Supplier<GasCylinderItem> GAS_CYLINDER = cylinder();

    private ModLogistics() {
    }

    private static Tank tank(TankKind kind, String zh, String en, String descZh, String descEn, BlockBehaviour.Properties properties) {
        @SuppressWarnings("unchecked")
        Supplier<BlockEntityType<TankBlockEntity>>[] type = new Supplier[1];
        Supplier<TankBlock> block = PlatformRegistry.block(kind.id(), props -> new TankBlock(props, kind, () -> type[0].get()), properties);
        type[0] = PlatformRegistry.blockEntity(kind.id(), (pos, state) -> new TankBlockEntity(type[0].get(), kind, pos, state), block);
        Supplier<TankBlockItem> item = PlatformRegistry.blockItem(kind.id(), block, TankBlockItem::new, new Item.Properties());
        PlatformMachineCapabilities.register(type[0]);
        Lang.add(Lang.block(kind.id()), zh, en);
        Lang.add(Lang.description(kind.id()), descZh, descEn);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return new Tank(kind, block, item, type[0]);
    }

    private static Supplier<GasCylinderItem> cylinder() {
        Supplier<GasCylinderItem> item = PlatformRegistry.item(GasCylinderItem.ID, GasCylinderItem::new, new Item.Properties().stacksTo(1));
        Lang.add(Lang.item(GasCylinderItem.ID), "气瓶", "Gas Cylinder");
        Lang.add(Lang.description(GasCylinderItem.ID), "搬运气体，在机器界面与储罐上装入取出", "Carries gases; fills and empties at machines and tanks");
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return item;
    }

    public static List<Tank> tanks() {
        return List.of(WOODEN_TANK, METAL_TANK);
    }

    /** Registers data generation and, on the client, the tank renderers. Called during mod construction. */
    public static void bootstrap() {
        LogisticsData.bootstrap();
        if (Platform.isPhysicalClient()) {
            com.tonywww.foodindustrial.logistics.client.LogisticsClient.bootstrap();
        }
    }
}
