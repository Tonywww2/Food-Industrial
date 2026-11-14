package com.tonywww.foodindustrial.machine.bio;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.block.CasingBlock;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
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
 * The three T4 machines M29–M31 (I17, additives/06 §T4 生物 / 精细：操作与实现规格, machines/03 §3) with
 * the clean frame, the sterile vessel wall and the membrane module. Numbers, names and recipes are game
 * settings (pending review).
 */
public final class BioMachines {
    private static final String NS = "foodindustrial";
    private static final String AIR = MachineDefinition.AIR;

    // ---- members and items ----

    public static final Supplier<CasingBlock> STERILE_VESSEL_WALL = PlatformRegistry.block("sterile_vessel_wall", CasingBlock::new,
        BlockBehaviour.Properties.of().strength(4.0F, 8.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());

    public static final Supplier<Item> CLEAN_FRAME = described("clean_frame", 64, "洁净框架", "Clean Frame",
        "T4 机器共用的框架", "Shared frame of the T4 machines");
    /** Consumable of M31 in its catalyst slot (tag {@code foodindustrial:catalysts/membrane}). */
    public static final Supplier<Item> MEMBRANE_MODULE = described("membrane_module", 64, "膜组件", "Membrane Module",
        "膜分离机的耗材，放催化剂槽", "Consumable of the membrane separator");

    static {
        Supplier<BlockItem> wall = PlatformRegistry.blockItem("sterile_vessel_wall", STERILE_VESSEL_WALL, new Item.Properties());
        Lang.add(Lang.block("sterile_vessel_wall"), "无菌罐壁", "Sterile Vessel Wall");
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(wall.get())));
        SharedTags.item(NS + ":catalysts/membrane", NS + ":membrane_module");
    }

    // ---- machines ----

    /**
     * M29 (I 2, O 1, FI 3, FO 2, C 2 cultures, U 4; phase 3; multiblock 3 × 3, 1–4 vessel layers): sterile,
     * aerated through air intakes or sealed; one parallel copy per vessel layer.
     */
    public static final MachineDefinition STERILE_FERMENTER = MachineDefinition.builder("sterile_fermenter")
        .name("无菌发酵罐", "Aseptic Fermenter")
        .description("无菌深层发酵，罐体越高一次做得越多", "Aseptic fermentation; a taller vessel makes more")
        .tier(Tier.T4)
        .types(ProcessTypes.FERMENTING)
        .layout(IoLayout.of(2, 1, 3, 2).withCatalyst(2).withUpgrades(4))
        .slotNames(SlotRole.CATALYST, "culture", "culture")
        .capabilities(Capabilities.of("sterile.sterile"))
        .modes(MachineMode.of("aerated", Capabilities.NONE).needing(AIR),
            MachineMode.of("sealed", Capabilities.of("atmosphere.airtight")))
        .airIntake()
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REACTION)
        .structure(StructurePattern.builder()
            .layer("###", "###", "#C#")
            .layer("###", "# #", "###").repeat(1, 4)
            .layer("###", "###", "###")
            .where('#', Lang.block("sterile_vessel_wall"), true, STERILE_VESSEL_WALL, MechanicalMachines.AIR_INTAKE)
            .build())
        .entity(SterileFermenterBlockEntity::new)
        .register();

    /** M30 (I 4, O 2, U 4; phase 3): its own window with a culture plate (2026-11-11). */
    public static final MachineDefinition CULTURE_BENCH = MachineDefinition.builder(CultureBenchBlockEntity.ID)
        .name("菌种工作台", "Culture Bench")
        .description("平板分离、纯化、诱变、基因重组与扩培", "Isolates, purifies, mutates, engineers and grows cultures")
        .tier(Tier.T4)
        .types(ProcessTypes.CULTURING)
        .layout(IoLayout.of(4, 2, 0, 0).withUpgrades(4))
        .slotNames(SlotRole.INPUT, "sample", "medium", "donor", "vector")
        .slotFilters(SlotRole.INPUT, CultureBenchSlots.SAMPLES, CultureBenchSlots.MEDIA, CultureBenchSlots.DONORS, CultureBenchSlots.VECTORS)
        .capabilities(Capabilities.of("sterile.sterile"))
        .modes(MachineMode.of("isolation", Capabilities.NONE), MachineMode.of("purification", Capabilities.NONE),
            MachineMode.of("mutagenesis", Capabilities.NONE), MachineMode.of("recombination", Capabilities.NONE),
            MachineMode.of(CultureBenchBlockEntity.EXPANSION, Capabilities.NONE))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REACTION)
        .entity(CultureBenchBlockEntity::new)
        .register();

    /** M31 (FI 2, FO 2, C 1 membrane, U 4; phase 2). */
    public static final MachineDefinition MEMBRANE_SEPARATOR = MachineDefinition.builder("membrane_separator")
        .name("膜分离机", "Membrane Separator")
        .description("超滤、纳滤、反渗透与双极膜电渗析", "Ultrafiltration, nanofiltration, RO and electrodialysis")
        .tier(Tier.T4)
        .types(ProcessTypes.PURIFYING)
        .layout(IoLayout.of(0, 0, 2, 2).withCatalyst(1).withUpgrades(4))
        .slotNames(SlotRole.CATALYST, "membrane")
        .modes(MachineMode.of("ultrafiltration", Capabilities.of("method.membrane")),
            MachineMode.of("nanofiltration", Capabilities.of("method.membrane")),
            MachineMode.of("reverse_osmosis", Capabilities.of("method.membrane")),
            MachineMode.of("bipolar_electrodialysis", Capabilities.of("method.membrane", "method.electrodialysis")))
        .power(PowerKind.ENERGY)
        .phase(TickPhase.REFINING)
        .register();

    private BioMachines() {
    }

    /** The three machines in M29–M31 order. */
    public static List<MachineDefinition> machines() {
        return List.of(STERILE_FERMENTER, CULTURE_BENCH, MEMBRANE_SEPARATOR);
    }

    /** Loads the class (registration happens in the static fields). Called during mod construction. */
    public static void bootstrap() {
        machines();
        BioData.bootstrap();
        CultureBenchSlots.bootstrap();
    }

    private static Supplier<Item> described(String id, int stack, String zh, String en, String descZh, String descEn) {
        Supplier<Item> item = PlatformRegistry.item(id, props -> new DescribedItem(props, id), new Item.Properties().stacksTo(stack));
        Lang.add(Lang.item(id), zh, en);
        Lang.add(Lang.description(id), descZh, descEn);
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(item.get())));
        return item;
    }
}
