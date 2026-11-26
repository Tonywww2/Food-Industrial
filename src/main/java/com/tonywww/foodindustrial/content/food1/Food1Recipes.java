package com.tonywww.foodindustrial.content.food1;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.content.Workbench;
import com.tonywww.foodindustrial.food.SemiFinished;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.recipe.RecipeGen;
import com.tonywww.foodindustrial.registry.SharedTags;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.BAKING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.BOILING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CHILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.COOKING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CRYSTALLIZING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CURING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FORMING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FUMIGATING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GRINDING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.MIXING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PACKAGING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PRESSING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PURIFYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SEPARATING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SOLID_FERMENTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.STERILIZING;

/**
 * The first batch of finished products (I47; foods/09 fats and sweeteners, foods/10 condiments): 48 products, the
 * intermediates they share and the steps of each card, as recipes {@code foodindustrial:food1/<name>}; the crafting-table
 * steps (T0 of R19, R20 and R29) as {@code foodindustrial:food1/workbench/<name>}.
 * <p>
 * Steps the T0 hand devices can do carry stirs (the pots) or need no more than the crock, rack, tray, mortar or
 * leaching barrel offer, and start at T0; the others at T1 (or where their device is). Amounts, times and stirs are
 * game settings (foods/09 opening: the card's numbers where it gives them).
 */
public final class Food1Recipes extends Batch {
    private static final String NS = FoodIndustrial.MOD_ID;
    /** One bottle or jar of a liquid product (foods/09 §1). */
    private static final int BOTTLE = 250;
    /** Stirs of a pot step (as the T0 chains of I12). */
    private static final int STIRS = 8;
    /** Actions of the mortar. */
    private static final int GRIND = 8;
    /** Runs of activated carbon in the leaching barrel or the adsorption column (game setting). */
    private static final int CARBON_RUNS = 16;
    private static final String SALT = "salt_position";
    private static final String GLASS_BOTTLE = "containers/glass_bottle";
    private static final String GLASS_JAR = "containers/glass_jar";
    private static final String PAPER_BAG = "containers/paper_bag";
    private static final String TABLET_DIE = "molds/tablet_die";
    private static final String GRANULATING_SCREEN = "molds/granulating_screen";

    /** Flavourings of the second batch (I48, foods/11 §4): the taste item of a card that names a flavouring type. */
    private static final String DAIRY = "dairy_flavoring";
    private static final String SWEET = "sweet_flavoring";
    private static final String SAVORY = "savory_flavoring";

    private Food1Recipes(RecipeGen.Output out) {
        super(out, "food1");
    }

    /** Registers stages, tags and both generators. Called during mod construction. */
    public static void bootstrap() {
        stages();
        tags();
        RecipeGen.register(out -> new Food1Recipes(out).generate());
    }

    // ---- stages and tags ----

    private static void stages() {
        SemiFinished.stage("wet_sugar", "湿糖", "Wet Sugar");
        SemiFinished.stage("wet_cube", "湿坯", "Wet Cube");
        SemiFinished.stage("molten_sugar", "熔糖", "Molten Sugar");
        SemiFinished.stage("powder", "粉", "Powder");
        SemiFinished.stage("block", "块", "Block");
        SemiFinished.stage("fermented_grains", "酒醅", "Fermented Grains");
        SemiFinished.stage("vinegar_grains", "醋醅", "Vinegar Grains");
        SemiFinished.stage("smoked_grains", "熏醅", "Smoked Grains");
        SemiFinished.stage("bean_koji", "豆瓣曲", "Bean Koji");
        SemiFinished.stage("dough_cake", "面糕", "Steamed Dough");
        SemiFinished.stage("koji_cake", "面糕曲", "Dough Koji");
        SemiFinished.stage("mustard_powder", "芥末粉", "Mustard Powder");
        SemiFinished.stage("chili_mash", "辣椒坯", "Chili Mash");
        SemiFinished.stage("wet_mix", "湿料", "Wet Mix");
        SemiFinished.stage("wet_granules", "湿粒", "Wet Granules");
        SemiFinished.stage("granules", "颗粒", "Granules");
        SemiFinished.stage("paste", "浓汤膏", "Stock Paste");
        SemiFinished.stage("mix", "料", "Mix");
        SemiFinished.stage("roux", "油面糊", "Roux");
        SemiFinished.stage("spice_blend_a", "香料甲", "Spice Blend A");
        SemiFinished.stage("sealed", "封瓶", "Sealed");  // huangjiu (foods/22 §2.2)
    }

