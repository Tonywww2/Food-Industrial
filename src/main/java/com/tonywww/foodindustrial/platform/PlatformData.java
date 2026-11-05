package com.tonywww.foodindustrial.platform;

import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//? if >=26.1 {
/*import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?} else {
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
//?}
//? if >=1.21 && <26.1 {
/*import net.minecraft.resources.RegistryOps;
*///?}

/**
 * {@link DataWriter} / {@link DataReader} implementations: {@code CompoundTag} before 26.1,
 * {@code ValueOutput} / {@code ValueInput} from 26.1 on.
 */
public final class PlatformData {
    private PlatformData() {
    }

    /**
     * Writes into a fresh compound. {@code registries} may be {@code null} on 1.20.1 and is
     * required from 1.21 on for registry-aware codecs and item stacks.
     */
    public static CompoundTag writeCompound(@Nullable HolderLookup.Provider registries, java.util.function.Consumer<DataWriter> body) {
        //? if >=26.1 {
        /*TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, requireRegistries(registries));
        body.accept(new ValueWriter(output));
        return output.buildResult();
        *///?} else {
        CompoundTag tag = new CompoundTag();
        body.accept(new TagWriter(tag, registries));
        return tag;
        //?}
    }

    /** Reads a compound produced by {@link #writeCompound}. */
    public static DataReader readCompound(@Nullable HolderLookup.Provider registries, CompoundTag tag) {
        //? if >=26.1 {
        /*return new ValueReader(TagValueInput.create(ProblemReporter.DISCARDING, requireRegistries(registries), tag));
        *///?} else {
        return new TagReader(tag, registries);
        //?}
    }

    private static HolderLookup.Provider requireRegistries(@Nullable HolderLookup.Provider registries) {
        if (registries == null) {
            throw new IllegalStateException("Registry access is required on this Minecraft version");
        }
        return registries;
    }

    //? if >=26.1 {
    /*public static DataWriter writer(ValueOutput output) {
        return new ValueWriter(output);
    }

    public static DataReader reader(ValueInput input) {
        return new ValueReader(input);
    }

    private record ValueWriter(ValueOutput out) implements DataWriter {
        @Override public void putBoolean(String key, boolean value) { out.putBoolean(key, value); }
        @Override public void putByte(String key, byte value) { out.putByte(key, value); }
        @Override public void putInt(String key, int value) { out.putInt(key, value); }
        @Override public void putLong(String key, long value) { out.putLong(key, value); }
        @Override public void putString(String key, String value) { out.putString(key, value); }
        @Override public void putIntArray(String key, int[] value) { out.putIntArray(key, value); }

        @Override
        public void putItem(String key, ItemStack stack) {
            if (!stack.isEmpty()) {
                out.store(key, ItemStack.CODEC, stack);
            }
        }

        @Override public <T> void putCodec(String key, Codec<T> codec, T value) { out.store(key, codec, value); }
        @Override public DataWriter child(String key) { return new ValueWriter(out.child(key)); }

        @Override
        public ListWriter list(String key) {
            ValueOutput.ValueOutputList list = out.childrenList(key);
            return () -> new ValueWriter(list.addChild());
        }
    }

    private record ValueReader(ValueInput in) implements DataReader {
        @Override public boolean getBoolean(String key, boolean defaultValue) { return in.getBooleanOr(key, defaultValue); }
        @Override public byte getByte(String key, byte defaultValue) { return in.getByteOr(key, defaultValue); }
        @Override public int getInt(String key, int defaultValue) { return in.getIntOr(key, defaultValue); }
        @Override public long getLong(String key, long defaultValue) { return in.getLongOr(key, defaultValue); }
        @Override public String getString(String key, String defaultValue) { return in.getStringOr(key, defaultValue); }
        @Override public int[] getIntArray(String key) { return in.getIntArray(key).orElse(new int[0]); }
        @Override public ItemStack getItem(String key) { return in.read(key, ItemStack.CODEC).orElse(ItemStack.EMPTY); }
        @Override public <T> Optional<T> getCodec(String key, Codec<T> codec) { return in.read(key, codec); }
        @Override public DataReader child(String key) { return new ValueReader(in.childOrEmpty(key)); }

        @Override
        public List<DataReader> list(String key) {
            List<DataReader> result = new ArrayList<>();
            in.childrenListOrEmpty(key).stream().forEach(child -> result.add(new ValueReader(child)));
            return result;
        }
    }
    *///?}

    //? if <26.1 {
    public static DataWriter writer(CompoundTag tag, @Nullable HolderLookup.Provider registries) {
        return new TagWriter(tag, registries);
    }

