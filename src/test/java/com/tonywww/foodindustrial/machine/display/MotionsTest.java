package com.tonywww.foodindustrial.machine.display;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotionsTest {
    @Test
    void progressMovesOnWhileRunningAndStaysWithinTheBar() {
        // 40 of 200 ticks done, 1.5 progress per tick
        assertEquals(0.2F, Motions.progress(40, 200, 1500, 0, true), 1e-6);
        assertEquals(0.275F, Motions.progress(40, 200, 1500, 10, true), 1e-6);
        assertEquals(1F, Motions.progress(40, 200, 1500, 1000, true), 1e-6);
        // a stopped machine does not move on
        assertEquals(0.2F, Motions.progress(40, 200, 1500, 10, false), 1e-6);
        // no recipe
        assertEquals(0F, Motions.progress(0, 0, 1000, 5, true), 1e-6);
        // a clock that ran backwards does not pull the bar back
        assertEquals(0.2F, Motions.progress(40, 200, 1500, -3, true), 1e-6);
    }

    @Test
    void aProductSwellsThenSettles() {
        assertEquals(1.4F, Motions.pop(0), 1e-6);
        assertTrue(Motions.pop(2) < Motions.pop(1));
        assertEquals(1F, Motions.pop(Motions.POP_TICKS), 1e-6);
        assertEquals(1F, Motions.pop(-1), 1e-6);
        assertEquals(1F, Motions.pop(1000), 1e-6);
    }

    @Test
    void everyMotionKeepsAnIconNearItsSlot() {
        float[] out = new float[5];
        for (Motion motion : Motion.values()) {
            for (int index = 0; index < 12; index++) {
                for (float time = 0; time < 400; time += 0.37F) {
                    Motions.input(motion, time, index, 12, out);
                    assertTrue(Math.abs(out[Motions.DX]) <= 2 && Math.abs(out[Motions.DY]) <= 2.5F, motion + " offset");
                    assertTrue(out[Motions.STRETCH_X] > 0.6F && out[Motions.STRETCH_X] < 1.3F, motion + " stretch x");
                    assertTrue(out[Motions.STRETCH_Y] > 0.6F && out[Motions.STRETCH_Y] < 1.3F, motion + " stretch y");
                    assertTrue(Math.abs(out[Motions.TURN]) < 0.3F, motion + " turn");
                }
            }
        }
    }

    @Test
    void nothingMovesWithoutAMotion() {
        float[] out = {9, 9, 9, 9, 9};
        Motions.input(Motion.NONE, 123, 2, 4, out);
        assertEquals(0F, out[Motions.DX]);
        assertEquals(0F, out[Motions.DY]);
        assertEquals(1F, out[Motions.STRETCH_X]);
        assertEquals(1F, out[Motions.STRETCH_Y]);
        assertEquals(0F, out[Motions.TURN]);
    }

    @Test
    void pressingKeepsTheLowerEdgeOfTheIcon() {
        float[] out = new float[5];
        for (float time = 0; time < 60; time += 0.5F) {
            Motions.input(Motion.PRESS, time, 0, 1, out);
            // centre + half height: the lower edge of a 16-pixel icon stays at -8
            assertEquals(-8F, out[Motions.DY] - 8 * out[Motions.STRETCH_Y], 1e-4);
        }
    }

    @Test
    void puffsAreSpreadAlongTheirWay() {
        float first = Motions.phase(10, 36, 0, 3);
        float second = Motions.phase(10, 36, 1, 3);
        assertTrue(first >= 0 && first < 1 && second >= 0 && second < 1);
        assertEquals(1 / 3F, second - first, 1e-5);
        assertEquals(first, Motions.phase(10 + 36, 36, 0, 3), 1e-4);
    }
}
