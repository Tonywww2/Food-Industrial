package com.tonywww.foodindustrial.food.flavor;

import org.jetbrains.annotations.Nullable;

/** The 11 flavour types (foods/11 §2): one flavouring item each, and the flavour group of the flavourings. */
public enum FlavorType {
    FRUITY("fruity", "果香", "Fruity", 0xF28C28),
    DAIRY("dairy", "乳香", "Dairy", 0xF3E6BE),
    SWEET("sweet", "甜香", "Sweet", 0xC98B3A),
    NUTTY_ROASTED("nutty_roasted", "坚果与焙烤", "Nutty and Roasted", 0x8B5A2B),
    SAVORY("savory", "肉味与咸味", "Savory", 0xA0522D),
    SPICY("spicy", "辛香", "Spicy", 0xC0392B),
    GREEN("green", "蔬菜与青香", "Green", 0x6AA84F),
    ALCOHOLIC("alcoholic", "酒香", "Alcoholic", 0x8E3B5E),
    FLORAL("floral", "花香", "Floral", 0xE78AC3),
    COOLING("cooling", "清凉", "Cooling", 0x7FD6C2),
    SMOKY("smoky", "烟熏", "Smoky", 0x6B6B6B);

    private final String id;
    private final String zh;
    private final String en;
    private final int color;

    FlavorType(String id, String zh, String en, int color) {
        this.id = id;
        this.zh = zh;
        this.en = en;
        this.color = color;
    }

    public String id() {
        return id;
    }

    public String zh() {
        return zh;
    }

    public String en() {
        return en;
    }

    /** Tint of the item textures. */
    public int color() {
        return color;
    }

    /** The flavouring item's id, {@code <type>_flavoring}. */
    public String itemId() {
        return id + "_flavoring";
    }

    /** The coarse emulsion fluid of the emulsion step (foods/11 §5.3), {@code <type>_flavoring_premix}. */
    public String premixId() {
        return id + "_flavoring_premix";
    }

    /** The tags of the flavour group (items and fluids), {@code flavor_notes/<type>}. */
    public String notesTag() {
        return "flavor_notes/" + id;
    }

    /** Language key of the type's name. */
    public String key() {
        return "flavor_type.foodindustrial." + id;
    }

    @Nullable
    public static FlavorType byId(String id) {
        for (FlavorType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
