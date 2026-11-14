package com.tonywww.foodindustrial.integration;

import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;

import java.util.List;
import java.util.Set;

/**
 * A machine of another mod that runs some of this mod's recipes (additives/06 §他模组机器参与, I25), described
 * like a device: the recipe types it takes, the conditions it meets and the inputs and outputs its recipe
 * format holds.
 *
 * @param mod             the integration
 * @param type            the machine's recipe type, e.g. {@code create:milling}
 * @param types           this mod's recipe types it runs
 * @param capabilities    conditions it meets ({@code temperature.heated}, {@code mode.concentration}, {@code method.centrifuge}, …)
 * @param anyOf           the recipe must require one of these (empty: no such need)
 * @param items           allowed number of item inputs
 * @param maxCount        largest count of one item input
 * @param fluids          allowed number of fluid inputs
 * @param itemOutputs     allowed number of item outputs
 * @param fluidOutputs    allowed number of fluid outputs
 * @param chances         outputs may have a chance below 1
 * @param purityInputs    item inputs may be purity ingredients (the machine reads standard ingredients)
 * @param fluidPurity     fluid outputs can carry purity
 */
public record ExternalMachine(Integrations.Mod mod, String type, Set<ProcessType> types, Capabilities capabilities, Set<String> anyOf,
                              Range items, int maxCount, Range fluids, Range itemOutputs, Range fluidOutputs,
                              boolean chances, boolean purityInputs, boolean fluidPurity) {
    /** Inclusive range of a count. */
    public record Range(int min, int max) {
        public boolean contains(int value) {
            return value >= min && value <= max;
        }
    }

    private static Range r(int min, int max) {
        return new Range(min, max);
    }

    private static final Range NONE = r(0, 0);

    /** The machines (additives/06 table; 2026-11-11). */
    public static final List<ExternalMachine> ALL = List.of(
        // Create, T1: millstone and crushing wheels grind; the press on a basin presses; the mixer on a
        // basin mixes, and over a heated basin concentrates (P11, P28).
        new ExternalMachine(Integrations.CREATE, "create:milling", Set.of(ProcessTypes.GRINDING), Capabilities.NONE, Set.of(),
            r(1, 1), 1, NONE, r(1, 4), NONE, true, true, true),
        new ExternalMachine(Integrations.CREATE, "create:crushing", Set.of(ProcessTypes.GRINDING), Capabilities.NONE, Set.of(),
            r(1, 1), 1, NONE, r(1, 4), NONE, true, true, true),
        new ExternalMachine(Integrations.CREATE, "create:compacting", Set.of(ProcessTypes.PRESSING), Capabilities.NONE, Set.of(),
            r(1, 9), 9, r(0, 2), r(0, 4), r(0, 2), true, true, true),
        new ExternalMachine(Integrations.CREATE, "create:mixing", Set.of(ProcessTypes.MIXING, ProcessTypes.BOILING),
            Capabilities.of("temperature.heated", "mode.blending", "mode.concentration"), Set.of(),
            r(0, 9), 9, r(0, 2), r(0, 4), r(0, 2), true, true, true),
        // Thermal Expansion, T2.
        new ExternalMachine(Integrations.THERMAL, "thermal:pulverizer", Set.of(ProcessTypes.GRINDING), Capabilities.NONE, Set.of(),
            r(1, 1), 64, NONE, r(1, 4), NONE, true, true, true),
        new ExternalMachine(Integrations.THERMAL, "thermal:press", Set.of(ProcessTypes.PRESSING), Capabilities.NONE, Set.of(),
            r(1, 1), 64, NONE, r(0, 1), r(0, 1), true, true, true),
        new ExternalMachine(Integrations.THERMAL, "thermal:centrifuge", Set.of(ProcessTypes.SEPARATING, ProcessTypes.PRESSING),
            Capabilities.of("method.centrifuge"), Set.of(), r(1, 1), 64, NONE, r(0, 4), r(0, 1), true, true, true),
        new ExternalMachine(Integrations.THERMAL, "thermal:crystallizer", Set.of(ProcessTypes.CRYSTALLIZING),
            Capabilities.of("temperature.heated", "temperature.cooling", "mode.evaporative_crystallization"), Set.of(),
            r(1, 2), 64, r(1, 1), r(1, 1), NONE, false, true, true), // Thermal skips crystallizer recipes without an item
        new ExternalMachine(Integrations.THERMAL, "thermal:refinery", Set.of(ProcessTypes.DISTILLING, ProcessTypes.BOILING),
            Capabilities.of("temperature.heated", "method.multi_fraction", "mode.concentration"), Set.of(),
            NONE, 0, r(1, 1), r(0, 1), r(1, 3), true, true, true),
        new ExternalMachine(Integrations.THERMAL, "thermal:pyrolyzer", Set.of(ProcessTypes.KILN),
            Capabilities.of("temperature.heated", "mode.dry_distillation"), Set.of("mode.dry_distillation"),
            r(1, 1), 64, NONE, r(0, 3), r(0, 1), true, true, true),
        // Mekanism, T2.
        new ExternalMachine(Integrations.MEKANISM, "mekanism:crushing", Set.of(ProcessTypes.GRINDING), Capabilities.NONE, Set.of(),
            r(1, 1), 64, NONE, r(1, 1), NONE, false, true, true),
        new ExternalMachine(Integrations.MEKANISM, "mekanism:enriching", Set.of(ProcessTypes.GRINDING), Capabilities.NONE, Set.of(),
            r(1, 1), 64, NONE, r(1, 1), NONE, false, true, true),
        new ExternalMachine(Integrations.MEKANISM, "mekanism:evaporating", Set.of(ProcessTypes.BOILING),
            Capabilities.of("temperature.heated", "mode.concentration"), Set.of(), NONE, 0, r(1, 1), NONE, r(1, 1), false, true, true),
        // PneumaticCraft: Repressurized, T2.
        new ExternalMachine(Integrations.PNEUMATICCRAFT, "pneumaticcraft:pressure_chamber", Set.of(ProcessTypes.PRESSING), Capabilities.NONE,
            Set.of(), r(1, 4), 64, NONE, r(1, 4), NONE, false, true, true),
        new ExternalMachine(Integrations.PNEUMATICCRAFT, "pneumaticcraft:thermo_plant", Set.of(ProcessTypes.BOILING),
            Capabilities.of("temperature.heated", "mode.concentration"), Set.of(), r(0, 1), 1, r(0, 1), r(0, 1), r(0, 1), false, true, true),
        new ExternalMachine(Integrations.PNEUMATICCRAFT, "pneumaticcraft:heat_frame_cooling", Set.of(ProcessTypes.CRYSTALLIZING),
            Capabilities.of("temperature.cooling"), Set.of("temperature.cooling"), NONE, 0, r(1, 1), r(1, 1), NONE, false, true, true),
        new ExternalMachine(Integrations.PNEUMATICCRAFT, "pneumaticcraft:refinery", Set.of(ProcessTypes.DISTILLING),
            Capabilities.of("temperature.heated", "method.multi_fraction"), Set.of(), NONE, 0, r(1, 1), NONE, r(2, 4), false, true, true),
        // NuclearCraft (1.20.1), T2: fluid outputs carry no purity, so only recipes whose fluid products bear none.
        new ExternalMachine(Integrations.NUCLEARCRAFT, "nuclearcraft:rock_crusher", Set.of(ProcessTypes.GRINDING), Capabilities.NONE, Set.of(),
            r(1, 1), 64, NONE, r(1, 3), NONE, false, true, false),
        new ExternalMachine(Integrations.NUCLEARCRAFT, "nuclearcraft:crystallizer", Set.of(ProcessTypes.CRYSTALLIZING),
            Capabilities.of("temperature.heated", "temperature.cooling", "mode.evaporative_crystallization"), Set.of(),
            NONE, 0, r(1, 1), r(1, 1), NONE, false, true, false),
        new ExternalMachine(Integrations.NUCLEARCRAFT, "nuclearcraft:electrolyzer", Set.of(ProcessTypes.ELECTROLYZING),
            Capabilities.of("mode.water_electrolysis", "mode.chlor_alkali"), Set.of(), NONE, 0, r(1, 1), NONE, r(1, 4), false, true, false)
    );

    /** The part after the colon of {@link #type}. */
    public String path() {
        return type.substring(type.indexOf(':') + 1);
    }
}
