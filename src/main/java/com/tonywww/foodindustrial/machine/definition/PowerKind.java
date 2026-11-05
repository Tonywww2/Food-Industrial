package com.tonywww.foodindustrial.machine.definition;

/** How a machine is powered (machines/03 §1). */
public enum PowerKind {
    /** No power: passive devices and machines driven by time alone. */
    NONE,
    /** Furnace fuel in the fuel slot (T1 heating machines). */
    FUEL,
    /** FE from any face that is not closed (T2 and up). */
    ENERGY
}
