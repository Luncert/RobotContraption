package com.luncert.robotcontraption.compat.computercraft;

import com.luncert.robotcontraption.content.robot.RobotStationTileEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotStationPeripheral implements IPeripheral {

    protected String type;
    protected RobotStationTileEntity tileEntity;

    public RobotStationPeripheral(String type, RobotStationTileEntity tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    @Override
    public Object getTarget() {
        return tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    // api

    @LuaFunction
    public final void setSpeed(int rpm) throws LuaException {
        if(rpm == getSpeed()) {
            return;
        }
        if(tileEntity != null) {
            if(!tileEntity.setRPM(rpm)) {
                throw new LuaException("Speed is set too many times per second (Anti Spam).");
            }
        }
    }

    @LuaFunction(mainThread = true)
    public final void stop() throws LuaException {
        setSpeed(0);
    }

    @LuaFunction(mainThread = true)
    public final int getSpeed() throws LuaException {
        if(tileEntity != null) {
            return tileEntity.getRPM();
        }
        return 0;
    }
}
