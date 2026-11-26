package com.tonywww.foodindustrial.content.food2;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.content.ContentRecipe;
import com.tonywww.foodindustrial.content.FoodTiers;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.content.Workbench;
import com.tonywww.foodindustrial.food.flavor.DosageForm;
import com.tonywww.foodindustrial.food.flavor.FlavorType;
import com.tonywww.foodindustrial.food.flavor.Flavorings;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.MIXING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;

/**
 * The second batch (I48, foods/11): the flavourings, {@code foodindustrial:food2/<form>/<type>_<main>_<secondary>}.
 * <p>
 * A blended flavouring takes one main note ({@code flavor_notes/<type>}) and one secondary note ({@code flavor_notes/all}),
 * each an item or 250 mB of a fluid (a bottle on the crafting table, 2026-11-19, user), so every type and form has four
 * recipes; which type a device makes when a main note belongs to several groups is the device's setting (M21, M41) or, on
 * the crafting table, the first group of the first flavouring material ({@link Flavorings#autoType}). Amounts and times are
 * the card's (foods/11 §5); the Maillard pastes take twice the reaction time (§8 #8: 100 °C instead of 110 °C).
 */
public final class Food2Recipes extends Batch {
    private static final String NS = FoodIndustrial.MOD_ID;
    /** A fluid note: one bottle (2026-11-19, user). */
    private static final int NOTE = 250;
    /** A flavouring recipe's yield (foods/11 §4, §8 #6). */
    private static final int MADE = 8;
    /** Stirs of a pot step (as the T0 chains of I12). */
    private static final int STIRS = 8;
    private static final String WATER_SOLUBLE = "flavor_carriers/water_soluble";
    private static final String OIL_SOLUBLE = "flavor_carriers/oil_soluble";
    private static final String POWDERS = "flavor_carriers/powders";
    private static final String EMULSIFIERS = "flavor_carriers/emulsifiers";

    private Food2Recipes(RecipeGen.Output out) {
        super(out, "food2");
    }

    /** Registers the carrier tags and the generator. Called during mod construction. */
    public static void bootstrap() {
        tags();
        RecipeGen.register(out -> new Food2Recipes(out).generate());
        RecipeGen.addSetCheck(Food2Recipes::productsTakeNoNotes);
    }

    /** The product batches' recipes take flavourings, never flavouring materials (foods/11 §1, I48 check). */
    private static java.util.List<String> productsTakeNoNotes(java.util.Map<String, ProcessRecipe> recipes) {
        java.util.List<String> problems = new java.util.ArrayList<>();
        recipes.forEach((path, recipe) -> {
            if (!path.startsWith("food") || path.startsWith("food2/")) {
                return;
            }
            for (ProcessInput input : recipe.inputs()) {
                java.util.List<String> refs = input instanceof ProcessInput.ItemInput item
                    ? (item.isSemi() ? java.util.List.of() : FoodTiers.refs(item.ingredient()))
                    : ((ProcessInput.FluidInput) input).fluid().tag().map(tag -> java.util.List.of("#" + PlatformTags.tagId(tag)))
                        .orElse(java.util.List.of());
                for (String ref : refs) {
                    String note = Flavorings.flavorMaterialOfTag(ref);
                    if (note != null) {
                        problems.add(NS + ":" + path + " takes the flavouring material " + note + "; products take a flavouring (foods/11 §6)");
                    }
                }
            }
        });
        return problems;
    }

    /** The carriers of the forms (foods/11 §5, GB 30616 appendix A). */
    private static void tags() {
        ModTags.extraFluid(WATER_SOLUBLE, "#" + NS + ":propylene_glycol");
        ModTags.extraFluid(WATER_SOLUBLE, "#" + NS + ":ethanol");
        ModTags.extraFluid(OIL_SOLUBLE, "#" + NS + ":vegetable_oil");
        ModTags.extraFluid(OIL_SOLUBLE, "#" + NS + ":caprylic_capric_triglyceride");
        ModTags.extraFluid(OIL_SOLUBLE, "#" + NS + ":triacetin");
        for (String carrier : new String[]{"maltodextrin", "starch", "salt", "glucose", "sucrose"}) {
            ModTags.extra(POWDERS, "#" + NS + ":chemicals/" + carrier);
        }
        ModTags.extra(EMULSIFIERS, "#" + NS + ":chemicals/gum_arabic");
        ModTags.extra(EMULSIFIERS, "#" + NS + ":chemicals/starch_sodium_octenylsuccinate");
    }

