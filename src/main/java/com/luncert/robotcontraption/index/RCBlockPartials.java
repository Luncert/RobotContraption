package com.luncert.robotcontraption.index;

import com.jozufozu.flywheel.core.PartialModel;
import com.luncert.robotcontraption.RobotContraption;

public class RCBlockPartials {

    public static void init() {
        // init static fields
    }

    public static final PartialModel
            SHAFT_HALF = block("shaft_half"),
            ENCASED_FAN_INNER = block("fuel_engine/propeller");

    private static PartialModel block(String path) {
        return new PartialModel(RobotContraption.asResource("block/" + path));
    }

    private static PartialModel entity(String path) {
        return new PartialModel(RobotContraption.asResource("entity/" + path));
    }
}
