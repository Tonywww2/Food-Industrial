package com.tonywww.foodindustrial.logistics;

import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformPlayers;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Block of a wooden or metal tank (machines/03 §5, 2026-11-05): empty-hand right click shows the
 * contents, empty-hand Shift + right click toggles drain mode, a fluid container pours in or takes out
 * one container. Dropped items keep the block's own fluid. Metal tanks refuse to be placed where they
 * would mix two fluids in one column or make a column taller than 8.
 */
public class TankBlock extends PlatformEntityBlock {
    private final TankKind kind;

    public TankBlock(Properties properties, TankKind kind, Supplier<? extends BlockEntityType<TankBlockEntity>> type) {
        super(properties, type);
        this.kind = kind;
    }

    public TankKind kind() {
        return kind;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!kind.stacks()) {
            return defaultBlockState();
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidAmount carried = TankBlockItem.fluid(context.getItemInHand());
        List<TankBlockEntity> below = run(level, pos, -1);
        List<TankBlockEntity> above = run(level, pos, 1);
        String problem = null;
        if (below.size() + above.size() + 1 > kind.maxColumn()) {
            problem = "too_tall";
        } else {
            FluidAmount fluid = carried;
            for (FluidAmount part : new FluidAmount[]{TankBlockEntity.total(below), TankBlockEntity.total(above)}) {
                if (!part.isEmpty()) {
                    if (!fluid.isEmpty() && fluid.fluid() != part.fluid()) {
                        problem = "mixed";
                    }
                    fluid = part;
                }
            }
        }
        if (problem != null) {
            if (!level.isClientSide() && context.getPlayer() != null) {
                PlatformPlayers.actionBar(context.getPlayer(), Component.translatable(GuiText.key("tank", problem)));
            }
            return null;
        }
        return defaultBlockState();
    }

    /** Tanks of this kind directly above ({@code step} 1) or below (-1), nearest first. */
    private List<TankBlockEntity> run(Level level, BlockPos pos, int step) {
        List<TankBlockEntity> run = new ArrayList<>();
        BlockPos next = pos.above(step);
        while (run.size() <= kind.maxColumn() && level.getBlockEntity(next) instanceof TankBlockEntity tank && tank.kind() == kind) {
            run.add(tank);
            next = next.above(step);
        }
        return run;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TankBlockEntity tank) {
            tank.load(TankBlockItem.fluid(stack), false);
            tank.settle();
        }
    }

    @Override
    protected UseResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, ItemStack held) {
        if (!(level.getBlockEntity(pos) instanceof TankBlockEntity tank)) {
            return UseResult.PASS;
        }
        if (!held.isEmpty()) {
            UseResult result = useContainer(level, tank, player, hand, held);
            return result == UseResult.PASS ? UseResult.PASS_TO_ITEM : result;
        }
        if (level.isClientSide()) {
            return UseResult.SUCCESS;
        }
        if (player.isSecondaryUseActive()) {
            boolean draining = tank.toggleDraining();
            PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("tank", draining ? "draining_on" : "draining_off")));
        } else {
            PlatformPlayers.actionBar(player, describe(tank));
        }
        return UseResult.SUCCESS;
    }

    /** Pours a full container in or fills an empty one (one container's worth). */
    private UseResult useContainer(Level level, TankBlockEntity tank, Player player, InteractionHand hand, ItemStack held) {
        FluidAmount inside = PlatformFluidContainers.contained(held);
        List<TankBlockEntity> column = tank.column();
        FluidAmount contents = TankBlockEntity.total(column);
        int capacity = column.size() * kind.capacity();
        ItemStack result;
        FluidAmount after;
        if (!inside.isEmpty()) {
            if (!kind.accepts(inside.fluid()) || (!contents.isEmpty() && contents.fluid() != inside.fluid())
                || capacity - contents.amount() < inside.amount()) {
                return UseResult.PASS;
            }
            result = PlatformFluidContainers.drained(held);
            if (result.isEmpty() && held.getCount() > 1) {
                return UseResult.PASS;
            }
            after = contents.isEmpty() ? inside : Purity.merge(contents, inside);
        } else {
            if (contents.isEmpty()) {
                return UseResult.PASS;
            }
            int unit = Math.min(contents.amount(), 1000);
            result = PlatformFluidContainers.filled(held, contents.withAmount(unit));
            if (result.isEmpty()) {
                return UseResult.PASS;
            }
            after = contents.withAmount(contents.amount() - unit);
        }
        if (!level.isClientSide()) {
            TankBlockEntity.distribute(column, after);
            if (!player.getAbilities().instabuild) {
                if (held.getCount() == 1) {
                    player.setItemInHand(hand, result);
                } else {
                    held.shrink(1);
                    player.getInventory().placeItemBackInInventory(result);
                }
            }
        }
        return UseResult.SUCCESS;
    }

    /** {@code 水 3000 / 4000 mB · 精制（97.1%） · 自动排出：开} for the action bar. */
    static Component describe(TankBlockEntity tank) {
        List<TankBlockEntity> column = tank.column();
        FluidAmount contents = TankBlockEntity.total(column);
        int capacity = column.size() * tank.kind().capacity();
        MutableComponent line = Component.empty();
        if (contents.isEmpty()) {
            line.append(Component.translatable(GuiText.key("tank", "empty")));
        } else {
            line.append(PlatformFluids.displayName(contents));
        }
        line.append("  ").append(Component.translatable(GuiText.key("tank", "amount"), String.valueOf(contents.amount()), String.valueOf(capacity)));
        if (!contents.isEmpty() && Purity.bears(contents.fluid())) {
            List<Component> purity = new ArrayList<>(1);
            Tooltips.purityLine(Purity.materialOf(contents.fluid()), Purity.get(contents), purity);
            purity.forEach(part -> line.append("  ").append(part));
        }
        line.append("  ").append(Component.translatable(GuiText.key("tank", tank.draining() ? "draining_on" : "draining_off")));
        return line;
    }

    @Override
    protected List<ItemStack> adjustDrops(BlockState state, @Nullable BlockEntity blockEntity, List<ItemStack> drops) {
        if (!(blockEntity instanceof TankBlockEntity tank) || tank.fluid().isEmpty()) {
            return drops;
        }
        List<ItemStack> result = new ArrayList<>(drops.size());
        for (ItemStack drop : drops) {
            ItemStack copy = drop.copy();
            if (copy.getItem() instanceof TankBlockItem && tank.getLevel() != null) {
                TankBlockItem.setFluid(copy, tank.fluid(), tank.getLevel().registryAccess());
            }
            result.add(copy);
        }
        return result;
    }
}
