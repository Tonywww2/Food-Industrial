package com.tonywww.foodindustrial.platform.compat.create;
//? if <26.1 {

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.tonywww.foodindustrial.machine.block.MachineBlock;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * The transmission interface (I25): a low casing with a vertical half shaft on top, only on top of a
 * machine driven by hand (M01, M02); breaks off when the machine goes.
 */
public class TransmissionInterfaceBlock extends KineticBlock implements IBE<TransmissionInterfaceBlockEntity> {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public TransmissionInterfaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.UP;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<TransmissionInterfaceBlockEntity> getBlockEntityClass() {
        return TransmissionInterfaceBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TransmissionInterfaceBlockEntity> getBlockEntityType() {
        return CreateTransmission.type.get();
    }

    /** Only on top of a machine driven by hand. */
    private static boolean onManualMachine(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof MachineBlock machine && machine.definition().power() == PowerKind.MANUAL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return onManualMachine(level, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return onManualMachine(level, pos) ? state : Blocks.AIR.defaultBlockState();
    }

    /** Drops itself; no loot table, so nodes and packs without Create have no file naming a missing block. */
    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
//?}
