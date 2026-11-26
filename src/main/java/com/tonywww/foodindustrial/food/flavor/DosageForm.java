package com.tonywww.foodindustrial.food.flavor;

import org.jetbrains.annotations.Nullable;

/**
 * Dosage forms of a flavouring (foods/11 §4, GB 30616 §3.5–3.11), kept as item data. The liquid forms are drawn as a
 * small vial, the paste as a jar, the powders as a sachet ({@link #variant}, the item model's variant).
 */
public enum DosageForm {
    WATER_SOLUBLE("water_soluble", "水溶性液体", "Water-Soluble Liquid", 0),
    OIL_SOLUBLE("oil_soluble", "油溶性液体", "Oil-Soluble Liquid", 0),
    EMULSION("emulsion", "乳化", "Emulsion", 0),
    PASTE("paste", "浆膏", "Paste", 1),
    BLENDED_POWDER("blended_powder", "拌和型粉末", "Blended Powder", 2),
    ENCAPSULATED_POWDER("encapsulated_powder", "胶囊型粉末", "Encapsulated Powder", 2);

    /** Model variants: the vial, the paste jar, the powder sachet. */
    public static final String[] VARIANTS = {"", "paste", "powder"};

    private final String id;
    private final String zh;
    private final String en;
    private final int variant;

    DosageForm(String id, String zh, String en, int variant) {
        this.id = id;
        this.zh = zh;
        this.en = en;
        this.variant = variant;
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

    /** Index of the item model variant ({@link #VARIANTS}). */
    public int variant() {
        return variant;
    }

    public String key() {
        return "dosage_form.foodindustrial." + id;
    }

    @Nullable
    public static DosageForm byId(String id) {
        for (DosageForm form : values()) {
            if (form.id.equals(id)) {
                return form;
            }
        }
        return null;
    }
}
