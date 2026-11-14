package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * H14 cold settling of tartar (additives/07 §操作与实现规格, 2026-11-10): wine in a wooden tank, in a
 * cold biome or next to ice, deposits one tartar on the walls every 3 days (half in a cold biome), up
 * to four; a knife scrapes them off. The wine is not used up. Tartar is potassium bitartrate at the
 * source purity 80% (additives/07 #55). Numbers are game settings.
 */
public final class Tartar {
    public static final int TIME = 72000;
    public static final int MAX = 4;
    public static final String WINE_TAG = "foodindustrial:wine";
    public static final String TARTAR = "foodindustrial:potassium_bitartrate";
    public static final double SOURCE_PERCENT = 80;

    private Tartar() {
    }

    /** Ticks to add for one batch of {@code delta} ticks at {@code pos}, 0 when the tank does not settle tartar now. */
    public static int progress(Level level, BlockPos pos, FluidAmount fluid, int delta) {
        if (fluid.isEmpty() || !fluid.fluid().defaultFluidState().is(PlatformTags.fluidTag(WINE_TAG))) {
            return 0;
        }
        boolean cold = Environment.at(level, pos).keys().contains("environment.cold_biome");
        if (cold) {
            return delta * 2;
        }
        for (Direction direction : Direction.values()) {
            BlockState next = level.getBlockState(pos.relative(direction));
            if (next.is(Blocks.ICE) || next.is(Blocks.PACKED_ICE) || next.is(Blocks.BLUE_ICE)) {
                return delta;
            }
        }
        return 0;
    }

    /** {@code count} tartar items. */
    public static ItemStack stack(int count) {
        return PlatformTags.item(TARTAR).map(item -> Purity.with(new ItemStack(item, count), PurityScale.ofPercent(SOURCE_PERCENT)))
            .orElse(ItemStack.EMPTY);
    }

    public static boolean isKnife(ItemStack stack) {
        return !stack.isEmpty() && stack.is(PlatformTags.itemTag("foodindustrial:knives"));
    }
}
