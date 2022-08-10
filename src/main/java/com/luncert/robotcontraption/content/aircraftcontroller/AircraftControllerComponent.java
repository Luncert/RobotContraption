package com.luncert.robotcontraption.content.aircraftcontroller;

import com.google.common.collect.ImmutableMap;
import com.luncert.robotcontraption.compat.computercraft.AircraftApiCallback;
import com.luncert.robotcontraption.compat.computercraft.BaseAircraftComponent;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineComponent;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Map;

import static com.luncert.robotcontraption.compat.computercraft.AircraftActionEvent.EVENT_AIRCRAFT_MOVEMENT_DONE;

public class AircraftControllerComponent extends BaseAircraftComponent {

    private int executionId;

    @Override
    public void tickComponent() {
        // TODO find jet engine and check whether they are working, if not pause aircraft motion
        // for (FuelEngineComponent fuelEngine : accessor.<FuelEngineComponent>findAll("FuelEngine")) {
        // }
        accessor.aircraft.pauseMotion("out of fuel");
    }

    @Override
    public String getComponentType() {
        return "AircraftController";
    }

    @LuaFunction
    public final MethodResult up(int n) throws LuaException {
        if (n <= 0) {
            throw new LuaException("n must be positive");
        }

        int executionId = this.executionId++;
        try {
            accessor.aircraft.up(n, data -> accessor.queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException e) {
            throw new LuaException(e.getMessage());
        }

        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult down(int n) throws LuaException {
        if (n <= 0) {
            throw new LuaException("n must be positive");
        }

        int executionId = this.executionId++;
        try {
            accessor.aircraft.down(n, data -> accessor.queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException e) {
            throw new LuaException(e.getMessage());
        }

        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult forward(int n) throws LuaException {
        if (n <= 0) {
            throw new LuaException("n must be positive");
        }

        int executionId = this.executionId++;
        try {
            accessor.aircraft.forward(n, data -> accessor.queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException e) {
            throw new LuaException(e.getMessage());
        }

        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult turnLeft() throws LuaException {
        int executionId = this.executionId++;
        try {
            accessor.aircraft.turnLeft(data -> accessor.queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException e) {
            throw new LuaException(e.getMessage());
        }

        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult turnRight() throws LuaException {
        int executionId = this.executionId++;
        try {
            accessor.aircraft.turnRight(data -> accessor.queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException e) {
            throw new LuaException(e.getMessage());
        }
        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction(mainThread = true)
    public final void setSpeed(int speed) throws LuaException {
        try {
            accessor.aircraft.setSpeed(speed);
        } catch (AircraftMovementException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction(mainThread = true)
    public final int getSpeed() throws LuaException {
        return accessor.aircraft.getSpeed();
    }

    @LuaFunction
    public final Map<String, Double> getAircraftPosition() throws LuaException {
        Vec3 pos = accessor.aircraft.getAircraftPosition();
        return ImmutableMap.of(
                "x", pos.x,
                "y", pos.y,
                "z", pos.z
        );
    }

    @LuaFunction
    public final Map<String, Double> getStationPosition() throws LuaException {
        Vec3 pos = accessor.station.getStationPosition();
        return ImmutableMap.of(
                "x", pos.x,
                "y", pos.y,
                "z", pos.z
        );
    }

    @LuaFunction
    public final Map<String, Object> getAircraftFacing() throws LuaException {
        Direction direction = accessor.aircraft.getAircraftFacing();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        return ImmutableMap.of(
                "axis", direction.getAxis().getName(),
                "step", axisDirection.getStep()
        );
    }

    @LuaFunction
    public final Map<String, Object> getStationFacing() throws LuaException {
        Direction direction = accessor.station.getStationFacing();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        return ImmutableMap.of(
                "axis", direction.getAxis().getName(),
                "step", axisDirection.getStep()
        );
    }

    @LuaFunction
    public final int calcRotationTo(String a, int step) throws LuaException {
        Direction.Axis axis;
        try {
            axis = Direction.Axis.valueOf(a);
        } catch (IllegalArgumentException e) {
            throw new LuaException("Invalid argument, must be one of " + Arrays.toString(Direction.Axis.values()));
        }

        int rotateStep = 0;

        Direction facingDirection = accessor.aircraft.getAircraftFacing();
        Direction.AxisDirection axisDirection = facingDirection.getAxisDirection();
        if (!axis.equals(facingDirection.getAxis())) {
            rotateStep++;
            axisDirection = Direction.fromYRot(facingDirection.toYRot() + 90).getAxisDirection();
        }

        if (axisDirection.getStep() != step) {
            rotateStep += 2;
        }

        if (rotateStep > 2) {
            rotateStep -= 4;
        }

        return rotateStep;
    }
}
