package com.tonywww.foodindustrial.content;

import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CULTURING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PURIFYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * A content batch (I18–I20): its recipes are {@code foodindustrial:<prefix>/<name>}; shared steps
 * (recrystallizing, decolouring, adsorbing, culture work) are written the same way in every batch
 * (additives/04 §实现约定, game settings).
 */
public abstract class Batch {
    /** One portion of fluid, mB (additives/09 计量建议). */
    protected static final int B = 1000;
    /** Runs of a culture in M05, M06 and M29. */
    protected static final int CULTURE_RUNS = 8;
    /** Threshold of the cultures of the T4 aseptic fermenter: purified on the culture bench (2026-11-11). */
    protected static final String PURE = "food";
    /** Runs of an enzyme or a resin in the C slot. */
    protected static final int ENZYME_RUNS = 16;
    /** Runs of an immobilized enzyme or cells (P96): beads used again and again (I30, game setting). */
    protected static final int IMMOBILIZED_RUNS = 64;

    protected final RecipeGen.Output out;
    private final String prefix;

    protected Batch(RecipeGen.Output out, String prefix) {
        this.out = out;
        this.prefix = prefix;
    }

    protected ContentRecipe r(ProcessType type) {
        return ContentRecipe.of(out, prefix, type);
    }

    /** A recipe written by material id (I31): items or fluids as the material table says. */
    protected Chem x(ProcessType type) {
        return new Chem(r(type));
    }

    // ---- devices of batch 5 (I31: a row of several steps is one recipe on the device of its hardest step, 11 问题 40) ----

    protected Chem m14() {
        return x(REACTING).temp("heated").tier(2);
    }

    protected Chem m14cold() {
        return x(REACTING).temp("cooling").tier(2);
    }

    protected Chem m14acid() {
        return m14().method("corrosion_resistant");
    }

    protected Chem m14inert() {
        return m14().inert();
    }

    protected Chem m14air() {
        return x(REACTING).temp("heated").tier(2).air();
    }

    protected Chem m23(String mode) {
        Chem chem = x(REACTING).temp("heated").pressure("pressurized");
        return mode == null ? chem : chem.mode(mode);
    }

    protected Chem hydrogenation(String catalyst) {
        return m23("hydrogenation").cat(catalyst, 64);
    }

    protected Chem m27(String mode) {
        return x(GAS_REACTING).mode(mode);
    }

    protected Chem column() {
        return x(DISTILLING).method("multi_fraction");
    }

    /** Recrystallization (T1 crystallizer up): four dissolved, three crystallize, three quarters of the impurities out. */
    protected void recrystallizing(String material) {
        r(CRYSTALLIZING).in("chemicals/" + material, 4).fin("water", B).out(material, 3).add(material + "_recrystallizing");
    }

    /** Decolouring with powdered activated carbon in the stirred reactor (T2, P24). */
    protected void decolouring(String material) {
        r(REACTING).in("chemicals/" + material, 4).in("chemicals/activated_carbon", 1).fin("water", B).out(material, 4)
            .tier(2).add(material + "_decolouring");
    }

    /**
     * A solid extract purified on a resin in the stirred reactor (the adsorption column takes only
     * fluids): adsorbed, washed out with ethanol, nine tenths of the impurities out (P24, P25).
     */
    protected void resin(String material, String resin) {
        r(REACTING).in("chemicals/" + material, 4).fin("ethanol", B).cat(resin, ENZYME_RUNS).out(material, 3).rm(90)
            .fout("ethanol", 800).src().sources(0).tier(2).add(material + "_resin");
    }

    /** Adsorption of a liquid in the adsorption column (T3); with {@code method} ion exchange. */
    protected void adsorbing(String fluid, String adsorbent, String method) {
        ContentRecipe recipe = r(PURIFYING).fin(fluid, B).fout(fluid, B).mode("adsorption").cat(adsorbent, 4);
        if (method != null) {
            recipe.method(method);
        }
        recipe.add(fluid + (method == null ? "_adsorbing" : "_ion_exchange"));
    }

    /** Spray drying of a heat-sensitive extract (T3, or the freeze dryer): two in, one out, half the impurities out. */
    protected void sprayDrying(String material) {
        r(DRYING).in("chemicals/" + material, 2).out(material, 1).heat("sensitive").add(material + "_spray_drying");
    }

    /**
     * Aluminium lake (P71, I20): the dye on fresh aluminium hydroxide, a third of the lake by mass, and the
     * dye salted out for refining.
     */
    protected void lake(String dye, String lake) {
        r(REACTING).in("chemicals/" + dye, 1).in("chemicals/aluminium_hydroxide", 2).fin("water", B).out(lake, 1).degrade(67)
            .sources(0).add(lake);
        r(REACTING).in("chemicals/" + dye, 4).in("chemicals/salt", 2).fin("water", B).out(dye, 3).rm(75).add(dye + "_salting_out");
    }

    /**
     * Immobilization (P96, I30): enzyme or cells mixed with sodium alginate and dropped into calcium chloride,
     * or set in carrageenan hardened with potassium chloride; the beads are a catalyst of their own.
     */
    protected void immobilizing(String enzyme, String immobilized, boolean carrageenan) {
        ContentRecipe recipe = r(REACTING).in("enzymes/" + enzyme, 2);
        if (carrageenan) {
            recipe.in("chemicals/carrageenan", 1).in("chemicals/potassium_chloride", 1);
        } else {
            recipe.in("chemicals/sodium_alginate", 1).in("chemicals/calcium_chloride", 1);
        }
        recipe.fin("water", B).out(immobilized, 2).tier(2).add(immobilized);
    }

    /** Purification plate and liquid expansion of a culture (M30, P51). */
    protected void cultureWork(String culture) {
        r(CULTURING).in("cultures/" + culture, 1).in("chemicals/agar", 1).out(culture, 1).out("wild_mold", 1).mode("purification")
            .plate(true).add(culture + "_purification");
        r(CULTURING).in("cultures/" + culture, 1).in("chemicals/glucose", 1).out(culture, 2).keep().sources(0).mode("expansion")
            .add(culture + "_expansion");
    }
}
