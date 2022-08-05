package com.luncert.robotcontraption.compat.create;

import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerTileEntity;

public enum EAircraftMovementMode {

    // Always face toward motion
    ROTATE(CartAssemblerTileEntity.CartMovementMode.ROTATE),

    // Pause actors while rotating
    ROTATE_PAUSED(CartAssemblerTileEntity.CartMovementMode.ROTATE_PAUSED),

    // Lock rotation
    ROTATION_LOCKED(CartAssemblerTileEntity.CartMovementMode.ROTATION_LOCKED),
    ;

    private final CartAssemblerTileEntity.CartMovementMode mode;

    EAircraftMovementMode(CartAssemblerTileEntity.CartMovementMode mode) {
        this.mode = mode;
    }

    public CartAssemblerTileEntity.CartMovementMode toCartMovementMode() {
        return mode;
    }
}