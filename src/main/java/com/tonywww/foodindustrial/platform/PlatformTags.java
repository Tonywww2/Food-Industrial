package com.tonywww.foodindustrial.platform;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tag keys and registry lookups by string id ({@code "namespace:path"}), so business code never
 * handles {@code ResourceLocation} / {@code Identifier}.
 */
public final class PlatformTags {
    private PlatformTags() {
    }

    public static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registries.ITEM, PlatformIdentifier.parse(id));
    }

    public static TagKey<Fluid> fluidTag(String id) {
        return TagKey.create(Registries.FLUID, PlatformIdentifier.parse(id));
    }

    public static TagKey<Block> blockTag(String id) {
        return TagKey.create(Registries.BLOCK, PlatformIdentifier.parse(id));
    }

    /** {@code namespace:path} of a tag key. */
    public static String tagId(TagKey<?> tag) {
        return tag.location().toString();
    }

    public static Optional<Item> item(String id) {
        return BuiltInRegistries.ITEM.getOptional(PlatformIdentifier.parse(id));
    }

    public static Optional<Fluid> fluid(String id) {
        return BuiltInRegistries.FLUID.getOptional(PlatformIdentifier.parse(id));
    }

    /** Items in an item tag, as currently bound (empty before tags load). */
    public static List<Item> itemsIn(TagKey<Item> tag) {
        List<Item> result = new ArrayList<>();
        for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            result.add(holder.value());
        }
        return result;
    }

    /** Source fluids in a fluid tag (flowing variants left out). */
    public static List<Fluid> sourceFluidsIn(TagKey<Fluid> tag) {
        List<Fluid> result = new ArrayList<>();
        for (var holder : BuiltInRegistries.FLUID.getTagOrEmpty(tag)) {
            Fluid fluid = holder.value();
            if (fluid.isSource(fluid.defaultFluidState())) {
                result.add(fluid);
            }
        }
        return result;
    }

    public static String itemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    public static String fluidId(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid).toString();
    }

    public static String blockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    /** Whether {@code id} is a syntactically valid resource id. */
    public static boolean isValidId(String id) {
        try {
            PlatformIdentifier.parse(id);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
