package com.luncert.robotcontraption.compat.computercraft;

import com.luncert.robotcontraption.content.robot.RobotStationTileEntity;
import net.minecraftforge.common.capabilities.Capability;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class Peripherals {

    public static boolean isPeripheral(Capability<?> cap) {
        return cap == CAPABILITY_PERIPHERAL;
    }

    public static RobotStationPeripheral createRobotStationPeripheral(RobotStationTileEntity te) {
        return new RobotStationPeripheral("robot_station", te);
    }
}
