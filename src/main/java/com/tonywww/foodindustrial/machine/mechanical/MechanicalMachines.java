package com.tonywww.foodindustrial.machine.mechanical;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.block.AirIntakeBlock;
import com.tonywww.foodindustrial.machine.block.CasingBlock;
import com.tonywww.foodindustrial.machine.definition.GasSupply;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.MachineModule;
import com.tonywww.foodindustrial.machine.definition.MachineRequirement;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.StructurePattern;
import com.tonywww.foodindustrial.machine.definition.TickPhase;
import com.tonywww.foodindustrial.machine.definition.Upgrade;
import com.tonywww.foodindustrial.machine.workshop.DescribedBlockItem;
import com.tonywww.foodindustrial.machine.workshop.DescribedItem;
import com.tonywww.foodindustrial.machine.workshop.WorkshopMachines;
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
 * The nine T2 machines M11, M12, M14, M15, M17–M21 (I15, additives/06 §T2 机械化：操作与实现规格,
 * machines/03 §3) with the upgrades, the modules, the machine frame, the two casings and the air
 * intake. Every machine runs on FE; slots, phases, modes and capabilities follow the design tables;
 * numbers, names and recipes are game settings (pending review).
 */
public final class MechanicalMachines {
    private static final String AIR = MachineDefinition.AIR;

    // ---- members ----

    public static final Supplier<CasingBlock> TOWER_WALL = casing("tower_wall", "塔壁", "Tower Wall");
    public static final Supplier<CasingBlock> KILN_SHELL = casing("kiln_shell", "窑筒壳", "Kiln Shell");
    public static final Supplier<AirIntakeBlock> AIR_INTAKE = PlatformRegistry.block("air_intake", AirIntakeBlock::new, metal());

    public static final Supplier<Item> MACHINE_FRAME = described("machine_frame", 64, "机械框架", "Machine Frame",
        "T2 机器共用的框架", "Shared frame of the T2 machines");

    // ---- upgrades (machines/01 §1 rule 4, §1.2; 2026-11-11) ----

    public static final Supplier<Item> SPEED_UPGRADE = upgrade("speed_upgrade", new Upgrade(1500, 2000, 0), "速率升级", "Speed Upgrade",
        "每个速率 ×1.5，能耗 ×2", "Each: speed ×1.5, energy use ×2");
    public static final Supplier<Item> EFFICIENCY_UPGRADE = upgrade("efficiency_upgrade", new Upgrade(1000, 700, 0), "能效升级",
        "Efficiency Upgrade", "每个能耗 ×0.7", "Each: energy use ×0.7");
    public static final Supplier<Item> PARALLEL_UPGRADE = upgrade("parallel_upgrade", new Upgrade(1000, 1000, 1), "并行升级", "Parallel Upgrade",
        "每个多处理一份，输入要够", "Each runs one more copy when the inputs allow");
    public static final Supplier<Item> STANDARDIZATION_MODULE = upgrade("standardization_module", Upgrade.standardisation(), "规范化模块",
        "Standardization Module", "产出纯度降到所在档位的下限", "Lowers products to their grade's lower bound");

    // ---- modules (machines/01 §4.3) ----

