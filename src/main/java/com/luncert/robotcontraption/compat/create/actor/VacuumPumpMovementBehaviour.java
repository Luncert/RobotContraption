package com.luncert.robotcontraption.compat.create.actor;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.luncert.robotcontraption.content.vacuumpump.VacuumPumpBlock;
import com.luncert.robotcontraption.content.vacuumpump.VacuumPumpRenderer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerActorInstance;
import com.simibubi.create.content.contraptions.components.deployer.DeployerRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ActorInstance;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.contraptions.fluids.actors.FluidSplashPacket;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.BBHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public class VacuumPumpMovementBehaviour implements MovementBehaviour {

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        Direction facing = VacuumPumpBlock.getFacing(context.state);
        Vec3 vec = Vec3.atLowerCornerOf(facing.getNormal());
        return vec.scale(.65);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        MovementBehaviour.super.visitNewPosition(context, pos);

        drain(context, pos);
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                    ContraptionMatrices matrices, MultiBufferSource buffers) {
        if (!ContraptionRenderDispatcher.canInstance())
            VacuumPumpRenderer.renderInContraption(context, renderWorld, matrices, buffers);
    }

    @Nullable
    @Override
    public ActorInstance createInstance(MaterialManager materialManager, VirtualRenderWorld simulationWorld,
                                        MovementContext context) {
        return new DeployerActorInstance(materialManager, simulationWorld, context);
    }

    @Override
    public boolean hasSpecialInstancedRendering() {
        return true;
    }

    private void drain(MovementContext context, BlockPos pos) {
        Level world = context.world;
        BlockState blockState = world.getBlockState(pos);

        BlockState emptied = null;
        Fluid fluid = null;

        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
            emptied = blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
            fluid = Fluids.WATER;
        } else if (blockState.getBlock() instanceof LiquidBlock flowingFluid) {
            emptied = Blocks.AIR.defaultBlockState();

            if (blockState.getValue(LiquidBlock.LEVEL) == 0) {
                fluid = flowingFluid.getFluid();
            } else {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2 | 16);
            }
        } else if (blockState.getFluidState()
                .getType() != Fluids.EMPTY && blockState.getCollisionShape(world, pos, CollisionContext.empty())
                .isEmpty()) {
            fluid = blockState.getFluidState().getType();
            emptied = Blocks.AIR.defaultBlockState();
        }

        if (fluid != null) {
            FluidStack fluidStack = new FluidStack(fluid, 1000);
            IFluidHandler sharedFluidTanks = context.contraption.getSharedFluidTanks();
            sharedFluidTanks.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE); // TODO return value?


            playEffect(world, pos, fluid, true);
            world.setBlock(pos, emptied, 2 | 16);
        }
    }

    private void playEffect(Level world, BlockPos sploshPos, Fluid fluid, boolean fillSound) {
        SoundEvent soundevent = fillSound ? fluid.getAttributes()
                .getFillSound()
                : fluid.getAttributes()
                .getEmptySound();
        if (soundevent == null)
            soundevent = FluidHelper.isTag(fluid, FluidTags.LAVA)
                    ? fillSound ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_EMPTY_LAVA
                    : fillSound ? SoundEvents.BUCKET_FILL : SoundEvents.BUCKET_EMPTY;

        world.playSound(null, sploshPos, soundevent, SoundSource.BLOCKS, 0.3F, 1.0F);
        if (world instanceof ServerLevel)
            AllPackets.sendToNear(world, sploshPos, 10, new FluidSplashPacket(sploshPos, new FluidStack(fluid, 1)));
    }
}
