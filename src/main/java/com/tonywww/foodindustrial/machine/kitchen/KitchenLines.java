package com.tonywww.foodindustrial.machine.kitchen;

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
import com.tonywww.foodindustrial.machine.workshop.DescribedItem;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Supplier;

/**
 * The nine T2 multiblock lines of the finished-food line (I42, machines/05 §2.2, foods/05 §4.3): food machine
 * casing with ports set by direction, 16,000 mB tanks, FE. Production lines (M39, M43, M49, M53) are 3 × 3 in
 * section and five long like the rotary kiln; the others are 3 × 3 × 3. IDs, slot counts and recipes are drafted
 * in I42 for review.
 */
public final class KitchenLines {
    /** The stainless-steel casing all food lines share (foods/05 §4.3). */
    public static final Supplier<CasingBlock> CASING = casing("food_machine_casing", "食品机组外壳", "Food Machine Casing");

    /** M39's module: extrusion cooking needs the high temperature it gives (foods/07 §5, I38: puffing is molding with the extrusion die). */
    public static final MachineModule EXTRUSION_SECTION = module("extrusion_section", "挤压膨化段", "Extrusion Section",
        "和面成型机组模块：高温，可挤压膨化", "Forming line module: high temperature for extrusion puffing",
        "temperature.heated", "temperature.high");
    /** M40's module: opens UHT sterilizing. */
    public static final MachineModule UHT_SECTION = module("uht_section", "超高温段", "UHT Section",
        "均质杀菌机组模块：高温，开放超高温灭菌", "Homogenizer-sterilizer module: high temperature for UHT",
        "temperature.high");

    /** M39 (R20, all modes; I 9, O 2, FI 3, FO 1, A 8, C 1 mold, U 2, X 1 extrusion section; I47.5). */
    public static final MachineDefinition FORMING_LINE = MachineDefinition.builder("forming_line")
        .name("和面成型机组", "Forming Line")
        .description("和面、压延、切条与成型，可装挤压膨化段", "Mixing, sheeting, slitting, molding; extrusion section")
        .tier(Tier.T2)
        .types(ProcessTypes.FORMING)
        .layout(IoLayout.of(9, 2, 3, 1).withCatalyst(1).withUpgrades(2).withModules(1).withAdditives(8))
        .slotNames(SlotRole.CATALYST, "mold")
        .modes(MachineMode.of("dough_mixing", Capabilities.NONE), MachineMode.of("sheeting", Capabilities.NONE),
            MachineMode.of("slitting", Capabilities.NONE), MachineMode.of("molding", Capabilities.NONE))
        .modules(EXTRUSION_SECTION)
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .structure(line())
        .shaped()
        .register();

    /**
     * M40 (R19 high-pressure homogenizing; R21 pasteurizing, UHT with its section; I 2, O 1, FI 3, FO 2, U 2, X 1; I47.5:
     * no additive slots, homogenizing is a processing step, not an ingredient step).
     */
    public static final MachineDefinition HOMOGENIZING_STERILIZER = MachineDefinition.builder("homogenizing_sterilizer")
        .name("均质杀菌机组", "Homogenizer-Sterilizer")
        .description("高压均质与巴氏杀菌，装超高温段可超高温灭菌", "Homogenizing and pasteurizing; UHT with its section")
        .tier(Tier.T2)
        .types(ProcessTypes.MIXING, ProcessTypes.STERILIZING)
        .layout(IoLayout.of(2, 1, 3, 2).withUpgrades(2).withModules(1))
        .capabilities(Capabilities.of("temperature.heated"))
        .modes(MachineMode.of("high_pressure_homogenizing", Capabilities.NONE, ProcessTypes.MIXING),
            MachineMode.of("pasteurizing", Capabilities.NONE, ProcessTypes.STERILIZING),
            MachineMode.of("uht", Capabilities.NONE, ProcessTypes.STERILIZING).needing("temperature.high"))
        .modules(UHT_SECTION)
        .power(PowerKind.ENERGY)
        .phase(TickPhase.HEATING)
        .structure(cube())
        .shaped()
        .register();

