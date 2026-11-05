package com.tonywww.foodindustrial.material;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * One row of the material master table (docs/additives 09 and 08 §C), exported to
 * {@code foodindustrial/materials.json} by {@code tools/materials/export_materials.py}.
 *
 * @param category open set: {@code chemical}, {@code catalyst}, {@code raw}, {@code culture}, {@code enzyme}, …
 * @param bearer   whether stacks carry a stored purity (a purity category other than K0, or K0 with a
 *                 source purity)
 * @param purity   purity data ({@code category}, {@code source_purity}, {@code grades}, additives/10 §4.7),
 *                 or {@code null} for materials without a purity category
 * @param color    ARGB tint of fluids, {@code 0} for solids
 * @param tag      recipe tag path ({@code chemicals/salt} for items, {@code sulfuric_acid} for fluids)
 * @param hazards  reserved hazard flags (machines/02 §8), no behaviour yet
 */
public record MaterialDef(String id, String nameZh, String nameEn, String professionalZh, String professionalEn,
                          @Nullable String commonZh, @Nullable String commonEn, @Nullable String formula,
                          State state, String category, boolean bearer, @Nullable JsonObject purity, int color,
                          @Nullable String gtceu, String tag, List<String> hazards) {
    /** Physical state column of the table. */
    public enum State {
        SOLID, LIQUID, GAS, SOLUTION;

        public boolean isFluid() {
            return this != SOLID;
        }

        static State parse(String value) {
            return valueOf(value.toUpperCase(Locale.ROOT));
        }
    }

    public boolean isFluid() {
        return state.isFluid();
    }
}
