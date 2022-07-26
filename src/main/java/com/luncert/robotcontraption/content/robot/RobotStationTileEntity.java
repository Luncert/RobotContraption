package com.luncert.robotcontraption.content.robot;

import com.luncert.robotcontraption.config.Config;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RobotStationTileEntity extends GeneratingKineticTileEntity {

    private static final Integer RPM_RANGE = Config.ROBOT_RPM_RANGE.get();

    // create

    private boolean cc_update_rpm = false;
    private int cc_new_rpm = 32;

    // CC

    int cc_antiSpam = 0;
    boolean first = true;

    public RobotStationTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // api

    public boolean setRPM(int rpm) {
        //System.out.println("SETSPEED" + rpm);
        rpm = Math.max(Math.min(rpm, RPM_RANGE), -RPM_RANGE);
        cc_new_rpm = rpm;
        cc_update_rpm = true;
        return cc_antiSpam > 0;
    }

    public int getRPM() {
        return cc_new_rpm;//generatedSpeed.getValue();
    }
}
