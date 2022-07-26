package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.content.index.RCBlocks;
import com.luncert.robotcontraption.content.index.RCShapes;
import com.luncert.robotcontraption.content.index.RCTileEntities;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AircraftStationBlock extends HorizontalKineticBlock implements ITE<AircraftStationTileEntity> {

    public static final VoxelShaper ROBOT_STATION_SHAPE = RCShapes
            .shape(0, 0, 1, 16, 15, 14)
            .add(0, 0, 14, 16, 16, 16)
            .forDirectional();

    public AircraftStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ROBOT_STATION_SHAPE.get(state.getValue(HORIZONTAL_FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction prefferedSide = getPreferredHorizontalFacing(context);
        if (prefferedSide != null)
            return defaultBlockState().setValue(HORIZONTAL_FACING, prefferedSide);
        return super.getStateForPlacement(context);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RCTileEntities.AIRCRAFT_STATION.create(pos, state);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public Class<AircraftStationTileEntity> getTileEntityClass() {
        return AircraftStationTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends AircraftStationTileEntity> getTileEntityType() {
        return RCTileEntities.AIRCRAFT_STATION.get();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING);
    }

    // contraption anchor

    public static BlockState createAnchor(BlockState state) {
        return RCBlocks.AIRCRAFT_ANCHOR.getDefaultState();
    }
}
