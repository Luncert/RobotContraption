package com.luncert.robotcontraption.index;

import com.luncert.robotcontraption.compat.create.AircraftContraption;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionType;

public class RCContraptionTypes {

    public static final ContraptionType AIRCRAFT = ContraptionType.register("aircraft", AircraftContraption::new);

    public static void register() {
    }
}
