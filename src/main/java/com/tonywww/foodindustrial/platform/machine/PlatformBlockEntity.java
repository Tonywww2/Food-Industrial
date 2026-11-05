package com.tonywww.foodindustrial.platform.machine;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.platform.PlatformData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
//? if forge {
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
//?}
//? if >=26.1 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}

/**
 * Base class for every block entity of the mod. Exposes stable, version-independent hooks:
 * <ul>
 *   <li>{@link #saveData} / {@link #loadData} — persistent data (world save);</li>
 *   <li>{@link #saveSyncData} / {@link #loadSyncData} — data sent to clients on chunk load and
 *       {@link #syncToClient()};</li>
 *   <li>{@link #serverTick()} / {@link #clientTick()} — called every tick by the ticker that
 *       {@link PlatformEntityBlock} installs (phase filtering is up to the subclass);</li>
 *   <li>{@link #invalidateStorage()} — after the faces exposed by a
 *       {@link com.tonywww.foodindustrial.core.storage.StorageHost} change.</li>
 * </ul>
 * Subclasses never override the loader / version specific save, load and sync methods.
 */
public abstract class PlatformBlockEntity extends BlockEntity {
    /** Loader-specific capability state, managed by {@link PlatformMachineCapabilities}. */
    @Nullable
    Object capabilityState;
    /** Forge 1.20.1 only: the LazyOptionals handed out for {@link #capabilityState}'s adapters. */
    @Nullable
    Object[] forgeOptionals;
    @Nullable
    PlatformNeighborCache neighborCache;

    protected PlatformBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Writes persistent data. */
    protected void saveData(DataWriter out) {
    }

    /** Reads persistent data. Missing keys must fall back to defaults. */
    protected void loadData(DataReader in) {
    }

    /** Writes the data clients need (rendering, block state independent visuals). Default: nothing. */
    protected void saveSyncData(DataWriter out) {
    }

    /** Applies data written by {@link #saveSyncData} on the client. */
    protected void loadSyncData(DataReader in) {
    }

    /** Called every server tick. */
    public void serverTick() {
    }

    /** Called every client tick. */
    public void clientTick() {
    }

    /** Sends {@link #saveSyncData} to clients tracking this block. Call sparingly (state changes only). */
    protected final void syncToClient() {
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    /** Lazily created cache of the neighbouring blocks' item / fluid / energy access. */
    public final PlatformNeighborCache neighbors() {
        if (neighborCache == null) {
            neighborCache = new PlatformNeighborCache(this);
        }
        return neighborCache;
    }

    /** Re-reads the storage views of a {@link com.tonywww.foodindustrial.core.storage.StorageHost}. */
    public final void invalidateStorage() {
        PlatformMachineCapabilities.invalidate(this);
    }

    /**
     * Server only: a neighbouring block changed (block update, including redstone). Called by
     * {@link PlatformEntityBlock}.
     */
    protected void onNeighborBlockChanged() {
    }

    /** Server and client: the block entity joined a loaded level (placed or chunk loaded). */
    protected void onLoaded() {
    }

    /**
     * The block entity left the level.
     *
     * @param unloaded {@code true} when its chunk unloaded, {@code false} when the block was removed
     */
    protected void onRemoved(boolean unloaded) {
    }

    private boolean unloading;

    @Override
    public void onLoad() {
        super.onLoad();
        unloading = false;
        onLoaded();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unloading = true;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        onRemoved(unloading);
    }

    /**
     * Server only: the block is being removed or replaced by another block (not when its chunk
     * unloads, not for a state change of the same block). The block entity is still in the level:
     * drop the contents here.
     */
    protected void onBroken() {
    }

    /** Called by {@link PlatformEntityBlock} (up to 1.21.1) or {@link #preRemoveSideEffects} (26.1). */
    void broken() {
        onBroken();
    }

    /**
     * Server only: a neighbouring block entity changed (its contents, or it appeared or vanished), as
     * reported by the loader's neighbour notification.
     */
    protected void onNeighborStorageChanged() {
    }

    /** Called by {@link PlatformEntityBlock} when a neighbouring block entity changes. */
    void onNeighborChanged() {
        if (neighborCache != null) {
            neighborCache.onNeighborChanged();
        }
        if (level != null && !level.isClientSide()) {
            onNeighborStorageChanged();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //? if >=26.1 {
    /*@Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        broken();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        saveData(PlatformData.writer(output));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        loadData(PlatformData.reader(input));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return PlatformData.writeCompound(registries, this::saveSyncData);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        loadSyncData(PlatformData.reader(input));
    }

    @Override
    public void onDataPacket(Connection connection, ValueInput input) {
        loadSyncData(PlatformData.reader(input));
    }
    *///?}

    //? if >=1.21 && <26.1 {
    /*@Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveData(PlatformData.writer(tag, registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadData(PlatformData.reader(tag, registries));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return PlatformData.writeCompound(registries, this::saveSyncData);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadSyncData(PlatformData.reader(tag, registries));
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        loadSyncData(PlatformData.reader(packet.getTag(), registries));
    }
    *///?}

    //? if <1.21 {
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveData(PlatformData.writer(tag, null));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadData(PlatformData.reader(tag, null));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return PlatformData.writeCompound(null, this::saveSyncData);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        loadSyncData(PlatformData.reader(tag, null));
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            loadSyncData(PlatformData.reader(tag, null));
        }
    }
    //?}

    //? if forge {
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        LazyOptional<T> own = PlatformMachineCapabilities.forgeCapability(this, capability, side);
        return own != null ? own : super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        PlatformMachineCapabilities.invalidate(this);
    }
    //?}
}
