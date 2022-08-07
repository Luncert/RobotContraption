package com.luncert.robotcontraption.content.vacuumpump;

import com.luncert.robotcontraption.index.RCShapes;
import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.content.contraptions.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class VacuumPumpBlock extends DirectionalAxisKineticBlock implements ITE<VacuumPumpTileEntity> {

    public static final VoxelShaper VACUUM_PUMP_SHAPE = RCShapes
            .shape(2, 0, 2, 14, 12, 14)
            .forDirectional();

    public VacuumPumpBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Nullable
    public static Direction getFacing(BlockState state) {
        if (!(state.getBlock() instanceof VacuumPumpBlock))
            return null;
        return state.getValue(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return VACUUM_PUMP_SHAPE.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredSide = getPreferredFacing(context);
        if (preferredSide != null)
            return defaultBlockState().setValue(FACING, preferredSide);
        return super.getStateForPlacement(context);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Class<VacuumPumpTileEntity> getTileEntityClass() {
        return VacuumPumpTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends VacuumPumpTileEntity> getTileEntityType() {
        return RCTileEntities.VACUUM_PUMP.get();
    }
}
