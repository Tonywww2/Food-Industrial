package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.FoodIndustrial;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;
//? if <1.21 {
import net.minecraft.world.entity.ai.attributes.Attribute;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
//?} else {
/*import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
*///?}
//? if forge {
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
*///?}

/**
 * Temporary attribute changes of players by id (I35, foods/02 §7): each id is one modifier, not saved — add them
 * again after login, respawn and changing dimension. Stats that a node has no attribute for are applied in
 * events instead: on 1.20.1 mining speed, jump strength and fall damage; on every node the healing received.
 * With Apothic Attributes installed, mining speed (1.20.1) and healing received use its attributes, so they
 * show in its attribute panel and are applied once.
 * <p>
 * Call {@link #set} on the server for every stat. On a client, calls only record the stats applied in events
 * (1.20.1 mining speed and jump strength are computed client side too); attributes reach clients through the
 * game's own sync.
 * <p>
 * The mod's own player attributes ({@link #registerPlayer}) are registered as {@code foodindustrial:<name>}, given to
 * every player and synced to clients; other mods, commands and equipment change them with ordinary modifiers.
 */
public final class PlatformAttributes {
    private static final Set<String> DECLARED = new LinkedHashSet<>();
    private static final Map<Player, Mods> SERVER = new WeakHashMap<>();
    private static final Map<Player, Mods> CLIENT = new WeakHashMap<>();
    private static final List<PlayerAttribute> PLAYER_ATTRIBUTES = new ArrayList<>();
    //? if <1.21 {
    private static final Map<UUID, String> BY_UUID = new HashMap<>();
    //?}

    private PlatformAttributes() {
    }

    /** What a modifier changes. */
    public enum Stat {
        ATTACK_DAMAGE, ATTACK_SPEED, MOVEMENT_SPEED, MAX_HEALTH, MINING_SPEED, JUMP_STRENGTH, FALL_DAMAGE, HEALING
    }

    /** How a modifier's amount applies, as vanilla's operations. */
    public enum Mode {
        /** Adds the amount (points). */
        ADD,
        /** Adds amount × base; amounts of one stat add up (−0.1 and −0.15 make −25%). */
        MULTIPLY_BASE,
        /** Multiplies the total by 1 + amount. */
        MULTIPLY_TOTAL
    }

    /**
     * Declares a modifier id (such as {@code metabolism/caffeine/attack_speed}), so modifiers can be told apart
     * on clients (Apothic Attributes' panel). Call during mod construction for every id passed to {@link #set}.
     */
    public static void declare(String id) {
        DECLARED.add(id);
        //? if <1.21 {
        BY_UUID.put(uuid(id), id);
        //?}
    }

    /** Declared ids, in declaration order. */
    public static Set<String> declared() {
        return DECLARED;
    }

