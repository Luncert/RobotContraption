package com.luncert.robotcontraption.content.vacuumpump;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.luncert.robotcontraption.index.RCBlockPartials;
import com.luncert.robotcontraption.util.Common;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class VacuumPumpRenderer extends KineticTileEntityRenderer {

    public VacuumPumpRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(KineticTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(te.getLevel())) return;

        Direction direction = te.getBlockState().getValue(FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        int lightBehind = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(direction.getOpposite()));
        int lightInFront = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(direction));

        SuperByteBuffer cogwheel =
                CachedBufferer.partialFacing(RCBlockPartials.COGWHEEL_NO_SHAFT, te.getBlockState(), direction);
        SuperByteBuffer slider =
                CachedBufferer.partialFacing(RCBlockPartials.VACUUM_PUMP_SLIDER, te.getBlockState(), direction);

        double offset = 0;
        if (te.getSpeed() != 0) {
            offset = calcSliderOffset(te.getLevel(), direction);
        }

        standardKineticRotationTransform(cogwheel, te, lightBehind).renderInto(ms, vb);
        kineticTranslate(slider, te, direction.getAxis(), offset, lightInFront).renderInto(ms, vb);
    }

    static double calcSliderOffset(Level world, Direction direction) {
        float renderTick = AnimationTickHolder.getRenderTime(world);
        return (6 - Math.pow((renderTick % 24 / 2 - 6) / 2.449489742783178, 2)) / 16
                * -direction.getAxisDirection().getStep();
    }

    private static SuperByteBuffer kineticTranslate(SuperByteBuffer buffer, KineticTileEntity te, Direction.Axis axis, double offset, int light) {
        buffer.light(light);
        buffer.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, axis), 0);
        buffer.translate(Common.relative(Common.convert(te.getBlockPos()), axis, offset));
        return buffer;
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                           ContraptionMatrices matrices, MultiBufferSource buffer) {
        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        BlockState blockState = context.state;
        Direction direction = blockState.getValue(FACING);

        Vec3 baseOffset = Common.convert(context.localPos);

        float speed = context.getAnimationSpeed();
        if (context.contraption.stalled)
            speed = 0;

        SuperByteBuffer cogwheel =
                CachedBufferer.partialFacing(RCBlockPartials.COGWHEEL_NO_SHAFT, blockState, direction);
        SuperByteBuffer slider =
                CachedBufferer.partialFacing(RCBlockPartials.VACUUM_PUMP_SLIDER, blockState, direction);

        PoseStack m = matrices.getModel();
        m.pushPose();

        m.pushPose();
        Direction.Axis axis = Direction.Axis.Y;
        if (context.state.getBlock() instanceof IRotate def) {
            axis = def.getRotationAxis(context.state);
        }

        float time = AnimationTickHolder.getRenderTime(context.world) / 20;
        float angle = (time * speed) % 360;

        TransformStack.cast(m)
                .centre()
                .rotateY(axis == Direction.Axis.Z ? 90 : 0)
                .rotateZ(axis.isHorizontal() ? 90 : 0)
                .unCentre();
        cogwheel.transform(m);
        cogwheel.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), angle);
        m.popPose();

        m.translate(baseOffset.x, baseOffset.y, baseOffset.z);
        cogwheel.light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                .renderInto(matrices.getViewProjection(), builder);

        double sliderOffset = 0;
        if (speed != 0) {
            sliderOffset = calcSliderOffset(context.world, direction);
        }
        slider.transform(m);

        slider.translate(Common.linear(axis, sliderOffset))
                .light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                .renderInto(matrices.getViewProjection(), builder);
        m.popPose();
    }
}
