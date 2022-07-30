package com.luncert.robotcontraption.content.fuelengine;

import com.jozufozu.flywheel.api.MaterialManager;
import com.luncert.robotcontraption.index.RCBlockPartials;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileInstance;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FuelEngineInstance extends KineticTileInstance<FuelEngineTileEntity> {

    protected final RotatingData shaft;
    protected final RotatingData fan;
    final Direction direction;

    public FuelEngineInstance(MaterialManager modelManager, FuelEngineTileEntity tile) {
        super(modelManager, tile);

        direction = blockState.getValue(HORIZONTAL_FACING);

        shaft = getRotatingMaterial().getModel(RCBlockPartials.SHAFT_HALF, blockState, direction).createInstance();
        fan = modelManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(RCBlockPartials.ENCASED_FAN_INNER, blockState, direction)
                .createInstance();

        setup(shaft);
        setup(fan);
    }

    @Override
    public void update() {
        updateRotation(shaft);
        updateRotation(fan);
    }

    @Override
    public void updateLight() {
        BlockPos behind = pos.relative(direction);
        relight(behind, shaft);

        BlockPos inFront = pos.relative(direction);
        relight(inFront, fan);
    }

    @Override
    protected void remove() {
        shaft.delete();
        fan.delete();
    }
}
