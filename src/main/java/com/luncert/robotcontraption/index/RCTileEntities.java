package com.luncert.robotcontraption.index;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.common.HorizontalHalfShaftInstance;
import com.luncert.robotcontraption.content.aircraft.AircraftStationRenderer;
import com.luncert.robotcontraption.content.aircraft.AircraftStationTileEntity;
import com.luncert.robotcontraption.content.aircraftcontroller.AircraftControllerTileEntity;
import com.luncert.robotcontraption.content.blockreader.BlockReaderTileEntity;
import com.luncert.robotcontraption.content.depothopper.DepotHopperTileEntity;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineInstance;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineRenderer;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineTileEntity;
import com.luncert.robotcontraption.content.geoscanner.GeoScannerTileEntity;
import com.luncert.robotcontraption.content.storageaccessor.StorageAccessorTileEntity;
import com.luncert.robotcontraption.content.vacuumpump.VacuumPumpInstance;
import com.luncert.robotcontraption.content.vacuumpump.VacuumPumpRenderer;
import com.luncert.robotcontraption.content.vacuumpump.VacuumPumpTileEntity;
import com.simibubi.create.repack.registrate.util.entry.BlockEntityEntry;

public class RCTileEntities {

    public static final BlockEntityEntry<AircraftStationTileEntity> AIRCRAFT_STATION =
            RobotContraption.registrate()
                    .tileEntity("aircraft_station", AircraftStationTileEntity::new)
                    .instance(() -> HorizontalHalfShaftInstance::new)
                    .validBlocks(RCBlocks.AIRCRAFT_STATION)
                    .renderer(() -> AircraftStationRenderer::new)
                    .register();

    public static final BlockEntityEntry<DepotHopperTileEntity> DEPOT_HOPPER =
            RobotContraption.registrate()
                    .tileEntity("depot_hopper", DepotHopperTileEntity::new)
                    .validBlocks(RCBlocks.DEPOT_HOPPER)
                    .register();

    public static final BlockEntityEntry<FuelEngineTileEntity> FUEL_ENGINE =
            RobotContraption.registrate()
                    .tileEntity("fuel_engine", FuelEngineTileEntity::new)
                    .instance(() -> FuelEngineInstance::new, false)
                    .validBlocks(RCBlocks.FUEL_ENGINE)
                    .renderer(() -> FuelEngineRenderer::new)
                    .register();

    public static final BlockEntityEntry<VacuumPumpTileEntity> VACUUM_PUMP =
            RobotContraption.registrate()
                    .tileEntity("vacuum_pump", VacuumPumpTileEntity::new)
                    .instance(() -> VacuumPumpInstance::new, false)
                    .validBlocks(RCBlocks.VACUUM_PUMP)
                    .renderer(() -> VacuumPumpRenderer::new)
                    .register();

    public static final BlockEntityEntry<GeoScannerTileEntity> GEO_SCANNER =
            RobotContraption.registrate()
                    .tileEntity("geo_scanner", GeoScannerTileEntity::new)
                    .validBlocks(RCBlocks.GEO_SCANNER)
                    .register();

    public static final BlockEntityEntry<AircraftControllerTileEntity> AIRCRAFT_CONTROLLER =
            RobotContraption.registrate()
                    .tileEntity("aircraft_controller", AircraftControllerTileEntity::new)
                    .validBlocks(RCBlocks.AIRCRAFT_CONTROLLER)
                    .register();

    public static final BlockEntityEntry<StorageAccessorTileEntity> STORAGE_ACCESSOR =
            RobotContraption.registrate()
                    .tileEntity("storage_accessor", StorageAccessorTileEntity::new)
                    .validBlocks(RCBlocks.STORAGE_ACCESSOR)
                    .register();

    public static final BlockEntityEntry<BlockReaderTileEntity> BLOCK_READER =
            RobotContraption.registrate()
                    .tileEntity("block_reader", BlockReaderTileEntity::new)
                    .validBlocks(RCBlocks.BLOCK_READER)
                    .register();

    public static void register() {}
}
