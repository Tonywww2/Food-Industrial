package com.tonywww.foodindustrial.content.flavors;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.content.Chem;
import com.tonywww.foodindustrial.content.flavors.FlavorRecipes.Input;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.ELECTROLYZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SEPARATING;

/**
 * The flavour recipes written by hand (I31): route rows the exporter cannot read as tokens, the intermediates of
 * 19 §2 described step by step, and the routes too wide for the device of their template (18 §1.4).
 */
final class FlavorManual extends Batch {
    /** Routes (code, or id of an intermediate) the template generator leaves to this class. */
    static final Set<String> HAND_WRITTEN = Set.of(
        // coal tar fractions (ST51, S0095 A, S0949): one column cut per fraction
        "S0095_a", "S0949", "S0675", "S0676", "S0705_a", "S0724_a", "S0740_b", "S0771_b",
        // coproducts of recipes of earlier batches (18 §1.1 联产): written into those recipes
        "N039", "N352", "S0100_a", "catechol", "S0668",
        // the liquid smoke settled from the wood vinegar of the wood dry distillation; pyroligneous acid, the vinegar
        // settled and cleared on carbon (2026-11-12)
        "S1418", "N267",
        // bitter almond oil: distilled, then freed of hydrocyanic acid in the steeping vat (2026-11-12)
        "N182",
        // turpentine: comes over with the steam from the resin that leaves the rosin (137, 联产 18 §1.5)
        "N298");
    /** Flavours without the optional refining recipe (refined another way here). */
    static final Set<String> NOT_REFINED = Set.of(
        // the column that cuts an isomer off is their refining: the remainder comes out at the column's purity (11 问题 41)
        "cyclocitral", "alpha_damascone", "ethyl_dimethylpyrazine_isomers");
    /**
     * Inputs that replace a row's tokens (route key and variant suffix): the solid acid that tells camphene from the other pinene isomers (S0657).
     */
    /**
     * Products whose rows give the same reactants in different ratios but which 18 §1.5 makes one step (联产,
     * 2026-11-12): the trithiolane and the tetrathiane of propanal, sulfur and hydrogen sulfide; the dimer and trimer
     * of the base condensation of acetone.
     */
    static final Set<String> COPRODUCTS_IN_RATIO = Set.of("diethyl_trithiolane", "diethyl_tetrathiane_trithiolane", "mesityl_oxide",
        "isophorone");
    /** Solid coproducts that are no flavour route of their own, put into the route's cut (18 §1.5 联产). */
    static final Map<String, List<String>> COPRODUCTS = Map.of(
        // cedrol crystallizes out of the same cut of cedarwood oil as cedrenol and the terpenes (2026-11-12)
        "S0074", List.of("cedrol"));
    static final Map<String, List<Input>> INPUTS = Map.of(
        // one-step aldol condensation and hydrogenation over palladium on an acid resin (one catalyst slot, 2026-11-12)
        "S1132", List.of(fluid("methyl_isobutyl_ketone"), fluid("acetone"), fluid("hydrogen"),
            new Input("catalysts/palladium_resin", false, true, "palladium_resin", false)),
        // the aldol of butyraldehyde needs its base: tells 2-ethylhexanol from n-butanol (431 B)
        "S0052", List.of(fluid("butyraldehyde"), fluid("hydrogen"), new Input("chemicals/sodium_hydroxide", false, false, "sodium_hydroxide", true)),
        "S0657", List.of(fluid("alpha_pinene"), new Input("chemicals/titanium_dioxide", false, true, "titanium_dioxide", true)),
        // the lactone hydrogenated on from the succinic anhydride of the same maleic anhydride (MA → SA → GBL), over copper
        // (11 问题 41: SA and GBL had the same reactants)
        "S0630", List.of(new Input("chemicals/succinic_anhydride", false, false, "succinic_anhydride", true), fluid("hydrogen"),
            new Input("catalysts/copper", false, true, "copper", false)));

    private static Input fluid(String id) {
        return new Input(id, true, false, id, true);
    }

    FlavorManual(RecipeGen.Output out) {
        super(out, "flavors");
    }

    void generate() {
        coalTar();
        smoke();
        carvone();
        variants();
        aliphatic();
        aromatic();
        nitrogenSulfur();
        natural();
    }

    // ---- intermediates of 19 §2 described step by step: aliphatic ----

