package com.luncert.robotcontraption.content.vacuumpump;

import com.luncert.robotcontraption.compat.aircraft.AircraftComponentType;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import com.simibubi.create.content.contraptions.fluids.actors.FluidSplashPacket;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.networking.AllPackets;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import static com.luncert.robotcontraption.compat.aircraft.AircraftComponentType.VACUUM_PUMP;

public class VacuumPumpComponent extends BaseAircraftComponent {

    private boolean active = true;
    private boolean filling = false;

    @Override
    public AircraftComponentType getComponentType() {
        return VACUUM_PUMP;
    }

    @LuaFunction
    public void enableDraining() {
        active = true;
        filling = false;
    }

    @LuaFunction
    public void enableFilling() {
        active = true;
        filling = true;
    }

    @LuaFunction
    public void turnOff() {
        active = false;
    }

    @Override
    public Tag writeNBT() {
        CompoundTag t = new CompoundTag();
        t.putBoolean("active", active);
        t.putBoolean("filling", filling);
        return t;
    }

    @Override
    public void readNBT(Level world, Tag tag) {
        CompoundTag t = (CompoundTag) tag;
        active = t.getBoolean("active");
        filling = t.getBoolean("filling");
    }

    @Override
    public void tickComponent() {
        if (!active) {
            return;
        }

        BlockPos targetPos = accessor.getComponentPos(name);
        System.out.println(targetPos);

        if (filling) {
            fill(targetPos);
        } else {
            drain(targetPos);
        }
    }

    private void fill(BlockPos targetPos) {
        // TODO
    }

    private void drain(BlockPos targetPos) {
        Level world = accessor.world;
        BlockState targetState = world.getBlockState(targetPos);

        BlockState emptied = null;
        Fluid fluid = null;

        if (targetState.hasProperty(BlockStateProperties.WATERLOGGED)
                && targetState.getValue(BlockStateProperties.WATERLOGGED)) {
            emptied = targetState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
            fluid = Fluids.WATER;
        } else if (targetState.getBlock() instanceof LiquidBlock flowingFluid) {
            emptied = Blocks.AIR.defaultBlockState();

            if (targetState.getValue(LiquidBlock.LEVEL) == 0) {
                fluid = flowingFluid.getFluid();
            } else {
                world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 2 | 16);
            }
        } else if (targetState.getFluidState()
                .getType() != Fluids.EMPTY && targetState.getCollisionShape(world, targetPos, CollisionContext.empty())
                .isEmpty()) {
            fluid = targetState.getFluidState().getType();
            emptied = Blocks.AIR.defaultBlockState();
        }

        if (fluid != null) {
            FluidStack fluidStack = new FluidStack(fluid, 1000);
            IFluidHandler sharedFluidTanks = accessor.contraption.getSharedFluidTanks();
            sharedFluidTanks.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE); // TODO return value?

            playEffect(world, targetPos, fluid, true);
            world.setBlock(targetPos, emptied, 2 | 16);
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
