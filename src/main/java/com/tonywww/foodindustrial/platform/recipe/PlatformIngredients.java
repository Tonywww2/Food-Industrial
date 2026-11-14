package com.tonywww.foodindustrial.platform.recipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.tonywww.foodindustrial.core.purity.PurityThreshold;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
//? if forge {
import com.mojang.serialization.DataResult;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Arrays;
//?} else {
/*import com.google.gson.JsonArray;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.neoforged.bus.api.IEventBus;
*///?}

/**
 * Registration and use of the purity ingredient {@code foodindustrial:purity} with each loader's
 * ingredient system: Forge {@code AbstractIngredient} + {@code IIngredientSerializer} on 1.20.1,
 * NeoForge {@code ICustomIngredient} + {@code IngredientType} from 1.21. Business code uses
 * {@code api.purity.PurityIngredient}.
 */
public final class PlatformIngredients {
    private PlatformIngredients() {
    }

    /** Ingredient matching {@code base} with effective purity reaching {@code threshold}. */
    public static Ingredient purity(Ingredient base, PurityThreshold threshold) {
        //? if forge {
        return new ForgePurityIngredient(base, threshold);
        //?} else {
        /*return new NeoPurityIngredient(base, threshold).toVanilla();
        *///?}
    }

    /** Threshold of a purity ingredient, or empty for any other ingredient. */
    public static Optional<PurityThreshold> purityThreshold(Ingredient ingredient) {
        //? if forge {
        return ingredient instanceof ForgePurityIngredient purity ? Optional.of(purity.threshold()) : Optional.empty();
        //?} else {
        /*return ingredient.getCustomIngredient() instanceof NeoPurityIngredient purity ? Optional.of(purity.threshold()) : Optional.empty();
        *///?}
    }

    /** The wrapped base of a purity ingredient; any other ingredient itself. */
    public static Ingredient unwrap(Ingredient ingredient) {
        //? if forge {
        return ingredient instanceof ForgePurityIngredient purity ? purity.base() : ingredient;
        //?} else {
        /*return ingredient.getCustomIngredient() instanceof NeoPurityIngredient purity ? purity.base() : ingredient;
        *///?}
    }

    /**
     * The tags and items an ingredient refers to, as JSON {@code Purity.materialHint} reads (works
     * before tags are bound, e.g. during data generation); {@code null} for custom ingredients.
     */
    @Nullable
    public static JsonElement references(Ingredient ingredient) {
        //? if forge {
        return ingredient.isVanilla() ? ingredient.toJson() : null;
        //?}
        //? if neoforge && <26.1 {
        /*if (ingredient.isCustom()) {
            return null;
        }
        JsonArray array = new JsonArray();
        for (Ingredient.Value value : ingredient.getValues()) {
            if (value instanceof Ingredient.TagValue tag) {
                array.add("#" + PlatformTags.tagId(tag.tag()));
            } else if (value instanceof Ingredient.ItemValue item) {
                array.add(PlatformTags.itemId(item.item().getItem()));
            } else {
                return null;
            }
        }
        return array;
        *///?}
        //? if >=26.1 {
        /*if (ingredient.isCustom()) {
            return null;
        }
        JsonArray array = new JsonArray();
        var tag = ingredient.getValues().unwrapKey();
        if (tag.isPresent()) {
            array.add("#" + PlatformTags.tagId(tag.get()));
        } else {
            ingredient.getValues().forEach(holder -> array.add(PlatformTags.itemId(holder.value())));
        }
        return array;
        *///?}
    }

    /**
     * Items an ingredient may accept regardless of purity (a purity ingredient's base items), each
     * once; for recipe indexes. Needs bound tags.
     */
    public static List<Item> candidateItems(Ingredient ingredient) {
        Set<Item> items = new LinkedHashSet<>();
        //? if forge {
        Ingredient base = fresh(ingredient instanceof ForgePurityIngredient purity ? purity.base() : ingredient);
        for (ItemStack stack : base.getItems()) {
            items.add(stack.getItem());
        }
        //?}
        //? if neoforge && <26.1 {
        /*Ingredient base = ingredient.getCustomIngredient() instanceof NeoPurityIngredient purity ? purity.base() : ingredient;
        for (ItemStack stack : base.getItems()) {
            items.add(stack.getItem());
        }
        *///?}
        //? if >=26.1 {
        /*ingredient.items().forEach(holder -> items.add(holder.value()));
        *///?}
        items.remove(Items.AIR);
        return new ArrayList<>(items);
    }

    /**
     * The ingredient with its item list rebuilt if tags changed since it was built (Forge 1.20.1 caches
     * the list of a tag ingredient for good); cheap when nothing changed. Other nodes: unchanged.
     */
    public static Ingredient fresh(Ingredient ingredient) {
        //? if forge {
        if (ingredient instanceof ForgePurityIngredient purity) {
            purity.base().getStackingIds();
        }
        ingredient.getStackingIds();
        //?}
        return ingredient;
    }

    /**
     * Stacks to show for an ingredient (purity ingredients list their qualifying purity variants); empty
     * when its tag has no items in this pack.
     */
    public static List<ItemStack> displayStacks(Ingredient ingredient) {
        //? if forge {
        List<ItemStack> stacks = Arrays.asList(fresh(ingredient).getItems());
        //?} else {
        /*List<ItemStack> stacks = NeoPurityIngredient.displayStacks(ingredient);
        *///?}
        return stacks.stream().anyMatch(PlatformIngredients::emptyTagMarker)
            ? stacks.stream().filter(stack -> !emptyTagMarker(stack)).toList() : stacks;
    }

    /** The barrier named "Empty Tag: …" that the loader shows for a tag without items (I22, 2026-11-11). */
    private static boolean emptyTagMarker(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.BARRIER) && stack.getHoverName().getString().startsWith("Empty Tag: ");
    }

    /** Codec of a non-empty ingredient in the node's JSON format (custom types included). */
    public static Codec<Ingredient> codec() {
        //? if forge {
        return ExtraCodecs.JSON.flatXmap(PlatformIngredients::fromJson, ingredient -> DataResult.success(ingredient.toJson()));
        //?} else {
        /*return NeoPurityIngredient.ingredientCodec();
        *///?}
    }

    //? if forge {
    private static DataResult<Ingredient> fromJson(JsonElement json) {
        try {
            return DataResult.success(Ingredient.fromJson(json, false));
        } catch (RuntimeException exception) {
            return DataResult.error(exception::getMessage);
        }
    }
    //?}

    /** Registers the ingredient type. Called by the mod entrypoint during construction. */
    public static void bind(IEventBus modBus) {
        //? if forge {
        modBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
                CraftingHelper.register(PlatformIdentifier.id("purity"), ForgePurityIngredient.SERIALIZER);
            }
        });
        // Forge 1.20.1 keeps an ingredient's item list once built; tag ingredients a client decodes from
        // the mod's recipes before the tags arrive would stay empty (I22). Every ingredient rebuilds once
        // after a tag update, the next time fresh() sees it.
        com.tonywww.foodindustrial.platform.PlatformEvents.onTagsUpdated(Ingredient::invalidateAll);
        //?} else {
        /*NeoPurityIngredient.bootstrap();
        *///?}
    }
}
