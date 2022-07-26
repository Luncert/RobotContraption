package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.compat.computercraft.AircraftStationPeripheral;
import com.luncert.robotcontraption.compat.computercraft.Peripherals;
import com.luncert.robotcontraption.compat.create.AircraftMovementMode;
import com.luncert.robotcontraption.config.Config;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class AircraftStationTileEntity extends GeneratingKineticTileEntity {

    private static final Integer RPM_RANGE = Config.ROBOT_RPM_RANGE.get();

    private LazyOptional<AircraftStationPeripheral> peripheral;

    // create

    private boolean cc_update_rpm = false;
    private int cc_new_rpm = 32;
    private boolean assembleNextTick;

    // CC

    int cc_antiSpam = 0;
    boolean first = true;

    public AircraftStationTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        peripheral = LazyOptional.of(() -> Peripherals.createRobotStationPeripheral(this));
        setLazyTickRate(20);
    }

    // api

    public void assemble(AircraftMovementMode mode) throws AssemblyException {
        AircraftEntity aircraft = new AircraftEntity(level, getBlockState());
        level.addFreshEntity(aircraft);
        aircraft.assembleStructure(worldPosition);
    }

    public void dissemble() {

    }

    public boolean setRPM(int rpm) {
        //System.out.println("SET SPEED" + rpm);
        rpm = Math.max(Math.min(rpm, RPM_RANGE), -RPM_RANGE);
        cc_new_rpm = rpm;
        cc_update_rpm = true;
        return cc_antiSpam > 0;
    }

    public int getRPM() {
        return cc_new_rpm;//generatedSpeed.getValue();
    }

    // forge

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(Peripherals.isPeripheral(cap)) {
            return peripheral.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        peripheral.invalidate();
    }

    // mc


    @Override
    public void tick() {
        super.tick();
    }
}
