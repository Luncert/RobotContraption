package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.common.ActionCallback;
import com.luncert.robotcontraption.compat.computercraft.AircraftStationPeripheral;
import com.luncert.robotcontraption.compat.computercraft.Peripherals;
import com.luncert.robotcontraption.compat.create.AircraftMovementMode;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class AircraftStationTileEntity extends KineticTileEntity {

    private final LazyOptional<AircraftStationPeripheral> peripheral;
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
        if (!aircraft.assemble(worldPosition, mode)) {
            aircraft.discard();
            throw new AircraftAssemblyException("structure_not_found");
        }
        this.entity = aircraft;
    }

    public void dissemble() throws AircraftAssemblyException {
        checkContraptionStatus();

        Vec3 blockPos = Vec3.atCenterOf(getBlockPos()).add(0, -0.5, 0);
        if (!blockPos.equals(entity.position())) {
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

    public void turnLeft(ActionCallback callback) throws AircraftMovementException, AircraftAssemblyException {
        checkContraptionStatus();
        if (getAircraftSpeed() == 0) {
            throw new AircraftMovementException("speed_is_zero");
        }
        entity.turnLeft(callback);
    }

    public void turnRight(ActionCallback callback) throws AircraftMovementException, AircraftAssemblyException {
        checkContraptionStatus();
        if (getAircraftSpeed() == 0) {
            throw new AircraftMovementException("speed_is_zero");
        }
        entity.turnRight(callback);
    }

    public void setAircraftSpeed(int speed) throws AircraftAssemblyException {
        checkContraptionStatus();
        entity.setSpeed(speed);
    }

    public int getAircraftSpeed() throws AircraftAssemblyException {
        checkContraptionStatus();
        return entity.getSpeed();//generatedSpeed.getValue();
    }

    public Vec3 getPosition() throws AircraftAssemblyException {
        checkContraptionStatus();
        return entity.getAircraftPosition();
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

    public void rebindAircraftEntity(AircraftEntity entity) {
        this.entity = entity;
    }
}
