package com.tonywww.foodindustrial.food.metabolism.client;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.food.metabolism.Metabolism;
import com.tonywww.foodindustrial.food.metabolism.MetabolismEffects;
import com.tonywww.foodindustrial.food.metabolism.MetabolismRules;
import com.tonywww.foodindustrial.food.metabolism.MetabolismText;
import com.tonywww.foodindustrial.food.metabolism.MetabolismView;
import com.tonywww.foodindustrial.food.metabolism.MetabolismView.Unconscious;
import com.tonywww.foodindustrial.food.metabolism.Substance;
import com.tonywww.foodindustrial.food.nutrition.Nutrition;
import com.tonywww.foodindustrial.food.nutrition.NutritionData;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.client.GuiBar;
import com.tonywww.foodindustrial.platform.client.PlatformClient;
import com.tonywww.foodindustrial.platform.client.PlatformHud;
import com.tonywww.foodindustrial.platform.client.TooltipRows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.tonywww.foodindustrial.food.metabolism.MetabolismRules.*;

/**
 * Client side of the metabolism (foods/02 §8, I35 UI chosen on 2026-11-13): the nutrition part of food tooltips,
 * the body panel beside the survival inventory with its hover cards, camera sway when drunk and the black screen
 * when unconscious.
 */
public final class MetabolismClient {
    private static final String KEY = "metabolism.foodindustrial.";
    /** Camera sway when drunk: roll amplitude (degrees) and period (ticks). */
    private static final float SWAY_ROLL = 3.0F;
    private static final float SWAY_ROLL_HEAVY = 5.0F;
    private static final float SWAY_PERIOD = 80.0F;
    private static final int BLACKOUT = 0xF5000000;
    /** Text colours of tooltips (vanilla §9, §c, §7, §8, §f; tier states §7, §a, §6, §c). */
    private static final int POSITIVE = 0x5555FF;
    private static final int NEGATIVE = 0xFF5555;
    private static final int GRAY = 0xAAAAAA;
    private static final int DARK = 0x555555;
    private static final int WHITE = 0xFFFFFF;
    private static final int[] TIER_TEXT = {0xAAAAAA, 0x55FF55, 0xFFAA00, 0xFF5555};
    /** Bar colours on the dark panel (ARGB), by state: normal, good, mild, severe. */
    private static final int[] BAR_FILL = {0xFF9AA0A6, 0xFF5BD16E, 0xFFF0A030, 0xFFFF5A4F};
    private static final int[] BAR_ZONE = {0, 0x475BD16E, 0x47F0A030, 0x47FF5A4F};
    private static final int TRACK = 0xFF24262C;
    private static final int EDGE = 0x1AFFFFFF;
    private static final int TICK = 0x59FFFFFF;
    private static final int STRIPE = 0x40FF5A4F;
    /** The panel's thin bars (mock-up F): fill by state, a faint track, tier bounds as notches in the pill's colour. */
    private static final int[] SLIM_FILL = {0xFFA3A9B0, 0xFF62D67A, 0xFFF2A63A, 0xFFFF5D52};
    private static final int SLIM_TRACK = 0x1CFFFFFF;
    private static final int SLIM_NOTCH = 0xFF121318;
    private static final float[] NO_ZONES = new float[0];
    private static final int[] NO_ZONE_COLORS = new int[0];
    private static final int NORMAL = 0;
    private static final int GOOD = 1;
    private static final int MILD = 2;
    private static final int SEVERE = 3;
    /** Bar scales of the acute substances (the right end of the bar). */
    private static final double[] SCALE = {500, 0.45, 60, 100};
    /** Hover card bar size. */
    private static final int CARD_BAR = 120;
    /** Food tooltip: the compact row wraps at this width. */
    private static final int CHIPS_WIDTH = 120;
    /** Text colours of the nutrition components (text standard §3.3), by component order. */
    private static final int[] COMPONENT_COLOR = {0xC9A27E, 0x7FD3E6, 0xF5B3D0, 0xFF7A33, 0xA8D26E, 0xFFF07A};
    /** Text colour of impurity in food tooltips (purple grey, like its icon). */
    private static final int IMPURITY_COLOR = 0xB4A7C6;

