package com.tonywww.foodindustrial.machine.workshop;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.block.CasingBlock;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.Climate;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.MachineModule;
import com.tonywww.foodindustrial.machine.definition.MachineRequirement;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.StructurePattern;
import com.tonywww.foodindustrial.machine.definition.TickPhase;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Supplier;

/**
 * The ten T1 workshop machines M01–M10 (I14, additives/06 §T1 作坊：操作与实现规格, machines/03 §3)
 * with their casings, the hand crank, the condenser and the essential oil separator module. Slots,
 * phases, modes and capabilities follow the design tables; numbers and recipes are game settings.
 */
public final class WorkshopMachines {
    // ---- members and attachments ----

    public static final Supplier<CasingBlock> KOJI_ROOM_WALL = casing("koji_room_wall", "曲房墙", "Koji Room Wall",
        BlockBehaviour.Properties.of().strength(1.5F).sound(SoundType.WOOD));
    public static final Supplier<CasingBlock> KILN_BRICK = casing("kiln_brick", "窑砖", "Kiln Bricks",
        BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops());
    public static final Supplier<CasingBlock> DRYING_ROOM_WALL = casing("drying_room_wall", "烘房墙", "Drying Room Wall",
        BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops());

    public static final Supplier<HandCrankBlock> HAND_CRANK = PlatformRegistry.block(HandCrankBlock.ID, HandCrankBlock::new,
        BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.WOOD).noOcclusion());
    public static final Supplier<Block> CONDENSER = PlatformRegistry.block("condenser", Block::new,
        BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.COPPER).requiresCorrectToolForDrops());

    public static final Supplier<Item> ESSENTIAL_OIL_SEPARATOR = PlatformRegistry.item("essential_oil_separator",
        props -> new DescribedItem(props, "essential_oil_separator"), new Item.Properties().stacksTo(1));
    /** M08 module: separates the essential oil layer in steam distillation (machines/01 §4.3). */
    public static final MachineModule OIL_SEPARATOR = MachineModule.register(
        new MachineModule("essential_oil_separator", Capabilities.of("method.oil_separation"), 1000), ESSENTIAL_OIL_SEPARATOR);

    static {
        describedBlockItem(HandCrankBlock.ID, HAND_CRANK, "手摇柄", "Hand Crank",
            "装在石磨或压榨机顶上，空手右键摇动", "Goes on a stone mill or screw press; right-click to turn");
        // Create's transmission interface (I25, 2026-11-11): registered only with Create; texts and assets always.
        com.tonywww.foodindustrial.platform.PlatformKinetics.registerInterface();
        Lang.add(Lang.block(com.tonywww.foodindustrial.platform.PlatformKinetics.INTERFACE_ID), "传动接口", "Transmission Interface");
        Lang.add(Lang.description(com.tonywww.foodindustrial.platform.PlatformKinetics.INTERFACE_ID), "装在石磨或压榨机顶上，接机械动力传动轴",
            "Goes on a stone mill or screw press; takes a Create shaft");
        com.tonywww.foodindustrial.platform.PlatformKinetics.onSpeedChanged((level, pos) -> {
            if (level.getBlockEntity(pos.below()) instanceof com.tonywww.foodindustrial.machine.entity.MachineBlockEntity machine) {
                machine.wake();
            }
        });
        describedBlockItem("condenser", CONDENSER, "冷凝器", "Condenser",
            "装在蒸馏甑顶上，冷凝馏出的蒸气", "Goes on top of a pot still to condense the vapour");
        Lang.add(Lang.item("essential_oil_separator"), "精油分层器", "Essential Oil Separator");
        Lang.add(Lang.description("essential_oil_separator"), "蒸馏甑模块：水蒸气蒸馏时分出精油",
            "Pot still module: separates oil in steam distillation");
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(ESSENTIAL_OIL_SEPARATOR.get())));
    }

    // ---- machines ----

    /** M01 (machines/03 §3: I 1, O 2, FI 1, FO 1; hand crank; phase 0). */
    public static final MachineDefinition STONE_MILL = MachineDefinition.builder("stone_mill")
        .name("石磨", "Stone Mill")
        .description("人力研磨，可干磨或加水湿磨", "Hand-cranked grinding, dry or wet with water")
        .tier(Tier.T1)
        .types(ProcessTypes.GRINDING)
        .layout(IoLayout.of(1, 2, 1, 1))
        .modes(MachineMode.of("dry_grinding", Capabilities.NONE), MachineMode.of("wet_grinding", Capabilities.NONE))
        .power(PowerKind.MANUAL)
        .phase(TickPhase.MECHANICAL)
        .properties(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops())
        .register();

    /** M02 (I 1, O 1, FO 1; hand crank; phase 0). */
    public static final MachineDefinition SCREW_PRESS = MachineDefinition.builder("screw_press")
        .name("螺旋压榨机", "Screw Press")
        .description("人力压榨：取汁、取油、凝胶脱水", "Hand-cranked pressing: juice, oil, gel dewatering")
        .tier(Tier.T1)
        .types(ProcessTypes.PRESSING)
        .layout(IoLayout.of(1, 1, 0, 1))
        .power(PowerKind.MANUAL)
        .phase(TickPhase.MECHANICAL)
        .properties(wood())
        .register();

    /** M03 (I 2, O 2, FI 2, FO 1, F 1; fuel; phase 1); also heated extraction and crystallization (2026-11-10); two input tanks from I31 (2026-11-12). */
    public static final MachineDefinition BOILING_STOVE = MachineDefinition.builder("boiling_stove")
        .name("熬煮灶", "Boiling Stove")
        .description("烧燃料熬煮、蒸发、热浸提与重结晶", "Fuel-fired boiling, hot extraction and recrystallizing")
        .tier(Tier.T1)
        .types(ProcessTypes.BOILING, ProcessTypes.REACTING, ProcessTypes.EXTRACTING, ProcessTypes.CRYSTALLIZING)
        .layout(IoLayout.of(2, 2, 2, 1).withFuel(1))
        .capabilities(Capabilities.of("temperature.heated", "method.open_flame"))
        .power(PowerKind.FUEL)
        .phase(TickPhase.HEATING)
        .register();

    /** M04 (I 2, O 2, FI 2 — water or solvent, the gas let in —, FO 1; no power; phase 0); settling and filtration (2026-11-10). */
    public static final MachineDefinition STEEPING_VAT = MachineDefinition.builder("steeping_vat")
        .name("浸提桶", "Steeping Vat")
        .description("浸泡、淋洗、过滤、澄清、浸提，可通入气体", "Soaking, leaching, filtering, extracting; takes gases")
        .tier(Tier.T1)
        .types(ProcessTypes.SEPARATING, ProcessTypes.EXTRACTING, ProcessTypes.ABSORBING)
        .layout(IoLayout.of(2, 2, 2, 1))
        .capabilities(Capabilities.of("method.settling", "method.filtration"))
        .phase(TickPhase.MECHANICAL)
        .properties(wood())
        .register();

    /** M05 (I 2, O 1, FI 1, FO 2 — broth, CO2 or biogas —, C 1 culture; no power; phase 3). */
    public static final MachineDefinition FERMENTATION_VAT = MachineDefinition.builder("fermentation_vat")
        .name("发酵缸", "Fermentation Vat")
        .description("液态发酵，可开口、密闭或厌氧", "Liquid fermentation: open, sealed or anaerobic")
        .tier(Tier.T1)
        .types(ProcessTypes.FERMENTING)
        .layout(IoLayout.of(2, 1, 1, 2).withCatalyst(1))
        .slotNames(SlotRole.CATALYST, "culture")
        .modes(MachineMode.of("open", Capabilities.of("atmosphere.air")),
            MachineMode.of("sealed", Capabilities.of("atmosphere.airtight")),
            MachineMode.of("anaerobic", Capabilities.of("atmosphere.airtight")))
        .phase(TickPhase.REACTION)
        .register();

    /** M06 (I 2, O 2, FI 1 water, C 1 culture, F 1 brazier; optional fuel; phase 3; multiblock). */
    public static final MachineDefinition KOJI_ROOM = MachineDefinition.builder("koji_room")
        .name("曲房", "Koji Room")
        .description("固态发酵与制曲，燃料和水调温湿度", "Solid fermentation; fuel and water set the climate")
        .tier(Tier.T1)
        .types(ProcessTypes.SOLID_FERMENTING)
        .layout(IoLayout.of(2, 2, 1, 0).withCatalyst(1).withFuel(1))
        .slotNames(SlotRole.CATALYST, "culture")
        .modes(MachineMode.of("koji_tray", Capabilities.NONE), MachineMode.of("shallow_tray", Capabilities.NONE))
        .climate(new Climate(10, 40))
        .phase(TickPhase.REACTION)
        .structure(room(KOJI_ROOM_WALL, "koji_room_wall"))
        .properties(wood())
        .register();

    /** M07 (I 2, O 3 — product, ash, charcoal —, FO 2 — condensate, gas —, F 1; fuel; phase 1; multiblock). */
    public static final MachineDefinition EARTH_KILN = MachineDefinition.builder("earth_kiln")
        .name("土窑", "Earthen Kiln")
        .description("煅烧、烧灰与干馏", "Calcining, ashing and dry distillation")
        .tier(Tier.T1)
        .types(ProcessTypes.KILN)
        .layout(IoLayout.of(2, 3, 0, 2).withFuel(1))
        .capabilities(Capabilities.of("temperature.heated", "temperature.high"))
        .modes(MachineMode.of("calcining", Capabilities.of("atmosphere.air")),
            MachineMode.of("ashing", Capabilities.of("atmosphere.air")),
            MachineMode.of("dry_distillation", Capabilities.of("atmosphere.airtight")))
        .power(PowerKind.FUEL)
        .phase(TickPhase.HEATING)
        .structure(room(KILN_BRICK, "kiln_brick"))
        .register();

    /** M08 (I 1, O 1 residue, FI 1, FO 2 — distillate, residual liquid —, F 1, X 1; fuel; phase 1). */
    public static final MachineDefinition POT_STILL = MachineDefinition.builder("pot_still")
        .name("蒸馏甑", "Pot Still")
        .description("蒸馏、水蒸气蒸馏、升华，顶上装冷凝器", "Distilling and subliming; needs a condenser on top")
        .tier(Tier.T1)
        .types(ProcessTypes.DISTILLING)
        .layout(IoLayout.of(1, 1, 1, 2).withFuel(1).withModules(1))
        .capabilities(Capabilities.of("temperature.heated"))
        .modes(MachineMode.of("distillation", Capabilities.NONE), MachineMode.of("steam_distillation", Capabilities.NONE),
            MachineMode.of("sublimation", Capabilities.NONE))
        .modules(OIL_SEPARATOR)
        .requires(MachineRequirement.blockAbove(CONDENSER, Lang.block("condenser")))
        .power(PowerKind.FUEL)
        .phase(TickPhase.HEATING)
        .register();

    /** M09 (I 1 seed or modifier, O 1, FI 1, FO 1 mother liquor; no power; phase 2). */
    public static final MachineDefinition CRYSTALLIZING_TANK = MachineDefinition.builder("crystallizing_tank")
        .name("结晶槽", "Crystallizing Tank")
        .description("冷却结晶或露天晾晒结晶，留下母液", "Cooling or sun crystallizing; leaves mother liquor")
        .tier(Tier.T1)
        .types(ProcessTypes.CRYSTALLIZING)
        .layout(IoLayout.of(1, 1, 1, 1))
        .modes(MachineMode.of("cooling_crystallization", Capabilities.of("temperature.cooling")),
            MachineMode.outdoors("sun_crystallization"))
        .phase(TickPhase.REFINING)
        .register();

    /** M10 (I 4, O 4, FO 1 flue gas, F 1; fuel; phase 1; multiblock). */
    public static final MachineDefinition DRYING_ROOM = MachineDefinition.builder("drying_room")
        .name("烘房", "Drying Room")
        .description("烘干、烟熏与燃硫熏蒸", "Drying, smoking and sulfur burning")
        .tier(Tier.T1)
        .types(ProcessTypes.DRYING, ProcessTypes.FUMIGATING, ProcessTypes.GAS_REACTING)
        .layout(IoLayout.of(4, 4, 0, 1).withFuel(1))
        .capabilities(Capabilities.of("temperature.heated"))
        .modes(MachineMode.of("drying", Capabilities.NONE, ProcessTypes.DRYING),
            MachineMode.of("smoking", Capabilities.NONE, ProcessTypes.FUMIGATING),
            MachineMode.of("sulfur_burning", Capabilities.NONE, ProcessTypes.GAS_REACTING))
        .power(PowerKind.FUEL)
        .phase(TickPhase.HEATING)
        .structure(room(DRYING_ROOM_WALL, "drying_room_wall"))
        .register();

    private WorkshopMachines() {
    }

    /** The ten machines in M01–M10 order. */
    public static List<MachineDefinition> machines() {
        return List.of(STONE_MILL, SCREW_PRESS, BOILING_STOVE, STEEPING_VAT, FERMENTATION_VAT, KOJI_ROOM, EARTH_KILN, POT_STILL,
            CRYSTALLIZING_TANK, DRYING_ROOM);
    }

    /** Casing block ids. */
    static List<String> casings() {
        return List.of("koji_room_wall", "kiln_brick", "drying_room_wall");
    }

    /** Machines built of wood (also mined with an axe). */
    static List<MachineDefinition> wooden() {
        return List.of(SCREW_PRESS, STEEPING_VAT, KOJI_ROOM);
    }

    /** Loads the class (registration happens in the static fields). Called during mod construction. */
    public static void bootstrap() {
        machines();
        WorkshopData.bootstrap();
    }

    private static BlockBehaviour.Properties wood() {
        return BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD);
    }

    /**
     * Fixed 3 × 3 × 3 room (2026-11-10): casing or ports all round, the centre left free, the controller
     * in the middle of the front face.
     */
    private static StructurePattern room(Supplier<? extends Block> casing, String casingId) {
        return StructurePattern.builder()
            .layer("###", "###", "###")
            .layer("###", "# #", "#C#")
            .layer("###", "###", "###")
            .where('#', Lang.block(casingId), true, casing)
            .build();
    }

    private static Supplier<CasingBlock> casing(String id, String zh, String en, BlockBehaviour.Properties properties) {
        Supplier<CasingBlock> block = PlatformRegistry.block(id, CasingBlock::new, properties);
        Supplier<BlockItem> item = PlatformRegistry.blockItem(id, block, new Item.Properties());
        Lang.add(Lang.block(id), zh, en);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return block;
    }

    private static void describedBlockItem(String id, Supplier<? extends Block> block, String zh, String en, String descZh, String descEn) {
        Supplier<DescribedBlockItem> item = PlatformRegistry.blockItem(id, block, (b, props) -> new DescribedBlockItem(b, props, id),
            new Item.Properties());
        Lang.add(Lang.block(id), zh, en);
        Lang.add(Lang.description(id), descZh, descEn);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
    }
}
