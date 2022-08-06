package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.common.ActionCallback;
import com.luncert.robotcontraption.common.LocalVariable;
import com.luncert.robotcontraption.compat.computercraft.AircraftStationPeripheral;
import com.luncert.robotcontraption.compat.computercraft.EHarvestable;
import com.luncert.robotcontraption.compat.computercraft.Peripherals;
import com.luncert.robotcontraption.compat.create.EAircraftMovementMode;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import com.luncert.robotcontraption.util.ScanUtils;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

import static com.simibubi.create.content.contraptions.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class AircraftStationTileEntity extends KineticTileEntity {

    private final LazyOptional<AircraftStationPeripheral> peripheral;
    private AircraftEntity entity;

    public AircraftStationTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        peripheral = LazyOptional.of(() -> Peripherals.createRobotStationPeripheral(this));
        setLazyTickRate(20);
    }

    // api

    public void assemble(EAircraftMovementMode mode) throws AircraftAssemblyException {
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

        // Vec3 blockPos = Vec3.atCenterOf(getBlockPos()).add(0, -0.5, 0);
        // if (!blockPos.equals(entity.position())) {
        //     throw new AircraftAssemblyException("not_dissemble_at_station");
        // }

        entity.dissemble();
        entity = null;
    }

    public void up(int n, ActionCallback callback) throws AircraftMovementException, AircraftAssemblyException {
        checkContraptionStatus();
        if (getAircraftSpeed() == 0) {
            throw new AircraftMovementException("speed_is_zero");
        }
        entity.up(n, callback);
    }

    public void down(int n, ActionCallback callback) throws AircraftMovementException, AircraftAssemblyException {
        checkContraptionStatus();
        if (getAircraftSpeed() == 0) {
            throw new AircraftMovementException("speed_is_zero");
        }
        entity.down(n, callback);
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

    public void setAircraftSpeed(int speed) throws AircraftAssemblyException, AircraftMovementException {
        checkContraptionStatus();
        entity.setSpeed(speed);
    }

    public int getAircraftSpeed() throws AircraftAssemblyException {
        checkContraptionStatus();
        return entity.getSpeed();//generatedSpeed.getValue();
    }

    public Vec3 getAircraftPosition() throws AircraftAssemblyException {
        checkContraptionStatus();
        return entity.getAircraftPosition();
    }

    public Vec3 getStationPosition() {
        return new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }

    public float getStorageUsage() throws AircraftAssemblyException {
        checkContraptionStatus();

        return entity.getStorageUsage();
    }

    public float getStorageSlotUsage() throws AircraftAssemblyException {
        checkContraptionStatus();

        return entity.getStorageSlotUsage();
    }

    public Optional<Pair<Vec3, Vec3>> search(EHarvestable target) throws AircraftAssemblyException {
        checkContraptionStatus();

        BlockPos center = entity.blockPosition();

        LocalVariable<Pair<Vec3, Vec3>> ref = new LocalVariable<>();

        ScanUtils.traverseBlocks(level, center, 8, (state, pos) -> {
            if (target.test(state)) {
                ref.set(ScanUtils.calcShapeForAdjacentBlocks(level, pos));
                return false;
            }
            return true;
        });

        return Optional.ofNullable(ref.get());
    }

    public Direction getAircraftFacing() throws AircraftAssemblyException {
        checkContraptionStatus();

        return entity.getAircraftFacing();
    }

    public Direction getStationFacing() {
        return getBlockState().getValue(HORIZONTAL_FACING);
    }


    public int calcRotationTo(Direction.Axis axis, int step) throws AircraftAssemblyException {
        checkContraptionStatus();

        int rotateStep = 0;

        Direction facingDirection = getAircraftFacing();
        Direction.AxisDirection axisDirection = facingDirection.getAxisDirection();
        if (!axis.equals(facingDirection.getAxis())) {
            rotateStep++;
            axisDirection = Direction.fromYRot(facingDirection.toYRot() + 90).getAxisDirection();
        }

        if (axisDirection.getStep() != step) {
            rotateStep += 2;
        }

        if (rotateStep > 2) {
            rotateStep -= 4;
        }

        return rotateStep;
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
