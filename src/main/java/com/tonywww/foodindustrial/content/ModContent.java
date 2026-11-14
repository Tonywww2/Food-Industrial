package com.tonywww.foodindustrial.content;

import com.tonywww.foodindustrial.content.batch1.Batch1Recipes;
import com.tonywww.foodindustrial.content.batch2.Batch2Recipes;
import com.tonywww.foodindustrial.content.batch3.Batch3Recipes;
import com.tonywww.foodindustrial.content.batch4.Batch4Colours;
import com.tonywww.foodindustrial.content.batch4.Batch4Emulsifiers;
import com.tonywww.foodindustrial.content.batch4.Batch4Enzymes;
import com.tonywww.foodindustrial.content.batch4.Batch4Others;
import com.tonywww.foodindustrial.content.batch4.Batch4Salts;
import com.tonywww.foodindustrial.content.batch4.Batch4Thickeners;
import com.tonywww.foodindustrial.content.batch5.Batch5Aids;
import com.tonywww.foodindustrial.content.batch5.Batch5GumBase;
import com.tonywww.foodindustrial.content.batch5.Batch5Minerals;
import com.tonywww.foodindustrial.content.batch5.Batch5Nutrients;
import com.tonywww.foodindustrial.content.batch5.Batch5Refining;
import com.tonywww.foodindustrial.content.batch5.Batch5Vitamins;
import com.tonywww.foodindustrial.content.flavors.FlavorRecipes;

/** The additive content batches (I18–I20, I30): their recipes by data generation. */
public final class ModContent {
    private ModContent() {
    }

    /** Registers the batches' recipe generators. Called during mod construction. */
    public static void bootstrap() {
        Batch1Recipes.bootstrap();
        Batch2Recipes.bootstrap();
        Batch3Recipes.bootstrap();
        Batch4Salts.bootstrap();
        Batch4Colours.bootstrap();
        Batch4Thickeners.bootstrap();
        Batch4Emulsifiers.bootstrap();
        Batch4Others.bootstrap();
        Batch4Enzymes.bootstrap();
        Batch5Aids.bootstrap();
        Batch5Vitamins.bootstrap();
        Batch5Nutrients.bootstrap();
        Batch5Minerals.bootstrap();
        Batch5GumBase.bootstrap();
        Batch5Refining.bootstrap();
        FlavorRecipes.bootstrap();
        Cultures.bootstrap();
    }
}
