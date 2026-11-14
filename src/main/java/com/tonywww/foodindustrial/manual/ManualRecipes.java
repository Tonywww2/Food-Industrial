package com.tonywww.foodindustrial.manual;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.recipe.RecipeGen;
import com.tonywww.foodindustrial.recipe.VanillaDevice;

/**
 * The hand-made chains of additives/07 (both product tables and the natural inoculation table) as
 * process recipes for the T0 tools and devices (I12, 2026-11-10); steps done by hand also carry a
 * machine time for the T1 machines (I14). Purity values follow 07; times,
 * stir counts, amounts and the drafted intermediate steps are game settings (pending review).
 * <p>
 * A device picks the recipe with the most conditions, ties by id (07 §原地转化与失败产物), so each
 * failure recipe is named after the step it spoils ({@code plaster} → {@code plaster_overheated}) and
 * sorts after it.
 */
final class ManualRecipes {
    private static final String NS = FoodIndustrial.MOD_ID;

    /** Ticks per in-game day. */
    private static final int DAY = 24000;
    /** Stirs of a pot step. */
    private static final int STIRS = 8;
    /** Actions of the mortar. */
    private static final int GRIND = 8;
    /** Actions of the sieve and the cloth bag, and of a light pounding in the mortar. */
    private static final int LIGHT = 4;
    /** Water of a pot or crock step. */
    private static final int BUCKET = 1000;
    /** Base time of a step on the vanilla blocks: the furnace's time (machines/01 §5; campfire ×3, smoker and blast furnace ×0.5). */
    private static final int VANILLA = 200;
    /**
     * Machine time of a step done by hand (tool actions or pot stirs), so the T1 machines run it too
     * (I14, additives/06 §T1 作坊, 2026-11-10).
     */
    private static final int MACHINE = 200;

    private ManualRecipes() {
    }

    static void bootstrap() {
        RecipeGen.register(ManualRecipes::generate);
    }

    private static void generate(RecipeGen.Output out) {
        wineAndVinegar(out);
        lacticAcid(out);
        ashes(out);
        salt(out);
        gypsum(out);
        agar(out);
        caramel(out);
        saltpeter(out);
        driedFood(out);
        gardenia(out);
        lecithin(out);
        indigo(out);
        malt(out);
        fishLiverOil(out);
        rubber(out);
        koji(out);
        vanillaBlocks(out);
        batch4(out);
    }

    // ---- #1 vinegar, H13 vinegar mother; the wines ----

    private static void wineAndVinegar(RecipeGen.Output out) {
        recipe(out, ProcessTypes.FERMENTING).in("materials/apple", 4).inFluid("water", BUCKET)
            .outFluid("fruit_wine", BUCKET).time(2 * DAY).sealed().add("fruit_wine_from_apples");
        recipe(out, ProcessTypes.FERMENTING).in("materials/berries", 4).inFluid("water", BUCKET)
            .outFluid("fruit_wine", BUCKET).time(2 * DAY).sealed().add("fruit_wine_from_berries");
        recipe(out, ProcessTypes.FERMENTING).in("materials/grape", 4).inFluid("water", BUCKET)
            .outFluid("wine", BUCKET).time(2 * DAY).sealed().add("grape_wine");
        recipe(out, ProcessTypes.FERMENTING).inFluid("fruit_wine", BUCKET).catalyst("cultures/acetobacter", 8)
            .outFluid("acetic_acid", BUCKET).source(4).time(2 * DAY).open().factor("cold_biome", 2.0, 1.0).add("vinegar");
        // H13: an open crock of fruit wine in a non-cold biome catches its own vinegar mother
        recipe(out, ProcessTypes.CULTURING).inFluid("fruit_wine", BUCKET)
            .out("acetobacter", 1).source(20).outFluid("acetic_acid", BUCKET).source(4)
            .time(3 * DAY).open().environment("non_cold_biome").add("wild_acetobacter");
    }

    // ---- #2 sour brine (lactic acid), H13 old brine ----

