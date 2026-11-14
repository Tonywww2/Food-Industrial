package com.tonywww.foodindustrial.machine.workshop;

import com.tonywww.foodindustrial.machine.block.MachineBlock;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import com.tonywww.foodindustrial.platform.machine.PlatformUsableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Hand crank (I14, additives/06 §T1 作坊, 2026-11-10): sits on top of a manual machine (M01, M02) and
 * breaks off when the machine goes; each right click with an empty hand stores
 * {@link PowerKind#MANUAL_PER_TURN} ticks of running in the machine and turns the handle a quarter.
 */
public class HandCrankBlock extends PlatformUsableBlock {
    public static final String ID = "hand_crank";
    /** Quarter turns of the handle, for the model. */
    public static final IntegerProperty TURN = IntegerProperty.create("turn", 0, 3);
    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);

    public HandCrankBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TURN, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TURN);
    }

    @Override
    protected VoxelShape shape(BlockState state) {
        return SHAPE;
    }

    /** Only on top of a machine driven by hand. */
    @Override
    protected boolean canStay(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof MachineBlock machine && machine.definition().power() == PowerKind.MANUAL;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return canStay(context.getLevel(), context.getClickedPos()) ? defaultBlockState() : null;
    }

    @Override
    protected PlatformEntityBlock.UseResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                                  BlockHitResult hit, ItemStack held) {
        if (!held.isEmpty()) {
            return PlatformEntityBlock.UseResult.PASS_TO_ITEM;
        }
        if (!level.isClientSide() && level.getBlockEntity(pos.below()) instanceof MachineBlockEntity machine && machine.crank()) {
            level.setBlock(pos, state.setValue(TURN, (state.getValue(TURN) + 1) & 3), 3);
            level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.5F, 0.8F + level.getRandom().nextFloat() * 0.3F);
        }
        return PlatformEntityBlock.UseResult.SUCCESS;
    }
}
