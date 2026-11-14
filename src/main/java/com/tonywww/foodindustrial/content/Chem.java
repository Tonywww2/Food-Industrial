package com.tonywww.foodindustrial.content;

import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;

import java.util.HashMap;
import java.util.Map;

/**
 * A process recipe written by material id (I31): an input or output is an item or a fluid as the material
 * table says, one unit being one item or 1000 mB; an output that carries a purity comes out at its source value
 * unless a rule follows. Paths with a slash ({@code materials/…}, {@code foods/…}) are item tags as written.
 */
public final class Chem {
    private static final int B = 1000;
    private static Map<String, MaterialDef> materials;

    private final ContentRecipe recipe;

    Chem(ContentRecipe recipe) {
        this.recipe = recipe;
    }

    private static MaterialDef def(String id) {
        if (materials == null) {
            Map<String, MaterialDef> map = new HashMap<>();
            MaterialTable.load().forEach(def -> map.put(def.id(), def));
            materials = map;
        }
        MaterialDef def = materials.get(id);
        if (def == null) {
            throw new IllegalStateException("Unknown material " + id);
        }
        return def;
    }

    private static boolean fluid(String id) {
        return id.equals("water") || (!id.contains("/") && def(id).isFluid());
    }

    public Chem in(String id) {
        return in(id, 1);
    }

    /** {@code units} items, or {@code units} × 1000 mB of a fluid. */
    public Chem in(String id, int units) {
        if (id.contains("/")) {
            recipe.in(id, units);
        } else if (fluid(id)) {
            recipe.fin(id.equals("water") ? id : def(id).tag(), units * B);
        } else {
            recipe.in(def(id).tag(), units);
        }
        return this;
    }

    /** An input with a purity threshold ({@code food} …). */
    public Chem in(String id, int units, String min) {
        if (fluid(id)) {
            recipe.fin(def(id).tag(), units * B, min);
        } else {
            recipe.in(id.contains("/") ? id : def(id).tag(), units, min);
        }
        return this;
    }

    /** A fluid input in mB. */
    public Chem inMb(String id, int mB) {
        recipe.fin(id.equals("water") ? id : def(id).tag(), mB);
        return this;
    }

    public Chem out(String id) {
        return out(id, 1);
    }

    /** {@code units} items, or {@code units} × 1000 mB; at the source value when the material carries a purity. */
    public Chem out(String id, int units) {
        return fluid(id) ? outMb(id, units * B) : outItem(id, units);
    }

    private Chem outItem(String id, int count) {
        recipe.out(id, count);
        if (!id.contains(":") && def(id).bearer()) {
            recipe.src();
        }
        return this;
    }

    /** A fluid output in mB. */
    public Chem outMb(String id, int mB) {
        recipe.fout(id.equals("water") ? "minecraft:water" : id, mB);
        if (!id.equals("water") && def(id).bearer()) {
            recipe.src();
        }
        return this;
    }

    /** An output without a purity rule of its own: the recipe type's default (machines/01 §1.1) applies. */
    public Chem outDefault(String id, int units) {
        if (fluid(id)) {
            recipe.fout(id, units * B);
        } else {
            recipe.out(id, units);
        }
        return this;
    }

    /** A fluid output in mB without a purity rule of its own (the recipe type's default). */
    public Chem outDefaultMb(String id, int mB) {
        recipe.fout(id, mB);
        return this;
    }

    /** Overrides the last output's purity with a source value. */
    public Chem src(double percent) {
        recipe.src(percent);
        return this;
    }

    /** The last output takes {@code percent} of the impurities out (instead of the recipe type's default). */
    public Chem rm(double percent) {
        recipe.rm(percent);
        return this;
    }

    public Chem cat(String tag, int runs) {
        recipe.cat(tag, runs);
        return this;
    }

    public Chem cat(String tag, int runs, String min) {
        recipe.cat(tag, runs, min);
        return this;
    }

    public Chem mode(String mode) {
        recipe.mode(mode);
        return this;
    }

    public Chem method(String... methods) {
        recipe.method(methods);
        return this;
    }

    public Chem temp(String value) {
        recipe.temp(value);
        return this;
    }

    public Chem pressure(String value) {
        recipe.pressure(value);
        return this;
    }

    public Chem inert() {
        recipe.inert();
        return this;
    }

    public Chem air() {
        recipe.air();
        return this;
    }

    public Chem airtight() {
        recipe.airtight();
        return this;
    }

    public Chem sterile() {
        recipe.sterile();
        return this;
    }

    public Chem tier(int tier) {
        recipe.tier(tier);
        return this;
    }

    public Chem time(int ticks) {
        recipe.time(ticks);
        return this;
    }

    /** Plate weights of a culture-bench recipe (I17): one per output. */
    public Chem plateWeights(int... weights) {
        recipe.plateWeights(weights);
        return this;
    }

    public Chem heat(String value) {
        recipe.heat(value);
        return this;
    }

    public void add(String name) {
        recipe.add(name);
    }
}
