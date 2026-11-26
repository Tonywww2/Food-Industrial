package com.tonywww.foodindustrial.food.quality;

import java.util.List;
import java.util.Set;

/**
 * The synergy table of foods/04 §3.1: two additives of one quality item that together make it excellent. Each row is
 * one item and two groups of materials; any member of the first group with any member of the second counts. Members
 * of a row are accepted by that item even when their own class improves another item (citric acid as a chelator for
 * keeping, ascorbate as a curing accelerator for colour), because the recipe uses them that way (foods/04 §1).
 */
public final class Synergies {
    /**
     * One synergy row.
     *
     * @param number row of foods/04 §3.1
     */
    public record Row(int number, QualityItem item, Set<String> first, Set<String> second) {
        public boolean pairs(String a, String b) {
            return first.contains(a) && second.contains(b) || first.contains(b) && second.contains(a);
        }

        public boolean member(String material) {
            return first.contains(material) || second.contains(material);
        }
    }

    private static final Set<String> HIGH_INTENSITY = Set.of("aspartame", "aspartame_acesulfame", "sucralose", "neotame", "advantame",
        "alitame", "sodium_saccharin", "sodium_cyclamate", "calcium_cyclamate", "steviol_glycosides", "mogroside", "thaumatin",
        "ammonium_glycyrrhizinate", "monopotassium_glycyrrhizinate", "tripotassium_glycyrrhizinate");
    private static final Set<String> CHELATORS = Set.of("citric_acid", "disodium_edta", "calcium_disodium_edta", "sodium_hexametaphosphate",
        "sodium_tripolyphosphate", "tetrasodium_pyrophosphate", "sodium_acid_pyrophosphate", "trisodium_phosphate", "disodium_phosphate",
        "monosodium_phosphate", "tetrapotassium_pyrophosphate", "potassium_polymetaphosphate");

    public static final List<Row> ROWS = List.of(
        new Row(1, QualityItem.STATE, Set.of("xanthan_gum"), Set.of("locust_bean_gum")),
        new Row(2, QualityItem.STATE, Set.of("carrageenan"), Set.of("locust_bean_gum")),
        new Row(3, QualityItem.TASTE, Set.of("monosodium_glutamate"), Set.of("inosinic_disodium", "guanylic_disodium")),
        new Row(4, QualityItem.TASTE, Set.of("acesulfame_potassium"), HIGH_INTENSITY),
        new Row(5, QualityItem.PRESERVATION, Set.of("bha", "bht", "tbhq", "propyl_gallate", "alpha_tocopherol", "dl_alpha_tocopherol"), CHELATORS),
        new Row(6, QualityItem.COLOR, Set.of("sodium_nitrite", "potassium_nitrite"), Set.of("sodium_ascorbate", "sodium_erythorbate")));

    private Synergies() {
    }

    /** Whether {@code a} and {@code b} form a row of {@code item}. */
    public static boolean pair(QualityItem item, String a, String b) {
        for (Row row : ROWS) {
            if (row.item() == item && row.pairs(a, b)) {
                return true;
            }
        }
        return false;
    }

    /** Whether {@code material} belongs to a row of {@code item}. */
    public static boolean member(QualityItem item, String material) {
        for (Row row : ROWS) {
            if (row.item() == item && row.member(material)) {
                return true;
            }
        }
        return false;
    }
}
