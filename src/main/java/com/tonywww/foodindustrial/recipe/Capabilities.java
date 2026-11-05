package com.tonywww.foodindustrial.recipe;

import com.tonywww.foodindustrial.core.text.ConditionVocabulary;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A set of condition keys ({@code category.value} of the condition vocabulary, text standard §5):
 * what a recipe requires or what a device offers (machines/01 §3). A device can run a recipe when its
 * capabilities contain every required key. The set is stored as a bit mask, so the comparison is a
 * few word operations; the vocabulary is open, new words get new bits.
 */
public final class Capabilities {
    public static final Capabilities NONE = new Capabilities(new TreeSet<>(), new long[0]);

    private static final Map<String, Integer> INDEX = new HashMap<>();

    private final Set<String> keys;
    private final long[] mask;

    private Capabilities(TreeSet<String> keys, long[] mask) {
        this.keys = Collections.unmodifiableSet(keys);
        this.mask = mask;
    }

    /**
     * Capabilities from vocabulary keys.
     *
     * @throws IllegalArgumentException for keys outside the vocabulary or of display-only categories
     */
    public static Capabilities of(String... keys) {
        return of(Arrays.asList(keys));
    }

    /** @see #of(String...) */
    public static Capabilities of(Collection<String> keys) {
        TreeSet<String> sorted = new TreeSet<>();
        for (String key : keys) {
            check(key);
            sorted.add(key);
        }
        return build(sorted);
    }

    /** Throws unless {@code key} is a condition word of the vocabulary. */
    static void check(String key) {
        int dot = key.indexOf('.');
        String category = dot < 0 ? key : key.substring(0, dot);
        String value = dot < 0 ? "" : key.substring(dot + 1);
        if (!ConditionVocabulary.isConditionCategory(category)) {
            throw new IllegalArgumentException("Unknown condition " + category);
        }
        if (ConditionVocabulary.find(category, value).isEmpty()) {
            List<String> known = ConditionVocabulary.category(category).stream().map(ConditionVocabulary.Word::value).toList();
            throw new IllegalArgumentException("Unknown " + category + " condition " + value + " " + known);
        }
    }

    private static Capabilities build(TreeSet<String> keys) {
        if (keys.isEmpty()) {
            return NONE;
        }
        int highest = 0;
        int[] bits = new int[keys.size()];
        int i = 0;
        for (String key : keys) {
            bits[i] = bit(key);
            highest = Math.max(highest, bits[i]);
            i++;
        }
        long[] mask = new long[highest / 64 + 1];
        for (int bit : bits) {
            mask[bit >>> 6] |= 1L << (bit & 63);
        }
        return new Capabilities(keys, mask);
    }

    private static synchronized int bit(String key) {
        Integer bit = INDEX.get(key);
        if (bit == null) {
            bit = INDEX.size();
            INDEX.put(key, bit);
        }
        return bit;
    }

    /** Union of both sets. */
    public Capabilities with(Capabilities other) {
        if (other.keys.isEmpty()) {
            return this;
        }
        if (keys.isEmpty()) {
            return other;
        }
        TreeSet<String> union = new TreeSet<>(keys);
        union.addAll(other.keys);
        return build(union);
    }

    /** Whether every key of {@code required} is in this set. */
    public boolean covers(Capabilities required) {
        long[] need = required.mask;
        for (int i = 0; i < need.length; i++) {
            long have = i < mask.length ? mask[i] : 0L;
            if ((need[i] & ~have) != 0L) {
                return false;
            }
        }
        return true;
    }

    /** Keys of {@code required} missing from this set, for messages. */
    public Set<String> missing(Capabilities required) {
        TreeSet<String> missing = new TreeSet<>(required.keys);
        missing.removeAll(keys);
        return missing;
    }

    public Set<String> keys() {
        return keys;
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Capabilities capabilities && keys.equals(capabilities.keys);
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    @Override
    public String toString() {
        return keys.toString();
    }
}
