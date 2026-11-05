package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuiLayoutTest {
    /** Layouts of machines/03 §3 that stress the automatic layout. */
    private static final IoLayout[] LAYOUTS = {
        IoLayout.of(1, 2, 0, 0).withUpgrades(2),                                   // M11
        IoLayout.of(3, 2, 3, 2).withCatalyst(1).withUpgrades(2).withModules(3),    // M14
        IoLayout.of(6, 2, 2, 1).withUpgrades(2).withModules(1),                    // M21
        IoLayout.of(2, 2, 1, 1).withFuel(1),                                       // M03
        IoLayout.of(4, 4, 0, 1).withFuel(1),                                       // M10
        IoLayout.of(0, 0, 2, 2).withCatalyst(1).withUpgrades(4),                   // M31
        IoLayout.of(1, 1, 2, 3).withCatalyst(1).withUpgrades(3),                   // M24
    };

    @Test
    void elementsStayInsideTheWindowAndDoNotOverlap() {
        for (IoLayout io : LAYOUTS) {
            PowerKind power = io.fuel() > 0 ? PowerKind.FUEL : PowerKind.ENERGY;
            GuiLayout layout = GuiLayout.of(io, power);
            List<GuiLayout.Box> boxes = new ArrayList<>();
            for (int i = 0; i < io.itemSlots(); i++) {
                SlotRole role = io.roleOf(i);
                GuiLayout.Box box = layout.slot(i);
                if (role == SlotRole.UPGRADE || role == SlotRole.MODULE) {
                    assertTrue(box.x() < 0 && box.x() + box.w() <= 0, io + " panel slot " + i);
                    continue;
                }
                boxes.add(box);
            }
            for (int i = 0; i < io.tanks(); i++) {
                boxes.add(layout.tank(i));
                if (layout.vent(i) != null) {
                    boxes.add(layout.vent(i));
                }
                assertEquals(i >= io.fluidIn(), layout.vent(i) != null, io + " vent " + i);
            }
            boxes.add(layout.arrow());
            if (layout.energy() != null) {
                boxes.add(layout.energy());
            }
            for (GuiLayout.Box box : boxes) {
                assertTrue(box.x() >= 8 && box.x() + box.w() <= GuiLayout.WIDTH - 8, io + " " + box);
                assertTrue(box.y() >= 16 && box.y() + box.h() <= layout.statusY(), io + " " + box);
            }
            for (int a = 0; a < boxes.size(); a++) {
                for (int b = a + 1; b < boxes.size(); b++) {
                    assertFalse(overlap(boxes.get(a), boxes.get(b)), io + " " + boxes.get(a) + " " + boxes.get(b));
                }
            }
            assertTrue(layout.statusY() + 9 <= layout.inventoryLabelY(), io.toString());
        }
    }

    @Test
    void heightFollowsTheSlots() {
        assertEquals(GuiLayout.SHORT, GuiLayout.of(IoLayout.of(1, 2, 0, 0).withUpgrades(2), PowerKind.ENERGY).height());
        assertEquals(GuiLayout.TALL, GuiLayout.of(IoLayout.of(1, 1, 1, 0), PowerKind.ENERGY).height());
        assertEquals(GuiLayout.TALL, GuiLayout.of(IoLayout.of(3, 1, 0, 0), PowerKind.NONE).height());
    }

    @Test
    void rolesInSlotOrder() {
        IoLayout io = IoLayout.of(2, 2, 0, 0).withCatalyst(1).withFuel(1).withUpgrades(2).withModules(1);
        SlotRole[] expected = {SlotRole.INPUT, SlotRole.INPUT, SlotRole.OUTPUT, SlotRole.OUTPUT, SlotRole.CATALYST, SlotRole.FUEL,
            SlotRole.UPGRADE, SlotRole.UPGRADE, SlotRole.MODULE};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], io.roleOf(i));
        }
        assertEquals(4, io.start(SlotRole.CATALYST));
        assertEquals(8, io.start(SlotRole.MODULE));
    }

    @Test
    void sideScopesCountAsSettings() {
        SideConfig sides = new SideConfig();
        sides.setItemScope(SideConfig.Face.BOTTOM, SideConfig.OutputScope.BYPRODUCT);
        assertFalse(sides.isDefault());
        sides.setItemScope(SideConfig.Face.BOTTOM, SideConfig.OutputScope.ALL);
        assertTrue(sides.isDefault());
        sides.setFluidOutputTank(SideConfig.Face.BACK, 1);
        sides.setFluidInputTank(SideConfig.Face.LEFT, 2);
        SideConfig copy = new SideConfig();
        copy.copyFrom(sides);
        assertEquals(1, copy.fluidOutputTank(SideConfig.Face.BACK));
        assertEquals(2, copy.fluidInputTank(SideConfig.Face.LEFT));
        copy.clampTanks(2, 1);
        assertEquals(SideConfig.ALL_TANKS, copy.fluidOutputTank(SideConfig.Face.BACK));
        assertEquals(SideConfig.ALL_TANKS, copy.fluidInputTank(SideConfig.Face.LEFT));
        assertTrue(copy.isDefault());
    }

    private static boolean overlap(GuiLayout.Box a, GuiLayout.Box b) {
        return a.x() < b.x() + b.w() && b.x() < a.x() + a.w() && a.y() < b.y() + b.h() && b.y() < a.y() + a.h();
    }
}
