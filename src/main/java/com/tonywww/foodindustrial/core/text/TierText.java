package com.tonywww.foodindustrial.core.text;

import com.tonywww.foodindustrial.core.Tier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tiers in player text: written bare as {@code T2}, never with a label, and always in the tier's colour
 * ({@link Tier#color()}; docs/text-standard.md §3.4, 2026-11-11).
 */
public final class TierText {
    private static final Pattern TOKEN = Pattern.compile("\\bT([0-4])\\b");

    private TierText() {
    }

    /** {@code T2} in its colour. */
    public static MutableComponent tier(Tier tier) {
        TextColor color = TextColor.fromRgb(tier.color());
        return Component.literal(tier.name()).withStyle(style -> style.withColor(color));
    }

    /** {@code text} in {@code base} with every {@code T0}–{@code T4} in its tier colour. */
    public static MutableComponent colored(String text, ChatFormatting base) {
        MutableComponent line = Component.empty();
        Matcher matcher = TOKEN.matcher(text);
        int last = 0;
        while (matcher.find()) {
            if (matcher.start() > last) {
                line.append(Component.literal(text.substring(last, matcher.start())).withStyle(base));
            }
            line.append(tier(Tier.values()[matcher.group(1).charAt(0) - '0']));
            last = matcher.end();
        }
        if (last < text.length()) {
            line.append(Component.literal(text.substring(last)).withStyle(base));
        }
        return line;
    }

    /** Whether {@code text} names a tier. */
    public static boolean namesTier(String text) {
        return TOKEN.matcher(text).find();
    }
}
