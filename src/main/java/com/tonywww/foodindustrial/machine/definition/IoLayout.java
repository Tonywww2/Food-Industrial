package com.tonywww.foodindustrial.machine.definition;

import com.tonywww.foodindustrial.recipe.DeviceSpec;

/**
 * Slot counts of a machine by role (machines/03 §1, §3): item input (I), item output (O), fluid input
 * (FI), fluid output (FO), catalyst (C), fuel (F), upgrade (U) and module (X).
 */
public record IoLayout(int itemIn, int itemOut, int fluidIn, int fluidOut, int catalyst, int fuel, int upgrades, int modules) {
    public IoLayout {
        if (itemIn < 0 || itemOut < 0 || fluidIn < 0 || fluidOut < 0 || catalyst < 0 || fuel < 0 || upgrades < 0 || modules < 0) {
            throw new IllegalArgumentException("Negative slot count");
        }
    }

    /** Item and fluid slots only. */
    public static IoLayout of(int itemIn, int itemOut, int fluidIn, int fluidOut) {
        return new IoLayout(itemIn, itemOut, fluidIn, fluidOut, 0, 0, 0, 0);
    }

    public IoLayout withCatalyst(int count) {
        return new IoLayout(itemIn, itemOut, fluidIn, fluidOut, count, fuel, upgrades, modules);
    }

    public IoLayout withFuel(int count) {
        return new IoLayout(itemIn, itemOut, fluidIn, fluidOut, catalyst, count, upgrades, modules);
    }

    public IoLayout withUpgrades(int count) {
        return new IoLayout(itemIn, itemOut, fluidIn, fluidOut, catalyst, fuel, count, modules);
    }

    public IoLayout withModules(int count) {
        return new IoLayout(itemIn, itemOut, fluidIn, fluidOut, catalyst, fuel, upgrades, count);
    }

    /** Role of inventory slot {@code slot} (roles in the order I, O, C, F, U, X). */
    public com.tonywww.foodindustrial.machine.component.SlotRole roleOf(int slot) {
        int[] sizes = {itemIn, itemOut, catalyst, fuel, upgrades, modules};
        com.tonywww.foodindustrial.machine.component.SlotRole[] roles = com.tonywww.foodindustrial.machine.component.SlotRole.values();
        int end = 0;
        for (int i = 0; i < sizes.length; i++) {
            end += sizes[i];
            if (slot < end) {
                return roles[i];
            }
        }
        throw new IndexOutOfBoundsException("Slot " + slot + " of " + itemSlots());
    }

    /** First inventory slot of {@code role}. */
    public int start(com.tonywww.foodindustrial.machine.component.SlotRole role) {
        int[] sizes = {itemIn, itemOut, catalyst, fuel, upgrades, modules};
        int start = 0;
        for (int i = 0; i < role.ordinal(); i++) {
            start += sizes[i];
        }
        return start;
    }

    /** Item slots of all roles. */
    public int itemSlots() {
        return itemIn + itemOut + catalyst + fuel + upgrades + modules;
    }

    public int tanks() {
        return fluidIn + fluidOut;
    }

    /** Slot counts the recipe framework checks recipes against. */
    public DeviceSpec.Slots deviceSlots() {
        return new DeviceSpec.Slots(itemIn, itemOut, fluidIn, fluidOut, catalyst);
    }
}
