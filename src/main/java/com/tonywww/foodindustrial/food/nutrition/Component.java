package com.tonywww.foodindustrial.food.nutrition;

import org.jetbrains.annotations.Nullable;

/**
 * The metabolic components a serving carries (foods/03 §2): the keys of {@code per_serving} in the
 * nutrition data files, in display order (text standard §3.3). Components added later (foods/02 §6)
 * are added here only.
 */
public enum Component {
    CAFFEINE("caffeine", "caffeine_mg", "mg"),
    ALCOHOL("alcohol", "alcohol_g", "g"),
    FAST_SUGAR("fast_sugar", "fast_sugar_g", "g"),
    VITAMIN_C("vitamin_c", "vitamin_c_mg", "mg"),
    VITAMIN_B1("vitamin_b1", "vitamin_b1_mg", "mg"),
    VITAMIN_D("vitamin_d", "vitamin_d_ug", "µg");

    private static final Component[] VALUES = values();
    /** Bit mask with every component set. */
    public static final int ALL = (1 << VALUES.length) - 1;

    private final String id;
    private final String key;
    private final String unit;

    Component(String id, String key, String unit) {
        this.id = id;
        this.key = key;
        this.unit = unit;
    }

    /** Short id, e.g. {@code vitamin_c}. */
    public String id() {
        return id;
    }

    /** Key in {@code per_serving}, e.g. {@code vitamin_c_mg}. */
    public String key() {
        return key;
    }

    /** Unit of the amounts: {@code mg}, {@code g} or {@code µg}. */
    public String unit() {
        return unit;
    }

    /** Language key of the component's name. */
    public String langKey() {
        return "metabolism.foodindustrial." + id;
    }

    /** Bit of this component in a mask. */
    public int bit() {
        return 1 << ordinal();
    }

    /** All components in display order (shared array: do not modify). */
    public static Component[] all() {
        return VALUES;
    }

    /** The component with this {@code per_serving} key, or {@code null}. */
    @Nullable
    public static Component byKey(String key) {
        for (Component component : VALUES) {
            if (component.key.equals(key)) {
                return component;
            }
        }
        return null;
    }
}
