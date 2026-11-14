package com.tonywww.foodindustrial.content.batch3;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.ABSORBING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.BOILING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.ELECTROLYZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.MIXING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SOLID_FERMENTING;

/**
 * The third content batch (I20): the T1-and-later routes of 79–139 (additives/14): synthetic colours and
 * their lakes, intense sweeteners, synthetic antioxidants, enzymes, nutrients, flavourings and gum base.
 * Multi-step syntheses whose intermediates are not in the material table are collapsed into one step
 * (additives/04 §实现约定). The chemistry steps 14 puts at T4 run on the T3 high-pressure reactor (no T4
 * machine takes liquid-phase reactions); their T4 parts are the enzymes and cultures.
 */
public final class Batch3Recipes extends Batch {
    private Batch3Recipes(RecipeGen.Output out) {
        super(out, "batch3");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch3Recipes(out).generate());
    }

    private void generate() {
        aromatics();
        colours();
        sweeteners();
        antioxidants();
        enzymes();
        nutrients();
        flavourings();
        gumBase();
    }

    // ---- the aromatic chain (P66–P70) ----

    private void aromatics() {
        // nitration, reduction, sulfonation: benzene → nitrobenzene → aniline → sulfanilic acid
        r(REACTING).fin("benzene", B).fin("nitric_acid", B).fin("sulfuric_acid", 200).fout("nitrobenzene", B).temp("cooling")
            .method("corrosion_resistant").add("nitrobenzene");
        r(REACTING).fin("nitrobenzene", B).fin("hydrogen", 3 * B).fout("aniline", B).mode("hydrogenation").pressure("high").temp("heated")
            .cat("catalysts/nickel", 64).add("aniline");
        r(REACTING).in("materials/iron", 3).fin("nitrobenzene", B).fin("hydrochloric_acid", 200).fout("aniline", B).temp("heated")
            .add("aniline_by_iron");
        r(REACTING).fin("aniline", B).fin("sulfuric_acid", B).out("sulfanilic_acid", 1).temp("heated").add("sulfanilic_acid");
        // diazotization at 0–5 °C (P68): C6H7NO3S + NaNO2 + HCl → C6H4N2O3S + NaCl + 2 H2O
        r(REACTING).in("chemicals/sulfanilic_acid", 1).in("chemicals/sodium_nitrite", 1).fin("hydrochloric_acid", B)
            .fout("sulfanilic_diazonium", B).out("salt", 1).temp("cooling").add("sulfanilic_diazonium");
        // sodium sulfite (reductions, P67): Na2CO3 + SO2 → Na2SO3 + CO2
        r(ABSORBING).in("chemicals/soda_ash", 1).fin("sulfur_dioxide", B).out("sodium_sulfite", 1).fout("carbon_dioxide", B)
            .add("sodium_sulfite");
        r(REACTING).in("chemicals/sodium_sulfite", 2).fin("sulfanilic_diazonium", B).out("hydrazinobenzenesulfonic_acid", 1)
            .out("sodium_sulfate", 1).add("hydrazinobenzenesulfonic_acid");
        // naphthalene: sulfonated hot, alkali fusion (P70) to 2-naphthol; Schaeffer's and G acid
        r(REACTING).in("chemicals/naphthalene", 1).fin("sulfuric_acid", B).out("naphthalene_sulfonic_acid", 1).temp("heated")
            .add("naphthalene_sulfonic_acid");
        // the fusion gives the naphtholate, acidified in water in the stirred reactor (the kiln takes no fluid, 11 问题 42):
        // C10H8O3S + 3 NaOH → C10H7NaO + Na2SO3 + 2 H2O; 2 C10H7NaO + H2SO4 → 2 C10H8O + Na2SO4
        r(KILN).in("chemicals/naphthalene_sulfonic_acid", 1).in("chemicals/sodium_hydroxide", 3).out("sodium_2_naphtholate", 1)
            .out("sodium_sulfite", 1).mode("alkali_fusion").add("sodium_2_naphtholate");
        r(REACTING).in("chemicals/sodium_2_naphtholate", 2).fin("water", B).fin("sulfuric_acid", B).out("beta_naphthol", 2)
            .out("sodium_sulfate", 1).tier(2).add("beta_naphthol");
        r(REACTING).in("chemicals/beta_naphthol", 1).fin("sulfuric_acid", B).out("schaeffer_acid", 1).add("schaeffer_acid");
        // G acid: co-produced with R acid since I30 (Batch4Colours, 2026-11-11, user)
        r(REACTING).in("chemicals/naphthalene", 1).fin("nitric_acid", B).fin("sulfuric_acid", 200).out("nitronaphthalene", 1)
            .temp("cooling").method("corrosion_resistant").add("nitronaphthalene");
        r(REACTING).in("chemicals/nitronaphthalene", 1).fin("hydrogen", 3 * B).out("naphthylamine", 1).mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("naphthylamine");
        // naphthionic acid sulfonated and diazotized in one charge
        r(REACTING).in("chemicals/naphthylamine", 1).in("chemicals/sodium_nitrite", 1).fin("sulfuric_acid", B)
            .fin("hydrochloric_acid", B).fout("naphthionic_diazonium", B).temp("cooling").add("naphthionic_diazonium");
        // tar acids washed out of coal tar with caustic soda (90–92)
        r(EXTRACTING).in("chemicals/sodium_hydroxide", 1).fin("coal_tar", B).out("phenol", 1).src(80).out("p_cresol", 1).src(80)
            .mode("liquid_extraction").add("tar_acids");
        // aluminium hydroxide, freshly made (P71): Al2(SO4)3 + 3 Na2CO3 + 3 H2O → 2 Al(OH)3 + 3 Na2SO4 + 3 CO2
        r(REACTING).in("chemicals/aluminium_sulfate", 1).in("chemicals/soda_ash", 3).fin("water", B).out("aluminium_hydroxide", 2)
            .out("sodium_sulfate", 3).fout("carbon_dioxide", 3 * B).add("aluminium_hydroxide");
    }

    // ---- 79–84 synthetic colours and their lakes ----

    private void colours() {
        // 79: the pyrazolone from the hydrazine and diethyl oxaloacetate, coupled with the diazonium
        r(REACTING).in("chemicals/oxalic_acid", 1).in("chemicals/sodium_methoxide", 1).fin("ethyl_acetate", B).fin("ethanol", B)
            .fout("diethyl_oxaloacetate", B).temp("heated").add("diethyl_oxaloacetate");
        r(REACTING).in("chemicals/hydrazinobenzenesulfonic_acid", 1).in("chemicals/sodium_hydroxide", 2).fin("diethyl_oxaloacetate", B)
            .fin("sulfanilic_diazonium", B).out("tartrazine", 1).fout("ethanol", B).src(50).add("tartrazine");
        // 80: diazonium on Schaeffer's acid; 81: naphthionic diazonium on G acid; 82: cresidine diazonium on Schaeffer's acid
        r(REACTING).in("chemicals/schaeffer_acid", 1).in("chemicals/sodium_hydroxide", 2).fin("sulfanilic_diazonium", B)
            .out("sunset_yellow", 1).add("sunset_yellow");
        r(REACTING).in("chemicals/g_acid", 1).in("chemicals/sodium_hydroxide", 3).fin("naphthionic_diazonium", B).out("ponceau_4r", 1)
            .add("ponceau_4r");
        r(REACTING).fin("m_cresol", B).fin("sulfuric_acid", B).fin("ammonia", B).out("cresidine_sulfonic_acid", 1).temp("heated")
            .method("corrosion_resistant").add("cresidine_sulfonic_acid");
        r(REACTING).in("chemicals/cresidine_sulfonic_acid", 1).in("chemicals/sodium_nitrite", 1).in("chemicals/schaeffer_acid", 1)
            .fin("hydrochloric_acid", B).fin("soda_lye", B).out("allura_red", 1).temp("cooling").add("allura_red");
        // 83: condensed to the leuco base, oxidized (air over vanadium, or manganese dioxide)
        r(REACTING).fin("benzaldehyde", B).fin("sulfuric_acid", B).out("benzaldehyde_2_sulfonic_acid", 1).method("corrosion_resistant")
            .add("benzaldehyde_2_sulfonic_acid");
        r(REACTING).fin("aniline", B).fin("ethanol", B).fin("toluene", B).out("ethyl_sulfobenzyl_aniline", 1).temp("heated")
            .method("corrosion_resistant").add("ethyl_sulfobenzyl_aniline");
        r(REACTING).in("chemicals/benzaldehyde_2_sulfonic_acid", 1).in("chemicals/ethyl_sulfobenzyl_aniline", 2).fin("sulfuric_acid", B)
            .out("brilliant_blue", 1).temp("heated").air().add("brilliant_blue");
        r(REACTING).in("chemicals/benzaldehyde_2_sulfonic_acid", 1).in("chemicals/ethyl_sulfobenzyl_aniline", 2)
            .in("chemicals/manganese_dioxide", 1).fin("sulfuric_acid", B).out("brilliant_blue", 1).add("brilliant_blue_by_manganese");
        // 84 B: N-phenylglycine, alkali fusion to indoxyl, oxidized in air; indigo (A or B) sulfonated to indigo carmine
        r(REACTING).in("chemicals/chloroacetic_acid", 1).fin("aniline", B).out("n_phenylglycine", 1).fout("hydrogen_chloride", B)
            .add("n_phenylglycine");
        r(KILN).in("chemicals/n_phenylglycine", 2).in("chemicals/sodium_hydroxide", 2).out("indoxyl", 2).mode("alkali_fusion")
            .add("indoxyl");
        r(REACTING).in("chemicals/indoxyl", 2).fin("water", B).out("indigo", 1).src(90).air().add("indigo_synthetic");
        r(REACTING).in("chemicals/indigo", 1).in("chemicals/sodium_hydroxide", 2).fin("sulfur_trioxide", B).fin("sulfuric_acid", B)
            .out("indigo_carmine", 1).method("corrosion_resistant").add("indigo_carmine");
        // lakes (P71): the dye on fresh aluminium hydroxide, a third of the lake by mass
        for (String dye : new String[]{"tartrazine", "sunset_yellow", "ponceau_4r", "allura_red", "brilliant_blue", "indigo_carmine"}) {
            r(REACTING).in("chemicals/" + dye, 1).in("chemicals/aluminium_hydroxide", 2).fin("water", B).out(dye + "_lake", 1).degrade(67)
                .sources(0).add(dye + "_lake");
            r(REACTING).in("chemicals/" + dye, 4).in("chemicals/salt", 2).fin("water", B).out(dye, 3).rm(75).add(dye + "_salting_out");
        }
    }

    // ---- 85–89 intense sweeteners ----

    private void sweeteners() {
        // 85: L-aspartic acid by aspartase, L-phenylalanine by fermentation (sucrose medium), methyl ester, thermolysin coupling
        r(FERMENTING).in("chemicals/fumaric_acid", 1).fin("ammonia", B).fin("water", B).cat("enzymes/aspartase", ENZYME_RUNS)
            .out("l_aspartic_acid", 1).src(99).sterile().airtight().add("l_aspartic_acid");
        r(FERMENTING).in("chemicals/sucrose", 1).fin("ammonia", B).fin("water", B).cat("cultures/corynebacterium", CULTURE_RUNS, PURE)
            .out("l_phenylalanine", 1).src(90).sterile().mode("aerated").add("l_phenylalanine");
        r(REACTING).in("chemicals/l_phenylalanine", 1).fin("methanol", B).fin("hydrochloric_acid", 100).fout("phenylalanine_methyl_ester", B)
            .temp("heated").add("phenylalanine_methyl_ester");
        r(FERMENTING).in("chemicals/l_aspartic_acid", 1).fin("phenylalanine_methyl_ester", B).cat("enzymes/thermolysin", ENZYME_RUNS)
            .out("aspartame", 1).src(95).sterile().airtight().add("aspartame");
        recrystallizing("aspartame");
        // 86: diketene, sulfamic acid (urea, SO3, H2SO4), adduct cyclized by SO3, neutralized by potash
        r(REACTING).fin("ammonia", 2 * B).fin("carbon_dioxide", B).out("urea", 1).pressure("high").temp("heated").add("urea");
        r(REACTING).fin("ketene", 2 * B).fout("diketene", B).temp("cooling").add("diketene");
        r(REACTING).in("chemicals/urea", 1).fin("sulfur_trioxide", B).fin("sulfuric_acid", B).out("sulfamic_acid", 2)
            .fout("carbon_dioxide", B).method("corrosion_resistant").add("sulfamic_acid");
        r(REACTING).in("chemicals/sulfamic_acid", 1).fin("diketene", B).fout("acetoacetamide_n_sulfonic_acid", B).temp("cooling")
            .add("acetoacetamide_n_sulfonic_acid");
        r(REACTING).fin("acetoacetamide_n_sulfonic_acid", B).fin("sulfur_trioxide", B).out("acesulfame_acid", 1).fout("sulfuric_acid", B)
            .temp("cooling").method("corrosion_resistant").add("acesulfame_acid");
        r(REACTING).in("chemicals/acesulfame_acid", 1).in("chemicals/potassium_hydroxide", 1).fin("water", B)
            .out("acesulfame_potassium", 1).add("acesulfame_potassium");
        recrystallizing("acesulfame_potassium");
        // 87: sucrose-6-acetate over organotin, chlorinated by thionyl chloride and deacetylated in one charge (T3 here)
        r(REACTING).in("materials/tin", 1).fin("butane", 2 * B).fin("chlorine", B).out("dibutyltin_oxide", 1).temp("heated")
            .add("dibutyltin_oxide");
        r(REACTING).fin("methanol", 2 * B).fin("ammonia", B).fin("carbon_monoxide", B).fout("dimethylformamide", B).pressure("high")
            .temp("heated").add("dimethylformamide");
        r(REACTING).in("chemicals/sulfur", 1).fin("chlorine", B).fout("sulfur_dichloride", B).add("sulfur_dichloride");
        r(REACTING).fin("sulfur_dichloride", B).fin("sulfur_dioxide", B).fin("chlorine", B).fout("thionyl_chloride", 2 * B)
            .method("corrosion_resistant").add("thionyl_chloride");
        r(REACTING).in("chemicals/sucrose", 1).fin("acetic_anhydride", B).cat("catalysts/organotin", 64).out("sucrose_6_acetate", 1)
            .fout("acetic_acid", B).src(50).add("sucrose_6_acetate");
        r(REACTING).in("chemicals/sucrose_6_acetate", 1).in("chemicals/sodium_methoxide", 1).fin("thionyl_chloride", 3 * B)
            .fin("dimethylformamide", 100).out("sucralose", 1).fout("sulfur_dioxide", 3 * B).fout("hydrogen_chloride", 3 * B)
            .temp("cooling").method("corrosion_resistant").add("sucralose");
        recrystallizing("sucralose");
        // 88: tert-butyl chloride + ethylene → 1-chloro-3,3-dimethylbutane → aldehyde; reductive amination of aspartame
        r(GAS_REACTING).fin("butane", B).fout("isobutylene", B).fout("hydrogen", B).mode("cracking").add("isobutylene");
        r(REACTING).fin("isobutylene", B).fin("hydrogen_chloride", B).fout("tert_butyl_chloride", B).add("tert_butyl_chloride");
        // aluminium chloride (catalyst, 135): chlorinating alumina with coke needs a chlorine feed, the high-pressure reactor
        r(REACTING).in("chemicals/alumina", 1).in("materials/coal", 3).fin("chlorine", 3 * B).out("aluminium_chloride", 2)
            .fout("carbon_monoxide", 3 * B).temp("high").add("aluminium_chloride");
        r(REACTING).fin("tert_butyl_chloride", B).fin("ethylene", B).cat("catalysts/aluminium_chloride", 64)
            .fout("chlorodimethylbutane", B).temp("cooling").add("chlorodimethylbutane");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("chlorodimethylbutane", B).fin("oxygen", B).fout("dimethylbutanal", B)
            .out("salt", 1).temp("heated").add("dimethylbutanal");
        r(REACTING).in("catalysts/noble_metal", 1).in("chemicals/activated_carbon", 1).out("palladium_on_carbon", 2)
            .temp("heated").add("palladium_on_carbon");
        r(REACTING).in("chemicals/aspartame", 1).fin("dimethylbutanal", B).fin("hydrogen", B).cat("catalysts/palladium", 64)
            .out("neotame", 1).mode("hydrogenation").pressure("pressurized").add("neotame");
        recrystallizing("neotame");
        // 89: aniline hydrogenated to cyclohexylamine, with sulfamic acid, neutralized
        r(REACTING).fin("aniline", B).fin("hydrogen", 3 * B).fout("cyclohexylamine", B).mode("hydrogenation").pressure("high")
            .temp("high").cat("catalysts/nickel", 64).add("cyclohexylamine");
        r(REACTING).in("chemicals/sulfamic_acid", 1).fin("cyclohexylamine", B).out("cyclamic_acid", 1).fout("ammonia", B)
            .temp("heated").add("cyclamic_acid");
        r(REACTING).in("chemicals/cyclamic_acid", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B).out("sodium_cyclamate", 1)
            .add("sodium_cyclamate");
        recrystallizing("sodium_cyclamate");
    }

    // ---- 90–93 synthetic antioxidants (P72 alkylation) ----

    private void antioxidants() {
        // 90: p-cresol + 2 isobutylene → BHT
        r(REACTING).in("chemicals/p_cresol", 1).fin("isobutylene", 2 * B).fin("sulfuric_acid", 100).out("bht", 1)
            .method("corrosion_resistant").add("bht");
        recrystallizing("bht");
        // hydrogen peroxide by electrolysing sulfuric acid (the persulfate route) for the hydroquinone of 91 and 92
        r(ELECTROLYZING).fin("sulfuric_acid", 2 * B).fin("water", B).fout("hydrogen_peroxide", B).fout("hydrogen", B).src()
            .mode("electrooxidation").cat("catalysts/electrode", 64).add("hydrogen_peroxide");
        // I31: catechol is the coproduct of the hydroxylation (19 §2 `catechol`, 联产)
        r(REACTING).in("chemicals/phenol", 2).fin("hydrogen_peroxide", 2 * B).out("hydroquinone", 1).out("catechol", 1).src().temp("heated")
            .add("hydroquinone");
        // 92 (historic): aniline oxidized by manganese dioxide to benzoquinone, reduced by sulfur dioxide
        r(REACTING).in("chemicals/manganese_dioxide", 4).fin("aniline", 2 * B).fin("sulfuric_acid", 5 * B).out("p_benzoquinone", 2)
            .out("ammonium_sulfate", 1).add("p_benzoquinone");
        r(REACTING).in("chemicals/p_benzoquinone", 1).fin("sulfur_dioxide", B).fin("water", 2 * B).out("hydroquinone", 1)
            .fout("sulfuric_acid", B).add("hydroquinone_by_reduction");
        // 91: hydroquinone methylated, then alkylated
        r(REACTING).in("chemicals/hydroquinone", 1).fin("methanol", B).fin("sulfuric_acid", 100).out("p_methoxyphenol", 1)
            .temp("heated").add("p_methoxyphenol");
        r(REACTING).in("chemicals/p_methoxyphenol", 1).fin("isobutylene", B).fin("phosphoric_acid", 100).out("bha", 1).add("bha");
        r(REACTING).in("chemicals/hydroquinone", 1).fin("isobutylene", B).fin("phosphoric_acid", 100).fin("water", 100).out("tbhq", 1)
            .add("tbhq");
        recrystallizing("tbhq");
        // 93: tannin split by tannase (Aspergillus oryzae), gallic acid esterified with n-propanol (fusel oil)
        r(REACTING).in("materials/tannin_source", 4).fin("water", B).cat("enzymes/tannase", ENZYME_RUNS).out("gallic_acid", 1).src()
            .temp("heated").tier(2).add("gallic_acid");
        // I31 (18 §5): the crude fusel oil of the side draw kept as well (N402)
        r(DISTILLING).fin("ethanol", 2 * B).fout("ethanol", B).fout("n_propanol", 20).src(60).fout("isoamyl_alcohol", 30).src(60)
            .fout("crude_fusel_oil", 50).method("multi_fraction").add("fusel_oil");
        r(REACTING).in("chemicals/gallic_acid", 1).fin("n_propanol", B).fin("sulfuric_acid", 100).out("propyl_gallate", 1)
            .temp("heated").add("propyl_gallate");
        recrystallizing("propyl_gallate");
    }

    // ---- 94–106 enzymes (no purity): fermentation on the culture's medium, or pressed and extracted ----

    private void enzymes() {
        enzyme("alpha_amylase", "bacillus_subtilis", "chemicals/starch", 2);
        enzyme("alpha_amylase", "bacillus_licheniformis", "chemicals/starch", 2);
        r(FERMENTING).fin("liquefied_starch", 2 * B).cat("cultures/aspergillus_niger", CULTURE_RUNS, PURE).out("glucoamylase", 4)
            .sterile().mode("aerated").add("glucoamylase");
        r(FERMENTING).in("chemicals/glucose", 1).in("chemicals/gluten", 1).fin("water", B).cat("cultures/aspergillus_niger", CULTURE_RUNS, PURE)
            .out("glucose_oxidase", 4).sterile().mode("aerated").add("glucose_oxidase");
        r(FERMENTING).fin("pectin_liquor", 2 * B).cat("cultures/aspergillus_niger", CULTURE_RUNS, PURE).out("pectinase", 4)
            .sterile().mode("aerated").add("pectinase");
        enzyme("pullulanase", "bacillus_acidopullulyticus", "chemicals/starch", 2);
        enzyme("cellulase", "trichoderma", "chemicals/cellulose", 2);
        enzyme("lipase", "rhizomucor", "chemicals/glucose", 2);
        r(FERMENTING).fin("whey", 2 * B).cat("cultures/kluyveromyces", CULTURE_RUNS, PURE).out("lactase", 4).sterile().mode("aerated")
            .add("lactase");
        enzyme("chymosin", "kluyveromyces", "chemicals/glucose", 2);
        enzyme("transglutaminase", "streptoverticillium", "chemicals/glucose", 2);
        enzyme("tannase", "aspergillus_oryzae", "chemicals/glucose", 2);
        enzyme("thermolysin", "bacillus_subtilis", "chemicals/gluten", 2);
        enzyme("aspartase", "e_coli", "chemicals/glucose", 2);
        enzyme("nitrile_hydratase", "corynebacterium", "chemicals/urea", 1);
        enzyme("nuclease", "aspergillus_oryzae", "chemicals/starch", 2);
        enzyme("deaminase", "aspergillus_oryzae", "chemicals/gluten", 2);
        enzyme("fumarase", "yeast", "chemicals/fumaric_acid", 1);
        enzyme("epoxysuccinate_hydrolase", "bacillus_megaterium", "chemicals/glucose", 2);
        // tannase also in the koji room (T2 route of 93)
        r(SOLID_FERMENTING).in("materials/steamed_wheat", 4).fin("water", B).cat("cultures/aspergillus_oryzae", CULTURE_RUNS)
            .out("tannase", 2).mode("koji_tray").add("tannase_koji");
        // 96: barley malt steeped (T1); 101: papaya and pineapple pressed (T1); 105: calf abomasum in brine (T1); 106: yeast autolysed (T2)
        // 96: the malt extract (402, α- and β-amylase) fractionated by salting out with ammonium sulfate (2026-11-12, 11 问题 41)
        r(REACTING).in("enzymes/malt_carbohydrase", 2).in("chemicals/ammonium_sulfate", 1).fin("water", B).out("beta_amylase", 1)
            .tier(2).add("beta_amylase");
        r(PRESSING).in("materials/papaya", 4).out("papain", 1).add("papain");
        r(PRESSING).in("materials/pineapple", 4).out("bromelain", 1).add("bromelain");
        r(EXTRACTING).in("materials/abomasum", 1).fin("brine", B).out("chymosin", 2).add("chymosin_from_abomasum");
        r(EXTRACTING).in("materials/spent_yeast", 4).fin("water", B).out("invertase", 2).temp("heated").tier(2).add("invertase");
    }

    /** An enzyme made by a culture on a medium in the aseptic fermenter (T4). */
    private void enzyme(String enzyme, String culture, String medium, int count) {
        r(FERMENTING).in(medium, count).fin("water", 2 * B).cat("cultures/" + culture, CULTURE_RUNS, PURE).out(enzyme, 4).sterile()
            .mode("aerated").add(enzyme + "_from_" + culture);
    }

    // ---- 107–121 nutrients ----

    private void nutrients() {
        // 107: vitamin A from fish liver oil by molecular distillation, acetylated
        r(DISTILLING).fin("fish_liver_oil", B).fout("retinol", 20).src(30).method("molecular_distillation").add("retinol");
        r(DISTILLING).fin("retinol", B).fout("retinol", 500).method("molecular_distillation").add("retinol_concentrating");
        r(REACTING).fin("retinol", B).fin("acetic_anhydride", B).out("retinyl_acetate", 1).fout("acetic_acid", B).src(50).add("retinyl_acetate");
        // 108: lanolin from wool, cholesterol, 7-dehydrocholesterol, ultraviolet light (P74)
        r(EXTRACTING).in("materials/wool", 4).in("chemicals/soda_ash", 1).fin("water", 2 * B).out("lanolin", 1).temp("heated")
            .add("lanolin");
        r(REACTING).in("chemicals/lanolin", 2).in("chemicals/sodium_hydroxide", 1).fin("ethanol", B).out("cholesterol", 1).src(60)
            .fout("ethanol", 800).src().temp("heated").add("cholesterol");
        r(REACTING).in("chemicals/cholesterol", 1).fin("acetic_anhydride", B).fin("chlorine", 500).out("dehydrocholesterol", 1)
            .fout("acetic_acid", B).src(50).add("dehydrocholesterol");
        r(REACTING).in("chemicals/dehydrocholesterol", 1).fin("ethanol", B).out("cholecalciferol", 1).fout("ethanol", 900).src()
            .method("ultraviolet").add("cholecalciferol");
        recrystallizing("cholecalciferol");
        // 109: the pyrimidine and thiazole halves built and joined (collapsed, T3 reactor)
        r(REACTING).in("chemicals/urea", 1).in("chemicals/sulfur", 1).fin("acetaldehyde", B).fin("ammonia", B).fin("hydrochloric_acid", B)
            .out("thiamine_hydrochloride", 1).src(80).temp("heated").tier(3).add("thiamine_hydrochloride");
        recrystallizing("thiamine_hydrochloride");
        // 110: 3-picoline from acetaldehyde and ammonia, ammoxidized, nitrile hydratase or alkaline hydrolysis
        r(REACTING).fin("acetaldehyde", 3 * B).fin("ammonia", B).fout("methylpyridine", B).temp("high").pressure("pressurized")
            .add("methylpyridine");
        r(GAS_REACTING).fin("methylpyridine", B).fin("ammonia", B).out("cyanopyridine", 1).mode("gas_catalysis").air()
            .cat("catalysts/vanadium", 64).add("cyanopyridine");
        r(FERMENTING).in("chemicals/cyanopyridine", 1).fin("water", B).cat("enzymes/nitrile_hydratase", ENZYME_RUNS)
            .out("nicotinamide", 1).src(99).sterile().airtight().add("nicotinamide");
        r(REACTING).in("chemicals/cyanopyridine", 1).in("chemicals/sodium_hydroxide", 1).fin("water", 2 * B).out("nicotinic_acid", 1)
            .fout("ammonia", B).temp("heated").add("nicotinic_acid");
        // 111: three parts condensed (pteridine, p-aminobenzoic acid, glutamic acid; collapsed, T3 reactor)
        r(REACTING).in("chemicals/glutamic_acid", 1).in("chemicals/urea", 1).fin("aniline", B).fin("carbon_dioxide", B)
            .out("folic_acid", 1).src(80).temp("heated").tier(3).add("folic_acid");
        recrystallizing("folic_acid");
        // 112: precipitated calcium carbonate (T2); 113: calcium gluconate; 114, 115: recrystallized
        r(ABSORBING).in("chemicals/slaked_lime", 1).fin("carbon_dioxide", B).fin("water", B).out("calcium_carbonate", 1).mode("carbonation")
            .add("precipitated_calcium_carbonate");
        r(REACTING).in("chemicals/calcium_carbonate", 1).fin("gluconic_acid", 2 * B).out("calcium_gluconate", 1).fout("carbon_dioxide", B)
            .add("calcium_gluconate");
        recrystallizing("calcium_gluconate");
        recrystallizing("calcium_lactate");
        recrystallizing("ferrous_sulfate");
        // 116: ferric chloride and sodium pyrophosphate: 3 Na4P2O7 + 4 FeCl3 → Fe4(P2O7)3 + 12 NaCl
        r(REACTING).in("materials/iron", 2).fin("chlorine", 3 * B).out("ferric_chloride", 2).temp("heated").method("corrosion_resistant")
            .add("ferric_chloride");
        r(REACTING).in("chemicals/tetrasodium_pyrophosphate", 3).in("chemicals/ferric_chloride", 4).fin("water", B)
            .out("ferric_pyrophosphate", 1).out("salt", 12).add("ferric_pyrophosphate");
        // 117: ethylenediamine, EDTA by the chloroacetic route (B), with ferric hydroxide and soda
        r(REACTING).fin("ethylene", B).fin("chlorine", B).fout("ethylene_dichloride", B).add("ethylene_dichloride");
        r(REACTING).fin("ethylene_dichloride", B).fin("ammonia", 4 * B).fout("ethylenediamine", B).out("ammonium_chloride", 2)
            .pressure("pressurized").add("ethylenediamine");
        r(REACTING).in("chemicals/sodium_chloroacetate", 4).in("chemicals/sodium_hydroxide", 4).fin("ethylenediamine", B)
            .fout("tetrasodium_edta", B).out("salt", 4).add("tetrasodium_edta");
        r(REACTING).fin("tetrasodium_edta", B).fin("sulfuric_acid", 2 * B).out("edta", 1).out("sodium_sulfate", 2).add("edta");
        r(REACTING).in("chemicals/ferric_chloride", 1).in("chemicals/sodium_hydroxide", 3).fin("water", B).out("ferric_hydroxide", 1)
            .out("salt", 3).add("ferric_hydroxide");
        r(REACTING).in("chemicals/edta", 1).in("chemicals/ferric_hydroxide", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B)
            .out("sodium_iron_edta", 1).add("sodium_iron_edta");
        // 118: zinc in sulfuric acid; zinc gluconate by double decomposition
        r(REACTING).in("materials/zinc", 1).fin("sulfuric_acid", B).out("zinc_sulfate", 1).fout("hydrogen", B).add("zinc_sulfate");
        r(REACTING).in("chemicals/calcium_gluconate", 1).in("chemicals/zinc_sulfate", 1).fin("water", B).out("zinc_gluconate", 1)
            .out("gypsum", 1).add("zinc_gluconate");
        recrystallizing("zinc_sulfate");
        // 119: iodine from kelp ash freed by chlorine; potassium iodide oxidized at the anode
        r(REACTING).in("chemicals/kelp_ash", 8).fin("chlorine", 500).fin("water", B).out("iodine", 1).src().add("iodine_from_kelp_ash");
        r(REACTING).in("chemicals/iodine", 3).in("chemicals/potassium_hydroxide", 6).fin("water", B).out("potassium_iodide", 5)
            .out("potassium_iodate", 1).add("potassium_iodide");
        r(ELECTROLYZING).in("chemicals/potassium_iodide", 1).fin("water", 3 * B).out("potassium_iodate", 1).fout("hydrogen", 3 * B).src()
            .mode("electrooxidation").cat("catalysts/electrode", 64).add("potassium_iodate");
        recrystallizing("potassium_iodate");
        // 120: ethylene oxide and bisulfite → isethionate, ammonolysed, acidified
        r(REACTING).fin("ethylene_oxide", B).fin("sodium_bisulfite", B).fout("sodium_isethionate", B).add("sodium_isethionate");
        r(REACTING).fin("sodium_isethionate", B).fin("ammonia", B).fin("hydrochloric_acid", B).out("taurine", 1).out("salt", 1)
            .pressure("high").temp("high").add("taurine");
        recrystallizing("taurine");
        // 121: Schizochytrium (T4), the oil extracted
        r(FERMENTING).in("chemicals/glucose", 2).fin("seawater", B).fin("water", B).cat("cultures/schizochytrium", CULTURE_RUNS, PURE)
            .fout("dha", 200).src(40).sterile().mode("aerated").add("dha");
        r(DISTILLING).fin("dha", B).fout("dha", 800).method("molecular_distillation").add("dha_concentrating");
    }

    // ---- 122–133 flavourings ----

    private void flavourings() {
        // 122: guaethol (collapsed from phenol) + glyoxylic acid, oxidized and decarboxylated
        r(REACTING).in("chemicals/phenol", 1).fin("ethanol", B).fin("sulfuric_acid", 100).fout("guaethol", B).temp("heated").add("guaethol");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("guaethol", B).fin("glyoxylic_acid", B).fout("ethyl_vanillylmandelic_acid", B)
            .add("ethyl_vanillylmandelic_acid");
        r(REACTING).fin("ethyl_vanillylmandelic_acid", 2 * B).out("ethyl_vanillin", 2).fout("carbon_dioxide", 2 * B).temp("heated").air()
            .add("ethyl_vanillin");
        recrystallizing("ethyl_vanillin");
        // 123: bark distilled dry, maltol sublimed (T2)
        r(KILN).in("materials/tannin_source", 4).out("maltol", 1).src(20).out("minecraft:charcoal", 1).fout("wood_vinegar", 500)
            .mode("dry_distillation").add("maltol");
        r(DISTILLING).in("chemicals/maltol", 2).out("maltol", 1).mode("sublimation").add("maltol_sublimation");
        // 124: furfural from pentosans, ethyl maltol (Grignard, ring expansion collapsed; T3 reactor)
        r(REACTING).in("materials/hemicellulose", 4).fin("sulfuric_acid", B).fout("furfural", B).temp("heated").add("furfural");
        r(REACTING).fin("furfural", B).fin("ethanol", B).fin("chlorine", B).out("ethyl_maltol", 1).src(80).temp("heated").tier(3)
            .add("ethyl_maltol");
        recrystallizing("ethyl_maltol");
        // 125, 126 A, 133 B: steam-distilled oils (T1)
        r(DISTILLING).in("materials/citrus", 8).fin("water", B).fout("citral", 50).src(60).fout("limonene", 100).src(70)
            .mode("steam_distillation").method("oil_separation").add("citral");
        r(DISTILLING).in("materials/citrus_peel", 8).fin("water", B).fout("limonene", 200).src(80).mode("steam_distillation")
            .method("oil_separation").add("limonene");
        r(DISTILLING).in("materials/cinnamon", 8).fin("water", B).fout("cinnamaldehyde", 100).src(70).fout("cassia_oil", 100)
            .mode("steam_distillation")
            .method("oil_separation").add("cinnamon_oil");  // I31: cassia oil N039 is the same distillate
        for (String oil : new String[]{"citral", "limonene", "cinnamaldehyde"}) {
            r(DISTILLING).fin(oil, B).fout(oil, 900).method("multi_fraction").add(oil + "_rectifying");
        }
        // 126 B and 127: benzaldehyde and acetaldehyde condensed; cinnamaldehyde split; benzal chloride hydrolysed
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("benzaldehyde", B).fin("acetaldehyde", B).fout("cinnamaldehyde", B)
            .add("cinnamaldehyde");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("cinnamaldehyde", B).fin("water", B).fout("benzaldehyde", B)
            .fout("acetaldehyde", B).temp("heated").add("benzaldehyde_natural");
        // side-chain chlorination goes on from benzyl chloride (the 19 §2 intermediate, 11 问题 41)
        r(REACTING).fin("benzyl_chloride", B).fin("chlorine", B).fout("benzal_chloride", B).fout("hydrogen_chloride", B)
            .method("ultraviolet").add("benzal_chloride");
        r(REACTING).fin("benzal_chloride", B).fin("water", B).fout("benzaldehyde", B).fout("hydrogen_chloride", 2 * B).temp("heated")
            .add("benzaldehyde");
        // 128, 129: esters of fusel isoamyl alcohol and of fermented butyric acid
        r(REACTING).fin("isoamyl_alcohol", B).fin("acetic_acid", B).fin("sulfuric_acid", 100).fout("isoamyl_acetate", B).temp("heated")
            .add("isoamyl_acetate");
        r(DISTILLING).fin("isoamyl_acetate", B).fout("isoamyl_acetate", 900).method("multi_fraction").add("isoamyl_acetate_rectifying");
        r(FERMENTING).in("chemicals/glucose", 1).fin("water", B).cat("cultures/clostridium_butyricum", CULTURE_RUNS)
            .fout("butyric_acid", B).src(10).fout("carbon_dioxide", 2 * B).src().mode("anaerobic").add("butyric_acid");
        r(DISTILLING).fin("butyric_acid", 2 * B).fout("butyric_acid", B).add("butyric_acid_distilling");
        r(REACTING).fin("butyric_acid", B).fin("ethanol", B).fin("sulfuric_acid", 100).fout("ethyl_butyrate", B).temp("heated")
            .add("ethyl_butyrate");
        // 130: castor oil split, ricinoleic acid β-oxidized by Yarrowia (P76), lactonized
        r(REACTING).fin("castor_oil", B).fin("water", 3 * B).fout("ricinoleic_acid", 3 * B).src().fout("glycerol", B).src()
            .pressure("high").temp("high").add("castor_oil_splitting");
        r(FERMENTING).in("chemicals/glucose", 1).fin("ricinoleic_acid", B).fin("water", B).cat("cultures/yarrowia", CULTURE_RUNS, PURE)
            .fout("gamma_decalactone", 200).src(60).sterile().mode("aerated").add("gamma_decalactone");
        r(DISTILLING).fin("gamma_decalactone", B).fout("gamma_decalactone", 900).method("multi_fraction").add("gamma_decalactone_rectifying");
        // 131, 132 A: Maillard reaction flavour (P75), the products separated
        r(REACTING).in("chemicals/glucose", 2).in("chemicals/glutamic_acid", 1).fin("water", B).out("furaneol", 1).src(20)
            .fout("trimethylpyrazine", 50).src(20).temp("heated").add("maillard_flavour");
        recrystallizing("furaneol");
        // 132 B: diacetyl and 1,2-propylenediamine condensed and dehydrogenated
        // isopropanolamine (propylene oxide and ammonia) aminated over nickel (11 问题 41: the second step from the first)
        r(REACTING).fin("isopropanolamine", B).fin("ammonia", B).fout("propylenediamine", B).fout("minecraft:water", B).pressure("pressurized")
            .temp("heated").cat("catalysts/nickel", 64).add("propylenediamine");
        r(REACTING).fin("diacetyl", B).fin("propylenediamine", B).fout("trimethylpyrazine", B).fout("hydrogen", B).temp("heated")
            .add("trimethylpyrazine");
        r(DISTILLING).fin("trimethylpyrazine", B).fout("trimethylpyrazine", 900).method("multi_fraction").add("trimethylpyrazine_rectifying");
        // 133: cut from the caraway and dill oils (the seeds steam-distilled by N216, N205 of I31); the limonene route gives
        // l-carvone and moved to S0224 B (2026-11-11, 11 问题 32)
        r(DISTILLING).fin("caraway_seed_oil", B).fout("d_carvone", 500).src().fout("limonene", 300).src(70).add("d_carvone");
        r(DISTILLING).fin("dill_herb_oil", B).fout("d_carvone", 300).src().fout("alpha_phellandrene", 300).src()
            .add("d_carvone_from_dill");
        r(DISTILLING).fin("d_carvone", B).fout("d_carvone", 900).method("multi_fraction").add("d_carvone_rectifying");
    }

    // ---- 134–139 gum base ----

    private void gumBase() {
        // 134: vinyl acetate over palladium, polymerized
        r(GAS_REACTING).fin("ethylene", 2 * B).fin("acetic_acid", 2 * B).fout("vinyl_acetate", 2 * B).mode("gas_catalysis").air()
            .cat("catalysts/palladium", 64).add("vinyl_acetate");
        r(REACTING).fin("vinyl_acetate", B).out("polyvinyl_acetate", 1).mode("polymerization").temp("heated").add("polyvinyl_acetate");
        // 135: isobutylene, cationic polymerization at freezing temperature with aluminium chloride
        r(REACTING).fin("isobutylene", B).cat("catalysts/aluminium_chloride", 64).out("polyisobutylene", 1).mode("polymerization")
            .temp("freezing").add("polyisobutylene");
        r(EXTRACTING).in("chemicals/polyisobutylene", 2).fin("n_hexane", B).out("polyisobutylene", 1).fout("n_hexane", 900).src()
            .mode("solvent_extraction").add("polyisobutylene_washing");
        // 137: pine resin steam-distilled to rosin, esterified with glycerol at 250 °C
        // turpentine (N298) comes over with the steam, rosin stays behind (联产, 18 §1.5)
        r(DISTILLING).fin("resin", B).out("rosin", 1).fout("turpentine", 100).mode("steam_distillation").method("oil_separation")
            .add("rosin");
        r(REACTING).in("chemicals/rosin", 2).fin("glycerol", B).out("rosin_glycerol_ester", 2).temp("high").add("rosin_glycerol_ester");
        // 138: beeswax melted from comb (T1); microcrystalline wax from crude oil vacuum residue (T3)
        r(BOILING).in("materials/honeycomb", 4).fin("water", B).out("beeswax", 1).temp("heated").add("beeswax");
        decolouring("beeswax");
        decolouring("microcrystalline_wax");
        // the vacuum distillate goes on to solvent dewaxing (I30, 272): paraffin wax and dewaxed oil
        r(DISTILLING).fin("crude_oil", B).out("microcrystalline_wax", 1).src().fout("vacuum_gas_oil", 500).mode("vacuum_distillation")
            .add("microcrystalline_wax");
        // 139: the gum base compounded on the internal mixer from food-grade parts (P78)
        r(MIXING).in("chemicals/polyvinyl_acetate", 1, "food").in("chemicals/polyisobutylene", 1, "food")
            .in("chemicals/rosin_glycerol_ester", 1, "food").in("chemicals/beeswax", 1, "food")
            .in("chemicals/glyceryl_monostearate", 1, "food").in("chemicals/calcium_carbonate", 1, "food").out("gum_base", 6)
            .mode("blending").method("internal_mixing").add("gum_base");
    }
}
