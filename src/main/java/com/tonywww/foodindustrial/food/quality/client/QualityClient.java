package com.tonywww.foodindustrial.food.quality.client;

import com.tonywww.foodindustrial.food.FoodLine;
import com.tonywww.foodindustrial.food.quality.QualityText;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.client.PlatformClient;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Client side of the food quality (I39): the quality and declaration lines of item tooltips (text-standard §3.3). */
public final class QualityClient {
    private QualityClient() {
    }

    public static void bootstrap() {
        PlatformClient.onItemTooltip(QualityClient::tooltip);
    }

    private static void tooltip(ItemStack stack, List<Component> lines) {
        int code = FoodLine.quality(stack);
        if (code == 0 || lines.isEmpty()) {
            return;
        }
        List<Component> quality = QualityText.lines(code, Platform.isShiftDown());
        lines.addAll(1, quality);
        List<Component> declarations = QualityText.declarations(code);
        if (!declarations.isEmpty()) {
            lines.addAll(beforeModName(stack, lines), declarations);
        }
    }

    /** Where lines go at the end: before the mod-name line tooltip mods put last, when there is one. */
    private static int beforeModName(ItemStack stack, List<Component> lines) {
        String modName = Platform.modName(PlatformTags.itemId(stack.getItem()).split(":")[0]);
        int last = lines.size() - 1;
        if (last > 0 && modName != null && modName.equals(ChatFormatting.stripFormatting(lines.get(last).getString()))) {
            return last;
        }
        return lines.size();
    }
}
