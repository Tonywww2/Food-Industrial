package com.tonywww.foodindustrial.manual.client;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.manual.DeviceContents;
import com.tonywww.foodindustrial.manual.Fumigation;
import com.tonywww.foodindustrial.manual.T0DeviceBlockEntity;
import com.tonywww.foodindustrial.manual.T0Devices;
import com.tonywww.foodindustrial.manual.T0Kind;
import com.tonywww.foodindustrial.platform.client.PlatformBlockRenderers;
import com.tonywww.foodindustrial.platform.client.PlatformBlockRenderers.Contents;
import com.tonywww.foodindustrial.platform.client.PlatformBlockRenderers.FluidBox;
import com.tonywww.foodindustrial.platform.client.PlatformBlockRenderers.ItemPlacement;
import com.tonywww.foodindustrial.platform.client.PlatformClient;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Client side of the T0 devices (I12): what is drawn in each block (liquids, items on the rack and
 * the tray) and the “sulfured” line on fumigated food.
 */
public final class ManualClient {
    /** Item positions on a rack or tray, in slot order. */
    private static final float[][] CORNERS = {{0.28F, 0.28F}, {0.72F, 0.28F}, {0.28F, 0.72F}, {0.72F, 0.72F}};

    private ManualClient() {
    }

    public static void bootstrap() {
        T0Devices.devices().forEach((kind, device) -> {
            if (kind != T0Kind.CROCK && kind != T0Kind.FUMIGATION_BOX) {
                PlatformBlockRenderers.<T0DeviceBlockEntity>registerContents(device.type(), ManualClient::contents);
            }
        });
        PlatformClient.onItemTooltip((stack, lines) -> {
            if (Fumigation.isMarked(stack)) {
                lines.add(Math.min(1, lines.size()), Component.translatable(GuiText.key("manual", "sulfured")).withStyle(ChatFormatting.YELLOW));
            }
        });
    }

    private static Contents contents(T0DeviceBlockEntity device) {
        DeviceContents contents = device.contents();
        List<ItemPlacement> items = new ArrayList<>();
        FluidBox box = null;
        switch (device.kind()) {
            case CLAY_POT, IRON_POT -> {
                box = level(contents.tank(0), contents.tankCapacity(), 3, 1, 13, 7);
                float y = box == null ? 0.12F : Math.max(0.12F, box.y1() - 0.04F);
                for (int i = 0; i < contents.itemSlots(); i++) {
                    items.add(new ItemPlacement(contents.item(i), 0.4F + 0.2F * i, y, 0.5F, 0.35F, true, 30 * i));
                }
            }
            case LEACH_BARREL -> {
                box = level(contents.tank(0), contents.tankCapacity(), 2, 2, 14, 13);
                items.add(new ItemPlacement(contents.item(0), 0.5F, 0.16F, 0.5F, 0.6F, true, 0));
            }
            case SALT_PAN -> {
                box = level(contents.tank(0), contents.tankCapacity(), 0, 1, 16, 2.5F);
                items.add(new ItemPlacement(contents.item(0), 0.5F, 0.075F, 0.5F, 0.55F, true, 0));
            }
            case DRYING_RACK -> corners(contents, items, 12.4F / 16F);
            case KOJI_TRAY -> corners(contents, items, 1.4F / 16F);
            default -> {
            }
        }
        return new Contents(box, items);
    }

    private static void corners(DeviceContents contents, List<ItemPlacement> items, float y) {
        for (int i = 0; i < Math.min(4, contents.itemSlots()); i++) {
            items.add(new ItemPlacement(contents.item(i), CORNERS[i][0], y, CORNERS[i][1], 0.4F, true, 15 * i));
        }
    }

    /** A liquid filling the inside from {@code bottom} up to {@code top} pixels by the fill level. */
    private static FluidBox level(FluidAmount fluid, int capacity, float inset, float bottom, float far, float height) {
        if (fluid.isEmpty() || capacity <= 0) {
            return null;
        }
        float fill = Math.min(1F, fluid.amount() / (float) capacity);
        float y1 = (bottom + Math.max(0.5F, fill * height)) / 16F;
        return new FluidBox(fluid, inset / 16F + 0.002F, bottom / 16F, inset / 16F + 0.002F, far / 16F - 0.002F, y1, far / 16F - 0.002F);
    }
}
