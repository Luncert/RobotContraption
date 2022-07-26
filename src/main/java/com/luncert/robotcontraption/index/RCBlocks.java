package com.luncert.robotcontraption.index;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.compat.aircraft.BlockDefaults;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineMovementBehaviour;
import com.luncert.robotcontraption.content.jetengine.JetEngineBlock;
import com.luncert.robotcontraption.content.vacuumpump.VacuumPumpMovementBehaviour;
import com.luncert.robotcontraption.content.aircraftcontroller.AircraftControllerBlock;
import com.luncert.robotcontraption.content.blockreader.BlockReaderBlock;
import com.luncert.robotcontraption.content.depothopper.DepotHopperBlock;
import com.luncert.robotcontraption.content.depothopper.DepotHopperItem;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineBlock;
import com.luncert.robotcontraption.content.geoscanner.GeoScannerBlock;
import com.luncert.robotcontraption.content.storageaccessor.StorageAccessorBlock;
import com.luncert.robotcontraption.content.vacuumpump.VacuumPumpBlock;
import com.luncert.robotcontraption.groups.ModGroup;
import com.luncert.robotcontraption.content.aircraft.AircraftAnchorBlock;
import com.luncert.robotcontraption.content.aircraft.AircraftStationBlock;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class RCBlocks {

    private static final CreateRegistrate REGISTRATE = RobotContraption.registrate()
            .creativeModeTab(() -> ModGroup.MAIN);

    public static final BlockEntry<AircraftStationBlock> AIRCRAFT_STATION =
            REGISTRATE.block("aircraft_station", AircraftStationBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag) //Dono what this tag means (contraption safe?).
                    .transform(BlockStressDefaults.setImpact(4d))
                    .item()
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<DepotHopperBlock> DEPOT_HOPPER =
            REGISTRATE.block("depot_hopper", DepotHopperBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .item(DepotHopperItem::new)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<AircraftAnchorBlock> AIRCRAFT_ANCHOR =
            REGISTRATE.block("aircraft_anchor", AircraftAnchorBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
                            .getExistingFile(p.modLoc("block/aircraft_station/" + c.getName()))))
                    .register();

    public static final BlockEntry<FuelEngineBlock> FUEL_ENGINE =
            REGISTRATE.block("fuel_engine", FuelEngineBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .blockstate(BlockStateGen.directionalBlockProvider(true))
                    .addLayer(() -> RenderType::translucent)
                    .transform(BlockStressDefaults.setCapacity(64d))
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .onRegister(AllMovementBehaviours.movementBehaviour(new FuelEngineMovementBehaviour()))
                    .item()
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<VacuumPumpBlock> VACUUM_PUMP =
            REGISTRATE.block("vacuum_pump", VacuumPumpBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .onRegister(AllMovementBehaviours.movementBehaviour(new VacuumPumpMovementBehaviour()))
                    .item()
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<GeoScannerBlock> GEO_SCANNER =
            REGISTRATE.block("geo_scanner", GeoScannerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .simpleItem()
                    .register();

    public static final BlockEntry<AircraftControllerBlock> AIRCRAFT_CONTROLLER =
            REGISTRATE.block("aircraft_controller", AircraftControllerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .simpleItem()
                    .register();

    public static final BlockEntry<StorageAccessorBlock> STORAGE_ACCESSOR =
            REGISTRATE.block("storage_accessor", StorageAccessorBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .simpleItem()
                    .register();

    public static final BlockEntry<BlockReaderBlock> BLOCK_READER =
            REGISTRATE.block("block_reader", BlockReaderBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .simpleItem()
                    .register();

    public static final BlockEntry<JetEngineBlock> JET_ENGINE =
            REGISTRATE.block("jet_engine", JetEngineBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .transform(BlockStressDefaults.setImpact(4d))
                    .transform(BlockDefaults.setThrust(16))
                    .simpleItem()
                    .register();

    public static void register() {
        Create.registrate().addToSection(AIRCRAFT_STATION, AllSections.KINETICS);
        Create.registrate().addToSection(FUEL_ENGINE, AllSections.KINETICS);
    }
}
