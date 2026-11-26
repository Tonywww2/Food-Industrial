package com.tonywww.foodindustrial.food.quality;

import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.food.flavor.FlavorType;
import com.tonywww.foodindustrial.food.flavor.Flavorings;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * How the ingredient step fixes a product's quality and label, and how processing steps carry them (foods/04 §2–§4,
 * foods/05 §2–§3, foods/01 §5).
 * <ol>
 *   <li>Each additive in the additive slots goes to one evaluated item that accepts it; an additive no item accepts,
 *       or more than two kinds on one item, keeps the device from starting (foods/05 §3: nothing is wasted). When an
 *       additive could go to several items, the assignment with the best result is taken.</li>
 *   <li>An item without additives is ordinary, with one or two good, with a synergy pair excellent. A flavouring (I48,
 *       foods/11 §6) goes on taste only: one of a type the recipe lists there makes taste excellent, any other counts as
 *       one good additive; the foods of GB 2760 table B.1 take no flavouring.</li>
 *   <li>The label compares the purity grade of every input and additive with the recipe's minimum: one grade below
 *       anywhere gives "homemade", two or more "poor", every one above "premium" (foods/01 §5).</li>
 *   <li>Flags: aspartame and its acesulfame salt among inputs or additives, a curing agent on colour, nitrite in the
 *       salt position (foods/02 §4.4); flags and the worse labels of inputs that already carry quality pass on.</li>
 * </ol>
 */
public final class QualityRules {
    /** Materials taken as salt when they sit in a salt position (foods/02 §4.4). */
    public static final Set<String> NITRITES = Set.of("sodium_nitrite", "potassium_nitrite");

    /** Whether {@code material} is a nitrite (salt-position misuse, foods/02 §4.4). */
    public static boolean isNitrite(@org.jetbrains.annotations.Nullable String material) {
        return material != null && NITRITES.contains(material);
    }
    /** Curing agents that count on the colour item (foods/02 §4.4). */
    public static final Set<String> CURING_AGENTS = Set.of("sodium_nitrite", "potassium_nitrite", "sodium_nitrate", "potassium_nitrate");

    /**
     * Products of GB 2760 table B.1 among the finished products, which take no flavouring (foods/11 §1, §6): fats and
     * oils, sugars and salts of the first batch; later batches add theirs.
     */
    private static final Set<String> NO_FLAVORING = new java.util.HashSet<>(List.of("lard", "tallow", "edible_oil", "peanut_oil",
        "sesame_oil", "soft_white_sugar", "brown_sugar", "rock_sugar", "sugar_cube", "icing_sugar", "iodized_salt", "low_sodium_salt",
        "sea_salt"));

    private QualityRules() {
    }

    /** Declares a product (plain id) of table B.1: its ingredient step takes no flavouring (foods/11 §6). */
    public static void noFlavoring(String product) {
        NO_FLAVORING.add(product);
    }

    /**
     * One required input of the ingredient step.
     *
     * @param grade         purity grade of the consumed stack or fluid, or {@code null} when it carries none
     * @param code          quality the input already carries
     * @param material      its material, or {@code null}
     * @param nitriteAsSalt a nitrite sits in the recipe's salt position
     */
    public record Input(@Nullable PurityGrade grade, int code, @Nullable String material, boolean nitriteAsSalt) {
    }

    /**
     * Result of the ingredient step.
     *
     * @param code    quality of the products, or {@code -1} when the device must not start
     * @param take    items taken from each additive slot per run
     * @param refusal language key suffix of why it must not start, or {@code null}
     */
    public record Outcome(int code, int[] take, @Nullable String refusal) {
        public boolean refused() {
            return code < 0;
        }
    }

    /**
     * The material an additive stack stands for: its own, or the fluid in a bottle or bucket; this mod's materials
     * without the namespace (the ids of the additive table and of materials.json).
     */
    @Nullable
    public static String material(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        String own = Purity.materialOf(stack.getItem());
        if (own != null) {
            return plain(own);
        }
        FlavorType flavoring = Flavorings.type(stack.getItem());
        if (flavoring != null) {
            return flavoring.itemId();
        }
        FluidAmount fluid = PlatformFluidContainers.contained(stack);
        return fluid.isEmpty() ? null : plain(Purity.materialOf(fluid.fluid()));
    }