    private static void lacticAcid(RecipeGen.Output out) {
        recipe(out, ProcessTypes.EXTRACTING).in("chemicals/salt", 1).inFluid("water", BUCKET)
            .outFluid("brine", BUCKET).time(200).add("brine");
        recipe(out, ProcessTypes.FERMENTING).in("materials/vegetables", 4).inFluid("brine", BUCKET)
            .catalyst("cultures/lactobacillus", 8).outFluid("lactic_acid", BUCKET).source(1.2).time(DAY).sealed().add("lactic_acid");
        recipe(out, ProcessTypes.CULTURING).in("materials/vegetables", 4).inFluid("brine", BUCKET)
            .outFluid("lactic_acid", BUCKET).source(1.2).out("lactobacillus", 1).source(20)
            .time(2 * DAY).sealed().add("wild_lactobacillus");
    }

    // ---- #5 crude soda, #7 crude potash ----

    private static void ashes(RecipeGen.Output out) {
        recipe(out, ProcessTypes.EXTRACTING).in("chemicals/kelp_ash", 4).inFluid("water", BUCKET)
            .outFluid("soda_lye", BUCKET).time(1200).add("soda_lye");
        recipe(out, ProcessTypes.BOILING).inFluid("soda_lye", BUCKET).out("soda_ash", 1).source(20).heated().stirs(STIRS).add("soda_ash");
        recrystallizing(out, "soda_ash", "crude");
        recipe(out, ProcessTypes.EXTRACTING).in("chemicals/wood_ash", 4).inFluid("water", BUCKET)
            .outFluid("potash_lye", BUCKET).time(1200).add("potash_lye");
        recipe(out, ProcessTypes.BOILING).inFluid("potash_lye", BUCKET).out("potassium_carbonate", 1).source(50)
            .heated().stirs(STIRS).add("potassium_carbonate");
        recrystallizing(out, "potassium_carbonate", "crude");
    }

    /** H03: four dissolved in hot water crystallize again into two, half the impurities removed, up to {@code t0Max}. */
    private static void recrystallizing(RecipeGen.Output out, String material, String t0Max) {
        recipe(out, ProcessTypes.CRYSTALLIZING).in("chemicals/" + material, 4).inFluid("water", BUCKET)
            .out(material, 2).removal(50, t0Max).heated().stirs(STIRS).add(material + "_recrystallizing");
    }

    // ---- #11 bittern and sea salt (H06) ----

    private static void salt(RecipeGen.Output out) {
        // bittern first: a hot biome raises its yield (07 #11), and a factor scales the main product
        recipe(out, ProcessTypes.CRYSTALLIZING).inFluid("seawater", BUCKET)
            .outFluid("bittern", 250).out("salt", 1).source(83)
            .time(DAY).environment("open_sky", "clear_weather", "daytime").factor("hot_biome", 0.5, 1.5).add("sea_salt");
        recrystallizing(out, "salt", "refined");
    }

    // ---- #12 plaster ----

    private static void gypsum(RecipeGen.Output out) {
        recipe(out, ProcessTypes.GRINDING).in("materials/gypsum", 1).out("gypsum", 1).source(80).stirs(GRIND).add("gypsum");
        recipe(out, ProcessTypes.KILN).in("chemicals/gypsum", 4).out("plaster", 4).keep().heated().stirs(STIRS).add("plaster");
        recipe(out, ProcessTypes.KILN).in("chemicals/plaster", 4).out("dead_burnt_gypsum", 4).heated().stirs(STIRS)
            .add("plaster_overheated");
    }

    // ---- #14 agar (H12 freeze-thaw) ----

    private static void agar(RecipeGen.Output out) {
        recipe(out, ProcessTypes.BOILING).in("materials/red_algae", 4).inFluid("water", BUCKET)
            .outFluid("agar_broth", BUCKET).heated().stirs(STIRS).add("agar_broth");
        recipe(out, ProcessTypes.SEPARATING).inFluid("agar_broth", BUCKET).out("agar_jelly", 2).filtration().stirs(LIGHT).add("agar_jelly");
        recipe(out, ProcessTypes.CRYSTALLIZING).in("chemicals/agar_jelly", 2).out("agar", 1).source(75)
            .time(4 * DAY).environment("cold_biome", "open_sky").add("agar");
    }

    // ---- #18 caramel colour ----

