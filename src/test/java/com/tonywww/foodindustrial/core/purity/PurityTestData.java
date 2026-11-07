package com.tonywww.foodindustrial.core.purity;

import com.google.gson.JsonParser;
import com.tonywww.foodindustrial.material.MaterialDef;
import com.tonywww.foodindustrial.material.MaterialTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /** A K0 material with a source purity, built outside the registered data (the master table has none now). */
    public static PurityData.Material k0Material(double sourcePercent) {
        List<String> errors = new ArrayList<>();
        String id = "foodindustrial:test_k0";
        PurityData data = PurityData.build(PurityData.builtinCategories(),
            Map.of(id, JsonParser.parseString("{\"category\":\"foodindustrial:k0_raw\",\"source_purity\":" + sourcePercent + "}")), errors::add);
        if (!errors.isEmpty()) {
            throw new IllegalStateException(errors.toString());
        }
        return data.material(id);
    }
}
