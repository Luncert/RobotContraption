package com.luncert.robotcontraption.content.index;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.content.robot.RobotStationInstance;
import com.luncert.robotcontraption.content.robot.RobotStationRenderer;
import com.luncert.robotcontraption.content.robot.RobotStationTileEntity;
import com.simibubi.create.repack.registrate.util.entry.BlockEntityEntry;

public class RCTileEntities {

    public static final BlockEntityEntry<RobotStationTileEntity> ROBOT_STATION = RobotContraption.registrate()
            .tileEntity("robot_station", RobotStationTileEntity::new)
            .instance(() -> RobotStationInstance::new)
            .validBlocks(RCBlocks.ROBOT_STATION)
            .renderer(() -> RobotStationRenderer::new)
            .register();

    public static void register() {}
}
