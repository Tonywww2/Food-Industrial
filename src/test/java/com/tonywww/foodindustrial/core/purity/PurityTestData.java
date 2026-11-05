package com.tonywww.foodindustrial.core.purity;

import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;

/** Registers the master table's purity data for tests outside this package. */
public final class PurityTestData {
    private PurityTestData() {
    }

    public static synchronized void registerMaterials() {
        if (PurityData.builtin().materials().isEmpty()) {
            for (MaterialDef def : MaterialTable.load()) {
                if (def.purity() != null) {
                    PurityData.registerBuiltin("foodindustrial:" + def.id(), def.purity());
                }
            }
        }
    }
}
