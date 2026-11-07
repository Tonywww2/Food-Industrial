package com.tonywww.foodindustrial.devtest;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.machine.ModMachines;
import com.tonywww.foodindustrial.machine.block.CasingBlock;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.StructurePattern;
import com.tonywww.foodindustrial.machine.definition.TickPhase;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/** Development-only machines for checking the machine framework (I09). */
final class DevMachines {
    static final Supplier<CasingBlock> CASING = PlatformRegistry.block("dev_casing", CasingBlock::new, BlockBehaviour.Properties.of().strength(1.0F));
    static final Supplier<BlockItem> CASING_ITEM = PlatformRegistry.blockItem("dev_casing", CASING, new Item.Properties());

    /** A fuel-burning T1 crystallizer with a fluid input (furnace rules). */
    static final MachineDefinition FUEL_CRYSTALLIZER = MachineDefinition.builder("dev_fuel_crystallizer")
        .name("测试燃料结晶器", "Test Fuel Crystallizer")
        .tier(Tier.T1)
        .types(ProcessTypes.CRYSTALLIZING)
        .layout(IoLayout.of(1, 1, 1, 1).withFuel(1))
        .capabilities(Capabilities.of("temperature.cooling"))
        .power(PowerKind.FUEL)
        .phase(TickPhase.REFINING)
        .development()
        .register();

    /** Dense FE layout with a catalyst, like M14 (layout checks of machines/04 §1). */
    static final MachineDefinition REACTOR = MachineDefinition.builder("dev_reactor")
        .name("测试反应罐", "Test Reactor")
        .tier(Tier.T2)
        .types(ProcessTypes.CRYSTALLIZING)
        .layout(IoLayout.of(3, 2, 3, 2).withCatalyst(1).withUpgrades(2).withModules(3))
        .capabilities(Capabilities.of("temperature.cooling"))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REFINING)
        .development()
        .register();

    /** Unpowered layout with two catalysts, like M05 / M29 (layout checks of machines/04 §1). */
    static final MachineDefinition FERMENTER = MachineDefinition.builder("dev_fermenter")
        .name("测试发酵缸", "Test Fermenter")
        .tier(Tier.T1)
        .types(ProcessTypes.CRYSTALLIZING)
        .layout(IoLayout.of(2, 1, 1, 2).withCatalyst(2))
        .capabilities(Capabilities.of("temperature.cooling"))
        .power(PowerKind.NONE)
        .phase(TickPhase.REFINING)
        .development()
        .register();

    /** A 3×3×3 multiblock of test casing and ports, controller in the middle of the front, hollow centre. */
    static final MachineDefinition MULTIBLOCK = MachineDefinition.builder("dev_multiblock")
        .name("测试多方块", "Test Multiblock")
        .tier(Tier.T3)
        .types(ProcessTypes.GRINDING, ProcessTypes.DISTILLING)
        .layout(IoLayout.of(1, 2, 2, 2))
        .capabilities(Capabilities.of("temperature.heated", "method.multi_fraction"))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.HEATING)
        .structure(StructurePattern.builder()
            .layer("###", "###", "###")
            .layer("###", "# #", "#C#")
            .layer("###", "###", "###")
            .where('#', "test casing or port", true, CASING)
            .build())
        .development()
        .register();

    private DevMachines() {
    }

    static void bootstrap() {
        ModMachines.ports();
    }
}
