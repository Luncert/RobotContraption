package com.luncert.robotcontraption.content.aircraftcontroller;

import com.google.common.collect.ImmutableMap;
import com.luncert.robotcontraption.compat.aircraft.AircraftComponentType;
import com.luncert.robotcontraption.compat.aircraft.IAircraftComponent;
import com.luncert.robotcontraption.compat.computercraft.AircraftApiCallback;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import com.luncert.robotcontraption.content.aircraft.TickOrder;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineComponent;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Map;

import static com.luncert.robotcontraption.compat.aircraft.AircraftComponentType.AIRCRAFT_CONTROLLER;
import static com.luncert.robotcontraption.compat.computercraft.AircraftActionEvent.EVENT_AIRCRAFT_MOVEMENT_DONE;

@TickOrder(2)
public class AircraftControllerComponent extends BaseAircraftComponent {

    private int speed = 0;
    private int maxSpeed = 256;
    private int executionId;

    @Override
    public void tickComponent() {
        double thrust = accessor.resources.getResource("thrust", 0d);
        maxSpeed = (int) (Mth.clamp(thrust / accessor.contraption.getBlocks().size(), 0, 1) * 256);
        if (speed > maxSpeed) {
            speed = maxSpeed;
            accessor.aircraft.setKineticSpeed(speed);
        }
    }

    @Override
    public Tag writeNBT() {
        return IntTag.valueOf(speed);
    }

    @Override
    public void readNBT(Level world, Tag tag) {
        speed = ((IntTag) tag).getAsInt();
    }

    @Override
    public AircraftComponentType getComponentType() {
        return AIRCRAFT_CONTROLLER;
    }

    @LuaFunction
    public final void turnOnGenerators() {
        for (IAircraftComponent component : accessor.findAll("fuel-engine")) {
            ((FuelEngineComponent) component).turnOn();
        }
    }

    @LuaFunction
    public final void turnOffGenerators() {
        for (IAircraftComponent component : accessor.findAll("fuel-engine")) {
            ((FuelEngineComponent) component).turnOff();
        }
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

    @LuaFunction
    public final void setSpeed(int speed) throws LuaException {
        if (speed > maxSpeed) {
            throw new LuaException("max speed is " + maxSpeed);
        }

        this.speed = speed;
        accessor.aircraft.setKineticSpeed(speed);
    }

    @LuaFunction
    public final int getSpeed() {
        return speed;
    }

    @LuaFunction
    public final Map<String, Double> getAircraftPosition() {
        Vec3 pos = accessor.aircraft.getAircraftPosition();
        return ImmutableMap.of(
                "x", pos.x,
                "y", pos.y,
                "z", pos.z
        );
    }

    @LuaFunction
    public final Map<String, Double> getStationPosition() {
        Vec3 pos = accessor.station.getStationPosition();
        return ImmutableMap.of(
                "x", pos.x,
                "y", pos.y,
                "z", pos.z
        );
    }

    @LuaFunction
    public final Map<String, Object> getAircraftFacing() {
        Direction direction = accessor.aircraft.getAircraftFacing();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        return ImmutableMap.of(
                "axis", direction.getAxis().getName(),
                "step", axisDirection.getStep()
        );
    }

    @LuaFunction
    public final Map<String, Object> getStationFacing() {
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
