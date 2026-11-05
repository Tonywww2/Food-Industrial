package com.tonywww.foodindustrial.api.purity;

import net.minecraft.ChatFormatting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PurityGradeTest {
    @Test
    void namesAndColours() {
        // text standard §3.1
        assertSame(ChatFormatting.RED, PurityGrade.POOR.color());
        assertSame(ChatFormatting.GOLD, PurityGrade.CRUDE.color());
        assertSame(ChatFormatting.AQUA, PurityGrade.REFINED.color());
        assertSame(ChatFormatting.GREEN, PurityGrade.FOOD.color());
        assertSame(ChatFormatting.LIGHT_PURPLE, PurityGrade.LAB.color());
        assertEquals("purity.foodindustrial.refined", PurityGrade.REFINED.translationKey());
        assertArrayEquals(new PurityGrade[]{PurityGrade.CRUDE, PurityGrade.REFINED, PurityGrade.FOOD, PurityGrade.LAB}, PurityGrade.BOUNDED);
    }

    @Test
    void byId() {
        for (PurityGrade grade : PurityGrade.values()) {
            assertSame(grade, PurityGrade.byId(grade.id()));
        }
        assertSame(PurityGrade.FOOD, PurityGrade.byId(" Food "));
        assertNull(PurityGrade.byId("pure"));
    }
}
