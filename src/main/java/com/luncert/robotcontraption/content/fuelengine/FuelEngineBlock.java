package com.luncert.robotcontraption.content.fuelengine;

import com.luncert.robotcontraption.index.RCShapes;
import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class FuelEngineBlock extends HorizontalKineticBlock implements ITE<FuelEngineTileEntity> {

    public static final VoxelShaper FUEL_ENGINE_SHAPE = RCShapes
            .shape(1, 0.5, 1, 15, 14, 15)
            .forDirectional();

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public FuelEngineBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        // TODO
        builder.add(POWERED);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return FUEL_ENGINE_SHAPE.get(state.getValue(HORIZONTAL_FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredSide = getPreferredHorizontalFacing(context);
        if (preferredSide != null)
            return defaultBlockState().setValue(HORIZONTAL_FACING, preferredSide);
        return super.getStateForPlacement(context);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING);
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }

    @Override
    public Class<FuelEngineTileEntity> getTileEntityClass() {
        return FuelEngineTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends FuelEngineTileEntity> getTileEntityType() {
        return RCTileEntities.FUEL_ENGINE.get();
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos from, boolean b) {
        if (!world.isClientSide) {
            boolean flag = state.getValue(POWERED);
            if (flag != world.hasNeighborSignal(pos)) {
                if (flag)
                    world.scheduleTick(pos, this, 4);
                else
                    world.setBlock(pos, state.cycle(POWERED), 2);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        if (state.getValue(POWERED) && !world.hasNeighborSignal(pos))
            world.setBlock(pos, state.cycle(POWERED), 2);
    }
}
