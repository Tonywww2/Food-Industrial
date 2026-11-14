package com.tonywww.foodindustrial.content.batch4;

import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.ABSORBING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DISTILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.ELECTROLYZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GAS_REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GRINDING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.KILN;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SEPARATING;

/**
 * The fourth content batch, group D (I30, additives/15 §D): stabilizers, firming agents and thickeners,
 * 281–318. Seed gums share the seed gum line (P84), modified starches the slurry and dry-heat
 * treatments (P85, P86), cellulose ethers and carboxymethyl starch the alkali–etherification (P87).
 * The exudate gums' hand steps (283, 288 A) are manual recipes.
 */
public final class Batch4Thickeners extends Batch {
    private Batch4Thickeners(RecipeGen.Output out) {
        super(out, "batch4");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Batch4Thickeners(out).generate());
    }

    private void generate() {
        stabilizers();
        seedGums();
        modifiedStarches();
        thickeners();
    }

    // ---- 281–286 stabilizers and firming agents ----

    private void stabilizers() {
        // 281 A (optional route, hydrogen cyanide): formaldehyde over silver (P35), cyanomethylation of ethylenediamine;
        // B is I20's chloroacetate route; the acid of I20, then the disodium salt crystallized cold as the dihydrate
        r(GAS_REACTING).fin("methanol", 2 * B).fout("formaldehyde", 2 * B).mode("gas_catalysis").air().cat("catalysts/silver", 64)
            .add("formaldehyde");
        r(REACTING).in("chemicals/sodium_hydroxide", 4).fin("ethylenediamine", B).fin("formaldehyde", 4 * B).fin("hydrogen_cyanide", 4 * B)
            .fout("tetrasodium_edta", B).fout("ammonia", 4 * B).add("tetrasodium_edta_cyanomethylation");
        r(REACTING).in("chemicals/edta", 1).in("chemicals/sodium_hydroxide", 2).fin("water", B).out("disodium_edta", 1).temp("cooling")
            .add("disodium_edta");
        recrystallizing("disodium_edta");
        // 282: stannous chloride from tin and hydrochloric acid; distannous citrate; the disodium salt in ethanol
        r(REACTING).in("materials/tin", 1).fin("hydrochloric_acid", 2 * B).out("stannous_chloride", 1).fout("hydrogen", B)
            .method("corrosion_resistant").add("stannous_chloride");
        r(REACTING).in("chemicals/stannous_chloride", 2).in("chemicals/sodium_hydroxide", 4).in("chemicals/citric_acid", 1).fin("water", B)
            .out("distannous_citrate", 1).out("salt", 4).temp("heated").tier(2).add("distannous_citrate");
        r(REACTING).in("chemicals/distannous_citrate", 1).in("chemicals/citric_acid", 1).in("chemicals/sodium_hydroxide", 4)
            .fin("ethanol", B).out("disodium_stannous_citrate", 2).fout("ethanol", 900).src().temp("heated").tier(2)
            .add("disodium_stannous_citrate");
        // 284, 301: cyclodextrin glucanotransferase on liquefied starch with a guest that precipitates the complex (P88);
        // the guest steam-stripped back; 285: without guest, the leftover dextrin split to glucose by glucoamylase
        r(REACTING).fin("liquefied_starch", B).fin("cyclohexane", 200).cat("enzymes/cgtase", ENZYME_RUNS)
            .out("alpha_cd_cyclohexane_complex", 1).temp("heated").tier(2).add("alpha_cd_cyclohexane_complex");
        r(DISTILLING).in("chemicals/alpha_cd_cyclohexane_complex", 1).fin("water", B).out("alpha_cyclodextrin", 1).src()
            .fout("cyclohexane", 150).src().mode("steam_distillation").add("alpha_cyclodextrin");
        r(REACTING).fin("liquefied_starch", B).fin("toluene", 200).cat("enzymes/cgtase", ENZYME_RUNS)
            .out("beta_cd_toluene_complex", 1).temp("heated").tier(2).add("beta_cd_toluene_complex");
        r(DISTILLING).in("chemicals/beta_cd_toluene_complex", 1).fin("water", B).out("beta_cyclodextrin", 1).src()
            .fout("toluene", 150).src().mode("steam_distillation").add("beta_cyclodextrin");
        r(REACTING).fin("liquefied_starch", B).fin("water", B).cat("enzymes/cgtase", ENZYME_RUNS).out("gamma_cyclodextrin", 1).src(30)
            .out("glucose", 2).src().temp("heated").tier(2).add("gamma_cyclodextrin");
        resin("gamma_cyclodextrin", "catalysts/macroporous_resin");
        for (String cd : new String[]{"alpha_cyclodextrin", "beta_cyclodextrin", "gamma_cyclodextrin"}) {
            decolouring(cd);
            recrystallizing(cd);
        }
        // maltodextrin (carrier of the water-soluble colour line): starch liquefied to DE 10–20, spray dried
        r(DRYING).fin("liquefied_starch", B).out("maltodextrin", 1).src().tier(2).add("maltodextrin");
        // 286: L-aspartic acid polycondensed solid-state in the kiln (P43), ring opened with potash, spray dried
        r(KILN).in("chemicals/l_aspartic_acid", 1).out("polysuccinimide", 1).mode("condensation").add("polysuccinimide");
        r(REACTING).in("chemicals/polysuccinimide", 1).in("chemicals/potassium_hydroxide", 1).fin("water", B)
            .out("potassium_polyaspartate", 1).tier(2).add("potassium_polyaspartate");
        sprayDrying("potassium_polyaspartate");
    }

    // ---- P84 seed gums ----

    private void seedGums() {
        // dry line (T1): dehulled, split, the endosperm flaked and milled in one step, crude gum; refined in ethanol (T2)
        dryGum("sesbania_gum", "materials/sesbania_seed");
        dryGum("locust_bean_gum", "materials/carob_seed");
        dryGum("guar_gum", "materials/guar_seed");
        dryGum("gleditsia_gum", "materials/gleditsia_seed");
        dryGum("tara_gum", "materials/tara_seed");
        dryGum("cassia_gum", "materials/cassia_seed");
        // wet line (T2): seed or kernel in hot (290, 297) or acidified (311) water, centrifuged and concentrated
        wetGum("tamarind_gum", "materials/tamarind_seed", false);
        wetGum("flaxseed_gum", "materials/flaxseed", false);
        wetGum("artemisia_gum", "materials/artemisia_seed", true);
    }

    private void dryGum(String gum, String seed) {
        r(GRINDING).in(seed, 4).out(gum, 1).src().add(gum + "_milling");
        ethanolPrecipitation(gum);
    }

    private void wetGum(String gum, String seed, boolean acid) {
        if (acid) {
            r(EXTRACTING).in(seed, 4).fin("water", 2 * B).fin("hydrochloric_acid", 100).out(gum, 1).src().temp("heated").tier(2)
                .time(1200).add(gum + "_extract");
        } else {
            r(EXTRACTING).in(seed, 4).fin("water", 2 * B).out(gum, 1).src().temp("heated").tier(2).add(gum + "_extract");
        }
        ethanolPrecipitation(gum);
    }

    /** Dissolved, filtered and thrown down with ethanol (P23), pressed and dried; the ethanol recovered. */
    private void ethanolPrecipitation(String gum) {
        r(REACTING).in("chemicals/" + gum, 4).fin("water", B).fin("ethanol", B).out(gum, 3).fout("ethanol", 800).src().tier(2)
            .add(gum + "_ethanol_precipitation");
    }

    // ---- P85, P86 modified starches ----

    private void modifiedStarches() {
        // 309: sodium trimetaphosphate (slow-cooled melt, P43), starch crosslinked at pH 10–11
        r(KILN).in("chemicals/monosodium_phosphate", 3).out("sodium_trimetaphosphate", 1).mode("calcining").temp("high")
            .add("sodium_trimetaphosphate");
        r(REACTING).in("chemicals/starch", 4).in("chemicals/sodium_trimetaphosphate", 1).in("chemicals/sodium_hydroxide", 1)
            .fin("water", B).out("distarch_phosphate", 4).tier(2).add("distarch_phosphate");
        // 292, 295: steeped in sodium phosphate, dried and heated dry (P86)
        r(KILN).in("chemicals/starch", 4).in("chemicals/monosodium_phosphate", 1).out("sodium_starch_phosphate", 4).mode("condensation")
            .add("sodium_starch_phosphate");
        r(KILN).in("chemicals/distarch_phosphate", 4).in("chemicals/monosodium_phosphate", 1).out("phosphated_distarch_phosphate", 4)
            .mode("condensation").add("phosphated_distarch_phosphate");
        // 293: distarch phosphate acetylated at pH 8
        r(REACTING).in("chemicals/distarch_phosphate", 4).in("chemicals/sodium_hydroxide", 1).fin("acetic_anhydride", B)
            .out("acetylated_distarch_phosphate", 4).fout("acetic_acid", B).src(30).add("acetylated_distarch_phosphate");
        // 294: hydroxypropyl starch (I19) crosslinked with trimetaphosphate
        r(REACTING).in("chemicals/hydroxypropyl_starch", 4).in("chemicals/sodium_trimetaphosphate", 1).in("chemicals/sodium_hydroxide", 1)
            .fin("water", B).out("hydroxypropyl_distarch_phosphate", 4).tier(2).add("hydroxypropyl_distarch_phosphate");
        // 306, 308: hypochlorite oxidation at pH 8–10, residual chlorine taken off with bisulfite
        r(REACTING).in("chemicals/starch", 4).fin("sodium_hypochlorite", 2 * B).fin("sodium_bisulfite", 100).out("oxidized_starch", 4)
            .out("salt", 2).tier(2).add("oxidized_starch");
        r(REACTING).in("chemicals/hydroxypropyl_starch", 4).fin("sodium_hypochlorite", 2 * B).fin("sodium_bisulfite", 100)
            .out("oxidized_hydroxypropyl_starch", 4).out("salt", 2).tier(2).add("oxidized_hydroxypropyl_starch");
        // 307: adipic acid in acetic anhydride (mixed anhydride in the same pot), crosslinks and acetylates at once
        r(REACTING).in("chemicals/starch", 4).in("chemicals/adipic_acid", 1).in("chemicals/sodium_hydroxide", 1)
            .fin("acetic_anhydride", 2 * B).out("acetylated_distarch_adipate", 4).fout("acetic_acid", 2 * B).src(30)
            .add("acetylated_distarch_adipate");
        // 291: starch in aqueous ethanol, caustic soda, sodium chloroacetate (P87), washed with ethanol
        r(REACTING).in("chemicals/starch", 1).in("chemicals/sodium_chloroacetate", 1).in("chemicals/sodium_hydroxide", 1)
            .fin("ethanol", B).out("sodium_carboxymethyl_starch", 1).out("salt", 1).fout("ethanol", 900).src().tier(2)
            .add("sodium_carboxymethyl_starch");
        r(REACTING).in("chemicals/sodium_carboxymethyl_starch", 2).fin("ethanol", B).out("sodium_carboxymethyl_starch", 2)
            .fout("ethanol", 800).src().tier(2).add("sodium_carboxymethyl_starch_washing");
    }

    // ---- 287–318 thickeners ----

    private void thickeners() {
        // 287: wet alginic acid kneaded with potassium carbonate and a little ethanol
        r(REACTING).in("chemicals/alginic_acid", 2).in("chemicals/potassium_carbonate", 1).fin("ethanol", 100).out("potassium_alginate", 2)
            .fout("carbon_dioxide", B).tier(2).add("potassium_alginate");
        // 288 B, 270 B: lumps dissolved, filtered and centrifuged, dried (drum, or spray from T3)
        for (String gum : new String[]{"gum_arabic", "ghatti_gum", "karaya_gum"}) {
            r(SEPARATING).in("chemicals/" + gum, 4).fin("water", B).out(gum, 3).method("filtration").tier(2).add(gum + "_filtering");
        }
        sprayDrying("gum_arabic");
        // 289: alginic acid partly neutralized, propylene oxide under pressure at 80 °C (P60)
        r(REACTING).in("chemicals/alginic_acid", 4).in("chemicals/soda_ash", 1).fin("propylene_oxide", 4 * B)
            .out("propylene_glycol_alginate", 4).fout("carbon_dioxide", B).pressure("pressurized").temp("heated")
            .add("propylene_glycol_alginate");
        // 296 A: shells decalcified with acid, deproteinized with caustic soda, bleached
        r(REACTING).in("materials/crustacean_shell", 4).in("chemicals/sodium_hydroxide", 1).fin("hydrochloric_acid", 2 * B)
            .fin("sodium_hypochlorite", 200).out("chitin", 1).src().out("calcium_chloride", 1).temp("heated").tier(2).add("chitin");
        // 296 B: Aspergillus niger mycelium from the citric acid trays, hot caustic soda (chitin–glucan, counted as chitin)
        r(REACTING).in("materials/fungal_mycelium", 4).in("chemicals/sodium_hydroxide", 1).fin("water", B).out("chitin", 1).src()
            .temp("heated").tier(2).add("chitin_from_mycelium");
        // 303: deacetylated in strong hot caustic soda; refined by dissolving in dilute acetic acid and precipitating
        r(REACTING).in("chemicals/chitin", 1).in("chemicals/sodium_hydroxide", 1).fin("water", B).out("chitosan", 1)
            .out("sodium_acetate", 1).temp("heated").tier(2).add("chitosan");
        r(REACTING).in("chemicals/chitosan", 4).in("chemicals/sodium_hydroxide", 1).fin("acetic_acid", B).out("chitosan", 3).rm(75)
            .tier(2).add("chitosan_refining");
        // 299: glucose, sorbitol and citric acid 89 : 10 : 1 melt-polycondensed under vacuum (P77), decoloured, spray dried
        r(REACTING).in("chemicals/glucose", 16).in("chemicals/sorbitol", 2).in("chemicals/citric_acid", 1).out("polydextrose", 16)
            .mode("polymerization").pressure("vacuum").temp("heated").add("polydextrose");
        decolouring("polydextrose");
        sprayDrying("polydextrose");
        // 304, 314: alkali cellulose etherified with methyl chloride (and propylene oxide) under nitrogen pressure (P87),
        // washed in hot water
        r(REACTING).in("chemicals/cellulose", 1).in("chemicals/sodium_hydroxide", 1).fin("methyl_chloride", B).out("methylcellulose", 1)
            .out("salt", 1).pressure("pressurized").temp("heated").add("methylcellulose");
        r(REACTING).in("chemicals/cellulose", 1).in("chemicals/sodium_hydroxide", 1).fin("methyl_chloride", B).fin("propylene_oxide", B)
            .out("hydroxypropyl_methylcellulose", 1).out("salt", 1).pressure("pressurized").temp("heated")
            .add("hydroxypropyl_methylcellulose");
        for (String ether : new String[]{"methylcellulose", "hydroxypropyl_methylcellulose"}) {
            r(SEPARATING).in("chemicals/" + ether, 1).fin("water", B).out(ether, 1).tier(2).add(ether + "_washing");
        }
        // 310: propene oxidized on bismuth molybdate in one step (P35), rectified, neutralized, polymerized with ammonium
        // persulfate (P77); persulfate by anodic oxidation of ammonium bisulfate (P48)
        r(KILN).in("materials/bismuth", 2).in("materials/molybdenum", 3).out("bismuth_molybdate_catalyst", 1).mode("roasting")
            .add("bismuth_molybdate_catalyst");
        r(GAS_REACTING).fin("propene", 2 * B).fout("acrylic_acid", 2 * B).mode("gas_catalysis").air().temp("heated")
            .cat("catalysts/bismuth_molybdate", 64).add("acrylic_acid");
        r(DISTILLING).fin("acrylic_acid", B).fout("acrylic_acid", 900).add("acrylic_acid_rectifying");
        // ammonium sulfate taken up in sulfuric acid (2026-11-12, apart from the sulfate itself, 11 问题 41)
        r(REACTING).in("chemicals/ammonium_sulfate", 1).fin("sulfuric_acid", B).out("ammonium_bisulfate", 2).temp("heated").tier(2)
            .add("ammonium_bisulfate");
        r(ELECTROLYZING).in("chemicals/ammonium_bisulfate", 2).out("ammonium_persulfate", 1).fout("hydrogen", B).mode("electrooxidation")
            .cat("catalysts/electrode", 64).add("ammonium_persulfate");
        r(REACTING).in("chemicals/sodium_hydroxide", 4).in("chemicals/ammonium_persulfate", 1).fin("acrylic_acid", 4 * B)
            .out("sodium_polyacrylate", 4).mode("polymerization").add("sodium_polyacrylate");
        // 313: Alcaligenes faecalis var. myxogenes (N limited), dissolved in caustic soda, centrifuged, thrown down with acid
        r(FERMENTING).in("chemicals/glucose", 2).in("chemicals/ammonium_sulfate", 1).fin("water", 2 * B)
            .cat("cultures/alcaligenes_myxogenes", CULTURE_RUNS, PURE).out("curdlan", 1).src(40).sterile().mode("aerated").add("curdlan");
        r(REACTING).in("chemicals/curdlan", 2).in("chemicals/sodium_hydroxide", 1).fin("hydrochloric_acid", B).out("curdlan", 1).tier(2)
            .add("curdlan_reprecipitating");
        // 315: okara at pH 4–5.5 extracted at 120 °C under pressure, decoloured, spray dried
        r(REACTING).in("materials/okara", 4).fin("water", B).fin("hydrochloric_acid", 100).out("soluble_soybean_polysaccharide", 1).src()
            .pressure("pressurized").temp("heated").add("soluble_soybean_polysaccharide");
        decolouring("soluble_soybean_polysaccharide");
        sprayDrying("soluble_soybean_polysaccharide");
        // 317: sodium alginate and calcium chloride (also the calcium alginate of 15, washed)
        r(REACTING).in("chemicals/sodium_alginate", 2).in("chemicals/calcium_chloride", 1).fin("water", B).out("calcium_alginate", 1)
            .out("salt", 2).tier(2).add("calcium_alginate_from_sodium_alginate");
        // 318: Sphingomonas sanxanigenens on glucose at pH 7.5, sterilized, thrown down with ethanol (as xanthan)
        r(FERMENTING).in("chemicals/glucose", 2).fin("water", 2 * B).cat("cultures/sphingomonas_sanxanigenens", CULTURE_RUNS, PURE)
            .out("sanzan_gum", 1).src(40).sterile().mode("aerated").add("sanzan_gum");
        r(REACTING).in("chemicals/sanzan_gum", 2).fin("ethanol", B).out("sanzan_gum", 1).fout("ethanol", 800).src().tier(2)
            .add("sanzan_gum_precipitating");
    }
}
