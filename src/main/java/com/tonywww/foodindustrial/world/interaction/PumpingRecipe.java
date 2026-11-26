package com.tonywww.foodindustrial.world.interaction;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.recipe.PlatformRecipes;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.Conditions;
import com.tonywww.foodindustrial.recipe.Environment;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * W03 pumping (I43, additives/07 §H16, M54, machines/01 §2): no inputs, a fluid result per batch and the conditions
 * where the pump stands. The hand pump draws an eighth of a batch per right click, the electric pump a batch every
 * 40 ticks.
 * <pre>
 * { "type": "foodindustrial:pumping", "result": { "fluid": "foodindustrial:seawater", "amount": 1000 },
 *   "requirements": { "environment": [ "coastal" ] } }
 * </pre>
 */
public record PumpingRecipe(ProcessOutput result, Conditions requirements) {
    public static final MapCodec<PumpingRecipe> CODEC = RecordCodecBuilder.<PumpingRecipe>mapCodec(instance -> instance.group(
        ProcessOutput.CODEC.fieldOf("result").forGetter(PumpingRecipe::result),
        StrictCodecs.optional("requirements", Conditions.CODEC, Conditions.NONE).forGetter(PumpingRecipe::requirements)
    ).apply(instance, PumpingRecipe::new)).flatXmap(PumpingRecipe::validate, DataResult::success);

    public static final PlatformRecipes.Kind<PumpingRecipe> KIND = PlatformRecipes.register("pumping", CODEC);

    private static DataResult<PumpingRecipe> validate(PumpingRecipe recipe) {
        return recipe.result.isFluid() ? DataResult.success(recipe) : DataResult.error(() -> "pumping: the result is a fluid");
    }

    /** Loads the class, so the recipe kind registers. Called during mod construction. */
    public static void bootstrap() {
    }

    /** The batch the first recipe whose conditions {@code here} meets gives, or {@code null}. */
    @Nullable
    public static FluidAmount batch(Level level, Capabilities here) {
        for (PumpingRecipe recipe : PlatformRecipes.all(level, KIND)) {
            if (here.covers(recipe.requirements().required())) {
                return recipe.result().fluidAmount(recipe.result().amount());
            }
        }
        return null;
    }

    /** {@link #batch} at {@code pos}: the environment there, coastal included. */
    @Nullable
    public static FluidAmount batchAt(Level level, BlockPos pos) {
        return batch(level, Environment.at(level, pos));
    }
}
