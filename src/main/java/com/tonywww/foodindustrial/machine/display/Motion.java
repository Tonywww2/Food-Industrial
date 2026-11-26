package com.tonywww.foodindustrial.machine.display;

import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.recipe.ProcessType;

/**
 * How a machine's work moves on its panel (machines/04 §9.3): one small motion per family of process
 * types, taken from the machine's first type.
 */
public enum Motion {
    /** Nothing moves. */
    NONE,
    /** Grinding: the inputs shake. */
    GRIND,
    /** Pressing and separating: the inputs are squeezed flat again and again. */
    PRESS,
    /** Heating (boiling, drying, kilns, distilling, extracting): steam rises from the inputs. */
    HEAT,
    /** Reacting and mixing: the inputs circle. */
    MIX,
    /** Fermenting and culturing: the inputs swell slowly and bubbles rise. */
    FERMENT;

    public static Motion of(MachineDefinition definition) {
        if (definition.types().isEmpty()) {
            return NONE;
        }
        return of(definition.types().get(0));
    }

    public static Motion of(ProcessType type) {
        return switch (type.id()) {
            case "grinding", "cutting" -> GRIND;
            case "pressing", "separating", "forming", "packaging" -> PRESS;
            case "boiling", "crystallizing", "drying", "fumigating", "kiln", "distilling", "gas_reacting", "extracting",
                 "sterilizing", "cooking", "baking", "frying", "confectionery" -> HEAT;
            case "reacting", "absorbing", "electrolyzing", "purifying", "mixing", "chilling" -> MIX;
            case "fermenting", "solid_fermenting", "culturing", "curing" -> FERMENT;
            default -> NONE;
        };
    }
}
