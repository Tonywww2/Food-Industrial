package com.tonywww.foodindustrial.food.nutrition;

import org.jetbrains.annotations.Nullable;

/**
 * The metabolic components a serving carries (foods/03 §2): the keys of {@code per_serving} in the
 * nutrition data files, in display order (text standard §3.3). Components added later (foods/02 §6)
 * are added here only. Optional components (nitrite, added 2026-11-18, and the vitamin A and minerals of I46,
 * added after the hand-made data was confirmed) count as 0 when a file leaves them out and are never reported
 * missing.
 */
public enum Component {
    CAFFEINE("caffeine", "caffeine_mg", "mg"),
    ALCOHOL("alcohol", "alcohol_g", "g"),
    FAST_SUGAR("fast_sugar", "fast_sugar_g", "g"),
    /** Nitrite as sodium nitrite (foods/02 §4.4); potassium nitrite counts 0.81 mg per mg. */
    NITRITE("nitrite", "nitrite_mg", "mg", false),
    VITAMIN_C("vitamin_c", "vitamin_c_mg", "mg"),
    VITAMIN_B1("vitamin_b1", "vitamin_b1_mg", "mg"),
    VITAMIN_D("vitamin_d", "vitamin_d_ug", "µg"),
    /** Preformed vitamin A (retinol, µg RE): the part the upper level counts (foods/02 §5.6, I46). */
    VITAMIN_A("vitamin_a", "vitamin_a_ug", "µg", false),
    /** Provitamin A carotenoids as µg RAE (β-carotene ÷ 12, other provitamin A carotenoids ÷ 24; I46, 2026-11-19). */
    PROVITAMIN_A("provitamin_a", "provitamin_a_ug", "µg", false),
    CALCIUM("calcium", "calcium_mg", "mg", false),
    IRON("iron", "iron_mg", "mg", false),
    ZINC("zinc", "zinc_mg", "mg", false);

    private static final Component[] VALUES = values();
    /** Bit mask with every component set. */
    public static final int ALL = (1 << VALUES.length) - 1;
    /** Bit mask of the components a file must name (the others count as 0 when left out). */
    public static final int REQUIRED;

    static {
        int required = 0;
        for (Component component : VALUES) {
            if (component.required) {
                required |= component.bit();
            }
        }
        REQUIRED = required;
    }

    private final String id;
    private final String key;
    private final String unit;
    private final boolean required;

    Component(String id, String key, String unit) {
        this(id, key, unit, true);
    }

    Component(String id, String key, String unit, boolean required) {
        this.id = id;
        this.key = key;
        this.unit = unit;
        this.required = required;
    }

    /** Whether a hand-made file must name it (foods/03 §2). */
    public boolean required() {
        return required;
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