    private void generate() {
        Workbench bench = new Workbench(out.sink(), "food2");
        for (FlavorType type : FlavorType.values()) {
            for (boolean fluidMain : new boolean[]{false, true}) {
                for (boolean fluidSecond : new boolean[]{false, true}) {
                    String variant = type.id() + "_" + (fluidMain ? "fluid" : "item") + "_" + (fluidSecond ? "fluid" : "item");
                    blended(type, fluidMain, fluidSecond, variant, bench);
                    // 5.3 step 1: the coarse emulsion, M21 with the emulsifying head
                    notes(r(MIXING), type, fluidMain, fluidSecond).in(EMULSIFIERS, 1).fin("water", B).fout(type.premixId(), B)
                        .mode("blending").method("emulsifying").add("premix/" + variant);
                }
            }
            // 5.3 step 2: high-pressure homogenizing (M40; M21 has the mode too)
            r(MIXING).fin(type.premixId(), B).out(type.itemId(), MADE).form(DosageForm.EMULSION.id()).mode("high_pressure_homogenizing")
                .add("emulsion/" + type.id());
            // 5.4: the emulsion spray-dried with the carrier solution (M28, P79)
            r(DRYING).in("flavorings/" + type.id(), 4).inputForm(DosageForm.EMULSION.id()).fin("spray_carrier_solution", B)
                .out(type.itemId(), MADE).form(DosageForm.ENCAPSULATED_POWDER.id()).heat("sensitive").tier(3).add("encapsulated_powder/" + type.id());
        }
        // 5.6: Maillard pastes (P75), the evaporating merged in
        r(REACTING).fin("meat_stock", B).in("chemicals/xylose", 1).in("chemicals/l_cysteine", 1).out(FlavorType.SAVORY.itemId(), 4)
            .form(DosageForm.PASTE.id()).temp("heated").manual(STIRS).time(400).tier(0).add("paste/savory");
        r(REACTING).fin("water", B).in("chemicals/glucose", 1).in("chemicals/l_proline", 1).out(FlavorType.NUTTY_ROASTED.itemId(), 4)
            .form(DosageForm.PASTE.id()).temp("heated").manual(STIRS).time(400).tier(0).add("paste/nutty_roasted");
        // 5.7: the carrier solution of the spray-dried powders
        r(MIXING).in("chemicals/maltodextrin", 4).in("chemicals/starch_sodium_octenylsuccinate", 1).in("chemicals/gum_arabic", 1)
            .fin("water", B).fout("spray_carrier_solution", B).mode("blending").add("spray_carrier_solution");
    }

    /** 5.1, 5.2, 5.5 on the mixers (M41, M21) and on the crafting table. */
    private void blended(FlavorType type, boolean fluidMain, boolean fluidSecond, String variant, Workbench bench) {
        notes(r(MIXING), type, fluidMain, fluidSecond).fin(WATER_SOLUBLE, B).out(type.itemId(), MADE).form(DosageForm.WATER_SOLUBLE.id())
            .mode("blending").add("water_soluble/" + variant);
        notes(r(MIXING), type, fluidMain, fluidSecond).fin(OIL_SOLUBLE, B).out(type.itemId(), MADE).form(DosageForm.OIL_SOLUBLE.id())
            .mode("blending").add("oil_soluble/" + variant);
        notes(r(MIXING), type, fluidMain, fluidSecond).in(POWDERS, 4).out(type.itemId(), MADE).form(DosageForm.BLENDED_POWDER.id())
            .mode("blending").add("blended_powder/" + variant);
        benchNotes(bench, type, fluidMain, fluidSecond).fin(WATER_SOLUBLE, B).out(type.itemId(), MADE).form(DosageForm.WATER_SOLUBLE.id())
            .add("water_soluble/" + variant);
        benchNotes(bench, type, fluidMain, fluidSecond).fin(OIL_SOLUBLE, B).out(type.itemId(), MADE).form(DosageForm.OIL_SOLUBLE.id())
            .add("oil_soluble/" + variant);
        benchNotes(bench, type, fluidMain, fluidSecond).in(POWDERS, 4).out(type.itemId(), MADE).form(DosageForm.BLENDED_POWDER.id())
            .add("blended_powder/" + variant);
    }

    /** The main note (first input) and the secondary note. */
    private static ContentRecipe notes(ContentRecipe recipe, FlavorType type, boolean fluidMain, boolean fluidSecond) {
        if (fluidMain) {
            recipe.fin(type.notesTag(), NOTE);
        } else {
            recipe.in(type.notesTag(), 1);
        }
        return fluidSecond ? recipe.fin(Flavorings.ALL_NOTES, NOTE) : recipe.in(Flavorings.ALL_NOTES, 1);
    }

    private static Workbench.Recipe benchNotes(Workbench bench, FlavorType type, boolean fluidMain, boolean fluidSecond) {
        Workbench.Recipe recipe = bench.of(MIXING);
        if (fluidMain) {
            recipe.fin(type.notesTag(), NOTE);
        } else {
            recipe.in(type.notesTag(), 1);
        }
        return fluidSecond ? recipe.fin(Flavorings.ALL_NOTES, NOTE) : recipe.in(Flavorings.ALL_NOTES, 1);
    }
}