    private static void tags() {
        // foods/09 §5: what used animal fat takes lard, tallow or animal fat
        ModTags.extra("animal_fats", "#" + NS + ":chemicals/animal_fat");
        ModTags.extra("animal_fats", NS + ":lard");
        ModTags.extra("animal_fats", NS + ":tallow");
        // cards: 猪油或牛油 (stock cube, curry roux)
        ModTags.extra("cooking_fats", NS + ":lard");
        ModTags.extra("cooking_fats", NS + ":tallow");
        // margarine: the emulsifier the card requires (monoglycerides, lecithin)
        ModTags.extra("food_emulsifiers", "#" + NS + ":chemicals/glyceryl_monostearate");
        ModTags.extra("food_emulsifiers", "#" + NS + ":chemicals/lecithin");
        // mustard: 白芥子或芥菜籽
        ModTags.extra("mustard_seeds", "#" + NS + ":materials/yellow_mustard_seed");
        ModTags.extra("mustard_seeds", "#" + NS + ":materials/mustard_seed");
        // meat stock: 生肉（鸡、猪、牛的标签）
        for (String meat : new String[]{"raw_chicken", "raw_pork", "raw_beef"}) {
            ModTags.extra("stock_meats", "#" + NS + ":materials/" + meat);
        }
        // raw materials with no source yet (foods/08 §6): oyster sauce, thirteen-spice
        ModTags.declare("materials/oyster");
        ModTags.declare("materials/amomum");
        // foods/08 §5: the bulk form of vegetable cooking oil is the vegetable oil tag, refined soybean oil in it
        ModTags.extraFluid("vegetable_oil", NS + ":refined_soybean_oil");
        // foods/08 §4.6 product groups, as items (packaged) and fluids (bulk)
        group("soy_sauce", "light_soy_sauce", "dark_soy_sauce", "premium_soy_sauce");
        group("vinegar", "rice_vinegar", "aged_vinegar", "apple_cider_vinegar", "white_vinegar");
        group("cooking_oil", "edible_oil", "peanut_oil", "sesame_oil");
        ModTags.extraFluid("foods/cooking_oil", "#" + NS + ":vegetable_oil");
        ModTags.extraFluid("foods/cooking_oil", NS + ":crude_peanut_oil");
        ModTags.extraFluid("foods/cooking_oil", NS + ":sesame_oil");
        for (String id : new String[]{"light_soy_sauce", "dark_soy_sauce", "premium_soy_sauce"}) {
            ModTags.extraFluid("foods/soy_sauce", NS + ":" + id);
        }
        for (String id : new String[]{"rice_vinegar", "aged_vinegar", "apple_cider_vinegar", "white_vinegar"}) {
            ModTags.extraFluid("foods/vinegar", NS + ":" + id);
        }
        // I30 left the immobilized enzymes out of their catalyst tags; the recipes name catalysts/<id>
        for (String enzyme : new String[]{"lipase", "glucose_isomerase", "aspartate_decarboxylase", "psicose_epimerase"}) {
            ModTags.extra("catalysts/immobilized_" + enzyme, "#" + NS + ":chemicals/immobilized_" + enzyme);
        }
    }

    private static void group(String group, String... products) {
        for (String product : products) {
            SharedTags.item(NS + ":foods/" + group, "#" + NS + ":foods/" + product);
        }
    }

    // ---- recipes ----

    private void generate() {
        workbench(new Workbench(out.sink(), "food1"));
        intermediates();
        fats();
        sweeteners();
        salts();
        soySauceAndVinegar();
        pastes();
        compound();
        spices();
    }

    /** foods/09 §2, foods/10 §1 and the ginger juice of foods/15 §2 (first used by cooking wine). */
    private void intermediates() {
        r(PRESSING).in("materials/sugar_cane", 4).fout("cane_juice", B).add("cane_juice");
        // citric acid as a catalyst (the pots, M03 and M15 have a catalyst slot from I47.5); its runs are a game setting
        r(BOILING).in("chemicals/sucrose", 2).fin("water", B).cat("chemicals/citric_acid", ENZYME_RUNS).fout("invert_syrup", B)
            .temp("heated").manual(STIRS).tier(0).add("invert_syrup");
        r(REACTING).in("chemicals/sucrose", 2).fin("water", B).cat("enzymes/invertase", ENZYME_RUNS).fout("invert_syrup", B)
            .mode("enzymolysis").temp("heated").tier(2).add("invert_syrup_by_invertase");
        r(BAKING).in("materials/sesame", 4).out("roasted_sesame", 4).mode("pan_roasting").temp("heated").manual(STIRS).tier(0)
            .add("roasted_sesame");
        r(EXTRACTING).in("materials/soybean", 4).fin("water", B).out("soaked_soybean", 4).tier(0).add("soaked_soybean");
        r(COOKING).in("materials/soaked_soybean", 4).out("steamed_soybean", 4).mode("steaming").temp("heated").manual(STIRS).tier(0)
            .add("steamed_soybean");
        r(GRINDING).in("materials/wheat", 1).out("wheat_flour", 1).out("wheat_bran", 1).chance(0.25)
            .manual(GRIND).tier(0).add("wheat_flour");
        r(SOLID_FERMENTING).in("materials/steamed_soybean", 3).in("materials/wheat_bran", 2).cat("cultures/aspergillus_oryzae", CULTURE_RUNS)
            .out("soy_koji", 4).add("soy_koji");
        r(COOKING).in("materials/sorghum", 4).fin("water", BOTTLE).out("steamed_sorghum", 4).mode("steaming").temp("heated").manual(STIRS)
            .tier(0).add("steamed_sorghum");
        // 2026-11-19 (user): barley 3 + wheat 1 pressed with water into bricks, inoculated from the air (the koji room offers no
        // atmosphere: the solid fermentations of this batch leave it out)
        r(SOLID_FERMENTING).in("materials/barley", 3).in("materials/wheat", 1).fin("water", BOTTLE).out("daqu", 4).add("daqu");
        r(PRESSING).in("materials/apple", 4).fout("raw_apple_juice", B).add("raw_apple_juice");
        r(COOKING).in("stock_meats", 2).fin("water", B).fout("meat_stock", B).mode("water_boiling").temp("heated").manual(STIRS).tier(0)
            .add("meat_stock");
        r(PRESSING).in("materials/ginger", 4).fout("ginger_juice", 500).add("ginger_juice");
    }

