package com.luncert.robotcontraption.content.fuelengine;

import com.luncert.robotcontraption.compat.computercraft.AircraftAccessor;
import com.luncert.robotcontraption.compat.computercraft.IAircraftComponent;
import com.luncert.robotcontraption.util.Common;
import com.mrh0.createaddition.index.CAFluids;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FuelEngineComponent implements IAircraftComponent {

    private AircraftAccessor aircraftAccessor;

    @Override
    public void init(AircraftAccessor aircraftAccessor, String name) {
        this.aircraftAccessor = aircraftAccessor;
    }

    @Override
    public String getComponentType() {
        return "FuelEngine";
    }

    public int getAvailableMovingDistance() {
        return aircraftAccessor.aircraft.getContraption().map(contraption -> {
            IFluidHandler fluidTanks = contraption.getSharedFluidTanks();
            FluidStack seedOil = fluidTanks.drain(new FluidStack(CAFluids.SEED_OIL.get(), Integer.MAX_VALUE), FluidAction.SIMULATE);
            FluidStack bioethanol = fluidTanks.drain(new FluidStack(CAFluids.BIOETHANOL.get(), Integer.MAX_VALUE), FluidAction.SIMULATE);
            return seedOil.getAmount() / 5 + bioethanol.getAmount() / 2;
        }).orElse(0);
    }

    public boolean consumeFuel(int n, boolean simulate) {
        return aircraftAccessor.aircraft.getContraption().map(contraption -> {
            IFluidHandler fluidTanks = contraption.getSharedFluidTanks();
            return tryToDrain(fluidTanks, CAFluids.SEED_OIL.get(), n, simulate)
                    || tryToDrain(fluidTanks, CAFluids.BIOETHANOL.get(), n, simulate);
        }).orElse(false);
    }

    private boolean tryToDrain(IFluidHandler fluidTanks, Fluid fluid, int n, boolean simulate) {
        int consumption = getFuelConsumption(fluid) * n;
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
