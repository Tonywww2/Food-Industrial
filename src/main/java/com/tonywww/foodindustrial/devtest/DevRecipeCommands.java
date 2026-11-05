package com.tonywww.foodindustrial.devtest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.OutcomeResolver;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.PurityReport;
import com.tonywww.foodindustrial.recipe.RecipeChecks;
import com.tonywww.foodindustrial.recipe.RecipeGen;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Development commands for the recipe framework (I08):
 * <ul>
 *   <li>{@code /fidev recipes} — loaded recipes with base tier and devices, checks, and the JSON and
 *       network round trips (datapack files of the {@code fitest} pack compared too);</li>
 *   <li>{@code /fidev process <recipe> <device> [purity per input…]} — one completion: purity of each
 *       output and the average amount over 10,000 runs; purities as percent, {@code b<n>} or {@code -};</li>
 *   <li>{@code /fidev findrecipe <device> <item>*<count>[@<percent>] fluid:<fluid>*<mB>[@<percent>] …} —
 *       the index lookup for these contents;</li>
 *   <li>{@code /fidev purityreport} — the purity report of the loaded recipes, written to the world folder.</li>
 * </ul>
 */
final class DevRecipeCommands {
    private DevRecipeCommands() {
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fidev")
            .then(Commands.literal("recipes").executes(DevRecipeCommands::recipes))
            .then(Commands.literal("process")
                .then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevRecipeCommands::process)))
            .then(Commands.literal("findrecipe")
                .then(Commands.argument("args", StringArgumentType.greedyString()).executes(DevRecipeCommands::find)))
            .then(Commands.literal("purityreport").executes(DevRecipeCommands::report)));
    }

    private static int recipes(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        RecipeIndex index = RecipeIndex.get(level);
        int count = 0;
        for (RecipeIndex.Entry entry : index.all()) {
            count++;
            ProcessRecipe recipe = entry.recipe();
            List<String> runners = entry.runners().stream().map(device -> device.id() + "(" + device.tier().id() + ")").toList();
            reply(context, entry.id() + " [" + recipe.type().code() + "] base=" + (entry.baseTier() == null ? "none" : entry.baseTier().id())
                + " runners=" + runners);
            for (RecipeChecks.Problem problem : RecipeChecks.check(entry.id(), recipe, DeviceSpec.all())) {
                reply(context, "  " + problem);
            }
            DynamicOps<JsonElement> ops = PlatformRecipes.jsonOps(level);
            JsonElement encoded = RecipeGen.toJson(recipe, ops);
            DataResult<ProcessRecipe> decoded = ProcessRecipe.mapCodec(recipe.type()).codec().parse(ops, encoded);
            boolean json = decoded.result().map(again -> RecipeGen.toJson(again, ops).equals(encoded)).orElse(false);
            ProcessType type = recipe.type();
            boolean network = RecipeGen.toJson(PlatformRecipes.networkRoundTrip(level, type.kind(), recipe), ops).equals(encoded);
            String file = compareFile(context, entry.id(), encoded);
            reply(context, "  roundtrip json=" + json + " network=" + network + " file=" + file);
            if (!json || !network || file.startsWith("differs")) {
                FoodIndustrial.LOGGER.warn("Recipe {} encodes as {}", entry.id(), encoded);
            }
        }
        reply(context, count + " recipes, " + index.unrunnable().size() + " without device, " + DeviceSpec.all().size() + " devices");
        return count;
    }

    /** Compares the encoding with the recipe's file in the {@code fitest} data pack of the world. */
    private static String compareFile(CommandContext<CommandSourceStack> context, String id, JsonElement encoded) {
        int colon = id.indexOf(':');
        String namespace = id.substring(0, colon);
        if (!namespace.equals("fitest")) {
            return "n/a";
        }
        Path pack = context.getSource().getServer().getWorldPath(LevelResource.DATAPACK_DIR).resolve("fitest");
        Path path = pack.resolve(PlatformDatagen.relativePath(PlatformDatagen.Kind.RECIPE, namespace, id.substring(colon + 1)));
        try {
            JsonElement written = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
            return written.equals(encoded) ? "same" : "differs: " + written;
        } catch (IOException exception) {
            return "missing " + path;
        }
    }

    private static int process(CommandContext<CommandSourceStack> context) {
        String[] args = StringArgumentType.getString(context, "args").trim().split("\\s+");
        RecipeIndex.Entry entry = RecipeIndex.get(context.getSource().getLevel()).byId(args[0]);
        DeviceSpec device = args.length > 1 ? DeviceSpec.byId(args[1]) : null;
        if (entry == null || device == null) {
            reply(context, "usage: process <recipe> <device> [purity per input]; unknown recipe or device");
            return 0;
        }
        ProcessRecipe recipe = entry.recipe();
        String why = device.whyNot(recipe);
        if (why != null) {
            reply(context, device.id() + " cannot run " + entry.id() + ": " + why);
            return 0;
        }
        int size = recipe.inputs().size();
        int[] values = new int[size];
        long[] amounts = new long[size];
        for (int i = 0; i < size; i++) {
            String token = i + 2 < args.length ? args[i + 2] : "-";
            values[i] = token.equals("-") ? PurityData.NONE : token.startsWith("b") ? Integer.parseInt(token.substring(1)) : PurityScale.ofPercent(Double.parseDouble(token));
            amounts[i] = recipe.inputs().get(i).weight();
        }
        Tier base = entry.baseTier();
        DeviceProfile profile = device.profile();
        RandomSource random = RandomSource.create(42L);
        reply(context, entry.id() + " on " + device.id() + " (" + device.tier().id() + ", base " + base.id() + ", speed ×" + profile.speed()
            + ", " + profile.ticks(recipe) + " ticks, " + profile.energyPerTick(recipe) + " FE/t)");
        for (int index = 0; index < recipe.outputs().size(); index++) {
            int b = OutcomeResolver.purity(recipe, index, device.tier(), base, values, amounts, random);
            long total = 0;
            int runs = 10000;
            for (int run = 0; run < runs; run++) {
                total += OutcomeResolver.amount(recipe, index, profile, random);
            }
            String material = recipe.outputs().get(index).material();
            reply(context, "  " + recipe.outputs().get(index).id() + " purity=" + (b == PurityData.NONE ? "none" : PurityScale.format(b) + "% (b" + b + ", "
                + Purity.grade(material, b).map(grade -> grade.id()).orElse("content") + ")") + " amount avg=" + String.format("%.4f", total / (double) runs));
        }
        return 1;
    }

    private static int find(CommandContext<CommandSourceStack> context) {
        String[] args = StringArgumentType.getString(context, "args").trim().split("\\s+");
        DeviceSpec device = DeviceSpec.byId(args[0]);
        if (device == null) {
            reply(context, "unknown device " + args[0]);
            return 0;
        }
        List<ItemStack> items = new ArrayList<>();
        List<FluidAmount> fluids = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            String token = args[i];
            String purity = null;
            int at = token.indexOf('@');
            if (at >= 0) {
                purity = token.substring(at + 1);
                token = token.substring(0, at);
            }
            int star = token.indexOf('*');
            int amount = star >= 0 ? Integer.parseInt(token.substring(star + 1)) : 1;
            String id = star >= 0 ? token.substring(0, star) : token;
            if (id.startsWith("fluid:")) {
                FluidAmount fluid = FluidAmount.of(PlatformTags.fluid(id.substring(6)).orElseThrow(), amount);
                fluids.add(purity == null ? fluid : Purity.with(fluid, PurityScale.ofPercent(Double.parseDouble(purity))));
            } else {
                ItemStack stack = new ItemStack(PlatformTags.item(id).orElseThrow(), amount);
                items.add(purity == null ? stack : Purity.with(stack, PurityScale.ofPercent(Double.parseDouble(purity))));
            }
        }
        RecipeIndex.Found found = RecipeIndex.get(context.getSource().getLevel()).find(device, device.capabilities(), items, fluids);
        if (found == null) {
            reply(context, "no recipe for " + device.id());
            return 0;
        }
        reply(context, "found " + found.entry().id() + " slots=" + java.util.Arrays.toString(found.match().slots())
            + " tanks=" + java.util.Arrays.toString(found.match().tanks()));
        return 1;
    }

    private static int report(CommandContext<CommandSourceStack> context) {
        RecipeIndex index = RecipeIndex.get(context.getSource().getLevel());
        List<PlatformRecipes.Loaded<ProcessRecipe>> loaded = new ArrayList<>();
        for (RecipeIndex.Entry entry : index.all()) {
            loaded.add(new PlatformRecipes.Loaded<>(entry.id(), entry.recipe()));
        }
        Map<String, int[]> table = PurityReport.compute(loaded, DeviceSpec.all());
        String text = PurityReport.markdown(table);
        Path file = context.getSource().getServer().getWorldPath(LevelResource.ROOT).resolve("purity_report.md").normalize();
        try {
            Files.writeString(file, text, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            reply(context, "cannot write " + file + ": " + exception.getMessage());
            return 0;
        }
        for (String line : text.split("\n")) {
            if (line.startsWith("| foodindustrial:")) {
                reply(context, line);
            }
        }
        reply(context, table.size() + " materials, written to " + file);
        return table.size();
    }

    private static void reply(CommandContext<CommandSourceStack> context, String text) {
        context.getSource().sendSuccess(() -> Component.literal(text), false);
    }
}
