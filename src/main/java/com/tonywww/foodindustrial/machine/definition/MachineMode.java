package com.tonywww.foodindustrial.machine.definition;

import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.Environment;
import com.tonywww.foodindustrial.recipe.ProcessType;

import java.util.List;

/**
 * A machine mode (machines/01 §4): only changes the capabilities, and optionally narrows the recipe
 * types; no new block entity class. Recipes name a mode with {@code "mode"}, which the device must be
 * in ({@code mode.<id>} is part of the mode's capabilities).
 *
 * @param id           mode id, a word of the {@code mode} condition category
 * @param capabilities capabilities while in this mode ({@code mode.<id>} added automatically)
 * @param types        recipe types in this mode; empty = all of the machine's types
 * @param outdoors     the machine also takes the environment of its position in this mode (M09 sun
 *                     crystallization, I14): open sky, weather, daytime, biome, like the T0 passive devices
 * @param needs        capabilities the machine must have to run in this mode at all (I15: air for flash
 *                     drying, combustion, calcining and roasting); without them it waits and shows what
 *                     is missing
 */
public record MachineMode(String id, Capabilities capabilities, List<ProcessType> types, boolean outdoors, Capabilities needs) {
    public MachineMode {
        types = List.copyOf(types);
        capabilities = capabilities.with(Capabilities.of("mode." + id));
    }

    public MachineMode(String id, Capabilities capabilities, List<ProcessType> types, boolean outdoors) {
        this(id, capabilities, types, outdoors, Capabilities.NONE);
    }

    public MachineMode(String id, Capabilities capabilities, List<ProcessType> types) {
        this(id, capabilities, types, false);
    }

    /** This mode, which runs only while the machine has {@code keys} (air from its intake, I15). */
    public MachineMode needing(String... keys) {
        return new MachineMode(id, capabilities, types, outdoors, needs.with(Capabilities.of(keys)));
    }

    public static MachineMode of(String id, Capabilities capabilities) {
        return new MachineMode(id, capabilities, List.of());
    }

    /** A mode limited to {@code types}. */
    public static MachineMode of(String id, Capabilities capabilities, ProcessType... types) {
        return new MachineMode(id, capabilities, List.of(types));
    }

    /** A mode that takes the environment of the machine's position. */
    public static MachineMode outdoors(String id) {
        return new MachineMode(id, Capabilities.NONE, List.of(), true);
    }

    public boolean allows(ProcessType type) {
        return types.isEmpty() || types.contains(type);
    }

    /** Every capability the mode can offer: an outdoors mode can offer every environment value. */
    public Capabilities possibleCapabilities() {
        return outdoors ? capabilities.with(Environment.ALL) : capabilities;
    }
}
