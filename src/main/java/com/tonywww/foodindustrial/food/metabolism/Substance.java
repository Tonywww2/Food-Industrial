package com.tonywww.foodindustrial.food.metabolism;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.food.nutrition.Component;
import org.jetbrains.annotations.Nullable;

/** The seven substances the metabolism tracks (foods/02 §3), in display order. */
public enum Substance {
    CAFFEINE("caffeine", Component.CAFFEINE, 4),
    ALCOHOL("alcohol", Component.ALCOHOL, 5),
    FAST_SUGAR("fast_sugar", Component.FAST_SUGAR, 1),
    IMPURITY("impurity", null, 3),
    VITAMIN_C("vitamin_c", Component.VITAMIN_C, 2),
    VITAMIN_B1("vitamin_b1", Component.VITAMIN_B1, 2),
    VITAMIN_D("vitamin_d", Component.VITAMIN_D, 2);

    private static final Substance[] VALUES = values();
    private final String id;
    @Nullable
    private final Component component;
    private final int tiers;

    Substance(String id, @Nullable Component component, int tiers) {
        this.id = id;
        this.component = component;
        this.tiers = tiers;
    }

    public String id() {
        return id;
    }

    /** The nutrition data component it comes from; {@code null} for impurity (from product grades). */
    @Nullable
    public Component component() {
        return component;
    }

    /** Highest tier (tier 0 is normal). */
    public int tiers() {
        return tiers;
    }

    /** Whether it is a nutrient with a reserve in days. */
    public boolean nutrient() {
        return this == VITAMIN_C || this == VITAMIN_B1 || this == VITAMIN_D;
    }

    /** Index among the nutrients (0–2), or −1. */
    public int nutrientIndex() {
        return nutrient() ? ordinal() - VITAMIN_C.ordinal() : -1;
    }

    public String langKey() {
        return "metabolism.foodindustrial." + id;
    }

    /** Name of tier {@code tier} (0: normal, shared by all). */
    public String tierKey(int tier) {
        return tier == 0 ? "metabolism.foodindustrial.normal" : langKey() + ".tier" + tier;
    }

    /** The 16 × 16 icon shared by the body panel and Apothic Attributes' panel. */
    public String icon() {
        return FoodIndustrial.MOD_ID + ":textures/gui/metabolism/" + id + ".png";
    }

    public static Substance[] all() {
        return VALUES;
    }

    @Nullable
    public static Substance byId(String id) {
        for (Substance substance : VALUES) {
            if (substance.id.equals(id)) {
                return substance;
            }
        }
        return null;
    }
}