    /** foods/09 §3; lard and tallow replace the animal fat of Batch1Recipes (§5). */
    private void fats() {
        // lard and tallow (§3.1, §3.2): rendered in the pot, cracklings strained off
        r(BOILING).in("materials/raw_pork", 4).out("lard", 1).temp("heated").manual(STIRS).tier(0).add("lard");
        r(BOILING).in("materials/raw_beef", 4).out("tallow", 1).temp("heated").manual(STIRS).tier(0).add("tallow");
        // edible oil and peanut oil: the existing refined soybean oil and crude peanut oil bottled (§3.3, §3.4)
        bottle("edible_oil", "refined_soybean_oil");
        bottle("peanut_oil", "crude_peanut_oil");
        // sesame oil by water substitution (§3.5): ground to the bulk sesame paste, hot water floats the oil
        r(GRINDING).in("materials/roasted_sesame", 4).fout("sesame_paste", B).add("sesame_paste");
        r(EXTRACTING).fin("sesame_paste", B).fin("water", BOTTLE).fout("sesame_oil", 400).temp("heated").add("sesame_oil");
        bottle("sesame_oil", "sesame_oil");
        // margarine (§3.6): emulsion whipped, quick-chilled and kneaded, jarred
        r(MIXING).fin("refined_soybean_oil", 500).in("chemicals/hardened_oil", 1).fin("water", BOTTLE).in(SALT, 1)
            .in("food_emulsifiers", 1).fout("margarine_emulsion", B).mode("whipping")
            .quality("food", "color", "taste:" + DAIRY, "preservation").add("margarine_emulsion");
        r(CHILLING).fin("margarine_emulsion", BOTTLE).sout("margarine|block", 1).mode("churn_freezing").temp("cooling")
            .add("margarine_block");
        jar("margarine", "margarine|block", GLASS_JAR);
        // shortening (§3.7): hydrogenated oil, or animal fat, blended into soybean oil
        r(MIXING).fin("refined_soybean_oil", 500).in("chemicals/hardened_oil", 2).fout("shortening_blend", B).mode("blending")
            .quality("food", "state", "preservation").add("shortening_blend");
        r(MIXING).fin("refined_soybean_oil", 500).in("animal_fats", 2).fout("shortening_blend", B).mode("blending")
            .quality("food", "state", "preservation").add("shortening_blend_from_animal_fat");
        r(CHILLING).fin("shortening_blend", BOTTLE).sout("shortening|block", 1).mode("churn_freezing").temp("cooling")
            .add("shortening_block");
        jar("shortening", "shortening|block", GLASS_JAR);
        // non-dairy creamer (§3.8): emulsified on the high-shear head, homogenized, spray dried (T3), bagged
        r(MIXING).in("chemicals/hardened_oil", 1).fin("glucose_syrup", 500).in("chemicals/sodium_caseinate", 1).fin("water", 500)
            .fout("non_dairy_creamer_emulsion", B).mode("blending").method("emulsifying").tier(2)
            .quality("food", "state", "taste:" + DAIRY).add("non_dairy_creamer_emulsion");
        r(MIXING).fin("non_dairy_creamer_emulsion", B).fout("non_dairy_creamer_homogenized", B).mode("high_pressure_homogenizing").tier(2)
            .add("non_dairy_creamer_homogenized");
        r(DRYING).fin("non_dairy_creamer_homogenized", B).sout("non_dairy_creamer|powder", 4).temp("heated").heat("sensitive")
            .add("non_dairy_creamer_powder");
        jar("non_dairy_creamer", "non_dairy_creamer|powder", PAPER_BAG);
    }

