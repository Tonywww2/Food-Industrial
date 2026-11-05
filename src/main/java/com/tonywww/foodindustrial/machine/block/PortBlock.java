package com.tonywww.foodindustrial.machine.block;

import com.tonywww.foodindustrial.gui.ModMenus;
import com.tonywww.foodindustrial.machine.entity.PortBlockEntity;
import com.tonywww.foodindustrial.platform.machine.PlatformBlockEntity;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

/** Block of a multiblock port; its block entity does not tick (machines/02 §6.1 rule 4). Opens the port screen. */
public class PortBlock extends PlatformEntityBlock {
    public PortBlock(Properties properties, Supplier<? extends BlockEntityType<? extends PlatformBlockEntity>> type) {
        super(properties, type);
    }

    @Override
    protected boolean ticksOnServer() {
        return false;
    }

    @Override
    protected UseResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, ItemStack held) {
        if (!held.isEmpty() || !(level.getBlockEntity(pos) instanceof PortBlockEntity port)) {
            return UseResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            ModMenus.open(serverPlayer, port);
        }
        return UseResult.SUCCESS;
    }
}
