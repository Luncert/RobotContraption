package com.luncert.robotcontraption.content.vacuumpump;

import com.luncert.robotcontraption.compat.aircraft.AircraftComponentType;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.world.level.block.state.BlockState;

import static com.luncert.robotcontraption.compat.aircraft.AircraftComponentType.VACUUM_PUMP;

public class VacuumPumpComponent extends BaseAircraftComponent {

    @Override
    public AircraftComponentType getComponentType() {
        return VACUUM_PUMP;
    }

    @LuaFunction
    public void enableDraining() {
        BlockState blockState = accessor.getComponentBlockState(name);
        blockState.setValue(VacuumPumpBlock.ENABLE_FILLING, false);
    }

    @LuaFunction
    public void enableFilling() {
        BlockState blockState = accessor.getComponentBlockState(name);
        blockState.setValue(VacuumPumpBlock.ENABLE_FILLING, true);
    }
}
