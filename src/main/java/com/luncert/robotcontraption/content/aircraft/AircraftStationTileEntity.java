package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.compat.aircraft.IAircraftComponent;
import com.luncert.robotcontraption.compat.computercraft.AircraftStationPeripheral;
import com.luncert.robotcontraption.compat.computercraft.Peripherals;
import com.luncert.robotcontraption.compat.create.AircraftContraption;
import com.luncert.robotcontraption.compat.create.EAircraftMovementMode;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.simibubi.create.content.contraptions.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class AircraftStationTileEntity extends KineticTileEntity {

    private AircraftStationPeripheral peripheral;
    private UUID aircraftId;
    private AircraftEntity aircraft;

    public AircraftStationTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        peripheral = Peripherals.createRobotStationPeripheral(this);
        setLazyTickRate(20);
    }

    // api

    public AircraftStationPeripheral getPeripheral() {
        return peripheral;
    }

    public Map<String, List<IAircraftComponent>> getComponents() {
        if (aircraft == null) {
            return Collections.emptyMap();
        }
        return aircraft.getContraption().map(AircraftContraption::getComponents).orElse(Collections.emptyMap());
    }

    public void assemble(EAircraftMovementMode mode) throws AircraftAssemblyException {
        if (aircraft != null) {
            throw new AircraftAssemblyException("aircraft_assembled");
        }

        AircraftEntity aircraft = new AircraftEntity(level, worldPosition, getBlockState());
        level.addFreshEntity(aircraft);
        if (!aircraft.assemble(worldPosition, mode)) {
            aircraft.discard();
            throw new AircraftAssemblyException("structure_not_found");
        }
        this.aircraft = aircraft;
        this.aircraftId = aircraft.getUUID();
    }

    public void dissemble() throws AircraftAssemblyException {
        checkContraptionStatus();

        // Vec3 blockPos = Vec3.atCenterOf(getBlockPos()).add(0, -0.5, 0);
        // if (!blockPos.equals(entity.position())) {
        //     throw new AircraftAssemblyException("not_dissemble_at_station");
        // }

        aircraft.dissemble();
        aircraft = null;
    }

    public Vec3 getStationPosition() {
        return new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }

    public Direction getStationFacing() {
        return getBlockState().getValue(HORIZONTAL_FACING);
    }

    private void checkContraptionStatus() throws AircraftAssemblyException {
        if (aircraft == null) {
            throw new AircraftAssemblyException("aircraft_dissembled");
        }
    }

    // forge

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(Peripherals.isPeripheral(cap)) {
            return LazyOptional.of(() -> peripheral).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        peripheral = null;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        if (!clientPacket) {
            compound.putString("aircraft", aircraftId.toString());
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        if (!clientPacket) {
            aircraftId = UUID.fromString(compound.getString("aircraft"));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level instanceof ServerLevel world && aircraft == null && aircraftId != null) {
            if (world.getEntity(aircraftId) instanceof AircraftEntity aircraft) {
                this.aircraft = aircraft;
                aircraft.bindStation(this);
            }
        }
    }
}
