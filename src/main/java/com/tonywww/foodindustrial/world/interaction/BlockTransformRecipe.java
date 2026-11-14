package com.tonywww.foodindustrial.world.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.Conditions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * W01 block transformation (machines/01 §3.2, additives/07 H11, 2026-11-10): a block turns into
 * another when a random tick or a nearby entity drives it and the requirements hold at the block.
 * <pre>
 * { "type": "foodindustrial:block_transform", "block": "#foodindustrial:nitre_soil_sources",
 *   "result": "foodindustrial:nitre_soil", "random_tick_chance": 0.01,
 *   "entities": "#foodindustrial:livestock", "entity_chance": 0.1,
 *   "requirements": { "environment": [ "sheltered" ] } }
 * </pre>
 *
 * @param block            block id or {@code #} block tag
 * @param result           block id of the result (default state)
 * @param randomTickChance chance per random tick (only blocks that take random ticks get any)
 * @param entities         entity type id or {@code #} entity type tag that drives it, if any
 * @param entityChance     chance per check of a driving entity
 * @param requirements     conditions at the block
 */
public record BlockTransformRecipe(String block, String result, double randomTickChance, Optional<String> entities, double entityChance,
                                   Conditions requirements) {
    public static final MapCodec<BlockTransformRecipe> CODEC = RecordCodecBuilder.<BlockTransformRecipe>mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("block").forGetter(BlockTransformRecipe::block),
        Codec.STRING.fieldOf("result").forGetter(BlockTransformRecipe::result),
        StrictCodecs.optional("random_tick_chance", Codec.doubleRange(0.0, 1.0), 0.0).forGetter(BlockTransformRecipe::randomTickChance),
        StrictCodecs.optional("entities", Codec.STRING).forGetter(BlockTransformRecipe::entities),
        StrictCodecs.optional("entity_chance", Codec.doubleRange(0.0, 1.0), 0.0).forGetter(BlockTransformRecipe::entityChance),
        StrictCodecs.optional("requirements", Conditions.CODEC, Conditions.NONE).forGetter(BlockTransformRecipe::requirements)
    ).apply(instance, BlockTransformRecipe::new)).flatXmap(BlockTransformRecipe::validate, DataResult::success);

    private static DataResult<BlockTransformRecipe> validate(BlockTransformRecipe recipe) {
        if (recipe.randomTickChance <= 0 && (recipe.entities.isEmpty() || recipe.entityChance <= 0)) {
            return DataResult.error(() -> "block_transform needs random_tick_chance or entities with entity_chance");
        }
        if (!PlatformTags.isValidId(recipe.block.startsWith("#") ? recipe.block.substring(1) : recipe.block)
            || !PlatformTags.isValidId(recipe.result)) {
            return DataResult.error(() -> "block_transform: invalid block or result id");
        }
        return DataResult.success(recipe);
    }

    /** Whether {@code state} is a block this recipe transforms. */
    public boolean matches(BlockState state) {
        if (block.startsWith("#")) {
            return state.is(PlatformTags.blockTag(block.substring(1)));
        }
        return PlatformTags.blockId(state.getBlock()).equals(block);
    }

    public Optional<Block> resultBlock() {
        return PlatformTags.block(result);
    }
}
