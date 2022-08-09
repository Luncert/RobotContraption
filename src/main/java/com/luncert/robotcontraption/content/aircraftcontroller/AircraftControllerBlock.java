package com.luncert.robotcontraption.content.aircraftcontroller;

import com.luncert.robotcontraption.index.RCTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AircraftControllerBlock extends DirectionalBlock implements ITE<AircraftControllerTileEntity> {

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
