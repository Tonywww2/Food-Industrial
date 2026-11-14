package com.tonywww.foodindustrial.integration;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.platform.PlatformConfig;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipeConditions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * The mods whose machines run this mod's physical steps (additives/06 §他模组机器参与, additives/10 §7, I25):
 * each one has a config switch and a device tier, read by the {@code foodindustrial:integration} recipe
 * condition. Farmer's Delight and Create count as T1, the others as T2 by default.
 */
public final class Integrations {
    /** One integration: its id (config and condition), the mod id it needs, its name and default tier. */
    public record Mod(String id, String modid, String name, Tier defaultTier) {
    }

    public static final Mod FARMERS_DELIGHT = new Mod("farmersdelight", "farmersdelight", "Farmer's Delight", Tier.T1);
    public static final Mod CREATE = new Mod("create", "create", "Create", Tier.T1);
    public static final Mod THERMAL = new Mod("thermal", "thermal_expansion", "Thermal Expansion", Tier.T2);
    public static final Mod MEKANISM = new Mod("mekanism", "mekanism", "Mekanism", Tier.T2);
    public static final Mod PNEUMATICCRAFT = new Mod("pneumaticcraft", "pneumaticcraft", "PneumaticCraft: Repressurized", Tier.T2);
    public static final Mod NUCLEARCRAFT = new Mod("nuclearcraft", "nuclearcraft", "NuclearCraft", Tier.T2);

    public static final List<Mod> ALL = List.of(FARMERS_DELIGHT, CREATE, THERMAL, MEKANISM, PNEUMATICCRAFT, NUCLEARCRAFT);

    private static final List<String> TIERS = List.of("t1", "t2", "t3", "t4");
    private static final Map<String, BooleanSupplier> ENABLED = new LinkedHashMap<>();
    private static final Map<String, Supplier<String>> TIER = new LinkedHashMap<>();

    private Integrations() {
    }

    /** Declares the config options and the recipe condition. Call during mod construction. */
    public static void bootstrap() {
        for (Mod mod : ALL) {
            ENABLED.put(mod.id(), PlatformConfig.commonBoolean("integration", mod.id(),
                "Register this mod's physical steps for the machines of " + mod.name(), true));
            TIER.put(mod.id(), PlatformConfig.commonChoice("integration", mod.id() + "_tier",
                "Device tier the machines of " + mod.name() + " count as; it fixes the purity of their products (t1-t4)",
                mod.defaultTier().id(), TIERS));
        }
        PlatformRecipeConditions.setIntegrationTest((mod, tiers) -> enabled(mod) && (tiers.isEmpty() || tiers.contains(tier(mod).id())));
        com.tonywww.foodindustrial.recipe.RecipeGen.addFollower(IntegrationRecipes::generate);
        IntegrationPurityRules.bootstrap();
    }

    /** Whether integration {@code id} is switched on (unknown ids are off). */
    public static boolean enabled(String id) {
        BooleanSupplier enabled = ENABLED.get(id);
        return enabled != null && enabled.getAsBoolean();
    }

    /** Configured device tier of integration {@code id}. */
    public static Tier tier(String id) {
        Supplier<String> tier = TIER.get(id);
        Tier parsed = tier == null ? null : Tier.byId(tier.get());
        return parsed == null ? Tier.T2 : parsed;
    }
}
