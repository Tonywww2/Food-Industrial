package com.tonywww.foodindustrial.content.food3;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.content.Batch;
import com.tonywww.foodindustrial.content.ContentRecipe;
import com.tonywww.foodindustrial.content.Workbench;
import com.tonywww.foodindustrial.food.SemiFinished;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeGen;
import com.tonywww.foodindustrial.registry.SharedTags;

import java.util.ArrayList;
import java.util.List;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.BAKING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.BOILING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CHILLING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CONFECTIONERY;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.COOKING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CURING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.CUTTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.DRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.EXTRACTING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FORMING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.FRYING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.GRINDING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.MIXING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.PACKAGING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.SEPARATING;
import static com.tonywww.foodindustrial.recipe.ProcessTypes.STERILIZING;

/**
 * The third batch of finished products (I49; foods/13 grain products, foods/14 bakery): 63 products, the intermediates they
 * share and the steps of each card, as recipes {@code foodindustrial:food3/<name>}; the crafting-table steps (T0 of R19, R20,
 * R26, R27 and R29) as {@code foodindustrial:food3/workbench/<name>}.
 * <p>
 * The T0 ways: the pots boil, steam, fry and pasteurize with stirs; the crock proofs, marinates, steeps, settles and chills; the
 * drying rack dries and, in the cold, freezes; campfire, furnace and smoker bake what goes in one and comes out one; the
 * crafting table does the rest that it can hold (nine slots; knives and molds stay in the grid, brewer's yeast is used up,
 * 2026-11-19 user). Amounts are the cards'; what the cards leave open is a game setting: water to boil in a bottle (250 mB),
 * 50 mB of oil to a frying, 64 runs of a mold or knife in a machine, 8 of the yeast.
 */
public final class Food3Recipes extends Batch {
    private static final String NS = FoodIndustrial.MOD_ID;
    /** One bottle; also the water a boiling step takes when the card gives none. */
    private static final int BOTTLE = 250;
    /** Stirs of a pot step (as I12, I47). */
    private static final int STIRS = 8;
    /** Actions of the mortar. */
    private static final int GRIND = 8;
    /** Runs of a mold or knife in a machine (as the tablet die, I47). */
    private static final int MOLD = 64;
    /** Oil one frying takes (game setting; the cards name the oil only). */
    private static final int FRY_OIL = 50;

    private static final String SALT = "salt_position";
    private static final String WATER = "water";
    private static final String MILK = "milk";
    /** 食用油（植物油标签）, foods/13 §3.6. */
    private static final String OIL = "vegetable_oil";
    /** 枧水: soda lye or potash lye (foods/08 §5). */
    private static final String KANSUI = "kansui";
    /** 蓬灰 (plant ash) or potassium carbonate (foods/13 §3.3). */
    private static final String LAMIAN_ALKALI = "lamian_alkali";
    private static final String YEAST = "cultures/yeast";
    private static final String KNIFE = "knives";
    private static final String FLOUR = "materials/wheat_flour";
    private static final String SUGAR = "chemicals/sucrose";
    private static final String EGG = "materials/egg";
    private static final String STARCH = "chemicals/starch";
    private static final String SODA = "chemicals/sodium_bicarbonate";
    private static final String BUTTER = "foods/butter";
    private static final String LARD = "foods/lard";
    private static final String SHORTENING = "foods/shortening";

    private static final String FILLING_MOLD = "molds/filling_mold";
    private static final String STRETCHING_MOLD = "molds/stretching_mold";
    private static final String EXTRUSION_DIE = "molds/extrusion_die";
    private static final String SCREEN = "molds/granulating_screen";
    private static final String CAKE_MOLD = "molds/cake_mold";
    private static final String LOAF_PAN = "molds/loaf_pan";
    private static final String TART_PAN = "molds/tart_pan";
    private static final String BAKING_IRON = "molds/baking_iron";
    private static final String MOONCAKE_MOLD = "molds/mooncake_mold";
    private static final String PAPER_BAG = "containers/paper_bag";
    private static final String PLASTIC_BAG = "containers/plastic_bag";
    private static final String GLASS_JAR = "containers/glass_jar";

    /** The cards' quality items (foods/04 §2): the classes each card names, so that no other step's input passes as an additive. */
    private static final String LEAVENED = "state:leavening,sodium_acid_pyrophosphate";
    private static final String SWEET = "taste:sweet_flavoring";
    private static final String FRUITY = "taste:fruity_flavoring";

    private final Workbench bench;
    /** Steps the crafting table cannot take (more than nine slots), for the record. */
    private final List<String> machineOnly = new ArrayList<>();

    private Food3Recipes(RecipeGen.Output out) {
        super(out, "food3");
        bench = new Workbench(out.sink(), "food3");
    }

    /** Registers stages, tags and the generator. Called during mod construction. */
    public static void bootstrap() {
        stages();
        tags();
        RecipeGen.register(out -> new Food3Recipes(out).generate());
    }

    // ---- stages and tags ----

