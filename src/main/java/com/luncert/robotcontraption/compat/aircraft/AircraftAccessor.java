package com.luncert.robotcontraption.compat.aircraft;

import com.luncert.robotcontraption.compat.computercraft.AircraftStationPeripheral;
import com.luncert.robotcontraption.compat.create.AircraftContraption;
import com.luncert.robotcontraption.content.aircraft.AircraftEntity;
import com.luncert.robotcontraption.content.aircraft.AircraftStationTileEntity;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class AircraftAccessor {

    public final Level world;
    public final AircraftStationPeripheral peripheral;
    public final AircraftStationTileEntity station;
    public final AircraftEntity aircraft;
    public final AircraftContraption contraption;
    public final AircraftContextResources resources = new AircraftContextResources();

    public AircraftAccessor(Level world,
                            AircraftStationPeripheral peripheral,
                            AircraftStationTileEntity station,
                            AircraftEntity aircraft,
                            AircraftContraption contraption) {
        Objects.requireNonNull(world);
        Objects.requireNonNull(peripheral);
        Objects.requireNonNull(station);
        Objects.requireNonNull(aircraft);
        Objects.requireNonNull(contraption);
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

    public Optional<IAircraftComponent> getComponent(String componentName) {
        Pair<String, Integer> name = BaseAircraftComponent.parseName(componentName);

        List<IAircraftComponent> components = station.getComponents().get(name.getKey());
        if (components != null && components.size() > name.getValue()) {
            return Optional.of(components.get(name.getValue()));
        }

        return Optional.empty();
    }

    public BlockPos getComponentPos(String name) {
        StructureTemplate.StructureBlockInfo blockInfo = contraption.getComponentBlockInfo(name);
        if (blockInfo == null) {
            throw new IllegalArgumentException("block info missing for " + name);
        }

        return contraption.getWorldPos(blockInfo.pos);
    }

    public BlockState getComponentBlockState(String name) {
        StructureTemplate.StructureBlockInfo blockInfo = contraption.getComponentBlockInfo(name);
        if (blockInfo == null) {
            throw new IllegalArgumentException("block info missing for " + name);
        }

        return blockInfo.state;
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