    private static final float[][] ZONES = new float[Substance.all().length][];
    private static final int[][] ZONE_COLORS = new int[Substance.all().length][];
    private static final float[][] TICKS = new float[Substance.all().length][];

    /** The view last seen and the client game time it arrived at, for the countdowns. */
    private static MetabolismView seen;
    private static long seenAt;
    /** Substances of the panel's rows, in order. */
    private static final List<Substance> SHOWN = new ArrayList<>();

    static {
        for (Substance substance : Substance.all()) {
            if (substance.nutrient()) {
                continue;
            }
            double scale = SCALE[substance.ordinal()];
            int tiers = substance.tiers();
            TICKS[substance.ordinal()] = new float[tiers];
            List<float[]> zones = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();
            for (int tier = 1; tier <= tiers; tier++) {
                float from = (float) Math.min(1, bound(substance, tier) / scale);
                float to = tier < tiers ? (float) Math.min(1, bound(substance, tier + 1) / scale) : 1.0F;
                TICKS[substance.ordinal()][tier - 1] = from;
                int state = state(substance, tier);
                // neighbouring tiers of the same state share one zone
                if (!colors.isEmpty() && colors.get(colors.size() - 1) == BAR_ZONE[state]) {
                    zones.get(zones.size() - 1)[1] = to;
                } else {
                    zones.add(new float[] {from, to});
                    colors.add(BAR_ZONE[state]);
                }
            }
            ZONES[substance.ordinal()] = new float[zones.size() * 2];
            ZONE_COLORS[substance.ordinal()] = new int[zones.size()];
            for (int i = 0; i < zones.size(); i++) {
                ZONES[substance.ordinal()][2 * i] = zones.get(i)[0];
                ZONES[substance.ordinal()][2 * i + 1] = zones.get(i)[1];
                ZONE_COLORS[substance.ordinal()][i] = colors.get(i);
            }
        }
    }

    private MetabolismClient() {
    }

    public static void bootstrap() {
        PlatformClient.onTooltipRows(MetabolismClient::foodTooltip);
        Metabolism.onClientView(view -> Minecraft.getInstance().execute(() -> {
            seen = view;
            seenAt = now();
            applyLocally(view);
        }));
        PlatformHud.showModifierIcons(id -> {
            Substance substance = MetabolismEffects.substanceOf(id);
            return substance == null ? null : substance.icon();
        });
        PlatformHud.onCameraAngles((angles, partialTick) -> {
            MetabolismView view = Metabolism.client();
            int tier = view.tier(Substance.ALCOHOL);
            Player player = Minecraft.getInstance().player;
            if (tier < 2 || player == null) {
                return;
            }
            double time = (player.tickCount + partialTick) * (2 * Math.PI / SWAY_PERIOD);
            float amplitude = tier >= 3 ? SWAY_ROLL_HEAVY : SWAY_ROLL;
            angles.roll += (float) (Math.sin(time) * amplitude);
            angles.yaw += (float) (Math.sin(time * 0.5) * amplitude * 0.5);
        });
        PlatformHud.onScreenTint(() -> Metabolism.client().applies(Unconscious.SCREEN) ? BLACKOUT : 0);
        PlatformHud.registerInventoryPanel(new PlatformHud.InventoryPanel() {
            @Override
            public boolean available() {
                return Minecraft.getInstance().player != null;
            }

            @Override
            public List<PlatformHud.Row> rows() {
                return MetabolismClient.rows(Metabolism.client());
            }

            @Override
            @Nullable
            public TooltipRows tooltip(int index) {
                return index < SHOWN.size() ? card(SHOWN.get(index), Metabolism.client(), Platform.isShiftDown()) : null;
            }

            @Override
            public String buttonIcon() {
                return FoodIndustrial.MOD_ID + ":textures/gui/metabolism/button.png";
            }

            @Override
            public Component buttonTooltip() {
                return Component.translatable(KEY + "panel");
            }
        });
    }

