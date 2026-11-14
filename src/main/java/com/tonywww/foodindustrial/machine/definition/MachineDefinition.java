package com.tonywww.foodindustrial.machine.definition;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.block.MachineBlock;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.machine.PlatformMachineCapabilities;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * One machine, declared in one place (machines/02 §4.2): tier, recipe types, slot layout, profile,
 * modes, modules, power, scheduling phase and, for multiblocks, the structure. {@link Builder#register}
 * creates the block, its item, the block entity type, the language entries and the device entry the
 * recipe framework uses. No food concepts here (machines/02 §8): names describe the process.
 */
public final class MachineDefinition {
    private static final Map<String, MachineDefinition> BY_ID = new LinkedHashMap<>();

    private final String id;
    private final Tier tier;
    private final List<ProcessType> types;
    private final IoLayout layout;
    private final DeviceProfile profile;
    private final List<MachineMode> modes;
    private final List<MachineModule> modules;
    private final Capabilities capabilities;
    private final PowerKind power;
    private final long energyCapacity;
    private final int tankCapacity;
    private final TickPhase phase;
    @Nullable
    private final StructurePattern structure;
    private final List<MachineRequirement> requirements;
    private final boolean watchesNeighbours;
    @Nullable
    private final Climate climate;
    private final boolean airIntake;
    @Nullable
    private final GasSupply gas;
    private final int heightBase;
    private final int heightPermille;
    @Nullable
    private final EntityFactory entityFactory;
    private final Map<SlotRole, List<String>> slotNames;
    private final Map<SlotRole, List<String>> slotFilters;
    private final boolean described;
    private final DeviceSpec spec;
    private final boolean development;

    private Supplier<MachineBlock> block;
    private Supplier<? extends BlockItem> item;
    private Supplier<BlockEntityType<MachineBlockEntity>> blockEntityType;

    private MachineDefinition(Builder builder) {
        this.id = builder.id;
        this.tier = builder.tier;
        this.types = List.copyOf(builder.types);
        this.layout = builder.layout;
        this.profile = builder.profile != null ? builder.profile : DeviceProfile.standard(builder.tier);
        this.modes = List.copyOf(builder.modes);
        this.modules = List.copyOf(builder.modules);
        this.capabilities = builder.capabilities;
        this.power = builder.power;
        this.energyCapacity = builder.energyCapacity > 0 ? builder.energyCapacity : defaultEnergy(builder.tier);
        this.tankCapacity = builder.tankCapacity > 0 ? builder.tankCapacity
            : builder.structure != null ? MULTIBLOCK_TANK : defaultTank(builder.tier);
        this.phase = builder.phase;
        this.structure = builder.structure;
        this.requirements = List.copyOf(builder.requirements);
        this.watchesNeighbours = requirements.stream().anyMatch(MachineRequirement::watchesNeighbours);
        this.climate = builder.climate;
        this.airIntake = builder.airIntake;
        this.gas = builder.gas;
        this.heightBase = builder.heightBase;
        this.heightPermille = builder.heightPermille;
        this.entityFactory = builder.entityFactory;
        this.slotNames = Map.copyOf(builder.slotNames);
        this.slotFilters = Map.copyOf(builder.slotFilters);
        this.described = builder.descriptionZh != null;
        this.development = builder.development;
        Capabilities all = capabilities;
        for (MachineMode mode : modes) {
            all = all.with(mode.possibleCapabilities());
        }
        for (MachineModule module : modules) {
            all = all.with(module.capabilities());
        }
        if (airIntake) {
            all = all.with(Capabilities.of(AIR));
        }
        if (gas != null) {
            all = all.with(Capabilities.of(gas.capability()));
        }
        // one configuration per mode: what the machine has, that mode's capabilities, every module, its air and gas
        Capabilities fixed = capabilities;
        for (MachineModule module : modules) {
            fixed = fixed.with(module.capabilities());
        }
        if (airIntake) {
            fixed = fixed.with(Capabilities.of(AIR));
        }
        if (gas != null) {
            fixed = fixed.with(Capabilities.of(gas.capability()));
        }
        List<DeviceSpec.Configuration> configurations = new java.util.ArrayList<>();
        for (MachineMode mode : modes) {
            configurations.add(new DeviceSpec.Configuration(fixed.with(mode.possibleCapabilities()), Set.copyOf(mode.types())));
        }
        this.spec = new DeviceSpec(FoodIndustrial.MOD_ID + ":" + id, profile, Set.copyOf(types), all, layout.deviceSlots(), false, true, null,
            false, configurations);
    }

    /** Capability of a machine that breathes air (machines/03 §1). */
    public static final String AIR = "atmosphere.air";
    /** Tank of a multiblock controller, every tier (machines/03 §2, 2026-11-11). */
    public static final int MULTIBLOCK_TANK = 16_000;

    /**
     * Creates the block entity of a machine with its own class (M21, M22, M25, M27, M29, M30; machines/02
     * §4); a multiblock's class extends {@link MultiblockControllerBlockEntity}.
     */
    @FunctionalInterface
    public interface EntityFactory {
        MachineBlockEntity create(MachineDefinition definition, BlockPos pos, BlockState state);
    }

    /** Energy buffer by tier (machines/03 §2). */
    private static long defaultEnergy(Tier tier) {
        return switch (tier) {
            case T0, T1 -> 0;
            case T2 -> 20_000;
            case T3 -> 100_000;
            case T4 -> 200_000;
        };
    }

    /** Single tank by tier (machines/03 §2). */
    private static int defaultTank(Tier tier) {
        return switch (tier) {
            case T0, T1 -> 4_000;
            case T2 -> 8_000;
            case T3, T4 -> 16_000;
        };
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    @Nullable
    public static MachineDefinition byId(String id) {
        return BY_ID.get(id);
    }

    public static List<MachineDefinition> all() {
        return List.copyOf(BY_ID.values());
    }

    public String id() {
        return id;
    }

    public Tier tier() {
        return tier;
    }

    public List<ProcessType> types() {
        return types;
    }

    public IoLayout layout() {
        return layout;
    }

    public DeviceProfile profile() {
        return profile;
    }

    public List<MachineMode> modes() {
        return modes;
    }

    public List<MachineModule> modules() {
        return modules;
    }

    /** Capabilities the machine always has (modes and modules add more). */
    public Capabilities capabilities() {
        return capabilities;
    }

    public PowerKind power() {
        return power;
    }

    public long energyCapacity() {
        return energyCapacity;
    }

    public int tankCapacity() {
        return tankCapacity;
    }

    public TickPhase phase() {
        return phase;
    }

    @Nullable
    public StructurePattern structure() {
        return structure;
    }

    public boolean isMultiblock() {
        return structure != null;
    }

    /** Translation key of the first requirement {@code machine} lacks now, or {@code null} (I14). */
    @Nullable
    public String lacking(MachineBlockEntity machine) {
        // called every batch: an indexed loop, no iterator (machines/02 §6.3 rule 10; I27 found the
        // iterator in a JFR allocation sample on 1.20.1, 2026-11-11)
        for (int i = 0; i < requirements.size(); i++) {
            String key = requirements.get(i).lacking(machine);
            if (key != null) {
                return key;
            }
        }
        return null;
    }

    /** Whether a neighbouring block change can change what the machine lacks. */
    public boolean watchesNeighbours() {
        return watchesNeighbours;
    }

    /**
     * Whether the machine breathes air (machines/03 §1, 2026-11-11): a single block through its back
     * face, a multiblock through its air intakes; it has {@link #AIR} while one is open.
     */
    public boolean airIntake() {
        return airIntake;
    }

    /** Gas taken from the input tanks for a capability (M14 nitrogen), or {@code null}. */
    @Nullable
    public GasSupply gas() {
        return gas;
    }

    /**
     * Speed of a multiblock of {@code layers} layers (M19, 2026-11-11), per mille: each layer above the
     * base adds {@code heightPermille}; 1000 without a height bonus.
     */
    public int heightSpeedPermille(int layers) {
        return heightPermille == 0 ? 1000 : 1000 + heightPermille * Math.max(0, layers - heightBase);
    }

    /**
     * Name of the {@code index}-th slot of {@code role} shown when hovering it (2026-11-11): the
     * machine's own word ({@code gui.foodindustrial.slot.<word>}) or the role's name.
     */
    public String slotName(SlotRole role, int index) {
        List<String> names = slotNames.get(role);
        String word = names != null && index < names.size() ? names.get(index) : role.name().toLowerCase(Locale.ROOT);
        return "gui." + FoodIndustrial.MOD_ID + ".slot." + word;
    }

    /**
     * Item tags the slots of each role accept (2026-11-11, M30): slot {@code i} of the role takes only
     * items in the {@code i}-th tag; roles and slots past the list take anything.
     */
    public Map<SlotRole, List<String>> slotFilters() {
        return slotFilters;
    }

    /** Whether the {@code index}-th slot of {@code role} accepts {@code stack} ({@link #slotFilters()}). */
    public boolean slotAccepts(SlotRole role, int index, ItemStack stack) {
        List<String> tags = slotFilters.get(role);
        return tags == null || index >= tags.size() || stack.is(PlatformTags.itemTag(tags.get(index)));
    }

    /** Whether the machine has a Shift description ({@code tooltip.foodindustrial.<id>.desc}). */
    public boolean described() {
        return described;
    }

    /** Climate of a fermentation room (M06), or {@code null}. */
    @Nullable
    public Climate climate() {
        return climate;
    }

    /** Development-only test machine: no generated assets, no creative tab entry. */
    public boolean development() {
        return development;
    }

    /** The device entry of the recipe framework. */
    public DeviceSpec spec() {
        return spec;
    }

    public MachineBlock block() {
        return block.get();
    }

    public Item item() {
        return item.get();
    }

    public BlockEntityType<MachineBlockEntity> blockEntityType() {
        return blockEntityType.get();
    }

    /** Builder; {@link #register} registers everything during mod construction. */
    public static final class Builder {
        private final String id;
        private Tier tier = Tier.T1;
        private final Set<ProcessType> types = new LinkedHashSet<>();
        private IoLayout layout = IoLayout.of(1, 1, 0, 0);
        @Nullable
        private DeviceProfile profile;
        private final List<MachineMode> modes = new ArrayList<>();
        private final List<MachineModule> modules = new ArrayList<>();
        private Capabilities capabilities = Capabilities.NONE;
        private PowerKind power = PowerKind.NONE;
        private long energyCapacity;
        private int tankCapacity;
        private TickPhase phase = TickPhase.MECHANICAL;
        @Nullable
        private StructurePattern structure;
        private final List<MachineRequirement> requirements = new ArrayList<>();
        @Nullable
        private Climate climate;
        private boolean airIntake;
        @Nullable
        private GasSupply gas;
        private int heightBase;
        private int heightPermille;
        @Nullable
        private EntityFactory entityFactory;
        private final Map<SlotRole, List<String>> slotNames = new EnumMap<>(SlotRole.class);
        private final Map<SlotRole, List<String>> slotFilters = new EnumMap<>(SlotRole.class);
        private String nameZh;
        private String nameEn;
        @Nullable
        private String descriptionZh;
        private String descriptionEn;
        private BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().strength(3.5F).requiresCorrectToolForDrops();
        private boolean development;

        private Builder(String id) {
            this.id = id;
        }

        /** Block name (text standard §3): Chinese and English. */
        public Builder name(String zh, String en) {
            this.nameZh = zh;
            this.nameEn = en;
            return this;
        }

        /** Shift description (text standard §3.2): at most 24 Han characters, no full stop. */
        public Builder description(String zh, String en) {
            this.descriptionZh = zh;
            this.descriptionEn = en;
            return this;
        }

        /** Something the machine needs besides a recipe to run (I14). */
        public Builder requires(MachineRequirement... values) {
            requirements.addAll(List.of(values));
            return this;
        }

        /** The machine breathes air (back face, or air intakes of a multiblock). */
        /**
         * Names of the slots of {@code role} in order (words of {@code gui.foodindustrial.slot.*});
         * slots past the list keep the role's name.
         */
        public Builder slotNames(SlotRole role, String... words) {
            this.slotNames.put(role, List.of(words));
            return this;
        }

        /** Item tags (full ids) the slots of {@code role} accept, in slot order ({@link MachineDefinition#slotFilters()}). */
        public Builder slotFilters(SlotRole role, String... tags) {
            this.slotFilters.put(role, List.of(tags));
            return this;
        }

        public Builder airIntake() {
            this.airIntake = true;
            return this;
        }

        /** A gas from the input tanks gives a capability (needs a fluid input). */
        public Builder gas(GasSupply value) {
            this.gas = value;
            return this;
        }

        /** Multiblock of variable height: each layer above {@code baseLayers} adds {@code permille} speed. */
        public Builder heightBonus(int baseLayers, int permille) {
            this.heightBase = baseLayers;
            this.heightPermille = permille;
            return this;
        }

        /** Its own block entity class (M21 …); a multiblock's extends the controller. */
        public Builder entity(EntityFactory value) {
            this.entityFactory = value;
            return this;
        }

        /** Climate of a fermentation room: needs a fuel slot and a fluid input. */
        public Builder climate(Climate value) {
            this.climate = value;
            return this;
        }

        public Builder tier(Tier value) {
            this.tier = value;
            return this;
        }

        public Builder types(ProcessType... values) {
            types.addAll(List.of(values));
            return this;
        }

        public Builder layout(IoLayout value) {
            this.layout = value;
            return this;
        }

        /** Profile other than {@link DeviceProfile#standard} of the tier. */
        public Builder profile(DeviceProfile value) {
            this.profile = value;
            return this;
        }

        public Builder modes(MachineMode... values) {
            modes.addAll(List.of(values));
            return this;
        }

        /** Modules the module slots accept. */
        public Builder modules(MachineModule... values) {
            modules.addAll(List.of(values));
            return this;
        }

        public Builder capabilities(Capabilities value) {
            this.capabilities = value;
            return this;
        }

        public Builder power(PowerKind value) {
            this.power = value;
            return this;
        }

        public Builder energyCapacity(long value) {
            this.energyCapacity = value;
            return this;
        }

        public Builder tankCapacity(int value) {
            this.tankCapacity = value;
            return this;
        }

        public Builder phase(TickPhase value) {
            this.phase = value;
            return this;
        }

        public Builder structure(StructurePattern value) {
            this.structure = value;
            return this;
        }

        public Builder properties(BlockBehaviour.Properties value) {
            this.properties = value;
            return this;
        }

        /** Marks a development-only test machine. */
        public Builder development() {
            this.development = true;
            return this;
        }

        /** Registers the machine. Call during mod construction. */
        public MachineDefinition register() {
            if (types.isEmpty() || nameZh == null) {
                throw new IllegalStateException("Machine " + id + " needs recipe types and a name");
            }
            if (power == PowerKind.FUEL && layout.fuel() == 0) {
                throw new IllegalStateException("Machine " + id + " burns fuel but has no fuel slot");
            }
            if (layout.fuel() > 0 && power != PowerKind.FUEL && power != PowerKind.NONE) {
                throw new IllegalStateException("Machine " + id + " has a fuel slot but runs on " + power);
            }
            if (gas != null && layout.fluidIn() == 0) {
                throw new IllegalStateException("Machine " + id + " takes a gas but has no fluid input");
            }
            if (climate != null && (layout.fuel() == 0 || layout.fluidIn() == 0)) {
                throw new IllegalStateException("Machine " + id + " has a climate but no fuel slot or fluid input");
            }
            MachineDefinition definition = new MachineDefinition(this);
            if (BY_ID.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate machine " + id);
            }
            definition.block = PlatformRegistry.block(id, props -> new MachineBlock(props, definition), properties);
            definition.item = PlatformRegistry.blockItem(id, definition.block, com.tonywww.foodindustrial.machine.block.MachineBlockItem::new, new Item.Properties());
            definition.blockEntityType = PlatformRegistry.blockEntity(id, (pos, state) -> definition.entityFactory != null
                ? definition.entityFactory.create(definition, pos, state)
                : definition.isMultiblock() ? new MultiblockControllerBlockEntity(definition, pos, state)
                : new ProcessingMachineBlockEntity(definition, pos, state), definition.block);
            PlatformMachineCapabilities.register(definition.blockEntityType);
            DeviceSpec.register(definition.spec);
            if (!development) {
                Lang.add(Lang.block(id), nameZh, nameEn);
                if (descriptionZh != null) {
                    Lang.add(Lang.description(id), descriptionZh, descriptionEn);
                }
                ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(definition.item.get())));
            }
            return definition;
        }
    }
}
