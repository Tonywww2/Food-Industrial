package com.tonywww.foodindustrial.food.quality;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.data.StrictCodecs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The ingredient step of a finished product (foods/05 §2, §3; foods/04 §2): the recipe field {@code quality}. A recipe
 * with it is the product's single ingredient step: it fixes the quality from the additives in the device's additive
 * slots (A) and the purity label from its inputs, and writes both onto its products. Recipes without it carry the
 * quality of their inputs over.
 * <pre>
 * "quality": {
 *   "items": [ "color", { "item": "taste", "accepts": [ "sweetener", "citric_acid" ], "count": 2 } ],
 *   "min_grade": "food"
 * }
 * </pre>
 * {@code items}: the evaluated quality items, 0–4, each with two quality slots (foods/04 §2); {@code accepts} lists
 * function classes ({@link AdditiveFunctions}) or materials and replaces the item's default classes; {@code count} is
 * what each additive used on the item takes per run (default 1). {@code min_grade}: the purity the inputs must reach,
 * default food grade (foods/01 §5).
 *
 * @param items    evaluated items in display order
 * @param minGrade purity required of the inputs for no label
 */
public record QualitySpec(List<Slot> items, PurityGrade minGrade) {
    /**
     * One evaluated item.
     *
     * @param accepts classes or materials; empty = the item's default classes and its synergy partners
     */
    public record Slot(QualityItem item, List<String> accepts, int count) {
        private static final Codec<QualityItem> ITEM = Codec.STRING.comapFlatMap(id -> {
            QualityItem item = QualityItem.byId(id);
            return item == null ? DataResult.error(() -> "Unknown quality item " + id) : DataResult.success(item);
        }, QualityItem::id);

        private static final Codec<Slot> OBJECT = RecordCodecBuilder.create(instance -> instance.group(
            ITEM.fieldOf("item").forGetter(Slot::item),
            StrictCodecs.optional("accepts", Codec.STRING.listOf(), List.of()).forGetter(Slot::accepts),
            StrictCodecs.optional("count", Codec.intRange(1, 64), 1).forGetter(Slot::count)
        ).apply(instance, Slot::new));

        /** A bare item name, or the full object. */
        public static final Codec<Slot> CODEC = Codec.either(ITEM, OBJECT).xmap(
            either -> either.map(item -> new Slot(item, List.of(), 1), slot -> slot),
            slot -> slot.accepts.isEmpty() && slot.count == 1 ? Either.left(slot.item) : Either.right(slot));

        public Slot {
            accepts = List.copyOf(accepts);
        }

        /** Whether an additive of {@code material} may go on this item; a flavouring goes on taste only (I48, foods/11 §6). */
        public boolean accepts(String material) {
            if (com.tonywww.foodindustrial.food.flavor.Flavorings.typeOfId(material) != null) {
                return item == QualityItem.TASTE;
            }
            if (accepts.isEmpty()) {
                return AdditiveFunctions.itemOf(AdditiveFunctions.classOf(material)) == item || Synergies.member(item, material);
            }
            String className = AdditiveFunctions.classOf(material);
            for (String entry : accepts) {
                if (entry.equals(material) || entry.equals(className)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final Codec<PurityGrade> GRADE = Codec.STRING.comapFlatMap(id -> {
        PurityGrade grade = PurityGrade.byId(id);
        return grade == null ? DataResult.error(() -> "Unknown purity grade " + id) : DataResult.success(grade);
    }, PurityGrade::id);

    public static final Codec<QualitySpec> CODEC = RecordCodecBuilder.<QualitySpec>create(instance -> instance.group(
        StrictCodecs.optional("items", Slot.CODEC.listOf(), List.of()).forGetter(QualitySpec::items),
        StrictCodecs.optional("min_grade", GRADE, PurityGrade.FOOD).forGetter(QualitySpec::minGrade)
    ).apply(instance, QualitySpec::new)).flatXmap(QualitySpec::validate, DataResult::success);

    public QualitySpec {
        items = List.copyOf(items);
    }

    private static DataResult<QualitySpec> validate(QualitySpec spec) {
        List<String> problems = new ArrayList<>();
        Set<QualityItem> seen = new HashSet<>();
        for (Slot slot : spec.items) {
            if (!seen.add(slot.item)) {
                problems.add("quality item " + slot.item.id() + " listed twice");
            }
        }
        return problems.isEmpty() ? DataResult.success(spec) : DataResult.error(() -> String.join("; ", problems));
    }

    /** Whether the recipe evaluates {@code item}. */
    public boolean evaluates(QualityItem item) {
        for (Slot slot : items) {
            if (slot.item == item) {
                return true;
            }
        }
        return false;
    }
}
