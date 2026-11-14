package com.tonywww.foodindustrial.food.metabolism;

import com.tonywww.foodindustrial.core.text.Lang;
import net.minecraft.network.chat.MutableComponent;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Player-facing text of the metabolism (foods/02 §8, text standard §3.3): names, tiers, amounts. */
public final class MetabolismText {
    static final String KEY = "metabolism.foodindustrial.";

    private MetabolismText() {
    }

    static void bootstrap() {
        Lang.add(KEY + "caffeine", "咖啡因", "Caffeine");
        Lang.add(KEY + "alcohol", "酒精", "Alcohol");
        Lang.add(KEY + "fast_sugar", "快吸收糖", "Fast sugar");
        Lang.add(KEY + "impurity", "杂质", "Impurity");
        Lang.add(KEY + "vitamin_c", "维生素 C", "Vitamin C");
        Lang.add(KEY + "vitamin_b1", "维生素 B1", "Vitamin B1");
        Lang.add(KEY + "vitamin_d", "维生素 D", "Vitamin D");
        Lang.add(KEY + "normal", "正常", "Normal");
        Lang.add(KEY + "caffeine.tier1", "警觉", "Alert");
        Lang.add(KEY + "caffeine.tier2", "难以入睡", "Sleepless");
        Lang.add(KEY + "caffeine.tier3", "耐力", "Endurance");
        Lang.add(KEY + "caffeine.tier4", "过量", "Excess");
        Lang.add(KEY + "alcohol.tier1", "微醺", "Tipsy");
        Lang.add(KEY + "alcohol.tier2", "醉酒", "Drunk");
        Lang.add(KEY + "alcohol.tier3", "呕吐", "Vomiting");
        Lang.add(KEY + "alcohol.tier4", "失去意识", "Unconscious");
        Lang.add(KEY + "alcohol.tier5", "酒精中毒", "Alcohol poisoning");
        Lang.add(KEY + "fast_sugar.tier1", "耐力", "Endurance");
        Lang.add(KEY + "impurity.tier1", "疲劳", "Fatigue");
        Lang.add(KEY + "impurity.tier2", "乏力", "Weakness");
        Lang.add(KEY + "impurity.tier3", "贫血", "Anemia");
        Lang.add(KEY + "vitamin_c.tier1", "缺乏", "Deficient");
        Lang.add(KEY + "vitamin_c.tier2", "坏血病", "Scurvy");
        Lang.add(KEY + "vitamin_b1.tier1", "缺乏", "Deficient");
        Lang.add(KEY + "vitamin_b1.tier2", "脚气病", "Beriberi");
        Lang.add(KEY + "vitamin_d.tier1", "缺乏", "Deficient");
        Lang.add(KEY + "vitamin_d.tier2", "骨软化", "Osteomalacia");
        Lang.add(KEY + "panel", "体内", "Body");
        // metabolic rate attribute (2026-11-13 user request)
        Lang.add("attribute.foodindustrial." + MetabolismRules.RATE_ATTRIBUTE, "代谢速率", "Metabolic Rate");
        Lang.add("attribute.foodindustrial." + MetabolismRules.RATE_ATTRIBUTE + ".desc", "体内成分消退、营养素消耗的快慢",
            "How fast substances leave the body and nutrient reserves are used");
        Lang.add(KEY + "rate", "代谢速率 ×%s", "Metabolic rate ×%s");
        Lang.add(KEY + "change", "%s：%s", "%s: %s");
        Lang.add(KEY + "reserve", "储备 %s 天", "%s days in reserve");
        Lang.add(KEY + "no_sleep", "咖啡因过多，无法入睡", "Too much caffeine to sleep");
        // hover card of the body panel (I35 UI, 2026-11-13)
        Lang.add(KEY + "no_effect", "暂无效果", "No effects");
        Lang.add(KEY + "drops_in", "%s 后降为 %s", "%2$s in %1$s");
        Lang.add(KEY + "normal_in", "%s 后恢复正常", "Normal in %s");
        Lang.add(KEY + "drops_in_days", "约 %s 天后降为 %s", "%2$s in about %1$s days");
        Lang.add(KEY + "normal_in_days", "约 %s 天后恢复正常", "Normal in about %s days");
        Lang.add(KEY + "runs_out", "%s 天后储备耗尽", "Runs out in %s days");
        Lang.add(KEY + "deficient_day", "缺乏第 %s 天，%s 天后转为 %s", "Day %s of deficiency; %3$s in %2$s days");
        Lang.add(KEY + "deficient_for", "已缺乏 %s 天，进食后恢复", "Deficient for %s days; eat to recover");
        Lang.add(KEY + "empty", "储备耗尽", "Reserve empty");
        Lang.add(KEY + "deficient_days", "缺乏 %s 天", "%s days deficient");
        Lang.add(KEY + "need", "每日需要 %s，储备上限 %s 天", "Daily need %s; reserve up to %s days");
        Lang.add(KEY + "stat_percent", "%s%% %s", "%s%% %s");
        Lang.add(KEY + "stat_add", "%s %s", "%s %s");
        Lang.add(KEY + "stat.attack_damage", "攻击伤害", "Attack damage");
        Lang.add(KEY + "stat.attack_speed", "攻击速度", "Attack speed");
        Lang.add(KEY + "stat.movement_speed", "移动速度", "Movement speed");
        Lang.add(KEY + "stat.max_health", "最大生命值", "Max health");
        Lang.add(KEY + "stat.mining_speed", "挖掘速度", "Mining speed");
        Lang.add(KEY + "stat.jump", "跳跃高度", "Jump height");
        Lang.add(KEY + "stat.healing", "受到的治疗", "Healing received");
        Lang.add(KEY + "stat.fall_damage", "摔落伤害", "Fall damage");
        Lang.add(KEY + "stat.exhaustion", "饥饿消耗", "Exhaustion");
        Lang.add(KEY + "effect.sleepless", "无法入睡", "Can't sleep");
        Lang.add(KEY + "effect.sway", "视野摇晃", "Swaying view");
        Lang.add(KEY + "effect.vomit", "每 %s 秒呕吐一次", "Vomiting every %s s");
        Lang.add(KEY + "effect.blackout", "画面变黑", "Blacked-out screen");
        Lang.add(KEY + "effect.helpless", "不能行动", "Can't act");
        Lang.add(KEY + "effect.unconscious", "画面变黑，不能行动", "Blacked-out screen, can't act");
        Lang.add(KEY + "effect.poison", "每 %s 秒受到 %s 点伤害", "%2$s damage every %1$s s");
        Lang.add(KEY + "effect.cancelled", "警觉、耐力取消", "Alert and endurance end");
        Lang.add(KEY + "effect.cap", "与咖啡因合计不超过 %s%%", "At most %s%% with caffeine");
        // food tooltip notes (text standard §3.3)
        Lang.add(KEY + "daily_share", "日需 %s%%", "%s%% of daily need");
        Lang.add(KEY + "blood_alcohol", "≈ %s%%", "≈ %s%%");
        Lang.add("death.attack.foodindustrial.alcohol_poisoning", "%1$s 死于酒精中毒", "%1$s died of alcohol poisoning");
    }

