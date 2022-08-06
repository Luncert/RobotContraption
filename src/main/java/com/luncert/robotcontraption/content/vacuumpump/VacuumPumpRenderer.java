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

        if (Backend.canUseInstancing(te.getLevel())) return;

        Direction direction = te.getBlockState().getValue(FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());

        int lightBehind = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(direction.getOpposite()));
        int lightInFront = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(direction));

        SuperByteBuffer cogwheel =
                CachedBufferer.partialFacing(RCBlockPartials.COGWHEEL_NO_SHAFT, te.getBlockState(), direction);
        SuperByteBuffer slider =
                CachedBufferer.partialFacing(RCBlockPartials.VACUUM_PUMP_SLIDER, te.getBlockState(), direction);

        float time = AnimationTickHolder.getRenderTime(te.getLevel());
        double offset = 10 - Math.sqrt((time % 16 - 4));

        standardKineticRotationTransform(cogwheel, te, lightBehind).renderInto(ms, vb);
        kineticTranslate(slider, direction.getAxis(), offset, lightInFront).renderInto(ms, vb);
    }

    private SuperByteBuffer kineticTranslate(SuperByteBuffer buffer, Direction.Axis axis, double offset, int light) {
        buffer.light(light);
        buffer.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, axis), 0);
        buffer.translate(Common.linear(axis, offset));
        return buffer;
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                           ContraptionMatrices matrices, MultiBufferSource buffer) {
        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        BlockState blockState = context.state;
        Direction direction = blockState.getValue(FACING);

        SuperByteBuffer cogwheel =
                CachedBufferer.partialFacing(RCBlockPartials.COGWHEEL_NO_SHAFT, blockState, direction);

        Vec3 center = VecHelper.getCenterOf(new BlockPos(context.position));
        double distance = context.position.distanceTo(center);
        double nextDistance = context.position.add(context.motion)
                .distanceTo(center);
        double factor = .5f - Mth.clamp(Mth.lerp(AnimationTickHolder.getPartialTicks(), distance, nextDistance), 0, 1);
        Vec3 offset = Vec3.atLowerCornerOf(blockState.getValue(DirectionalKineticBlock.FACING)
                .getNormal()).scale(factor);

        PoseStack m = matrices.getModel();
        m.pushPose();

        m.pushPose();
        Direction.Axis axis = Direction.Axis.Y;
        if (context.state.getBlock() instanceof IRotate) {
            IRotate def = (IRotate) context.state.getBlock();
            axis = def.getRotationAxis(context.state);
        }

        float time = AnimationTickHolder.getRenderTime(context.world) / 20;
        int speed = 64;
        float angle = (time * speed) % 360;

        TransformStack.cast(m)
                .centre()
                .rotateY(axis == Direction.Axis.Z ? 90 : 0)
                .rotateZ(axis.isHorizontal() ? 90 : 0)
                .unCentre();
        cogwheel.transform(m);
        cogwheel.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), angle);
        m.popPose();

        m.translate(offset.x, offset.y, offset.z);

        cogwheel.light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                .renderInto(matrices.getViewProjection(), builder);

        m.popPose();
    }
}
