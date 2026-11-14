package com.tonywww.foodindustrial.platform.compat.patchouli;

import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.server.level.ServerPlayer;
import vazkii.patchouli.api.PatchouliAPI;

/** Opens the mod's Patchouli books (I23); does nothing when Patchouli is not installed. */
public final class PatchouliBooks {
    private PatchouliBooks() {
    }

    /** Opens book {@code foodindustrial:<book>} for {@code player}; {@code false} without Patchouli. */
    public static boolean open(ServerPlayer player, String book) {
        if (!Platform.isModLoaded("patchouli")) {
            return false;
        }
        Api.open(player, book);
        return true;
    }

    /** Kept apart so Patchouli classes load only when the mod is present. */
    private static final class Api {
        static void open(ServerPlayer player, String book) {
            PatchouliAPI.get().openBookGUI(player, PlatformIdentifier.id(book));
        }
    }
}
