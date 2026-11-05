package com.tonywww.foodindustrial.machine.block;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.gui.ModMenus;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.tool.MachineTool;
import com.tonywww.foodindustrial.platform.PlatformItemData;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Block of a machine or multiblock controller: horizontal facing (front towards the player who placed
 * it) and {@code active} while processing (switched off only after 40 idle ticks, machines/02 §6.3
 * rule 11).
 */
public class MachineBlock extends PlatformEntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private final MachineDefinition definition;

    public MachineBlock(Properties properties, MachineDefinition definition) {
        super(properties, definition::blockEntityType);
        this.definition = definition;
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    public MachineDefinition definition() {
        return definition;
    }

    /** Front of a machine block state. */
    public static Direction facing(BlockState state) {
        return state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, ACTIVE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MachineBlockEntity machine) {
            DataReader config = PlatformItemData.read(stack, MachineBlockEntity.CONFIG_ITEM_KEY, level.registryAccess());
            if (config != null) {
                machine.applyConfig(config);
                machine.settingsApplied();
            }
            machine.onPlaced();
        }
    }

    /** Tools act first (machines/04 §4); otherwise the machine's screen opens. */
    @Override
    protected UseResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, ItemStack held) {
        if (!(level.getBlockEntity(pos) instanceof MachineBlockEntity machine)) {
            return UseResult.PASS;
        }
        if (held.getItem() instanceof MachineTool tool) {
            return tool.useOnMachine(level, machine, player, held, player.isSecondaryUseActive());
        }
        if (!held.isEmpty()) {
            return UseResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            ModMenus.open(serverPlayer, machine);
        }
        return UseResult.SUCCESS;
    }
}
