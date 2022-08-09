package com.luncert.robotcontraption.compat.computercraft;

import com.luncert.robotcontraption.compat.create.AircraftContraption;
import com.luncert.robotcontraption.content.aircraft.AircraftEntity;
import com.luncert.robotcontraption.content.aircraft.AircraftStationTileEntity;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class AircraftAccessor {

    public final Level world;
    public final AircraftStationPeripheral peripheral;
    public final AircraftStationTileEntity station;
    public final AircraftEntity aircraft;
    public final AircraftContraption contraption;

    public AircraftAccessor(Level world,
                            AircraftStationPeripheral peripheral,
                            AircraftStationTileEntity station,
                            AircraftEntity aircraft,
                            AircraftContraption contraption) {
        this.world = world;
        this.peripheral = peripheral;
        this.station = station;
        this.aircraft = aircraft;
        this.contraption = contraption;
    }

    @SuppressWarnings("unchecked")
    public <T extends IAircraftComponent> Optional<T> findOne(String componentType) {
        List<IAircraftComponent> components = station.getComponents().get(componentType);
        if (components != null && components.size() > 0) {
            return Optional.of((T) components.get(0));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends IAircraftComponent> List<T> findAll(String componentType) {
        List<IAircraftComponent> components = station.getComponents().get(componentType);
        return components == null ? Collections.emptyList() : (List<T>) components;
    }

    public void queueEvent(String event, Object... args) {
        withComputer(c -> c.queueEvent(event, args));
    }

    private void withComputer(Consumer<IComputerAccess> action) {
        for (IComputerAccess computer : peripheral.getConnectedComputers()) {
            action.accept(computer);
        }
    }
}
