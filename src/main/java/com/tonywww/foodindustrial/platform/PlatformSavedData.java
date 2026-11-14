package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.function.Supplier;
//? if >=26.1 {
/*import com.mojang.serialization.Codec;
import net.minecraft.world.level.saveddata.SavedDataType;
*///?}

/**
 * Data saved with a level ({@code data/<name>.dat} of the dimension), written through
 * {@link DataWriter} / {@link DataReader} on every node.
 */
public final class PlatformSavedData {
    private PlatformSavedData() {
    }

    /** Contents kept with a level. */
    public interface Stored {
        void save(DataWriter out);

        void load(DataReader in);
    }

    /** A loaded value; call {@link #setDirty} after changing it. */
    public static final class Handle<T extends Stored> extends SavedData {
        private final T value;
        private final HolderLookup.Provider registries;

        Handle(T value, HolderLookup.Provider registries) {
            this.value = value;
            this.registries = registries;
        }

        public T value() {
            return value;
        }

        //? if <1.21 {
        @Override
        public CompoundTag save(CompoundTag tag) {
            return write(tag);
        }
        //?}

        //? if >=1.21 && <26.1 {
        /*@Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
            return write(tag);
        }
        *///?}

        CompoundTag write(CompoundTag tag) {
            tag.merge(PlatformData.writeCompound(registries, value::save));
            return tag;
        }
    }

    private static <T extends Stored> Handle<T> read(Supplier<T> factory, CompoundTag tag, HolderLookup.Provider registries) {
        T value = factory.get();
        value.load(PlatformData.readCompound(registries, tag));
        return new Handle<>(value, registries);
    }

    /** The data named {@code name} (a lower-case id path such as {@code foodindustrial_harvest}) of the level. */
    public static <T extends Stored> Handle<T> get(ServerLevel level, String name, Supplier<T> factory) {
        HolderLookup.Provider registries = level.registryAccess();
        //? if >=26.1 {
        /*Codec<Handle<T>> codec = CompoundTag.CODEC.xmap(tag -> read(factory, tag, registries), handle -> handle.write(new CompoundTag()));
        SavedDataType<Handle<T>> type = new SavedDataType<>(PlatformIdentifier.parse("foodindustrial:" + name),
            () -> new Handle<>(factory.get(), registries), codec);
        return level.getDataStorage().computeIfAbsent(type);
        *///?}
        //? if >=1.21 && <26.1 {
        /*return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(() -> new Handle<>(factory.get(), registries),
            (tag, lookup) -> read(factory, tag, registries), null), name);
        *///?}
        //? if <1.21 {
        return level.getDataStorage().computeIfAbsent(tag -> read(factory, tag, registries), () -> new Handle<>(factory.get(), registries), name);
        //?}
    }
}
