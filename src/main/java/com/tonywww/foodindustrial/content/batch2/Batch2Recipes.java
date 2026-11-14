package com.tonywww.foodindustrial.content.batch2;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.ABSORBING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SEPARATING;

/**
 * The second content batch (I19): the T1-and-later routes of additives 31–78 (additives/13) and the
 * petrochemical start (ketene, ethylene, propene). Same conventions as batch 1 (additives/04 §实现约定).
 */
public final class Batch2Recipes extends Batch {
    private Batch2Recipes(RecipeGen.Output out) {
        super(out, "batch2");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch2Recipes(out).generate());
    }

    private void generate() {
        petrochemicals();
        catalysts();
        preservatives();
        sweeteners();
        thickeners();
        phosphates();
        acidityRegulators();
        colours();
        antioxidants();
        emulsifiers();
        others();
    }

    // ---- shared: petrochemicals (P57), solvents, catalysts ----

    private void petrochemicals() {
        // crude oil (from other mods) cut and cracked; ethylene also from ethanol over alumina
        // I31 (11 问题 38): propane and light naphtha come off the top of the same cut (428, 432, 433)
        r(DISTILLING).fin("crude_oil", B).fout("n_hexane", 200).src(80).fout("benzene", 100).src(70).fout("toluene", 100).src(70)
            .fout("butane", 100).src().fout("propane", 100).src().fout("light_naphtha", 200).method("multi_fraction")
            .add("crude_oil_distillation");
        r(GAS_REACTING).fin("crude_oil", B).fout("ethylene", 400).src().fout("propene", 200).src().fout("butane", 100).src()
            .mode("cracking").add("crude_oil_cracking");
        r(GAS_REACTING).fin("ethanol", B).fout("ethylene", B).mode("gas_catalysis").temp("high").cat("catalysts/alumina", 64)
            .add("ethanol_dehydration");
        // ketene: acetic acid cracked at 700 °C (31, 48): C2H4O2 → C2H2O + H2O
        r(GAS_REACTING).fin("acetic_acid", B).fout("ketene", B).mode("cracking").add("ketene");
        // ethyl acetate (59): esterification
        r(REACTING).fin("ethanol", B).fin("acetic_acid", B).fin("sulfuric_acid", 100).fout("ethyl_acetate", B).temp("heated")
            .add("ethyl_acetate");
        // fatty acids: animal fat split under pressure, the olein pressed out (71, 72)
        r(REACTING).in("chemicals/animal_fat", 1).fin("water", 3 * B).out("tallow_fatty_acids", 3).fout("glycerol", B).src()
            .pressure("high").temp("high").add("fat_splitting");
        r(PRESSING).in("chemicals/tallow_fatty_acids", 2).out("stearic_acid", 1).src().fout("oleic_acid", B).src().add("olein_pressing");
        // soybean oil pressed (65, 69)
        r(PRESSING).in("materials/soybean", 4).fout("crude_soybean_oil", 250).out("okara", 1).add("crude_soybean_oil");
        r(PRESSING).in("materials/tomato", 4).out("tomato_pomace", 1).add("tomato_pomace");
    }

    private void catalysts() {
        r(REACTING).in("chemicals/vanadium_pentoxide", 1).fin("phosphoric_acid", B).out("vpo_catalyst", 2).temp("heated").tier(2)
            .add("vpo_catalyst");
        r(REACTING).in("materials/silver", 1).in("chemicals/alumina", 1).out("silver_catalyst", 2).temp("heated").tier(2)
            .add("silver_catalyst");
        r(REACTING).in("materials/zinc", 1).fin("hydrochloric_acid", 2 * B).out("zinc_chloride", 1).fout("hydrogen", B)
            .add("zinc_chloride");
        // the resins (36, 62) from styrene (11 问题 43): suspension polymerization with toluene as porogen, the toluene stripped
        // off (merged; divinylbenzene left out, game simplification); polystyrene (441) sulfonated for ion exchange
        r(REACTING).fin("styrene", B).fin("toluene", 500).fin("water", B).out("macroporous_resin", 4).fout("toluene", 450)
            .mode("polymerization").temp("heated").add("macroporous_resin");
        r(REACTING).in("chemicals/polystyrene", 4).fin("sulfuric_acid", B).out("ion_exchange_resin", 4).temp("heated")
            .method("corrosion_resistant").add("ion_exchange_resin");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("methanol", B).out("sodium_methoxide", 1).add("sodium_methoxide");
    }

    // ---- 31–35 preservatives ----

    private void preservatives() {
        // 31 A: ketene and crotonaldehyde (T3): 2 C2H4O → C4H6O + H2O; C2H2O + C4H6O → C6H8O2
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("acetaldehyde", 2 * B).fout("crotonaldehyde", B).add("crotonaldehyde");
        r(REACTING).fin("ketene", B).fin("crotonaldehyde", B).fin("hydrochloric_acid", 200).cat("catalysts/zinc_chloride", 64)
            .out("sorbic_acid", 1).add("sorbic_acid");
        r(REACTING).in("chemicals/sorbic_acid", 1).in("chemicals/potassium_hydroxide", 1).fin("water", B).out("potassium_sorbate", 1)
            .add("potassium_sorbate");
        recrystallizing("sorbic_acid");
        recrystallizing("potassium_sorbate");
        // 32 A: propionibacterium (T2): 3 C6H12O6 → 4 C3H6O2 + 2 C2H4O2 + 2 CO2 + 2 H2O; limed
        r(FERMENTING).in("chemicals/glucose", 3).fin("water", B).cat("cultures/propionibacterium", CULTURE_RUNS)
            .fout("propionic_acid", 4 * B).src(5).fout("carbon_dioxide", 2 * B).src().airtight().add("propionic_acid_fermentation");
        r(FERMENTING).fin("whey", 4 * B).cat("cultures/propionibacterium", CULTURE_RUNS).fout("propionic_acid", 2 * B).src(4)
            .airtight().add("propionic_acid_from_whey");
        // 32 B: ethylene carbonylation (T3): C2H4 + CO + H2O → C3H6O2
        r(REACTING).fin("ethylene", B).fin("carbon_monoxide", B).fin("water", B).fout("propionic_acid", B).mode("carbonylation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("propionic_acid_carbonylation");
        r(DISTILLING).fin("propionic_acid", 2 * B).fout("propionic_acid", B).add("propionic_acid_distilling");
        r(REACTING).in("chemicals/slaked_lime", 1).fin("propionic_acid", 2 * B).out("calcium_propionate", 1).add("calcium_propionate");
        recrystallizing("calcium_propionate");
        // 33 A: sodium acetate (1) taken up in glacial acetic acid, cooled (T2, 11 问题 41): C2H3NaO2 + C2H4O2 → C4H7NaO4
        r(REACTING).in("chemicals/sodium_acetate", 1).fin("acetic_acid", B, "90").out("sodium_diacetate", 1).temp("cooling")
            .add("sodium_diacetate");
        // 34 A: Lactococcus fermentation (T4), salted out, spray dried on salt
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/lactococcus", CULTURE_RUNS, PURE).out("nisin", 1)
            .src(30).sterile().airtight().add("nisin_fermentation");
        r(REACTING).in("chemicals/nisin", 2).in("chemicals/salt", 1).fin("water", B).out("nisin", 1).rm(75).tier(2).add("nisin_salting_out");
        r(DRYING).in("chemicals/nisin", 2).out("nisin", 1).heat("sensitive").add("nisin_spray_drying");
        // 35 A: Streptomyces fermentation (T4), methanol extraction, crystallized
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/streptomyces_gilvosporeus", CULTURE_RUNS, PURE)
            .out("natamycin", 1).src(30).sterile().mode("aerated").add("natamycin_fermentation");
        r(EXTRACTING).in("chemicals/natamycin", 2).fin("methanol", B).out("natamycin", 1).fout("methanol", 800).src()
            .mode("solvent_extraction").add("natamycin_extraction");
        recrystallizing("natamycin");
    }

    // ---- 36–41 sweeteners ----

    private void sweeteners() {
        // 36 B: stevia leaves extracted, flocculated with lime, on macroporous resin (T2)
        r(EXTRACTING).in("materials/dried_stevia", 4).in("chemicals/slaked_lime", 1).fin("water", 2 * B).out("steviol_glycosides", 1).src(10)
            .temp("heated").add("steviol_glycosides_extract");
        resin("steviol_glycosides", "catalysts/macroporous_resin");
        r(DRYING).in("chemicals/steviol_glycosides", 2).out("steviol_glycosides", 1).heat("sensitive").add("steviol_glycosides_spray_drying");
        // 37 A: hemicellulose hydrolysed (T3), xylose decoloured, hydrogenated: C5H10O5 + H2 → C5H12O5
        r(REACTING).in("materials/hemicellulose", 4).fin("sulfuric_acid", 500).fin("water", 2 * B).out("xylose", 2).src()
            .temp("heated").tier(2).add("xylose");
        decolouring("xylose");
        recrystallizing("xylose");
        r(REACTING).in("chemicals/xylose", 1).fin("hydrogen", B).fin("water", B).out("xylitol", 1).mode("hydrogenation").pressure("high")
            .temp("heated").cat("catalysts/nickel", 64).add("xylitol");
        recrystallizing("xylitol");
        // 38 A: osmophilic yeast (T4)
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", B).cat("cultures/moniliella", CULTURE_RUNS, PURE).out("erythritol", 1)
            .src(90).sterile().mode("aerated").add("erythritol");
        recrystallizing("erythritol");
        // 39 A: maltose by β-amylase, hydrogenated (T3): C12H22O11 + H2 → C12H24O11
        r(REACTING).fin("liquefied_starch", B).cat("enzymes/beta_amylase", ENZYME_RUNS).out("maltose", 4).src(90).temp("heated")
            .tier(2).add("maltose");
        r(REACTING).in("chemicals/maltose", 1).fin("hydrogen", B).fin("water", B).fout("maltitol_syrup", B).mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("maltitol_syrup");
        adsorbing("maltitol_syrup", "catalysts/ion_exchange_resin", "ion_exchange");
        r(CRYSTALLIZING).fin("maltitol_syrup", 2 * B).out("maltitol", 1).mode("evaporative_crystallization").add("maltitol");
        // 40 A: kelp steep water (the kelp extract, N203) concentrated, desalted with ethanol, crystallized (T2, 11 问题 41);
        // B: fructose hydrogenated (T3)
        r(CRYSTALLIZING).fin("kelp_extract", B).fin("ethanol", 250).out("mannitol", 1).src(60).out("iodine", 1).src().chance(0.25)
            .fout("ethanol", 200).temp("heated").tier(2).add("mannitol_from_kelp");
        r(REACTING).in("chemicals/fructose", 2).fin("hydrogen", 2 * B).fin("water", B).out("mannitol", 1).fout("sorbitol_syrup", B)
            .mode("hydrogenation").pressure("high").temp("heated").cat("catalysts/nickel", 64).add("mannitol_from_fructose");
        r(CRYSTALLIZING).in("chemicals/mannitol", 4).fin("ethanol", B).out("mannitol", 3).temp("cooling").add("mannitol_recrystallizing");
        // 41 A: Remsen–Fahlberg (T3): chlorosulfonic acid from SO3 and HCl; sulfonyl chloride, amide, oxidized, neutralized
        r(ABSORBING).fin("sulfur_trioxide", B).fin("hydrogen_chloride", B).fout("chlorosulfonic_acid", B).add("chlorosulfonic_acid");
        r(REACTING).fin("toluene", B).fin("chlorosulfonic_acid", 2 * B).fout("o_toluenesulfonyl_chloride", B).fout("sulfuric_acid", B)
            .temp("cooling").method("corrosion_resistant").add("o_toluenesulfonyl_chloride");
        r(REACTING).fin("o_toluenesulfonyl_chloride", B).fin("ammonia", 2 * B).out("o_toluenesulfonamide", 1).out("ammonium_chloride", 1)
            .add("o_toluenesulfonamide");
        r(REACTING).in("chemicals/o_toluenesulfonamide", 2).in("chemicals/potassium_permanganate", 2).fin("water", B).out("saccharin", 2)
            .add("saccharin");
        r(REACTING).in("chemicals/o_toluenesulfonamide", 2).fin("oxygen", 3 * B).out("saccharin", 2).mode("pressure_oxidation")
            .pressure("pressurized").temp("heated").cat("catalysts/cobalt", 64).add("saccharin_by_air");
        r(REACTING).in("chemicals/saccharin", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B).out("sodium_saccharin", 1)
            .add("sodium_saccharin");
        recrystallizing("sodium_saccharin");
    }

    // ---- 42–48 thickeners ----

    private void thickeners() {
        // 42 A: semi-refined carrageenan in hot potash (T1); B: extracted and precipitated by ethanol (T2)
        r(REACTING).in("materials/red_algae_carrageenan", 4).fin("potash_lye", B).out("carrageenan", 1).src(40).temp("heated")
            .add("carrageenan_semi_refined");
        r(REACTING).in("chemicals/carrageenan", 2).fin("ethanol", B).fin("water", B).out("carrageenan", 1).fout("ethanol", 800).src()
            .tier(2).add("carrageenan_refining");
        // 43, 44 A: viscous fermentations (T4), precipitated by ethanol
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/xanthomonas", CULTURE_RUNS, PURE).out("xanthan_gum", 1)
            .src(40).sterile().mode("aerated").add("xanthan_fermentation");
        r(REACTING).in("chemicals/xanthan_gum", 2).fin("ethanol", B).out("xanthan_gum", 1).fout("ethanol", 800).src().tier(2)
            .add("xanthan_precipitating");
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/sphingomonas", CULTURE_RUNS, PURE).out("gellan_gum", 1)
            .src(40).sterile().mode("aerated").add("gellan_fermentation");
        r(REACTING).in("chemicals/gellan_gum", 2).in("chemicals/sodium_hydroxide", 1).fin("ethanol", B).out("gellan_gum", 1)
            .fout("ethanol", 800).src().temp("heated").tier(2).add("gellan_precipitating");
        // 45 A: chloroacetic acid, alkali cellulose etherified (T3)
        r(REACTING).fin("acetic_acid", B).fin("chlorine", B).out("chloroacetic_acid", 1).fout("hydrogen_chloride", B).temp("heated")
            .method("corrosion_resistant").add("chloroacetic_acid");
        r(REACTING).in("chemicals/chloroacetic_acid", 2).in("chemicals/soda_ash", 1).out("sodium_chloroacetate", 2)
            .fout("carbon_dioxide", B).add("sodium_chloroacetate");
        r(REACTING).in("chemicals/cellulose", 1).in("chemicals/sodium_chloroacetate", 1).in("chemicals/sodium_hydroxide", 1)
            .out("cmc_unit", 1).out("salt", 1).add("carboxymethyl_cellulose");
        r(REACTING).in("chemicals/cmc_unit", 2).fin("ethanol", B).out("cmc_unit", 2).fout("ethanol", 800).src().tier(2)
            .add("carboxymethyl_cellulose_washing");
        // 46 A: starch milk in dilute hydrochloric acid at 40–55 °C, neutralized with soda (T2)
        r(REACTING).in("chemicals/starch", 4).in("chemicals/soda_ash", 1).fin("hydrochloric_acid", 200).fin("water", B)
            .out("acid_treated_starch", 4).temp("heated").tier(2).add("acid_treated_starch");
        // 47 A: propylene oxide by the chlorohydrin route, starch etherified (T3)
        r(REACTING).fin("propene", B).fin("chlorine", B).fin("water", B).fout("propylene_chlorohydrin", B).fout("hydrogen_chloride", B)
            .add("propylene_chlorohydrin");
        r(REACTING).in("chemicals/slaked_lime", 1).fin("propylene_chlorohydrin", 2 * B).fout("propylene_oxide", 2 * B)
            .out("calcium_chloride", 1).add("propylene_oxide");
        r(REACTING).in("chemicals/starch", 4).in("chemicals/sodium_hydroxide", 1).fin("propylene_oxide", B).out("hydroxypropyl_starch", 4)
            .pressure("pressurized").add("hydroxypropyl_starch");
        // 48 A: acetic anhydride from ketene, starch acetylated (T3)
        r(REACTING).fin("ketene", B).fin("acetic_acid", B).fout("acetic_anhydride", B).add("acetic_anhydride");
        r(REACTING).in("chemicals/starch", 4).in("chemicals/sodium_hydroxide", 1).fin("acetic_anhydride", B).out("starch_acetate", 4)
            .fout("acetic_acid", B).src(30).add("starch_acetate");
    }

    // ---- 49–53 phosphates ----

    private void phosphates() {
        // 49 A: phosphoric acid limed past the hydrogen phosphate (170) (T2, 11 问题 41): 2 CaHPO4 + Ca(OH)2 → Ca3(PO4)2 + 2 H2O;
        // B: bone ash dissolved and reprecipitated
        r(REACTING).in("chemicals/dicalcium_phosphate", 2).in("chemicals/slaked_lime", 1).fin("water", B).out("tricalcium_phosphate", 1)
            .tier(2).add("tricalcium_phosphate");
        r(REACTING).in("chemicals/hydroxyapatite", 1).in("chemicals/slaked_lime", 1).fin("hydrochloric_acid", 2 * B)
            .out("tricalcium_phosphate", 1).out("calcium_chloride", 1).tier(2).add("tricalcium_phosphate_from_bone_ash");
        // 50 A: to pH 9 with soda (T2): 2 NaH2PO4 + Na2CO3 → 2 Na2HPO4 + CO2 + H2O
        r(REACTING).in("chemicals/monosodium_phosphate", 2).in("chemicals/soda_ash", 1).fin("water", B).out("disodium_phosphate", 2)
            .fout("carbon_dioxide", B).tier(2).add("disodium_phosphate");
        // 51 A: mixed salts condensed (T3): 2 Na2HPO4 + NaH2PO4 → Na5P3O10 + 2 H2O; 53 A: 2 Na2HPO4 → Na4P2O7 + H2O
        r(KILN).in("chemicals/disodium_phosphate", 2).in("chemicals/monosodium_phosphate", 1).out("sodium_tripolyphosphate", 1)
            .mode("condensation").add("sodium_tripolyphosphate");
        r(KILN).in("chemicals/disodium_phosphate", 2).out("tetrasodium_pyrophosphate", 1).mode("condensation")
            .add("tetrasodium_pyrophosphate");
        // 52 A: monosodium phosphate melted and quenched to a glass (T2; the chill roll step is collapsed)
        r(KILN).in("chemicals/monosodium_phosphate", 1).out("sodium_hexametaphosphate", 1).mode("melting").add("sodium_hexametaphosphate");
    }

    // ---- 54–58 acidity regulators ----

    private void acidityRegulators() {
        // 54 A: tartar to calcium tartrate (T1; with calcium chloride from T2), split with sulfuric acid
        r(REACTING).in("chemicals/potassium_bitartrate", 2).in("chemicals/calcium_carbonate", 1).fin("water", B).out("calcium_tartrate", 1)
            .fout("carbon_dioxide", B).add("calcium_tartrate");
        r(REACTING).in("chemicals/potassium_bitartrate", 2).in("chemicals/calcium_carbonate", 1).in("chemicals/calcium_chloride", 1)
            .fin("water", B).out("calcium_tartrate", 2).out("potassium_chloride", 2).fout("carbon_dioxide", B)
            .add("calcium_tartrate_with_calcium_chloride");
        r(REACTING).in("chemicals/calcium_tartrate", 1).fin("sulfuric_acid", B).out("tartaric_acid", 1).out("gypsum", 1).add("tartaric_acid");
        recrystallizing("tartaric_acid");
        // dissolved hot like the T0 recrystallization, so a heated machine takes this one (11 问题 41)
        r(CRYSTALLIZING).in("chemicals/potassium_bitartrate", 4).fin("water", B).out("potassium_bitartrate", 3).temp("heated")
            .add("potassium_bitartrate_recrystallizing");
        // 54 B: maleic anhydride epoxidized over tungstate, hydrolysed by the enzyme (T4)
        r(REACTING).in("chemicals/maleic_anhydride", 1).fin("hydrogen_peroxide", B).cat("catalysts/tungstate", 64)
            .out("epoxysuccinic_acid", 1).add("epoxysuccinic_acid");
        r(FERMENTING).in("chemicals/epoxysuccinic_acid", 1).fin("water", B).cat("enzymes/epoxysuccinate_hydrolase", ENZYME_RUNS)
            .out("tartaric_acid", 1).src(99).sterile().airtight().add("tartaric_acid_enzymatic");
        // 56 A: maleic anhydride from butane (VPO) or benzene (V2O5), hydrated to DL-malic acid (T3)
        r(GAS_REACTING).fin("butane", 2 * B).out("maleic_anhydride", 2).mode("gas_catalysis").air().cat("catalysts/vpo", 64)
            .add("maleic_anhydride");
        r(GAS_REACTING).fin("benzene", B).out("maleic_anhydride", 1).fout("carbon_dioxide", 2 * B).mode("gas_catalysis").air()
            .cat("catalysts/vanadium", 64).add("maleic_anhydride_from_benzene");
        r(REACTING).in("chemicals/maleic_anhydride", 1).fin("water", B).out("maleic_acid", 1).add("maleic_acid");
        r(REACTING).in("chemicals/maleic_acid", 1).fin("water", B).out("malic_acid", 1).pressure("pressurized").temp("high")
            .add("malic_acid");
        recrystallizing("malic_acid");
        // 56 B: fumarase on fumaric acid (T4) → L-malic acid
        r(FERMENTING).in("chemicals/fumaric_acid", 1).fin("water", B).cat("enzymes/fumarase", ENZYME_RUNS).out("l_malic_acid", 1)
            .src(99).sterile().airtight().add("l_malic_acid");
        // 57 A: maleic acid isomerized (T3); B: Rhizopus fermentation (T4)
        r(REACTING).in("chemicals/maleic_acid", 1).fin("hydrochloric_acid", 100).out("fumaric_acid", 1).temp("heated").add("fumaric_acid");
        r(FERMENTING).in("chemicals/glucose", 1).in("chemicals/calcium_carbonate", 1).fin("water", B)
            .cat("cultures/rhizopus", CULTURE_RUNS, PURE).out("fumaric_acid", 1).src(90).sterile().mode("aerated")
            .add("fumaric_acid_fermentation");
        recrystallizing("fumaric_acid");
        // 58 A: citric acid on soda (T2): 2 C6H8O7 + 3 Na2CO3 → 2 C6H5Na3O7 + 3 CO2 + 3 H2O
        r(REACTING).in("chemicals/citric_acid", 2).in("chemicals/soda_ash", 3).fin("water", B).out("trisodium_citrate", 2)
            .fout("carbon_dioxide", 3 * B).tier(2).add("trisodium_citrate");
        recrystallizing("trisodium_citrate");
    }

    // ---- 59–64 colours ----

    private void colours() {
        // 59 A: tomato skins extracted with ethyl acetate (T2), the oleoresin crystallized
        r(EXTRACTING).in("materials/tomato_pomace", 4).fin("ethyl_acetate", B).fout("tomato_oleoresin", 250).src(3)
            .fout("ethyl_acetate", 750).src().mode("solvent_extraction").add("tomato_oleoresin");
        r(CRYSTALLIZING).fin("tomato_oleoresin", B).out("lycopene", 1).temp("cooling").add("lycopene");
        r(CRYSTALLIZING).in("chemicals/lycopene", 2).fin("ethanol", B).out("lycopene", 1).add("lycopene_recrystallizing");
        // 60 B: chili extracted with hexane or acetone (T2), capsaicin taken out with ethanol
        r(EXTRACTING).in("materials/chili_powder", 4).fin("n_hexane", B).fout("paprika_oleoresin", 250).fout("n_hexane", 750).src()
            .mode("solvent_extraction").add("paprika_oleoresin");
        r(EXTRACTING).in("materials/chili_powder", 4).fin("acetone", B).fout("paprika_oleoresin", 250).fout("acetone", 750).src()
            .mode("solvent_extraction").add("paprika_oleoresin_by_acetone");
        r(EXTRACTING).fin("paprika_oleoresin", B).fin("ethanol", B).out("capsanthin", 1).out("capsaicin", 1).src(30)
            .fout("ethanol", 800).src().mode("liquid_extraction").add("capsanthin");
        // 61 B: turmeric extracted with ethanol, crystallized cold (T2)
        r(EXTRACTING).in("chemicals/turmeric", 4).fin("ethanol", B).out("curcumin", 1).src(10).fout("ethanol", 750).src()
            .mode("solvent_extraction").add("curcumin_extract");
        r(CRYSTALLIZING).in("chemicals/curcumin", 2).fin("ethanol", B).out("curcumin", 1).temp("cooling").add("curcumin_crystallizing");
        // 62 B: gardenia extracted, on macroporous resin, spray dried (T2–T3)
        r(EXTRACTING).in("materials/crushed_gardenia", 4).fin("ethanol", B).out("gardenia_yellow", 1).src(0.5).fout("ethanol", 750).src()
            .add("gardenia_yellow_extract");
        resin("gardenia_yellow", "catalysts/macroporous_resin");
        r(DRYING).in("chemicals/gardenia_yellow", 2).out("gardenia_yellow", 1).heat("sensitive").add("gardenia_yellow_spray_drying");
        // 63 A: Ashbya fermentation on oil and glucose (T4), recrystallized
        r(FERMENTING).in("chemicals/glucose", 2).fin("crude_soybean_oil", 500).fin("water", B).cat("cultures/ashbya", CULTURE_RUNS, PURE)
            .out("riboflavin", 1).src(80).sterile().mode("aerated").add("riboflavin");
        recrystallizing("riboflavin");
        // 64 A: caramel with ammonia (class III) or ammonia and sulfite (class IV) (T3)
        r(REACTING).in("chemicals/sucrose", 2).fin("ammonia", 200).fin("water", B).fout("caramel", B).src(45).temp("heated")
            .add("caramel_ammonia");
        r(REACTING).in("chemicals/sucrose", 2).fin("ammonia", 200).fin("sulfur_dioxide", 200).fin("water", B).fout("caramel", B).src(45)
            .temp("heated").add("caramel_ammonium_sulfite");
    }

    // ---- 65–69 antioxidants ----

    private void antioxidants() {
        // 69 B, 65 A: soybean oil degummed (lecithin from the gums), deodorized by stripping (tocopherol in the distillate)
        r(SEPARATING).fin("crude_soybean_oil", B).fout("refined_soybean_oil", 900).out("lecithin", 1).src(60).method("centrifuge")
            .add("soybean_oil_degumming");
        r(EXTRACTING).in("chemicals/lecithin", 2).fin("acetone", B).out("lecithin", 1).fout("acetone", 900).src()
            .mode("solvent_extraction").add("lecithin_deoiling");
        r(DISTILLING).fin("refined_soybean_oil", B).fout("refined_soybean_oil", 980).fout("alpha_tocopherol", 20).src(10)
            .mode("stripping").add("soybean_oil_deodorizing");
        r(DISTILLING).fin("alpha_tocopherol", B).fout("alpha_tocopherol", 500).method("molecular_distillation").add("tocopherol_concentrate");
        // 66 A: tea brewed and boiled down (T1); B: extracted with ethyl acetate, spray dried (T2)
        r(EXTRACTING).in("materials/tea", 4).fin("water", B).out("epigallocatechin_gallate", 1).src(5).temp("heated").add("tea_polyphenols");
        r(EXTRACTING).in("chemicals/epigallocatechin_gallate", 2).fin("ethyl_acetate", B).out("epigallocatechin_gallate", 1)
            .fout("ethyl_acetate", 900).src().mode("liquid_extraction").add("tea_polyphenols_extraction");
        r(DRYING).in("chemicals/epigallocatechin_gallate", 2).out("epigallocatechin_gallate", 1).heat("sensitive")
            .add("tea_polyphenols_spray_drying");
        // 67 A: rosemary in ethanol (T2); B: supercritical carbon dioxide (T4)
        r(EXTRACTING).in("materials/rosemary", 4).fin("ethanol", B).out("carnosic_acid", 1).src(10).fout("ethanol", 750).src()
            .mode("solvent_extraction").add("rosemary_extract");
        r(EXTRACTING).in("materials/rosemary", 4).fin("carbon_dioxide", B).out("carnosic_acid", 1).src(30).fout("carbon_dioxide", 900).src()
            .method("supercritical").add("rosemary_extract_supercritical");
        resin("carnosic_acid", "catalysts/macroporous_resin");
        // 68 A: ascorbic acid on bicarbonate, crystallized from ethanol (T2)
        r(REACTING).in("chemicals/ascorbic_acid", 1).in("chemicals/sodium_bicarbonate", 1).fin("ethanol", B).out("sodium_ascorbate", 1)
            .fout("carbon_dioxide", B).fout("ethanol", 900).src().tier(2).add("sodium_ascorbate_from_ascorbic_acid");
        recrystallizing("sodium_ascorbate");
        recrystallizing("lecithin");
    }

    // ---- 70–72 emulsifiers ----

    private void emulsifiers() {
        // 70 A: fat methanolysis to the methyl ester, transesterified with sucrose under vacuum (T3)
        r(REACTING).in("chemicals/animal_fat", 1).in("chemicals/sodium_methoxide", 1).fin("methanol", 3 * B).fout("methyl_stearate", 3 * B)
            .fout("glycerol", B).src().temp("heated").add("methyl_stearate");
        r(REACTING).in("chemicals/sucrose", 1).in("chemicals/potassium_carbonate", 1).fin("methyl_stearate", B)
            .out("sucrose_monostearate", 1).fout("methanol", B).src().temp("heated").pressure("vacuum").add("sucrose_esters");
        // 71 A: sorbitol and stearic acid dehydrated and esterified under nitrogen (T2); span 80 the same with oleic acid
        r(REACTING).in("chemicals/sorbitol", 1).in("chemicals/stearic_acid", 1).in("chemicals/sodium_hydroxide", 1)
            .out("sorbitan_monostearate", 1).temp("heated").inert().add("sorbitan_monostearate");
        r(REACTING).in("chemicals/sorbitol", 1).in("chemicals/sodium_hydroxide", 1).fin("oleic_acid", B)
            .fout("sorbitan_monooleate", B).temp("heated").inert().add("sorbitan_monooleate");
        // 72 A: ethylene oxide over silver, twenty of it on span 80 (T3)
        r(GAS_REACTING).fin("ethylene", 2 * B).fout("ethylene_oxide", 2 * B).mode("gas_catalysis").air().cat("catalysts/silver", 64)
            .add("ethylene_oxide");
        r(REACTING).fin("sorbitan_monooleate", 500).fin("ethylene_oxide", 10 * B).fout("polysorbate_80", 500).mode("ethoxylation")
            .pressure("high").temp("heated").add("polysorbate_80");
    }

    // ---- 73–78 flavour enhancers and others ----

    private void others() {
        // 73 A: yeast RNA cut by nuclease (T4); with deaminase the adenylic acid becomes inosinic acid
        // I31: the rest of the hydrolysate (cytidylic and uridylic acids) is kept for 551, 552
        r(FERMENTING).in("materials/spent_yeast", 4).in("chemicals/salt", 1).fin("water", B).cat("enzymes/nuclease", ENZYME_RUNS)
            .out("guanylic_acid", 1).src(60).fout("nucleotide_hydrolysate", 500).sterile().airtight().add("guanylic_acid");
        r(FERMENTING).in("materials/spent_yeast", 4).in("chemicals/salt", 1).fin("water", B).cat("enzymes/nuclease", ENZYME_RUNS)
            .cat("enzymes/deaminase", ENZYME_RUNS).out("inosinic_acid", 1).src(60).sterile().airtight().add("inosinic_acid");
        r(REACTING).in("chemicals/inosinic_acid", 1).in("chemicals/sodium_hydroxide", 2).fin("water", B).out("inosinic_disodium", 1)
            .add("inosinic_disodium");
        r(REACTING).in("chemicals/guanylic_acid", 1).in("chemicals/sodium_hydroxide", 2).fin("water", B).out("guanylic_disodium", 1)
            .add("guanylic_disodium");
        recrystallizing("inosinic_disodium");
        recrystallizing("guanylic_disodium");
        // 74, 75: calcium chloride and potassium chloride recrystallized (made in batch 1)
        recrystallizing("calcium_chloride");
        recrystallizing("potassium_chloride");
        // 76 A: water glass (T3): SiO2 + Na2CO3 → Na2SiO3 + CO2; precipitated by sulfuric acid
        r(KILN).in("materials/quartz", 1).in("chemicals/soda_ash", 1).fout("sodium_silicate", B).mode("melting").add("sodium_silicate");
        r(REACTING).fin("sodium_silicate", B).fin("sulfuric_acid", B).out("silicon_dioxide", 1).out("sodium_sulfate", 1).add("silicon_dioxide");
        // 77 A: blood-lye fusion of bones, potash and iron (the earth kiln has two inputs: the rotary kiln, T2)
        r(KILN).in("materials/bone", 4).in("chemicals/potassium_carbonate", 2).in("materials/iron", 1).out("potassium_ferrocyanide", 1)
            .src(50).mode("melting").add("potassium_ferrocyanide_blood_lye");
        recrystallizing("potassium_ferrocyanide");
        // 77 B: cyanide route (T3, optional): FeSO4 + 6 HCN + 3 Ca(OH)2 → Ca2[Fe(CN)6] + CaSO4 + 6 H2O
        r(REACTING).in("materials/iron", 1).fin("sulfuric_acid", B).out("ferrous_sulfate", 1).fout("hydrogen", B).add("ferrous_sulfate");
        r(REACTING).in("chemicals/ferrous_sulfate", 1).in("chemicals/slaked_lime", 3).fin("hydrogen_cyanide", 6 * B)
            .fout("calcium_ferrocyanide", B).out("gypsum", 1).add("calcium_ferrocyanide");
        r(REACTING).in("chemicals/potassium_carbonate", 2).fin("calcium_ferrocyanide", B).out("potassium_ferrocyanide", 1)
            .out("calcium_carbonate", 2).add("potassium_ferrocyanide");
        // 78 A: glycerol from the soap lye distilled (T2); B: propylene oxide hydrated (T3)
        r(DISTILLING).fin("glycerol", B).fout("glycerol", 900).add("glycerol_distilling");
        r(REACTING).fin("propylene_oxide", B).fin("water", B).fout("propylene_glycol", B).pressure("pressurized").add("propylene_glycol");
        r(DISTILLING).fin("propylene_glycol", B).fout("propylene_glycol", 900).method("multi_fraction").add("propylene_glycol_rectifying");
    }
}
