package com.tonywww.foodindustrial.content.batch5;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CULTURING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The fifth content batch, group C other nutrients and announced additions (I31, additives/17 §C 其他营养成分,
 * 公告增补, 531–563), with the new strains and enzymes (17 新增菌种与酶): mutants on the mutagenesis plate,
 * engineered E. coli by recombination with an expression vector (as I30), soil strains on the isolation plates
 * ({@code Cultures}).
 */
public final class Batch5Nutrients extends Batch {
    private Batch5Nutrients(RecipeGen.Output out) {
        super(out, "batch5");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch5Nutrients(out).generate());
    }

    private void generate() {
        strains();
        cholines();
        aminoAcids();
        lipids();
        saccharides();
        proteinsAndNucleotides();
        announced();
    }

    // ---- 17 新增菌种与酶 ----

    private void strains() {
        // mutants of Corynebacterium glutamicum on one probability plate (additives/18 §1.5): lysine (homoserine
        // auxotroph, AEC resistant), leucine, proline, arginine and glutamine producers (11 问题 39)
        x(CULTURING).in("cultures/corynebacterium").in("agar").out("corynebacterium_lysine").src(20).out("corynebacterium_leucine").src(20)
            .out("corynebacterium_proline").src(20).out("corynebacterium_arginine").src(20).out("corynebacterium_glutamine").src(20)
            .mode("mutagenesis").plateWeights(1, 1, 1, 1, 1).add("corynebacterium_mutants");
        // mutants of Bacillus subtilis: transketolase-deficient ribose producer (S0077), acetoin producer (S0204 A)
        x(CULTURING).in("cultures/bacillus_subtilis").in("agar").out("bacillus_subtilis_ribose").src(20).out("bacillus_subtilis_acetoin").src(20)
            .mode("mutagenesis").plateWeights(1, 1).add("bacillus_subtilis_mutants");
        // an L(+)-lactic acid strain of the lactic acid bacteria (495 A), or the parent strain on failure
        x(CULTURING).in("cultures/lactobacillus").in("agar").out("lactobacillus_l_lactate").src(20).out("lactobacillus")
            .mode("mutagenesis").plateWeights(1, 1).add("lactobacillus_l_lactate");
        // engineered E. coli: the tyrosine and tryptophan producers share the plate of e_coli_cysteine (batch 4);
        // the HMO strains with a sample as gene donor
        recombined("engineered_2fl", "samples/soil");
        recombined("engineered_lnnt", "samples/compost");
        recombined("engineered_3sl", "samples/leaf");
        // D-pantolactone hydrolase in Fusarium mycelium, entrapped in calcium alginate (P96); aminoacylase from A. oryzae
        x(FERMENTING).in("glucose", 2).in("water", 2).in("sodium_alginate").cat("cultures/fusarium", CULTURE_RUNS, PURE).out("d_lactonase", 2)
            .sterile().mode("aerated").add("d_lactonase");
        x(FERMENTING).in("glucose", 2).in("water", 2).in("sodium_alginate").cat("cultures/aspergillus_oryzae", CULTURE_RUNS, PURE)
            .out("aminoacylase", 2).sterile().mode("aerated").add("aminoacylase");
    }

    private void recombined(String strain, String donor) {
        x(CULTURING).in("cultures/e_coli").in("agar").in(donor).in("cultures/expression_vector").out(strain).src(20).out("e_coli")
            .mode("recombination").plateWeights(1, 1).add(strain);
    }

    // ---- 531–533 ----

    private void cholines() {
        x(REACTING).in("trimethylamine").in("hydrochloric_acid").in("ethylene_oxide").out("choline_chloride").mode("ethoxylation")
            .pressure("pressurized").temp("heated").add("choline_chloride");
        x(REACTING).in("trimethylamine").in("tartaric_acid").in("ethylene_oxide").in("water").out("choline_bitartrate").mode("ethoxylation")
            .pressure("pressurized").temp("heated").add("choline_bitartrate");
        // 533: phytic acid hydrolysed hot under pressure, or by phytase; phosphate taken out as calcium phosphate
        m23(null).in("phytic_acid").in("water", 6).in("slaked_lime", 3).temp("high").out("myo_inositol").out("tricalcium_phosphate", 3)
            .add("myo_inositol");
        m14().in("phytic_acid").in("water", 6).in("slaked_lime", 3).cat("enzymes/phytase", ENZYME_RUNS).out("myo_inositol")
            .out("tricalcium_phosphate", 3).add("myo_inositol_by_phytase");
    }

    // ---- 534–537, 545–547 ----

    private void aminoAcids() {
        // 534: the lysine of S0344 neutralized with hydrochloric acid (the resin step merged)
        m14().in("l_lysine").in("hydrochloric_acid").out("l_lysine_hydrochloride").add("l_lysine_hydrochloride");
        m14().in("l_lysine").in("l_aspartic_acid").in("ethanol").out("l_lysine_l_aspartate").outMb("ethanol", 800).add("l_lysine_l_aspartate");
        // 536: γ-butyrobetaine (lactone opened, esterified, quaternized, saponified), turned over by the HK4 cells
        m23(null).in("gamma_butyrolactone").in("hydrogen_chloride").in("ethanol").in("trimethylamine").in("sodium_hydroxide")
            .out("gamma_butyrobetaine").out("salt").add("gamma_butyrobetaine");
        x(FERMENTING).in("gamma_butyrobetaine", 2).in("water", 2).cat("cultures/carnitine_hk4", CULTURE_RUNS, PURE).out("l_carnitine", 2)
            .sterile().mode("aerated").add("l_carnitine");
        m14().in("l_carnitine", 2).in("tartaric_acid").in("ethanol").out("l_carnitine_l_tartrate").outMb("ethanol", 800).add("l_carnitine_l_tartrate");
        // 545: DL-methionine (S0327) acetylated, the L form freed by immobilized aminoacylase, the D form racemized back
        m14().in("dl_methionine").in("acetic_anhydride").in("sodium_hydroxide").out("acetyl_dl_methionine").out("acetic_acid")
            .add("acetyl_dl_methionine");
        m14().in("acetyl_dl_methionine", 2).in("water").cat("enzymes/aminoacylase", IMMOBILIZED_RUNS).out("l_methionine")
            .out("acetyl_d_methionine").out("acetic_acid").add("l_methionine");
        m14().in("acetyl_d_methionine").inMb("acetic_anhydride", 100).out("acetyl_dl_methionine").add("acetyl_dl_methionine_racemization");
        // 546, 547: engineered E. coli fed glucose
        x(FERMENTING).in("glucose", 2).in("ammonium_sulfate").in("water", 2).cat("cultures/e_coli_tyrosine", CULTURE_RUNS, PURE).out("l_tyrosine")
            .sterile().mode("aerated").add("l_tyrosine");
        x(FERMENTING).in("glucose", 2).in("ammonium_sulfate").in("water", 2).cat("cultures/e_coli_tryptophan", CULTURE_RUNS, PURE)
            .out("l_tryptophan").sterile().mode("aerated").add("l_tryptophan");
    }

    // ---- 538, 540, 541 ----

    private void lipids() {
        x(PRESSING).in("materials/evening_primrose_seed", 8).outMb("evening_primrose_oil", 250).add("evening_primrose_oil");
        // 538: saponified and acidified (merged), the saturated and monounsaturated acids taken out with urea (P102)
        x(CRYSTALLIZING).in("evening_primrose_oil").in("urea").in("methanol").outMb("gamma_linolenic_acid", 300).outMb("methanol", 750)
            .temp("cooling").add("gamma_linolenic_acid");
        // 540: palm stearin acidolysed with oleic acid on the sn-1,3 lipase (the fractionation merged)
        m14().in("palm_oil").in("oleic_acid", 2).cat("catalysts/immobilized_lipase", IMMOBILIZED_RUNS).out("dioleoyl_palmitoyl_glycerol")
            .out("palmitic_acid", 2).add("dioleoyl_palmitoyl_glycerol");
        // 541: Mortierella mycelium grown, dried and extracted (merged)
        x(FERMENTING).in("glucose", 2).in("materials/soybean").in("water", 2).cat("cultures/mortierella", CULTURE_RUNS, PURE)
            .out("arachidonic_acid_oil").sterile().mode("aerated").add("arachidonic_acid_oil");
    }

    // ---- 539, 548–550 ----

    private void saccharides() {
        m14().in("inulin").in("water").cat("enzymes/inulinase", ENZYME_RUNS).out("fructooligosaccharides").add("fructooligosaccharides");
        m14().in("lactose", 2).in("water").cat("enzymes/lactase", ENZYME_RUNS).out("galactooligosaccharides").out("glucose").add("galactooligosaccharides");
        // 549: chicory roots leached hot, limed and carbonated
        m14().in("materials/chicory_root", 4).in("water").in("slaked_lime").in("carbon_dioxide").out("inulin").out("calcium_carbonate")
            .add("inulin");
        // 550: beet molasses (the sugar boiled out of beetroot juice), raffinose cut on the resin
        x(CRYSTALLIZING).in("beetroot_juice", 2).in("slaked_lime").out("sucrose").outMb("beet_molasses", 500).mode("evaporative_crystallization")
            .add("beet_molasses");
        m14().in("beet_molasses").in("water").cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("raffinose").add("raffinose");
    }

    // ---- 542–544, 551–554 ----

    private void proteinsAndNucleotides() {
        // 542: skimmed milk over a weak cation resin, eluted with brine (the ultrafiltration and freeze drying merged)
        m14().in("materials/milk", 4).in("brine").cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("lactoferrin").add("lactoferrin");
        // 543, 544: casein digested by trypsin, the phosphopeptides precipitated with calcium and ethanol; decalcified on resin
        m14().in("casein").in("water").in("calcium_chloride").in("ethanol").cat("enzymes/trypsin", ENZYME_RUNS).out("casein_calcium_peptide")
            .outMb("ethanol", 800).add("casein_calcium_peptide");
        m14().in("casein_calcium_peptide").in("water").cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("casein_phosphopeptide")
            .add("casein_phosphopeptide");
        // 551, 552: the cytidylic and uridylic fractions of the yeast RNA hydrolysate (73), cut on the resin
        m14().in("nucleotide_hydrolysate", 2).cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("cytidylic_acid").out("uridylic_acid")
            .add("cytidylic_uridylic_acids");
        m14().in("uridylic_acid").in("sodium_hydroxide", 2).in("ethanol").out("uridylic_disodium").outMb("ethanol", 800).add("uridylic_disodium");
        m14().in("cytidylic_acid").in("sodium_hydroxide", 2).in("ethanol").out("cytidylic_disodium").outMb("ethanol", 800).add("cytidylic_disodium");
    }

    // ---- 555–563 ----

    private void announced() {
        // 555: natto Bacillus on soybean and glycerol
        x(FERMENTING).in("materials/soybean", 2).in("glycerol").in("water", 2).cat("cultures/bacillus_subtilis", CULTURE_RUNS, PURE)
            .out("menaquinone_7").sterile().mode("aerated").add("menaquinone_7");
        // 556, 557: folic acid hydrogenated, the 6S form enriched (game simplification), methylated and reduced
        hydrogenation("catalysts/palladium").in("folic_acid").in("hydrogen", 2).out("tetrahydrofolic_acid").add("tetrahydrofolic_acid");
        hydrogenation("catalysts/palladium").in("tetrahydrofolic_acid").in("formaldehyde").in("hydrogen").out("levomefolic_acid")
            .add("levomefolic_acid");
        m14acid().in("chitin").in("hydrochloric_acid", 2).out("glucosamine_hydrochloride").outMb("acetic_acid", 500).add("glucosamine_hydrochloride");
        m14().in("levomefolic_acid").in("glucosamine_hydrochloride", 2).in("sodium_hydroxide", 2).out("levomefolate_glucosamine")
            .add("levomefolate_glucosamine");
        m14().in("levomefolic_acid").in("calcium_chloride").in("sodium_hydroxide", 2).out("levomefolate_calcium").out("salt", 2)
            .add("levomefolate_calcium");
        // 558: sodium citrate and ferrous sulfate under nitrogen, precipitated with ethanol
        m14inert().in("trisodium_citrate", 2).in("ferrous_sulfate").in("ethanol").out("sodium_ferrous_citrate").out("sodium_sulfate")
            .outMb("ethanol", 800).add("sodium_ferrous_citrate");
        // 559: calcium L-threonate (496) exchanged with basic magnesium carbonate and oxalic acid
        m14().in("calcium_l_threonate", 5).in("magnesium_carbonate").in("oxalic_acid", 5).out("magnesium_l_threonate", 5)
            .out("calcium_oxalate", 5).add("magnesium_l_threonate");
        // 560: yeast autolysed, the walls freed of protein and mannan, washed with acid and ethanol
        m14().in("materials/spent_yeast", 4).in("sodium_hydroxide").in("hydrochloric_acid").in("ethanol").out("yeast_beta_glucan")
            .outMb("ethanol", 800).add("yeast_beta_glucan");
        // 561–563: engineered E. coli fed lactose and glycerol; the sialyllactose neutralized to its sodium salt
        x(FERMENTING).in("lactose").in("glucose").in("glycerol").in("water", 2).cat("cultures/engineered_2fl", CULTURE_RUNS, PURE)
            .out("fucosyllactose").sterile().mode("aerated").add("fucosyllactose");
        x(FERMENTING).in("lactose").in("glucose").in("glycerol").in("water", 2).cat("cultures/engineered_lnnt", CULTURE_RUNS, PURE)
            .out("lacto_n_neotetraose").sterile().mode("aerated").add("lacto_n_neotetraose");
        x(FERMENTING).in("lactose").in("sodium_hydroxide").in("glycerol").in("water", 2).cat("cultures/engineered_3sl", CULTURE_RUNS, PURE)
            .out("sialyllactose_sodium").sterile().mode("aerated").add("sialyllactose_sodium");
    }
}
