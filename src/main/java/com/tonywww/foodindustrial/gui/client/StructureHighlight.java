package com.tonywww.foodindustrial.gui.client;

import com.tonywww.foodindustrial.platform.client.ClientHooks;
import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * Highlights the wrong positions of a multiblock in the world (machines/04 §5): red marker particles
 * along the edges of each position for 10 seconds after the button on the structure page is pressed.
 */
final class StructureHighlight {
    private static final int DURATION = 200;
    private static final int PERIOD = 4;
    private static final double[][] CORNERS = {
        {0, 0, 0}, {1, 0, 0}, {0, 0, 1}, {1, 0, 1}, {0, 1, 0}, {1, 1, 0}, {0, 1, 1}, {1, 1, 1}, {0.5, 0.5, 0.5}};

    private static List<BlockPos> positions = List.of();
    private static int ticksLeft;

    private StructureHighlight() {
    }

    static void show(List<BlockPos> wrong) {
        positions = List.copyOf(wrong);
        ticksLeft = DURATION;
    }

    /** Client tick. */
    static void tick() {
        if (ticksLeft <= 0) {
            return;
        }
        ticksLeft--;
        if (ticksLeft % PERIOD != 0) {
            return;
        }
        for (BlockPos pos : positions) {
            for (double[] corner : CORNERS) {
                ClientHooks.markerParticle(pos.getX() + corner[0], pos.getY() + corner[1], pos.getZ() + corner[2]);
            }
        }
    }
}
