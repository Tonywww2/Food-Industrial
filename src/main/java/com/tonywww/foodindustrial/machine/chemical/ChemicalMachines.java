package com.tonywww.foodindustrial.machine.chemical;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.block.CasingBlock;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.MachineModule;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.StructurePattern;
import com.tonywww.foodindustrial.machine.definition.TickPhase;
import com.tonywww.foodindustrial.machine.mechanical.MechanicalMachines;
import com.tonywww.foodindustrial.machine.workshop.DescribedItem;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import com.tonywww.foodindustrial.registry.SharedTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Supplier;

/**
 * The seven T3 machines M22–M28 (I16, additives/06 §T3 化工：操作与实现规格, machines/03 §3) with the
 * chemical frame, the casings, the furnace tube, the graphite electrode and the molecular distillation
 * module. Every machine runs on FE; numbers, names and recipes are game settings (pending review).
 */
public final class ChemicalMachines {
    private static final String NS = "foodindustrial";

    // ---- members and items ----

    public static final Supplier<CasingBlock> CHEMICAL_TOWER_WALL = casing("chemical_tower_wall", "化工塔壁", "Chemical Tower Wall");
    public static final Supplier<CasingBlock> REFRACTORY_BRICKS = casing("refractory_bricks", "耐火砖", "Refractory Bricks");
    public static final Supplier<CasingBlock> FURNACE_TUBE = casing("furnace_tube", "炉管", "Furnace Tube");

    public static final Supplier<Item> CHEMICAL_FRAME = described("chemical_frame", 64, "化工框架", "Chemical Frame",
        "T3 机器共用的框架", "Shared frame of the T3 machines");
    /** Electrode of M24 and M26, worn in their catalyst slot (tag {@code foodindustrial:catalysts/electrode}). */
    public static final Supplier<Item> GRAPHITE_ELECTRODE = described("graphite_electrode", 64, "石墨电极", "Graphite Electrode",
        "电解槽、电弧炉的电极，放催化剂槽", "Electrode of the electrolytic cell and arc furnace");
    /** T4 module of M22; crafted from I17 on. */
    public static final MachineModule MOLECULAR_DISTILLATION = MachineModule.register(new MachineModule("molecular_distillation",
        Capabilities.of("method.molecular_distillation", "pressure.vacuum", "method.vacuum"), 1000),
        described("molecular_distillation", 1, "分子蒸馏组件", "Molecular Distillation Unit",
            "精馏塔模块：分子蒸馏、真空", "Distillation column module: molecular distillation"));

    static {
        SharedTags.item(NS + ":catalysts/electrode", NS + ":graphite_electrode");
    }

    // ---- machines ----

    /** M22 (O 1, FI 2, FO 6, U 3, X 1; phase 1; multiblock): a column of 2–6 trays, one fraction per tray. */
    public static final MachineDefinition DISTILLATION_COLUMN = MachineDefinition.builder("distillation_column")
        .name("精馏塔", "Distillation Column")
        .description("多馏分精馏，塔板层数即馏分数", "Fractional distillation; one tray per fraction")
        .tier(Tier.T3)
        .types(ProcessTypes.DISTILLING)
        .layout(IoLayout.of(0, 1, 2, 6).withUpgrades(3).withModules(1))
        .capabilities(Capabilities.of("temperature.heated", "method.multi_fraction"))
        .modes(MachineMode.of("atmospheric_distillation", Capabilities.NONE),
            MachineMode.of("vacuum_distillation", Capabilities.of("pressure.vacuum")),
            MachineMode.of("cryogenic_separation", Capabilities.of("temperature.freezing")),
            MachineMode.of("stripping", Capabilities.NONE))
        .modules(MOLECULAR_DISTILLATION)
        .power(PowerKind.ENERGY)
        .phase(TickPhase.HEATING)
        .structure(StructurePattern.builder()
            .layer("###", "###", "#C#")
            .layer("###", "# #", "###").repeat(2, 6)
            .layer("###", "###", "###")
            .where('#', Lang.block("chemical_tower_wall"), true, CHEMICAL_TOWER_WALL)
            .build())
        .entity(DistillationColumnBlockEntity::new)
        .register();

