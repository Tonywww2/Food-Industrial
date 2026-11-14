package com.tonywww.foodindustrial.platform.compat.jade;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.gui.MachineReadout;
import com.tonywww.foodindustrial.machine.block.MachineBlock;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.manual.T0DeviceBlock;
import com.tonywww.foodindustrial.manual.T0DeviceBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformData;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ProgressView;
import snownee.jade.api.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
//? if forge {
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//?}
//? if >=26.1 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

/**
 * Jade plugin (2026-11-11, user): machines and T0 devices show their state and the conditions their
 * recipes care about ({@link MachineReadout}), and the recipe progress as Jade progress bars. Items,
 * fluids and energy come from Jade's own storage providers through the machines' capabilities.
 * <p>
 * Jade finds this class by its annotation and loads it only when Jade is installed; nothing else names it.
 * Jade 11 (1.20.1), 15 (1.21.1) and 26.1 differ in the progress data type and in identifiers.
 */
@WailaPlugin(FoodIndustrial.MOD_ID)
public class FoodIndustrialJadePlugin implements IWailaPlugin {
    private static final String DATA = FoodIndustrial.MOD_ID;

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(Data.MACHINE, MachineBlockEntity.class);
        registration.registerBlockDataProvider(Data.DEVICE, T0DeviceBlockEntity.class);
        registration.registerProgress(Progress.INSTANCE, MachineBlockEntity.class);
        registration.registerProgress(Progress.INSTANCE, T0DeviceBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(State.MACHINE, MachineBlock.class);
        registration.registerBlockComponent(State.DEVICE, T0DeviceBlock.class);
        registration.registerProgressClient(Progress.INSTANCE);
    }

    /**
     * The server data of a machine or a device. A separate object from {@link State} under the same id:
     * Jade for 1.21.6 on refuses a data provider that is also a component provider.
     */
    private enum Data implements IServerDataProvider<BlockAccessor> {
        MACHINE("machine"),
        DEVICE("device");

        private final String path;

        Data(String path) {
            this.path = path;
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            BlockEntity blockEntity = accessor.getBlockEntity();
            if (blockEntity != null && MachineReadout.shows(blockEntity)) {
                data.put(DATA, PlatformData.writeCompound(accessor.getLevel().registryAccess(), out -> MachineReadout.write(blockEntity, out)));
            }
        }

        //? if >=26.1 {
        /*@Override
        public Identifier getUid() {
            return PlatformIdentifier.id(path);
        }
        *///?} else {
        @Override
        public ResourceLocation getUid() {
            return PlatformIdentifier.id(path);
        }
        //?}
    }

    /** The lines of a machine or a device, drawn from {@link Data}. */
    private enum State implements IBlockComponentProvider {
        MACHINE("machine"),
        DEVICE("device");

        private final String path;

        State(String path) {
            this.path = path;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            BlockEntity blockEntity = accessor.getBlockEntity();
            CompoundTag data = accessor.getServerData();
            if (blockEntity == null || !data.contains(DATA)) {
                return;
            }
            DataReader in = PlatformData.readCompound(accessor.getLevel().registryAccess(), compound(data, DATA));
            for (MachineReadout.Line line : MachineReadout.lines(blockEntity, in)) {
                tooltip.add(styled(line));
            }
        }

        //? if >=26.1 {
        /*@Override
        public Identifier getUid() {
            return PlatformIdentifier.id(path);
        }
        *///?} else {
        @Override
        public ResourceLocation getUid() {
            return PlatformIdentifier.id(path);
        }
        //?}
    }

    /** Progress bars: the server sends each bar's fraction with its text data, the client sets the text. */
    //? if forge {
    private enum Progress implements IServerExtensionProvider<BlockEntity, CompoundTag>, IClientExtensionProvider<CompoundTag, ProgressView> {
        INSTANCE;

        @Override
        public List<ViewGroup<CompoundTag>> getGroups(ServerPlayer player, ServerLevel level, BlockEntity target, boolean showDetails) {
            List<ViewGroup<CompoundTag>> groups = new ArrayList<>();
            for (MachineReadout.Bar bar : MachineReadout.bars(target)) {
                groups.add(group(ProgressView.create(bar.fraction()), bar, level));
            }
            return groups;
        }

        @Override
        public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
            return ClientViewGroup.map(groups, ProgressView::read, (group, client) -> text(group, client, accessor.getLevel()));
        }

        @Override
        public ResourceLocation getUid() {
            return PlatformIdentifier.id("progress");
        }
    }
    //?} else if <26.1 {
    /*private enum Progress implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, ProgressView> {
        INSTANCE;

        @Override
        public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
            List<ViewGroup<CompoundTag>> groups = new ArrayList<>();
            if (accessor.getTarget() instanceof BlockEntity target) {
                for (MachineReadout.Bar bar : MachineReadout.bars(target)) {
                    groups.add(group(ProgressView.create(bar.fraction()), bar, accessor.getLevel()));
                }
            }
            return groups;
        }

        @Override
        public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
            return ClientViewGroup.map(groups, ProgressView::read, (group, client) -> text(group, client, accessor.getLevel()));
        }

        @Override
        public ResourceLocation getUid() {
            return PlatformIdentifier.id("progress");
        }
    }
    *///?} else {
    /*private enum Progress implements IServerExtensionProvider<ProgressView.Data>, IClientExtensionProvider<ProgressView.Data, ProgressView> {
        INSTANCE;

        @Override
        public List<ViewGroup<ProgressView.Data>> getGroups(Accessor<?> accessor) {
            List<ViewGroup<ProgressView.Data>> groups = new ArrayList<>();
            if (accessor.getTarget() instanceof BlockEntity target) {
                for (MachineReadout.Bar bar : MachineReadout.bars(target)) {
                    groups.add(group(new ProgressView.Data(bar.fraction()), bar, accessor.getLevel()));
                }
            }
            return groups;
        }

        @Override
        public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ProgressView.Data>> groups) {
            return ClientViewGroup.map(groups, ProgressView::read, (group, client) -> text(group, client, accessor.getLevel()));
        }

        @Override
        public Identifier getUid() {
            return PlatformIdentifier.id("progress");
        }
    }
    *///?}

    /** One bar as its own group, its text data in the group's extra data. */
    private static <T> ViewGroup<T> group(T view, MachineReadout.Bar bar, Level level) {
        ViewGroup<T> group = new ViewGroup<>(new ArrayList<>(List.of(view)));
        group.getExtraData().put(DATA, PlatformData.writeCompound(level.registryAccess(), bar::write));
        return group;
    }

    private static <T> void text(ViewGroup<T> group, ClientViewGroup<ProgressView> client, Level level) {
        CompoundTag extra = group.getExtraData();
        if (!extra.contains(DATA) || client.views.isEmpty()) {
            return;
        }
        MachineReadout.Bar bar = MachineReadout.Bar.read(PlatformData.readCompound(level.registryAccess(), compound(extra, DATA)));
        client.views.get(0).text = bar.text();
    }

    private static CompoundTag compound(CompoundTag tag, String key) {
        //? if >=26.1 {
        /*return tag.getCompoundOrEmpty(key);
        *///?} else {
        return tag.getCompound(key);
        //?}
    }

    private static Component styled(MachineReadout.Line line) {
        IThemeHelper theme = IThemeHelper.get();
        return switch (line.tone()) {
            case GOOD -> theme.success(line.text());
            case INFO -> theme.info(line.text());
            case WARNING -> theme.warning(line.text());
            case BAD -> theme.danger(line.text());
            case NORMAL -> line.text();
        };
    }
}
