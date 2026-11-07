package com.tonywww.foodindustrial.material;

import com.tonywww.foodindustrial.core.text.ChemicalFormula;
import com.tonywww.foodindustrial.platform.client.PlatformClient;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Client only: the chemical formula along the top of chemical items and their buckets in GUI slots, so
 * that look-alike icons can be told apart (2026-11-04, text standard §3.1), element symbols in their
 * group colours like the tooltip formula line (2026-11-05). Materials without a definite formula
 * (mixtures, extracts) get none.
 */
final class MaterialLabels {
    private MaterialLabels() {
    }

    static void register(Collection<ModMaterials.Entry> entries) {
        for (ModMaterials.Entry entry : entries) {
            MaterialDef def = entry.def();
            if (!"chemical".equals(def.category()) || def.formula() == null) {
                continue;
            }
            Supplier<? extends Item> item = entry.item() != null ? entry.item() : entry.fluid().bucket();
            if (item == null) {
                continue;
            }
            List<PlatformClient.LabelRun> runs = ChemicalFormula.runs(def.formula()).stream()
                .map(run -> new PlatformClient.LabelRun(run.ascii(), run.subscript(), 0xFF000000 | run.color()))
                .toList();
            PlatformClient.registerSlotLabel(item, runs);
        }
    }
}
