package com.luncert.robotcontraption.compat.aircraft;

import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IAircraftComponent {

    default void init(AircraftAccessor aircraftAccessor, String name) {
    }

    default void tickComponent() {
    }

    AircraftComponentType getComponentType();

    @Nullable
    Tag writeNBT();

    void readNBT(Level world, Tag tag);
}
