package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.component.RedstoneControl;
import com.tonywww.foodindustrial.machine.component.SideConfig;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.component.TankSet;
import com.tonywww.foodindustrial.machine.component.VentMode;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineModule;
import com.tonywww.foodindustrial.machine.definition.Upgrade;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.platform.PlatformFuel;
import com.tonywww.foodindustrial.platform.PlatformItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

/**
 * The menu every machine and multiblock controller opens (machines/04 §1–§5): the machine's item
 * slots at the positions of its {@link GuiLayout} (upgrade and module slots in the upgrades panel,
 * shown only while it is open), the player's inventory, the synced {@link MachineView}, and the
 * server side of every setting.
 */
public class MachineMenu extends FIMenu {
    private static final int BASE_GROUPS = MachineView.STATUS | MachineView.POWER | MachineView.PROGRESS | MachineView.TANKS
        | MachineView.LOCKS | MachineView.CATALYST | MachineView.CONFIG | MachineView.UPGRADES;

    private final MachineDefinition definition;
    private final BlockPos pos;
    @Nullable
    private final MachineBlockEntity machine;
    private final GuiLayout layout;
    private final int machineSlots;
    private boolean upgradesOpen;
    private boolean structurePage;

    /** Server side. */
    public MachineMenu(int containerId, Inventory inventory, MachineBlockEntity machine) {
        this(containerId, inventory, machine.definition(), machine.getBlockPos(), machine, new MachineContainer(machine));
    }

    /** Client side, from the data {@link #writeOpenData} sent. */
    public MachineMenu(int containerId, Inventory inventory, DataReader extra) {
        this(containerId, inventory, definition(extra), BlockPos.of(extra.getLong("Pos", 0)), null,
            new SimpleContainer(definition(extra).layout().itemSlots()));
    }

    private MachineMenu(int containerId, Inventory inventory, MachineDefinition definition, BlockPos pos,
                        @Nullable MachineBlockEntity machine, Container container) {
        super(ModMenus.MACHINE.get(), containerId, inventory);
        this.definition = definition;
        this.pos = pos;
        this.machine = machine;
        this.layout = GuiLayout.of(definition);
        this.machineSlots = definition.layout().itemSlots();
        for (int i = 0; i < machineSlots; i++) {
            GuiLayout.Box box = layout.slot(i);
            addSlot(new MachineSlot(container, i, box.x() + 1, box.y() + 1, definition.layout().roleOf(i)));
        }
        addPlayerInventory(inventory, layout.playerInventoryY());
    }

    private static MachineDefinition definition(DataReader extra) {
        MachineDefinition definition = MachineDefinition.byId(extra.getString("Machine", ""));
        if (definition == null) {
            throw new IllegalStateException("Unknown machine " + extra.getString("Machine", ""));
        }
        return definition;
    }

    /** What the client needs to build the menu. */
    public static void writeOpenData(MachineBlockEntity machine, DataWriter out) {
        out.putString("Machine", machine.definition().id());
        out.putLong("Pos", machine.getBlockPos().asLong());
    }

    public MachineDefinition definition() {
        return definition;
    }

    public BlockPos pos() {
        return pos;
    }

    public GuiLayout layout() {
        return layout;
    }

    /** Number of machine slots (the player's inventory follows). */
    public int machineSlots() {
        return machineSlots;
    }

    /** Client: the upgrades panel is open, so its slots take clicks. */
    public void setUpgradesOpen(boolean open) {
        upgradesOpen = open;
    }

    /** Client: the structure page of a multiblock hides the machine's slots. */
    public void setStructurePage(boolean page) {
        structurePage = page;
    }

    @Override
    protected int syncGroups() {
        return definition.isMultiblock() ? BASE_GROUPS | MachineView.STRUCTURE : BASE_GROUPS;
    }

    @Override
    @Nullable
    protected MachineView capture(int groups) {
        return machine == null ? null : MachineView.of(machine, groups);
    }

