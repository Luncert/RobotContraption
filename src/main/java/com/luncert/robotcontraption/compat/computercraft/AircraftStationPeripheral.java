package com.luncert.robotcontraption.compat.computercraft;

import com.google.common.collect.ImmutableMap;
import com.luncert.robotcontraption.compat.create.EAircraftMovementMode;
import com.luncert.robotcontraption.content.aircraft.AircraftStationTileEntity;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static com.luncert.robotcontraption.compat.computercraft.AircraftActionEvent.EVENT_AIRCRAFT_MOVEMENT_DONE;

public class AircraftStationPeripheral implements IPeripheral {

    protected String type;
    protected AircraftStationTileEntity tileEntity;
    private int executionId;

    protected final List<IComputerAccess> connected = new ArrayList<>();

    public AircraftStationPeripheral(String type, AircraftStationTileEntity tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    // computer access

    public void queueEvent(String event, Object... args) {
        withComputer(c -> c.queueEvent(event, args));
    }

    private void withComputer(Consumer<IComputerAccess> action) {
        for (IComputerAccess computer : connected) {
            action.accept(computer);
        }
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connected;
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

    // api

    @LuaFunction(mainThread = true)
    public final void assemble(String rotationMode) throws LuaException {
        checkTileEntity();

        EAircraftMovementMode mode;
        try {
            mode = EAircraftMovementMode.valueOf(rotationMode.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new LuaException("Invalid argument, must be one of " + Arrays.toString(EAircraftMovementMode.values()));
        }

        try {
            tileEntity.assemble(mode);
        } catch (AircraftAssemblyException e) {
            e.printStackTrace();
            throw new LuaException("failed to assemble structure: " + e.getMessage());
        }
    }

    @LuaFunction(mainThread = true)
    public final void dissemble() throws LuaException {
        checkTileEntity();

        try {
            tileEntity.dissemble();
        } catch (AircraftAssemblyException e) {
            e.printStackTrace();
            throw new LuaException("failed to dissemble structure: " + e.getMessage());
        }
    }

    @LuaFunction
    public final MethodResult up(int n) throws LuaException {
        checkTileEntity();

        if (n <= 0) {
            throw new LuaException("n must be positive");
        }

        int executionId = this.executionId++;
        try {
            tileEntity.up(n, data -> queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException | AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }

        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult down(int n) throws LuaException {
        checkTileEntity();

        if (n <= 0) {
            throw new LuaException("n must be positive");
        }

        int executionId = this.executionId++;
        try {
            tileEntity.down(n, data -> queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException | AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }

        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult forward(int n) throws LuaException {
        checkTileEntity();

        if (n <= 0) {
            throw new LuaException("n must be positive");
        }

        int executionId = this.executionId++;
        try {
            tileEntity.forward(n, data -> queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException | AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }

        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult turnLeft() throws LuaException {
        checkTileEntity();

        int executionId = this.executionId++;
        try {
            tileEntity.turnLeft(data -> queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException | AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }

        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction
    public final MethodResult turnRight() throws LuaException {
        checkTileEntity();

        int executionId = this.executionId++;
        try {
            tileEntity.turnRight(data -> queueEvent(EVENT_AIRCRAFT_MOVEMENT_DONE, executionId, data));
        } catch (AircraftMovementException | AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }
        return AircraftApiCallback.hook(executionId, EVENT_AIRCRAFT_MOVEMENT_DONE);
    }

    @LuaFunction(mainThread = true)
    public final void setSpeed(int speed) throws LuaException {
        checkTileEntity();

        try {
            tileEntity.setAircraftSpeed(speed);
        } catch (AircraftAssemblyException | AircraftMovementException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction(mainThread = true)
    public final int getSpeed() throws LuaException {
        checkTileEntity();

        try {
            return tileEntity.getAircraftSpeed();
        } catch (AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final Map<String, Double> getAircraftPosition() throws LuaException {
        checkTileEntity();

        try {
            Vec3 pos = tileEntity.getAircraftPosition();
            return ImmutableMap.of(
                "x", pos.x,
                "y", pos.y,
                "z", pos.z
            );
        } catch (AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final Map<String, Double> getStationPosition() throws LuaException {
        checkTileEntity();

        Vec3 pos = tileEntity.getStationPosition();
        return ImmutableMap.of(
                "x", pos.x,
                "y", pos.y,
                "z", pos.z
        );
    }

    // contraption access

    @LuaFunction
    public final float getStorageUsage() throws LuaException {
        checkTileEntity();

        try {
            return tileEntity.getStorageUsage();
        } catch (AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final float getStorageSlotUsage() throws LuaException {
        checkTileEntity();

        try {
            return tileEntity.getStorageSlotUsage();
        } catch (AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult search(String harvestable) throws LuaException {
        checkTileEntity();

        EHarvestable h;
        try {
            h = EHarvestable.valueOf(harvestable.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new LuaException("Invalid argument, must be one of " + Arrays.toString(EHarvestable.values()));
        }

        Optional<Pair<Vec3, Vec3>> opt;
        try {
            opt = tileEntity.search(h);
        } catch (AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }
        if (opt.isEmpty()) {
            return MethodResult.of(false);
        }

        Pair<Vec3, Vec3> locator = opt.get();
        Vec3 a = locator.getLeft();
        Vec3 b = locator.getRight();
        return MethodResult.of(
                ImmutableMap.of(
                        "x1", a.x,
                        "y1", a.y,
                        "z1", a.z,
                        "x2", b.x,
                        "y2", b.y,
                        "z2", b.z
                ));
    }

    @LuaFunction
    public final Map<String, Object> getFacingDirection() throws LuaException {
        checkTileEntity();

        try {
            Direction direction = tileEntity.getFacingDirection();
            Direction.AxisDirection axisDirection = direction.getAxisDirection();
            return ImmutableMap.of(
                    "axis", direction.getAxis().getName(),
                    "step", axisDirection.getStep()
            );
        } catch (AircraftAssemblyException e) {
            throw new LuaException(e.getMessage());
        }
    }

    private void checkTileEntity() throws LuaException {
        if (tileEntity == null) {
            throw new LuaException("block entity missing");
        }
    }
}
