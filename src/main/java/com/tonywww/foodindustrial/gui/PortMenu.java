package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.component.VentMode;
import com.tonywww.foodindustrial.machine.definition.IoLayout;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.machine.entity.PortBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformFuel;
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
 * The small window of a multiblock port (machines/04 §5): the controller's item slots (inputs, products,
 * catalysts and fuel), tanks with the venting switches, or energy that the port connects, at the
 * positions a single-block window has them; the port's direction (input, output, off) and automatic
 * output switch. The controller's own window shows no slots or tanks, so contents are handled here
 * (2026-11-10). Without a formed structure only the settings are shown.
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
        this.layout = controller == null ? null : GuiLayout.full(controller);
        int count = 0;
        if (controller != null && kind == PortBlockEntity.Kind.ITEM) {
            Container slots = container != null ? container : new SimpleContainer(controller.layout().itemSlots());
            IoLayout io = controller.layout();
            int last = io.itemIn() + io.itemOut() + io.catalyst() + io.fuel();
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
            MachineDefinition controller = controller();
            if (controller != null && !controller.slotAccepts(role, getContainerSlot() - controller.layout().start(role), stack)) {
                return false;
            }
            return switch (role) {
                case INPUT -> {
                    MultiblockControllerBlockEntity attached = port != null ? port.controller() : null;
                    ItemStack lock = attached != null ? attached.inventory().lock(getContainerSlot()) : ItemStack.EMPTY;
                    yield lock.isEmpty() || PlatformItems.sameItemSameData(lock, stack);
                }
                case CATALYST -> true;
                case FUEL -> PlatformFuel.burnTime(player.level(), stack) > 0;
                default -> false;
            };
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
            moved = moveIntoPort(stack) || moveWithinPlayer(stack, index, portSlots);
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

    /**
     * Shift-click from the player's inventory: fuel that no recipe of the machine uses goes only to the
     * fuel slots (the server decides); anything else to the inputs, then the catalyst and fuel slots.
     */
    private boolean moveIntoPort(ItemStack stack) {
        if (controller == null || kind != PortBlockEntity.Kind.ITEM) {
            return false;
        }
        MultiblockControllerBlockEntity attached = port != null ? port.controller() : null;
        SlotRole[] order = attached != null && attached.fuelOnly(stack)
            ? new SlotRole[]{SlotRole.FUEL}
            : new SlotRole[]{SlotRole.INPUT, SlotRole.CATALYST, SlotRole.FUEL};
        IoLayout io = controller.layout();
        boolean moved = false;
        for (SlotRole role : order) {
            int start = io.start(role);
            int count = switch (role) {
                case INPUT -> io.itemIn();
                case CATALYST -> io.catalyst();
                case FUEL -> io.fuel();
                default -> 0;
            };
            if (count > 0 && slots.get(start).mayPlace(stack)) {
                moved |= moveItemStackTo(stack, start, start + count, false);
                if (stack.isEmpty()) {
                    return true;
                }
            }
        }
        return moved;
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
            case MenuActions.VENT -> {
                if (attached != null && attached.formed() && kind == PortBlockEntity.Kind.FLUID && a >= attached.tanks().inputs()
                    && a < attached.tanks().size() && b >= 0 && b < VentMode.values().length) {
                    attached.setVent(a, VentMode.values()[b]);
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