    private void aliphatic() {
        m14inert().in("sodium").in("isopropanol", 3).in("aluminium_chloride").out("aluminium_isopropoxide").out("salt", 3)
            .add("aluminium_isopropoxide");
        // tetrahydrofurfuryl alcohol, dihydropyran, chlorinated, methylated by methylmagnesium chloride (collapsed)
        hydrogenation("catalysts/nickel").in("furfural").in("hydrogen", 2).in("chlorine").in("methyl_chloride").in("magnesium")
            .out("chloro_methyl_tetrahydropyran").out("magnesium_chloride").add("chloro_methyl_tetrahydropyran");
        // lanolin acids saponified, methylated and cut, the iso-C14 ester hydrolysed (not confirmed)
        m14().in("lanolin", 2).in("sodium_hydroxide").in("sulfuric_acid").in("methanol").out("methyltridecanoic_acid").src(30)
            .add("methyltridecanoic_acid");
        // Michael addition, then one ester group removed with brine (Krapcho)
        m14().in("diethyl_malonate").in("ethyl_crotonate").in("sodium_methoxide").in("salt").in("water").out("diethyl_methylglutarate")
            .add("diethyl_methylglutarate");
        // Stetter addition on the thiazolium catalyst
        m14().in("heptanal").in("methyl_vinyl_ketone").cat("chemicals/thiamine_hydrochloride", ENZYME_RUNS).out("undecanedione")
            .add("undecanedione");
        m14().in("cis_4_heptenal").in("methyl_vinyl_ketone").cat("chemicals/thiamine_hydrochloride", ENZYME_RUNS).out("undecenedione")
            .add("undecenedione");
        // diesters hydrogenated to the diols on copper and chlorinated
        hydrogenation("catalysts/copper").in("sebacic_acid").in("methanol", 2).in("hydrogen", 4).in("hydrogen_chloride", 2)
            .out("dichlorodecane").add("dichlorodecane");
        hydrogenation("catalysts/copper").in("suberic_acid").in("methanol", 2).in("hydrogen", 4).in("hydrogen_chloride", 2)
            .out("dichlorooctane").add("dichlorooctane");
        hydrogenation("catalysts/copper").in("suberic_acid").in("methanol", 2).in("hydrogen", 4).in("hydrochloric_acid")
            .out("chlorooctanol").add("chlorooctanol");
        m14().in("ethyl_acetoacetate", 2).in("dichlorodecane").in("sodium_methoxide", 2).out("hexadecanedione").add("hexadecanedione");
        // crossed aldols hydrogenated (fully on nickel, the C=C only on palladium)
        hydrogenation("catalysts/nickel").in("butyraldehyde").in("propionaldehyde").in("hydrogen", 2).out("methylhexanol").add("methylhexanol");
        hydrogenation("catalysts/nickel").in("valeraldehyde").in("propionaldehyde").in("hydrogen", 2).out("methylheptanol")
            .add("methylheptanol");
        hydrogenation("catalysts/palladium").in("isobutyraldehyde").in("acetaldehyde").in("hydrogen").out("methylpentanal").add("methylpentanal");
        hydrogenation("catalysts/palladium").in("cyclopentanone").in("undecanal").in("hydrogen").out("undecylcyclopentanone")
            .add("undecylcyclopentanone");
        hydrogenation("catalysts/palladium").in("cyclohexanone").in("hexanal").in("hydrogen").out("hexylcyclohexanone")
            .add("hexylcyclohexanone");
        // not confirmed (19 §2): cyclopentanone with 2-pentenal, the C=C of the side chain kept (game simplification)
        hydrogenation("catalysts/palladium").in("cyclopentanone").in("trans_2_pentenal").in("hydrogen").out("pentenylcyclopentanone")
            .add("pentenylcyclopentanone");
        // allyl acetate hydrolysed on resin, the alcohol distilled off as it forms
        m14().in("allyl_acetate").in("water").cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("allyl_alcohol").outMb("acetic_acid", 800)
            .add("allyl_alcohol");
        // anhydride exchange with acetic anhydride, acetic acid distilled off
        m14().in("isobutyric_acid", 2).in("acetic_anhydride").out("isobutyric_anhydride").out("acetic_acid").add("isobutyric_anhydride");
        m14().in("hexanoic_acid", 2).in("acetic_anhydride").out("hexanoic_anhydride").out("acetic_acid").add("hexanoic_anhydride");
        m14().in("benzoic_acid", 2).in("acetic_anhydride").out("benzoic_anhydride").out("acetic_acid").add("benzoic_anhydride");
        // propargyl alcohol (acetylene and formaldehyde) coupled with allyl chloride, half-hydrogenated, chlorinated (not confirmed)
        hydrogenation("catalysts/palladium").in("acetylene").in("formaldehyde").in("allyl_chloride").in("hydrogen").in("hydrochloric_acid")
            .out("z_hexadienyl_chloride").add("z_hexadienyl_chloride");
        // menthofuran photo-oxidized, the hydroperoxide reduced with sodium sulfite (ST42; S1152 without the sulfite)
        m14cold().method("ultraviolet").in("menthofuran").in("oxygen").in("sodium_sulfite").out("hydroxymenthofurolactone")
            .add("hydroxymenthofurolactone");
        // aldehydes oxidized by air (P44)
        m14air().in("trans_2_nonenal").out("trans_2_nonenoic_acid").add("trans_2_nonenoic_acid");
        m14air().in("trans_2_cis_6_nonadienal").out("nonadienoic_acid").add("nonadienoic_acid");
        m14air().in("trans_trans_2_4_decadienal").out("decadienoic_acid").add("decadienoic_acid");
        m14air().in("piperonal").out("piperonylic_acid").add("piperonylic_acid");
        m27("cracking").in("dimethyldioxane").out("isoprene").outMb("formaldehyde", 800).add("isoprene");
        m27("gas_catalysis").in("propene").in("oxygen").cat("catalysts/bismuth_molybdate", 64).out("acrolein").add("acrolein");
        m27("gas_catalysis").in("propene").in("chlorine").temp("high").out("allyl_chloride").out("hydrogen_chloride").add("allyl_chloride");
        m14().in("prenol").in("hydrochloric_acid").in("sodium_methoxide").out("prenyl_methyl_ether").out("salt").add("prenyl_methyl_ether");
        m14acid().in("acetoin").in("thionyl_chloride").out("chlorobutanone").add("chlorobutanone");
        m14().in("ethyl_chloride").in("sodium_nitrite").in("dimethylformamide").out("nitroethane").out("salt").add("nitroethane");
        // Prins condensation of isobutylene and formaldehyde in water, hot and at high pressure without acid (as 572);
        // with sulfuric acid in the stirred reactor it gives 4,4-dimethyl-1,3-dioxane instead (18 §1.5)
        m23(null).in("isobutylene").in("formaldehyde").in("water").pressure("high").temp("high").out("isoprenol").add("isoprenol");
        // the aldol of acetone and formaldehyde is 4-hydroxy-2-butanone (its own recipe); dehydrated with acid
        m14().in("hydroxybutanone").in("sulfuric_acid").out("methyl_vinyl_ketone").add("methyl_vinyl_ketone");
        m27("gas_catalysis").in("isobutyric_acid").in("acetic_acid").cat("chemicals/manganese_dioxide", ENZYME_RUNS).out("methyl_isopropyl_ketone")
            .outMb("carbon_dioxide", 1000).add("methyl_isopropyl_ketone");
        m23(null).in("propene", 2).cat("catalysts/nickel", 64).out("methylpentene").add("methylpentene");
        m27("gas_catalysis").in("furfural").cat("catalysts/palladium", 64).out("furan").out("carbon_monoxide").add("furan");
        // gem-dichloride (or vicinal) and sodium amide: double dehydrochlorination
        m14().in("heptanal").in("phosphorus_trichloride").in("chlorine").in("sodium", 2).in("ammonia", 2).out("heptyne").add("heptyne");
        m14().in("octene").in("chlorine").in("sodium", 2).in("ammonia", 2).out("octyne").add("octyne");
        m27("cracking").in("acetone").in("ketene").out("acetylacetone").add("acetylacetone");
        m14().in("isobutylene", 2).cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("diisobutylene").add("diisobutylene");
        x(KILN).in("glutaric_acid").out("glutaric_anhydride").mode("condensation").add("glutaric_anhydride");
        m27("cracking").in("acetaldehyde_diethyl_acetal").out("ethyl_vinyl_ether").out("ethanol").add("ethyl_vinyl_ether");
        // menthyl chloride, Grignard reagent carbonated
        m14inert().in("menthol").in("hydrogen_chloride").in("magnesium").in("carbon_dioxide").out("menthanecarboxylic_acid")
            .out("magnesium_chloride").add("menthanecarboxylic_acid");
        m14().in("glycerol").in("hydrogen_chloride", 2).in("sodium_hydroxide").out("epichlorohydrin").out("salt").add("epichlorohydrin");
        m14acid().in("butene").in("chlorine").out("dichlorobutane").add("dichlorobutane");
        m27("gas_catalysis").in("cyclohexanol").in("hydrogen_sulfide").cat("catalysts/alumina", 64).out("cyclohexanethiol").add("cyclohexanethiol");
        m14().in("castor_oil").in("nitric_acid", 2).out("suberic_acid").add("suberic_acid");
        m14().in("cyclopentanone").in("morpholine").in("cis_3_hexenyl_chloride").out("hexenylcyclopentanone").outMb("morpholine", 800)
            .add("hexenylcyclopentanone");
        // allylic bromination, dehydrobromination
        m14().in("dihydro_beta_ionone").in("bromine").out("dehydrodihydroionone").out("hydrobromic_acid").add("dehydrodihydroionone");
        // 4-chlorobutyrate closed with methoxide; the amide degraded (Hofmann)
        m14().in("gamma_butyrolactone").in("hydrogen_chloride").in("methanol").in("sodium_methoxide").in("ammonia").in("sodium_hypochlorite")
            .out("cyclopropylamine").add("cyclopropylamine");
        m14().in("gamma_butyrolactone").in("hydrogen_chloride").in("methanol").in("sodium_methoxide").in("sodium_hydroxide")
            .out("cyclopropanecarboxylic_acid").add("cyclopropanecarboxylic_acid");
        hydrogenation("catalysts/nickel").in("benzene").in("adipic_acid").in("hydrogen", 4).out("cyclohexylhexanoic_acid")
            .add("cyclohexylhexanoic_acid");
        hydrogenation("catalysts/nickel").in("cinnamaldehyde").in("acetic_anhydride").in("sodium_acetate").in("hydrogen", 4)
            .out("cyclohexylvaleric_acid").add("cyclohexylvaleric_acid");
        m14().in("heptene_1").in("hydrogen_peroxide").in("water").out("heptanediol").add("heptanediol");
        m14().in("glycine").in("ethanol").in("hydrogen_chloride").out("glycine_ethyl_ester").add("glycine_ethyl_ester");
        // the dichloride (batch 3) taken back by molten sulfur to the monochloride, P59 (11 问题 41: sulfur and chlorine make the dichloride)
        m14acid().in("sulfur").in("sulfur_dichloride").out("disulfur_dichloride").add("disulfur_dichloride");
        // propargyl alcohol dianion alkylated (1-chloropropane from n-propanol, collapsed; game simplification)
        m14cold().inert().in("acetylene").in("formaldehyde").in("n_propanol").in("hydrogen_chloride").in("sodium", 2).out("hexynol")
            .add("hexynol");
        m14cold().method("corrosion_resistant").in("propionaldehyde").in("chlorine").out("chloropropanal").out("hydrogen_chloride")
            .add("chloropropanal");
        m27("cracking").in("ethylene_dichloride").out("vinyl_chloride").out("hydrogen_chloride").add("vinyl_chloride");
        // allylmagnesium chloride coupled with 1,4-dichlorobutane (from the lactone, hydrogenated and chlorinated)
        hydrogenation("catalysts/nickel").in("gamma_butyrolactone").in("hydrogen", 2).in("hydrogen_chloride", 2).in("allyl_chloride", 2)
            .in("magnesium", 2).out("decadiene").out("magnesium_chloride", 2).add("decadiene");
        m14inert().in("isobutanol").in("hydrogen_chloride").in("magnesium").in("allyl_chloride").out("methylhexene").out("magnesium_chloride")
            .add("methylhexene");
        m14inert().in("active_amyl_alcohol").in("hydrogen_chloride").in("magnesium").in("allyl_chloride").out("methylheptene")
            .out("magnesium_chloride").add("methylheptene");
        m14acid().in("acetic_acid").in("bromine").in("phosphorus_trichloride").in("ethanol").out("ethyl_bromoacetate").out("hydrobromic_acid")
            .add("ethyl_bromoacetate");
        // 2-methyl-2-butene from isoamyl alcohol, Prins condensation under pressure, double bond moved (game simplification)
        m23(null).in("isoamyl_alcohol").in("formaldehyde").out("methylprenol").add("methylprenol");
        m14().in("methylprenol").in("prenal").out("methyl_citral").add("methyl_citral");
        m14inert().in("allyl_chloride").in("magnesium").in("formaldehyde").out("butenol_3").out("magnesium_chloride").add("butenol_3");
        hydrogenation("catalysts/nickel").in("furfural").in("hydrogen", 3).in("hydrogen_chloride").in("sodium", 2).out("pentenol_4")
            .add("pentenol_4");
        m27("gas_catalysis").in("n_heptanol").cat("catalysts/alumina", 64).out("heptene_1").add("heptene_1");
    }

