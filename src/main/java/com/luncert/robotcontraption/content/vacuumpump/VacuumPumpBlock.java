package com.luncert.robotcontraption.content.vacuumpump;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class VacuumPumpBlock extends DirectionalKineticBlock implements ITE<VacuumPumpTileEntity> {

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
