package com.tonywww.foodindustrial.platform.compat.create;
//? if <26.1 {

import com.tonywww.foodindustrial.machine.workshop.DescribedBlockItem;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformKinetics;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
*///?}

/**
 * Create's transmission interface (I25, 2026-11-11): registered only with Create installed. It sits on
 * top of the stone mill or the screw press instead of the hand crank, takes a shaft from above and
 * costs {@link PlatformKinetics#STRESS_PER_RPM} stress units per RPM. Only this package and
 * {@code compat.create05} touch Create.
 * <p>
 * The block and its block entity use API that Create 6 and Create 0.5.1 share. The stress impact and the
 * rendering differ: with Flywheel 1.x (Create 6) this package registers them; with Flywheel 0.x (Create
 * 0.5.1, still common in 1.20.1 packs) the 1.20.1 Forge jar carries {@code compat.create05}, compiled
 * against Create 0.5.1 and called here by reflection (2026-11-14, user: keep Flywheel rendering, choose by
 * Flywheel's version).
 */
public final class CreateTransmission {
    static Supplier<TransmissionInterfaceBlock> block;
    static Supplier<BlockEntityType<TransmissionInterfaceBlockEntity>> type;

    private CreateTransmission() {
    }

    /** Registers the block, its item and block entity, the stress impact and the client visuals. */
    public static void register(IEventBus modBus) {
        String id = PlatformKinetics.INTERFACE_ID;
        block = PlatformRegistry.block(id, TransmissionInterfaceBlock::new,
            BlockBehaviour.Properties.of().strength(1.5F).sound(SoundType.WOOD).noOcclusion());
        Supplier<DescribedBlockItem> item = PlatformRegistry.blockItem(id, block, (b, props) -> new DescribedBlockItem(b, props, id),
            new Item.Properties());
        type = PlatformRegistry.blockEntity(id, (pos, state) -> new TransmissionInterfaceBlockEntity(type.get(), pos, state), block);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        boolean legacy = legacyFlywheel();
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            if (legacy) {
                legacy("registerStress", new Class<?>[] {Block.class, double.class}, block.get(), PlatformKinetics.STRESS_PER_RPM);
            } else {
                CreateStress.register(block.get(), PlatformKinetics.STRESS_PER_RPM);
            }
        }));
        if (Platform.isPhysicalClient()) {
            if (legacy) {
                legacy("bindClient", new Class<?>[] {IEventBus.class}, modBus);
            } else {
                CreateTransmissionClient.bind(modBus);
            }
        }
    }

    /** The interface's block entity type (for {@code compat.create05}). */
    public static BlockEntityType<TransmissionInterfaceBlockEntity> blockEntityType() {
        return type.get();
    }

    /** Whether the installed Flywheel is 0.x, the one Create 0.5.1 bundles (Create 6 bundles 1.x). */
    private static boolean legacyFlywheel() {
        String version = Platform.modVersion("flywheel");
        return version != null && version.startsWith("0.");
    }

    /** Calls a static method of {@code compat.create05.LegacyCreate}, which only the 1.20.1 Forge jar carries. */
    private static void legacy(String method, Class<?>[] types, Object... args) {
        String name = "com.tonywww.foodindustrial.platform.compat.create05.LegacyCreate";
        try {
            Class.forName(name).getMethod(method, types).invoke(null, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Create 0.5.1 support (" + name + "." + method + ") is missing from this jar", e);
        }
    }

    /** RPM (absolute) of the interface at {@code pos}, 0 without one. */
    public static float speedAt(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TransmissionInterfaceBlockEntity be ? Math.abs(be.getSpeed()) : 0;
    }
}
//?}
