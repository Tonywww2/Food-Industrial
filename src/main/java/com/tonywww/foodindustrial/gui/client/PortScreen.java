package com.tonywww.foodindustrial.gui.client;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.GuiLayout;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.gui.MachineView;
import com.tonywww.foodindustrial.gui.MenuActions;
import com.tonywww.foodindustrial.gui.PortMenu;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.component.VentMode;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.entity.PortBlockEntity;
import com.tonywww.foodindustrial.platform.Platform;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;
import com.tonywww.foodindustrial.platform.client.gui.PlatformScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Port screen (machines/04 §5): the controller's slots (with the fuel flame), tanks (with the venting
 * switches) or energy that the port connects, at the positions a single-block window has them, and two
 * buttons: direction (input, output, off) and automatic output.
 */
public class PortScreen extends PlatformScreen<PortMenu> {
    private static final SideConfig.Mode[] DIRECTIONS = {SideConfig.Mode.INPUT, SideConfig.Mode.OUTPUT, SideConfig.Mode.OFF};
    private static final long DUMP_CONFIRM_MS = 2000;

    private int dumpTank = -1;
    private long dumpUntil;

    public PortScreen(PortMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, GuiLayout.WIDTH, menu.height());
    }

    private int statusY() {
        return menu.layout() != null ? menu.layout().statusY() : 60;
    }

    private GuiLayout.Box modeButton() {
        return new GuiLayout.Box(8, statusY() - 4, 78, 14);
    }

    private GuiLayout.Box autoButton() {
        return new GuiLayout.Box(90, statusY() - 4, 78, 14);
    }

    private boolean hasAuto() {
        return menu.kind() != PortBlockEntity.Kind.ENERGY;
    }

    @Override
    protected void drawBackground(Canvas canvas, int mouseX, int mouseY) {
        int mx = mouseX - leftPos;
        int my = mouseY - topPos;
        MachineView view = menu.view();
        canvas.push();
        canvas.translate(leftPos, topPos);
        Widgets.window(canvas, 0, 0, imageWidth, imageHeight);
        GuiLayout layout = menu.layout();
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            boolean catalyst = menu.controller() != null && menu.kind() == PortBlockEntity.Kind.ITEM
                && i < menu.controller().layout().itemSlots() && menu.controller().layout().roleOf(i) == SlotRole.CATALYST;
            Widgets.slot(canvas, catalyst ? Widgets.SlotKind.CATALYST : Widgets.SlotKind.NORMAL, slot.x - 1, slot.y - 1);
        }
        if (layout == null || menu.controller() == null) {
            canvas.text(Component.translatable(GuiText.key("port", "unattached")), 8, 24, 0xFFB71C1C, false);
        } else if (menu.kind() == PortBlockEntity.Kind.FLUID) {
            IoLayout io = menu.controller().layout();
            for (int i = 0; i < io.tanks(); i++) {
                GuiLayout.Box box = layout.tank(i);
                FluidAmount fluid = i < view.tanks.length ? view.tanks[i] : FluidAmount.EMPTY;
                Widgets.tank(canvas, box.x(), box.y(), box.w(), box.h(), fluid, view.tankCapacity);
                if (i == dumpTank && System.currentTimeMillis() < dumpUntil) {
                    canvas.outline(box.x(), box.y(), box.w(), box.h(), 0xFFFF4040);
                }
                GuiLayout.Box vent = layout.vent(i);
                if (vent != null) {
                    int ventMode = i < view.tankVents.length ? view.tankVents[i] : 0;
                    Widgets.button(canvas, vent.x(), vent.y(), vent.w(), vent.h(), vent.contains(mx, my), ventMode != 0);
                    Widgets.vent(canvas, ventMode, vent.x() + (vent.w() - 8) / 2, vent.y() + (vent.h() - 8) / 2);
                }
            }
        } else if (menu.kind() == PortBlockEntity.Kind.ITEM && layout.flame() != null) {
            GuiLayout.Box flame = layout.flame();
            int lit = view.burnTotal > 0 && view.burnLeft > 0 ? Math.max(1, view.burnLeft * 14 / view.burnTotal) : 0;
            Widgets.flame(canvas, flame.x(), flame.y(), lit);
        } else if (menu.kind() == PortBlockEntity.Kind.ENERGY && layout.energy() != null) {
            GuiLayout.Box box = layout.energy();
            Widgets.energy(canvas, box.x(), box.y(), box.w(), box.h(), view.energy, view.energyCapacity);
        }
        SideConfig.Mode mode = SideConfig.Mode.values()[Math.floorMod(view.portMode, 4)];
        GuiLayout.Box modeBox = modeButton();
        Widgets.textButton(canvas, modeBox.x(), modeBox.y(), modeBox.w(), modeBox.h(),
            Component.translatable(GuiText.key("port", "mode"), Component.translatable(GuiText.key("io", mode.name().toLowerCase(java.util.Locale.ROOT)))),
            modeBox.contains(mx, my), false);
        if (hasAuto()) {
            GuiLayout.Box autoBox = autoButton();
            Widgets.textButton(canvas, autoBox.x(), autoBox.y(), autoBox.w(), autoBox.h(),
                Component.translatable(GuiText.key("port", "auto"), Component.translatable(GuiText.key("io", view.portAuto ? "on" : "switched_off"))),
                autoBox.contains(mx, my), view.portAuto);
        }
        canvas.pop();
    }

    @Override
    protected void drawOverlay(Canvas canvas, int mouseX, int mouseY) {
        int mx = mouseX - leftPos;
        int my = mouseY - topPos;
        MachineView view = menu.view();
        GuiLayout layout = menu.layout();
        List<Component> lines = new ArrayList<>();
        if (layout != null && menu.controller() != null) {
            if (menu.kind() == PortBlockEntity.Kind.FLUID) {
                for (int i = 0; i < menu.controller().layout().tanks(); i++) {
                    if (layout.tank(i).contains(mx, my)) {
                        FluidAmount fluid = i < view.tanks.length ? view.tanks[i] : FluidAmount.EMPTY;
                        Component role = Component.translatable(GuiText.key("tank", i < menu.controller().layout().fluidIn() ? "input" : "output"));
                        if (fluid.isEmpty()) {
                            lines.add(role);
                            lines.add(Component.translatable(GuiText.key("tank", "empty")).withStyle(ChatFormatting.GRAY));
                        } else {
                            lines.add(PlatformFluids.displayName(fluid));
                            if (Purity.bears(fluid.fluid())) {
                                Tooltips.purityLine(Purity.materialOf(fluid.fluid()), Purity.get(fluid), lines);
                            }
                            lines.add(role.copy().withStyle(ChatFormatting.GRAY));
                        }
                        lines.add(Component.translatable(GuiText.key("tank", "amount"), String.valueOf(fluid.amount()), String.valueOf(view.tankCapacity))
                            .withStyle(ChatFormatting.GRAY));
                        if (i == dumpTank && System.currentTimeMillis() < dumpUntil) {
                            lines.add(Component.translatable(GuiText.key("tank", "dump")).withStyle(ChatFormatting.RED));
                        }
                    }
                    GuiLayout.Box vent = layout.vent(i);
                    if (vent != null && vent.contains(mx, my)) {
                        int ventMode = i < view.tankVents.length ? view.tankVents[i] : 0;
                        lines.add(Component.translatable(GuiText.key("vent", "title"),
                            Component.translatable(GuiText.key("vent", VentMode.byIndex(ventMode).id()))));
                        lines.add(Component.translatable(GuiText.key("vent", "gas_only")).withStyle(ChatFormatting.GRAY));
                    }
                }
            } else if (menu.kind() == PortBlockEntity.Kind.ITEM) {
                Slot slot = hoveredSlot();
                if (slot != null && !slot.hasItem()) {
                    Component name = slotName(slot.index);
                    if (name != null) {
                        lines.add(name);
                    }
                }
            } else if (menu.kind() == PortBlockEntity.Kind.ENERGY && layout.energy() != null && layout.energy().contains(mx, my)) {
                lines.add(Component.translatable(GuiText.key("energy", "amount"), String.valueOf(view.energy), String.valueOf(view.energyCapacity)));
            }
        }
        if (!lines.isEmpty()) {
            canvas.tooltip(lines, mouseX, mouseY);
        }
    }

    /** Name of the controller's slot {@code index} (2026-11-11), or {@code null} for the player's slots. */
    @Nullable
    private Component slotName(int index) {
        if (menu.controller() == null || menu.kind() != PortBlockEntity.Kind.ITEM || index >= menu.controller().layout().itemSlots()) {
            return null;
        }
        IoLayout io = menu.controller().layout();
        SlotRole role = io.roleOf(index);
        return Component.translatable(menu.controller().slotName(role, index - io.start(role)));
    }

    @Override
    protected void appendSlotTooltip(Slot slot, List<Component> lines) {
        Component name = slotName(slot.index);
        if (name != null && !name.getString().equals(slot.getItem().getHoverName().getString())) {
            lines.add(name.copy().withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected boolean onClick(double mouseX, double mouseY, int button) {
        double mx = mouseX - leftPos;
        double my = mouseY - topPos;
        MachineView view = menu.view();
        if (modeButton().contains(mx, my)) {
            int current = 0;
            for (int i = 0; i < DIRECTIONS.length; i++) {
                if (DIRECTIONS[i].ordinal() == view.portMode) {
                    current = i;
                }
            }
            int next = Math.floorMod(current + (button == 1 ? -1 : 1), DIRECTIONS.length);
            menu.send(MenuActions.PORT_MODE, DIRECTIONS[next].ordinal(), 0);
            return true;
        }
        if (hasAuto() && autoButton().contains(mx, my)) {
            menu.send(MenuActions.PORT_AUTO, 0, view.portAuto ? 0 : 1);
            return true;
        }
        GuiLayout layout = menu.layout();
        if (layout != null && menu.controller() != null && menu.kind() == PortBlockEntity.Kind.FLUID) {
            for (int i = 0; i < menu.controller().layout().tanks(); i++) {
                GuiLayout.Box vent = layout.vent(i);
                if (vent != null && vent.contains(mx, my)) {
                    int ventMode = i < view.tankVents.length ? view.tankVents[i] : 0;
                    menu.send(MenuActions.VENT, i, VentMode.byIndex(ventMode).cycle(button != 1).ordinal());
                    return true;
                }
                if (layout.tank(i).contains(mx, my)) {
                    if (button == 1 && Platform.isShiftDown()) {
                        long now = System.currentTimeMillis();
                        if (dumpTank == i && now < dumpUntil) {
                            menu.send(MenuActions.TANK_DUMP, i, 0);
                            dumpTank = -1;
                        } else {
                            dumpTank = i;
                            dumpUntil = now + DUMP_CONFIRM_MS;
                        }
                    } else if (!menu.getCarried().isEmpty()) {
                        menu.send(MenuActions.TANK_CLICK, i, 0);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
