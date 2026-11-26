package com.tonywww.foodindustrial.machine.kitchen;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.TickPhase;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.registry.SharedTags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

/**
 * The seven T1 machines of the finished-food line (I41, machines/05 §2.2, foods/07 §5): single blocks, hand-cranked
 * or fuel-fired. IDs, names, slot counts beyond the design tables and recipes are drafted in I41 for review.
 */
public final class KitchenMachines {
    private static final String NS = FoodIndustrial.MOD_ID;
    /** Ice for M50 (I41): ice, packed ice, blue ice. */
    public static final String ICE = NS + ":coolants/ice";
    /** Salt for M50's freezing: the salt chemical. */
    public static final String SALT = NS + ":chemicals/salt";
    /** Blocks under M48 that give it cooling (I41): stone like a marble slab. */
    public static final String COOLING_STONE = NS + ":cooling_stone";

    static {
        for (String ice : new String[]{"minecraft:ice", "minecraft:packed_ice", "minecraft:blue_ice"}) {
            SharedTags.item(ICE, ice);
        }
        for (String stone : new String[]{"#minecraft:base_stone_overworld", "minecraft:smooth_stone", "minecraft:stone_bricks",
            "minecraft:polished_andesite", "minecraft:polished_diorite", "minecraft:polished_granite", "minecraft:calcite",
            "minecraft:quartz_block", "minecraft:smooth_quartz"}) {
            SharedTags.block(COOLING_STONE, stone);
        }
    }

    /** M38 (machines/03 §3, I47.5: I 9, O 2, FI 3, FO 1, A 8, C 1 mold; hand crank). */
    public static final MachineDefinition NOODLE_PRESS = MachineDefinition.builder("noodle_press")
        .name("手摇压面机", "Noodle Press")
        .description("人力和面、压延、切条与成型", "Hand-cranked dough mixing, sheeting, slitting, molding")
        .tier(Tier.T1)
        .types(ProcessTypes.FORMING)
        .layout(IoLayout.of(9, 2, 3, 1).withCatalyst(1).withAdditives(8))
        .slotNames(SlotRole.CATALYST, "mold")
        .modes(MachineMode.of("dough_mixing", Capabilities.NONE), MachineMode.of("sheeting", Capabilities.NONE),
            MachineMode.of("slitting", Capabilities.NONE), MachineMode.of("molding", Capabilities.NONE))
        .power(PowerKind.MANUAL)
        .phase(TickPhase.MECHANICAL)
        .shaped()
        .register();

    /** M41 (I 9, O 2, FI 3, FO 1, A 8; hand crank; I47.5; C 1 for brewer's yeast from I49). */
    public static final MachineDefinition MIXING_CHURN = MachineDefinition.builder("mixing_churn")
        .name("手摇搅拌桶", "Mixing Churn")
        .description("人力调配、搅打与搅乳", "Hand-cranked blending, whipping and churning")
        .tier(Tier.T1)
        .types(ProcessTypes.MIXING)
        .layout(IoLayout.of(9, 2, 3, 1).withCatalyst(1).withAdditives(8))
        .slotNames(SlotRole.CATALYST, "culture")
        .modes(MachineMode.of("blending", Capabilities.NONE), MachineMode.of("whipping", Capabilities.NONE),
            MachineMode.of("churning", Capabilities.NONE))
        .power(PowerKind.MANUAL)
        .phase(TickPhase.MECHANICAL)
        .properties(wood())
        .shaped()
        .register();

    /** M42 (I 4, O 2, FI 1, F 1, C 1 mold; fuel; I47.5). */
    public static final MachineDefinition BRICK_OVEN = MachineDefinition.builder("brick_oven")
        .name("砖烤炉", "Brick Oven")
        .description("烧燃料烘烤与焙炒", "Fuel-fired baking and pan roasting")
        .tier(Tier.T1)
        .types(ProcessTypes.BAKING)
        .layout(IoLayout.of(4, 2, 1, 0).withCatalyst(1).withFuel(1))
        .slotNames(SlotRole.CATALYST, "mold")
        .capabilities(Capabilities.of("temperature.heated"))
        .modes(MachineMode.of("baking", Capabilities.NONE), MachineMode.of("pan_roasting", Capabilities.NONE))
        .power(PowerKind.FUEL)
        .phase(TickPhase.HEATING)
        .shaped()
        .register();

