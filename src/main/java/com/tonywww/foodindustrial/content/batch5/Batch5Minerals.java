package com.tonywww.foodindustrial.content.batch5;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.ELECTROLYZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The fifth content batch, group C minerals (I31, additives/17 §C 矿物质, 482–530), with their intermediates
 * (17 新增中间体), animal blood pressed from raw meat and selenium-rich soybeans (17 新增原料). Washing,
 * filtering, drying and milling between the steps of a route are part of its core recipe (11 问题 40).
 */
public final class Batch5Minerals extends Batch {
    private static final String[] MEATS = {"raw_pork", "raw_beef", "raw_mutton", "raw_chicken", "raw_rabbit"};

    private Batch5Minerals(RecipeGen.Output out) {
        super(out, "batch5");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch5Minerals(out).generate());
    }

    private void generate() {
        rawMaterials();
        iron();
        calcium();
        zinc();
        selenium();
        magnesiumCopperManganese();
        others();
    }

    // ---- 17 新增原料 ----

    private void rawMaterials() {
        // animal blood pressed out of vanilla raw meat (game simplification, 11 问题 29)
        for (String meat : MEATS) {
            x(PRESSING).in("materials/" + meat, 4).outMb("animal_blood", 250).tier(2).add("animal_blood_from_" + meat);
        }
        // soybeans steeped to sprout in dilute sodium selenite (game simplification)
        x(EXTRACTING).in("materials/soybean", 8).in("sodium_selenite").in("water").out("selenium_rich_soybean", 8)
            .add("selenium_rich_soybean");
    }

    // ---- 482–494 ----

    private void iron() {
        // 482, 492, 490: double decomposition of green vitriol under nitrogen
        m14inert().in("fumaric_acid").in("soda_ash").in("ferrous_sulfate").in("water").out("ferrous_fumarate").out("sodium_sulfate")
            .out("carbon_dioxide").add("ferrous_fumarate");
        m14inert().in("disodium_succinate").in("ferrous_sulfate").in("water").out("ferrous_succinate").out("sodium_sulfate")
            .add("ferrous_succinate");
        m14inert().in("ferrous_sulfate").in("soda_ash").in("water").out("ferrous_carbonate").out("sodium_sulfate").add("ferrous_carbonate");
        // 483: the ferric citrate solution drum-dried to scales
        x(DRYING).in("ferric_citrate").out("ferric_citrate_powder").mode("drum_drying").tier(2).add("ferric_citrate_powder");
        // 484 A from calcium lactate, B lactic acid on reduced iron
        m14inert().in("calcium_lactate").in("ferrous_sulfate").in("water").out("ferrous_lactate").out("gypsum").add("ferrous_lactate");
        m14inert().in("lactic_acid", 2).in("reduced_iron").in("water").out("ferrous_lactate").out("hydrogen").add("ferrous_lactate_from_iron");
        // 485: hemin crystallized from blood in hot glacial acetic acid with salt
        m14().in("animal_blood", 4).in("acetic_acid", 1, "90").in("salt").out("hemin").add("hemin");
        // 493 (and 486, one material since 2026-11-12): globin hydrolysed by protease, the haem aggregated with acid
        m14().in("animal_blood", 4).in("hydrochloric_acid").cat("enzymes/papain", ENZYME_RUNS).out("heme_iron").add("heme_iron");
        m14().in("animal_blood", 4).in("hydrochloric_acid").cat("enzymes/microbial_protease", ENZYME_RUNS).out("heme_iron")
            .add("heme_iron_by_microbial_protease");
        // 487: reduced iron and glycine 1 : 2 with a little citric acid, which stays in the product
        m14inert().in("reduced_iron", 4).in("glycine", 8).in("citric_acid").in("water", 2).out("ferrous_bisglycinate", 4).out("hydrogen", 4)
            .add("ferrous_bisglycinate");
        // 488: iron oxide black (for mill scale) reduced by coke in the kiln, or by hydrogen in a fluidized bed (P104)
        x(KILN).in("iron_oxide_black").in("materials/coke", 2).out("reduced_iron", 3).out("carbon_dioxide", 2).mode("reduction").tier(2)
            .add("reduced_iron");
        m27("fluidized_bed").in("iron_oxide_black").in("hydrogen", 4).out("reduced_iron", 3).out("water", 4).add("reduced_iron_by_hydrogen");
        // 489: carbonyl process, the carbon monoxide given back by the decomposition
        m23("carbonylation").in("reduced_iron").in("carbon_monoxide", 5).out("iron_pentacarbonyl").add("iron_pentacarbonyl");
        m27("cracking").in("iron_pentacarbonyl").out("carbonyl_iron").out("carbon_monoxide", 5).add("carbonyl_iron");
        // 491 A from fresh ferrous carbonate, B citric acid on reduced iron
        m14inert().in("ferrous_carbonate").in("citric_acid").in("water").out("ferrous_citrate").out("carbon_dioxide").add("ferrous_citrate");
        m14inert().in("reduced_iron").in("citric_acid").in("water").out("ferrous_citrate").out("hydrogen").add("ferrous_citrate_from_iron");
        // 494: iron anode in a ferrous sulfate electrolyte, the cathode deposit stripped (the electrolyte is not used up and
        // not written, a game simplification; 11 问题 42)
        x(ELECTROLYZING).in("materials/iron").out("electrolytic_iron").mode("electroreduction").cat("catalysts/electrode", 64)
            .add("electrolytic_iron");
    }

    // ---- 495–501 ----

    private void calcium() {
        // 495 A: calcium salt fermentation with the L-lactic acid strain (114 uses the parent strain); B: L-lactic acid of the
        // aseptic route neutralized with chalk
        x(FERMENTING).in("glucose").in("calcium_carbonate").in("water").cat("cultures/lactobacillus_l_lactate", CULTURE_RUNS)
            .out("calcium_l_lactate").src(80).out("carbon_dioxide").airtight().add("calcium_l_lactate_fermentation");
        m14().in("lactic_acid", 2, "food").in("calcium_carbonate").in("water").out("calcium_l_lactate").out("carbon_dioxide")
            .add("calcium_l_lactate");
        // 496: vitamin C cleaved by hydrogen peroxide over chalk; calcium oxalate filtered off
        m14().in("ascorbic_acid", 2).in("calcium_carbonate", 3).in("hydrogen_peroxide", 4).out("calcium_l_threonate").out("calcium_oxalate", 2)
            .out("carbon_dioxide", 3).add("calcium_l_threonate");
        m14().in("glycine", 2).in("slaked_lime").in("water").out("calcium_glycinate").add("calcium_glycinate");
        m14().in("l_aspartic_acid", 2).in("calcium_carbonate").in("water").out("calcium_aspartate").out("carbon_dioxide").add("calcium_aspartate");
        // 499: Ca : citric : malic = 6 : 2 : 3
        m14().in("calcium_carbonate", 6).in("citric_acid", 2).in("malic_acid", 3).in("water").out("calcium_citrate_malate")
            .out("carbon_dioxide", 6).add("calcium_citrate_malate");
        // 500: glycerol esterified with phosphoric acid under vacuum, then neutralized with slaked lime
        m14().in("glycerol").in("phosphoric_acid").pressure("vacuum").out("glycerophosphoric_acid").add("glycerophosphoric_acid");
        m14().in("glycerophosphoric_acid").in("slaked_lime").in("water").out("calcium_glycerophosphate").add("calcium_glycerophosphate");
        // 501: bones crushed, boiled to take off the bone fat, dried, milled ultrafine and sterilized
        m14().in("materials/bone", 8).in("water", 2).out("bone_powder", 4).out("animal_fat").add("bone_powder");
    }

    // ---- 502–508 ----

    private void zinc() {
        m14().in("glycine", 2).in("zinc_oxide").in("water").out("zinc_glycinate").add("zinc_glycinate");
        // 503 A wet process: zinc carbonate calcined; B indirect process: zinc vaporized and burnt
        x(KILN).in("zinc_carbonate").out("zinc_oxide").out("carbon_dioxide").mode("calcining").tier(2).add("zinc_oxide");
        m27("burning").in("materials/zinc").out("zinc_oxide").add("zinc_oxide_from_zinc");
        m14().in("lactic_acid", 2).in("zinc_oxide").in("water").out("zinc_lactate").add("zinc_lactate");
        m14().in("citric_acid", 2).in("zinc_oxide", 3).in("water").out("zinc_citrate").add("zinc_citrate");
        m14().in("citric_acid", 2).in("zinc_carbonate", 3).in("water").out("zinc_citrate").out("carbon_dioxide", 3)
            .add("zinc_citrate_from_carbonate");
        // 506 food grade: hydrogen peroxide takes out the iron (the plain acid route of 08 stays as it is)
        m14acid().in("zinc_oxide").in("hydrochloric_acid", 2).in("hydrogen_peroxide").out("zinc_chloride").add("zinc_chloride_food_grade");
        m14acid().in("materials/zinc").in("hydrochloric_acid", 2).in("hydrogen_peroxide").out("zinc_chloride").out("hydrogen")
            .add("zinc_chloride_from_zinc_food_grade");
        m14().in("zinc_oxide").in("acetic_acid", 2).out("zinc_acetate").add("zinc_acetate");
        // 508 at room temperature; ammonium sulfate recovered from the mother liquor
        x(REACTING).in("zinc_sulfate").in("ammonium_bicarbonate", 2).in("water").out("zinc_carbonate").out("ammonium_sulfate")
            .out("carbon_dioxide").tier(2).add("zinc_carbonate");
    }

    // ---- 509–515 ----

    private void selenium() {
        // 509: selenium dioxide sublimed from burnt crude selenium, or roasted out of selenium ore
        m27("burning").in("materials/selenium").out("selenium_dioxide").add("selenium_dioxide");
        x(KILN).in("materials/selenium_ore", 4).out("selenium_dioxide").out("sulfur_dioxide").mode("roasting").tier(2)
            .add("selenium_dioxide_from_ore");
        m14().in("selenium_dioxide").in("sodium_hydroxide", 2).in("water").out("sodium_selenite").add("sodium_selenite");
        m14().in("sodium_selenite").in("hydrogen_peroxide").out("sodium_selenate").add("sodium_selenate");
        // 511: selenium-rich soybeans defatted with hexane, protein extracted with dilute alkali and precipitated with ethanol
        m14().in("selenium_rich_soybean", 4).in("sodium_hydroxide").in("n_hexane").in("water", 2).in("ethanol").out("selenoprotein")
            .outMb("crude_soybean_oil", 500).outMb("n_hexane", 800).outMb("ethanol", 800).add("selenoprotein");
        // 512, 515: enrichment culture, sodium selenite fed into an aerated fermentation; the whole cells used
        x(FERMENTING).in("glucose", 2).in("sodium_selenite").in("water", 2).cat("cultures/edible_fungus", CULTURE_RUNS, PURE)
            .out("selenium_mushroom_powder", 2).sterile().mode("aerated").add("selenium_mushroom_powder");
        x(FERMENTING).in("glucose", 2).in("sodium_selenite").in("water", 2).cat("cultures/yeast", CULTURE_RUNS, PURE).out("selenium_yeast", 2)
            .sterile().mode("aerated").add("selenium_yeast");
        // 513: sodium methylselenolate added to α-acetamidoacrylic acid, the L form freed by aminoacylase; the N-acetyl-D form
        // left over is racemized with a little acetic anhydride and goes back to the resolution (11 问题 42)
        m14inert().in("materials/selenium").in("sodium", 2).in("methyl_chloride").out("sodium_methylselenolate").out("salt")
            .add("sodium_methylselenolate");
        m14().in("acetic_acid").in("ammonia").out("acetamide").add("acetamide");
        m14().in("pyruvic_acid").in("acetamide").inMb("sulfuric_acid", 100).out("acetamidoacrylic_acid").add("acetamidoacrylic_acid");
        m14inert().in("acetamidoacrylic_acid").in("sodium_methylselenolate").in("hydrochloric_acid").out("acetyl_methylselenocysteine")
            .out("salt").add("acetyl_methylselenocysteine");
        m14().in("acetyl_methylselenocysteine", 2).in("water").cat("enzymes/aminoacylase", IMMOBILIZED_RUNS).out("methylselenocysteine")
            .out("acetyl_d_methylselenocysteine").out("acetic_acid").add("methylselenocysteine");
        m14().in("acetyl_d_methylselenocysteine").inMb("acetic_anhydride", 100).out("acetyl_methylselenocysteine")
            .add("acetyl_methylselenocysteine_racemization");
        // 514: carrageenan esterified with selenite in dilute nitric acid, neutralized, precipitated with ethanol
        m14().in("carrageenan").in("sodium_selenite").in("soda_ash").in("nitric_acid").in("ethanol").out("selenium_carrageenan")
            .outMb("ethanol", 800).add("selenium_carrageenan");
    }

    // ---- 516–524 ----

    private void magnesiumCopperManganese() {
        m14().in("phosphoric_acid").in("magnesium_hydroxide").in("water").out("magnesium_hydrogen_phosphate").add("magnesium_hydrogen_phosphate");
        // 517: glucono-δ-lactone hydrolysed hot, neutralized with magnesium carbonate
        m14().in("glucono_delta_lactone", 2).in("magnesium_carbonate").in("water").out("magnesium_gluconate").out("carbon_dioxide")
            .add("magnesium_gluconate");
        // 518: gluconic acid (electrolytic liquor or the hydrolysed lactone) on copper carbonate
        m14().in("gluconic_acid", 4).in("copper_carbonate").out("copper_gluconate", 2).out("carbon_dioxide").add("copper_gluconate");
        m14().in("glucono_delta_lactone", 4).in("copper_carbonate").in("water").out("copper_gluconate", 2).out("carbon_dioxide")
            .add("copper_gluconate_from_lactone");
        // 519 A copper carbonate in citric acid, B blue vitriol and sodium citrate
        m14().in("copper_carbonate", 2).in("citric_acid", 2).in("water").out("copper_citrate").out("carbon_dioxide", 2).add("copper_citrate");
        m14().in("copper_sulfate", 2).in("trisodium_citrate").in("water").out("copper_citrate").out("sodium_sulfate")
            .add("copper_citrate_from_copper_sulfate");
        m14().in("copper_sulfate", 2).in("soda_ash", 2).in("water").out("copper_carbonate").out("sodium_sulfate", 2).out("carbon_dioxide")
            .add("copper_carbonate");
        m14acid().in("manganese_carbonate").in("hydrochloric_acid", 2).out("manganese_chloride").out("carbon_dioxide").add("manganese_chloride");
        // 522: manganese sulfate cleaned with activated carbon, precipitated with ammonium bicarbonate at room temperature
        x(REACTING).in("manganese_sulfate").in("ammonium_bicarbonate", 2).in("water").out("manganese_carbonate").out("ammonium_sulfate")
            .out("carbon_dioxide").tier(2).add("manganese_carbonate");
        m14().in("manganese_carbonate", 3).in("citric_acid", 2).in("water").out("manganese_citrate").out("carbon_dioxide", 3)
            .add("manganese_citrate");
        m14().in("gluconic_acid", 2).in("manganese_carbonate").out("manganese_gluconate").out("carbon_dioxide").add("manganese_gluconate");
        m14().in("glucono_delta_lactone", 2).in("manganese_carbonate").in("water").out("manganese_gluconate").out("carbon_dioxide")
            .add("manganese_gluconate_from_lactone");
    }

    // ---- 525–530 ----

    private void others() {
        // 525 A neutralization; B as 153 A, the pH held with caustic potash
        m14().in("gluconic_acid").in("potassium_hydroxide").out("potassium_gluconate").add("potassium_gluconate");
        x(FERMENTING).in("glucose", 2).in("potassium_hydroxide", 2).in("water", 2).cat("cultures/aspergillus_niger", CULTURE_RUNS, PURE)
            .out("potassium_gluconate", 2).sterile().mode("aerated").add("potassium_gluconate_fermentation");
        // 526: iodine in caustic soda, the iodate reduced by iron filings
        m14().in("iodine", 3).in("sodium_hydroxide", 6).in("materials/iron", 2).in("water", 2).out("sodium_iodide", 6).out("ferric_hydroxide", 2)
            .add("sodium_iodide");
        // 527, 528 A: chromium metal (other mods) dissolved, the chromium(II) oxidized by air
        m14air().method("corrosion_resistant").in("materials/chromium", 2).in("sulfuric_acid", 3).out("chromium_sulfate").out("hydrogen", 2)
            .add("chromium_sulfate");
        m14air().method("corrosion_resistant").in("materials/chromium").in("hydrochloric_acid", 3).out("chromium_chloride").out("hydrogen")
            .add("chromium_chloride");
        // 527, 528 B (optional toxic route): chromite roasted with soda ash, acidified to dichromate, reduced
        x(KILN).in("materials/chromite", 2).in("soda_ash", 4).out("sodium_chromate", 4).out("iron_oxide_red").out("carbon_dioxide", 4)
            .mode("roasting").tier(2).add("sodium_chromate");
        m14acid().in("sodium_chromate", 2).in("sulfuric_acid").out("sodium_dichromate").out("sodium_sulfate").add("sodium_dichromate");
        m14acid().in("sodium_dichromate").in("sulfuric_acid").in("sulfur_dioxide", 3).out("chromium_sulfate").out("sodium_sulfate")
            .add("chromium_sulfate_from_dichromate");
        // 528 B: methanol the reducing agent, so no chlorine and eight parts of acid (US4528175; 11 问题 42)
        m14acid().in("sodium_dichromate").in("hydrochloric_acid", 8).in("methanol").out("chromium_chloride", 2).out("salt", 2)
            .out("carbon_dioxide").add("chromium_chloride_from_dichromate");
        // 529, 530: molybdenum roasted to the trioxide, dissolved in caustic soda or ammonia
        x(KILN).in("materials/molybdenum").out("molybdenum_trioxide").mode("roasting").tier(2).add("molybdenum_trioxide");
        m14().in("molybdenum_trioxide").in("sodium_hydroxide", 2).in("water").out("sodium_molybdate").add("sodium_molybdate");
        m14().in("molybdenum_trioxide", 7).in("ammonia", 6).in("water", 4).out("ammonium_molybdate").add("ammonium_molybdate");
    }
}