    private static void caramel(RecipeGen.Output out) {
        recipe(out, ProcessTypes.BOILING).in("chemicals/sucrose", 4).inFluid("water", BUCKET)
            .outFluid("caramel", BUCKET).source(30).heated().openFlame().stirs(12).add("caramel");
        recipe(out, ProcessTypes.BOILING).inFluid("caramel", BUCKET).out("burnt_caramel", 1).heated().openFlame().stirs(STIRS)
            .add("caramel_burnt");
    }

    // ---- #24 saltpeter ----

    private static void saltpeter(RecipeGen.Output out) {
        recipe(out, ProcessTypes.EXTRACTING).in("materials/nitre_soil", 4).inFluid("water", BUCKET)
            .outFluid("saltpeter_liquor", BUCKET).time(1200).add("saltpeter_liquor");
        recipe(out, ProcessTypes.CRYSTALLIZING).inFluid("saltpeter_liquor", BUCKET).in("chemicals/wood_ash", 1)
            .out("potassium_nitrate", 1).source(90).heated().stirs(STIRS).add("potassium_nitrate");
        recrystallizing(out, "potassium_nitrate", "refined");
    }

    // ---- sun-dried food: #25 dried fruit, #36 stevia, #60 chili, #61 turmeric; #55 cream of tartar ----

    private static void driedFood(RecipeGen.Output out) {
        sunDried(out, "materials/apple", 1, "dried_apple");
        sunDried(out, "materials/berries", 4, "dried_berries");
        sunDried(out, "materials/stevia", 1, "dried_stevia");
        recipe(out, ProcessTypes.EXTRACTING).in("materials/dried_stevia", 4).inFluid("water", BUCKET)
            .out("steviol_glycosides", 1).source(1.5).heated().stirs(STIRS).add("steviol_glycosides");
        recrystallizing(out, "potassium_bitartrate", "refined");

        sunDried(out, "materials/chili", 1, "dried_chili");
        recipe(out, ProcessTypes.GRINDING).in("materials/dried_chili", 1).out("chili_powder", 1).stirs(GRIND).add("chili_powder");
        recipe(out, ProcessTypes.EXTRACTING).in("materials/chili_powder", 4).inFluid("edible_oils", BUCKET)
            .out("capsanthin", 1).source(0.2).heated().stirs(STIRS).add("capsanthin");

        recipe(out, ProcessTypes.BOILING).in("materials/turmeric", 4).inFluid("water", BUCKET)
            .out("boiled_turmeric", 4).heated().stirs(STIRS).add("boiled_turmeric");
        sunDried(out, "materials/boiled_turmeric", 1, "dried_turmeric");
        recipe(out, ProcessTypes.DRYING).in("materials/boiled_turmeric", 1).out("moldy_turmeric", 1)
            .time(2400).environment("rain").add("moldy_turmeric");
        recipe(out, ProcessTypes.GRINDING).in("materials/dried_turmeric", 1).out("turmeric", 1).source(3).stirs(GRIND).add("turmeric");
    }

    /** H07 in the sun: one position of {@code count} dries in a day under an open, clear sky. */
    private static void sunDried(RecipeGen.Output out, String input, int count, String result) {
        recipe(out, ProcessTypes.DRYING).in(input, count).out(result, 1)
            .time(DAY).environment("open_sky", "clear_weather").add(result);
    }

    // ---- #62 gardenia yellow ----

    private static void gardenia(RecipeGen.Output out) {
        recipe(out, ProcessTypes.GRINDING).in("materials/gardenia", 1).out("crushed_gardenia", 1).stirs(LIGHT).add("crushed_gardenia");
        recipe(out, ProcessTypes.BOILING).in("materials/crushed_gardenia", 4).inFluid("water", BUCKET)
            .outFluid("gardenia_broth", BUCKET).heated().stirs(STIRS).add("gardenia_broth");
        recipe(out, ProcessTypes.SEPARATING).inFluid("gardenia_broth", BUCKET).out("gardenia_yellow", 1).source(0.2)
            .filtration().stirs(LIGHT).add("gardenia_yellow");
    }

    // ---- #69 egg yolk lecithin ----

