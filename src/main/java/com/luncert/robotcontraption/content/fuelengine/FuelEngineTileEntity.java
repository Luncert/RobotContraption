package com.luncert.robotcontraption.content.fuelengine;

import com.luncert.robotcontraption.common.Capabilities;
import com.luncert.robotcontraption.index.RCBlocks;
import com.luncert.robotcontraption.util.Lang;
import com.mrh0.createaddition.index.CAFluids;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.simibubi.create.content.logistics.block.funnel.AbstractHorizontalFunnelBlock.HORIZONTAL_FACING;

public class FuelEngineTileEntity extends GeneratingKineticTileEntity {

    private final FluidStack seedOil = new FluidStack(FluidHelper.convertToStill(CAFluids.SEED_OIL.get()), 1000);
    private final FluidStack bioethanol = new FluidStack(FluidHelper.convertToStill(CAFluids.BIOETHANOL.get()), 1000);

    private final LazyOptional<FuelEngineComponent> component;
    private final LazyOptional<FluidTank> fluidTank;

    private boolean active = false;
    protected ScrollValueBehaviour generatedSpeed;
    private boolean cc_update_rpm = false;
    private int cc_new_rpm = 32;
    int cc_antiSpam = 0;
    boolean first = true;

    public FuelEngineTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);

        component = LazyOptional.of(FuelEngineComponent::new);
        fluidTank = LazyOptional.of(() ->
                new FluidTank(1000, fluid -> fluid.containsFluid(seedOil) || fluid.containsFluid(bioethanol)));
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        FuelEngineBoxTransform slot = new FuelEngineBoxTransform();

        generatedSpeed = new ScrollValueBehaviour(Lang.translateRaw("create.generic.speed"), this, slot);
        generatedSpeed.between(0, 256);
        generatedSpeed.value = 32;
        generatedSpeed.scrollableValue = 32;
        generatedSpeed.withUnit(i -> Lang.translateRaw("generic.unit.rpm"));
        generatedSpeed.withCallback(i -> {
            this.updateGeneratedRotation();
            cc_new_rpm = i;
        });
        generatedSpeed.withStepFunction(FuelEngineTileEntity::step);
        behaviours.add(generatedSpeed);
    }

    public static int step(ScrollValueBehaviour.StepContext context) {
        int current = context.currentValue;
        int step = 1;

        if (!context.shift) {
            int magnitude = Math.abs(current) - (context.forward == current > 0 ? 0 : 1);

            if (magnitude >= 4)
                step *= 4;
            if (magnitude >= 32)
                step *= 4;
            if (magnitude >= 128)
                step *= 4;
        }

        return step;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        if (!RCBlocks.FUEL_ENGINE.has(getBlockState()))
            return 0;
        return active ? generatedSpeed.getValue() : 0;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        cc_antiSpam = 5;
    }

    @Override
    public void tick() {
        super.tick();

        if(first) {
            updateGeneratedRotation();
            first = false;
        }

        if(cc_update_rpm && cc_antiSpam > 0) {
            generatedSpeed.setValue(cc_new_rpm);
            cc_update_rpm = false;
            cc_antiSpam--;
            updateGeneratedRotation();
        }

        if(level.isClientSide())
            return;

        int con = getFuelConsumptionRate(generatedSpeed.getValue());
        System.out.println(con + " " + getGeneratedSpeed() + " " + generatedSpeed.getValue());
        fluidTank.ifPresent(t -> {
            System.out.println(active + " " + t.getFluidAmount());
            if (!active) {
                if (t.getFluidAmount() > con * 2) {
                    active = true;
                    updateGeneratedRotation();
                }
            } else {
                t.drain(con, FluidAction.EXECUTE);
                if (t.getFluidAmount() < con) {
                    active = false;
                    updateGeneratedRotation();
                }
            }
        });
    }

    private int getFuelConsumptionRate(int rpm) {
        int consumptionFactor = fluidTank.map(t -> {
            FluidStack fluid = t.getFluid();
            if (fluid.containsFluid(seedOil)) {
                return 10;
            } else {
                return 5;
            }
        }).orElse(0);
        return (int) (rpm / 256d * consumptionFactor);
    }

    @Override
    public void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        active = compound.getBoolean("active");
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("active", active);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (Capabilities.isAircraftComponent(cap)) {
            return component.cast();
        }
        if (isFluidHandlerCap(cap)) {
            return fluidTank.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        component.invalidate();
        fluidTank.invalidate();
    }

    // cc api

    public boolean setRPM(int rpm) {
        //System.out.println("SETSPEED" + rpm);
        rpm = Math.max(Math.min(rpm, 256), 0);
        cc_new_rpm = rpm;
        cc_update_rpm = true;
        return cc_antiSpam > 0;
    }

    public int getRPM() {
        return cc_new_rpm;//generatedSpeed.getValue();
    }

    public int getGeneratedStress() {
        return (int) calculateAddedStressCapacity();
    }
}
