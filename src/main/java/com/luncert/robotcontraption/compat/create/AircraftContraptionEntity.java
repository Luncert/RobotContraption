package com.luncert.robotcontraption.compat.create;

import com.luncert.robotcontraption.content.aircraft.AircraftEntity;
import com.simibubi.create.AllEntityTypes;
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
        AircraftContraptionEntity entity = new AircraftContraptionEntity((EntityType) AllEntityTypes.ORIENTED_CONTRAPTION.get(), world);
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

    @Override
    protected boolean updateOrientation(boolean rotationLock, boolean wasStalled, Entity riding, boolean isOnCoupling) {
        if (contraption instanceof StabilizedContraption) {
            if (!(riding instanceof OrientedContraptionEntity))
                return false;
            StabilizedContraption stabilized = (StabilizedContraption) contraption;
            Direction facing = stabilized.getFacing();
            if (facing.getAxis()
                    .isVertical())
                return false;
            OrientedContraptionEntity parent = (OrientedContraptionEntity) riding;
            prevYaw = yaw;
            yaw = -parent.getViewYRot(1);
            return false;
        }

        prevYaw = yaw;
        if (wasStalled)
            return false;

        boolean rotating = false;

        if (!rotationLock) {
            AircraftEntity aircraft = (AircraftEntity) riding;
            if (aircraft.isRotating) {
                targetYaw = aircraft.getTargetYRot();
                if (targetYaw < 0)
                    targetYaw += 360;
                if (yaw < 0)
                    yaw += 360;

                prevYaw = yaw;
                yaw += aircraft.deltaRotation;
                rotating = true;
            }
        }

        return rotating;
    }
}
