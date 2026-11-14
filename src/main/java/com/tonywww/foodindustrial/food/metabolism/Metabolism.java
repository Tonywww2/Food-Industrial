package com.tonywww.foodindustrial.food.metabolism;

import com.tonywww.foodindustrial.food.ProductGrade;
import com.tonywww.foodindustrial.food.metabolism.MetabolismView.Unconscious;
import com.tonywww.foodindustrial.food.nutrition.Component;
import com.tonywww.foodindustrial.food.nutrition.Nutrition;
import com.tonywww.foodindustrial.food.nutrition.NutritionData;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformAttributes;
import com.tonywww.foodindustrial.platform.PlatformConfig;
import com.tonywww.foodindustrial.platform.PlatformPlayerData;
import com.tonywww.foodindustrial.platform.PlatformPlayerEvents;
import com.tonywww.foodindustrial.platform.PlatformPlayerEvents.Interaction;
import com.tonywww.foodindustrial.platform.PlatformPlayers;
import com.tonywww.foodindustrial.platform.network.PlatformNetwork;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static com.tonywww.foodindustrial.food.metabolism.MetabolismRules.*;

/**
 * The metabolism (foods/02, I35): what players eat and drink goes into the body and leaves it by real elimination
 * rules over world time; tiers change attributes and cause special effects. Settled every 20 ticks per player and
 * at once on eating, login, logout, respawn and configuration changes. The player attribute
 * {@code foodindustrial:metabolic_rate} (default 1, 0–100) scales how fast the body's time runs (2026-11-13).
 */
public final class Metabolism {
    /** Damage type of alcohol poisoning (bypasses armour). */
    public static final String ALCOHOL_POISONING = "foodindustrial:alcohol_poisoning";
    private static final PlatformPlayerData.Key<MetabolismState> DATA = PlatformPlayerData.register("metabolism", MetabolismState::new);
    private static final PlatformNetwork.Channel<MetabolismView> SYNC = PlatformNetwork.toClient("metabolism", MetabolismView::read, Metabolism::received);

    private static final String SECTION = "metabolism";
    private static BooleanSupplier clearOnDeath = () -> true;
    private static BooleanSupplier impurity = () -> true;
    private static BooleanSupplier nutrients = () -> true;
    private static final BooleanSupplier[] UNCONSCIOUS = new BooleanSupplier[Unconscious.values().length];
    @org.jetbrains.annotations.Nullable
    private static PlatformAttributes.PlayerAttribute rate;

    /** What the client was told (client side only). */
    private static volatile MetabolismView client = new MetabolismView();
    /** Client hook run when a view arrives (set by the client code). */
    private static Consumer<MetabolismView> clientListener = view -> {
    };

    private Metabolism() {
    }

