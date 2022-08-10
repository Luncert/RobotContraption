package com.luncert.robotcontraption.compat.aircraft;

public interface IAircraftComponent {

    default void init(AircraftAccessor aircraftAccessor, String name) {}

    default void tickComponent() {}

    String getComponentType();
}
