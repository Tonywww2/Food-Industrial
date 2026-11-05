package com.tonywww.foodindustrial.machine.component;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import net.minecraft.core.Direction;

/**
 * Six-face configuration (machines/04 §3): each face has an item and a fluid channel that is off,
 * input, output or both, with the advanced scopes of §3.2 (which output slots or tank a face serves);
 * plus automatic input and output switches per channel. Faces are relative to
 * the machine's facing, so copying a configuration to a machine facing elsewhere keeps its meaning.
 * Energy and air enter through any face that is not closed (both channels off).
 */
public final class SideConfig {
    /** Faces relative to the machine's front. */
    public enum Face {
        FRONT, BACK, LEFT, RIGHT, TOP, BOTTOM;

        /** The absolute direction of this face for a machine facing {@code facing} (horizontal). */
        public Direction toDirection(Direction facing) {
            return switch (this) {
                case FRONT -> facing;
                case BACK -> facing.getOpposite();
                case LEFT -> facing.getClockWise();
                case RIGHT -> facing.getCounterClockWise();
                case TOP -> Direction.UP;
                case BOTTOM -> Direction.DOWN;
            };
        }

        /** The relative face looking in {@code side} for a machine facing {@code facing}. */
        public static Face of(Direction side, Direction facing) {
            if (side == Direction.UP) {
                return TOP;
            }
            if (side == Direction.DOWN) {
                return BOTTOM;
            }
            if (side == facing) {
                return FRONT;
            }
            if (side == facing.getOpposite()) {
                return BACK;
            }
            return side == facing.getClockWise() ? LEFT : RIGHT;
        }
    }

    /** State of one channel of one face. */
    public enum Mode {
        OFF, INPUT, OUTPUT, BOTH;

        public boolean inputs() {
            return this == INPUT || this == BOTH;
        }

        public boolean outputs() {
            return this == OUTPUT || this == BOTH;
        }

        /** Next state when cycling forwards (left click) or backwards. */
        public Mode cycle(boolean forwards) {
            Mode[] values = values();
            return values[Math.floorMod(ordinal() + (forwards ? 1 : -1), values.length)];
        }
    }

    /** Output slots an item output face serves (machines/04 §3.2). */
    public enum OutputScope {
        ALL, MAIN, BYPRODUCT;

        public OutputScope cycle(boolean forwards) {
            OutputScope[] values = values();
            return values[Math.floorMod(ordinal() + (forwards ? 1 : -1), values.length)];
        }
    }

    /** Value of a fluid scope that serves every tank of its role. */
    public static final int ALL_TANKS = -1;

    private static final Mode[] DEFAULT_ITEMS = {Mode.OFF, Mode.OFF, Mode.OFF, Mode.OFF, Mode.INPUT, Mode.OUTPUT};
    private static final Mode[] DEFAULT_FLUIDS = {Mode.OFF, Mode.OUTPUT, Mode.INPUT, Mode.INPUT, Mode.OFF, Mode.OFF};

    private final Mode[] items = DEFAULT_ITEMS.clone();
    private final Mode[] fluids = DEFAULT_FLUIDS.clone();
    private final OutputScope[] itemScopes = {OutputScope.ALL, OutputScope.ALL, OutputScope.ALL, OutputScope.ALL, OutputScope.ALL, OutputScope.ALL};
    private final int[] fluidOutputTanks = {ALL_TANKS, ALL_TANKS, ALL_TANKS, ALL_TANKS, ALL_TANKS, ALL_TANKS};
    private final int[] fluidInputTanks = {ALL_TANKS, ALL_TANKS, ALL_TANKS, ALL_TANKS, ALL_TANKS, ALL_TANKS};
    private boolean autoInputItems;
    private boolean autoInputFluids;
    private boolean autoOutputItems;
    private boolean autoOutputFluids;

    public Mode items(Face face) {
        return items[face.ordinal()];
    }

    public Mode fluids(Face face) {
        return fluids[face.ordinal()];
    }

    public void setItems(Face face, Mode mode) {
        items[face.ordinal()] = mode;
    }

    public void setFluids(Face face, Mode mode) {
        fluids[face.ordinal()] = mode;
    }

    public OutputScope itemScope(Face face) {
        return itemScopes[face.ordinal()];
    }

    public void setItemScope(Face face, OutputScope scope) {
        itemScopes[face.ordinal()] = scope;
    }

    /** Output tank (0-based among the output tanks) a fluid output face serves, or {@link #ALL_TANKS}. */
    public int fluidOutputTank(Face face) {
        return fluidOutputTanks[face.ordinal()];
    }

    public void setFluidOutputTank(Face face, int tank) {
        fluidOutputTanks[face.ordinal()] = Math.max(ALL_TANKS, tank);
    }

    /** Input tank (0-based among the input tanks) a fluid input face fills, or {@link #ALL_TANKS}. */
    public int fluidInputTank(Face face) {
        return fluidInputTanks[face.ordinal()];
    }

