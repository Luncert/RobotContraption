package com.luncert.robotcontraption.content.jetengine;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class JetEngineBlock extends HorizontalDirectionalBlock implements ITE<JetEngineTileEntity> {

    public JetEngineBlock(Properties p_54120_) {
        super(p_54120_);
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
    public Class<JetEngineTileEntity> getTileEntityClass() {
        return JetEngineTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends JetEngineTileEntity> getTileEntityType() {
        return RCTileEntities.JET_ENGINE.get();
    }
}