    // ---- intermediates of 19 §2: aromatic ----

    private void aromatic() {
        m27("gas_catalysis").in("p_cresol").in("methanol").cat("catalysts/alumina", 64).out("xylenol_2_4").add("xylenol_2_4");
        // phthalamic acid degraded with hypochlorite (Hofmann), acidified
        m14().in("phthalic_anhydride").in("ammonia").in("sodium_hydroxide").in("sodium_hypochlorite").in("hydrochloric_acid")
            .out("anthranilic_acid").out("salt").add("anthranilic_acid");
        // mixed-acid nitration and hydrogenation: one nitro group gives p-toluidine, two the diamines (ratio, 18 §1.1)
        // toluene nitrated once (the para isomer crystallized out) or twice, the nitro groups hydrogenated (18 §1.5)
        m14acid().in("toluene").in("nitric_acid").in("sulfuric_acid").out("nitrotoluene").add("nitrotoluene");
        m14acid().in("nitrotoluene").in("nitric_acid").in("sulfuric_acid").out("dinitrotoluene").add("dinitrotoluene");
        hydrogenation("catalysts/nickel").in("nitrotoluene").in("hydrogen", 3).out("p_toluidine").add("p_toluidine");
        hydrogenation("catalysts/nickel").in("dinitrotoluene").in("hydrogen", 6).out("toluene_2_3_diamine").add("toluene_2_3_diamine");
        // diazotized and reduced with sulfite
        m14cold().in("aniline").in("sodium_nitrite").in("hydrochloric_acid", 2).in("sodium_sulfite").out("phenylhydrazine")
            .add("phenylhydrazine");
        // o-nitroethylbenzene oxidized by air under pressure on cobalt
        m23("pressure_oxidation").in("ethylbenzene").in("nitric_acid").in("sulfuric_acid").in("oxygen").cat("catalysts/cobalt", 64)
            .out("o_nitroacetophenone").add("o_nitroacetophenone");
        hydrogenation("catalysts/nickel").in("vanillin").in("ammonia").in("hydrogen").out("vanillylamine").add("vanillylamine");
        // cut off the cyclocitral of S0715 (citral anil cyclized in sulfuric acid) in the column, as 19 says (11 问题 41)
        column().in("cyclocitral").outMb("alpha_cyclocitral", 400).outDefaultMb("cyclocitral", 500).add("alpha_cyclocitral");
        m14().in("veratraldehyde").in("acetic_anhydride").in("sodium_acetate").out("dimethoxycinnamic_acid").out("acetic_acid")
            .add("dimethoxycinnamic_acid");
        hydrogenation("catalysts/nickel").in("dimethoxycinnamic_acid").in("hydrogen").in("ammonia").in("sodium_hypochlorite")
            .out("homoveratrylamine").add("homoveratrylamine");
        m14().in("phenol").in("chloroacetic_acid").in("sodium_hydroxide", 2).out("phenoxyacetic_acid").out("salt").add("phenoxyacetic_acid");
        hydrogenation("catalysts/nickel").in("methylsalicylaldehyde").in("methyl_chloride").in("ammonia").in("hydrogen")
            .out("methoxymethylbenzylamine").add("methoxymethylbenzylamine");
        m14().in("m_dimethoxybenzene").in("formaldehyde").in("hydrogen_chloride").in("ammonia", 2).out("dimethoxybenzylamine")
            .add("dimethoxybenzylamine");
        m14().in("benzyl_chloride").in("hydrogen_cyanide").in("sodium_hydroxide").out("benzyl_cyanide").out("salt").add("benzyl_cyanide");
        hydrogenation("catalysts/palladium").in("benzyl_cyanide").in("nitric_acid").in("sulfuric_acid").in("hydrogen", 3)
            .out("aminophenylacetonitrile").add("aminophenylacetonitrile");
        hydrogenation("catalysts/nickel").in("p_nitrochlorobenzene").in("sodium_methoxide").in("methanol").in("hydrogen", 3)
            .out("p_anisidine").add("p_anisidine");
        // geranyl chloride aminated with a large excess of ammonia
        m23(null).in("geraniol").in("phosphorus_trichloride").in("ammonia", 3).out("geranylamine").add("geranylamine");
        hydrogenation("catalysts/nickel").in("menthone").in("ammonia").in("hydrogen").out("menthylamine").add("menthylamine");
        hydrogenation("catalysts/nickel").in("dipropyl_ketone").in("ammonia").in("hydrogen").out("heptan_4_amine").add("heptan_4_amine");
        hydrogenation("catalysts/nickel").in("isobutanol").in("ammonia").in("hydrogen").out("isobutylamine").add("isobutylamine");
        hydrogenation("catalysts/nickel").in("acetone").in("ammonia").in("hydrogen").out("isopropylamine").add("isopropylamine");
        // GBL from maleic anhydride, opened with hydrobromic acid in ethanol, quaternized with triphenylphosphine
        hydrogenation("catalysts/nickel").in("maleic_anhydride").in("hydrogen", 2).in("ethanol").in("hydrobromic_acid").in("triphenylphosphine")
            .out("carbethoxypropyl_tpp_bromide").add("carbethoxypropyl_tpp_bromide");
        m14().in("furan").in("succinic_anhydride").in("hydrazine_hydrate").in("potassium_hydroxide").cat("catalysts/zinc_chloride", 64)
            .out("furylbutyric_acid").add("furylbutyric_acid");
        m14().in("furfural").in("malonic_acid").out("furylacrylic_acid").out("carbon_dioxide").add("furylacrylic_acid");
        hydrogenation("catalysts/palladium").in("furylacrylic_acid").in("hydrogen").out("furylpropionic_acid").add("furylpropionic_acid");
        m14acid().in("ethylbenzene").in("sulfuric_acid").in("sodium_hydroxide").out("sodium_ethylbenzenesulfonate")
            .add("sodium_ethylbenzenesulfonate");
        // fructose dehydrated on an acid resin, extracted
        m14().in("fructose", 2).in("water").cat("catalysts/ion_exchange_resin", ENZYME_RUNS).out("hydroxymethylfurfural").add("hydroxymethylfurfural");
        m14().in("shellac", 2).in("sodium_hydroxide").in("sulfuric_acid").out("aleuritic_acid").add("aleuritic_acid");
    }

