package com.tonywww.foodindustrial.food.nutrition;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Nutrition data files (foods/03 §2, I34). */
class NutritionFileTest {
    private static NutritionFile parse(String source, String json) {
        return NutritionFile.parse(source, JsonParser.parseString(json));
    }

    private static String file(String mod, int priority, String items, String perServing) {
        return "{\"mod\":\"" + mod + "\",\"priority\":" + priority + ",\"entries\":[{\"items\":[" + items
            + "],\"serving_g\":100,\"per_serving\":{" + perServing + "}}]}";
    }

    @Test
    void missingKeysCountAsZero() {
        NutritionFile file = parse("a:coffee", file("mod", 0, "\"mod:coffee\"", "\"caffeine_mg\":95"));
        NutritionFile.Entry entry = file.entries().get(0);
        assertEquals(95.0, entry.amount(Component.CAFFEINE));
        assertEquals(0.0, entry.amount(Component.VITAMIN_C));
        assertEquals(Component.ALL & ~Component.CAFFEINE.bit(), entry.missing());
        assertEquals(0, file.priority());
    }

    @Test
    void allKeysNamedLeaveNothingMissing() {
        StringBuilder keys = new StringBuilder();
        for (Component component : Component.all()) {
            keys.append(keys.length() == 0 ? "" : ",").append('"').append(component.key()).append("\":0");
        }
        assertEquals(0, parse("a:b", file("mod", 0, "\"mod:x\"", keys.toString())).entries().get(0).missing());
    }

    @Test
    void impurityIsOptionalAndWrittenOnlyWhenAboveZero() {
        NutritionFile plain = parse("a:b", file("mod", 0, "\"mod:x\"", "\"caffeine_mg\":0"));
        assertEquals(0.0, plain.entries().get(0).impurity());
        assertFalse(plain.toJson().getAsJsonArray("entries").get(0).getAsJsonObject().has("impurity"));
        NutritionFile flesh = parse("a:b",
            "{\"mod\":\"m\",\"entries\":[{\"items\":[\"m:x\"],\"serving_g\":50,\"per_serving\":{},\"impurity\":5}]}");
        assertEquals(5.0, flesh.entries().get(0).impurity());
        assertEquals(5, flesh.toJson().getAsJsonArray("entries").get(0).getAsJsonObject().get("impurity").getAsInt());
        assertThrows(IllegalArgumentException.class, () -> parse("a:b",
            "{\"mod\":\"m\",\"entries\":[{\"items\":[\"m:x\"],\"serving_g\":50,\"per_serving\":{},\"impurity\":-1}]}"));
    }