    /** foods/09 §4. */
    private void sweeteners() {
        // soft white sugar (§4.1): sugar sprayed with invert syrup, dried
        r(MIXING).in("chemicals/sucrose", 8).fin("invert_syrup", BOTTLE).sout("soft_white_sugar|wet_sugar", 8).mode("blending")
            .quality("food").add("soft_white_sugar_wet");
        r(DRYING).semi("soft_white_sugar|wet_sugar", 8).out("soft_white_sugar", 8).tier(0).add("soft_white_sugar");
        // brown sugar (§4.2): cane juice boiled down over an open fire, stirred to a powder as it cools
        r(BOILING).fin("cane_juice", B).out("brown_sugar", 2).temp("heated").method("open_flame").manual(STIRS).tier(0).add("brown_sugar");
        // rock sugar (§4.3): dissolved and cleared with egg white, crystallized on cooling
        r(BOILING).in("chemicals/sucrose", 4).in("materials/egg_white", 1).fin("water", B).fout("rock_sugar_liquor", B)
            .temp("heated").manual(STIRS).tier(0).add("rock_sugar_liquor");
        r(CRYSTALLIZING).fin("rock_sugar_liquor", B).out("rock_sugar", 3).temp("cooling").add("rock_sugar");
        // sugar cube (§4.4; 2026-11-19: the tablet die): moistened sugar pressed, dried warm
        r(FORMING).in("chemicals/sucrose", 4).fin("water", 50).cat(TABLET_DIE, 64).sout("sugar_cube|wet_cube", 4).mode("molding")
            .add("sugar_cube_wet");
        r(DRYING).semi("sugar_cube|wet_cube", 1).out("sugar_cube", 1).temp("heated").add("sugar_cube");
        // icing sugar (§4.5): ground with starch (M01 and M11 take two solids from I47.5)
        r(GRINDING).in("chemicals/sucrose", 4).in("chemicals/starch", 1).out("icing_sugar", 4).add("icing_sugar");
        // maltose syrup (§4.6): steamed rice saccharified with malt, boiled down
        r(REACTING).in("materials/steamed_rice", 4).in("enzymes/malt", 1).fin("water", B).fout("maltose_syrup_wort", B)
            .mode("enzymolysis").temp("heated").manual(STIRS).tier(0).add("maltose_syrup_wort");
        r(BOILING).fin("maltose_syrup_wort", B).fout("maltose_syrup", 500).temp("heated").manual(STIRS).tier(0).add("maltose_syrup");
        bottle("maltose_syrup", "maltose_syrup");
        // glucose syrup (§4.7): the liquefied starch saccharified (formerly crystallized to glucose), decolourized, concentrated
        r(REACTING).fin("liquefied_starch", B).cat("enzymes/glucoamylase", ENZYME_RUNS).fout("glucose_syrup_saccharified", B)
            .mode("enzymolysis").temp("heated").tier(2).add("glucose_syrup_saccharified");
        r(PURIFYING).fin("glucose_syrup_saccharified", B).cat("catalysts/activated_carbon", CARBON_RUNS)
            .fout("glucose_syrup_decolorized", B).tier(0).add("glucose_syrup_decolorized");
        r(BOILING).fin("glucose_syrup_decolorized", B).fout("glucose_syrup", 500).temp("heated").manual(STIRS).tier(0).add("glucose_syrup");
        bottle("glucose_syrup", "glucose_syrup");
        // high-fructose syrup (§4.8): glucose syrup over the immobilized isomerase, decolourized, concentrated
        r(REACTING).fin("glucose_syrup", B).cat("catalysts/immobilized_glucose_isomerase", IMMOBILIZED_RUNS)
            .fout("high_fructose_syrup_isomerized", B).temp("heated").tier(2).add("high_fructose_syrup_isomerized");
        r(PURIFYING).fin("high_fructose_syrup_isomerized", B).cat("catalysts/activated_carbon", CARBON_RUNS)
            .fout("high_fructose_syrup_decolorized", B).tier(0).add("high_fructose_syrup_decolorized");
        r(BOILING).fin("high_fructose_syrup_decolorized", B).fout("high_fructose_syrup", 800).temp("heated").manual(STIRS).tier(0)
            .add("high_fructose_syrup");
        bottle("high_fructose_syrup", "high_fructose_syrup");
        // caramel syrup (§4.9): sugar melted dry over the fire, let down with water
        r(BOILING).in("chemicals/sucrose", 4).sout("caramel_syrup|molten_sugar", 4).temp("heated").method("open_flame")
            .manual(STIRS).tier(0).add("caramel_syrup_molten");
        r(MIXING).semi("caramel_syrup|molten_sugar", 4).fin("water", B).fout("caramel_syrup", B).mode("blending")
            .quality("food", "color", "taste:" + SWEET).add("caramel_syrup");
        bottle("caramel_syrup", "caramel_syrup");
        // tabletop sweetener (§4.10): sorbitol, sweeteners on the taste item, pressed into tablets
        r(MIXING).in("chemicals/sorbitol", 4).sout("tabletop_sweetener|powder", 4).mode("blending")
            .quality("food", "taste:sweetener").add("tabletop_sweetener_powder");
        r(FORMING).semi("tabletop_sweetener|powder", 1).cat(TABLET_DIE, 64).out("tabletop_sweetener", 1).mode("molding")
            .add("tabletop_sweetener");
    }

    /** foods/10 §2. */
    private void salts() {
        r(MIXING).in(SALT, 8).in("chemicals/potassium_iodate", 1).out("iodized_salt", 8).mode("blending")
            .quality("food", "state").add("iodized_salt");
        r(MIXING).in(SALT, 6).in("chemicals/potassium_chloride", 2).out("low_sodium_salt", 8).mode("blending")
            .quality("food", "state").add("low_sodium_salt");
        r(DRYING).in(SALT, 4).out("sea_salt", 4).tier(0).add("sea_salt");
    }

