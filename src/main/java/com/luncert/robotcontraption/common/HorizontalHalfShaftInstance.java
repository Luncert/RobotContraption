package com.luncert.robotcontraption.common;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.SingleRotatingInstance;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class HorizontalHalfShaftInstance extends SingleRotatingInstance {

    public HorizontalHalfShaftInstance(MaterialManager modelManager, KineticTileEntity tile) {
        super(modelManager, tile);
    }

    protected Instancer<RotatingData> getModel() {
        Direction dir = this.getShaftDirection();
        return getRotatingMaterial().getModel(AllBlockPartials.SHAFT_HALF, this.blockState, dir);
    }

    protected Direction getShaftDirection() {
        return blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }
}
