package com.luncert.robotcontraption.exception;

import com.luncert.robotcontraption.content.util.Lang;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;

public class AircraftAssemblyException extends AssemblyException {

    public AircraftAssemblyException(AssemblyException e) {
        super(e.component);
    }

    public AircraftAssemblyException(String langKey, Object... objects) {
        super(Lang.translateDirect("gui.assembly.exception." + langKey, objects));
    }

    @Override
    public String getMessage() {
        return component.getString();
    }
}