    private static void lecithin(RecipeGen.Output out) {
        recipe(out, ProcessTypes.SEPARATING).in("materials/egg", 1).out("egg_yolk", 1).out("egg_white", 1)
            .filtration().stirs(LIGHT).add("egg_separating");
        recipe(out, ProcessTypes.BOILING).in("materials/egg_yolk", 4).inFluid("water", BUCKET)
            .out("cooked_egg_yolk", 4).heated().stirs(STIRS).add("cooked_egg_yolk");
        recipe(out, ProcessTypes.EXTRACTING).in("materials/cooked_egg_yolk", 4).inFluid("fruit_wine", BUCKET)
            .outFluid("yolk_extract", BUCKET).time(DAY).sealed().add("yolk_extract");
        recipe(out, ProcessTypes.BOILING).inFluid("yolk_extract", BUCKET).out("lecithin", 1).source(30).heated().stirs(STIRS).add("lecithin");
    }

    // ---- #84 indigo paste ----

    private static void indigo(RecipeGen.Output out) {
        recipe(out, ProcessTypes.FERMENTING).in("materials/indigo_plant", 4).inFluid("water", BUCKET)
            .outFluid("indigo_ferment", BUCKET).time(2 * DAY).sealed().add("indigo_ferment");
        recipe(out, ProcessTypes.REACTING).inFluid("indigo_ferment", BUCKET).in("chemicals/slaked_lime", 1)
            .out("indigo", 2).source(2).time(DAY).settling().add("indigo");
    }

    // ---- #96 malt ----

    private static void malt(RecipeGen.Output out) {
        recipe(out, ProcessTypes.EXTRACTING).in("materials/wheat", 4).inFluid("water", BUCKET)
            .out("soaked_wheat", 4).time(DAY / 2).add("soaked_wheat");
        recipe(out, ProcessTypes.SOLID_FERMENTING).in("materials/soaked_wheat", 4).out("sprouted_wheat", 4).time(2 * DAY).add("sprouted_wheat");
        recipe(out, ProcessTypes.SOLID_FERMENTING).in("materials/sprouted_wheat", 4).out("overgrown_malt", 4).time(2 * DAY)
            .add("sprouted_wheat_overgrown");
        sunDried(out, "materials/sprouted_wheat", 1, "malt");
    }

    // ---- #107 fish liver oil ----

    private static void fishLiverOil(RecipeGen.Output out) {
        recipe(out, ProcessTypes.BOILING).in("materials/fish_liver", 4).outFluid("fish_liver_oil", BUCKET).heated().stirs(STIRS)
            .add("fish_liver_oil");
        recipe(out, ProcessTypes.BOILING).inFluid("fish_liver_oil", BUCKET).outFluid("fish_liver_oil", 750).heated().stirs(STIRS)
            .add("fish_liver_oil_overcooked");
    }

    // ---- #136 natural rubber sheet ----

    private static void rubber(RecipeGen.Output out) {
        recipe(out, ProcessTypes.REACTING).inFluid("latex", BUCKET).inFluid("acetic_acid", 250)
            .out("rubber_coagulum", 4).time(DAY / 2).add("rubber_coagulum");
        recipe(out, ProcessTypes.PRESSING).in("materials/rubber_coagulum", 1).out("wet_rubber_sheet", 1).stirs(LIGHT).add("wet_rubber_sheet");
        recipe(out, ProcessTypes.DRYING).in("materials/wet_rubber_sheet", 1).out("natural_rubber", 1).source(90)
            .time(2 * DAY).add("natural_rubber");
    }

    // ---- H13 koji tray inoculation ----

