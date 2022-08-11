package com.luncert.robotcontraption.compat.aircraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface IAircraftComponent {

    default void init(AircraftAccessor aircraftAccessor, String name) {
    }

    default void tickComponent() {
    }

    AircraftComponentType getComponentType();

    CompoundTag writeNBT();

    void readNBT(Level world, CompoundTag root);
}