    public static final MachineModule FILTER_PLATES = module("filter_plates", 1000, "滤板组", "Filter Plates",
        "固液分离机模块：过滤", "Separator module: filtration", "method.filtration");
    public static final MachineModule CENTRIFUGE_DRUM = module("centrifuge_drum", 1000, "离心转鼓", "Centrifuge Drum",
        "固液分离机模块：离心", "Separator module: centrifugation", "method.centrifuge");
    public static final MachineModule CORROSION_LINING = module("corrosion_lining", 1000, "耐腐蚀内衬", "Corrosion-Resistant Lining",
        "搅拌反应罐模块：耐腐蚀", "Reactor module: corrosion resistant", "method.corrosion_resistant");
    /** One item for M14 and M15 (2026-11-05); gives both vacuum words of the vocabulary. */
    public static final MachineModule VACUUM_PUMP = module("vacuum_pump", 1000, "真空泵", "Vacuum Pump",
        "反应罐、蒸发结晶器模块：真空", "Reactor and evaporator module: vacuum", "pressure.vacuum", "method.vacuum");
    public static final MachineModule CRYO_JACKET = module("cryo_jacket", 1000, "低温夹套", "Cryogenic Jacket",
        "搅拌反应罐模块：冷冻", "Reactor module: freezing", "temperature.freezing");
    public static final MachineModule UV_LAMP = module("uv_lamp", 1000, "紫外灯", "UV Lamp",
        "搅拌反应罐模块：紫外", "Reactor module: ultraviolet", "method.ultraviolet");
    public static final MachineModule MULTI_EFFECT = module("multi_effect", 500, "多效蒸发器", "Multiple-Effect Evaporator",
        "蒸发结晶器模块：能耗减半", "Evaporator module: half energy use");
    public static final MachineModule CHILL_ROLL = module("chill_roll", 1000, "冷却辊", "Chill Roll",
        "干燥机模块：冷却，熔体急冷成片", "Dryer module: cooling, flakes melts", "temperature.cooling");
    public static final MachineModule FREEZE_DRYER = module("freeze_dryer", 1000, "冷冻干燥单元", "Freeze-Drying Unit",
        "干燥机模块：冷冻、真空，可干热敏物料", "Dryer module: freezing and vacuum for heat-sensitive goods",
        "temperature.freezing", "pressure.vacuum", "method.vacuum", "heat_sensitivity.sensitive", "heat_sensitivity.very_sensitive");
    public static final MachineModule SUPERCRITICAL_UNIT = module("supercritical_unit", 1000, "超临界单元", "Supercritical Unit",
        "萃取罐模块：超临界、高压", "Extraction tank module: supercritical, high pressure", "method.supercritical", "pressure.high");
    public static final MachineModule LEAD_CHAMBER = module("lead_chamber", 1000, "铅室衬里", "Lead Chamber Lining",
        "吸收塔模块：铅室法", "Absorption tower module: lead chamber process", "method.lead_chamber");
    public static final MachineModule EMULSIFYING_HEAD = module("emulsifying_head", 1000, "高剪切乳化头", "High-Shear Emulsifying Head",
        "调配机模块：乳化", "Mixer module: emulsifying", "method.emulsifying");
    public static final MachineModule INTERNAL_MIXER = module("internal_mixer", 1000, "密炼转子", "Internal Mixer Rotors",
        "调配机模块：密炼", "Mixer module: internal mixing", "method.internal_mixing");

    static {
        describedBlockItem("air_intake", AIR_INTAKE, "进气口", "Air Intake",
            "多方块外壳位置可用，朝外一面通空气即可进气", "Replaces casing; lets air in through an open outer side");
    }

    // ---- machines ----

    /** M11 (machines/03 §3: I 1, O 2, U 2; phase 0); the I09 sample made a real machine. */
    public static final MachineDefinition CRUSHER = MachineDefinition.builder("crusher")
        .name("粉碎机", "Crusher")
        .description("电动研磨，比石磨快、出得多", "Electric grinding, faster and richer than the stone mill")
        .tier(Tier.T2)
        .types(ProcessTypes.GRINDING)
        .layout(IoLayout.of(1, 2, 0, 0).withUpgrades(2))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .register();

    /** M12 (I 1, O 2, FI 1, FO 2, U 2, X 2; phase 0): pressing and separation; filtering and spinning by module. */
    public static final MachineDefinition SOLID_LIQUID_SEPARATOR = MachineDefinition.builder("solid_liquid_separator")
        .name("固液分离机", "Solid-Liquid Separator")
        .description("压榨与固液分离，模块可过滤、离心", "Pressing and separating; modules add filters and spinning")
        .tier(Tier.T2)
        .types(ProcessTypes.PRESSING, ProcessTypes.SEPARATING)
        .layout(IoLayout.of(1, 2, 1, 2).withUpgrades(2).withModules(2))
        .modules(FILTER_PLATES, CENTRIFUGE_DRUM)
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .register();

