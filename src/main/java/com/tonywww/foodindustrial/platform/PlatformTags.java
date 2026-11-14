package com.tonywww.foodindustrial.platform;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    public static Optional<Block> block(String id) {
        return BuiltInRegistries.BLOCK.getOptional(PlatformIdentifier.parse(id));
    }

    public static TagKey<EntityType<?>> entityTag(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, PlatformIdentifier.parse(id));
    }

    /** Whether the entity's type is {@code id} or, for {@code #tag}, in that tag. */
    public static boolean entityMatches(Entity entity, String idOrTag) {
        if (idOrTag.startsWith("#")) {
            return entity.getType().builtInRegistryHolder().is(entityTag(idOrTag.substring(1)));
        }
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString().equals(idOrTag);
    }

    /** Items in an item tag, as currently bound (empty before tags load). */
    public static List<Item> itemsIn(TagKey<Item> tag) {
        List<Item> result = new ArrayList<>();
        for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            result.add(holder.value());
        }
        return result;
    }

    /** Blocks named by a block id or {@code #} block tag, as currently bound (empty before tags load). */
    public static List<Block> blocks(String idOrTag) {
        List<Block> result = new ArrayList<>();
        if (idOrTag.startsWith("#")) {
            for (var holder : BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag(idOrTag.substring(1)))) {
                result.add(holder.value());
            }
        } else {
            block(idOrTag).ifPresent(result::add);
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

    /** Network id of an item (same on server and client of one game). */
    public static int itemRawId(Item item) {
        return BuiltInRegistries.ITEM.getId(item);
    }

    /** Item of a network id, or empty when unknown. */
    public static Optional<Item> itemByRawId(int id) {
        Item item = BuiltInRegistries.ITEM.byId(id);
        return item == null || item == net.minecraft.world.item.Items.AIR ? Optional.empty() : Optional.of(item);
    }

    /** Network id of a fluid. */
    public static int fluidRawId(Fluid fluid) {
        return BuiltInRegistries.FLUID.getId(fluid);
    }

    /** Fluid of a network id, or empty when unknown. */
    public static Optional<Fluid> fluidByRawId(int id) {
        Fluid fluid = BuiltInRegistries.FLUID.byId(id);
        return fluid == null || fluid == net.minecraft.world.level.material.Fluids.EMPTY ? Optional.empty() : Optional.of(fluid);
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
