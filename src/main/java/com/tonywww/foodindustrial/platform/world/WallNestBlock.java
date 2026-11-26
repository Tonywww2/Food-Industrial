package com.tonywww.foodindustrial.platform.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
//? if >=1.21 {
/*import com.mojang.serialization.MapCodec;
*///?}

/**
 * A nest stuck to a rock face (I44: the swiftlet nest of 08 §C 燕根): {@code facing} points to the wall it hangs on,
 * which must be sturdy. It does not fall when the wall goes (like a placed decoration).
 */
public class WallNestBlock extends HorizontalDirectionalBlock {
    //? if >=1.21 {
    /*public static final MapCodec<WallNestBlock> CODEC = simpleCodec(WallNestBlock::new);
    *///?}
    private static final VoxelShape NORTH = Block.box(3.0, 5.0, 0.0, 13.0, 11.0, 6.0);
    private static final VoxelShape SOUTH = Block.box(3.0, 5.0, 10.0, 13.0, 11.0, 16.0);
    private static final VoxelShape WEST = Block.box(0.0, 5.0, 3.0, 6.0, 11.0, 13.0);
    private static final VoxelShape EAST = Block.box(10.0, 5.0, 3.0, 16.0, 11.0, 13.0);

    public WallNestBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    //? if >=1.21 {
    /*@Override
    protected MapCodec<WallNestBlock> codec() {
        return CODEC;
    }
    *///?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> NORTH;
        };
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction wall = state.getValue(FACING);
        BlockPos support = pos.relative(wall);
        return level.getBlockState(support).isFaceSturdy(level, support, wall.getOpposite());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                BlockState state = defaultBlockState().setValue(FACING, direction);
                if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return state;
                }
            }
        }
        return null;
    }
}
