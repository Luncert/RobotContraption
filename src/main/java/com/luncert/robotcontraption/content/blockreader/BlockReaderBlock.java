package com.luncert.robotcontraption.content.blockreader;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class BlockReaderBlock extends DirectionalBlock implements ITE<BlockReaderTileEntity> {

    public BlockReaderBlock(Properties p_52591_) {
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
        Direction direction = context.getNearestLookingDirection();
        if (context.getPlayer() != null && context.getPlayer()
                .isSteppingCarefully())
            direction = direction.getOpposite();
        return this.defaultBlockState().setValue(FACING, direction.getOpposite());
    }

    @Override
    public Class<BlockReaderTileEntity> getTileEntityClass() {
        return BlockReaderTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlockReaderTileEntity> getTileEntityType() {
        return RCTileEntities.BLOCK_READER.get();
    }
}
