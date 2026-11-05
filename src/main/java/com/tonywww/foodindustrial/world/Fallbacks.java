package com.tonywww.foodindustrial.world;

import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformConfig;
import com.tonywww.foodindustrial.platform.PlatformWorldgen;

import java.util.function.BooleanSupplier;

/**
 * Fallback sources for materials normally provided by other mods (additives/11 question 14,
 * 2026-11-04): they are registered on every node and only generate or drop while the providing mod is
 * absent, unless forced in {@code [fallbacks]} of the common config.
 */
public enum Fallbacks {
    SULFUR("sulfur", "thermal_foundation", "Thermal Foundation"),
    APATITE("apatite", "thermal_foundation", "Thermal Foundation"),
    RICE("rice", "farmersdelight", "Farmer's Delight"),
    TREE_BARK("tree_bark", "farmersdelight", "Farmer's Delight");

    /** Prefix of the world generation switch names ({@code fallback/<id>}). */
    public static final String SWITCH_PREFIX = "fallback/";

    private final String id;
    private final String provider;
    private final String providerName;
    private BooleanSupplier force = () -> false;

    Fallbacks(String id, String provider, String providerName) {
        this.id = id;
        this.provider = provider;
        this.providerName = providerName;
    }

    public String id() {
        return id;
    }

    /** World generation switch name for {@code PlatformWorldgen.ENABLED_FILTER}. */
    public String switchName() {
        return SWITCH_PREFIX + id;
    }

    /** Whether the fallback is active: forced in the config, or the providing mod is absent. */
    public boolean enabled() {
        return force.getAsBoolean() || !Platform.isModLoaded(provider);
    }

    /** Declares the config options and the world generation switches. Call during mod construction. */
    static void bootstrap() {
        for (Fallbacks fallback : values()) {
            fallback.force = PlatformConfig.commonBoolean("fallbacks", "force_" + fallback.id,
                "Also provide the fallback " + fallback.id.replace('_', ' ') + " when " + fallback.providerName + " is installed", false);
        }
        PlatformWorldgen.setSwitches(name -> {
            for (Fallbacks fallback : values()) {
                if (fallback.switchName().equals(name)) {
                    return fallback.enabled();
                }
            }
            return true;
        });
    }
}
