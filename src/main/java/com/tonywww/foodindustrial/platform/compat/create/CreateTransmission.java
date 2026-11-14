package com.tonywww.foodindustrial.platform.compat.create;
//? if <26.1 {

import com.simibubi.create.api.stress.BlockStressValues;
import com.tonywww.foodindustrial.machine.workshop.DescribedBlockItem;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformKinetics;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
 * costs {@link PlatformKinetics#STRESS_PER_RPM} stress units per RPM. Only this package touches Create.
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
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(
            () -> BlockStressValues.IMPACTS.register(block.get(), () -> PlatformKinetics.STRESS_PER_RPM)));
        if (Platform.isPhysicalClient()) {
            CreateTransmissionClient.bind(modBus);
        }
    }

    /** RPM (absolute) of the interface at {@code pos}, 0 without one. */
    public static float speedAt(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TransmissionInterfaceBlockEntity be ? Math.abs(be.getSpeed()) : 0;
    }
}
//?}
