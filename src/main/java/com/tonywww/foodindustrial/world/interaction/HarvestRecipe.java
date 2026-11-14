package com.tonywww.foodindustrial.world.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.recipe.PlatformIngredients;
import com.tonywww.foodindustrial.recipe.Conditions;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * W02 harvesting (machines/01 §3.2, additives/07 H15, 2026-11-10): right-click a block with the tool
 * in the main hand and the container in the off hand. The container is used up; an item result goes to
 * the inventory, a fluid result (250 mB) comes in a liquid bottle. The tool wears by 1 and the block
 * rests for {@code cooldown} ticks.
 * <pre>
 * { "type": "foodindustrial:harvesting", "block": "#foodindustrial:harvest/latex",
 *   "tool": { "tag": "foodindustrial:knives" }, "container": { "item": "minecraft:glass_bottle" },
 *   "result": { "fluid": "foodindustrial:latex", "amount": 250 }, "cooldown": 6000 }
 * </pre>
 */
public record HarvestRecipe(String block, Ingredient tool, Optional<Ingredient> container, ProcessOutput result, int cooldown,
                            Conditions requirements) {
    public static final MapCodec<HarvestRecipe> CODEC = RecordCodecBuilder.<HarvestRecipe>mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("block").forGetter(HarvestRecipe::block),
        PlatformIngredients.codec().fieldOf("tool").forGetter(HarvestRecipe::tool),
        StrictCodecs.optional("container", PlatformIngredients.codec()).forGetter(HarvestRecipe::container),
        ProcessOutput.CODEC.fieldOf("result").forGetter(HarvestRecipe::result),
        Codec.intRange(0, 1_000_000).fieldOf("cooldown").forGetter(HarvestRecipe::cooldown),
        StrictCodecs.optional("requirements", Conditions.CODEC, Conditions.NONE).forGetter(HarvestRecipe::requirements)
    ).apply(instance, HarvestRecipe::new)).flatXmap(HarvestRecipe::validate, DataResult::success);

    private static DataResult<HarvestRecipe> validate(HarvestRecipe recipe) {
        if (!PlatformTags.isValidId(recipe.block.startsWith("#") ? recipe.block.substring(1) : recipe.block)) {
            return DataResult.error(() -> "harvesting: invalid block id");
        }
        if (recipe.result.isFluid() && recipe.result.amount() > FluidBottleItem.CAPACITY) {
            return DataResult.error(() -> "harvesting: a fluid result fills one bottle (" + FluidBottleItem.CAPACITY + " mB)");
        }
        return DataResult.success(recipe);
    }

    public boolean matches(BlockState state) {
        if (block.startsWith("#")) {
            return state.is(PlatformTags.blockTag(block.substring(1)));
        }
        return PlatformTags.blockId(state.getBlock()).equals(block);
    }
}
