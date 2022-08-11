package com.luncert.robotcontraption.content.geoscanner;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class GeoScannerBlock extends HorizontalDirectionalBlock implements ITE<GeoScannerTileEntity> {

    public GeoScannerBlock(Properties p_49795_) {
        super(p_49795_);
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
    public Class<GeoScannerTileEntity> getTileEntityClass() {
        return GeoScannerTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends GeoScannerTileEntity> getTileEntityType() {
        return RCTileEntities.GEO_SCANNER.get();
    }
}
