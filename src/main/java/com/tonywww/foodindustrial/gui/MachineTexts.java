package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.bio.CultureBenchBlockEntity;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Texts of a machine's state shared by its screen and the Jade overlay (machines/04 §1, §7): the status
 * line and how it reads (good, neutral, a problem).
 */
public final class MachineTexts {
    /** How a line reads; screens and overlays pick their own colours for it. */
    public enum Tone {
        NORMAL, GOOD, INFO, WARNING, BAD
    }

    private MachineTexts() {
    }

    /** The status line: the status, what is missing, fuel or crank for those machines, the plate stage of M30. */
    public static Component status(MachineView view, MachineDefinition definition) {
        MachineStatus status = view.status;
        if (status == MachineStatus.MISSING) {
            MutableComponent conditions = Component.empty();
            String[] keys = view.missing.isEmpty() ? new String[0] : view.missing.split(",");
            for (int i = 0; i < keys.length; i++) {
                if (i > 0) {
                    conditions.append("、");
                }
                conditions.append(keys[i].equals("catalyst") ? Component.translatable(GuiText.key("status", "catalyst"))
                    : keys[i].startsWith("@") ? Component.translatable(keys[i].substring(1))
                    : Component.translatable(Lang.condition(keys[i])));
            }
            return Component.translatable(GuiText.key("status", "missing"), conditions);
        }
        if (status == MachineStatus.NO_POWER && definition.power() == PowerKind.FUEL) {
            return Component.translatable(GuiText.key("status", "no_fuel"));
        }
        if (status == MachineStatus.NO_POWER && definition.power() == PowerKind.MANUAL) {
            return Component.translatable(GuiText.key("status", "no_crank"));
        }
        if (definition.id().equals(CultureBenchBlockEntity.ID) && status == MachineStatus.RUNNING) {
            if (view.plateStage == CultureBenchBlockEntity.Stage.INCUBATING.ordinal()) {
                return Component.translatable(GuiText.key("plate", "incubating"));
            }
            if (view.plateStage == CultureBenchBlockEntity.Stage.IDENTIFYING.ordinal()) {
                return Component.translatable(GuiText.key("plate", "identifying"));
            }
        }
        return Component.translatable(GuiText.key("status", status.id()));
    }

    /** How the status reads: running is good, waiting neutral, a ready plate news, anything else a problem. */
    public static Tone tone(MachineStatus status) {
        return switch (status) {
            case RUNNING, STERILIZING -> Tone.GOOD;
            case WAITING, INOCULATE -> Tone.NORMAL;
            case PLATE -> Tone.INFO;
            default -> Tone.BAD;
        };
    }
}
