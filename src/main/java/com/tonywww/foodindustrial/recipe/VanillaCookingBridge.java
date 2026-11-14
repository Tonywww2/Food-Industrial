package com.tonywww.foodindustrial.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.google.gson.JsonElement;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.data.StrictCodecs;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.platform.recipe.PlatformVanillaCooking;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Bridge recipes of the vanilla cooking blocks (machines/01 §5, machines/02 §3; decided 2026-11-10:
 * data generation writes the files). Each R07–R09 recipe listing {@code vanilla_devices} gets one
 * file per block at {@code foodindustrial:vanilla/<block>/<recipe path>}:
 * <pre>
 * { "type": "foodindustrial:vanilla_smelting", "recipe": { "type": "foodindustrial:kiln", … } }
 * </pre>
 * The embedded recipe decides matching (purity thresholds included) and the result's purity, worked
 * out with the block's tier; the cooking time is the recipe time times the block's factor. A data pack
 * adding its own R07–R09 recipe for a vanilla block writes the bridge file the same way.
 */
public final class VanillaCookingBridge {
    /** Experience of a bridged result unless the file writes {@code experience} (game setting, as vanilla dried kelp). */
    public static final float EXPERIENCE = 0.1F;

    private static final RandomSource UNUSED = RandomSource.create(0);

    private VanillaCookingBridge() {
    }

    /** A bridge recipe: the R07–R09 recipe it runs and the experience. */
    public static final class Data {
        private final ProcessRecipe recipe;
        private final float experience;
        @Nullable
        private volatile Tier base;

        Data(ProcessRecipe recipe, float experience) {
            this.recipe = recipe;
            this.experience = experience;
        }

        public ProcessRecipe recipe() {
            return recipe;
        }

        /** Lowest tier among the devices that run the recipe (additives/10 §2.1 step 3). */
        Tier base() {
            Tier tier = base;
            if (tier == null) {
                List<DeviceSpec> runners = DeviceSpec.runnersOf(recipe);
                tier = runners.isEmpty() ? Tier.T0 : runners.get(0).tier();
                base = tier;
            }
            return tier;
        }
    }

    static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ProcessRecipe.TYPED_CODEC.fieldOf("recipe").forGetter(Data::recipe),
        StrictCodecs.optional("experience", Codec.FLOAT, EXPERIENCE).forGetter(data -> data.experience)
    ).apply(instance, Data::new));

    static void bootstrap() {
        VanillaDevice.registerSpecs();
        PlatformVanillaCooking.register(new PlatformVanillaCooking.Bridge<Data>() {
            @Override
            public MapCodec<Data> codec() {
                return CODEC;
            }

            @Override
            public Ingredient ingredient(Data data) {
                return input(data).ingredient();
            }

            @Override
            public Item resultItem(Data data) {
                return data.recipe.outputs().get(0).item();
            }

            @Override
            public int resultCount(Data data) {
                return data.recipe.outputs().get(0).amount();
            }

            @Override
            public int cookingTime(Data data, PlatformVanillaCooking.Block block) {
                return device(block).cookingTime(data.recipe);
            }

            @Override
            public float experience(Data data) {
                return data.experience;
            }

            @Override
            public boolean matches(Data data, ItemStack input) {
                return input(data).test(input);
            }

            @Override
            public ItemStack assemble(Data data, PlatformVanillaCooking.Block block, ItemStack input) {
                return VanillaCookingBridge.assemble(data, device(block), input);
            }
        });
    }

    private static ProcessInput.ItemInput input(Data data) {
        return (ProcessInput.ItemInput) data.recipe.inputs().get(0);
    }

    private static VanillaDevice device(PlatformVanillaCooking.Block block) {
        return VanillaDevice.valueOf(block.name());
    }

    /** The result of one item on {@code device}, with its purity; empty when the block may not make it. */
    public static ItemStack assemble(Data data, VanillaDevice device, ItemStack input) {
        ProcessRecipe recipe = data.recipe;
        ProcessOutput output = recipe.outputs().get(0);
        if (!output.producedAt(device.tier())) {
            return ItemStack.EMPTY;
        }
        ItemStack result = output.stack(output.amount());
        ProcessInput.ItemInput item = input(data);
        int b = OutcomeResolver.purity(recipe, 0, device.spec().profile(), data.base(), new int[]{item.purityOf(input)},
            new long[]{item.weight()}, UNUSED);
        return b == OutcomeResolver.NONE ? result : Purity.with(result, b);
    }

    /** Bridge file of {@code recipe} for {@code device}: the recipe as a data pack writes it, embedded. */
    public static JsonObject bridgeJson(ProcessRecipe recipe, VanillaDevice device, DynamicOps<JsonElement> ops) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PlatformVanillaCooking.Block.valueOf(device.name()).serializerId());
        json.add("recipe", RecipeGen.toJson(recipe, ops));
        return json;
    }
}
