package com.tonywww.foodindustrial.material;

import com.tonywww.foodindustrial.core.text.ChemicalFormula;
import com.tonywww.foodindustrial.platform.client.PlatformClient;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Client only: the chemical formula along the top of chemical items and their buckets in GUI slots, so
 * that look-alike icons can be told apart (2026-11-04, text standard §3.1). Materials without a
 * definite formula (mixtures, extracts) get none.
 */
final class MaterialLabels {
    /** Same colour as the tooltip formula line (yellow §e). */
    private static final int COLOR = 0xFF000000 | ChatFormatting.YELLOW.getColor();

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
            List<PlatformClient.LabelRun> runs = ChemicalFormula.parts(def.formula()).stream()
                .map(part -> new PlatformClient.LabelRun(part.text(), part.subscript()))
                .toList();
            PlatformClient.registerSlotLabel(item, runs, COLOR);
        }
    }
}
