package com.tonywww.foodindustrial.machine.definition;

import com.tonywww.foodindustrial.recipe.Capabilities;
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
 */
public record MachineMode(String id, Capabilities capabilities, List<ProcessType> types) {
    public MachineMode {
        types = List.copyOf(types);
        capabilities = capabilities.with(Capabilities.of("mode." + id));
    }

    public static MachineMode of(String id, Capabilities capabilities) {
        return new MachineMode(id, capabilities, List.of());
    }

    public boolean allows(ProcessType type) {
        return types.isEmpty() || types.contains(type);
    }
}