    private static void stages() {
        String[][] stages = {
            {"bulk", "散装", "Bulk"}, {"dough", "面团", "Dough"}, {"wet_noodles", "湿面", "Wet Noodles"},
            {"dry_noodles", "干面", "Dried Noodles"}, {"raw_noodles", "生面", "Raw Noodles"}, {"steamed_noodles", "蒸面", "Steamed Noodles"},
            {"noodle_cake", "面饼", "Noodle Block"}, {"raw", "生", "Raw"}, {"filling", "馅", "Filling"}, {"blank", "生坯", "Shaped"},
            {"frozen_dumpling", "冻饺", "Frozen Dumplings"}, {"frozen", "冻", "Frozen"}, {"cake_blank", "糕坯", "Cake Block"},
            {"wet_vermicelli", "湿粉", "Wet Vermicelli"}, {"dry_vermicelli", "干粉", "Dried Vermicelli"}, {"rice_sheet", "粉皮", "Rice Sheet"},
            {"raw_strands", "生丝", "Raw Strands"}, {"cooked_strands", "熟丝", "Cooked Strands"}, {"dry_strands", "干丝", "Dried Strands"},
            {"raw_strips", "生条", "Raw Strips"}, {"cooked_strips", "熟条", "Cooked Strips"}, {"dry_strips", "干条", "Dried Strips"},
            {"dough_sheet", "面皮", "Dough Sheet"}, {"starch_paste", "粉浆", "Starch Paste"}, {"jelly_block", "粉坨", "Jelly Block"},
            {"lye_rice", "碱米", "Lye-soaked Rice"}, {"raw_zongzi", "生粽", "Raw Zongzi"}, {"proofed_dough", "醒好的面团", "Proofed Dough"},
            {"dough_strands", "面条坯", "Dough Ropes"}, {"proofed_blank", "醒好的生坯", "Proofed Shape"}, {"batter", "面糊", "Batter"},
            {"risen_batter", "发好的面糊", "Risen Batter"}, {"steamed_oats", "蒸麦", "Steamed Oats"}, {"wet_flakes", "湿片", "Wet Flakes"},
            {"dry_flakes", "干片", "Dried Flakes"}, {"mix", "料", "Mix"}, {"puffed", "膨化料", "Puffed Mix"}, {"toasted", "烘干料", "Toasted Mix"},
            {"coated", "糖衣片", "Sugar-coated Flakes"}, {"congee", "米粥", "Rice Porridge"}, {"raw_pearls", "生圆", "Raw Pearls"},
            {"dry_pearls", "干圆", "Dried Pearls"}, {"rice_dough", "糯米面团", "Rice Dough"}, {"sesame_filling", "芝麻馅", "Sesame Filling"},
            {"loaf", "整条", "Whole Loaf"}, {"poolish", "波兰种", "Poolish"}, {"laminated", "酥坯", "Laminated Dough"},
            {"boiled", "烫坯", "Boiled"}, {"lye_dipped", "碱坯", "Lye-dipped"}, {"fried", "炸圈", "Fried Ring"}, {"baked", "烤好", "Baked"},
            {"whipped_cream", "打发奶油", "Whipped Cream"}, {"nut_filling", "五仁馅", "Five-nut Filling"}, {"pastry", "酥皮", "Pastry"},
            {"pancake", "饼", "Pancake"}, {"risen_dough", "发好的面团", "Risen Dough"}, {"wafer_sheet", "威化片", "Wafer Sheet"},
            {"biscuit", "饼片", "Biscuit"}, {"choux_paste", "烫面", "Choux Paste"}, {"shell", "壳", "Shell"}, {"tart_shell", "挞皮", "Tart Shell"},
            {"bean_mash", "豆泥", "Bean Mash"}};
        for (String[] stage : stages) {
            SemiFinished.stage(stage[0], stage[1], stage[2]);
        }
    }

    private static void tags() {
        // 蓬灰 (plant ash, foods/08 §5) or potassium carbonate, the lamian alkali
        ModTags.extra(LAMIAN_ALKALI, "#" + NS + ":chemicals/wood_ash");
        ModTags.extra(LAMIAN_ALKALI, "#" + NS + ":chemicals/potassium_carbonate");
        // 枧水: the existing soda lye and potash lye (foods/08 §5)
        ModTags.extraFluid(KANSUI, "#" + NS + ":soda_lye");
        ModTags.extraFluid(KANSUI, "#" + NS + ":potash_lye");
        // foods/14 §7 #5, #6: group tags
        group("cake_base", "sponge_cake", "chiffon_cake");
        group("biscuits", "taosu", "butter_cookie", "soda_cracker");
    }

    private static void group(String group, String... products) {
        for (String product : products) {
            SharedTags.item(NS + ":foods/" + group, "#" + NS + ":foods/" + product);
        }
    }

    // ---- recipes ----

    private void generate() {
        intermediates();
        noodles();
        dumplings();
        riceAndStarch();
        friedAndSteamed();
        cereals();
        bakeryIntermediates();
        breads();
        cakes();
        chinesePastries();
        biscuitsAndPastries();
        FoodIndustrial.LOGGER.debug("food3: crafting table cannot take {}", machineOnly);
    }

    /** foods/13 §1.1, §2: the shared intermediates of the grain products. */
    private void intermediates() {
        // 糯米: the glutinous paddy hulled like paddy (Batch4Others), bran as by-product
        r(GRINDING).in("materials/glutinous_paddy", 4).out("glutinous_rice", 3).out("rice_bran", 1).add("glutinous_rice");
        r(GRINDING).in("materials/glutinous_rice", 1).out("glutinous_rice_flour", 1).manual(GRIND).tier(0).add("glutinous_rice_flour");
        r(GRINDING).in("materials/rice", 4).fin(WATER, B).fout("rice_slurry", B).mode("wet_grinding").add("rice_slurry");
        steam().in("materials/glutinous_rice", 4).fin(WATER, BOTTLE).out("steamed_glutinous_rice", 4).add("steamed_glutinous_rice");
        step(CUTTING, "chopping").in("materials/raw_pork", 1).tool(KNIFE).out("minced_pork", 1).add("minced_pork");
        step(MIXING, "blending").in("materials/minced_pork", 2).in("materials/leafy_greens", 2).fin("light_soy_sauce", BOTTLE).in(SALT, 1)
            .out("meat_filling", 4).quality("taste:flavor_enhancer", "state:thickener").add("meat_filling");
        step(FORMING, "sheeting").in(FLOUR, 4).fin(WATER, BOTTLE).out("dumpling_wrapper", 8).add("dumpling_wrapper");
        step(FORMING, "sheeting").in(FLOUR, 4).in("materials/egg_white", 2).out("wonton_wrapper", 8).add("wonton_wrapper");
        step(FORMING, "molding").in("materials/dumpling_wrapper", 2).in("materials/meat_filling", 1).tool(FILLING_MOLD)
            .out("raw_dumplings", 2).add("raw_dumplings");
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(WATER, BOTTLE).in(SALT, 1).out("wheat_dough", 4)
            .quality("state:thickener,flour_treatment").add("wheat_dough");
        step(FORMING, "dough_mixing").in(STARCH, 4).fin(WATER, BOTTLE).out("starch_dough", 4).quality("state").add("starch_dough");
        steam().fin("rice_slurry", B).out("steamed_rice_dough", 2).add("steamed_rice_dough");
        // 发面团: mixed with brewer's yeast, proofed
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(WATER, BOTTLE).yeast().sout("leavened_dough|dough", 4).quality()
            .add("leavened_dough_mixed");
        proof().semi("leavened_dough|dough", 4).out("leavened_dough", 4).add("leavened_dough");
        // 生汤圆: rice dough wrapped round the sesame filling
        step(FORMING, "dough_mixing").in("materials/glutinous_rice_flour", 4).fin(WATER, BOTTLE).sout("raw_tangyuan|rice_dough", 4)
            .quality().add("raw_tangyuan_dough");
        step(MIXING, "blending").in("materials/roasted_sesame", 2).in(SUGAR, 1).in(LARD, 1).sout("raw_tangyuan|sesame_filling", 4)
            .add("raw_tangyuan_filling");
        step(FORMING, "molding").semi("raw_tangyuan|rice_dough", 2).semi("raw_tangyuan|sesame_filling", 1).tool(FILLING_MOLD)
            .out("raw_tangyuan", 4).add("raw_tangyuan");
    }