    /** Applies the stats that 1.20.1 computes on the client too (mining, jumping) to the local player. */
    private static void applyLocally(MetabolismView view) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Metabolism.applyClient(player, view);
        }
    }

    private static long now() {
        var level = Minecraft.getInstance().level;
        return level == null ? 0 : level.getGameTime();
    }

    /** Client ticks since {@code view} arrived. */
    private static long elapsed(MetabolismView view) {
        return view == seen ? Math.max(0, now() - seenAt) : 0;
    }

    /** Ticks of the body's time since {@code view} arrived (world time × metabolic rate). */
    private static long bodyElapsed(MetabolismView view) {
        return Math.round(elapsed(view) * view.rate());
    }

    /** World ticks a stretch of the body's time takes at the view's metabolic rate, or −1 when it never passes (rate 0). */
    private static long worldTicks(long bodyTicks, MetabolismView view) {
        if (bodyTicks < 0 || view.rate() <= 0) {
            return -1;
        }
        return (long) Math.ceil(bodyTicks / view.rate());
    }

    /** Seconds between the hourly alcohol effects at the view's metabolic rate. */
    private static long hourlySeconds(MetabolismView view) {
        return view.rate() > 0 ? Math.max(1, Math.round(HOUR / 20.0 / view.rate())) : HOUR / 20;
    }

    // ---------------------------------------------------------------- panel

    /** The state a tier is drawn in: 0 normal, 1 good, 2 mild, 3 severe. */
    private static int state(Substance substance, int tier) {
        if (tier == 0) {
            return NORMAL;
        }
        if (substance == Substance.FAST_SUGAR || substance == Substance.CAFFEINE && tier <= 3) {
            return GOOD;
        }
        return tier == 1 ? MILD : SEVERE;
    }

    private static List<PlatformHud.Row> rows(MetabolismView view) {
        SHOWN.clear();
        List<PlatformHud.Row> rows = new ArrayList<>(Substance.all().length);
        for (Substance substance : Substance.all()) {
            if (view.shown(substance)) {
                SHOWN.add(substance);
                rows.add(new PlatformHud.Row(substance.icon(), slimBar(substance, view)));
            }
        }
        return rows;
    }

    /** The panel's thin bar: the amount against its scale with notches at the tier bounds, or a nutrient's reserve or deficiency. */
    private static GuiBar slimBar(Substance substance, MetabolismView view) {
        int tier = view.tier(substance);
        int state = state(substance, tier);
        if (substance.nutrient()) {
            double cap = RESERVE_CAP[substance.nutrientIndex()];
            double reserve = view.amount(substance);
            if (reserve > 0) {
                return GuiBar.plain((float) (reserve / cap), SLIM_FILL[NORMAL], SLIM_TRACK, 0, 0);
            }
            float progress = tier >= 2 ? 1.0F : (float) Math.max(0, view.deficient(substance) + bodyElapsed(view)) / (float) (cap * DAY);
            return GuiBar.plain(progress, SLIM_FILL[state], SLIM_TRACK, 0, 0);
        }
        int index = substance.ordinal();
        float fill = (float) Math.min(1, view.amount(substance) / SCALE[index]);
        return new GuiBar(fill, SLIM_FILL[state], NO_ZONES, NO_ZONE_COLORS, TICKS[index], SLIM_TRACK, 0, SLIM_NOTCH, 0);
    }

    /** The hover card's bar: amount against its scale with tier zones, or a nutrient's reserve or deficiency. */
    private static GuiBar bar(Substance substance, MetabolismView view) {
        int tier = view.tier(substance);
        int state = state(substance, tier);
        if (substance.nutrient()) {
            double cap = RESERVE_CAP[substance.nutrientIndex()];
            double reserve = view.amount(substance);
            if (reserve > 0) {
                return GuiBar.plain((float) (reserve / cap), BAR_FILL[NORMAL], TRACK, EDGE, 0);
            }
            long deficient = view.deficient(substance);
            float progress = tier >= 2 ? 1.0F : (float) Math.max(0, deficient + bodyElapsed(view)) / (float) (cap * DAY);
            return GuiBar.plain(progress, BAR_FILL[state], TRACK, EDGE, STRIPE);
        }
        int index = substance.ordinal();
        float fill = (float) Math.min(1, view.amount(substance) / SCALE[index]);
        return new GuiBar(fill, BAR_FILL[state], ZONES[index], ZONE_COLORS[index], TICKS[index], TRACK, EDGE, TICK, 0);
    }

    // ---------------------------------------------------------------- hover card

    /** The hover card of a panel row: compact by default, every tier with Shift. */
    private static TooltipRows card(Substance substance, MetabolismView view, boolean full) {
        int tier = view.tier(substance);
        TooltipRows.Builder card = TooltipRows.builder();
        MutableComponent title = MetabolismText.name(substance).copy().withStyle(style -> style.withColor(WHITE))
            .append(" ").append(MetabolismText.tier(substance, tier).withStyle(style -> style.withColor(TIER_TEXT[state(substance, tier)])));
        // a nutrient's reserve is shown in days of world time at the player's rate
        double amount = substance.nutrient() && view.rate() > 0 ? view.amount(substance) / view.rate() : view.amount(substance);
        card.row().icon(substance.icon(), 16).gap(4)
            .lines(List.of(title, colored(MetabolismText.amount(substance, amount), GRAY)), 0);
        if (full) {
            fullCard(card, substance, view, tier);
        } else {
            List<Component> effects = effects(substance, tier, view);
            if (effects.isEmpty()) {
                card.row(2).text(colored(Component.translatable(KEY + "no_effect"), DARK));
            }
            for (int i = 0; i < effects.size(); i++) {
                card.row(i == 0 ? 2 : 0).text(effects.get(i));
            }
        }
        Component next = countdown(substance, view, tier);
        if (next != null) {
            card.row(2).text(colored(next, GRAY));
        }
        if (view.rate() != 1.0) {
            card.row(next != null ? 0 : 2).text(colored(Component.translatable(KEY + "rate", rate(view.rate())), DARK));
        }
        return card.build();
    }

    private static void fullCard(TooltipRows.Builder card, Substance substance, MetabolismView view, int tier) {
        Font font = Minecraft.getInstance().font;
        card.row(2).bar(bar(substance, view), CARD_BAR, 4);
        Component marker = Component.literal("▶").withStyle(style -> style.withColor(WHITE));
        int markerWidth = font.width(marker);
        int atWidth = 0;
        int nameWidth = 0;
        for (int i = 1; i <= substance.tiers(); i++) {
            atWidth = Math.max(atWidth, font.width(threshold(substance, i)));
            nameWidth = Math.max(nameWidth, font.width(MetabolismText.tier(substance, i)));
        }
        for (int i = 1; i <= substance.tiers(); i++) {
            boolean reached = i <= tier;
            List<Component> deltas = deltas(substance, i, view);
            if (deltas.isEmpty()) {
                deltas = List.of(Component.empty());
            }
            for (int k = 0; k < deltas.size(); k++) {
                TooltipRows.Builder row = k == 0 && i == 1 ? card.row(3) : card.row();
                if (k == 0) {
                    row.text(i == tier ? marker : Component.empty(), markerWidth)
                        .text(colored(threshold(substance, i), reached ? GRAY : DARK), atWidth)
                        .text(colored(MetabolismText.tier(substance, i), reached ? TIER_TEXT[state(substance, i)] : DARK), nameWidth);
                } else {
                    row.text(Component.empty(), markerWidth + 2 + atWidth + 2 + nameWidth);
                }
                row.text(reached ? deltas.get(k) : colored(deltas.get(k).copy(), DARK));
            }
        }
        if (substance.nutrient()) {
            int index = substance.nutrientIndex();
            String need = MetabolismText.serving(DAILY_NEED[index]) + " " + substance.component().unit();
            card.row(2).text(colored(Component.translatable(KEY + "need", need, number(RESERVE_CAP[index], 0)), DARK));
        }
    }

    /** The amount a tier starts at: “75 mg”, “>0.20%”, “30 g”, “10”, “储备耗尽”, “缺乏 30 天”. */
    private static Component threshold(Substance substance, int tier) {
        double bound = bound(substance, tier);
        boolean strict = substance == Substance.CAFFEINE && tier == 4 || substance == Substance.ALCOHOL && (tier == 3 || tier == 4);
        String prefix = strict ? ">" : "";
        return switch (substance) {
            case CAFFEINE -> Component.literal(prefix + number(bound, 0) + " mg");
            case ALCOHOL -> Component.literal(prefix + number(bound, 2) + "%");
            case FAST_SUGAR -> Component.literal(number(bound, 0) + " g");
            case IMPURITY -> Component.literal(number(bound, 0));
            case VITAMIN_C, VITAMIN_B1, VITAMIN_D -> tier == 1 ? Component.translatable(KEY + "empty")
                : Component.translatable(KEY + "deficient_days", number(RESERVE_CAP[substance.nutrientIndex()], 0));
        };
    }

    /** The effects in force at {@code tier}. */
    private static List<Component> effects(Substance substance, int tier, MetabolismView view) {
        List<Component> out = new ArrayList<>();
        switch (substance) {
            case CAFFEINE -> {
                if (tier >= 1 && tier <= 3) {
                    out.add(percent("mining_speed", CAFFEINE_MINING, true));
                    out.add(percent("attack_speed", CAFFEINE_ATTACK_SPEED, true));
                    if (tier == 3) {
                        out.add(percent("exhaustion", -CAFFEINE_EXHAUSTION, false));
                    }
                } else if (tier == 4) {
                    out.add(percent("attack_damage", CAFFEINE_EXCESS_ATTACK_DAMAGE, true));
                }
                if (tier >= 2) {
                    out.add(bad(Component.translatable(KEY + "effect.sleepless")));
                }
            }
            case ALCOHOL -> {
                if (tier == 1) {
                    out.add(percent("attack_speed", ALCOHOL_TIPSY_ATTACK_SPEED, true));
                }
                for (int i = 2; i <= tier; i++) {
                    out.addAll(deltas(substance, i, view));
                }
            }
            case FAST_SUGAR -> {
                if (tier >= 1) {
                    out.add(percent("exhaustion", -SUGAR_EXHAUSTION, false));
                }
            }
            case VITAMIN_B1 -> {
                if (tier >= 1) {
                    out.add(percent("movement_speed", tier >= 2 ? VITAMIN_B1_MOVEMENT_SEVERE : VITAMIN_B1_MOVEMENT, true));
                    out.add(percent("jump", VITAMIN_B1_JUMP, true));
                }
            }
            default -> {
                for (int i = 1; i <= tier; i++) {
                    out.addAll(deltas(substance, i, view));
                }
            }
        }
        return out;
    }

    /** What reaching {@code tier} adds or changes (the Shift card's ladder). */
    private static List<Component> deltas(Substance substance, int tier, MetabolismView view) {
        List<Component> out = new ArrayList<>(2);
        switch (substance) {
            case CAFFEINE -> {
                switch (tier) {
                    case 1 -> {
                        out.add(percent("mining_speed", CAFFEINE_MINING, true));
                        out.add(percent("attack_speed", CAFFEINE_ATTACK_SPEED, true));
                    }
                    case 2 -> out.add(bad(Component.translatable(KEY + "effect.sleepless")));
                    case 3 -> out.add(percent("exhaustion", -CAFFEINE_EXHAUSTION, false));
                    default -> {
                        out.add(percent("attack_damage", CAFFEINE_EXCESS_ATTACK_DAMAGE, true));
                        out.add(colored(Component.translatable(KEY + "effect.cancelled"), GRAY));
                    }
                }
            }
            case ALCOHOL -> {
                switch (tier) {
                    case 1 -> out.add(percent("attack_speed", ALCOHOL_TIPSY_ATTACK_SPEED, true));
                    case 2 -> {
                        out.add(percent("attack_speed", ALCOHOL_DRUNK_ATTACK_SPEED, true));
                        out.add(percent("movement_speed", ALCOHOL_DRUNK_MOVEMENT, true));
                        out.add(bad(Component.translatable(KEY + "effect.sway")));
                    }
                    case 3 -> out.add(bad(Component.translatable(KEY + "effect.vomit", hourlySeconds(view))));
                    case 4 -> {
                        Component line = unconsciousLine(view);
                        if (line != null) {
                            out.add(line);
                        }
                    }
                    default -> out.add(bad(Component.translatable(KEY + "effect.poison", hourlySeconds(view), MetabolismText.serving(POISON_DAMAGE))));
                }
            }
            case FAST_SUGAR -> {
                out.add(percent("exhaustion", -SUGAR_EXHAUSTION, false));
                out.add(colored(Component.translatable(KEY + "effect.cap", MetabolismText.signed(-EXHAUSTION_CAP * 100)), GRAY));
            }
            case IMPURITY -> {
                switch (tier) {
                    case 1 -> {
                        out.add(percent("attack_damage", IMPURITY_ATTACK_DAMAGE, true));
                        out.add(percent("mining_speed", IMPURITY_MINING, true));
                    }
                    case 2 -> out.add(percent("movement_speed", IMPURITY_MOVEMENT, true));
                    default -> out.add(add("max_health", IMPURITY_MAX_HEALTH));
                }
            }
            case VITAMIN_C -> {
                if (tier == 1) {
                    out.add(percent("attack_damage", VITAMIN_C_ATTACK_DAMAGE, true));
                    out.add(percent("healing", VITAMIN_C_HEALING, true));
                } else {
                    out.add(add("max_health", VITAMIN_C_MAX_HEALTH));
                }
            }
            case VITAMIN_B1 -> {
                if (tier == 1) {
                    out.add(percent("movement_speed", VITAMIN_B1_MOVEMENT, true));
                    out.add(percent("jump", VITAMIN_B1_JUMP, true));
                } else {
                    out.add(percent("movement_speed", VITAMIN_B1_MOVEMENT_SEVERE, true));
                }
            }
            case VITAMIN_D -> out.add(tier == 1 ? percent("fall_damage", VITAMIN_D_FALL_DAMAGE, false)
                : percent("attack_damage", VITAMIN_D_ATTACK_DAMAGE, true));
        }
        return out;
    }

    /** What unconsciousness does under the server's configuration, or {@code null} for nothing shown. */
    @Nullable
    private static Component unconsciousLine(MetabolismView view) {
        boolean screen = view.rule(Unconscious.SCREEN);
        boolean helpless = view.rule(Unconscious.MOVEMENT) || view.rule(Unconscious.ATTACK) || view.rule(Unconscious.BLOCKS)
            || view.rule(Unconscious.USE_ITEMS) || view.rule(Unconscious.CONTAINERS);
        if (screen && helpless) {
            return bad(Component.translatable(KEY + "effect.unconscious"));
        }
        if (screen || helpless) {
            return bad(Component.translatable(KEY + (screen ? "effect.blackout" : "effect.helpless")));
        }
        return null;
    }

    /**
     * When the tier next changes: real time for caffeine, alcohol and fast sugar, game days for the rest; all in
     * world time at the player's metabolic rate (nothing when the rate is 0).
     */
    @Nullable
    private static Component countdown(Substance substance, MetabolismView view, int tier) {
        if (view.rate() <= 0) {
            return null;
        }
        long next = view.next(substance);
        long left = next < 0 ? -1 : worldTicks(Math.max(0, next - bodyElapsed(view)), view);
        if (substance.nutrient()) {
            long deficient = view.deficient(substance) < 0 ? -1 : worldTicks(view.deficient(substance) + bodyElapsed(view), view);
            if (tier == 0) {
                return left < 0 ? null : Component.translatable(KEY + "runs_out", MetabolismText.reserveDays(view.amount(substance) / view.rate()));
            }
            if (tier == 1) {
                long day = Math.max(1, ceilDiv(Math.max(0, deficient), DAY));
                long rest = Math.max(1, ceilDiv(Math.max(0, left), DAY));
                return Component.translatable(KEY + "deficient_day", day, rest, MetabolismText.tier(substance, 2));
            }
            return Component.translatable(KEY + "deficient_for", Math.max(0, deficient) / DAY);
        }
        if (tier == 0 || left < 0) {
            return null;
        }
        boolean days = substance == Substance.IMPURITY;
        String span = days ? Long.toString(Math.max(1, Math.round((double) left / DAY))) : MetabolismText.clock(left);
        if (tier == 1) {
            return Component.translatable(KEY + (days ? "normal_in_days" : "normal_in"), span);
        }
        return Component.translatable(KEY + (days ? "drops_in_days" : "drops_in"), span, MetabolismText.tier(substance, tier - 1));
    }

    private static long ceilDiv(long value, long divisor) {
        return (value + divisor - 1) / divisor;
    }

    /** An effect line like vanilla attribute lines: “+10% 挖掘速度”, blue when it helps and red when it hurts. */
    private static Component percent(String stat, double fraction, boolean higherIsBetter) {
        Component line = Component.translatable(KEY + "stat_percent", MetabolismText.signed(fraction * 100), Component.translatable(KEY + "stat." + stat));
        return colored(line, fraction > 0 == higherIsBetter ? POSITIVE : NEGATIVE);
    }

    /** An effect line with a plain amount: “-4 最大生命值”. */
    private static Component add(String stat, double amount) {
        Component line = Component.translatable(KEY + "stat_add", MetabolismText.signed(amount), Component.translatable(KEY + "stat." + stat));
        return colored(line, amount > 0 ? POSITIVE : NEGATIVE);
    }

    private static Component bad(Component line) {
        return colored(line, NEGATIVE);
    }

    private static MutableComponent colored(Component text, int rgb) {
        return text.copy().withStyle(style -> style.withColor(rgb));
    }

    private static String number(double value, int decimals) {
        return BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP).toPlainString();
    }

    /** A metabolic rate as written after “×”: up to two decimals, no trailing zeros (0.5, 2, 1.25). */
    private static String rate(double value) {
        BigDecimal rounded = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return rounded.scale() < 0 ? rounded.setScale(0).toPlainString() : rounded.toPlainString();
    }

    // ---------------------------------------------------------------- food tooltip

    /**
     * The nutrition part of a food's tooltip (text standard §3.3): a row of icons and amounts, or with Shift a table
     * of names, amounts and notes; {@code null} without data or with every component at 0.
     */
    @Nullable
    private static TooltipRows foodTooltip(ItemStack stack) {
        NutritionData data = Nutrition.client(stack);
        if (data == null) {
            return null;
        }
        List<Substance> parts = new ArrayList<>(7);
        for (Substance substance : Substance.all()) {
            if (substance == Substance.IMPURITY ? data.impurity() > 0 : data.amount(substance.component()) > 0) {
                parts.add(substance);
            }
        }
        if (parts.isEmpty()) {
            return null;
        }
        TooltipRows.Builder rows = TooltipRows.builder();
        if (!Platform.isShiftDown()) {
            rows.row().wrap(CHIPS_WIDTH);
            for (Substance substance : parts) {
                rows.group().icon(substance.icon(), 8).text(colored(Component.literal(amount(data, substance)), color(substance))).gap(6);
            }
            return rows.build();
        }
        Font font = Minecraft.getInstance().font;
        int nameWidth = 0;
        int valueWidth = 0;
        for (Substance substance : parts) {
            nameWidth = Math.max(nameWidth, font.width(MetabolismText.name(substance)));
            valueWidth = Math.max(valueWidth, font.width(amount(data, substance)));
        }
        for (Substance substance : parts) {
            rows.row().icon(substance.icon(), 8).gap(3)
                .text(colored(MetabolismText.name(substance), color(substance)), nameWidth).gap(3)
                .text(colored(Component.literal(amount(data, substance)), WHITE), valueWidth).gap(3);
            Component note = note(data, substance);
            if (note != null) {
                rows.text(colored(note, GRAY));
            }
        }
        return rows.build();
    }

    private static int color(Substance substance) {
        return substance == Substance.IMPURITY ? IMPURITY_COLOR : COMPONENT_COLOR[substance.component().ordinal()];
    }

    /** “95 mg” per serving; impurity has no unit (“5”). */
    private static String amount(NutritionData data, Substance substance) {
        if (substance == Substance.IMPURITY) {
            return MetabolismText.serving(data.impurity());
        }
        return MetabolismText.serving(data.amount(substance.component())) + " " + substance.component().unit();
    }

    /** “日需 75%” for nutrients, “≈ 0.033%” (blood alcohol of one serving) for alcohol. */
    @Nullable
    private static Component note(NutritionData data, Substance substance) {
        if (substance == Substance.IMPURITY) {
            return null;
        }
        double value = data.amount(substance.component());
        if (substance.nutrient()) {
            double share = value / DAILY_NEED[substance.nutrientIndex()] * 100;
            return Component.translatable(KEY + "daily_share", share < 1 ? "<1" : Long.toString(Math.round(share)));
        }
        if (substance == Substance.ALCOHOL) {
            double percent = MetabolismRules.bloodAlcohol(value);
            return Component.translatable(KEY + "blood_alcohol", percent < 0.001 ? "<0.001" : number(percent, 3));
        }
        return null;
    }
}
