package com.luncert.robotcontraption.compat.computercraft;

public interface IAircraftComponent {

    default void init(AircraftAccessor aircraftAccessor) {}

    default void tickComponent() {}

    String getComponentType();
}