    /** Declares configuration, text, data and hooks. Call from {@code FoodIndustrial.init()}. */
    public static void bootstrap() {
        clearOnDeath = PlatformConfig.commonBoolean(SECTION, "clear_on_death",
            "Clear caffeine, alcohol, fast sugar and impurity, and refill nutrient reserves, when a player dies", true);
        impurity = PlatformConfig.commonBoolean(SECTION, "impurity", "Impurity from homemade and poor products and its effects", true);
        nutrients = PlatformConfig.commonBoolean(SECTION, "nutrients", "Vitamin C, B1 and D reserves and their deficiency effects", true);
        UNCONSCIOUS[Unconscious.SCREEN.ordinal()] = PlatformConfig.commonBoolean(SECTION, "unconscious_screen",
            "Unconscious players see a black screen", true);
        UNCONSCIOUS[Unconscious.MOVEMENT.ordinal()] = PlatformConfig.commonBoolean(SECTION, "unconscious_movement",
            "Unconscious players cannot walk or jump", true);
        UNCONSCIOUS[Unconscious.ATTACK.ordinal()] = PlatformConfig.commonBoolean(SECTION, "unconscious_attack",
            "Unconscious players cannot attack", true);
        UNCONSCIOUS[Unconscious.BLOCKS.ordinal()] = PlatformConfig.commonBoolean(SECTION, "unconscious_blocks",
            "Unconscious players cannot break or place blocks", true);
        UNCONSCIOUS[Unconscious.USE_ITEMS.ordinal()] = PlatformConfig.commonBoolean(SECTION, "unconscious_use_items",
            "Unconscious players cannot use items", true);
        UNCONSCIOUS[Unconscious.CONTAINERS.ordinal()] = PlatformConfig.commonBoolean(SECTION, "unconscious_containers",
            "Unconscious players cannot open containers", true);
        UNCONSCIOUS[Unconscious.WAKE_ON_DAMAGE.ordinal()] = PlatformConfig.commonBoolean(SECTION, "unconscious_wake_on_damage",
            "Unconscious players wake up when they take damage", false);
        rate = PlatformAttributes.registerPlayer(RATE_ATTRIBUTE, 1.0, RATE_MIN, RATE_MAX);
        MetabolismText.bootstrap();
        MetabolismEffects.declare();
        MetabolismData.bootstrap();

        PlatformPlayerEvents.onLogin(player -> {
            MetabolismState state = DATA.get(player);
            state.startTiming(PlatformPlayers.overworldDayTime(player), PlatformPlayers.gameTime(player));
            refresh(player, state);
        });
        PlatformPlayerEvents.onLogout(player -> settle(player, DATA.get(player), true));
        PlatformPlayerEvents.onClone((original, fresh, death) -> {
            if (death) {
                if (clearOnDeath.getAsBoolean()) {
                    DATA.reset(fresh);
                } else {
                    DATA.copy(original, fresh);
                }
            }
        });
        PlatformPlayerEvents.onRespawn(player -> {
            MetabolismState state = DATA.get(player);
            state.startTiming(PlatformPlayers.overworldDayTime(player), PlatformPlayers.gameTime(player));
            refresh(player, state);
        });
        PlatformPlayerEvents.onChangedDimension(player -> refresh(player, DATA.get(player)));
        PlatformPlayerEvents.onTick(player -> {
            if (player.tickCount % SETTLE_INTERVAL == 0) {
                settle(player, DATA.get(player), false);
            }
        });
        PlatformPlayerEvents.onConsumed(Metabolism::consumed);
        PlatformPlayerEvents.onDamaged(player -> {
            MetabolismState state = DATA.get(player);
            if (state.unconscious && rule(Unconscious.WAKE_ON_DAMAGE)) {
                state.unconscious = false;
                lockMovement(player, state);
                sync(player, state, false);
            }
        });
        PlatformPlayerEvents.onSleepCheck(player -> DATA.get(player).appliedTier(Substance.CAFFEINE) >= 2
            ? net.minecraft.network.chat.Component.translatable(MetabolismText.KEY + "no_sleep") : null);
        PlatformPlayerEvents.setExhaustionScale((player, amount) ->
            player.level().isClientSide() ? amount : (float) (amount * DATA.get(player).exhaustionFactor));
        PlatformPlayerEvents.setInteractionGate(Metabolism::blocked);
        if (Platform.isPhysicalClient()) {
            com.tonywww.foodindustrial.food.metabolism.client.MetabolismClient.bootstrap();
        }
    }

    /** The player's state (server players; client players hold an unused one). */
    public static MetabolismState state(Player player) {
        return DATA.get(player);
    }

    /** What this client knows of its own player's metabolism. */
    public static MetabolismView client() {
        return client;
    }

    /** Sets the client code's hook for arriving views. */
    public static void onClientView(Consumer<MetabolismView> listener) {
        clientListener = listener;
    }

    /** Client side: applies a received view's tiers to the local player (only event-applied stats take effect). */
    public static void applyClient(Player player, MetabolismView view) {
        for (Substance substance : Substance.all()) {
            MetabolismEffects.apply(player, substance, view.tier(substance));
        }
        MetabolismEffects.lockMovement(player, view.applies(Unconscious.MOVEMENT));
    }

    static boolean rule(Unconscious rule) {
        return UNCONSCIOUS[rule.ordinal()].getAsBoolean();
    }

    /** Settles the time since the last settlement, then applies changed tiers. */
    public static void settle(ServerPlayer player, MetabolismState state, boolean silent) {
        boolean impurityPart = impurity.getAsBoolean();
        boolean nutrientPart = nutrients.getAsBoolean();
        switches(state, impurityPart, nutrientPart);
        long dayTime = PlatformPlayers.overworldDayTime(player);
        long gameTime = PlatformPlayers.gameTime(player);
        long sunStep = Math.max(0, Math.min(SUN_STEP_LIMIT, gameTime - state.lastGameTime()));
        long elapsed = state.elapsed(dayTime, gameTime);
        state.rate = rate(player);
        state.advance(elapsed, state.rate, impurityPart, nutrientPart);
        if (nutrientPart && sunStep > 0 && PlatformPlayers.inDirectSunlight(player)) {
            state.addSunlight(sunStep);
        }
        update(player, state, silent);
    }

