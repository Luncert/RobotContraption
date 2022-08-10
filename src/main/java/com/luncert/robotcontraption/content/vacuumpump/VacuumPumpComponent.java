package com.luncert.robotcontraption.content.vacuumpump;

import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.world.level.block.state.BlockState;

public class VacuumPumpComponent extends BaseAircraftComponent {

    @Override
    public String getComponentType() {
        return "VacuumPump";
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
