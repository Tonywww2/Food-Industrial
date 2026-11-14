package com.tonywww.foodindustrial.content.batch1;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.content.ContentRecipe;
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
import static com.tonywww.foodindustrial.recipe.ProcessTypes.MIXING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PURIFYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SEPARATING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SOLID_FERMENTING;

/**
 * The first content batch (I18): the shared intermediates and the T1-and-later routes of additives 1–30
 * (additives/04; the T0 chains are the I12 manual recipes). Quantities follow the balanced equations of
 * 04 with 1 portion = 1 item = 1,000 mB (09 计量建议); amounts of natural raw materials, times, purities
 * written as source values and the collapsed steps are game settings (additives/04 §实现约定, pending
 * review).
 */
public final class Batch1Recipes extends Batch {
    private Batch1Recipes(RecipeGen.Output out) {
        super(out, "batch1");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch1Recipes(out).generate());
    }

    private void generate() {
        calcium();
        starchAndSugar();
        ethanol();
        alkaliAndAcids();
        gases();
        oilsAndSoap();
        catalysts();
        purification();
        aceticAcid();
        lacticAcid();
        citricAcid();
        glutamate();
        sodaAsh();
        bicarbonates();
        potassium();
        alum();
        phosphates();
        magnesium();
        gypsum();
        gluconoLactone();
        colloids();
        colours();
        preservatives();
        sorbitolAndAscorbicAcid();
        monoglycerides();
        flavours();
    }

    // ---- shared intermediates (04 §共用中间体与基础化学品) ----

    private void calcium() {
        r(GRINDING).in("materials/limestone", 1).out("calcium_carbonate", 1).src().add("calcium_carbonate");
        r(KILN).in("chemicals/calcium_carbonate", 1).out("quicklime", 1).fout("carbon_dioxide", B).mode("calcining").add("quicklime");
        r(KILN).in("materials/limestone", 1).out("quicklime", 1).src().fout("carbon_dioxide", B).src().mode("calcining")
            .add("quicklime_from_limestone");
        r(REACTING).in("chemicals/quicklime", 1).fin("water", B).out("slaked_lime", 1).add("slaked_lime");
        r(GRINDING).in("materials/rock_salt", 1).out("salt", 1).src().add("salt_from_rock_salt");
    }

    private void starchAndSugar() {
        r(GRINDING).in("materials/potato", 4).fin("water", B).out("starch", 1).src().mode("wet_grinding").add("starch_from_potatoes");
        // corn gluten meal, the by-product of corn wet milling (I30, 214 corn yellow)
        r(GRINDING).in("materials/corn", 4).fin("water", B).out("starch", 1).src().out("corn_gluten_meal", 1).mode("wet_grinding")
            .add("starch_from_corn");
        // washing wheat dough leaves starch and gluten (4 A)
        r(GRINDING).in("materials/wheat", 4).fin("water", B).out("starch", 1).src().out("gluten", 1).src().mode("wet_grinding")
            .add("starch_from_wheat");
        // malt saccharification (T1), acid hydrolysis (T2), liquefaction and saccharification by enzymes (T2)
        r(REACTING).in("chemicals/starch", 2).in("enzymes/malt", 1).fin("water", B).out("glucose", 2).temp("heated").add("glucose_by_malt");
        r(REACTING).in("chemicals/starch", 2).fin("water", B).fin("hydrochloric_acid", 100).out("glucose", 2).temp("heated").tier(2)
            .add("glucose_by_acid");
        r(REACTING).in("chemicals/starch", 4).fin("water", B).cat("enzymes/alpha_amylase", 16).fout("liquefied_starch", B).temp("heated")
            .tier(2).add("liquefied_starch");
        r(REACTING).fin("liquefied_starch", B).cat("enzymes/glucoamylase", 16).out("glucose", 4).src(95).temp("heated").tier(2)
            .add("glucose_by_enzymes");
    }

    private void ethanol() {
        // yeast fermentation (T1–T2): a weak wash, distilled up
        r(FERMENTING).in("chemicals/glucose", 1).fin("water", B).cat("cultures/yeast", CULTURE_RUNS).fout("ethanol", 2 * B).src(8)
            .fout("carbon_dioxide", 2 * B).src().airtight().add("ethanol_from_glucose");
        r(FERMENTING).in("chemicals/sucrose", 1).fin("water", B).cat("cultures/yeast", CULTURE_RUNS).fout("ethanol", 4 * B).src(8)
            .fout("carbon_dioxide", 4 * B).src().airtight().add("ethanol_from_sucrose");
        r(DISTILLING).fin("fruit_wine", B).fout("ethanol", 250).src(40).add("ethanol_from_fruit_wine");
        r(DISTILLING).fin("wine", B).fout("ethanol", 250).src(40).add("ethanol_from_wine");
        // the pot still in its distillation mode; the column cuts fusel oil from the same wash (11 问题 41)
        r(DISTILLING).fin("ethanol", 2 * B).fout("ethanol", B).mode("distillation").add("ethanol_redistilling");
    }

    private void alkaliAndAcids() {
        // sodium hydroxide: causticizing (T1), chlor-alkali (T3)
        r(REACTING).in("chemicals/soda_ash", 1).in("chemicals/slaked_lime", 1).fin("water", B).out("sodium_hydroxide", 2)
            .out("calcium_carbonate", 1).add("sodium_hydroxide_causticizing");
        r(ELECTROLYZING).fin("brine", 2 * B).out("sodium_hydroxide", 2).fout("hydrogen", B).fout("chlorine", B).mode("chlor_alkali")
            .cat("catalysts/electrode", 64).add("chlor_alkali");
        // hydrochloric acid: absorbing hydrogen chloride (Leblanc byproduct, or burnt from H2 + Cl2)
        r(ABSORBING).fin("hydrogen_chloride", B).fin("water", B).fout("hydrochloric_acid", B).add("hydrochloric_acid");
        r(GAS_REACTING).fin("hydrogen", B).fin("chlorine", B).fout("hydrogen_chloride", 2 * B).mode("burning").add("hydrogen_chloride");
        // sulfuric acid: sulfur burnt (M10 at T1, M27 at T3); Ward (T1), lead chamber (T2), contact (T3)
        r(GAS_REACTING).in("chemicals/sulfur", 1).fout("sulfur_dioxide", B).add("sulfur_dioxide");
        r(GAS_REACTING).in("chemicals/sulfur", 2).in("chemicals/potassium_nitrate", 2).out("potassium_sulfate", 1)
            .fout("sulfur_trioxide", B).add("ward_process");
        r(ABSORBING).fin("sulfur_trioxide", B).fin("water", B).fout("sulfuric_acid", B).add("sulfuric_acid");
        r(ABSORBING).fin("sulfur_dioxide", B).fin("water", B).fout("sulfuric_acid", B).method("lead_chamber").add("lead_chamber_process");
        r(GAS_REACTING).fin("sulfur_dioxide", 2 * B).fout("sulfur_trioxide", 2 * B).mode("gas_catalysis").air()
            .cat("catalysts/vanadium", 64).add("contact_process");
        // nitric acid: saltpetre and sulfuric acid in the still (T1); ammonia oxidation (T3)
        r(DISTILLING).in("chemicals/potassium_nitrate", 1).fin("sulfuric_acid", B).fout("nitric_acid", B).out("potassium_bisulfate", 1)
            .add("nitric_acid_from_saltpetre");
        r(GAS_REACTING).fin("ammonia", B).fout("nitric_oxide", B).mode("gas_catalysis").air().cat("catalysts/platinum_gauze", 64)
            .add("ammonia_oxidation");
        r(GAS_REACTING).fin("nitric_oxide", B).fout("nitrogen_dioxide", B).mode("burning").air().add("nitrogen_dioxide");
        r(ABSORBING).fin("nitrogen_dioxide", 3 * B).fin("water", B).fout("nitric_acid", 2 * B).fout("nitric_oxide", B).add("nitric_acid");
        // sodium nitrate: nitratine (T1), nitric acid and soda (T2)
        r(CRYSTALLIZING).in("materials/nitratine", 1).fin("water", B).out("sodium_nitrate", 1).src().add("sodium_nitrate_from_nitratine");
        r(REACTING).in("chemicals/soda_ash", 1).fin("nitric_acid", 2 * B).out("sodium_nitrate", 2).fout("carbon_dioxide", B)
            .add("sodium_nitrate");
        // sodium hypochlorite (T3, cooled)
        r(REACTING).in("chemicals/sodium_hydroxide", 2).fin("chlorine", B).fin("water", B).fout("sodium_hypochlorite", B).out("salt", 1)
            .temp("cooling").add("sodium_hypochlorite");
        // acetaldehyde: ethanol dehydrogenated over copper (T3)
        r(REACTING).fin("ethanol", B).fout("acetaldehyde", B).fout("hydrogen", B).temp("high").cat("catalysts/copper", 64)
            .add("acetaldehyde");
        // glyoxylic acid: sucrose oxidized by nitric acid to oxalic acid, reduced at the cathode (T3);
        // a sixth of the nitric acid of the equation, the nitrogen dioxide goes back to the absorber
        r(REACTING).in("chemicals/sucrose", 1).fin("nitric_acid", 6 * B).out("oxalic_acid", 6).fout("nitrogen_dioxide", 6 * B)
            .temp("heated").method("corrosion_resistant").add("oxalic_acid");
        r(ELECTROLYZING).in("chemicals/oxalic_acid", 1).fin("water", B).fout("glyoxylic_acid", B).fout("oxygen", 500)
            .mode("electroreduction").cat("catalysts/electrode", 64).add("glyoxylic_acid");
    }

    private void gases() {
        // nitrogen: air through glowing charcoal, the carbon dioxide taken out by lime water (T2, M19 combustion)
        r(ABSORBING).in("materials/coal", 1).fout("nitrogen", 2 * B).src().fout("carbon_dioxide", B).src().mode("combustion")
            .add("nitrogen_by_combustion");
        // hydrogen and oxygen by electrolysis (T3)
        r(ELECTROLYZING).fin("water", 2 * B).fout("hydrogen", 2 * B).src().fout("oxygen", B).src().mode("water_electrolysis")
            .cat("catalysts/electrode", 64).add("water_electrolysis");
        // water gas (T3), methanol and ammonia synthesis (T3)
        r(GAS_REACTING).in("materials/coal", 1).fin("water", B).fout("carbon_monoxide", B).src().fout("hydrogen", B).src()
            .temp("high").add("water_gas");
        r(REACTING).fin("carbon_monoxide", B).fin("hydrogen", 2 * B).fout("methanol", B).pressure("high").temp("high")
            .cat("catalysts/copper", 64).add("methanol_synthesis");
        r(REACTING).fin("nitrogen", B).fin("hydrogen", 3 * B).fout("ammonia", 2 * B).mode("ammonia_synthesis").pressure("high")
            .temp("high").cat("catalysts/iron", 64).add("ammonia_synthesis");
        // methane: anaerobic digestion (M05) or coal coking; hydrogen cyanide by the Andrussow process (optional route)
        r(FERMENTING).in("chemicals/glucose", 1).fin("water", B).fout("methane", 3 * B).src().fout("carbon_dioxide", 3 * B).src()
            .mode("anaerobic").add("biogas");
        r(KILN).in("materials/coal", 4).fout("coal_tar", B).fout("methane", B).src().mode("dry_distillation").add("coal_coking");
        r(GAS_REACTING).fin("methane", 2 * B).fin("ammonia", 2 * B).fout("hydrogen_cyanide", 2 * B).mode("gas_catalysis").air()
            .cat("catalysts/platinum_gauze", 64).add("andrussow_process");
    }

    private void oilsAndSoap() {
        r(BOILING).in("materials/raw_pork", 4).out("animal_fat", 1).add("animal_fat_from_pork");
        r(BOILING).in("materials/raw_beef", 4).out("animal_fat", 1).add("animal_fat_from_beef");
        r(BOILING).in("materials/raw_mutton", 4).out("animal_fat", 1).add("animal_fat_from_mutton");
        // saponification: C57H110O6 + 3 NaOH → 3 C18H35NaO2 + C3H8O3 (27)
        r(REACTING).in("chemicals/animal_fat", 1).in("chemicals/sodium_hydroxide", 3).fin("water", B).out("sodium_stearate", 3).src()
            .fout("glycerol", B).src().temp("heated").add("saponification");
        r(REACTING).in("chemicals/sodium_stearate", 2).fin("sulfuric_acid", B).out("stearic_acid", 2).out("sodium_sulfate", 1)
            .add("stearic_acid");
    }

    private void catalysts() {
        // activated carbon: charcoal or bone char activated in the rotary kiln (T2)
        r(KILN).in("materials/coal", 2).out("activated_carbon", 1).fout("carbon_dioxide", B).mode("activation").add("activated_carbon");
        r(REACTING).in("materials/nickel", 1).in("catalysts/diatomite", 1).in("chemicals/sodium_hydroxide", 1).out("nickel_catalyst", 2)
            .tier(2).add("nickel_catalyst");
        r(KILN).in("materials/iron", 4).in("chemicals/potassium_carbonate", 1).in("chemicals/alumina", 1).out("iron_catalyst", 4)
            .mode("melting").add("iron_catalyst");
        // cobalt acetate (11 问题 43): other mods' cobalt dissolved in glacial acetic acid with air in the stirred reactor, the
        // crystallization merged (no solution registered): 2 Co + 4 C2H4O2 + O2 → 2 Co(C2H3O2)2 + 2 H2O
        r(REACTING).in("materials/cobalt", 1).fin("acetic_acid", 2 * B, "90").out("cobalt_acetate", 1).fout("minecraft:water", B)
            .temp("heated").air().tier(2).add("cobalt_acetate");
        r(REACTING).in("materials/rhodium", 1).in("chemicals/iodine", 2).out("rhodium_iodide_catalyst", 2).temp("heated").tier(2)
            .add("rhodium_iodide_catalyst");
        r(KILN).in("materials/platinum", 2).in("materials/rhodium", 1).out("platinum_gauze", 2).mode("melting").add("platinum_gauze");
        r(KILN).in("materials/vanadium", 2).out("vanadium_pentoxide", 1).mode("roasting").add("vanadium_pentoxide");
        r(KILN).in("materials/gold", 1).in("catalysts/diatomite", 1).out("noble_metal_catalyst", 2).mode("melting")
            .add("noble_metal_catalyst_from_gold");
        r(KILN).in("materials/palladium", 1).in("catalysts/diatomite", 1).out("noble_metal_catalyst", 2).mode("melting")
            .add("noble_metal_catalyst_from_palladium");
        r(REACTING).in("materials/copper", 2).in("catalysts/diatomite", 1).out("copper_catalyst", 2).temp("heated").tier(2)
            .add("copper_catalyst");
        // copper sulfate: 2 Cu + 2 H2SO4 + O2 → 2 CuSO4 + 2 H2O (22)
        r(REACTING).in("materials/copper", 1).fin("sulfuric_acid", B).out("copper_sulfate", 1).temp("heated").air().add("copper_sulfate");
    }

    /** Regeneration of the adsorbents (M25) and the decolouring that later steps share. */
    private void purification() {
        r(PURIFYING).fin("water", B).fout("minecraft:water", 800).mode("regeneration").cat("catalysts/activated_carbon", 4)
            .add("activated_carbon_regeneration");
        r(PURIFYING).fin("hydrochloric_acid", 500).fout("brine", 500).mode("regeneration").cat("catalysts/ion_exchange_resin", 4)
            .add("ion_exchange_resin_regeneration");
    }

    // ---- 1 glacial acetic acid ----

    private void aceticAcid() {
        // B: vinegar → sodium acetate → distilled with sulfuric acid (T1–T2)
        r(REACTING).in("chemicals/soda_ash", 1).fin("acetic_acid", 2 * B).out("sodium_acetate", 2).fout("carbon_dioxide", B)
            .add("sodium_acetate");
        r(DISTILLING).in("chemicals/sodium_acetate", 2).fin("sulfuric_acid", B).fout("acetic_acid", 2 * B).out("sodium_sulfate", 1)
            .add("acetic_acid_from_sodium_acetate");
        // C: wood distilled dry → wood vinegar → methanol and acid; lime → calcium acetate → sulfuric acid
        r(KILN).in("materials/logs", 4).out("minecraft:charcoal", 2).fout("wood_vinegar", B).fout("wood_tar", 250)
            .mode("dry_distillation").add("wood_dry_distillation");
        r(DISTILLING).fin("wood_vinegar", B).fout("methanol", 100).src(60).fout("acetic_acid", 800).src(5).mode("distillation")
            .add("wood_vinegar");
        r(DISTILLING).fin("wood_vinegar", B).fout("methanol", 100).src(60).fout("acetic_acid", 800).src(5).fout("acetone", 50).src(60)
            .method("multi_fraction").add("wood_vinegar_rectifying");
        r(REACTING).in("chemicals/slaked_lime", 1).fin("acetic_acid", 2 * B).out("calcium_acetate", 1).add("calcium_acetate");
        r(DISTILLING).in("chemicals/calcium_acetate", 1).fin("sulfuric_acid", B).fout("acetic_acid", 2 * B).out("gypsum", 1)
            .add("acetic_acid_from_calcium_acetate");
        // calcium acetate gives acetone when heated (the old acetone route): Ca(C2H3O2)2 → CaCO3 + C3H6O
        r(KILN).in("chemicals/calcium_acetate", 1).out("calcium_carbonate", 1).fout("acetone", B).mode("dry_distillation")
            .add("acetone_from_calcium_acetate");
        // D: methanol carbonylation (T3), rectified
        r(REACTING).fin("methanol", B).fin("carbon_monoxide", B).fout("acetic_acid", B).mode("carbonylation").pressure("pressurized")
            .temp("heated").cat("catalysts/carbonylation", 64).add("methanol_carbonylation");
        r(DISTILLING).fin("acetic_acid", B).fout("acetic_acid", 900).method("multi_fraction").add("acetic_acid_rectifying");
    }

    // ---- 2 lactic acid ----

    private void lacticAcid() {
        // B: calcium salt process (T2): fermented with chalk, acid split, decoloured
        r(FERMENTING).in("chemicals/glucose", 1).in("chemicals/calcium_carbonate", 1).fin("water", B)
            .cat("cultures/lactobacillus", CULTURE_RUNS).out("calcium_lactate", 1).src(80).fout("carbon_dioxide", B).src().airtight()
            .add("calcium_lactate");
        r(REACTING).in("chemicals/calcium_lactate", 1).fin("sulfuric_acid", B).fout("lactic_acid", 2 * B).out("gypsum", 1)
            .add("lactic_acid_from_calcium_lactate");
        r(BOILING).fin("lactic_acid", 2 * B).fout("lactic_acid", B).add("lactic_acid_concentrating");
        adsorbing("lactic_acid", "catalysts/activated_carbon", null);
        // C: aseptic fermentation with ammonia, electrodialysis, molecular distillation (T4)
        r(FERMENTING).in("chemicals/glucose", 1).fin("ammonia", B).fin("water", B).cat("cultures/lactobacillus", CULTURE_RUNS, PURE)
            .fout("lactic_acid", 2 * B).src(20).fout("ammonia", 500).src().sterile().airtight().add("lactic_acid_aseptic");
        r(PURIFYING).fin("lactic_acid", B).fout("lactic_acid", B).fout("ammonia", 100).src().mode("bipolar_electrodialysis")
            .method("membrane", "electrodialysis").cat("catalysts/membrane", 16).add("lactic_acid_electrodialysis");
        r(DISTILLING).fin("lactic_acid", B).fout("lactic_acid", 900).method("molecular_distillation").add("lactic_acid_molecular_distillation");
        // D: lactonitrile route (T3, optional)
        r(REACTING).fin("acetaldehyde", B).fin("hydrogen_cyanide", B).fout("lactonitrile", B).add("lactonitrile");
        r(REACTING).fin("lactonitrile", B).fin("sulfuric_acid", B).fin("water", 2 * B).fout("lactic_acid", B).out("ammonium_bisulfate", 1)
            .temp("heated").method("corrosion_resistant").add("lactic_acid_from_lactonitrile");
    }

    // ---- 3 citric acid ----

    private void citricAcid() {
        // A: lemon juice limed (Scheele): 2 C6H8O7 + 3 CaCO3 → Ca3(C6H5O7)2 + 3 CO2 + 3 H2O
        r(REACTING).in("materials/lemon", 8).in("chemicals/calcium_carbonate", 3).out("calcium_citrate", 1).src(80)
            .fout("carbon_dioxide", 3 * B).src().temp("heated").add("calcium_citrate_from_lemons");
        r(REACTING).in("chemicals/calcium_citrate", 1).fin("sulfuric_acid", 3 * B).out("citric_acid", 2).out("gypsum", 3)
            .add("citric_acid_from_calcium_citrate");
        // B: shallow tray fermentation with chalk (T1 koji room), same acid split
        r(SOLID_FERMENTING).in("chemicals/glucose", 2).in("chemicals/calcium_carbonate", 3).fin("water", B)
            .cat("cultures/aspergillus_niger", CULTURE_RUNS).out("calcium_citrate", 1).src(85).out("fungal_mycelium", 1)
            .mode("shallow_tray").add("calcium_citrate_shallow_tray");
        // C: aseptic aerated fermentation (T4)
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/aspergillus_niger", CULTURE_RUNS, PURE)
            .out("citric_acid", 2).src(95).sterile().mode("aerated").add("citric_acid_aseptic");
        recrystallizing("citric_acid");
        decolouring("citric_acid");
    }

    // ---- 4 monosodium glutamate ----

    private void glutamate() {
        // A: gluten hydrolysed by hydrochloric acid (T2), neutralized to the isoelectric point
        r(REACTING).in("chemicals/gluten", 4).fin("hydrochloric_acid", 2 * B).out("glutamic_acid_hydrochloride", 1).temp("heated")
            .method("corrosion_resistant").add("glutamic_acid_hydrochloride");
        r(REACTING).in("chemicals/glutamic_acid_hydrochloride", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B)
            .out("glutamic_acid", 1).out("salt", 1).tier(2).add("glutamic_acid_from_hydrochloride");
        // neutralized with soda: 2 C5H9NO4 + Na2CO3 → 2 C5H8NNaO4 + CO2 + H2O
        r(REACTING).in("chemicals/glutamic_acid", 2).in("chemicals/soda_ash", 1).fin("water", B).out("monosodium_glutamate", 2)
            .fout("carbon_dioxide", B).add("monosodium_glutamate");
        // B: aseptic fermentation with ammonia (T4), broth to isoelectric crystals
        r(FERMENTING).in("chemicals/glucose", 1).fin("ammonia", B).fin("water", B).cat("cultures/corynebacterium", CULTURE_RUNS, PURE)
            .fout("glutamate_broth", B).fout("carbon_dioxide", B).src().sterile().mode("aerated").add("glutamate_broth");
        r(CRYSTALLIZING).fin("glutamate_broth", B).fin("sulfuric_acid", 500).out("glutamic_acid", 1).src(95).mode("isoelectric")
            .add("glutamic_acid_isoelectric");
        recrystallizing("monosodium_glutamate");
        decolouring("monosodium_glutamate");
    }

    // ---- 5 sodium carbonate ----

    private void sodaAsh() {
        // B: trona calcined (T0–T1): 2 Na3H(CO3)2·2H2O → 3 Na2CO3 + CO2 + 5 H2O
        r(GRINDING).in("materials/trona", 1).out("trona", 1).src().add("trona");
        r(KILN).in("chemicals/trona", 2).out("soda_ash", 3).fout("carbon_dioxide", B).mode("calcining").add("soda_ash_from_trona");
        // C: Leblanc (T2): salt cake, black ash fused in the rotary kiln
        r(REACTING).in("chemicals/salt", 2).fin("sulfuric_acid", B).out("sodium_sulfate", 1).fout("hydrogen_chloride", 2 * B)
            .temp("heated").tier(2).add("salt_cake");
        r(KILN).in("chemicals/sodium_sulfate", 1).in("chemicals/calcium_carbonate", 1).in("materials/coal", 2).out("soda_ash", 1)
            .out("calcium_sulfide", 1).fout("carbon_dioxide", 2 * B).mode("melting").add("leblanc_process");
        // D, E: ammonia-soda (Solvay; Hou leaves the ammonium chloride as a product): T3
        r(REACTING).in("chemicals/salt", 1).fin("ammonia", B).fin("carbon_dioxide", B).fin("water", B).out("sodium_bicarbonate", 1)
            .out("ammonium_chloride", 1).temp("cooling").add("solvay_carbonation");
        r(KILN).in("chemicals/sodium_bicarbonate", 2).out("soda_ash", 1).fout("carbon_dioxide", B).mode("calcining")
            .add("soda_ash_from_bicarbonate");
        r(REACTING).in("chemicals/ammonium_chloride", 2).in("chemicals/quicklime", 1).out("calcium_chloride", 1).fout("ammonia", 2 * B)
            .temp("heated").add("ammonia_recovery");
        // dissolved hot like the T0 recrystallization, so a heated machine takes this one (11 问题 41)
        r(CRYSTALLIZING).in("chemicals/soda_ash", 4).fin("water", B).out("soda_ash", 3).temp("heated").add("soda_ash_recrystallizing");
    }

    // ---- 6 sodium bicarbonate, 8 ammonium bicarbonate ----

    private void bicarbonates() {
        // 6 A/B: soda solution carbonated (T1 steeping vat with CO2, T2 absorption tower)
        r(ABSORBING).in("chemicals/soda_ash", 1).fin("carbon_dioxide", B).fin("water", B).out("sodium_bicarbonate", 2)
            .add("sodium_bicarbonate");
        recrystallizing("sodium_bicarbonate");
        // baking powder: soda, acid salt and starch, all food grade (6, 10)
        r(MIXING).in("chemicals/sodium_bicarbonate", 1, "food").in("chemicals/sodium_acid_pyrophosphate", 1, "food")
            .in("chemicals/starch", 1, "food").out("baking_powder", 3).mode("blending").add("baking_powder");
        // 8 A: bones and horn distilled dry → hartshorn salt, bone ash left; carbonated to the bicarbonate
        r(KILN).in("materials/bone", 4).out("ammonium_carbonate", 1).src(60).out("hydroxyapatite", 2).src().mode("dry_distillation")
            .add("hartshorn_salt");
        r(KILN).in("materials/horn", 1).out("ammonium_carbonate", 1).src(60).mode("dry_distillation").add("hartshorn_salt_from_horn");
        r(ABSORBING).in("chemicals/ammonium_carbonate", 1).fin("carbon_dioxide", B).fin("water", B).out("ammonium_bicarbonate", 2)
            .add("ammonium_bicarbonate_from_carbonate");
        // 8 B: ammonia and carbon dioxide (T3)
        r(ABSORBING).fin("ammonia", B).fin("carbon_dioxide", B).out("ammonium_bicarbonate", 1).mode("carbonation")
            .add("ammonium_bicarbonate");
    }

    // ---- 7 potassium carbonate; potassium salts ----

    private void potassium() {
        // C: potassium chloride electrolysed, the hydroxide carbonated (T3)
        r(ELECTROLYZING).in("chemicals/potassium_chloride", 2).fin("water", 2 * B).out("potassium_hydroxide", 2).fout("hydrogen", B)
            .fout("chlorine", B).mode("chlor_alkali").cat("catalysts/electrode", 64).add("potassium_hydroxide");
        r(ABSORBING).in("chemicals/potassium_hydroxide", 2).fin("carbon_dioxide", B).out("potassium_carbonate", 1)
            .add("potassium_carbonate_from_hydroxide");
        // potassium sulfate from potash (9)
        r(REACTING).in("chemicals/potassium_carbonate", 1).fin("sulfuric_acid", B).out("potassium_sulfate", 1).fout("carbon_dioxide", B)
            .add("potassium_sulfate");
    }

    // ---- 9 potassium alum ----

    private void alum() {
        // A: alunite calcined (T1): 2 KAl3(SO4)2(OH)6 → K2SO4 + Al2(SO4)3 + 2 Al2O3 + 6 H2O
        r(GRINDING).in("materials/alunite", 1).out("alunite", 1).src().add("alunite");
        r(KILN).in("chemicals/alunite", 2).out("potassium_sulfate", 1).out("aluminium_sulfate", 1).out("alumina", 2).mode("calcining")
            .add("alunite_calcining");
        r(REACTING).in("chemicals/potassium_sulfate", 1).in("chemicals/aluminium_sulfate", 1).fin("water", B).out("potassium_alum", 2)
            .add("potassium_alum");
        // B: calcined clay in sulfuric acid (T2); C: aluminium hydroxide (T3)
        r(GRINDING).in("materials/clay", 4).out("kaolinite", 1).src().add("kaolinite");
        r(REACTING).in("chemicals/kaolinite", 1).fin("sulfuric_acid", 3 * B).out("aluminium_sulfate", 1).temp("heated").tier(2)
            .add("aluminium_sulfate_from_clay");
        r(REACTING).in("chemicals/aluminium_hydroxide", 2).fin("sulfuric_acid", 3 * B).out("aluminium_sulfate", 1)
            .add("aluminium_sulfate_from_hydroxide");
        recrystallizing("potassium_alum");
    }

    // ---- 10 sodium acid pyrophosphate, the phosphoric acid line ----

    private void phosphates() {
        // A: bone ash and sulfuric acid (T2): Ca5(PO4)3OH + 5 H2SO4 → 3 H3PO4 + 5 CaSO4 + H2O
        r(KILN).in("materials/bone", 4).out("hydroxyapatite", 1).src().mode("calcining").add("bone_ash");
        r(REACTING).in("chemicals/hydroxyapatite", 1).fin("sulfuric_acid", 5 * B).fout("phosphoric_acid", 3 * B).out("gypsum", 5)
            .tier(2).add("phosphoric_acid_from_bone_ash");
        r(BOILING).fin("phosphoric_acid", 2 * B).fout("phosphoric_acid", B).add("phosphoric_acid_concentrating");
        // B: wet process from apatite, purified by solvent extraction (T3)
        r(GRINDING).in("materials/apatite", 1).out("fluorapatite", 1).src().add("fluorapatite");
        r(REACTING).in("chemicals/fluorapatite", 1).fin("sulfuric_acid", 5 * B).fout("phosphoric_acid", 3 * B).out("gypsum", 5)
            .fout("hydrogen_fluoride", B).method("corrosion_resistant").tier(2).add("wet_process_phosphoric_acid");
        r(EXTRACTING).fin("phosphoric_acid", B).fout("phosphoric_acid", 900).mode("liquid_extraction").add("phosphoric_acid_extraction");
        // C: thermal process (T3): electric furnace, phosphorus burnt and hydrated
        r(KILN).in("chemicals/fluorapatite", 2).in("materials/quartz", 6).in("materials/coal", 5).out("white_phosphorus", 1)
            .out("phosphorus_slag", 6).fout("carbon_monoxide", 5 * B).src().temp("extreme").cat("catalysts/electrode", 16)
            .add("white_phosphorus");
        r(GAS_REACTING).in("chemicals/white_phosphorus", 1).out("phosphorus_pentoxide", 1).mode("burning").air().add("phosphorus_pentoxide");
        r(REACTING).in("chemicals/phosphorus_pentoxide", 1).fin("water", 6 * B).fout("phosphoric_acid", 4 * B).tier(2)
            .add("phosphoric_acid_thermal");
        // D: neutralized and condensed (T2): 2 NaH2PO4 → Na2H2P2O7 + H2O
        r(REACTING).in("chemicals/soda_ash", 1).fin("phosphoric_acid", 2 * B).out("monosodium_phosphate", 2).fout("carbon_dioxide", B)
            .add("monosodium_phosphate");
        r(KILN).in("chemicals/monosodium_phosphate", 2).out("sodium_acid_pyrophosphate", 1).mode("condensation")
            .add("sodium_acid_pyrophosphate");
    }

    // ---- 11 magnesium chloride ----

    private void magnesium() {
        // B: bittern boiled down: salt and Epsom salt first; cooled, carnallite and magnesium chloride (T1)
        r(CRYSTALLIZING).fin("bittern", 2 * B).out("salt", 1).src().out("epsom_salt", 1).src().temp("heated").add("bittern_evaporating");
        r(CRYSTALLIZING).fin("bittern", 2 * B).out("carnallite", 1).src().temp("cooling").add("bittern_cooling");
        r(CRYSTALLIZING).in("chemicals/carnallite", 1).fin("water", B).out("potassium_chloride", 1).out("magnesium_chloride", 1)
            .temp("heated").add("carnallite_dissolving");
        // C: magnesia precipitated from sea water by lime, dissolved in hydrochloric acid (T3)
        r(REACTING).in("chemicals/slaked_lime", 1).fin("seawater", 4 * B).out("magnesium_hydroxide", 1).src().add("magnesium_hydroxide");
        r(REACTING).in("chemicals/magnesium_hydroxide", 1).fin("hydrochloric_acid", 2 * B).out("magnesium_chloride", 1)
            .add("magnesium_chloride");
        // calcium chloride: limestone in hydrochloric acid (15, 17), also left by the ammonia recovery (5 D)
        r(REACTING).in("chemicals/calcium_carbonate", 1).fin("hydrochloric_acid", 2 * B).out("calcium_chloride", 1)
            .fout("carbon_dioxide", B).add("calcium_chloride");
        recrystallizing("magnesium_chloride");
    }

    // ---- 12 calcium sulfate ----

    private void gypsum() {
        // B: limestone in dilute sulfuric acid (T2); C: the calcium-salt filter cakes washed
        r(REACTING).in("chemicals/calcium_carbonate", 1).fin("sulfuric_acid", B).out("gypsum", 1).fout("carbon_dioxide", B).tier(2)
            .add("gypsum_precipitated");
        r(SEPARATING).in("chemicals/gypsum", 4).fin("water", B).out("gypsum", 4).method("filtration").add("gypsum_washing");
    }

    // ---- 13 glucono-δ-lactone ----

    private void gluconoLactone() {
        // B: glucose oxidized at the anode or by air over gold / palladium (T3)
        r(ELECTROLYZING).in("chemicals/glucose", 1).fin("water", B).fout("gluconic_acid", B).fout("hydrogen", B).src()
            .mode("electrooxidation").cat("catalysts/electrode", 64).add("gluconic_acid_electrolysis");
        r(REACTING).in("chemicals/glucose", 2).fin("oxygen", B).fin("water", B).fout("gluconic_acid", 2 * B).mode("pressure_oxidation")
            .pressure("pressurized").cat("catalysts/noble_metal", 64).add("gluconic_acid_catalytic");
        // A: aseptic aerated fermentation (T4), freed by ion exchange
        // the pH held by chalk: calcium gluconate (113), split with sulfuric acid
        r(FERMENTING).in("chemicals/glucose", 2).in("chemicals/calcium_carbonate", 1).fin("water", 2 * B)
            .cat("cultures/aspergillus_niger", CULTURE_RUNS, PURE).out("calcium_gluconate", 1).src(90).fout("carbon_dioxide", B).src()
            .sterile().mode("aerated").add("calcium_gluconate_aseptic");
        r(REACTING).in("chemicals/calcium_gluconate", 1).fin("sulfuric_acid", B).fout("gluconic_acid", 2 * B).out("gypsum", 1)
            .add("gluconic_acid_from_calcium_gluconate");
        adsorbing("gluconic_acid", "catalysts/ion_exchange_resin", "ion_exchange");
        // concentrated under vacuum, crystallized as the lactone: C6H12O7 → C6H10O6 + H2O
        r(CRYSTALLIZING).fin("gluconic_acid", B).out("glucono_delta_lactone", 1).mode("evaporative_crystallization").pressure("vacuum")
            .add("glucono_delta_lactone");
        recrystallizing("glucono_delta_lactone");
    }

    // ---- 14 agar, 15 sodium alginate, 16 pectin, 17 gelatin ----

    private void colloids() {
        // 14 C: red algae treated with alkali, extracted, the jelly pressed (T2)
        r(REACTING).in("materials/red_algae", 4).in("chemicals/sodium_hydroxide", 1).fin("water", 2 * B).fout("agar_broth", 2 * B)
            .temp("heated").tier(2).add("agar_broth_alkali");
        r(PRESSING).in("chemicals/agar_jelly", 2).out("agar", 1).src(85).add("agar_pressing");
        // 15 A: kelp digested with soda, calcium alginate precipitated; mannitol from the steep water (T2)
        r(REACTING).in("materials/kelp", 8).in("chemicals/soda_ash", 1).in("chemicals/calcium_chloride", 1).fin("water", 2 * B)
            .out("calcium_alginate", 1).src(60).out("mannitol", 1).src().chance(0.25).tier(2).add("calcium_alginate");
        r(REACTING).in("chemicals/calcium_alginate", 1).fin("hydrochloric_acid", 2 * B).out("alginic_acid", 1).out("calcium_chloride", 1)
            .add("alginic_acid");
        r(REACTING).in("chemicals/alginic_acid", 2).in("chemicals/soda_ash", 1).out("sodium_alginate", 2).fout("carbon_dioxide", B)
            .add("sodium_alginate");
        // 16 A: apples boiled → pectin liquor (T1); B: acid extraction, ethanol precipitation (T2)
        r(BOILING).in("materials/apple", 4).fin("water", B).fout("pectin_liquor", B).add("pectin_liquor");
        r(REACTING).in("materials/apple", 8).fin("water", 2 * B).fin("hydrochloric_acid", 200).fout("pectin_liquor", 2 * B).src(2)
            .temp("heated").tier(2).add("pectin_liquor_by_acid");
        r(REACTING).in("materials/citrus_peel", 4).fin("water", 2 * B).fin("hydrochloric_acid", 200).fout("pectin_liquor", 2 * B).src(3)
            .temp("heated").tier(2).add("pectin_liquor_from_citrus_peel");
        r(REACTING).fin("pectin_liquor", 2 * B).fin("ethanol", B).out("pectin", 1).src(60).fout("ethanol", 800).src().tier(2)
            .add("pectin_precipitating");
        // 17 A: hide or bone glue boiled down (T1); B: bones demineralized and limed; C: hides in acid (T2)
        r(BOILING).in("materials/hide", 4).fin("water", 2 * B).out("gelatin", 1).src(30).add("hide_glue");
        r(BOILING).in("materials/bone", 8).fin("water", 2 * B).out("gelatin", 1).src(25).add("bone_glue");
        r(REACTING).in("materials/bone", 8).in("chemicals/slaked_lime", 1).fin("hydrochloric_acid", 4 * B).out("gelatin", 1).src(60)
            .out("dicalcium_phosphate", 2).src().temp("heated").tier(2).add("gelatin_from_bones");
        r(REACTING).in("materials/hide", 4).fin("hydrochloric_acid", B).fin("water", 2 * B).out("gelatin", 1).src(60).temp("heated")
            .tier(2).add("gelatin_from_hides");
        decolouring("gelatin");
        decolouring("pectin");
        decolouring("sodium_alginate");
        decolouring("agar");
    }

    // ---- 18 caramel, 19 beet red, 20 β-carotene, 21 red yeast rice, 22 chlorophyllin ----

    private void colours() {
        // 18 B: sugar caramelized with a little acid in the stirred reactor (T2)
        r(REACTING).in("chemicals/sucrose", 2).fin("water", B).fin("sulfuric_acid", 100).fout("caramel", B).src(40).temp("heated").tier(2)
            .add("caramel_by_acid");
        r(REACTING).in("chemicals/glucose", 2).fin("water", B).fin("sulfuric_acid", 100).fout("caramel", B).src(40).temp("heated").tier(2)
            .add("caramel_from_glucose");
        // 19 A: beet pressed, the juice concentrated (T1); B: concentrated under vacuum (T2), spray dried (T3)
        r(PRESSING).in("materials/beetroot", 4).fout("beetroot_juice", B).add("beetroot_juice");
        r(BOILING).fin("beetroot_juice", 2 * B).out("betanin", 1).src(0.05).temp("heated").add("betanin_concentrate");
        r(BOILING).fin("beetroot_juice", 2 * B).out("betanin", 1).src(0.15).mode("concentration").pressure("vacuum")
            .add("betanin_vacuum_concentrate");
        r(DRYING).in("chemicals/betanin", 2).out("betanin", 1).heat("sensitive").add("betanin_spray_drying");
        // 20 A: carrots extracted with acetone (T2), saponified, crystallized; B: two-strain fermentation (T4)
        r(EXTRACTING).in("materials/carrot", 8).fin("acetone", B).fout("natural_carotene", 250).src(2).fout("acetone", 750).src()
            .mode("solvent_extraction").add("carrot_oleoresin");
        r(REACTING).fin("natural_carotene", B).in("chemicals/sodium_hydroxide", 1).out("beta_carotene", 1).src(20).temp("heated")
            .tier(2).add("beta_carotene_from_oleoresin");
        r(CRYSTALLIZING).in("chemicals/beta_carotene", 2).fin("ethanol", B).out("beta_carotene", 1).add("beta_carotene_recrystallizing");
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/blakeslea_plus", CULTURE_RUNS, PURE)
            .cat("cultures/blakeslea_minus", CULTURE_RUNS, PURE).out("beta_carotene", 1).src(50).sterile().mode("aerated")
            .add("beta_carotene_aseptic");
        // 21 A: red yeast rice in the koji room (T1, random purity); B: extracted with ethanol (T2), spray dried (T3); C: aseptic (T4)
        r(SOLID_FERMENTING).in("materials/steamed_rice", 4).fin("water", B).cat("cultures/monascus", CULTURE_RUNS)
            .out("red_yeast_rice", 4).purityRange(40, 73).mode("koji_tray").add("red_yeast_rice");
        r(EXTRACTING).in("chemicals/red_yeast_rice", 4).fin("ethanol", B).out("monascorubrin", 1).src(5).fout("ethanol", 800).src()
            .mode("solvent_extraction").add("monascus_red_extract");
        r(DRYING).in("chemicals/monascorubrin", 2).out("monascorubrin", 1).heat("sensitive").add("monascus_red_spray_drying");
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/monascus", CULTURE_RUNS, PURE)
            .out("monascorubrin", 1).src(10).sterile().mode("aerated").add("monascus_red_aseptic");
        // 22 A: leaves extracted with ethanol, saponified and coppered (T2)
        r(EXTRACTING).in("materials/leaves", 8).fin("ethanol", B).out("chlorophyll_a", 1).src(10).fout("ethanol", 800).src()
            .mode("solvent_extraction").add("chlorophyll_extract");
        r(REACTING).in("chemicals/chlorophyll_a", 1).in("chemicals/sodium_hydroxide", 1).in("chemicals/copper_sulfate", 1)
            .fin("water", B).out("sodium_copper_chlorophyllin", 1).tier(2).add("sodium_copper_chlorophyllin");
        decolouring("sodium_copper_chlorophyllin");
    }

    // ---- 23 sodium benzoate, 24 nitrite and saltpetre, 25 metabisulfite ----

    private void preservatives() {
        // 23: coal tar cut into fractions (T3), toluene oxidized by air over cobalt, sublimed, neutralized
        // I31: the wash oil (1-methylnaphthalene, S0676) and quinoline bases (S0771 B) cut from the same column
        r(DISTILLING).fin("coal_tar", B).fout("benzene", 300).src(70).fout("toluene", 200).src(70).fout("m_cresol", 100).src(60)
            .fout("methylnaphthalene_1", 100).src().fout("quinoline", 50).src()
            .out("naphthalene", 1).src(70).chance(0.25).method("multi_fraction").add("coal_tar_rectifying");
        r(DISTILLING).fin("coal_tar", B).fout("benzene", 300).src(50).mode("distillation").add("crude_benzene");
        r(REACTING).fin("toluene", 2 * B).fin("oxygen", 3 * B).out("benzoic_acid", 2).mode("pressure_oxidation").pressure("pressurized")
            .temp("heated").cat("catalysts/cobalt", 64).add("benzoic_acid");
        r(DISTILLING).in("chemicals/benzoic_acid", 2).out("benzoic_acid", 1).mode("sublimation").add("benzoic_acid_sublimation");
        r(REACTING).in("chemicals/benzoic_acid", 2).in("chemicals/soda_ash", 1).fin("water", B).out("sodium_benzoate", 2)
            .fout("carbon_dioxide", B).add("sodium_benzoate");
        recrystallizing("sodium_benzoate");
        // 24 C: potassium nitrate by double decomposition, cooled (T2)
        r(REACTING).in("chemicals/sodium_nitrate", 1).in("chemicals/potassium_chloride", 1).fin("water", B).out("potassium_nitrate", 1)
            .out("salt", 1).temp("cooling").add("potassium_nitrate_conversion");
        // 24 D: nitrous gases absorbed in soda (T3): Na2CO3 + NO + NO2 → 2 NaNO2 + CO2
        r(ABSORBING).in("chemicals/soda_ash", 1).fin("nitric_oxide", B).fin("nitrogen_dioxide", B).out("sodium_nitrite", 2)
            .fout("carbon_dioxide", B).add("sodium_nitrite");
        recrystallizing("sodium_nitrite");
        // 25 B: sulfur dioxide absorbed in soda to bisulfite, crystallized as metabisulfite (T2)
        r(ABSORBING).in("chemicals/soda_ash", 1).fin("sulfur_dioxide", 2 * B).fin("water", B).fout("sodium_bisulfite", 2 * B)
            .fout("carbon_dioxide", B).add("sodium_bisulfite");
        r(CRYSTALLIZING).fin("sodium_bisulfite", 2 * B).out("sodium_metabisulfite", 1).temp("cooling").add("sodium_metabisulfite");
    }

    // ---- 26 sorbitol, 28 ascorbic acid ----

    private void sorbitolAndAscorbicAcid() {
        // 26 A: electroreduction (T3); B: hydrogenation over nickel (T3), nickel taken out, decoloured, crystallized
        r(ELECTROLYZING).in("chemicals/glucose", 1).fin("water", B).fout("sorbitol_syrup", B).fout("oxygen", 500).src()
            .mode("electroreduction").cat("catalysts/electrode", 64).add("sorbitol_electrolysis");
        r(REACTING).in("chemicals/glucose", 1).fin("hydrogen", B).fin("water", B).fout("sorbitol_syrup", B).mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("sorbitol_hydrogenation");
        adsorbing("sorbitol_syrup", "catalysts/ion_exchange_resin", "ion_exchange");
        adsorbing("sorbitol_syrup", "catalysts/activated_carbon", null);
        r(CRYSTALLIZING).fin("sorbitol_syrup", 2 * B).out("sorbitol", 1).mode("evaporative_crystallization").add("sorbitol");
        // 28 A (Reichstein) and B (two-step fermentation): sorbitol → L-sorbose (Gluconobacter)
        r(FERMENTING).in("chemicals/sorbitol", 2).fin("water", B).cat("cultures/gluconobacter", CULTURE_RUNS, PURE).out("l_sorbose", 2)
            .src(95).sterile().mode("aerated").add("l_sorbose");
        r(REACTING).in("chemicals/l_sorbose", 1).fin("acetone", 2 * B).fin("sulfuric_acid", 100).out("diacetone_sorbose", 1)
            .tier(2).add("diacetone_sorbose");
        r(REACTING).in("chemicals/diacetone_sorbose", 1).fin("sodium_hypochlorite", B).fin("hydrochloric_acid", B)
            .out("ketogulonic_acid", 1).out("salt", 1).fout("acetone", 2 * B).src().tier(2).add("ketogulonic_acid_chemical");
        r(FERMENTING).in("chemicals/l_sorbose", 1).fin("water", B).cat("cultures/ketogulonicigenium", CULTURE_RUNS, PURE)
            .cat("cultures/bacillus_megaterium", CULTURE_RUNS, PURE).out("ketogulonic_acid", 1).src(95).sterile().mode("aerated")
            .add("ketogulonic_acid_fermentation");
        r(REACTING).in("chemicals/ketogulonic_acid", 1).fin("methanol", B).fin("sulfuric_acid", 100).out("methyl_ketogulonate", 1)
            .temp("heated").add("methyl_ketogulonate");
        r(REACTING).in("chemicals/methyl_ketogulonate", 2).in("chemicals/soda_ash", 1).out("sodium_ascorbate", 2).fout("methanol", 2 * B)
            .keep().fout("carbon_dioxide", B).add("sodium_ascorbate");
        r(REACTING).in("chemicals/sodium_ascorbate", 2).fin("sulfuric_acid", B).out("ascorbic_acid", 2).out("sodium_sulfate", 1)
            .add("ascorbic_acid");
        recrystallizing("ascorbic_acid");
    }

    // ---- 27 mono- and diglycerides ----

    private void monoglycerides() {
        // A: glycerolysis under nitrogen (T2): C57H110O6 + 2 C3H8O3 → 3 C21H42O4; B: direct esterification
        r(REACTING).in("chemicals/animal_fat", 1).in("chemicals/sodium_hydroxide", 1).fin("glycerol", 2 * B)
            .out("glyceryl_monostearate", 3).src(45).temp("heated").inert().add("glycerolysis");
        r(REACTING).in("chemicals/stearic_acid", 1).fin("glycerol", B).fin("sulfuric_acid", 100).out("glyceryl_monostearate", 1).src(50)
            .temp("heated").inert().add("monoglyceride_esterification");
    }

    // ---- 29 menthol, 30 vanillin ----

    private void flavours() {
        // 29 A: mint steam-distilled, the oil separated (T1), frozen out (T1 with cold, T2 crystallizer)
        r(DISTILLING).in("materials/mint", 8).fin("water", B).fout("mint_oil", 100).mode("steam_distillation").method("oil_separation")
            .add("mint_oil");
        r(CRYSTALLIZING).fin("mint_oil", B).out("menthol", 1).src(90).temp("cooling").add("menthol_freezing");
        recrystallizing("menthol");
        // 29 B: thymol hydrogenated (T3)
        // I31: the racemate cut on the column is dl-menthol (19 §2 `dl_menthol`, 联产)
        r(REACTING).in("chemicals/thymol", 2).fin("hydrogen", 6 * B).out("menthol", 1).out("dl_menthol", 1).src().mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("menthol_from_thymol");
        // 30 A: vanilla pods extracted with ethanol (T1 tincture)
        r(EXTRACTING).in("materials/vanilla", 4).fin("ethanol", B).out("vanillin", 1).src(2).fout("ethanol", 750).src().add("vanilla_tincture");
        // 30 B: lignin from sulfite pulping oxidized by air over copper (T3)
        r(REACTING).in("materials/logs", 4).fin("sodium_bisulfite", 2 * B).fout("sulfite_spent_liquor", 2 * B).out("cellulose", 2).src()
            .pressure("pressurized").temp("heated").add("sulfite_pulping");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("sulfite_spent_liquor", 2 * B).fin("oxygen", B).out("vanillin", 1).src(80)
            .mode("pressure_oxidation").pressure("pressurized").cat("catalysts/copper", 64).add("vanillin_from_lignin");
        // 30 C: guaiacol (wood tar) and glyoxylic acid condensed, oxidized and decarboxylated (T3)
        // I31: creosol (S0100 A) and the pine tar oil (N352) are further cuts of the same column (18 §1.1 联产)
        r(DISTILLING).fin("wood_tar", B).fout("guaiacol", 200).src(70).fout("m_cresol", 100).src(60).fout("creosol", 100).src()
            .fout("pine_tar_oil", 150).method("multi_fraction").add("wood_tar_rectifying");
        r(REACTING).in("chemicals/sodium_hydroxide", 1).fin("guaiacol", B).fin("glyoxylic_acid", B).out("vanillylmandelic_acid", 1)
            .add("vanillylmandelic_acid");
        r(REACTING).in("chemicals/vanillylmandelic_acid", 2).out("vanillin", 2).fout("carbon_dioxide", 2 * B).temp("heated").air()
            .add("vanillin_from_guaiacol");
        recrystallizing("vanillin");
    }
}