    /** foods/13 §3. */
    private void noodles() {
        // 3.1 self-rising flour
        step(MIXING, "blending").in(FLOUR, 8).in(SODA, 1).in("chemicals/sodium_acid_pyrophosphate", 1).in(SALT, 1)
            .sout("self_rising_flour|bulk", 8).quality().add("self_rising_flour_bulk");
        pack("self_rising_flour", "self_rising_flour|bulk", 1, PAPER_BAG);
        // 3.2 dried noodles
        step(FORMING, "slitting").in("materials/wheat_dough", 1).sout("dried_noodles|wet_noodles", 1).add("dried_noodles_wet");
        dry().semi("dried_noodles|wet_noodles", 1).sout("dried_noodles|dry_noodles", 1).add("dried_noodles_dry");
        pack("dried_noodles", "dried_noodles|dry_noodles", 1, PAPER_BAG);
        // 3.3 Lanzhou pulled noodles: the alkali the pulling needs
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(WATER, BOTTLE).in(SALT, 1).in(LAMIAN_ALKALI, 1)
            .sout("lanzhou_pulled_noodles|dough", 4).quality().add("lanzhou_pulled_noodles_dough");
        step(FORMING, "molding").semi("lanzhou_pulled_noodles|dough", 1).tool(STRETCHING_MOLD).sout("lanzhou_pulled_noodles|raw_noodles", 1)
            .add("lanzhou_pulled_noodles_raw");
        boil().semi("lanzhou_pulled_noodles|raw_noodles", 1).fin(WATER, BOTTLE).out("lanzhou_pulled_noodles", 1).add("lanzhou_pulled_noodles");
        // 3.4 alkaline noodles
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(KANSUI, BOTTLE).in(SALT, 1).sout("alkaline_noodles|dough", 4).quality()
            .add("alkaline_noodles_dough");
        step(FORMING, "slitting").semi("alkaline_noodles|dough", 1).sout("alkaline_noodles|wet_noodles", 1).add("alkaline_noodles_wet");
        pack("alkaline_noodles", "alkaline_noodles|wet_noodles", 1, PLASTIC_BAG);
        // 3.5 egg noodles
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(EGG, 1).fin(WATER, BOTTLE).sout("egg_noodles|dough", 4).quality("state:emulsifier")
            .add("egg_noodles_dough");
        step(FORMING, "slitting").semi("egg_noodles|dough", 1).sout("egg_noodles|wet_noodles", 1).add("egg_noodles_wet");
        dry().semi("egg_noodles|wet_noodles", 1).sout("egg_noodles|dry_noodles", 1).add("egg_noodles_dry");
        pack("egg_noodles", "egg_noodles|dry_noodles", 1, PAPER_BAG);
        // 3.6 instant noodles: steamed and fried are two recipe types, five steps
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(WATER, BOTTLE).in(SALT, 1).in("chemicals/soda_ash", 1)
            .sout("instant_noodles|dough", 4).quality("state:humectant,emulsifier").add("instant_noodles_dough");
        step(FORMING, "slitting").semi("instant_noodles|dough", 1).sout("instant_noodles|raw_noodles", 1).add("instant_noodles_raw");
        steam().semi("instant_noodles|raw_noodles", 1).sout("instant_noodles|steamed_noodles", 1).add("instant_noodles_steamed");
        fry().semi("instant_noodles|steamed_noodles", 1).fin(OIL, FRY_OIL).sout("instant_noodles|noodle_cake", 1).add("instant_noodles_fried");
        pack("instant_noodles", "instant_noodles|noodle_cake", 1, PLASTIC_BAG);
        // 3.7 knife-cut noodles
        step(CUTTING, "slicing").in("materials/wheat_dough", 1).tool(KNIFE).sout("knife_cut_noodles|raw_noodles", 1).add("knife_cut_noodles_raw");
        boil().semi("knife_cut_noodles|raw_noodles", 1).fin(WATER, BOTTLE).out("knife_cut_noodles", 1).add("knife_cut_noodles");
    }

    /** foods/13 §4. */
    private void dumplings() {
        boil().in("materials/raw_dumplings", 4).fin(WATER, BOTTLE).out("dumplings", 4).add("dumplings");
        boil().in("foods/frozen_dumplings", 1).fin(WATER, BOTTLE).out("dumplings", 1).add("dumplings_from_frozen");
        step(FORMING, "molding").in("materials/wonton_wrapper", 2).in("materials/meat_filling", 1).tool(FILLING_MOLD).sout("wontons|raw", 4)
            .add("wontons_raw");
        boil().semi("wontons|raw", 4).fin(WATER, BOTTLE).out("wontons", 4).add("wontons");
        step(MIXING, "blending").in("materials/steamed_glutinous_rice", 2).in("materials/meat_filling", 1).fin("light_soy_sauce", BOTTLE)
            .sout("shumai|filling", 4).quality("taste:flavor_enhancer", "state:thickener").add("shumai_filling");
        step(FORMING, "molding").in("materials/wonton_wrapper", 2).semi("shumai|filling", 1).tool(FILLING_MOLD).sout("shumai|blank", 2)
            .add("shumai_blank");
        steam().semi("shumai|blank", 1).out("shumai", 1).add("shumai");
        freeze().in("materials/raw_dumplings", 8).sout("frozen_dumplings|frozen_dumpling", 8).add("frozen_dumplings_frozen");
        pack("frozen_dumplings", "frozen_dumplings|frozen_dumpling", 8, PLASTIC_BAG);
        boil().in("materials/raw_tangyuan", 4).fin(WATER, BOTTLE).out("tangyuan", 4).add("tangyuan");
        boil().in("foods/frozen_tangyuan", 1).fin(WATER, BOTTLE).out("tangyuan", 1).add("tangyuan_from_frozen");
        freeze().in("materials/raw_tangyuan", 8).sout("frozen_tangyuan|frozen", 8).add("frozen_tangyuan_frozen");
        pack("frozen_tangyuan", "frozen_tangyuan|frozen", 8, PLASTIC_BAG);
    }

