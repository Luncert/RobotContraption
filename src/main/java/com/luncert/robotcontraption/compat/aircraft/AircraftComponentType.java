package com.luncert.robotcontraption.compat.aircraft;

import com.luncert.robotcontraption.content.aircraftcontroller.AircraftControllerComponent;
import com.luncert.robotcontraption.content.blockreader.BlockReaderComponent;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineComponent;
import com.luncert.robotcontraption.content.geoscanner.GeoScannerComponent;
import com.luncert.robotcontraption.content.jetengine.JetEngineComponent;
import com.luncert.robotcontraption.content.storageaccessor.StorageAccessorComponent;
import com.luncert.robotcontraption.content.vacuumpump.VacuumPumpComponent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AircraftComponentType {

    private static final Map<String, Class<? extends IAircraftComponent>> TYPE_MAPPINGS = new HashMap<>();

    public static final AircraftComponentType VACUUM_PUMP = AircraftComponentType.register("vacuum-pump", VacuumPumpComponent.class);
    public static final AircraftComponentType STORAGE_ACCESSOR = AircraftComponentType.register("storage-accessor", StorageAccessorComponent.class);
    public static final AircraftComponentType JET_ENGINE = AircraftComponentType.register("jet-engine", JetEngineComponent.class);
    public static final AircraftComponentType GEO_SCANNER = AircraftComponentType.register("geo-scanner", GeoScannerComponent.class);
    public static final AircraftComponentType FUEL_ENGINE = AircraftComponentType.register("fuel-engine", FuelEngineComponent.class);
    public static final AircraftComponentType AIRCRAFT_CONTROLLER = AircraftComponentType.register("controller", AircraftControllerComponent.class);
    public static final AircraftComponentType BLOCK_READER = AircraftComponentType.register("block-reader", BlockReaderComponent.class);

    public static AircraftComponentType register(String name, Class<? extends IAircraftComponent> type) {
        if (TYPE_MAPPINGS.containsKey(name)) {
            throw new IllegalArgumentException("cannot register duplicated component type: " + name + " " + type);
        }
        TYPE_MAPPINGS.put(name, type);
        return new AircraftComponentType(name);
    }

    public static IAircraftComponent createComponent(String name) {
        Class<? extends IAircraftComponent> type = TYPE_MAPPINGS.get(name);
        if (type == null) {
            throw new IllegalArgumentException("invalid type name: " + name);
        }

        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final String name;

    private AircraftComponentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
