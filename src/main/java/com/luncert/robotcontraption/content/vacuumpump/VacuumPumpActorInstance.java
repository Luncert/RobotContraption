package com.luncert.robotcontraption.content.vacuumpump;

import com.jozufozu.flywheel.api.Material;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.luncert.robotcontraption.index.RCBlockPartials;
import com.luncert.robotcontraption.util.Common;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ActorInstance;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.luncert.robotcontraption.util.Common.relative;
import static com.luncert.robotcontraption.util.Common.toV3f;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class VacuumPumpActorInstance extends ActorInstance {

    private final PoseStack stack = new PoseStack();

    protected final ModelData cogwheel;
    // protected final ModelData slider;
    protected final Direction direction;

    public VacuumPumpActorInstance(MaterialManager materialManager, VirtualRenderWorld world, MovementContext context) {
        super(materialManager, world, context);

        Material<ModelData> mat = materialManager.defaultSolid()
                .material(Materials.TRANSFORMED);

        BlockState blockState = context.state;
        direction = blockState.getValue(FACING);

        cogwheel = mat
                .getModel(RCBlockPartials.COGWHEEL_NO_SHAFT, blockState, direction)
                .createInstance();
        // slider = mat.getModel(RCBlockPartials.VACUUM_PUMP_SLIDER, blockState, direction)
        //         .createInstance();

        animeCogwheel();
        // animeSlider();
    }

    @Override
    public void beginFrame() {
        animeCogwheel();
        // animeSlider();
    }

    private Vec3 getInstancePosition() {
        return Common.toV3(context.localPos);
    }

    private void animeCogwheel() {
        Direction.Axis axis = Direction.Axis.Y;
        if (context.state.getBlock() instanceof IRotate def) {
            axis = def.getRotationAxis(context.state);
        }

        stack.pushPose();
        TransformStack msr = TransformStack.cast(stack)
                .centre()
                .rotateY(axis == Direction.Axis.Z ? 90 : 0)
                .rotateZ(axis.isHorizontal() ? 90 : 0)
                .unCentre();

        msr.translate(relative(getInstancePosition(),
                direction.getAxis(), direction.getAxisDirection().getStep() * -1f / 16 * 11));

        float speed = context.getAnimationSpeed();
        if (context.contraption.stalled)
            speed = 0;
        float time = AnimationTickHolder.getRenderTime(context.world) / 20;
        float angle = (time * speed) % 360;
        cogwheel.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), angle);
        cogwheel.setTransform(stack);

        cogwheel.setBlockLight(localBlockLight());
        stack.popPose();
    }

    private void animeSlider() {
        stack.pushPose();
        TransformStack msr = TransformStack.cast(stack);

        if (context.getAnimationSpeed() != 0) {
            double offset = VacuumPumpRenderer.calcSliderOffset(context.world, direction);
            msr.translate(relative(getInstancePosition(), direction.getAxis(), offset));
        } else {
            msr.translate(getInstancePosition());
        }

        // msr.centre();
        // slider.setTransform(stack);

        stack.popPose();
    }
}