    @Test
    void malformedFilesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> parse("a:b", file("mod", 0, "\"mod:x\"", "\"taurine_mg\":1")));
        assertThrows(IllegalArgumentException.class, () -> parse("a:b", file("mod", 0, "\"mod:x\"", "\"caffeine_mg\":-1")));
        assertThrows(IllegalArgumentException.class, () -> parse("a:b",
            "{\"mod\":\"m\",\"entries\":[{\"items\":[\"m:x\"],\"serving_g\":0,\"per_serving\":{}}]}"));
        assertThrows(IllegalArgumentException.class, () -> parse("a:b",
            "{\"mod\":\"m\",\"entries\":[{\"items\":[\"m:x\"],\"serving_g\":-5,\"per_serving\":{}}]}"));
        assertThrows(IllegalArgumentException.class, () -> parse("a:b", "{\"entries\":[]}"));
        assertThrows(IllegalArgumentException.class, () -> parse("a:b",
            "{\"mod\":\"m\",\"priority\":\"high\",\"entries\":[]}"));
    }

    @Test
    void basisIsKeptAsWritten() {
        NutritionFile file = parse("a:b", "{\"mod\":\"m\",\"entries\":[{\"items\":[\"m:x\"],\"serving_g\":50,\"per_serving\":{},"
            + "\"basis\":{\"food\":\"apple\",\"sources\":[\"FDC 171688\"],\"confirmed\":\"2026-11-13\",\"extra\":[1,2]}}]}");
        assertEquals("apple", file.entries().get(0).basis().orElseThrow().getAsJsonObject().get("food").getAsString());
        assertTrue(file.entries().get(0).basis().orElseThrow().getAsJsonObject().has("extra"));
    }

    @Test
    void onlyOnFiltersInputsAndIsDroppedFromOutput() {
        NutritionFile input = NutritionFile.parseInput("minecraft.json", JsonParser.parseString(
            "{\"mod\":\"minecraft\",\"entries\":[{\"items\":[\"minecraft:ominous_bottle\"],\"serving_g\":10,\"per_serving\":{},"
                + "\"only_on\":[\"1.21.1\",\"26.1.2\"]},{\"items\":[\"minecraft:apple\"],\"serving_g\":182,\"per_serving\":{}}]}"));
        NutritionFile.Entry bottle = input.entries().get(0);
        NutritionFile.Entry apple = input.entries().get(1);
        assertFalse(bottle.appliesTo("1.20.1"));
        assertTrue(bottle.appliesTo("1.21.1"));
        assertTrue(apple.appliesTo("1.20.1"));
        assertFalse(input.toJson().toString().contains("only_on"));
        // A data pack file does not read only_on.
        NutritionFile pack = NutritionFile.parse("a:b", input.toJson());
        assertTrue(pack.entries().get(0).onlyOn().isEmpty());
        assertEquals(Component.ALL, NutritionFile.parse("a:b", JsonParser.parseString(
            "{\"mod\":\"m\",\"entries\":[{\"items\":[\"m:x\"],\"serving_g\":1,\"per_serving\":{}}]}")).entries().get(0).missing());
        assertEquals(0, pack.entries().get(0).missing());
    }

    @Test
    void unloadedModsAreSkippedAndPriorityWins() {
        List<NutritionFile> files = List.of(
            parse("pack:z_low", file("mod", 0, "\"mod:tea\"", "\"caffeine_mg\":10")),
            parse("pack:a_high", file("mod", 5, "\"mod:tea\"", "\"caffeine_mg\":40")),
            parse("pack:other", file("absent", 9, "\"mod:tea\"", "\"caffeine_mg\":99")));
        List<String> warnings = new ArrayList<>();
        Map<String, NutritionData> table = NutritionData.resolve(files, Set.of("mod")::contains, Optional::of, warnings::add);
        assertEquals(40.0, table.get("mod:tea").amount(Component.CAFFEINE));
        assertEquals("pack:a_high", table.get("mod:tea").source());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void equalPriorityTakesTheLastFileIdWithAWarning() {
        List<NutritionFile> files = List.of(
            parse("pack:b", file("mod", 0, "\"mod:tea\"", "\"caffeine_mg\":20")),
            parse("pack:a", file("mod", 0, "\"mod:tea\"", "\"caffeine_mg\":10")));
        List<String> warnings = new ArrayList<>();
        Map<String, NutritionData> table = NutritionData.resolve(files, mod -> true, Optional::of, warnings::add);
        assertEquals(20.0, table.get("mod:tea").amount(Component.CAFFEINE));
        assertEquals(1, warnings.size());
    }

    @Test
    void unknownItemsAreWarnedAboutAndIgnored() {
        List<NutritionFile> files = List.of(parse("pack:a", file("mod", 0, "\"mod:tea\",\"mod:gone\"", "\"caffeine_mg\":20")));
        List<String> warnings = new ArrayList<>();
        Map<String, NutritionData> table = NutritionData.resolve(files, mod -> true,
            id -> id.equals("mod:gone") ? Optional.empty() : Optional.of(id), warnings::add);
        assertEquals(Set.of("mod:tea"), table.keySet());
        assertEquals(1, warnings.size());
    }
}
