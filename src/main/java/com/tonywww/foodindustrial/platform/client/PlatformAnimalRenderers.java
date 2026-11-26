package com.tonywww.foodindustrial.platform.client;

import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import com.tonywww.foodindustrial.platform.world.PlatformAnimals;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.world.entity.EntityType;
//? if >=26.1 {
/*import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.fox.Fox;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
*///?}
//? if neoforge && <26.1 {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
*///?}
//? if forge {
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
//?}

/**
 * Renderers of the mod's animals (I44): the vanilla body's renderer with the animal's own texture,
 * {@code textures/entity/<id>.png} (26.1 also {@code <id>_baby.png} for the baby model).
 */
public final class PlatformAnimalRenderers {
    private PlatformAnimalRenderers() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void bind(IEventBus modBus) {
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            for (PlatformAnimals.Registered animal : PlatformAnimals.animals()) {
                String id = animal.id();
                if (animal.body() == PlatformAnimals.Body.OCELOT) {
                    event.registerEntityRenderer((EntityType<Ocelot>) animal.type().get(), context -> new OcelotRenderer(context) {
                        //? if >=26.1 {
                        /*@Override
                        public Identifier getTextureLocation(FelineRenderState state) {
                            return PlatformIdentifier.id("textures/entity/" + id + (state.isBaby ? "_baby" : "") + ".png");
                        }
                        *///?} else {
                        @Override
                        public ResourceLocation getTextureLocation(Ocelot entity) {
                            return PlatformIdentifier.id("textures/entity/" + id + ".png");
                        }
                        //?}
                    });
                } else if (animal.body() == PlatformAnimals.Body.CHICKEN) {
                    event.registerEntityRenderer((EntityType<Chicken>) animal.type().get(), context -> new ChickenRenderer(context) {
                        //? if >=26.1 {
                        /*@Override
                        public Identifier getTextureLocation(ChickenRenderState state) {
                            return PlatformIdentifier.id("textures/entity/" + id + (state.isBaby ? "_baby" : "") + ".png");
                        }
                        *///?} else {
                        @Override
                        public ResourceLocation getTextureLocation(Chicken entity) {
                            return PlatformIdentifier.id("textures/entity/" + id + ".png");
                        }
                        //?}
                    });
                } else {
                    event.registerEntityRenderer((EntityType<Fox>) animal.type().get(), context -> new FoxRenderer(context) {
                        //? if >=26.1 {
                        /*@Override
                        public Identifier getTextureLocation(FoxRenderState state) {
                            return PlatformIdentifier.id("textures/entity/" + id + (state.isBaby ? "_baby" : "") + ".png");
                        }
                        *///?} else {
                        @Override
                        public ResourceLocation getTextureLocation(Fox entity) {
                            return PlatformIdentifier.id("textures/entity/" + id + ".png");
                        }
                        //?}
                    });
                }
            }
        });
    }
}
