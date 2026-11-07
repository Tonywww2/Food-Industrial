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

    /** Layouts of machines/03 §3 that stress the automatic layout. */
    private static final IoLayout[] MORE = {
        IoLayout.of(1, 1, 3, 3).withCatalyst(1).withUpgrades(3),                   // M27
        IoLayout.of(2, 1, 3, 2).withCatalyst(2).withUpgrades(4),                   // M29
        IoLayout.of(0, 1, 2, 6).withUpgrades(3),                                   // M22
        IoLayout.of(2, 2, 2, 1),                                                   // M04 (no power)
    };

    private static PowerKind power(IoLayout io) {
        return io.fuel() > 0 ? PowerKind.FUEL : io == MORE[3] ? PowerKind.NONE : PowerKind.ENERGY;
    }

    private static List<IoLayout> all() {
        List<IoLayout> all = new ArrayList<>(List.of(LAYOUTS));
        all.addAll(List.of(MORE));
        return all;
    }

    @Test
    void elementsStayInsideTheirZonesAndDoNotOverlap() {
        for (IoLayout io : all()) {
            GuiLayout layout = GuiLayout.of(io, power(io));
            List<GuiLayout.Box> boxes = new ArrayList<>();
            List<GuiLayout.ZoneKind> owners = new ArrayList<>();
            for (int i = 0; i < io.itemSlots(); i++) {
                SlotRole role = io.roleOf(i);
                GuiLayout.Box box = layout.slot(i);
                if (role == SlotRole.UPGRADE || role == SlotRole.MODULE) {
                    assertTrue(box.x() < 0 && box.x() + box.w() <= 0, io + " panel slot " + i);
                    continue;
                }
                boxes.add(box);
                owners.add(switch (role) {
                    case INPUT -> GuiLayout.ZoneKind.INPUTS;
                    case OUTPUT -> GuiLayout.ZoneKind.PRODUCTS;
                    case CATALYST -> GuiLayout.ZoneKind.PROCESSING;
                    default -> GuiLayout.ZoneKind.ENERGY;
                });
            }
            for (int i = 0; i < io.tanks(); i++) {
                GuiLayout.ZoneKind owner = i < io.fluidIn() ? GuiLayout.ZoneKind.INPUTS : GuiLayout.ZoneKind.PRODUCTS;
                boxes.add(layout.tank(i));
                owners.add(owner);
                if (layout.vent(i) != null) {
                    boxes.add(layout.vent(i));
                    owners.add(owner);
                }
                assertEquals(i >= io.fluidIn(), layout.vent(i) != null, io + " vent " + i);
            }
            boxes.add(layout.arrow());
            owners.add(GuiLayout.ZoneKind.PROCESSING);
            boxes.add(layout.purity());
            owners.add(GuiLayout.ZoneKind.PROCESSING);
            if (layout.energy() != null) {
                boxes.add(layout.energy());
                owners.add(GuiLayout.ZoneKind.ENERGY);
            }
            for (int i = 0; i < boxes.size(); i++) {
                GuiLayout.Box box = boxes.get(i);
                GuiLayout.Box frame = zone(layout, owners.get(i)).frame();
                int pad = GuiLayout.ZONE_PAD;
                assertTrue(box.x() >= frame.x() + pad && box.x() + box.w() <= frame.x() + frame.w() - pad
                    && box.y() >= frame.y() + pad && box.y() + box.h() <= frame.y() + frame.h() - pad, io + " " + owners.get(i) + " " + box);
            }
            for (int a = 0; a < boxes.size(); a++) {
                for (int b = a + 1; b < boxes.size(); b++) {
                    assertFalse(overlap(boxes.get(a), boxes.get(b)), io + " " + boxes.get(a) + " " + boxes.get(b));
                }
            }
            assertTrue(layout.statusY() >= layout.zones().get(0).frame().y() + layout.zones().get(0).frame().h() + 1, io.toString());
            assertTrue(layout.statusY() + 9 <= layout.inventoryLabelY(), io.toString());
        }
    }

    /** Zones in the order energy, inputs, processing, products, spanning the window without touching. */
    @Test
    void zonesSpanTheWindowInOrder() {
        for (IoLayout io : all()) {
            GuiLayout layout = GuiLayout.of(io, power(io));
            List<GuiLayout.Zone> zones = layout.zones();
            assertEquals(GuiLayout.AREA_LEFT, zones.get(0).frame().x(), io.toString());
            GuiLayout.Box last = zones.get(zones.size() - 1).frame();
            assertEquals(GuiLayout.AREA_RIGHT, last.x() + last.w(), io.toString());
            for (int i = 1; i < zones.size(); i++) {
                GuiLayout.Box a = zones.get(i - 1).frame();
                GuiLayout.Box b = zones.get(i).frame();
                assertTrue(zones.get(i - 1).kind().ordinal() < zones.get(i).kind().ordinal(), io.toString());
                assertEquals(a.x() + a.w() + GuiLayout.ZONE_GAP, b.x(), io.toString());
                assertEquals(a.y(), b.y(), io + " frames share the top");
                assertEquals(a.h(), b.h(), io + " frames share the bottom");
            }
            assertEquals(power(io) != PowerKind.NONE, zones.get(0).kind() == GuiLayout.ZoneKind.ENERGY, io.toString());
        }
    }

    /**
     * Slots sit on the rows of the 18-pixel grid, columns starting at the top row; tanks, the energy bar
     * and the arrow start at the top; the energy zone (fuel) is the only one centred vertically.
     */
    @Test
    void elementsSitOnTheGrid() {
        for (IoLayout io : all()) {
            GuiLayout layout = GuiLayout.of(io, power(io));
            for (SlotRole role : new SlotRole[]{SlotRole.INPUT, SlotRole.OUTPUT}) {
                int first = io.start(role);
                int count = role == SlotRole.INPUT ? io.itemIn() : io.itemOut();
                for (int i = 0; i < count; i++) {
                    assertEquals(GuiLayout.TOP + (i % GuiLayout.ROWS) * GuiLayout.SLOT, layout.slot(first + i).y(), io + " " + role + " " + i);
                }
            }
            for (int i = 0; i < io.tanks(); i++) {
                assertEquals(GuiLayout.TOP, layout.tank(i).y(), io + " tank " + i);
            }
            assertEquals(GuiLayout.TOP, layout.arrow().y(), io + " arrow on the top row");
            if (layout.energy() != null) {
                assertEquals(GuiLayout.TOP, layout.energy().y(), io + " energy");
            }
            if (layout.flame() != null) {
                int top = layout.flame().y() - 2;
                int bottom = layout.slot(io.start(SlotRole.FUEL) + io.fuel() - 1).y() + GuiLayout.SLOT;
                assertEquals(top - GuiLayout.TOP, GuiLayout.TOP + GuiLayout.ROWS * GuiLayout.SLOT - bottom, io + " fuel centred");
            }
        }
    }

    /** Arrow, purity frame and catalysts are centred in the processing zone, in that order from the top. */
    @Test
    void processingZoneStack() {
        for (IoLayout io : all()) {
            GuiLayout layout = GuiLayout.of(io, power(io));
            GuiLayout.Box frame = zone(layout, GuiLayout.ZoneKind.PROCESSING).frame();
            int centre = frame.x() + frame.w() / 2;
            GuiLayout.Box arrow = layout.arrow();
            GuiLayout.Box purity = layout.purity();
            assertEquals(centre, arrow.x() + arrow.w() / 2, 1, io + " arrow centred");
            assertEquals(centre, purity.x() + purity.w() / 2, 1, io + " purity centred");
            assertTrue(purity.y() >= arrow.y() + arrow.h(), io + " purity under the arrow");
            for (int i = 0; i < io.catalyst(); i++) {
                GuiLayout.Box catalyst = layout.slot(io.start(SlotRole.CATALYST) + i);
                assertTrue(catalyst.y() >= purity.y() + purity.h(), io + " catalyst under the purity");
                assertEquals(GuiLayout.TOP + 2 * GuiLayout.SLOT, catalyst.y(), io + " catalyst on the bottom row");
            }
            if (io.catalyst() > 0) {
                GuiLayout.Box first = layout.slot(io.start(SlotRole.CATALYST));
                assertEquals(centre, first.x() + io.catalyst() * GuiLayout.SLOT / 2, 1, io + " catalysts centred");
            }
        }
    }

    @Test
    void heightFollowsTheSlots() {
        for (IoLayout io : all()) {
            assertEquals(GuiLayout.TALL, GuiLayout.of(io, power(io)).height(), io + " every machine uses the tall window");
        }
    }
    private static GuiLayout.Zone zone(GuiLayout layout, GuiLayout.ZoneKind kind) {
        return layout.zones().stream().filter(zone -> zone.kind() == kind).findFirst().orElseThrow(() -> new AssertionError("no zone " + kind));
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
