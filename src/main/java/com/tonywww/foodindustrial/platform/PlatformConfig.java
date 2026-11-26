package com.tonywww.foodindustrial.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
//? if forge {
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
//?} else {
/*import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
*///?}

/**
 * The mod's config files: {@code config/foodindustrial-common.toml} and, for what only the client decides
 * (I37), {@code config/foodindustrial-client.toml}. Business code declares options during
 * {@code FoodIndustrial.init()}; the entrypoint builds and registers the files afterwards.
 */
public final class PlatformConfig {
    private static final List<Option<?>> OPTIONS = new ArrayList<>();
    private static boolean registered;

    private PlatformConfig() {
    }

    /**
     * Declares a boolean option {@code [section] name}. The returned supplier reads the loaded value
     * and gives {@code defaultValue} before the file is loaded.
     */
    public static BooleanSupplier commonBoolean(String section, String name, String comment, boolean defaultValue) {
        Option<Boolean> option = declare(new Option<>(false, section, name, comment, defaultValue, null, 0, 0));
        return option::get;
    }

    /** Like {@link #commonBoolean}, in the client file: read on the client only. */
    public static BooleanSupplier clientBoolean(String section, String name, String comment, boolean defaultValue) {
        Option<Boolean> option = declare(new Option<>(true, section, name, comment, defaultValue, null, 0, 0));
        return option::get;
    }

    /** A whole number from {@code min} to {@code max} in the client file. */
    public static IntSupplier clientInt(String section, String name, String comment, int defaultValue, int min, int max) {
        if (defaultValue < min || defaultValue > max) {
            throw new IllegalArgumentException("Default " + defaultValue + " of " + section + "." + name + " is outside " + min + ".." + max);
        }
        Option<Integer> option = declare(new Option<>(true, section, name, comment, defaultValue, null, min, max));
        return option::get;
    }

    /**
     * Declares an option {@code [section] name} that takes one of {@code allowed} (I25, 2026-11-11). The
     * returned supplier reads the loaded value and gives {@code defaultValue} before the file is loaded.
     */
    public static Supplier<String> commonChoice(String section, String name, String comment, String defaultValue, List<String> allowed) {
        if (!allowed.contains(defaultValue)) {
            throw new IllegalArgumentException("Default " + defaultValue + " of " + section + "." + name + " is not in " + allowed);
        }
        Option<String> option = declare(new Option<>(false, section, name, comment, defaultValue, List.copyOf(allowed), 0, 0));
        return option::get;
    }

    /**
     * Declares an option {@code [section] name} holding a list of strings, each one of {@code allowed}; the list may
     * be empty (I46, 2026-11-19). Entries that are not allowed are dropped when the file is loaded. The returned
     * supplier reads the loaded list and gives {@code defaultValue} before the file is loaded.
     */
    public static Supplier<List<String>> commonStringList(String section, String name, String comment, List<String> defaultValue,
                                                          List<String> allowed) {
        if (allowed.isEmpty() || !allowed.containsAll(defaultValue)) {
            throw new IllegalArgumentException("Default " + defaultValue + " of " + section + "." + name + " is not in " + allowed);
        }
        Option<List<String>> option = declare(new Option<>(false, section, name, comment, List.copyOf(defaultValue), List.copyOf(allowed), 0, 0));
        option.list = true;
        return option::get;
    }

    private static <T> Option<T> declare(Option<T> option) {
        if (registered) {
            throw new IllegalStateException("Config option " + option.section + "." + option.name + " declared after the config was registered");
        }
        OPTIONS.add(option);
        return option;
    }

    /** Options of one file grouped by section, in declaration order of the first option of each section. */
    private static List<Option<?>> sorted(boolean client) {
        List<String> sections = new ArrayList<>();
        for (Option<?> option : OPTIONS) {
            if (option.client == client && !sections.contains(option.section)) {
                sections.add(option.section);
            }
        }
        List<Option<?>> result = new ArrayList<>();
        for (String section : sections) {
            for (Option<?> option : OPTIONS) {
                if (option.client == client && option.section.equals(section)) {
                    result.add(option);
                }
            }
        }
        return result;
    }

    //? if forge {
    static void register(ModLoadingContext context) {
        registered = true;
        context.registerConfig(ModConfig.Type.COMMON, build(false));
        if (!sorted(true).isEmpty()) {
            context.registerConfig(ModConfig.Type.CLIENT, build(true));
        }
    }

