package com.tonywww.foodindustrial.content.batch4;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.ABSORBING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.BOILING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.ELECTROLYZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The fourth content batch, group A (I30, additives/15 §A): acidity regulators, anticaking, antifoaming,
 * bleaching, raising, colour-retention and flour-treatment agents, flavour enhancers and humectants,
 * 140–193. Routes already made by the first three batches are not written again (140 L-malic acid,
 * 143 hydrochloric acid, 145 slaked lime, 146 potassium hydroxide, 148 trona, 166 sodium bisulfite,
 * 173 sodium nitrate, 176, 177 the nucleotides); their missing steps are.
 */
public final class Batch4Salts extends Batch {
    private Batch4Salts(RecipeGen.Output out) {
        super(out, "batch4");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch4Salts(out).generate());
    }

    private void generate() {
        acidityRegulators();
        anticaking();
        antifoaming();
        bleaching();
        raising();
        colourRetention();
        flavourEnhancers();
        flourTreatment();
        humectants();
    }

    // ---- 140–155 acidity regulators ----

    private void acidityRegulators() {
        // 141: tartaric acid melted and dehydrated in the kiln (P43); as the dimer ester 2 C4H6O6 → C8H10O11 + H2O
        r(KILN).in("chemicals/tartaric_acid", 2).out("metatartaric_acid", 1).mode("condensation").add("metatartaric_acid");
        // 142 B: extracted wet-process acid freed of arsenic with sodium sulfide, then on activated carbon
        r(REACTING).in("chemicals/sodium_sulfide", 1).fin("phosphoric_acid", 4 * B).fout("phosphoric_acid", 4 * B).tier(2)
            .add("phosphoric_acid_dearsenic");
        adsorbing("phosphoric_acid", "catalysts/activated_carbon", null);
        // sodium sulfide by reducing salt cake with coal (black-ash process): Na2SO4 + 2 C → Na2S + 2 CO2
        r(KILN).in("chemicals/sodium_sulfate", 1).in("materials/coal", 2).out("sodium_sulfide", 1).fout("carbon_dioxide", 2 * B)
            .mode("melting").add("sodium_sulfide");
        // 143 B: distilled water for the food-grade absorption (pot still or column)
        r(DISTILLING).fin("water", B).fout("distilled_water", 900).add("distilled_water");
        r(ABSORBING).fin("hydrogen_chloride", B).fin("distilled_water", B).fout("hydrochloric_acid", B).tier(2)
            .add("hydrochloric_acid_distilled");
        // 144: benzene to cyclohexane, air oxidation to KA oil (counted as cyclohexanol, 09), nitric acid oxidation (P44, P45)
        r(REACTING).fin("benzene", B).fin("hydrogen", 3 * B).fout("cyclohexane", B).mode("hydrogenation").pressure("high")
            .temp("heated").cat("catalysts/nickel", 64).add("cyclohexane");
        r(REACTING).fin("cyclohexane", 2 * B).fin("oxygen", B).fout("cyclohexanol", 2 * B).mode("pressure_oxidation")
            .pressure("pressurized").temp("heated").cat("catalysts/cobalt", 64).add("cyclohexanol");
        r(REACTING).in("chemicals/phenol", 1).fin("hydrogen", 3 * B).fout("cyclohexanol", B).mode("hydrogenation").pressure("high")
            .temp("heated").cat("catalysts/nickel", 64).add("cyclohexanol_from_phenol");
        // I31: glutaric acid crystallizes from the mother liquor (19 §2 `glutaric_acid`, 联产)
        r(REACTING).fin("cyclohexanol", B).fin("nitric_acid", 2 * B).out("adipic_acid", 1).out("glutaric_acid", 1).src().chance(0.2)
            .fout("nitrous_oxide", B).temp("heated").method("corrosion_resistant").add("adipic_acid");
        recrystallizing("adipic_acid");
        // 146 A: causticizing potassium carbonate with slaked lime: K2CO3 + Ca(OH)2 → 2 KOH + CaCO3
        r(REACTING).in("chemicals/potassium_carbonate", 1).in("chemicals/slaked_lime", 1).fin("water", B).out("potassium_hydroxide", 2)
            .out("calcium_carbonate", 1).add("potassium_hydroxide_causticizing");
        // 147: 2 C6H8O7 + 3 K2CO3 → 2 C6H5K3O7 + 3 CO2 + 3 H2O
        r(REACTING).in("chemicals/citric_acid", 2).in("chemicals/potassium_carbonate", 3).fin("water", B).out("potassium_citrate", 2)
            .fout("carbon_dioxide", 3 * B).tier(2).add("potassium_citrate");
        decolouring("potassium_citrate");
        recrystallizing("potassium_citrate");
        // 148: the ground ore dissolved in hot mother liquor, clarified and crystallized
        recrystallizing("trona");
        decolouring("trona");
        // 149: half neutralization 2 : 1
        // 149 with sodium bicarbonate (2026-11-12, apart from 58 with soda ash, 11 问题 41)
        r(REACTING).in("chemicals/citric_acid", 1).in("chemicals/sodium_bicarbonate", 1).fin("water", B).out("monosodium_citrate", 1)
            .fout("carbon_dioxide", B).tier(2).add("monosodium_citrate");
        recrystallizing("monosodium_citrate");
        // 150 A: fermenter carbon dioxide into potassium carbonate solution (steeping vat); B: absorption tower
        r(ABSORBING).in("chemicals/potassium_carbonate", 1).fin("carbon_dioxide", B).fin("water", B).out("potassium_bicarbonate", 2)
            .add("potassium_bicarbonate");
        r(ABSORBING).in("chemicals/potassium_carbonate", 1).fin("carbon_dioxide", B).fin("water", B).out("potassium_bicarbonate", 2)
            .mode("carbonation").add("potassium_bicarbonate_tower");
        // 151: H3PO4 + 3 KOH → K3PO4 + 3 H2O, calcined anhydrous
        r(REACTING).in("chemicals/potassium_hydroxide", 3).fin("phosphoric_acid", B).out("tripotassium_phosphate", 1).tier(2)
            .add("tripotassium_phosphate");
        recrystallizing("tripotassium_phosphate");
        // 152: 2 C4H4O4 + Na2CO3 → 2 C4H3NaO4 + CO2 + H2O, warm
        r(REACTING).in("chemicals/fumaric_acid", 2).in("chemicals/soda_ash", 1).fin("water", B).out("monosodium_fumarate", 2)
            .fout("carbon_dioxide", B).temp("heated").add("monosodium_fumarate");
        recrystallizing("monosodium_fumarate");
        // 153 A: Aspergillus niger on glucose, pH held with caustic soda; B: gluconic acid neutralized
        r(FERMENTING).in("chemicals/glucose", 2).in("chemicals/sodium_hydroxide", 2).fin("water", 2 * B)
            .cat("cultures/aspergillus_niger", CULTURE_RUNS, PURE).out("sodium_gluconate", 2).src(90).sterile().mode("aerated")
            .add("sodium_gluconate_fermentation");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("gluconic_acid", B).out("sodium_gluconate", 1).tier(2).add("sodium_gluconate");
        decolouring("sodium_gluconate");
        recrystallizing("sodium_gluconate");
        // 154, 155: C4H6O5 + Na2CO3 → C4H4Na2O5 + CO2 + H2O
        r(REACTING).in("chemicals/malic_acid", 1).in("chemicals/soda_ash", 1).fin("water", B).out("sodium_dl_malate", 1)
            .fout("carbon_dioxide", B).tier(2).add("sodium_dl_malate");
        decolouring("sodium_dl_malate");
        r(REACTING).in("chemicals/l_malic_acid", 1).in("chemicals/soda_ash", 1).fin("water", B).out("sodium_l_malate", 1)
            .fout("carbon_dioxide", B).tier(2).add("sodium_l_malate");
        decolouring("sodium_l_malate");
    }

    // ---- 156–162 anticaking agents ----

    private void anticaking() {
        // 156: dissolving pulp hydrolysed with dilute hydrochloric acid (P39), washed, spray dried
        r(REACTING).in("chemicals/cellulose", 4).fin("hydrochloric_acid", B).out("microcrystalline_cellulose", 3).temp("heated")
            .method("corrosion_resistant").add("microcrystalline_cellulose");
        // 157 A: double decomposition of soap: 2 C18H35NaO2 + MgCl2 → C36H70MgO4 + 2 NaCl; with Epsom salt likewise
        r(REACTING).in("chemicals/sodium_stearate", 2).in("chemicals/magnesium_chloride", 1).fin("water", B)
            .out("magnesium_stearate", 1).out("salt", 2).temp("heated").tier(2).add("magnesium_stearate");
        r(REACTING).in("chemicals/sodium_stearate", 2).in("chemicals/epsom_salt", 1).fin("water", B)
            .out("magnesium_stearate", 1).out("sodium_sulfate", 1).temp("heated").tier(2).add("magnesium_stearate_from_epsom_salt");
        // 157 B: direct fusion: 2 C18H36O2 + Mg(OH)2 → C36H70MgO4 + 2 H2O
        r(REACTING).in("chemicals/stearic_acid", 2).in("chemicals/magnesium_hydroxide", 1).out("magnesium_stearate", 1).temp("heated")
            .tier(2).add("magnesium_stearate_direct");
        // 158 (optional route, hydrogen cyanide): the calcium salt of 77 B turned over with soda ash
        r(REACTING).in("chemicals/soda_ash", 2).fin("calcium_ferrocyanide", B).out("sodium_ferrocyanide", 1).out("calcium_carbonate", 2)
            .add("sodium_ferrocyanide");
        recrystallizing("sodium_ferrocyanide");
        // 159: hydrothermal synthesis (P81): Ca(OH)2 + SiO2 → CaSiO3 + H2O; diatomite or precipitated silica
        r(REACTING).in("chemicals/slaked_lime", 1).in("catalysts/diatomite", 1).fin("water", B).out("calcium_silicate", 1)
            .pressure("pressurized").temp("heated").add("calcium_silicate");
        r(REACTING).in("chemicals/slaked_lime", 1).in("chemicals/silicon_dioxide", 1).fin("water", B).out("calcium_silicate", 1)
            .pressure("pressurized").temp("heated").add("calcium_silicate_from_silica");
        // 160: fresh ferric hydroxide (ammonia) dissolved in citric acid, ammonia added, vacuum concentrated, drum dried
        r(REACTING).in("chemicals/ferric_chloride", 1).fin("ammonia", 3 * B).fin("water", 3 * B).out("ferric_hydroxide", 1)
            .out("ammonium_chloride", 3).add("ferric_hydroxide_by_ammonia");
        r(REACTING).in("chemicals/ferric_hydroxide", 1).in("chemicals/citric_acid", 1).fin("water", B).fout("ferric_citrate", B)
            .add("ferric_citrate");
        r(REACTING).fin("ferric_citrate", B).fin("ammonia", B).out("ferric_ammonium_citrate", 1).pressure("vacuum")
            .add("ferric_ammonium_citrate");
        // 161: sodium tartrate isomerized by long alkaline heating, complexed with ferric chloride
        r(REACTING).in("chemicals/tartaric_acid", 1).in("chemicals/sodium_hydroxide", 2).fin("water", B).fout("sodium_tartrate", B)
            .temp("heated").add("sodium_tartrate");
        r(REACTING).in("chemicals/ferric_chloride", 1).fin("sodium_tartrate", B).fout("ferric_tartrate", B).add("ferric_tartrate");
        // 162 A: bamboo cooked in potash lye, pounded and dried (crude); B: the sulfite pulp of I18 bleached, hemicellulose leached
        r(BOILING).in("materials/bamboo", 4).fin("potash_lye", B).out("cellulose", 1).src().add("cellulose_from_bamboo");
        r(REACTING).in("chemicals/cellulose", 4).in("chemicals/sodium_hydroxide", 1).fin("sodium_hypochlorite", B).out("cellulose", 4)
            .tier(2).add("cellulose_bleaching");
    }

    // ---- 163 polydimethylsiloxane ----

    private void antifoaming() {
        // silicon by carbothermal reduction in the arc furnace (P34): SiO2 + 2 C → Si + 2 CO
        r(KILN).in("materials/quartz", 1).in("materials/coke", 2).out("silicon", 1).fout("carbon_monoxide", 2 * B)
            .cat("catalysts/electrode", 16).temp("extreme").add("silicon");
        // methyl chloride by hydrochlorination of methanol over alumina (P82): CH4O + HCl → CH3Cl + H2O
        r(GAS_REACTING).fin("methanol", B).fin("hydrogen_chloride", B).fout("methyl_chloride", B).mode("gas_catalysis")
            .temp("heated").cat("catalysts/alumina", 64).add("methyl_chloride");
        // direct process in the fluidized bed on copper (P82): 2 CH3Cl + Si → C2H6Cl2Si; the crude silanes split as they leave
        r(GAS_REACTING).in("chemicals/silicon", 1).fin("methyl_chloride", 2 * B).fout("dimethyldichlorosilane", 800)
            .fout("methyltrichlorosilane", 100).fout("trimethylchlorosilane", 100).mode("fluidized_bed").temp("heated")
            .cat("catalysts/copper", 64).add("methylchlorosilanes");
        r(DISTILLING).fin("dimethyldichlorosilane", B).fout("dimethyldichlorosilane", 900).fout("methyltrichlorosilane", 100)
            .add("dimethyldichlorosilane_rectifying");
        // hydrolysis (lined reactor): 4 C2H6Cl2Si + 4 H2O → C8H24O4Si4 + 8 HCl; end-capper 2 C3H9ClSi + H2O → C6H18OSi2 + 2 HCl
        r(REACTING).fin("dimethyldichlorosilane", B).fin("water", B).fout("octamethylcyclotetrasiloxane", 250)
            .fout("hydrogen_chloride", 2 * B).method("corrosion_resistant").add("octamethylcyclotetrasiloxane");
        r(REACTING).fin("trimethylchlorosilane", 2 * B).fin("water", B).fout("hexamethyldisiloxane", B).fout("hydrogen_chloride", 2 * B)
            .method("corrosion_resistant").add("hexamethyldisiloxane");
        // base-catalysed ring-opening polymerization, end-capped; light ends stripped under vacuum
        r(REACTING).in("chemicals/potassium_hydroxide", 1).fin("octamethylcyclotetrasiloxane", 4 * B).fin("hexamethyldisiloxane", 100)
            .fout("polydimethylsiloxane", 4 * B).mode("polymerization").temp("heated").add("polydimethylsiloxane");
        r(DISTILLING).fin("polydimethylsiloxane", B).fout("polydimethylsiloxane", 900).fout("octamethylcyclotetrasiloxane", 100)
            .mode("vacuum_distillation").add("polydimethylsiloxane_stripping");
    }

    // ---- 164–168 bleaching agents ----

    private void bleaching() {
        // 164: K2CO3 + 2 SO2 → K2S2O5 + CO2, absorbed to pH 4 and crystallized cold
        r(ABSORBING).in("chemicals/potassium_carbonate", 1).fin("sulfur_dioxide", 2 * B).out("potassium_metabisulfite", 1)
            .fout("carbon_dioxide", B).tier(2).add("potassium_metabisulfite");
        // 165: 2 NaHSO3 + Na2CO3 → 2 Na2SO3 + CO2 + H2O, hot crystallized anhydrous
        r(REACTING).in("chemicals/soda_ash", 1).fin("sodium_bisulfite", 2 * B).out("sodium_sulfite", 2).fout("carbon_dioxide", B)
            .temp("heated").tier(2).add("sodium_sulfite_from_bisulfite");
        recrystallizing("sodium_sulfite");
        // 166: the solution decoloured on activated carbon (P24)
        r(REACTING).in("chemicals/activated_carbon", 1).fin("sodium_bisulfite", 4 * B).fout("sodium_bisulfite", 4 * B).tier(2)
            .add("sodium_bisulfite_decolouring");
        // 167 A: zinc dust and sulfur dioxide, zinc thrown down with caustic soda, salted out, dried under nitrogen
        r(REACTING).in("materials/zinc", 1).fin("sulfur_dioxide", 2 * B).fin("water", B).fout("zinc_dithionite", B).tier(2)
            .add("zinc_dithionite");
        r(REACTING).in("chemicals/sodium_hydroxide", 2).in("chemicals/salt", 1).fin("zinc_dithionite", B)
            .out("sodium_dithionite", 1).out("zinc_hydroxide", 1).inert().add("sodium_dithionite");
        // 167 B: sodium formate from carbon monoxide, reduced with sulfur dioxide in aqueous methanol (inert)
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("carbon_monoxide", B).out("sodium_formate", 1).pressure("pressurized")
            .temp("heated").add("sodium_formate");
        r(REACTING).in("chemicals/sodium_formate", 1).in("chemicals/sodium_hydroxide", 1).fin("sulfur_dioxide", 2 * B)
            .fin("methanol", B).out("sodium_dithionite", 1).fout("methanol", 900).src().pressure("pressurized").temp("heated")
            .add("sodium_dithionite_formate");
        // 168 B: sublimed sulfur (P20)
        r(DISTILLING).in("chemicals/sulfur", 2).out("sulfur", 1).mode("sublimation").add("sulfur_sublimation");
    }

    // ---- 169–172 raising agents ----

    private void raising() {
        // ammonium sulfate from hartshorn salt or ammonia and sulfuric acid
        r(REACTING).in("chemicals/ammonium_bicarbonate", 2).fin("sulfuric_acid", B).out("ammonium_sulfate", 1).fout("carbon_dioxide", 2 * B)
            .add("ammonium_sulfate");
        r(ABSORBING).fin("ammonia", 2 * B).fin("sulfuric_acid", B).out("ammonium_sulfate", 1).add("ammonium_sulfate_absorbing");
        // 169: Al2(SO4)3 + (NH4)2SO4 → 2 NH4Al(SO4)2·12H2O, cold crystallized
        r(REACTING).in("chemicals/aluminium_sulfate", 1).in("chemicals/ammonium_sulfate", 1).fin("water", B).out("ammonium_alum", 2)
            .temp("cooling").add("ammonium_alum");
        recrystallizing("ammonium_alum");
        // 170 B: H3PO4 + Ca(OH)2 → CaHPO4·2H2O, cold precipitation keeps the dihydrate
        r(REACTING).in("chemicals/slaked_lime", 1).fin("phosphoric_acid", B).out("dicalcium_phosphate", 1).temp("cooling").tier(2)
            .add("dicalcium_phosphate");
        // 171: phosphoric acid takes up ammonia to pH 8 in the tower
        // 171: monoammonium phosphate (454) ammoniated a second time (2026-11-12, 11 问题 41)
        r(ABSORBING).in("chemicals/monoammonium_phosphate", 1).fin("ammonia", B).out("diammonium_phosphate", 1).add("diammonium_phosphate");
        // 172 A: hartshorn salt resublimed (P20); B: ammonia, carbon dioxide and steam (carbamate mixture, as ammonium carbonate)
        r(DISTILLING).in("chemicals/ammonium_carbonate", 2).out("ammonium_carbonate", 1).mode("sublimation")
            .add("ammonium_carbonate_sublimation");
        r(ABSORBING).fin("ammonia", 2 * B).fin("carbon_dioxide", B).out("ammonium_carbonate", 1).mode("absorption")
            .add("ammonium_carbonate");
    }

    // ---- 173–175 colour-retention agents ----

    private void colourRetention() {
        // 173: sodium nitrate recrystallized to food grade
        recrystallizing("sodium_nitrate");
        // 174 A: saltpetre melted and partly decomposed (P33): 2 KNO3 → 2 KNO2 + O2, leached and fractionally crystallized
        r(KILN).in("chemicals/potassium_nitrate", 2).out("potassium_nitrite", 2).fout("oxygen", B).mode("melting").add("potassium_nitrite");
        // 174 B: nitrous gases absorbed in potassium carbonate: K2CO3 + NO + NO2 → 2 KNO2 + CO2
        r(ABSORBING).in("chemicals/potassium_carbonate", 1).fin("nitric_oxide", B).fin("nitrogen_dioxide", B).out("potassium_nitrite", 2)
            .fout("carbon_dioxide", B).add("potassium_nitrite_absorbing");
        recrystallizing("potassium_nitrite");
        // 175: C12H22CaO14 + FeSO4 → C12H22FeO14 + CaSO4, under nitrogen
        r(REACTING).in("chemicals/calcium_gluconate", 1).in("chemicals/ferrous_sulfate", 1).fin("water", B)
            .out("ferrous_gluconate", 1).out("gypsum", 1).inert().add("ferrous_gluconate");
        recrystallizing("ferrous_gluconate");
    }

    // ---- 176–180 flavour enhancers ----

    private void flavourEnhancers() {
        // 178: maleic acid hydrogenated on nickel (P46), neutralized: C4H4O4 + H2 → C4H6O4; + 2 NaOH → C4H4Na2O4
        r(REACTING).in("chemicals/maleic_acid", 1).fin("hydrogen", B).out("succinic_acid", 1).mode("hydrogenation").pressure("high")
            .temp("heated").cat("catalysts/nickel", 64).add("succinic_acid");
        recrystallizing("succinic_acid");
        r(REACTING).in("chemicals/succinic_acid", 1).in("chemicals/sodium_hydroxide", 2).fin("water", B).out("disodium_succinate", 1)
            .tier(2).add("disodium_succinate");
        recrystallizing("disodium_succinate");
        // 179: L-aspartic acid decarboxylated by immobilized Pseudomonas dacunhae cells: C4H7NO4 → C3H7NO2 + CO2
        r(FERMENTING).in("chemicals/l_aspartic_acid", 1).fin("water", B).cat("catalysts/immobilized_aspartate_decarboxylase", IMMOBILIZED_RUNS)
            .out("l_alanine", 1).src(95).fout("carbon_dioxide", B).sterile().airtight().add("l_alanine");
        decolouring("l_alanine");
        recrystallizing("l_alanine");
        // 180 A: gelatin boiled with dilute sulfuric acid, limed, the glycine crystallized (crude, with other amino acids)
        r(REACTING).in("chemicals/gelatin", 4).in("chemicals/slaked_lime", 1).fin("sulfuric_acid", B).out("glycine", 1).src()
            .out("gypsum", 1).temp("heated").tier(2).add("glycine_from_gelatin");
        // 180 B: C2H3ClO2 + 2 NH3 → C2H5NO2 + NH4Cl, the ammonium chloride crystallized apart
        r(REACTING).in("chemicals/chloroacetic_acid", 1).fin("ammonia", 2 * B).fin("water", B).out("glycine", 1).out("ammonium_chloride", 1)
            .tier(2).add("glycine");
        recrystallizing("glycine");
    }

    // ---- 181, 182 flour-treatment agents ----

    private void flourTreatment() {
        // 181 A: keratin refluxed in hydrochloric acid (P39), cystine at its isoelectric point, reduced at the cathode (P48)
        r(REACTING).in("materials/feather", 8).fin("hydrochloric_acid", 2 * B).out("l_cystine", 1).src().temp("heated")
            .method("corrosion_resistant").add("l_cystine");
        r(REACTING).in("materials/wool", 8).fin("hydrochloric_acid", 2 * B).out("l_cystine", 1).src().temp("heated")
            .method("corrosion_resistant").add("l_cystine_from_wool");
        decolouring("l_cystine");
        recrystallizing("l_cystine");
        r(ELECTROLYZING).in("chemicals/l_cystine", 1).fin("hydrochloric_acid", B).out("l_cysteine", 2).mode("electroreduction")
            .cat("catalysts/electrode", 64).add("l_cysteine");
        // 181 B: the engineered E. coli on glucose and ammonium sulfate
        r(FERMENTING).in("chemicals/glucose", 2).in("chemicals/ammonium_sulfate", 1).fin("water", B)
            .cat("cultures/e_coli_cysteine", CULTURE_RUNS, PURE).out("l_cysteine", 1).src(90).sterile().mode("aerated")
            .add("l_cysteine_fermentation");
        // the hydrochloride, crystallized as the monohydrate: C3H7NO2S + HCl + H2O → C3H8ClNO2S·H2O
        r(CRYSTALLIZING).in("chemicals/l_cysteine", 1).fin("hydrochloric_acid", B).out("l_cysteine_hydrochloride", 1).temp("cooling")
            .add("l_cysteine_hydrochloride");
        recrystallizing("l_cysteine_hydrochloride");
        // 182 A: basic carbonate thrown down from magnesium chloride and soda ash
        r(REACTING).in("chemicals/magnesium_chloride", 5).in("chemicals/soda_ash", 5).fin("water", B).out("magnesium_carbonate", 1)
            .out("salt", 10).fout("carbon_dioxide", B).add("magnesium_carbonate");
        // 182 B: magnesium hydroxide carbonated under pressure, the bicarbonate solution boiled
        r(REACTING).in("chemicals/magnesium_hydroxide", 1).fin("carbon_dioxide", 2 * B).fin("water", B).fout("magnesium_bicarbonate", B)
            .pressure("pressurized").add("magnesium_bicarbonate");
        r(CRYSTALLIZING).fin("magnesium_bicarbonate", 5 * B).out("magnesium_carbonate", 1).fout("carbon_dioxide", 6 * B)
            .temp("heated").tier(2).add("magnesium_carbonate_from_bicarbonate");
    }

    // ---- 183–193 humectants ----

    private void humectants() {
        // 183: Na2HPO4 + NaOH → Na3PO4 + H2O, crystallized as the dodecahydrate
        r(REACTING).in("chemicals/disodium_phosphate", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B).out("trisodium_phosphate", 1)
            .tier(2).add("trisodium_phosphate");
        recrystallizing("trisodium_phosphate");
        // 184: crystallized at 40–95 °C
        recrystallizing("monosodium_phosphate");
        // 185: 2 H3PO4 + CaCO3 → Ca(H2PO4)2·H2O + CO2
        r(REACTING).in("chemicals/calcium_carbonate", 1).fin("phosphoric_acid", 2 * B).out("monocalcium_phosphate", 1)
            .fout("carbon_dioxide", B).tier(2).add("monocalcium_phosphate");
        // 187: H3PO4 + KCl → KH2PO4 + HCl (the hydrogen chloride to the absorption tower); 186: 2 KH2PO4 + K2CO3 →
        // 2 K2HPO4 + CO2 + H2O (2026-11-12, 11 问题 41)
        r(REACTING).in("chemicals/potassium_chloride", 1).fin("phosphoric_acid", B).out("monopotassium_phosphate", 1)
            .fout("hydrogen_chloride", B).temp("heated").tier(2).add("monopotassium_phosphate");
        recrystallizing("monopotassium_phosphate");
        r(REACTING).in("chemicals/monopotassium_phosphate", 2).in("chemicals/potassium_carbonate", 1).fin("water", B).out("dipotassium_phosphate", 2)
            .fout("carbon_dioxide", B).tier(2).add("dipotassium_phosphate");
        recrystallizing("dipotassium_phosphate");
        // 188, 189: lactic acid neutralized, decoloured (P24), concentrated to a 60 % solution
        r(REACTING).in("chemicals/potassium_carbonate", 1).fin("lactic_acid", 2 * B).fout("potassium_lactate", 2 * B)
            .fout("carbon_dioxide", B).tier(2).add("potassium_lactate");
        r(REACTING).in("chemicals/soda_ash", 1).fin("lactic_acid", 2 * B).fout("sodium_lactate", 2 * B).fout("carbon_dioxide", B)
            .tier(2).add("sodium_lactate");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("lactic_acid", B).fout("sodium_lactate", B).tier(2)
            .add("sodium_lactate_from_caustic_soda");
        for (String lactate : new String[]{"potassium_lactate", "sodium_lactate"}) {
            r(REACTING).in("chemicals/activated_carbon", 1).fin(lactate, 4 * B).fout(lactate, 4 * B).tier(2).add(lactate + "_decolouring");
        }
        // 190: the acid pyrophosphate and the tetrasodium salt (53) dissolved together in the stirred reactor and dried below
        // 170 °C, where the trisodium salt splits back into the two (11 问题 41: the 1 : 1 orthophosphates in the kiln are 51's
        // inputs): Na2H2P2O7 + Na4P2O7 → 2 Na3HP2O7; 191–193 condensed in the rotary kiln (P43)
        r(REACTING).in("chemicals/sodium_acid_pyrophosphate", 1).in("chemicals/tetrasodium_pyrophosphate", 1).fin("water", B)
            .out("trisodium_pyrophosphate", 2).tier(2).add("trisodium_pyrophosphate");
        r(KILN).in("chemicals/monopotassium_phosphate", 1).out("potassium_polymetaphosphate", 1).mode("melting").temp("high")
            .add("potassium_polymetaphosphate");
        r(KILN).in("chemicals/monocalcium_phosphate", 1).out("calcium_acid_pyrophosphate", 1).mode("condensation")
            .add("calcium_acid_pyrophosphate");
        r(KILN).in("chemicals/dipotassium_phosphate", 2).out("tetrapotassium_pyrophosphate", 1).mode("condensation")
            .add("tetrapotassium_pyrophosphate");
    }
}
