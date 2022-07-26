package com.luncert.robotcontraption.index;

import com.jozufozu.flywheel.core.PartialModel;
import com.luncert.robotcontraption.RobotContraption;

public class RCBlockPartials {

    public static final PartialModel
            SHAFT_HALF = block("shaft_half"),
            ENCASED_FAN_INNER = block("fuel_engine/propeller"),
            COGWHEEL_NO_SHAFT = block("cogwheel_no_shaft"),
            VACUUM_PUMP_SLIDER = block("vacuum_pump_slider")
    ;

    private static PartialModel block(String path) {
        return new PartialModel(RobotContraption.asResource("block/" + path));
    }

    private static PartialModel entity(String path) {
        return new PartialModel(RobotContraption.asResource("entity/" + path));
    }

    public static void init() {
        // init static fields
    }
}
