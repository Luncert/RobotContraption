package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.compat.computercraft.AircraftActionEvent;
import com.luncert.robotcontraption.compat.computercraft.AircraftStationPeripheral;
import com.luncert.robotcontraption.compat.computercraft.Peripherals;
import com.luncert.robotcontraption.compat.create.AircraftMovementMode;
import com.luncert.robotcontraption.config.Config;
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

    public void assemble(AircraftMovementMode mode) throws AircraftAssemblyException {
        AircraftEntity aircraft = new AircraftEntity(level, worldPosition, getBlockState());
        level.addFreshEntity(aircraft);
        aircraft.assemble(worldPosition, mode);
        this.entity = aircraft;
    }

    public void dissemble() throws AircraftAssemblyException {
        if (entity == null) {
            throw new AircraftAssemblyException("entity_missing");
        }

        Vec3 blockPos = Vec3.atCenterOf(getBlockPos()).add(0, -0.5, 0);
        if (!blockPos.equals(entity.position())) {
            System.out.println(blockPos);
            System.out.println(entity.position());
            throw new AircraftAssemblyException("not_dissemble_at_station");
        }

        entity.dissemble();
    }

    public void forward(int n, AircraftEntityActionCallback callback) throws AircraftMovementException {
        entity.forward(n, callback);
    }

    public void rotate(int degree, AircraftEntityActionCallback callback) throws AircraftMovementException {
        entity.rotate(degree, callback);
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