    // ---- intermediates of 19 §2: nitrogen and sulfur ----

    private void nitrogenSulfur() {
        m27("gas_catalysis").in("methane").in("sulfur", 4).cat("catalysts/alumina", 64).out("carbon_disulfide").out("hydrogen_sulfide", 2)
            .add("carbon_disulfide");
        m27("gas_catalysis").in("pyrazine").in("chlorine").out("chloropyrazine").out("hydrogen_chloride").add("chloropyrazine");
        m27("gas_catalysis").in("methylpyrazine").in("chlorine").out("chloromethylpyrazine").out("hydrogen_chloride").add("chloromethylpyrazine");
        // Raschig: ammonia and hypochlorite
        m14().in("ammonia", 2).in("sodium_hypochlorite").out("hydrazine_hydrate").out("salt").add("hydrazine_hydrate");
        hydrogenation("catalysts/nickel").in("acrylonitrile").in("methyl_mercaptan").in("ammonia").in("hydrogen", 2).out("methylthiopropylamine")
            .add("methylthiopropylamine");
        // ethylene oxide with a large excess of aqueous ammonia (the diethanolamine recipe of I30 uses a little)
        m23("ethoxylation").in("ethylene_oxide").in("ammonia", 3).in("water").out("ethanolamine").outMb("diethanolamine", 200).add("ethanolamine");
        // Pinner reactions, cold
        m14cold().in("hydrogen_cyanide").in("methanol", 3).in("hydrogen_chloride").out("trimethyl_orthoformate").out("ammonium_chloride")
            .add("trimethyl_orthoformate");
        m14cold().in("acetonitrile").in("methanol", 3).in("hydrogen_chloride").out("trimethyl_orthoacetate").out("ammonium_chloride")
            .add("trimethyl_orthoacetate");
        m14cold().in("acetonitrile").in("ethanol").in("hydrogen_chloride").in("ammonia").out("acetamidine_hydrochloride")
            .add("acetamidine_hydrochloride");
        m14acid().in("ethyl_acetoacetate").in("phosphorus_trichloride").in("chlorine").out("ethyl_chlorocrotonate").add("ethyl_chlorocrotonate");
        m27("gas_catalysis").in("isobutylene").in("chlorine").temp("high").out("methallyl_chloride").out("hydrogen_chloride")
            .add("methallyl_chloride");
        m14().in("acetaldehyde").in("chlorine").in("water").out("chloroacetaldehyde").out("hydrochloric_acid").add("chloroacetaldehyde");
        // formamide sulfurized with phosphorus pentasulfide (white phosphorus and sulfur fused), used at once
        m14().in("formic_acid").in("ammonia").in("white_phosphorus").in("sulfur", 2).out("thioformamide").add("thioformamide");
        m14().in("carbon_disulfide").in("ammonia", 2).out("thiourea").out("hydrogen_sulfide").add("thiourea");
        m23(null).in("ethylene", 2).in("chlorine").in("ammonia", 2).cat("catalysts/nickel", 64).out("butanediamine").add("butanediamine");
        m14().in("methylpyrazine").in("formaldehyde").out("vinylpyrazine").add("vinylpyrazine");
        m23(null).in("propionaldehyde").in("acetaldehyde").in("ammonia", 2).in("formaldehyde").out("methylpyridylethylamine")
            .add("methylpyridylethylamine");
        m14().in("alpha_picoline").in("formaldehyde").in("ammonia").out("pyridylethylamine").add("pyridylethylamine");
        m14().in("alpha_picoline").in("formaldehyde").in("sodium_hydroxide").out("vinylpyridine").add("vinylpyridine");
        m14().in("ethanolamine").in("sulfuric_acid").in("carbon_disulfide").out("cysteamine").add("cysteamine");
        m14().in("acetic_anhydride").in("hydrogen_sulfide").out("thioacetic_acid").out("acetic_acid").add("thioacetic_acid");
        m14().in("dl_valine").in("glycine").out("valylglycine").add("valylglycine");
        // chloro-olefins aminated with a large excess of ammonia
        m23(null).in("butenol_3").in("hydrogen_chloride").in("ammonia", 3).out("butenylamine").add("butenylamine");
        m23(null).in("pentenol_4").in("hydrogen_chloride").in("ammonia", 3).out("pentenylamine").add("pentenylamine");
        m23(null).in("dibromohexane").in("potassium_hydroxide").in("ammonia", 3).out("hexenylamine").add("hexenylamine");
        // methanethiol added anti-Markovnikov (radical, the initiator left out)
        m14().in("pentenylamine").in("methyl_mercaptan").out("methylthiopentylamine").add("methylthiopentylamine");
        m14().in("hexenylamine").in("methyl_mercaptan").out("methylthiohexylamine").add("methylthiohexylamine");
        // not confirmed (19 §2): 3-butyn-1-ol and (Z)-1-chloro-2-pentene coupled (collapsed)
        hydrogenation("catalysts/palladium").in("acetylene", 2).in("ethylene_oxide").in("ethyl_chloride").in("formaldehyde").in("hydrogen")
            .out("nonenynol").add("nonenynol");
        // magnesium from its chloride by molten-salt electrolysis
        x(ELECTROLYZING).in("magnesium_chloride").out("magnesium").out("chlorine").mode("molten_salt").add("magnesium");
    }

