package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformPlayers;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * H16 hand seawater pump (I43): empty-hand right click pumps one stroke and shows the tank on the action bar, or
 * that the place is not coastal; an empty bucket or liquid bottle takes the seawater out.
 */
public class HandPumpBlock extends PlatformEntityBlock {
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 14, 13);

    public HandPumpBlock(Properties properties, Supplier<? extends BlockEntityType<HandPumpBlockEntity>> type) {
        super(properties, type);
    }

    @Override
    protected VoxelShape shape(BlockState state) {
        return SHAPE;
    }

    @Override
    protected UseResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, ItemStack held) {
        if (!(level.getBlockEntity(pos) instanceof HandPumpBlockEntity pump)) {
            return UseResult.PASS;
        }
        if (!held.isEmpty()) {
            FluidAmount inside = pump.fluid();
            if (inside.isEmpty() || !PlatformFluidContainers.contained(held).isEmpty()) {
                return UseResult.PASS_TO_ITEM;
            }
            int unit = Math.min(inside.amount(), 1000);
            ItemStack result = PlatformFluidContainers.filled(held, inside.withAmount(unit));
            if (result.isEmpty()) {
                return UseResult.PASS_TO_ITEM;
            }
            if (!level.isClientSide()) {
                int used = PlatformFluidContainers.contained(result).amount();
                pump.set(inside.withAmount(inside.amount() - used));
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
        if (level.isClientSide()) {
            return UseResult.SUCCESS;
        }
        HandPumpBlockEntity.Stroke stroke = pump.stroke();
        if (stroke == HandPumpBlockEntity.Stroke.NOT_COASTAL) {
            PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", "missing"),
                Component.translatable(Lang.condition("environment.coastal"))));
        } else {
            FluidAmount now = pump.fluid();
            PlatformPlayers.actionBar(player, Component.empty()
                .append(now.isEmpty() ? Component.translatable(GuiText.key("tank", "empty")) : PlatformFluids.displayName(now))
                .append("  ").append(Component.translatable(GuiText.key("tank", "amount"), String.valueOf(now.amount()),
                    String.valueOf(HandPumpBlockEntity.CAPACITY))));
        }
        return UseResult.SUCCESS;
    }
}