    /** Lets {@code ticks} of world time pass at once, at the player's metabolic rate (development command). */
    public static void advance(ServerPlayer player, long ticks) {
        MetabolismState state = DATA.get(player);
        settle(player, state, false);
        state.advance(ticks, state.rate, impurity.getAsBoolean(), nutrients.getAsBoolean());
        update(player, state, false);
    }

    /**
     * One serving of {@code stack}: its nutrition data, plus impurity from its data and from its grade (development
     * commands pass the grade).
     */
    public static void eat(ServerPlayer player, ItemStack stack, ProductGrade grade) {
        MetabolismState state = DATA.get(player);
        settle(player, state, true);
        NutritionData data = Nutrition.of(stack);
        double units = (grade == ProductGrade.POOR ? IMPURITY_POOR : grade == ProductGrade.HOMEMADE ? IMPURITY_HOMEMADE : 0)
            + (data == null ? 0 : data.impurity());
        if (data == null && units == 0) {
            return;
        }
        state.intake(
            data == null ? 0 : data.amount(Component.CAFFEINE),
            data == null ? 0 : data.amount(Component.ALCOHOL),
            data == null ? 0 : data.amount(Component.FAST_SUGAR),
            data == null ? 0 : data.amount(Component.VITAMIN_C),
            data == null ? 0 : data.amount(Component.VITAMIN_B1),
            data == null ? 0 : data.amount(Component.VITAMIN_D),
            units, impurity.getAsBoolean(), nutrients.getAsBoolean());
        update(player, state, false);
    }

    /** Applies a changed state at once (development commands). */
    public static void changed(ServerPlayer player) {
        update(player, DATA.get(player), false);
    }

    /** Back to a new player's state (development command). */
    public static void reset(ServerPlayer player) {
        MetabolismState state = DATA.get(player);
        state.reset();
        refresh(player, state);
    }

    private static void consumed(ServerPlayer player, ItemStack stack) {
        eat(player, stack, ProductGrade.of(stack));
    }

    /** A part turned on or off: it starts again from a new player's state. */
    private static void switches(MetabolismState state, boolean impurityPart, boolean nutrientPart) {
        if (impurityPart != state.impurityOn) {
            state.resetImpurity();
            state.impurityOn = impurityPart;
        }
        if (nutrientPart != state.nutrientsOn) {
            state.resetNutrients();
            state.nutrientsOn = nutrientPart;
        }
    }

    /** The player's metabolic rate (attribute {@code foodindustrial:metabolic_rate}). */
    public static double rate(Player player) {
        return rate == null ? 1.0 : PlatformAttributes.value(player, rate);
    }

    /** Applies every effect again, silently, and sends the full state (login, respawn, dimension change). */
    private static void refresh(ServerPlayer player, MetabolismState state) {
        switches(state, impurity.getAsBoolean(), nutrients.getAsBoolean());
        state.rate = rate(player);
        for (Substance substance : Substance.all()) {
            int tier = state.tier(substance, state.impurityOn, state.nutrientsOn);
            state.tiers[substance.ordinal()] = tier;
            MetabolismEffects.apply(player, substance, tier);
        }
        state.unconscious = state.tiers[Substance.ALCOHOL.ordinal()] >= 4;
        state.movementLocked = !lockWanted(state);
        lockMovement(player, state);
        state.exhaustionFactor = 1.0 - exhaustionCut(state.tiers[Substance.CAFFEINE.ordinal()], state.tiers[Substance.FAST_SUGAR.ordinal()]);
        sync(player, state, true);
    }

    /** Applies changed tiers, the hourly effects of alcohol and the exhaustion factor, then syncs. */
    private static void update(ServerPlayer player, MetabolismState state, boolean silent) {
        MutableComponent message = null;
        for (Substance substance : Substance.all()) {
            int tier = state.tier(substance, state.impurityOn, state.nutrientsOn);
            if (tier != state.tiers[substance.ordinal()]) {
                state.tiers[substance.ordinal()] = tier;
                MetabolismEffects.apply(player, substance, tier);
                if (!silent && state.sent != null) {
                    MutableComponent line = MetabolismText.change(substance, tier);
                    message = message == null ? line : message.append(" · ").append(line);
                }
            }
        }
        int alcoholTier = state.tiers[Substance.ALCOHOL.ordinal()];
        // Re-judged every settlement: a player woken by damage loses consciousness again while above 0.30 %.
        state.unconscious = alcoholTier >= 4;
        lockMovement(player, state);
        if (alcoholTier >= 3 && state.clock - state.lastVomit >= HOUR) {
            state.lastVomit = state.clock;
            PlatformPlayers.loseFood(player, VOMIT_FOOD);
        }
        if (alcoholTier >= 5 && state.clock - state.lastPoison >= HOUR) {
            state.lastPoison = state.clock;
            PlatformPlayers.hurt(player, ALCOHOL_POISONING, POISON_DAMAGE);
        }
        state.exhaustionFactor = 1.0 - exhaustionCut(state.tiers[Substance.CAFFEINE.ordinal()], state.tiers[Substance.FAST_SUGAR.ordinal()]);
        if (message != null) {
            PlatformPlayers.actionBar(player, message);
        }
        sync(player, state, false);
    }