    // ---- intermediates of 18 §5 and 19 §2 from natural sources ----

    private void natural() {
        // the tallow tree kernels pressed (the tallow sieved off before)
        x(PRESSING).in("materials/tallow_tree_seed", 8).outMb("stillingia_oil", 250).add("stillingia_oil");
        // rapeseed oil saponified for erucic acid, split by peroxide on tungstate (collapsed)
        m14().in("materials/rapeseed", 4).in("sodium_hydroxide").in("hydrogen_peroxide").cat("catalysts/tungstate", 64).out("brassylic_acid")
            .outMb("nonanoic_acid", 500).add("brassylic_acid");
        // N108 straw extracted with hexane, sclareol frozen out
        x(EXTRACTING).in("materials/clary_sage_herb", 4).in("n_hexane").out("sclareol").outMb("n_hexane", 750).mode("solvent_extraction")
            .add("sclareol");
        // young bitter oranges extracted in ethanol over macroporous resin
        m14().in("materials/bitter_orange", 4).in("ethanol").cat("catalysts/macroporous_resin", ENZYME_RUNS).out("neohesperidin")
            .outMb("ethanol", 750).add("neohesperidin");
        x(FERMENTING).in("glucose", 2).in("water", 2).cat("cultures/lasiodiplodia", CULTURE_RUNS, PURE).out("jasmonic_acid").sterile()
            .mode("aerated").add("jasmonic_acid");
        x(EXTRACTING).in("materials/yerba_santa", 4).in("ethyl_acetate").out("homoeriodictyol").outMb("ethyl_acetate", 750)
            .mode("solvent_extraction").add("homoeriodictyol");
        m23(null).in("refined_soybean_oil").in("water").temp("high").out("soybean_fatty_acids").outMb("glycerol", 100).add("soybean_fatty_acids");
        x(FERMENTING).in("materials/milk", 4).in("citric_acid").cat("cultures/lactococcus", CULTURE_RUNS).out("starter_culture_broth")
            .mode("sealed").add("starter_culture_broth");
        // 18 §5: the six intermediates of the natural flavours (crude fusel oil comes off the batch 3 column, 128)
        x(EXTRACTING).in("materials/jasmine_officinale_flower", 4).in("n_hexane").out("common_jasmine_concrete").outMb("n_hexane", 750)
            .mode("solvent_extraction").add("common_jasmine_concrete");
        x(DISTILLING).in("materials/camphor_wood", 8).in("water").outMb("crude_camphor_oil", 100).mode("steam_distillation")
            .method("oil_separation").add("crude_camphor_oil");
        // cane pressed and boiled down, the sugar crystallized out of it, the mother liquor kept (game simplification)
        x(CRYSTALLIZING).in("materials/sugar_cane", 8).in("water").out("sucrose", 2).outMb("cane_molasses", 250).temp("heated").add("cane_molasses");
        // peel or buds steeped in lime water, the extract acidified and crystallized
        x(EXTRACTING).in("materials/citrus_peel", 4).in("slaked_lime").in("water").in("hydrochloric_acid").out("hesperidin").add("hesperidin");
        // N182: bitter almonds steam-distilled, the hydrocyanic acid taken out of the oil with lime and green vitriol (NT12)
        x(DISTILLING).in("materials/bitter_almond", 8).in("water").outMb("crude_bitter_almond_oil", 100).mode("steam_distillation")
            .method("oil_separation").add("crude_bitter_almond_oil");
        x(SEPARATING).in("crude_bitter_almond_oil").in("slaked_lime").in("ferrous_sulfate").in("water").outMb("bitter_almond_oil", 900)
            .add("bitter_almond_oil");
        // the bifunctional catalyst of S1132: palladium put on the acid resin
        x(REACTING).in("catalysts/noble_metal").in("catalysts/ion_exchange_resin").in("water").out("palladium_resin", 2).temp("heated")
            .tier(2).add("palladium_resin");
        x(EXTRACTING).in("materials/sophora_flower", 4).in("slaked_lime").in("water").in("hydrochloric_acid").out("rutin").temp("heated")
            .add("rutin");
    }

