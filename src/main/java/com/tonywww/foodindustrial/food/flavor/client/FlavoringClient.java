package com.tonywww.foodindustrial.food.flavor.client;

import com.tonywww.foodindustrial.food.flavor.DosageForm;
import com.tonywww.foodindustrial.food.flavor.FlavorType;
import com.tonywww.foodindustrial.food.flavor.Flavorings;
import com.tonywww.foodindustrial.platform.client.PlatformClient;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** The flavour type and dosage form lines of a flavouring's tooltip (I48, foods/11 §4). */
public final class FlavoringClient {
    private FlavoringClient() {
    }

    public static void bootstrap() {
        PlatformClient.onItemTooltip((stack, lines) -> {
            FlavorType type = Flavorings.type(stack.getItem());
            if (type == null || lines.isEmpty()) {
                return;
            }
            int at = 1;
            lines.add(at++, Component.translatable("tooltip.foodindustrial.flavoring.type", Component.translatable(type.key()))
                .withStyle(ChatFormatting.GRAY));
            DosageForm form = Flavorings.form(stack);
            if (form != null) {
                lines.add(at, Component.translatable("tooltip.foodindustrial.flavoring.form", Component.translatable(form.key()))
                    .withStyle(ChatFormatting.GRAY));
            }
        });
    }
}
