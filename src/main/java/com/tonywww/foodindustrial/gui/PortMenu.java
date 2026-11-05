package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.machine.entity.PortBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The small window of a multiblock port (machines/04 §5): the controller's item input and output
 * slots, tanks or energy that the port connects, at the positions of the controller's window; the
 * port's direction (input, output, off) and automatic output switch. Without a formed structure only
 * the settings are shown.
 */
public class PortMenu extends FIMenu {
    private static final int GROUPS = MachineView.PORT | MachineView.POWER | MachineView.TANKS | MachineView.STATUS;

    private final PortBlockEntity.Kind kind;
    private final BlockPos pos;
    @Nullable
    private final PortBlockEntity port;
    /** Controller's definition when the port was attached at opening, else {@code null}. */
    @Nullable
    private final MachineDefinition controller;
    @Nullable
    private final GuiLayout layout;
    private final int portSlots;

    /** Server side. */
    public PortMenu(int containerId, Inventory inventory, PortBlockEntity port) {
        this(containerId, inventory, port.kind(), port.getBlockPos(), port, attached(port),
            attached(port) == null ? null : new MachineContainer(port.controller()));
    }

    /** Client side, from the data {@link #writeOpenData} sent. */
    public PortMenu(int containerId, Inventory inventory, DataReader extra) {
        this(containerId, inventory, PortBlockEntity.Kind.values()[Math.floorMod(extra.getInt("Kind", 0), PortBlockEntity.Kind.values().length)],
            BlockPos.of(extra.getLong("Pos", 0)), null, MachineDefinition.byId(extra.getString("Controller", "")), null);
    }

    private PortMenu(int containerId, Inventory inventory, PortBlockEntity.Kind kind, BlockPos pos, @Nullable PortBlockEntity port,
                     @Nullable MachineDefinition controller, @Nullable Container container) {
        super(ModMenus.PORT.get(), containerId, inventory);
        this.kind = kind;
        this.pos = pos;
        this.port = port;
        this.controller = controller;
        this.layout = controller == null ? null : GuiLayout.of(controller);
        int count = 0;
        if (controller != null && kind == PortBlockEntity.Kind.ITEM) {
            Container slots = container != null ? container : new SimpleContainer(controller.layout().itemSlots());
            int last = controller.layout().itemIn() + controller.layout().itemOut();
            for (int i = 0; i < last; i++) {
                GuiLayout.Box box = layout.slot(i);
                SlotRole role = controller.layout().roleOf(i);
                addSlot(new PortSlot(slots, i, box.x() + 1, box.y() + 1, role));
                count++;
            }
        }
        this.portSlots = count;
        addPlayerInventory(inventory, (layout != null ? layout.height() : GuiLayout.SHORT) - 83);
    }

    @Nullable
    private static MachineDefinition attached(PortBlockEntity port) {
        MultiblockControllerBlockEntity controller = port.controller();
        return controller != null && controller.formed() ? controller.definition() : null;
    }

    public static void writeOpenData(PortBlockEntity port, DataWriter out) {
        out.putInt("Kind", port.kind().ordinal());
        out.putLong("Pos", port.getBlockPos().asLong());
        MachineDefinition controller = attached(port);
        if (controller != null) {
            out.putString("Controller", controller.id());
        }
    }

    public PortBlockEntity.Kind kind() {
        return kind;
    }

    public BlockPos pos() {
        return pos;
    }

    /** The controller's definition, or {@code null} when the port was not attached at opening. */
    @Nullable
    public MachineDefinition controller() {
        return controller;
    }

    /** The controller's window layout, or {@code null}. */
    @Nullable
    public GuiLayout layout() {
        return layout;
    }

    public int height() {
        return layout != null ? layout.height() : GuiLayout.SHORT;
    }

    @Override
    protected int syncGroups() {
        return GROUPS;
    }

    @Override
    @Nullable
    protected MachineView capture(int groups) {
        return port == null ? null : MachineView.of(port, groups);
    }

    @Override
    public boolean stillValid(Player player) {
        return port == null || (!port.isRemoved() && Container.stillValidBlockEntity(port, player));
    }

    private final class PortSlot extends Slot {
        private final SlotRole role;

        PortSlot(Container container, int index, int x, int y, SlotRole role) {
            super(container, index, x, y);
            this.role = role;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (role != SlotRole.INPUT) {
                return false;
            }
            MultiblockControllerBlockEntity attached = port != null ? port.controller() : null;
            ItemStack lock = attached != null ? attached.inventory().lock(getContainerSlot()) : ItemStack.EMPTY;
            return lock.isEmpty() || PlatformItems.sameItemSameData(lock, stack);
        }

        @Override
        public boolean mayPickup(Player player) {
            return port == null || (port.controller() != null && port.controller().formed());
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        boolean moved;
        if (index < portSlots) {
            moved = moveItemStackTo(stack, portSlots, portSlots + 36, true);
        } else {
            int inputs = controller != null && kind == PortBlockEntity.Kind.ITEM ? controller.layout().itemIn() : 0;
            moved = (inputs > 0 && slots.get(0).mayPlace(stack) && moveItemStackTo(stack, 0, inputs, false))
                || moveWithinPlayer(stack, index, portSlots);
        }
        if (!moved) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    protected void handleAction(ServerPlayer sender, int action, int a, int b) {
        if (port == null) {
            return;
        }
        MultiblockControllerBlockEntity attached = port.controller();
        switch (action) {
            case MenuActions.PORT_MODE -> {
                if (a == SideConfig.Mode.OFF.ordinal() || a == SideConfig.Mode.INPUT.ordinal() || a == SideConfig.Mode.OUTPUT.ordinal()) {
                    port.configure(SideConfig.Mode.values()[a], port.autoOutput());
                }
            }
            case MenuActions.PORT_AUTO -> {
                if (kind != PortBlockEntity.Kind.ENERGY) {
                    port.configure(port.mode(), b != 0);
                }
            }
            case MenuActions.TANK_CLICK -> {
                if (attached != null && attached.formed() && kind == PortBlockEntity.Kind.FLUID) {
                    MachineMenu.clickTank(sender, attached.tanks(), a, this);
                    attached.settingsApplied();
                }
            }
            case MenuActions.TANK_DUMP -> {
                if (attached != null && attached.formed() && kind == PortBlockEntity.Kind.FLUID && a >= 0 && a < attached.tanks().size()) {
                    attached.tanks().set(a, FluidAmount.EMPTY);
                    attached.settingsApplied();
                }
            }
            default -> {
            }
        }
    }
}
