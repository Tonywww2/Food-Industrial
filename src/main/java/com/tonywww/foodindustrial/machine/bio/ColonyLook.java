package com.tonywww.foodindustrial.machine.bio;

import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Appearance of a colony on an M30 plate (I17, 2026-11-11): every item has a fixed colour and pattern
 * of colour patches, the same on every plate, so a player who knows them can pick before identifying.
 * Cultures of the material table get distinct looks (by a hash of the id, the next free look on a clash,
 * in id order); other items (wild colonies, test items) take the look of their hash.
 * A look is {@code colour + pattern * COLORS.length}.
 */
public final class ColonyLook {
    /** Colony colours (ARGB). */
    public static final int[] COLORS = {
        0xFFF2E6C8, 0xFFFAFAF4, 0xFFF3E27A, 0xFFE8C030, 0xFFE08A30, 0xFFC83C32, 0xFFE890A8, 0xFF8A5A34,
        0xFFC8A070, 0xFF6AA84F, 0xFF3E7A3A, 0xFF7C8CA8, 0xFF9A9A9A, 0xFF3A3A3A, 0xFF8A5AA8, 0xFF4AA8A0};
    /** Patterns: solid, centre dot, ring, speckled, wrinkled, rayed, fuzzy edge, concentric. */
    public static final int PATTERNS = 8;
    private static final int LOOKS = COLORS.length * PATTERNS;

    private static volatile Map<Item, Integer> cultures;

    private ColonyLook() {
    }

    /** Look of colonies of {@code item}. */
    public static int of(Item item) {
        Integer look = cultures().get(item);
        return look != null ? look : hash(PlatformTags.itemId(item));
    }

    public static int color(int look) {
        return COLORS[Math.floorMod(look, COLORS.length)];
    }

    public static int pattern(int look) {
        return Math.floorMod(look / COLORS.length, PATTERNS);
    }

    private static int hash(String id) {
        return Math.floorMod(id.hashCode() * 31 + id.length(), LOOKS);
    }

    private static Map<Item, Integer> cultures() {
        Map<Item, Integer> map = cultures;
        if (map == null) {
            List<String> ids = new ArrayList<>();
            Map<String, Item> items = new HashMap<>();
            for (ModMaterials.Entry entry : ModMaterials.all()) {
                if (entry.def().category().equals("culture") && entry.item() != null) {
                    Item item = entry.item().get();
                    String id = PlatformTags.itemId(item);
                    ids.add(id);
                    items.put(id, item);
                }
            }
            ids.sort(null);
            boolean[] taken = new boolean[LOOKS];
            map = new HashMap<>();
            for (String id : ids) {
                int look = hash(id);
                for (int tries = 0; taken[look] && tries < LOOKS; tries++) {
                    look = (look + 1) % LOOKS;
                }
                taken[look] = true;
                map.put(items.get(id), look);
            }
            cultures = map;
        }
        return map;
    }
}
