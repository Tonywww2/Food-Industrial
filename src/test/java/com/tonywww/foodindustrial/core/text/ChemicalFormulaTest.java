package com.tonywww.foodindustrial.core.text;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void partsSplitSubscripts() {
        assertEquals(List.of(new ChemicalFormula.Part("CaSO", false), new ChemicalFormula.Part("4", true),
            new ChemicalFormula.Part("·2H", false), new ChemicalFormula.Part("2", true), new ChemicalFormula.Part("O", false)),
            ChemicalFormula.parts("CaSO4·2H2O"));
        assertEquals(List.of(new ChemicalFormula.Part("(C", false), new ChemicalFormula.Part("6", true),
            new ChemicalFormula.Part("H", false), new ChemicalFormula.Part("7", true), new ChemicalFormula.Part("NaO", false),
            new ChemicalFormula.Part("6", true), new ChemicalFormula.Part(")", false), new ChemicalFormula.Part("n", true)),
            ChemicalFormula.parts("(C6H7NaO6)n"));
        assertEquals(List.of(new ChemicalFormula.Part("Ni", false)), ChemicalFormula.parts("Ni"));
    }
}
