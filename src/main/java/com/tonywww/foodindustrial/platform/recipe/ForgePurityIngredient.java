package com.tonywww.foodindustrial.platform.recipe;

//? if forge {
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tonywww.foodindustrial.api.purity.PurityIngredient;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityMatching;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Forge 1.20.1 implementation of {@code foodindustrial:purity}. Not simple (matching reads the stack's
 * purity); {@link #isEmpty()} delegates to the wrapped ingredient so serializers that drop empty
 * ingredients (e.g. Farmer's Delight's cooking pot) keep it. The threshold is resolved when the
 * recipe loads; the network carries the resolved byte and material.
 */
final class ForgePurityIngredient extends AbstractIngredient {
    static final IIngredientSerializer<ForgePurityIngredient> SERIALIZER = new Serializer();

    private final Ingredient base;
    private final PurityThreshold threshold;
    @Nullable
    private ItemStack[] display;

    ForgePurityIngredient(Ingredient base, PurityThreshold threshold) {
        this.base = base;
        this.threshold = threshold;
    }

    PurityThreshold threshold() {
        return threshold;
    }

    Ingredient base() {
        return base;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return stack != null && PurityMatching.test(base, threshold, stack);
    }

    @Override
    public ItemStack[] getItems() {
        if (display == null || checkInvalidation()) {
            markValid();
            display = PurityMatching.displayStacks(Arrays.asList(base.getItems()), threshold).toArray(ItemStack[]::new);
        }
        return display;
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        display = null;
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", PurityIngredient.TYPE);
        json.add("base", base.toJson());
        json.add("min_purity", threshold.writtenJson());
        if (threshold.explicitMaterial() != null) {
            json.addProperty("material", threshold.explicitMaterial());
        }
        return json;
    }

    private static final class Serializer implements IIngredientSerializer<ForgePurityIngredient> {
        @Override
        public ForgePurityIngredient parse(FriendlyByteBuf buffer) {
            Ingredient base = Ingredient.fromNetwork(buffer);
            int value = buffer.readVarInt();
            return new ForgePurityIngredient(base, PurityThreshold.received(value, buffer.readUtf()));
        }

        @Override
        public ForgePurityIngredient parse(JsonObject json) {
            if (!json.has("base") || !json.has("min_purity")) {
                throw new JsonParseException("foodindustrial:purity needs \"base\" and \"min_purity\"");
            }
            JsonElement baseJson = json.get("base");
            Ingredient base = Ingredient.fromJson(baseJson, false);
            String material = json.has("material") ? json.get("material").getAsString() : null;
            try {
                return new ForgePurityIngredient(base, PurityThreshold.resolve(json.get("min_purity"), material, Purity.materialHint(baseJson, false)));
            } catch (IllegalArgumentException exception) {
                throw new JsonParseException(exception.getMessage());
            }
        }

        @Override
        public void write(FriendlyByteBuf buffer, ForgePurityIngredient ingredient) {
            ingredient.base.toNetwork(buffer);
            buffer.writeVarInt(ingredient.threshold.value());
            buffer.writeUtf(ingredient.threshold.material() == null ? "" : ingredient.threshold.material());
        }
    }
}
//?}
