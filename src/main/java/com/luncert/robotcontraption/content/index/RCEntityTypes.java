package com.luncert.robotcontraption.content.index;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.content.aircraft.AircraftEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.MobCategory;

public class RCEntityTypes {

    private static final CreateRegistrate REGISTRATE = RobotContraption.registrate();

    public static void register() {}

    public static final EntityEntry<AircraftEntity> AIRCRAFT =
            REGISTRATE.<AircraftEntity>entity("robot", AircraftEntity::new, MobCategory.MISC)
                    .properties(b -> b.sized(1, 1))
                    .register();
}
