package com.luncert.robotcontraption.content.storageaccessor;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class StorageAccessorBlock extends DirectionalBlock implements ITE<StorageAccessorTileEntity> {

    public StorageAccessorBlock(Properties p_52591_) {
        super(p_52591_);
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
