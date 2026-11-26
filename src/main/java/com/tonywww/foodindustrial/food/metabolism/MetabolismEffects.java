package com.tonywww.foodindustrial.food.metabolism;

import com.tonywww.foodindustrial.platform.PlatformAttributes;
import com.tonywww.foodindustrial.platform.PlatformAttributes.Mode;
import com.tonywww.foodindustrial.platform.PlatformAttributes.Stat;
import net.minecraft.world.entity.player.Player;

import static com.tonywww.foodindustrial.food.metabolism.MetabolismRules.*;

/**
 * Attribute changes per substance and tier (foods/02 §4–5, §7): one modifier per substance and attribute, ids
 * {@code metabolism/<substance>/<attribute>}; percentages of one attribute add up. Applied on the server for all
 * stats and on the client for the stats a node applies in events.
 */
public final class MetabolismEffects {
    private static final Stat[] STATS = Stat.values();
    /** Ids by substance ordinal and stat ordinal. */
    private static final String[][] IDS = new String[Substance.all().length][STATS.length];
    /** Ids of the movement and jump lock while unconscious. */
    static final String UNCONSCIOUS_MOVEMENT = "metabolism/unconscious/movement_speed";
    static final String UNCONSCIOUS_JUMP = "metabolism/unconscious/jump_strength";

    static {
        for (Substance substance : Substance.all()) {
            for (Stat stat : STATS) {
                IDS[substance.ordinal()][stat.ordinal()] = "metabolism/" + substance.id() + "/" + stat.name().toLowerCase(java.util.Locale.ROOT);
            }
        }
    }

    private MetabolismEffects() {
    }

    /** Declares every modifier id. Call during mod construction. */
    static void declare() {
        for (String[] ids : IDS) {
            for (String id : ids) {
                PlatformAttributes.declare(id);
            }
        }
        PlatformAttributes.declare(UNCONSCIOUS_MOVEMENT);
        PlatformAttributes.declare(UNCONSCIOUS_JUMP);
    }

    /**
     * The substance a modifier id belongs to (the lock while unconscious belongs to alcohol, which nitrite shares), or
     * {@code null}.
     */
    public static Substance substanceOf(String id) {
        String[] parts = id.split("/");
        if (parts.length != 3 || !parts[0].equals("metabolism")) {
            return null;
        }
        return parts[1].equals("unconscious") ? Substance.ALCOHOL : Substance.byId(parts[1]);
    }

