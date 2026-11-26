package com.tonywww.foodindustrial.devtest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.food.FoodCrafting;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@code /fidev foodsamples} (I38 acceptance): one sample recipe of each food-line type R20–R29 and a workbench step,
 * decoded, written back and sent through the recipe network encoding; reports each type and whether the JSON came
 * back unchanged.
 */
final class DevFoodCommands {
    private static final Map<ProcessType, String> SAMPLES = new LinkedHashMap<>();

    static {
        SAMPLES.put(ProcessTypes.FORMING, """
            {"inputs":[{"ingredient":{"item":"minecraft:wheat"},"count":3},{"fluid":{"type":"foodindustrial:fluid","fluid":"minecraft:water","amount":250}}],
             "outputs":[{"semi_finished":"steamed_bun|dough","count":2}],"time":100,"mode":"dough_mixing",
             "quality":{"items":["state",{"item":"taste","accepts":["sweetener","citric_acid"],"count":2}],"min_grade":"refined"}}""");
        SAMPLES.put(ProcessTypes.STERILIZING, """
            {"inputs":[{"fluid":{"type":"foodindustrial:fluid","fluid":"minecraft:water","amount":1000}}],
             "outputs":[{"fluid":"minecraft:water","amount":1000}],"time":200,"mode":"pasteurizing","requirements":{"temperature":"heated"}}""");
        SAMPLES.put(ProcessTypes.COOKING, """
            {"inputs":[{"semi_finished":"steamed_bun|dough","count":2}],"outputs":[{"item":"minecraft:bread"}],
             "time":200,"mode":"steaming","requirements":{"temperature":"heated","min_tier":"t2"}}""");
        SAMPLES.put(ProcessTypes.BAKING, """
            {"inputs":[{"semi_finished":"steamed_bun|dough"}],"outputs":[{"item":"minecraft:bread"}],"time":200,"mode":"baking",
             "catalyst":{"ingredient":{"item":"minecraft:iron_ingot"}},"requirements":{"temperature":"heated"}}""");
        SAMPLES.put(ProcessTypes.FRYING, """
            {"inputs":[{"ingredient":{"item":"minecraft:potato"}},{"fluid":{"type":"foodindustrial:fluid","fluid":"minecraft:water","amount":100}}],
             "outputs":[{"item":"minecraft:baked_potato"}],"time":100,"requirements":{"temperature":"heated"}}""");
        SAMPLES.put(ProcessTypes.CURING, """
            {"inputs":[{"ingredient":{"item":"minecraft:beef"},"count":4}],"outputs":[{"item":"minecraft:cooked_beef","count":4}],"time":12000,
             "mode":"aging","catalyst":{"ingredient":{"item":"minecraft:barrel"},"runs":32}}""");
        SAMPLES.put(ProcessTypes.CUTTING, """
            {"inputs":[{"ingredient":{"item":"minecraft:melon"}}],"outputs":[{"item":"minecraft:melon_slice","count":7},
             {"item":"minecraft:melon_seeds","chance":0.5}],"time":100,"mode":"slicing"}""");
        SAMPLES.put(ProcessTypes.CONFECTIONERY, """
            {"inputs":[{"ingredient":{"item":"minecraft:sugar"}},{"ingredient":{"item":"minecraft:cocoa_beans"}}],"outputs":[{"item":"minecraft:cookie"}],
             "time":200,"mode":"tempering","requirements":{"temperature":"cooling"}}""");
        SAMPLES.put(ProcessTypes.CHILLING, """
            {"inputs":[{"fluid":{"type":"foodindustrial:fluid","fluid":"minecraft:water","amount":1000}}],"outputs":[{"item":"minecraft:ice"}],
             "time":400,"mode":"quick_freezing","requirements":{"temperature":"freezing"}}""");
        SAMPLES.put(ProcessTypes.PACKAGING, """
            {"inputs":[{"ingredient":{"item":"minecraft:bread"}},{"ingredient":{"item":"minecraft:paper"}}],"outputs":[{"item":"minecraft:bread"}],"time":40}""");
    }

    private static final String CRAFTING = """
        {"process":"forming","ingredients":[{"semi_finished":"steamed_bun|dough"}],"tools":[{"item":"minecraft:shears"}],
         "result":{"item":"minecraft:bread","count":2},"byproduct":{"item":"minecraft:wheat_seeds"},"quality":{"items":["state"]}}""";

    static {
        // stage of the samples and of the in-game workbench test (dev only)
        com.tonywww.foodindustrial.food.SemiFinished.stage("dough", "面团", "Dough");
    }

    private DevFoodCommands() {
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fidev").then(Commands.literal("foodsamples").executes(DevFoodCommands::samples)));
        dispatcher.register(Commands.literal("fidev").then(Commands.literal("setquality")
            .then(Commands.argument("code", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
                .executes(DevFoodCommands::setQuality))));
    }

    private static int samples(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        DynamicOps<JsonElement> ops = PlatformRecipes.jsonOps(level);
        int ok = 0;
        for (Map.Entry<ProcessType, String> sample : SAMPLES.entrySet()) {
            ProcessType type = sample.getKey();
            JsonElement written = JsonParser.parseString(sample.getValue());
            DataResult<ProcessRecipe> parsed = ProcessRecipe.mapCodec(type).codec().parse(ops, written);
            if (parsed.result().isEmpty()) {
                line(context, type.code() + " " + type.id() + " FAILED: " + parsed.error().map(error -> error.message()).orElse("?"));
                continue;
            }
            ProcessRecipe recipe = parsed.result().get();
            ProcessRecipe back = PlatformRecipes.networkRoundTrip(level, type.kind(), recipe);
            JsonElement before = ProcessRecipe.mapCodec(type).codec().encodeStart(ops, recipe).result().orElseThrow();
            JsonElement after = ProcessRecipe.mapCodec(type).codec().encodeStart(ops, back).result().orElseThrow();
            boolean same = before.equals(after);
            ok += same ? 1 : 0;
            line(context, type.code() + " " + type.id() + (same ? " ok" : " CHANGED") + " quality=" + recipe.quality().isPresent()
                + " mode=" + recipe.mode().orElse("-") + " catalysts=" + recipe.catalysts().size() + " " + after);
        }
        DataResult<FoodCrafting> crafting = FoodCrafting.CODEC.codec().parse(ops, JsonParser.parseString(CRAFTING));
        if (crafting.result().isPresent()) {
            JsonElement again = FoodCrafting.CODEC.codec().encodeStart(ops, crafting.result().get()).result().orElseThrow();
            line(context, "workbench ok " + again);
            ok++;
        } else {
            line(context, "workbench FAILED: " + crafting.error().map(error -> error.message()).orElse("?"));
        }
        line(context, ok + " of " + (SAMPLES.size() + 1) + " samples ok");
        return ok;
    }

    /** {@code /fidev setquality <code>}: stamps the held item as a production step would (I39 saturation test). */
    private static int setQuality(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        net.minecraft.world.entity.player.Player player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack held = player.getMainHandItem();
        int code = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "code");
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, com.tonywww.foodindustrial.food.FoodLine.withQuality(held, code));
        line(context, "quality " + code + " -> " + held.getHoverName().getString());
        return 1;
    }

    private static void line(CommandContext<CommandSourceStack> context, String text) {
        FoodIndustrial.LOGGER.info("[foodsamples] {}", text);
        context.getSource().sendSuccess(() -> Component.literal(text), false);
    }
}
