package com.tonywww.foodindustrial.content.batch5;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.BOILING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The fifth content batch, group D gum bases (I31, additives/17 §D, 564–584), with the cracked gas, C4 and rosin
 * intermediates (17 新增中间体) and piperylene for δ-damascone (19 S0987). The four latexes have no source
 * yet (08 §C); the natural gums get their refining (the B routes) as recipes of their own.
 */
public final class Batch5GumBase extends Batch {
    private Batch5GumBase(RecipeGen.Output out) {
        super(out, "batch5");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch5GumBase(out).generate());
    }

    private void generate() {
        naturalGums();
        cracking();
        rubbers();
        resins();
        waxes();
        others();
    }

    // ---- 564–568 ----

    private void naturalGums() {
        // 564 A: balata latex spread thin and dried in sheets
        x(DRYING).in("balata_latex").out("massaranduba_balata").add("massaranduba_balata");
        // 565 A: jelutong latex coagulated hot with dilute phosphoric acid on the boiling stove, boiled clean
        x(REACTING).in("jelutong_latex").inMb("phosphoric_acid", 100).out("jelutong").temp("heated").add("jelutong");
        // 566 A: sorva latex boiled until it sets
        x(BOILING).in("sorva_latex").out("leche_caspi").add("leche_caspi");
        // 567, 568 A: sapodilla latex boiled down with stirring and moulded (one material, 11 问题 34)
        x(BOILING).in("sapodilla_latex").out("chicle").add("chicle");
        // B: melted in hot water, spun clean and dried; sorva gum only soaked in hot water
        for (String gum : new String[] {"massaranduba_balata", "jelutong", "chicle"}) {
            m14().in(gum, 4).in("water").outDefault(gum, 3).add(gum + "_refining");
        }
        x(EXTRACTING).in("leche_caspi", 4).in("water").outDefault("leche_caspi", 3).add("leche_caspi_refining");
    }

    // ---- C4 cut (569, 570) and C5 (S0987) ----

    private void cracking() {
        // light naphtha steam-cracked to the cracked gas, separated cold in one go: ethylene, propene, the C4 cut and the
        // piperylene of the C5 cut (11 问题 42; the methane and hydrogen not written)
        m27("cracking").in("light_naphtha").in("water").out("naphtha_cracked_gas").add("naphtha_cracked_gas");
        column().mode("cryogenic_separation").in("naphtha_cracked_gas").outMb("ethylene", 300).outMb("propene", 150)
            .outMb("c4_fraction", 100).outMb("piperylene", 10).add("naphtha_cracked_gas_separation");
        // butadiene drawn off the C4 cut by extractive distillation with dimethylformamide (P99, a little solvent lost)
        column().mode("atmospheric_distillation").in("c4_fraction").inMb("dimethylformamide", 50).outMb("butadiene", 450).outMb("c4_raffinate", 500)
            .add("butadiene");
    }

    // ---- 569–572 ----

    private void rubbers() {
        // 569: emulsion polymerization with soap and ammonium persulfate, 75/25 (the 50/50 grade only in the description,
        // 2026-11-12: a ratio does not tell recipes apart)
        x(REACTING).in("sodium_stearate").in("ammonium_persulfate").in("butadiene", 3).in("styrene").in("water", 2)
            .out("styrene_butadiene_rubber", 4).mode("polymerization").temp("heated").tier(2).add("styrene_butadiene_rubber");
        // 570: the raffinate C4 cut polymerized cold over aluminium chloride, washed with caustic soda
        x(REACTING).in("c4_raffinate", 2).in("sodium_hydroxide").cat("catalysts/aluminium_chloride", 64).out("polybutene")
            .mode("polymerization").temp("cooling").tier(2).add("polybutene");
        // 571: high-pressure radical polymerization, propene as chain transfer agent, a trace of oxygen to start it
        x(REACTING).in("ethylene", 2).inMb("propene", 100).inMb("oxygen", 10).out("polyethylene", 2).mode("polymerization")
            .pressure("high").temp("high").add("polyethylene");
        // 572: isoprene by dehydrating isoprenol over alumina; isobutylene with 2% isoprene in methyl chloride at −100 °C
        m27("gas_catalysis").in("isoprenol").cat("catalysts/alumina", 64).out("isoprene").outMb("water", 300).add("isoprene_from_isoprenol");
        x(REACTING).in("isobutylene", 4).inMb("isoprene", 100).in("methyl_chloride").cat("catalysts/aluminium_chloride", 64)
            .out("butyl_rubber", 4).outMb("methyl_chloride", 900).mode("polymerization").temp("freezing").tier(2).add("butyl_rubber");
    }

    // ---- 573–579 ----

    private void resins() {
        // 573, 579: rosin (or wood rosin) in toluene dimerized cold by sulfuric acid; the unpolymerized acids distilled off
        for (String rosin : new String[] {"rosin", "wood_rosin"}) {
            m14cold().method("corrosion_resistant").in(rosin, 2).in("toluene").inMb("sulfuric_acid", 200).out("partially_dimerized_rosin", 2)
                .outMb("toluene", 900).add("partially_dimerized_rosin" + (rosin.equals("rosin") ? "" : "_from_wood_rosin"));
        }
        // melted first (the column takes no items), the unpolymerized acids distilled off under vacuum, the residue kept
        x(BOILING).in("partially_dimerized_rosin").outDefault("molten_partially_dimerized_rosin", 1).temp("heated")
            .add("molten_partially_dimerized_rosin");
        column().mode("vacuum_distillation").pressure("vacuum").in("molten_partially_dimerized_rosin", 2).out("polymerized_rosin")
            .add("polymerized_rosin");
        // the esters under nitrogen, as 137 and 426
        m14inert().in("partially_dimerized_rosin", 3).in("glycerol").out("dimerized_rosin_glycerol_ester").add("dimerized_rosin_glycerol_ester");
        m14inert().in("polymerized_rosin", 3).in("glycerol").out("polymerized_rosin_glycerol_ester").add("polymerized_rosin_glycerol_ester");
        m14inert().in("hydrogenated_rosin", 4).in("pentaerythritol").out("hydrogenated_rosin_pentaerythritol_ester")
            .add("hydrogenated_rosin_pentaerythritol_ester");
        m23(null).in("hydrogenated_rosin").in("methanol").temp("high").out("hydrogenated_rosin_methyl_ester").add("hydrogenated_rosin_methyl_ester");
        // 577: vinyl laurate by transvinylation over palladium, copolymerized with vinyl acetate in solution
        m14().in("lauric_acid").in("vinyl_acetate", 2).cat("catalysts/palladium", 64).out("vinyl_laurate").out("acetic_acid")
            .outMb("vinyl_acetate", 900).add("vinyl_laurate");
        x(REACTING).in("vinyl_acetate", 3).in("vinyl_laurate").out("vinyl_acetate_laurate_copolymer", 4).mode("polymerization").temp("heated")
            .tier(2).add("vinyl_acetate_laurate_copolymer");
        // 578: turpentine, β-pinene or limonene in hexane, cationic polymerization over aluminium chloride
        for (String terpene : new String[] {"turpentine", "beta_pinene", "limonene"}) {
            x(REACTING).in(terpene, 2).in("n_hexane").cat("catalysts/aluminium_chloride", 64).out("terpene_resin", 2).outMb("n_hexane", 900)
                .mode("polymerization").tier(2).add(terpene.equals("turpentine") ? "terpene_resin" : "terpene_resin_from_" + terpene);
        }
    }

    // ---- 580–582 ----

    private void waxes() {
        // 580: as 571 with acetaldehyde as the chain transfer agent, hotter (US4820876, 2026-11-12)
        x(REACTING).in("ethylene", 2).inMb("acetaldehyde", 200).inMb("oxygen", 10).out("polyethylene_wax", 2).mode("polymerization")
            .pressure("high").temp("high").add("polyethylene_wax");
        // 581: Fischer–Tropsch synthesis over iron, the reaction water parted and the light end taken off under vacuum (merged)
        // as light naphtha (11 问题 42; share not confirmed); the crude wax is not food grade until hydrofinished
        m23(null).in("carbon_monoxide", 4).in("hydrogen", 8).cat("catalysts/iron", 64).out("fischer_tropsch_wax").out("water", 4)
            .outMb("light_naphtha", 250).add("fischer_tropsch_wax");
        hydrogenation("catalysts/nickel").in("fischer_tropsch_wax", 4).in("hydrogen").outDefault("fischer_tropsch_wax", 4)
            .add("fischer_tropsch_wax_hydrofinishing");
        // 582 A: candelilla stems boiled in dilute sulfuric acid, the wax skimmed off; B decoloured
        x(BOILING).in("materials/candelilla_stem", 8).in("water").inMb("sulfuric_acid", 100).out("candelilla_wax").temp("heated")
            .add("candelilla_wax");
        decolouring("candelilla_wax");
    }

    // ---- 584 ----

    private void others() {
        x(REACTING).in("alginic_acid").in("ammonium_bicarbonate").out("ammonium_alginate").out("carbon_dioxide").tier(2).add("ammonium_alginate");
        x(REACTING).in("alginic_acid", 2).in("ammonium_carbonate").out("ammonium_alginate", 2).out("carbon_dioxide").tier(2)
            .add("ammonium_alginate_from_ammonium_carbonate");
    }
}
