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
    /** Damage type of nitrite poisoning (bypasses armour, foods/02 §4.4). */
    public static final String NITRITE_POISONING = "foodindustrial:nitrite_poisoning";
    private static final PlatformPlayerData.Key<MetabolismState> DATA = PlatformPlayerData.register("metabolism", MetabolismState::new);
    private static final PlatformNetwork.Channel<MetabolismView> SYNC = PlatformNetwork.toClient("metabolism", MetabolismView::read, Metabolism::received);

    private static final String SECTION = "metabolism";
    private static BooleanSupplier clearOnDeath = () -> true;
    private static BooleanSupplier impurity = () -> true;
    private static java.util.function.Supplier<java.util.List<String>> disabledNutrients = java.util.List::of;
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
        java.util.List<String> nutrientIds = new java.util.ArrayList<>();
        for (Substance substance : Substance.all()) {
            if (substance.nutrient()) {
                nutrientIds.add(substance.id());
            }
        }
        disabledNutrients = PlatformConfig.commonStringList(SECTION, "disabled_nutrients",
            "Nutrients that are switched off: not counted, used up or shown, and without effects. Any of " + String.join(", ", nutrientIds)
                + "; list them all to turn the nutrient part off", java.util.List.of(), nutrientIds);
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
        MetabolismCommands.bootstrap();
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
        PlatformPlayerEvents.setDamageTakenScale((player, amount) -> {
            double extra = MetabolismEffects.damageTaken(DATA.get(player).tiers);
            return extra == 0 ? amount : (float) (amount * (1 + extra));
        });
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
        int nutrientPart = nutrientMask();
        switches(state, impurityPart, nutrientPart);
        long dayTime = PlatformPlayers.overworldDayTime(player);
        long gameTime = PlatformPlayers.gameTime(player);
        long sunStep = Math.max(0, Math.min(SUN_STEP_LIMIT, gameTime - state.lastGameTime()));
        long elapsed = state.elapsed(dayTime, gameTime);
        state.rate = rate(player);
        state.advance(elapsed, state.rate, impurityPart, nutrientPart);
        if (MetabolismState.on(nutrientPart, Substance.VITAMIN_D.nutrientIndex()) && sunStep > 0 && PlatformPlayers.inDirectSunlight(player)) {
            state.addSunlight(sunStep);
        }
        update(player, state, silent);
    }

    /** Lets {@code ticks} of world time pass at once, at the player's metabolic rate (development command). */
    public static void advance(ServerPlayer player, long ticks) {
        MetabolismState state = DATA.get(player);
        settle(player, state, false);
        state.advance(ticks, state.rate, impurity.getAsBoolean(), nutrientMask());
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
        int quality = com.tonywww.foodindustrial.food.FoodLine.quality(stack);
        double units = (grade == ProductGrade.POOR ? IMPURITY_POOR : grade == ProductGrade.HOMEMADE ? IMPURITY_HOMEMADE : 0)
            + (data == null ? 0 : data.impurity());
        if (data == null && units == 0) {
            return;
        }
        double[] amounts = null;
        if (data != null) {
            amounts = new double[MetabolismState.NUTRIENTS];
            for (Substance substance : Substance.all()) {
                if (substance.nutrient()) {
                    amounts[substance.nutrientIndex()] = data.amount(substance.component());
                }
            }
        }
        state.intake(
            data == null ? 0 : data.amount(Component.CAFFEINE),
            data == null ? 0 : data.amount(Component.ALCOHOL),
            data == null ? 0 : data.amount(Component.FAST_SUGAR),
            data == null ? 0 : data.nitrite(quality),
            amounts,
            data == null ? 0 : data.amount(Component.PROVITAMIN_A),
            units, impurity.getAsBoolean(), nutrientMask());
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

    /** A part or a nutrient turned on or off: it starts again from a new player's state. */
    private static void switches(MetabolismState state, boolean impurityPart, int nutrientPart) {
        if (impurityPart != state.impurityOn) {
            state.resetImpurity();
            state.impurityOn = impurityPart;
        }
        if (nutrientPart != state.nutrientsOn) {
            for (int i = 0; i < MetabolismState.NUTRIENTS; i++) {
                if (MetabolismState.on(nutrientPart, i) != MetabolismState.on(state.nutrientsOn, i)) {
                    state.resetNutrient(i);
                }
            }
            state.nutrientsOn = nutrientPart;
        }
    }

    /** The nutrients the configuration leaves on (option {@code disabled_nutrients}, I46), as a mask by nutrient index. */
    static int nutrientMask() {
        int mask = MetabolismState.ALL_NUTRIENTS;
        for (String id : disabledNutrients.get()) {
            Substance substance = Substance.byId(id);
            if (substance != null && substance.nutrient()) {
                mask &= ~(1 << substance.nutrientIndex());
            }
        }
        return mask;
    }

    /** The player's metabolic rate (attribute {@code foodindustrial:metabolic_rate}). */
    public static double rate(Player player) {
        return rate == null ? 1.0 : PlatformAttributes.value(player, rate);
    }

    /** Applies every effect again, silently, and sends the full state (login, respawn, dimension change). */
    private static void refresh(ServerPlayer player, MetabolismState state) {
        switches(state, impurity.getAsBoolean(), nutrientMask());
        state.rate = rate(player);
        for (Substance substance : Substance.all()) {
            int tier = state.tier(substance, state.impurityOn, state.nutrientsOn);
            state.tiers[substance.ordinal()] = tier;
            MetabolismEffects.apply(player, substance, tier);
        }
        state.unconscious = unconsciousTier(state);
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
        // Re-judged every settlement: a player woken by damage loses consciousness again while above 0.30 % blood
        // alcohol or 50 % methemoglobin.
        state.unconscious = unconsciousTier(state);
        lockMovement(player, state);
        if (alcoholTier >= 3 && state.clock - state.lastVomit >= HOUR) {
            state.lastVomit = state.clock;
            PlatformPlayers.loseFood(player, VOMIT_FOOD);
        }
        if (alcoholTier >= 5 && state.clock - state.lastPoison >= HOUR) {
            state.lastPoison = state.clock;
            PlatformPlayers.hurt(player, ALCOHOL_POISONING, POISON_DAMAGE);
        }
        if (state.tiers[Substance.NITRITE.ordinal()] >= 4 && state.clock - state.lastNitritePoison >= NITRITE_POISON_INTERVAL) {
            state.lastNitritePoison = state.clock;
            PlatformPlayers.hurt(player, NITRITE_POISONING, NITRITE_POISON_DAMAGE);
        }
        state.exhaustionFactor = 1.0 - exhaustionCut(state.tiers[Substance.CAFFEINE.ordinal()], state.tiers[Substance.FAST_SUGAR.ordinal()]);
        if (message != null) {
            PlatformPlayers.actionBar(player, message);
        }
        sync(player, state, false);
    }

    /** Unconscious from alcohol (> 0.30 %) or nitrite (> 50 % methemoglobin). */
    private static boolean unconsciousTier(MetabolismState state) {
        return state.tiers[Substance.ALCOHOL.ordinal()] >= 4 || state.tiers[Substance.NITRITE.ordinal()] >= 3;
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
        view.amounts[Substance.NITRITE.ordinal()] = state.methemoglobin;
        view.nitriteToday = state.nitriteToday();
        view.amounts[Substance.IMPURITY.ordinal()] = state.impurity;
        for (Substance substance : Substance.all()) {
            if (substance.nutrient()) {
                view.amounts[substance.ordinal()] = state.reserve[substance.nutrientIndex()];
            }
            int tier = state.tiers[substance.ordinal()];
            view.tiers[substance.ordinal()] = tier;
            view.next[substance.ordinal()] = substance == Substance.FAST_SUGAR ? (tier > 0 ? state.sugarEnduranceLeft() : -1)
                : substance.nutrient() ? nextNutrientChange(state, substance, tier)
                : MetabolismRules.ticksToDrop(substance, view.amounts[substance.ordinal()], tier);
            view.deficient[substance.ordinal()] = substance.nutrient() ? state.deficientFor(substance) : -1;
            view.today[substance.ordinal()] = substance.hasExcess() ? state.today(substance) : 0;
        }
        view.unconscious = state.unconscious;
        view.rate = state.rate;
        view.impurityOn = state.impurityOn;
        view.nutrientsOn = state.nutrientsOn;
        for (Unconscious rule : Unconscious.values()) {
            view.unconsciousRules[rule.ordinal()] = rule(rule);
        }
    }

    /**
     * Ticks until a nutrient's tier changes: the reserve runs out (tier 0), the deficiency turns severe (tier 1), or the
     * excess ends or eases (tiers 3 and 4).
     */
    private static long nextNutrientChange(MetabolismState state, Substance nutrient, int tier) {
        if (nutrient.excessTier(tier)) {
            return state.excessLeft(nutrient, tier);
        }
        if (tier == 0) {
            return MetabolismState.on(state.nutrientsOn, nutrient.nutrientIndex())
                ? (long) Math.ceil(state.reserve(nutrient) * MetabolismRules.DAY) : -1;
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