    /** A material id without this mod's namespace. */
    @Nullable
    public static String plain(@Nullable String material) {
        return material != null && material.startsWith("foodindustrial:") ? material.substring("foodindustrial:".length()) : material;
    }

    /** Purity grade of an additive stack (bottled additives by their fluid), or {@code null}. */
    @Nullable
    public static PurityGrade grade(ItemStack stack) {
        if (Purity.materialOf(stack.getItem()) != null) {
            return Purity.grade(stack).orElse(null);
        }
        FluidAmount fluid = PlatformFluidContainers.contained(stack);
        return fluid.isEmpty() ? null : Purity.grade(fluid).orElse(null);
    }

    /**
     * The ingredient step of {@code spec} for a run of {@code product} with {@code additives} in the additive slots
     * (empty stacks allowed) and the required {@code inputs}.
     */
    public static Outcome ingredient(QualitySpec spec, String product, List<ItemStack> additives, List<Input> inputs) {
        int[] take = new int[additives.size()];
        // the kinds of additive, in slot order
        List<String> kinds = new ArrayList<>();
        List<List<Integer>> kindSlots = new ArrayList<>();
        for (int slot = 0; slot < additives.size(); slot++) {
            ItemStack stack = additives.get(slot);
            if (stack.isEmpty()) {
                continue;
            }
            String material = material(stack);
            if (material == null) {
                return refused(take, "additive_not_accepted");
            }
            int kind = kinds.indexOf(material);
            if (kind < 0) {
                kinds.add(material);
                kindSlots.add(new ArrayList<>());
                kind = kinds.size() - 1;
            }
            kindSlots.get(kind).add(slot);
        }
        List<QualitySpec.Slot> items = spec.items();
        String productId = plain(product);
        for (String kind : kinds) {
            if (Flavorings.typeOfId(kind) != null && NO_FLAVORING.contains(productId)) {
                return refused(take, "additive_not_accepted");
            }
        }
        int[][] candidates = new int[kinds.size()][];
        for (int k = 0; k < kinds.size(); k++) {
            List<Integer> fits = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).accepts(kinds.get(k))) {
                    fits.add(i);
                }
            }
            if (fits.isEmpty()) {
                return refused(take, "additive_not_accepted");
            }
            candidates[k] = fits.stream().mapToInt(Integer::intValue).toArray();
        }
        Search search = new Search(items, kinds, candidates);
        search.run(0, new int[kinds.size()], new int[items.size()]);
        if (search.best == null) {
            return refused(take, "additive_too_many");
        }
        int code = 0;
        int[] grades = search.grades(search.best);
        for (int i = 0; i < items.size(); i++) {
            code = FoodQuality.withItem(code, items.get(i).item(), grades[i]);
        }
        for (int k = 0; k < kinds.size(); k++) {
            int need = items.get(search.best[k]).count();
            for (int slot : kindSlots.get(k)) {
                int part = Math.min(need, additives.get(slot).getCount());
                take[slot] = part;
                need -= part;
            }
            if (need > 0) {
                return refused(new int[additives.size()], "additive_short");
            }
            String material = kinds.get(k);
            if (items.get(search.best[k]).item() == QualityItem.COLOR && CURING_AGENTS.contains(material)) {
                code |= FoodQuality.CURING_AGENT;
            }
            code |= declaration(material);
        }
        // the label and the flags of the inputs
        List<PurityGrade> grades2 = new ArrayList<>();
        int inherited = FoodQuality.LABEL_NONE;
        for (Input input : inputs) {
            if (input.grade() != null) {
                grades2.add(input.grade());
            }
            code |= declaration(input.material());
            if (input.nitriteAsSalt()) {
                code |= FoodQuality.NITRITE_MISUSE;
            }
            code |= FoodQuality.flags(input.code());
            inherited = worse(inherited, FoodQuality.label(input.code()));
        }
        for (int slot = 0; slot < additives.size(); slot++) {
            if (!additives.get(slot).isEmpty()) {
                PurityGrade grade = grade(additives.get(slot));
                if (grade != null) {
                    grades2.add(grade);
                }
            }
        }
        int label = label(grades2, spec.minGrade());
        if (inherited == FoodQuality.LABEL_POOR || inherited == FoodQuality.LABEL_HOMEMADE) {
            label = worse(label == FoodQuality.LABEL_PREMIUM ? FoodQuality.LABEL_NONE : label, inherited);
        }
        return new Outcome(FoodQuality.withLabel(code, label), take, null);
    }

    /**
     * The purity label (foods/01 §5): any grade two or more below {@code min} poor, one below homemade, all above
     * premium; none without graded inputs.
     */
    public static int label(List<PurityGrade> grades, PurityGrade min) {
        if (grades.isEmpty()) {
            return FoodQuality.LABEL_NONE;
        }
        boolean allAbove = true;
        int worst = Integer.MAX_VALUE;
        for (PurityGrade grade : grades) {
            int difference = grade.ordinal() - min.ordinal();
            worst = Math.min(worst, difference);
            allAbove &= difference >= 1;
        }
        if (worst <= -2) {
            return FoodQuality.LABEL_POOR;
        }
        if (worst == -1) {
            return FoodQuality.LABEL_HOMEMADE;
        }
        return allAbove ? FoodQuality.LABEL_PREMIUM : FoodQuality.LABEL_NONE;
    }

    /** A processing step: the inputs' quality merged (foods/05 §2). */
    public static int carried(int[] codes) {
        int code = FoodQuality.NONE;
        for (int value : codes) {
            code = FoodQuality.merge(code, value);
        }
        return code;
    }

    /** Declaration flags of a material (text-standard §3.3). */
    public static int declaration(@Nullable String material) {
        if ("aspartame".equals(material)) {
            return FoodQuality.ASPARTAME;
        }
        if ("aspartame_acesulfame".equals(material)) {
            return FoodQuality.ASPARTAME_ACESULFAME;
        }
        return 0;
    }

    private static int worse(int a, int b) {
        int rankA = a == FoodQuality.LABEL_POOR ? 3 : a == FoodQuality.LABEL_HOMEMADE ? 2 : a == FoodQuality.LABEL_NONE ? 1 : 0;
        int rankB = b == FoodQuality.LABEL_POOR ? 3 : b == FoodQuality.LABEL_HOMEMADE ? 2 : b == FoodQuality.LABEL_NONE ? 1 : 0;
        return rankA >= rankB ? a : b;
    }

    private static Outcome refused(int[] take, String why) {
        return new Outcome(-1, take, why);
    }

    /** Best assignment of additive kinds to items: at most two kinds per item, the highest total score. */
    private static final class Search {
        private final List<QualitySpec.Slot> items;
        private final List<String> kinds;
        private final int[][] candidates;
        @Nullable
        private int[] best;
        private int bestScore = -1;

        Search(List<QualitySpec.Slot> items, List<String> kinds, int[][] candidates) {
            this.items = items;
            this.kinds = kinds;
            this.candidates = candidates;
        }

        void run(int k, int[] assign, int[] used) {
            if (k == kinds.size()) {
                int score = 0;
                for (int grade : grades(assign)) {
                    score += grade;
                }
                if (score > bestScore) {
                    bestScore = score;
                    best = assign.clone();
                }
                return;
            }
            for (int item : candidates[k]) {
                if (used[item] < 2) {
                    used[item]++;
                    assign[k] = item;
                    run(k + 1, assign, used);
                    used[item]--;
                }
            }
        }

        /** Grade of each item under {@code assign}. */
        int[] grades(int[] assign) {
            int[] grades = new int[items.size()];
            for (int i = 0; i < items.size(); i++) {
                List<String> on = new ArrayList<>(2);
                for (int k = 0; k < kinds.size(); k++) {
                    if (assign[k] == i) {
                        on.add(kinds.get(k));
                    }
                }
                QualityItem item = items.get(i).item();
                int grade = on.isEmpty() ? FoodQuality.ORDINARY : FoodQuality.GOOD;
                if (on.size() == 2 && Synergies.pair(item, on.get(0), on.get(1))) {
                    grade = FoodQuality.EXCELLENT;
                }
                if (item == QualityItem.TASTE) {
                    // a flavouring of a type the recipe names on taste (foods/11 §6)
                    for (String material : on) {
                        if (Flavorings.typeOfId(material) != null && items.get(i).accepts().contains(material)) {
                            grade = FoodQuality.EXCELLENT;
                        }
                    }
                }
                grades[i] = grade;
            }
            return grades;
        }
    }
}