    /** foods/13 §5. */
    private void riceAndStarch() {
        // 5.1 niangao: pounded, cut
        step(FORMING, "sheeting").in("materials/steamed_rice_dough", 1).sout("niangao|cake_blank", 1).add("niangao_blank");
        step(CUTTING, "slicing").semi("niangao|cake_blank", 1).tool(KNIFE).out("niangao", 2).add("niangao");
        // 5.2 rice vermicelli
        step(FORMING, "molding").in("materials/steamed_rice_dough", 1).tool(EXTRUSION_DIE).sout("rice_vermicelli|wet_vermicelli", 1)
            .add("rice_vermicelli_wet");
        dry().semi("rice_vermicelli|wet_vermicelli", 1).sout("rice_vermicelli|dry_vermicelli", 1).add("rice_vermicelli_dry");
        pack("rice_vermicelli", "rice_vermicelli|dry_vermicelli", 1, PAPER_BAG);
        // 5.3 hor fun: the slurry with starch, steamed in sheets
        steam().fin("rice_slurry", B).in(STARCH, 1).sout("hor_fun|rice_sheet", 2).add("hor_fun_sheet");
        step(CUTTING, "slicing").semi("hor_fun|rice_sheet", 1).tool(KNIFE).out("hor_fun", 2).add("hor_fun");
        // 5.4 glass noodles: extruded, boiled, dried (the retrogradation merged in)
        step(FORMING, "molding").in("materials/starch_dough", 1).tool(EXTRUSION_DIE).sout("glass_noodles|raw_strands", 1)
            .add("glass_noodles_raw");
        boil().semi("glass_noodles|raw_strands", 1).fin(WATER, BOTTLE).sout("glass_noodles|cooked_strands", 1).add("glass_noodles_cooked");
        dry().semi("glass_noodles|cooked_strands", 1).sout("glass_noodles|dry_strands", 1).add("glass_noodles_dry");
        pack("glass_noodles", "glass_noodles|dry_strands", 1, PAPER_BAG);
        // 5.5 starch noodles: the wide ones, slit (foods/13 §9 #7)
        step(FORMING, "slitting").in("materials/starch_dough", 1).sout("starch_noodles|raw_strips", 1).add("starch_noodles_raw");
        boil().semi("starch_noodles|raw_strips", 1).fin(WATER, BOTTLE).sout("starch_noodles|cooked_strips", 1).add("starch_noodles_cooked");
        dry().semi("starch_noodles|cooked_strips", 1).sout("starch_noodles|dry_strips", 1).add("starch_noodles_dry");
        pack("starch_noodles", "starch_noodles|dry_strips", 1, PAPER_BAG);
        // 5.6 liangpi: the dough washed (gluten out), the starch batter steamed, cut; no ingredient step
        r(EXTRACTING).in(FLOUR, 4).fin(WATER, B).fout("liangpi_batter", B).out("gluten", 1).src().tier(0).add("liangpi_batter");
        steam().fin("liangpi_batter", B).sout("liangpi|dough_sheet", 2).add("liangpi_sheet");
        step(CUTTING, "slicing").semi("liangpi|dough_sheet", 1).tool(KNIFE).out("liangpi", 2).add("liangpi");
        // 5.7 starch jelly: boiled, cooled, cut
        r(BOILING).in(STARCH, 1).fin(WATER, B).sout("starch_jelly|starch_paste", 4).temp("heated").manual(STIRS).tier(0)
            .add("starch_jelly_paste");
        r(CHILLING).semi("starch_jelly|starch_paste", 1).sout("starch_jelly|jelly_block", 1).mode("chilling").temp("cooling").tier(0)
            .add("starch_jelly_block");
        step(CUTTING, "slicing").semi("starch_jelly|jelly_block", 1).tool(KNIFE).out("starch_jelly", 2).add("starch_jelly");
        // 5.8 alkaline zongzi
        r(EXTRACTING).in("materials/glutinous_rice", 4).fin(KANSUI, BOTTLE).fin(WATER, BOTTLE).sout("alkaline_zongzi|lye_rice", 4).tier(0)
            .add("alkaline_zongzi_rice");
        step(FORMING, "molding").semi("alkaline_zongzi|lye_rice", 2).in("materials/zongzi_leaf", 2).tool(FILLING_MOLD)
            .sout("alkaline_zongzi|raw_zongzi", 1).add("alkaline_zongzi_raw");
        boil().semi("alkaline_zongzi|raw_zongzi", 1).fin(WATER, BOTTLE).out("alkaline_zongzi", 1).add("alkaline_zongzi");
    }

    /** foods/13 §6. */
    private void friedAndSteamed() {
        // 6.1 youtiao
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(WATER, BOTTLE).in(SALT, 1).in(SODA, 1).sout("youtiao|dough", 4)
            .quality("state:leavening,sodium_acid_pyrophosphate").add("youtiao_dough");
        proof().semi("youtiao|dough", 4).sout("youtiao|proofed_dough", 4).add("youtiao_proofed");
        step(CUTTING, "slicing").semi("youtiao|proofed_dough", 1).tool(KNIFE).sout("youtiao|blank", 2).add("youtiao_blank");
        fry().semi("youtiao|blank", 1).fin(OIL, FRY_OIL).out("youtiao", 1).add("youtiao");
        // 6.2 mahua
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(SUGAR, 1).in(EGG, 1).sout("mahua|dough", 4).quality("state:leavening")
            .add("mahua_dough");
        proof().semi("mahua|dough", 4).sout("mahua|proofed_dough", 4).add("mahua_proofed");
        step(FORMING, "molding").semi("mahua|proofed_dough", 1).tool(STRETCHING_MOLD).sout("mahua|blank", 2).add("mahua_blank");
        fry().semi("mahua|blank", 1).fin(OIL, FRY_OIL).out("mahua", 1).add("mahua");
        // 6.3 sanzi
        proof().in("materials/wheat_dough", 4).sout("sanzi|dough_strands", 4).add("sanzi_strands");
        step(FORMING, "molding").semi("sanzi|dough_strands", 1).tool(STRETCHING_MOLD).sout("sanzi|blank", 2).add("sanzi_blank");
        fry().semi("sanzi|blank", 1).fin(OIL, FRY_OIL).out("sanzi", 1).add("sanzi");
        // 6.4 steamed bun
        step(CUTTING, "slicing").in("materials/leavened_dough", 1).tool(KNIFE).sout("steamed_bun|blank", 2).add("steamed_bun_blank");
        proof().semi("steamed_bun|blank", 2).sout("steamed_bun|proofed_blank", 2).add("steamed_bun_proofed");
        steam().semi("steamed_bun|proofed_blank", 1).out("steamed_bun", 1).add("steamed_bun");
        // 6.5 baozi
        step(FORMING, "molding").in("materials/leavened_dough", 2).in("materials/meat_filling", 1).tool(FILLING_MOLD).sout("baozi|blank", 2)
            .add("baozi_blank");
        proof().semi("baozi|blank", 2).sout("baozi|proofed_blank", 2).add("baozi_proofed");
        steam().semi("baozi|proofed_blank", 1).out("baozi", 1).add("baozi");
        // 6.6 flower roll: rolled with scallion oil (scallion: foods/23, 2026-11-25 user, before the onion tag)
        step(FORMING, "sheeting").in("materials/leavened_dough", 2).in("materials/scallion", 1).in(SALT, 1).fin(OIL, BOTTLE)
            .sout("flower_roll|blank", 4).add("flower_roll_blank");
        proof().semi("flower_roll|blank", 4).sout("flower_roll|proofed_blank", 4).add("flower_roll_proofed");
        steam().semi("flower_roll|proofed_blank", 1).out("flower_roll", 1).add("flower_roll");
        // 6.7 fagao
        step(MIXING, "blending").fin("rice_slurry", B).in(FLOUR, 1).in(SUGAR, 2).yeast().sout("fagao|batter", 4).quality("state:leavening")
            .add("fagao_batter");
        proof().semi("fagao|batter", 4).sout("fagao|risen_batter", 4).add("fagao_risen");
        steam().semi("fagao|risen_batter", 1).out("fagao", 6).add("fagao");
    }