    private static void koji(RecipeGen.Output out) {
        recipe(out, ProcessTypes.BOILING).in("materials/rice", 4).inFluid("water", BUCKET)
            .out("steamed_rice", 4).heated().stirs(STIRS).add("steamed_rice");
        recipe(out, ProcessTypes.BOILING).in("materials/wheat", 4).inFluid("water", BUCKET)
            .out("steamed_wheat", 4).heated().stirs(STIRS).add("steamed_wheat");
        inoculation(out, "steamed_rice", "monascus", "monascus", "sheltered", "jungle_biome");
        inoculation(out, "steamed_rice", "rhizopus", "rhizopus", "sheltered", "bamboo_biome");
        inoculation(out, "steamed_rice", "aspergillus_oryzae", "aspergillus_oryzae_from_rice", "sheltered");
        inoculation(out, "steamed_wheat", "aspergillus_oryzae", "aspergillus_oryzae_from_wheat", "sheltered");
        // wild mold falls from the air: an open tray or crock, never the sterile culture bench (2026-11-11)
        recipe(out, ProcessTypes.CULTURING).in("materials/steamed_rice", 4).out("wild_mold", 1).time(2 * DAY).open().add("wild_mold_from_rice");
        recipe(out, ProcessTypes.CULTURING).in("materials/steamed_wheat", 4).out("wild_mold", 1).time(2 * DAY).open().add("wild_mold_from_wheat");
    }

    private static void inoculation(RecipeGen.Output out, String grain, String culture, String name, String... environment) {
        recipe(out, ProcessTypes.CULTURING).in("materials/" + grain, 4).out(culture, 1).source(20)
            .time(2 * DAY).environment(environment).add(name);
    }

    // ---- H10 ashes and the vanilla blocks (I13, 2026-11-10) ----

    private static void vanillaBlocks(RecipeGen.Output out) {
        // H10: dried kelp, logs and dead bush burn to ash; logs stay charcoal in the furnace (vanilla first)
        recipe(out, ProcessTypes.KILN).in("materials/dried_kelp", 1).out("kelp_ash", 1).time(VANILLA).heated()
            .vanilla(VanillaDevice.CAMPFIRE, VanillaDevice.FURNACE).add("kelp_ash");
        recipe(out, ProcessTypes.KILN).in("materials/logs", 1).out("wood_ash", 1).time(VANILLA).heated()
            .vanilla(VanillaDevice.CAMPFIRE).add("wood_ash");
        recipe(out, ProcessTypes.KILN).in("materials/saltbush", 1).out("wood_ash", 1).time(VANILLA).heated()
            .vanilla(VanillaDevice.CAMPFIRE, VanillaDevice.FURNACE).add("wood_ash_from_dead_bush");
        // machines/01 §5 example: gypsum to plaster in the furnace and the blast furnace, purity kept
        recipe(out, ProcessTypes.KILN).in("chemicals/gypsum", 1).out("plaster", 1).keep().time(VANILLA).heated()
            .vanilla(VanillaDevice.FURNACE, VanillaDevice.BLAST_FURNACE).add("plaster_kiln");
        // heated drying of what the drying rack dries in the sun (2026-11-10); the rack offers no heat
        heatedDrying(out, "materials/apple", "dried_apple");
        heatedDrying(out, "materials/stevia", "dried_stevia");
        heatedDrying(out, "materials/chili", "dried_chili");
        heatedDrying(out, "materials/boiled_turmeric", "dried_turmeric");
        heatedDrying(out, "materials/sprouted_wheat", "malt");
        recipe(out, ProcessTypes.DRYING).in("materials/wet_rubber_sheet", 1).out("natural_rubber", 1).source(90).time(VANILLA).heated()
            .vanilla(VanillaDevice.CAMPFIRE, VanillaDevice.FURNACE, VanillaDevice.SMOKER).add("natural_rubber_heated");
    }

    // ---- the T0 routes of the fourth batch (I30, additives/15): lime, hot-oil colours, waxes, exudate gums, monk fruit ----

