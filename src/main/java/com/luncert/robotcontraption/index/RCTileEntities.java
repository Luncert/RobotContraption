package com.luncert.robotcontraption.index;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.content.aircraft.AircraftStationInstance;
import com.luncert.robotcontraption.content.aircraft.AircraftStationRenderer;
import com.luncert.robotcontraption.content.aircraft.AircraftStationTileEntity;
import com.luncert.robotcontraption.content.depothopper.DepotHopperTileEntity;
import com.simibubi.create.repack.registrate.util.entry.BlockEntityEntry;

public class RCTileEntities {

    public static void register() {}

    public static final BlockEntityEntry<AircraftStationTileEntity> AIRCRAFT_STATION =
            RobotContraption.registrate()
                    .tileEntity("aircraft_station", AircraftStationTileEntity::new)
                    .instance(() -> AircraftStationInstance::new)
                    .validBlocks(RCBlocks.AIRCRAFT_STATION)
                    .renderer(() -> AircraftStationRenderer::new)
                    .register();

    public static final BlockEntityEntry<DepotHopperTileEntity> DEPOT_HOPPER =
            RobotContraption.registrate()
                    .tileEntity("depot_hopper", DepotHopperTileEntity::new)
                    .validBlocks(RCBlocks.DEPOT_HOPPER)
                    .register();

}