    private static boolean lockWanted(MetabolismState state) {
        return state.unconscious && rule(Unconscious.MOVEMENT);
    }

    private static void lockMovement(Player player, MetabolismState state) {
        boolean wanted = lockWanted(state);
        if (wanted != state.movementLocked) {
            state.movementLocked = wanted;
            MetabolismEffects.lockMovement(player, wanted);
        }
    }

    /** Sends the client its state when what it shows changed (or always when {@code force}). */
    private static void sync(ServerPlayer player, MetabolismState state, boolean force) {
        MetabolismView view = state.scratch;
        fill(view, state);
        if (force || state.sent == null || view.differs(state.sent)) {
            if (state.sent == null) {
                state.sent = new MetabolismView();
            }
            state.sent.copy(view);
            MetabolismView payload = new MetabolismView();
            payload.copy(view);
            PlatformNetwork.sendToPlayer(player, SYNC, payload);
        }
    }

    private static void fill(MetabolismView view, MetabolismState state) {
        view.amounts[Substance.CAFFEINE.ordinal()] = state.caffeine;
        view.amounts[Substance.ALCOHOL.ordinal()] = state.alcohol;
        view.amounts[Substance.FAST_SUGAR.ordinal()] = state.sugarInWindow();
        view.amounts[Substance.IMPURITY.ordinal()] = state.impurity;
        for (Substance substance : Substance.all()) {
            if (substance.nutrient()) {
                view.amounts[substance.ordinal()] = state.reserve[substance.nutrientIndex()];
            }
            int tier = state.tiers[substance.ordinal()];
            view.tiers[substance.ordinal()] = tier;
            view.next[substance.ordinal()] = switch (substance) {
                case FAST_SUGAR -> tier > 0 ? state.sugarEnduranceLeft() : -1;
                case VITAMIN_C, VITAMIN_B1, VITAMIN_D -> nextNutrientChange(state, substance, tier);
                default -> MetabolismRules.ticksToDrop(substance, view.amounts[substance.ordinal()], tier);
            };
            view.deficient[substance.ordinal()] = substance.nutrient() ? state.deficientFor(substance) : -1;
        }
        view.unconscious = state.unconscious;
        view.rate = state.rate;
        view.impurityOn = state.impurityOn;
        view.nutrientsOn = state.nutrientsOn;
        for (Unconscious rule : Unconscious.values()) {
            view.unconsciousRules[rule.ordinal()] = rule(rule);
        }
    }

    /** Ticks until a nutrient's tier changes: the reserve runs out (tier 0), or the deficiency turns severe (tier 1). */
    private static long nextNutrientChange(MetabolismState state, Substance nutrient, int tier) {
        if (tier == 0) {
            return state.nutrientsOn ? (long) Math.ceil(state.reserve(nutrient) * MetabolismRules.DAY) : -1;
        }
        if (tier == 1) {
            long severe = (long) (MetabolismRules.RESERVE_CAP[nutrient.nutrientIndex()] * MetabolismRules.DAY);
            return Math.max(0, severe - Math.max(0, state.deficientFor(nutrient)));
        }
        return -1;
    }

    private static void received(MetabolismView view) {
        client = view;
        clientListener.accept(view);
    }

    /** Interactions blocked while unconscious, on either side. */
    private static boolean blocked(Player player, Interaction interaction) {
        Unconscious rule = switch (interaction) {
            case ATTACK -> Unconscious.ATTACK;
            case BREAK_BLOCK, PLACE_BLOCK -> Unconscious.BLOCKS;
            case USE_ITEM -> Unconscious.USE_ITEMS;
            case OPEN_CONTAINER -> Unconscious.CONTAINERS;
        };
        if (player.level().isClientSide()) {
            return client.applies(rule);
        }
        return DATA.get(player).unconscious && rule(rule);
    }
}