    /** M23 (I 2, O 2, FI 5, FO 2, C 1, U 3; phase 3; five input tanks from I31, 2026-11-11). */
    public static final MachineDefinition HIGH_PRESSURE_REACTOR = MachineDefinition.builder("high_pressure_reactor")
        .name("高压反应釜", "High-Pressure Reactor")
        .description("加压、高压液相反应，带催化剂槽", "Pressurized reactions with a catalyst slot")
        .tier(Tier.T3)
        .types(ProcessTypes.REACTING)
        .layout(IoLayout.of(2, 2, 5, 2).withCatalyst(1).withUpgrades(3))
        .capabilities(Capabilities.of("temperature.heated", "temperature.high", "pressure.pressurized", "pressure.high"))
        .modes(MachineMode.of("hydrogenation", Capabilities.NONE), MachineMode.of("carbonylation", Capabilities.NONE),
            MachineMode.of("ammonia_synthesis", Capabilities.NONE), MachineMode.of("pressure_oxidation", Capabilities.NONE),
            MachineMode.of("epoxidation", Capabilities.NONE), MachineMode.of("ethoxylation", Capabilities.NONE),
            // I31 (P97): high-pressure radical polymerization of ethylene
            MachineMode.of("polymerization", Capabilities.of("method.polymerization")))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REACTION)
        .register();

    /** M24 (I 1, O 1, FI 2, FO 3, C 1 electrode, U 3; phase 2). */
    public static final MachineDefinition ELECTROLYZER = MachineDefinition.builder("electrolyzer")
        .name("电解槽", "Electrolytic Cell")
        .description("电解：氯碱、还原、氧化、电解水、熔盐", "Electrolysis: chlor-alkali, water, molten salt and more")
        .tier(Tier.T3)
        .types(ProcessTypes.ELECTROLYZING)
        .layout(IoLayout.of(1, 1, 2, 3).withCatalyst(1).withUpgrades(3))
        .slotNames(SlotRole.CATALYST, "electrode")
        .modes(MachineMode.of("chlor_alkali", Capabilities.NONE), MachineMode.of("electroreduction", Capabilities.NONE),
            MachineMode.of("electrooxidation", Capabilities.NONE), MachineMode.of("water_electrolysis", Capabilities.NONE),
            MachineMode.of("molten_salt", Capabilities.of("temperature.high")))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REFINING)
        .register();

    /** M25 (FI 2, FO 2, C 1 adsorbent, U 3; phase 2): saturated adsorbents regenerate. */
    public static final MachineDefinition ADSORPTION_COLUMN = MachineDefinition.builder("adsorption_column")
        .name("吸附柱", "Adsorption Column")
        .description("吸附与离子交换，吸附剂饱和后自动再生", "Adsorption and ion exchange; regenerates its adsorbent")
        .tier(Tier.T3)
        .types(ProcessTypes.PURIFYING)
        .layout(IoLayout.of(0, 0, 2, 2).withCatalyst(1).withUpgrades(3))
        .slotNames(SlotRole.CATALYST, "adsorbent")
        .capabilities(Capabilities.of("method.ion_exchange", AdsorptionColumnBlockEntity.REGENERATION))
        .modes(MachineMode.of("adsorption", Capabilities.NONE), MachineMode.of("fractionation", Capabilities.NONE))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REFINING)
        .entity(AdsorptionColumnBlockEntity::new)
        .register();

    /** M26 (I 3, O 2, FO 2, C 1 electrode, U 3; phase 1; multiblock 3 × 3 × 3 of refractory bricks). */
    public static final MachineDefinition ELECTRIC_ARC_FURNACE = MachineDefinition.builder("electric_arc_furnace")
        .name("电弧炉", "Electric Arc Furnace")
        .description("极高温碳热还原，电极放催化剂槽", "Extreme-heat reduction; electrodes in the catalyst slot")
        .tier(Tier.T3)
        .types(ProcessTypes.KILN)
        .layout(IoLayout.of(3, 2, 0, 2).withCatalyst(1).withUpgrades(3))
        .slotNames(SlotRole.CATALYST, "electrode")
        .capabilities(Capabilities.of("temperature.heated", "temperature.high", "temperature.extreme"))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.HEATING)
        .structure(room())
        .register();

    /**
     * M27 (I 1, O 1, FI 3, FO 3, C 1, U 3; phase 1; multiblock): a furnace of refractory bricks with 1–4
     * furnace tube segments standing on the middle of its top.
     */
    public static final MachineDefinition GAS_PHASE_REACTOR = MachineDefinition.builder("gas_phase_reactor")
        .name("气相反应炉", "Gas-Phase Reactor")
        .description("燃烧、气相催化、裂解、流化床，炉管越多裂解越足", "Burning, catalysis, cracking; tubes raise cracking yield")
        .tier(Tier.T3)
        .types(ProcessTypes.GAS_REACTING)
        .layout(IoLayout.of(1, 1, 3, 3).withCatalyst(1).withUpgrades(3))
        .capabilities(Capabilities.of("temperature.heated", "temperature.high"))
        .modes(MachineMode.of("burning", Capabilities.NONE), MachineMode.of("gas_catalysis", Capabilities.NONE),
            MachineMode.of(GasPhaseReactorBlockEntity.CRACKING, Capabilities.NONE), MachineMode.of("fluidized_bed", Capabilities.NONE))
        .airIntake()
        .power(PowerKind.ENERGY)
        .phase(TickPhase.HEATING)
        .structure(StructurePattern.builder()
            .layer("###", "###", "###")
            .layer("###", "# #", "#C#")
            .layer("###", "###", "###")
            .layer("   ", " T ", "   ").repeat(1, 4)
            .where('#', Lang.block("refractory_bricks"), true, REFRACTORY_BRICKS, MechanicalMachines.AIR_INTAKE)
            .where('T', Lang.block("furnace_tube"), false, FURNACE_TUBE)
            .build())
        .entity(GasPhaseReactorBlockEntity::new)
        .register();

    /** M28 (I 1, O 1, FI 1, U 3; phase 2; multiblock 3 × 3 × 4): heat-sensitive drying. */
    public static final MachineDefinition SPRAY_DRYER = MachineDefinition.builder("spray_dryer")
        .name("喷雾干燥塔", "Spray Drying Tower")
        .description("低温瞬时喷雾干燥，热敏物料用它", "Flash spray drying for heat-sensitive goods")
        .tier(Tier.T3)
        .types(ProcessTypes.DRYING)
        .layout(IoLayout.of(1, 1, 1, 0).withUpgrades(3))
        .capabilities(Capabilities.of("temperature.heated", "heat_sensitivity.sensitive", "heat_sensitivity.very_sensitive"))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REFINING)
        .structure(StructurePattern.builder()
            .layer("###", "###", "#C#")
            .layer("###", "# #", "###")
            .layer("###", "# #", "###")
            .layer("###", "###", "###")
            .where('#', Lang.block("chemical_tower_wall"), true, CHEMICAL_TOWER_WALL)
            .build())
        .register();

    private ChemicalMachines() {
    }

    /** The seven machines in M22–M28 order. */
    public static List<MachineDefinition> machines() {
        return List.of(DISTILLATION_COLUMN, HIGH_PRESSURE_REACTOR, ELECTROLYZER, ADSORPTION_COLUMN, ELECTRIC_ARC_FURNACE, GAS_PHASE_REACTOR,
            SPRAY_DRYER);
    }

    /** Block ids of the members. */
    static List<String> members() {
        return List.of("chemical_tower_wall", "refractory_bricks", "furnace_tube");
    }

    /** Loads the class (registration happens in the static fields). Called during mod construction. */
    public static void bootstrap() {
        machines();
        ChemicalData.bootstrap();
    }

    /** 3 × 3 × 3 of refractory bricks or ports, the centre free, the controller in the middle of the front. */
    private static StructurePattern room() {
        return StructurePattern.builder()
            .layer("###", "###", "###")
            .layer("###", "# #", "#C#")
            .layer("###", "###", "###")
            .where('#', Lang.block("refractory_bricks"), true, REFRACTORY_BRICKS)
            .build();
    }

    private static Supplier<CasingBlock> casing(String id, String zh, String en) {
        Supplier<CasingBlock> block = PlatformRegistry.block(id, CasingBlock::new,
            BlockBehaviour.Properties.of().strength(4.0F, 8.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
        Supplier<BlockItem> item = PlatformRegistry.blockItem(id, block, new Item.Properties());
        Lang.add(Lang.block(id), zh, en);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return block;
    }

    private static Supplier<Item> described(String id, int stack, String zh, String en, String descZh, String descEn) {
        Supplier<Item> item = PlatformRegistry.item(id, props -> new DescribedItem(props, id), new Item.Properties().stacksTo(stack));
        Lang.add(Lang.item(id), zh, en);
        Lang.add(Lang.description(id), descZh, descEn);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return item;
    }
}
