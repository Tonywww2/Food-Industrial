package com.tonywww.foodindustrial.machine.component;

/** The role letters of the save keys of slots and tanks (machines/03 §1; I47.5). */
final class SlotKeys {
    private SlotKeys() {
    }

    static String code(SlotRole role) {
        return switch (role) {
            case INPUT -> "I";
            case OUTPUT -> "O";
            case CATALYST -> "C";
            case FUEL -> "F";
            case UPGRADE -> "U";
            case MODULE -> "X";
            case ADDITIVE -> "A";
        };
    }
}