    /** foods/10 §3. */
    private void soySauceAndVinegar() {
        // light soy sauce (§3.1): koji in brine, sealed (the soybean paste ferments open); the mash pressed (R02; M02 takes a
        // fluid from I47.5), pasteurized
        r(FERMENTING).in("materials/soy_koji", 4).in(SALT, 2).fin("water", 2 * B).fout("light_soy_sauce_moromi", 2 * B).airtight()
            .tier(0).add("light_soy_sauce_moromi");
        r(PRESSING).fin("light_soy_sauce_moromi", 2 * B).fout("light_soy_sauce_raw", 1500).add("light_soy_sauce_raw");
        r(STERILIZING).fin("light_soy_sauce_raw", 1500).fout("light_soy_sauce", 1500).mode("pasteurizing").temp("heated").manual(STIRS)
            .tier(0).add("light_soy_sauce");
        bottle("light_soy_sauce", "light_soy_sauce");
        // dark soy sauce (§3.2): colour and preservative in, aged in the sun
        r(MIXING).fin("light_soy_sauce", B).fout("dark_soy_sauce_base", B).mode("blending").quality("food", "color", "preservation")
            .add("dark_soy_sauce_base");
        r(CURING).fin("dark_soy_sauce_base", B).fout("dark_soy_sauce", B).mode("aging").tier(0).add("dark_soy_sauce");
        bottle("dark_soy_sauce", "dark_soy_sauce");
        // premium soy sauce (§3.3, §9): light soy sauce with high-fructose syrup, flavour enhancers on the taste item
        r(MIXING).fin("light_soy_sauce", 750).fin("high_fructose_syrup", BOTTLE).fout("premium_soy_sauce", B).mode("blending")
            .quality("food", "taste", "preservation").add("premium_soy_sauce");
        bottle("premium_soy_sauce", "premium_soy_sauce");
        // rice vinegar (§3.4): sealed alcoholic fermentation on rice mould and brewer's yeast (two culture slots from I47.5), then
        // open acetic fermentation, pasteurized with the filtering merged in. Brewer's yeast comes only from the culture bench
        // (T4), so the rice mould alone still makes the wine mash (I47), until I44 gives the yeast an earlier source
        r(FERMENTING).in("materials/steamed_rice", 4).fin("water", 2 * B).cat("cultures/rhizopus", CULTURE_RUNS)
            .cat("cultures/yeast", CULTURE_RUNS).fout("rice_vinegar_wine_mash", 2 * B).airtight().tier(0).add("rice_vinegar_wine_mash");
        r(FERMENTING).in("materials/steamed_rice", 4).fin("water", 2 * B).cat("cultures/rhizopus", CULTURE_RUNS)
            .fout("rice_vinegar_wine_mash", 2 * B).airtight().tier(0).add("rice_vinegar_wine_mash_by_rhizopus");
        r(FERMENTING).fin("rice_vinegar_wine_mash", 2 * B).cat("cultures/acetobacter", CULTURE_RUNS).fout("rice_vinegar_acetic_mash", 2 * B)
            .air().tier(0).add("rice_vinegar_acetic_mash");
        r(STERILIZING).fin("rice_vinegar_acetic_mash", 2 * B).fout("rice_vinegar", 1500).mode("pasteurizing").temp("heated").manual(STIRS)
            .tier(0).add("rice_vinegar");
        bottle("rice_vinegar", "rice_vinegar");
        // aged vinegar (§3.5, five steps): solid alcoholic and acetic fermentation, half the grains smoked, leached together
        r(SOLID_FERMENTING).in("materials/steamed_sorghum", 4).in("materials/daqu", 1).sout("aged_vinegar|fermented_grains", 4)
            .add("aged_vinegar_fermented_grains");
        r(SOLID_FERMENTING).semi("aged_vinegar|fermented_grains", 4).in("materials/wheat_bran", 4).cat("cultures/acetobacter", CULTURE_RUNS)
            .sout("aged_vinegar|vinegar_grains", 4).add("aged_vinegar_vinegar_grains");
        r(FUMIGATING).semi("aged_vinegar|vinegar_grains", 2).sout("aged_vinegar|smoked_grains", 2).temp("heated").add("aged_vinegar_smoked_grains");
        r(EXTRACTING).semi("aged_vinegar|vinegar_grains", 2).semi("aged_vinegar|smoked_grains", 2).fin("water", B).fout("aged_vinegar", B)
            .tier(0).add("aged_vinegar");
        bottle("aged_vinegar", "aged_vinegar");
        // apple cider vinegar (§3.6): the apple juice fermented to cider (batch 7's bulk cider), then to vinegar
        r(FERMENTING).fin("raw_apple_juice", B).cat("cultures/yeast", CULTURE_RUNS).fout("apple_wine", B).airtight().tier(0)
            .add("apple_wine");
        // and on its own wild yeasts in a sealed crock, slower, as the fruit wines of the T0 chains (I12)
        r(FERMENTING).fin("raw_apple_juice", B).fout("apple_wine", B).airtight().time(48000).tier(0).add("apple_wine_wild");
        r(FERMENTING).fin("apple_wine", B).cat("cultures/acetobacter", CULTURE_RUNS).fout("apple_cider_vinegar", B).air().tier(0)
            .add("apple_cider_vinegar");
        bottle("apple_cider_vinegar", "apple_cider_vinegar");
        // white vinegar (§3.7): food-grade acetic acid let down
        r(MIXING).fin("acetic_acid", B).fout("white_vinegar", B).mode("blending").quality("food").add("white_vinegar");
        bottle("white_vinegar", "white_vinegar");
    }

    /** foods/10 §4. */
    private void pastes() {
        // doubanjiang (§4.1): bean koji, brine-fermented to the sweet mash, aged in the sun with salted chili
        r(SOLID_FERMENTING).in("materials/broad_bean", 4).in("materials/wheat_flour", 1).cat("cultures/aspergillus_oryzae", CULTURE_RUNS)
            .sout("doubanjiang|bean_koji", 4).add("doubanjiang_bean_koji");
        r(FERMENTING).semi("doubanjiang|bean_koji", 4).in(SALT, 2).fin("water", B).fout("doubanjiang_sweet_mash", B).tier(0)
            .add("doubanjiang_sweet_mash");
        r(CURING).fin("doubanjiang_sweet_mash", B).in("materials/chili", 4).in(SALT, 1).fout("doubanjiang", B).mode("aging").tier(0)
            .add("doubanjiang");
        bottle("doubanjiang", "doubanjiang", GLASS_JAR);
        // sweet bean sauce (§4.2): flour steamed to cakes, koji grown on them, brine-fermented
        r(COOKING).in("materials/wheat_flour", 4).fin("water", BOTTLE).sout("sweet_bean_sauce|dough_cake", 4).mode("steaming").temp("heated")
            .manual(STIRS).tier(0).add("sweet_bean_sauce_dough_cake");
        r(SOLID_FERMENTING).semi("sweet_bean_sauce|dough_cake", 4).cat("cultures/aspergillus_oryzae", CULTURE_RUNS)
            .sout("sweet_bean_sauce|koji_cake", 4).add("sweet_bean_sauce_koji_cake");
        r(FERMENTING).semi("sweet_bean_sauce|koji_cake", 4).in(SALT, 1).fin("water", B).fout("sweet_bean_sauce", B).tier(0)
            .add("sweet_bean_sauce");
        bottle("sweet_bean_sauce", "sweet_bean_sauce", GLASS_JAR);
        // soybean paste (§4.3): the soy koji in less brine, fermented open (the soy sauce ferments sealed)
        r(FERMENTING).in("materials/soy_koji", 4).in(SALT, 2).fin("water", 500).fout("soybean_paste", B).air().tier(0)
            .add("soybean_paste");
        bottle("soybean_paste", "soybean_paste", GLASS_JAR);
    }

