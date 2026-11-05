package com.tonywww.foodindustrial.core.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts ASCII formulas from the material table ({@code CaSO4·2H2O}, {@code (C6H7NaO6)n}) to their
 * display form with subscripts ({@code CaSO₄·2H₂O}, {@code (C₆H₇NaO₆)ₙ}). Digits directly after an
 * element symbol, a closing bracket or another subscript digit become subscripts; leading
 * coefficients (start of text, after {@code ·} or a space) stay normal; {@code n} after a closing
 * bracket becomes {@code ₙ}.
 */
public final class ChemicalFormula {
    private static final char[] SUBSCRIPT_DIGITS = {'₀', '₁', '₂', '₃', '₄', '₅', '₆', '₇', '₈', '₉'};

    /** A run of the ASCII formula that is either all subscript or all normal text. */
    public record Part(String text, boolean subscript) {
    }

    private ChemicalFormula() {
    }

    public static String toDisplay(String ascii) {
        StringBuilder out = new StringBuilder(ascii.length());
        for (Part part : parts(ascii)) {
            for (int i = 0; i < part.text().length(); i++) {
                char c = part.text().charAt(i);
                out.append(!part.subscript() ? c : c == 'n' ? 'ₙ' : SUBSCRIPT_DIGITS[c - '0']);
            }
        }
        return out.toString();
    }

    /** The formula split into normal and subscript runs, by the rules of {@link #toDisplay}. */
    public static List<Part> parts(String ascii) {
        List<Part> parts = new ArrayList<>();
        StringBuilder run = new StringBuilder();
        boolean runSubscript = false;
        boolean subscript = false;
        char previous = 0;
        for (int i = 0; i < ascii.length(); i++) {
            char c = ascii.charAt(i);
            boolean afterClosing = previous == ')' || previous == ']';
            boolean isSubscript;
            if (Character.isDigit(c)) {
                isSubscript = Character.isLetter(previous) || afterClosing || (subscript && Character.isDigit(previous));
                subscript = isSubscript;
            } else {
                isSubscript = c == 'n' && afterClosing;
                subscript = false;
            }
            if (run.length() > 0 && isSubscript != runSubscript) {
                parts.add(new Part(run.toString(), runSubscript));
                run.setLength(0);
            }
            run.append(c);
            runSubscript = isSubscript;
            previous = c;
        }
        if (run.length() > 0) {
            parts.add(new Part(run.toString(), runSubscript));
        }
        return parts;
    }
}
