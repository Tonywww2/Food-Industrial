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

import java.util.ArrayList;
import java.util.List;

/**
 * Port screen (machines/04 §5): the controller's slots, tanks or energy that the port connects, at the
 * positions of the controller's window, and two buttons: direction (input, output, off) and automatic
 * output.
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
        for (Slot slot : menu.slots) {
            Widgets.slot(canvas, Widgets.SlotKind.NORMAL, slot.x - 1, slot.y - 1);
        }
        GuiLayout layout = menu.layout();
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
            }
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
                        if (fluid.isEmpty()) {
                            lines.add(Component.translatable(GuiText.key("tank", "empty")));
                        } else {
                            lines.add(PlatformFluids.displayName(fluid));
                            if (Purity.bears(fluid.fluid())) {
                                Tooltips.purityLine(Purity.materialOf(fluid.fluid()), Purity.get(fluid), lines);
                            }
                        }
                        lines.add(Component.translatable(GuiText.key("tank", "amount"), String.valueOf(fluid.amount()), String.valueOf(view.tankCapacity))
                            .withStyle(ChatFormatting.GRAY));
                        if (i == dumpTank && System.currentTimeMillis() < dumpUntil) {
                            lines.add(Component.translatable(GuiText.key("tank", "dump")).withStyle(ChatFormatting.RED));
                        }
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
