package com.tonywww.foodindustrial.platform.world;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
//? if >=26.1 {
/*import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.chicken.ChickenVariants;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.animal.fox.Fox;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
*///?}
//? if neoforge && <26.1 {
/*import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
*///?}
//? if forge {
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
//?}

/**
 * Animals of the mod (I44: civet, beaver; user 2026-11-19: new animals on vanilla bodies; foods/23: duck). Each borrows a
 * vanilla animal's body, behaviour and attributes ({@link Body}); only its type, texture, loot and the egg it lays are its
 * own, and it breeds its own kind. They spawn on grass like other animals; the biomes come from data (biome modifiers).
 */
public final class PlatformAnimals {
    /** The vanilla animal a mod animal is built on. */
    public enum Body {
        /** Ocelot: 0.6 × 0.7, flees players, eats raw fish. */
        OCELOT,
        /** Fox: 0.6 × 0.7, sleeps by day, eats sweet berries. */
        FOX,
        /** Chicken: 0.4 × 0.7, flutters down, eats seeds, lays eggs (its own kind, see {@link #register(String, Body, Supplier)}). */
        CHICKEN
    }

    /** A registered animal: id, body and type. */
    public record Registered(String id, Body body, Supplier<? extends EntityType<?>> type) {
    }

    private static final List<Registered> ANIMALS = new ArrayList<>();

    private PlatformAnimals() {
    }

    /** Registers an animal on {@code body}. Call during mod construction. */
    public static Supplier<? extends EntityType<?>> register(String id, Body body) {
        return register(id, body, null);
    }

    /** Registers an animal on {@code body}; a chicken body lays {@code lays} (null: none) instead of eggs. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Supplier<? extends EntityType<?>> register(String id, Body body, @Nullable Supplier<? extends Item> lays) {
        Supplier<? extends EntityType<?>> type = switch (body) {
            case OCELOT -> {
                Supplier<EntityType<OcelotBody>>[] self = new Supplier[1];
                self[0] = PlatformRegistry.register(Registries.ENTITY_TYPE, id,
                    () -> build(EntityType.Builder.<OcelotBody>of((t, level) -> new OcelotBody(t, level), MobCategory.CREATURE)
                        .sized(0.6F, 0.7F).clientTrackingRange(10), id));
                yield self[0];
            }
            case FOX -> {
                Supplier<EntityType<FoxBody>>[] self = new Supplier[1];
                self[0] = PlatformRegistry.register(Registries.ENTITY_TYPE, id,
                    () -> build(EntityType.Builder.<FoxBody>of((t, level) -> new FoxBody(t, level), MobCategory.CREATURE)
                        .sized(0.6F, 0.7F).clientTrackingRange(8), id));
                yield self[0];
            }
            case CHICKEN -> {
                Supplier<EntityType<ChickenBody>>[] self = new Supplier[1];
                self[0] = PlatformRegistry.register(Registries.ENTITY_TYPE, id,
                    () -> build(EntityType.Builder.<ChickenBody>of((t, level) -> new ChickenBody(t, level, lays), MobCategory.CREATURE)
                        .sized(0.4F, 0.7F).clientTrackingRange(10), id));
                yield self[0];
            }
        };
        ANIMALS.add(new Registered(id, body, type));
        return type;
    }

    public static List<Registered> animals() {
        return List.copyOf(ANIMALS);
    }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> build(EntityType.Builder<T> builder, String id) {
        //? if >=26.1 {
        /*return builder.build(ResourceKey.create(Registries.ENTITY_TYPE, com.tonywww.foodindustrial.platform.PlatformIdentifier.id(id)));
        *///?} else {
        return builder.build(FoodIndustrial.MOD_ID + ":" + id);
        //?}
    }

    /** A new animal of the same type as {@code parent} (breeding gives the mod animal, not the vanilla one). */
    @Nullable
    @SuppressWarnings("unchecked")
    private static <T extends AgeableMob> T offspring(T parent, ServerLevel level) {
        //? if >=26.1 {
        /*return (T) parent.getType().create(level, EntitySpawnReason.BREEDING);
        *///?} else {
        return (T) parent.getType().create(level);
        //?}
    }

    /** An animal on the ocelot's body. */
    public static class OcelotBody extends Ocelot {
        public OcelotBody(EntityType<? extends Ocelot> type, Level level) {
            super(type, level);
        }

        @Override
        public @Nullable Ocelot getBreedOffspring(ServerLevel level, AgeableMob partner) {
            return offspring(this, level);
        }
    }

    /** An animal on the fox's body. */
    public static class FoxBody extends Fox {
        public FoxBody(EntityType<? extends Fox> type, Level level) {
            super(type, level);
        }

        @Override
        public @Nullable Fox getBreedOffspring(ServerLevel level, AgeableMob partner) {
            return offspring(this, level);
        }
    }

    /**
     * An animal on the chicken's body that lays {@code lays} instead of eggs (the vanilla timer: every 6,000–12,000 ticks).
     * On 26.1 it keeps the temperate chicken's look, whatever the biome (the cold chicken has another model).
     */
    public static class ChickenBody extends Chicken {
        @Nullable
        private final Supplier<? extends Item> lays;

        public ChickenBody(EntityType<? extends Chicken> type, Level level, @Nullable Supplier<? extends Item> lays) {
            super(type, level);
            this.lays = lays;
        }

        @Override
        public void aiStep() {
            // lay before the vanilla countdown reaches zero, so the vanilla egg never comes
            if (lays != null && level() instanceof ServerLevel server && isAlive() && !isBaby() && !isChickenJockey() && eggTime <= 1) {
                playSound(SoundEvents.CHICKEN_EGG, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                //? if >=26.1 {
                /*spawnAtLocation(server, lays.get());
                *///?} else {
                spawnAtLocation(lays.get());
                //?}
                gameEvent(GameEvent.ENTITY_PLACE);
                eggTime = random.nextInt(6000) + 6000;
            }
            super.aiStep();
        }

        @Override
        public @Nullable Chicken getBreedOffspring(ServerLevel level, AgeableMob partner) {
            return offspring(this, level);
        }
        //? if >=26.1 {
        /*@Override
        public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason,
                                                     @Nullable SpawnGroupData data) {
            SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
            setVariant(registryAccess().lookupOrThrow(Registries.CHICKEN_VARIANT).getOrThrow(ChickenVariants.TEMPERATE));
            return result;
        }
        *///?}
    }

    /** Attributes and spawn rules (mod bus). Called by {@code ModEntrypoint} through {@code PlatformEvents}. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void bind(IEventBus modBus) {
        modBus.addListener((EntityAttributeCreationEvent event) -> {
            for (Registered animal : ANIMALS) {
                event.put((EntityType) animal.type().get(), (switch (animal.body()) {
                    case OCELOT -> Ocelot.createAttributes();
                    case FOX -> Fox.createAttributes();
                    case CHICKEN -> Chicken.createAttributes();
                }).build());
            }
        });
        //? if forge {
        modBus.addListener((SpawnPlacementRegisterEvent event) -> {
            for (Registered animal : ANIMALS) {
                event.register((EntityType<Animal>) animal.type().get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            }
        });
        //?} else {
        /*modBus.addListener((RegisterSpawnPlacementsEvent event) -> {
            for (Registered animal : ANIMALS) {
                event.register((EntityType<Animal>) animal.type().get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            }
        });
        *///?}
    }
}
