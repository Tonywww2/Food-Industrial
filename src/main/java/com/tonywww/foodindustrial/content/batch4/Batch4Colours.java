package com.tonywww.foodindustrial.content.batch4;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.content.ContentRecipe;
import com.tonywww.foodindustrial.recipe.RecipeGen;

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
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SEPARATING;

/**
 * The fourth content batch, group B (I30, additives/15 §B): colours 194–243 and their aluminium lakes.
 * Synthetic colours continue the aromatic chain of I20; natural colours run on the two extraction lines
 * (P89 water-soluble, P90 oil-soluble). Routes the earlier batches already make (197, 203, 235, 236 A,
 * 238) are not written again.
 */
public final class Batch4Colours extends Batch {
    /** P89 options: acidified (citric acid), hot, macroporous resin, spray dried. */
    private static final int ACID = 1;
    private static final int HOT = 2;
    private static final int RESIN = 4;
    private static final int DRY = 8;

    private Batch4Colours(RecipeGen.Output out) {
        super(out, "batch4");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch4Colours(out).generate());
    }

    private void generate() {
        byproducts();
        synthetic();
        titaniumDioxide();
        ironOxides();
        carotenoids();
        waterSoluble();
        oilSoluble();
        others();
    }

    // ---- the raw by-products with their main products (11 问题 43, 2026-11-12: one recipe gives both) ----

    private void byproducts() {
        // 226, 231: grapes and citrus pressed on the screw press, the juice running off as a fluid with the pomace or peel
        // (2026-11-12, user: the juice and the peanut oil come out as fluids, so the T1 press makes them; 4 → 3 + 1 and 4 → 2 + 1
        // in quarters of a bucket)
        r(PRESSING).in("materials/grape", 4).fout("raw_grape_juice", 750).out("grape_pomace", 1).add("grape_pomace");
        r(PRESSING).in("materials/citrus", 4).fout("raw_citrus_juice", 500).out("citrus_peel", 1).add("citrus_peel");
        // cocoa beans roasted (merged) and winnowed: nibs and shell; peanuts pressed, the skins rubbed off first (merged)
        r(GRINDING).in("materials/cocoa_beans", 4).out("cocoa_nibs", 3).out("cocoa_shell", 1).add("cocoa_shell");
        r(PRESSING).in("materials/peanut", 4).fout("crude_peanut_oil", 500).out("peanut_skin", 1).add("peanut_skin");
        // the finished juices and oil (12) bottled from the pressed fluids, a bottle a quarter bucket (pending review)
        bottle("raw_grape_juice", "grape_juice");
        bottle("raw_citrus_juice", "citrus_juice");
        bottle("crude_peanut_oil", "peanut_oil");
        // 210: red rice milled white, the bran kept
        r(GRINDING).in("materials/red_rice", 4).out("rice", 3).out("red_rice_bran", 1).add("red_rice_bran");
    }

    private void bottle(String fluid, String product) {
        r(MIXING).fin(fluid, 250).in("materials/glass_bottle", 1).out(product, 1).mode("blending").add(product + "_bottling");
    }

    // ---- 194–196, 199, 202 synthetic colours ----

    private void synthetic() {
        // 194 / 81: 2-naphthol disulfonated to the R and G acid mixture, G acid salted out with potassium sulfate, R acid
        // with salt cake (replaces the G acid recipe of I20, 2026-11-11, user)
        r(REACTING).in("chemicals/beta_naphthol", 2).in("chemicals/potassium_sulfate", 1).in("chemicals/sodium_sulfate", 1)
            .fin("sulfuric_acid", 4 * B).out("g_acid", 1).out("r_acid", 1).temp("heated").add("r_and_g_acid");
        r(REACTING).in("chemicals/r_acid", 1).in("chemicals/sodium_hydroxide", 3).fin("naphthionic_diazonium", B).out("amaranth", 1)
            .add("amaranth");
        // 199: naphthylamine baked with sulfuric acid and turned into the naphthol by the Bucherer reaction (P91);
        // coupled with the naphthionic diazonium
        r(REACTING).in("chemicals/naphthylamine", 1).fin("sulfuric_acid", B).fin("sodium_bisulfite", 200).out("nevile_winther_acid", 1)
            .fout("ammonia", B).temp("heated").add("nevile_winther_acid");
        r(REACTING).in("chemicals/nevile_winther_acid", 1).in("chemicals/sodium_hydroxide", 2).fin("naphthionic_diazonium", B)
            .out("azorubine", 1).add("azorubine");
        // 196: naphthalene trisulfonated, nitrated, reduced to Koch acid, fused under pressure to H acid, acetylated;
        // coupled with the sulfanilic diazonium
        // sulfonated on from the monosulfonic acid (11 问题 41: the later step from the earlier)
        r(REACTING).in("chemicals/naphthalene_sulfonic_acid", 1).fin("sulfuric_acid", 2 * B).fout("naphthalene_trisulfonic_acid", B)
            .temp("high").add("naphthalene_trisulfonic_acid");
        r(REACTING).fin("naphthalene_trisulfonic_acid", B).fin("nitric_acid", B).fout("nitronaphthalene_trisulfonic_acid", B)
            .temp("cooling").method("corrosion_resistant").add("nitronaphthalene_trisulfonic_acid");
        r(REACTING).fin("nitronaphthalene_trisulfonic_acid", B).fin("hydrogen", 3 * B).out("koch_acid", 1).mode("hydrogenation")
            .pressure("high").temp("heated").cat("catalysts/nickel", 64).add("koch_acid");
        r(REACTING).in("materials/iron", 3).fin("nitronaphthalene_trisulfonic_acid", B).fin("hydrochloric_acid", 200).out("koch_acid", 1)
            .temp("heated").add("koch_acid_by_iron");
        r(REACTING).in("chemicals/koch_acid", 1).in("chemicals/sodium_hydroxide", 2).fin("water", B).out("h_acid", 1)
            .out("sodium_sulfite", 1).pressure("high").temp("high").add("h_acid");
        r(REACTING).in("chemicals/h_acid", 1).fin("acetic_anhydride", B).out("acetyl_h_acid", 1).fout("acetic_acid", B).add("acetyl_h_acid");
        r(REACTING).in("chemicals/acetyl_h_acid", 1).in("chemicals/sodium_hydroxide", 3).fin("sulfanilic_diazonium", B).out("new_red", 1)
            .add("new_red");
        // 195: phthalic anhydride (naphthalene over vanadium), resorcinol (benzene disulfonated, alkali fusion),
        // fluorescein (P58), iodinated with iodine and potassium iodate in ethanol, the disodium salt
        r(GAS_REACTING).in("chemicals/naphthalene", 2).out("phthalic_anhydride", 2).fout("carbon_dioxide", 4 * B).mode("gas_catalysis")
            .air().cat("catalysts/vanadium", 64).add("phthalic_anhydride");
        r(REACTING).fin("benzene", B).fin("sulfuric_acid", 2 * B).fout("benzenedisulfonic_acid", B).temp("high").add("benzenedisulfonic_acid");
        // fused with caustic soda to the disodium salt (the acid is a solution, so not in the kiln; high temperature, so in the
        // high-pressure reactor, the stirred reactor having no high step, 11 问题 44), acidified in water (11 问题 42): C6H6O6S2 + 6 NaOH → C6H4Na2O2 + 2 Na2SO3 + 4 H2O; C6H4Na2O2 + H2SO4 → C6H6O2 + Na2SO4
        r(REACTING).in("chemicals/sodium_hydroxide", 6).fin("benzenedisulfonic_acid", B).out("disodium_resorcinolate", 1)
            .out("sodium_sulfite", 2).temp("high").add("disodium_resorcinolate");
        r(REACTING).in("chemicals/disodium_resorcinolate", 1).fin("water", B).fin("sulfuric_acid", B).out("resorcinol", 1)
            .out("sodium_sulfate", 1).tier(2).add("resorcinol");
        r(REACTING).in("chemicals/phthalic_anhydride", 1).in("chemicals/resorcinol", 2).out("fluorescein", 1).temp("high").add("fluorescein");
        r(REACTING).in("chemicals/fluorescein", 5).in("chemicals/iodine", 8).in("chemicals/potassium_iodate", 4).fin("ethanol", B)
            .out("tetraiodofluorescein", 5).out("potassium_hydroxide", 4).add("tetraiodofluorescein");
        r(REACTING).in("chemicals/tetraiodofluorescein", 1).in("chemicals/sodium_hydroxide", 2).fin("water", B).out("erythrosine", 1)
            .add("erythrosine");
        // 202: aniline and crotonaldehyde ring-closed (Doebner–Miller, P58), rectified; condensed with phthalic anhydride,
        // sulfonated with oleum, neutralized
        r(REACTING).fin("aniline", 2 * B).fin("crotonaldehyde", 2 * B).fin("hydrochloric_acid", 200).fout("quinaldine", 2 * B).air()
            .temp("heated").add("quinaldine");
        r(DISTILLING).fin("quinaldine", B).fout("quinaldine", 900).add("quinaldine_rectifying");
        r(REACTING).in("chemicals/phthalic_anhydride", 1).fin("quinaldine", B).out("quinophthalone", 1).temp("high").add("quinophthalone");
        r(REACTING).in("chemicals/quinophthalone", 1).in("chemicals/sodium_hydroxide", 2).fin("sulfur_trioxide", 2 * B)
            .out("quinoline_yellow", 1).method("corrosion_resistant").add("quinoline_yellow");
        lake("amaranth", "amaranth_lake");
        lake("erythrosine", "erythrosine_lake");
        lake("new_red", "new_red_lake");
        lake("quinoline_yellow", "quinoline_yellow_lake");
        lakeSaltingOutOnly("azorubine");
    }

    /** Salting out (P61) for a colour without a lake. */
    private void lakeSaltingOutOnly(String dye) {
        r(REACTING).in("chemicals/" + dye, 4).in("chemicals/salt", 2).fin("water", B).out(dye, 3).rm(75).add(dye + "_salting_out");
    }

    // ---- 198 titanium dioxide ----

    private void titaniumDioxide() {
        // A sulfate process: digested in a lined reactor, copperas crystallized out, hydrolysed hot (P92), washed, calcined (anatase)
        r(REACTING).in("materials/ilmenite", 1).fin("sulfuric_acid", 2 * B).fin("water", B).fout("titanyl_sulfate", B)
            .out("ferrous_sulfate", 1).src().temp("heated").method("corrosion_resistant").add("titanyl_sulfate");
        r(REACTING).fin("titanyl_sulfate", B).fin("water", B).out("metatitanic_acid", 1).src().fout("sulfuric_acid", B).src()
            .temp("heated").add("metatitanic_acid");
        r(SEPARATING).in("chemicals/metatitanic_acid", 1).fin("water", B).out("metatitanic_acid", 1).tier(2).add("metatitanic_acid_washing");
        r(KILN).in("chemicals/metatitanic_acid", 1).out("titanium_dioxide", 1).mode("calcining").temp("high").add("titanium_dioxide");
        // B chloride process: smelted to slag and pig iron in the arc furnace (P34); slag and coke premixed, chlorinated in the
        // fluidized bed (P93, 2026-11-11, user); the tetrachloride rectified and burnt in oxygen (rutile), chlorine back
        r(KILN).in("materials/ilmenite", 2).in("materials/coke", 1).out("titanium_slag", 1).out("minecraft:iron_ingot", 1)
            .fout("carbon_monoxide", B).cat("catalysts/electrode", 16).temp("extreme").add("titanium_slag");
        r(MIXING).in("chemicals/titanium_slag", 1).in("materials/coke", 1).out("chlorination_charge", 2).mode("blending")
            .add("chlorination_charge");
        r(GAS_REACTING).in("chemicals/chlorination_charge", 1).fin("chlorine", 2 * B).fout("titanium_tetrachloride", B).src()
            .fout("carbon_monoxide", 2 * B).src().mode("fluidized_bed").temp("high").add("titanium_tetrachloride");
        r(DISTILLING).fin("titanium_tetrachloride", B).fout("titanium_tetrachloride", 900).add("titanium_tetrachloride_rectifying");
        r(GAS_REACTING).fin("titanium_tetrachloride", B).fin("oxygen", B).out("titanium_dioxide", 1).fout("chlorine", 2 * B)
            .mode("burning").temp("high").add("titanium_dioxide_chloride");
    }

    // ---- 200, 201 iron oxides ----

    private void ironOxides() {
        // 200: 6 FeSO4 + 12 NaOH + O2 → 2 Fe3O4 + 6 Na2SO4 + 6 H2O, air blown at 90 °C
        r(REACTING).in("chemicals/ferrous_sulfate", 6).in("chemicals/sodium_hydroxide", 12).fin("water", B).out("iron_oxide_black", 2)
            .out("sodium_sulfate", 6).temp("heated").air().add("iron_oxide_black");
        // 201 A: copperas calcined in the earth kiln, the sulfur oxides taken up as dilute acid
        r(KILN).in("chemicals/ferrous_sulfate", 2).out("iron_oxide_red", 1).fout("sulfur_dioxide", B).fout("sulfur_trioxide", B)
            .mode("calcining").add("iron_oxide_red");
        // 201 B: 4 Fe3O4 + O2 → 6 Fe2O3 in the rotary kiln
        r(KILN).in("chemicals/iron_oxide_black", 4).out("iron_oxide_red", 6).mode("roasting").add("iron_oxide_red_from_black");
    }

    // ---- 197, 204, 236 carotenoids and vitamin B2 ----

    private void carotenoids() {
        // 197 algal: Dunaliella powder in hexane to the carotene oleoresin, saponified and crystallized as in 20
        r(EXTRACTING).in("materials/dunaliella", 4).fin("n_hexane", B).fout("natural_carotene", 250).src(5).fout("n_hexane", 750).src()
            .mode("solvent_extraction").add("natural_carotene_from_dunaliella");
        // 204: triphenylphosphine from phosphorus trichloride, chlorobenzene and sodium (molten salt electrolysis)
        r(REACTING).in("chemicals/white_phosphorus", 1).fin("chlorine", 6 * B).fout("phosphorus_trichloride", 4 * B)
            .method("corrosion_resistant").add("phosphorus_trichloride");
        r(REACTING).in("chemicals/ferric_chloride", 1).fin("benzene", 4 * B).fin("chlorine", 4 * B).fout("chlorobenzene", 4 * B)
            .fout("hydrogen_chloride", 4 * B).method("corrosion_resistant").add("chlorobenzene");
        r(ELECTROLYZING).in("chemicals/salt", 2).out("sodium", 2).fout("chlorine", B).mode("molten_salt").cat("catalysts/electrode", 64)
            .add("sodium");
        r(REACTING).in("chemicals/sodium", 6).fin("phosphorus_trichloride", B).fin("chlorobenzene", 3 * B).out("triphenylphosphine", 1)
            .out("salt", 6).inert().add("triphenylphosphine");
        // the phosphonium salt and the one-sided Wittig condensation with the C10 dialdehyde (inert, P58)
        r(REACTING).in("chemicals/triphenylphosphine", 1).fin("retinol", B).fin("hydrogen_chloride", B)
            .out("retinyl_phosphonium_chloride", 1).add("retinyl_phosphonium_chloride");
        r(REACTING).in("chemicals/retinyl_phosphonium_chloride", 1).in("chemicals/octatrienedial", 1).in("chemicals/sodium_hydroxide", 1)
            .out("apocarotenal", 1).out("triphenylphosphine_oxide", 1).inert().add("apocarotenal");
        recrystallizing("apocarotenal");
        // triphenylphosphine oxide recycled (2026-11-11, user): phosgene to the dichloride in chlorobenzene, reduced with
        // iron (US 5,527,966)
        r(REACTING).in("chemicals/triphenylphosphine_oxide", 1).in("materials/iron", 1).fin("phosgene", B).fin("chlorobenzene", 100)
            .out("triphenylphosphine", 1).fout("carbon_dioxide", B).temp("heated").method("corrosion_resistant")
            .add("triphenylphosphine_recycling");
        // 236 B: Bacillus subtilis bred for riboflavin (mutagenesis plate), deep fermentation as 63
        r(FERMENTING).in("chemicals/glucose", 2).in("chemicals/ammonium_sulfate", 1).fin("water", 2 * B)
            .cat("cultures/bacillus_subtilis", CULTURE_RUNS, PURE).out("riboflavin", 1).src(80).sterile().mode("aerated")
            .add("riboflavin_from_bacillus_subtilis");
    }

    // ---- P89 water-soluble colours ----

    private void waterSoluble() {
        waterLine("safflower_yellow", "materials/safflower", 4, RESIN | DRY);
        waterLine("lingonberry_red", "materials/lingonberry", 8, ACID | RESIN | DRY);
        waterLine("red_rice_red", "materials/red_rice_bran", 4, ACID | RESIN | DRY);
        waterLine("coreopsis_yellow", "materials/coreopsis", 4, HOT);
        waterLine("black_bean_red", "materials/black_soybean_hull", 4, ACID | RESIN | DRY);
        waterLine("sorghum_red", "materials/sorghum_husk", 4, HOT | RESIN | DRY);
        waterLine("radish_red", "materials/red_radish", 8, ACID | RESIN | DRY);
        waterLine("blackcurrant_red", "materials/blackcurrant", 8, ACID | RESIN | DRY);
        waterLine("roselle_red", "materials/roselle", 4, ACID | RESIN | DRY);
        waterLine("acorn_shell_brown", "materials/acorn_shell", 4, HOT | DRY);
        waterLine("mulberry_red", "materials/mulberry", 8, ACID | RESIN | DRY);
        waterLine("amaranthus_red", "materials/red_amaranth", 8, DRY);
        waterLine("rosa_laevigata_brown", "materials/rosa_laevigata", 4, HOT | DRY);
        waterLine("peanut_skin_red", "materials/peanut_skin", 4, RESIN | DRY);
        waterLine("grape_skin_red", "materials/grape_pomace", 4, ACID | RESIN | DRY);
        waterLine("haskap_red", "materials/haskap", 8, ACID | RESIN | DRY);
        waterLine("bayberry_red", "materials/bayberry", 8, ACID | RESIN | DRY);
        waterLine("purple_sweet_potato_color", "materials/purple_sweet_potato", 8, ACID | HOT | RESIN | DRY);
        // 215: the extract flocculated with sodium alginate before the resin
        r(REACTING).in("chemicals/radish_red", 4).in("chemicals/sodium_alginate", 1).fin("water", B).out("radish_red", 3).rm(50).tier(2)
            .add("radish_red_flocculating");
        // 216: cocoa shells in dilute soda ash, neutralized, concentrated (T1 stops at the liquor)
        r(EXTRACTING).in("materials/cocoa_shell", 4).in("chemicals/soda_ash", 1).fin("water", B).out("cocoa_shell_color", 1).src()
            .temp("heated").add("cocoa_shell_color_extract");
        sprayDrying("cocoa_shell_color");
        // 207: sticklac washed (seedlac left for 271), the wash acidified, the dye dewaxed with hexane
        r(EXTRACTING).in("materials/sticklac", 4).fin("water", B).out("seedlac", 2).fout("lac_wash_liquor", B).add("seedlac");
        r(REACTING).fin("lac_wash_liquor", B).fin("hydrochloric_acid", 100).out("lac_dye", 1).src().tier(2).add("lac_dye");
        r(EXTRACTING).in("chemicals/lac_dye", 4).fin("n_hexane", B).out("lac_dye", 3).fout("n_hexane", 900).src().mode("solvent_extraction")
            .add("lac_dye_dewaxing");
        resin("lac_dye", "catalysts/macroporous_resin");
        sprayDrying("lac_dye");
        // 218: the gardenia extract on resin, ethanol takes the yellow, water the geniposide; β-glucosidase to genipin;
        // genipin with soy protein hydrolysate (okara and papain, 2026-11-11, user; FDA 21 CFR 73.168) turns blue at 80 °C
        r(REACTING).in("chemicals/gardenia_yellow", 4).fin("ethanol", B).fin("water", B).cat("catalysts/macroporous_resin", ENZYME_RUNS)
            .out("gardenia_yellow", 3).rm(90).fout("geniposide", B).fout("ethanol", 800).src().tier(2).add("geniposide");
        r(REACTING).in("materials/okara", 4).fin("water", B).cat("enzymes/papain", ENZYME_RUNS).fout("soy_protein_hydrolysate", B)
            .temp("heated").tier(2).add("soy_protein_hydrolysate");
        r(REACTING).fin("geniposide", B).cat("enzymes/beta_glucosidase", ENZYME_RUNS).out("genipin", 1).src().temp("heated").tier(2)
            .add("genipin");
        r(REACTING).in("chemicals/genipin", 1).fin("soy_protein_hydrolysate", B).out("gardenia_blue", 1).src().temp("heated").tier(2)
            .time(1200).add("gardenia_blue");
        resin("gardenia_blue", "catalysts/macroporous_resin");
        sprayDrying("gardenia_blue");
        // 223 as beetroot red: cold water; 228 phycocyanin: buffer soak, freeze–thaw, ammonium sulfate fractions, freeze dried only
        r(REACTING).in("materials/spirulina", 4).in("chemicals/disodium_phosphate", 1).fin("water", B).out("phycocyanin", 1).src()
            .tier(2).add("phycocyanin");
        r(REACTING).in("chemicals/phycocyanin", 4).in("chemicals/ammonium_sulfate", 2).fin("water", B).out("phycocyanin", 3).rm(75)
            .tier(2).add("phycocyanin_salting_out");
        r(DRYING).in("chemicals/phycocyanin", 2).out("phycocyanin", 1).temp("freezing").heat("very_sensitive")
            .add("phycocyanin_freeze_drying");
        // 233 A: cochineal boiled with soda ash, the lake thrown down with alum (T1); B: hot water, vacuum concentrated, spray dried
        r(EXTRACTING).in("materials/dried_cochineal", 4).in("chemicals/soda_ash", 1).fin("water", B).out("carminic_acid", 1).src()
            .temp("heated").add("carminic_acid");
        r(EXTRACTING).in("materials/dried_cochineal", 4).fin("water", B).out("carminic_acid", 1).src(20).temp("heated").tier(2)
            .add("carminic_acid_hot_water");
        sprayDrying("carminic_acid");
        r(REACTING).in("chemicals/carminic_acid", 2).in("chemicals/potassium_alum", 1).fin("water", B).out("carmine", 1).degrade(50)
            .sources(0).add("carmine");
        r(REACTING).in("chemicals/carminic_acid", 1).in("chemicals/aluminium_hydroxide", 1).in("chemicals/calcium_carbonate", 1)
            .fin("water", B).out("carmine", 1).degrade(50).sources(0).tier(2).add("carmine_lake");
    }

    /** One P89 colour: T1 water extract, T2 dilute ethanol extract (ethanol recovered), resin, spray drying. */
    private void waterLine(String pigment, String raw, int count, int options) {
        ContentRecipe crude = r(EXTRACTING).in(raw, count);
        ContentRecipe solvent = r(EXTRACTING).in(raw, count);
        if ((options & ACID) != 0) {
            crude.in("chemicals/citric_acid", 1);
            solvent.in("chemicals/citric_acid", 1);
        }
        crude.fin("water", B).out(pigment, 1).src();
        solvent.fin("ethanol", B).out(pigment, 1).src(20).fout("ethanol", 750).src().mode("solvent_extraction");
        if ((options & HOT) != 0) {
            crude.temp("heated");
            solvent.temp("heated");
        }
        crude.add(pigment + "_extract");
        solvent.add(pigment + "_solvent_extract");
        if ((options & RESIN) != 0) {
            resin(pigment, "catalysts/macroporous_resin");
        }
        if ((options & DRY) != 0) {
            sprayDrying(pigment);
        }
    }

    // ---- P90 oil-soluble colours ----

    private void oilSoluble() {
        // 209: the paprika oleoresin split with acetone into the red (capsanthin) and orange fractions
        r(EXTRACTING).fin("paprika_oleoresin", B).fin("acetone", B).out("capsanthin", 1).out("capsicum_orange", 1).src(10)
            .fout("acetone", 800).src().mode("liquid_extraction").add("capsicum_orange");
        // 214: corn gluten meal in ethanol
        oilLine("corn_yellow", "materials/corn_gluten_meal", "ethanol");
        // 219: sea buckthorn pressed, the dried residue in ethanol
        oilLine("sea_buckthorn_yellow", "materials/sea_buckthorn", "ethanol");
        sprayDrying("sea_buckthorn_yellow");
        // 230 B, 231
        oilLine("lithospermum_red", "materials/gromwell_root", "ethanol");
        r(CRYSTALLIZING).in("chemicals/lithospermum_red", 2).fin("ethanol", B).out("lithospermum_red", 1).temp("cooling")
            .add("lithospermum_red_crystallizing");
        oilLine("citrus_yellow", "materials/citrus_peel", "n_hexane");
        // 232 B: seed in solvent, crystallized bixin; C: cold potash, hydrolysed hot, acidified to norbixin
        oilLine("bixin", "materials/annatto_seed", "acetone");
        recrystallizing("bixin");
        r(REACTING).in("materials/annatto_seed", 4).in("chemicals/potassium_hydroxide", 2).fin("water", B).fout("potassium_norbixinate", B)
            .fout("methanol", 100).src().temp("heated").tier(2).add("potassium_norbixinate");
        r(REACTING).fin("potassium_norbixinate", B).fin("sulfuric_acid", B).out("norbixin", 1).src().out("potassium_sulfate", 1)
            .tier(2).add("norbixin");
        recrystallizing("norbixin");
        // 234: marigold oleoresin in hexane, saponified with potash in propylene glycol (inert), crystallized cold
        r(EXTRACTING).in("materials/marigold", 4).fin("n_hexane", B).out("marigold_oleoresin", 1).fout("n_hexane", 750).src()
            .mode("solvent_extraction").add("marigold_oleoresin");
        r(REACTING).in("chemicals/marigold_oleoresin", 1).in("chemicals/potassium_hydroxide", 2).fin("propylene_glycol", B)
            .out("lutein", 1).src().out("potassium_palmitate", 2).src().fout("propylene_glycol", 900).src().temp("heated").inert().add("lutein");
        r(CRYSTALLIZING).in("chemicals/lutein", 2).fin("ethanol", B).out("lutein", 1).temp("cooling").add("lutein_crystallizing");
        // 241: the crude chlorophyll demagnesiated with dilute acid, copper put in (P49); 243 with potash as 22
        r(REACTING).in("chemicals/chlorophyll_a", 1).fin("hydrochloric_acid", 200).out("pheophytin_a", 1).out("magnesium_chloride", 1)
            .tier(2).add("pheophytin_a");
        r(REACTING).in("chemicals/pheophytin_a", 1).in("chemicals/copper_sulfate", 1).fin("ethanol", B).out("copper_chlorophyll", 1)
            .fout("ethanol", 900).src().tier(2).add("copper_chlorophyll");
        r(REACTING).in("chemicals/chlorophyll_a", 1).in("chemicals/potassium_hydroxide", 1).in("chemicals/copper_sulfate", 1)
            .fin("water", B).out("potassium_copper_chlorophyllin", 1).tier(2).add("potassium_copper_chlorophyllin");
        decolouring("potassium_copper_chlorophyllin");
    }

    /** One P90 colour by solvent: raw in the extraction tank, the solvent recovered. */
    private void oilLine(String pigment, String raw, String solvent) {
        r(EXTRACTING).in(raw, 4).fin(solvent, B).out(pigment, 1).src(20).fout(solvent, 750).src().mode("solvent_extraction")
            .add(pigment + "_extract");
    }

    // ---- 229, 239, 240 ----

    private void others() {
        // 229: bamboo or wood charred in the closed kiln (T1, poor), acid washed (T2)
        r(KILN).in("materials/bamboo", 4).out("vegetable_carbon", 1).src().fout("wood_vinegar", 250).mode("dry_distillation")
            .add("vegetable_carbon");
        r(REACTING).in("chemicals/vegetable_carbon", 4).fin("hydrochloric_acid", B).out("vegetable_carbon", 3).tier(2)
            .method("corrosion_resistant").add("vegetable_carbon_acid_washing");
        // 239: caustic sulfite caramel (class II)
        r(REACTING).in("chemicals/sucrose", 2).in("chemicals/sodium_sulfite", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B)
            .fout("caramel", B).src(40).temp("heated").tier(2).add("caramel_caustic_sulfite");
        // 240 A: red yeast rice in ethanol over resin, the yellow and red fractions eluted apart
        r(REACTING).in("chemicals/red_yeast_rice", 4).fin("ethanol", B).cat("catalysts/macroporous_resin", ENZYME_RUNS)
            .out("monascus_yellow", 1).src(20).out("monascorubrin", 1).src(5).fout("ethanol", 800).src().tier(2).add("monascus_yellow");
        sprayDrying("monascus_yellow");
    }
}
