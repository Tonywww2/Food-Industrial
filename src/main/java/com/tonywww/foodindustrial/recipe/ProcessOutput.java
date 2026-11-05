package com.tonywww.foodindustrial.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * One recipe output (machines/01 §3.1): a concrete item with a count or a fluid with an amount in mB.
 * <pre>
 * { "item": "foodindustrial:salt", "count": 2, "purity": { "mode": "removal", "value": 50 } }
 * { "fluid": "foodindustrial:bittern", "amount": 50, "chance": 0.5, "min_tier": "t2" }
 * </pre>
 * The first output of a recipe is its main product; {@code chance} (0–1) is only for by-products.
 *
 * @param item    item, or {@code null} for a fluid output
 * @param fluid   fluid, or {@code null} for an item output
 * @param amount  item count or mB
 * @param minTier lowest device tier that produces this output, or {@code null}
 * @param chance  probability per completion, {@code 1} = always
 * @param purity  written purity rule, or {@code null} for the recipe type's default
 */
public record ProcessOutput(@Nullable Item item, @Nullable Fluid fluid, int amount, @Nullable Tier minTier,
                            double chance, @Nullable PurityRule purity) {
    private static final MapCodec<ProcessOutput> ITEM = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ProcessOutput::item),
        StrictCodecs.optional("count", Codec.intRange(1, 64), 1).forGetter(ProcessOutput::amount),
        StrictCodecs.optional("min_tier", Tier.CODEC).forGetter(output -> Optional.ofNullable(output.minTier)),
        StrictCodecs.optional("chance", Codec.DOUBLE, 1.0).forGetter(ProcessOutput::chance),
        StrictCodecs.optional("purity", PurityRule.CODEC).forGetter(output -> Optional.ofNullable(output.purity))
    ).apply(instance, (item, count, minTier, chance, purity) -> new ProcessOutput(item, null, count, minTier.orElse(null), chance, purity.orElse(null))));

    private static final MapCodec<ProcessOutput> FLUID = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(ProcessOutput::fluid),
        Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(ProcessOutput::amount),
        StrictCodecs.optional("min_tier", Tier.CODEC).forGetter(output -> Optional.ofNullable(output.minTier)),
        StrictCodecs.optional("chance", Codec.DOUBLE, 1.0).forGetter(ProcessOutput::chance),
        StrictCodecs.optional("purity", PurityRule.CODEC).forGetter(output -> Optional.ofNullable(output.purity))
    ).apply(instance, (fluid, amount, minTier, chance, purity) -> new ProcessOutput(null, fluid, amount, minTier.orElse(null), chance, purity.orElse(null))));

    /** Item outputs have {@code item}, fluid outputs {@code fluid}. */
    public static final Codec<ProcessOutput> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<ProcessOutput, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(map -> decodeMap(ops, map)).flatMap(ProcessOutput::validate).map(value -> Pair.of(value, input));
        }

        private <T> DataResult<ProcessOutput> decodeMap(DynamicOps<T> ops, MapLike<T> map) {
            if (map.get("fluid") != null) {
                return FLUID.decode(ops, map);
            }
            if (map.get("item") != null) {
                return ITEM.decode(ops, map);
            }
            return DataResult.error(() -> "Recipe output needs \"item\" or \"fluid\"");
        }

        @Override
        public <T> DataResult<T> encode(ProcessOutput output, DynamicOps<T> ops, T prefix) {
            return (output.isFluid() ? FLUID : ITEM).codec().encode(output, ops, prefix);
        }
    };

    public static ProcessOutput item(Item item, int count) {
        return new ProcessOutput(item, null, count, null, 1.0, null);
    }

    public static ProcessOutput fluid(Fluid fluid, int amount) {
        return new ProcessOutput(null, fluid, amount, null, 1.0, null);
    }

    public ProcessOutput withPurity(@Nullable PurityRule rule) {
        return new ProcessOutput(item, fluid, amount, minTier, chance, rule);
    }

    public ProcessOutput withChance(double probability) {
        return new ProcessOutput(item, fluid, amount, minTier, probability, purity);
    }

    public ProcessOutput withMinTier(@Nullable Tier tier) {
        return new ProcessOutput(item, fluid, amount, tier, chance, purity);
    }

    private static DataResult<ProcessOutput> validate(ProcessOutput output) {
        if (!(output.chance > 0.0 && output.chance <= 1.0)) {
            return DataResult.error(() -> "Output chance must be in (0, 1]: " + output.chance);
        }
        if (output.isFluid() ? output.fluid == Fluids.EMPTY : output.item == Items.AIR) {
            return DataResult.error(() -> "Output must not be empty");
        }
        return DataResult.success(output);
    }

    public boolean isFluid() {
        return fluid != null;
    }

    /** Whether a device of {@code tier} produces this output. */
    public boolean producedAt(Tier tier) {
        return minTier == null || tier.atLeast(minTier);
    }

    public boolean isChanced() {
        return chance < 1.0;
    }

    /** Whether the product carries a purity. */
    public boolean bearsPurity() {
        return isFluid() ? Purity.bears(fluid) : Purity.bears(item);
    }

    /** Material of the product, or {@code null}. */
    @Nullable
    public String material() {
        return isFluid() ? Purity.materialOf(fluid) : Purity.materialOf(item);
    }

    /** A fresh stack of {@code count} items (without purity); empty for fluid outputs. */
    public ItemStack stack(int count) {
        return isFluid() ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    /** The fluid of {@code mB} (without purity); empty for item outputs. */
    public FluidAmount fluidAmount(int mB) {
        return isFluid() ? FluidAmount.of(fluid, mB) : FluidAmount.EMPTY;
    }

    /** Registry id of the product. */
    public String id() {
        return isFluid() ? PlatformTags.fluidId(fluid) : PlatformTags.itemId(item);
    }
}
