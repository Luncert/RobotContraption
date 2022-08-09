package com.luncert.robotcontraption.content.geoscanner;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GeoScannerBlock extends HorizontalDirectionalBlock implements ITE<GeoScannerTileEntity> {

    public GeoScannerBlock(Properties p_49795_) {
        super(p_49795_);
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
