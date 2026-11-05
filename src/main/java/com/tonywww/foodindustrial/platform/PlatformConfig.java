package com.tonywww.foodindustrial.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
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
    private static final List<BooleanOption> BOOLEANS = new ArrayList<>();
    private static boolean registered;

    private PlatformConfig() {
    }

    /**
     * Declares a boolean option {@code [section] name}. The returned supplier reads the loaded value
     * and gives {@code defaultValue} before the file is loaded.
     */
    public static BooleanSupplier commonBoolean(String section, String name, String comment, boolean defaultValue) {
        if (registered) {
            throw new IllegalStateException("Config option " + section + "." + name + " declared after the config was registered");
        }
        BooleanOption option = new BooleanOption(section, name, comment, defaultValue);
        BOOLEANS.add(option);
        return option::get;
    }

    //? if forge {
    static void register(ModLoadingContext context) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        build(builder);
        context.registerConfig(ModConfig.Type.COMMON, builder.build());
    }

    private static void build(ForgeConfigSpec.Builder builder) {
        String section = null;
        for (BooleanOption option : BOOLEANS) {
            if (!option.section.equals(section)) {
                if (section != null) {
                    builder.pop();
                }
                builder.push(option.section);
                section = option.section;
            }
            ForgeConfigSpec.BooleanValue value = builder.comment(option.comment).define(option.name, option.defaultValue);
            option.reader = value::get;
        }
        if (section != null) {
            builder.pop();
        }
        registered = true;
    }
    //?} else {
    /*static void register(ModContainer container) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        String section = null;
        for (BooleanOption option : BOOLEANS) {
            if (!option.section.equals(section)) {
                if (section != null) {
                    builder.pop();
                }
                builder.push(option.section);
                section = option.section;
            }
            ModConfigSpec.BooleanValue value = builder.comment(option.comment).define(option.name, option.defaultValue);
            option.reader = value::get;
        }
        if (section != null) {
            builder.pop();
        }
        registered = true;
        container.registerConfig(ModConfig.Type.COMMON, builder.build());
    }
    *///?}

    private static final class BooleanOption {
        private final String section;
        private final String name;
        private final String comment;
        private final boolean defaultValue;
        private volatile java.util.function.Supplier<Boolean> reader;

        private BooleanOption(String section, String name, String comment, boolean defaultValue) {
            this.section = section;
            this.name = name;
            this.comment = comment;
            this.defaultValue = defaultValue;
        }

        private boolean get() {
            java.util.function.Supplier<Boolean> current = reader;
            if (current == null) {
                return defaultValue;
            }
            try {
                return current.get();
            } catch (IllegalStateException notLoaded) {
                return defaultValue;
            }
        }
    }
}
