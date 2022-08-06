package com.luncert.robotcontraption.content.vacuumpump;

import com.jozufozu.flywheel.api.MaterialManager;
import com.luncert.robotcontraption.index.RCBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileInstance;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class VacuumPumpInstance extends KineticTileInstance<VacuumPumpTileEntity> {

    protected final RotatingData cogwheel;
    protected final RotatingData slider;
    final Direction direction;

    public VacuumPumpInstance(MaterialManager modelManager, VacuumPumpTileEntity tile) {
        super(modelManager, tile);

        direction = blockState.getValue(FACING);

        cogwheel = modelManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(RCBlockPartials.COGWHEEL_NO_SHAFT, blockState, direction)
                .createInstance();
        slider = modelManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(RCBlockPartials.VACUUM_PUMP_SLIDER, blockState, direction)
                .createInstance();

        setup(cogwheel);
        setup(slider);
    }

    @Override
    public void update() {
        updateRotation(cogwheel);
        updateRotation(slider);
    }

    @Override
    public void updateLight() {
        BlockPos behind = pos.relative(direction);
        relight(behind, cogwheel);

        BlockPos inFront = pos.relative(direction);
        relight(inFront, slider);
    }

    @Override
    protected void remove() {
        cogwheel.delete();
        slider.delete();
    }
}
