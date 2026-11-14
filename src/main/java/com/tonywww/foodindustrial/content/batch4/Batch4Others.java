package com.tonywww.foodindustrial.content.batch4;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.ABSORBING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.BOILING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.ELECTROLYZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GRINDING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PURIFYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The fourth content batch, group E (I30, additives/15 §E): antioxidants, preservatives, sweeteners and
 * the rest, 319–373. Routes the earlier batches already make (332 benzoic acid, 333 sorbic acid, 341
 * propionic acid, 351 sorbitol, 359 maltitol syrup, 360 sorbitol syrup, 373 nitrogen) are not written again.
 */
public final class Batch4Others extends Batch {
    private Batch4Others(RecipeGen.Output out) {
        super(out, "batch4");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch4Others(out).generate());
    }

    private void generate() {
        antioxidants();
        preservatives();
        sweeteners();
        others();
    }

    // ---- 319–331 antioxidants ----

    private void antioxidants() {
        // 319, 326: Pseudomonas fluorescens to 2-keto-D-gluconic acid, methyl ester, lactonized with methoxide, acidified
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/pseudomonas_fluorescens", CULTURE_RUNS, PURE)
            .fout("ketogluconic_acid", 2 * B).sterile().mode("aerated").add("ketogluconic_acid");
        r(REACTING).fin("ketogluconic_acid", B).fin("methanol", B).fin("sulfuric_acid", 100).out("methyl_ketogluconate", 1).src()
            .temp("heated").add("methyl_ketogluconate");
        r(REACTING).in("chemicals/methyl_ketogluconate", 1).in("chemicals/sodium_methoxide", 1).out("sodium_erythorbate", 1)
            .fout("methanol", 2 * B).src().temp("heated").add("sodium_erythorbate");
        recrystallizing("sodium_erythorbate");
        r(REACTING).in("chemicals/sodium_erythorbate", 2).fin("sulfuric_acid", B).out("erythorbic_acid", 2).out("sodium_sulfate", 1)
            .add("erythorbic_acid");
        recrystallizing("erythorbic_acid");
        // 320, 331: rice bran in dilute sulfuric acid, taken up on anion resin and eluted with caustic soda (sodium phytate),
        // stripped of sodium on cation resin (phytic acid), concentrated and decoloured
        // paddy milled: the rice and the bran (11 问题 43: paddy apart from the milled rice)
        r(GRINDING).in("materials/paddy", 4).out("rice", 3).out("rice_bran", 1).add("rice_bran");
        r(REACTING).in("materials/rice_bran", 4).in("chemicals/sodium_hydroxide", 1).fin("sulfuric_acid", 100).fin("water", B)
            .cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("sodium_phytate", 1).src().tier(2).add("sodium_phytate");
        r(REACTING).in("chemicals/sodium_phytate", 1).fin("water", B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS)
            .fout("phytic_acid", B).tier(2).add("phytic_acid");
        adsorbing("phytic_acid", "catalysts/activated_carbon", null);
        r(REACTING).in("chemicals/sodium_hydroxide", 12).fin("phytic_acid", B).out("sodium_phytate", 1).tier(2)
            .add("sodium_phytate_by_neutralizing");
        // 321: the licorice marc left by 352 (dried, merged) in ethanol: the oil-soluble flavonoids (11 问题 43)
        r(EXTRACTING).in("materials/licorice_residue", 4).fin("ethanol", B).out("licorice_antioxidant", 1).src(20).fout("ethanol", 750).src()
            .mode("solvent_extraction").add("licorice_antioxidant");
        // 322: 2 C6H8O6 + CaCO3 → C12H14CaO12 + CO2, crystallized with ethanol
        r(REACTING).in("chemicals/ascorbic_acid", 2).in("chemicals/calcium_carbonate", 1).fin("water", B).out("calcium_ascorbate", 1)
            .fout("carbon_dioxide", B).tier(2).add("calcium_ascorbate");
        recrystallizing("calcium_ascorbate");
        // 323: in concentrated sulfuric acid (lined); 330: immobilized lipase in acetone at 70 °C (P96)
        r(REACTING).in("chemicals/ascorbic_acid", 1).in("chemicals/palmitic_acid", 1).fin("sulfuric_acid", B).out("ascorbyl_palmitate", 1)
            .method("corrosion_resistant").add("ascorbyl_palmitate");
        r(REACTING).in("chemicals/ascorbic_acid", 1).in("chemicals/palmitic_acid", 1).fin("acetone", B)
            .cat("catalysts/immobilized_lipase", IMMOBILIZED_RUNS).out("ascorbyl_palmitate", 1).fout("acetone", 900).src().temp("heated")
            .add("ascorbyl_palmitate_enzymatic");
        recrystallizing("ascorbyl_palmitate");
        // 324: acrylonitrile (ammoxidation on bismuth molybdate), sodium sulfide addition, hydrolysis, esterified with lauryl
        // alcohol under vacuum
        // I31: acetonitrile is cut from the crude nitrile (19 §2 `acetonitrile`, 联产)
        r(GAS_REACTING).fin("propene", 2 * B).fin("ammonia", 2 * B).fout("acrylonitrile", 2 * B).fout("acetonitrile", 100).src()
            .mode("gas_catalysis").air().temp("heated").cat("catalysts/bismuth_molybdate", 64).add("acrylonitrile");
        r(REACTING).in("chemicals/sodium_sulfide", 1).fin("acrylonitrile", 2 * B).fin("water", 2 * B).fout("thiodipropionitrile", B)
            .out("sodium_hydroxide", 2).add("thiodipropionitrile");
        r(REACTING).fin("thiodipropionitrile", B).fin("sulfuric_acid", B).fin("water", 4 * B).out("thiodipropionic_acid", 1)
            .out("ammonium_sulfate", 1).temp("heated").add("thiodipropionic_acid");
        r(REACTING).in("chemicals/thiodipropionic_acid", 1).fin("lauryl_alcohol", 2 * B).fin("sulfuric_acid", 50)
            .out("dilauryl_thiodipropionate", 1).pressure("vacuum").temp("heated").add("dilauryl_thiodipropionate");
        recrystallizing("dilauryl_thiodipropionate");
        // 325: benzene dialkylated with propene (P72), Hock cleavage to resorcinol and acetone, acylated with hexanoic acid on
        // zinc chloride, the ketone hydrogenated (palladium, game simplification)
        // 1,3-diisopropylbenzene by alkylating cumene once more (2026-11-12, 11 问题 41)
        r(REACTING).fin("cumene", B).fin("propene", B).fout("diisopropylbenzene", B).cat("catalysts/aluminium_chloride", 64)
            .add("diisopropylbenzene");
        r(REACTING).fin("diisopropylbenzene", B).fin("oxygen", 2 * B).fin("sulfuric_acid", 50).out("resorcinol", 1).fout("acetone", 2 * B)
            .src().mode("pressure_oxidation").pressure("pressurized").temp("heated").add("resorcinol_by_hock");
        r(REACTING).in("chemicals/resorcinol", 1).fin("hexanoic_acid", B).cat("catalysts/zinc_chloride", 64).out("hexanoylresorcinol", 1)
            .temp("heated").method("corrosion_resistant").add("hexanoylresorcinol");
        r(REACTING).in("chemicals/hexanoylresorcinol", 1).fin("hydrogen", 2 * B).out("hexylresorcinol", 1).mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/palladium", 64).add("hexylresorcinol");
        recrystallizing("hexylresorcinol");
        // 327: bamboo leaves in aqueous ethanol (T2), resin and spray drying (T3)
        r(EXTRACTING).in("materials/bamboo", 8).fin("ethanol", B).out("bamboo_leaf_antioxidant", 1).src(20).fout("ethanol", 750).src()
            .mode("solvent_extraction").add("bamboo_leaf_antioxidant");
        resin("bamboo_leaf_antioxidant", "catalysts/macroporous_resin");
        sprayDrying("bamboo_leaf_antioxidant");
        // 328: EDTA, caustic soda and calcium acetate
        r(REACTING).in("chemicals/edta", 1).in("chemicals/sodium_hydroxide", 2).in("chemicals/calcium_acetate", 1).fin("water", B)
            .out("calcium_disodium_edta", 1).fout("acetic_acid", 2 * B).src().tier(2).add("calcium_disodium_edta");
        recrystallizing("calcium_disodium_edta");
        // 329 A: fresh tea leaves fermented open (polyphenol oxidase), extracted hot (crude); B: tannase under nitrogen, then
        // aerated, ethyl acetate and resin, spray dried
        r(FERMENTING).in("materials/tea", 8).fin("water", B).out("theaflavins", 1).src().air().time(2400).add("theaflavins");
        r(REACTING).in("materials/tea", 8).fin("water", B).cat("enzymes/tannase", ENZYME_RUNS).out("theaflavins", 1).src(20).air().tier(2)
            .add("theaflavins_enzymatic");
        resin("theaflavins", "catalysts/macroporous_resin");
        sprayDrying("theaflavins");
    }

    // ---- 334–349 preservatives ----

    private void preservatives() {
        // 334: propionic acid neutralized
        r(REACTING).in("chemicals/soda_ash", 1).fin("propionic_acid", 2 * B).out("sodium_propionate", 2).fout("carbon_dioxide", B)
            .tier(2).add("sodium_propionate");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("propionic_acid", B).out("sodium_propionate", 1).tier(2)
            .add("sodium_propionate_by_caustic_soda");
        recrystallizing("sodium_propionate");
        // 335, 343, 347: cumene, Hock phenol; Kolbe–Schmitt on potassium phenoxide (P47) to p-hydroxybenzoic acid; the esters
        // and their sodium salts
        r(REACTING).fin("benzene", B).fin("propene", B).fout("cumene", B).cat("catalysts/aluminium_chloride", 64).add("cumene");
        r(REACTING).fin("cumene", B).fin("oxygen", B).fin("sulfuric_acid", 50).out("phenol", 1).fout("acetone", B).src()
            .mode("pressure_oxidation").pressure("pressurized").temp("heated").add("phenol_by_hock");
        r(REACTING).in("chemicals/phenol", 2).in("chemicals/potassium_hydroxide", 2).fin("carbon_dioxide", 2 * B).fin("sulfuric_acid", B)
            .out("p_hydroxybenzoic_acid", 2).out("potassium_sulfate", 1).pressure("high").temp("heated").add("p_hydroxybenzoic_acid");
        recrystallizing("p_hydroxybenzoic_acid");
        r(REACTING).in("chemicals/p_hydroxybenzoic_acid", 1).fin("ethanol", B).fin("sulfuric_acid", 50).out("ethyl_paraben", 1)
            .temp("heated").add("ethyl_paraben");
        r(REACTING).in("chemicals/p_hydroxybenzoic_acid", 1).fin("methanol", B).fin("sulfuric_acid", 50).out("methyl_paraben", 1)
            .temp("heated").add("methyl_paraben");
        recrystallizing("ethyl_paraben");
        recrystallizing("methyl_paraben");
        r(REACTING).in("chemicals/methyl_paraben", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B).out("sodium_methyl_paraben", 1)
            .tier(2).add("sodium_methyl_paraben");
        r(REACTING).in("chemicals/ethyl_paraben", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B).out("sodium_ethyl_paraben", 1)
            .tier(2).add("sodium_ethyl_paraben");
        // 336: diketene dimerized cold on soda ash (game simplification), the sodium salt crystallized cold
        r(REACTING).in("chemicals/soda_ash", 1).fin("diketene", 4 * B).out("dehydroacetic_acid", 2).temp("cooling").add("dehydroacetic_acid");
        recrystallizing("dehydroacetic_acid");
        r(REACTING).in("chemicals/dehydroacetic_acid", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B)
            .out("sodium_dehydroacetate", 1).temp("cooling").add("sodium_dehydroacetate");
        // 337: chlorobenzene nitrated (para isomer), ethoxylated, hydrogenated, condensed with acetone on cation resin
        r(REACTING).fin("chlorobenzene", B).fin("nitric_acid", B).fin("sulfuric_acid", 200).out("p_nitrochlorobenzene", 1).temp("cooling")
            .method("corrosion_resistant").add("p_nitrochlorobenzene");
        r(REACTING).in("chemicals/p_nitrochlorobenzene", 1).in("chemicals/sodium_hydroxide", 1).fin("ethanol", B).out("p_nitrophenetole", 1)
            .out("salt", 1).temp("heated").add("p_nitrophenetole");
        r(REACTING).in("chemicals/p_nitrophenetole", 1).fin("hydrogen", 3 * B).fout("p_phenetidine", B).mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("p_phenetidine");
        r(REACTING).fin("p_phenetidine", B).fin("acetone", 2 * B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS).fout("ethoxyquin", B)
            .temp("heated").add("ethoxyquin");
        r(DISTILLING).fin("ethoxyquin", B).fout("ethoxyquin", 900).mode("vacuum_distillation").add("ethoxyquin_rectifying");
        // 338 B: kiln and fermenter gas washed in the tower
        r(ABSORBING).fin("carbon_dioxide", 2 * B).fin("water", B).fout("carbon_dioxide", 2 * B).add("carbon_dioxide_washing");
        // 339: Ullmann ether synthesis on copper at 200 °C
        r(REACTING).in("chemicals/phenol", 1).in("chemicals/sodium_hydroxide", 1).fin("chlorobenzene", B).cat("catalysts/copper", 64)
            .out("diphenyl_ether", 1).out("salt", 1).temp("heated").add("diphenyl_ether");
        recrystallizing("diphenyl_ether");
        // 340: sodium chlorate (undivided cell), chlorine dioxide from chlorate and hydrochloric acid, taken up in soda ash
        // and hydrogen peroxide as chlorite
        r(ELECTROLYZING).in("chemicals/salt", 1).fin("water", 3 * B).out("sodium_chlorate", 1).fout("hydrogen", 3 * B)
            .mode("electrooxidation").cat("catalysts/electrode", 64).add("sodium_chlorate");
        r(REACTING).in("chemicals/sodium_chlorate", 2).fin("hydrochloric_acid", 4 * B).fout("chlorine_dioxide", 2 * B).fout("chlorine", B)
            .out("salt", 2).method("corrosion_resistant").add("chlorine_dioxide");
        r(ABSORBING).in("chemicals/soda_ash", 1).fin("chlorine_dioxide", 2 * B).fin("hydrogen_peroxide", B)
            .fout("stabilized_chlorine_dioxide", 2 * B).fout("carbon_dioxide", B).add("stabilized_chlorine_dioxide");
        // 342: caprylic acid and glycerol, acid or lipase, under nitrogen (molecular distillation in group C)
        r(REACTING).fin("caprylic_acid", B).fin("glycerol", 2 * B).fin("sulfuric_acid", 50).out("glyceryl_monocaprylate", 1).src(45)
            .inert().temp("heated").add("glyceryl_monocaprylate");
        r(REACTING).fin("caprylic_acid", B).fin("glycerol", 2 * B).cat("enzymes/lipase", ENZYME_RUNS).out("glyceryl_monocaprylate", 1)
            .src(55).inert().temp("heated").add("glyceryl_monocaprylate_enzymatic");
        // 344 (optional route, phosgene): phosgene on activated carbon (P82), methyl chloroformate, dicarbonate in toluene
        r(GAS_REACTING).fin("carbon_monoxide", B).fin("chlorine", B).fout("phosgene", B).mode("gas_catalysis")
            .cat("catalysts/activated_carbon", 64).add("phosgene");
        r(REACTING).fin("phosgene", B).fin("methanol", B).fout("methyl_chloroformate", B).fout("hydrogen_chloride", B).temp("cooling")
            .add("methyl_chloroformate");
        r(REACTING).in("chemicals/sodium_hydroxide", 2).fin("methyl_chloroformate", 2 * B).fin("toluene", 100).fout("dimethyl_dicarbonate", B)
            .out("salt", 2).temp("cooling").add("dimethyl_dicarbonate");
        r(DISTILLING).fin("dimethyl_dicarbonate", B).fout("dimethyl_dicarbonate", 900).mode("vacuum_distillation")
            .add("dimethyl_dicarbonate_rectifying");
        // 345: water gas shifted on iron (P82), taken up in hot potash and boiled out, rectified
        r(GAS_REACTING).fin("carbon_monoxide", B).fin("water", B).fout("carbon_dioxide", B).fout("hydrogen", B).mode("gas_catalysis")
            .temp("heated").cat("catalysts/iron", 64).add("carbon_monoxide_shift");
        r(BOILING).in("chemicals/potassium_bicarbonate", 2).out("potassium_carbonate", 1).fout("carbon_dioxide", B).temp("heated")
            .add("carbon_dioxide_desorption");
        r(DISTILLING).fin("carbon_dioxide", B).fout("carbon_dioxide", 950).mode("cryogenic_separation").add("carbon_dioxide_rectifying");
        // 346 A: egg white with salt at pH 9.5, seeded and cooled; B: on cation resin, eluted with salt
        r(REACTING).in("materials/egg_white", 8).in("chemicals/salt", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B)
            .out("lysozyme", 1).temp("cooling").tier(2).add("lysozyme");
        r(REACTING).in("materials/egg_white", 8).in("chemicals/salt", 1).fin("water", B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS)
            .out("lysozyme", 2).tier(2).add("lysozyme_ion_exchange");
        // 348, 349: Streptomyces albulus at pH 4 on glucose and ammonium sulfate, cation resin eluted with ammonia or acid
        r(FERMENTING).in("chemicals/glucose", 2).in("chemicals/ammonium_sulfate", 1).fin("water", 2 * B)
            .cat("cultures/streptomyces_albulus", CULTURE_RUNS, PURE).out("epsilon_polylysine", 1).src(40).sterile().mode("aerated")
            .add("epsilon_polylysine");
        r(REACTING).in("chemicals/epsilon_polylysine", 4).fin("ammonia", B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS)
            .out("epsilon_polylysine", 3).rm(90).tier(2).add("epsilon_polylysine_ion_exchange");
        r(REACTING).in("chemicals/epsilon_polylysine", 4).fin("hydrochloric_acid", B).cat("catalysts/ion_exchange_resin", ENZYME_RUNS)
            .out("epsilon_polylysine_hydrochloride", 3).tier(2).add("epsilon_polylysine_hydrochloride");
        decolouring("epsilon_polylysine");
        sprayDrying("epsilon_polylysine");
        sprayDrying("epsilon_polylysine_hydrochloride");
    }

    // ---- 350–363 sweeteners ----

    private void sweeteners() {
        // 350: Protaminobacter cells (the culture) set in calcium alginate (P96); sucrose run over the beads, crystallized cold
        r(REACTING).in("cultures/protaminobacter", 2).in("chemicals/sodium_alginate", 1).in("chemicals/calcium_chloride", 1).fin("water", B)
            .out("immobilized_sucrose_isomerase", 2).tier(2).add("immobilized_sucrose_isomerase");
        r(REACTING).in("chemicals/sucrose", 4).fin("water", B).cat("catalysts/immobilized_sucrose_isomerase", IMMOBILIZED_RUNS)
            .out("isomaltulose", 3).temp("heated").add("isomaltulose");
        recrystallizing("isomaltulose");
        // 352, 353, 362: licorice root in hot water, crude glycyrrhizic acid at pH 2, the marc kept for 321 (联产, 11 问题 43);
        // its potassium and ammonium salts in ethanol
        r(EXTRACTING).in("materials/licorice_root", 4).fin("water", B).fin("sulfuric_acid", 100).out("glycyrrhizic_acid", 1).src()
            .out("licorice_residue", 3).temp("heated").tier(2).add("glycyrrhizic_acid");
        r(REACTING).in("chemicals/glycyrrhizic_acid", 1).in("chemicals/potassium_hydroxide", 1).fin("ethanol", B)
            .out("monopotassium_glycyrrhizinate", 1).fout("ethanol", 900).src().temp("cooling").add("monopotassium_glycyrrhizinate");
        r(REACTING).in("chemicals/glycyrrhizic_acid", 1).fin("ammonia", B).fin("ethanol", B).out("ammonium_glycyrrhizinate", 1)
            .fout("ethanol", 900).src().temp("cooling").add("ammonium_glycyrrhizinate");
        r(REACTING).in("chemicals/glycyrrhizic_acid", 1).in("chemicals/potassium_hydroxide", 3).fin("water", B)
            .out("tripotassium_glycyrrhizinate", 1).tier(2).add("tripotassium_glycyrrhizinate");
        for (String salt : new String[]{"monopotassium_glycyrrhizinate", "ammonium_glycyrrhizinate"}) {
            decolouring(salt);
            recrystallizing(salt);
        }
        sprayDrying("tripotassium_glycyrrhizinate");
        // 354: Koch carbonylation to isobutyric acid, calcium salt dry-distilled to diisopropyl ketone, the thietanone and amine;
        // D-alanine by Arthrobacter D-amidase; condensed with L-aspartic acid (protection merged)
        r(REACTING).fin("propene", B).fin("carbon_monoxide", B).fin("water", B).fout("isobutyric_acid", B).mode("carbonylation")
            .pressure("high").temp("heated").add("isobutyric_acid");
        r(REACTING).in("chemicals/slaked_lime", 1).fin("isobutyric_acid", 2 * B).out("calcium_isobutyrate", 1).add("calcium_isobutyrate");
        r(KILN).in("chemicals/calcium_isobutyrate", 1).out("calcium_carbonate", 1).fout("diisopropyl_ketone", B).mode("dry_distillation")
            .add("diisopropyl_ketone");
        r(REACTING).in("chemicals/sodium_hydroxide", 2).fin("diisopropyl_ketone", B).fin("sulfur_dichloride", B)
            .fout("tetramethylthietanone", B).out("salt", 2).add("tetramethylthietanone");
        r(REACTING).fin("tetramethylthietanone", B).fin("ammonia", B).fin("hydrogen", B).fout("tetramethylthietanamine", B)
            .mode("hydrogenation").pressure("high").temp("heated").cat("catalysts/nickel", 64).add("tetramethylthietanamine");
        r(REACTING).fin("acetaldehyde", B).fin("hydrogen_cyanide", B).fin("ammonia", B).out("alaninamide", 1).add("alaninamide");
        r(FERMENTING).in("chemicals/alaninamide", 2).fin("water", B).cat("cultures/arthrobacter", CULTURE_RUNS, PURE).out("d_alanine", 1)
            .src(90).fout("ammonia", B).sterile().airtight().add("d_alanine");
        r(REACTING).in("chemicals/l_aspartic_acid", 1).in("chemicals/d_alanine", 1).fin("tetramethylthietanamine", B).out("alitame", 1)
            .temp("heated").add("alitame");
        recrystallizing("alitame");
        // 355: whey heated and filtered, lactose crystallized; hydrogenated on nickel (P46)
        r(CRYSTALLIZING).fin("whey", 4 * B).out("lactose", 1).src().temp("cooling").add("lactose");
        recrystallizing("lactose");
        r(REACTING).in("chemicals/lactose", 1).fin("hydrogen", B).fin("water", B).out("lactitol", 1).mode("hydrogenation").pressure("high")
            .temp("heated").cat("catalysts/nickel", 64).add("lactitol");
        recrystallizing("lactitol");
        // 365: lactose isomerized at pH 13, neutralized, desalted on resin
        r(REACTING).in("chemicals/lactose", 2).in("chemicals/sodium_hydroxide", 1).fin("hydrochloric_acid", 100).fin("water", B)
            .fout("lactulose_syrup", B).temp("heated").tier(2).add("lactulose_syrup");
        adsorbing("lactulose_syrup", "catalysts/ion_exchange_resin", "ion_exchange");
        // 356 B: monk fruit in hot water, resin, spray dried (A is a hand recipe)
        r(EXTRACTING).in("materials/monk_fruit", 4).fin("water", B).out("mogroside", 1).src(20).temp("heated").tier(2)
            .add("mogroside_extract");
        resin("mogroside", "catalysts/macroporous_resin");
        sprayDrying("mogroside");
        // 357: the arils in acidified water at 40 °C, ultrafiltered, freeze dried
        r(EXTRACTING).in("materials/katemfe_fruit", 4).fin("water", B).fin("hydrochloric_acid", 50).out("thaumatin", 1).src().tier(2)
            .add("thaumatin");
        r(DRYING).in("chemicals/thaumatin", 2).out("thaumatin", 1).temp("freezing").heat("very_sensitive").add("thaumatin_freeze_drying");
        // 358: aspartame and acesulfame K with hydrochloric acid
        r(REACTING).in("chemicals/aspartame", 1).in("chemicals/acesulfame_potassium", 1).fin("hydrochloric_acid", B).fin("water", B)
            .out("aspartame_acesulfame", 1).out("potassium_chloride", 1).tier(2).add("aspartame_acesulfame");
        // 361: 2 C6H13NO3S + Ca(OH)2 → C12H24CaN2O6S2
        r(REACTING).in("chemicals/cyclamic_acid", 2).in("chemicals/slaked_lime", 1).fin("water", B).out("calcium_cyclamate", 1).tier(2)
            .add("calcium_cyclamate");
        recrystallizing("calcium_cyclamate");
        // 363: ethyl vanillin O-methylated, de-ethylated to isovanillin, aldol with acetaldehyde, hydrogenated (palladium),
        // reductive alkylation of aspartame
        r(REACTING).in("chemicals/ethyl_vanillin", 1).in("chemicals/sodium_hydroxide", 1).fin("methyl_chloride", B)
            .out("ethoxy_methoxybenzaldehyde", 1).out("salt", 1).add("ethoxy_methoxybenzaldehyde");
        r(REACTING).in("chemicals/ethoxy_methoxybenzaldehyde", 1).fin("sulfuric_acid", B).out("isovanillin", 1).fout("ethanol", B).src()
            .temp("heated").method("corrosion_resistant").add("isovanillin");
        r(REACTING).in("chemicals/isovanillin", 1).fin("acetaldehyde", B).fin("methanol", B).out("hydroxymethoxycinnamaldehyde", 1)
            .fout("methanol", 900).src().add("hydroxymethoxycinnamaldehyde");
        r(REACTING).in("chemicals/hydroxymethoxycinnamaldehyde", 1).fin("hydrogen", B).fout("hydroxymethoxyphenylpropanal", B)
            .mode("hydrogenation").pressure("high").temp("heated").cat("catalysts/palladium", 64).add("hydroxymethoxyphenylpropanal");
        r(REACTING).in("chemicals/aspartame", 1).fin("hydroxymethoxyphenylpropanal", B).fin("hydrogen", B).out("advantame", 1)
            .mode("hydrogenation").pressure("high").temp("heated").cat("catalysts/palladium", 64).add("advantame");
        recrystallizing("advantame");
    }

    // ---- 364–372 others ----

    private void others() {
        // 364: pyrolusite fused with potash in air (P33), A disproportionated with carbon dioxide, B oxidized at the anode
        r(GRINDING).in("materials/pyrolusite", 1).out("manganese_dioxide", 1).src().add("manganese_dioxide");
        r(KILN).in("chemicals/manganese_dioxide", 2).in("chemicals/potassium_hydroxide", 4).out("potassium_manganate", 2).mode("roasting")
            .add("potassium_manganate");
        r(REACTING).in("chemicals/potassium_manganate", 3).fin("carbon_dioxide", 2 * B).fin("water", B).out("potassium_permanganate", 2)
            .out("manganese_dioxide", 1).tier(2).add("potassium_permanganate");
        r(ELECTROLYZING).in("chemicals/potassium_manganate", 2).fin("water", 2 * B).out("potassium_permanganate", 2).fout("hydrogen", B)
            .mode("electrooxidation").cat("catalysts/electrode", 64).add("potassium_permanganate_electrolytic");
        recrystallizing("potassium_permanganate");
        // 366 A: tea dust sublimed (crude); B (optional route, hydrogen cyanide): urea, methylamine, dimethylurea, cyanoacetic acid,
        // the uracil, theophylline in one pot, methylated; C: supercritical decaffeination of green coffee
        r(DISTILLING).in("materials/tea", 8).out("caffeine", 1).src().mode("sublimation").add("caffeine_sublimation");
        r(REACTING).fin("methanol", B).fin("ammonia", B).fout("methylamine", B).pressure("pressurized").temp("heated").add("methylamine");
        r(REACTING).in("chemicals/urea", 1).fin("methylamine", 2 * B).out("dimethylurea", 1).fout("ammonia", 2 * B).temp("heated")
            .add("dimethylurea");
        r(REACTING).in("chemicals/chloroacetic_acid", 1).in("chemicals/sodium_hydroxide", 1).fin("hydrogen_cyanide", B)
            .out("cyanoacetic_acid", 1).out("salt", 1).add("cyanoacetic_acid");
        r(REACTING).in("chemicals/dimethylurea", 1).in("chemicals/cyanoacetic_acid", 1).fin("acetic_anhydride", B)
            .out("aminodimethyluracil", 1).fout("acetic_acid", 2 * B).src(30).add("aminodimethyluracil");
        r(REACTING).in("chemicals/sodium_methoxide", 1).fin("carbon_monoxide", 4 * B).fin("water", 4 * B).fin("methanol", 100)
            .fout("formic_acid", 4 * B).pressure("pressurized").temp("heated").add("formic_acid");
        r(REACTING).in("chemicals/aminodimethyluracil", 1).in("chemicals/sodium_nitrite", 1).fin("hydrochloric_acid", B)
            .fin("formic_acid", B).fin("hydrogen", 2 * B).out("theophylline", 1).out("salt", 1).mode("hydrogenation").pressure("high")
            .cat("catalysts/palladium", 64).add("theophylline");
        r(REACTING).in("chemicals/theophylline", 1).in("chemicals/sodium_hydroxide", 1).fin("methyl_chloride", B).out("caffeine", 1)
            .out("salt", 1).add("caffeine");
        r(EXTRACTING).in("materials/coffee_bean", 8).fin("carbon_dioxide", B).out("caffeine", 1).src(60).fout("carbon_dioxide", 900).src()
            .method("supercritical").add("caffeine_from_decaffeination");
        recrystallizing("caffeine");
        // 367: chili powder in ethanol (the oleoresin of 60 B)
        r(EXTRACTING).in("materials/chili_powder", 4).fin("ethanol", B).fout("paprika_oleoresin", 250).fout("ethanol", 750).src()
            .mode("solvent_extraction").add("paprika_oleoresin_by_ethanol");
        // 368: with caustic soda (T2)
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("acetic_acid", B).out("sodium_acetate", 1).tier(2)
            .add("sodium_acetate_by_caustic_soda");
        recrystallizing("sodium_acetate");
        // 369: guar gum partly hydrolysed with hemicellulase (mannanase) and papain (P56), decoloured, spray dried
        r(REACTING).in("chemicals/guar_gum", 4).in("enzymes/papain", 1).fin("water", B).cat("enzymes/hemicellulase", ENZYME_RUNS)
            .out("galactomannan", 4).temp("heated").tier(2).add("galactomannan");
        decolouring("galactomannan");
        sprayDrying("galactomannan");
        // 370: hardened oil oxidized warm in air
        r(REACTING).in("chemicals/hardened_oil", 1).fin("oxygen", B).out("oxystearin", 1).src().mode("pressure_oxidation")
            .pressure("pressurized").temp("heated").add("oxystearin");
        // 371: the recombinant yeast induced with galactose (lactose as its source), ultrafiltered
        r(FERMENTING).in("chemicals/glucose", 2).in("chemicals/lactose", 1).fin("water", 2 * B).cat("cultures/yeast_isp", CULTURE_RUNS, PURE)
            .fout("ice_structuring_protein", B).src(0.2).sterile().mode("aerated").add("ice_structuring_protein");
        r(PURIFYING).fin("ice_structuring_protein", B).fout("ice_structuring_protein", 500).mode("ultrafiltration")
            .add("ice_structuring_protein_ultrafiltration");
        // 372 B: magnesite and dilute sulfuric acid; A recrystallized from the bittern salt
        r(GRINDING).in("materials/magnesite", 1).out("magnesite", 1).src().add("magnesite");
        r(REACTING).in("chemicals/magnesite", 1).fin("sulfuric_acid", B).out("epsom_salt", 1).fout("carbon_dioxide", B).tier(2)
            .add("epsom_salt");
        recrystallizing("epsom_salt");
    }
}
