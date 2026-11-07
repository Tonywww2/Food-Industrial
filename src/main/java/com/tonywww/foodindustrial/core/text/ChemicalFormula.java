package com.tonywww.foodindustrial.core.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts ASCII formulas from the material table ({@code CaSO4·2H2O}, {@code (C6H7NaO6)n}) to their
 * display form with subscripts ({@code CaSO₄·2H₂O}, {@code (C₆H₇NaO₆)ₙ}). Digits directly after an
 * element symbol, a closing bracket or another subscript digit become subscripts; leading
 * coefficients (start of text, after {@code ·} or a space) stay normal; {@code n} after a closing
 * bracket becomes {@code ₙ}. {@link #runs} also colours the element symbols by {@link ElementColors}.
 */
public final class ChemicalFormula {
    private static final char[] SUBSCRIPT_DIGITS = {'₀', '₁', '₂', '₃', '₄', '₅', '₆', '₇', '₈', '₉'};

    private ChemicalFormula() {
    }

    /** A run of the ASCII formula with one colour (RGB), either all subscript or all normal. */
    public record Run(String ascii, boolean subscript, int color) {
        /** The run with subscript characters ({@code 2} → {@code ₂}) when it is a subscript. */
        public String display() {
            return ChemicalFormula.display(ascii, subscript);
        }
    }

    public static String toDisplay(String ascii) {
        StringBuilder out = new StringBuilder(ascii.length());
        for (Run run : runs(ascii)) {
            out.append(run.display());
        }
        return out.toString();
    }

    /**
     * The formula split into runs of one colour and one size: each element symbol in its
     * {@link ElementColors} colour, everything else (digits, brackets, {@code ·}, coefficients) in
     * {@link ElementColors#OTHER}. Subscript rules as in {@link #toDisplay}.
     */
    public static List<Run> runs(String ascii) {
        List<Run> runs = new ArrayList<>();
        StringBuilder run = new StringBuilder();
        int runColor = -1;
        boolean runSubscript = false;
        boolean subscript = false;
        char previous = 0;
        int i = 0;
        while (i < ascii.length()) {
            char c = ascii.charAt(i);
            String token = String.valueOf(c);
            int color = ElementColors.OTHER;
            boolean isSubscript = false;
            if (Character.isUpperCase(c)) {
                if (i + 1 < ascii.length() && Character.isLowerCase(ascii.charAt(i + 1))
                    && ElementColors.isElement(ascii.substring(i, i + 2))) {
                    token = ascii.substring(i, i + 2);
                }
                color = ElementColors.of(token);
                subscript = false;
            } else if (Character.isDigit(c)) {
                boolean afterClosing = previous == ')' || previous == ']';
                isSubscript = Character.isLetter(previous) || afterClosing || (subscript && Character.isDigit(previous));
                subscript = isSubscript;
            } else {
                isSubscript = c == 'n' && (previous == ')' || previous == ']');
                subscript = false;
            }
            if (run.length() > 0 && (color != runColor || isSubscript != runSubscript)) {
                runs.add(new Run(run.toString(), runSubscript, runColor));
                run.setLength(0);
            }
            run.append(token);
            runColor = color;
            runSubscript = isSubscript;
            previous = token.charAt(token.length() - 1);
            i += token.length();
        }
        if (run.length() > 0) {
            runs.add(new Run(run.toString(), runSubscript, runColor));
        }
        return runs;
    }

    private static String display(CharSequence ascii, boolean subscript) {
        if (!subscript) {
            return ascii.toString();
        }
        StringBuilder out = new StringBuilder(ascii.length());
        for (int i = 0; i < ascii.length(); i++) {
            char c = ascii.charAt(i);
            out.append(c == 'n' ? 'ₙ' : SUBSCRIPT_DIGITS[c - '0']);
        }
        return out.toString();
    }
}
