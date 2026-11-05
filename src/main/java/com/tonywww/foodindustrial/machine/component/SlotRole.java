package com.tonywww.foodindustrial.machine.component;

/** Role of an item slot or tank (machines/03 §1). */
public enum SlotRole {
    /** I: recipe input; automation inserts. */
    INPUT,
    /** O: products, main first; automation extracts. */
    OUTPUT,
    /** C: catalysts and cultures; automation inserts; worn by internal count. */
    CATALYST,
    /** F: furnace fuel; automation inserts. */
    FUEL,
    /** U: upgrades; by hand only. */
    UPGRADE,
    /** X: modules; by hand only. */
    MODULE;

    /** Whether automation may insert into slots of this role. */
    public boolean automationInserts() {
        return this == INPUT || this == CATALYST || this == FUEL;
    }

    public boolean automationExtracts() {
        return this == OUTPUT;
    }
}
