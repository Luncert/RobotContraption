package com.luncert.robotcontraption.content.robot;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.SingleRotatingInstance;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RobotStationInstance extends SingleRotatingInstance {

    public RobotStationInstance(MaterialManager modelManager, KineticTileEntity tile) {
        super(modelManager, tile);
    }

    protected Instancer<RotatingData> getModel() {
        Direction dir = this.getShaftDirection();
        return this.getRotatingMaterial().getModel(AllBlockPartials.SHAFT_HALF, this.blockState, dir);
    }

    protected Direction getShaftDirection() {
        return (Direction)this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }
}
