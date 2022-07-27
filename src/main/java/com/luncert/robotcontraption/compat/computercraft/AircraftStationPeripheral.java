package com.luncert.robotcontraption.compat.computercraft;

import com.luncert.robotcontraption.compat.create.AircraftMovementMode;
import com.luncert.robotcontraption.content.aircraft.AircraftStationTileEntity;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AircraftStationPeripheral implements IPeripheral {

    protected String type;
    protected AircraftStationTileEntity tileEntity;
    private int executionId;

    protected final List<IComputerAccess> connected = new ArrayList<>();

    public AircraftStationPeripheral(String type, AircraftStationTileEntity tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    public void queueEvent(String event, Object... args) {
        for (IComputerAccess computer : connected) {
            computer.queueEvent(event, args);
        }
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

    @Override
    public void attach(IComputerAccess computer) {
        connected.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        connected.remove(computer);
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connected;
    }

    // api

    @LuaFunction
    public final void assemble(String rotationMode) throws LuaException {
        AircraftMovementMode mode;
        try {
            mode = AircraftMovementMode.valueOf(rotationMode);
        }catch (IllegalArgumentException e) {
            throw new LuaException("Invalid mode, must be one of " + Arrays.toString(AircraftMovementMode.values()));
        }

        if (tileEntity != null) {
            try {
                tileEntity.assemble(mode);
            } catch (AircraftAssemblyException e) {
                e.printStackTrace();
                throw new LuaException("failed to assemble structure: " + e.getMessage());
            }
        }
    }

    @LuaFunction
    public final void dissemble() throws LuaException {
        if (tileEntity != null) {
            try {
                tileEntity.dissemble();
            } catch (AircraftAssemblyException e) {
                e.printStackTrace();
                throw new LuaException("failed to dissemble structure: " + e.getMessage());
            }
        }
    }

    @LuaFunction
    public final MethodResult forward(int n) throws LuaException {
        if (getSpeed() == 0) {
            throw new LuaException("speed is zero");
        }

        int executionId = this.executionId++;
        if (tileEntity != null) {
            try {
                tileEntity.forward(n,
                        success -> queueEvent(AircraftActionEvent.EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, success));
            } catch (AircraftMovementException e) {
                throw new LuaException(e.getMessage());
            }
        }

        return AircraftApiCallback.hook(executionId, AircraftActionEvent.EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult turnLeft() throws LuaException {
        if (getSpeed() == 0) {
            throw new LuaException("speed is zero");
        }

        int executionId = this.executionId++;
        if (tileEntity != null) {
            try {
                tileEntity.turnLeft(success -> queueEvent(AircraftActionEvent.EVENT_AIRCRAFT_ROTATE_DONE, executionId, success));
            } catch (AircraftMovementException e) {
                throw new LuaException(e.getMessage());
            }
        }

        return AircraftApiCallback.hook(executionId, AircraftActionEvent.EVENT_AIRCRAFT_ROTATE_DONE);
    }

    @LuaFunction
    public final MethodResult turnRight() throws LuaException {
        if (getSpeed() == 0) {
            throw new LuaException("speed is zero");
        }

        int executionId = this.executionId++;
        if (tileEntity != null) {
            try {
                tileEntity.turnRight(success -> queueEvent(AircraftActionEvent.EVENT_AIRCRAFT_ROTATE_DONE, executionId, success));
            } catch (AircraftMovementException e) {
                throw new LuaException(e.getMessage());
            }
        }

        return AircraftApiCallback.hook(executionId, AircraftActionEvent.EVENT_AIRCRAFT_ROTATE_DONE);
    }

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
