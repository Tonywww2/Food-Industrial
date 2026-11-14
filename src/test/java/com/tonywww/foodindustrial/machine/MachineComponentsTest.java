package com.tonywww.foodindustrial.machine;

import com.tonywww.foodindustrial.machine.component.Backoff;
import com.tonywww.foodindustrial.machine.component.CatalystCounters;
import com.tonywww.foodindustrial.machine.component.RedstoneControl;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.core.Tier;
import net.minecraft.core.BlockPos;
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

    @Test
    void outdoorsModesOfferTheEnvironment() {
        // M09 sun crystallization (I14): the device entry can offer every environment word, the mode itself none
        com.tonywww.foodindustrial.machine.definition.MachineMode sun = com.tonywww.foodindustrial.machine.definition.MachineMode.outdoors("sun_crystallization");
        assertTrue(sun.outdoors());
        assertEquals(java.util.Set.of("mode.sun_crystallization"), sun.capabilities().keys());
        assertTrue(sun.possibleCapabilities().covers(com.tonywww.foodindustrial.recipe.Capabilities.of(
            "mode.sun_crystallization", "environment.open_sky", "environment.clear_weather", "environment.daytime")));
        com.tonywww.foodindustrial.machine.definition.MachineMode cooling = com.tonywww.foodindustrial.machine.definition.MachineMode.of(
            "cooling_crystallization", com.tonywww.foodindustrial.recipe.Capabilities.of("temperature.cooling"));
        assertFalse(cooling.outdoors());
        assertEquals(cooling.capabilities(), cooling.possibleCapabilities());
    }

    @Test
    void modesThatNeedAir() {
        // M17 flash drying, M19 combustion, M20 calcining and roasting run only with air (I15)
        com.tonywww.foodindustrial.machine.definition.MachineMode flash = com.tonywww.foodindustrial.machine.definition.MachineMode.of(
            "flash_drying", com.tonywww.foodindustrial.recipe.Capabilities.NONE).needing("atmosphere.air");
        assertEquals(java.util.Set.of("atmosphere.air"), flash.needs().keys());
        assertEquals(java.util.Set.of("mode.flash_drying"), flash.capabilities().keys());
        assertEquals(java.util.Set.of(), com.tonywww.foodindustrial.machine.definition.MachineMode.of(
            "drum_drying", com.tonywww.foodindustrial.recipe.Capabilities.NONE).needs().keys());
    }

    @Test
    void variableHeightStructures() {
        // M19 (2026-11-11): bottom, 2–6 body layers, top = 4–8 layers
        com.tonywww.foodindustrial.machine.definition.StructurePattern tower = com.tonywww.foodindustrial.machine.definition.StructurePattern.builder()
            .layer("###", "###", "#C#")
            .layer("###", "# #", "###").repeat(2, 6)
            .layer("###", "###", "###")
            .where('#', "tower_wall", true)
            .build();
        assertEquals(4, tower.minLayers());
        assertEquals(8, tower.maxLayers());
        BlockPos origin = new BlockPos(0, 64, 0);
        BlockPos[] tall = tower.bounds(origin, Direction.NORTH);
        BlockPos[] low = tower.bounds(origin, Direction.NORTH, 4);
        assertEquals(64 + 7, tall[1].getY());
        assertEquals(64 + 3, low[1].getY());
        assertEquals(64, low[0].getY());
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
            () -> com.tonywww.foodindustrial.machine.definition.StructurePattern.builder().repeat(1, 2));
    }

    @Test
    void typeDefaultEnergy() {
        // machines/01 §2 (2026-11-11): FE/t of recipes that write none
        assertEquals(10, com.tonywww.foodindustrial.recipe.ProcessTypes.GRINDING.defaultEnergy());
        assertEquals(15, com.tonywww.foodindustrial.recipe.ProcessTypes.ABSORBING.defaultEnergy());
        assertEquals(20, com.tonywww.foodindustrial.recipe.ProcessTypes.REACTING.defaultEnergy());
        assertEquals(40, com.tonywww.foodindustrial.recipe.ProcessTypes.KILN.defaultEnergy());
        assertEquals(80, com.tonywww.foodindustrial.recipe.ProcessTypes.ELECTROLYZING.defaultEnergy());
        assertEquals(5, com.tonywww.foodindustrial.recipe.ProcessTypes.FERMENTING.defaultEnergy());
        assertEquals(0, com.tonywww.foodindustrial.recipe.ProcessTypes.HARVESTING.defaultEnergy());
    }

    @Test
    void crackingYieldFollowsTheTubeSegments() {
        // M27 (I16): ×0.7 with one segment, 0.1 more per segment, ×1.0 with four
        assertEquals(700, com.tonywww.foodindustrial.machine.chemical.CrackingYield.permille(1));
        assertEquals(900, com.tonywww.foodindustrial.machine.chemical.CrackingYield.permille(3));
        assertEquals(1000, com.tonywww.foodindustrial.machine.chemical.CrackingYield.permille(4));
        assertEquals(1000, com.tonywww.foodindustrial.machine.chemical.CrackingYield.permille(6));
        assertEquals(700, com.tonywww.foodindustrial.machine.chemical.CrackingYield.permille(0));
    }

    @Test
    void colonyLooksSplitIntoColourAndPattern() {
        // M30 (I17): a look is colour + pattern × colours
        int colors = com.tonywww.foodindustrial.machine.bio.ColonyLook.COLORS.length;
        int look = 3 + 5 * colors;
        assertEquals(com.tonywww.foodindustrial.machine.bio.ColonyLook.COLORS[3], com.tonywww.foodindustrial.machine.bio.ColonyLook.color(look));
        assertEquals(5, com.tonywww.foodindustrial.machine.bio.ColonyLook.pattern(look));
        assertEquals(8, com.tonywww.foodindustrial.machine.bio.ColonyLook.PATTERNS);
    }

    @Test
    void plateWeights() {
        // M30 plate recipes (I17): weights default to 1
        com.tonywww.foodindustrial.recipe.Plate plain = new com.tonywww.foodindustrial.recipe.Plate(4, java.util.List.of(), false);
        assertEquals(1, plain.weight(2));
        com.tonywww.foodindustrial.recipe.Plate weighted = new com.tonywww.foodindustrial.recipe.Plate(4, java.util.List.of(1, 3), false);
        assertEquals(3, weighted.weight(1));
    }
}
