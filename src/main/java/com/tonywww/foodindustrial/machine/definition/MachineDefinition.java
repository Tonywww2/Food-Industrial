package com.tonywww.foodindustrial.machine.definition;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.block.MachineBlock;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.machine.PlatformMachineCapabilities;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
        this.tankCapacity = builder.tankCapacity > 0 ? builder.tankCapacity : defaultTank(builder.tier);
        this.phase = builder.phase;
        this.structure = builder.structure;
        this.development = builder.development;
        Capabilities all = capabilities;
        for (MachineMode mode : modes) {
            all = all.with(mode.capabilities());
        }
        for (MachineModule module : modules) {
            all = all.with(module.capabilities());
        }
        this.spec = new DeviceSpec(FoodIndustrial.MOD_ID + ":" + id, profile, Set.copyOf(types), all, layout.deviceSlots(), false, true, null);
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
        private String nameZh;
        private String nameEn;
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
            MachineDefinition definition = new MachineDefinition(this);
            if (BY_ID.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate machine " + id);
            }
            definition.block = PlatformRegistry.block(id, props -> new MachineBlock(props, definition), properties);
            definition.item = PlatformRegistry.blockItem(id, definition.block, com.tonywww.foodindustrial.machine.block.MachineBlockItem::new, new Item.Properties());
            definition.blockEntityType = PlatformRegistry.blockEntity(id, (pos, state) -> definition.isMultiblock()
                ? new MultiblockControllerBlockEntity(definition, pos, state)
                : new ProcessingMachineBlockEntity(definition, pos, state), definition.block);
            PlatformMachineCapabilities.register(definition.blockEntityType);
            DeviceSpec.register(definition.spec);
            if (!development) {
                Lang.add(Lang.block(id), nameZh, nameEn);
                ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(definition.item.get())));
            }
            return definition;
        }
    }
}
