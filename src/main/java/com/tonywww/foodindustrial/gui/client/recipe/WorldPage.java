package com.tonywww.foodindustrial.gui.client.recipe;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.ConditionVocabulary;
import com.tonywww.foodindustrial.gui.RecipeText;
import com.tonywww.foodindustrial.gui.client.Widgets;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;
import com.tonywww.foodindustrial.platform.purity.PlatformPurity;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.recipe.Conditions;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.world.interaction.BlockTransformRecipe;
import com.tonywww.foodindustrial.world.interaction.HarvestRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The world interactions as JEI pages (W01 block transformation, W02 harvesting; I22): the block, the
 * tool and container for harvesting, the result, the conditions and the chances.
 */
public final class WorldPage {
    public static final int WIDTH = 160;
    public static final int HEIGHT = 50;
    public static final int BLOCK_X = 0;
    public static final int TOOL_X = 22;
    public static final int CONTAINER_X = 40;
    public static final int RESULT_X = 100;
    private static final int ARROW_X = 64;
    private static final int TEXT = 0xFF404040;

    private final String block;
    private final List<String> conditions = new ArrayList<>();
    private final List<Component> words = new ArrayList<>();
    private final List<Component> notes = new ArrayList<>();
    @Nullable
    private final HarvestRecipe harvest;
    @Nullable
    private final BlockTransformRecipe transform;

    private WorldPage(String block, Conditions requirements, @Nullable HarvestRecipe harvest, @Nullable BlockTransformRecipe transform) {
        this.harvest = harvest;
        this.transform = transform;
        this.block = block;
        for (String key : requirements.required().keys()) {
            int dot = key.indexOf('.');
            conditions.add(key.substring(0, dot));
            words.add(ConditionVocabulary.find(key.substring(0, dot), key.substring(dot + 1))
                .<Component>map(word -> Component.translatable(word.translationKey())).orElse(Component.literal(key)));
        }
    }

    public static WorldPage of(BlockTransformRecipe recipe) {
        WorldPage page = new WorldPage(recipe.block(), recipe.requirements(), null, recipe);
        if (recipe.randomTickChance() > 0) {
            page.notes.add(Component.translatable(RecipeText.key("random_tick"), percent(recipe.randomTickChance())));
        }
        if (recipe.entities().isPresent() && recipe.entityChance() > 0) {
            page.notes.add(Component.translatable(RecipeText.key("entities"), percent(recipe.entityChance())));
        }
        return page;
    }

    public static WorldPage of(HarvestRecipe recipe) {
        WorldPage page = new WorldPage(recipe.block(), recipe.requirements(), recipe, null);
        if (recipe.cooldown() > 0) {
            page.notes.add(Component.translatable(RecipeText.key("cooldown"), String.valueOf(recipe.cooldown() / 20)));
        }
        return page;
    }

    private static String percent(double chance) {
        String text = String.format(Locale.ROOT, "%.2f", chance * 100);
        while (text.endsWith("0")) {
            text = text.substring(0, text.length() - 1);
        }
        return (text.endsWith(".") ? text.substring(0, text.length() - 1) : text) + "%";
    }

    /** Items of the blocks the interaction works on (read from the block tag when asked). */
    public List<ItemStack> blocks() {
        List<ItemStack> blocks = new ArrayList<>();
        for (Block candidate : PlatformTags.blocks(block)) {
            ItemStack stack = new ItemStack(candidate.asItem());
            if (!stack.isEmpty() && stack.getItem() != Items.AIR) {
                blocks.add(stack);
            }
        }
        return blocks;
    }

    /** Tool items (harvesting), else empty. */
    public List<ItemStack> tools() {
        return harvest == null ? List.of() : PlatformIngredients.displayStacks(harvest.tool());
    }

    /** Container items (harvesting), else empty. */
    public List<ItemStack> containers() {
        return harvest == null || harvest.container().isEmpty() ? List.of() : PlatformIngredients.displayStacks(harvest.container().get());
    }

    /** Result block item (transformation). */
    public ItemStack resultBlock() {
        if (transform == null) {
            return ItemStack.EMPTY;
        }
        Optional<Block> block = PlatformTags.block(transform.result());
        return block.map(b -> new ItemStack(b.asItem())).orElse(ItemStack.EMPTY);
    }

    /** Harvest result: an item, or empty when the result is a fluid. */
    public ItemStack resultItem() {
        if (harvest == null || harvest.result().isFluid()) {
            return ItemStack.EMPTY;
        }
        ProcessOutput output = harvest.result();
        ItemStack stack = output.stack(output.amount());
        int b = sourcePurity(output);
        if (b != Purity.NONE) {
            PlatformPurity.set(stack, (byte) b);
        }
        return stack;
    }

    /** Harvest result fluid, or empty. */
    public FluidAmount resultFluid() {
        if (harvest == null || !harvest.result().isFluid()) {
            return FluidAmount.EMPTY;
        }
        ProcessOutput output = harvest.result();
        int b = sourcePurity(output);
        FluidAmount fluid = output.fluidAmount(output.amount());
        return b == Purity.NONE ? fluid : fluid.withPurity(b);
    }

    /** World interactions give the material's source purity (machines/01 §3.2). */
    private static int sourcePurity(ProcessOutput output) {
        if (!output.bearsPurity()) {
            return Purity.NONE;
        }
        PurityData.Material material = PurityData.current().material(output.material());
        return material == null ? Purity.NONE : com.tonywww.foodindustrial.recipe.OutcomeResolver.apply(
            output.purity() != null ? output.purity() : com.tonywww.foodindustrial.recipe.PurityRule.SOURCE_DEFAULT, material, Double.NaN, 0);
    }

    public boolean harvesting() {
        return harvest != null;
    }

    public void draw(Canvas canvas, double mouseX, double mouseY) {
        Widgets.slot(canvas, Widgets.SlotKind.NORMAL, BLOCK_X, 0);
        if (harvest != null) {
            Widgets.slot(canvas, Widgets.SlotKind.CATALYST, TOOL_X, 0);
            if (harvest.container().isPresent()) {
                Widgets.slot(canvas, Widgets.SlotKind.NORMAL, CONTAINER_X, 0);
            }
        }
        Widgets.arrow(canvas, ARROW_X, 0, 0);
        Widgets.slot(canvas, Widgets.SlotKind.MAIN, RESULT_X, 0);
        for (int i = 0; i < conditions.size(); i++) {
            RecipeTextures.condition(canvas, conditions.get(i), i * 11, 22);
        }
        int y = 33;
        for (Component note : notes) {
            Widgets.fitText(canvas, note, 0, y, WIDTH, TEXT, false);
            y += 9;
        }
    }

    public List<Component> tooltip(double mouseX, double mouseY) {
        List<Component> lines = new ArrayList<>();
        if (mouseY >= 22 && mouseY < 31) {
            int index = (int) (mouseX / 11);
            if (mouseX >= 0 && index < words.size() && mouseX - index * 11 < 9) {
                lines.add(words.get(index));
            }
        }
        return lines;
    }

    /** Extra line of the tool slot. */
    public static Component toolLine() {
        return Component.translatable(RecipeText.key("tool")).withStyle(ChatFormatting.GRAY);
    }

    /** Extra line of the container slot. */
    public static Component containerLine() {
        return Component.translatable(RecipeText.key("container")).withStyle(ChatFormatting.GRAY);
    }
}
