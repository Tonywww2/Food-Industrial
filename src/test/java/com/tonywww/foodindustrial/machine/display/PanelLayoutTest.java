package com.tonywww.foodindustrial.machine.display;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanelLayoutTest {
    @Test
    void anEmptySideStillShowsOneSlot() {
        PanelLayout layout = new PanelLayout(0, 0);
        assertEquals(1, layout.slots(false));
        assertEquals(1, layout.slots(true));
        // padding, slot, gap, arrow, gap, slot, padding
        assertEquals(4 + 18 + 3 + 24 + 3 + 18 + 4, layout.width());
        assertEquals(4 + 18 + 4, layout.height());
    }

    @Test
    void sidesGrowToTwoAndThreeRows() {
        assertEquals(3, new PanelLayout(3, 1).slots(false));
        assertEquals(4, new PanelLayout(4, 1).slots(false));     // 2 x 2
        assertEquals(8, new PanelLayout(7, 1).slots(false));     // 4 x 2
        assertEquals(9, new PanelLayout(9, 1).slots(false));     // 3 x 3
        assertEquals(12, new PanelLayout(12, 1).slots(false));   // 4 x 3
        assertEquals(12, new PanelLayout(40, 1).inputs());
        assertEquals(4 + 2 * 18 + 4, new PanelLayout(4, 1).height());
        assertEquals(4 + 3 * 18 + 4, new PanelLayout(9, 2).height());
    }

    @Test
    void everySlotLiesInsideThePanelAndSlotsDoNotOverlap() {
        for (int inputs = 0; inputs <= 12; inputs++) {
            for (int outputs = 0; outputs <= 12; outputs++) {
                PanelLayout layout = new PanelLayout(inputs, outputs);
                float halfWidth = layout.width() / 2F;
                float halfHeight = layout.height() / 2F;
                for (int side = 0; side < 2; side++) {
                    boolean output = side == 1;
                    int slots = layout.slots(output);
                    assertTrue(slots >= (output ? layout.outputs() : layout.inputs()));
                    for (int i = 0; i < slots; i++) {
                        float x = layout.slotX(output, i);
                        float y = layout.slotY(output, i);
                        assertTrue(x >= -halfWidth + PanelLayout.PAD - 0.01F && x + PanelLayout.SLOT <= halfWidth - PanelLayout.PAD + 0.01F,
                            inputs + "/" + outputs + " slot " + i + " x " + x);
                        assertTrue(y >= -halfHeight + PanelLayout.PAD - 0.01F && y + PanelLayout.SLOT <= halfHeight - PanelLayout.PAD + 0.01F,
                            inputs + "/" + outputs + " slot " + i + " y " + y);
                        for (int j = 0; j < i; j++) {
                            boolean apart = Math.abs(layout.slotX(output, j) - x) >= PanelLayout.SLOT - 0.01F
                                || Math.abs(layout.slotY(output, j) - y) >= PanelLayout.SLOT - 0.01F;
                            assertTrue(apart, inputs + "/" + outputs + " slots " + j + " and " + i);
                        }
                    }
                }
                // the arrow sits between the two sides
                assertTrue(layout.arrowX() >= layout.slotX(false, layout.slots(false) - 1) + PanelLayout.SLOT - 0.01F || layout.slots(false) > 1);
                assertTrue(layout.arrowX() + PanelLayout.ARROW_W <= layout.slotX(true, 0) + 0.01F);
                assertTrue(layout.arrowX() >= layout.inputLeft() + layout.inputWidth() - 0.01F);
            }
        }
    }

    @Test
    void theInputAreaCoversTheInputSlots() {
        PanelLayout layout = new PanelLayout(5, 2);
        assertEquals(layout.slotX(false, 0), layout.inputLeft());
        assertEquals(layout.slotY(false, 0) + PanelLayout.SLOT, layout.inputTop());
        assertEquals(3 * 18, layout.inputWidth());
        assertEquals(2 * 18, layout.inputHeight());
    }
}