    /** M46 (I 4, O 2, FI 1, C 1 knife or stuffing nozzle; hand crank; I47.5). */
    public static final MachineDefinition MEAT_GRINDER = MachineDefinition.builder("meat_grinder")
        .name("手摇绞肉机", "Meat Grinder")
        .description("人力切割、斩拌与灌肠", "Hand-cranked cutting, chopping and stuffing")
        .tier(Tier.T1)
        .types(ProcessTypes.CUTTING)
        .layout(IoLayout.of(4, 2, 1, 0).withCatalyst(1))
        .slotNames(SlotRole.CATALYST, "tool")
        .modes(MachineMode.of("slicing", Capabilities.NONE), MachineMode.of("chopping", Capabilities.NONE),
            MachineMode.of("stuffing", Capabilities.NONE))
        .power(PowerKind.MANUAL)
        .phase(TickPhase.MECHANICAL)
        .shaped()
        .register();

    /** M48 (I 4, O 2, FI 2, F 1, C 1 mold; fuel heats, a stone block underneath cools; I47.5). */
    public static final MachineDefinition CANDY_TABLE = MachineDefinition.builder("candy_table")
        .name("糖果台", "Candy Table")
        .description("烧燃料加热，放在石块上才能冷却", "Fuel heats; cools only when set on stone")
        .tier(Tier.T1)
        .types(ProcessTypes.CONFECTIONERY)
        .layout(IoLayout.of(4, 2, 2, 0).withCatalyst(1).withFuel(1))
        .slotNames(SlotRole.CATALYST, "mold")
        .capabilities(Capabilities.of("temperature.heated"))
        .modes(MachineMode.of("tempering", Capabilities.NONE), MachineMode.of("depositing", Capabilities.NONE),
            MachineMode.of("panning", Capabilities.NONE))
        .power(PowerKind.FUEL)
        .phase(TickPhase.HEATING)
        .mayHave(Capabilities.of("temperature.cooling"))
        .entity(CandyTableBlockEntity::new)
        .shaped()
        .register();

    /**
     * M50 (I 3, O 2, FI 2, F 2 ice and salt, C 1 mold; hand crank; I47.5): the F slots hold the coolant, used up batch by batch,
     * not fuel (machines/03 §3).
     */
    public static final MachineDefinition ICE_SALT_FREEZER = MachineDefinition.builder("ice_salt_freezer")
        .name("冰盐冷冻桶", "Ice-Salt Freezer")
        .description("人力冷却，加冰与盐才能冷冻", "Hand-cranked chilling; ice and salt to freeze")
        .tier(Tier.T1)
        .types(ProcessTypes.CHILLING)
        .layout(IoLayout.of(3, 2, 2, 0).withCatalyst(1).withFuel(2))
        .slotNames(SlotRole.FUEL, "ice", "salt")
        .slotNames(SlotRole.CATALYST, "mold")
        .slotFilters(SlotRole.FUEL, ICE, SALT)
        .coolant()
        .modes(MachineMode.of("chilling", Capabilities.NONE), MachineMode.of("quick_freezing", Capabilities.NONE),
            MachineMode.of("churn_freezing", Capabilities.NONE))
        .power(PowerKind.MANUAL)
        .phase(TickPhase.MECHANICAL)
        .mayHave(Capabilities.of("temperature.cooling", "temperature.freezing"))
        .entity(IceSaltFreezerBlockEntity::new)
        .properties(wood())
        .shaped()
        .register();

    /** M52 (I 4 — product, container, lid —, O 2, FI 2, C 1; hand crank; I47.5). */
    public static final MachineDefinition FILLING_SEALER = MachineDefinition.builder("filling_sealer")
        .name("手摇灌装封口机", "Filling Sealer")
        .description("人力灌装与封口", "Hand-cranked filling and sealing")
        .tier(Tier.T1)
        .types(ProcessTypes.PACKAGING)
        .layout(IoLayout.of(4, 2, 2, 0).withCatalyst(1))
        .power(PowerKind.MANUAL)
        .phase(TickPhase.MECHANICAL)
        .shaped()
        .register();

    private KitchenMachines() {
    }

    /** The seven machines in M38–M52 order. */
    public static List<MachineDefinition> machines() {
        return List.of(NOODLE_PRESS, MIXING_CHURN, BRICK_OVEN, MEAT_GRINDER, CANDY_TABLE, ICE_SALT_FREEZER, FILLING_SEALER);
    }

    /** Machines built of wood (also mined with an axe). */
    static List<MachineDefinition> wooden() {
        return List.of(MIXING_CHURN, ICE_SALT_FREEZER);
    }

    /** Loads the class (registration happens in the static fields). Called during mod construction. */
    public static void bootstrap() {
        machines();
        KitchenLines.machines();
        KitchenData.bootstrap();
    }

    private static BlockBehaviour.Properties wood() {
        return BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD);
    }
}
