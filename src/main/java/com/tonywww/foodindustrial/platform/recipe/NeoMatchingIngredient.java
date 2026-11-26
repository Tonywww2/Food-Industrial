package com.tonywww.foodindustrial.platform.recipe;

//? if neoforge {
/*import com.mojang.serialization.MapCodec;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
*///?}
//? if >=26.1 {
/*import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
*///?}

//? if neoforge {
/*/^*
 * NeoForge implementation of {@code foodindustrial:matching} (1.21.1 and 26.1): an ingredient that matches what the
 * business code decides ({@link PlatformIngredients#matching}), shown as the stacks it supplies. Built at run time for a
 * recipe's ingredient list (the food line's crafting-table steps, 2026-11-25); written out as those stacks, and read
 * back as "the item, with at least the stack's components", which is all a copy elsewhere can know.
 ^/
record NeoMatchingIngredient(Predicate<ItemStack> test, Supplier<List<ItemStack>> stacks) implements ICustomIngredient {
    static final MapCodec<NeoMatchingIngredient> CODEC = ItemStack.CODEC.listOf().fieldOf("stacks")
        .xmap(NeoMatchingIngredient::ofTemplates, ingredient -> ingredient.stacks().get());
    static final StreamCodec<RegistryFriendlyByteBuf, NeoMatchingIngredient> STREAM_CODEC =
        ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    static final Supplier<IngredientType<NeoMatchingIngredient>> TYPE = PlatformRegistry.register(
        NeoForgeRegistries.Keys.INGREDIENT_TYPES, "matching", () -> new IngredientType<>(CODEC, STREAM_CODEC));

    /^* Triggers the type registration during mod construction. ^/
    static void bootstrap() {
    }

    /^* Matches the item of any template whose components the stack has with the same values. ^/
    static NeoMatchingIngredient ofTemplates(List<ItemStack> templates) {
        List<ItemStack> copies = templates.stream().map(ItemStack::copy).toList();
        return new NeoMatchingIngredient(stack -> copies.stream().anyMatch(template -> matchesTemplate(template, stack)), () -> copies);
    }

    private static boolean matchesTemplate(ItemStack template, ItemStack stack) {
        if (!stack.is(template.getItem())) {
            return false;
        }
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : template.getComponentsPatch().entrySet()) {
            if (entry.getValue().isPresent() && !Objects.equals(stack.get(entry.getKey()), entry.getValue().get())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean test(ItemStack stack) {
        return !stack.isEmpty() && test.test(stack);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return TYPE.get();
    }
*///?}
//? if neoforge && <26.1 {
/*
    @Override
    public Stream<ItemStack> getItems() {
        return stacks.get().stream().map(ItemStack::copy);
    }
*///?}
//? if >=26.1 {
/*
    @Override
    @SuppressWarnings("deprecation")
    public Stream<Holder<Item>> items() {
        return stacks.get().stream().<Holder<Item>>map(stack -> stack.getItem().builtInRegistryHolder()).distinct();
    }

    @Override
    public SlotDisplay display() {
        return new SlotDisplay.Composite(stacks.get().stream()
            .<SlotDisplay>map(stack -> new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack)))
            .toList());
    }
*///?}
//? if neoforge {
/*}
*///?}