    // ---- variants of flavour rows written as prose (19 §1) ----

    private void variants() {
        // S0101: the sulfonate fused with caustic soda to the phenolate, acidified in water in the stirred reactor (11 问题 42)
        x(KILN).in("sodium_ethylbenzenesulfonate").in("sodium_hydroxide", 2).out("sodium_p_ethylphenolate").out("sodium_sulfite")
            .mode("alkali_fusion").add("sodium_p_ethylphenolate");
        m14().in("sodium_p_ethylphenolate").in("water").in("hydrochloric_acid").out("p_ethylphenol").out("salt").add("p_ethylphenol");
        // S0015's still residue, disodium sebacate, acidified to sebacic acid (11 问题 42)
        m14().in("disodium_sebacate").in("water").in("sulfuric_acid").out("sebacic_acid").out("sodium_sulfate").add("sebacic_acid");
        // S0233 B: 6-methylcitral and acetone to pseudoirone, cyclized with sulfuric acid
        m14().in("methyl_citral").in("acetone").in("sulfuric_acid").out("alpha_irone").add("alpha_irone_b");
        // S0316: benzyl chloride carbonylated in caustic soda on cobalt, acidified
        m23("carbonylation").in("benzyl_chloride").in("carbon_monoxide").in("sodium_hydroxide", 2).in("hydrochloric_acid").cat("catalysts/cobalt", 64)
            .out("phenylacetic_acid").out("salt", 2).add("phenylacetic_acid");
        // S0445 B: tiglate isomerized on resin, the angelate (about a tenth) taken off (reactive distillation, game simplification)
        m14().in("isobutyl_tiglate").cat("catalysts/ion_exchange_resin", ENZYME_RUNS).outMb("isobutyl_angelate", 100).outMb("isobutyl_tiglate", 850)
            .add("isobutyl_angelate_b");
        // S0719, S0931: single isomers cut from the isomer mixtures on the column, the rest back
        column().in("ethyl_dimethylpyrazine_isomers").outMb("ethyl_dimethylpyrazine_3_2_6", 300).outDefaultMb("ethyl_dimethylpyrazine_isomers", 600)
            .add("ethyl_dimethylpyrazine_3_2_6");
        column().in("alpha_damascone").outMb("trans_alpha_damascone", 400).outDefaultMb("alpha_damascone", 500).add("trans_alpha_damascone");
        // S0880: urea inclusion of the soybean fatty acids frozen out of methanol (P102)
        x(CRYSTALLIZING).in("soybean_fatty_acids").in("urea").in("methanol").outMb("linoleic_acid", 500).outMb("methanol", 750)
            .mode("freezing").add("linoleic_acid");
        // S0955: Eucalyptus dives leaves steam-distilled (NT1 + ST13)
        x(DISTILLING).in("materials/eucalyptus_dives", 8).in("water").outMb("r_piperitone", 100).mode("steam_distillation")
            .method("oil_separation").add("r_piperitone");
        // S1081 A, S1087 A: wasabi macerated and steam-distilled (ST53 B), the isothiocyanates taken together (S1088 by its B route)
        x(DISTILLING).in("materials/wasabi", 8).in("water").outMb("hexenyl_isothiocyanate", 50).outMb("methylthiohexyl_isothiocyanate", 30)
            .mode("steam_distillation").method("oil_separation").add("wasabi_isothiocyanates");
        // S1455: 4-acetoxybutanal and the hexyl ylide in one charge (Wittig, Z-selective)
        m14inert().in("hydroxybutanal").in("acetic_anhydride").in("hexyl_chloride").in("triphenylphosphine").in("sodium_methoxide")
            .out("cis_4_decenyl_acetate").out("triphenylphosphine_oxide").out("acetic_acid").add("cis_4_decenyl_acetate");
    }

