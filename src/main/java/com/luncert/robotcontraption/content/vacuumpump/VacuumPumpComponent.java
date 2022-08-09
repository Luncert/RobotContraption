package com.luncert.robotcontraption.content.vacuumpump;

import com.luncert.robotcontraption.compat.computercraft.AircraftAccessor;
import com.luncert.robotcontraption.compat.computercraft.IAircraftComponent;
import dan200.computercraft.api.lua.LuaFunction;

public class VacuumPumpComponent implements IAircraftComponent {

    private AircraftAccessor accessor;

    @Override
    public void init(AircraftAccessor aircraftAccessor, String name) {
        this.accessor = aircraftAccessor;
    }

    @Override
    public String getComponentType() {
        return "VacuumPump";
    }

    @LuaFunction
    public void enableDraining() {
        // TODO
    }

    @LuaFunction
    public void enableFilling() {

    }
}
