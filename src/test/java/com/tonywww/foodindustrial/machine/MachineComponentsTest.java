package com.tonywww.foodindustrial.machine;

import com.tonywww.foodindustrial.machine.component.Backoff;
import com.tonywww.foodindustrial.machine.component.CatalystCounters;
import com.tonywww.foodindustrial.machine.component.RedstoneControl;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.core.Tier;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MachineComponentsTest {
    @Test
    void facesFollowTheFacing() {
        for (Direction facing : new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}) {
            for (SideConfig.Face face : SideConfig.Face.values()) {
                assertEquals(face, SideConfig.Face.of(face.toDirection(facing), facing), facing + " " + face);
            }
        }
        // looking at the front of a machine facing north (standing north of it), its left is east
        assertEquals(Direction.EAST, SideConfig.Face.LEFT.toDirection(Direction.NORTH));
        assertEquals(Direction.SOUTH, SideConfig.Face.BACK.toDirection(Direction.NORTH));
    }

    @Test
    void sideDefaults() {
        SideConfig sides = new SideConfig();
        assertTrue(sides.isDefault());
        assertTrue(sides.closed(SideConfig.Face.FRONT));
        assertEquals(SideConfig.Mode.INPUT, sides.items(SideConfig.Face.TOP));
        assertEquals(SideConfig.Mode.OUTPUT, sides.items(SideConfig.Face.BOTTOM));
        assertEquals(SideConfig.Mode.INPUT, sides.fluids(SideConfig.Face.LEFT));
        assertEquals(SideConfig.Mode.OUTPUT, sides.fluids(SideConfig.Face.BACK));
        assertEquals(SideConfig.Mode.BOTH, SideConfig.Mode.OFF.cycle(false));
        sides.setAuto(false, false, true);
        assertFalse(sides.isDefault());
    }

    @Test
    void backoffDoublesUpTo80() {
        Backoff backoff = new Backoff();
        long time = 100;
        int[] expected = {8, 16, 32, 64, 80, 80};
        for (int interval : expected) {
            assertTrue(backoff.due(time));
            backoff.failed(time);
            assertEquals(time + interval, backoff.next());
            time = backoff.next();
        }
        backoff.succeeded(time);
        assertEquals(time + 4, backoff.next());
    }

    @Test
    void catalystWearsAfterRuns() {
        CatalystCounters counters = new CatalystCounters(1);
        for (int i = 1; i < 64; i++) {
            assertFalse(counters.wear(0, 64));
        }
        assertTrue(counters.wear(0, 64));
        assertEquals(0, counters.used(0));
    }

    @Test
    void redstone() {
        assertTrue(RedstoneControl.IGNORE.allows(false));
        assertTrue(RedstoneControl.LOW.allows(false));
        assertFalse(RedstoneControl.LOW.allows(true));
        assertTrue(RedstoneControl.HIGH.allows(true));
        assertFalse(RedstoneControl.DISABLED.allows(true));
    }

    @Test
    void layoutAndTicks() {
        IoLayout layout = IoLayout.of(3, 2, 3, 2).withCatalyst(1).withUpgrades(2).withModules(3);
        assertEquals(11, layout.itemSlots());
        assertEquals(5, layout.tanks());
        assertEquals(1, layout.deviceSlots().catalyst());
        // 200-tick recipe: T2 ×2 → 100 ticks, T3 ×4 → 52 ticks (whole 4-tick batches)
        assertEquals(1100, DeviceProfile.standard(Tier.T2).yieldPermille());
    }
}