    public static DataReader reader(CompoundTag tag, @Nullable HolderLookup.Provider registries) {
        return new TagReader(tag, registries);
    }

    private record TagWriter(CompoundTag tag, @Nullable HolderLookup.Provider registries) implements DataWriter {
        @Override public void putBoolean(String key, boolean value) { tag.putBoolean(key, value); }
        @Override public void putByte(String key, byte value) { tag.putByte(key, value); }
        @Override public void putInt(String key, int value) { tag.putInt(key, value); }
        @Override public void putLong(String key, long value) { tag.putLong(key, value); }
        @Override public void putString(String key, String value) { tag.putString(key, value); }
        @Override public void putIntArray(String key, int[] value) { tag.putIntArray(key, value); }

        @Override
        public void putItem(String key, ItemStack stack) {
            if (stack.isEmpty()) {
                return;
            }
            tag.put(key, saveStack(stack, registries));
        }

        @Override
        public <T> void putCodec(String key, Codec<T> codec, T value) {
            codec.encodeStart(ops(registries), value).result().ifPresent(encoded -> tag.put(key, encoded));
        }

        @Override
        public DataWriter child(String key) {
            CompoundTag child = new CompoundTag();
            tag.put(key, child);
            return new TagWriter(child, registries);
        }

        @Override
        public ListWriter list(String key) {
            ListTag list = new ListTag();
            tag.put(key, list);
            return () -> {
                CompoundTag child = new CompoundTag();
                list.add(child);
                return new TagWriter(child, registries);
            };
        }
    }

    private record TagReader(CompoundTag tag, @Nullable HolderLookup.Provider registries) implements DataReader {
        @Override
        public boolean getBoolean(String key, boolean defaultValue) {
            return tag.contains(key, Tag.TAG_BYTE) ? tag.getBoolean(key) : defaultValue;
        }

        @Override
        public byte getByte(String key, byte defaultValue) {
            return tag.contains(key, Tag.TAG_BYTE) ? tag.getByte(key) : defaultValue;
        }

        @Override
        public int getInt(String key, int defaultValue) {
            return tag.contains(key, Tag.TAG_INT) ? tag.getInt(key) : defaultValue;
        }

        @Override
        public long getLong(String key, long defaultValue) {
            return tag.contains(key, Tag.TAG_LONG) ? tag.getLong(key) : defaultValue;
        }

        @Override
        public String getString(String key, String defaultValue) {
            return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : defaultValue;
        }

        @Override
        public int[] getIntArray(String key) {
            return tag.getIntArray(key);
        }

        @Override
        public ItemStack getItem(String key) {
            if (!tag.contains(key, Tag.TAG_COMPOUND)) {
                return ItemStack.EMPTY;
            }
            return loadStack(tag.getCompound(key), registries);
        }

        @Override
        public <T> Optional<T> getCodec(String key, Codec<T> codec) {
            Tag value = tag.get(key);
            return value == null ? Optional.empty() : codec.parse(ops(registries), value).result();
        }

        @Override
        public DataReader child(String key) {
            return new TagReader(tag.getCompound(key), registries);
        }

        @Override
        public List<DataReader> list(String key) {
            ListTag list = tag.getList(key, Tag.TAG_COMPOUND);
            List<DataReader> result = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                result.add(new TagReader(list.getCompound(i), registries));
            }
            return result;
        }
    }
    //?}

    //? if >=1.21 && <26.1 {
    /*private static com.mojang.serialization.DynamicOps<Tag> ops(@Nullable HolderLookup.Provider registries) {
        return registries != null ? RegistryOps.create(NbtOps.INSTANCE, registries) : NbtOps.INSTANCE;
    }

    private static Tag saveStack(ItemStack stack, @Nullable HolderLookup.Provider registries) {
        return stack.save(requireRegistries(registries));
    }

    private static ItemStack loadStack(CompoundTag tag, @Nullable HolderLookup.Provider registries) {
        return ItemStack.parseOptional(requireRegistries(registries), tag);
    }
    *///?}

    //? if <1.21 {
    private static com.mojang.serialization.DynamicOps<Tag> ops(@Nullable HolderLookup.Provider registries) {
        return NbtOps.INSTANCE;
    }

    private static Tag saveStack(ItemStack stack, @Nullable HolderLookup.Provider registries) {
        return stack.save(new CompoundTag());
    }

    private static ItemStack loadStack(CompoundTag tag, @Nullable HolderLookup.Provider registries) {
        return ItemStack.of(tag);
    }
    //?}
}
