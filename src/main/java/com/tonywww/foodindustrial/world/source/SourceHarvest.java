package com.tonywww.foodindustrial.world.source;

import com.tonywww.foodindustrial.platform.world.PlantHarvest;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Picking an I44 plant (additives/08 §C: parts told apart by tool and stage). Ages 0–3: flowers at age 2 when the
 * species also fruits (age 3 otherwise), fruit at age 3, leaf products with shears at age 3. A hand pick needs an
 * empty hand or an item that is not a block (so blocks can still be placed against leaves).
 */
final class SourceHarvest implements PlantHarvest {
    private final List<SourceTable.Drop> flower;
    private final List<SourceTable.Drop> fruit;
    private final List<SourceTable.Drop> leaf;
    private final int resetAge;
    private final Supplier<ItemStack> pickBlock;

    SourceHarvest(List<SourceTable.Drop> flower, List<SourceTable.Drop> fruit, List<SourceTable.Drop> leaf, int resetAge,
                  Supplier<ItemStack> pickBlock) {
        this.flower = flower;
        this.fruit = fruit;
        this.leaf = leaf;
        this.resetAge = resetAge;
        this.pickBlock = pickBlock;
    }

    @Override
    @Nullable
    public Picked pick(int age, ItemStack held, RandomSource random) {
        if (held.getItem() instanceof ShearsItem) {
            return !leaf.isEmpty() && age == 3 ? new Picked(roll(leaf, random), resetAge, true) : null;
        }
        if (!held.isEmpty() && (held.is(Items.BONE_MEAL) || held.getItem() instanceof BlockItem)) {
            return null;
        }
        if (!fruit.isEmpty()) {
            if (age == 3) {
                return new Picked(roll(fruit, random), resetAge, false);
            }
            if (age == 2 && !flower.isEmpty()) {
                return new Picked(roll(flower, random), resetAge, false);
            }
            return null;
        }
        return !flower.isEmpty() && age == 3 ? new Picked(roll(flower, random), resetAge, false) : null;
    }

    @Override
    public ItemStack pickBlock() {
        return pickBlock.get();
    }

    /** The stage a part is picked at (for texts): 2 for flowers of a fruiting species, else 3. */
    static int flowerAge(SourceTable.Tree tree) {
        return tree.fruit().isEmpty() ? 3 : 2;
    }

    static List<ItemStack> roll(List<SourceTable.Drop> drops, RandomSource random) {
        List<ItemStack> result = new ArrayList<>();
        for (SourceTable.Drop drop : drops) {
            int count = drop.min() + (drop.max() > drop.min() ? random.nextInt(drop.max() - drop.min() + 1) : 0);
            if (count > 0) {
                result.add(new ItemStack(SourceBlocks.item(drop.item()), count));
            }
        }
        return result;
    }
}