    /** foods/10 §5. */
    private void compound() {
        // ketchup (§5.1)
        r(GRINDING).in("materials/tomato", 8).fout("ketchup_puree", B).mode("wet_grinding").add("ketchup_puree");
        r(BOILING).fin("ketchup_puree", B).fout("ketchup_concentrate", 500).temp("heated").manual(STIRS).tier(0).add("ketchup_concentrate");
        r(MIXING).fin("ketchup_concentrate", 500).in("chemicals/sucrose", 1).fin("foods/vinegar", BOTTLE).in(SALT, 1)
            .fout("ketchup", 750).mode("blending").quality("food", "taste", "state", "preservation").add("ketchup");
        bottle("ketchup", "ketchup");
        // salad dressing (§5.2): whipped with the emulsifying merged in
        r(MIXING).fin("vegetable_oil", 500).in("materials/egg_yolk", 2).fin("foods/vinegar", BOTTLE).in("chemicals/sucrose", 1)
            .in("chemicals/starch", 1).fin("water", BOTTLE).fout("salad_dressing", B).mode("whipping")
            .quality("food", "taste", "state", "preservation").add("salad_dressing");
        bottle("salad_dressing", "salad_dressing", GLASS_JAR);
        // mayonnaise (§5.3)
        r(MIXING).fin("vegetable_oil", 750).in("materials/egg_yolk", 2).fin("foods/vinegar", 50).in(SALT, 1)
            .fout("mayonnaise", B).mode("whipping").quality("food", "taste", "preservation").add("mayonnaise");
        bottle("mayonnaise", "mayonnaise", GLASS_JAR);
        // mustard (§5.4)
        r(GRINDING).in("mustard_seeds", 4).sout("mustard|mustard_powder", 4).manual(GRIND).tier(0).add("mustard_powder");
        r(MIXING).semi("mustard|mustard_powder", 4).fin("water", 500).fin("foods/vinegar", BOTTLE).in(SALT, 1).fout("mustard", B)
            .mode("blending").quality("food", "color", "taste").add("mustard");
        bottle("mustard", "mustard", GLASS_JAR);
        // chili sauce (§5.5)
        r(CURING).in("materials/chili", 8).in(SALT, 1).sout("chili_sauce|chili_mash", 4).mode("marinating").tier(0)
            .add("chili_sauce_mash");
        r(BOILING).semi("chili_sauce|chili_mash", 4).fin("water", BOTTLE).fout("chili_sauce_base", B).temp("heated").manual(STIRS).tier(0)
            .add("chili_sauce_base");
        r(MIXING).fin("chili_sauce_base", B).fout("chili_sauce", B).mode("blending").quality("food", "preservation").add("chili_sauce");
        bottle("chili_sauce", "chili_sauce", GLASS_JAR);
        // oyster sauce (§5.6; oysters have no source yet, foods/08 §6)
        r(BOILING).in("materials/oyster", 4).fin("water", B).fout("oyster_sauce_extract", 500).temp("heated").manual(STIRS).tier(0)
            .add("oyster_sauce_extract");
        r(MIXING).fin("oyster_sauce_extract", 500).in("chemicals/sucrose", 1).in(SALT, 1).in("chemicals/starch", 1)
            .fout("oyster_sauce_base", B).mode("blending").quality("food", "color", "taste:flavor_enhancer," + SAVORY).add("oyster_sauce_base");
        r(BOILING).fin("oyster_sauce_base", B).fout("oyster_sauce", B).temp("heated").manual(STIRS).tier(0).add("oyster_sauce");
        bottle("oyster_sauce", "oyster_sauce");
        // chicken bouillon powder (§5.7)
        r(MIXING).in("chemicals/monosodium_glutamate", 4).in(SALT, 2).fin("meat_stock", BOTTLE).in("chemicals/starch", 1)
            .sout("chicken_bouillon_powder|wet_mix", 4).mode("blending").quality("food", "taste:flavor_enhancer," + SAVORY)
            .add("chicken_bouillon_powder_wet_mix");
        r(FORMING).semi("chicken_bouillon_powder|wet_mix", 4).cat(GRANULATING_SCREEN, 64).sout("chicken_bouillon_powder|wet_granules", 4)
            .mode("molding").add("chicken_bouillon_powder_wet_granules");
        r(DRYING).semi("chicken_bouillon_powder|wet_granules", 1).sout("chicken_bouillon_powder|granules", 1).tier(0)
            .add("chicken_bouillon_powder_granules");
        jar("chicken_bouillon_powder", "chicken_bouillon_powder|granules", PAPER_BAG);
        // stock cube (§5.8)
        r(BOILING).fin("meat_stock", B).sout("stock_cube|paste", 2).temp("heated").manual(STIRS).tier(0).add("stock_cube_paste");
        r(MIXING).semi("stock_cube|paste", 2).in(SALT, 2).in("cooking_fats", 1).sout("stock_cube|mix", 4).mode("blending")
            .quality("food", "color", "taste:flavor_enhancer," + SAVORY).add("stock_cube_mix");
        r(FORMING).semi("stock_cube|mix", 1).cat(TABLET_DIE, 64).out("stock_cube", 1).mode("molding").add("stock_cube");
        // kombu dashi (§5.9): cold steeping
        r(EXTRACTING).in("materials/dried_kelp", 1).fin("water", B).fout("kombu_dashi", B).tier(0).add("kombu_dashi");
        bottle("kombu_dashi", "kombu_dashi");
        // master stock (§5.10)
        r(MIXING).fin("foods/soy_sauce", BOTTLE).in("chemicals/sucrose", 1).in("materials/star_anise", 1).in("materials/cinnamon", 1)
            .fin("water", 750).fout("master_stock_raw", B).mode("blending").quality("food", "color").add("master_stock_raw");
        r(BOILING).fin("master_stock_raw", B).fout("master_stock", B).temp("heated").manual(STIRS).tier(0).add("master_stock");
        bottle("master_stock", "master_stock");
        // huangjiu (foods/22 §2.2, brought forward for the cooking wine: foods/23 §6, 2026-11-25 user): wheat qu cultured like
        // the daqu; steamed glutinous rice, wheat qu and water fermented sealed on brewer's yeast; pressed, the raw wine
        // (the bulk huangjiu) bottled and pasteurized in the bottle (煎酒)
        r(SOLID_FERMENTING).in("materials/wheat", 4).fin("water", BOTTLE).out("wheat_qu", 4).add("wheat_qu");
        r(FERMENTING).in("materials/steamed_glutinous_rice", 4).in("materials/wheat_qu", 1).fin("water", B).cat("cultures/yeast", CULTURE_RUNS)
            .fout("huangjiu_mash", B).airtight().tier(0).add("huangjiu_mash");
        r(SEPARATING).fin("huangjiu_mash", B).fout("huangjiu", 750).out("vinasse", 1).add("huangjiu_pressing");
        r(PACKAGING).fin("huangjiu", BOTTLE).in(GLASS_BOTTLE, 1).sout("huangjiu|sealed", 1).add("huangjiu_sealed");
        r(STERILIZING).semi("huangjiu|sealed", 1).out("huangjiu", 1).mode("pasteurizing").temp("heated").add("huangjiu");
        // cooking wine (§5.11): on the bulk huangjiu above
        r(MIXING).fin("huangjiu", 750).in(SALT, 1).fin("ginger_juice", BOTTLE).fout("cooking_wine", B).mode("blending")
            .quality("food").add("cooking_wine");
        bottle("cooking_wine", "cooking_wine");
        // hotpot base (§5.12): stir-fried in tallow over an open flame (R05; the iron pot and M03 take four solids from I47.5)
        r(BOILING).in("foods/tallow", 4).fin("doubanjiang", BOTTLE).in("materials/dried_chili", 2).in("materials/sichuan_pepper", 1)
            .fout("hotpot_base_melt", B).temp("heated").method("open_flame").manual(STIRS).tier(0).add("hotpot_base_melt");
        r(CHILLING).fin("hotpot_base_melt", BOTTLE).out("hotpot_base", 1).mode("chilling").temp("cooling").tier(0).add("hotpot_base");
        // curry roux (§5.13)
        r(BOILING).in("cooking_fats", 2).in("materials/wheat_flour", 2).sout("curry_roux|roux", 4).temp("heated").method("open_flame")
            .manual(STIRS).tier(0).add("curry_roux_roux");
        r(MIXING).semi("curry_roux|roux", 4).in("materials/dried_turmeric", 1).in("materials/coriander_seed", 1).in("materials/cumin_seed", 1)
            .in(SALT, 1).sout("curry_roux|mix", 4).mode("blending").quality("food", "state", "taste").add("curry_roux_mix");
        r(CHILLING).semi("curry_roux|mix", 1).out("curry_roux", 1).mode("chilling").temp("cooling").tier(0).add("curry_roux");
    }

