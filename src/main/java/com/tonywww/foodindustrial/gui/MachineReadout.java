package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.ConditionVocabulary;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.bio.SterileFermenterBlockEntity;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.manual.DeviceContents;
import com.tonywww.foodindustrial.manual.T0DeviceBlockEntity;
import com.tonywww.foodindustrial.manual.T0Devices;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * What an in-world overlay (Jade, 2026-11-11) shows for a machine or a T0 device beyond its storage:
 * the status line, the states of the conditions the device's recipes care about (temperature, pressure,
 * atmosphere, surroundings, sterility), fuel or crank, catalyst uses, and the progress bars. The server
 * writes it with {@link #write} and {@link #bars}; the client turns it into lines with {@link #lines}.
 */
public final class MachineReadout {
    private static final int GROUPS = MachineView.STATUS | MachineView.POWER | MachineView.PROGRESS | MachineView.UPGRADES
        | MachineView.CATALYST | MachineView.PLATE;
    /** Condition categories shown as states, in this order (machines/01 §3). */
    private static final List<String> CATEGORIES = List.of("temperature", "pressure", "atmosphere", "environment", "sterile");
    /** Value shown for a category with no value right now. */
    private static final Map<String, String> DEFAULTS = Map.of("temperature", "temperature.ambient", "pressure", "pressure.atmospheric");
    /** What is shown instead of an asked value that is missing: sheltered for open sky, rain for clear weather. */
    private static final Map<String, String> OPPOSITES = Map.of("environment.open_sky", "environment.sheltered",
        "environment.clear_weather", "environment.rain");
    /** Per recipe index (dropped with it on reload): the condition values each device's recipes ask for. */
    private static final Map<RecipeIndex, Map<String, List<String>>> ASKED = new WeakHashMap<>();

    /** One line and how it reads. */
    public record Line(Component text, MachineTexts.Tone tone) {
    }

    /**
     * One progress bar: how far it is ({@code fraction}) and what its text says: the time left
     * ({@code amount} ticks), stirs done of {@code goal} for a pot, or the percentage.
     */
    public record Bar(float fraction, Kind kind, long amount, long goal) {
        public enum Kind {
            TIME, STIRS, PERCENT
        }

        public Component text() {
            return switch (kind) {
                case TIME -> Component.translatable(GuiText.key("jade", "remaining"), duration(amount));
                case STIRS -> Component.translatable(GuiText.key("manual", "stir"), String.valueOf(amount), String.valueOf(goal));
                case PERCENT -> Component.translatable(GuiText.key("manual", "progress"), String.valueOf((int) (fraction * 100)));
            };
        }

        public void write(DataWriter out) {
            out.putInt("Fraction", Float.floatToIntBits(fraction));
            out.putByte("Kind", (byte) kind.ordinal());
            out.putLong("Amount", amount);
            out.putLong("Goal", goal);
        }

        public static Bar read(DataReader in) {
            Kind[] kinds = Kind.values();
            int kind = in.getByte("Kind", (byte) 0);
            return new Bar(Float.intBitsToFloat(in.getInt("Fraction", 0)), kinds[Math.max(0, Math.min(kinds.length - 1, kind))],
                in.getLong("Amount", 0), in.getLong("Goal", 0));
        }
    }

    private MachineReadout() {
    }

    /** Whether {@code blockEntity} has a readout. */
    public static boolean shows(BlockEntity blockEntity) {
        return blockEntity instanceof MachineBlockEntity || blockEntity instanceof T0DeviceBlockEntity;
    }

    // ---- server ----

    /** Writes the readout of {@code blockEntity} (server side). */
    public static void write(BlockEntity blockEntity, DataWriter out) {
        if (blockEntity instanceof MachineBlockEntity machine) {
            MachineDefinition definition = machine.definition();
            MachineView.of(machine, GROUPS).write(out.child("View"), GROUPS);
            out.putInt("Mode", machine.modeIndex());
            out.putBoolean("AirBlocked", definition.airIntake() && !machine.airOpen());
            out.putBoolean("Interrupted", machine instanceof SterileFermenterBlockEntity fermenter && fermenter.interrupted());
            MachineMode mode = machine.mode();
            List<String> asked = new ArrayList<>(asked(definition.spec(), machine.getLevel()));
            if (mode == null || !mode.outdoors()) {
                asked.removeIf(key -> key.startsWith("environment."));
            }
            out.putString("Asked", String.join(",", asked));
            DataWriter.ListWriter catalysts = out.list("Catalysts");
            for (int i = 0; i < machine.inventory().count(SlotRole.CATALYST); i++) {
                catalysts.add().putItem("Item", machine.inventory().get(SlotRole.CATALYST, i));
            }
        } else if (blockEntity instanceof T0DeviceBlockEntity device) {
            out.putString("Asked", String.join(",", asked(device.kind().spec(), device.getLevel())));
            out.putString("Caps", String.join(",", device.capabilities().keys()));
            DeviceContents contents = device.contents();
            DataWriter.ListWriter items = out.list("Items");
            for (int i = 0; i < contents.itemSlots(); i++) {
                items.add().putItem("Item", contents.item(i));
            }
            for (int i = 0; i < contents.catalystSlots(); i++) {
                items.add().putItem("Item", contents.catalyst(i));
            }
            DataWriter.ListWriter fluids = out.list("Fluids");
            for (int i = 0; i < contents.tankCount(); i++) {
                fluids.add().putFluid("Fluid", contents.tank(i));
            }
            DataWriter.ListWriter missing = out.list("Missing");
            for (T0DeviceBlockEntity.UnitState unit : device.unitStates()) {
                if (unit.goal() == 0 && !unit.missing().isEmpty()) {
                    missing.add().putString("Keys", String.join(",", unit.missing()));
                }
            }
        }
    }

    /** The progress bars of {@code blockEntity} (server side): the recipe a machine runs, each running unit of a device. */
    public static List<Bar> bars(BlockEntity blockEntity) {
        List<Bar> bars = new ArrayList<>();
        if (blockEntity instanceof ProcessingMachineBlockEntity machine) {
            ProcessingMachineBlockEntity.Job job = machine.job();
            if (job != null && job.total() > 0) {
                int rate = machine.definition().profile().speed() * machine.speedPermille();
                float fraction = Math.min(1F, (float) job.progress() / job.total());
                bars.add(rate > 0 ? new Bar(fraction, Bar.Kind.TIME, (job.total() - job.progress()) * 1000L / rate, 0)
                    : new Bar(fraction, Bar.Kind.PERCENT, 0, 0));
            }
        } else if (blockEntity instanceof T0DeviceBlockEntity device) {
            for (T0DeviceBlockEntity.UnitState unit : device.unitStates()) {
                if (unit.goal() <= 0) {
                    continue;
                }
                float fraction = Math.min(1F, (float) unit.progress() / unit.goal());
                bars.add(unit.manual() ? new Bar(fraction, Bar.Kind.STIRS, unit.progress(), unit.goal())
                    : new Bar(fraction, Bar.Kind.TIME, unit.goal() - unit.progress(), 0));
            }
        }
        return bars;
    }

    /**
     * The values of the shown categories that {@code spec} can offer and at least one of its recipes asks
     * for, in vocabulary order: a drying rack shows open sky and clear weather, not the biome words none of
     * its recipes uses, and the leaching barrel stands outdoors but shows nothing of it.
     */
    static List<String> asked(DeviceSpec spec, Level level) {
        RecipeIndex index = RecipeIndex.get(level);
        Map<String, List<String>> cache;
        synchronized (ASKED) {
            cache = ASKED.computeIfAbsent(index, key -> new HashMap<>());
        }
        synchronized (cache) {
            return cache.computeIfAbsent(spec.id(), id -> {
                Set<String> asked = new java.util.HashSet<>();
                for (ProcessType type : spec.types()) {
                    for (RecipeIndex.Entry entry : index.all(type)) {
                        asked.addAll(entry.recipe().required().keys());
                    }
                }
                List<String> result = new ArrayList<>();
                for (String category : CATEGORIES) {
                    for (ConditionVocabulary.Word word : ConditionVocabulary.category(category)) {
                        if (asked.contains(word.key()) && spec.capabilities().keys().contains(word.key())) {
                            result.add(word.key());
                        }
                    }
                }
                return List.copyOf(result);
            });
        }
    }

    // ---- client ----

    /** The lines of a readout written by {@link #write}; {@code blockEntity} is the client's copy. */
    public static List<Line> lines(BlockEntity blockEntity, DataReader in) {
        List<Line> lines = new ArrayList<>();
        List<String> asked = split(in.getString("Asked", ""));
        if (blockEntity instanceof MachineBlockEntity machine) {
            MachineDefinition definition = machine.definition();
            MachineView view = MachineView.read(in.child("View"), GROUPS);
            lines.add(new Line(MachineTexts.status(view, definition), MachineTexts.tone(view.status)));
            int mode = in.getInt("Mode", 0);
            if (definition.modes().size() > 1 && mode < definition.modes().size()) {
                lines.add(new Line(Component.translatable(GuiText.key("mode", "current"),
                    Component.translatable(Lang.condition("mode." + definition.modes().get(mode).id()))), MachineTexts.Tone.NORMAL));
            }
            conditions(lines, asked, Set.copyOf(view.capabilities), in.getBoolean("AirBlocked", false));
            if (definition.climate() != null) {
                String climate = switch (view.climate) {
                    case 3 -> "met";
                    case 1 -> "warm";
                    case 2 -> "humid";
                    default -> "none";
                };
                lines.add(new Line(Component.translatable(GuiText.key("info", "climate"), Component.translatable(GuiText.key("climate", climate))),
                    view.climate == 3 ? MachineTexts.Tone.GOOD : MachineTexts.Tone.WARNING));
            }
            if (in.getBoolean("Interrupted", false)) {
                lines.add(new Line(Component.translatable(GuiText.key("jade", "interrupted")), MachineTexts.Tone.WARNING));
            }
            if (view.contaminated) {
                lines.add(new Line(Component.translatable(GuiText.key("info", "contaminated")), MachineTexts.Tone.BAD));
            }
            if (definition.power() == PowerKind.FUEL && view.burnLeft > 0) {
                lines.add(new Line(Component.translatable(GuiText.key("jade", "fuel"), duration(view.burnLeft)), MachineTexts.Tone.NORMAL));
            } else if (definition.power() == PowerKind.MANUAL && view.energy > 0) {
                lines.add(new Line(Component.translatable(GuiText.key("jade", "crank"), duration(view.energy)), MachineTexts.Tone.NORMAL));
            }
            List<DataReader> catalysts = in.list("Catalysts");
            // uses left of each catalyst in use (Jade shows the items themselves); 0 is a catalyst no recipe uses yet
            for (int i = 0; i < catalysts.size() && i < view.catalystLeft.length; i++) {
                ItemStack stack = catalysts.get(i).getItem("Item");
                if (stack.isEmpty() || view.catalystLeft[i] == 0) {
                    continue;
                }
                lines.add(view.catalystLeft[i] < 0
                    ? new Line(Component.translatable(GuiText.key("jade", "saturated"), stack.getHoverName()), MachineTexts.Tone.WARNING)
                    : new Line(Component.translatable(GuiText.key("jade", "catalyst"), stack.getHoverName(), String.valueOf(view.catalystLeft[i])),
                        MachineTexts.Tone.NORMAL));
            }
        } else if (blockEntity instanceof T0DeviceBlockEntity) {
            boolean any = false;
            for (DataReader item : in.list("Items")) {
                ItemStack stack = item.getItem("Item");
                if (!stack.isEmpty()) {
                    lines.add(new Line(stack.getHoverName().copy().append(" ×" + stack.getCount()), MachineTexts.Tone.NORMAL));
                    any = true;
                }
            }
            for (DataReader tank : in.list("Fluids")) {
                FluidAmount fluid = tank.getFluid("Fluid");
                if (!fluid.isEmpty()) {
                    lines.add(new Line(PlatformFluids.displayName(fluid).copy().append(" " + fluid.amount() + " mB"), MachineTexts.Tone.NORMAL));
                    any = true;
                }
            }
            if (!any) {
                lines.add(new Line(Component.translatable(GuiText.key("tank", "empty")), MachineTexts.Tone.NORMAL));
            }
            conditions(lines, asked, Set.copyOf(split(in.getString("Caps", ""))), false);
            for (DataReader unit : in.list("Missing")) {
                lines.add(new Line(Component.translatable(GuiText.key("manual", "missing"),
                    T0Devices.conditionNames(new LinkedHashSet<>(split(unit.getString("Keys", ""))))), MachineTexts.Tone.WARNING));
            }
        }
        return lines;
    }

    /**
     * One line per category with asked values: those of them offered right now, or the plain value
     * (ambient, atmospheric pressure) when there is none.
     */
    private static void conditions(List<Line> lines, List<String> asked, Set<String> capabilities, boolean airBlocked) {
        for (String category : CATEGORIES) {
            List<String> values = new ArrayList<>();
            boolean shown = false;
            for (String key : asked) {
                if (key.startsWith(category + ".")) {
                    shown = true;
                    if (capabilities.contains(key)) {
                        values.add(key);
                    } else if (OPPOSITES.containsKey(key) && capabilities.contains(OPPOSITES.get(key))) {
                        values.add(OPPOSITES.get(key));
                    }
                }
            }
            if (!shown) {
                continue;
            }
            if (category.equals("sterile")) {
                if (!values.isEmpty()) {
                    lines.add(new Line(words(values), MachineTexts.Tone.GOOD));
                }
                continue;
            }
            if (category.equals("atmosphere") && airBlocked && !values.contains(MachineDefinition.AIR)) {
                lines.add(new Line(Component.translatable(GuiText.key("jade", category), Component.translatable(GuiText.key("jade", "air_blocked"))),
                    MachineTexts.Tone.WARNING));
                continue;
            }
            if (values.isEmpty() && DEFAULTS.containsKey(category)) {
                values.add(DEFAULTS.get(category));
            }
            Component text = values.isEmpty() ? Component.translatable(GuiText.key("info", "none")) : words(values);
            lines.add(new Line(Component.translatable(GuiText.key("jade", category), text), MachineTexts.Tone.NORMAL));
        }
    }

    private static Component words(List<String> keys) {
        MutableComponent words = Component.empty();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                words.append(Component.translatable(GuiText.key("manual", "separator")));
            }
            String key = keys.get(i);
            int dot = key.indexOf('.');
            words.append(ConditionVocabulary.find(key.substring(0, dot), key.substring(dot + 1))
                .map(word -> (Component) Component.translatable(word.translationKey())).orElse(Component.literal(key)));
        }
        return words;
    }

    /** {@code ticks} as seconds, or minutes and seconds from a minute on. */
    private static Component duration(long ticks) {
        long seconds = Math.max(0, (ticks + 19) / 20);
        return seconds >= 60
            ? Component.translatable(GuiText.key("jade", "minutes"), String.valueOf(seconds / 60), String.valueOf(seconds % 60))
            : Component.translatable(GuiText.key("jade", "seconds"), String.valueOf(seconds));
    }

    private static List<String> split(String text) {
        return text.isEmpty() ? List.of() : Arrays.asList(text.split(","));
    }
}
