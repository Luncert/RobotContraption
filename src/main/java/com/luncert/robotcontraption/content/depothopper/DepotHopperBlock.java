package com.luncert.robotcontraption.content.depothopper;

import com.luncert.robotcontraption.content.index.RCShapes;
import com.luncert.robotcontraption.content.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class DepotHopperBlock extends Block implements ITE<DepotHopperTileEntity> {

    public DepotHopperBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<DepotHopperTileEntity> getTileEntityClass() {
        return DepotHopperTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends DepotHopperTileEntity> getTileEntityType() {
        return RCTileEntities.DEPOT_HOPPER.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = RCShapes.DEPOT_HOPPER.get(state.getValue(FACING));
        return shape == null ? RCShapes.DEFAULT : shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state, world, pos, context);
    }
}
