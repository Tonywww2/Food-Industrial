package com.tonywww.foodindustrial.devtest;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.core.purity.PurityRules;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.machine.PlatformMachineCapabilities;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Development-only content used to verify the platform layer on every node (registered only when
 * running from a development environment).
 */
public final class DevTestContent {
    public static final Supplier<PlatformTestBlock> PLATFORM_TEST_BLOCK = PlatformRegistry.block("platform_test",
        properties -> new PlatformTestBlock(properties, DevTestContent::blockEntityType), BlockBehaviour.Properties.of().strength(1.0F));
    public static final Supplier<BlockItem> PLATFORM_TEST_ITEM = PlatformRegistry.blockItem("platform_test", PLATFORM_TEST_BLOCK, new Item.Properties());
    public static final Supplier<BlockEntityType<PlatformTestBlockEntity>> PLATFORM_TEST = PlatformRegistry.blockEntity("platform_test",
        PlatformTestBlockEntity::new, PLATFORM_TEST_BLOCK);

    public static final Supplier<PurityTestItem> PURITY_TEST_ITEM = PlatformRegistry.item("purity_test", PurityTestItem::new, new Item.Properties());
    public static final PlatformFluids.Registered PURITY_TEST_FLUID = PlatformFluids.register("purity_test_fluid", PlatformFluids.Spec.liquid(0xFF8040C0));

    private DevTestContent() {
    }

    private static BlockEntityType<PlatformTestBlockEntity> blockEntityType() {
        return PLATFORM_TEST.get();
    }

    /** Devices for checking the recipe framework before the real machines exist (I08). */
    private static void registerTestDevices() {
        device("dev_pot", Tier.T0, true, false, new DeviceSpec.Slots(2, 2, 1, 1),
            Capabilities.of("temperature.heated", "method.open_flame"), ProcessTypes.CRYSTALLIZING, ProcessTypes.BOILING, ProcessTypes.GRINDING);
        device("dev_salt_pan", Tier.T0, true, false, new DeviceSpec.Slots(0, 1, 1, 1),
            Capabilities.of("environment.open_sky", "environment.clear_weather"), ProcessTypes.CRYSTALLIZING);
        device("dev_crystallizer", Tier.T1, false, true, new DeviceSpec.Slots(1, 1, 1, 1),
            Capabilities.of("temperature.cooling"), ProcessTypes.CRYSTALLIZING, ProcessTypes.GRINDING);
        device("dev_evaporator", Tier.T2, false, true, new DeviceSpec.Slots(1, 2, 2, 2),
            Capabilities.of("temperature.heated", "pressure.vacuum"), ProcessTypes.CRYSTALLIZING, ProcessTypes.BOILING, ProcessTypes.GRINDING);
        device("dev_still", Tier.T3, false, true, new DeviceSpec.Slots(0, 1, 2, 6),
            Capabilities.of("temperature.heated", "method.multi_fraction"), ProcessTypes.DISTILLING);
        device("dev_simple_still", Tier.T1, false, true, new DeviceSpec.Slots(0, 1, 1, 2),
            Capabilities.of("temperature.heated"), ProcessTypes.DISTILLING);
    }

    private static void device(String name, Tier tier, boolean manual, boolean timed, DeviceSpec.Slots slots, Capabilities capabilities,
                               ProcessType... types) {
        DeviceSpec.register(new DeviceSpec("foodindustrial:" + name, DeviceProfile.standard(tier), Set.of(types), capabilities, slots,
            manual, timed, null));
    }

    public static void bootstrap() {
        PlatformMachineCapabilities.register(PLATFORM_TEST);
        Purity.registerItem(PURITY_TEST_ITEM, "foodindustrial:gypsum");
        Purity.registerFluid(PURITY_TEST_FLUID.source(), "foodindustrial:sulfuric_acid");
        PlatformEvents.onRegisterCommands(DevCommands::register);
        PlatformEvents.onRegisterCommands(DevRecipeCommands::register);
        PlatformEvents.onRegisterCommands(DevMachineCommands::register);
        PlatformEvents.onRegisterCommands(DevManualCommands::register);
        PlatformEvents.onRegisterCommands(DevMetabolismCommands::register);
        DevMachines.bootstrap();
        registerTestDevices();
        if (Platform.isPhysicalClient()) {
            PurityRules.onChanged(ClientPurityDump::dump);
        }
    }
}
