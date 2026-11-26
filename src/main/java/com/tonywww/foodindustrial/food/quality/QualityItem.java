package com.tonywww.foodindustrial.food.quality;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The four quality items of a finished product (foods/01 §3.1, foods/04 §1), in display order. Additives of the
 * GB 2760 function classes mapped to an item improve it (foods/04 §1–§3).
 */
public enum QualityItem {
    COLOR("color", "色泽", "Colour"),
    TASTE("taste", "滋味和气味", "Taste and smell"),
    STATE("state", "状态", "Texture"),
    PRESERVATION("preservation", "保存性", "Keeping");

    private final String id;
    private final String zh;
    private final String en;

    QualityItem(String id, String zh, String en) {
        this.id = id;
        this.zh = zh;
        this.en = en;
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

    /** Item tag of the additives this item accepts by default: the function classes mapped to it (foods/04 §1). */
    public String tag() {
        return "foodindustrial:quality/" + id;
    }

    @Nullable
    public static QualityItem byId(String id) {
        String key = id.toLowerCase(Locale.ROOT);
        for (QualityItem item : values()) {
            if (item.id.equals(key)) {
                return item;
            }
        }
        return null;
    }
}
