package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Block of a T0 device (additives/07 §操作与实现规格, 2026-11-10). Contents are put in and taken out on
 * the block itself, without a screen; see {@link T0DeviceBlockEntity#use}. The crock has a lid
 * ({@link #SEALED}); salt pans join their horizontal neighbours ({@link #NORTH} …).
 */
public class T0DeviceBlock extends PlatformEntityBlock {
    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    private static final VoxelShape POT = Block.box(2, 0, 2, 14, 8, 14);
    private static final VoxelShape CROCK = Block.box(3, 0, 3, 13, 14, 13);
    private static final VoxelShape PAN = Block.box(0, 0, 0, 16, 4, 16);
    private static final VoxelShape RACK = Block.box(0, 0, 0, 16, 12, 16);
    private static final VoxelShape TRAY = Block.box(1, 0, 1, 15, 3, 15);

    /** Set before the block is constructed so {@link #createBlockStateDefinition} can see the kind. */
    private static final ThreadLocal<T0Kind> CONSTRUCTING = new ThreadLocal<>();

    private final T0Kind kind;

    public T0DeviceBlock(Properties properties, T0Kind kind, Supplier<? extends BlockEntityType<? extends T0DeviceBlockEntity>> type) {
        super(properties, type);
        this.kind = kind;
        BlockState state = stateDefinition.any();
        if (kind == T0Kind.CROCK) {
            state = state.setValue(SEALED, false);
        } else if (kind == T0Kind.SALT_PAN) {
            state = state.setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false);
        }
        registerDefaultState(state);
    }

    /** Creates the block of {@code kind}; the kind decides the block state properties. */
    public static T0DeviceBlock create(Properties properties, T0Kind kind, Supplier<? extends BlockEntityType<? extends T0DeviceBlockEntity>> type) {
        CONSTRUCTING.set(kind);
        try {
            return new T0DeviceBlock(properties, kind, type);
        } finally {
            CONSTRUCTING.remove();
        }
    }

    public T0Kind kind() {
        return kind;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        T0Kind constructing = CONSTRUCTING.get();
        if (constructing == T0Kind.CROCK) {
            builder.add(SEALED);
        } else if (constructing == T0Kind.SALT_PAN) {
            builder.add(NORTH, EAST, SOUTH, WEST);
        }
    }

    @Override
    protected VoxelShape shape(BlockState state) {
        return switch (kind) {
            case CLAY_POT, IRON_POT -> POT;
            case CROCK -> CROCK;
            case SALT_PAN -> PAN;
            case DRYING_RACK -> RACK;
            case KOJI_TRAY -> TRAY;
            default -> Shapes.block();
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        if (kind == T0Kind.SALT_PAN) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                state = connect(state, direction, context.getLevel().getBlockState(context.getClickedPos().relative(direction)));
            }
        }
        return state;
    }

    @Override
    protected BlockState onNeighborShape(BlockState state, Direction direction, BlockState neighbor, BlockGetter level, BlockPos pos) {
        return kind == T0Kind.SALT_PAN && direction.getAxis().isHorizontal() ? connect(state, direction, neighbor) : state;
    }

    private BlockState connect(BlockState state, Direction direction, BlockState neighbor) {
        boolean joined = neighbor.getBlock() == this;
        return switch (direction) {
            case NORTH -> state.setValue(NORTH, joined);
            case EAST -> state.setValue(EAST, joined);
            case SOUTH -> state.setValue(SOUTH, joined);
            case WEST -> state.setValue(WEST, joined);
            default -> state;
        };
    }

    @Override
    protected UseResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, ItemStack held) {
        if (!(level.getBlockEntity(pos) instanceof T0DeviceBlockEntity device)) {
            return UseResult.PASS;
        }
        if (hand != InteractionHand.MAIN_HAND) {
            return UseResult.PASS;
        }
        return device.use(player, hand, held, hit);
    }

    @Override
    protected List<ItemStack> adjustDrops(BlockState state, @Nullable BlockEntity blockEntity, List<ItemStack> drops) {
        if (blockEntity instanceof T0DeviceBlockEntity device) {
            List<ItemStack> all = new java.util.ArrayList<>(drops);
            all.addAll(device.dropContents());
            return all;
        }
        return drops;
    }
}
