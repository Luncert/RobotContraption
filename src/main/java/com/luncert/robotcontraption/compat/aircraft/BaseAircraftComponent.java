package com.luncert.robotcontraption.compat.aircraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public abstract class BaseAircraftComponent implements IAircraftComponent {

    protected AircraftAccessor accessor;
    protected String name;

    @Override
    public void init(AircraftAccessor aircraftAccessor, String name) {
        this.accessor = aircraftAccessor;
        this.name = name;
    }

    @Override
    public CompoundTag writeNBT() {
        return null;
    }

    @Override
    public void readNBT(Level world, CompoundTag root) {
    }
}
