package com.luncert.robotcontraption.content.blockreader;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockReaderBlock extends DirectionalBlock implements ITE<BlockReaderTileEntity> {

    public BlockReaderBlock(Properties p_52591_) {
        super(p_52591_);
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
