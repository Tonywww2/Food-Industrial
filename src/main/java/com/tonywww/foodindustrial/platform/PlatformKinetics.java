package com.tonywww.foodindustrial.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
//? if forge {
import net.minecraftforge.eventbus.api.IEventBus;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
*///?}

/**
 * Rotational power from Create (I25, 2026-11-11): the transmission interface on top of a hand-cranked
 * machine (M01, M02) takes a shaft from above and drives the machine instead of the hand crank. The
 * block exists only when Create is installed (1.20.1 and 1.21.1); elsewhere every query answers 0.
 */
public final class PlatformKinetics {
    /** Block id of the transmission interface. */
    public static final String INTERFACE_ID = "transmission_interface";
    /** Stress units the interface takes per RPM. */
    public static final double STRESS_PER_RPM = 4.0;

    private static BiConsumer<Level, BlockPos> speedListener = (level, pos) -> {
    };

    private PlatformKinetics() {
    }

    private static IEventBus bus;

    /** Called by the mod entrypoint during construction, before the content registers. */
    static void bind(IEventBus modBus) {
        bus = modBus;
    }

    /**
     * Registers the transmission interface when Create is installed (its item goes to the machines tab
     * where this is called). Call during mod construction.
     */
    public static void registerInterface() {
        //? if <26.1 {
        if (Platform.isModLoaded("create")) {
            com.tonywww.foodindustrial.platform.compat.create.CreateTransmission.register(bus);
        }
        //?}
    }

    /** RPM (absolute) of the transmission interface at {@code pos}; 0 without one, when stopped or overstressed. */
    public static float speedAt(Level level, BlockPos pos) {
        //? if <26.1 {
        if (Platform.isModLoaded("create")) {
            return com.tonywww.foodindustrial.platform.compat.create.CreateTransmission.speedAt(level, pos);
        }
        //?}
        return 0;
    }

    /** Sets what runs when the speed of the interface at a position changes (the machine below wakes). */
    public static void onSpeedChanged(BiConsumer<Level, BlockPos> listener) {
        speedListener = listener;
    }

    /** Called by the interface when its speed changes. */
    public static void speedChanged(Level level, BlockPos pos) {
        speedListener.accept(level, pos);
    }
}
