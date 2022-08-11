package com.luncert.robotcontraption.content.aircraftcontroller;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class AircraftControllerBlock extends HorizontalDirectionalBlock implements ITE<AircraftControllerTileEntity> {

    public AircraftControllerBlock(Properties p_52591_) {
        super(p_52591_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public Class<AircraftControllerTileEntity> getTileEntityClass() {
        return AircraftControllerTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends AircraftControllerTileEntity> getTileEntityType() {
        return RCTileEntities.AIRCRAFT_CONTROLLER.get();
    }
}
