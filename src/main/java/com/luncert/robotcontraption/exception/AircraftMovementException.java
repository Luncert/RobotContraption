package com.luncert.robotcontraption.exception;

import com.simibubi.create.foundation.utility.Lang;

public class AircraftMovementException extends Exception {

    public AircraftMovementException(String langKey, Object... objects) {
        super(Lang.translateDirect("gui.movement.exception." + langKey, objects).getString());
    }
}
