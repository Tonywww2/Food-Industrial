package com.tonywww.foodindustrial;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.text.ConditionVocabulary;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.content.ModContent;
import com.tonywww.foodindustrial.food.FinishedFoods;
import com.tonywww.foodindustrial.gui.ModMenus;
import com.tonywww.foodindustrial.logistics.ModLogistics;
import com.tonywww.foodindustrial.manual.T0Devices;
import com.tonywww.foodindustrial.world.interaction.WorldInteractions;
import com.tonywww.foodindustrial.machine.ModMachines;
import com.tonywww.foodindustrial.material.tags.ModTags;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.world.ModWorldBlocks;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Version-independent mod core.
 * <p>
 * The loader entrypoint ({@code @Mod}) lives in {@link com.tonywww.foodindustrial.platform.ModEntrypoint};
 * it calls {@link #init()} once during mod construction. This class and everything outside the
 * {@code platform} package must not contain Stonecutter conditions, loader imports or version-specific
 * Minecraft APIs; use the {@code platform} API instead (see CONVENTIONS.md).
 */
public final class FoodIndustrial {
    public static final String MOD_ID = "foodindustrial";
    public static final Logger LOGGER = LoggerFactory.getLogger("FoodIndustrial");

    private FoodIndustrial() {
    }

    /**
     * Mod construction hook. Register content and lifecycle callbacks here through the
     * {@code platform} API (for example {@code ModItems.bootstrap()} that uses {@code PlatformRegistry}).
     */
    public static void init() {
        LOGGER.info("Initializing {} on {} / Minecraft {}", MOD_ID, Platform.loaderName(), Platform.minecraftVersion());
        Lang.bootstrap();
        ConditionVocabulary.bootstrap();
        Purity.bootstrap();
        ModCreativeTabs.bootstrap();
        ModMaterials.bootstrap();
        ModTags.bootstrap();
        ModWorldBlocks.bootstrap();
        ProcessTypes.bootstrap();
        ModMachines.bootstrap();
        ModMenus.bootstrap();
        ModLogistics.bootstrap();
        T0Devices.bootstrap();
        FinishedFoods.bootstrap();
        com.tonywww.foodindustrial.food.nutrition.Nutrition.bootstrap();
        com.tonywww.foodindustrial.food.metabolism.Metabolism.bootstrap();
        ModContent.bootstrap();
        WorldInteractions.bootstrap();
        com.tonywww.foodindustrial.book.ManualBook.bootstrap();
        com.tonywww.foodindustrial.integration.Integrations.bootstrap();
        if (Platform.isDevelopment()) {
            com.tonywww.foodindustrial.devtest.DevTestContent.bootstrap();
        }
    }
}