    public static MutableComponent name(Substance substance) {
        return net.minecraft.network.chat.Component.translatable(substance.langKey());
    }

    public static MutableComponent tier(Substance substance, int tier) {
        return net.minecraft.network.chat.Component.translatable(substance.tierKey(tier));
    }

    /** Action bar line on a change of tier: “咖啡因：警觉”. */
    static MutableComponent change(Substance substance, int tier) {
        return net.minecraft.network.chat.Component.translatable(KEY + "change", name(substance), tier(substance, tier));
    }

    /** What the panel shows of an amount, as a number: equal numbers look the same. */
    static long displayed(Substance substance, double value) {
        return switch (substance) {
            case ALCOHOL -> Math.round(value * 1000);
            case IMPURITY -> Math.round(value * 10);
            case VITAMIN_C, VITAMIN_B1, VITAMIN_D -> value > 0 && value < 1 ? -1 : (long) Math.floor(value);
            default -> Math.round(value);
        };
    }

    /** The amount as the body panel shows it: “95 mg”, “0.033%”, “12 g”, “4.5”, “储备 12 天”. */
    public static MutableComponent amount(Substance substance, double value) {
        return switch (substance) {
            case CAFFEINE -> net.minecraft.network.chat.Component.literal(Math.round(value) + " mg");
            case ALCOHOL -> net.minecraft.network.chat.Component.literal(fixed(value, 3) + "%");
            case FAST_SUGAR -> net.minecraft.network.chat.Component.literal(Math.round(value) + " g");
            case IMPURITY -> net.minecraft.network.chat.Component.literal(fixed(value, 1));
            case VITAMIN_C, VITAMIN_B1, VITAMIN_D -> net.minecraft.network.chat.Component.translatable(KEY + "reserve", reserveDays(value));
        };
    }

    /** Whole days of a nutrient reserve as shown: “12”, “<1”, “0”. */
    public static String reserveDays(double value) {
        return value > 0 && value < 1 ? "<1" : Long.toString((long) Math.floor(value));
    }

    /** A real-time span for a countdown: “0:53”, “1:02:07” (like vanilla effect durations). */
    public static String clock(long ticks) {
        long seconds = Math.max(1, (ticks + 19) / 20);
        long hours = seconds / 3600;
        String rest = String.format(java.util.Locale.ROOT, "%02d", seconds % 60);
        return hours > 0
            ? hours + ":" + String.format(java.util.Locale.ROOT, "%02d", seconds / 60 % 60) + ":" + rest
            : seconds / 60 + ":" + rest;
    }

    /** A signed number for an effect line: “+10”, “-4”, “+2.5”. */
    public static String signed(double value) {
        String digits = BigDecimal.valueOf(Math.abs(value)).setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        return (value < 0 ? "-" : "+") + digits;
    }

    /** An amount per serving: whole numbers from 100, one decimal from 1, two below; “<0.01” for traces. */
    public static String serving(double value) {
        if (value < 0.01) {
            return "<0.01";
        }
        int decimals = value >= 100 ? 0 : value >= 1 ? 1 : 2;
        return BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private static String fixed(double value, int decimals) {
        return BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP).toPlainString();
    }
}