    /** foods/13 §7. */
    private void cereals() {
        // 7.1 oat flakes (oats: this mod's crop since foods/23, or other mods')
        String oats = "materials/oats";
        steam().in(oats, 4).sout("oat_flakes|steamed_oats", 4).add("oat_flakes_steamed");
        step(FORMING, "sheeting").semi("oat_flakes|steamed_oats", 1).sout("oat_flakes|wet_flakes", 1).add("oat_flakes_wet");
        dry().semi("oat_flakes|wet_flakes", 1).sout("oat_flakes|dry_flakes", 1).add("oat_flakes_dry");
        step(PACKAGING, null).semi("oat_flakes|dry_flakes", 1).in(PAPER_BAG, 1).out("oat_flakes", 1).add("oat_flakes_packaging");
        // 7.2 breakfast cereal: the extrusion puffing only on the forming line with its extrusion section (T2)
        step(MIXING, "blending").in("materials/corn", 4).in(SUGAR, 1).in("enzymes/malt", 1).in(SALT, 1).sout("breakfast_cereal|mix", 4)
            .quality("color", "preservation").add("breakfast_cereal_mix");
        r(FORMING).semi("breakfast_cereal|mix", 1).sout("breakfast_cereal|puffed", 1).mode("molding").temp("high").tier(2)
            .add("breakfast_cereal_puffed");
        bake().semi("breakfast_cereal|puffed", 1).sout("breakfast_cereal|toasted", 1).vanilla("campfire", "furnace", "smoker").tier(0)
            .add("breakfast_cereal_toasted");
        step(CONFECTIONERY, "panning").semi("breakfast_cereal|toasted", 1).in(SUGAR, 1).sout("breakfast_cereal|coated", 1)
            .add("breakfast_cereal_coated");
        pack("breakfast_cereal", "breakfast_cereal|coated", 1, PAPER_BAG);
        // 7.3 rice pudding: a bulk fluid, jarred
        boil().in("materials/rice", 1).fin(MILK, B).sout("rice_pudding|congee", 4).add("rice_pudding_congee");
        r(MIXING).semi("rice_pudding|congee", 4).in(SUGAR, 1).fout("rice_pudding", B).mode("blending").quality("food", "state:thickener", SWEET)
            .add("rice_pudding_bulk");
        r(PACKAGING).fin("rice_pudding", BOTTLE).in(GLASS_JAR, 1).out("rice_pudding", 1).add("rice_pudding_packaging");
        bench.of(PACKAGING).in(NS + ":rice_pudding_bucket", 1).in(GLASS_JAR, 4).out("rice_pudding", 4).add("rice_pudding_packaging");
        // 7.4 tapioca pearls
        step(FORMING, "dough_mixing").in(STARCH, 4).in(SUGAR, 1).fin(WATER, BOTTLE).sout("tapioca_pearls|dough", 4).quality("color")
            .add("tapioca_pearls_dough");
        step(FORMING, "molding").semi("tapioca_pearls|dough", 1).tool(SCREEN).sout("tapioca_pearls|raw_pearls", 1).add("tapioca_pearls_raw");
        dry().semi("tapioca_pearls|raw_pearls", 1).sout("tapioca_pearls|dry_pearls", 1).add("tapioca_pearls_dry");
        pack("tapioca_pearls", "tapioca_pearls|dry_pearls", 1, PAPER_BAG);
    }

    /** foods/14 §2, and the dairy and egg intermediates the bakery takes first (foods/15 §2, foods/16 §2). */
    private void bakeryIntermediates() {
        step(MIXING, "blending").in(FLOUR, 3).in("materials/wheat_bran", 1).out("whole_wheat_flour", 4).add("whole_wheat_flour");
        // pasteurized milk (foods/15 §2): first used by the cream (foods/08 §7 #4)
        r(STERILIZING).fin(MILK, B).fout("pasteurized_milk", B).mode("pasteurizing").temp("heated").manual(STIRS).tier(0)
            .add("pasteurized_milk");
        // cream: skimmed in the crock (T0) or spun off (T2); skim milk as by-product
        r(SEPARATING).fin("pasteurized_milk", B).fout("cream", 100).fout("skim_milk", 900).tier(0).add("cream");
        // red bean paste: boiled to a mash, fried with sugar and lard
        boil().in("materials/adzuki_bean", 4).fin(WATER, B).sout("red_bean_paste|bean_mash", 4).add("red_bean_paste_mash");
        r(BOILING).semi("red_bean_paste|bean_mash", 4).in(SUGAR, 1).in(LARD, 1).out("red_bean_paste", 4).temp("heated").manual(STIRS).tier(0)
            .add("red_bean_paste");
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin("invert_syrup", 500).fin(OIL, BOTTLE).fin(KANSUI, BOTTLE).out("mooncake_dough", 4)
            .quality().add("mooncake_dough");
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(BUTTER, 2).fin(WATER, BOTTLE).out("pie_crust", 4).add("pie_crust");
        // egg custard mix (foods/16 §2): first used by the egg tart
        r(MIXING).in(EGG, 2).in(SUGAR, 1).fin(MILK, BOTTLE).fout("egg_custard_mix", BOTTLE).mode("blending").quality("food", "state:thickener")
            .add("egg_custard_mix");
    }

