package com.luncert.robotcontraption.content.vacuumpump;

import com.jozufozu.flywheel.api.Material;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.luncert.robotcontraption.index.RCBlockPartials;
import com.luncert.robotcontraption.util.Common;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.base.KineticTileInstance;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class VacuumPumpInstance extends KineticTileInstance<VacuumPumpTileEntity> implements DynamicInstance {

    protected final RotatingData cogwheel;
    protected final ModelData slider;
    final Direction direction;
    private final VacuumPumpTileEntity te;

    public VacuumPumpInstance(MaterialManager modelManager, VacuumPumpTileEntity te) {
        super(modelManager, te);

        direction = blockState.getValue(FACING);
        this.te = te;

        Material<ModelData> mat = getTransformMaterial();

        cogwheel = modelManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(RCBlockPartials.COGWHEEL_NO_SHAFT, blockState, direction)
                .createInstance();
        slider = mat.getModel(RCBlockPartials.VACUUM_PUMP_SLIDER, blockState, direction)
                .createInstance();

        setup(cogwheel);
        animeSlider();
    }

    @Override
    public void beginFrame() {
        updateRotation(cogwheel);
        animeSlider();
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

    int t;

    private void animeSlider() {
        PoseStack msLocal = new PoseStack();
        TransformStack msr = TransformStack.cast(msLocal);

        if (blockEntity.getSpeed() != 0) {
            float renderTick = AnimationTickHolder.getRenderTime(te.getLevel());
            Direction direction = blockEntity.getBlockState().getValue(FACING);
            double offset = (6 - Math.pow((renderTick % 12 - 6) / 2.449489742783178, 2)) / 16;
            msr.translate(Common.relative(Common.convert(getInstancePosition()), direction.getAxis(), offset));
        } else {
            msr.translate(getInstancePosition());
        }

        // msr.centre();
        slider.setTransform(msLocal).setColor(0xFFFFFF);
    }
}