    private static ForgeConfigSpec build(boolean client) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        String section = null;
        for (Option<?> option : sorted(client)) {
            if (!option.section.equals(section)) {
                if (section != null) {
                    builder.pop();
                }
                builder.push(option.section);
                section = option.section;
            }
            builder.comment(option.comment);
            if (option.list) {
                List<String> allowed = option.allowed;
                @SuppressWarnings("unchecked")
                List<String> initial = (List<String>) option.defaultValue;
                ForgeConfigSpec.ConfigValue<List<? extends String>> value = builder.defineListAllowEmpty(option.name, initial,
                    entry -> entry instanceof String text && allowed.contains(text));
                option.reader = () -> List.copyOf(value.get());
            } else if (option.allowed != null) {
                ForgeConfigSpec.ConfigValue<String> value = builder.defineInList(option.name, (String) option.defaultValue, new ArrayList<>(option.allowed));
                option.reader = value::get;
            } else if (option.defaultValue instanceof Integer number) {
                ForgeConfigSpec.IntValue value = builder.defineInRange(option.name, (int) number, option.min, option.max);
                option.reader = value::get;
            } else {
                ForgeConfigSpec.BooleanValue value = builder.define(option.name, (boolean) (Boolean) option.defaultValue);
                option.reader = value::get;
            }
        }
        if (section != null) {
            builder.pop();
        }
        return builder.build();
    }
    //?} else {
    /*static void register(ModContainer container) {
        registered = true;
        container.registerConfig(ModConfig.Type.COMMON, build(false));
        if (!sorted(true).isEmpty()) {
            container.registerConfig(ModConfig.Type.CLIENT, build(true));
        }
    }

    private static ModConfigSpec build(boolean client) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        String section = null;
        for (Option<?> option : sorted(client)) {
            if (!option.section.equals(section)) {
                if (section != null) {
                    builder.pop();
                }
                builder.push(option.section);
                section = option.section;
            }
            builder.comment(option.comment);
            if (option.list) {
                List<String> allowed = option.allowed;
                @SuppressWarnings("unchecked")
                List<String> initial = (List<String>) option.defaultValue;
                ModConfigSpec.ConfigValue<List<? extends String>> value = builder.defineListAllowEmpty(option.name, initial,
                    () -> allowed.get(0), entry -> entry instanceof String text && allowed.contains(text));
                option.reader = () -> List.copyOf(value.get());
            } else if (option.allowed != null) {
                ModConfigSpec.ConfigValue<String> value = builder.defineInList(option.name, (String) option.defaultValue, new ArrayList<>(option.allowed));
                option.reader = value::get;
            } else if (option.defaultValue instanceof Integer number) {
                ModConfigSpec.IntValue value = builder.defineInRange(option.name, (int) number, option.min, option.max);
                option.reader = value::get;
            } else {
                ModConfigSpec.BooleanValue value = builder.define(option.name, (boolean) (Boolean) option.defaultValue);
                option.reader = value::get;
            }
        }
        if (section != null) {
            builder.pop();
        }
        return builder.build();
    }
    *///?}

    private static final class Option<T> {
        /** In the client file. */
        private final boolean client;
        private final String section;
        private final String name;
        private final String comment;
        private final Object defaultValue;
        @org.jetbrains.annotations.Nullable
        private final List<String> allowed;
        /** Range of a whole number. */
        private final int min;
        private final int max;
        /** A list of strings out of {@link #allowed}. */
        private boolean list;
        private volatile Supplier<?> reader;

        private Option(boolean client, String section, String name, String comment, T defaultValue,
                       @org.jetbrains.annotations.Nullable List<String> allowed, int min, int max) {
            this.client = client;
            this.min = min;
            this.max = max;
            this.section = section;
            this.name = name;
            this.comment = comment;
            this.defaultValue = defaultValue;
            this.allowed = allowed;
        }

        @SuppressWarnings("unchecked")
        private T get() {
            Supplier<?> current = reader;
            if (current == null) {
                return (T) defaultValue;
            }
            try {
                return (T) current.get();
            } catch (IllegalStateException notLoaded) {
                return (T) defaultValue;
            }
        }
    }
}
