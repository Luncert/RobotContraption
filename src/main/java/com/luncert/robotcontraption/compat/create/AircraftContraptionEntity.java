package com.luncert.robotcontraption.compat.create;

import com.luncert.robotcontraption.content.index.RCEntityTypes;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.StabilizedContraption;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
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

        Entity e = getVehicle();
        if (e == null)
            return;

        boolean rotationLock = false;
        boolean pauseWhileRotating = false;
        boolean wasStalled = isStalled();
        if (contraption instanceof AircraftContraption mountedContraption) {
            rotationLock = mountedContraption.rotationMode == AircraftMovementMode.ROTATION_LOCKED;
            pauseWhileRotating = mountedContraption.rotationMode == AircraftMovementMode.ROTATE_PAUSED;
        }

        Entity riding = e;
        while (riding.getVehicle() != null && !(contraption instanceof StabilizedContraption))
            riding = riding.getVehicle();

        boolean rotating = updateOrientation(rotationLock, wasStalled, riding, false);
        if (!rotating || !pauseWhileRotating)
            tickActors();
    }
}
