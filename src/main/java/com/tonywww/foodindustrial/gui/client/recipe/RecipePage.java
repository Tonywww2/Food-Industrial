package com.tonywww.foodindustrial.gui.client.recipe;

import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.ConditionVocabulary;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.TierText;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.RecipeText;
import com.tonywww.foodindustrial.gui.client.Widgets;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;
import com.tonywww.foodindustrial.platform.purity.PlatformPurity;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.recipe.Catalyst;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.OutcomeResolver;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.PurityRule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * A recipe of the 19 recipe types as one JEI page (I22, additives/10 §5): where the inputs, catalysts
 * and outputs go, the condition icons, the devices that run the recipe and the purity each one gives.
 * Loader- and JEI-independent: the JEI category places the slots this page lists, draws through
 * {@link #draw}, asks {@link #tooltip} and passes clicks to {@link #click}.
 * <p>
 * The output slots show the result of the selected device with every input at its threshold (the
 * external default without one). Selecting a device changes the outputs; the selection stays while the
 * game runs, so a pattern encoded from JEI follows the device the player picked.
 */
public final class RecipePage {
    public static final int WIDTH = 160;
    public static final int HEIGHT = 88;
    public static final int SLOT = 18;
    private static final int OUTPUT_X = 100;
    private static final int ARROW_X = 64;
    private static final int ARROW_Y = 9;
    private static final int CONDITIONS_Y = 47;
    private static final int TEXT_Y = 58;
    private static final int DEVICES_Y = 69;
    private static final int ICON = 9;
    private static final int TEXT = 0xFF404040;
    /** Fixed random source: purity ranges show as a range, so nothing depends on it. */
    private static final Map<ProcessRecipe, RecipePage> PAGES = Collections.synchronizedMap(new WeakHashMap<>());

    /** Where a slot goes and what it holds. */
    public enum Role {
        INPUT, CATALYST, OUTPUT
    }

    /**
     * A slot of the page.
     *
     * @param index recipe input, catalyst or output index
     * @param x     left of the 18 × 18 frame; the ingredient sits 1 pixel inside
     */
    public record Slot(Role role, int index, int x, int y, boolean fluid) {
    }

    private record Condition(String category, Component word, int x) {
    }

    private final String id;
    private final ProcessRecipe recipe;
    private final List<DeviceSpec> devices;
    private final Tier base;
    private final List<Slot> slots = new ArrayList<>();
    private final List<Condition> conditions = new ArrayList<>();
    private int selected;

    private RecipePage(String id, ProcessRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
        this.devices = RecipeDevices.runners(recipe);
        List<DeviceSpec> all = DeviceSpec.runnersOf(recipe);
        this.base = all.isEmpty() ? Tier.T0 : all.get(0).tier();
        layout();
    }

    /** The page of {@code recipe}; one per recipe while the recipe is loaded. */
    public static RecipePage of(String id, ProcessRecipe recipe) {
        return PAGES.computeIfAbsent(recipe, key -> new RecipePage(id, recipe));
    }

    public String id() {
        return id;
    }

    public ProcessRecipe recipe() {
        return recipe;
    }

    public List<Slot> slots() {
        return slots;
    }

    public List<DeviceSpec> devices() {
        return devices;
    }

    @Nullable
    public DeviceSpec selectedDevice() {
        return devices.isEmpty() ? null : devices.get(Math.min(selected, devices.size() - 1));
    }

    private void layout() {
        List<ProcessInput> inputs = recipe.inputs();
        int columns = 3;
        for (int i = 0; i < inputs.size(); i++) {
            slots.add(new Slot(Role.INPUT, i, (i % columns) * SLOT, (i / columns) * SLOT, inputs.get(i) instanceof ProcessInput.FluidInput));
        }
        List<Catalyst> catalysts = recipe.catalysts();
        int catalystX = ARROW_X + 12 - catalysts.size() * SLOT / 2;
        for (int i = 0; i < catalysts.size(); i++) {
            slots.add(new Slot(Role.CATALYST, i, catalystX + i * SLOT, 28, false));
        }
        List<ProcessOutput> outputs = recipe.outputs();
        for (int i = 0; i < outputs.size(); i++) {
            slots.add(new Slot(Role.OUTPUT, i, OUTPUT_X + (i % columns) * SLOT, (i / columns) * SLOT, outputs.get(i).isFluid()));
        }
        int x = 0;
        for (String key : recipe.required().keys()) {
            int dot = key.indexOf('.');
            String category = key.substring(0, dot);
            Optional<ConditionVocabulary.Word> word = ConditionVocabulary.find(category, key.substring(dot + 1));
            conditions.add(new Condition(category, word.<Component>map(w -> Component.translatable(w.translationKey())).orElse(Component.literal(key)), x));
            x += ICON + 2;
        }
        if (recipe.mode().isPresent()) {
            String mode = recipe.mode().get();
            Optional<ConditionVocabulary.Word> word = ConditionVocabulary.find("mode", mode);
            conditions.add(new Condition("mode", word.<Component>map(w -> Component.translatable(w.translationKey())).orElse(Component.literal(mode)), x));
        }
    }

    // ---- ingredients ----

    /**
     * Whether every input and catalyst has something to show. A recipe whose tag has no items in this
     * pack (an optional mod's crop, say) cannot be made, and JEI leaves it out like its own recipes
     * with an empty ingredient (I22, 2026-11-11).
     */
    public boolean complete() {
        for (int i = 0; i < recipe.inputs().size(); i++) {
            boolean empty = recipe.inputs().get(i) instanceof ProcessInput.FluidInput ? inputFluids(i).isEmpty() : inputItems(i).isEmpty();
            if (empty) {
                return false;
            }
        }
        for (int i = 0; i < recipe.catalysts().size(); i++) {
            if (catalystItems(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** Stacks shown in input slot {@code index}: the qualifying items at the threshold, with the recipe count. */
    public List<ItemStack> inputItems(int index) {
        ProcessInput.ItemInput input = (ProcessInput.ItemInput) recipe.inputs().get(index);
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : PlatformIngredients.displayStacks(input.ingredient())) {
            ItemStack copy = stack.copy();
            copy.setCount(input.count());
            stacks.add(copy);
        }
        return stacks;
    }

    /** Fluids shown in input slot {@code index}. */
    public List<FluidAmount> inputFluids(int index) {
        return ((ProcessInput.FluidInput) recipe.inputs().get(index)).fluid().displayFluids();
    }

    public List<ItemStack> catalystItems(int index) {
        return PlatformIngredients.displayStacks(recipe.catalysts().get(index).ingredient());
    }

    /** Item of output {@code index} on the selected device, with its purity. */
    public ItemStack outputItem(int index) {
        ProcessOutput output = recipe.outputs().get(index);
        ItemStack stack = output.stack(output.amount());
        int b = outputPurity(index);
        if (b != Purity.NONE) {
            PlatformPurity.set(stack, (byte) b);
        }
        return stack;
    }

    /** Fluid of output {@code index} on the selected device, with its purity. */
    public FluidAmount outputFluid(int index) {
        ProcessOutput output = recipe.outputs().get(index);
        FluidAmount fluid = output.fluidAmount(output.amount());
        int b = outputPurity(index);
        return b == Purity.NONE ? fluid : fluid.withPurity(b);
    }

    /** Purity of output {@code index} on the selected device, or {@link Purity#NONE}. */
    public int outputPurity(int index) {
        DeviceSpec device = selectedDevice();
        if (device == null) {
            return Purity.NONE;
        }
        return OutcomeResolver.atThreshold(recipe, index, device.profile(), base);
    }

    // ---- interaction ----

    /** Selects the device under the mouse; {@code true} when the click hit one. */
    public boolean click(double mouseX, double mouseY) {
        int index = deviceAt(mouseX, mouseY);
        if (index < 0) {
            return false;
        }
        selected = index;
        return true;
    }

    /** Index of the device under the mouse, or -1. */
    public int deviceAt(double mouseX, double mouseY) {
        if (mouseY < DEVICES_Y || mouseY >= DEVICES_Y + SLOT || mouseX < 0) {
            return -1;
        }
        int index = (int) (mouseX / (SLOT + 1));
        return index < devices.size() && mouseX - index * (SLOT + 1) < SLOT ? index : -1;
    }

    // ---- drawing ----

    public void draw(Canvas canvas, double mouseX, double mouseY) {
        for (Slot slot : slots) {
            Widgets.slot(canvas, slot.role == Role.CATALYST ? Widgets.SlotKind.CATALYST : slot.role == Role.OUTPUT && slot.index == 0
                ? Widgets.SlotKind.MAIN : Widgets.SlotKind.NORMAL, slot.x, slot.y);
        }
        Widgets.arrow(canvas, ARROW_X, ARROW_Y, 0);
        DeviceSpec device = selectedDevice();
        for (Slot slot : slots) {
            if (slot.role == Role.OUTPUT && device != null && !recipe.outputs().get(slot.index).producedAt(device.tier())) {
                canvas.fill(slot.x + 1, slot.y + 1, slot.x + 17, slot.y + 17, 0x80555555);
            }
        }
        for (Condition condition : conditions) {
            RecipeTextures.condition(canvas, condition.category, condition.x, CONDITIONS_Y);
        }
        if (device != null) {
            Widgets.fitText(canvas, deviceSummary(device), 0, TEXT_Y, WIDTH, TEXT, false);
        }
        for (int i = 0; i < devices.size(); i++) {
            int x = i * (SLOT + 1);
            Widgets.slot(canvas, Widgets.SlotKind.NORMAL, x, DEVICES_Y);
            canvas.item(RecipeDevices.icon(devices.get(i)), x + 1, DEVICES_Y + 1);
            if (i == selected) {
                RecipeTextures.selected(canvas, x, DEVICES_Y);
            }
        }
    }

    /**
     * {@code 能耗 20 FE/t · 耗时 10 秒} of a device, with {@code 操作 4 次} last for hand tools; the tier is
     * not on the page, only in the device tooltip (2026-11-11).
     */
    private Component deviceSummary(DeviceSpec device) {
        List<Component> parts = new ArrayList<>();
        long energy = RecipeDevices.energy(device, recipe);
        if (energy > 0) {
            parts.add(Component.translatable(RecipeText.key("energy"), String.valueOf(energy)));
        }
        int ticks = RecipeDevices.ticks(device, recipe);
        if (ticks > 0) {
            parts.add(Component.translatable(RecipeText.key("time"), seconds(ticks)));
        }
        int actions = RecipeDevices.actions(device, recipe);
        if (actions > 0) {
            parts.add(Component.translatable(RecipeText.key("actions"), String.valueOf(actions)));
        }
        MutableComponent line = Component.empty();
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) {
                line.append(" · ");
            }
            line.append(parts.get(i));
        }
        return line;
    }

    private static String seconds(int ticks) {
        double s = ticks / 20.0;
        return s == Math.rint(s) ? String.valueOf((int) s) : String.format(Locale.ROOT, "%.1f", s);
    }

    // ---- tooltips ----

    /** Tooltip of the page outside the slots: condition words and device details. */
    public List<Component> tooltip(double mouseX, double mouseY) {
        List<Component> lines = new ArrayList<>();
        if (mouseY >= CONDITIONS_Y && mouseY < CONDITIONS_Y + ICON) {
            for (Condition condition : conditions) {
                if (mouseX >= condition.x && mouseX < condition.x + ICON) {
                    lines.add(condition.word);
                }
            }
        }
        int index = deviceAt(mouseX, mouseY);
        if (index >= 0) {
            DeviceSpec device = devices.get(index);
            // name, then the tier alone, energy and time, and the yield (2026-11-11)
            lines.add(RecipeDevices.icon(device).getHoverName());
            lines.add(TierText.tier(device.tier()));
            lines.add(deviceSummary(device).copy().withStyle(ChatFormatting.GRAY));
            int yield = recipe.type().yieldScalable() ? device.profile().yieldPermille() : 1000;
            lines.add(Component.translatable(RecipeText.key("yield"), trim(yield / 1000.0)).withStyle(ChatFormatting.GRAY));
            lines.add(Component.translatable(RecipeText.key(index == selected ? "selected" : "select")).withStyle(ChatFormatting.DARK_GRAY));
        }
        return lines;
    }

    private static String trim(double value) {
        String text = String.format(Locale.ROOT, "%.2f", value);
        while (text.endsWith("0")) {
            text = text.substring(0, text.length() - 1);
        }
        return text.endsWith(".") ? text.substring(0, text.length() - 1) : text;
    }

    /** Lines JEI adds below the ingredient tooltip of {@code slot}. */
    public List<Component> slotLines(Slot slot) {
        List<Component> lines = new ArrayList<>();
        switch (slot.role) {
            case INPUT -> inputLines(recipe.inputs().get(slot.index), lines);
            case CATALYST -> lines.add(Component.translatable(RecipeText.key("catalyst_runs"),
                String.valueOf(recipe.catalysts().get(slot.index).runs())).withStyle(ChatFormatting.GRAY));
            case OUTPUT -> outputLines(slot.index, lines);
        }
        return lines;
    }

    private static void inputLines(ProcessInput input, List<Component> lines) {
        String tag = tagOf(input);
        if (tag != null) {
            lines.add(Component.translatable(RecipeText.key("tag"), "#" + tag).withStyle(ChatFormatting.DARK_GRAY));
        }
        Optional<PurityThreshold> threshold = input.threshold();
        if (threshold.isPresent()) {
            int value = threshold.get().value();
            List<Component> purity = new ArrayList<>();
            Tooltips.purityLine(threshold.get().material(), value, purity);
            Component shown = purity.isEmpty() ? Component.literal(PurityScale.format(value) + "%") : purity.get(0);
            lines.add(Component.translatable(RecipeText.key("need_purity"), shown).withStyle(ChatFormatting.GOLD));
            PurityData.Material material = PurityData.current().material(threshold.get().material());
            if (material != null && material.missing() >= value) {
                lines.add(Component.translatable(RecipeText.key("as_poor")).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Nullable
    private static String tagOf(ProcessInput input) {
        if (input instanceof ProcessInput.FluidInput fluid) {
            return fluid.fluid().tag().map(PlatformTags::tagId).orElse(null);
        }
        com.google.gson.JsonElement references = PlatformIngredients.references(PlatformIngredients.unwrap(((ProcessInput.ItemInput) input).ingredient()));
        if (references != null && references.isJsonObject() && references.getAsJsonObject().has("tag")) {
            return references.getAsJsonObject().get("tag").getAsString();
        }
        return null;
    }

    private void outputLines(int index, List<Component> lines) {
        ProcessOutput output = recipe.outputs().get(index);
        DeviceSpec device = selectedDevice();
        if (device != null && !output.producedAt(device.tier())) {
            lines.add(Component.translatable(RecipeText.key("not_made")).withStyle(ChatFormatting.RED));
        }
        if (output.minTier() != null) {
            lines.add(Component.translatable(RecipeText.key("min_tier"), TierText.tier(output.minTier())).withStyle(ChatFormatting.GRAY));
        }
        if (output.isChanced()) {
            lines.add(Component.translatable(RecipeText.key("chance"), percent(output.chance() * 100)).withStyle(ChatFormatting.GRAY));
        }
        if (recipe.plate().isPresent()) {
            lines.add(Component.translatable(RecipeText.key("plate")).withStyle(ChatFormatting.GRAY));
            if (recipe.plate().get().byPurity()) {
                lines.add(Component.translatable(RecipeText.key("plate_by_purity")).withStyle(ChatFormatting.GRAY));
            } else {
                int total = 0;
                for (int i = 0; i < recipe.outputs().size(); i++) {
                    total += recipe.plate().get().weight(i);
                }
                if (total > 0) {
                    lines.add(Component.translatable(RecipeText.key("plate_weight"),
                        percent(100.0 * recipe.plate().get().weight(index) / total)).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        if (!output.bearsPurity()) {
            return;
        }
        if (index == 0 && recipe.purityRange().isPresent()) {
            lines.add(Component.translatable(RecipeText.key("random"), PurityScale.format(recipe.rangeLow()) + "%",
                PurityScale.format(recipe.rangeHigh()) + "%").withStyle(ChatFormatting.GRAY));
            return;
        }
        lines.add(ruleLine(recipe.ruleOf(index), PurityData.current().material(output.material())));
    }

    /** {@code 来源 83%}, {@code 除杂 50%}, {@code 保持}, {@code 劣化 10%}, with the caps. */
    private static Component ruleLine(PurityRule rule, @Nullable PurityData.Material material) {
        MutableComponent line = Component.translatable(Lang.condition("purity_mode." + rule.mode().id()));
        switch (rule.mode()) {
            case SOURCE -> {
                int b = OutcomeResolver.apply(rule, material, Double.NaN, 0);
                if (b != Purity.NONE) {
                    line.append(" " + PurityScale.format(b) + "%");
                }
            }
            case REMOVAL, DEGRADE -> line.append(" " + percent(rule.value()));
            default -> {
            }
        }
        if (rule.hasMax()) {
            line.append(" · ").append(Component.translatable(RecipeText.key("rule_max"), PurityScale.format(rule.maxByte()) + "%"));
        }
        if (rule.hasT0Max()) {
            int cap = rule.t0MaxByte(material);
            if (cap != Purity.NONE) {
                line.append(" · ").append(Component.translatable(RecipeText.key("rule_t0_max"), PurityScale.format(cap) + "%"));
            }
        }
        return line.withStyle(ChatFormatting.GRAY);
    }

    private static String percent(double value) {
        return trim(value) + "%";
    }
}
