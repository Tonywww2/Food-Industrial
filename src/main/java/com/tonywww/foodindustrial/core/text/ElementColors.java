package com.tonywww.foodindustrial.core.text;

import java.util.HashMap;
import java.util.Map;

/**
 * Display colours of chemical elements in formulas (text standard §3.1, 2026-11-05): one colour per
 * IUPAC group 1–18, hydrogen its own colour, everything that is not an element symbol (digits,
 * brackets, the hydrate dot, coefficients) light grey. Lanthanides and actinides count as group 3.
 */
public final class ElementColors {
    /** Hydrogen (group 1 but a non-metal, nearly every organic formula has it). */
    public static final int HYDROGEN = 0xFFFFFF;
    /** Digits, brackets, {@code ·}, coefficients and unknown symbols. */
    public static final int OTHER = 0xAAAAAA;

    /** Colour of groups 1–18 (index = group − 1). */
    private static final int[] GROUP_COLORS = {
        0xD57BFF, // 1 alkali metals: violet
        0x3FD8D8, // 2 alkaline earth metals: cyan
        0xB0E57C, // 3 scandium group, lanthanides, actinides: lime
        0xC8B6E2, // 4 titanium group: lilac grey
        0x9C8CFF, // 5 vanadium group: periwinkle
        0x6FC3B2, // 6 chromium group: teal grey
        0xE58FB0, // 7 manganese group: rose
        0xE8743B, // 8 iron group: rust
        0x3A6BD8, // 9 cobalt group: cobalt blue
        0x8FD19E, // 10 nickel group: sage
        0xF0B37E, // 11 copper group: copper
        0xB6C2D9, // 12 zinc group: zinc grey
        0xD9A86C, // 13 boron group: tan
        0xFFD34E, // 14 carbon group: gold
        0x5B9BFF, // 15 nitrogen group: blue
        0xFF5A5A, // 16 oxygen group: red
        0x5BE86A, // 17 halogens: green
        0xFF9AD5, // 18 noble gases: pink
    };

    private static final Map<String, Integer> GROUPS = new HashMap<>();

    static {
        group(1, "H Li Na K Rb Cs Fr");
        group(2, "Be Mg Ca Sr Ba Ra");
        group(3, "Sc Y La Ac Ce Pr Nd Pm Sm Eu Gd Tb Dy Ho Er Tm Yb Lu Th Pa U Np Pu Am Cm Bk Cf Es Fm Md No Lr");
        group(4, "Ti Zr Hf Rf");
        group(5, "V Nb Ta Db");
        group(6, "Cr Mo W Sg");
        group(7, "Mn Tc Re Bh");
        group(8, "Fe Ru Os Hs");
        group(9, "Co Rh Ir Mt");
        group(10, "Ni Pd Pt Ds");
        group(11, "Cu Ag Au Rg");
        group(12, "Zn Cd Hg Cn");
        group(13, "B Al Ga In Tl Nh");
        group(14, "C Si Ge Sn Pb Fl");
        group(15, "N P As Sb Bi Mc");
        group(16, "O S Se Te Po Lv");
        group(17, "F Cl Br I At Ts");
        group(18, "He Ne Ar Kr Xe Rn Og");
    }

    private ElementColors() {
    }

    private static void group(int group, String symbols) {
        for (String symbol : symbols.split(" ")) {
            GROUPS.put(symbol, group);
        }
    }

    public static boolean isElement(String symbol) {
        return GROUPS.containsKey(symbol);
    }

    /** IUPAC group 1–18 of {@code symbol}, or 0 when it is not an element. */
    public static int group(String symbol) {
        return GROUPS.getOrDefault(symbol, 0);
    }

    /** Group colour (RGB) of a group 1–18. */
    public static int ofGroup(int group) {
        return GROUP_COLORS[group - 1];
    }

    /** Display colour (RGB) of {@code symbol}: hydrogen, its group, or {@link #OTHER}. */
    public static int of(String symbol) {
        if (symbol.equals("H")) {
            return HYDROGEN;
        }
        int group = group(symbol);
        return group == 0 ? OTHER : ofGroup(group);
    }
}
