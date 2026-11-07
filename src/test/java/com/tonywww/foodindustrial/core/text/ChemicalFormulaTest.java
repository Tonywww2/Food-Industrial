package com.tonywww.foodindustrial.core.text;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChemicalFormulaTest {
    @Test
    void textStandardExamples() {
        assertEquals("Na₂CO₃", ChemicalFormula.toDisplay("Na2CO3"));
        assertEquals("Ca(OH)₂", ChemicalFormula.toDisplay("Ca(OH)2"));
        assertEquals("CaSO₄·2H₂O", ChemicalFormula.toDisplay("CaSO4·2H2O"));
        assertEquals("(C₆H₇NaO₆)ₙ", ChemicalFormula.toDisplay("(C6H7NaO6)n"));
        assertEquals("C₅H₈NNaO₄·H₂O", ChemicalFormula.toDisplay("C5H8NNaO4·H2O"));
        assertEquals("NaHCO₃", ChemicalFormula.toDisplay("NaHCO3"));
        assertEquals("Ni", ChemicalFormula.toDisplay("Ni"));
    }

    @Test
    void multiDigitAndBrackets() {
        assertEquals("C₁₂H₂₂O₁₁", ChemicalFormula.toDisplay("C12H22O11"));
        assertEquals("KAl₃(SO₄)₂(OH)₆", ChemicalFormula.toDisplay("KAl3(SO4)2(OH)6"));
        assertEquals("(VO)₂P₂O₇", ChemicalFormula.toDisplay("(VO)2P2O7"));
        assertEquals("Ca₅(PO₄)₃F", ChemicalFormula.toDisplay("Ca5(PO4)3F"));
        assertEquals("KCl·MgCl₂·6H₂O", ChemicalFormula.toDisplay("KCl·MgCl2·6H2O"));
        assertEquals("Na₃H(CO₃)₂·2H₂O", ChemicalFormula.toDisplay("Na3H(CO3)2·2H2O"));
        assertEquals("Na₂SO₄·10H₂O", ChemicalFormula.toDisplay("Na2SO4·10H2O"));
    }

    @Test
    void runsColourElementsByGroup() {
        int grey = ElementColors.OTHER;
        int white = ElementColors.HYDROGEN;
        int red = ElementColors.ofGroup(16);
        assertEquals(List.of(new ChemicalFormula.Run("Ca", false, ElementColors.ofGroup(2)), new ChemicalFormula.Run("SO", false, red),
                new ChemicalFormula.Run("4", true, grey), new ChemicalFormula.Run("·2", false, grey), new ChemicalFormula.Run("H", false, white),
                new ChemicalFormula.Run("2", true, grey), new ChemicalFormula.Run("O", false, red)),
            ChemicalFormula.runs("CaSO4·2H2O"));
        assertEquals(List.of(new ChemicalFormula.Run("(", false, grey), new ChemicalFormula.Run("C", false, ElementColors.ofGroup(14)),
                new ChemicalFormula.Run("6", true, grey), new ChemicalFormula.Run("H", false, white), new ChemicalFormula.Run("7", true, grey),
                new ChemicalFormula.Run("Na", false, ElementColors.ofGroup(1)), new ChemicalFormula.Run("O", false, red),
                new ChemicalFormula.Run("6", true, grey), new ChemicalFormula.Run(")", false, grey), new ChemicalFormula.Run("n", true, grey)),
            ChemicalFormula.runs("(C6H7NaO6)n"));
        assertEquals(List.of(new ChemicalFormula.Run("Ni", false, ElementColors.ofGroup(10))), ChemicalFormula.runs("Ni"));
        assertEquals("₁₀", new ChemicalFormula.Run("10", true, grey).display());
    }

    @Test
    void groupsAndColours() {
        assertEquals(1, ElementColors.group("Na"));
        assertEquals(17, ElementColors.group("Cl"));
        assertEquals(8, ElementColors.group("Fe"));
        assertEquals(3, ElementColors.group("La"));
        assertEquals(0, ElementColors.group("Xx"));
        assertEquals(ElementColors.OTHER, ElementColors.of("Xx"));
        java.util.Set<Integer> colours = new java.util.HashSet<>();
        colours.add(ElementColors.HYDROGEN);
        colours.add(ElementColors.OTHER);
        for (int group = 1; group <= 18; group++) {
            colours.add(ElementColors.ofGroup(group));
        }
        assertEquals(20, colours.size(), "every group, hydrogen and the rest have their own colour");
    }

    /** Every capital letter in the material table's formulas starts a known element symbol. */
    @Test
    void materialFormulasUseKnownElements() throws java.io.IOException {
        String json;
        try (java.io.InputStream in = ChemicalFormulaTest.class.getResourceAsStream("/foodindustrial/materials.json")) {
            assertNotNull(in, "materials.json on the test class path");
            json = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
        java.util.regex.Matcher formula = java.util.regex.Pattern.compile("\"formula\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        int count = 0;
        while (formula.find()) {
            count++;
            for (ChemicalFormula.Run run : ChemicalFormula.runs(formula.group(1))) {
                for (int i = 0; i < run.ascii().length(); i++) {
                    if (Character.isUpperCase(run.ascii().charAt(i))) {
                        assertNotEquals(ElementColors.OTHER, run.color(), "unknown element in " + formula.group(1));
                    }
                }
            }
        }
        assertTrue(count > 300, "formulas found: " + count);
    }
}
