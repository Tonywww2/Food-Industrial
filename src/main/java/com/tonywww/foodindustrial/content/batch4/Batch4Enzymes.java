package com.tonywww.foodindustrial.content.batch4;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.CULTURING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SOLID_FERMENTING;

/**
 * The fourth content batch, group F (I30, additives/15 §F): the enzymes 374–423, the engineered strains
 * (P94, a host culture, a donor sample and one expression vector each), the fractionated enzymes (P95)
 * and the immobilized enzymes (P96, an item each, 2026-11-11, user). Enzymes carry no purity.
 * <p>
 * P95 runs in the stirred reactor with the ion-exchange resin as catalyst, the eluent choosing the
 * fraction: the enzymes are items and the adsorption column takes only fluids (additives/04 §实现约定).
 */
public final class Batch4Enzymes extends Batch {
    private Batch4Enzymes(RecipeGen.Output out) {
        super(out, "batch4");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch4Enzymes(out).generate());
    }

    private void generate() {
        recombination();
        fermentedEnzymes();
        fractionated();
        tissueEnzymes();
        immobilized();
    }

    // ---- P94 genetic recombination ----

    /**
     * Donors stand for the gene sources by habitat (game settings): rumen bacteria straw, Bacillus brevis and
     * B. thuringiensis soil, Pseudomonas leaf, P. stutzeri water plants, thermophiles compost, Aeromonas salmonicida
     * raw fish, Chondrus crispus the red alga, calf chymosin the abomasum.
     */
    private void recombination() {
        // the expression vector prepared from E. coli colonies (game abstraction)
        r(CULTURING).in("cultures/e_coli", 1).in("chemicals/agar", 1).out("expression_vector", 1).out("e_coli", 1).mode("recombination")
            .plateWeights(3, 1).add("expression_vector");
        engineered("engineered_psicose_epimerase", "bacillus_subtilis", "samples/straw");
        engineered("engineered_aldc", "bacillus_subtilis", "samples/soil");
        engineered("engineered_gcat", "bacillus_licheniformis", "materials/raw_fish");
        engineered("engineered_cgtase", "bacillus_licheniformis", "samples/compost");
        engineered("engineered_hexose_oxidase", "hansenula", "materials/red_algae_carrageenan");
        engineered("engineered_phospholipase_a1", "aspergillus_oryzae", "samples/soil");
        engineered("engineered_phospholipase_c", "bacillus_licheniformis", "samples/soil");
        engineered("engineered_pi_plc", "bacillus_licheniformis", "samples/leaf");
        engineered("engineered_maltogenic_amylase", "bacillus_subtilis", "samples/compost");
        engineered("engineered_maltotetraohydrolase", "bacillus_licheniformis", "samples/aquatic_plant");
        engineered("engineered_alpha_glucosidase", "trichoderma", "cultures/aspergillus_niger");
        engineered("engineered_laccase", "aspergillus_oryzae", "samples/compost");
        engineered("engineered_chymosin", "e_coli", "materials/abomasum");
        engineered("yeast_isp", "yeast", "materials/raw_fish");
        // 181 B: E. coli engineered for cysteine (game simplification: metabolic engineering, no donor); the threonine,
        // tyrosine and tryptophan producers of batch 5 (N359, 546, 547) share the plate, the colony picked by chance
        // (probability plate, additives/18 §1.5, 2026-11-12)
        r(CULTURING).in("cultures/e_coli", 1).in("chemicals/agar", 1).in("cultures/expression_vector", 1).out("e_coli_cysteine", 1).src(20)
            .out("e_coli_threonine", 1).src(20).out("e_coli_tyrosine", 1).src(20).out("e_coli_tryptophan", 1).src(20).mode("recombination")
            .plateWeights(1, 1, 1, 1).add("e_coli_cysteine");
    }

    /**
     * Host culture, agar, donor and one expression vector: the engineered strain or, on failure, the host, 1 : 1
     * (additives/18 §1.5, 2026-11-12).
     */
    private void engineered(String strain, String host, String donor) {
        r(CULTURING).in("cultures/" + host, 1).in("chemicals/agar", 1).in(donor, 1).in("cultures/expression_vector", 1).out(strain, 1)
            .src(20).out(host, 1).mode("recombination").plateWeights(1, 1).add(strain);
    }

    // ---- enzymes from the aseptic fermenter: one culture, a medium (inducer) per enzyme ----

    private void fermentedEnzymes() {
        enzyme("psicose_epimerase", "engineered_psicose_epimerase", "chemicals/glucose", 2);
        enzyme("alpha_galactosidase", "aspergillus_niger", "materials/soybean", 2);
        enzyme("aldc", "engineered_aldc", "chemicals/glucose", 2);
        enzyme("beta_fructosyltransferase", "aspergillus_fijiensis", "chemicals/sucrose", 2);
        enzyme("beta_glucanase", "bacillus_subtilis", "materials/barley", 2);
        enzyme("arabinofuranosidase", "aspergillus_niger", "materials/beetroot", 2);
        enzyme("aminopeptidase", "aspergillus_oryzae", "materials/soybean", 2);
        enzyme("hemicellulase", "aspergillus_niger", "materials/hemicellulose", 2);
        enzyme("microbial_protease", "bacillus_subtilis", "materials/okara", 2);
        enzyme("protein_glutaminase", "chryseobacterium", "chemicals/glucose", 2);
        enzyme("gcat", "engineered_gcat", "chemicals/glucose", 2);
        enzyme("glutaminase", "bacillus_amyloliquefaciens", "chemicals/glucose", 2);
        enzyme("nuclease", "penicillium_citrinum_nontoxic", "chemicals/glucose", 2);
        enzyme("trehalase", "trichoderma", "materials/spent_yeast", 2);
        enzyme("cgtase", "engineered_cgtase", "chemicals/starch", 2);
        enzyme("hexose_oxidase", "engineered_hexose_oxidase", "chemicals/glucose", 2);
        enzyme("inulinase", "aspergillus_niger", "materials/jerusalem_artichoke", 2);
        enzyme("chitosanase", "bacillus_subtilis", "chemicals/chitosan", 1);
        enzyme("phospholipase_a1", "engineered_phospholipase_a1", "chemicals/glucose", 2);
        enzyme("phospholipase_c", "engineered_phospholipase_c", "chemicals/glucose", 2);
        enzyme("pi_plc", "engineered_pi_plc", "chemicals/glucose", 2);
        enzyme("maltogenic_amylase", "engineered_maltogenic_amylase", "chemicals/starch", 2);
        enzyme("maltotetraohydrolase", "engineered_maltotetraohydrolase", "chemicals/starch", 2);
        enzyme("xylanase", "trichoderma", "materials/hemicellulose", 2);
        enzyme("glucose_isomerase", "streptomyces_rubiginosus", "chemicals/xylose", 1);
        enzyme("beta_glucosidase", "aspergillus_niger", "materials/straw", 2);
        enzyme("alpha_glucosidase", "engineered_alpha_glucosidase", "chemicals/glucose", 2);
        enzyme("laccase", "engineered_laccase", "chemicals/glucose", 2);
        enzyme("lysophospholipase", "aspergillus_niger", "chemicals/lecithin", 1);
        enzyme("carboxypeptidase", "aspergillus_niger", "chemicals/gluten", 2);
        enzyme("alpha_glucanotransferase", "aeribacillus", "chemicals/starch", 2);
        enzyme("asparaginase", "aspergillus_oryzae", "materials/potato", 2);
        enzyme("dextranase", "chaetomium", "chemicals/sucrose", 2);
        enzyme("sucrose_fructosyltransferase", "aspergillus_oryzae", "chemicals/sucrose", 2);
        enzyme("phytase", "aspergillus_niger", "materials/corn", 2);
        enzyme("transglucosidase", "aspergillus_niger", "chemicals/maltose", 2);
        // 179: Pseudomonas dacunhae cells grown on aspartic acid (the decarboxylase induced)
        enzyme("aspartate_decarboxylase", "pseudomonas_dacunhae", "chemicals/l_aspartic_acid", 1);
        // 218: β-glucosidase also from Aspergillus niger on steamed wheat in the koji room (T2 route)
        r(SOLID_FERMENTING).in("materials/steamed_wheat", 4).fin("water", B).cat("cultures/aspergillus_niger", CULTURE_RUNS)
            .out("beta_glucosidase", 2).mode("koji_tray").add("beta_glucosidase_koji");
        // 402: barley steeped and sprouted (malt), steeped in warm water to the malt carbohydrase
        r(SOLID_FERMENTING).in("materials/barley", 4).fin("water", B).out("barley_malt", 4).mode("koji_tray").time(2400)
            .add("barley_malt");
        r(EXTRACTING).in("enzymes/barley_malt", 2).fin("water", B).out("malt_carbohydrase", 2).add("malt_carbohydrase");
    }

    /** An enzyme made by a culture on a medium in the aseptic fermenter (T4), as I20. */
    private void enzyme(String enzyme, String culture, String medium, int count) {
        r(FERMENTING).in(medium, count).fin("water", 2 * B).cat("cultures/" + culture, CULTURE_RUNS, PURE).out(enzyme, 4).sterile()
            .mode("aerated").add(enzyme + "_from_" + culture);
    }

    // ---- P95 fractionation on ion-exchange resin (game simplification) ----

    private void fractionated() {
        // 385, 388, 389 from pectinase: water elutes polygalacturonase and pectin lyase, brine pectinesterase
        r(REACTING).in("enzymes/pectinase", 4).fin("water", B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("polygalacturonase", 2)
            .out("pectin_lyase", 1).tier(2).add("polygalacturonase");
        r(REACTING).in("enzymes/pectinase", 4).fin("brine", B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("pectinesterase", 2)
            .out("pectin_lyase", 1).tier(2).add("pectinesterase");
        // 390 B from glucose oxidase; 421 from lipase
        r(REACTING).in("enzymes/glucose_oxidase", 4).fin("water", B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS)
            .out("glucose_oxidase", 3).out("catalase", 1).tier(2).add("catalase_fractionation");
        r(REACTING).in("enzymes/lipase", 4).fin("water", B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("lipase", 3)
            .out("esterase", 1).tier(2).add("esterase");
    }

    // ---- enzymes from animal and plant tissue ----

    private void tissueEnzymes() {
        // 390 A: beef liver minced, extracted cold, salted out, dried cool
        r(EXTRACTING).in("materials/liver", 4).in("chemicals/ammonium_sulfate", 1).fin("water", B).out("catalase", 2).tier(2)
            .add("catalase_from_liver");
        // 418, 419: pancreas in cold dilute sulfuric acid (the pancreatic extract), the zymogens salted out with ammonium
        // sulfate and activated; 397: crude phospholipase salted out; 399: phospholipase A2 survives acid heating
        r(EXTRACTING).in("materials/pancreas", 4).fin("water", B).fin("sulfuric_acid", 100).fout("pancreatic_extract", B)
            .add("pancreatic_extract");
        r(REACTING).in("chemicals/ammonium_sulfate", 2).fin("pancreatic_extract", B).out("trypsin", 1).out("chymotrypsin", 1).tier(2)
            .add("trypsin");
        r(REACTING).in("chemicals/salt", 2).fin("pancreatic_extract", B).out("phospholipase", 2).tier(2).add("phospholipase");
        r(REACTING).in("chemicals/ammonium_sulfate", 1).fin("pancreatic_extract", B).fin("hydrochloric_acid", 100).out("phospholipase_a2", 2)
            .temp("heated").tier(2).add("phospholipase_a2");
        // 415: gastric mucosa self-digested at pH 2, salted out
        r(REACTING).in("materials/gastric_mucosa", 4).in("chemicals/ammonium_sulfate", 1).fin("hydrochloric_acid", 100).fin("water", B)
            .out("pepsin", 2).temp("heated").tier(2).add("pepsin");
        // 416: fig latex filtered and dried cool
        r(DRYING).fin("ficus_latex", B).out("ficin", 1).add("ficin");
    }

    // ---- P96 immobilization (2026-11-11, user: an item each) ----

    private void immobilized() {
        immobilizing("aspartate_decarboxylase", "immobilized_aspartate_decarboxylase", true);
        immobilizing("lipase", "immobilized_lipase", false);
        immobilizing("psicose_epimerase", "immobilized_psicose_epimerase", false);
        immobilizing("glucose_isomerase", "immobilized_glucose_isomerase", false);
        // 406: glucose isomerized on the column to high-fructose syrup (about 42 % fructose), the fructose taken out
        r(REACTING).in("chemicals/glucose", 4).fin("water", B).cat("catalysts/immobilized_glucose_isomerase", IMMOBILIZED_RUNS)
            .out("fructose", 2).out("glucose", 2).temp("heated").add("fructose_by_isomerase");
    }
}
