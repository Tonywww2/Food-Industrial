package com.tonywww.foodindustrial.content.batch5;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The fifth content batch, group C vitamins (I31, additives/17 §C 维生素, 460–481) and their intermediates.
 * Several steps of a route are one recipe on the device of its hardest step (11 问题 40); toxic reagents are
 * named only (11 问题 5).
 */
public final class Batch5Vitamins extends Batch {
    private Batch5Vitamins(RecipeGen.Output out) {
        super(out, "batch5");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch5Vitamins(out).generate());
    }

    private void generate() {
        fatSoluble();
        tocopherols();
        vitaminK();
        bVitamins();
        cobalamins();
        pantothenates();
        biotin();
    }

    // ---- 460, 461 ----

    private void fatSoluble() {
        // 460: retinol esterified with palmitic acid on immobilized lipase in hexane, under nitrogen
        m14inert().in("retinol").in("palmitic_acid").in("n_hexane").cat("catalysts/immobilized_lipase", IMMOBILIZED_RUNS)
            .out("retinyl_palmitate").outMb("n_hexane", 750).add("retinyl_palmitate");
        // 461: spent yeast saponified, the unsaponifiable extracted (ergosterol); opened by UV, isomerized by heat
        m14().in("materials/spent_yeast", 4).in("potassium_hydroxide").in("ethanol").in("n_hexane").out("ergosterol").outMb("n_hexane", 750)
            .add("ergosterol");
        x(REACTING).in("ergosterol").in("n_hexane").out("ergocalciferol").outMb("n_hexane", 750).method("ultraviolet").temp("cooling").tier(2)
            .add("ergocalciferol");
    }

    // ---- 462–468 ----

    private void tocopherols() {
        // 462: the mixed tocopherols (65) methylated on the free ring positions (aminoalkylation and hydrogenolysis in one)
        hydrogenation("catalysts/palladium").in("alpha_tocopherol").in("formaldehyde").in("hydrogen").outDefault("alpha_tocopherol", 1)
            .add("alpha_tocopherol_methylation");
        // 463: trimethylhydroquinone (m-cresol methylated, oxidized, hydrogenated) and isophytol (pseudoionone extended with
        // acetylene and diketene, hydrogenated in one step, game simplification), condensed
        // 2,5-xylenol (S0113) methylated at its free ortho position (2026-11-12, 11 问题 41)
        m27("gas_catalysis").in("xylenol_2_5").in("methanol").cat("catalysts/alumina", 64).out("trimethylphenol").out("water")
            .add("trimethylphenol");
        m23("pressure_oxidation").in("trimethylphenol").in("oxygen").cat("catalysts/copper", 64).out("trimethylbenzoquinone").add("trimethylbenzoquinone");
        hydrogenation("catalysts/palladium").in("trimethylbenzoquinone").in("hydrogen").out("trimethylhydroquinone").add("trimethylhydroquinone");
        x(KILN).in("quicklime").in("materials/coke", 3).out("calcium_carbide").outMb("carbon_monoxide", 1000).temp("extreme")
            .add("calcium_carbide");
        m14().in("calcium_carbide").in("water", 2).out("acetylene").out("slaked_lime").add("acetylene");
        hydrogenation("catalysts/palladium").in("pseudoionone").in("acetylene").in("diketene").in("hydrogen", 5).out("phytone")
            .outMb("carbon_dioxide", 1000).add("phytone");
        hydrogenation("catalysts/palladium").in("phytone").in("acetylene").in("hydrogen").out("isophytol").add("isophytol");
        m14().in("trimethylhydroquinone").in("isophytol").in("hydrogen_chloride").out("dl_alpha_tocopherol").add("dl_alpha_tocopherol");
        // 464–468: acetylated, succinylated; the calcium salt from the succinate
        m14().in("alpha_tocopherol").in("acetic_anhydride").out("d_alpha_tocopheryl_acetate").out("acetic_acid").add("d_alpha_tocopheryl_acetate");
        m14().in("dl_alpha_tocopherol").in("acetic_anhydride").out("dl_alpha_tocopheryl_acetate").out("acetic_acid")
            .add("dl_alpha_tocopheryl_acetate");
        m14().in("alpha_tocopherol").in("succinic_anhydride").out("d_alpha_tocopheryl_succinate").add("d_alpha_tocopheryl_succinate");
        m14().in("dl_alpha_tocopherol").in("succinic_anhydride").out("dl_alpha_tocopheryl_succinate").add("dl_alpha_tocopheryl_succinate");
        m14().in("dl_alpha_tocopheryl_succinate", 2).in("slaked_lime").in("ethanol").out("tocopheryl_calcium_succinate").outMb("ethanol", 800)
            .add("tocopheryl_calcium_succinate");
    }

    // ---- 469 ----

    private void vitaminK() {
        // 2-methylnaphthalene frozen out of the wash oil with indole (FlavorManual), oxidized, hydrogenated, condensed with isophytol
        m14().in("methylnaphthalene").in("hydrogen_peroxide", 3).in("acetic_acid").out("menadione").add("menadione");
        hydrogenation("catalysts/palladium").in("menadione").in("hydrogen").out("menadiol").add("menadiol");
        m14air().in("menadiol").in("isophytol").cat("catalysts/zinc_chloride", 64).out("phylloquinone").add("phylloquinone");
    }

    // ---- 470–473, 477, 478 ----

    private void bVitamins() {
        // 470: thiamine base from the hydrochloride, made the nitrate
        m14().in("thiamine_hydrochloride").in("sodium_hydroxide", 2).in("nitric_acid").out("thiamine_mononitrate").out("salt", 2)
            .add("thiamine_mononitrate");
        // 471: phosphoryl chloride, riboflavin phosphorylated in γ-butyrolactone, hydrolysed, adjusted with caustic soda
        m14acid().in("phosphorus_trichloride", 2).in("oxygen").out("phosphoryl_chloride", 2).add("phosphoryl_chloride");
        m14acid().in("riboflavin").in("phosphoryl_chloride").in("gamma_butyrolactone").in("water").in("sodium_hydroxide")
            .out("riboflavin_phosphate_sodium").outMb("gamma_butyrolactone", 800).add("riboflavin_phosphate_sodium");
        // 472: oxazole method
        m14().in("l_alanine").in("ethanol").in("formic_acid").in("hydrochloric_acid").out("ethyl_formylalaninate").add("ethyl_formylalaninate");
        m14acid().in("ethyl_formylalaninate", 3).in("phosphoryl_chloride").in("sodium_hydroxide", 3).out("methyl_ethoxy_oxazole", 3)
            .out("phosphoric_acid").add("methyl_ethoxy_oxazole");
        m23(null).in("acetylene").in("formaldehyde", 2).cat("catalysts/copper", 64).out("butynediol").add("butynediol");
        hydrogenation("catalysts/palladium").in("butynediol").in("hydrogen").out("butenediol").add("butenediol");
        m14().in("butenediol").in("isobutyraldehyde").cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("isopropyl_dioxepine")
            .out("water").add("isopropyl_dioxepine");
        m14().in("methyl_ethoxy_oxazole").in("isopropyl_dioxepine").in("hydrochloric_acid").out("pyridoxine_hydrochloride")
            .outMb("isobutyraldehyde", 800).outMb("ethanol", 800).add("pyridoxine_hydrochloride");
        // 473: pyridoxal (manganese dioxide), protected as the Schiff base with p-phenetidine, phosphorylated, freed
        m14().in("pyridoxine_hydrochloride").in("manganese_dioxide").in("sulfuric_acid").out("pyridoxal").out("manganese_sulfate").add("pyridoxal");
        m14acid().in("pyridoxal").in("p_phenetidine").in("phosphoric_acid").out("pyridoxal_phosphate").outMb("p_phenetidine", 800)
            .add("pyridoxal_phosphate");
        // 477: ascorbic acid neutralized with potassium bicarbonate under nitrogen
        m14inert().in("ascorbic_acid").in("potassium_bicarbonate").in("water").out("potassium_ascorbate").outMb("carbon_dioxide", 1000)
            .add("potassium_ascorbate");
        // 478: phosphorylated at C2 in strong alkali, cations exchanged, neutralized with magnesium oxide
        x(REACTING).in("ascorbic_acid", 2).in("sodium_hydroxide", 2).in("phosphoryl_chloride", 2).in("magnesium_oxide", 3)
            .out("magnesium_ascorbyl_phosphate").outMb("hydrochloric_acid", 1000).temp("cooling").method("corrosion_resistant").tier(2)
            .add("magnesium_ascorbyl_phosphate");
    }

    // ---- 474–476 ----

    private void cobalamins() {
        m14().in("materials/cobalt").in("hydrochloric_acid", 2).out("cobalt_chloride").out("hydrogen").add("cobalt_chloride");
        // propionibacteria, first closed then aerated (one fermentation here); ammonia for the ammonium sulfate
        x(FERMENTING).in("glucose", 2).in("cobalt_chloride").in("ammonia").in("water", 2).cat("cultures/propionibacterium", CULTURE_RUNS, PURE)
            .out("cobalamin_broth", 2).sterile().mode("aerated").add("cobalamin_broth");
        // 474: cells broken hot with cyanide, the cobalamins made cyanocobalamin, adsorbed on resin and crystallized
        m14().in("cobalamin_broth", 2).in("hydrogen_cyanide").in("sodium_hydroxide").cat("catalysts/macroporous_resin", ENZYME_RUNS)
            .out("cyanocobalamin").add("cyanocobalamin");
        // 475: crystallized from dilute hydrochloric acid with acetone
        x(CRYSTALLIZING).in("cyanocobalamin").in("hydrochloric_acid").in("acetone").out("cyanocobalamin_hydrochloride").outMb("acetone", 800)
            .temp("cooling").add("cyanocobalamin_hydrochloride");
        // 476: the broth photolysed (no cyanide), adsorbed on resin
        x(REACTING).in("cobalamin_broth", 2).cat("catalysts/macroporous_resin", ENZYME_RUNS).out("hydroxocobalamin").method("ultraviolet")
            .tier(2).add("hydroxocobalamin");
    }

    // ---- 479, 480 ----

    private void pantothenates() {
        // DL-pantolactone: aldol, cyanohydrin, hydrolysis and ring closure; the D form hydrolysed by immobilized cells,
        // the L lactone racemized and returned
        m14().in("isobutyraldehyde").in("formaldehyde").in("hydrogen_cyanide").in("hydrochloric_acid").out("dl_pantolactone")
            .out("ammonium_chloride").add("dl_pantolactone");
        m14().in("dl_pantolactone", 2).in("water").in("sulfuric_acid").cat("enzymes/d_lactonase", IMMOBILIZED_RUNS).out("d_pantolactone")
            .outDefault("dl_pantolactone", 1).add("d_pantolactone");
        m14().in("d_pantolactone", 2).in("beta_alanine", 2).in("slaked_lime").in("methanol").out("calcium_d_pantothenate")
            .outMb("methanol", 800).add("calcium_d_pantothenate");
        m14().in("d_pantolactone").in("beta_alanine").in("sodium_hydroxide").out("sodium_d_pantothenate").add("sodium_d_pantothenate");
    }

    // ---- 481 ----

    private void biotin() {
        // bromine blown out of chlorinated bittern with steam (P103); hydrobromic acid burnt from it and absorbed
        x(DISTILLING).in("bittern", 4).in("chlorine").outMb("bromine", 100).mode("stripping").add("bromine");
        m27("burning").in("bromine").in("hydrogen").in("water").out("hydrobromic_acid", 2).add("hydrobromic_acid");
        m23(null).in("benzyl_chloride").in("ammonia", 2).out("benzylamine").out("ammonium_chloride").add("benzylamine");
        // fumaric acid route, several steps each in one recipe (game simplification); the side chain from the lactone (not
        // confirmed by the design)
        m14acid().in("fumaric_acid").in("bromine").in("benzylamine", 4).in("phosgene").out("dibenzyl_imidazolidinone_diacid")
            .out("hydrochloric_acid").add("dibenzyl_imidazolidinone_diacid");
        hydrogenation("catalysts/palladium").in("dibenzyl_imidazolidinone_diacid").in("acetic_anhydride").in("hydrogen", 2)
            .out("biotin_lactone").out("acetic_acid").add("biotin_lactone");
        m14().in("biotin_lactone").in("sodium_sulfide").out("biotin_thiolactone").add("biotin_thiolactone");
        hydrogenation("catalysts/palladium").in("biotin_thiolactone").in("magnesium").in("gamma_butyrolactone").in("hydrogen_chloride")
            .in("hydrogen").out("dibenzylbiotin").out("magnesium_chloride").add("dibenzylbiotin");
        m14().in("dibenzylbiotin").in("hydrobromic_acid", 2).out("d_biotin").add("d_biotin");
    }
}
