package com.tonywww.foodindustrial.platform;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.client.PlatformClient;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.machine.PlatformMachineCapabilities;
import com.tonywww.foodindustrial.platform.network.PlatformNetwork;
import com.tonywww.foodindustrial.platform.purity.PlatformPurity;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
//? if forge {
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
*///?}

/**
 * Loader entrypoint. Owns the {@code @Mod} annotation and the mod event bus; business code starts in
 * {@link FoodIndustrial#init()} and never touches loader types directly.
 */
@Mod(FoodIndustrial.MOD_ID)
public final class ModEntrypoint {
    //? if forge {
    public ModEntrypoint(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
    //?} else {
    /*public ModEntrypoint(IEventBus modBus, ModContainer container) {
    *///?}
        PlatformEvents.bind(modBus);
        PlatformPlayerData.bind();
        PlatformPlayerEvents.bind();
        PlatformAttributes.bind(modBus);
        PlatformMachineCapabilities.bind(modBus);
        PlatformNetwork.bind(modBus);
        PlatformDatagen.bind(modBus);
        if (Platform.isPhysicalClient()) {
            PlatformClient.bind(modBus);
        }
        PlatformPurity.bootstrap();
        PlatformIngredients.bind(modBus);
        com.tonywww.foodindustrial.platform.recipe.PlatformRecipeConditions.bind();
        PlatformKinetics.bind(modBus);
        PlatformRecipes.bindSync();
        PlatformFluids.bind(modBus);
        PlatformTankItem.bind(modBus);
        PlatformWorldgen.bind();
        FoodIndustrial.init();
        //? if forge {
        PlatformConfig.register(context);
        //?} else {
        /*PlatformConfig.register(container);
        *///?}
        PlatformRegistry.bind(modBus);
    }
}
