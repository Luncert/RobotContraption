package com.luncert.robotcontraption.content.fuelengine;

import com.luncert.robotcontraption.compat.computercraft.BaseAircraftComponent;
import com.luncert.robotcontraption.util.Common;
import com.mrh0.createaddition.index.CAFluids;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FuelEngineComponent extends BaseAircraftComponent {

    // TODO
    private int speed = 32;

    @Override
    public String getComponentType() {
        return "FuelEngine";
    }

    @Override
    public void tickComponent() {
        final double newCapacity;
        if (consumeFuel(true)) {
            consumeFuel(false);
            BlockState state = accessor.getComponentBlockState(name);
            Block block = state.getBlock();
            Double defaultCapacity = BlockStressDefaults.DEFAULT_CAPACITIES.get(block.getRegistryName());
            newCapacity = defaultCapacity * speed;
        } else {
            newCapacity = 0;
        }

        accessor.resources.updateResource("capacity", 0d, old -> old + newCapacity);
    }

    private boolean consumeFuel(boolean simulate) {
        return accessor.aircraft.getContraption().map(contraption -> {
            IFluidHandler fluidTanks = contraption.getSharedFluidTanks();
            return tryToDrain(fluidTanks, CAFluids.SEED_OIL.get(), simulate)
                    || tryToDrain(fluidTanks, CAFluids.BIOETHANOL.get(), simulate);
        }).orElse(false);
    }

    private boolean tryToDrain(IFluidHandler fluidTanks, Fluid fluid, boolean simulate) {
        int consumption = getFuelConsumption(fluid);
        FluidStack drain = fluidTanks.drain(new FluidStack(fluid, consumption), simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
        return drain.getAmount() == consumption;
    }

    private int getFuelConsumption(Fluid fluid) {
        int consumptionFactor;
        if (Common.compareFluidKinds(fluid, CAFluids.SEED_OIL.get())) {
            consumptionFactor = 5;
        } else {
            consumptionFactor = 2;
        }
        return consumptionFactor;
    }
}
