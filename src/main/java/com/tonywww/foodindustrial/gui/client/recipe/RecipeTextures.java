package com.tonywww.foodindustrial.gui.client.recipe;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;

import java.util.List;

/** Coordinates of {@code textures/gui/recipe.png}, mirrored from {@code tools/gui/recipe_textures.py}. */
public final class RecipeTextures {
    public static final String TEXTURE = FoodIndustrial.MOD_ID + ":textures/gui/recipe.png";
    private static final int W = 96;
    private static final int H = 48;
    private static final List<String> CATEGORIES = List.of("temperature", "pressure", "atmosphere", "sterile", "method",
        "heat_sensitivity", "environment", "mode");

    private RecipeTextures() {
    }

    /** 9 × 9 icon of a condition category (the method icon for unknown ones). */
    public static void condition(Canvas canvas, String category, int x, int y) {
        int index = CATEGORIES.indexOf(category);
        canvas.blit(TEXTURE, x, y, (index < 0 ? 4 : index) * 10, 0, 9, 9, W, H);
    }

    /** 18 × 18 frame of a structure position that may be a port. */
    public static void port(Canvas canvas, int x, int y) {
        canvas.blit(TEXTURE, x, y, 0, 16, 18, 18, W, H);
    }

    /** 18 × 18 frame of the controller position. */
    public static void controller(Canvas canvas, int x, int y) {
        canvas.blit(TEXTURE, x, y, 18, 16, 18, 18, W, H);
    }

    /** 18 × 18 frame of the selected device. */
    public static void selected(Canvas canvas, int x, int y) {
        canvas.blit(TEXTURE, x, y, 36, 16, 18, 18, W, H);
    }
}