    private static void batch4(RecipeGen.Output out) {
        // 145: limestone burnt in the furnace, slaked with water
        recipe(out, ProcessTypes.KILN).in("materials/limestone", 1).out("quicklime", 1).source(90).time(VANILLA).heated()
            .vanilla(VanillaDevice.FURNACE, VanillaDevice.BLAST_FURNACE).add("quicklime");
        recipe(out, ProcessTypes.REACTING).in("chemicals/quicklime", 1).inFluid("water", BUCKET).out("slaked_lime", 1).keep()
            .stirs(LIGHT).add("slaked_lime");
        // 230 A, 232 A: gromwell root and annatto seed rubbed in hot oil (colour and oil together, as 60 A)
        recipe(out, ProcessTypes.EXTRACTING).in("materials/gromwell_root", 4).inFluid("edible_oils", BUCKET).out("lithospermum_red", 1)
            .source(5).heated().stirs(STIRS).add("lithospermum_red");
        recipe(out, ProcessTypes.EXTRACTING).in("materials/annatto_seed", 4).inFluid("edible_oils", BUCKET).out("bixin", 1)
            .source(5).heated().stirs(STIRS).add("bixin");
        // 275 A, 280 A: wax powder or comb melted in water, strained hot through the cloth bag (one pot step)
        recipe(out, ProcessTypes.BOILING).in("materials/carnauba_powder", 4).inFluid("water", BUCKET).out("carnauba_wax", 1).source(90)
            .heated().stirs(STIRS).add("carnauba_wax");
        recipe(out, ProcessTypes.BOILING).in("materials/honeycomb", 4).inFluid("water", BUCKET).out("beeswax", 1).source(90)
            .heated().stirs(STIRS).add("beeswax");
        // 270 A, 283, 288 A: exudate lumps picked clean and ground in the mortar
        recipe(out, ProcessTypes.GRINDING).in("materials/ghatti_lump", 1).out("ghatti_gum", 1).source(40).stirs(GRIND).add("ghatti_gum");
        recipe(out, ProcessTypes.GRINDING).in("materials/karaya_lump", 1).out("karaya_gum", 1).source(40).stirs(GRIND).add("karaya_gum");
        recipe(out, ProcessTypes.GRINDING).in("materials/acacia_lump", 1).out("gum_arabic", 1).source(40).stirs(GRIND).add("gum_arabic");
        // 297 A: flaxseed soaked hot, strained and dried (one pot step)
        recipe(out, ProcessTypes.BOILING).in("materials/flaxseed", 4).inFluid("water", BUCKET).out("flaxseed_gum", 1).source(40)
            .heated().stirs(STIRS).add("flaxseed_gum");
        // 356 A: monk fruit steeped hot (as 36 A)
        recipe(out, ProcessTypes.EXTRACTING).in("materials/monk_fruit", 4).inFluid("water", BUCKET).out("mogroside", 1).source(2)
            .heated().stirs(STIRS).add("mogroside");
    }

    private static void heatedDrying(RecipeGen.Output out, String input, String result) {
        recipe(out, ProcessTypes.DRYING).in(input, 1).out(result, 1).time(VANILLA).heated()
            .vanilla(VanillaDevice.CAMPFIRE, VanillaDevice.FURNACE, VanillaDevice.SMOKER).add(result + "_heated");
    }

    // ---- builder ----

    private static Builder recipe(RecipeGen.Output out, ProcessType type) {
        return new Builder(out, type);
    }

    /**
     * A recipe written as JSON. Inputs name {@code foodindustrial:} tags, outputs ids of this mod;
     * {@code source}, {@code removal} and {@code keep} set the purity rule of the last output.
     */
    private static final class Builder {
        private final RecipeGen.Output out;
        private final ProcessType type;
        private final JsonArray inputs = new JsonArray();
        private final JsonArray outputs = new JsonArray();
        private final JsonObject body = new JsonObject();
        private final JsonObject requirements = new JsonObject();
        private final JsonObject factors = new JsonObject();
        private final JsonArray catalysts = new JsonArray();

        Builder(RecipeGen.Output out, ProcessType type) {
            this.out = out;
            this.type = type;
        }

        /** Item input: {@code count} of tag {@code foodindustrial:<tag>}. */
        Builder in(String tag, int count) {
            JsonObject input = new JsonObject();
            input.addProperty("ingredient", "#" + NS + ":" + tag);
            if (count != 1) {
                input.addProperty("count", count);
            }
            inputs.add(input);
            return this;
        }

        /** Fluid input: {@code amount} mB of fluid tag {@code foodindustrial:<tag>}. */
        Builder inFluid(String tag, int amount) {
            JsonObject fluid = new JsonObject();
            fluid.addProperty("type", NS + ":fluid");
            fluid.addProperty("tag", NS + ":" + tag);
            fluid.addProperty("amount", amount);
            JsonObject input = new JsonObject();
            input.add("fluid", fluid);
            inputs.add(input);
            return this;
        }