    /** foods/14 §3. */
    private void breads() {
        // 3.1 white bread
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(MILK, BOTTLE).in(SUGAR, 1).fin(OIL, BOTTLE).yeast().sout("white_bread|dough", 4)
            .quality("state:emulsifier,flour_treatment,ascorbic_acid").add("white_bread_dough");
        proof().semi("white_bread|dough", 4).sout("white_bread|blank", 4).add("white_bread_blank");
        oven("white_bread|blank", "white_bread", 2);
        // 3.2 whole wheat bread
        step(FORMING, "dough_mixing").in("materials/whole_wheat_flour", 4).fin(MILK, BOTTLE).in(SUGAR, 1).yeast()
            .sout("whole_wheat_bread|dough", 4).quality("state:emulsifier").add("whole_wheat_bread_dough");
        proof().semi("whole_wheat_bread|dough", 4).sout("whole_wheat_bread|blank", 4).add("whole_wheat_bread_blank");
        oven("whole_wheat_bread|blank", "whole_wheat_bread", 2);
        // 3.3 toast loaf: baked in the loaf pan, sliced
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(MILK, BOTTLE).in(BUTTER, 1).in(SUGAR, 1).yeast().sout("toast_loaf|dough", 4)
            .quality("state:emulsifier,flour_treatment,ascorbic_acid").add("toast_loaf_dough");
        proof().semi("toast_loaf|dough", 4).sout("toast_loaf|blank", 4).add("toast_loaf_blank");
        bake().semi("toast_loaf|blank", 2).cat(LOAF_PAN, MOLD).sout("toast_loaf|loaf", 1).add("toast_loaf_loaf");
        step(CUTTING, "slicing").semi("toast_loaf|loaf", 1).tool(KNIFE).out("toast_loaf", 8).add("toast_loaf");
        // 3.4 baguette: the poolish first
        proof().in(FLOUR, 1).fin(WATER, BOTTLE).cat(YEAST, CULTURE_RUNS).sout("baguette|poolish", 1).add("baguette_poolish");
        step(FORMING, "dough_mixing").semi("baguette|poolish", 1).in(FLOUR, 3).fin(WATER, BOTTLE).in(SALT, 1).sout("baguette|dough", 4)
            .quality("state:flour_treatment,ascorbic_acid").add("baguette_dough");
        proof().semi("baguette|dough", 4).sout("baguette|blank", 4).add("baguette_blank");
        oven("baguette|blank", "baguette", 2);
        // 3.5 croissant: laminated with butter
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(EGG, 1).in(SUGAR, 1).fin(WATER, BOTTLE).yeast().sout("croissant|dough", 4)
            .quality("state:emulsifier").add("croissant_dough");
        step(FORMING, "sheeting").semi("croissant|dough", 2).in(BUTTER, 2).sout("croissant|laminated", 4).add("croissant_laminated");
        proof().semi("croissant|laminated", 4).sout("croissant|blank", 4).add("croissant_blank");
        oven("croissant|blank", "croissant", 1);
        // 3.6 bagel: boiled before baking
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(WATER, BOTTLE).in(SALT, 1).in("enzymes/malt", 1).yeast().sout("bagel|dough", 4)
            .quality("preservation:preservative").add("bagel_dough");
        proof().semi("bagel|dough", 4).sout("bagel|blank", 4).add("bagel_blank");
        boil().semi("bagel|blank", 1).fin(WATER, BOTTLE).sout("bagel|boiled", 1).add("bagel_boiled");
        oven("bagel|boiled", "bagel", 1);
        // 3.7 pretzel: dipped in lye, salted
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(WATER, BOTTLE).in(BUTTER, 1).yeast().sout("pretzel|dough", 4).quality()
            .add("pretzel_dough");
        step(FORMING, "molding").semi("pretzel|dough", 1).tool(STRETCHING_MOLD).sout("pretzel|blank", 1).add("pretzel_blank");
        r(CURING).semi("pretzel|blank", 4).fin("soda_lye", BOTTLE).sout("pretzel|lye_dipped", 4).mode("marinating").tier(0)
            .add("pretzel_lye_dipped");
        bake().semi("pretzel|lye_dipped", 1).in(SALT, 1).out("pretzel", 1).add("pretzel");
        // 3.8 donut: fried, glazed
        step(FORMING, "dough_mixing").in(FLOUR, 4).fin(MILK, BOTTLE).in(SUGAR, 1).in(EGG, 1).yeast().sout("donut|dough", 4)
            .quality(LEAVENED).add("donut_dough");
        proof().semi("donut|dough", 4).sout("donut|blank", 4).add("donut_blank");
        fry().semi("donut|blank", 1).fin(OIL, FRY_OIL).sout("donut|fried", 1).add("donut_fried");
        step(CONFECTIONERY, "panning").semi("donut|fried", 4).in(SUGAR, 1).out("donut", 4).add("donut");
    }

    /** foods/14 §4. */
    private void cakes() {
        // 4.1 sponge cake, 4.2 chiffon cake: baked in the cake mold
        step(MIXING, "whipping").in(EGG, 4).in(SUGAR, 2).in(FLOUR, 2).sout("sponge_cake|batter", 4)
            .quality("state:emulsifier,leavening,sodium_acid_pyrophosphate").add("sponge_cake_batter");
        bake().semi("sponge_cake|batter", 4).cat(CAKE_MOLD, MOLD).out("sponge_cake", 4).add("sponge_cake");
        step(MIXING, "whipping").in("materials/egg_yolk", 2).in("materials/egg_white", 2).in(SUGAR, 2).in(FLOUR, 2).fin(OIL, BOTTLE)
            .fin(MILK, BOTTLE).in(SODA, 1).sout("chiffon_cake|batter", 4).quality(LEAVENED).add("chiffon_cake_batter");
        bake().semi("chiffon_cake|batter", 4).cat(CAKE_MOLD, MOLD).out("chiffon_cake", 4).add("chiffon_cake");
        // 4.3 cheesecake: on a biscuit base, chilled
        step(MIXING, "blending").in("foods/cream_cheese", 4).in(SUGAR, 1).in(EGG, 1)
            .sout("cheesecake|filling", 4).quality("state:thickener", SWEET).add("cheesecake_filling");
        bake().semi("cheesecake|filling", 4).in("foods/biscuits", 1).cat(CAKE_MOLD, MOLD).sout("cheesecake|baked", 4).add("cheesecake_baked");
        r(CHILLING).semi("cheesecake|baked", 4).out("cheesecake", 4).mode("chilling").temp("cooling").tier(0).add("cheesecake");
        // 4.4 mousse cake: gelatin sets it in the cake mold at any temperature (machines/05 §1.1)
        r(MIXING).fin("cream", B).in(SUGAR, 1).in("materials/berries", 2).in("chemicals/gelatin", 1).fout("mousse_cake_mix", B).mode("whipping")
            .quality("food", FRUITY).add("mousse_cake_mix");
        r(CHILLING).fin("mousse_cake_mix", B).in("foods/cake_base", 1).cat(CAKE_MOLD, MOLD).out("mousse_cake", 4).mode("chilling").tier(0)
            .add("mousse_cake");
        // 4.5 cream cake
        step(MIXING, "whipping").fin("whipping_cream", B).in(SUGAR, 1).sout("cream_cake|whipped_cream", 4).quality("color", "state:emulsifier")
            .add("cream_cake_whipped");
        step(CONFECTIONERY, "panning").in("foods/cake_base", 1).semi("cream_cake|whipped_cream", 1).out("cream_cake", 1).add("cream_cake");
    }

