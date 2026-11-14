package com.tonywww.foodindustrial.content.batch5;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SEPARATING;

/**
 * Optional refining of the fifth batch's products (I31, as 18 §1.4 可选精制 for the flavours): the core
 * recipes give the source value, these take the products up to food grade. One recipe per product, by what
 * the product is: recrystallized from water or ethanol, washed, annealed in hydrogen, purified on a resin,
 * steam-stripped, distilled or molecular-distilled (the purification steps the 17 routes name, one each).
 */
public final class Batch5Refining extends Batch {
    /** Soluble salts and small organic molecules: recrystallized from water. */
    private static final String[] FROM_WATER = {
        "monoammonium_phosphate", "sodium_selenite", "sodium_selenate", "sodium_iodide", "chromium_sulfate", "chromium_chloride",
        "sodium_molybdate", "ammonium_molybdate", "manganese_chloride", "thiamine_mononitrate", "riboflavin_phosphate_sodium",
        "pyridoxine_hydrochloride", "pyridoxal_phosphate", "cyanocobalamin", "cyanocobalamin_hydrochloride", "hydroxocobalamin",
        "potassium_ascorbate", "magnesium_ascorbyl_phosphate", "calcium_d_pantothenate", "sodium_d_pantothenate", "d_biotin",
        "ferrous_lactate", "ferrous_bisglycinate", "calcium_l_lactate", "calcium_l_threonate", "calcium_glycinate", "calcium_aspartate",
        "calcium_glycerophosphate", "zinc_glycinate", "zinc_lactate", "zinc_acetate", "magnesium_gluconate", "copper_gluconate",
        "manganese_gluconate", "manganese_citrate", "potassium_gluconate", "methylselenocysteine", "choline_chloride", "choline_bitartrate",
        "myo_inositol", "l_lysine_hydrochloride", "l_lysine_l_aspartate", "l_carnitine", "l_carnitine_l_tartrate", "l_methionine",
        "l_tyrosine", "l_tryptophan", "raffinose", "cytidylic_acid", "uridylic_acid", "uridylic_disodium", "cytidylic_disodium",
        "levomefolate_glucosamine", "levomefolate_calcium", "sodium_ferrous_citrate", "magnesium_l_threonate", "fucosyllactose",
        "lacto_n_neotetraose", "sialyllactose_sodium",
        // earlier materials that are also flavours (18, 19 ST26), so food grade now matters
        "glutamic_acid", "l_phenylalanine"};
    /** Lipophilic solids: recrystallized from ethanol. */
    private static final String[] FROM_ETHANOL = {
        "ergocalciferol", "d_alpha_tocopheryl_succinate", "dl_alpha_tocopheryl_succinate", "menaquinone_7", "tea_polyphenol_palmitate",
        "calcium_stearoyl_lactylate", "lauric_acid", "capric_acid", "palmitic_acid"};
    /** Insoluble precipitates: washed with water. */
    private static final String[] WASHED = {
        "magnesium_trisilicate", "magnesium_oxide", "zinc_oxide", "zinc_carbonate", "copper_carbonate", "ferrous_carbonate",
        "manganese_carbonate", "magnesium_hydrogen_phosphate", "ferrous_fumarate", "calcium_citrate_malate", "zinc_citrate", "copper_citrate",
        "ferric_citrate_powder", "ferrous_citrate", "ferrous_succinate"};
    /** Polymers, gums and solids that ethanol does not dissolve: washed with ethanol. */
    private static final String[] ETHANOL_WASHED = {
        "polyacrylamide", "styrene_butadiene_rubber", "polyethylene", "butyl_rubber", "vinyl_acetate_laurate_copolymer", "terpene_resin",
        "polyethylene_wax", "selenium_carrageenan", "yeast_beta_glucan", "ammonium_alginate", "inulin", "hemin",
        "tocopheryl_calcium_succinate"};
    /** Iron powders: annealed in hydrogen (17 494). */
    private static final String[] ANNEALED = {"reduced_iron", "carbonyl_iron", "electrolytic_iron"};
    /** Proteins, peptides and oligosaccharides: purified on a resin, nine tenths of the impurities out. */
    private static final String[] ON_RESIN = {
        "heme_iron", "selenoprotein", "lactoferrin", "casein_calcium_peptide", "casein_phosphopeptide",
        "galactooligosaccharides", "fructooligosaccharides"};
    /** Rosin esters: steam-stripped (P63). */
    private static final String[] STRIPPED = {
        "hydrogenated_rosin_glycerol_ester", "wood_rosin_glycerol_ester", "dimerized_rosin_glycerol_ester",
        "hydrogenated_rosin_pentaerythritol_ester", "polymerized_rosin_glycerol_ester"};
    /** Liquids: distilled (the pot still takes the main cut, the column more). */
    private static final String[] DISTILLED = {
        "propane", "n_butanol", "isopropanol", "diethyl_ether", "n_hexane", "glutaraldehyde", "gp_polyether", "gpe_polyether",
        "bape_polyether", "ppe_polyether", "sucrose_polyoxypropylene_ether", "polybutene", "hydrogenated_rosin_methyl_ester",
        "caprylic_acid", "hexanoic_acid", "oleic_acid"};
    /** Heat-sensitive oils and vitamins: molecular distillation. */
    private static final String[] MOLECULAR = {
        "retinyl_palmitate", "dl_alpha_tocopherol", "d_alpha_tocopheryl_acetate", "dl_alpha_tocopheryl_acetate", "phylloquinone",
        "gamma_linolenic_acid", "dioleoyl_palmitoyl_glycerol", "arachidonic_acid_oil"};

    private Batch5Refining(RecipeGen.Output out) {
        super(out, "batch5");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch5Refining(out).generate());
    }

    private void generate() {
        for (String id : FROM_WATER) {
            x(CRYSTALLIZING).in(id, 4).in("water").outDefault(id, 3).add(id + "_refining");
        }
        for (String id : FROM_ETHANOL) {
            x(CRYSTALLIZING).in(id, 4).in("ethanol").outDefault(id, 3).outMb("ethanol", 800).add(id + "_refining");
        }
        for (String id : WASHED) {
            x(SEPARATING).in(id, 4).in("water").outDefault(id, 3).add(id + "_refining");
        }
        for (String id : ETHANOL_WASHED) {
            x(EXTRACTING).in(id, 4).in("ethanol").outDefault(id, 3).outMb("ethanol", 800).add(id + "_refining");
        }
        for (String id : ANNEALED) {
            m27("fluidized_bed").in(id, 4).in("hydrogen").outDefault(id, 4).add(id + "_refining");
        }
        for (String id : ON_RESIN) {
            m14().in(id, 4).in("water").cat("catalysts/macroporous_resin", ENZYME_RUNS).outDefault(id, 3).rm(90).add(id + "_refining");
        }
        for (String id : STRIPPED) {
            x(DISTILLING).in(id, 4).in("water").outDefault(id, 3).mode("steam_distillation").add(id + "_refining");
        }
        for (String id : DISTILLED) {
            x(DISTILLING).in(id).outDefaultMb(id, 750).add(id + "_refining");
        }
        for (String id : MOLECULAR) {
            x(DISTILLING).in(id).outDefaultMb(id, 900).method("molecular_distillation").add(id + "_refining");
        }
        // talc washed with dilute acid (carbonates and iron out)
        m14acid().in("talc", 4).in("hydrochloric_acid").outDefault("talc", 3).add("talc_refining");
    }
}
