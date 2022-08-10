package com.luncert.robotcontraption.compat.aircraft;

public abstract class BaseAircraftComponent implements IAircraftComponent {

    protected AircraftAccessor accessor;
    protected String name;

    @Override
    public void init(AircraftAccessor aircraftAccessor, String name) {
        this.accessor = aircraftAccessor;
        this.name = name;
    }
}
