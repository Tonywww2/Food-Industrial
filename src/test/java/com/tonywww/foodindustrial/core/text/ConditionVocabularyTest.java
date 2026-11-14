package com.tonywww.foodindustrial.core.text;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionVocabularyTest {
    @Test
    void everyWordPassesTheTextChecks() {
        List<String> problems = new ArrayList<>();
        for (ConditionVocabulary.Word word : ConditionVocabulary.all().values()) {
            TextChecks.checkZh(word.translationKey(), word.zh(), problems);
            TextChecks.checkEn(word.translationKey(), word.en(), problems);
        }
        assertEquals(List.of(), problems);
    }

    @Test
    void tableOfTextStandard() {
        assertEquals(6, ConditionVocabulary.category("temperature").size());
        assertEquals(19, ConditionVocabulary.category("method").size());
        // machine modes (I14–I17): one word per mode of M01–M31, shared modes once, and M25's regeneration
        assertEquals(73, ConditionVocabulary.category("mode").size());
        assertEquals("晾晒结晶", ConditionVocabulary.find("mode", "sun_crystallization").orElseThrow().zh());
        assertTrue(ConditionVocabulary.isConditionCategory("mode"));
        assertEquals("极高温", ConditionVocabulary.find("temperature", "extreme").orElseThrow().zh());
        assertEquals("condition.foodindustrial.pressure.vacuum", ConditionVocabulary.find("pressure", "vacuum").orElseThrow().translationKey());
        assertTrue(ConditionVocabulary.find("method", "vacuum").isPresent());
        assertTrue(ConditionVocabulary.find("temperature", "warm").isEmpty());
        assertEquals(List.of("fixed", "refine", "keep"), ConditionVocabulary.category("manual_purity").stream().map(ConditionVocabulary.Word::value).toList());
    }
}