    /**
     * Sets the modifier {@code id} of {@code stat} to {@code amount}; 0 removes it. A lowered maximum health
     * also lowers the current health to it.
     */
    public static void set(Player player, Stat stat, String id, Mode mode, double amount) {
        boolean client = player.level().isClientSide();
        Object attribute = attribute(stat);
        if (attribute == null) {
            Mods mods = mods(player, client);
            mods.put(stat, id, mode, amount);
            return;
        }
        if (client) {
            return;
        }
        AttributeInstance instance = instance(player, attribute);
        if (instance == null) {
            return;
        }
        apply(instance, id, mode, amount);
        if (stat == Stat.MAX_HEALTH && player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    /** The declared id of a modifier of this mod, or {@code null} (for integrations on this side). */
    @Nullable
    public static String idOf(AttributeModifier modifier) {
        //? if <1.21 {
        return BY_UUID.get(modifier.getId());
        //?} else {
        /*var id = modifier.id();
        if (!id.getNamespace().equals(FoodIndustrial.MOD_ID)) {
            return null;
        }
        return DECLARED.contains(id.getPath()) ? id.getPath() : null;
        *///?}
    }

    /** A player attribute of this mod, from {@link #registerPlayer}. */
    public static final class PlayerAttribute {
        private final Supplier<Attribute> entry;
        private final double defaultValue;

        private PlayerAttribute(Supplier<Attribute> entry, double defaultValue) {
            this.entry = entry;
            this.defaultValue = defaultValue;
        }
    }

    /**
     * Registers the attribute {@code foodindustrial:<name>} with a default and a range, gives it to every player and
     * syncs it to clients; its name is the language key {@code attribute.foodindustrial.<name>} (Apothic Attributes
     * shows {@code ….desc} under it). Call during mod construction.
     */
    public static PlayerAttribute registerPlayer(String name, double defaultValue, double min, double max) {
        String key = "attribute." + FoodIndustrial.MOD_ID + "." + name;
        Supplier<Attribute> entry = PlatformRegistry.register(Registries.ATTRIBUTE, name, () -> create(key, defaultValue, min, max));
        PlayerAttribute attribute = new PlayerAttribute(entry, defaultValue);
        PLAYER_ATTRIBUTES.add(attribute);
        return attribute;
    }

    /** The value of one of the mod's player attributes on {@code player} (its default if the player lacks it). */
    public static double value(Player player, PlayerAttribute attribute) {
        AttributeInstance instance = instance(player, holder(attribute));
        return instance == null ? attribute.defaultValue : instance.getValue();
    }

    private static Attribute create(String key, double defaultValue, double min, double max) {
        Attribute attribute = new RangedAttribute(key, defaultValue, min, max).setSyncable(true);
        //? if >=26.1 {
        /*attribute.setSentiment(Attribute.Sentiment.NEUTRAL);
        *///?}
        return attribute;
    }

    //? if <1.21 {
    private static Object holder(PlayerAttribute attribute) {
        return attribute.entry.get();
    }
    //?} else {
    /*@SuppressWarnings("unchecked")
    private static Object holder(PlayerAttribute attribute) {
        // a DeferredHolder: the registry entry's holder
        return (Holder<Attribute>) (Object) attribute.entry;
    }
    *///?}

    /** Whether {@code stat} is carried by an attribute on this node (and so shows in attribute views). */
    public static boolean usesAttribute(Stat stat) {
        return attribute(stat) != null;
    }

    /** The attribute instance of {@code stat} on {@code player}, or {@code null} (for integrations). */
    @Nullable
    public static AttributeInstance instance(Player player, Stat stat) {
        Object attribute = attribute(stat);
        return attribute == null ? null : instance(player, attribute);
    }

    private static Mods mods(Player player, boolean client) {
        Map<Player, Mods> map = client ? CLIENT : SERVER;
        synchronized (map) {
            return map.computeIfAbsent(player, key -> new Mods());
        }
    }

    /** Factor of an event-applied stat (1 when nothing changes it). Allocation-free. */
    static double factor(Player player, Stat stat) {
        Map<Player, Mods> map = player.level().isClientSide() ? CLIENT : SERVER;
        Mods mods;
        synchronized (map) {
            mods = map.get(player);
        }
        return mods == null ? 1.0 : mods.factors[stat.ordinal()];
    }

    /** Modifiers of the stats a node applies in events, with their combined factors. */
    private static final class Mods {
        private final EnumMap<Stat, Map<String, double[]>> entries = new EnumMap<>(Stat.class);
        private final double[] factors = new double[Stat.values().length];

        Mods() {
            java.util.Arrays.fill(factors, 1.0);
        }

        void put(Stat stat, String id, Mode mode, double amount) {
            Map<String, double[]> byId = entries.computeIfAbsent(stat, key -> new HashMap<>());
            if (amount == 0) {
                byId.remove(id);
            } else {
                byId.put(id, new double[] {mode.ordinal(), amount});
            }
            double base = 1.0;
            double total = 1.0;
            for (double[] entry : byId.values()) {
                if (entry[0] == Mode.MULTIPLY_BASE.ordinal()) {
                    base += entry[1];
                } else if (entry[0] == Mode.MULTIPLY_TOTAL.ordinal()) {
                    total *= 1.0 + entry[1];
                }
            }
            factors[stat.ordinal()] = Math.max(0.0, base) * Math.max(0.0, total);
        }
    }

    //? if <1.21 {
    private static UUID uuid(String id) {
        return UUID.nameUUIDFromBytes((FoodIndustrial.MOD_ID + ":" + id).getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    private static Object attribute(Stat stat) {
        return switch (stat) {
            case ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED;
            case MAX_HEALTH -> Attributes.MAX_HEALTH;
            case MINING_SPEED -> apothic("mining_speed");
            case HEALING -> apothic("healing_received");
            case JUMP_STRENGTH, FALL_DAMAGE -> null;
        };
    }

    @Nullable
    private static Attribute apothic(String name) {
        if (!Platform.isModLoaded("attributeslib")) {
            return null;
        }
        return BuiltInRegistries.ATTRIBUTE.get(PlatformIdentifier.id("attributeslib", name));
    }

    @Nullable
    private static AttributeInstance instance(Player player, Object attribute) {
        return player.getAttribute((Attribute) attribute);
    }

    private static void apply(AttributeInstance instance, String id, Mode mode, double amount) {
        UUID uuid = uuid(id);
        instance.removeModifier(uuid);
        if (amount != 0) {
            AttributeModifier.Operation operation = switch (mode) {
                case ADD -> AttributeModifier.Operation.ADDITION;
                case MULTIPLY_BASE -> AttributeModifier.Operation.MULTIPLY_BASE;
                case MULTIPLY_TOTAL -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            };
            instance.addTransientModifier(new AttributeModifier(uuid, FoodIndustrial.MOD_ID + ":" + id, amount, operation));
        }
    }
    //?} else {
    /*@Nullable
    private static Object attribute(Stat stat) {
        return switch (stat) {
            case ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED;
            case MAX_HEALTH -> Attributes.MAX_HEALTH;
            case MINING_SPEED -> Attributes.BLOCK_BREAK_SPEED;
            case JUMP_STRENGTH -> Attributes.JUMP_STRENGTH;
            case FALL_DAMAGE -> Attributes.FALL_DAMAGE_MULTIPLIER;
            case HEALING -> apothic("healing_received");
        };
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static AttributeInstance instance(Player player, Object attribute) {
        return player.getAttribute((Holder<Attribute>) attribute);
    }

    private static void apply(AttributeInstance instance, String id, Mode mode, double amount) {
        var key = PlatformIdentifier.id(FoodIndustrial.MOD_ID, id);
        if (amount == 0) {
            instance.removeModifier(key);
            return;
        }
        AttributeModifier.Operation operation = switch (mode) {
            case ADD -> AttributeModifier.Operation.ADD_VALUE;
            case MULTIPLY_BASE -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case MULTIPLY_TOTAL -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        };
        instance.addOrUpdateTransientModifier(new AttributeModifier(key, amount, operation));
    }
    *///?}

    //? if >=1.21 && <26.1 {
    /*@Nullable
    private static Holder<Attribute> apothic(String name) {
        if (!Platform.isModLoaded("apothic_attributes")) {
            return null;
        }
        return BuiltInRegistries.ATTRIBUTE.getHolder(PlatformIdentifier.id("apothic_attributes", name)).orElse(null);
    }
    *///?}

    //? if >=26.1 {
    /*@Nullable
    private static Holder<Attribute> apothic(String name) {
        if (!Platform.isModLoaded("apothic_attributes")) {
            return null;
        }
        return BuiltInRegistries.ATTRIBUTE.get(PlatformIdentifier.id("apothic_attributes", name)).orElse(null);
    }
    *///?}

    /** Called by the mod entrypoint: the mod's player attributes and the events of the stats without attributes. */
    @SuppressWarnings("unchecked")
    static void bind(IEventBus modBus) {
        modBus.addListener((EntityAttributeModificationEvent event) -> {
            for (PlayerAttribute attribute : PLAYER_ATTRIBUTES) {
                //? if <1.21 {
                event.add(EntityType.PLAYER, (Attribute) holder(attribute));
                //?} else {
                /*event.add(EntityType.PLAYER, (Holder<Attribute>) holder(attribute));
                *///?}
            }
        });
        //? if forge {
        MinecraftForge.EVENT_BUS.addListener((LivingHealEvent event) -> {
            if (event.getEntity() instanceof Player player && attribute(Stat.HEALING) == null) {
                event.setAmount((float) (event.getAmount() * factor(player, Stat.HEALING)));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.BreakSpeed event) -> {
            if (attribute(Stat.MINING_SPEED) == null) {
                event.setNewSpeed((float) (event.getNewSpeed() * factor(event.getEntity(), Stat.MINING_SPEED)));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((LivingEvent.LivingJumpEvent event) -> {
            if (event.getEntity() instanceof Player player) {
                double factor = factor(player, Stat.JUMP_STRENGTH);
                if (factor != 1.0) {
                    Vec3 motion = player.getDeltaMovement();
                    player.setDeltaMovement(motion.x, motion.y * factor, motion.z);
                }
            }
        });
        MinecraftForge.EVENT_BUS.addListener((LivingFallEvent event) -> {
            if (event.getEntity() instanceof Player player) {
                event.setDamageMultiplier((float) (event.getDamageMultiplier() * factor(player, Stat.FALL_DAMAGE)));
            }
        });
        //?} else {
        /*NeoForge.EVENT_BUS.addListener((LivingHealEvent event) -> {
            if (event.getEntity() instanceof Player player && attribute(Stat.HEALING) == null) {
                event.setAmount((float) (event.getAmount() * factor(player, Stat.HEALING)));
            }
        });
        *///?}
    }
}