        /** Item output {@code foodindustrial:<id>}. */
        Builder out(String id, int count) {
            JsonObject output = new JsonObject();
            output.addProperty("item", NS + ":" + id);
            if (count != 1) {
                output.addProperty("count", count);
            }
            outputs.add(output);
            return this;
        }

        /** Fluid output {@code foodindustrial:<id>}. */
        Builder outFluid(String id, int amount) {
            JsonObject output = new JsonObject();
            output.addProperty("fluid", NS + ":" + id);
            output.addProperty("amount", amount);
            outputs.add(output);
            return this;
        }

        Builder source(double value) {
            return purity("source", value, null);
        }

        Builder removal(double value, String t0Max) {
            return purity("removal", value, t0Max);
        }

        Builder keep() {
            return purity("keep", Double.NaN, null);
        }

        private Builder purity(String mode, double value, String t0Max) {
            JsonObject rule = new JsonObject();
            rule.addProperty("mode", mode);
            if (!Double.isNaN(value)) {
                rule.addProperty("value", value);
            }
            if (t0Max != null) {
                rule.addProperty("t0_max", t0Max);
            }
            outputs.get(outputs.size() - 1).getAsJsonObject().add("purity", rule);
            return this;
        }

        Builder catalyst(String tag, int runs) {
            JsonObject catalyst = new JsonObject();
            catalyst.addProperty("ingredient", "#" + NS + ":" + tag);
            catalyst.addProperty("runs", runs);
            catalysts.add(catalyst);
            return this;
        }

        Builder time(int ticks) {
            body.addProperty("time", ticks);
            return this;
        }

        /** Stirs of a pot, or actions of a hand tool; machines take {@link #MACHINE} ticks for the step. */
        Builder stirs(int actions) {
            JsonObject manual = new JsonObject();
            manual.addProperty("actions", actions);
            body.add("manual", manual);
            if (!body.has("time")) {
                body.addProperty("time", MACHINE);
            }
            return this;
        }

        Builder heated() {
            requirements.addProperty("temperature", "heated");
            return this;
        }

        Builder openFlame() {
            return method("open_flame");
        }

        Builder filtration() {
            return method("filtration");
        }

        Builder settling() {
            return method("settling");
        }

        private Builder method(String method) {
            JsonArray methods = requirements.has("methods") ? requirements.getAsJsonArray("methods") : new JsonArray();
            methods.add(method);
            requirements.add("methods", methods);
            return this;
        }

        /** Vanilla blocks that run it (machines/01 §5). */
        Builder vanilla(VanillaDevice... devices) {
            JsonArray list = new JsonArray();
            for (VanillaDevice device : devices) {
                list.add(device.id());
            }
            body.add("vanilla_devices", list);
            return this;
        }

        /** Crock sealed. */
        Builder sealed() {
            requirements.addProperty("atmosphere", "airtight");
            return this;
        }

        /** Crock open. */
        Builder open() {
            requirements.addProperty("atmosphere", "air");
            return this;
        }

        Builder environment(String... words) {
            JsonArray environment = new JsonArray();
            for (String word : words) {
                environment.add(word);
            }
            requirements.add("environment", environment);
            return this;
        }

        Builder factor(String environment, double time, double yield) {
            JsonObject factor = new JsonObject();
            if (time != 1.0) {
                factor.addProperty("time", time);
            }
            if (yield != 1.0) {
                factor.addProperty("yield", yield);
            }
            factors.add(environment, factor);
            return this;
        }

        void add(String name) {
            JsonObject json = new JsonObject();
            json.add("inputs", inputs);
            json.add("outputs", outputs);
            body.entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
            if (requirements.size() > 0) {
                json.add("requirements", requirements);
            }
            if (!catalysts.isEmpty()) {
                json.add("catalyst", catalysts.size() == 1 ? catalysts.get(0) : catalysts);
            }
            if (factors.size() > 0) {
                json.add("environment_factors", factors);
            }
            out.add("manual/" + name, out.parse(type, json));
        }
    }
}
