package com.tonywww.foodindustrial.content.batch4;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.BOILING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.MIXING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The fourth content batch, group C (I30, additives/15 §C): emulsifiers and glazing agents, 244–280, with
 * the fats and fatty acids they share (coconut, palm, tallow, hardened oil). Molecular distillation of
 * the solid monoesters runs on their melts: melted, distilled and the distillate crystallized cold
 * (2026-11-11, user).
 */
public final class Batch4Emulsifiers extends Batch {
    private Batch4Emulsifiers(RecipeGen.Output out) {
        super(out, "batch4");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch4Emulsifiers(out).generate());
    }

    private void generate() {
        fatsAndAcids();
        sorbitanEsters();
        glycerolEsters();
        otherEmulsifiers();
        glazingAgents();
    }

    // ---- fats and fatty acids ----

    private void fatsAndAcids() {
        // coconut and palm oil pressed, split under pressure (P42); the acids cut apart
        r(PRESSING).in("materials/copra", 4).fout("coconut_oil", 500).add("coconut_oil");
        r(PRESSING).in("materials/oil_palm_fruit", 4).fout("palm_oil", 500).add("palm_oil");
        r(REACTING).fin("coconut_oil", B).fin("water", 3 * B).fout("coconut_fatty_acids", 3 * B).fout("glycerol", B).src()
            .pressure("high").temp("high").add("coconut_oil_splitting");
        r(REACTING).fin("palm_oil", B).fin("water", 3 * B).fout("palm_fatty_acids", 3 * B).fout("glycerol", B).src()
            .pressure("high").temp("high").add("palm_oil_splitting");
        // coconut acids: C6, C8 and C10 by vacuum distillation; the C12 (lauric) crystallizes out cold
        r(DISTILLING).fin("coconut_fatty_acids", B).fout("hexanoic_acid", 50).src().fout("caprylic_acid", 100).src()
            .out("capric_acid", 1).src().mode("vacuum_distillation").add("coconut_fatty_acids_distillation");
        r(CRYSTALLIZING).fin("coconut_fatty_acids", 2 * B).out("lauric_acid", 1).src().fout("coconut_fatty_acids", B)
            .temp("cooling").add("lauric_acid");
        // palm acids: the C16 cut, the C18 cut as oleic acid
        r(DISTILLING).fin("palm_fatty_acids", B).out("palmitic_acid", 1).src().fout("oleic_acid", 400).src()
            .mode("vacuum_distillation").add("palm_fatty_acids_distillation");
        // 276 B, 248: tallow acids split by solvent crystallization into stearic, palmitic and oleic acid (the column takes
        // only fluids, game simplification for the C16 / C18 cuts)
        r(CRYSTALLIZING).in("chemicals/tallow_fatty_acids", 2).fin("acetone", B).out("stearic_acid", 1).src().out("palmitic_acid", 1)
            .src().fout("oleic_acid", 500).src().fout("acetone", 900).src().temp("cooling").add("tallow_fatty_acids_fractionation");
        // 246: vegetable oil split for oleic acid
        r(REACTING).fin("edible_oils", B).fin("water", 3 * B).fout("oleic_acid", 3 * B).src().fout("glycerol", B).src()
            .pressure("high").temp("high").add("vegetable_oil_splitting");
        // hardened oil (265, 276 B, 370): vegetable oil hydrogenated on nickel (P46)
        r(REACTING).fin("edible_oils", B).fin("hydrogen", 3 * B).out("hardened_oil", 1).mode("hydrogenation").pressure("high")
            .temp("heated").cat("catalysts/nickel", 64).add("hardened_oil");
        r(REACTING).in("chemicals/hardened_oil", 1).fin("water", 3 * B).out("stearic_acid", 3).src().fout("glycerol", B).src()
            .pressure("high").temp("high").add("hardened_oil_splitting");
        // 324: lauryl alcohol by hydrogenating lauric acid (nickel, game simplification)
        r(REACTING).in("chemicals/lauric_acid", 1).fin("hydrogen", 2 * B).fout("lauryl_alcohol", B).mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("lauryl_alcohol");
    }

    // ---- 245–258 sorbitan esters and polysorbates ----

    private void sorbitanEsters() {
        // as 71: sorbitol and the fatty acid, caustic soda (with phosphoric acid), dehydrated and esterified under nitrogen
        r(REACTING).in("chemicals/sorbitol", 1).in("chemicals/stearic_acid", 3).in("chemicals/sodium_hydroxide", 1)
            .fin("phosphoric_acid", 100).out("sorbitan_tristearate", 1).inert().temp("heated").add("sorbitan_tristearate");
        decolouring("sorbitan_tristearate");
        r(REACTING).in("chemicals/xylitol", 1).in("chemicals/stearic_acid", 1).in("chemicals/sodium_hydroxide", 1)
            .out("xylitan_monostearate", 1).inert().temp("heated").add("xylitan_monostearate");
        decolouring("xylitan_monostearate");
        r(REACTING).in("chemicals/sorbitol", 1).in("chemicals/palmitic_acid", 1).in("chemicals/sodium_hydroxide", 1)
            .out("sorbitan_monopalmitate", 1).inert().temp("heated").add("sorbitan_monopalmitate");
        r(REACTING).in("chemicals/sorbitol", 1).in("chemicals/lauric_acid", 1).in("chemicals/sodium_hydroxide", 1)
            .fout("sorbitan_monolaurate", B).inert().temp("heated").add("sorbitan_monolaurate");
        // as 72 (P60): twenty parts of ethylene oxide; for one portion of a solid Span half the coefficient is written,
        // the tank holds no more (additives/04 §实现约定)
        // 251, 258: halved as the equation (20 ethylene oxide to 1 span does not fit the tank), the solid span taken whole
        // (one half more than the equation, game simplification, 11 问题 43)
        r(REACTING).in("chemicals/sorbitan_monostearate", 1).fin("ethylene_oxide", 10 * B).fout("polysorbate_60", 500).mode("ethoxylation")
            .pressure("high").temp("heated").add("polysorbate_60");
        r(REACTING).in("chemicals/sorbitan_monopalmitate", 1).fin("ethylene_oxide", 10 * B).fout("polysorbate_40", 500)
            .mode("ethoxylation").pressure("high").temp("heated").add("polysorbate_40");
        r(REACTING).fin("sorbitan_monolaurate", 500).fin("ethylene_oxide", 10 * B).fout("polysorbate_20", 500).mode("ethoxylation")
            .pressure("high").temp("heated").add("polysorbate_20");
    }

    // ---- 249–266 glycerol and glycol esters ----

    private void glycerolEsters() {
        // 27 C, 254 A, 342 at T4: melted, molecular distillation of the melt, the distillate crystallized cold (2026-11-11, user)
        molecularDistillation("glyceryl_monostearate");
        molecularDistillation("propylene_glycol_monostearate");
        molecularDistillation("glyceryl_monocaprylate");
        // 249: diacetyltartaric anhydride, then on the melted monoglyceride in acetic acid; the acetic acid stripped
        r(REACTING).in("chemicals/tartaric_acid", 1).fin("acetic_anhydride", 3 * B).fin("sulfuric_acid", 50)
            .out("diacetyltartaric_anhydride", 1).fout("acetic_acid", 4 * B).src(50).add("diacetyltartaric_anhydride");
        r(REACTING).in("chemicals/glyceryl_monostearate", 1).in("chemicals/diacetyltartaric_anhydride", 1).fin("acetic_acid", B)
            .out("diacetyltartaric_monoglycerides", 1).fout("acetic_acid", 900).src().temp("heated").add("diacetyltartaric_monoglycerides");
        // 252: glycerol with caprylic and capric acid under nitrogen, alkali-refined, deodorized (P63)
        r(REACTING).in("chemicals/capric_acid", 1).fin("glycerol", B).fin("caprylic_acid", 2 * B).fout("caprylic_capric_triglyceride", B)
            .inert().temp("heated").add("caprylic_capric_triglyceride");
        r(DISTILLING).fin("caprylic_capric_triglyceride", B).fout("caprylic_capric_triglyceride", 950).mode("stripping")
            .add("caprylic_capric_triglyceride_deodorizing");
        // 254: A direct esterification; B interesterification with fat (as 27 A)
        r(REACTING).in("chemicals/stearic_acid", 1).fin("propylene_glycol", B).fin("sulfuric_acid", 50)
            .out("propylene_glycol_monostearate", 1).src(50).inert().temp("heated").add("propylene_glycol_monostearate");
        r(REACTING).in("chemicals/animal_fat", 1).in("chemicals/sodium_hydroxide", 1).fin("propylene_glycol", 3 * B)
            .out("propylene_glycol_monostearate", 3).src(45).fout("glycerol", B).src().inert().temp("heated")
            .add("propylene_glycol_monostearate_interesterification");
        // 255, 261: polyglycerol (P58); polyricinoleic acid by self-condensation; their esters
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("glycerol", 3 * B).fout("polyglycerol", B).inert().temp("heated")
            .add("polyglycerol");
        r(REACTING).in("chemicals/stearic_acid", 1).fin("polyglycerol", B).out("polyglycerol_esters", 1).inert().temp("heated")
            .add("polyglycerol_esters");
        decolouring("polyglycerol_esters");
        r(REACTING).in("chemicals/sodium_hydroxide", 6).fin("castor_oil", 2 * B).fin("sulfuric_acid", 3 * B).fout("ricinoleic_acid", 6 * B)
            .src().fout("glycerol", 2 * B).src().out("sodium_sulfate", 3).temp("heated").add("ricinoleic_acid_by_saponification");
        r(REACTING).fin("ricinoleic_acid", 4 * B).fout("polyricinoleic_acid", B).inert().temp("heated").add("polyricinoleic_acid");
        r(REACTING).fin("polyglycerol", B).fin("polyricinoleic_acid", B).fout("polyglycerol_polyricinoleate", B).src().inert()
            .temp("heated").add("polyglycerol_polyricinoleate");
        // 259 A: triacetin, interesterified with fat; B: monoglyceride with acetic anhydride
        r(REACTING).fin("glycerol", B).fin("acetic_acid", 3 * B).fin("sulfuric_acid", 50).fout("triacetin", B).temp("heated")
            .add("triacetin");
        r(REACTING).in("chemicals/animal_fat", 1).in("chemicals/sodium_hydroxide", 1).fin("triacetin", 2 * B)
            .fout("acetylated_monoglycerides", 3 * B).inert().temp("heated").add("acetylated_monoglycerides");
        r(REACTING).in("chemicals/glyceryl_monostearate", 1).fin("acetic_anhydride", 2 * B).fout("acetylated_monoglycerides", B)
            .fout("acetic_acid", 2 * B).src(30).add("acetylated_monoglycerides_by_anhydride");
        // 263, 264: the monoglyceride esterified with lactic or citric acid
        r(REACTING).in("chemicals/glyceryl_monostearate", 1).fin("lactic_acid", B).out("lactylated_monoglycerides", 1).inert()
            .temp("heated").add("lactylated_monoglycerides");
        r(REACTING).in("chemicals/glyceryl_monostearate", 1).in("chemicals/citric_acid", 1).out("citrated_monoglycerides", 1)
            .temp("heated").tier(2).add("citrated_monoglycerides");
        // 265: monoglycerides of hardened oil, phosphated with phosphorus pentoxide, neutralized with ammonia
        r(REACTING).in("chemicals/hardened_oil", 1).in("chemicals/sodium_hydroxide", 1).fin("glycerol", 2 * B)
            .out("glyceryl_monostearate", 3).src(45).inert().temp("heated").add("glycerolysis_of_hardened_oil");
        r(REACTING).in("chemicals/glyceryl_monostearate", 4).in("chemicals/phosphorus_pentoxide", 1).fin("ammonia", 4 * B)
            .out("ammonium_phosphatide", 4).temp("heated").add("ammonium_phosphatide");
        // 266: maleic anhydride hydrogenated to succinic anhydride (P46), the monoglyceride succinylated
        r(REACTING).in("chemicals/maleic_anhydride", 1).fin("hydrogen", B).out("succinic_anhydride", 1).mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("succinic_anhydride");
        r(REACTING).in("chemicals/glyceryl_monostearate", 1).in("chemicals/succinic_anhydride", 1).out("succinylated_monoglycerides", 1)
            .temp("heated").tier(2).add("succinylated_monoglycerides");
    }

    /** Melted (no refining), molecularly distilled, the distillate crystallized cold (no refining). */
    private void molecularDistillation(String ester) {
        String melt = "molten_" + ester;
        r(BOILING).in("chemicals/" + ester, 1).fout(melt, B).keep().temp("heated").add(melt);
        r(DISTILLING).fin(melt, B).fout(melt, 600).method("molecular_distillation").add(ester + "_molecular_distillation");
        r(CRYSTALLIZING).fin(melt, B).out(ester, 1).keep().temp("cooling").add(melt + "_crystallizing");
    }

    // ---- 244, 250, 253, 260, 262, 267–270 ----

    private void otherEmulsifiers() {
        // 244 A: milk curdled with vinegar, casein filtered (the whey kept), dissolved with soda ash; B: with caustic soda
        r(REACTING).in("materials/milk", 4).fin("acetic_acid", 100).out("casein", 1).src().out("minecraft:bucket", 4).fout("whey", 3 * B)
            .temp("heated").add("casein");
        r(REACTING).in("chemicals/casein", 2).in("chemicals/soda_ash", 1).fin("water", B).out("sodium_caseinate", 2)
            .fout("carbon_dioxide", B).add("sodium_caseinate");
        r(REACTING).in("chemicals/casein", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B).out("sodium_caseinate", 1).tier(2)
            .add("sodium_caseinate_by_caustic_soda");
        sprayDrying("sodium_caseinate");
        // 250: stearic acid with two lactic acid, part neutralized, under nitrogen
        r(REACTING).in("chemicals/stearic_acid", 1).in("chemicals/sodium_hydroxide", 1).fin("lactic_acid", 2 * B)
            .out("sodium_stearoyl_lactylate", 1).inert().temp("heated").add("sodium_stearoyl_lactylate");
        // 253 A: acetylated; B: hydroxylated with hydrogen peroxide and lactic acid; peroxide from persulfate hydrolysis
        r(REACTING).in("chemicals/lecithin", 1).fin("acetic_anhydride", B).out("modified_soy_lecithin", 1).fout("acetic_acid", B).src(30)
            .temp("heated").add("modified_soy_lecithin");
        r(REACTING).in("chemicals/lecithin", 1).in("chemicals/sodium_hydroxide", 1).fin("hydrogen_peroxide", 2 * B).fin("lactic_acid", 100)
            .out("modified_soy_lecithin", 1).temp("heated").add("modified_soy_lecithin_hydroxylated");
        r(REACTING).in("chemicals/ammonium_persulfate", 1).fin("water", 2 * B).fout("hydrogen_peroxide", B).out("ammonium_bisulfate", 2)
            .pressure("vacuum").temp("heated").add("hydrogen_peroxide_from_persulfate");
        // 260 A: fat boiled with potash lye (soft soap, crude); B: stearic acid neutralized
        r(REACTING).in("chemicals/animal_fat", 1).in("chemicals/potassium_hydroxide", 3).fin("water", B).out("potassium_stearate", 3).src()
            .fout("glycerol", B).src().temp("heated").add("potassium_stearate_by_saponification");
        r(REACTING).in("chemicals/stearic_acid", 1).in("chemicals/potassium_hydroxide", 1).fin("water", B).out("potassium_stearate", 1)
            .tier(2).add("potassium_stearate");
        // 262: paraffin cracked (P57), 1-octene cut; ene reaction with maleic anhydride; the starch esterified at pH 8
        // I31: 1-butene and 1-decene (19 §2 `butene`, `decene`) are cut from the same cracked paraffin (联产)
        r(GAS_REACTING).in("chemicals/paraffin_wax", 1).fout("octene", 250).src().fout("butene", 150).src().fout("decene", 150).src()
            .mode("cracking").add("octene");
        r(DISTILLING).fin("octene", B).fout("octene", 900).add("octene_rectifying");
        r(REACTING).in("chemicals/maleic_anhydride", 1).fin("octene", B).fout("octenyl_succinic_anhydride", B).inert().temp("heated")
            .add("octenyl_succinic_anhydride");
        r(REACTING).in("chemicals/starch", 4).in("chemicals/sodium_hydroxide", 1).fin("octenyl_succinic_anhydride", 100)
            .fin("hydrochloric_acid", 100).out("starch_sodium_octenylsuccinate", 4).tier(2).add("starch_sodium_octenylsuccinate");
        // 267 A: stearic acid melted with quicklime; B: soap and calcium chloride
        r(REACTING).in("chemicals/stearic_acid", 2).in("chemicals/quicklime", 1).out("calcium_stearate", 1).temp("heated")
            .add("calcium_stearate");
        r(REACTING).in("chemicals/sodium_stearate", 2).in("chemicals/calcium_chloride", 1).fin("water", B).out("calcium_stearate", 1)
            .out("salt", 2).tier(2).add("calcium_stearate_by_double_decomposition");
        // 268: lecithin hydrolysed by phospholipase A2 (P65)
        r(REACTING).in("chemicals/lecithin", 1).fin("water", B).cat("enzymes/phospholipase_a2", ENZYME_RUNS).out("hydrolyzed_lecithin", 1)
            .temp("heated").tier(2).add("hydrolyzed_lecithin");
        // 269 A: bark in hot water, concentrated (crude); B: resin, spray dried
        r(EXTRACTING).in("materials/quillaja_bark", 4).fin("water", B).out("quillaja_extract", 1).src().temp("heated")
            .add("quillaja_extract");
        resin("quillaja_extract", "catalysts/macroporous_resin");
        sprayDrying("quillaja_extract");
    }

    // ---- 271–280 glazing agents ----

    private void glazingAgents() {
        // 271 A: seedlac melted in a cloth bag over the stove; B: in ethanol, filtered, the ethanol recovered;
        // bleached: soda ash, hypochlorite, thrown down with acid
        r(BOILING).in("chemicals/seedlac", 2).out("shellac", 1).src().temp("heated").add("shellac");
        r(EXTRACTING).in("chemicals/seedlac", 2).fin("ethanol", B).out("shellac", 1).src(60).fout("ethanol", 900).src()
            .mode("solvent_extraction").add("shellac_by_ethanol");
        r(REACTING).in("chemicals/shellac", 4).in("chemicals/soda_ash", 1).fin("sodium_hypochlorite", B).fin("sulfuric_acid", B)
            .out("shellac", 3).rm(75).tier(2).add("shellac_bleaching");
        // 272: the vacuum distillate dewaxed in solvent (P12 + P07); A oleum and clay, B hydrotreated and stripped
        r(CRYSTALLIZING).fin("vacuum_gas_oil", B).fin("acetone", 500).out("paraffin_wax", 1).fout("dewaxed_oil", 600)
            .fout("acetone", 450).src().temp("freezing").add("solvent_dewaxing");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("dewaxed_oil", B).fin("sulfur_trioxide", 200).fout("liquid_paraffin", 800).src()
            .method("corrosion_resistant").add("liquid_paraffin_acid_refining");
        adsorbing("liquid_paraffin", "catalysts/diatomite", null);
        r(REACTING).fin("dewaxed_oil", B).fin("hydrogen", 2 * B).fout("liquid_paraffin", B).src().mode("hydrogenation").pressure("high")
            .temp("heated").cat("catalysts/nickel", 64).add("liquid_paraffin_hydrotreating");
        r(DISTILLING).fin("liquid_paraffin", B).fout("liquid_paraffin", 950).mode("stripping").add("liquid_paraffin_stripping");
        // 273: diethanolamine (P60), dehydrated to morpholine, rectified; wax melted with oleic acid and morpholine,
        // emulsified in hot water
        r(REACTING).fin("ethylene_oxide", 2 * B).fin("ammonia", B).fout("diethanolamine", B).pressure("pressurized").temp("heated")
            .add("diethanolamine");
        r(REACTING).in("chemicals/sodium_hydroxide", 2).fin("diethanolamine", B).fin("sulfuric_acid", B).fout("morpholine", B)
            .out("sodium_sulfate", 1).temp("heated").method("corrosion_resistant").add("morpholine");
        r(DISTILLING).fin("morpholine", B).fout("morpholine", 900).add("morpholine_rectifying");
        r(REACTING).in("chemicals/carnauba_wax", 1, "food").fin("oleic_acid", 200, "food").fin("morpholine", 100, "food").fin("water", B)
            .fout("morpholine_fruit_wax", B).temp("heated").add("morpholine_fruit_wax");
        r(REACTING).in("chemicals/shellac", 1, "food").fin("oleic_acid", 200, "food").fin("morpholine", 100, "food").fin("water", B)
            .fout("morpholine_fruit_wax", B).temp("heated").add("morpholine_fruit_wax_from_shellac");
        // 274: pentaerythritol (P58, sodium formate in the mother liquor); esterified with rosin under nitrogen
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("acetaldehyde", B).fin("formaldehyde", 4 * B).out("pentaerythritol", 1)
            .out("sodium_formate", 1).add("pentaerythritol");
        recrystallizing("pentaerythritol");
        r(REACTING).in("chemicals/rosin", 4).in("chemicals/pentaerythritol", 1).out("rosin_pentaerythritol_ester", 1).inert()
            .temp("heated").add("rosin_pentaerythritol_ester");
        // 275 B: melted, filtered, decoloured; 280 is I20's beeswax
        decolouring("carnauba_wax");
        // 277: polyvinyl acetate alcoholysed in ethanol with caustic soda (P42), the ethyl acetate recovered
        r(REACTING).in("chemicals/polyvinyl_acetate", 4).in("chemicals/sodium_hydroxide", 1).fin("ethanol", 4 * B)
            .out("polyvinyl_alcohol", 4).fout("ethyl_acetate", 4 * B).src().add("polyvinyl_alcohol");
        // 278: Aureobasidium on saccharified starch (P55), decoloured, thrown down with ethanol
        r(FERMENTING).fin("liquefied_starch", 2 * B).fin("water", B).cat("cultures/aureobasidium", CULTURE_RUNS, PURE).out("pullulan", 1)
            .src(40).sterile().mode("aerated").add("pullulan");
        decolouring("pullulan");
        r(REACTING).in("chemicals/pullulan", 2).fin("ethanol", B).out("pullulan", 1).fout("ethanol", 800).src().tier(2)
            .add("pullulan_precipitating");
        // 279: ethylene glycol by hydrating ethylene oxide, rectified; ethoxylated to PEG 400 (P60)
        r(REACTING).fin("ethylene_oxide", B).fin("water", B).fout("ethylene_glycol", B).pressure("pressurized").temp("heated")
            .add("ethylene_glycol");
        r(DISTILLING).fin("ethylene_glycol", B).fout("ethylene_glycol", 900).add("ethylene_glycol_rectifying");
        r(REACTING).fin("ethylene_glycol", B).fin("ethylene_oxide", 8 * B).out("polyethylene_glycol", 1).mode("ethoxylation")
            .pressure("high").temp("heated").add("polyethylene_glycol");
        // 163 emulsion (P83, 2026-11-11, user): silicone oil, Span 60 and water under the high-shear head; the mixer takes two
        // fluids, so Tween 80 is left out (pending review)
        r(MIXING).in("chemicals/sorbitan_monostearate", 1, "food").fin("polydimethylsiloxane", B, "food").fin("water", 2 * B)
            .fout("polydimethylsiloxane_emulsion", 3 * B).mode("blending").method("emulsifying")
            .add("polydimethylsiloxane_emulsion");
    }
}