    /** foods/14 §5. */
    private void chinesePastries() {
        // 5.1 Cantonese mooncake
        step(FORMING, "molding").in("materials/mooncake_dough", 2).in("materials/red_bean_paste", 3).tool(MOONCAKE_MOLD)
            .sout("cantonese_mooncake|blank", 4).add("cantonese_mooncake_blank");
        bake().semi("cantonese_mooncake|blank", 1).in(EGG, 1).out("cantonese_mooncake", 4).add("cantonese_mooncake");
        // 5.2 five-nut mooncake
        step(MIXING, "blending").in("materials/nuts", 2).in("materials/roasted_sesame", 1).in("materials/glutinous_rice_flour", 1).in(SUGAR, 1)
            .sout("wuren_mooncake|nut_filling", 4).add("wuren_mooncake_filling");
        step(FORMING, "molding").in("materials/mooncake_dough", 2).semi("wuren_mooncake|nut_filling", 3).tool(MOONCAKE_MOLD)
            .sout("wuren_mooncake|blank", 4).add("wuren_mooncake_blank");
        bake().semi("wuren_mooncake|blank", 1).in(EGG, 1).out("wuren_mooncake", 4).add("wuren_mooncake");
        // 5.3 egg yolk pastry
        step(FORMING, "sheeting").in(FLOUR, 4).in(SHORTENING, 1).in(LARD, 1).fin(WATER, BOTTLE).sout("egg_yolk_pastry|pastry", 4)
            .add("egg_yolk_pastry_pastry");
        step(FORMING, "molding").semi("egg_yolk_pastry|pastry", 2).in("materials/red_bean_paste", 1).in("materials/salted_egg_yolk", 1)
            .tool(FILLING_MOLD).sout("egg_yolk_pastry|blank", 2).add("egg_yolk_pastry_blank");
        bake().semi("egg_yolk_pastry|blank", 1).in(EGG, 1).out("egg_yolk_pastry", 1).add("egg_yolk_pastry");
        // 5.4 taosu
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(LARD, 2).in(SUGAR, 1).in(EGG, 1).in("materials/nuts", 1).in(SODA, 1)
            .sout("taosu|dough", 4).quality("state:leavening").add("taosu_dough");
        oven("taosu|dough", "taosu", 4);
        // 5.5 dorayaki
        step(MIXING, "blending").in(EGG, 2).in(SUGAR, 2).in(FLOUR, 2).in("materials/honey", 1).in(SODA, 1).sout("dorayaki|batter", 4)
            .quality(LEAVENED).add("dorayaki_batter");
        bake().semi("dorayaki|batter", 1).sout("dorayaki|pancake", 2).vanilla("campfire", "furnace", "smoker").tier(0).add("dorayaki_pancake");
        step(FORMING, "molding").semi("dorayaki|pancake", 2).in("materials/red_bean_paste", 1).tool(FILLING_MOLD).out("dorayaki", 1)
            .add("dorayaki");
    }

    /** foods/14 §6. */
    private void biscuitsAndPastries() {
        // 6.1 soda cracker: soda in at the proofing
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(SHORTENING, 1).fin(WATER, BOTTLE).yeast().sout("soda_cracker|dough", 4).quality()
            .add("soda_cracker_dough");
        proof().semi("soda_cracker|dough", 4).in(SODA, 1).sout("soda_cracker|risen_dough", 4).add("soda_cracker_risen");
        step(FORMING, "sheeting").semi("soda_cracker|risen_dough", 1).sout("soda_cracker|blank", 1).add("soda_cracker_blank");
        oven("soda_cracker|blank", "soda_cracker", 4);
        // 6.2 butter cookie
        step(MIXING, "whipping").in(BUTTER, 2).in(SUGAR, 1).in(EGG, 1).in(FLOUR, 2).sout("butter_cookie|batter", 4).quality(SWEET)
            .add("butter_cookie_batter");
        oven("butter_cookie|batter", "butter_cookie", 4);
        // 6.3 wafer: a batter fluid, baked in the iron, filled
        r(MIXING).in(FLOUR, 2).fin(WATER, B).in(SODA, 1).fout("wafer_batter", B).mode("blending")
            .quality("food", "state:emulsifier", SWEET, "color").add("wafer_batter");
        bake().fin("wafer_batter", BOTTLE).cat(BAKING_IRON, MOLD).sout("wafer|wafer_sheet", 2).add("wafer_sheet");
        step(FORMING, "molding").semi("wafer|wafer_sheet", 2).in(SUGAR, 1).in(SHORTENING, 1).tool(FILLING_MOLD).out("wafer", 4).add("wafer");
        // 6.4 sandwich biscuit
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(SUGAR, 1).in(SHORTENING, 1).in(SODA, 1).sout("sandwich_biscuit|dough", 4)
            .quality("color", FRUITY, "state:emulsifier").add("sandwich_biscuit_dough");
        step(FORMING, "sheeting").semi("sandwich_biscuit|dough", 1).sout("sandwich_biscuit|blank", 1).add("sandwich_biscuit_blank");
        bake().semi("sandwich_biscuit|blank", 1).sout("sandwich_biscuit|biscuit", 4).vanilla("campfire", "furnace", "smoker").tier(0)
            .add("sandwich_biscuit_biscuit");
        step(FORMING, "molding").semi("sandwich_biscuit|biscuit", 2).in(SUGAR, 1).in(SHORTENING, 1).tool(FILLING_MOLD)
            .out("sandwich_biscuit", 4).add("sandwich_biscuit");
        // 6.5 egg roll: baked and rolled in the iron
        step(MIXING, "blending").in(BUTTER, 1).in(SUGAR, 1).in(EGG, 2).in(FLOUR, 1).in("materials/roasted_sesame", 1).sout("egg_roll|batter", 4)
            .quality("color").add("egg_roll_batter");
        bake().semi("egg_roll|batter", 1).cat(BAKING_IRON, MOLD).out("egg_roll", 4).add("egg_roll");
        // 6.6 cream puff
        r(BOILING).fin(WATER, B).in(BUTTER, 1).in(FLOUR, 2).sout("cream_puff|choux_paste", 4).temp("heated").manual(STIRS).tier(0)
            .add("cream_puff_choux");
        step(MIXING, "whipping").semi("cream_puff|choux_paste", 4).in(EGG, 2).sout("cream_puff|batter", 4).quality("state:leavening")
            .add("cream_puff_batter");
        bake().semi("cream_puff|batter", 1).sout("cream_puff|shell", 2).vanilla("campfire", "furnace", "smoker").tier(0).add("cream_puff_shell");
        step(FORMING, "molding").semi("cream_puff|shell", 2).fin("cream", BOTTLE).in(SUGAR, 1).tool(FILLING_MOLD).out("cream_puff", 2)
            .add("cream_puff");
        // 6.7 egg tart: the Hong Kong shell, the custard from batch 4's shared mix
        step(FORMING, "sheeting").in(FLOUR, 4).in(BUTTER, 2).in(SUGAR, 1).in(EGG, 1).sout("egg_tart|tart_shell", 4).add("egg_tart_shell");
        bake().semi("egg_tart|tart_shell", 1).fin("egg_custard_mix", BOTTLE).cat(TART_PAN, MOLD).out("egg_tart", 2).add("egg_tart");
        // 6.8 apple pie, 6.9 berry pie
        pie("apple_pie", "materials/apple");
        pie("berry_pie", "materials/berries");
        // 6.10 muffin
        step(MIXING, "blending").in(FLOUR, 2).in(SUGAR, 1).in(EGG, 1).fin(MILK, BOTTLE).fin(OIL, BOTTLE).in(SODA, 1).sout("muffin|batter", 4)
            .quality(LEAVENED).add("muffin_batter");
        bake().semi("muffin|batter", 4).cat(CAKE_MOLD, MOLD).out("muffin", 4).add("muffin");
        // 6.11 scone
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(BUTTER, 1).fin("buttermilk", BOTTLE).in(SODA, 1).sout("scone|dough", 4)
            .quality(LEAVENED).add("scone_dough");
        oven("scone|dough", "scone", 2);
        // 6.12 waffle
        step(MIXING, "blending").in(FLOUR, 2).in(EGG, 1).fin("buttermilk", BOTTLE).in(BUTTER, 1).in(SODA, 1).sout("waffle|batter", 4)
            .quality(LEAVENED).add("waffle_batter");
        bake().semi("waffle|batter", 1).cat(BAKING_IRON, MOLD).out("waffle", 1).add("waffle");
        // 6.13 gingerbread
        step(FORMING, "dough_mixing").in(FLOUR, 4).in(BUTTER, 1).in("materials/honey", 1).in("materials/ginger", 1).in(SODA, 1)
            .sout("gingerbread|dough", 4).quality("state:leavening").add("gingerbread_dough");
        step(FORMING, "sheeting").semi("gingerbread|dough", 1).sout("gingerbread|blank", 2).add("gingerbread_blank");
        oven("gingerbread|blank", "gingerbread", 4);
    }