    /**
     * ST51 and S0095 A, S0949 (18 §1.4: R10 on the column, R04 the washes): the wash oil and quinoline bases are
     * cut with the coal tar fractions (Batch1 coal_tar_rectifying), indole frozen out of the wash oil, tar acids
     * washed out with caustic lye and pyridine bases with dilute sulfuric acid, isoquinoline crystallized from
     * quinoline as its sulfate (game simplification: no separate fraction materials).
     */
    /** S1418: the aqueous condensate of the wood dry distillation settled free of tar and washed (NT11 沉降、精制). */
    private void smoke() {
        r(SEPARATING).fin("wood_vinegar", B).fin("water", B).fout("smokez_c_10", B).add("smokez_c_10");
        // N267: the tar settles out on standing, the carbon takes up the polycyclic aromatics (P24; tar share not confirmed)
        r(REACTING).fin("wood_vinegar", B).cat("catalysts/activated_carbon", 4).fout("refined_wood_vinegar", 900).fout("wood_tar", 100)
            .tier(2).add("refined_wood_vinegar");
    }

    /**
     * S0224 B: the former 133 B route, limonene and nitrosyl chloride, oxime hydrolysed (collapsed, 11 问题 32); the
     * nitrosyl chloride made fresh from sodium nitrite and hydrochloric acid (toxic, reagent only, 18 §1.5).
     */
    private void carvone() {
        m14cold().in("sodium_nitrite").in("hydrochloric_acid", 2).out("nitrosyl_chloride").out("salt").add("nitrosyl_chloride");
        m14cold().in("limonene").in("nitrosyl_chloride").out("l_carvone").add("l_carvone_from_limonene");
    }

    private void coalTar() {
        // 2-methylnaphthalene (469, I31 §C) freezes out of the same wash oil
        r(CRYSTALLIZING).fin("methylnaphthalene_1", B).out("indole", 1).src().out("methylnaphthalene", 1).src().fout("methylnaphthalene_1", 750)
            .temp("cooling").add("indole_from_wash_oil");
        r(EXTRACTING).fin("coal_tar", B).fin("soda_lye", B).out("o_cresol", 1).src().out("dimethylphenol_3_4", 1).src()
            .mode("liquid_extraction").add("coal_tar_cresols");
        r(EXTRACTING).fin("coal_tar", B).fin("sulfuric_acid", B).fout("pyridine", 150).src().fout("dimethylpyridine_2_6", 100).src()
            .mode("liquid_extraction").add("coal_tar_pyridine_bases");
        r(CRYSTALLIZING).fin("quinoline", B).fin("sulfuric_acid", B).out("isoquinoline", 1).src().fout("quinoline", 750).temp("cooling")
            .add("isoquinoline");
    }
}