    /** M43 (R23 baking, pan roasting; I 4, O 2, FI 1, FO 1, C 1 mold, U 2; I47.5). */
    public static final MachineDefinition BAKING_LINE = MachineDefinition.builder("baking_line")
        .name("烘烤机组", "Baking Line")
        .description("隧道炉烘烤与滚筒焙炒", "Tunnel-oven baking and drum roasting")
        .tier(Tier.T2)
        .types(ProcessTypes.BAKING)
        .layout(IoLayout.of(4, 2, 1, 1).withCatalyst(1).withUpgrades(2))
        .slotNames(SlotRole.CATALYST, "mold")
        .capabilities(Capabilities.of("temperature.heated"))
        .modes(MachineMode.of("baking", Capabilities.NONE), MachineMode.of("pan_roasting", Capabilities.NONE))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.HEATING)
        .structure(line())
        .shaped()
        .register();

    /** M44 (R22 boiling, steaming, braising; R24 frying; I 4, O 2, FI 3, FO 2, C 1, U 2; I47.5). */
    public static final MachineDefinition COOKER_FRYER = MachineDefinition.builder("cooker_fryer")
        .name("蒸煮油炸机组", "Cooker-Fryer")
        .description("煮、蒸、卤与连续油炸", "Boiling, steaming, braising and continuous frying")
        .tier(Tier.T2)
        .types(ProcessTypes.COOKING, ProcessTypes.FRYING)
        .layout(IoLayout.of(4, 2, 3, 2).withCatalyst(1).withUpgrades(2))
        .capabilities(Capabilities.of("temperature.heated"))
        .modes(MachineMode.of("water_boiling", Capabilities.NONE, ProcessTypes.COOKING),
            MachineMode.of("steaming", Capabilities.NONE, ProcessTypes.COOKING),
            MachineMode.of("braising", Capabilities.NONE, ProcessTypes.COOKING),
            MachineMode.of("frying", Capabilities.NONE, ProcessTypes.FRYING))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.HEATING)
        .structure(cube())
        .shaped()
        .register();

    /**
     * M45 (R25 proofing, marinating, aging; R16 only where the main product carries quality; I 6, O 2, FI 3, FO 2, C 2 — a culture
     * and a second catalyst, the oak barrel among them —, U 2; I47.5).
     */
    public static final MachineDefinition AGING_CELLAR = MachineDefinition.builder("aging_cellar")
        .name("发酵陈酿库", "Aging Cellar")
        .description("醒发、腌制、陈酿与成品发酵", "Proofing, marinating, aging and food fermentation")
        .tier(Tier.T2)
        .types(ProcessTypes.CURING, ProcessTypes.FERMENTING)
        .layout(IoLayout.of(6, 2, 3, 2).withCatalyst(2).withUpgrades(2))
        .slotNames(SlotRole.CATALYST, "culture")
        .modes(MachineMode.of("proofing", Capabilities.NONE, ProcessTypes.CURING),
            MachineMode.of("marinating", Capabilities.NONE, ProcessTypes.CURING),
            MachineMode.of("aging", Capabilities.NONE, ProcessTypes.CURING),
            MachineMode.of("open", Capabilities.of("atmosphere.air"), ProcessTypes.FERMENTING),
            MachineMode.of("sealed", Capabilities.of("atmosphere.airtight"), ProcessTypes.FERMENTING),
            MachineMode.of("anaerobic", Capabilities.of("atmosphere.airtight"), ProcessTypes.FERMENTING))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .entity(AgingCellarBlockEntity::new)
        .structure(cube())
        .shaped()
        .register();

    /** M47 (R26 cutting, chopping, stuffing; I 4, O 3, FI 1, FO 1, C 1, U 2; I47.5). */
    public static final MachineDefinition CUTTING_STATION = MachineDefinition.builder("cutting_station")
        .name("切配机组", "Cutting Station")
        .description("切片切丁、斩拌与灌肠", "Slicing and dicing, chopping and stuffing")
        .tier(Tier.T2)
        .types(ProcessTypes.CUTTING)
        .layout(IoLayout.of(4, 3, 1, 1).withCatalyst(1).withUpgrades(2))
        .slotNames(SlotRole.CATALYST, "tool")
        .modes(MachineMode.of("slicing", Capabilities.NONE), MachineMode.of("chopping", Capabilities.NONE),
            MachineMode.of("stuffing", Capabilities.NONE))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .structure(cube())
        .shaped()
        .register();

    /** M49 (R27 tempering, depositing, panning; heats and cools; I 4, O 2, FI 2, FO 1, C 1, U 2; I47.5). */
    public static final MachineDefinition CONFECTIONERY_LINE = MachineDefinition.builder("confectionery_line")
        .name("糖果巧克力机组", "Confectionery Line")
        .description("调温、浇注与包衣", "Tempering, depositing and panning")
        .tier(Tier.T2)
        .types(ProcessTypes.CONFECTIONERY)
        .layout(IoLayout.of(4, 2, 2, 1).withCatalyst(1).withUpgrades(2))
        .slotNames(SlotRole.CATALYST, "mold")
        .capabilities(Capabilities.of("temperature.heated", "temperature.cooling"))
        .modes(MachineMode.of("tempering", Capabilities.NONE), MachineMode.of("depositing", Capabilities.NONE),
            MachineMode.of("panning", Capabilities.NONE))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .structure(line())
        .shaped()
        .register();

    /** M51 (R28 chilling, quick freezing, churn freezing; I 3, O 2, FI 2, FO 1, C 1 mold, U 2; I47.5). */
    public static final MachineDefinition FREEZING_UNIT = MachineDefinition.builder("freezing_unit")
        .name("冷冻机组", "Freezing Unit")
        .description("冷却、速冻与凝冻", "Chilling, quick freezing and churn freezing")
        .tier(Tier.T2)
        .types(ProcessTypes.CHILLING)
        .layout(IoLayout.of(3, 2, 2, 1).withCatalyst(1).withUpgrades(2))
        .slotNames(SlotRole.CATALYST, "mold")
        .capabilities(Capabilities.of("temperature.cooling", "temperature.freezing"))
        .modes(MachineMode.of("chilling", Capabilities.NONE), MachineMode.of("quick_freezing", Capabilities.NONE),
            MachineMode.of("churn_freezing", Capabilities.NONE))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .structure(cube())
        .shaped()
        .register();

    /** M53 (R29; R21 in-container sterilizing under pressure, the retort; I 4, O 2, FI 3, FO 1, C 1, U 2; I47.5). */
    public static final MachineDefinition FILLING_RETORT_LINE = MachineDefinition.builder("filling_retort_line")
        .name("灌装灭菌机组", "Filling and Retort Line")
        .description("灌装封口与罐内加压灭菌", "Filling, sealing and retort sterilizing")
        .tier(Tier.T2)
        .types(ProcessTypes.PACKAGING, ProcessTypes.STERILIZING)
        .layout(IoLayout.of(4, 2, 3, 1).withCatalyst(1).withUpgrades(2))
        .capabilities(Capabilities.of("temperature.heated", "pressure.pressurized", "mode.retorting"))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.MECHANICAL)
        .structure(line())
        .shaped()
        .register();

    private KitchenLines() {
    }

    public static List<MachineDefinition> machines() {
        return List.of(FORMING_LINE, HOMOGENIZING_STERILIZER, BAKING_LINE, COOKER_FRYER, AGING_CELLAR, CUTTING_STATION,
            CONFECTIONERY_LINE, FREEZING_UNIT, FILLING_RETORT_LINE);
    }

    /** 3 × 3 in section, five long along the facing, hollow like the rotary kiln. */
    private static StructurePattern line() {
        return StructurePattern.builder()
            .layer("###", "###", "###", "###", "###")
            .layer("# #", "# #", "# #", "# #", "#C#")
            .layer("###", "###", "###", "###", "###")
            .where('#', Lang.block("food_machine_casing"), true, CASING)
            .build();
    }

    /** 3 × 3 × 3, hollow. */
    private static StructurePattern cube() {
        return StructurePattern.builder()
            .layer("###", "###", "###")
            .layer("###", "# #", "#C#")
            .layer("###", "###", "###")
            .where('#', Lang.block("food_machine_casing"), true, CASING)
            .build();
    }

    private static Supplier<CasingBlock> casing(String id, String zh, String en) {
        Supplier<CasingBlock> block = PlatformRegistry.block(id, CasingBlock::new,
            BlockBehaviour.Properties.of().strength(3.5F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
        Supplier<BlockItem> item = PlatformRegistry.blockItem(id, block, new Item.Properties());
        Lang.add(Lang.block(id), zh, en);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return block;
    }

    private static MachineModule module(String id, String zh, String en, String descZh, String descEn, String... capabilities) {
        Supplier<Item> item = PlatformRegistry.item(id, props -> new DescribedItem(props, id), new Item.Properties().stacksTo(1));
        Lang.add(Lang.item(id), zh, en);
        Lang.add(Lang.description(id), descZh, descEn);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return MachineModule.register(new MachineModule(id, Capabilities.of(capabilities), 1000), item);
    }
}