    /** foods/14 §6.8, §6.9: the filling mixed raw, wrapped in the crust, baked in the tart pan. */
    private void pie(String product, String fruit) {
        step(MIXING, "blending").in(fruit, 4).in(SUGAR, 1).in(FLOUR, 1).sout(product + "|filling", 4).quality("state:thickener")
            .add(product + "_filling");
        step(FORMING, "molding").in("materials/pie_crust", 2).semi(product + "|filling", 2).tool(FILLING_MOLD).sout(product + "|blank", 1)
            .add(product + "_blank");
        bake().semi(product + "|blank", 1).cat(TART_PAN, MOLD).out(product, 4).add(product);
    }

    // ---- the devices of each step ----

    /** R22 steaming: the pots with stirs from T0, the boiling stove, the cooker-fryer. */
    private ContentRecipe steam() {
        return r(COOKING).mode("steaming").temp("heated").manual(STIRS).tier(0);
    }

    /** R22 boiling in water. */
    private ContentRecipe boil() {
        return r(COOKING).mode("water_boiling").temp("heated").manual(STIRS).tier(0);
    }

    /** R24 frying: the pots from T0, the boiling stove, the cooker-fryer. */
    private ContentRecipe fry() {
        return r(FRYING).temp("heated").manual(STIRS).tier(0);
    }

    /** R25 proofing: the crock from T0, the fermentation vat, the aging cellar. */
    private ContentRecipe proof() {
        return r(CURING).mode("proofing").tier(0);
    }

    /** R07 drying: the drying rack from T0, the drying room and the dryer. */
    private ContentRecipe dry() {
        return r(DRYING).tier(0);
    }

    /** R28 quick freezing: the drying rack out in a cold biome (T0), the ice-salt freezer, the freezing unit. */
    private ContentRecipe freeze() {
        return r(CHILLING).mode("quick_freezing").temp("freezing").tier(0);
    }

    /** R23 baking: the brick oven and the baking line (T1). */
    private ContentRecipe bake() {
        return r(BAKING).mode("baking").temp("heated");
    }

    /** R23 baking of one item into one item: campfire, furnace and smoker as well (T0, machines/05 §2.3). */
    private void oven(String semi, String product, int count) {
        bake().semi(semi, 1).out(product, count).vanilla("campfire", "furnace", "smoker").tier(0).add(product);
    }

    /** R29: one packed product from {@code count} of a semi-finished stage and a container (machine and crafting table). */
    private void pack(String product, String semi, int count, String container) {
        step(PACKAGING, null).semi(semi, count).in(container, 1).out(product, 1).add(product + "_packaging");
    }

    /** A step of R19, R20, R26, R27 or R29: the machine recipe and, when the grid can hold it, the crafting-table one. */
    private Step step(ProcessType type, String mode) {
        ContentRecipe machine = r(type);
        if (mode != null) {
            machine.mode(mode);
        }
        return new Step(machine, bench.of(type));
    }

    /** Writes one step both ways: catalysts become tools in the grid, yeast an ingredient (2026-11-19 user). */
    private final class Step {
        private final ContentRecipe machine;
        private final Workbench.Recipe hand;
        private int units;

        private Step(ContentRecipe machine, Workbench.Recipe hand) {
            this.machine = machine;
            this.hand = hand;
        }

        Step in(String tag, int count) {
            machine.in(tag, count);
            hand.in(tag, count);
            units += count;
            return this;
        }

        Step fin(String tag, int amount) {
            machine.fin(tag, amount);
            hand.fin(tag, amount);
            units++;
            return this;
        }

        Step semi(String key, int count) {
            machine.semi(key, count);
            hand.semi(key, count);
            units += count;
            return this;
        }

        /** A mold or knife: a catalyst used {@link #MOLD} times in a machine, a tool kept in the grid. */
        Step tool(String tag) {
            machine.cat(tag, MOLD);
            hand.tool(tag);
            units++;
            return this;
        }

        /** Brewer's yeast: a catalyst in a machine, used up on the crafting table. */
        Step yeast() {
            machine.cat(YEAST, CULTURE_RUNS);
            hand.in(YEAST, 1);
            units++;
            return this;
        }

        Step out(String item, int count) {
            machine.out(item, count);
            hand.out(item, count);
            return this;
        }

        Step sout(String key, int count) {
            machine.sout(key, count);
            hand.sout(key, count);
            return this;
        }

        /** The ingredient step: its quality items (food grade required of the inputs). */
        Step quality(String... items) {
            machine.quality("food", items);
            hand.quality("food", items);
            return this;
        }

        Step when(String tag) {
            machine.whenTag(tag);
            hand.whenTag(tag);
            return this;
        }

        void add(String name) {
            machine.add(name);
            if (units <= 9) {
                hand.add(name);
            } else {
                machineOnly.add(name);
            }
        }
    }
}
