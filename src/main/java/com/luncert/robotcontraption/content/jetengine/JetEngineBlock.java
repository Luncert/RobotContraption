package com.luncert.robotcontraption.content.jetengine;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class JetEngineBlock extends HorizontalDirectionalBlock implements ITE<JetEngineTileEntity> {

    public JetEngineBlock(Properties p_54120_) {
        super(p_54120_);
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
