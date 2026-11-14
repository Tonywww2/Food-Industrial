package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
//? if forge {
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//?} else {
/*import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
*///?}
//? if neoforge && <26.1 {
/*import net.minecraft.core.HolderLookup;
*///?}
//? if >=26.1 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}

/**
 * Data kept with each player and saved with the player (I35): a Forge capability on 1.20.1, a NeoForge data
 * attachment from 1.21 on. The data is not copied to the new player entity after death; listen to
 * {@link PlatformPlayerEvents#onClone} and use {@link Key#copy} where the business wants it kept. Other clones
 * (returning from the End) keep the data.
 */
public final class PlatformPlayerData {
    private static final Map<String, Key<?>> KEYS = new LinkedHashMap<>();

    private PlatformPlayerData() {
    }

    /** Contents kept with a player. */
    public interface Stored {
        void save(DataWriter out);

        void load(DataReader in);
    }

    /** Access to one kind of player data. */
    public static final class Key<T extends Stored> {
        private final String name;
        private final Supplier<T> factory;

        private Key(String name, Supplier<T> factory) {
            this.name = name;
            this.factory = factory;
        }

        /** The player's data (created on first access). Server and client players hold separate data. */
        @SuppressWarnings("unchecked")
        public T get(Player player) {
            return (T) holder(player).values.computeIfAbsent(name, key -> factory.get());
        }

        /** Copies the data of {@code from} into {@code to} (both server players), through save and load. */
        public void copy(Player from, Player to) {
            T source = get(from);
            CompoundTag tag = PlatformData.writeCompound(from.level().registryAccess(), source::save);
            T target = factory.get();
            target.load(PlatformData.readCompound(to.level().registryAccess(), tag));
            holder(to).values.put(name, target);
        }

        /** Replaces the player's data with a fresh instance. */
        public T reset(Player player) {
            T fresh = factory.get();
            holder(player).values.put(name, fresh);
            return fresh;
        }
    }

    /**
     * Registers a kind of player data. Call during mod construction.
     *
     * @param name    key of the data in the player's save, unique within the mod
     * @param factory a new player's data
     */
    public static <T extends Stored> Key<T> register(String name, Supplier<T> factory) {
        Key<T> key = new Key<>(name, factory);
        if (KEYS.putIfAbsent(name, key) != null) {
            throw new IllegalStateException("Player data " + name + " registered twice");
        }
        return key;
    }

    /** Every kind of player data of one player. */
    //? if forge {
    @AutoRegisterCapability
    //?}
    static final class Holder {
        final Map<String, Stored> values = new LinkedHashMap<>();

        void save(DataWriter out) {
            values.forEach((name, value) -> value.save(out.child(name)));
        }

        void load(DataReader in) {
            values.clear();
            for (Key<?> key : KEYS.values()) {
                Stored value = key.factory.get();
                value.load(in.child(key.name));
                values.put(key.name, value);
            }
        }
    }

    //? if forge {
    private static final Capability<Holder> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private static Holder holder(Player player) {
        return player.getCapability(CAPABILITY).orElseThrow(() -> new IllegalStateException("Player data missing on " + player));
    }

    /** Called by the mod entrypoint. */
    static void bind() {
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, (AttachCapabilitiesEvent<Entity> event) -> {
            if (event.getObject() instanceof Player) {
                event.addCapability(PlatformIdentifier.id(FoodIndustrial.MOD_ID, "player_data"), new Provider());
            }
        });
    }

    /** Opens a dead player's capabilities for {@link Key#copy} during a clone. */
    static void revive(Player original) {
        original.reviveCaps();
    }

    /** Closes them again. */
    static void invalidate(Player original) {
        original.invalidateCaps();
    }

    private static final class Provider implements ICapabilitySerializable<CompoundTag> {
        private final Holder holder = new Holder();
        private final LazyOptional<Holder> optional = LazyOptional.of(() -> holder);

        @Override
        public @NotNull <C> LazyOptional<C> getCapability(@NotNull Capability<C> capability, @Nullable Direction side) {
            return capability == CAPABILITY ? optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            holder.save(PlatformData.writer(tag, null));
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            holder.load(PlatformData.reader(tag, null));
        }
    }
    //?} else {
    /*private static Supplier<AttachmentType<Holder>> type;

    private static Holder holder(Player player) {
        return player.getData(type.get());
    }

    /^* Called by the mod entrypoint. ^/
    static void bind() {
        type = PlatformRegistry.register(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "player_data",
            () -> AttachmentType.builder(Holder::new).serialize(new Serializer()).build());
    }

    static void revive(Player original) {
    }

    static void invalidate(Player original) {
    }
    *///?}

    //? if neoforge && <26.1 {
    /*private static final class Serializer implements IAttachmentSerializer<CompoundTag, Holder> {
        @Override
        public Holder read(IAttachmentHolder owner, CompoundTag tag, HolderLookup.Provider registries) {
            Holder holder = new Holder();
            holder.load(PlatformData.reader(tag, registries));
            return holder;
        }

        @Override
        public CompoundTag write(Holder holder, HolderLookup.Provider registries) {
            CompoundTag tag = new CompoundTag();
            holder.save(PlatformData.writer(tag, registries));
            return tag;
        }
    }
    *///?}

    //? if >=26.1 {
    /*private static final class Serializer implements IAttachmentSerializer<Holder> {
        @Override
        public Holder read(IAttachmentHolder owner, ValueInput input) {
            Holder holder = new Holder();
            holder.load(PlatformData.reader(input));
            return holder;
        }

        @Override
        public boolean write(Holder holder, ValueOutput output) {
            holder.save(PlatformData.writer(output));
            return true;
        }
    }
    *///?}

    /** Copies every kind of data (a clone that is not a death, on loaders that do not copy it themselves). */
    static void copyAll(Player from, Player to) {
        //? if forge {
        for (Key<?> key : KEYS.values()) {
            key.copy(from, to);
        }
        //?}
    }
}
