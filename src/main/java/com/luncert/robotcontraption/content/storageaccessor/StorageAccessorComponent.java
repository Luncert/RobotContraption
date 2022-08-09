package com.luncert.robotcontraption.content.storageaccessor;

import com.luncert.robotcontraption.compat.computercraft.AircraftAccessor;
import com.luncert.robotcontraption.compat.computercraft.IAircraftComponent;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class StorageAccessorComponent implements IAircraftComponent {

    private AircraftAccessor accessor;

    @Override
    public void init(AircraftAccessor aircraftAccessor) {
        accessor = aircraftAccessor;
    }

    @Override
    public String getComponentType() {
        return "StorageAccessor";
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
}