package com.luncert.robotcontraption.content.aircraftcontroller;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AircraftControllerBlock extends HorizontalDirectionalBlock implements ITE<AircraftControllerTileEntity> {

    public AircraftControllerBlock(Properties p_52591_) {
        super(p_52591_);
    }

    @Override
    public Class<AircraftControllerTileEntity> getTileEntityClass() {
        return AircraftControllerTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends AircraftControllerTileEntity> getTileEntityType() {
        return RCTileEntities.AIRCRAFT_CONTROLLER.get();
    }
}