    @Override
    public boolean stillValid(Player player) {
        return machine == null || (!machine.isRemoved() && Container.stillValidBlockEntity(machine, player));
    }

    // ---- slots ----

    private boolean mayPlace(int slot, SlotRole role, ItemStack stack) {
        return switch (role) {
            case INPUT -> {
                ItemStack lock = machine != null ? machine.inventory().lock(slot)
                    : slot < view().slotLocks.length ? view().slotLocks[slot] : ItemStack.EMPTY;
                yield lock.isEmpty() || PlatformItems.sameItemSameData(lock, stack);
            }
            case OUTPUT -> false;
            case CATALYST -> true;
            case FUEL -> PlatformFuel.burnTime(player.level(), stack) > 0;
            case UPGRADE -> Upgrade.of(stack.getItem()) != null;
            case MODULE -> {
                MachineModule module = MachineModule.of(stack.getItem());
                yield module != null && definition.modules().contains(module);
            }
        };
    }

    private boolean active(SlotRole role) {
        if (machine != null) {
            return true;
        }
        if (structurePage) {
            return false;
        }
        return (role != SlotRole.UPGRADE && role != SlotRole.MODULE) || upgradesOpen;
    }

    private final class MachineSlot extends Slot {
        private final SlotRole role;

        MachineSlot(Container container, int index, int x, int y, SlotRole role) {
            super(container, index, x, y);
            this.role = role;
        }

        public SlotRole role() {
            return role;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return MachineMenu.this.mayPlace(getContainerSlot(), role, stack);
        }

        @Override
        public int getMaxStackSize() {
            return role == SlotRole.UPGRADE || role == SlotRole.MODULE ? 1 : 64;
        }

        @Override
        public boolean isActive() {
            return active(role);
        }
    }

