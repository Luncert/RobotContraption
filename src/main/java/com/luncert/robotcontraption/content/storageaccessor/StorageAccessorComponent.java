package com.luncert.robotcontraption.content.storageaccessor;

import com.luncert.robotcontraption.compat.aircraft.AircraftComponentType;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.HashMap;
import java.util.Map;

import static com.luncert.robotcontraption.compat.aircraft.AircraftComponentType.STORAGE_ACCESSOR;

public class StorageAccessorComponent extends BaseAircraftComponent {


    @Override
    public AircraftComponentType getComponentType() {
        return STORAGE_ACCESSOR;
    }

    @LuaFunction
    public final float getStorageUsage() {
        IItemHandlerModifiable sharedInventory = accessor.contraption.getSharedInventory();

        int totalSpace = 0;
        float used = 0;
        for (int slot = 0; slot < sharedInventory.getSlots(); slot++) {
            ItemStack stackInSlot = sharedInventory.getStackInSlot(slot);
            int space = Math.min(stackInSlot.getMaxStackSize(), sharedInventory.getSlotLimit(slot));
            int count = stackInSlot.getCount();
            if (space == 0)
                continue;

            totalSpace++;
            used += count * (1f / space);
        }
        return used / totalSpace;
    }

    @LuaFunction
    public final float getStorageSlotUsage() {
        IItemHandlerModifiable sharedInventory = accessor.contraption.getSharedInventory();

        int totalSpace = 0;
        float usedSpace = 0;
        for (int slot = 0; slot < sharedInventory.getSlots(); slot++) {
            ItemStack stackInSlot = sharedInventory.getStackInSlot(slot);
            int space = Math.min(stackInSlot.getMaxStackSize(), sharedInventory.getSlotLimit(slot));
            int count = stackInSlot.getCount();
            if (space == 0)
                continue;

            totalSpace++;

            if (count > 0) {
                usedSpace++;
            }
        }
        return usedSpace / totalSpace;
    }

    @LuaFunction
    public final float getFluidContainerUsage() {
        IFluidHandler sharedFluidTanks = accessor.contraption.getSharedFluidTanks();

        int totalCapacity = 0;
        int used = 0;
        for (int tankId = 0; tankId < sharedFluidTanks.getTanks(); tankId++) {
            FluidStack fluidInTank = sharedFluidTanks.getFluidInTank(tankId);
            int tankCapacity = sharedFluidTanks.getTankCapacity(tankId);
            if (tankCapacity == 0) {
                continue;
            }

            totalCapacity += tankCapacity;
            used += fluidInTank.getAmount();
        }

        return 1f * used / totalCapacity;
    }

    @LuaFunction
    public final Map<String, Integer> getFluidAmounts() {
        IFluidHandler sharedFluidTanks = accessor.contraption.getSharedFluidTanks();
        Map<String, Integer> result = new HashMap<>();

        for (int tankId = 0; tankId < sharedFluidTanks.getTanks(); tankId++) {
            FluidStack fluidInTank = sharedFluidTanks.getFluidInTank(tankId);
            result.compute(fluidInTank.getFluid().getRegistryName().toString(), (k, amount) -> {
               if (amount == null) {
                   amount = 0;
               }
               amount += fluidInTank.getAmount();
               return amount;
            });
        }

        return result;
    }
}