    public void setFluidInputTank(Face face, int tank) {
        fluidInputTanks[face.ordinal()] = Math.max(ALL_TANKS, tank);
    }

    /** Resets tank scopes that point past the machine's tanks. */
    public void clampTanks(int inputTanks, int outputTanks) {
        for (int i = 0; i < 6; i++) {
            if (fluidInputTanks[i] >= inputTanks) {
                fluidInputTanks[i] = ALL_TANKS;
            }
            if (fluidOutputTanks[i] >= outputTanks) {
                fluidOutputTanks[i] = ALL_TANKS;
            }
        }
    }

    /** Whether the face is closed: energy and air do not enter. */
    public boolean closed(Face face) {
        return items[face.ordinal()] == Mode.OFF && fluids[face.ordinal()] == Mode.OFF;
    }

    public boolean autoInputItems() {
        return autoInputItems;
    }

    public boolean autoInputFluids() {
        return autoInputFluids;
    }

    public boolean autoOutputItems() {
        return autoOutputItems;
    }

    public boolean autoOutputFluids() {
        return autoOutputFluids;
    }

    public void setAuto(boolean input, boolean fluid, boolean on) {
        if (input) {
            if (fluid) {
                autoInputFluids = on;
            } else {
                autoInputItems = on;
            }
        } else if (fluid) {
            autoOutputFluids = on;
        } else {
            autoOutputItems = on;
        }
    }

    /** Whether everything is at its default (machine items without data stay stackable). */
    public boolean isDefault() {
        return java.util.Arrays.equals(items, DEFAULT_ITEMS) && java.util.Arrays.equals(fluids, DEFAULT_FLUIDS)
            && !autoInputItems && !autoInputFluids && !autoOutputItems && !autoOutputFluids && defaultScopes();
    }

    private boolean defaultScopes() {
        for (int i = 0; i < 6; i++) {
            if (itemScopes[i] != OutputScope.ALL || fluidOutputTanks[i] != ALL_TANKS || fluidInputTanks[i] != ALL_TANKS) {
                return false;
            }
        }
        return true;
    }

    public void copyFrom(SideConfig other) {
        System.arraycopy(other.items, 0, items, 0, items.length);
        System.arraycopy(other.fluids, 0, fluids, 0, fluids.length);
        System.arraycopy(other.itemScopes, 0, itemScopes, 0, 6);
        System.arraycopy(other.fluidOutputTanks, 0, fluidOutputTanks, 0, 6);
        System.arraycopy(other.fluidInputTanks, 0, fluidInputTanks, 0, 6);
        autoInputItems = other.autoInputItems;
        autoInputFluids = other.autoInputFluids;
        autoOutputItems = other.autoOutputItems;
        autoOutputFluids = other.autoOutputFluids;
    }

    public void save(DataWriter out) {
        if (isDefault()) {
            return;
        }
        int[] packed = new int[2];
        for (int i = 0; i < 6; i++) {
            packed[0] |= items[i].ordinal() << (i * 2);
            packed[1] |= fluids[i].ordinal() << (i * 2);
        }
        out.putIntArray("Sides", packed);
        out.putByte("Auto", (byte) ((autoInputItems ? 1 : 0) | (autoInputFluids ? 2 : 0) | (autoOutputItems ? 4 : 0) | (autoOutputFluids ? 8 : 0)));
        if (!defaultScopes()) {
            int[] scopes = new int[6];
            for (int i = 0; i < 6; i++) {
                scopes[i] = itemScopes[i].ordinal() | (fluidOutputTanks[i] + 1) << 2 | (fluidInputTanks[i] + 1) << 8;
            }
            out.putIntArray("Scopes", scopes);
        }
    }

    public void load(DataReader in) {
        int[] packed = in.getIntArray("Sides");
        if (packed.length == 2) {
            for (int i = 0; i < 6; i++) {
                items[i] = Mode.values()[(packed[0] >> (i * 2)) & 3];
                fluids[i] = Mode.values()[(packed[1] >> (i * 2)) & 3];
            }
        } else {
            System.arraycopy(DEFAULT_ITEMS, 0, items, 0, 6);
            System.arraycopy(DEFAULT_FLUIDS, 0, fluids, 0, 6);
        }
        int auto = in.getByte("Auto", (byte) 0);
        autoInputItems = (auto & 1) != 0;
        autoInputFluids = (auto & 2) != 0;
        autoOutputItems = (auto & 4) != 0;
        autoOutputFluids = (auto & 8) != 0;
        int[] scopes = in.getIntArray("Scopes");
        for (int i = 0; i < 6; i++) {
            int scope = i < scopes.length ? scopes[i] : 0;
            itemScopes[i] = OutputScope.values()[Math.min(OutputScope.values().length - 1, scope & 3)];
            fluidOutputTanks[i] = ((scope >> 2) & 63) - 1;
            fluidInputTanks[i] = ((scope >> 8) & 63) - 1;
        }
    }
}