    /**
     * M14 (I 3, O 2, FI 6, FO 3, C 1, U 2, X 3; phase 3; six input tanks from I31, 2026-11-11; three output tanks, 2026-11-12): heated or cooled, air from the back, inert while
     * an input tank holds nitrogen (10 mB per 40 running ticks).
     */
    public static final MachineDefinition STIRRED_REACTOR = MachineDefinition.builder("stirred_reactor")
        .name("搅拌反应罐", "Stirred Reactor")
        .description("液相反应，可加热冷却、通氮，背面进气", "Liquid reactions; heats, cools, takes nitrogen and air")
        .tier(Tier.T2)
        .types(ProcessTypes.REACTING)
        .layout(IoLayout.of(3, 2, 6, 3).withCatalyst(1).withUpgrades(2).withModules(3))
        .capabilities(Capabilities.of("temperature.heated", "temperature.cooling"))
        .modes(MachineMode.of("reaction", Capabilities.NONE),
            MachineMode.of("polymerization", Capabilities.of("method.polymerization")))
        .modules(CORROSION_LINING, VACUUM_PUMP, CRYO_JACKET, UV_LAMP)
        .airIntake()
        .gas(new GasSupply("atmosphere.inert", "foodindustrial:nitrogen", 10, 40))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REACTION)
        .register();

    /** M15 (I 1, O 2, FI 2, FO 2, U 2, X 2; phase 2): five modes over boiling (R05) and crystallizing (R06). */
    public static final MachineDefinition EVAPORATING_CRYSTALLIZER = MachineDefinition.builder("evaporating_crystallizer")
        .name("蒸发结晶器", "Evaporating Crystallizer")
        .description("浓缩与结晶，可装真空、多效模块", "Concentrating and crystallizing; vacuum and multi-effect")
        .tier(Tier.T2)
        .types(ProcessTypes.BOILING, ProcessTypes.CRYSTALLIZING)
        .layout(IoLayout.of(1, 2, 2, 2).withUpgrades(2).withModules(2))
        .modes(MachineMode.of("concentration", Capabilities.of("temperature.heated"), ProcessTypes.BOILING),
            MachineMode.of("evaporative_crystallization", Capabilities.of("temperature.heated"), ProcessTypes.CRYSTALLIZING),
            MachineMode.of("cooling_crystallization", Capabilities.of("temperature.cooling"), ProcessTypes.CRYSTALLIZING),
            MachineMode.of("isoelectric", Capabilities.NONE, ProcessTypes.CRYSTALLIZING),
            MachineMode.of("freezing", Capabilities.of("temperature.freezing"), ProcessTypes.CRYSTALLIZING))
        .modules(VACUUM_PUMP, MULTI_EFFECT)
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REFINING)
        .register();

    /** M17 (I 2, O 2, FI 1, U 2, X 2; phase 2): drum or flash drying; flash drying breathes through the back. */
    public static final MachineDefinition DRYER = MachineDefinition.builder("dryer")
        .name("干燥机", "Dryer")
        .description("滚筒或气流干燥，气流要背面进气", "Drum or flash drying; flash drying needs air from behind")
        .tier(Tier.T2)
        .types(ProcessTypes.DRYING)
        .layout(IoLayout.of(2, 2, 1, 0).withUpgrades(2).withModules(2))
        .capabilities(Capabilities.of("temperature.heated"))
        .modes(MachineMode.of("drum_drying", Capabilities.NONE),
            MachineMode.of("flash_drying", Capabilities.NONE).needing(AIR))
        .modules(CHILL_ROLL, FREEZE_DRYER)
        .airIntake()
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REFINING)
        .register();

    /** M18 (I 2, O 2, FI 2, FO 2, U 2, X 1; phase 0): solvent recovery needs a condenser on top. */
    public static final MachineDefinition EXTRACTION_TANK = MachineDefinition.builder("extraction_tank")
        .name("萃取罐", "Extraction Tank")
        .description("溶剂浸提与萃取，顶上装冷凝器回收溶剂", "Solvent extraction; a condenser on top recovers solvent")
        .tier(Tier.T2)
        .types(ProcessTypes.EXTRACTING)
        .layout(IoLayout.of(2, 2, 2, 2).withUpgrades(2).withModules(1))
        .capabilities(Capabilities.of("method.solvent_recovery", "temperature.heated"))
        .modes(MachineMode.of("solvent_extraction", Capabilities.NONE), MachineMode.of("liquid_extraction", Capabilities.NONE))
        .modules(SUPERCRITICAL_UNIT)
        .requires(MachineRequirement.blockAbove(WorkshopMachines.CONDENSER, Lang.block("condenser")))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .register();

    /**
     * M19 (I 1, O 1, FI 2, FO 2, U 2, X 1; phase 0; multiblock): a 3 × 3 tower of 4–8 layers, the
     * controller in the middle of the bottom front; every layer above four adds 25% speed (2026-11-11).
     */
    public static final MachineDefinition ABSORPTION_TOWER = MachineDefinition.builder("absorption_tower")
        .name("吸收塔", "Absorption Tower")
        .description("气体吸收与碳化，塔高 4–8 层，越高越快", "Gas absorption and carbonation; 4–8 layers, taller is faster")
        .tier(Tier.T2)
        .types(ProcessTypes.ABSORBING)
        .layout(IoLayout.of(1, 1, 2, 2).withUpgrades(2).withModules(1))
        .modes(MachineMode.of("absorption", Capabilities.NONE), MachineMode.of("carbonation", Capabilities.NONE),
            MachineMode.of("combustion", Capabilities.NONE).needing(AIR))
        .modules(LEAD_CHAMBER)
        .airIntake()
        .heightBonus(4, 250)
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .structure(StructurePattern.builder()
            .layer("###", "###", "#C#")
            .layer("###", "# #", "###").repeat(2, 6)
            .layer("###", "###", "###")
            .where('#', Lang.block("tower_wall"), true, TOWER_WALL, AIR_INTAKE)
            .build())
        .register();

    /**
     * M20 (I 3, O 3, FO 1, U 2, X 1; phase 1; multiblock): a 3 × 3 tube five blocks long with a hollow
     * centre line, the controller in the middle of its front end.
     */
    public static final MachineDefinition ROTARY_KILN = MachineDefinition.builder("rotary_kiln")
        .name("回转窑", "Rotary Kiln")
        .description("煅烧、焙烧、熔融、活化等窑炉处理", "Calcining, roasting, melting, activation and more")
        .tier(Tier.T2)
        .types(ProcessTypes.KILN)
        .layout(IoLayout.of(3, 3, 0, 1).withUpgrades(2).withModules(1))
        .capabilities(Capabilities.of("temperature.heated", "temperature.high"))
        .modes(MachineMode.of("calcining", Capabilities.NONE).needing(AIR),
            MachineMode.of("roasting", Capabilities.NONE).needing(AIR),
            MachineMode.of("condensation", Capabilities.NONE),
            MachineMode.of("melting", Capabilities.NONE),
            MachineMode.of("alkali_fusion", Capabilities.NONE),
            MachineMode.of("activation", Capabilities.NONE),
            MachineMode.of("chlorination", Capabilities.NONE),
            // I31 (P104): oxides reduced by coke below their melting point, air kept out
            MachineMode.of("reduction", Capabilities.NONE))
        .airIntake()
        .power(PowerKind.ENERGY)
        .phase(TickPhase.HEATING)
        .structure(StructurePattern.builder()
            .layer("###", "###", "###", "###", "###")
            .layer("# #", "# #", "# #", "# #", "#C#")
            .layer("###", "###", "###", "###", "###")
            .where('#', Lang.block("kiln_shell"), true, KILN_SHELL, AIR_INTAKE)
            .build())
        .register();

    /** M21 (I 6, O 2, FI 3, FO 1, U 2, X 1; phase 0; three input tanks from I31): blending recipes, homogenizing and normalizing jobs. */
    public static final MachineDefinition MIXER = MachineDefinition.builder("mixer")
        .name("调配机", "Mixer")
        .description("配料；均质纯度；他模组物品归一", "Blending; evens out purity; converts other mods' items")
        .tier(Tier.T2)
        .types(ProcessTypes.MIXING)
        .layout(IoLayout.of(6, 2, 3, 1).withUpgrades(2).withModules(1))
        .modes(MachineMode.of("blending", Capabilities.NONE),
            MachineMode.of(MixerBlockEntity.HOMOGENIZING, Capabilities.NONE),
            MachineMode.of(MixerBlockEntity.NORMALIZING, Capabilities.NONE))
        .modules(EMULSIFYING_HEAD, INTERNAL_MIXER)
        .entity(MixerBlockEntity::new)
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .register();

    private MechanicalMachines() {
    }

    /** The nine machines in M11–M21 order. */
    public static List<MachineDefinition> machines() {
        return List.of(CRUSHER, SOLID_LIQUID_SEPARATOR, STIRRED_REACTOR, EVAPORATING_CRYSTALLIZER, DRYER, EXTRACTION_TANK,
            ABSORPTION_TOWER, ROTARY_KILN, MIXER);
    }

    /** Ids of the upgrade items. */
    static List<String> upgrades() {
        return List.of("speed_upgrade", "efficiency_upgrade", "parallel_upgrade", "standardization_module");
    }

    /** Ids of the module items, T2 first. */
    static List<String> modules() {
        return List.of("filter_plates", "centrifuge_drum", "corrosion_lining", "vacuum_pump", "chill_roll", "lead_chamber",
            "emulsifying_head", "cryo_jacket", "uv_lamp", "multi_effect", "freeze_dryer", "supercritical_unit", "internal_mixer");
    }

    /** Casing block ids. */
    static List<String> casings() {
        return List.of("tower_wall", "kiln_shell");
    }

    /** Loads the class (registration happens in the static fields). Called during mod construction. */
    public static void bootstrap() {
        machines();
        MechanicalData.bootstrap();
    }

    private static BlockBehaviour.Properties metal() {
        return BlockBehaviour.Properties.of().strength(3.5F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops();
    }

    private static Supplier<CasingBlock> casing(String id, String zh, String en) {
        Supplier<CasingBlock> block = PlatformRegistry.block(id, CasingBlock::new, metal());
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

    private static Supplier<Item> upgrade(String id, Upgrade effect, String zh, String en, String descZh, String descEn) {
        Supplier<Item> item = described(id, 16, zh, en, descZh, descEn);
        Upgrade.register(item, effect);
        return item;
    }

    private static MachineModule module(String id, int energyPermille, String zh, String en, String descZh, String descEn, String... capabilities) {
        Supplier<Item> item = described(id, 1, zh, en, descZh, descEn);
        return MachineModule.register(new MachineModule(id, capabilities.length == 0 ? Capabilities.NONE : Capabilities.of(capabilities), energyPermille), item);
    }

    private static void describedBlockItem(String id, Supplier<? extends Block> block, String zh, String en, String descZh, String descEn) {
        Supplier<DescribedBlockItem> item = PlatformRegistry.blockItem(id, block, (b, props) -> new DescribedBlockItem(b, props, id),
            new Item.Properties());
        Lang.add(Lang.block(id), zh, en);
        Lang.add(Lang.description(id), descZh, descEn);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
    }
}
