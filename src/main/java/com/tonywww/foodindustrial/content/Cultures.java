package com.tonywww.foodindustrial.content;

import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;
import com.tonywww.foodindustrial.recipe.RecipeGen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.tonywww.foodindustrial.recipe.ProcessTypes.CULTURING;

/**
 * The culture bench's work for the content batches (P51, additives/08 §菌种与酶): one isolation plate per
 * natural sample, on which every culture found in that sample grows beside wild mold; mutagenesis plates
 * for the bred strains; purification and expansion for every culture of the material table. The plates of
 * several cultures from one sample are one recipe, since the same inputs choose one recipe (game settings,
 * 2026-11-11).
 */
public final class Cultures extends Batch {
    /** Isolation: sample → the cultures that grow on its plate (additives/08 自然来源). */
    private static final Map<String, List<String>> ISOLATION = new LinkedHashMap<>();
    /** Mutagenesis (bred from soil, 08): sample → strains. */
    private static final Map<String, List<String>> MUTAGENESIS = new LinkedHashMap<>();

    static {
        ISOLATION.put("soil", List.of("corynebacterium", "streptomyces_gilvosporeus", "clostridium_butyricum", "bacillus_acidopullulyticus",
            "streptoverticillium", "pseudomonas_dacunhae", "alcaligenes_myxogenes", "sphingomonas_sanxanigenens", "streptomyces_albulus",
            "arthrobacter", "aspergillus_fijiensis", "chryseobacterium", "bacillus_amyloliquefaciens", "streptomyces_rubiginosus",
            // I31 (17 新增菌种与酶)
            "clostridium_acetobutylicum", "mortierella", "carnitine_hk4"));
        ISOLATION.put("straw", List.of("bacillus_subtilis", "bacillus_licheniformis", "bacillus_amyloliquefaciens"));
        ISOLATION.put("decaying_plant", List.of("blakeslea_plus", "blakeslea_minus", "ashbya", "fusarium"));
        ISOLATION.put("fruit", List.of("yeast", "gluconobacter", "pseudomonas_fluorescens", "hansenula"));
        ISOLATION.put("mold", List.of("aspergillus_niger", "penicillium_citrinum"));
        ISOLATION.put("dairy", List.of("propionibacterium", "lactococcus", "kluyveromyces"));
        ISOLATION.put("honey", List.of("moniliella"));
        ISOLATION.put("leaf", List.of("xanthomonas", "aureobasidium"));
        ISOLATION.put("aquatic_plant", List.of("sphingomonas"));
        ISOLATION.put("rotten_wood", List.of("trichoderma", "chaetomium"));
        ISOLATION.put("compost", List.of("rhizomucor", "aeribacillus"));
        ISOLATION.put("marine", List.of("schizochytrium"));
        ISOLATION.put("rotten_fat", List.of("yarrowia"));
        ISOLATION.put("raw_meat", List.of("e_coli"));
        ISOLATION.put("beetroot", List.of("protaminobacter"));
        ISOLATION.put("mushroom", List.of("edible_fungus"));
        ISOLATION.put("cocoa", List.of("lasiodiplodia"));  // I44: 可可毛色二孢 from cocoa (08 §C, game setting)
        MUTAGENESIS.put("soil", List.of("ketogulonicigenium", "bacillus_megaterium"));
    }

    private Cultures(RecipeGen.Output out) {
        super(out, "cultures");
    }

    public static void bootstrap() {
        RecipeGen.register(out -> new Cultures(out).generate());
    }

    private void generate() {
        ISOLATION.forEach((sample, cultures) -> plate("isolation", sample, cultures));
        MUTAGENESIS.forEach((sample, cultures) -> plate("mutagenesis", sample, cultures));
        // I44 (08 §C 菌藻类): the algae picked off a plate of their lake sample, dried to powder in the game's terms
        r(CULTURING).in("samples/alkaline_lake", 1).in("chemicals/agar", 1).out("spirulina", 1).out("wild_mold", 1).mode("isolation")
            .plateWeights(2, 1).add("alkaline_lake_isolation");
        r(CULTURING).in("samples/brine", 1).in("chemicals/agar", 1).out("dunaliella", 1).out("wild_mold", 1).mode("isolation")
            .plateWeights(2, 1).add("brine_isolation");
        // the non-toxigenic Penicillium citrinum bred from the wild strain (2026-11-11, user)
        r(CULTURING).in("cultures/penicillium_citrinum", 1).in("chemicals/agar", 1).out("penicillium_citrinum_nontoxic", 1).src(20)
            .out("penicillium_citrinum", 1).mode("mutagenesis").plateWeights(1, 3).add("penicillium_citrinum_mutagenesis");
        for (MaterialDef material : MaterialTable.load()) {
            if (material.category().equals("culture")) {
                cultureWork(material.id());
            }
        }
    }

    /** A plate of the sample: each culture weight 1, wild mold as many as the cultures together. */
    private void plate(String mode, String sample, List<String> cultures) {
        ContentRecipe recipe = r(CULTURING).in("samples/" + sample, 1).in("chemicals/agar", 1);
        int[] weights = new int[cultures.size() + 1];
        for (int i = 0; i < cultures.size(); i++) {
            recipe.out(cultures.get(i), 1).src(20);
            weights[i] = 1;
        }
        recipe.out("wild_mold", 1);
        weights[cultures.size()] = cultures.size();
        recipe.mode(mode).plateWeights(weights).add(sample + "_" + mode);
    }
}
