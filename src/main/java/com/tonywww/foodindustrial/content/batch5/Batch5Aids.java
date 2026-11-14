package com.tonywww.foodindustrial.content.batch5;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.ABSORBING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.MIXING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The fifth content batch, groups A and B (I31, additives/17 §A, §B): the four additives missing from table A.1
 * (424–427) and the processing aids 428–459, with their intermediates. Steps a route merges (crushing,
 * filtering, drying, solvent recovery) are not recipes of their own (18 §1.4); reagents and recovered byproducts
 * are inputs and outputs.
 */
public final class Batch5Aids extends Batch {
    private Batch5Aids(RecipeGen.Output out) {
        super(out, "batch5");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch5Aids(out).generate());
    }

    private void generate() {
        missing();
        solvents();
        filterAids();
        polyethers();
        others();
    }

    // ---- A: 424–427 ----

    private void missing() {
        // 424: palmitoyl chloride, then the tea polyphenols (as EGCG) esterified in ethyl acetate
        m14acid().in("palmitic_acid").in("thionyl_chloride").out("palmitoyl_chloride").outMb("hydrogen_chloride", 500).add("palmitoyl_chloride");
        m14().in("epigallocatechin_gallate").in("palmitoyl_chloride").in("ethyl_acetate").out("tea_polyphenol_palmitate")
            .outMb("ethyl_acetate", 750).add("tea_polyphenol_palmitate");
        // 425: as 250 with slaked lime, under nitrogen
        m14inert().in("stearic_acid", 2).in("lactic_acid", 2).in("slaked_lime").out("calcium_stearoyl_lactylate").add("calcium_stearoyl_lactylate");
        // 426: rosin hydrogenated on palladium, esterified with glycerol under nitrogen (as 137)
        hydrogenation("catalysts/palladium").in("rosin", 2).in("hydrogen").out("hydrogenated_rosin", 2).add("hydrogenated_rosin");
        m14inert().in("hydrogenated_rosin", 3).in("glycerol").out("hydrogenated_rosin_glycerol_ester").add("hydrogenated_rosin_glycerol_ester");
        // 427: pine stumps steamed of turpentine and extracted with hexane, the dark rosin washed with furfural
        x(EXTRACTING).in("materials/pine_stump", 4).in("n_hexane").out("wood_rosin").outMb("turpentine", 250).outMb("n_hexane", 750)
            .mode("solvent_extraction").add("wood_rosin");
        m14inert().in("wood_rosin", 3).in("glycerol").in("furfural").out("wood_rosin_glycerol_ester").outMb("furfural", 800)
            .add("wood_rosin_glycerol_ester");
    }

    // ---- B: solvents and gases 428, 431–437, 457 ----

    private void solvents() {
        // 428: propane (from the crude oil cut) washed with caustic soda in the absorption tower
        x(ABSORBING).in("propane").in("sodium_hydroxide").outMb("propane", 950).add("propane_caustic_wash");
        // 431 A crotonaldehyde, B butyraldehyde hydrogenated; C acetone–butanol fermentation to the broth, its carbon dioxide
        // let out of the second fluid output, and the broth cut 3 : 6 : 1 (acetone, butanol, ethanol by mass; 20 % solvents,
        // a game simplification of the real 2 %; 11 问题 42)
        hydrogenation("catalysts/nickel").in("crotonaldehyde").in("hydrogen", 2).out("n_butanol").add("n_butanol_from_crotonaldehyde");
        hydrogenation("catalysts/nickel").in("butyraldehyde").in("hydrogen").out("n_butanol").add("n_butanol_from_butyraldehyde");
        x(FERMENTING).in("liquefied_starch", 2).cat("cultures/clostridium_acetobutylicum", CULTURE_RUNS).out("abe_broth", 2)
            .outMb("carbon_dioxide", 1000).mode("sealed").add("abe_broth");
        column().in("abe_broth").outMb("acetone", 60).outMb("n_butanol", 120).outMb("ethanol", 20).add("n_butanol_from_abe_broth");
        // 432, 433, 437: light naphtha hydrotreated, cut into petroleum ether and extraction solvent; n-hexane a narrow cut of it
        hydrogenation("catalysts/nickel").in("light_naphtha").in("hydrogen").out("hydrotreated_naphtha").outMb("hydrogen_sulfide", 100)
            .add("hydrotreated_naphtha");
        column().in("hydrotreated_naphtha").outMb("vegetable_oil_extraction_solvent", 600).outMb("petroleum_ether", 300)
            .add("extraction_solvents");
        column().in("vegetable_oil_extraction_solvent").outMb("n_hexane", 500).outMb("vegetable_oil_extraction_solvent", 450)
            .add("n_hexane_from_extraction_solvent");
        // 434 A propene indirectly hydrated, B acetone hydrogenated
        m14acid().in("propene").in("sulfuric_acid").in("water").out("isopropanol").outMb("sulfuric_acid", 900).add("isopropanol");
        hydrogenation("catalysts/nickel").in("acetone").in("hydrogen").out("isopropanol").add("isopropanol_from_acetone");
        // 435 A over sulfuric acid; B on a solid acid: alumina would only be told from the ethylene of the same ethanol (ethanol_dehydration)
        // by temperature, so B runs liquid under pressure on the acid ion exchange resin (11 问题 41, 2026-11-12)
        m14acid().in("ethanol", 2).in("sulfuric_acid").out("diethyl_ether").outMb("sulfuric_acid", 900).add("diethyl_ether");
        m23(null).in("ethanol", 2).cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("diethyl_ether").out("water")
            .add("diethyl_ether_on_resin");
        // 436 B: ethanol dehydrogenated on copper to the ester
        m27("gas_catalysis").in("ethanol", 2).cat("catalysts/copper", 64).out("ethyl_acetate").out("hydrogen", 2).add("ethyl_acetate_by_dehydrogenation");
        // 457 A: ammonium nitrate made in the absorption tower and decomposed by melting
        x(ABSORBING).in("ammonia").in("nitric_acid").out("ammonium_nitrate").add("ammonium_nitrate");
        m14().in("ammonium_nitrate").out("nitrous_oxide").out("water").add("nitrous_oxide_from_ammonium_nitrate");
    }

    // ---- B: adsorbents and filter aids 429, 430, 438–444, 451 ----

    private void filterAids() {
        // 429: magnesium sulfate and water glass (as metasilicate), a little acid for the excess alkali
        m14().in("epsom_salt", 2).in("sodium_silicate", 3).in("sulfuric_acid").out("magnesium_trisilicate").out("sodium_sulfate", 3)
            .add("magnesium_trisilicate");
        // 430 A light (basic carbonate in the earth kiln), B heavy (hydroxide in the rotary kiln): one item
        x(KILN).in("magnesium_carbonate").out("magnesium_oxide").outMb("carbon_dioxide", 800).mode("calcining").add("magnesium_oxide_light");
        x(KILN).in("magnesium_hydroxide").out("magnesium_oxide").mode("calcining").tier(2).add("magnesium_oxide_heavy");
        // 438, 443: clays slurried free of sand; bentonite sodium-activated with soda ash
        x(EXTRACTING).in("materials/attapulgite", 2).in("water").out("attapulgite_clay").tier(2).add("attapulgite_clay");
        m14().in("materials/bentonite", 2).in("soda_ash").in("water").out("bentonite").out("calcium_carbonate").add("bentonite");
        // 440: bentonite activated with hot dilute sulfuric acid
        m14acid().in("bentonite", 2).in("sulfuric_acid").out("activated_clay", 2).out("aluminium_sulfate").add("activated_clay");
        // 444: perlite flash-roasted, the bound water expanding it
        x(KILN).in("materials/perlite", 2).out("perlite", 2).mode("calcining").tier(2).add("perlite");
        // 451: talc washed with dilute hydrochloric acid (asbestos-free ore)
        m14().in("materials/talc", 2).in("hydrochloric_acid").out("talc", 2).out("calcium_chloride").add("talc");
        // 439: 2-pyrrolidone, vinylated with acetylene (P100), popcorn-polymerized in dilute alkali (P77)
        m23(null).in("gamma_butyrolactone").in("ammonia").out("pyrrolidone").out("water").add("pyrrolidone");
        m23(null).in("pyrrolidone").in("acetylene").in("potassium_hydroxide").out("n_vinylpyrrolidone").add("n_vinylpyrrolidone");
        x(REACTING).in("n_vinylpyrrolidone").in("sodium_hydroxide").in("water").out("pvpp").mode("polymerization").tier(2).add("pvpp");
        // 441: styrene bulk-polymerized by heat
        x(REACTING).in("styrene").out("polystyrene").mode("polymerization").temp("heated").tier(2).add("polystyrene");
        // 442: acrylonitrile hydrated by nitrile hydratase, polymerized with persulfate
        m14cold().in("acrylonitrile").in("water").cat("enzymes/nitrile_hydratase", ENZYME_RUNS).out("acrylamide").add("acrylamide");
        x(REACTING).in("acrylamide").in("ammonium_persulfate").out("polyacrylamide").mode("polymerization").tier(2).add("polyacrylamide");
        // 452: wax de-oiled (as 272), hydrotreated on nickel, the clay treatment merged
        hydrogenation("catalysts/nickel").in("paraffin_wax", 2).in("hydrogen").outDefault("paraffin_wax", 2).add("paraffin_wax_hydrotreating");
    }

    // ---- B: 446–450 propylene and ethylene oxide adducts (P60, the ethoxylation mode) ----

    private void polyethers() {
        x(REACTING).in("glycerol").in("propylene_oxide", 4).in("potassium_hydroxide").out("gp_polyether", 4).mode("ethoxylation")
            .pressure("pressurized").temp("heated").add("gp_polyether");
        x(REACTING).in("gp_polyether", 4).in("ethylene_oxide").out("gpe_polyether", 4).mode("ethoxylation").pressure("pressurized")
            .temp("heated").add("gpe_polyether");
        x(REACTING).in("ethylenediamine").in("propylene_oxide", 4).in("ethylene_oxide").in("potassium_hydroxide").out("bape_polyether", 4)
            .mode("ethoxylation").pressure("pressurized").temp("heated").add("bape_polyether");
        x(REACTING).in("pentaerythritol").in("propylene_oxide", 4).in("ethylene_oxide").in("potassium_hydroxide").out("ppe_polyether", 4)
            .mode("ethoxylation").pressure("pressurized").temp("heated").add("ppe_polyether");
        x(REACTING).in("sucrose").in("glycerol").in("propylene_oxide", 4).in("potassium_hydroxide").out("sucrose_polyoxypropylene_ether", 4)
            .mode("ethoxylation").pressure("pressurized").temp("heated").add("sucrose_polyoxypropylene_ether");
    }

    // ---- B: 445, 454, 456 ----

    private void others() {
        // 445: emulsified antifoam (composition a game setting): every component food grade
        x(MIXING).in("stearyl_alcohol", 1, PURE).in("glyceryl_monostearate", 1, PURE).in("sorbitan_monostearate", 1, PURE)
            .in("polysorbate_80", 1, PURE).in("water", 2).outDefault("higher_alcohol_fatty_ester_complex", 3).mode("blending").method("emulsifying")
            .add("higher_alcohol_fatty_ester_complex");
        hydrogenation("catalysts/nickel").in("stearic_acid").in("hydrogen", 2).out("stearyl_alcohol").add("stearyl_alcohol");
        // 454: as 171, neutralized to pH 4.4 (one ammonia)
        x(ABSORBING).in("phosphoric_acid").in("ammonia").out("monoammonium_phosphate").add("monoammonium_phosphate");
        // 456: acrolein and ethyl vinyl ether (Diels–Alder), hydrolysed in the same charge, made up to 50%
        m23(null).in("acrolein").in("ethyl_vinyl_ether").in("water").out("glutaraldehyde").outMb("ethanol", 800).add("glutaraldehyde");
    }
}
