package com.tonywww.foodindustrial;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.tonywww.foodindustrial.FoodIndustrial.MOD_ID;

@Mod(MOD_ID)
public class FoodIndustrial {
    public static final String MOD_ID = "foodindustrial";
    IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
}
