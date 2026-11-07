package com.tonywww.foodindustrial.core.text;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextChecksTest {
    private static List<String> zh(String key, String value) {
        List<String> problems = new ArrayList<>();
        TextChecks.checkZh(key, value, problems);
        return problems;
    }

    private static List<String> en(String key, String value) {
        List<String> problems = new ArrayList<>();
        TextChecks.checkEn(key, value, problems);
        return problems;
    }

    @Test
    void standardExamplesPass() {
        assertEquals(List.of(), zh("tooltip.foodindustrial.purity", "纯度 %s%%（%s）"));
        assertEquals(List.of(), zh("tooltip.foodindustrial.soda_ash.desc", "面食用碱，由氨碱法或海藻灰制得"));
        assertEquals(List.of(), zh("tooltip.foodindustrial.reactor.desc", "常压液相反应：中和、酸解、皂化、酯化"));
        assertEquals(List.of(), zh("item.foodindustrial.propylene_glycol", "1,2-丙二醇"));
        assertEquals(List.of(), zh("item.foodindustrial.msg", "谷氨酸钠（味精）"));
        assertEquals(List.of(), zh("gui.foodindustrial.status.missing", "条件不满足：%s"));
        assertEquals(List.of(), zh("jei.foodindustrial.range", "40%–73.5%"));
        assertEquals(List.of(), zh("tooltip.foodindustrial.x", "T2 机械化，1000 mB"));
        assertEquals(List.of(), en("item.foodindustrial.salt", "Sodium Chloride (Salt)"));
        assertEquals(List.of(), en("item.foodindustrial.msg", "Monosodium Glutamate (MSG)"));
        assertEquals(List.of(), en("item.foodindustrial.menthol", "l-Menthol"));
        assertEquals(List.of(), en("tooltip.foodindustrial.purity", "Purity %s%% (%s)"));
        assertEquals(List.of(), zh("item.foodindustrial.dimethylformamide", "N,N-二甲基甲酰胺"));
        assertEquals(List.of(), zh("item.foodindustrial.tartaric_acid", "L(+)-酒石酸"));
        assertEquals(List.of(), zh("item.foodindustrial.furaneol", "4-羟基-2,5-二甲基-3(2H)-呋喃酮"));
        assertEquals(List.of(), zh("item.foodindustrial.ethanol_bucket", "乙醇（酒精）桶"));
        assertEquals(List.of(), en("item.foodindustrial.plaster", "Calcium Sulfate Hemihydrate (Plaster of Paris)"));
    }

    @Test
    void violationsAreReported() {
        assertTrue(!zh("tooltip.foodindustrial.a.desc", "可以用来做豆腐").isEmpty(), "forbidden word");
        assertTrue(!zh("tooltip.foodindustrial.a.desc", "豆腐凝固剂！").isEmpty(), "exclamation");
        assertTrue(!zh("tooltip.foodindustrial.a.desc", "豆腐凝固剂。").isEmpty(), "period");
        assertTrue(!zh("tooltip.foodindustrial.a.desc", "一二三四五六七八九十一二三四五六七八九十一二三四五").isEmpty(), "25 Han characters");
        assertTrue(!zh("tooltip.foodindustrial.a", "T2机械化").isEmpty(), "missing space");
        assertTrue(!zh("tooltip.foodindustrial.a", "40%-73.5%").isEmpty(), "hyphen range");
        assertTrue(!zh("tooltip.foodindustrial.a", "纯度 60 %").isEmpty(), "space before %");
        assertTrue(!zh("tooltip.foodindustrial.a", "容量 1000mB").isEmpty(), "unit without space");
        assertTrue(!zh("tooltip.foodindustrial.a", "中和,酸解").isEmpty(), "half-width comma");
        assertTrue(!zh("tooltip.foodindustrial.a", "盐(NaCl)").isEmpty(), "half-width parenthesis after Han");
        assertTrue(zh("item.foodindustrial.a", "聚氧乙烯(20)山梨醇酐单硬脂酸酯（吐温 60）").isEmpty(), "GB 2760 degree of polymerisation");
        assertTrue(zh("item.foodindustrial.a", "3-(3-羟基-4-甲氧基苯基)丙醛").isEmpty(), "substituent group after a locant");
        assertTrue(!en("item.foodindustrial.salt", "Sodium chloride").isEmpty(), "name not capitalised");
        assertTrue(!en("tooltip.foodindustrial.a", "used in tofu.").isEmpty(), "lower case and period");
        assertTrue(!en("tooltip.foodindustrial.a", "A".repeat(61)).isEmpty(), "too long");
    }

    @Test
    void checkAllRejectsMismatchedKeys() {
        assertThrows(IllegalStateException.class, () -> TextChecks.checkAll(Map.of("a", "甲"), Map.of("b", "B"), Map.of()));
    }

    @Test
    void naming() {
        Map<String, String> common = Map.of("碳酸钠", "纯碱");
        assertEquals(List.of(), TextChecks.checkNaming(Map.of("item.foodindustrial.soda_ash", "碳酸钠（纯碱）"), common));
        assertEquals(1, TextChecks.checkNaming(Map.of("item.foodindustrial.soda_ash", "碳酸钠"), common).size());
        assertEquals(1, TextChecks.checkNaming(Map.of("item.foodindustrial.soda_ash", "纯碱"), common).size());
        assertEquals(List.of(), TextChecks.checkNaming(Map.of("item.foodindustrial.soda_ash_bucket", "碳酸钠（纯碱）桶"), common));
    }
}
