package com.tonywww.foodindustrial.core.text;

import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Tooltip lines in the formats of the text standard §3. No line ever hints at holding Shift.
 * Other categories (finished food, tools, decoration, §3.3) add no lines until their rules exist.
 */
public final class Tooltips {
    /** Language key of the purity line: {@code %s（%s%%）}, grade then percentage. */
    public static final String PURITY_KEY = "tooltip.foodindustrial.purity";
    /** Language key of the content line of K0 materials: {@code 含量 %s%%}. */
    public static final String CONTENT_KEY = "tooltip.foodindustrial.content";

    private Tooltips() {
    }

    /**
     * Material / additive item (§3.1): formula (element colours) when known, purity line (grade colour) when
     * the item carries purity, and the one-line description (grey) while Shift is held.
     *
     * @param formula        ASCII formula from the material table, or {@code null}
     * @param descriptionKey language key of the Shift description
     */
    public static void material(ItemStack stack, @Nullable String formula, String descriptionKey, List<Component> lines, boolean shiftDown) {
        formula(formula, lines);
        if (!stack.isEmpty() && Purity.bears(stack.getItem())) {
            purityLine(Purity.materialOf(stack.getItem()), Purity.get(stack), lines);
        }
        description(descriptionKey, lines, shiftDown);
    }

    /** Material in fluid form (tanks, JEI): same lines as {@link #material(ItemStack, String, String, List, boolean)}. */
    public static void material(FluidAmount fluid, @Nullable String formula, String descriptionKey, List<Component> lines, boolean shiftDown) {
        formula(formula, lines);
        if (!fluid.isEmpty() && Purity.bears(fluid.fluid())) {
            purityLine(Purity.materialOf(fluid.fluid()), Purity.get(fluid), lines);
        }
        description(descriptionKey, lines, shiftDown);
    }

    /** Machine, multiblock part or hand tool (§3.2): nothing by default, the description while Shift is held. */
    public static void machine(String descriptionKey, List<Component> lines, boolean shiftDown) {
        description(descriptionKey, lines, shiftDown);
    }

    /**
     * {@code 精制（96.3%）} coloured by the grade of {@code material}, or {@code 含量 7.3%} (grey) for
     * K0 materials; nothing for {@link Purity#NONE}.
     */
    public static void purityLine(@Nullable String material, int b, List<Component> lines) {
        if (b == Purity.NONE) {
            return;
        }
        Optional<PurityGrade> grade = Purity.grade(material, b);
        if (grade.isPresent()) {
            lines.add(Component.translatable(PURITY_KEY, Component.translatable(grade.get().translationKey()), PurityScale.format(b))
                .withStyle(grade.get().color()));
        } else {
            lines.add(Component.translatable(CONTENT_KEY, PurityScale.format(b)).withStyle(ChatFormatting.GRAY));
        }
    }

    private static void formula(@Nullable String formula, List<Component> lines) {
        if (formula != null && !formula.isEmpty()) {
            lines.add(coloredFormula(formula));
        }
    }

    /** A formula with each element symbol in its group colour and the rest light grey (§3.1). */
    public static MutableComponent coloredFormula(String formula) {
        MutableComponent line = Component.empty();
        for (ChemicalFormula.Run run : ChemicalFormula.runs(formula)) {
            TextColor color = TextColor.fromRgb(run.color());
            line.append(Component.literal(run.display()).withStyle(style -> style.withColor(color)));
        }
        return line;
    }

    private static void description(String descriptionKey, List<Component> lines, boolean shiftDown) {
        if (shiftDown) {
            // a tier named in the sentence takes its colour (§3.4)
            String text = Language.getInstance().getOrDefault(descriptionKey);
            lines.add(TierText.namesTier(text) ? TierText.colored(text, ChatFormatting.GRAY)
                : Component.translatable(descriptionKey).withStyle(ChatFormatting.GRAY));
        }
    }
}
