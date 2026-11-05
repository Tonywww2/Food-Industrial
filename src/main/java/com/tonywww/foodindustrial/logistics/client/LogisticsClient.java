package com.tonywww.foodindustrial.logistics.client;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.logistics.ModLogistics;
import com.tonywww.foodindustrial.logistics.TankBlockEntity;
import com.tonywww.foodindustrial.logistics.TankKind;
import com.tonywww.foodindustrial.platform.client.PlatformBlockRenderers;

/** Client side of the tanks: the fluid drawn inside each block (machines/03 §5, 2026-11-05). */
public final class LogisticsClient {
    /** Inner space of the wooden tank (open top, 2-pixel walls, 1-pixel bottom). */
    private static final float WOOD_INSET = 2 / 16F;
    private static final float WOOD_BOTTOM = 1 / 16F;
    /** Inner space of the metal tank (1-pixel frame). */
    private static final float METAL_INSET = 1 / 16F;

    private LogisticsClient() {
    }

    public static void bootstrap() {
        for (ModLogistics.Tank tank : ModLogistics.tanks()) {
            PlatformBlockRenderers.<TankBlockEntity>registerFluidBox(tank.type(), LogisticsClient::box);
        }
    }

    private static PlatformBlockRenderers.FluidBox box(TankBlockEntity tank) {
        FluidAmount fluid = tank.fluid();
        if (fluid.isEmpty()) {
            return null;
        }
        float fill = Math.min(1F, fluid.amount() / (float) tank.kind().capacity());
        if (tank.kind() == TankKind.WOODEN) {
            float top = WOOD_BOTTOM + fill * (1 - WOOD_BOTTOM - 0.5F / 16F);
            return new PlatformBlockRenderers.FluidBox(fluid, WOOD_INSET, WOOD_BOTTOM, WOOD_INSET, 1 - WOOD_INSET, top, 1 - WOOD_INSET);
        }
        float top = METAL_INSET + fill * (1 - 2 * METAL_INSET);
        return new PlatformBlockRenderers.FluidBox(fluid, METAL_INSET + 0.01F, METAL_INSET, METAL_INSET + 0.01F,
            1 - METAL_INSET - 0.01F, top, 1 - METAL_INSET - 0.01F);
    }
}