    /** foods/10 §6; the two thirteen-spice blends take seven and eight solids, which the ingredient-step devices hold from I47.5. */
    private void spices() {
        r(MIXING).in("materials/sichuan_pepper", 1).in("materials/star_anise", 1).in("materials/cinnamon", 1).in("materials/clove_bud", 1)
            .in("materials/fennel_seed", 1).out("five_spice_powder", 5).mode("blending").add("five_spice_powder");
        r(MIXING).in("materials/star_anise", 1).in("materials/fennel_seed", 1).in("materials/sichuan_pepper", 1)
            .in("materials/galangal_root", 1).in("materials/citrus_peel", 1).in("materials/black_pepper", 1).in("materials/nutmeg", 1)
            .sout("thirteen_spice|spice_blend_a", 1).mode("blending").add("thirteen_spice_blend_a");
        r(MIXING).semi("thirteen_spice|spice_blend_a", 1).in("materials/cinnamon", 1).in("materials/ginger", 1)
            .in("materials/hawthorn_fruit", 1).in("materials/licorice_root", 1).in("materials/amomum", 1).in("materials/clove_bud", 1)
            .in("materials/angelica_dahurica_root", 1).out("thirteen_spice", 8).mode("blending").add("thirteen_spice");
        r(BOILING).fin("foods/cooking_oil", B).in("materials/dried_chili", 2).fout("chili_oil", B).temp("heated").method("open_flame")
            .manual(STIRS).tier(0).add("chili_oil");
        bottle("chili_oil", "chili_oil");
        r(EXTRACTING).fin("foods/cooking_oil", B).in("materials/sichuan_pepper", 2).fout("sichuan_pepper_oil", B).temp("heated")
            .manual(STIRS).tier(0).add("sichuan_pepper_oil");
        bottle("sichuan_pepper_oil", "sichuan_pepper_oil");
    }

    /** R29: a bottle of a liquid product from its bulk fluid. */
    private void bottle(String product, String fluid) {
        bottle(product, fluid, GLASS_BOTTLE);
    }

    private void bottle(String product, String fluid, String container) {
        r(PACKAGING).fin(fluid, BOTTLE).in(container, 1).out(product, 1).add(product + "_packaging");
    }

    /** R29: a solid product packed from its last semi-finished stage. */
    private void jar(String product, String semi, String container) {
        r(PACKAGING).semi(semi, 1).in(container, 1).out(product, 1).add(product + "_packaging");
    }

