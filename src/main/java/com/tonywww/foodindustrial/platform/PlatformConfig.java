package com.tonywww.foodindustrial.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
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
 * The mod's common config file ({@code config/foodindustrial-common.toml}). Business code declares
 * options during {@code FoodIndustrial.init()}; the entrypoint builds and registers the file afterwards.
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
        Option<Boolean> option = declare(new Option<>(section, name, comment, defaultValue, null));
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
        Option<String> option = declare(new Option<>(section, name, comment, defaultValue, List.copyOf(allowed)));
        return option::get;
    }

    private static <T> Option<T> declare(Option<T> option) {
        if (registered) {
            throw new IllegalStateException("Config option " + option.section + "." + option.name + " declared after the config was registered");
        }
        OPTIONS.add(option);
        return option;
    }

    /** Options grouped by section, in declaration order of the first option of each section. */
    private static List<Option<?>> sorted() {
        List<String> sections = new ArrayList<>();
        for (Option<?> option : OPTIONS) {
            if (!sections.contains(option.section)) {
                sections.add(option.section);
            }
        }
        List<Option<?>> result = new ArrayList<>();
        for (String section : sections) {
            for (Option<?> option : OPTIONS) {
                if (option.section.equals(section)) {
                    result.add(option);
                }
            }
        }
        return result;
    }

    //? if forge {
    static void register(ModLoadingContext context) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        String section = null;
        for (Option<?> option : sorted()) {
            if (!option.section.equals(section)) {
                if (section != null) {
                    builder.pop();
                }
                builder.push(option.section);
                section = option.section;
            }
            builder.comment(option.comment);
            if (option.allowed == null) {
                ForgeConfigSpec.BooleanValue value = builder.define(option.name, (boolean) (Boolean) option.defaultValue);
                option.reader = value::get;
            } else {
                ForgeConfigSpec.ConfigValue<String> value = builder.defineInList(option.name, (String) option.defaultValue, new ArrayList<>(option.allowed));
                option.reader = value::get;
            }
        }
        if (section != null) {
            builder.pop();
        }
        registered = true;
        context.registerConfig(ModConfig.Type.COMMON, builder.build());
    }
    //?} else {
    /*static void register(ModContainer container) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        String section = null;
        for (Option<?> option : sorted()) {
            if (!option.section.equals(section)) {
                if (section != null) {
                    builder.pop();
                }
                builder.push(option.section);
                section = option.section;
            }
            builder.comment(option.comment);
            if (option.allowed == null) {
                ModConfigSpec.BooleanValue value = builder.define(option.name, (boolean) (Boolean) option.defaultValue);
                option.reader = value::get;
            } else {
                ModConfigSpec.ConfigValue<String> value = builder.defineInList(option.name, (String) option.defaultValue, new ArrayList<>(option.allowed));
                option.reader = value::get;
            }
        }
        if (section != null) {
            builder.pop();
        }
        registered = true;
        container.registerConfig(ModConfig.Type.COMMON, builder.build());
    }
    *///?}

    private static final class Option<T> {
        private final String section;
        private final String name;
        private final String comment;
        private final Object defaultValue;
        @org.jetbrains.annotations.Nullable
        private final List<String> allowed;
        private volatile Supplier<?> reader;

        private Option(String section, String name, String comment, T defaultValue, @org.jetbrains.annotations.Nullable List<String> allowed) {
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
