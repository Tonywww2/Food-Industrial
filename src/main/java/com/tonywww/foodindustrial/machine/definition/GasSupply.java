package com.tonywww.foodindustrial.machine.definition;

/**
 * A gas a machine takes from its input tanks to offer a capability (I15, additives/06 §T2 机械化):
 * M14 offers an inert atmosphere while an input tank holds nitrogen. While a recipe that needs the
 * capability runs, {@code amount} mB are used every {@code interval} ticks.
 *
 * @param capability the capability the gas gives ({@code atmosphere.inert})
 * @param fluid      fluid id of the gas ({@code foodindustrial:nitrogen})
 * @param amount     mB used each interval, and the least the tank must hold
 * @param interval   running ticks between two uses
 */
public record GasSupply(String capability, String fluid, int amount, int interval) {
    public GasSupply {
        if (amount <= 0 || interval <= 0) {
            throw new IllegalArgumentException("Gas supply amount and interval must be positive");
        }
    }
}
