package com.tonywww.foodindustrial.devtest;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.tonywww.foodindustrial.api.purity.FluidPurityIngredient;
import com.tonywww.foodindustrial.api.purity.PurityGrade;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import net.minecraft.world.item.crafting.Ingredient;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityRules;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.PlatformWorldgen;
import com.tonywww.foodindustrial.world.Fallbacks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;

/**
 * Development-only commands ({@code /fidev ...}) to inspect platform and core behaviour from the
 * server console or RCON.
 */
final class DevCommands {
    private DevCommands() {
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fidev")
            .then(Commands.literal("purity")
                .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(DevCommands::purity)))
            .then(Commands.literal("setpurity")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(Commands.argument("slot", IntegerArgumentType.integer(0))
                        .then(Commands.argument("percent", StringArgumentType.word()).executes(DevCommands::setPurity)))))
            .then(Commands.literal("fluidpurity")
                .then(Commands.argument("fluid", StringArgumentType.greedyString()).executes(DevCommands::fluidPurity)))
            .then(Commands.literal("rules").executes(DevCommands::rules))
            .then(Commands.literal("recipe")
                .then(Commands.argument("id", StringArgumentType.greedyString()).executes(DevCommands::recipe)))
            .then(Commands.literal("ingredient")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(Commands.argument("json", StringArgumentType.greedyString()).executes(DevCommands::ingredient))))
            .then(Commands.literal("container")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(Commands.argument("slot", IntegerArgumentType.integer(0)).executes(DevCommands::containerContents))))
            .then(Commands.literal("fill")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(Commands.argument("slot", IntegerArgumentType.integer(0))
                        .then(Commands.argument("percent", StringArgumentType.word())
                            .then(Commands.argument("fluid", StringArgumentType.greedyString()).executes(DevCommands::fill))))))
            .then(Commands.literal("drain")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(Commands.argument("slot", IntegerArgumentType.integer(0)).executes(DevCommands::drain))))
            .then(Commands.literal("fluidingredient")
                .then(Commands.argument("json", StringArgumentType.greedyString()).executes(DevCommands::fluidIngredient)))
            .then(Commands.literal("placed")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(Commands.argument("feature", StringArgumentType.greedyString()).executes(DevCommands::placed))))
            .then(Commands.literal("scan")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(Commands.argument("radius", IntegerArgumentType.integer(0, 8)).executes(DevCommands::scan))))
            .then(Commands.literal("fallbacks").executes(DevCommands::fallbacks))
            .then(Commands.literal("tag")
                .then(Commands.literal("item")
                    .then(Commands.argument("tag", StringArgumentType.greedyString()).executes(context -> tag(context, false))))
                .then(Commands.literal("fluid")
                    .then(Commands.argument("tag", StringArgumentType.greedyString()).executes(context -> tag(context, true))))));
    }

    /** Places a placed feature at a position as world generation would. */
    private static int placed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        String feature = StringArgumentType.getString(context, "feature");
        boolean placed = PlatformWorldgen.placeFeature(context.getSource().getLevel(), feature, pos);
        reply(context, "placed " + feature + " at " + pos.toShortString() + ": " + placed);
        return placed ? 1 : 0;
    }

    /** Generates the chunks around a position and counts the mod's blocks in them. */
    private static int scan(CommandContext<CommandSourceStack> context) {
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        int radius = IntegerArgumentType.getInteger(context, "radius");
        var counts = PlatformWorldgen.countBlocks(context.getSource().getLevel(), pos.getX() >> 4, pos.getZ() >> 4, radius, "foodindustrial");
        reply(context, "scan " + pos.toShortString() + " r" + radius + ": " + counts);
        return counts.size();
    }

    private static int fallbacks(CommandContext<CommandSourceStack> context) {
        for (Fallbacks fallback : Fallbacks.values()) {
            reply(context, "fallback " + fallback.id() + " enabled=" + fallback.enabled());
        }
        return 1;
    }

    /** Lists the members of an item tag, or the source fluids of a fluid tag, as loaded on the server. */
    private static int tag(CommandContext<CommandSourceStack> context, boolean fluid) {
        String id = StringArgumentType.getString(context, "tag");
        List<String> members = fluid
            ? PlatformTags.sourceFluidsIn(PlatformTags.fluidTag(id)).stream().map(PlatformTags::fluidId).toList()
            : PlatformTags.itemsIn(PlatformTags.itemTag(id)).stream().map(PlatformTags::itemId).toList();
        reply(context, "tag " + id + " (" + members.size() + "): " + String.join(" ", members));
        return members.size();
    }

    private static Container container(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        if (context.getSource().getLevel().getBlockEntity(pos) instanceof Container container) {
            return container;
        }
        throw new IllegalArgumentException("no container at " + pos);
    }

    /** Byte, display, grade (or content) and category of a purity value. */
    static String describe(@Nullable String material, int b) {
        if (b == Purity.NONE) {
            return "none";
        }
        PurityData.Material data = PurityData.current().material(material);
        String grade = Purity.grade(material, b).map(PurityGrade::id).orElse("content");
        return "b=" + b + " " + PurityScale.format(b) + "% " + grade + " [" + (data == null ? "no data" : data.category()) + "]";
    }

    private static int purity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Container container = container(context);
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty()) {
                OptionalInt stored = Purity.stored(stack);
                String material = Purity.materialOf(stack.getItem());
                String line = "slot " + slot + ": " + PlatformTags.itemId(stack.getItem()) + " material=" + material
                    + " bearer=" + Purity.bears(stack.getItem())
                    + " stored=" + (stored.isPresent() ? describe(material, stored.getAsInt()) : "none")
                    + " effective=" + describe(material, Purity.get(stack));
                reply(context, line);
            }
        }
        return 1;
    }

    /** {@code b} of a grade name (by the material's bounds) or a percentage. */
    private static int parsePurity(String text, @Nullable String material) {
        PurityGrade grade = PurityGrade.byId(text);
        if (grade == null) {
            return PurityScale.ofPercent(Double.parseDouble(text));
        }
        PurityData.Material data = PurityData.current().material(material);
        int b = data == null ? Purity.NONE : data.gradeValue(grade);
        if (b == Purity.NONE) {
            throw new IllegalArgumentException(material + " has no grade " + grade.id());
        }
        return b;
    }

    private static int setPurity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Container container = container(context);
        int slot = IntegerArgumentType.getInteger(context, "slot");
        ItemStack stack = container.getItem(slot);
        String material = Purity.materialOf(stack.getItem());
        int b = parsePurity(StringArgumentType.getString(context, "percent"), material);
        Purity.set(stack, b);
        container.setChanged();
        reply(context, "slot " + slot + " set to " + describe(material, b));
        return 1;
    }

    private static int fluidPurity(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "fluid");
        Fluid fluid = PlatformTags.fluid(id).orElseThrow(() -> new IllegalArgumentException("unknown fluid " + id));
        FluidAmount amount = FluidAmount.of(fluid, 1000);
        String material = Purity.materialOf(fluid);
        reply(context, id + " material=" + material + " bearer=" + Purity.bears(fluid) + " effective=" + describe(material, Purity.get(amount)));
        return 1;
    }

    private static int rules(CommandContext<CommandSourceStack> context) {
        reply(context, PurityRules.rules().size() + " rules");
        for (PurityRules.Rule rule : PurityRules.rules()) {
            reply(context, rule.source() + " items=" + rule.items() + " fluids=" + rule.fluids() + " purity=" + rule.value().text());
        }
        return 1;
    }

    /** Purity thresholds of a loaded recipe's ingredients, as resolved when the recipe loaded. */
    private static int recipe(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        List<Ingredient> ingredients = PlatformRecipes.ingredientsOf(context.getSource().getLevel(), id);
        reply(context, "recipe " + id + ": " + ingredients.size() + " ingredients");
        for (Ingredient ingredient : ingredients) {
            reply(context, "  threshold=" + PlatformIngredients.purityThreshold(ingredient)
                .map(value -> describe(value.material(), value.value()) + " material=" + value.material()).orElse("none"));
        }
        return ingredients.size();
    }

    /** Parses an ingredient, tests every stack of the container and lists the displayed stacks. */
    private static int ingredient(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Container container = container(context);
        var ops = RegistryOps.create(JsonOps.INSTANCE, context.getSource().getLevel().registryAccess());
        var parsed = PlatformIngredients.codec().parse(ops, JsonParser.parseString(StringArgumentType.getString(context, "json")));
        if (parsed.result().isEmpty()) {
            reply(context, "parse error: " + parsed.error().map(error -> error.message()).orElse("?"));
            return 0;
        }
        Ingredient ingredient = parsed.result().get();
        var threshold = PlatformIngredients.purityThreshold(ingredient);
        reply(context, "threshold=" + threshold.map(value -> describe(value.material(), value.value()) + " material=" + value.material()).orElse("none")
            + " empty=" + ingredient.isEmpty() + " encoded=" + PlatformIngredients.codec().encodeStart(ops, ingredient).result().map(Object::toString).orElse("?"));
        String hint = threshold.map(value -> value.material()).orElse(null);
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty()) {
                String material = Purity.materialOf(stack.getItem());
                reply(context, "slot " + slot + " " + PlatformTags.itemId(stack.getItem()) + " " + describe(material != null ? material : hint, Purity.get(stack, hint))
                    + " -> " + ingredient.test(stack));
            }
        }
        StringBuilder shown = new StringBuilder("display:");
        for (ItemStack stack : PlatformIngredients.displayStacks(ingredient)) {
            OptionalInt stored = Purity.stored(stack);
            shown.append(' ').append(PlatformTags.itemId(stack.getItem())).append(stored.isPresent() ? "@" + PurityScale.format(stored.getAsInt()) : "");
        }
        reply(context, shown.toString());
        return 1;
    }

    private static int fluidIngredient(CommandContext<CommandSourceStack> context) {
        var parsed = FluidPurityIngredient.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(StringArgumentType.getString(context, "json")));
        if (parsed.result().isEmpty()) {
            reply(context, "parse error: " + parsed.error().map(error -> error.message()).orElse("?"));
            return 0;
        }
        FluidPurityIngredient ingredient = parsed.result().get();
        reply(context, "encoded=" + FluidPurityIngredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).result().map(Object::toString).orElse("?"));
        StringBuilder shown = new StringBuilder("display:");
        for (FluidAmount fluid : ingredient.displayFluids()) {
            shown.append(' ').append(PlatformTags.fluidId(fluid.fluid())).append(' ').append(fluid.amount())
                .append(fluid.hasPurity() ? "@" + PurityScale.format(fluid.purity()) : "")
                .append(" test=").append(ingredient.test(fluid));
        }
        reply(context, shown.toString());
        String hint = ingredient.threshold().map(value -> value.material()).orElse(null);
        for (String id : new String[]{"minecraft:water", "minecraft:lava"}) {
            FluidAmount plain = FluidAmount.of(PlatformTags.fluid(id).orElseThrow(), 1000);
            reply(context, id + " " + describe(hint, Purity.get(plain, hint)) + " -> " + ingredient.test(plain));
        }
        return 1;
    }

    private static String describe(FluidAmount fluid) {
        if (fluid.isEmpty()) {
            return "empty";
        }
        String material = Purity.materialOf(fluid.fluid());
        return PlatformTags.fluidId(fluid.fluid()) + " " + fluid.amount() + " mB purity="
            + (fluid.hasPurity() ? describe(material, fluid.purity()) : "none")
            + " effective=" + describe(material, Purity.get(fluid));
    }

    private static int containerContents(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ItemStack stack = container(context).getItem(IntegerArgumentType.getInteger(context, "slot"));
        reply(context, PlatformTags.itemId(stack.getItem()) + " holds " + describe(PlatformFluidContainers.contained(stack)));
        return 1;
    }

    private static int fill(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Container container = container(context);
        int slot = IntegerArgumentType.getInteger(context, "slot");
        Fluid fluid = PlatformTags.fluid(StringArgumentType.getString(context, "fluid")).orElseThrow();
        FluidAmount amount = Purity.with(FluidAmount.of(fluid, 1000), parsePurity(StringArgumentType.getString(context, "percent"), Purity.materialOf(fluid)));
        ItemStack filled = PlatformFluidContainers.filled(container.getItem(slot), amount);
        reply(context, "filled with " + describe(amount) + " -> " + (filled.isEmpty() ? "failed" : PlatformTags.itemId(filled.getItem())
            + " stored=" + Purity.stored(filled).stream().mapToObj(b -> PurityScale.format(b) + "%").findFirst().orElse("none")));
        if (!filled.isEmpty()) {
            container.setItem(slot, filled);
        }
        return 1;
    }

    private static int drain(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Container container = container(context);
        int slot = IntegerArgumentType.getInteger(context, "slot");
        ItemStack stack = container.getItem(slot);
        FluidAmount held = PlatformFluidContainers.contained(stack);
        ItemStack drained = PlatformFluidContainers.drained(stack);
        reply(context, "drained " + describe(held) + " -> " + (drained.isEmpty() ? "failed" : PlatformTags.itemId(drained.getItem())));
        if (!drained.isEmpty()) {
            container.setItem(slot, drained);
        }
        return 1;
    }

    private static void reply(CommandContext<CommandSourceStack> context, String text) {
        context.getSource().sendSuccess(() -> Component.literal(text), false);
    }
}