    // ---- the crafting table (T0 of R19, R20, R29) ----

    private static void workbench(Workbench w) {
        w.of(MIXING).in(SALT, 8).in("chemicals/potassium_iodate", 1).out("iodized_salt", 8).quality("food", "state")
            .add("iodized_salt");
        w.of(MIXING).in(SALT, 6).in("chemicals/potassium_chloride", 2).out("low_sodium_salt", 8).quality("food", "state")
            .add("low_sodium_salt");
        w.of(MIXING).in("chemicals/sorbitol", 4).sout("tabletop_sweetener|powder", 4).quality("food", "taste:sweetener")
            .add("tabletop_sweetener_powder");
        w.of(FORMING).semi("tabletop_sweetener|powder", 1).tool(TABLET_DIE).out("tabletop_sweetener", 1).add("tabletop_sweetener");
        w.of(FORMING).semi("chicken_bouillon_powder|wet_mix", 4).tool(GRANULATING_SCREEN).sout("chicken_bouillon_powder|wet_granules", 4)
            .add("chicken_bouillon_powder_wet_granules");
        w.of(MIXING).semi("stock_cube|paste", 2).in(SALT, 2).in("cooking_fats", 1).sout("stock_cube|mix", 4)
            .quality("food", "color", "taste:flavor_enhancer," + SAVORY).add("stock_cube_mix");
        w.of(FORMING).semi("stock_cube|mix", 1).tool(TABLET_DIE).out("stock_cube", 1).add("stock_cube");
        w.of(MIXING).semi("curry_roux|roux", 4).in("materials/dried_turmeric", 1).in("materials/coriander_seed", 1)
            .in("materials/cumin_seed", 1).in(SALT, 1).sout("curry_roux|mix", 4).quality("food", "state", "taste").add("curry_roux_mix");
        w.of(MIXING).in("materials/sichuan_pepper", 1).in("materials/star_anise", 1).in("materials/cinnamon", 1).in("materials/clove_bud", 1)
            .in("materials/fennel_seed", 1).out("five_spice_powder", 5).add("five_spice_powder");
        // thirteen-spice (foods/10 §6.2, §8 #3): fourteen spices in two blends; dried ginger stands in as ginger
        w.of(MIXING).in("materials/star_anise", 1).in("materials/fennel_seed", 1).in("materials/sichuan_pepper", 1)
            .in("materials/galangal_root", 1).in("materials/citrus_peel", 1).in("materials/black_pepper", 1).in("materials/nutmeg", 1)
            .sout("thirteen_spice|spice_blend_a", 1).add("thirteen_spice_blend_a");
        w.of(MIXING).semi("thirteen_spice|spice_blend_a", 1).in("materials/cinnamon", 1).in("materials/ginger", 1)
            .in("materials/hawthorn_fruit", 1).in("materials/licorice_root", 1).in("materials/amomum", 1).in("materials/clove_bud", 1)
            .in("materials/angelica_dahurica_root", 1).out("thirteen_spice", 8).add("thirteen_spice");
        // R29 by hand: a bucket of the bulk fluid fills four bottles or jars; a solid stage one container
        String[][] liquids = {
            {"edible_oil", "refined_soybean_oil"}, {"peanut_oil", "crude_peanut_oil"}, {"sesame_oil", "sesame_oil"},
            {"maltose_syrup", "maltose_syrup"}, {"glucose_syrup", "glucose_syrup"}, {"high_fructose_syrup", "high_fructose_syrup"},
            {"caramel_syrup", "caramel_syrup"}, {"light_soy_sauce", "light_soy_sauce"}, {"dark_soy_sauce", "dark_soy_sauce"},
            {"premium_soy_sauce", "premium_soy_sauce"}, {"rice_vinegar", "rice_vinegar"}, {"aged_vinegar", "aged_vinegar"},
            {"apple_cider_vinegar", "apple_cider_vinegar"}, {"white_vinegar", "white_vinegar"}, {"ketchup", "ketchup"},
            {"oyster_sauce", "oyster_sauce"}, {"kombu_dashi", "kombu_dashi"}, {"master_stock", "master_stock"},
            {"cooking_wine", "cooking_wine"}, {"chili_oil", "chili_oil"}, {"sichuan_pepper_oil", "sichuan_pepper_oil"}};
        for (String[] liquid : liquids) {
            w.of(PACKAGING).in(NS + ":" + liquid[1] + "_bucket", 1).in(GLASS_BOTTLE, 4).out(liquid[0], 4).add(liquid[0] + "_packaging");
        }
        for (String paste : new String[]{"doubanjiang", "sweet_bean_sauce", "soybean_paste", "salad_dressing", "mayonnaise", "mustard",
            "chili_sauce"}) {
            w.of(PACKAGING).in(NS + ":" + paste + "_bucket", 1).in(GLASS_JAR, 4).out(paste, 4).add(paste + "_packaging");
        }
        w.of(PACKAGING).semi("margarine|block", 1).in(GLASS_JAR, 1).out("margarine", 1).add("margarine_packaging");
        w.of(PACKAGING).semi("shortening|block", 1).in(GLASS_JAR, 1).out("shortening", 1).add("shortening_packaging");
        w.of(PACKAGING).semi("non_dairy_creamer|powder", 1).in(PAPER_BAG, 1).out("non_dairy_creamer", 1).add("non_dairy_creamer_packaging");
        w.of(PACKAGING).semi("chicken_bouillon_powder|granules", 1).in(PAPER_BAG, 1).out("chicken_bouillon_powder", 1)
            .add("chicken_bouillon_powder_packaging");
    }
}
