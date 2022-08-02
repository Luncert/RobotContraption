package com.luncert.robotcontraption.compat.create;

import com.luncert.robotcontraption.content.aircraft.AircraftEntity;
import com.luncert.robotcontraption.index.RCEntityTypes;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class AircraftContraptionEntity extends OrientedContraptionEntity {

    public AircraftContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static AircraftContraptionEntity create(Level world, Contraption contraption, Direction initialOrientation) {
        AircraftContraptionEntity entity = new AircraftContraptionEntity(RCEntityTypes.AIRCRAFT_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        entity.setInitialOrientation(initialOrientation);
        entity.startAtInitialYaw();
        return entity;
    }

    @Override
    protected void tickContraption() {
        if (nonDamageTicks > 0)
            nonDamageTicks--;

        AircraftEntity vehicle = (AircraftEntity) getVehicle();
        if (vehicle == null)
            return;

        boolean rotationLock = false;
        boolean pauseWhileRotating = false;
        boolean wasStalled = isStalled();
        if (contraption instanceof AircraftContraption contraption) {
            rotationLock = contraption.rotationMode == AircraftMovementMode.ROTATION_LOCKED;
            pauseWhileRotating = contraption.rotationMode == AircraftMovementMode.ROTATE_PAUSED;
        }

        boolean rotating = updateOrientation(rotationLock, wasStalled, vehicle, false);
        if (!rotating || !pauseWhileRotating)
            tickActors();
    }
}
