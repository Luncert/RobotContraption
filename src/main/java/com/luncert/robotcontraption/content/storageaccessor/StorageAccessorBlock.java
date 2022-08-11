package com.luncert.robotcontraption.content.storageaccessor;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class StorageAccessorBlock extends HorizontalDirectionalBlock implements ITE<StorageAccessorTileEntity> {

    public StorageAccessorBlock(Properties p_52591_) {
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
    public Class<StorageAccessorTileEntity> getTileEntityClass() {
        return StorageAccessorTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends StorageAccessorTileEntity> getTileEntityType() {
        return RCTileEntities.STORAGE_ACCESSOR.get();
    }
}
