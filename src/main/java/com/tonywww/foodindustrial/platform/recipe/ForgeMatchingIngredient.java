package com.tonywww.foodindustrial.platform.recipe;

//? if forge {
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Forge 1.20.1 implementation of {@code foodindustrial:matching}: an ingredient that matches what the business code
 * decides ({@link PlatformIngredients#matching}), shown as the stacks it supplies. Built at run time for a recipe's
 * ingredient list (the food line's crafting-table steps, 2026-11-25); written out as those stacks, and read back as
 * "the item, with at least the stack's data", which is all a copy elsewhere can know.
 */
final class ForgeMatchingIngredient extends AbstractIngredient {
    static final IIngredientSerializer<ForgeMatchingIngredient> SERIALIZER = new Serializer();
    static final ResourceLocation ID = PlatformIdentifier.id("matching");

    private final Predicate<ItemStack> test;
    private final Supplier<List<ItemStack>> stacks;
    @Nullable
    private ItemStack[] display;

    ForgeMatchingIngredient(Predicate<ItemStack> test, Supplier<List<ItemStack>> stacks) {
        this.test = test;
        this.stacks = stacks;
    }

    /** Matches the item of any template whose data the stack contains. */
    static ForgeMatchingIngredient ofTemplates(List<ItemStack> templates) {
        List<ItemStack> copies = templates.stream().map(ItemStack::copy).toList();
        return new ForgeMatchingIngredient(stack -> copies.stream().anyMatch(template -> matchesTemplate(template, stack)), () -> copies);
    }

    private static boolean matchesTemplate(ItemStack template, ItemStack stack) {
        return stack.is(template.getItem()) && (template.getTag() == null || NbtUtils.compareNbt(template.getTag(), stack.getTag(), true));
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return stack != null && !stack.isEmpty() && test.test(stack);
    }

    @Override
    public ItemStack[] getItems() {
        if (display == null || checkInvalidation()) {
            markValid();
            display = stacks.get().stream().map(ItemStack::copy).toArray(ItemStack[]::new);
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
        return getItems().length == 0;
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
        json.addProperty("type", ID.toString());
        JsonArray list = new JsonArray();
        for (ItemStack stack : getItems()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            if (stack.getTag() != null) {
                entry.addProperty("nbt", stack.getTag().toString());
            }
            list.add(entry);
        }
        json.add("stacks", list);
        return json;
    }

    private static final class Serializer implements IIngredientSerializer<ForgeMatchingIngredient> {
        @Override
        public ForgeMatchingIngredient parse(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            List<ItemStack> stacks = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                stacks.add(buffer.readItem());
            }
            return ofTemplates(stacks);
        }

        @Override
        public ForgeMatchingIngredient parse(JsonObject json) {
            if (!json.has("stacks")) {
                throw new JsonParseException("foodindustrial:matching needs \"stacks\"");
            }
            List<ItemStack> stacks = new ArrayList<>();
            for (JsonElement element : json.getAsJsonArray("stacks")) {
                JsonObject entry = element.getAsJsonObject();
                Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(entry.get("item").getAsString()));
                if (item == null) {
                    continue;
                }
                ItemStack stack = new ItemStack(item);
                if (entry.has("nbt")) {
                    try {
                        CompoundTag tag = TagParser.parseTag(entry.get("nbt").getAsString());
                        stack.setTag(tag);
                    } catch (Exception exception) {
                        throw new JsonParseException("foodindustrial:matching: bad nbt " + entry.get("nbt"));
                    }
                }
                stacks.add(stack);
            }
            return ofTemplates(stacks);
        }

        @Override
        public void write(FriendlyByteBuf buffer, ForgeMatchingIngredient ingredient) {
            ItemStack[] items = ingredient.getItems();
            buffer.writeVarInt(items.length);
            for (ItemStack stack : items) {
                buffer.writeItem(stack);
            }
        }
    }
}
//?}