    /** Sets the modifiers of {@code substance} for {@code tier} (0 removes them). */
    static void apply(Player player, Substance substance, int tier) {
        double attackDamage = 0;
        double attackSpeed = 0;
        double movement = 0;
        double maxHealth = 0;
        double mining = 0;
        double jump = 0;
        double fall = 0;
        double healing = 0;
        switch (substance) {
            case CAFFEINE -> {
                if (tier >= 1 && tier <= 3) {
                    mining = CAFFEINE_MINING;
                    attackSpeed = CAFFEINE_ATTACK_SPEED;
                } else if (tier == 4) {
                    attackDamage = CAFFEINE_EXCESS_ATTACK_DAMAGE;
                }
            }
            case ALCOHOL -> {
                if (tier == 1) {
                    attackSpeed = ALCOHOL_TIPSY_ATTACK_SPEED;
                } else if (tier >= 2) {
                    attackSpeed = ALCOHOL_DRUNK_ATTACK_SPEED;
                    movement = ALCOHOL_DRUNK_MOVEMENT;
                }
            }
            case FAST_SUGAR -> {
                // exhaustion only (not an attribute)
            }
            case NITRITE -> {
                if (tier >= 1) {
                    attackDamage = NITRITE_MILD_ATTACK_DAMAGE;
                    movement = tier >= 2 ? NITRITE_MODERATE_MOVEMENT : NITRITE_MILD_MOVEMENT;
                }
                if (tier >= 2) {
                    attackSpeed = NITRITE_MODERATE_ATTACK_SPEED;
                }
            }
            case IMPURITY -> {
                if (tier >= 1) {
                    attackDamage = IMPURITY_ATTACK_DAMAGE;
                    mining = IMPURITY_MINING;
                }
                if (tier >= 2) {
                    movement = IMPURITY_MOVEMENT;
                }
                if (tier >= 3) {
                    maxHealth = IMPURITY_MAX_HEALTH;
                }
            }
            case VITAMIN_C -> {
                if (tier >= 1) {
                    attackDamage = VITAMIN_C_ATTACK_DAMAGE;
                    healing = VITAMIN_C_HEALING;
                }
                if (tier >= 2) {
                    maxHealth = VITAMIN_C_MAX_HEALTH;
                }
            }
            case VITAMIN_B1 -> {
                if (tier >= 1) {
                    movement = tier >= 2 ? VITAMIN_B1_MOVEMENT_SEVERE : VITAMIN_B1_MOVEMENT;
                    jump = VITAMIN_B1_JUMP;
                }
            }
            case VITAMIN_D -> {
                if (tier >= 1) {
                    fall = VITAMIN_D_FALL_DAMAGE;
                }
                if (tier >= 2) {
                    attackDamage = VITAMIN_D_ATTACK_DAMAGE;
                }
            }
            // tiers 1–2 deficiency, 3–4 excess (foods/02 §5.6); vitamin A's darkened view is drawn by the client
            case VITAMIN_A -> {
                if (tier == 2) {
                    maxHealth = VITAMIN_A_MAX_HEALTH;
                } else if (tier >= 3) {
                    mining = VITAMIN_A_EXCESS_MINING;
                    maxHealth = tier >= 4 ? VITAMIN_A_EXCESS_MAX_HEALTH : 0;
                }
            }
            case CALCIUM -> {
                if (tier == 1 || tier == 2) {
                    fall = CALCIUM_FALL_DAMAGE;
                    maxHealth = tier == 2 ? CALCIUM_MAX_HEALTH : 0;
                } else if (tier >= 3) {
                    movement = CALCIUM_EXCESS_MOVEMENT;
                }
            }
            case IRON -> {
                if (tier == 1 || tier == 2) {
                    maxHealth = IRON_MAX_HEALTH;
                    attackDamage = IRON_ATTACK_DAMAGE;
                    movement = tier == 2 ? IRON_MOVEMENT : 0;
                } else if (tier >= 4) {
                    // damage taken comes from damageTaken()
                    maxHealth = IRON_EXCESS_MAX_HEALTH;
                }
            }
            case ZINC -> {
                if (tier == 1 || tier == 2) {
                    healing = ZINC_HEALING;
                    attackDamage = tier == 2 ? ZINC_ATTACK_DAMAGE : 0;
                } else if (tier >= 3) {
                    maxHealth = ZINC_EXCESS_MAX_HEALTH;
                }
            }
        }
        String[] ids = IDS[substance.ordinal()];
        PlatformAttributes.set(player, Stat.ATTACK_DAMAGE, ids[Stat.ATTACK_DAMAGE.ordinal()], Mode.MULTIPLY_BASE, attackDamage);
        PlatformAttributes.set(player, Stat.ATTACK_SPEED, ids[Stat.ATTACK_SPEED.ordinal()], Mode.MULTIPLY_BASE, attackSpeed);
        PlatformAttributes.set(player, Stat.MOVEMENT_SPEED, ids[Stat.MOVEMENT_SPEED.ordinal()], Mode.MULTIPLY_BASE, movement);
        PlatformAttributes.set(player, Stat.MAX_HEALTH, ids[Stat.MAX_HEALTH.ordinal()], Mode.ADD, maxHealth);
        PlatformAttributes.set(player, Stat.MINING_SPEED, ids[Stat.MINING_SPEED.ordinal()], Mode.MULTIPLY_BASE, mining);
        PlatformAttributes.set(player, Stat.JUMP_STRENGTH, ids[Stat.JUMP_STRENGTH.ordinal()], Mode.MULTIPLY_BASE, jump);
        PlatformAttributes.set(player, Stat.FALL_DAMAGE, ids[Stat.FALL_DAMAGE.ordinal()], Mode.MULTIPLY_BASE, fall);
        PlatformAttributes.set(player, Stat.HEALING, ids[Stat.HEALING.ordinal()], Mode.MULTIPLY_BASE, healing);
    }

    /** Extra share of damage taken at the applied tiers (by substance ordinal): iron excess +10 % (foods/02 §5.6.2). */
    static double damageTaken(int[] tiers) {
        return tiers[Substance.IRON.ordinal()] >= 3 ? IRON_EXCESS_DAMAGE_TAKEN : 0;
    }

    /** Whether the applied tier darkens the view: nitrite from tier 2, vitamin A deficiency (night blindness). */
    public static boolean darkens(Substance substance, int tier) {
        return substance == Substance.NITRITE ? tier >= 2 : substance == Substance.VITAMIN_A && (tier == 1 || tier == 2);
    }

    /** Locks or frees walking and jumping (unconscious, when the configuration says so). */
    static void lockMovement(Player player, boolean locked) {
        PlatformAttributes.set(player, Stat.MOVEMENT_SPEED, UNCONSCIOUS_MOVEMENT, Mode.MULTIPLY_TOTAL, locked ? -1.0 : 0.0);
        PlatformAttributes.set(player, Stat.JUMP_STRENGTH, UNCONSCIOUS_JUMP, Mode.MULTIPLY_TOTAL, locked ? -1.0 : 0.0);
    }
}
