package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.compat.computercraft.AircraftStationPeripheral;
import com.luncert.robotcontraption.compat.computercraft.Peripherals;
import com.luncert.robotcontraption.compat.create.AircraftMovementMode;
import com.luncert.robotcontraption.config.Config;
import com.luncert.robotcontraption.common.ActionCallback;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class AircraftStationTileEntity extends GeneratingKineticTileEntity {

    private static final Integer RPM_RANGE = Config.ROBOT_RPM_RANGE.get();

    private LazyOptional<AircraftStationPeripheral> peripheral;
    private AircraftEntity entity;

    public AircraftStationTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        peripheral = LazyOptional.of(() -> Peripherals.createRobotStationPeripheral(this));
        setLazyTickRate(20);
    }

    // api

    public void assemble(AircraftMovementMode mode) throws AircraftAssemblyException {
        if (entity != null) {
            throw new AircraftAssemblyException("aircraft_assembled");
        }

        AircraftEntity aircraft = new AircraftEntity(level, worldPosition, getBlockState());
        level.addFreshEntity(aircraft);
        aircraft.assemble(worldPosition, mode);
        this.entity = aircraft;
    }

    public void dissemble() throws AircraftAssemblyException {
        checkContraptionStatus();

        Vec3 blockPos = Vec3.atCenterOf(getBlockPos()).add(0, -0.5, 0);
        if (!blockPos.equals(entity.position())) {
            System.out.println(blockPos);
            System.out.println(entity.position());
            throw new AircraftAssemblyException("not_dissemble_at_station");
        }

        entity.dissemble();
        entity = null;
    }

    public void forward(int n, ActionCallback callback) throws AircraftMovementException, AircraftAssemblyException {
        checkContraptionStatus();
        if (getAircraftSpeed() == 0) {
            throw new AircraftMovementException("speed_is_zero");
        }
        entity.forward(n, callback);
    }

    public void turnLeft(int n, ActionCallback callback) throws AircraftMovementException, AircraftAssemblyException {
        checkContraptionStatus();
        if (getAircraftSpeed() == 0) {
            throw new AircraftMovementException("speed_is_zero");
        }
        entity.turnLeft(n, callback);
    }

    public void turnRight(int n, ActionCallback callback) throws AircraftMovementException, AircraftAssemblyException {
        checkContraptionStatus();
        if (getAircraftSpeed() == 0) {
            throw new AircraftMovementException("speed_is_zero");
        }
        entity.turnRight(n, callback);
    }

    public void setAircraftSpeed(int speed) throws AircraftAssemblyException {
        checkContraptionStatus();
        entity.setSpeed(speed);
    }

    public int getAircraftSpeed() throws AircraftAssemblyException {
        checkContraptionStatus();
        return entity.getSpeed();//generatedSpeed.getValue();
    }

    private void checkContraptionStatus() throws AircraftAssemblyException {
        if (entity == null) {
            throw new AircraftAssemblyException("aircraft_dissembled");
        }
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