    /** Role of menu slot {@code index}, or {@code null} for the player's slots. */
    @Nullable
    public SlotRole roleOf(int index) {
        return index < machineSlots && slots.get(index) instanceof MachineSlot slot ? slot.role() : null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < machineSlots) {
            if (!moveItemStackTo(stack, machineSlots, machineSlots + 36, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveIntoMachine(stack) && !moveWithinPlayer(stack, index, machineSlots)) {
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

    private boolean moveIntoMachine(ItemStack stack) {
        boolean moved = false;
        for (SlotRole role : new SlotRole[]{SlotRole.INPUT, SlotRole.CATALYST, SlotRole.FUEL, SlotRole.UPGRADE, SlotRole.MODULE}) {
            int start = definition.layout().start(role);
            int count = countOf(role);
            if (count > 0 && mayPlace(start, role, stack)) {
                moved |= moveItemStackTo(stack, start, start + count, false);
                if (stack.isEmpty()) {
                    return true;
                }
            }
        }
        return moved;
    }

    private int countOf(SlotRole role) {
        return switch (role) {
            case INPUT -> definition.layout().itemIn();
            case OUTPUT -> definition.layout().itemOut();
            case CATALYST -> definition.layout().catalyst();
            case FUEL -> definition.layout().fuel();
            case UPGRADE -> definition.layout().upgrades();
            case MODULE -> definition.layout().modules();
        };
    }

    // ---- actions (server) ----

    @Override
    protected void handleAction(ServerPlayer sender, int action, int a, int b) {
        if (machine == null) {
            return;
        }
        SideConfig sides = machine.sides();
        SideConfig.Face[] faces = SideConfig.Face.values();
        boolean faceOk = a >= 0 && a < faces.length && !definition.isMultiblock();
        switch (action) {
            case MenuActions.SIDE -> {
                int mode = b & 15;
                if (faceOk && mode < SideConfig.Mode.values().length) {
                    if ((b >> 4) == 0) {
                        sides.setItems(faces[a], SideConfig.Mode.values()[mode]);
                    } else {
                        sides.setFluids(faces[a], SideConfig.Mode.values()[mode]);
                    }
                    machine.sidesChanged();
                }
            }
            case MenuActions.SCOPE_ITEMS -> {
                if (faceOk && b >= 0 && b < SideConfig.OutputScope.values().length) {
                    sides.setItemScope(faces[a], SideConfig.OutputScope.values()[b]);
                    machine.sidesChanged();
                }
            }
            case MenuActions.SCOPE_FLUID_OUT -> {
                if (faceOk && b >= SideConfig.ALL_TANKS && b < machine.tanks().outputs()) {
                    sides.setFluidOutputTank(faces[a], b);
                    machine.sidesChanged();
                }
            }
            case MenuActions.SCOPE_FLUID_IN -> {
                if (faceOk && b >= SideConfig.ALL_TANKS && b < machine.tanks().inputs()) {
                    sides.setFluidInputTank(faces[a], b);
                    machine.sidesChanged();
                }
            }
            case MenuActions.AUTO -> {
                if (a >= 0 && a < 4 && !definition.isMultiblock()) {
                    sides.setAuto(a < 2, (a & 1) == 1, b != 0);
                    machine.sidesChanged();
                }
            }
            case MenuActions.REDSTONE -> {
                if (a >= 0 && a < RedstoneControl.values().length) {
                    machine.setRedstone(RedstoneControl.values()[a]);
                }
            }
            case MenuActions.MODE -> {
                if (a >= 0 && a < definition.modes().size()) {
                    machine.setMode(a);
                }
            }
            case MenuActions.LOCK_SLOT -> {
                if (a >= 0 && a < machineSlots) {
                    machine.toggleSlotLock(a, getCarried());
                }
            }
            case MenuActions.LOCK_TANK -> {
                if (a >= 0 && a < machine.tanks().size()) {
                    machine.toggleTankLock(a, PlatformFluidContainers.contained(getCarried()));
                }
            }
            case MenuActions.TANK_CLICK -> clickTank(sender, machine.tanks(), a);
            case MenuActions.VENT -> {
                if (a >= machine.tanks().inputs() && a < machine.tanks().size() && b >= 0 && b < VentMode.values().length) {
                    machine.setVent(a, VentMode.values()[b]);
                }
            }
            case MenuActions.TANK_DUMP -> {
                if (a >= 0 && a < machine.tanks().size()) {
                    machine.tanks().set(a, FluidAmount.EMPTY);
                }
            }
            default -> {
                return;
            }
        }
        machine.settingsApplied();
    }

    /**
     * A click with a fluid container on a tank (machines/04 §1): a full container pours into an input
     * tank that takes its fluid; an empty one is filled from any tank.
     */
    static void clickTank(Player player, TankSet tanks, int tank, FIMenu menu) {
        if (tank < 0 || tank >= tanks.size()) {
            return;
        }
        ItemStack carried = menu.getCarried();
        if (carried.isEmpty()) {
            return;
        }
        FluidAmount held = PlatformFluidContainers.contained(carried);
        if (!held.isEmpty()) {
            if (tanks.role(tank) == SlotRole.INPUT && tanks.allows(tank, held) && tanks.space(tank, held) >= held.amount()) {
                ItemStack emptied = PlatformFluidContainers.drained(carried);
                if (!emptied.isEmpty() || carried.getCount() == 1) {
                    tanks.set(tank, tanks.joined(tank, held, held.amount()));
                    menu.replaceOneCarried(player, emptied);
                }
            }
            return;
        }
        FluidAmount current = tanks.get(tank);
        if (current.amount() >= 1000) {
            ItemStack filled = PlatformFluidContainers.filled(carried, current.withAmount(1000));
            if (!filled.isEmpty()) {
                tanks.drain(tank, 1000);
                menu.replaceOneCarried(player, filled);
            }
        }
    }

    private void clickTank(ServerPlayer sender, TankSet tanks, int tank) {
        clickTank(sender, tanks, tank, this);
    }
}
